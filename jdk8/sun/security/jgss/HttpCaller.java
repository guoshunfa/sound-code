package sun.security.jgss;

import sun.net.www.protocol.http.HttpCallerInfo;

public class HttpCaller extends GSSCaller {
   private final HttpCallerInfo hci;

   public HttpCaller(HttpCallerInfo var1) {
      super("HTTP_CLIENT");
      this.hci = var1;
   }

   public HttpCallerInfo info() {
      return this.hci;
   }
}
