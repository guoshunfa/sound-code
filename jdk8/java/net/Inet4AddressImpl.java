package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet4AddressImpl implements InetAddressImpl {
   private InetAddress anyLocalAddress;
   private InetAddress loopbackAddress;

   public native String getLocalHostName() throws UnknownHostException;

   public native InetAddress[] lookupAllHostAddr(String var1) throws UnknownHostException;

   public native String getHostByAddr(byte[] var1) throws UnknownHostException;

   private native boolean isReachable0(byte[] var1, int var2, byte[] var3, int var4) throws IOException;

   public synchronized InetAddress anyLocalAddress() {
      if (this.anyLocalAddress == null) {
         this.anyLocalAddress = new Inet4Address();
         this.anyLocalAddress.holder().hostName = "0.0.0.0";
      }

      return this.anyLocalAddress;
   }

   public synchronized InetAddress loopbackAddress() {
      if (this.loopbackAddress == null) {
         byte[] var1 = new byte[]{127, 0, 0, 1};
         this.loopbackAddress = new Inet4Address("localhost", var1);
      }

      return this.loopbackAddress;
   }

   public boolean isReachable(InetAddress var1, int var2, NetworkInterface var3, int var4) throws IOException {
      byte[] var5 = null;
      if (var3 != null) {
         Enumeration var6 = var3.getInetAddresses();

         InetAddress var7;
         for(var7 = null; !(var7 instanceof Inet4Address) && var6.hasMoreElements(); var7 = (InetAddress)var6.nextElement()) {
         }

         if (var7 instanceof Inet4Address) {
            var5 = var7.getAddress();
         }
      }

      return this.isReachable0(var1.getAddress(), var2, var5, var4);
   }
}
