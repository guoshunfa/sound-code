package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import sun.net.NetHooks;
import sun.security.action.GetPropertyAction;

class UnixAsynchronousSocketChannelImpl extends AsynchronousSocketChannelImpl implements Port.PollableChannel {
   private static final NativeDispatcher nd = new SocketDispatcher();
   private static final boolean disableSynchronousRead;
   private final Port port;
   private final int fdVal;
   private final Object updateLock = new Object();
   private boolean connectPending;
   private CompletionHandler<Void, Object> connectHandler;
   private Object connectAttachment;
   private PendingFuture<Void, Object> connectFuture;
   private SocketAddress pendingRemote;
   private boolean readPending;
   private boolean isScatteringRead;
   private ByteBuffer readBuffer;
   private ByteBuffer[] readBuffers;
   private CompletionHandler<Number, Object> readHandler;
   private Object readAttachment;
   private PendingFuture<Number, Object> readFuture;
   private Future<?> readTimer;
   private boolean writePending;
   private boolean isGatheringWrite;
   private ByteBuffer writeBuffer;
   private ByteBuffer[] writeBuffers;
   private CompletionHandler<Number, Object> writeHandler;
   private Object writeAttachment;
   private PendingFuture<Number, Object> writeFuture;
   private Future<?> writeTimer;
   private Runnable readTimeoutTask = new Runnable() {
      public void run() {
         CompletionHandler var1 = null;
         Object var2 = null;
         PendingFuture var3 = null;
         synchronized(UnixAsynchronousSocketChannelImpl.this.updateLock) {
            if (!UnixAsynchronousSocketChannelImpl.this.readPending) {
               return;
            }

            UnixAsynchronousSocketChannelImpl.this.readPending = false;
            var1 = UnixAsynchronousSocketChannelImpl.this.readHandler;
            var2 = UnixAsynchronousSocketChannelImpl.this.readAttachment;
            var3 = UnixAsynchronousSocketChannelImpl.this.readFuture;
         }

         UnixAsynchronousSocketChannelImpl.this.enableReading(true);
         InterruptedByTimeoutException var4 = new InterruptedByTimeoutException();
         if (var1 == null) {
            var3.setFailure(var4);
         } else {
            UnixAsynchronousSocketChannelImpl var5 = UnixAsynchronousSocketChannelImpl.this;
            Invoker.invokeIndirectly((AsynchronousChannel)var5, (CompletionHandler)var1, var2, (Object)null, (Throwable)var4);
         }

      }
   };
   private Runnable writeTimeoutTask = new Runnable() {
      public void run() {
         CompletionHandler var1 = null;
         Object var2 = null;
         PendingFuture var3 = null;
         synchronized(UnixAsynchronousSocketChannelImpl.this.updateLock) {
            if (!UnixAsynchronousSocketChannelImpl.this.writePending) {
               return;
            }

            UnixAsynchronousSocketChannelImpl.this.writePending = false;
            var1 = UnixAsynchronousSocketChannelImpl.this.writeHandler;
            var2 = UnixAsynchronousSocketChannelImpl.this.writeAttachment;
            var3 = UnixAsynchronousSocketChannelImpl.this.writeFuture;
         }

         UnixAsynchronousSocketChannelImpl.this.enableWriting(true);
         InterruptedByTimeoutException var4 = new InterruptedByTimeoutException();
         if (var1 != null) {
            Invoker.invokeIndirectly((AsynchronousChannel)UnixAsynchronousSocketChannelImpl.this, (CompletionHandler)var1, var2, (Object)null, (Throwable)var4);
         } else {
            var3.setFailure(var4);
         }

      }
   };

   UnixAsynchronousSocketChannelImpl(Port var1) throws IOException {
      super(var1);

      try {
         IOUtil.configureBlocking(this.fd, false);
      } catch (IOException var3) {
         nd.close(this.fd);
         throw var3;
      }

      this.port = var1;
      this.fdVal = IOUtil.fdVal(this.fd);
      var1.register(this.fdVal, this);
   }

   UnixAsynchronousSocketChannelImpl(Port var1, FileDescriptor var2, InetSocketAddress var3) throws IOException {
      super(var1, var2, var3);
      this.fdVal = IOUtil.fdVal(var2);
      IOUtil.configureBlocking(var2, false);

      try {
         var1.register(this.fdVal, this);
      } catch (ShutdownChannelGroupException var5) {
         throw new IOException(var5);
      }

      this.port = var1;
   }

   public AsynchronousChannelGroupImpl group() {
      return this.port;
   }

   private void updateEvents() {
      assert Thread.holdsLock(this.updateLock);

      int var1 = 0;
      if (this.readPending) {
         var1 |= Net.POLLIN;
      }

      if (this.connectPending || this.writePending) {
         var1 |= Net.POLLOUT;
      }

      if (var1 != 0) {
         this.port.startPoll(this.fdVal, var1);
      }

   }

   private void lockAndUpdateEvents() {
      synchronized(this.updateLock) {
         this.updateEvents();
      }
   }

   private void finish(boolean var1, boolean var2, boolean var3) {
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      synchronized(this.updateLock) {
         if (var2 && this.readPending) {
            this.readPending = false;
            var4 = true;
         }

         if (var3) {
            if (this.writePending) {
               this.writePending = false;
               var5 = true;
            } else if (this.connectPending) {
               this.connectPending = false;
               var6 = true;
            }
         }
      }

      if (var4) {
         if (var5) {
            this.finishWrite(false);
         }

         this.finishRead(var1);
      } else {
         if (var5) {
            this.finishWrite(var1);
         }

         if (var6) {
            this.finishConnect(var1);
         }

      }
   }

   public void onEvent(int var1, boolean var2) {
      boolean var3 = (var1 & Net.POLLIN) > 0;
      boolean var4 = (var1 & Net.POLLOUT) > 0;
      if ((var1 & (Net.POLLERR | Net.POLLHUP)) > 0) {
         var3 = true;
         var4 = true;
      }

      this.finish(var2, var3, var4);
   }

   void implClose() throws IOException {
      this.port.unregister(this.fdVal);
      nd.close(this.fd);
      this.finish(false, true, true);
   }

   public void onCancel(PendingFuture<?, ?> var1) {
      if (var1.getContext() == UnixAsynchronousSocketChannelImpl.OpType.CONNECT) {
         this.killConnect();
      }

      if (var1.getContext() == UnixAsynchronousSocketChannelImpl.OpType.READ) {
         this.killReading();
      }

      if (var1.getContext() == UnixAsynchronousSocketChannelImpl.OpType.WRITE) {
         this.killWriting();
      }

   }

   private void setConnected() throws IOException {
      synchronized(this.stateLock) {
         this.state = 2;
         this.localAddress = Net.localAddress(this.fd);
         this.remoteAddress = (InetSocketAddress)this.pendingRemote;
      }
   }

   private void finishConnect(boolean var1) {
      Object var2 = null;

      try {
         this.begin();
         checkConnect(this.fdVal);
         this.setConnected();
      } catch (Throwable var10) {
         Object var3 = var10;
         if (var10 instanceof ClosedChannelException) {
            var3 = new AsynchronousCloseException();
         }

         var2 = var3;
      } finally {
         this.end();
      }

      if (var2 != null) {
         try {
            this.close();
         } catch (Throwable var9) {
            ((Throwable)var2).addSuppressed(var9);
         }
      }

      CompletionHandler var12 = this.connectHandler;
      Object var4 = this.connectAttachment;
      PendingFuture var5 = this.connectFuture;
      if (var12 == null) {
         var5.setResult((Object)null, (Throwable)var2);
      } else if (var1) {
         Invoker.invokeUnchecked(var12, var4, (Object)null, (Throwable)var2);
      } else {
         Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var12, var4, (Object)null, (Throwable)var2);
      }

   }

   <A> Future<Void> implConnect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3) {
      if (!this.isOpen()) {
         ClosedChannelException var23 = new ClosedChannelException();
         if (var3 == null) {
            return CompletedFuture.withFailure(var23);
         } else {
            Invoker.invoke(this, var3, var2, (Object)null, var23);
            return null;
         }
      } else {
         InetSocketAddress var4 = Net.checkAddress(var1);
         SecurityManager var5 = System.getSecurityManager();
         if (var5 != null) {
            var5.checkConnect(var4.getAddress().getHostAddress(), var4.getPort());
         }

         boolean var6;
         synchronized(this.stateLock) {
            if (this.state == 2) {
               throw new AlreadyConnectedException();
            }

            if (this.state == 1) {
               throw new ConnectionPendingException();
            }

            this.state = 1;
            this.pendingRemote = var1;
            var6 = this.localAddress == null;
         }

         Object var7 = null;

         label186: {
            PendingFuture var10;
            try {
               this.begin();
               if (var6) {
                  NetHooks.beforeTcpConnect(this.fd, var4.getAddress(), var4.getPort());
               }

               int var24 = Net.connect(this.fd, var4.getAddress(), var4.getPort());
               if (var24 != -2) {
                  this.setConnected();
                  break label186;
               }

               PendingFuture var9 = null;
               synchronized(this.updateLock) {
                  if (var3 == null) {
                     var9 = new PendingFuture(this, UnixAsynchronousSocketChannelImpl.OpType.CONNECT);
                     this.connectFuture = var9;
                  } else {
                     this.connectHandler = var3;
                     this.connectAttachment = var2;
                  }

                  this.connectPending = true;
                  this.updateEvents();
               }

               var10 = var9;
            } catch (Throwable var20) {
               Object var8 = var20;
               if (var20 instanceof ClosedChannelException) {
                  var8 = new AsynchronousCloseException();
               }

               var7 = var8;
               break label186;
            } finally {
               this.end();
            }

            return var10;
         }

         if (var7 != null) {
            try {
               this.close();
            } catch (Throwable var18) {
               ((Throwable)var7).addSuppressed(var18);
            }
         }

         if (var3 == null) {
            return CompletedFuture.withResult((Object)null, (Throwable)var7);
         } else {
            Invoker.invoke(this, var3, var2, (Object)null, (Throwable)var7);
            return null;
         }
      }
   }

   private void finishRead(boolean var1) {
      int var2 = -1;
      Object var3 = null;
      boolean var4 = this.isScatteringRead;
      CompletionHandler var5 = this.readHandler;
      Object var6 = this.readAttachment;
      PendingFuture var7 = this.readFuture;
      Future var8 = this.readTimer;

      Object var9;
      try {
         this.begin();
         if (var4) {
            var2 = (int)IOUtil.read(this.fd, this.readBuffers, nd);
         } else {
            var2 = IOUtil.read(this.fd, this.readBuffer, -1L, nd);
         }

         if (var2 == -2) {
            synchronized(this.updateLock) {
               this.readPending = true;
               return;
            }
         }

         this.readBuffer = null;
         this.readBuffers = null;
         this.readAttachment = null;
         this.enableReading();
      } catch (Throwable var16) {
         var9 = var16;
         this.enableReading();
         if (var16 instanceof ClosedChannelException) {
            var9 = new AsynchronousCloseException();
         }

         var3 = var9;
      } finally {
         if (!(var3 instanceof AsynchronousCloseException)) {
            this.lockAndUpdateEvents();
         }

         this.end();
      }

      if (var8 != null) {
         var8.cancel(false);
      }

      var9 = var3 != null ? null : (var4 ? (long)var2 : var2);
      if (var5 == null) {
         var7.setResult(var9, (Throwable)var3);
      } else if (var1) {
         Invoker.invokeUnchecked(var5, var6, var9, (Throwable)var3);
      } else {
         Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var5, var6, (Object)var9, (Throwable)var3);
      }

   }

   <V extends Number, A> Future<V> implRead(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      Invoker.GroupAndInvokeCount var9 = null;
      boolean var10 = false;
      boolean var11 = false;
      if (!disableSynchronousRead) {
         if (var8 == null) {
            var11 = true;
         } else {
            var9 = Invoker.getGroupAndInvokeCount();
            var10 = Invoker.mayInvokeDirect(var9, this.port);
            var11 = var10 || !this.port.isFixedThreadPool();
         }
      }

      int var12 = -2;
      Object var13 = null;
      boolean var14 = false;

      Object var15;
      try {
         this.begin();
         if (var11) {
            if (var1) {
               var12 = (int)IOUtil.read(this.fd, var3, nd);
            } else {
               var12 = IOUtil.read(this.fd, var2, -1L, nd);
            }
         }

         if (var12 == -2) {
            PendingFuture var25 = null;
            synchronized(this.updateLock) {
               this.isScatteringRead = var1;
               this.readBuffer = var2;
               this.readBuffers = var3;
               if (var8 == null) {
                  this.readHandler = null;
                  var25 = new PendingFuture(this, UnixAsynchronousSocketChannelImpl.OpType.READ);
                  this.readFuture = var25;
                  this.readAttachment = null;
               } else {
                  this.readHandler = var8;
                  this.readAttachment = var7;
                  this.readFuture = null;
               }

               if (var4 > 0L) {
                  this.readTimer = this.port.schedule(this.readTimeoutTask, var4, var6);
               }

               this.readPending = true;
               this.updateEvents();
            }

            var14 = true;
            PendingFuture var16 = var25;
            return var16;
         }
      } catch (Throwable var23) {
         var15 = var23;
         if (var23 instanceof ClosedChannelException) {
            var15 = new AsynchronousCloseException();
         }

         var13 = var15;
      } finally {
         if (!var14) {
            this.enableReading();
         }

         this.end();
      }

      var15 = var13 != null ? null : (var1 ? (long)var12 : var12);
      if (var8 != null) {
         if (var10) {
            Invoker.invokeDirect(var9, var8, var7, var15, (Throwable)var13);
         } else {
            Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var8, var7, (Object)var15, (Throwable)var13);
         }

         return null;
      } else {
         return CompletedFuture.withResult(var15, (Throwable)var13);
      }
   }

   private void finishWrite(boolean var1) {
      int var2 = -1;
      Object var3 = null;
      boolean var4 = this.isGatheringWrite;
      CompletionHandler var5 = this.writeHandler;
      Object var6 = this.writeAttachment;
      PendingFuture var7 = this.writeFuture;
      Future var8 = this.writeTimer;

      Object var9;
      try {
         this.begin();
         if (var4) {
            var2 = (int)IOUtil.write(this.fd, this.writeBuffers, nd);
         } else {
            var2 = IOUtil.write(this.fd, this.writeBuffer, -1L, nd);
         }

         if (var2 == -2) {
            synchronized(this.updateLock) {
               this.writePending = true;
               return;
            }
         }

         this.writeBuffer = null;
         this.writeBuffers = null;
         this.writeAttachment = null;
         this.enableWriting();
      } catch (Throwable var16) {
         var9 = var16;
         this.enableWriting();
         if (var16 instanceof ClosedChannelException) {
            var9 = new AsynchronousCloseException();
         }

         var3 = var9;
      } finally {
         if (!(var3 instanceof AsynchronousCloseException)) {
            this.lockAndUpdateEvents();
         }

         this.end();
      }

      if (var8 != null) {
         var8.cancel(false);
      }

      var9 = var3 != null ? null : (var4 ? (long)var2 : var2);
      if (var5 == null) {
         var7.setResult(var9, (Throwable)var3);
      } else if (var1) {
         Invoker.invokeUnchecked(var5, var6, var9, (Throwable)var3);
      } else {
         Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var5, var6, (Object)var9, (Throwable)var3);
      }

   }

   <V extends Number, A> Future<V> implWrite(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      Invoker.GroupAndInvokeCount var9 = Invoker.getGroupAndInvokeCount();
      boolean var10 = Invoker.mayInvokeDirect(var9, this.port);
      boolean var11 = var8 == null || var10 || !this.port.isFixedThreadPool();
      int var12 = -2;
      Object var13 = null;
      boolean var14 = false;

      Object var15;
      label189: {
         PendingFuture var16;
         try {
            this.begin();
            if (var11) {
               if (var1) {
                  var12 = (int)IOUtil.write(this.fd, var3, nd);
               } else {
                  var12 = IOUtil.write(this.fd, var2, -1L, nd);
               }
            }

            if (var12 != -2) {
               break label189;
            }

            PendingFuture var25 = null;
            synchronized(this.updateLock) {
               this.isGatheringWrite = var1;
               this.writeBuffer = var2;
               this.writeBuffers = var3;
               if (var8 == null) {
                  this.writeHandler = null;
                  var25 = new PendingFuture(this, UnixAsynchronousSocketChannelImpl.OpType.WRITE);
                  this.writeFuture = var25;
                  this.writeAttachment = null;
               } else {
                  this.writeHandler = var8;
                  this.writeAttachment = var7;
                  this.writeFuture = null;
               }

               if (var4 > 0L) {
                  this.writeTimer = this.port.schedule(this.writeTimeoutTask, var4, var6);
               }

               this.writePending = true;
               this.updateEvents();
            }

            var14 = true;
            var16 = var25;
         } catch (Throwable var23) {
            var15 = var23;
            if (var23 instanceof ClosedChannelException) {
               var15 = new AsynchronousCloseException();
            }

            var13 = var15;
            break label189;
         } finally {
            if (!var14) {
               this.enableWriting();
            }

            this.end();
         }

         return var16;
      }

      var15 = var13 != null ? null : (var1 ? (long)var12 : var12);
      if (var8 != null) {
         if (var10) {
            Invoker.invokeDirect(var9, var8, var7, var15, (Throwable)var13);
         } else {
            Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var8, var7, (Object)var15, (Throwable)var13);
         }

         return null;
      } else {
         return CompletedFuture.withResult(var15, (Throwable)var13);
      }
   }

   private static native void checkConnect(int var0) throws IOException;

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.nio.ch.disableSynchronousRead", "false")));
      disableSynchronousRead = var0.length() == 0 ? true : Boolean.valueOf(var0);
      IOUtil.load();
   }

   private static enum OpType {
      CONNECT,
      READ,
      WRITE;
   }
}
