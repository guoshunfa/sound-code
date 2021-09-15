package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ResourceManager;
import sun.security.action.GetPropertyAction;

abstract class AbstractPlainDatagramSocketImpl extends DatagramSocketImpl {
   int timeout = 0;
   boolean connected = false;
   private int trafficClass = 0;
   protected InetAddress connectedAddress = null;
   private int connectedPort = -1;
   private static final String os = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
   private static final boolean connectDisabled;

   protected synchronized void create() throws SocketException {
      ResourceManager.beforeUdpCreate();
      this.fd = new FileDescriptor();

      try {
         this.datagramSocketCreate();
      } catch (SocketException var2) {
         ResourceManager.afterUdpClose();
         this.fd = null;
         throw var2;
      }
   }

   protected synchronized void bind(int var1, InetAddress var2) throws SocketException {
      this.bind0(var1, var2);
   }

   protected abstract void bind0(int var1, InetAddress var2) throws SocketException;

   protected abstract void send(DatagramPacket var1) throws IOException;

   protected void connect(InetAddress var1, int var2) throws SocketException {
      this.connect0(var1, var2);
      this.connectedAddress = var1;
      this.connectedPort = var2;
      this.connected = true;
   }

   protected void disconnect() {
      this.disconnect0(this.connectedAddress.holder().getFamily());
      this.connected = false;
      this.connectedAddress = null;
      this.connectedPort = -1;
   }

   protected abstract int peek(InetAddress var1) throws IOException;

   protected abstract int peekData(DatagramPacket var1) throws IOException;

   protected synchronized void receive(DatagramPacket var1) throws IOException {
      this.receive0(var1);
   }

   protected abstract void receive0(DatagramPacket var1) throws IOException;

   protected abstract void setTimeToLive(int var1) throws IOException;

   protected abstract int getTimeToLive() throws IOException;

   /** @deprecated */
   @Deprecated
   protected abstract void setTTL(byte var1) throws IOException;

   /** @deprecated */
   @Deprecated
   protected abstract byte getTTL() throws IOException;

   protected void join(InetAddress var1) throws IOException {
      this.join(var1, (NetworkInterface)null);
   }

   protected void leave(InetAddress var1) throws IOException {
      this.leave(var1, (NetworkInterface)null);
   }

   protected void joinGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      if (var1 != null && var1 instanceof InetSocketAddress) {
         this.join(((InetSocketAddress)var1).getAddress(), var2);
      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   protected abstract void join(InetAddress var1, NetworkInterface var2) throws IOException;

   protected void leaveGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      if (var1 != null && var1 instanceof InetSocketAddress) {
         this.leave(((InetSocketAddress)var1).getAddress(), var2);
      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   protected abstract void leave(InetAddress var1, NetworkInterface var2) throws IOException;

   protected void close() {
      if (this.fd != null) {
         this.datagramSocketClose();
         ResourceManager.afterUdpClose();
         this.fd = null;
      }

   }

   protected boolean isClosed() {
      return this.fd == null;
   }

   protected void finalize() {
      this.close();
   }

   public void setOption(int var1, Object var2) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket Closed");
      } else {
         switch(var1) {
         case 3:
            if (var2 != null && var2 instanceof Integer) {
               this.trafficClass = (Integer)var2;
               break;
            }

            throw new SocketException("bad argument for IP_TOS");
         case 4:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad argument for SO_REUSEADDR");
            }
            break;
         case 15:
            throw new SocketException("Cannot re-bind Socket");
         case 16:
            if (var2 != null && var2 instanceof InetAddress) {
               break;
            }

            throw new SocketException("bad argument for IP_MULTICAST_IF");
         case 18:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad argument for IP_MULTICAST_LOOP");
            }
            break;
         case 31:
            if (var2 != null && var2 instanceof NetworkInterface) {
               break;
            }

            throw new SocketException("bad argument for IP_MULTICAST_IF2");
         case 32:
            if (var2 == null || !(var2 instanceof Boolean)) {
               throw new SocketException("bad argument for SO_BROADCAST");
            }
            break;
         case 4097:
         case 4098:
            if (var2 != null && var2 instanceof Integer && (Integer)var2 >= 0) {
               break;
            }

            throw new SocketException("bad argument for SO_SNDBUF or SO_RCVBUF");
         case 4102:
            if (var2 != null && var2 instanceof Integer) {
               int var3 = (Integer)var2;
               if (var3 < 0) {
                  throw new IllegalArgumentException("timeout < 0");
               }

               this.timeout = var3;
               return;
            }

            throw new SocketException("bad argument for SO_TIMEOUT");
         default:
            throw new SocketException("invalid option: " + var1);
         }

         this.socketSetOption(var1, var2);
      }
   }

   public Object getOption(int var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket Closed");
      } else {
         Object var2;
         switch(var1) {
         case 3:
            var2 = this.socketGetOption(var1);
            if ((Integer)var2 == -1) {
               var2 = new Integer(this.trafficClass);
            }
            break;
         case 4:
         case 15:
         case 16:
         case 18:
         case 31:
         case 32:
         case 4097:
         case 4098:
            var2 = this.socketGetOption(var1);
            break;
         case 4102:
            var2 = new Integer(this.timeout);
            break;
         default:
            throw new SocketException("invalid option: " + var1);
         }

         return var2;
      }
   }

   protected abstract void datagramSocketCreate() throws SocketException;

   protected abstract void datagramSocketClose();

   protected abstract void socketSetOption(int var1, Object var2) throws SocketException;

   protected abstract Object socketGetOption(int var1) throws SocketException;

   protected abstract void connect0(InetAddress var1, int var2) throws SocketException;

   protected abstract void disconnect0(int var1);

   protected boolean nativeConnectDisabled() {
      return connectDisabled;
   }

   abstract int dataAvailable();

   static {
      connectDisabled = os.contains("OS X");
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
   }
}
