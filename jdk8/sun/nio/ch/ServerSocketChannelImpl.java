package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.net.NetHooks;

class ServerSocketChannelImpl extends ServerSocketChannel implements SelChImpl {
   private static NativeDispatcher nd;
   private final FileDescriptor fd;
   private int fdVal;
   private volatile long thread = 0L;
   private final Object lock = new Object();
   private final Object stateLock = new Object();
   private static final int ST_UNINITIALIZED = -1;
   private static final int ST_INUSE = 0;
   private static final int ST_KILLED = 1;
   private int state = -1;
   private InetSocketAddress localAddress;
   private boolean isReuseAddress;
   ServerSocket socket;

   ServerSocketChannelImpl(SelectorProvider var1) throws IOException {
      super(var1);
      this.fd = Net.serverSocket(true);
      this.fdVal = IOUtil.fdVal(this.fd);
      this.state = 0;
   }

   ServerSocketChannelImpl(SelectorProvider var1, FileDescriptor var2, boolean var3) throws IOException {
      super(var1);
      this.fd = var2;
      this.fdVal = IOUtil.fdVal(var2);
      this.state = 0;
      if (var3) {
         this.localAddress = Net.localAddress(var2);
      }

   }

   public ServerSocket socket() {
      synchronized(this.stateLock) {
         if (this.socket == null) {
            this.socket = ServerSocketAdaptor.create(this);
         }

         return this.socket;
      }
   }

   public SocketAddress getLocalAddress() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else {
            return this.localAddress == null ? this.localAddress : Net.getRevealedLocalAddress(Net.asInetSocketAddress(this.localAddress));
         }
      }
   }

   public <T> ServerSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         synchronized(this.stateLock) {
            if (!this.isOpen()) {
               throw new ClosedChannelException();
            } else if (var1 == StandardSocketOptions.IP_TOS) {
               StandardProtocolFamily var4 = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
               Net.setSocketOption(this.fd, var4, var1, var2);
               return this;
            } else {
               if (var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                  this.isReuseAddress = (Boolean)var2;
               } else {
                  Net.setSocketOption(this.fd, Net.UNSPEC, var1, var2);
               }

               return this;
            }
         }
      }
   }

   public <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         synchronized(this.stateLock) {
            if (!this.isOpen()) {
               throw new ClosedChannelException();
            } else {
               return var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind() ? this.isReuseAddress : Net.getSocketOption(this.fd, Net.UNSPEC, var1);
            }
         }
      }
   }

   public final Set<SocketOption<?>> supportedOptions() {
      return ServerSocketChannelImpl.DefaultOptionsHolder.defaultOptions;
   }

   public boolean isBound() {
      synchronized(this.stateLock) {
         return this.localAddress != null;
      }
   }

   public InetSocketAddress localAddress() {
      synchronized(this.stateLock) {
         return this.localAddress;
      }
   }

   public ServerSocketChannel bind(SocketAddress var1, int var2) throws IOException {
      synchronized(this.lock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (this.isBound()) {
            throw new AlreadyBoundException();
         } else {
            InetSocketAddress var4 = var1 == null ? new InetSocketAddress(0) : Net.checkAddress(var1);
            SecurityManager var5 = System.getSecurityManager();
            if (var5 != null) {
               var5.checkListen(var4.getPort());
            }

            NetHooks.beforeTcpBind(this.fd, var4.getAddress(), var4.getPort());
            Net.bind(this.fd, var4.getAddress(), var4.getPort());
            Net.listen(this.fd, var2 < 1 ? 50 : var2);
            synchronized(this.stateLock) {
               this.localAddress = Net.localAddress(this.fd);
            }

            return this;
         }
      }
   }

   public SocketChannel accept() throws IOException {
      synchronized(this.lock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (!this.isBound()) {
            throw new NotYetBoundException();
         } else {
            SocketChannelImpl var2 = null;
            int var3 = 0;
            FileDescriptor var4 = new FileDescriptor();
            InetSocketAddress[] var5 = new InetSocketAddress[1];

            InetSocketAddress var6;
            try {
               this.begin();
               if (!this.isOpen()) {
                  var6 = null;
                  return var6;
               }

               this.thread = NativeThread.current();

               do {
                  var3 = this.accept(this.fd, var4, var5);
               } while(var3 == -3 && this.isOpen());
            } finally {
               this.thread = 0L;
               this.end(var3 > 0);

               assert IOStatus.check(var3);

            }

            if (var3 < 1) {
               return null;
            } else {
               IOUtil.configureBlocking(var4, true);
               var6 = var5[0];
               var2 = new SocketChannelImpl(this.provider(), var4, var6);
               SecurityManager var7 = System.getSecurityManager();
               if (var7 != null) {
                  try {
                     var7.checkAccept(var6.getAddress().getHostAddress(), var6.getPort());
                  } catch (SecurityException var13) {
                     var2.close();
                     throw var13;
                  }
               }

               return var2;
            }
         }
      }
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      IOUtil.configureBlocking(this.fd, var1);
   }

   protected void implCloseSelectableChannel() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 1) {
            nd.preClose(this.fd);
         }

         long var2 = this.thread;
         if (var2 != 0L) {
            NativeThread.signal(var2);
         }

         if (!this.isRegistered()) {
            this.kill();
         }

      }
   }

   public void kill() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 1) {
            if (this.state == -1) {
               this.state = 1;
            } else {
               assert !this.isOpen() && !this.isRegistered();

               nd.close(this.fd);
               this.state = 1;
            }
         }
      }
   }

   public boolean translateReadyOps(int var1, int var2, SelectionKeyImpl var3) {
      int var4 = var3.nioInterestOps();
      int var5 = var3.nioReadyOps();
      int var6 = var2;
      if ((var1 & Net.POLLNVAL) != 0) {
         return false;
      } else if ((var1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
         var3.nioReadyOps(var4);
         return (var4 & ~var5) != 0;
      } else {
         if ((var1 & Net.POLLIN) != 0 && (var4 & 16) != 0) {
            var6 = var2 | 16;
         }

         var3.nioReadyOps(var6);
         return (var6 & ~var5) != 0;
      }
   }

   public boolean translateAndUpdateReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, var2.nioReadyOps(), var2);
   }

   public boolean translateAndSetReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, 0, var2);
   }

   int poll(int var1, long var2) throws IOException {
      assert Thread.holdsLock(this.blockingLock()) && !this.isBlocking();

      synchronized(this.lock) {
         int var5 = 0;

         try {
            this.begin();
            synchronized(this.stateLock) {
               if (!this.isOpen()) {
                  byte var7 = 0;
                  return var7;
               }

               this.thread = NativeThread.current();
            }

            var5 = Net.poll(this.fd, var1, var2);
            return var5;
         } finally {
            this.thread = 0L;
            this.end(var5 > 0);
         }
      }
   }

   public void translateAndSetInterestOps(int var1, SelectionKeyImpl var2) {
      int var3 = 0;
      if ((var1 & 16) != 0) {
         var3 |= Net.POLLIN;
      }

      var2.selector.putEventOps(var2, var3);
   }

   public FileDescriptor getFD() {
      return this.fd;
   }

   public int getFDVal() {
      return this.fdVal;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getClass().getName());
      var1.append('[');
      if (!this.isOpen()) {
         var1.append("closed");
      } else {
         synchronized(this.stateLock) {
            InetSocketAddress var3 = this.localAddress();
            if (var3 == null) {
               var1.append("unbound");
            } else {
               var1.append(Net.getRevealedLocalAddressAsString(var3));
            }
         }
      }

      var1.append(']');
      return var1.toString();
   }

   private int accept(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException {
      return this.accept0(var1, var2, var3);
   }

   private native int accept0(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException;

   private static native void initIDs();

   static {
      IOUtil.load();
      initIDs();
      nd = new SocketDispatcher();
   }

   private static class DefaultOptionsHolder {
      static final Set<SocketOption<?>> defaultOptions = defaultOptions();

      private static Set<SocketOption<?>> defaultOptions() {
         HashSet var0 = new HashSet(2);
         var0.add(StandardSocketOptions.SO_RCVBUF);
         var0.add(StandardSocketOptions.SO_REUSEADDR);
         var0.add(StandardSocketOptions.IP_TOS);
         return Collections.unmodifiableSet(var0);
      }
   }
}
