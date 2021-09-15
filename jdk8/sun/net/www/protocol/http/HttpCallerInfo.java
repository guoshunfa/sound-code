package sun.net.www.protocol.http;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.URL;

public final class HttpCallerInfo {
   public final URL url;
   public final String host;
   public final String protocol;
   public final String prompt;
   public final String scheme;
   public final int port;
   public final InetAddress addr;
   public final Authenticator.RequestorType authType;

   public HttpCallerInfo(HttpCallerInfo var1, String var2) {
      this.url = var1.url;
      this.host = var1.host;
      this.protocol = var1.protocol;
      this.prompt = var1.prompt;
      this.port = var1.port;
      this.addr = var1.addr;
      this.authType = var1.authType;
      this.scheme = var2;
   }

   public HttpCallerInfo(URL var1) {
      this.url = var1;
      this.prompt = "";
      this.host = var1.getHost();
      int var2 = var1.getPort();
      if (var2 == -1) {
         this.port = var1.getDefaultPort();
      } else {
         this.port = var2;
      }

      InetAddress var3;
      try {
         var3 = InetAddress.getByName(var1.getHost());
      } catch (Exception var5) {
         var3 = null;
      }

      this.addr = var3;
      this.protocol = var1.getProtocol();
      this.authType = Authenticator.RequestorType.SERVER;
      this.scheme = "";
   }

   public HttpCallerInfo(URL var1, String var2, int var3) {
      this.url = var1;
      this.host = var2;
      this.port = var3;
      this.prompt = "";
      this.addr = null;
      this.protocol = var1.getProtocol();
      this.authType = Authenticator.RequestorType.PROXY;
      this.scheme = "";
   }
}
