package javax.xml.ws;

import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.spi.ServiceDelegate;

public class Service {
   private ServiceDelegate delegate;

   protected Service(URL wsdlDocumentLocation, QName serviceName) {
      this.delegate = javax.xml.ws.spi.Provider.provider().createServiceDelegate(wsdlDocumentLocation, serviceName, this.getClass());
   }

   protected Service(URL wsdlDocumentLocation, QName serviceName, WebServiceFeature... features) {
      this.delegate = javax.xml.ws.spi.Provider.provider().createServiceDelegate(wsdlDocumentLocation, serviceName, this.getClass(), features);
   }

   public <T> T getPort(QName portName, Class<T> serviceEndpointInterface) {
      return this.delegate.getPort(portName, serviceEndpointInterface);
   }

   public <T> T getPort(QName portName, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return this.delegate.getPort(portName, serviceEndpointInterface, features);
   }

   public <T> T getPort(Class<T> serviceEndpointInterface) {
      return this.delegate.getPort(serviceEndpointInterface);
   }

   public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return this.delegate.getPort(serviceEndpointInterface, features);
   }

   public <T> T getPort(EndpointReference endpointReference, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return this.delegate.getPort(endpointReference, serviceEndpointInterface, features);
   }

   public void addPort(QName portName, String bindingId, String endpointAddress) {
      this.delegate.addPort(portName, bindingId, endpointAddress);
   }

   public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Service.Mode mode) {
      return this.delegate.createDispatch(portName, type, mode);
   }

   public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Service.Mode mode, WebServiceFeature... features) {
      return this.delegate.createDispatch(portName, type, mode, features);
   }

   public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Service.Mode mode, WebServiceFeature... features) {
      return this.delegate.createDispatch(endpointReference, type, mode, features);
   }

   public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Service.Mode mode) {
      return this.delegate.createDispatch(portName, context, mode);
   }

   public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Service.Mode mode, WebServiceFeature... features) {
      return this.delegate.createDispatch(portName, context, mode, features);
   }

   public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Service.Mode mode, WebServiceFeature... features) {
      return this.delegate.createDispatch(endpointReference, context, mode, features);
   }

   public QName getServiceName() {
      return this.delegate.getServiceName();
   }

   public Iterator<QName> getPorts() {
      return this.delegate.getPorts();
   }

   public URL getWSDLDocumentLocation() {
      return this.delegate.getWSDLDocumentLocation();
   }

   public HandlerResolver getHandlerResolver() {
      return this.delegate.getHandlerResolver();
   }

   public void setHandlerResolver(HandlerResolver handlerResolver) {
      this.delegate.setHandlerResolver(handlerResolver);
   }

   public Executor getExecutor() {
      return this.delegate.getExecutor();
   }

   public void setExecutor(Executor executor) {
      this.delegate.setExecutor(executor);
   }

   public static Service create(URL wsdlDocumentLocation, QName serviceName) {
      return new Service(wsdlDocumentLocation, serviceName);
   }

   public static Service create(URL wsdlDocumentLocation, QName serviceName, WebServiceFeature... features) {
      return new Service(wsdlDocumentLocation, serviceName, features);
   }

   public static Service create(QName serviceName) {
      return new Service((URL)null, serviceName);
   }

   public static Service create(QName serviceName, WebServiceFeature... features) {
      return new Service((URL)null, serviceName, features);
   }

   public static enum Mode {
      MESSAGE,
      PAYLOAD;
   }
}
