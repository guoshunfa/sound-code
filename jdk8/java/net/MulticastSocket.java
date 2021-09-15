package java.net;

import java.io.IOException;
import java.util.Enumeration;

public class MulticastSocket extends DatagramSocket {
   private boolean interfaceSet;
   private Object ttlLock;
   private Object infLock;
   private InetAddress infAddress;

   public MulticastSocket() throws IOException {
      this(new InetSocketAddress(0));
   }

   public MulticastSocket(int var1) throws IOException {
      this(new InetSocketAddress(var1));
   }

   public MulticastSocket(SocketAddress var1) throws IOException {
      super((SocketAddress)null);
      this.ttlLock = new Object();
      this.infLock = new Object();
      this.infAddress = null;
      this.setReuseAddress(true);
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

   /** @deprecated */
   @Deprecated
   public void setTTL(byte var1) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.getImpl().setTTL(var1);
      }
   }

   public void setTimeToLive(int var1) throws IOException {
      if (var1 >= 0 && var1 <= 255) {
         if (this.isClosed()) {
            throw new SocketException("Socket is closed");
         } else {
            this.getImpl().setTimeToLive(var1);
         }
      } else {
         throw new IllegalArgumentException("ttl out of range");
      }
   }

   /** @deprecated */
   @Deprecated
   public byte getTTL() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return this.getImpl().getTTL();
      }
   }

   public int getTimeToLive() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         return this.getImpl().getTimeToLive();
      }
   }

   public void joinGroup(InetAddress var1) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.checkAddress(var1, "joinGroup");
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkMulticast(var1);
         }

         if (!var1.isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
         } else {
            NetworkInterface var3 = NetworkInterface.getDefault();
            if (!this.interfaceSet && var3 != null) {
               this.setNetworkInterface(var3);
            }

            this.getImpl().join(var1);
         }
      }
   }

   public void leaveGroup(InetAddress var1) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.checkAddress(var1, "leaveGroup");
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkMulticast(var1);
         }

         if (!var1.isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
         } else {
            this.getImpl().leave(var1);
         }
      }
   }

   public void joinGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (var1 != null && var1 instanceof InetSocketAddress) {
         if (this.oldImpl) {
            throw new UnsupportedOperationException();
         } else {
            this.checkAddress(((InetSocketAddress)var1).getAddress(), "joinGroup");
            SecurityManager var3 = System.getSecurityManager();
            if (var3 != null) {
               var3.checkMulticast(((InetSocketAddress)var1).getAddress());
            }

            if (!((InetSocketAddress)var1).getAddress().isMulticastAddress()) {
               throw new SocketException("Not a multicast address");
            } else {
               this.getImpl().joinGroup(var1, var2);
            }
         }
      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   public void leaveGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (var1 != null && var1 instanceof InetSocketAddress) {
         if (this.oldImpl) {
            throw new UnsupportedOperationException();
         } else {
            this.checkAddress(((InetSocketAddress)var1).getAddress(), "leaveGroup");
            SecurityManager var3 = System.getSecurityManager();
            if (var3 != null) {
               var3.checkMulticast(((InetSocketAddress)var1).getAddress());
            }

            if (!((InetSocketAddress)var1).getAddress().isMulticastAddress()) {
               throw new SocketException("Not a multicast address");
            } else {
               this.getImpl().leaveGroup(var1, var2);
            }
         }
      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   public void setInterface(InetAddress var1) throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.checkAddress(var1, "setInterface");
         synchronized(this.infLock) {
            this.getImpl().setOption(16, var1);
            this.infAddress = var1;
            this.interfaceSet = true;
         }
      }
   }

   public InetAddress getInterface() throws SocketException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         synchronized(this.infLock) {
            InetAddress var2 = (InetAddress)this.getImpl().getOption(16);
            if (this.infAddress == null) {
               return var2;
            } else if (var2.equals(this.infAddress)) {
               return var2;
            } else {
               InetAddress var10000;
               try {
                  NetworkInterface var3 = NetworkInterface.getByInetAddress(var2);
                  Enumeration var4 = var3.getInetAddresses();

                  InetAddress var5;
                  do {
                     if (!var4.hasMoreElements()) {
                        this.infAddress = null;
                        var10000 = var2;
                        return var10000;
                     }

                     var5 = (InetAddress)var4.nextElement();
                  } while(!var5.equals(this.infAddress));

                  var10000 = this.infAddress;
               } catch (Exception var7) {
                  return var2;
               }

               return var10000;
            }
         }
      }
   }

   public void setNetworkInterface(NetworkInterface var1) throws SocketException {
      synchronized(this.infLock) {
         this.getImpl().setOption(31, var1);
         this.infAddress = null;
         this.interfaceSet = true;
      }
   }

   public NetworkInterface getNetworkInterface() throws SocketException {
      NetworkInterface var1 = (NetworkInterface)this.getImpl().getOption(31);
      if (var1.getIndex() != 0 && var1.getIndex() != -1) {
         return var1;
      } else {
         InetAddress[] var2 = new InetAddress[]{InetAddress.anyLocalAddress()};
         return new NetworkInterface(var2[0].getHostName(), 0, var2);
      }
   }

   public void setLoopbackMode(boolean var1) throws SocketException {
      this.getImpl().setOption(18, var1);
   }

   public boolean getLoopbackMode() throws SocketException {
      return (Boolean)this.getImpl().getOption(18);
   }

   /** @deprecated */
   @Deprecated
   public void send(DatagramPacket var1, byte var2) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         this.checkAddress(var1.getAddress(), "send");
         synchronized(this.ttlLock) {
            synchronized(var1) {
               if (this.connectState == 0) {
                  SecurityManager var15 = System.getSecurityManager();
                  if (var15 != null) {
                     if (var1.getAddress().isMulticastAddress()) {
                        var15.checkMulticast(var1.getAddress(), var2);
                     } else {
                        var15.checkConnect(var1.getAddress().getHostAddress(), var1.getPort());
                     }
                  }
               } else {
                  InetAddress var5 = null;
                  var5 = var1.getAddress();
                  if (var5 == null) {
                     var1.setAddress(this.connectedAddress);
                     var1.setPort(this.connectedPort);
                  } else if (!var5.equals(this.connectedAddress) || var1.getPort() != this.connectedPort) {
                     throw new SecurityException("connected address and packet address differ");
                  }
               }

               byte var16 = this.getTTL();

               try {
                  if (var2 != var16) {
                     this.getImpl().setTTL(var2);
                  }

                  this.getImpl().send(var1);
               } finally {
                  if (var2 != var16) {
                     this.getImpl().setTTL(var16);
                  }

               }
            }

         }
      }
   }
}
