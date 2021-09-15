package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

abstract class AsynchronousSocketChannelImpl extends AsynchronousSocketChannel implements Cancellable, Groupable {
   protected final FileDescriptor fd;
   protected final Object stateLock = new Object();
   protected volatile InetSocketAddress localAddress = null;
   protected volatile InetSocketAddress remoteAddress = null;
   static final int ST_UNINITIALIZED = -1;
   static final int ST_UNCONNECTED = 0;
   static final int ST_PENDING = 1;
   static final int ST_CONNECTED = 2;
   protected volatile int state = -1;
   private final Object readLock = new Object();
   private boolean reading;
   private boolean readShutdown;
   private boolean readKilled;
   private final Object writeLock = new Object();
   private boolean writing;
   private boolean writeShutdown;
   private boolean writeKilled;
   private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
   private volatile boolean open = true;
   private boolean isReuseAddress;

   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl var1) throws IOException {
      super(var1.provider());
      this.fd = Net.socket(true);
      this.state = 0;
   }

   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl var1, FileDescriptor var2, InetSocketAddress var3) throws IOException {
      super(var1.provider());
      this.fd = var2;
      this.state = 2;
      this.localAddress = Net.localAddress(var2);
      this.remoteAddress = var3;
   }

   public final boolean isOpen() {
      return this.open;
   }

   final void begin() throws IOException {
      this.closeLock.readLock().lock();
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      }
   }

   final void end() {
      this.closeLock.readLock().unlock();
   }

   abstract void implClose() throws IOException;

   public final void close() throws IOException {
      this.closeLock.writeLock().lock();

      label38: {
         try {
            if (this.open) {
               this.open = false;
               break label38;
            }
         } finally {
            this.closeLock.writeLock().unlock();
         }

         return;
      }

      this.implClose();
   }

   final void enableReading(boolean var1) {
      synchronized(this.readLock) {
         this.reading = false;
         if (var1) {
            this.readKilled = true;
         }

      }
   }

   final void enableReading() {
      this.enableReading(false);
   }

   final void enableWriting(boolean var1) {
      synchronized(this.writeLock) {
         this.writing = false;
         if (var1) {
            this.writeKilled = true;
         }

      }
   }

   final void enableWriting() {
      this.enableWriting(false);
   }

   final void killReading() {
      synchronized(this.readLock) {
         this.readKilled = true;
      }
   }

   final void killWriting() {
      synchronized(this.writeLock) {
         this.writeKilled = true;
      }
   }

   final void killConnect() {
      this.killReading();
      this.killWriting();
   }

   abstract <A> Future<Void> implConnect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3);

   public final Future<Void> connect(SocketAddress var1) {
      return this.implConnect(var1, (Object)null, (CompletionHandler)null);
   }

   public final <A> void connect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3) {
      if (var3 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.implConnect(var1, var2, var3);
      }
   }

   abstract <V extends Number, A> Future<V> implRead(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8);

   private <V extends Number, A> Future<V> read(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      if (!this.isOpen()) {
         ClosedChannelException var14 = new ClosedChannelException();
         if (var8 == null) {
            return CompletedFuture.withFailure(var14);
         } else {
            Invoker.invoke(this, var8, var7, (Object)null, var14);
            return null;
         }
      } else if (this.remoteAddress == null) {
         throw new NotYetConnectedException();
      } else {
         boolean var9 = var1 || var2.hasRemaining();
         boolean var10 = false;
         synchronized(this.readLock) {
            if (this.readKilled) {
               throw new IllegalStateException("Reading not allowed due to timeout or cancellation");
            }

            if (this.reading) {
               throw new ReadPendingException();
            }

            if (this.readShutdown) {
               var10 = true;
            } else if (var9) {
               this.reading = true;
            }
         }

         if (!var10 && var9) {
            return this.implRead(var1, var2, var3, var4, var6, var7, var8);
         } else {
            Object var11;
            if (var1) {
               var11 = var10 ? -1L : 0L;
            } else {
               var11 = var10 ? -1 : 0;
            }

            if (var8 == null) {
               return CompletedFuture.withResult(var11);
            } else {
               Invoker.invoke(this, var8, var7, var11, (Throwable)null);
               return null;
            }
         }
      }
   }

   public final Future<Integer> read(ByteBuffer var1) {
      if (var1.isReadOnly()) {
         throw new IllegalArgumentException("Read-only buffer");
      } else {
         return this.read(false, var1, (ByteBuffer[])null, 0L, TimeUnit.MILLISECONDS, (Object)null, (CompletionHandler)null);
      }
   }

   public final <A> void read(ByteBuffer var1, long var2, TimeUnit var4, A var5, CompletionHandler<Integer, ? super A> var6) {
      if (var6 == null) {
         throw new NullPointerException("'handler' is null");
      } else if (var1.isReadOnly()) {
         throw new IllegalArgumentException("Read-only buffer");
      } else {
         this.read(false, var1, (ByteBuffer[])null, var2, var4, var5, var6);
      }
   }

   public final <A> void read(ByteBuffer[] var1, int var2, int var3, long var4, TimeUnit var6, A var7, CompletionHandler<Long, ? super A> var8) {
      if (var8 == null) {
         throw new NullPointerException("'handler' is null");
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ByteBuffer[] var9 = Util.subsequence(var1, var2, var3);

         for(int var10 = 0; var10 < var9.length; ++var10) {
            if (var9[var10].isReadOnly()) {
               throw new IllegalArgumentException("Read-only buffer");
            }
         }

         this.read(true, (ByteBuffer)null, var9, var4, var6, var7, var8);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   abstract <V extends Number, A> Future<V> implWrite(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8);

   private <V extends Number, A> Future<V> write(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      boolean var9 = var1 || var2.hasRemaining();
      boolean var10 = false;
      if (this.isOpen()) {
         if (this.remoteAddress == null) {
            throw new NotYetConnectedException();
         }

         synchronized(this.writeLock) {
            if (this.writeKilled) {
               throw new IllegalStateException("Writing not allowed due to timeout or cancellation");
            }

            if (this.writing) {
               throw new WritePendingException();
            }

            if (this.writeShutdown) {
               var10 = true;
            } else if (var9) {
               this.writing = true;
            }
         }
      } else {
         var10 = true;
      }

      if (var10) {
         ClosedChannelException var14 = new ClosedChannelException();
         if (var8 == null) {
            return CompletedFuture.withFailure(var14);
         } else {
            Invoker.invoke(this, var8, var7, (Object)null, var14);
            return null;
         }
      } else if (!var9) {
         Object var11 = var1 ? 0L : 0;
         if (var8 == null) {
            return CompletedFuture.withResult(var11);
         } else {
            Invoker.invoke(this, var8, var7, var11, (Throwable)null);
            return null;
         }
      } else {
         return this.implWrite(var1, var2, var3, var4, var6, var7, var8);
      }
   }

   public final Future<Integer> write(ByteBuffer var1) {
      return this.write(false, var1, (ByteBuffer[])null, 0L, TimeUnit.MILLISECONDS, (Object)null, (CompletionHandler)null);
   }

   public final <A> void write(ByteBuffer var1, long var2, TimeUnit var4, A var5, CompletionHandler<Integer, ? super A> var6) {
      if (var6 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.write(false, var1, (ByteBuffer[])null, var2, var4, var5, var6);
      }
   }

   public final <A> void write(ByteBuffer[] var1, int var2, int var3, long var4, TimeUnit var6, A var7, CompletionHandler<Long, ? super A> var8) {
      if (var8 == null) {
         throw new NullPointerException("'handler' is null");
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         var1 = Util.subsequence(var1, var2, var3);
         this.write(true, (ByteBuffer)null, var1, var4, var6, var7, var8);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final AsynchronousSocketChannel bind(SocketAddress var1) throws IOException {
      try {
         this.begin();
         synchronized(this.stateLock) {
            if (this.state == 1) {
               throw new ConnectionPendingException();
            }

            if (this.localAddress != null) {
               throw new AlreadyBoundException();
            }

            InetSocketAddress var3 = var1 == null ? new InetSocketAddress(0) : Net.checkAddress(var1);
            SecurityManager var4 = System.getSecurityManager();
            if (var4 != null) {
               var4.checkListen(var3.getPort());
            }

            NetHooks.beforeTcpBind(this.fd, var3.getAddress(), var3.getPort());
            Net.bind(this.fd, var3.getAddress(), var3.getPort());
            this.localAddress = Net.localAddress(this.fd);
         }
      } finally {
         this.end();
      }

      return this;
   }

   public final SocketAddress getLocalAddress() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         return Net.getRevealedLocalAddress(this.localAddress);
      }
   }

   public final <T> AsynchronousSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         AsynchronousSocketChannelImpl var3;
         try {
            this.begin();
            if (this.writeShutdown) {
               throw new IOException("Connection has been shutdown for writing");
            }

            if (var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
               this.isReuseAddress = (Boolean)var2;
            } else {
               Net.setSocketOption(this.fd, Net.UNSPEC, var1, var2);
            }

            var3 = this;
         } finally {
            this.end();
         }

         return var3;
      }
   }

   public final <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         Boolean var2;
         try {
            this.begin();
            if (var1 != StandardSocketOptions.SO_REUSEADDR || !Net.useExclusiveBind()) {
               Object var6 = Net.getSocketOption(this.fd, Net.UNSPEC, var1);
               return var6;
            }

            var2 = this.isReuseAddress;
         } finally {
            this.end();
         }

         return var2;
      }
   }

   public final Set<SocketOption<?>> supportedOptions() {
      return AsynchronousSocketChannelImpl.DefaultOptionsHolder.defaultOptions;
   }

   public final SocketAddress getRemoteAddress() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         return this.remoteAddress;
      }
   }

   public final AsynchronousSocketChannel shutdownInput() throws IOException {
      try {
         this.begin();
         if (this.remoteAddress == null) {
            throw new NotYetConnectedException();
         }

         synchronized(this.readLock) {
            if (!this.readShutdown) {
               Net.shutdown(this.fd, 0);
               this.readShutdown = true;
            }
         }
      } finally {
         this.end();
      }

      return this;
   }

   public final AsynchronousSocketChannel shutdownOutput() throws IOException {
      try {
         this.begin();
         if (this.remoteAddress == null) {
            throw new NotYetConnectedException();
         }

         synchronized(this.writeLock) {
            if (!this.writeShutdown) {
               Net.shutdown(this.fd, 1);
               this.writeShutdown = true;
            }
         }
      } finally {
         this.end();
      }

      return this;
   }

   public final String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getClass().getName());
      var1.append('[');
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            var1.append("closed");
         } else {
            switch(this.state) {
            case 0:
               var1.append("unconnected");
               break;
            case 1:
               var1.append("connection-pending");
               break;
            case 2:
               var1.append("connected");
               if (this.readShutdown) {
                  var1.append(" ishut");
               }

               if (this.writeShutdown) {
                  var1.append(" oshut");
               }
            }

            if (this.localAddress != null) {
               var1.append(" local=");
               var1.append(Net.getRevealedLocalAddressAsString(this.localAddress));
            }

            if (this.remoteAddress != null) {
               var1.append(" remote=");
               var1.append(this.remoteAddress.toString());
            }
         }
      }

      var1.append(']');
      return var1.toString();
   }

   private static class DefaultOptionsHolder {
      static final Set<SocketOption<?>> defaultOptions = defaultOptions();

      private static Set<SocketOption<?>> defaultOptions() {
         HashSet var0 = new HashSet(5);
         var0.add(StandardSocketOptions.SO_SNDBUF);
         var0.add(StandardSocketOptions.SO_RCVBUF);
         var0.add(StandardSocketOptions.SO_KEEPALIVE);
         var0.add(StandardSocketOptions.SO_REUSEADDR);
         var0.add(StandardSocketOptions.TCP_NODELAY);
         if (ExtendedOptionsImpl.flowSupported()) {
            var0.add(ExtendedSocketOptions.SO_FLOW_SLA);
         }

         return Collections.unmodifiableSet(var0);
      }
   }
}
