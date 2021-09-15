package javax.xml.ws.spi;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class Provider {
   public static final String JAXWSPROVIDER_PROPERTY = "javax.xml.ws.spi.Provider";
   static final String DEFAULT_JAXWSPROVIDER = "com.sun.xml.internal.ws.spi.ProviderImpl";
   private static final Method loadMethod;
   private static final Method iteratorMethod;

   protected Provider() {
   }

   public static Provider provider() {
      try {
         Object provider = getProviderUsingServiceLoader();
         if (provider == null) {
            provider = FactoryFinder.find("javax.xml.ws.spi.Provider", "com.sun.xml.internal.ws.spi.ProviderImpl");
         }

         if (!(provider instanceof Provider)) {
            Class pClass = Provider.class;
            String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
            ClassLoader loader = pClass.getClassLoader();
            if (loader == null) {
               loader = ClassLoader.getSystemClassLoader();
            }

            URL targetTypeURL = loader.getResource(classnameAsResource);
            throw new LinkageError("ClassCastException: attempting to cast" + provider.getClass().getClassLoader().getResource(classnameAsResource) + "to" + targetTypeURL.toString());
         } else {
            return (Provider)provider;
         }
      } catch (WebServiceException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new WebServiceException("Unable to createEndpointReference Provider", var6);
      }
   }

   private static Provider getProviderUsingServiceLoader() {
      if (loadMethod != null) {
         Object loader;
         try {
            loader = loadMethod.invoke((Object)null, Provider.class);
         } catch (Exception var4) {
            throw new WebServiceException("Cannot invoke java.util.ServiceLoader#load()", var4);
         }

         Iterator it;
         try {
            it = (Iterator)iteratorMethod.invoke(loader);
         } catch (Exception var3) {
            throw new WebServiceException("Cannot invoke java.util.ServiceLoader#iterator()", var3);
         }

         return it.hasNext() ? (Provider)it.next() : null;
      } else {
         return null;
      }
   }

   public abstract ServiceDelegate createServiceDelegate(URL var1, QName var2, Class<? extends Service> var3);

   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeature... features) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   public abstract Endpoint createEndpoint(String var1, Object var2);

   public abstract Endpoint createAndPublishEndpoint(String var1, Object var2);

   public abstract EndpointReference readEndpointReference(Source var1);

   public abstract <T> T getPort(EndpointReference var1, Class<T> var2, WebServiceFeature... var3);

   public abstract W3CEndpointReference createW3CEndpointReference(String var1, QName var2, QName var3, List<Element> var4, String var5, List<Element> var6);

   public W3CEndpointReference createW3CEndpointReference(String address, QName interfaceName, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters, List<Element> elements, Map<QName, String> attributes) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   public Endpoint createAndPublishEndpoint(String address, Object implementor, WebServiceFeature... features) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   public Endpoint createEndpoint(String bindingId, Object implementor, WebServiceFeature... features) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   public Endpoint createEndpoint(String bindingId, Class<?> implementorClass, Invoker invoker, WebServiceFeature... features) {
      throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
   }

   static {
      Method tLoadMethod = null;
      Method tIteratorMethod = null;

      try {
         Class<?> clazz = Class.forName("java.util.ServiceLoader");
         tLoadMethod = clazz.getMethod("load", Class.class);
         tIteratorMethod = clazz.getMethod("iterator");
      } catch (ClassNotFoundException var3) {
      } catch (NoSuchMethodException var4) {
      }

      loadMethod = tLoadMethod;
      iteratorMethod = tIteratorMethod;
   }
}
