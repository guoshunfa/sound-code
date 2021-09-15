package sun.net.www.protocol.mailto;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
   public synchronized URLConnection openConnection(URL var1) {
      return new MailToURLConnection(var1);
   }

   public void parseURL(URL var1, String var2, int var3, int var4) {
      String var5 = var1.getProtocol();
      String var6 = "";
      int var7 = var1.getPort();
      String var8 = "";
      if (var3 < var4) {
         var8 = var2.substring(var3, var4);
      }

      boolean var9 = false;
      if (var8 != null && !var8.equals("")) {
         boolean var10 = true;

         for(int var11 = 0; var11 < var8.length(); ++var11) {
            if (!Character.isWhitespace(var8.charAt(var11))) {
               var10 = false;
            }
         }

         if (var10) {
            var9 = true;
         }
      } else {
         var9 = true;
      }

      if (var9) {
         throw new RuntimeException("No email address");
      } else {
         this.setURLHandler(var1, var5, var6, var7, var8, (String)null);
      }
   }

   private void setURLHandler(URL var1, String var2, String var3, int var4, String var5, String var6) {
      this.setURL(var1, var2, var3, var4, var5, (String)null);
   }
}
