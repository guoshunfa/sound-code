package com.sun.xml.internal.ws.spi;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public class ProviderImpl extends Provider {
   private static final ContextClassloaderLocal<JAXBContext> eprjc = new ContextClassloaderLocal<JAXBContext>() {
      protected JAXBContext initialValue() throws Exception {
         return ProviderImpl.getEPRJaxbContext();
      }
   };
   public static final ProviderImpl INSTANCE = new ProviderImpl();

   public Endpoint createEndpoint(String bindingId, Object implementor) {
      return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
   }

   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass) {
      return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass, new WebServiceFeature[0]);
   }

   public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class serviceClass, WebServiceFeature... features) {
      WebServiceFeature[] var5 = features;
      int var6 = features.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         WebServiceFeature feature = var5[var7];
         if (!(feature instanceof ServiceSharedFeatureMarker)) {
            throw new WebServiceException("Doesn't support any Service specific features");
         }
      }

      return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass, features);
   }

   public ServiceDelegate createServiceDelegate(Source wsdlSource, QName serviceName, Class serviceClass) {
      return new WSServiceDelegate(wsdlSource, serviceName, serviceClass, new WebServiceFeature[0]);
   }

   public Endpoint createAndPublishEndpoint(String address, Object implementor) {
      Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
      endpoint.publish(address);
      return endpoint;
   }

   public Endpoint createEndpoint(String bindingId, Object implementor, WebServiceFeature... features) {
      return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, features);
   }

   public Endpoint createAndPublishEndpoint(String address, Object implementor, WebServiceFeature... features) {
      Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, features);
      endpoint.publish(address);
      return endpoint;
   }

   public Endpoint createEndpoint(String bindingId, Class implementorClass, Invoker invoker, WebServiceFeature... features) {
      return new EndpointImpl(bindingId != null ? BindingID.parse(bindingId) : BindingID.parse(implementorClass), implementorClass, invoker, features);
   }

   public EndpointReference readEndpointReference(Source eprInfoset) {
      try {
         Unmarshaller unmarshaller = ((JAXBContext)eprjc.get()).createUnmarshaller();
         return (EndpointReference)unmarshaller.unmarshal(eprInfoset);
      } catch (JAXBException var3) {
         throw new WebServiceException("Error creating Marshaller or marshalling.", var3);
      }
   }

   public <T> T getPort(EndpointReference endpointReference, Class<T> clazz, WebServiceFeature... webServiceFeatures) {
      if (endpointReference == null) {
         throw new WebServiceException(ProviderApiMessages.NULL_EPR());
      } else {
         WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
         WSEndpointReference.Metadata metadata = wsepr.getMetaData();
         if (metadata.getWsdlSource() != null) {
            WSService service = (WSService)this.createServiceDelegate(metadata.getWsdlSource(), metadata.getServiceName(), Service.class);
            return service.getPort(wsepr, clazz, webServiceFeatures);
         } else {
            throw new WebServiceException("WSDL metadata is missing in EPR");
         }
      }
   }

   public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters) {
      return this.createW3CEndpointReference(address, (QName)null, serviceName, portName, metadata, wsdlDocumentLocation, referenceParameters, (List)null, (Map)null);
   }

   public W3CEndpointReference createW3CEndpointReference(String address, QName interfaceName, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters, List<Element> elements, Map<QName, String> attributes) {
      Container container = ContainerResolver.getInstance().getContainer();
      if (address == null) {
         if (serviceName == null || portName == null) {
            throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS_SERVICE_ENDPOINT());
         }

         Module module = (Module)container.getSPI(Module.class);
         if (module != null) {
            List<BoundEndpoint> beList = module.getBoundEndpoints();
            Iterator var13 = beList.iterator();

            while(var13.hasNext()) {
               BoundEndpoint be = (BoundEndpoint)var13.next();
               WSEndpoint wse = be.getEndpoint();
               if (wse.getServiceName().equals(serviceName) && wse.getPortName().equals(portName)) {
                  try {
                     address = be.getAddress().toString();
                  } catch (WebServiceException var18) {
                  }
                  break;
               }
            }
         }

         if (address == null) {
            throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS());
         }
      }

      if (serviceName == null && portName != null) {
         throw new IllegalStateException(ProviderApiMessages.NULL_SERVICE());
      } else {
         String wsdlTargetNamespace = null;
         if (wsdlDocumentLocation != null) {
            try {
               EntityResolver er = XmlUtil.createDefaultCatalogResolver();
               URL wsdlLoc = new URL(wsdlDocumentLocation);
               WSDLModel wsdlDoc = RuntimeWSDLParser.parse(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), er, true, container, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
               if (serviceName != null) {
                  WSDLService wsdlService = wsdlDoc.getService(serviceName);
                  if (wsdlService == null) {
                     throw new IllegalStateException(ProviderApiMessages.NOTFOUND_SERVICE_IN_WSDL(serviceName, wsdlDocumentLocation));
                  }

                  if (portName != null) {
                     WSDLPort wsdlPort = wsdlService.get(portName);
                     if (wsdlPort == null) {
                        throw new IllegalStateException(ProviderApiMessages.NOTFOUND_PORT_IN_WSDL(portName, serviceName, wsdlDocumentLocation));
                     }
                  }

                  wsdlTargetNamespace = serviceName.getNamespaceURI();
               } else {
                  QName firstService = wsdlDoc.getFirstServiceName();
                  wsdlTargetNamespace = firstService.getNamespaceURI();
               }
            } catch (Exception var17) {
               throw new IllegalStateException(ProviderApiMessages.ERROR_WSDL(wsdlDocumentLocation), var17);
            }
         }

         if (metadata != null && metadata.size() == 0) {
            metadata = null;
         }

         return (W3CEndpointReference)(new WSEndpointReference(AddressingVersion.fromSpecClass(W3CEndpointReference.class), address, serviceName, portName, interfaceName, metadata, wsdlDocumentLocation, wsdlTargetNamespace, referenceParameters, elements, attributes)).toSpec(W3CEndpointReference.class);
      }
   }

   private static JAXBContext getEPRJaxbContext() {
      return (JAXBContext)AccessController.doPrivileged(new PrivilegedAction<JAXBContext>() {
         public JAXBContext run() {
            try {
               return JAXBContext.newInstance(MemberSubmissionEndpointReference.class, W3CEndpointReference.class);
            } catch (JAXBException var2) {
               throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", var2);
            }
         }
      });
   }
}
