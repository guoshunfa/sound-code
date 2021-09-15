package com.sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import jdk.Exported;

@Exported
public abstract class HttpExchange {
   protected HttpExchange() {
   }

   public abstract Headers getRequestHeaders();

   public abstract Headers getResponseHeaders();

   public abstract URI getRequestURI();

   public abstract String getRequestMethod();

   public abstract HttpContext getHttpContext();

   public abstract void close();

   public abstract InputStream getRequestBody();

   public abstract OutputStream getResponseBody();

   public abstract void sendResponseHeaders(int var1, long var2) throws IOException;

   public abstract InetSocketAddress getRemoteAddress();

   public abstract int getResponseCode();

   public abstract InetSocketAddress getLocalAddress();

   public abstract String getProtocol();

   public abstract Object getAttribute(String var1);

   public abstract void setAttribute(String var1, Object var2);

   public abstract void setStreams(InputStream var1, OutputStream var2);

   public abstract HttpPrincipal getPrincipal();
}
