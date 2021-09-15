package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public final class HttpEndpoint extends com.sun.xml.internal.ws.api.server.HttpEndpoint {
   private String address;
   private HttpContext httpContext;
   private final HttpAdapter adapter;
   private final Executor executor;

   public HttpEndpoint(Executor executor, HttpAdapter adapter) {
      this.executor = executor;
      this.adapter = adapter;
   }

   public void publish(String address) {
      this.address = address;
      this.httpContext = ServerMgr.getInstance().createContext(address);
      this.publish((Object)this.httpContext);
   }

   public void publish(Object serverContext) {
      if (serverContext instanceof javax.xml.ws.spi.http.HttpContext) {
         this.setHandler((javax.xml.ws.spi.http.HttpContext)serverContext);
      } else if (serverContext instanceof HttpContext) {
         this.httpContext = (HttpContext)serverContext;
         this.setHandler(this.httpContext);
      } else {
         throw new ServerRtException(ServerMessages.NOT_KNOW_HTTP_CONTEXT_TYPE(serverContext.getClass(), HttpContext.class, javax.xml.ws.spi.http.HttpContext.class), new Object[0]);
      }
   }

   HttpAdapterList getAdapterOwner() {
      return this.adapter.owner;
   }

   private String getEPRAddress() {
      if (this.address == null) {
         return this.httpContext.getServer().getAddress().toString();
      } else {
         try {
            URL u = new URL(this.address);
            if (u.getPort() == 0) {
               return (new URL(u.getProtocol(), u.getHost(), this.httpContext.getServer().getAddress().getPort(), u.getFile())).toString();
            }
         } catch (MalformedURLException var2) {
         }

         return this.address;
      }
   }

   public void stop() {
      if (this.httpContext != null) {
         if (this.address == null) {
            this.httpContext.getServer().removeContext(this.httpContext);
         } else {
            ServerMgr.getInstance().removeContext(this.httpContext);
         }
      }

      this.adapter.getEndpoint().dispose();
   }

   private void setHandler(HttpContext context) {
      context.setHandler(new WSHttpHandler(this.adapter, this.executor));
   }

   private void setHandler(javax.xml.ws.spi.http.HttpContext context) {
      context.setHandler(new PortableHttpHandler(this.adapter, this.executor));
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
      String eprAddress = this.getEPRAddress();
      return (EndpointReference)clazz.cast(this.adapter.getEndpoint().getEndpointReference(clazz, eprAddress, eprAddress + "?wsdl", referenceParameters));
   }
}
