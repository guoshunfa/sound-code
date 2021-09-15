package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebModule;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public final class ServerAdapter extends HttpAdapter implements BoundEndpoint {
   final String name;
   private static final Logger LOGGER = Logger.getLogger(ServerAdapter.class.getName());

   protected ServerAdapter(String name, String urlPattern, WSEndpoint endpoint, ServerAdapterList owner) {
      super(endpoint, owner, urlPattern);
      this.name = name;
      Module module = (Module)endpoint.getContainer().getSPI(Module.class);
      if (module == null) {
         LOGGER.log(Level.WARNING, "Container {0} doesn''t support {1}", new Object[]{endpoint.getContainer(), Module.class});
      } else {
         module.getBoundEndpoints().add(this);
      }

   }

   public String getName() {
      return this.name;
   }

   @NotNull
   public URI getAddress() {
      WebModule webModule = (WebModule)this.endpoint.getContainer().getSPI(WebModule.class);
      if (webModule == null) {
         throw new WebServiceException("Container " + this.endpoint.getContainer() + " doesn't support " + WebModule.class);
      } else {
         return this.getAddress(webModule.getContextPath());
      }
   }

   @NotNull
   public URI getAddress(String baseAddress) {
      String adrs = baseAddress + this.getValidPath();

      try {
         return new URI(adrs);
      } catch (URISyntaxException var4) {
         throw new WebServiceException("Unable to compute address for " + this.endpoint, var4);
      }
   }

   public void dispose() {
      this.endpoint.dispose();
   }

   public String getUrlPattern() {
      return this.urlPattern;
   }

   public String toString() {
      return super.toString() + "[name=" + this.name + ']';
   }
}
