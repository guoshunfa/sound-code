package com.sun.net.httpserver;

import javax.net.ssl.SSLContext;
import jdk.Exported;

@Exported
public class HttpsConfigurator {
   private SSLContext context;

   public HttpsConfigurator(SSLContext var1) {
      if (var1 == null) {
         throw new NullPointerException("null SSLContext");
      } else {
         this.context = var1;
      }
   }

   public SSLContext getSSLContext() {
      return this.context;
   }

   public void configure(HttpsParameters var1) {
      var1.setSSLParameters(this.getSSLContext().getDefaultSSLParameters());
   }
}
