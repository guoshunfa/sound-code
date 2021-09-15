package java.net;

import java.io.IOException;
import sun.net.util.IPAddressUtil;

public abstract class URLStreamHandler {
   protected abstract URLConnection openConnection(URL var1) throws IOException;

   protected URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      throw new UnsupportedOperationException("Method not implemented.");
   }

   protected void parseURL(URL var1, String var2, int var3, int var4) {
      String var5 = var1.getProtocol();
      String var6 = var1.getAuthority();
      String var7 = var1.getUserInfo();
      String var8 = var1.getHost();
      int var9 = var1.getPort();
      String var10 = var1.getPath();
      String var11 = var1.getQuery();
      String var12 = var1.getRef();
      boolean var13 = false;
      boolean var14 = false;
      int var15;
      if (var3 < var4) {
         var15 = var2.indexOf(63);
         var14 = var15 == var3;
         if (var15 != -1 && var15 < var4) {
            var11 = var2.substring(var15 + 1, var4);
            if (var4 > var15) {
               var4 = var15;
            }

            var2 = var2.substring(0, var15);
         }
      }

      boolean var19 = false;
      boolean var16 = var3 <= var4 - 4 && var2.charAt(var3) == '/' && var2.charAt(var3 + 1) == '/' && var2.charAt(var3 + 2) == '/' && var2.charAt(var3 + 3) == '/';
      int var17;
      String var18;
      if (!var16 && var3 <= var4 - 2 && var2.charAt(var3) == '/' && var2.charAt(var3 + 1) == '/') {
         var3 += 2;
         var15 = var2.indexOf(47, var3);
         if (var15 < 0 || var15 > var4) {
            var15 = var2.indexOf(63, var3);
            if (var15 < 0 || var15 > var4) {
               var15 = var4;
            }
         }

         var8 = var6 = var2.substring(var3, var15);
         var17 = var6.indexOf(64);
         if (var17 != -1) {
            if (var17 != var6.lastIndexOf(64)) {
               var7 = null;
               var8 = null;
            } else {
               var7 = var6.substring(0, var17);
               var8 = var6.substring(var17 + 1);
            }
         } else {
            var7 = null;
         }

         if (var8 != null) {
            if (var8.length() > 0 && var8.charAt(0) == '[') {
               if ((var17 = var8.indexOf(93)) <= 2) {
                  throw new IllegalArgumentException("Invalid authority field: " + var6);
               }

               var18 = var8;
               var8 = var8.substring(0, var17 + 1);
               if (!IPAddressUtil.isIPv6LiteralAddress(var8.substring(1, var17))) {
                  throw new IllegalArgumentException("Invalid host: " + var8);
               }

               var9 = -1;
               if (var18.length() > var17 + 1) {
                  if (var18.charAt(var17 + 1) != ':') {
                     throw new IllegalArgumentException("Invalid authority field: " + var6);
                  }

                  ++var17;
                  if (var18.length() > var17 + 1) {
                     var9 = Integer.parseInt(var18.substring(var17 + 1));
                  }
               }
            } else {
               var17 = var8.indexOf(58);
               var9 = -1;
               if (var17 >= 0) {
                  if (var8.length() > var17 + 1) {
                     var9 = Integer.parseInt(var8.substring(var17 + 1));
                  }

                  var8 = var8.substring(0, var17);
               }
            }
         } else {
            var8 = "";
         }

         if (var9 < -1) {
            throw new IllegalArgumentException("Invalid port number :" + var9);
         }

         var3 = var15;
         if (var6 != null && var6.length() > 0) {
            var10 = "";
         }
      }

      if (var8 == null) {
         var8 = "";
      }

      if (var3 < var4) {
         if (var2.charAt(var3) == '/') {
            var10 = var2.substring(var3, var4);
         } else if (var10 != null && var10.length() > 0) {
            var13 = true;
            var17 = var10.lastIndexOf(47);
            var18 = "";
            if (var17 == -1 && var6 != null) {
               var18 = "/";
            }

            var10 = var10.substring(0, var17 + 1) + var18 + var2.substring(var3, var4);
         } else {
            String var20 = var6 != null ? "/" : "";
            var10 = var20 + var2.substring(var3, var4);
         }
      } else if (var14 && var10 != null) {
         var17 = var10.lastIndexOf(47);
         if (var17 < 0) {
            var17 = 0;
         }

         var10 = var10.substring(0, var17) + "/";
      }

      if (var10 == null) {
         var10 = "";
      }

      if (var13) {
         while((var15 = var10.indexOf("/./")) >= 0) {
            var10 = var10.substring(0, var15) + var10.substring(var15 + 2);
         }

         var15 = 0;

         while(true) {
            while((var15 = var10.indexOf("/../", var15)) >= 0) {
               if (var15 > 0 && (var4 = var10.lastIndexOf(47, var15 - 1)) >= 0 && var10.indexOf("/../", var4) != 0) {
                  var10 = var10.substring(0, var4) + var10.substring(var15 + 3);
                  var15 = 0;
               } else {
                  var15 += 3;
               }
            }

            while(var10.endsWith("/..")) {
               var15 = var10.indexOf("/..");
               if ((var4 = var10.lastIndexOf(47, var15 - 1)) < 0) {
                  break;
               }

               var10 = var10.substring(0, var4 + 1);
            }

            if (var10.startsWith("./") && var10.length() > 2) {
               var10 = var10.substring(2);
            }

            if (var10.endsWith("/.")) {
               var10 = var10.substring(0, var10.length() - 1);
            }
            break;
         }
      }

      this.setURL(var1, var5, var8, var9, var6, var7, var10, var11, var12);
   }

   protected int getDefaultPort() {
      return -1;
   }

   protected boolean equals(URL var1, URL var2) {
      String var3 = var1.getRef();
      String var4 = var2.getRef();
      return (var3 == var4 || var3 != null && var3.equals(var4)) && this.sameFile(var1, var2);
   }

   protected int hashCode(URL var1) {
      int var2 = 0;
      String var3 = var1.getProtocol();
      if (var3 != null) {
         var2 += var3.hashCode();
      }

      InetAddress var4 = this.getHostAddress(var1);
      String var5;
      if (var4 != null) {
         var2 += var4.hashCode();
      } else {
         var5 = var1.getHost();
         if (var5 != null) {
            var2 += var5.toLowerCase().hashCode();
         }
      }

      var5 = var1.getFile();
      if (var5 != null) {
         var2 += var5.hashCode();
      }

      if (var1.getPort() == -1) {
         var2 += this.getDefaultPort();
      } else {
         var2 += var1.getPort();
      }

      String var6 = var1.getRef();
      if (var6 != null) {
         var2 += var6.hashCode();
      }

      return var2;
   }

   protected boolean sameFile(URL var1, URL var2) {
      if (var1.getProtocol() == var2.getProtocol() || var1.getProtocol() != null && var1.getProtocol().equalsIgnoreCase(var2.getProtocol())) {
         if (var1.getFile() == var2.getFile() || var1.getFile() != null && var1.getFile().equals(var2.getFile())) {
            int var3 = var1.getPort() != -1 ? var1.getPort() : var1.handler.getDefaultPort();
            int var4 = var2.getPort() != -1 ? var2.getPort() : var2.handler.getDefaultPort();
            if (var3 != var4) {
               return false;
            } else {
               return this.hostsEqual(var1, var2);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected synchronized InetAddress getHostAddress(URL var1) {
      if (var1.hostAddress != null) {
         return var1.hostAddress;
      } else {
         String var2 = var1.getHost();
         if (var2 != null && !var2.equals("")) {
            try {
               var1.hostAddress = InetAddress.getByName(var2);
            } catch (UnknownHostException var4) {
               return null;
            } catch (SecurityException var5) {
               return null;
            }

            return var1.hostAddress;
         } else {
            return null;
         }
      }
   }

   protected boolean hostsEqual(URL var1, URL var2) {
      InetAddress var3 = this.getHostAddress(var1);
      InetAddress var4 = this.getHostAddress(var2);
      if (var3 != null && var4 != null) {
         return var3.equals(var4);
      } else if (var1.getHost() != null && var2.getHost() != null) {
         return var1.getHost().equalsIgnoreCase(var2.getHost());
      } else {
         return var1.getHost() == null && var2.getHost() == null;
      }
   }

   protected String toExternalForm(URL var1) {
      int var2 = var1.getProtocol().length() + 1;
      if (var1.getAuthority() != null && var1.getAuthority().length() > 0) {
         var2 += 2 + var1.getAuthority().length();
      }

      if (var1.getPath() != null) {
         var2 += var1.getPath().length();
      }

      if (var1.getQuery() != null) {
         var2 += 1 + var1.getQuery().length();
      }

      if (var1.getRef() != null) {
         var2 += 1 + var1.getRef().length();
      }

      StringBuffer var3 = new StringBuffer(var2);
      var3.append(var1.getProtocol());
      var3.append(":");
      if (var1.getAuthority() != null && var1.getAuthority().length() > 0) {
         var3.append("//");
         var3.append(var1.getAuthority());
      }

      if (var1.getPath() != null) {
         var3.append(var1.getPath());
      }

      if (var1.getQuery() != null) {
         var3.append('?');
         var3.append(var1.getQuery());
      }

      if (var1.getRef() != null) {
         var3.append("#");
         var3.append(var1.getRef());
      }

      return var3.toString();
   }

   protected void setURL(URL var1, String var2, String var3, int var4, String var5, String var6, String var7, String var8, String var9) {
      if (this != var1.handler) {
         throw new SecurityException("handler for url different from this handler");
      } else {
         var1.set(var1.getProtocol(), var3, var4, var5, var6, var7, var8, var9);
      }
   }

   /** @deprecated */
   @Deprecated
   protected void setURL(URL var1, String var2, String var3, int var4, String var5, String var6) {
      String var7 = null;
      String var8 = null;
      if (var3 != null && var3.length() != 0) {
         var7 = var4 == -1 ? var3 : var3 + ":" + var4;
         int var9 = var3.lastIndexOf(64);
         if (var9 != -1) {
            var8 = var3.substring(0, var9);
            var3 = var3.substring(var9 + 1);
         }
      }

      String var12 = null;
      String var10 = null;
      if (var5 != null) {
         int var11 = var5.lastIndexOf(63);
         if (var11 != -1) {
            var10 = var5.substring(var11 + 1);
            var12 = var5.substring(0, var11);
         } else {
            var12 = var5;
         }
      }

      this.setURL(var1, var2, var3, var4, var7, var8, var12, var10, var6);
   }
}
