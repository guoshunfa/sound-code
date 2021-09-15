package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetBoundException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

class UnixAsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannelImpl implements Port.PollableChannel {
   private static final NativeDispatcher nd = new SocketDispatcher();
   private final Port port;
   private final int fdVal;
   private final AtomicBoolean accepting = new AtomicBoolean();
   private final Object updateLock = new Object();
   private boolean acceptPending;
   private CompletionHandler<AsynchronousSocketChannel, Object> acceptHandler;
   private Object acceptAttachment;
   private PendingFuture<AsynchronousSocketChannel, Object> acceptFuture;
   private AccessControlContext acceptAcc;

   private void enableAccept() {
      this.accepting.set(false);
   }

   UnixAsynchronousServerSocketChannelImpl(Port var1) throws IOException {
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

   void implClose() throws IOException {
      this.port.unregister(this.fdVal);
      nd.close(this.fd);
      CompletionHandler var1;
      Object var2;
      PendingFuture var3;
      synchronized(this.updateLock) {
         if (!this.acceptPending) {
            return;
         }

         this.acceptPending = false;
         var1 = this.acceptHandler;
         var2 = this.acceptAttachment;
         var3 = this.acceptFuture;
      }

      AsynchronousCloseException var4 = new AsynchronousCloseException();
      var4.setStackTrace(new StackTraceElement[0]);
      if (var1 == null) {
         var3.setFailure(var4);
      } else {
         Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var1, var2, (Object)null, (Throwable)var4);
      }

   }

   public AsynchronousChannelGroupImpl group() {
      return this.port;
   }

   public void onEvent(int var1, boolean var2) {
      synchronized(this.updateLock) {
         if (!this.acceptPending) {
            return;
         }

         this.acceptPending = false;
      }

      FileDescriptor var3 = new FileDescriptor();
      InetSocketAddress[] var4 = new InetSocketAddress[1];
      Object var5 = null;

      try {
         this.begin();
         int var23 = this.accept(this.fd, var3, var4);
         if (var23 == -2) {
            synchronized(this.updateLock) {
               this.acceptPending = true;
            }

            this.port.startPoll(this.fdVal, Net.POLLIN);
            return;
         }
      } catch (Throwable var20) {
         Object var6 = var20;
         if (var20 instanceof ClosedChannelException) {
            var6 = new AsynchronousCloseException();
         }

         var5 = var6;
      } finally {
         this.end();
      }

      AsynchronousSocketChannel var24 = null;
      if (var5 == null) {
         try {
            var24 = this.finishAccept(var3, var4[0], this.acceptAcc);
         } catch (Throwable var19) {
            Object var7 = var19;
            if (!(var19 instanceof IOException) && !(var19 instanceof SecurityException)) {
               var7 = new IOException(var19);
            }

            var5 = var7;
         }
      }

      CompletionHandler var25 = this.acceptHandler;
      Object var8 = this.acceptAttachment;
      PendingFuture var9 = this.acceptFuture;
      this.enableAccept();
      if (var25 == null) {
         var9.setResult(var24, (Throwable)var5);
         if (var24 != null && var9.isCancelled()) {
            try {
               var24.close();
            } catch (IOException var17) {
            }
         }
      } else {
         Invoker.invoke(this, var25, var8, var24, (Throwable)var5);
      }

   }

   private AsynchronousSocketChannel finishAccept(FileDescriptor var1, final InetSocketAddress var2, AccessControlContext var3) throws IOException, SecurityException {
      UnixAsynchronousSocketChannelImpl var4 = null;

      try {
         var4 = new UnixAsynchronousSocketChannelImpl(this.port, var1, var2);
      } catch (IOException var9) {
         nd.close(var1);
         throw var9;
      }

      try {
         if (var3 != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  SecurityManager var1 = System.getSecurityManager();
                  if (var1 != null) {
                     var1.checkAccept(var2.getAddress().getHostAddress(), var2.getPort());
                  }

                  return null;
               }
            }, var3);
         } else {
            SecurityManager var5 = System.getSecurityManager();
            if (var5 != null) {
               var5.checkAccept(var2.getAddress().getHostAddress(), var2.getPort());
            }
         }

         return var4;
      } catch (SecurityException var8) {
         try {
            var4.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }
   }

   Future<AsynchronousSocketChannel> implAccept(Object var1, CompletionHandler<AsynchronousSocketChannel, Object> var2) {
      if (!this.isOpen()) {
         ClosedChannelException var19 = new ClosedChannelException();
         if (var2 == null) {
            return CompletedFuture.withFailure(var19);
         } else {
            Invoker.invoke(this, var2, var1, (Object)null, var19);
            return null;
         }
      } else if (this.localAddress == null) {
         throw new NotYetBoundException();
      } else if (this.isAcceptKilled()) {
         throw new RuntimeException("Accept not allowed due cancellation");
      } else if (!this.accepting.compareAndSet(false, true)) {
         throw new AcceptPendingException();
      } else {
         FileDescriptor var3 = new FileDescriptor();
         InetSocketAddress[] var4 = new InetSocketAddress[1];
         Object var5 = null;

         label155: {
            PendingFuture var8;
            try {
               this.begin();
               int var20 = this.accept(this.fd, var3, var4);
               if (var20 != -2) {
                  break label155;
               }

               PendingFuture var7 = null;
               synchronized(this.updateLock) {
                  if (var2 == null) {
                     this.acceptHandler = null;
                     var7 = new PendingFuture(this);
                     this.acceptFuture = var7;
                  } else {
                     this.acceptHandler = var2;
                     this.acceptAttachment = var1;
                  }

                  this.acceptAcc = System.getSecurityManager() == null ? null : AccessController.getContext();
                  this.acceptPending = true;
               }

               this.port.startPoll(this.fdVal, Net.POLLIN);
               var8 = var7;
            } catch (Throwable var17) {
               Object var6 = var17;
               if (var17 instanceof ClosedChannelException) {
                  var6 = new AsynchronousCloseException();
               }

               var5 = var6;
               break label155;
            } finally {
               this.end();
            }

            return var8;
         }

         AsynchronousSocketChannel var21 = null;
         if (var5 == null) {
            try {
               var21 = this.finishAccept(var3, var4[0], (AccessControlContext)null);
            } catch (Throwable var15) {
               var5 = var15;
            }
         }

         this.enableAccept();
         if (var2 == null) {
            return CompletedFuture.withResult(var21, (Throwable)var5);
         } else {
            Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var2, var1, (Object)var21, (Throwable)var5);
            return null;
         }
      }
   }

   private int accept(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException {
      return this.accept0(var1, var2, var3);
   }

   private static native void initIDs();

   private native int accept0(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException;

   static {
      IOUtil.load();
      initIDs();
   }
}
