package sun.management.snmp.jvminstr;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NotificationTargetImpl implements NotificationTarget {
   private InetAddress address;
   private int port;
   private String community;

   public NotificationTargetImpl(String var1) throws IllegalArgumentException, UnknownHostException {
      this.parseTarget(var1);
   }

   public NotificationTargetImpl(String var1, int var2, String var3) throws UnknownHostException {
      this(InetAddress.getByName(var1), var2, var3);
   }

   public NotificationTargetImpl(InetAddress var1, int var2, String var3) {
      this.address = var1;
      this.port = var2;
      this.community = var3;
   }

   private void parseTarget(String var1) throws IllegalArgumentException, UnknownHostException {
      if (var1 != null && var1.length() != 0) {
         String var2;
         int var3;
         int var4;
         if (var1.startsWith("[")) {
            var3 = var1.indexOf("]");
            var4 = var1.lastIndexOf(":");
            if (var3 == -1) {
               throw new IllegalArgumentException("Host starts with [ but does not end with ]");
            }

            var2 = var1.substring(1, var3);
            this.port = Integer.parseInt(var1.substring(var3 + 2, var4));
            if (!isNumericIPv6Address(var2)) {
               throw new IllegalArgumentException("Address inside [...] must be numeric IPv6 address");
            }

            if (var2.startsWith("[")) {
               throw new IllegalArgumentException("More than one [[...]]");
            }
         } else {
            var3 = var1.indexOf(":");
            var4 = var1.lastIndexOf(":");
            if (var3 == -1) {
               throw new IllegalArgumentException("Missing port separator \":\"");
            }

            var2 = var1.substring(0, var3);
            this.port = Integer.parseInt(var1.substring(var3 + 1, var4));
         }

         this.address = InetAddress.getByName(var2);
         var3 = var1.lastIndexOf(":");
         this.community = var1.substring(var3 + 1, var1.length());
      } else {
         throw new IllegalArgumentException("Invalid target [" + var1 + "]");
      }
   }

   private static boolean isNumericIPv6Address(String var0) {
      return var0.indexOf(58) >= 0;
   }

   public String getCommunity() {
      return this.community;
   }

   public InetAddress getAddress() {
      return this.address;
   }

   public int getPort() {
      return this.port;
   }

   public String toString() {
      return "address : " + this.address + " port : " + this.port + " community : " + this.community;
   }
}
