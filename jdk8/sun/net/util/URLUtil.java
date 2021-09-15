package sun.net.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLPermission;
import java.security.Permission;

public class URLUtil {
   public static String urlNoFragString(URL var0) {
      StringBuilder var1 = new StringBuilder();
      String var2 = var0.getProtocol();
      if (var2 != null) {
         var2 = var2.toLowerCase();
         var1.append(var2);
         var1.append("://");
      }

      String var3 = var0.getHost();
      if (var3 != null) {
         var3 = var3.toLowerCase();
         var1.append(var3);
         int var4 = var0.getPort();
         if (var4 == -1) {
            var4 = var0.getDefaultPort();
         }

         if (var4 != -1) {
            var1.append(":").append(var4);
         }
      }

      String var5 = var0.getFile();
      if (var5 != null) {
         var1.append(var5);
      }

      return var1.toString();
   }

   public static Permission getConnectPermission(URL var0) throws IOException {
      String var1 = var0.toString().toLowerCase();
      if (!var1.startsWith("http:") && !var1.startsWith("https:")) {
         if (!var1.startsWith("jar:http:") && !var1.startsWith("jar:https:")) {
            return var0.openConnection().getPermission();
         } else {
            String var2 = var0.toString();
            int var3 = var2.indexOf("!/");
            var2 = var2.substring(4, var3 > -1 ? var3 : var2.length());
            URL var4 = new URL(var2);
            return getURLConnectPermission(var4);
         }
      } else {
         return getURLConnectPermission(var0);
      }
   }

   private static Permission getURLConnectPermission(URL var0) {
      String var1 = var0.getProtocol() + "://" + var0.getAuthority() + var0.getPath();
      return new URLPermission(var1);
   }
}
