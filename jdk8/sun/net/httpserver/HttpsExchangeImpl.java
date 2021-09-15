package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import javax.net.ssl.SSLSession;

class HttpsExchangeImpl extends HttpsExchange {
   ExchangeImpl impl;

   HttpsExchangeImpl(ExchangeImpl var1) throws IOException {
      this.impl = var1;
   }

   public Headers getRequestHeaders() {
      return this.impl.getRequestHeaders();
   }

   public Headers getResponseHeaders() {
      return this.impl.getResponseHeaders();
   }

   public URI getRequestURI() {
      return this.impl.getRequestURI();
   }

   public String getRequestMethod() {
      return this.impl.getRequestMethod();
   }

   public HttpContextImpl getHttpContext() {
      return this.impl.getHttpContext();
   }

   public void close() {
      this.impl.close();
   }

   public InputStream getRequestBody() {
      return this.impl.getRequestBody();
   }

   public int getResponseCode() {
      return this.impl.getResponseCode();
   }

   public OutputStream getResponseBody() {
      return this.impl.getResponseBody();
   }

   public void sendResponseHeaders(int var1, long var2) throws IOException {
      this.impl.sendResponseHeaders(var1, var2);
   }

   public InetSocketAddress getRemoteAddress() {
      return this.impl.getRemoteAddress();
   }

   public InetSocketAddress getLocalAddress() {
      return this.impl.getLocalAddress();
   }

   public String getProtocol() {
      return this.impl.getProtocol();
   }

   public SSLSession getSSLSession() {
      return this.impl.getSSLSession();
   }

   public Object getAttribute(String var1) {
      return this.impl.getAttribute(var1);
   }

   public void setAttribute(String var1, Object var2) {
      this.impl.setAttribute(var1, var2);
   }

   public void setStreams(InputStream var1, OutputStream var2) {
      this.impl.setStreams(var1, var2);
   }

   public HttpPrincipal getPrincipal() {
      return this.impl.getPrincipal();
   }

   ExchangeImpl getExchangeImpl() {
      return this.impl;
   }
}
