package sun.net.www.protocol.file;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import sun.net.www.ParseUtil;

public class Handler extends URLStreamHandler {
   private String getHost(URL var1) {
      String var2 = var1.getHost();
      if (var2 == null) {
         var2 = "";
      }

      return var2;
   }

   protected void parseURL(URL var1, String var2, int var3, int var4) {
      super.parseURL(var1, var2.replace(File.separatorChar, '/'), var3, var4);
   }

   public synchronized URLConnection openConnection(URL var1) throws IOException {
      return this.openConnection(var1, (Proxy)null);
   }

   public synchronized URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      String var3 = var1.getHost();
      if (var3 != null && !var3.equals("") && !var3.equals("~") && !var3.equalsIgnoreCase("localhost")) {
         URLConnection var8;
         try {
            URL var5 = new URL("ftp", var3, var1.getFile() + (var1.getRef() == null ? "" : "#" + var1.getRef()));
            if (var2 != null) {
               var8 = var5.openConnection(var2);
            } else {
               var8 = var5.openConnection();
            }
         } catch (IOException var7) {
            var8 = null;
         }

         if (var8 == null) {
            throw new IOException("Unable to connect to: " + var1.toExternalForm());
         } else {
            return var8;
         }
      } else {
         File var4 = new File(ParseUtil.decode(var1.getPath()));
         return this.createFileURLConnection(var1, var4);
      }
   }

   protected URLConnection createFileURLConnection(URL var1, File var2) {
      return new FileURLConnection(var1, var2);
   }

   protected boolean hostsEqual(URL var1, URL var2) {
      String var3 = var1.getHost();
      String var4 = var2.getHost();
      if (!"localhost".equalsIgnoreCase(var3) || var4 != null && !"".equals(var4)) {
         return !"localhost".equalsIgnoreCase(var4) || var3 != null && !"".equals(var3) ? super.hostsEqual(var1, var2) : true;
      } else {
         return true;
      }
   }
}
