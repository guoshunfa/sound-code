package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import jdk.Exported;

@Exported
public abstract class HttpServer {
   protected HttpServer() {
   }

   public static HttpServer create() throws IOException {
      return create((InetSocketAddress)null, 0);
   }

   public static HttpServer create(InetSocketAddress var0, int var1) throws IOException {
      HttpServerProvider var2 = HttpServerProvider.provider();
      return var2.createHttpServer(var0, var1);
   }

   public abstract void bind(InetSocketAddress var1, int var2) throws IOException;

   public abstract void start();

   public abstract void setExecutor(Executor var1);

   public abstract Executor getExecutor();

   public abstract void stop(int var1);

   public abstract HttpContext createContext(String var1, HttpHandler var2);

   public abstract HttpContext createContext(String var1);

   public abstract void removeContext(String var1) throws IllegalArgumentException;

   public abstract void removeContext(HttpContext var1);

   public abstract InetSocketAddress getAddress();
}
