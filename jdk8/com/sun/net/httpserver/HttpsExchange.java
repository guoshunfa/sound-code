package com.sun.net.httpserver;

import javax.net.ssl.SSLSession;
import jdk.Exported;

@Exported
public abstract class HttpsExchange extends HttpExchange {
   protected HttpsExchange() {
   }

   public abstract SSLSession getSSLSession();
}
