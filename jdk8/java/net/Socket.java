package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.net.ApplicationProxy;

public class Socket implements Closeable {
   private boolean created;
   private boolean bound;
   private boolean connected;
   private boolean closed;
   private Object closeLock;
   private boolean shutIn;
   private boolean shutOut;
   SocketImpl impl;
   private boolean oldImpl;
   private static SocketImplFactory factory = null;

   public Socket() {
      this.created = false;
      this.bound = false;
      this.connected = false;
      this.closed = false;
      this.closeLock = new Object();
      this.shutIn = false;
      this.shutOut = false;
      this.oldImpl = false;
      this.setImpl();
   }

   public Socket(Proxy var1) {
      this.created = false;
      this.bound = false;
      this.connected = false;
      this.closed = false;
      this.closeLock = new Object();
      this.shutIn = false;
      this.shutOut = false;
      this.oldImpl = false;
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid Proxy");
      } else {
         Object var2 = var1 == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(var1);
         Proxy.Type var3 = ((Proxy)var2).type();
         if (var3 != Proxy.Type.SOCKS && var3 != Proxy.Type.HTTP) {
            if (var2 != Proxy.NO_PROXY) {
               throw new IllegalArgumentException("Invalid Proxy");
            }

            if (factory == null) {
               this.impl = new PlainSocketImpl();
               this.impl.setSocket(this);
            } else {
               this.setImpl();
            }
         } else {
            SecurityManager var4 = System.getSecurityManager();
            InetSocketAddress var5 = (InetSocketAddress)((Proxy)var2).address();
            if (var5.getAddress() != null) {
               this.checkAddress(var5.getAddress(), "Socket");
            }

            if (var4 != null) {
               if (var5.isUnresolved()) {
                  var5 = new InetSocketAddress(var5.getHostName(), var5.getPort());
               }

               if (var5.isUnresolved()) {
                  var4.checkConnect(var5.getHostName(), var5.getPort());
               } else {
                  var4.checkConnect(var5.getAddress().getHostAddress(), var5.getPort());
               }
            }

            this.impl = (SocketImpl)(var3 == Proxy.Type.SOCKS ? new SocksSocketImpl((Proxy)var2) : new HttpConnectSocketImpl((Proxy)var2));
            this.impl.setSocket(this);
         }

      }
   }

   protected Socket(SocketImpl var1) throws SocketException {
      this.created = false;
      this.bound = false;
      this.connected = false;
      this.closed = false;
      this.closeLock = new Object();
      this.shutIn = false;
      this.shutOut = false;
      this.oldImpl = false;
      this.impl = var1;
      if (var1 != null) {
         this.checkOldImpl();
         this.impl.setSocket(this);
      }

   }

   public Socket(String var1, int var2) throws UnknownHostException, IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : new InetSocketAddress(InetAddress.getByName((String)null), var2), (SocketAddress)null, true);
   }

   public Socket(InetAddress var1, int var2) throws IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : null, (SocketAddress)null, true);
   }

   public Socket(String var1, int var2, InetAddress var3, int var4) throws IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : new InetSocketAddress(InetAddress.getByName((String)null), var2), new InetSocketAddress(var3, var4), true);
   }

   public Socket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : null, new InetSocketAddress(var3, var4), true);
   }

   /** @deprecated */
   @Deprecated
   public Socket(String var1, int var2, boolean var3) throws IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : new InetSocketAddress(InetAddress.getByName((String)null), var2), (SocketAddress)null, var3);
   }

   /** @deprecated */
   @Deprecated
   public Socket(InetAddress var1, int var2, boolean var3) throws IOException {
      this(var1 != null ? new InetSocketAddress(var1, var2) : null, new InetSocketAddress(0), var3);
   }

   private Socket(SocketAddress var1, SocketAddress var2, boolean var3) throws IOException {
      this.created = false;
      this.bound = false;
      this.connected = false;
      this.closed = false;
      this.closeLock = new Object();
      this.shutIn = false;
      this.shutOut = false;
      this.oldImpl = false;
      this.setImpl();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         try {
            this.createImpl(var3);
            if (var2 != null) {
               this.bind(var2);
            }

            this.connect(var1);
         } catch (IllegalArgumentException | SecurityException | IOException var7) {
            try {
               this.close();
            } catch (IOException var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }
      }
   }

   void createImpl(boolean var1) throws SocketException {
      if (this.impl == null) {
         this.setImpl();
      }

      try {
         this.impl.create(var1);
         this.created = true;
      } catch (IOException var3) {
         throw new SocketException(var3.getMessage());
      }
   }

   private void checkOldImpl() {
      if (this.impl != null) {
         this.oldImpl = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
               Class var1 = Socket.this.impl.getClass();

               while(true) {
                  try {
                     var1.getDeclaredMethod("connect", SocketAddress.class, Integer.TYPE);
                     return Boolean.FALSE;
                  } catch (NoSuchMethodException var3) {
                     var1 = var1.getSuperclass();
                     if (var1.equals(SocketImpl.class)) {
                        return Boolean.TRUE;
                     }
                  }
               }
            }
         });
      }
   }

   void setImpl() {
      if (factory != null) {
         this.impl = factory.createSocketImpl();
         this.checkOldImpl();
      } else {
         this.impl = new SocksSocketImpl();
      }

      if (this.impl != null) {
         this.impl.setSocket(this);
      }

   }

   SocketImpl getImpl() throws SocketException {
      if (!this.created) {
         this.createImpl(true);
      }

      return this.impl;
   }

   public void connect(SocketAddress var1) throws IOException {
      this.connect(var1, 0);
   }

   public void connect(SocketAddress var1, int var2) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("connect: The address can't be null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("connect: timeout can't be negative");
      } else if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.oldImpl && this.isConnected()) {
         throw new SocketException("already connected");
      } else if (!(var1 instanceof InetSocketAddress)) {
         throw new IllegalArgumentException("Unsupported address type");
      } else {
         InetSocketAddress var3 = (InetSocketAddress)var1;
         InetAddress var4 = var3.getAddress();
         int var5 = var3.getPort();
         this.checkAddress(var4, "connect");
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            if (var3.isUnresolved()) {
               var6.checkConnect(var3.getHostName(), var5);
            } else {
               var6.checkConnect(var4.getHostAddress(), var5);
            }
         }

         if (!this.created) {
            this.createImpl(true);
         }

         if (!this.oldImpl) {
            this.impl.connect((SocketAddress)var3, var2);
         } else {
            if (var2 != 0) {
               throw new UnsupportedOperationException("SocketImpl.connect(addr, timeout)");
            }

            if (var3.isUnresolved()) {
               this.impl.connect(var4.getHostName(), var5);
            } else {
               this.impl.connect(var4, var5);
            }
         }

         this.connected = true;
         this.bound = true;
      }
   }

   public void bind(SocketAddress var1) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.oldImpl && this.isBound()) {
         throw new SocketException("Already bound");
      } else if (var1 != null && !(var1 instanceof InetSocketAddress)) {
         throw new IllegalArgumentException("Unsupported address type");
      } else {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         if (var2 != null && var2.isUnresolved()) {
            throw new SocketException("Unresolved address");
         } else {
            if (var2 == null) {
               var2 = new InetSocketAddress(0);
            }

            InetAddress var3 = var2.getAddress();
            int var4 = var2.getPort();
            this.checkAddress(var3, "bind");
            SecurityManager var5 = System.getSecurityManager();
            if (var5 != null) {
               var5.checkListen(var4);
            }

            this.getImpl().bind(var3, var4);
            this.bound = true;
         }
      }
   }

   private void checkAddress(InetAddress var1, String var2) {
      if (var1 != null) {
         if (!(var1 instanceof Inet4Address) && !(var1 instanceof Inet6Address)) {
            throw new IllegalArgumentException(var2 + ": invalid address type");
         }
      }
   }

   final void postAccept() {
      this.connected = true;
      this.created = true;
      this.bound = true;
   }

   void setCreated() {
      this.created = true;
   }

   void setBound() {
      this.bound = true;
   }

   void setConnected() {
      this.connected = true;
   }

   public InetAddress getInetAddress() {
      if (!this.isConnected()) {
         return null;
      } else {
         try {
            return this.getImpl().getInetAddress();
         } catch (SocketException var2) {
            return null;
         }
      }
   }

   public InetAddress getLocalAddress() {
      if (!this.isBound()) {
         return InetAddress.anyLocalAddress();
      } else {
         InetAddress var1 = null;

         try {
            var1 = (InetAddress)this.getImpl().getOption(15);
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkConnect(var1.getHostAddress(), -1);
            }

            if (var1.isAnyLocalAddress()) {
               var1 = InetAddress.anyLocalAddress();
            }
         } catch (SecurityException var3) {
            var1 = InetAddress.getLoopbackAddress();
         } catch (Exception var4) {
            var1 = InetAddress.anyLocalAddress();
         }

         return var1;
      }
   }

   public int getPort() {
      if (!this.isConnected()) {
         return 0;
      } else {
         try {
            return this.getImpl().getPort();
         } catch (SocketException var2) {
            return -1;
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

   public SocketAddress getRemoteSocketAddress() {
      return !this.isConnected() ? null : new InetSocketAddress(this.getInetAddress(), this.getPort());
   }

   public SocketAddress getLocalSocketAddress() {
      return !this.isBound() ? null : new InetSocketAddress(this.getLocalAddress(), this.getLocalPort());
   }

   public SocketChannel getChannel() {
      return null;
   }

   public InputStream getInputStream() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (this.isInputShutdown()) {
         throw new SocketException("Socket input is shutdown");
      } else {
         InputStream var2 = null;

         try {
            var2 = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
               public InputStream run() throws IOException {
                  return Socket.this.impl.getInputStream();
               }
            });
            return var2;
         } catch (PrivilegedActionException var4) {
            throw (IOException)var4.getException();
         }
      }
   }

   public OutputStream getOutputStream() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (this.isOutputShutdown()) {
         throw new SocketException("Socket output is shutdown");
      } else {
         OutputStream var2 = null;

         try {
            var2 = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
               public OutputStream run() throws IOException {
                  return Socket.this.impl.getOutputStream();
               }
            });
            return var2;
         } catch (PrivilegedActionException var4) {
            throw (IOException)var4.getException();
         }
      }
   }

   public void setTcpNoDelay(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(1, var1);
      }
   }

   public boolean getTcpNoDelay() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Boolean)this.getImpl().getOption(1);
      }
   }

   public void setSoLinger(boolean var1, int var2) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         if (!var1) {
            this.getImpl().setOption(128, new Boolean(var1));
         } else {
            if (var2 < 0) {
               throw new IllegalArgumentException("invalid value for SO_LINGER");
            }

            if (var2 > 65535) {
               var2 = 65535;
            }

            this.getImpl().setOption(128, new Integer(var2));
         }

      }
   }

   public int getSoLinger() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         Object var1 = this.getImpl().getOption(128);
         return var1 instanceof Integer ? (Integer)var1 : -1;
      }
   }

   public void sendUrgentData(int var1) throws IOException {
      if (!this.getImpl().supportsUrgentData()) {
         throw new SocketException("Urgent data not supported");
      } else {
         this.getImpl().sendUrgentData(var1);
      }
   }

   public void setOOBInline(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4099, var1);
      }
   }

   public boolean getOOBInline() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Boolean)this.getImpl().getOption(4099);
      }
   }

   public synchronized void setSoTimeout(int var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("timeout can't be negative");
      } else {
         this.getImpl().setOption(4102, new Integer(var1));
      }
   }

   public synchronized int getSoTimeout() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         Object var1 = this.getImpl().getOption(4102);
         return var1 instanceof Integer ? (Integer)var1 : 0;
      }
   }

   public synchronized void setSendBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("negative send size");
      } else if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4097, new Integer(var1));
      }
   }

   public synchronized int getSendBufferSize() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         int var1 = 0;
         Object var2 = this.getImpl().getOption(4097);
         if (var2 instanceof Integer) {
            var1 = (Integer)var2;
         }

         return var1;
      }
   }

   public synchronized void setReceiveBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("invalid receive size");
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

   public void setKeepAlive(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(8, var1);
      }
   }

   public boolean getKeepAlive() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Boolean)this.getImpl().getOption(8);
      }
   }

   public void setTrafficClass(int var1) throws SocketException {
      if (var1 >= 0 && var1 <= 255) {
         if (this.isClosed()) {
            throw new SocketException("Socket is closed");
         } else {
            try {
               this.getImpl().setOption(3, var1);
            } catch (SocketException var3) {
               if (!this.isConnected()) {
                  throw var3;
               }
            }

         }
      } else {
         throw new IllegalArgumentException("tc is not in range 0 -- 255");
      }
   }

   public int getTrafficClass() throws SocketException {
      return (Integer)((Integer)this.getImpl().getOption(3));
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

   public synchronized void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.isClosed()) {
            if (this.created) {
               this.impl.close();
            }

            this.closed = true;
         }
      }
   }

   public void shutdownInput() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (this.isInputShutdown()) {
         throw new SocketException("Socket input is already shutdown");
      } else {
         this.getImpl().shutdownInput();
         this.shutIn = true;
      }
   }

   public void shutdownOutput() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (!this.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (this.isOutputShutdown()) {
         throw new SocketException("Socket output is already shutdown");
      } else {
         this.getImpl().shutdownOutput();
         this.shutOut = true;
      }
   }

   public String toString() {
      try {
         if (this.isConnected()) {
            return "Socket[addr=" + this.getImpl().getInetAddress() + ",port=" + this.getImpl().getPort() + ",localport=" + this.getImpl().getLocalPort() + "]";
         }
      } catch (SocketException var2) {
      }

      return "Socket[unconnected]";
   }

   public boolean isConnected() {
      return this.connected || this.oldImpl;
   }

   public boolean isBound() {
      return this.bound || this.oldImpl;
   }

   public boolean isClosed() {
      synchronized(this.closeLock) {
         return this.closed;
      }
   }

   public boolean isInputShutdown() {
      return this.shutIn;
   }

   public boolean isOutputShutdown() {
      return this.shutOut;
   }

   public static synchronized void setSocketImplFactory(SocketImplFactory var0) throws IOException {
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

   public void setPerformancePreferences(int var1, int var2, int var3) {
   }
}
