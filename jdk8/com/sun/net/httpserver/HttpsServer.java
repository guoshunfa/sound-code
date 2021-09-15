package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpsServer extends HttpServer {
   protected HttpsServer() {
   }

   public static HttpsServer create() throws IOException {
      return create((InetSocketAddress)null, 0);
   }

   public static HttpsServer create(InetSocketAddress var0, int var1) throws IOException {
      HttpServerProvider var2 = HttpServerProvider.provider();
      return var2.createHttpsServer(var0, var1);
   }

   public abstract void setHttpsConfigurator(HttpsConfigurator var1);

   public abstract HttpsConfigurator getHttpsConfigurator();
}
