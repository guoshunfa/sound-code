package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
   protected String proxy;
   protected int proxyPort;

   protected int getDefaultPort() {
      return 80;
   }

   public Handler() {
      this.proxy = null;
      this.proxyPort = -1;
   }

   public Handler(String var1, int var2) {
      this.proxy = var1;
      this.proxyPort = var2;
   }

   protected URLConnection openConnection(URL var1) throws IOException {
      return this.openConnection(var1, (Proxy)null);
   }

   protected URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      return new HttpURLConnection(var1, var2, this);
   }
}
