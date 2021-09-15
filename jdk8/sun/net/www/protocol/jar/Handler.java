package sun.net.www.protocol.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import sun.net.www.ParseUtil;

public class Handler extends URLStreamHandler {
   private static final String separator = "!/";

   protected URLConnection openConnection(URL var1) throws IOException {
      return new JarURLConnection(var1, this);
   }

   private static int indexOfBangSlash(String var0) {
      for(int var1 = var0.length(); (var1 = var0.lastIndexOf(33, var1)) != -1; --var1) {
         if (var1 != var0.length() - 1 && var0.charAt(var1 + 1) == '/') {
            return var1 + 1;
         }
      }

      return -1;
   }

   protected boolean sameFile(URL var1, URL var2) {
      if (var1.getProtocol().equals("jar") && var2.getProtocol().equals("jar")) {
         String var3 = var1.getFile();
         String var4 = var2.getFile();
         int var5 = var3.indexOf("!/");
         int var6 = var4.indexOf("!/");
         if (var5 != -1 && var6 != -1) {
            String var7 = var3.substring(var5 + 2);
            String var8 = var4.substring(var6 + 2);
            if (!var7.equals(var8)) {
               return false;
            } else {
               URL var9 = null;
               URL var10 = null;

               try {
                  var9 = new URL(var3.substring(0, var5));
                  var10 = new URL(var4.substring(0, var6));
               } catch (MalformedURLException var12) {
                  return super.sameFile(var1, var2);
               }

               return super.sameFile(var9, var10);
            }
         } else {
            return super.sameFile(var1, var2);
         }
      } else {
         return false;
      }
   }

   protected int hashCode(URL var1) {
      int var2 = 0;
      String var3 = var1.getProtocol();
      if (var3 != null) {
         var2 += var3.hashCode();
      }

      String var4 = var1.getFile();
      int var5 = var4.indexOf("!/");
      if (var5 == -1) {
         return var2 + var4.hashCode();
      } else {
         URL var6 = null;
         String var7 = var4.substring(0, var5);

         try {
            var6 = new URL(var7);
            var2 += var6.hashCode();
         } catch (MalformedURLException var9) {
            var2 += var7.hashCode();
         }

         String var8 = var4.substring(var5 + 2);
         var2 += var8.hashCode();
         return var2;
      }
   }

   protected void parseURL(URL var1, String var2, int var3, int var4) {
      String var5 = null;
      String var6 = null;
      int var7 = var2.indexOf(35, var4);
      boolean var8 = var7 == var3;
      if (var7 > -1) {
         var6 = var2.substring(var7 + 1, var2.length());
         if (var8) {
            var5 = var1.getFile();
         }
      }

      boolean var9 = false;
      if (var2.length() >= 4) {
         var9 = var2.substring(0, 4).equalsIgnoreCase("jar:");
      }

      var2 = var2.substring(var3, var4);
      if (var9) {
         var5 = this.parseAbsoluteSpec(var2);
      } else if (!var8) {
         var5 = this.parseContextSpec(var1, var2);
         int var10 = indexOfBangSlash(var5);
         String var11 = var5.substring(0, var10);
         String var12 = var5.substring(var10);
         ParseUtil var13 = new ParseUtil();
         var12 = var13.canonizeString(var12);
         var5 = var11 + var12;
      }

      this.setURL(var1, "jar", "", -1, var5, var6);
   }

   private String parseAbsoluteSpec(String var1) {
      Object var2 = null;
      boolean var3 = true;
      int var6;
      if ((var6 = indexOfBangSlash(var1)) == -1) {
         throw new NullPointerException("no !/ in spec");
      } else {
         try {
            String var4 = var1.substring(0, var6 - 1);
            new URL(var4);
            return var1;
         } catch (MalformedURLException var5) {
            throw new NullPointerException("invalid url: " + var1 + " (" + var5 + ")");
         }
      }
   }

   private String parseContextSpec(URL var1, String var2) {
      String var3 = var1.getFile();
      int var4;
      if (var2.startsWith("/")) {
         var4 = indexOfBangSlash(var3);
         if (var4 == -1) {
            throw new NullPointerException("malformed context url:" + var1 + ": no !/");
         }

         var3 = var3.substring(0, var4);
      }

      if (!var3.endsWith("/") && !var2.startsWith("/")) {
         var4 = var3.lastIndexOf(47);
         if (var4 == -1) {
            throw new NullPointerException("malformed context url:" + var1);
         }

         var3 = var3.substring(0, var4 + 1);
      }

      return var3 + var2;
   }
}
