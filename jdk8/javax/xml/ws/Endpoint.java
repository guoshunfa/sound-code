package javax.xml.ws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.transform.Source;
import javax.xml.ws.spi.http.HttpContext;
import org.w3c.dom.Element;

public abstract class Endpoint {
   public static final String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
   public static final String WSDL_PORT = "javax.xml.ws.wsdl.port";

   public static Endpoint create(Object implementor) {
      return create((String)null, (Object)implementor);
   }

   public static Endpoint create(Object implementor, WebServiceFeature... features) {
      return create((String)null, implementor, features);
   }

   public static Endpoint create(String bindingId, Object implementor) {
      return javax.xml.ws.spi.Provider.provider().createEndpoint(bindingId, implementor);
   }

   public static Endpoint create(String bindingId, Object implementor, WebServiceFeature... features) {
      return javax.xml.ws.spi.Provider.provider().createEndpoint(bindingId, implementor, features);
   }

   public abstract Binding getBinding();

   public abstract Object getImplementor();

   public abstract void publish(String var1);

   public static Endpoint publish(String address, Object implementor) {
      return javax.xml.ws.spi.Provider.provider().createAndPublishEndpoint(address, implementor);
   }

   public static Endpoint publish(String address, Object implementor, WebServiceFeature... features) {
      return javax.xml.ws.spi.Provider.provider().createAndPublishEndpoint(address, implementor, features);
   }

   public abstract void publish(Object var1);

   public void publish(HttpContext serverContext) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   public abstract void stop();

   public abstract boolean isPublished();

   public abstract List<Source> getMetadata();

   public abstract void setMetadata(List<Source> var1);

   public abstract Executor getExecutor();

   public abstract void setExecutor(Executor var1);

   public abstract Map<String, Object> getProperties();

   public abstract void setProperties(Map<String, Object> var1);

   public abstract EndpointReference getEndpointReference(Element... var1);

   public abstract <T extends EndpointReference> T getEndpointReference(Class<T> var1, Element... var2);

   public void setEndpointContext(EndpointContext ctxt) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }
}
