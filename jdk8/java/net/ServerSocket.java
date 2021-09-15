package java.net;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class ServerSocket implements Closeable {
   private boolean created;
   private boolean bound;
   private boolean closed;
   private Object closeLock;
   private SocketImpl impl;
   private boolean oldImpl;
   private static SocketImplFactory factory = null;

   ServerSocket(SocketImpl var1) {
      this.created = false;
      this.bound = false;
      this.closed = false;
      this.closeLock = new Object();
      this.oldImpl = false;
      this.impl = var1;
      var1.setServerSocket(this);
   }

   public ServerSocket() throws IOException {
      this.created = false;
      this.bound = false;
      this.closed = false;
      this.closeLock = new Object();
      this.oldImpl = false;
      this.setImpl();
   }

   public ServerSocket(int var1) throws IOException {
      this(var1, 50, (InetAddress)null);
   }

   public ServerSocket(int var1, int var2) throws IOException {
      this(var1, var2, (InetAddress)null);
   }

   public ServerSocket(int var1, int var2, InetAddress var3) throws IOException {
      this.created = false;
      this.bound = false;
      this.closed = false;
      this.closeLock = new Object();
      this.oldImpl = false;
      this.setImpl();
      if (var1 >= 0 && var1 <= 65535) {
         if (var2 < 1) {
            var2 = 50;
         }

         try {
            this.bind(new InetSocketAddress(var3, var1), var2);
         } catch (SecurityException var5) {
            this.close();
            throw var5;
         } catch (IOException var6) {
            this.close();
            throw var6;
         }
      } else {
         throw new IllegalArgumentException("Port value out of range: " + var1);
      }
   }

   SocketImpl getImpl() throws SocketException {
      if (!this.created) {
         this.createImpl();
      }

      return this.impl;
   }

   private void checkOldImpl() {
      if (this.impl != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws NoSuchMethodException {
                  ServerSocket.this.impl.getClass().getDeclaredMethod("connect", SocketAddress.class, Integer.TYPE);
                  return null;
               }
            });
         } catch (PrivilegedActionException var2) {
            this.oldImpl = true;
         }

      }
   }

   private void setImpl() {
      if (factory != null) {
         this.impl = factory.createSocketImpl();
         this.checkOldImpl();
      } else {
         this.impl = new SocksSocketImpl();
      }

      if (this.impl != null) {
         this.impl.setServerSocket(this);
      }

   }

   void createImpl() throws SocketException {
      if (this.impl == null) {
         this.setImpl();
      }

      try {
         this.impl.create(true);
         this.created = true;
      } catch (IOException var2) {
         throw new SocketException(var2.getMessage());
      }
   }

   public void bind(SocketAddress var1) throws IOException {
      this.bind(var1, 50);
   }

   public void bind(SocketAddress var1, int var2) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.oldImpl && this.isBound()) {
         throw new SocketException("Already bound");
      } else {
         if (var1 == null) {
            var1 = new InetSocketAddress(0);
         }

         if (!(var1 instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
         } else {
            InetSocketAddress var3 = (InetSocketAddress)var1;
            if (var3.isUnresolved()) {
               throw new SocketException("Unresolved address");
            } else {
               if (var2 < 1) {
                  var2 = 50;
               }

               try {
                  SecurityManager var4 = System.getSecurityManager();
                  if (var4 != null) {
                     var4.checkListen(var3.getPort());
                  }

                  this.getImpl().bind(var3.getAddress(), var3.getPort());
                  this.getImpl().listen(var2);
                  this.bound = true;
               } catch (SecurityException var5) {
                  this.bound = false;
                  throw var5;
               } catch (IOException var6) {
                  this.bound = false;
                  throw var6;
               }
            }
         }
      }
   }

   public InetAddress getInetAddress() {
      if (!this.isBound()) {
         return null;
      } else {
         try {
            InetAddress var1 = this.getImpl().getInetAddress();
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkConnect(var1.getHostAddress(), -1);
            }

            return var1;
         } catch (SecurityException var3) {
            return InetAddress.getLoopbackAddress();
         } catch (SocketException var4) {
            return null;
         }
      }
   }

   public int getLocalPort() {
      if (!this.isBound()) {
         return -1;
      } else {
         try {
            return this.getImpl().getLocalPort();
         } catch (SocketException var2) {
            return -1;
         }
      }
   }

   public SocketAddress getLocalSocketAddress() {
      return !this.isBound() ? null : new InetSocketAddress(this.getInetAddress(), this.getLocalPort());
   }

   public Socket accept() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.isBound()) {
         throw new SocketException("Socket is not bound yet");
      } else {
         Socket var1 = new Socket((SocketImpl)null);
         this.implAccept(var1);
         return var1;
      }
   }

   protected final void implAccept(Socket var1) throws IOException {
      SocketImpl var2 = null;

      try {
         if (var1.impl == null) {
            var1.setImpl();
         } else {
            var1.impl.reset();
         }

         var2 = var1.impl;
         var1.impl = null;
         var2.address = new InetAddress();
         var2.fd = new FileDescriptor();
         this.getImpl().accept(var2);
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            var3.checkAccept(var2.getInetAddress().getHostAddress(), var2.getPort());
         }
      } catch (IOException var4) {
         if (var2 != null) {
            var2.reset();
         }

         var1.impl = var2;
         throw var4;
      } catch (SecurityException var5) {
         if (var2 != null) {
            var2.reset();
         }

         var1.impl = var2;
         throw var5;
      }

      var1.impl = var2;
      var1.postAccept();
   }

   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.isClosed()) {
            if (this.created) {
               this.impl.close();
            }

            this.closed = true;
         }
      }
   }

   public ServerSocketChannel getChannel() {
      return null;
   }

   public boolean isBound() {
      return this.bound || this.oldImpl;
   }

   public boolean isClosed() {
      synchronized(this.closeLock) {
         return this.closed;
      }
   }

   public synchronized void setSoTimeout(int var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4102, new Integer(var1));
      }
   }

   public synchronized int getSoTimeout() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         Object var1 = this.getImpl().getOption(4102);
         return var1 instanceof Integer ? (Integer)var1 : 0;
      }
   }

   public void setReuseAddress(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4, var1);
      }
   }

   public boolean getReuseAddress() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Boolean)((Boolean)this.getImpl().getOption(4));
      }
   }

   public String toString() {
      if (!this.isBound()) {
         return "ServerSocket[unbound]";
      } else {
         InetAddress var1;
         if (System.getSecurityManager() != null) {
            var1 = InetAddress.getLoopbackAddress();
         } else {
            var1 = this.impl.getInetAddress();
         }

         return "ServerSocket[addr=" + var1 + ",localport=" + this.impl.getLocalPort() + "]";
      }
   }

   void setBound() {
      this.bound = true;
   }

   void setCreated() {
      this.created = true;
   }

   public static synchronized void setSocketFactory(SocketImplFactory var0) throws IOException {
      if (factory != null) {
         throw new SocketException("factory already defined");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         factory = var0;
      }
   }

   public synchronized void setReceiveBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("negative receive size");
      } else if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4098, new Integer(var1));
      }
   }

   public synchronized int getReceiveBufferSize() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         int var1 = 0;
         Object var2 = this.getImpl().getOption(4098);
         if (var2 instanceof Integer) {
            var1 = (Integer)var2;
         }

         return var1;
      }
   }

   public void setPerformancePreferences(int var1, int var2, int var3) {
   }
}
