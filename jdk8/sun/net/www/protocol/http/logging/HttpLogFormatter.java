package sun.net.www.protocol.http.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpLogFormatter extends SimpleFormatter {
   private static volatile Pattern pattern = null;
   private static volatile Pattern cpattern = null;

   public HttpLogFormatter() {
      if (pattern == null) {
         pattern = Pattern.compile("\\{[^\\}]*\\}");
         cpattern = Pattern.compile("[^,\\] ]{2,}");
      }

   }

   public String format(LogRecord var1) {
      String var2 = var1.getSourceClassName();
      if (var2 == null || !var2.startsWith("sun.net.www.protocol.http") && !var2.startsWith("sun.net.www.http")) {
         return super.format(var1);
      } else {
         String var3 = var1.getMessage();
         StringBuilder var4 = new StringBuilder("HTTP: ");
         int var7;
         String var8;
         if (var3.startsWith("sun.net.www.MessageHeader@")) {
            for(Matcher var5 = pattern.matcher(var3); var5.find(); var4.append("\t").append(var8).append("\n")) {
               int var6 = var5.start();
               var7 = var5.end();
               var8 = var3.substring(var6 + 1, var7 - 1);
               if (var8.startsWith("null: ")) {
                  var8 = var8.substring(6);
               }

               if (var8.endsWith(": null")) {
                  var8 = var8.substring(0, var8.length() - 6);
               }
            }
         } else if (var3.startsWith("Cookies retrieved: {")) {
            String var12 = var3.substring(20);
            var4.append("Cookies from handler:\n");

            while(true) {
               int var9;
               int var10;
               String var11;
               String var13;
               Matcher var14;
               label80:
               do {
                  do {
                     if (var12.length() < 7) {
                        return var4.toString();
                     }

                     if (!var12.startsWith("Cookie=[")) {
                        continue label80;
                     }

                     var13 = var12.substring(8);
                     var7 = var13.indexOf("Cookie2=[");
                     if (var7 > 0) {
                        var13 = var13.substring(0, var7 - 1);
                        var12 = var13.substring(var7);
                     } else {
                        var12 = "";
                     }
                  } while(var13.length() < 4);

                  var14 = cpattern.matcher(var13);

                  while(var14.find()) {
                     var9 = var14.start();
                     var10 = var14.end();
                     if (var9 >= 0) {
                        var11 = var13.substring(var9 + 1, var10 > 0 ? var10 - 1 : var13.length() - 1);
                        var4.append("\t").append(var11).append("\n");
                     }
                  }
               } while(!var12.startsWith("Cookie2=["));

               var13 = var12.substring(9);
               var7 = var13.indexOf("Cookie=[");
               if (var7 > 0) {
                  var13 = var13.substring(0, var7 - 1);
                  var12 = var13.substring(var7);
               } else {
                  var12 = "";
               }

               var14 = cpattern.matcher(var13);

               while(var14.find()) {
                  var9 = var14.start();
                  var10 = var14.end();
                  if (var9 >= 0) {
                     var11 = var13.substring(var9 + 1, var10 > 0 ? var10 - 1 : var13.length() - 1);
                     var4.append("\t").append(var11).append("\n");
                  }
               }
            }
         } else {
            var4.append(var3).append("\n");
         }

         return var4.toString();
      }
   }
}
