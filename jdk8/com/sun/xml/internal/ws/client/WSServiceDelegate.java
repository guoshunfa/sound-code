package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.Closeable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import com.sun.xml.internal.ws.api.client.ServiceInterceptorFactory;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.pipe.Stubs;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.AddressingFeature;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class WSServiceDelegate extends WSService {
   private final Map<QName, PortInfo> ports;
   @NotNull
   private HandlerConfigurator handlerConfigurator;
   private final Class<? extends Service> serviceClass;
   private final WebServiceFeatureList features;
   @NotNull
   private final QName serviceName;
   private final Map<QName, SEIPortInfo> seiContext;
   private volatile Executor executor;
   @Nullable
   private WSDLService wsdlService;
   private final Container container;
   @NotNull
   final ServiceInterceptor serviceInterceptor;
   private URL wsdlURL;
   protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];

   protected Map<QName, PortInfo> getQNameToPortInfoMap() {
      return this.ports;
   }

   public WSServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeature... features) {
      this(wsdlDocumentLocation, serviceName, serviceClass, new WebServiceFeatureList(features));
   }

   protected WSServiceDelegate(URL wsdlDocumentLocation, QName serviceName, Class<? extends Service> serviceClass, WebServiceFeatureList features) {
      this((Source)(wsdlDocumentLocation == null ? null : new StreamSource(wsdlDocumentLocation.toExternalForm())), serviceName, serviceClass, (WebServiceFeatureList)features);
      this.wsdlURL = wsdlDocumentLocation;
   }

   public WSServiceDelegate(@Nullable Source wsdl, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeature... features) {
      this(wsdl, serviceName, serviceClass, new WebServiceFeatureList(features));
   }

   protected WSServiceDelegate(@Nullable Source wsdl, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeatureList features) {
      this(wsdl, (WSDLService)null, serviceName, serviceClass, (WebServiceFeatureList)features);
   }

   public WSServiceDelegate(@Nullable Source wsdl, @Nullable WSDLService service, @NotNull QName serviceName, @NotNull Class<? extends Service> serviceClass, WebServiceFeature... features) {
      this(wsdl, service, serviceName, serviceClass, new WebServiceFeatureList(features));
   }

   public WSServiceDelegate(@Nullable Source wsdl, @Nullable WSDLService service, @NotNull QName serviceName, @NotNull final Class<? extends Service> serviceClass, WebServiceFeatureList features) {
      this.ports = new HashMap();
      this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl((HandlerResolver)null);
      this.seiContext = new HashMap();
      if (serviceName == null) {
         throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL((Object)null));
      } else {
         this.features = features;
         WSService.InitParams initParams = (WSService.InitParams)INIT_PARAMS.get();
         INIT_PARAMS.set((Object)null);
         if (initParams == null) {
            initParams = EMPTY_PARAMS;
         }

         this.serviceName = serviceName;
         this.serviceClass = serviceClass;
         Container tContainer = initParams.getContainer() != null ? initParams.getContainer() : ContainerResolver.getInstance().getContainer();
         if (tContainer == Container.NONE) {
            tContainer = new ClientContainer();
         }

         this.container = (Container)tContainer;
         ComponentFeature cf = (ComponentFeature)this.features.get(ComponentFeature.class);
         if (cf != null) {
            switch(cf.getTarget()) {
            case SERVICE:
               this.getComponents().add(cf.getComponent());
               break;
            case CONTAINER:
               this.container.getComponents().add(cf.getComponent());
               break;
            default:
               throw new IllegalArgumentException();
            }
         }

         ComponentsFeature csf = (ComponentsFeature)this.features.get(ComponentsFeature.class);
         if (csf != null) {
            Iterator var10 = csf.getComponentFeatures().iterator();

            while(var10.hasNext()) {
               ComponentFeature cfi = (ComponentFeature)var10.next();
               switch(cfi.getTarget()) {
               case SERVICE:
                  this.getComponents().add(cfi.getComponent());
                  break;
               case CONTAINER:
                  this.container.getComponents().add(cfi.getComponent());
                  break;
               default:
                  throw new IllegalArgumentException();
               }
            }
         }

         ServiceInterceptor interceptor = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
         ServiceInterceptor si = (ServiceInterceptor)this.container.getSPI(ServiceInterceptor.class);
         if (si != null) {
            interceptor = ServiceInterceptor.aggregate(interceptor, si);
         }

         this.serviceInterceptor = interceptor;
         if (service == null) {
            if (wsdl == null && serviceClass != Service.class) {
               WebServiceClient wsClient = (WebServiceClient)AccessController.doPrivileged(new PrivilegedAction<WebServiceClient>() {
                  public WebServiceClient run() {
                     return (WebServiceClient)serviceClass.getAnnotation(WebServiceClient.class);
                  }
               });
               String wsdlLocation = wsClient.wsdlLocation();
               wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
               wsdl = new StreamSource(wsdlLocation);
            }

            if (wsdl != null) {
               try {
                  URL url = ((Source)wsdl).getSystemId() == null ? null : JAXWSUtils.getEncodedURL(((Source)wsdl).getSystemId());
                  WSDLModel model = this.parseWSDL(url, (Source)wsdl, serviceClass);
                  service = model.getService(this.serviceName);
                  if (service == null) {
                     throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(model.getServices().keySet())));
                  }

                  Iterator var14 = service.getPorts().iterator();

                  while(var14.hasNext()) {
                     WSDLPort port = (WSDLPort)var14.next();
                     this.ports.put(port.getName(), new PortInfo(this, port));
                  }
               } catch (MalformedURLException var16) {
                  throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(((Source)wsdl).getSystemId()));
               }
            }
         } else {
            Iterator var20 = service.getPorts().iterator();

            while(var20.hasNext()) {
               WSDLPort port = (WSDLPort)var20.next();
               this.ports.put(port.getName(), new PortInfo(this, port));
            }
         }

         this.wsdlService = service;
         if (serviceClass != Service.class) {
            HandlerChain handlerChain = (HandlerChain)AccessController.doPrivileged(new PrivilegedAction<HandlerChain>() {
               public HandlerChain run() {
                  return (HandlerChain)serviceClass.getAnnotation(HandlerChain.class);
               }
            });
            if (handlerChain != null) {
               this.handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this);
            }
         }

      }
   }

   private WSDLModel parseWSDL(URL wsdlDocumentLocation, Source wsdlSource, Class serviceClass) {
      try {
         return RuntimeWSDLParser.parse(wsdlDocumentLocation, wsdlSource, this.createCatalogResolver(), true, this.getContainer(), serviceClass, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
      } catch (IOException var5) {
         throw new WebServiceException(var5);
      } catch (XMLStreamException var6) {
         throw new WebServiceException(var6);
      } catch (SAXException var7) {
         throw new WebServiceException(var7);
      } catch (ServiceConfigurationError var8) {
         throw new WebServiceException(var8);
      }
   }

   protected EntityResolver createCatalogResolver() {
      return XmlUtil.createDefaultCatalogResolver();
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setExecutor(Executor executor) {
      this.executor = executor;
   }

   public HandlerResolver getHandlerResolver() {
      return this.handlerConfigurator.getResolver();
   }

   final HandlerConfigurator getHandlerConfigurator() {
      return this.handlerConfigurator;
   }

   public void setHandlerResolver(HandlerResolver resolver) {
      this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(resolver);
   }

   public <T> T getPort(QName portName, Class<T> portInterface) throws WebServiceException {
      return this.getPort(portName, portInterface, EMPTY_FEATURES);
   }

   public <T> T getPort(QName portName, Class<T> portInterface, WebServiceFeature... features) {
      if (portName != null && portInterface != null) {
         WSDLService tWsdlService = this.wsdlService;
         if (tWsdlService == null) {
            tWsdlService = this.getWSDLModelfromSEI(portInterface);
            if (tWsdlService == null) {
               throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
            }
         }

         WSDLPort portModel = this.getPortModel(tWsdlService, portName);
         return this.getPort(portModel.getEPR(), portName, portInterface, new WebServiceFeatureList(features));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public <T> T getPort(EndpointReference epr, Class<T> portInterface, WebServiceFeature... features) {
      return this.getPort(WSEndpointReference.create(epr), portInterface, features);
   }

   public <T> T getPort(WSEndpointReference wsepr, Class<T> portInterface, WebServiceFeature... features) {
      WebServiceFeatureList featureList = new WebServiceFeatureList(features);
      QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(featureList, portInterface.getClassLoader()));
      QName portName = this.getPortNameFromEPR(wsepr, portTypeName);
      return this.getPort(wsepr, portName, portInterface, featureList);
   }

   protected <T> T getPort(WSEndpointReference wsepr, QName portName, Class<T> portInterface, WebServiceFeatureList features) {
      ComponentFeature cf = (ComponentFeature)features.get(ComponentFeature.class);
      if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
         throw new IllegalArgumentException();
      } else {
         ComponentsFeature csf = (ComponentsFeature)features.get(ComponentsFeature.class);
         if (csf != null) {
            Iterator var7 = csf.getComponentFeatures().iterator();

            while(var7.hasNext()) {
               ComponentFeature cfi = (ComponentFeature)var7.next();
               if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                  throw new IllegalArgumentException();
               }
            }
         }

         features.addAll(this.features);
         SEIPortInfo spi = this.addSEI(portName, portInterface, features);
         return this.createEndpointIFBaseProxy(wsepr, portName, portInterface, features, spi);
      }
   }

   public <T> T getPort(Class<T> portInterface, WebServiceFeature... features) {
      QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(new WebServiceFeatureList(features), portInterface.getClassLoader()));
      WSDLService tmpWsdlService = this.wsdlService;
      if (tmpWsdlService == null) {
         tmpWsdlService = this.getWSDLModelfromSEI(portInterface);
         if (tmpWsdlService == null) {
            throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
         }
      }

      WSDLPort port = tmpWsdlService.getMatchingPort(portTypeName);
      if (port == null) {
         throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
      } else {
         QName portName = port.getName();
         return this.getPort(portName, portInterface, features);
      }
   }

   public <T> T getPort(Class<T> portInterface) throws WebServiceException {
      return this.getPort(portInterface, EMPTY_FEATURES);
   }

   public void addPort(QName portName, String bindingId, String endpointAddress) throws WebServiceException {
      if (!this.ports.containsKey(portName)) {
         BindingID bid = bindingId == null ? BindingID.SOAP11_HTTP : BindingID.parse(bindingId);
         this.ports.put(portName, new PortInfo(this, endpointAddress == null ? null : EndpointAddress.create(endpointAddress), portName, (BindingID)bid));
      } else {
         throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(portName.toString()));
      }
   }

   public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode) throws WebServiceException {
      return this.createDispatch(portName, aClass, mode, EMPTY_FEATURES);
   }

   public <T> Dispatch<T> createDispatch(QName portName, WSEndpointReference wsepr, Class<T> aClass, Service.Mode mode, WebServiceFeature... features) {
      return this.createDispatch(portName, wsepr, aClass, mode, new WebServiceFeatureList(features));
   }

   public <T> Dispatch<T> createDispatch(QName portName, WSEndpointReference wsepr, Class<T> aClass, Service.Mode mode, WebServiceFeatureList features) {
      PortInfo port = this.safeGetPort(portName);
      ComponentFeature cf = (ComponentFeature)features.get(ComponentFeature.class);
      if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
         throw new IllegalArgumentException();
      } else {
         ComponentsFeature csf = (ComponentsFeature)features.get(ComponentsFeature.class);
         if (csf != null) {
            Iterator var9 = csf.getComponentFeatures().iterator();

            while(var9.hasNext()) {
               ComponentFeature cfi = (ComponentFeature)var9.next();
               if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                  throw new IllegalArgumentException();
               }
            }
         }

         features.addAll(this.features);
         BindingImpl binding = port.createBinding(features, (Class)null, (BindingImpl)null);
         binding.setMode(mode);
         Dispatch<T> dispatch = Stubs.createDispatch(port, this, binding, aClass, mode, wsepr);
         this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
         return dispatch;
      }
   }

   public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode, WebServiceFeature... features) {
      return this.createDispatch(portName, aClass, mode, new WebServiceFeatureList(features));
   }

   public <T> Dispatch<T> createDispatch(QName portName, Class<T> aClass, Service.Mode mode, WebServiceFeatureList features) {
      WSEndpointReference wsepr = null;
      boolean isAddressingEnabled = false;
      AddressingFeature af = (AddressingFeature)features.get(AddressingFeature.class);
      if (af == null) {
         af = (AddressingFeature)this.features.get(AddressingFeature.class);
      }

      if (af != null && af.isEnabled()) {
         isAddressingEnabled = true;
      }

      MemberSubmissionAddressingFeature msa = (MemberSubmissionAddressingFeature)features.get(MemberSubmissionAddressingFeature.class);
      if (msa == null) {
         msa = (MemberSubmissionAddressingFeature)this.features.get(MemberSubmissionAddressingFeature.class);
      }

      if (msa != null && msa.isEnabled()) {
         isAddressingEnabled = true;
      }

      if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
         wsepr = this.wsdlService.get(portName).getEPR();
      }

      return this.createDispatch(portName, wsepr, aClass, mode, features);
   }

   public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Service.Mode mode, WebServiceFeature... features) {
      WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
      QName portName = this.addPortEpr(wsepr);
      return this.createDispatch(portName, wsepr, type, mode, features);
   }

   @NotNull
   public PortInfo safeGetPort(QName portName) {
      PortInfo port = (PortInfo)this.ports.get(portName);
      if (port == null) {
         throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildNameList(this.ports.keySet())));
      } else {
         return port;
      }
   }

   private StringBuilder buildNameList(Collection<QName> names) {
      StringBuilder sb = new StringBuilder();

      QName qn;
      for(Iterator var3 = names.iterator(); var3.hasNext(); sb.append((Object)qn)) {
         qn = (QName)var3.next();
         if (sb.length() > 0) {
            sb.append(',');
         }
      }

      return sb;
   }

   public EndpointAddress getEndpointAddress(QName qName) {
      PortInfo p = (PortInfo)this.ports.get(qName);
      return p != null ? p.targetEndpoint : null;
   }

   public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode) throws WebServiceException {
      return this.createDispatch(portName, jaxbContext, mode, EMPTY_FEATURES);
   }

   public Dispatch<Object> createDispatch(QName portName, WSEndpointReference wsepr, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature... features) {
      return this.createDispatch(portName, wsepr, jaxbContext, mode, new WebServiceFeatureList(features));
   }

   protected Dispatch<Object> createDispatch(QName portName, WSEndpointReference wsepr, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeatureList features) {
      PortInfo port = this.safeGetPort(portName);
      ComponentFeature cf = (ComponentFeature)features.get(ComponentFeature.class);
      if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
         throw new IllegalArgumentException();
      } else {
         ComponentsFeature csf = (ComponentsFeature)features.get(ComponentsFeature.class);
         if (csf != null) {
            Iterator var9 = csf.getComponentFeatures().iterator();

            while(var9.hasNext()) {
               ComponentFeature cfi = (ComponentFeature)var9.next();
               if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                  throw new IllegalArgumentException();
               }
            }
         }

         features.addAll(this.features);
         BindingImpl binding = port.createBinding(features, (Class)null, (BindingImpl)null);
         binding.setMode(mode);
         Dispatch<Object> dispatch = Stubs.createJAXBDispatch(port, binding, jaxbContext, mode, wsepr);
         this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
         return dispatch;
      }
   }

   @NotNull
   public Container getContainer() {
      return this.container;
   }

   public Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeature... webServiceFeatures) {
      return this.createDispatch(portName, jaxbContext, mode, new WebServiceFeatureList(webServiceFeatures));
   }

   protected Dispatch<Object> createDispatch(QName portName, JAXBContext jaxbContext, Service.Mode mode, WebServiceFeatureList features) {
      WSEndpointReference wsepr = null;
      boolean isAddressingEnabled = false;
      AddressingFeature af = (AddressingFeature)features.get(AddressingFeature.class);
      if (af == null) {
         af = (AddressingFeature)this.features.get(AddressingFeature.class);
      }

      if (af != null && af.isEnabled()) {
         isAddressingEnabled = true;
      }

      MemberSubmissionAddressingFeature msa = (MemberSubmissionAddressingFeature)features.get(MemberSubmissionAddressingFeature.class);
      if (msa == null) {
         msa = (MemberSubmissionAddressingFeature)this.features.get(MemberSubmissionAddressingFeature.class);
      }

      if (msa != null && msa.isEnabled()) {
         isAddressingEnabled = true;
      }

      if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
         wsepr = this.wsdlService.get(portName).getEPR();
      }

      return this.createDispatch(portName, wsepr, jaxbContext, mode, features);
   }

   public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Service.Mode mode, WebServiceFeature... features) {
      WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
      QName portName = this.addPortEpr(wsepr);
      return this.createDispatch(portName, wsepr, context, mode, features);
   }

   private QName addPortEpr(WSEndpointReference wsepr) {
      if (wsepr == null) {
         throw new WebServiceException(ProviderApiMessages.NULL_EPR());
      } else {
         QName eprPortName = this.getPortNameFromEPR(wsepr, (QName)null);
         PortInfo portInfo = new PortInfo(this, wsepr.getAddress() == null ? null : EndpointAddress.create(wsepr.getAddress()), eprPortName, this.getPortModel(this.wsdlService, eprPortName).getBinding().getBindingId());
         if (!this.ports.containsKey(eprPortName)) {
            this.ports.put(eprPortName, portInfo);
         }

         return eprPortName;
      }
   }

   private QName getPortNameFromEPR(@NotNull WSEndpointReference wsepr, @Nullable QName portTypeName) {
      WSEndpointReference.Metadata metadata = wsepr.getMetaData();
      QName eprServiceName = metadata.getServiceName();
      QName eprPortName = metadata.getPortName();
      if (eprServiceName != null && !eprServiceName.equals(this.serviceName)) {
         throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match");
      } else {
         if (this.wsdlService == null) {
            Source eprWsdlSource = metadata.getWsdlSource();
            if (eprWsdlSource == null) {
               throw new WebServiceException(ProviderApiMessages.NULL_WSDL());
            }

            try {
               WSDLModel eprWsdlMdl = this.parseWSDL(new URL(wsepr.getAddress()), eprWsdlSource, (Class)null);
               this.wsdlService = eprWsdlMdl.getService(this.serviceName);
               if (this.wsdlService == null) {
                  throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(eprWsdlMdl.getServices().keySet())));
               }
            } catch (MalformedURLException var9) {
               throw new WebServiceException(ClientMessages.INVALID_ADDRESS(wsepr.getAddress()));
            }
         }

         QName portName = eprPortName;
         if (eprPortName == null && portTypeName != null) {
            WSDLPort port = this.wsdlService.getMatchingPort(portTypeName);
            if (port == null) {
               throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
            }

            portName = port.getName();
         }

         if (portName == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME());
         } else if (this.wsdlService.get(portName) == null) {
            throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(portName, this.buildWsdlPortNames()));
         } else {
            return portName;
         }
      }
   }

   private <T> T createProxy(final Class<T> portInterface, final InvocationHandler pis) {
      final ClassLoader loader = getDelegatingLoader(portInterface.getClassLoader(), WSServiceDelegate.class.getClassLoader());
      RuntimePermission perm = new RuntimePermission("accessClassInPackage.com.sun.xml.internal.*");
      PermissionCollection perms = perm.newPermissionCollection();
      perms.add(perm);
      return AccessController.doPrivileged(new PrivilegedAction<T>() {
         public T run() {
            Object proxy = Proxy.newProxyInstance(loader, new Class[]{portInterface, WSBindingProvider.class, Closeable.class}, pis);
            return portInterface.cast(proxy);
         }
      }, new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, perms)}));
   }

   private WSDLService getWSDLModelfromSEI(final Class sei) {
      WebService ws = (WebService)AccessController.doPrivileged(new PrivilegedAction<WebService>() {
         public WebService run() {
            return (WebService)sei.getAnnotation(WebService.class);
         }
      });
      if (ws != null && !ws.wsdlLocation().equals("")) {
         String wsdlLocation = ws.wsdlLocation();
         wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
         Source wsdl = new StreamSource(wsdlLocation);
         WSDLService service = null;

         try {
            URL url = wsdl.getSystemId() == null ? null : new URL(wsdl.getSystemId());
            WSDLModel model = this.parseWSDL(url, wsdl, sei);
            service = model.getService(this.serviceName);
            if (service == null) {
               throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(model.getServices().keySet())));
            } else {
               return service;
            }
         } catch (MalformedURLException var8) {
            throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
         }
      } else {
         return null;
      }
   }

   public QName getServiceName() {
      return this.serviceName;
   }

   public Class getServiceClass() {
      return this.serviceClass;
   }

   public Iterator<QName> getPorts() throws WebServiceException {
      return this.ports.keySet().iterator();
   }

   public URL getWSDLDocumentLocation() {
      if (this.wsdlService == null) {
         return null;
      } else {
         try {
            return new URL(this.wsdlService.getParent().getLocation().getSystemId());
         } catch (MalformedURLException var2) {
            throw new AssertionError(var2);
         }
      }
   }

   private <T> T createEndpointIFBaseProxy(@Nullable WSEndpointReference epr, QName portName, Class<T> portInterface, WebServiceFeatureList webServiceFeatures, SEIPortInfo eif) {
      if (this.wsdlService == null) {
         throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(this.serviceName));
      } else if (this.wsdlService.get(portName) == null) {
         throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
      } else {
         BindingImpl binding = eif.createBinding(webServiceFeatures, portInterface);
         InvocationHandler pis = this.getStubHandler(binding, eif, epr);
         T proxy = this.createProxy(portInterface, pis);
         if (this.serviceInterceptor != null) {
            this.serviceInterceptor.postCreateProxy((WSBindingProvider)proxy, portInterface);
         }

         return proxy;
      }
   }

   protected InvocationHandler getStubHandler(BindingImpl binding, SEIPortInfo eif, @Nullable WSEndpointReference epr) {
      return new SEIStub(eif, binding, eif.model, epr);
   }

   private StringBuilder buildWsdlPortNames() {
      Set<QName> wsdlPortNames = new HashSet();
      Iterator var2 = this.wsdlService.getPorts().iterator();

      while(var2.hasNext()) {
         WSDLPort port = (WSDLPort)var2.next();
         wsdlPortNames.add(port.getName());
      }

      return this.buildNameList(wsdlPortNames);
   }

   @NotNull
   public WSDLPort getPortModel(WSDLService wsdlService, QName portName) {
      WSDLPort port = wsdlService.get(portName);
      if (port == null) {
         throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
      } else {
         return port;
      }
   }

   private SEIPortInfo addSEI(QName portName, Class portInterface, WebServiceFeatureList features) throws WebServiceException {
      boolean ownModel = this.useOwnSEIModel(features);
      if (ownModel) {
         return this.createSEIPortInfo(portName, portInterface, features);
      } else {
         SEIPortInfo spi = (SEIPortInfo)this.seiContext.get(portName);
         if (spi == null) {
            spi = this.createSEIPortInfo(portName, portInterface, features);
            this.seiContext.put(spi.portName, spi);
            this.ports.put(spi.portName, spi);
         }

         return spi;
      }
   }

   public SEIModel buildRuntimeModel(QName serviceName, QName portName, Class portInterface, WSDLPort wsdlPort, WebServiceFeatureList features) {
      DatabindingFactory fac = DatabindingFactory.newInstance();
      DatabindingConfig config = new DatabindingConfig();
      config.setContractClass(portInterface);
      config.getMappingInfo().setServiceName(serviceName);
      config.setWsdlPort(wsdlPort);
      config.setFeatures((Iterable)features);
      config.setClassLoader(portInterface.getClassLoader());
      config.getMappingInfo().setPortName(portName);
      config.setWsdlURL(this.wsdlURL);
      config.setMetadataReader(this.getMetadadaReader(features, portInterface.getClassLoader()));
      DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
      return rt.getModel();
   }

   private MetadataReader getMetadadaReader(WebServiceFeatureList features, ClassLoader classLoader) {
      if (features == null) {
         return null;
      } else {
         ExternalMetadataFeature ef = (ExternalMetadataFeature)features.get(ExternalMetadataFeature.class);
         return ef != null ? ef.getMetadataReader(classLoader, false) : null;
      }
   }

   private SEIPortInfo createSEIPortInfo(QName portName, Class portInterface, WebServiceFeatureList features) {
      WSDLPort wsdlPort = this.getPortModel(this.wsdlService, portName);
      SEIModel model = this.buildRuntimeModel(this.serviceName, portName, portInterface, wsdlPort, features);
      return new SEIPortInfo(this, portInterface, (SOAPSEIModel)model, wsdlPort);
   }

   private boolean useOwnSEIModel(WebServiceFeatureList features) {
      return features.contains(UsesJAXBContextFeature.class);
   }

   public WSDLService getWsdlService() {
      return this.wsdlService;
   }

   private static ClassLoader getDelegatingLoader(ClassLoader loader1, ClassLoader loader2) {
      if (loader1 == null) {
         return loader2;
      } else {
         return (ClassLoader)(loader2 == null ? loader1 : new WSServiceDelegate.DelegatingLoader(loader1, loader2));
      }
   }

   private static final class DelegatingLoader extends ClassLoader {
      private final ClassLoader loader;

      public int hashCode() {
         int prime = true;
         int result = 1;
         int result = 31 * result + (this.loader == null ? 0 : this.loader.hashCode());
         result = 31 * result + (this.getParent() == null ? 0 : this.getParent().hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            WSServiceDelegate.DelegatingLoader other = (WSServiceDelegate.DelegatingLoader)obj;
            if (this.loader == null) {
               if (other.loader != null) {
                  return false;
               }
            } else if (!this.loader.equals(other.loader)) {
               return false;
            }

            if (this.getParent() == null) {
               if (other.getParent() != null) {
                  return false;
               }
            } else if (!this.getParent().equals(other.getParent())) {
               return false;
            }

            return true;
         }
      }

      DelegatingLoader(ClassLoader loader1, ClassLoader loader2) {
         super(loader2);
         this.loader = loader1;
      }

      protected Class findClass(String name) throws ClassNotFoundException {
         return this.loader.loadClass(name);
      }

      protected URL findResource(String name) {
         return this.loader.getResource(name);
      }
   }

   static class DaemonThreadFactory implements ThreadFactory {
      public Thread newThread(Runnable r) {
         Thread daemonThread = new Thread(r);
         daemonThread.setDaemon(Boolean.TRUE);
         return daemonThread;
      }
   }
}
