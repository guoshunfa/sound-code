package java.net;

import java.util.Formatter;
import java.util.Locale;
import sun.net.util.IPAddressUtil;

class HostPortrange {
   String hostname;
   String scheme;
   int[] portrange;
   boolean wildcard;
   boolean literal;
   boolean ipv6;
   boolean ipv4;
   static final int PORT_MIN = 0;
   static final int PORT_MAX = 65535;
   static final int CASE_DIFF = -32;
   static final int[] HTTP_PORT = new int[]{80, 80};
   static final int[] HTTPS_PORT = new int[]{443, 443};
   static final int[] NO_PORT = new int[]{-1, -1};

   boolean equals(HostPortrange var1) {
      return this.hostname.equals(var1.hostname) && this.portrange[0] == var1.portrange[0] && this.portrange[1] == var1.portrange[1] && this.wildcard == var1.wildcard && this.literal == var1.literal;
   }

   public int hashCode() {
      return this.hostname.hashCode() + this.portrange[0] + this.portrange[1];
   }

   HostPortrange(String var1, String var2) {
      String var4 = null;
      this.scheme = var1;
      String var3;
      int var5;
      int var6;
      if (var2.charAt(0) == '[') {
         this.ipv6 = this.literal = true;
         var5 = var2.indexOf(93);
         if (var5 == -1) {
            throw new IllegalArgumentException("invalid IPv6 address: " + var2);
         }

         var3 = var2.substring(1, var5);
         var6 = var2.indexOf(58, var5 + 1);
         if (var6 != -1 && var2.length() > var6) {
            var4 = var2.substring(var6 + 1);
         }

         byte[] var7 = IPAddressUtil.textToNumericFormatV6(var3);
         if (var7 == null) {
            throw new IllegalArgumentException("illegal IPv6 address");
         }

         StringBuilder var8 = new StringBuilder();
         Formatter var9 = new Formatter(var8, Locale.US);
         var9.format("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", var7[0], var7[1], var7[2], var7[3], var7[4], var7[5], var7[6], var7[7], var7[8], var7[9], var7[10], var7[11], var7[12], var7[13], var7[14], var7[15]);
         this.hostname = var8.toString();
      } else {
         var5 = var2.indexOf(58);
         if (var5 != -1 && var2.length() > var5) {
            var3 = var2.substring(0, var5);
            var4 = var2.substring(var5 + 1);
         } else {
            var3 = var5 == -1 ? var2 : var2.substring(0, var5);
         }

         if (var3.lastIndexOf(42) > 0) {
            throw new IllegalArgumentException("invalid host wildcard specification");
         }

         if (var3.startsWith("*")) {
            this.wildcard = true;
            if (var3.equals("*")) {
               var3 = "";
            } else {
               if (!var3.startsWith("*.")) {
                  throw new IllegalArgumentException("invalid host wildcard specification");
               }

               var3 = toLowerCase(var3.substring(1));
            }
         } else {
            var6 = var3.lastIndexOf(46);
            if (var6 != -1 && var3.length() > 1) {
               boolean var12 = true;
               int var13 = var6 + 1;

               for(int var15 = var3.length(); var13 < var15; ++var13) {
                  char var10 = var3.charAt(var13);
                  if (var10 < '0' || var10 > '9') {
                     var12 = false;
                     break;
                  }
               }

               this.ipv4 = this.literal = var12;
               if (var12) {
                  byte[] var14 = IPAddressUtil.textToNumericFormatV4(var3);
                  if (var14 == null) {
                     throw new IllegalArgumentException("illegal IPv4 address");
                  }

                  StringBuilder var16 = new StringBuilder();
                  Formatter var17 = new Formatter(var16, Locale.US);
                  var17.format("%d.%d.%d.%d", var14[0], var14[1], var14[2], var14[3]);
                  var3 = var16.toString();
               } else {
                  var3 = toLowerCase(var3);
               }
            }
         }

         this.hostname = var3;
      }

      try {
         this.portrange = this.parsePort(var4);
      } catch (Exception var11) {
         throw new IllegalArgumentException("invalid port range: " + var4);
      }
   }

   static String toLowerCase(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = null;

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if ((var4 < 'a' || var4 > 'z') && var4 != '.') {
            if ((var4 < '0' || var4 > '9') && var4 != '-') {
               if (var4 < 'A' || var4 > 'Z') {
                  throw new IllegalArgumentException("Invalid characters in hostname");
               }

               if (var2 == null) {
                  var2 = new StringBuilder(var1);
                  var2.append((CharSequence)var0, 0, var3);
               }

               var2.append((char)(var4 - -32));
            } else if (var2 != null) {
               var2.append(var4);
            }
         } else if (var2 != null) {
            var2.append(var4);
         }
      }

      return var2 == null ? var0 : var2.toString();
   }

   public boolean literal() {
      return this.literal;
   }

   public boolean ipv4Literal() {
      return this.ipv4;
   }

   public boolean ipv6Literal() {
      return this.ipv6;
   }

   public String hostname() {
      return this.hostname;
   }

   public int[] portrange() {
      return this.portrange;
   }

   public boolean wildcard() {
      return this.wildcard;
   }

   int[] defaultPort() {
      if (this.scheme.equals("http")) {
         return HTTP_PORT;
      } else {
         return this.scheme.equals("https") ? HTTPS_PORT : NO_PORT;
      }
   }

   int[] parsePort(String var1) {
      if (var1 != null && !var1.equals("")) {
         if (var1.equals("*")) {
            return new int[]{0, 65535};
         } else {
            try {
               int var2 = var1.indexOf(45);
               if (var2 == -1) {
                  int var8 = Integer.parseInt(var1);
                  return new int[]{var8, var8};
               } else {
                  String var3 = var1.substring(0, var2);
                  String var4 = var1.substring(var2 + 1);
                  int var5;
                  if (var3.equals("")) {
                     var5 = 0;
                  } else {
                     var5 = Integer.parseInt(var3);
                  }

                  int var6;
                  if (var4.equals("")) {
                     var6 = 65535;
                  } else {
                     var6 = Integer.parseInt(var4);
                  }

                  return var5 >= 0 && var6 >= 0 && var6 >= var5 ? new int[]{var5, var6} : this.defaultPort();
               }
            } catch (IllegalArgumentException var7) {
               return this.defaultPort();
            }
         }
      } else {
         return this.defaultPort();
      }
   }
}
