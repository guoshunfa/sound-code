package com.sun.xml.internal.ws.transport.http.server;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.http.HttpExchange;

final class PortableConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate {
   private final HttpExchange httpExchange;
   private int status;
   private final HttpAdapter adapter;
   private boolean outputWritten;
   private static final BasePropertySet.PropertyMap model = parse(PortableConnectionImpl.class);

   public PortableConnectionImpl(@NotNull HttpAdapter adapter, @NotNull HttpExchange httpExchange) {
      this.adapter = adapter;
      this.httpExchange = httpExchange;
   }

   @PropertySet.Property({"javax.xml.ws.http.request.headers", "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers"})
   @NotNull
   public Map<String, List<String>> getRequestHeaders() {
      return this.httpExchange.getRequestHeaders();
   }

   public String getRequestHeader(String headerName) {
      return this.httpExchange.getRequestHeader(headerName);
   }

   public void setResponseHeaders(Map<String, List<String>> headers) {
      Map<String, List<String>> r = this.httpExchange.getResponseHeaders();
      r.clear();
      Iterator var3 = headers.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
         String name = (String)entry.getKey();
         List<String> values = (List)entry.getValue();
         if (!name.equalsIgnoreCase("Content-Length") && !name.equalsIgnoreCase("Content-Type")) {
            r.put(name, new ArrayList(values));
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
      return (List)this.httpExchange.getRequestHeaders().get(headerName);
   }

   @PropertySet.Property({"javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers"})
   public Map<String, List<String>> getResponseHeaders() {
      return this.httpExchange.getResponseHeaders();
   }

   public void setContentTypeResponseHeader(@NotNull String value) {
      this.httpExchange.addResponseHeader("Content-Type", value);
   }

   public void setStatus(int status) {
      this.status = status;
   }

   @PropertySet.Property({"javax.xml.ws.http.response.code"})
   public int getStatus() {
      return this.status;
   }

   @NotNull
   public InputStream getInput() throws IOException {
      return this.httpExchange.getRequestBody();
   }

   @NotNull
   public OutputStream getOutput() throws IOException {
      assert !this.outputWritten;

      this.outputWritten = true;
      this.httpExchange.setStatus(this.getStatus());
      return this.httpExchange.getResponseBody();
   }

   @NotNull
   public WebServiceContextDelegate getWebServiceContextDelegate() {
      return this;
   }

   public Principal getUserPrincipal(Packet request) {
      return this.httpExchange.getUserPrincipal();
   }

   public boolean isUserInRole(Packet request, String role) {
      return this.httpExchange.isUserInRole(role);
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

   @PropertySet.Property({"javax.xml.ws.servlet.context"})
   public Object getServletContext() {
      return this.httpExchange.getAttribute("javax.xml.ws.servlet.context");
   }

   @PropertySet.Property({"javax.xml.ws.servlet.response"})
   public Object getServletResponse() {
      return this.httpExchange.getAttribute("javax.xml.ws.servlet.response");
   }

   @PropertySet.Property({"javax.xml.ws.servlet.request"})
   public Object getServletRequest() {
      return this.httpExchange.getAttribute("javax.xml.ws.servlet.request");
   }

   public String getWSDLAddress(@NotNull Packet request, @NotNull WSEndpoint endpoint) {
      String eprAddress = this.getEPRAddress(request, endpoint);
      return this.adapter.getEndpoint().getPort() != null ? eprAddress + "?wsdl" : null;
   }

   public boolean isSecure() {
      return this.httpExchange.getScheme().equals("https");
   }

   @PropertySet.Property({"javax.xml.ws.http.request.method"})
   @NotNull
   public String getRequestMethod() {
      return this.httpExchange.getRequestMethod();
   }

   @PropertySet.Property({"javax.xml.ws.http.request.querystring"})
   public String getQueryString() {
      return this.httpExchange.getQueryString();
   }

   @PropertySet.Property({"javax.xml.ws.http.request.pathinfo"})
   public String getPathInfo() {
      return this.httpExchange.getPathInfo();
   }

   @PropertySet.Property({"com.sun.xml.internal.ws.http.exchange"})
   public HttpExchange getExchange() {
      return this.httpExchange;
   }

   @NotNull
   public String getBaseAddress() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.httpExchange.getScheme());
      sb.append("://");
      sb.append(this.httpExchange.getLocalAddress().getHostName());
      sb.append(":");
      sb.append(this.httpExchange.getLocalAddress().getPort());
      sb.append(this.httpExchange.getContextPath());
      return sb.toString();
   }

   public String getProtocol() {
      return this.httpExchange.getProtocol();
   }

   public void setContentLengthResponseHeader(int value) {
      this.httpExchange.addResponseHeader("Content-Length", "" + value);
   }

   public String getRequestURI() {
      return this.httpExchange.getRequestURI().toString();
   }

   public String getRequestScheme() {
      return this.httpExchange.getScheme();
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
}
