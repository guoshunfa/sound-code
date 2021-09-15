package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ConnectionResetException;
import sun.net.NetHooks;
import sun.net.ResourceManager;

abstract class AbstractPlainSocketImpl extends SocketImpl {
   int timeout;
   private int trafficClass;
   private boolean shut_rd = false;
   private boolean shut_wr = false;
   private SocketInputStream socketInputStream = null;
   private SocketOutputStream socketOutputStream = null;
   protected int fdUseCount = 0;
   protected final Object fdLock = new Object();
   protected boolean closePending = false;
   private int CONNECTION_NOT_RESET = 0;
   private int CONNECTION_RESET_PENDING = 1;
   private int CONNECTION_RESET = 2;
   private int resetState;
   private final Object resetLock = new Object();
   protected boolean stream;
   public static final int SHUT_RD = 0;
   public static final int SHUT_WR = 1;

   protected synchronized void create(boolean var1) throws IOException {
      this.stream = var1;
      if (!var1) {
         ResourceManager.beforeUdpCreate();
         this.fd = new FileDescriptor();

         try {
            this.socketCreate(false);
         } catch (IOException var3) {
            ResourceManager.afterUdpClose();
            this.fd = null;
            throw var3;
         }
      } else {
         this.fd = new FileDescriptor();
         this.socketCreate(true);
      }

      if (this.socket != null) {
         this.socket.setCreated();
      }

      if (this.serverSocket != null) {
         this.serverSocket.setCreated();
      }

   }

   protected void connect(String var1, int var2) throws UnknownHostException, IOException {
      boolean var3 = false;

      try {
         InetAddress var4 = InetAddress.getByName(var1);
         this.port = var2;
         this.address = var4;
         this.connectToAddress(var4, var2, this.timeout);
         var3 = true;
      } finally {
         if (!var3) {
            try {
               this.close();
            } catch (IOException var10) {
            }
         }

      }

   }

   protected void connect(InetAddress var1, int var2) throws IOException {
      this.port = var2;
      this.address = var1;

      try {
         this.connectToAddress(var1, var2, this.timeout);
      } catch (IOException var4) {
         this.close();
         throw var4;
      }
   }

   protected void connect(SocketAddress var1, int var2) throws IOException {
      boolean var3 = false;

      try {
         if (var1 == null || !(var1 instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("unsupported address type");
         }

         InetSocketAddress var4 = (InetSocketAddress)var1;
         if (var4.isUnresolved()) {
            throw new UnknownHostException(var4.getHostName());
         }

         this.port = var4.getPort();
         this.address = var4.getAddress();
         this.connectToAddress(this.address, this.port, var2);
         var3 = true;
      } finally {
         if (!var3) {
            try {
               this.close();
            } catch (IOException var10) {
            }
         }

      }

   }

   private void connectToAddress(InetAddress var1, int var2, int var3) throws IOException {
      if (var1.isAnyLocalAddress()) {
         this.doConnect(InetAddress.getLocalHost(), var2, var3);
      } else {
         this.doConnect(var1, var2, var3);
      }

   }

   public void setOption(int var1, Object var2) throws SocketException {
      if (this.isClosedOrPending()) {
         throw new SocketException("Socket Closed");
      } else {
         boolean var3 = true;
         switch(var1) {
         case 1:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad parameter for TCP_NODELAY");
            }

            var3 = (Boolean)var2;
            break;
         case 3:
            if (var2 == null || !(var2 instanceof Integer)) {
               throw new SocketException("bad argument for IP_TOS");
            }

            this.trafficClass = (Integer)var2;
            break;
         case 4:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad parameter for SO_REUSEADDR");
            }

            var3 = (Boolean)var2;
            break;
         case 8:
            if (var2 != null && var2 instanceof Boolean) {
               var3 = (Boolean)var2;
               break;
            }

            throw new SocketException("bad parameter for SO_KEEPALIVE");
         case 15:
            throw new SocketException("Cannot re-bind socket");
         case 128:
            if (var2 == null || !(var2 instanceof Integer) && !(var2 instanceof Boolean)) {
               throw new SocketException("Bad parameter for option");
            }

            if (var2 instanceof Boolean) {
               var3 = false;
            }
            break;
         case 4097:
         case 4098:
            if (var2 != null && var2 instanceof Integer && (Integer)var2 > 0) {
               break;
            }

            throw new SocketException("bad parameter for SO_SNDBUF or SO_RCVBUF");
         case 4099:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad parameter for SO_OOBINLINE");
            }

            var3 = (Boolean)var2;
            break;
         case 4102:
            if (var2 != null && var2 instanceof Integer) {
               int var4 = (Integer)var2;
               if (var4 < 0) {
                  throw new IllegalArgumentException("timeout < 0");
               }

               this.timeout = var4;
               break;
            }

            throw new SocketException("Bad parameter for SO_TIMEOUT");
         default:
            throw new SocketException("unrecognized TCP option: " + var1);
         }

         this.socketSetOption(var1, var3, var2);
      }
   }

   public Object getOption(int var1) throws SocketException {
      if (this.isClosedOrPending()) {
         throw new SocketException("Socket Closed");
      } else if (var1 == 4102) {
         return new Integer(this.timeout);
      } else {
         boolean var2 = false;
         int var6;
         switch(var1) {
         case 1:
            var6 = this.socketGetOption(var1, (Object)null);
            return var6 != -1;
         case 3:
            try {
               var6 = this.socketGetOption(var1, (Object)null);
               if (var6 == -1) {
                  return this.trafficClass;
               }

               return var6;
            } catch (SocketException var5) {
               return this.trafficClass;
            }
         case 4:
            var6 = this.socketGetOption(var1, (Object)null);
            return var6 != -1;
         case 8:
            var6 = this.socketGetOption(var1, (Object)null);
            return var6 != -1;
         case 15:
            InetAddressContainer var3 = new InetAddressContainer();
            this.socketGetOption(var1, var3);
            return var3.addr;
         case 128:
            var6 = this.socketGetOption(var1, (Object)null);
            return var6 == -1 ? Boolean.FALSE : new Integer(var6);
         case 4097:
         case 4098:
            var6 = this.socketGetOption(var1, (Object)null);
            return new Integer(var6);
         case 4099:
            var6 = this.socketGetOption(var1, (Object)null);
            return var6 != -1;
         default:
            return null;
         }
      }
   }

   synchronized void doConnect(InetAddress var1, int var2, int var3) throws IOException {
      synchronized(this.fdLock) {
         if (!this.closePending && (this.socket == null || !this.socket.isBound())) {
            NetHooks.beforeTcpConnect(this.fd, var1, var2);
         }
      }

      try {
         this.acquireFD();

         try {
            this.socketConnect(var1, var2, var3);
            synchronized(this.fdLock) {
               if (this.closePending) {
                  throw new SocketException("Socket closed");
               }
            }

            if (this.socket != null) {
               this.socket.setBound();
               this.socket.setConnected();
            }
         } finally {
            this.releaseFD();
         }

      } catch (IOException var14) {
         this.close();
         throw var14;
      }
   }

   protected synchronized void bind(InetAddress var1, int var2) throws IOException {
      synchronized(this.fdLock) {
         if (!this.closePending && (this.socket == null || !this.socket.isBound())) {
            NetHooks.beforeTcpBind(this.fd, var1, var2);
         }
      }

      this.socketBind(var1, var2);
      if (this.socket != null) {
         this.socket.setBound();
      }

      if (this.serverSocket != null) {
         this.serverSocket.setBound();
      }

   }

   protected synchronized void listen(int var1) throws IOException {
      this.socketListen(var1);
   }

   protected void accept(SocketImpl var1) throws IOException {
      this.acquireFD();

      try {
         this.socketAccept(var1);
      } finally {
         this.releaseFD();
      }

   }

   protected synchronized InputStream getInputStream() throws IOException {
      synchronized(this.fdLock) {
         if (this.isClosedOrPending()) {
            throw new IOException("Socket Closed");
         }

         if (this.shut_rd) {
            throw new IOException("Socket input is shutdown");
         }

         if (this.socketInputStream == null) {
            this.socketInputStream = new SocketInputStream(this);
         }
      }

      return this.socketInputStream;
   }

   void setInputStream(SocketInputStream var1) {
      this.socketInputStream = var1;
   }

   protected synchronized OutputStream getOutputStream() throws IOException {
      synchronized(this.fdLock) {
         if (this.isClosedOrPending()) {
            throw new IOException("Socket Closed");
         }

         if (this.shut_wr) {
            throw new IOException("Socket output is shutdown");
         }

         if (this.socketOutputStream == null) {
            this.socketOutputStream = new SocketOutputStream(this);
         }
      }

      return this.socketOutputStream;
   }

   void setFileDescriptor(FileDescriptor var1) {
      this.fd = var1;
   }

   void setAddress(InetAddress var1) {
      this.address = var1;
   }

   void setPort(int var1) {
      this.port = var1;
   }

   void setLocalPort(int var1) {
      this.localport = var1;
   }

   protected synchronized int available() throws IOException {
      if (this.isClosedOrPending()) {
         throw new IOException("Stream closed.");
      } else if (!this.isConnectionReset() && !this.shut_rd) {
         int var1 = 0;

         try {
            var1 = this.socketAvailable();
            if (var1 == 0 && this.isConnectionResetPending()) {
               this.setConnectionReset();
            }
         } catch (ConnectionResetException var5) {
            this.setConnectionResetPending();

            try {
               var1 = this.socketAvailable();
               if (var1 == 0) {
                  this.setConnectionReset();
               }
            } catch (ConnectionResetException var4) {
            }
         }

         return var1;
      } else {
         return 0;
      }
   }

   protected void close() throws IOException {
      synchronized(this.fdLock) {
         if (this.fd != null) {
            if (!this.stream) {
               ResourceManager.afterUdpClose();
            }

            if (this.fdUseCount == 0) {
               if (this.closePending) {
                  return;
               }

               this.closePending = true;

               try {
                  this.socketPreClose();
               } finally {
                  this.socketClose();
               }

               this.fd = null;
               return;
            }

            if (!this.closePending) {
               this.closePending = true;
               --this.fdUseCount;
               this.socketPreClose();
            }
         }

      }
   }

   void reset() throws IOException {
      if (this.fd != null) {
         this.socketClose();
      }

      this.fd = null;
      super.reset();
   }

   protected void shutdownInput() throws IOException {
      if (this.fd != null) {
         this.socketShutdown(0);
         if (this.socketInputStream != null) {
            this.socketInputStream.setEOF(true);
         }

         this.shut_rd = true;
      }

   }

   protected void shutdownOutput() throws IOException {
      if (this.fd != null) {
         this.socketShutdown(1);
         this.shut_wr = true;
      }

   }

   protected boolean supportsUrgentData() {
      return true;
   }

   protected void sendUrgentData(int var1) throws IOException {
      if (this.fd == null) {
         throw new IOException("Socket Closed");
      } else {
         this.socketSendUrgentData(var1);
      }
   }

   protected void finalize() throws IOException {
      this.close();
   }

   FileDescriptor acquireFD() {
      synchronized(this.fdLock) {
         ++this.fdUseCount;
         return this.fd;
      }
   }

   void releaseFD() {
      synchronized(this.fdLock) {
         --this.fdUseCount;
         if (this.fdUseCount == -1 && this.fd != null) {
            try {
               this.socketClose();
            } catch (IOException var8) {
            } finally {
               this.fd = null;
            }
         }

      }
   }

   public boolean isConnectionReset() {
      synchronized(this.resetLock) {
         return this.resetState == this.CONNECTION_RESET;
      }
   }

   public boolean isConnectionResetPending() {
      synchronized(this.resetLock) {
         return this.resetState == this.CONNECTION_RESET_PENDING;
      }
   }

   public void setConnectionReset() {
      synchronized(this.resetLock) {
         this.resetState = this.CONNECTION_RESET;
      }
   }

   public void setConnectionResetPending() {
      synchronized(this.resetLock) {
         if (this.resetState == this.CONNECTION_NOT_RESET) {
            this.resetState = this.CONNECTION_RESET_PENDING;
         }

      }
   }

   public boolean isClosedOrPending() {
      synchronized(this.fdLock) {
         return this.closePending || this.fd == null;
      }
   }

   public int getTimeout() {
      return this.timeout;
   }

   private void socketPreClose() throws IOException {
      this.socketClose0(true);
   }

   protected void socketClose() throws IOException {
      this.socketClose0(false);
   }

   abstract void socketCreate(boolean var1) throws IOException;

   abstract void socketConnect(InetAddress var1, int var2, int var3) throws IOException;

   abstract void socketBind(InetAddress var1, int var2) throws IOException;

   abstract void socketListen(int var1) throws IOException;

   abstract void socketAccept(SocketImpl var1) throws IOException;

   abstract int socketAvailable() throws IOException;

   abstract void socketClose0(boolean var1) throws IOException;

   abstract void socketShutdown(int var1) throws IOException;

   abstract void socketSetOption(int var1, boolean var2, Object var3) throws SocketException;

   abstract int socketGetOption(int var1, Object var2) throws SocketException;

   abstract void socketSendUrgentData(int var1) throws IOException;

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
   }
}
