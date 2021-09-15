package com.sun.xml.internal.ws.transport.http.server;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import com.sun.xml.internal.ws.util.ReadAllStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;

final class ServerConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate {
   private final HttpExchange httpExchange;
   private int status;
   private final HttpAdapter adapter;
   private ServerConnectionImpl.LWHSInputStream in;
   private OutputStream out;
   private static final BasePropertySet.PropertyMap model = parse(ServerConnectionImpl.class);

   public ServerConnectionImpl(@NotNull HttpAdapter adapter, @NotNull HttpExchange httpExchange) {
      this.adapter = adapter;
      this.httpExchange = httpExchange;
   }

   @PropertySet.Property({"javax.xml.ws.http.request.headers", "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers"})
   @NotNull
   public Map<String, List<String>> getRequestHeaders() {
      return this.httpExchange.getRequestHeaders();
   }

   public String getRequestHeader(String headerName) {
      return this.httpExchange.getRequestHeaders().getFirst(headerName);
   }

   public void setResponseHeaders(Map<String, List<String>> headers) {
      Headers r = this.httpExchange.getResponseHeaders();
      r.clear();
      Iterator var3 = headers.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
         String name = (String)entry.getKey();
         List<String> values = (List)entry.getValue();
         if (!"Content-Length".equalsIgnoreCase(name) && !"Content-Type".equalsIgnoreCase(name)) {
            r.put((String)name, (List)(new ArrayList(values)));
         }
      }

   }

   public void setResponseHeader(String key, List<String> value) {
      this.httpExchange.getResponseHeaders().put(key, value);
   }

   public Set<String> getRequestHeaderNames() {
      return this.httpExchange.getRequestHeaders().keySet();
   }

   public List<String> getRequestHeaderValues(String headerName) {
      return this.httpExchange.getRequestHeaders().get(headerName);
   }

   @PropertySet.Property({"javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers"})
   public Map<String, List<String>> getResponseHeaders() {
      return this.httpExchange.getResponseHeaders();
   }

   public void setContentTypeResponseHeader(@NotNull String value) {
      this.httpExchange.getResponseHeaders().set("Content-Type", value);
   }

   public void setStatus(int status) {
      this.status = status;
   }

   @PropertySet.Property({"javax.xml.ws.http.response.code"})
   public int getStatus() {
      return this.status;
   }

   @NotNull
   public InputStream getInput() {
      if (this.in == null) {
         this.in = new ServerConnectionImpl.LWHSInputStream(this.httpExchange.getRequestBody());
      }

      return this.in;
   }

   @NotNull
   public OutputStream getOutput() throws IOException {
      if (this.out == null) {
         String lenHeader = this.httpExchange.getResponseHeaders().getFirst("Content-Length");
         int length = lenHeader != null ? Integer.parseInt(lenHeader) : 0;
         this.httpExchange.sendResponseHeaders(this.getStatus(), (long)length);
         this.out = new FilterOutputStream(this.httpExchange.getResponseBody()) {
            boolean closed;

            public void close() throws IOException {
               if (!this.closed) {
                  this.closed = true;
                  ServerConnectionImpl.this.in.readAll();

                  try {
                     super.close();
                  } catch (IOException var2) {
                  }
               }

            }

            public void write(byte[] buf, int start, int len) throws IOException {
               this.out.write(buf, start, len);
            }
         };
      }

      return this.out;
   }

   @NotNull
   public WebServiceContextDelegate getWebServiceContextDelegate() {
      return this;
   }

   public Principal getUserPrincipal(Packet request) {
      return this.httpExchange.getPrincipal();
   }

   public boolean isUserInRole(Packet request, String role) {
      return false;
   }

   @NotNull
   public String getEPRAddress(Packet request, WSEndpoint endpoint) {
      PortAddressResolver resolver = this.adapter.owner.createPortAddressResolver(this.getBaseAddress(), endpoint.getImplementationClass());
      String address = resolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
      if (address == null) {
         throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(endpoint.getPortName()));
      } else {
         return address;
      }
   }

   public String getWSDLAddress(@NotNull Packet request, @NotNull WSEndpoint endpoint) {
      String eprAddress = this.getEPRAddress(request, endpoint);
      return this.adapter.getEndpoint().getPort() != null ? eprAddress + "?wsdl" : null;
   }

   public boolean isSecure() {
      return this.httpExchange instanceof HttpsExchange;
   }

   @PropertySet.Property({"javax.xml.ws.http.request.method"})
   @NotNull
   public String getRequestMethod() {
      return this.httpExchange.getRequestMethod();
   }

   @PropertySet.Property({"javax.xml.ws.http.request.querystring"})
   public String getQueryString() {
      URI requestUri = this.httpExchange.getRequestURI();
      String query = requestUri.getQuery();
      return query != null ? query : null;
   }

   @PropertySet.Property({"javax.xml.ws.http.request.pathinfo"})
   public String getPathInfo() {
      URI requestUri = this.httpExchange.getRequestURI();
      String reqPath = requestUri.getPath();
      String ctxtPath = this.httpExchange.getHttpContext().getPath();
      return reqPath.length() > ctxtPath.length() ? reqPath.substring(ctxtPath.length()) : null;
   }

   @PropertySet.Property({"com.sun.xml.internal.ws.http.exchange"})
   public HttpExchange getExchange() {
      return this.httpExchange;
   }

   @NotNull
   public String getBaseAddress() {
      StringBuilder strBuf = new StringBuilder();
      strBuf.append(this.httpExchange instanceof HttpsExchange ? "https" : "http");
      strBuf.append("://");
      String hostHeader = this.httpExchange.getRequestHeaders().getFirst("Host");
      if (hostHeader != null) {
         strBuf.append(hostHeader);
      } else {
         strBuf.append(this.httpExchange.getLocalAddress().getHostName());
         strBuf.append(":");
         strBuf.append(this.httpExchange.getLocalAddress().getPort());
      }

      return strBuf.toString();
   }

   public String getProtocol() {
      return this.httpExchange.getProtocol();
   }

   public void setContentLengthResponseHeader(int value) {
      this.httpExchange.getResponseHeaders().set("Content-Length", "" + value);
   }

   public String getRequestURI() {
      return this.httpExchange.getRequestURI().toString();
   }

   public String getRequestScheme() {
      return this.httpExchange instanceof HttpsExchange ? "https" : "http";
   }

   public String getServerName() {
      return this.httpExchange.getLocalAddress().getHostName();
   }

   public int getServerPort() {
      return this.httpExchange.getLocalAddress().getPort();
   }

   protected BasePropertySet.PropertyMap getPropertyMap() {
      return model;
   }

   private static class LWHSInputStream extends FilterInputStream {
      boolean closed;
      boolean readAll;

      LWHSInputStream(InputStream in) {
         super(in);
      }

      void readAll() throws IOException {
         if (!this.closed && !this.readAll) {
            ReadAllStream all = new ReadAllStream();
            all.readAll(this.in, 4000000L);
            this.in.close();
            this.in = all;
            this.readAll = true;
         }

      }

      public void close() throws IOException {
         if (!this.closed) {
            this.readAll();
            super.close();
            this.closed = true;
         }

      }
   }
}
