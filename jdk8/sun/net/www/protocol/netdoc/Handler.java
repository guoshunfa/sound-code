package sun.net.www.protocol.netdoc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class Handler extends URLStreamHandler {
   static URL base;

   public synchronized URLConnection openConnection(URL var1) throws IOException {
      URLConnection var2 = null;
      Boolean var4 = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("newdoc.localonly")));
      boolean var5 = var4;
      String var6 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("doc.url")));
      String var7 = var1.getFile();
      URL var3;
      if (!var5) {
         try {
            if (base == null) {
               base = new URL(var6);
            }

            var3 = new URL(base, var7);
         } catch (MalformedURLException var11) {
            var3 = null;
         }

         if (var3 != null) {
            var2 = var3.openConnection();
         }
      }

      if (var2 == null) {
         try {
            var3 = new URL("file", "~", var7);
            var2 = var3.openConnection();
            InputStream var8 = var2.getInputStream();
         } catch (MalformedURLException var9) {
            var2 = null;
         } catch (IOException var10) {
            var2 = null;
         }
      }

      if (var2 == null) {
         throw new IOException("Can't find file for URL: " + var1.toExternalForm());
      } else {
         return var2;
      }
   }
}
