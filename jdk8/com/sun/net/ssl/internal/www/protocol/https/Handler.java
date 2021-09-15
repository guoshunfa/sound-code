package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends sun.net.www.protocol.https.Handler {
   public Handler() {
   }

   public Handler(String var1, int var2) {
      super(var1, var2);
   }

   protected URLConnection openConnection(URL var1) throws IOException {
      return this.openConnection(var1, (Proxy)null);
   }

   protected URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      return new HttpsURLConnectionOldImpl(var1, var2, this);
   }
}
