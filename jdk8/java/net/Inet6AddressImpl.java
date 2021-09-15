package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet6AddressImpl implements InetAddressImpl {
   private InetAddress anyLocalAddress;
   private InetAddress loopbackAddress;

   public native String getLocalHostName() throws UnknownHostException;

   public native InetAddress[] lookupAllHostAddr(String var1) throws UnknownHostException;

   public native String getHostByAddr(byte[] var1) throws UnknownHostException;

   private native boolean isReachable0(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) throws IOException;

   public boolean isReachable(InetAddress var1, int var2, NetworkInterface var3, int var4) throws IOException {
      byte[] var5 = null;
      int var6 = -1;
      int var7 = -1;
      if (var3 != null) {
         Enumeration var8 = var3.getInetAddresses();
         InetAddress var9 = null;

         while(var8.hasMoreElements()) {
            var9 = (InetAddress)var8.nextElement();
            if (var9.getClass().isInstance(var1)) {
               var5 = var9.getAddress();
               if (var9 instanceof Inet6Address) {
                  var7 = ((Inet6Address)var9).getScopeId();
               }
               break;
            }
         }

         if (var5 == null) {
            return false;
         }
      }

      if (var1 instanceof Inet6Address) {
         var6 = ((Inet6Address)var1).getScopeId();
      }

      return this.isReachable0(var1.getAddress(), var6, var2, var5, var4, var7);
   }

   public synchronized InetAddress anyLocalAddress() {
      if (this.anyLocalAddress == null) {
         if (InetAddress.preferIPv6Address) {
            this.anyLocalAddress = new Inet6Address();
            this.anyLocalAddress.holder().hostName = "::";
         } else {
            this.anyLocalAddress = (new Inet4AddressImpl()).anyLocalAddress();
         }
      }

      return this.anyLocalAddress;
   }

   public synchronized InetAddress loopbackAddress() {
      if (this.loopbackAddress == null) {
         if (InetAddress.preferIPv6Address) {
            byte[] var1 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
            this.loopbackAddress = new Inet6Address("localhost", var1);
         } else {
            this.loopbackAddress = (new Inet4AddressImpl()).loopbackAddress();
         }
      }

      return this.loopbackAddress;
   }
}
