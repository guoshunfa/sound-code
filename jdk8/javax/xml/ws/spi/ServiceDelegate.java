package javax.xml.ws.spi;

import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;

public abstract class ServiceDelegate {
   protected ServiceDelegate() {
   }

   public abstract <T> T getPort(QName var1, Class<T> var2);

   public abstract <T> T getPort(QName var1, Class<T> var2, WebServiceFeature... var3);

   public abstract <T> T getPort(EndpointReference var1, Class<T> var2, WebServiceFeature... var3);

   public abstract <T> T getPort(Class<T> var1);

   public abstract <T> T getPort(Class<T> var1, WebServiceFeature... var2);

   public abstract void addPort(QName var1, String var2, String var3);

   public abstract <T> Dispatch<T> createDispatch(QName var1, Class<T> var2, Service.Mode var3);

   public abstract <T> Dispatch<T> createDispatch(QName var1, Class<T> var2, Service.Mode var3, WebServiceFeature... var4);

   public abstract <T> Dispatch<T> createDispatch(EndpointReference var1, Class<T> var2, Service.Mode var3, WebServiceFeature... var4);

   public abstract Dispatch<Object> createDispatch(QName var1, JAXBContext var2, Service.Mode var3);

   public abstract Dispatch<Object> createDispatch(QName var1, JAXBContext var2, Service.Mode var3, WebServiceFeature... var4);

   public abstract Dispatch<Object> createDispatch(EndpointReference var1, JAXBContext var2, Service.Mode var3, WebServiceFeature... var4);

   public abstract QName getServiceName();

   public abstract Iterator<QName> getPorts();

   public abstract URL getWSDLDocumentLocation();

   public abstract HandlerResolver getHandlerResolver();

   public abstract void setHandlerResolver(HandlerResolver var1);

   public abstract Executor getExecutor();

   public abstract void setExecutor(Executor var1);
}
