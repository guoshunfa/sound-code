package sun.awt.image;

import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import sun.net.util.URLUtil;

public class URLImageSource extends InputStreamImageSource {
   URL url;
   URLConnection conn;
   String actualHost;
   int actualPort;

   public URLImageSource(URL var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         try {
            Permission var3 = URLUtil.getConnectPermission(var1);
            if (var3 != null) {
               try {
                  var2.checkPermission(var3);
               } catch (SecurityException var5) {
                  if (var3 instanceof FilePermission && var3.getActions().indexOf("read") != -1) {
                     var2.checkRead(var3.getName());
                  } else {
                     if (!(var3 instanceof SocketPermission) || var3.getActions().indexOf("connect") == -1) {
                        throw var5;
                     }

                     var2.checkConnect(var1.getHost(), var1.getPort());
                  }
               }
            }
         } catch (IOException var6) {
            var2.checkConnect(var1.getHost(), var1.getPort());
         }
      }

      this.url = var1;
   }

   public URLImageSource(String var1) throws MalformedURLException {
      this(new URL((URL)null, var1));
   }

   public URLImageSource(URL var1, URLConnection var2) {
      this(var1);
      this.conn = var2;
   }

   public URLImageSource(URLConnection var1) {
      this(var1.getURL(), var1);
   }

   final boolean checkSecurity(Object var1, boolean var2) {
      if (this.actualHost != null) {
         try {
            SecurityManager var3 = System.getSecurityManager();
            if (var3 != null) {
               var3.checkConnect(this.actualHost, this.actualPort, var1);
            }
         } catch (SecurityException var4) {
            if (!var2) {
               throw var4;
            }

            return false;
         }
      }

      return true;
   }

   private synchronized URLConnection getConnection() throws IOException {
      URLConnection var1;
      if (this.conn != null) {
         var1 = this.conn;
         this.conn = null;
      } else {
         var1 = this.url.openConnection();
      }

      return var1;
   }

   protected ImageDecoder getDecoder() {
      InputStream var1 = null;
      String var2 = null;
      URLConnection var3 = null;

      try {
         var3 = this.getConnection();
         var1 = var3.getInputStream();
         var2 = var3.getContentType();
         URL var4 = var3.getURL();
         if (var4 != this.url && (!var4.getHost().equals(this.url.getHost()) || var4.getPort() != this.url.getPort())) {
            if (this.actualHost != null && (!this.actualHost.equals(var4.getHost()) || this.actualPort != var4.getPort())) {
               throw new SecurityException("image moved!");
            }

            this.actualHost = var4.getHost();
            this.actualPort = var4.getPort();
         }
      } catch (IOException var8) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var6) {
            }
         } else if (var3 instanceof HttpURLConnection) {
            ((HttpURLConnection)var3).disconnect();
         }

         return null;
      }

      ImageDecoder var9 = this.decoderForType(var1, var2);
      if (var9 == null) {
         var9 = this.getDecoder(var1);
      }

      if (var9 == null) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var7) {
            }
         } else if (var3 instanceof HttpURLConnection) {
            ((HttpURLConnection)var3).disconnect();
         }
      }

      return var9;
   }
}
