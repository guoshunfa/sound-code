package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class DatagramSocket implements Closeable {
   private boolean created;
   private boolean bound;
   private boolean closed;
   private Object closeLock;
   DatagramSocketImpl impl;
   boolean oldImpl;
   private boolean explicitFilter;
   private int bytesLeftToFilter;
   static final int ST_NOT_CONNECTED = 0;
   static final int ST_CONNECTED = 1;
   static final int ST_CONNECTED_NO_IMPL = 2;
   int connectState;
   InetAddress connectedAddress;
   int connectedPort;
   static Class<?> implClass = null;
   static DatagramSocketImplFactory factory;

   private synchronized void connectInternal(InetAddress var1, int var2) throws SocketException {
      if (var2 >= 0 && var2 <= 65535) {
         if (var1 == null) {
            throw new IllegalArgumentException("connect: null address");
         } else {
            this.checkAddress(var1, "connect");
            if (!this.isClosed()) {
               SecurityManager var3 = System.getSecurityManager();
               if (var3 != null) {
                  if (var1.isMulticastAddress()) {
                     var3.checkMulticast(var1);
                  } else {
                     var3.checkConnect(var1.getHostAddress(), var2);
                     var3.checkAccept(var1.getHostAddress(), var2);
                  }
               }

               if (!this.isBound()) {
                  this.bind(new InetSocketAddress(0));
               }

               if (!this.oldImpl && (!(this.impl instanceof AbstractPlainDatagramSocketImpl) || !((AbstractPlainDatagramSocketImpl)this.impl).nativeConnectDisabled())) {
                  try {
                     this.getImpl().connect(var1, var2);
                     this.connectState = 1;
                     int var4 = this.getImpl().dataAvailable();
                     if (var4 == -1) {
                        throw new SocketException();
                     }

                     this.explicitFilter = var4 > 0;
                     if (this.explicitFilter) {
                        this.bytesLeftToFilter = this.getReceiveBufferSize();
                     }
                  } catch (SocketException var5) {
                     this.connectState = 2;
                  }
               } else {
                  this.connectState = 2;
               }

               this.connectedAddress = var1;
               this.connectedPort = var2;
            }
         }
      } else {
         throw new IllegalArgumentException("connect: " + var2);
      }
   }

   public DatagramSocket() throws SocketException {
      this((SocketAddress)(new InetSocketAddress(0)));
   }

   protected DatagramSocket(DatagramSocketImpl var1) {
      this.created = false;
      this.bound = false;
      this.closed = false;
      this.closeLock = new Object();
      this.oldImpl = false;
      this.explicitFilter = false;
      this.connectState = 0;
      this.connectedAddress = null;
      this.connectedPort = -1;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.impl = var1;
         this.checkOldImpl();
      }
   }

   public DatagramSocket(SocketAddress var1) throws SocketException {
      this.created = false;
      this.bound = false;
      this.closed = false;
      this.closeLock = new Object();
      this.oldImpl = false;
      this.explicitFilter = false;
      this.connectState = 0;
      this.connectedAddress = null;
      this.connectedPort = -1;
      this.createImpl();
      if (var1 != null) {
         try {
            this.bind(var1);
         } finally {
            if (!this.isBound()) {
               this.close();
            }

         }
      }

   }

   public DatagramSocket(int var1) throws SocketException {
      this(var1, (InetAddress)null);
   }

   public DatagramSocket(int var1, InetAddress var2) throws SocketException {
      this((SocketAddress)(new InetSocketAddress(var2, var1)));
   }

   private void checkOldImpl() {
      if (this.impl != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws NoSuchMethodException {
                  Class[] var1 = new Class[]{DatagramPacket.class};
                  DatagramSocket.this.impl.getClass().getDeclaredMethod("peekData", var1);
                  return null;
               }
            });
         } catch (PrivilegedActionException var2) {
            this.oldImpl = true;
         }

      }
   }

   void createImpl() throws SocketException {
      if (this.impl == null) {
         if (factory != null) {
            this.impl = factory.createDatagramSocketImpl();
            this.checkOldImpl();
         } else {
            boolean var1 = this instanceof MulticastSocket;
            this.impl = DefaultDatagramSocketImplFactory.createDatagramSocketImpl(var1);
            this.checkOldImpl();
         }
      }

      this.impl.create();
      this.impl.setDatagramSocket(this);
      this.created = true;
   }

   DatagramSocketImpl getImpl() throws SocketException {
      if (!this.created) {
         this.createImpl();
      }

      return this.impl;
   }

   public synchronized void bind(SocketAddress var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (this.isBound()) {
         throw new SocketException("already bound");
      } else {
         if (var1 == null) {
            var1 = new InetSocketAddress(0);
         }

         if (!(var1 instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type!");
         } else {
            InetSocketAddress var2 = (InetSocketAddress)var1;
            if (var2.isUnresolved()) {
               throw new SocketException("Unresolved address");
            } else {
               InetAddress var3 = var2.getAddress();
               int var4 = var2.getPort();
               this.checkAddress(var3, "bind");
               SecurityManager var5 = System.getSecurityManager();
               if (var5 != null) {
                  var5.checkListen(var4);
               }

               try {
                  this.getImpl().bind(var4, var3);
               } catch (SocketException var7) {
                  this.getImpl().close();
                  throw var7;
               }

               this.bound = true;
            }
         }
      }
   }

   void checkAddress(InetAddress var1, String var2) {
      if (var1 != null) {
         if (!(var1 instanceof Inet4Address) && !(var1 instanceof Inet6Address)) {
            throw new IllegalArgumentException(var2 + ": invalid address type");
         }
      }
   }

   public void connect(InetAddress var1, int var2) {
      try {
         this.connectInternal(var1, var2);
      } catch (SocketException var4) {
         throw new Error("connect failed", var4);
      }
   }

   public void connect(SocketAddress var1) throws SocketException {
      if (var1 == null) {
         throw new IllegalArgumentException("Address can't be null");
      } else if (!(var1 instanceof InetSocketAddress)) {
         throw new IllegalArgumentException("Unsupported address type");
      } else {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         if (var2.isUnresolved()) {
            throw new SocketException("Unresolved address");
         } else {
            this.connectInternal(var2.getAddress(), var2.getPort());
         }
      }
   }

   public void disconnect() {
      synchronized(this) {
         if (!this.isClosed()) {
            if (this.connectState == 1) {
               this.impl.disconnect();
            }

            this.connectedAddress = null;
            this.connectedPort = -1;
            this.connectState = 0;
            this.explicitFilter = false;
         }
      }
   }

   public boolean isBound() {
      return this.bound;
   }

   public boolean isConnected() {
      return this.connectState != 0;
   }

   public InetAddress getInetAddress() {
      return this.connectedAddress;
   }

   public int getPort() {
      return this.connectedPort;
   }

   public SocketAddress getRemoteSocketAddress() {
      return !this.isConnected() ? null : new InetSocketAddress(this.getInetAddress(), this.getPort());
   }

   public SocketAddress getLocalSocketAddress() {
      if (this.isClosed()) {
         return null;
      } else {
         return !this.isBound() ? null : new InetSocketAddress(this.getLocalAddress(), this.getLocalPort());
      }
   }

   public void send(DatagramPacket var1) throws IOException {
      InetAddress var2 = null;
      synchronized(var1) {
         if (this.isClosed()) {
            throw new SocketException("Socket is closed");
         } else {
            this.checkAddress(var1.getAddress(), "send");
            if (this.connectState == 0) {
               SecurityManager var4 = System.getSecurityManager();
               if (var4 != null) {
                  if (var1.getAddress().isMulticastAddress()) {
                     var4.checkMulticast(var1.getAddress());
                  } else {
                     var4.checkConnect(var1.getAddress().getHostAddress(), var1.getPort());
                  }
               }
            } else {
               var2 = var1.getAddress();
               if (var2 == null) {
                  var1.setAddress(this.connectedAddress);
                  var1.setPort(this.connectedPort);
               } else if (!var2.equals(this.connectedAddress) || var1.getPort() != this.connectedPort) {
                  throw new IllegalArgumentException("connected address and packet address differ");
               }
            }

            if (!this.isBound()) {
               this.bind(new InetSocketAddress(0));
            }

            this.getImpl().send(var1);
         }
      }
   }

   public synchronized void receive(DatagramPacket var1) throws IOException {
      synchronized(var1) {
         if (!this.isBound()) {
            this.bind(new InetSocketAddress(0));
         }

         DatagramPacket var7;
         if (this.connectState == 0) {
            SecurityManager var3 = System.getSecurityManager();
            if (var3 != null) {
               while(true) {
                  String var4 = null;
                  boolean var5 = false;
                  int var13;
                  if (!this.oldImpl) {
                     DatagramPacket var6 = new DatagramPacket(new byte[1], 1);
                     var13 = this.getImpl().peekData(var6);
                     var4 = var6.getAddress().getHostAddress();
                  } else {
                     InetAddress var14 = new InetAddress();
                     var13 = this.getImpl().peek(var14);
                     var4 = var14.getHostAddress();
                  }

                  try {
                     var3.checkAccept(var4, var13);
                     break;
                  } catch (SecurityException var9) {
                     var7 = new DatagramPacket(new byte[1], 1);
                     this.getImpl().receive(var7);
                  }
               }
            }
         }

         DatagramPacket var11 = null;
         if (this.connectState == 2 || this.explicitFilter) {
            boolean var12 = false;

            label58:
            while(true) {
               while(true) {
                  if (var12) {
                     break label58;
                  }

                  InetAddress var15 = null;
                  boolean var16 = true;
                  int var17;
                  if (!this.oldImpl) {
                     var7 = new DatagramPacket(new byte[1], 1);
                     var17 = this.getImpl().peekData(var7);
                     var15 = var7.getAddress();
                  } else {
                     var15 = new InetAddress();
                     var17 = this.getImpl().peek(var15);
                  }

                  if (this.connectedAddress.equals(var15) && this.connectedPort == var17) {
                     var12 = true;
                  } else {
                     var11 = new DatagramPacket(new byte[1024], 1024);
                     this.getImpl().receive(var11);
                     if (this.explicitFilter && this.checkFiltering(var11)) {
                        var12 = true;
                     }
                  }
               }
            }
         }

         this.getImpl().receive(var1);
         if (this.explicitFilter && var11 == null) {
            this.checkFiltering(var1);
         }

      }
   }

   private boolean checkFiltering(DatagramPacket var1) throws SocketException {
      this.bytesLeftToFilter -= var1.getLength();
      if (this.bytesLeftToFilter > 0 && this.getImpl().dataAvailable() > 0) {
         return false;
      } else {
         this.explicitFilter = false;
         return true;
      }
   }

   public InetAddress getLocalAddress() {
      if (this.isClosed()) {
         return null;
      } else {
         InetAddress var1 = null;

         try {
            var1 = (InetAddress)this.getImpl().getOption(15);
            if (var1.isAnyLocalAddress()) {
               var1 = InetAddress.anyLocalAddress();
            }

            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkConnect(var1.getHostAddress(), -1);
            }
         } catch (Exception var3) {
            var1 = InetAddress.anyLocalAddress();
         }

         return var1;
      }
   }

   public int getLocalPort() {
      if (this.isClosed()) {
         return -1;
      } else {
         try {
            return this.getImpl().getLocalPort();
         } catch (Exception var2) {
            return 0;
         }
      }
   }

   public synchronized void setSoTimeout(int var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(4102, new Integer(var1));
      }
   }

   public synchronized int getSoTimeout() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (this.getImpl() == null) {
         return 0;
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

   public synchronized void setReuseAddress(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         if (this.oldImpl) {
            this.getImpl().setOption(4, new Integer(var1 ? -1 : 0));
         } else {
            this.getImpl().setOption(4, var1);
         }

      }
   }

   public synchronized boolean getReuseAddress() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         Object var1 = this.getImpl().getOption(4);
         return (Boolean)var1;
      }
   }

   public synchronized void setBroadcast(boolean var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setOption(32, var1);
      }
   }

   public synchronized boolean getBroadcast() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Boolean)((Boolean)this.getImpl().getOption(32));
      }
   }

   public synchronized void setTrafficClass(int var1) throws SocketException {
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

   public synchronized int getTrafficClass() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return (Integer)((Integer)this.getImpl().getOption(3));
      }
   }

   public void close() {
      synchronized(this.closeLock) {
         if (!this.isClosed()) {
            this.impl.close();
            this.closed = true;
         }
      }
   }

   public boolean isClosed() {
      synchronized(this.closeLock) {
         return this.closed;
      }
   }

   public DatagramChannel getChannel() {
      return null;
   }

   public static synchronized void setDatagramSocketImplFactory(DatagramSocketImplFactory var0) throws IOException {
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
}
