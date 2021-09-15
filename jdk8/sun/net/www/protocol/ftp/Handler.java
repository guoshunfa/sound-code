package sun.net.www.protocol.ftp;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
   protected int getDefaultPort() {
      return 21;
   }

   protected boolean equals(URL var1, URL var2) {
      boolean var10000;
      label25: {
         String var3 = var1.getUserInfo();
         String var4 = var2.getUserInfo();
         if (super.equals(var1, var2)) {
            if (var3 == null) {
               if (var4 == null) {
                  break label25;
               }
            } else if (var3.equals(var4)) {
               break label25;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   protected URLConnection openConnection(URL var1) throws IOException {
      return this.openConnection(var1, (Proxy)null);
   }

   protected URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      return new FtpURLConnection(var1, var2);
   }
}
