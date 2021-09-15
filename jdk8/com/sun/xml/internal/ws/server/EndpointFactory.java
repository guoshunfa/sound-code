package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EndpointFactory {
   private static final EndpointFactory instance = new EndpointFactory();
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");

   public static EndpointFactory getInstance() {
      return instance;
   }

   public static <T> WSEndpoint<T> createEndpoint(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous) {
      return createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
   }

   public static <T> WSEndpoint<T> createEndpoint(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
      EndpointFactory factory = container != null ? (EndpointFactory)container.getSPI(EndpointFactory.class) : null;
      if (factory == null) {
         factory = getInstance();
      }

      return factory.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
   }

   public <T> WSEndpoint<T> create(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous) {
      return this.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
   }

   public <T> WSEndpoint<T> create(Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
      if (implType == null) {
         throw new IllegalArgumentException();
      } else {
         MetadataReader metadataReader = getExternalMetadatReader(implType, (WSBinding)binding);
         if (isStandard) {
            verifyImplementorClass(implType, metadataReader);
         }

         if (invoker == null) {
            invoker = InstanceResolver.createDefault(implType).createInvoker();
         }

         List<SDDocumentSource> md = new ArrayList();
         if (metadata != null) {
            md.addAll(metadata);
         }

         if (primaryWsdl != null && !md.contains(primaryWsdl)) {
            md.add(primaryWsdl);
         }

         if (container == null) {
            container = ContainerResolver.getInstance().getContainer();
         }

         if (serviceName == null) {
            serviceName = getDefaultServiceName(implType, metadataReader);
         }

         if (portName == null) {
            portName = getDefaultPortName(serviceName, implType, metadataReader);
         }

         String serviceNS = serviceName.getNamespaceURI();
         String portNS = portName.getNamespaceURI();
         if (!serviceNS.equals(portNS)) {
            throw new ServerRtException("wrong.tns.for.port", new Object[]{portNS, serviceNS});
         } else {
            if (binding == null) {
               binding = BindingImpl.create(BindingID.parse(implType));
            }

            if (isStandard && primaryWsdl != null) {
               verifyPrimaryWSDL(primaryWsdl, serviceName);
            }

            QName portTypeName = null;
            if (isStandard && implType.getAnnotation(WebServiceProvider.class) == null) {
               portTypeName = RuntimeModeler.getPortTypeName(implType, metadataReader);
            }

            List<SDDocumentImpl> docList = categoriseMetadata(md, serviceName, portTypeName);
            SDDocumentImpl primaryDoc = primaryWsdl != null ? SDDocumentImpl.create(primaryWsdl, serviceName, portTypeName) : findPrimary(docList);
            WSDLPort wsdlPort = null;
            AbstractSEIModelImpl seiModel = null;
            if (primaryDoc != null) {
               wsdlPort = getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
            }

            WebServiceFeatureList features = ((BindingImpl)binding).getFeatures();
            if (isStandard) {
               features.parseAnnotations(implType);
            }

            PolicyMap policyMap = null;
            EndpointAwareTube terminal;
            if (this.isUseProviderTube(implType, isStandard)) {
               Object configFtrs;
               if (wsdlPort != null) {
                  policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
                  configFtrs = wsdlPort.getFeatures();
               } else {
                  policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext((PolicyMap)null, container, implType, false, new PolicyMapMutator[0]));
                  configFtrs = PolicyUtil.getPortScopedFeatures(policyMap, serviceName, portName);
               }

               features.mergeFeatures((Iterable)configFtrs, true);
               terminal = this.createProviderInvokerTube(implType, (WSBinding)binding, invoker, container);
            } else {
               seiModel = createSEIModel(wsdlPort, implType, serviceName, portName, (WSBinding)binding, primaryDoc);
               if (binding instanceof SOAPBindingImpl) {
                  ((SOAPBindingImpl)binding).setPortKnownHeaders(((SOAPSEIModel)seiModel).getKnownHeaders());
               }

               if (primaryDoc == null) {
                  primaryDoc = generateWSDL((WSBinding)binding, seiModel, docList, container, implType);
                  wsdlPort = getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
                  seiModel.freeze(wsdlPort);
               }

               policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
               features.mergeFeatures((Iterable)wsdlPort.getFeatures(), true);
               terminal = this.createSEIInvokerTube(seiModel, invoker, (WSBinding)binding);
            }

            if (processHandlerAnnotation) {
               processHandlerAnnotation((WSBinding)binding, implType, serviceName, portName);
            }

            if (primaryDoc != null) {
               docList = findMetadataClosure(primaryDoc, docList, resolver);
            }

            ServiceDefinitionImpl serviceDefiniton = primaryDoc != null ? new ServiceDefinitionImpl(docList, primaryDoc) : null;
            return this.create(serviceName, portName, (WSBinding)binding, container, seiModel, wsdlPort, implType, serviceDefiniton, terminal, isTransportSynchronous, policyMap);
         }
      }
   }

   protected <T> WSEndpoint<T> create(QName serviceName, QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort wsdlPort, Class<T> implType, ServiceDefinitionImpl serviceDefinition, EndpointAwareTube terminal, boolean isTransportSynchronous, PolicyMap policyMap) {
      return new WSEndpointImpl(serviceName, portName, binding, container, seiModel, wsdlPort, implType, serviceDefinition, terminal, isTransportSynchronous, policyMap);
   }

   protected boolean isUseProviderTube(Class<?> implType, boolean isStandard) {
      return !isStandard || implType.getAnnotation(WebServiceProvider.class) != null;
   }

   protected EndpointAwareTube createSEIInvokerTube(AbstractSEIModelImpl seiModel, Invoker invoker, WSBinding binding) {
      return new SEIInvokerTube(seiModel, invoker, binding);
   }

   protected <T> EndpointAwareTube createProviderInvokerTube(Class<T> implType, WSBinding binding, Invoker invoker, Container container) {
      return ProviderInvokerTube.create(implType, binding, invoker, container);
   }

   private static List<SDDocumentImpl> findMetadataClosure(SDDocumentImpl primaryDoc, List<SDDocumentImpl> docList, EntityResolver resolver) {
      Map<String, SDDocumentImpl> oldMap = new HashMap();
      Iterator var4 = docList.iterator();

      while(var4.hasNext()) {
         SDDocumentImpl doc = (SDDocumentImpl)var4.next();
         oldMap.put(doc.getSystemId().toString(), doc);
      }

      Map<String, SDDocumentImpl> newMap = new HashMap();
      newMap.put(primaryDoc.getSystemId().toString(), primaryDoc);
      List<String> remaining = new ArrayList();
      remaining.addAll(primaryDoc.getImports());

      while(!remaining.isEmpty()) {
         String url = (String)remaining.remove(0);
         SDDocumentImpl doc = (SDDocumentImpl)oldMap.get(url);
         if (doc == null && resolver != null) {
            try {
               InputSource source = resolver.resolveEntity((String)null, url);
               if (source != null) {
                  MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
                  XMLStreamReader reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(source.getByteStream());
                  xsb.createFromXMLStreamReader(reader);
                  SDDocumentSource sdocSource = SDDocumentImpl.create(new URL(url), xsb);
                  doc = SDDocumentImpl.create(sdocSource, (QName)null, (QName)null);
               }
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }

         if (doc != null && !newMap.containsKey(url)) {
            newMap.put(url, doc);
            remaining.addAll(doc.getImports());
         }
      }

      List<SDDocumentImpl> newMetadata = new ArrayList();
      newMetadata.addAll(newMap.values());
      return newMetadata;
   }

   private static <T> void processHandlerAnnotation(WSBinding binding, Class<T> implType, QName serviceName, QName portName) {
      HandlerAnnotationInfo chainInfo = HandlerAnnotationProcessor.buildHandlerInfo(implType, serviceName, portName, binding);
      if (chainInfo != null) {
         binding.setHandlerChain(chainInfo.getHandlers());
         if (binding instanceof SOAPBinding) {
            ((SOAPBinding)binding).setRoles(chainInfo.getRoles());
         }
      }

   }

   public static boolean verifyImplementorClass(Class<?> clz) {
      return verifyImplementorClass(clz, (MetadataReader)null);
   }

   public static boolean verifyImplementorClass(Class<?> clz, MetadataReader metadataReader) {
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      WebServiceProvider wsProvider = (WebServiceProvider)((MetadataReader)metadataReader).getAnnotation(WebServiceProvider.class, clz);
      WebService ws = (WebService)((MetadataReader)metadataReader).getAnnotation(WebService.class, clz);
      if (wsProvider == null && ws == null) {
         throw new IllegalArgumentException(clz + " has neither @WebService nor @WebServiceProvider annotation");
      } else if (wsProvider != null && ws != null) {
         throw new IllegalArgumentException(clz + " has both @WebService and @WebServiceProvider annotations");
      } else if (wsProvider != null) {
         if (!Provider.class.isAssignableFrom(clz) && !AsyncProvider.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException(clz + " doesn't implement Provider or AsyncProvider interface");
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private static AbstractSEIModelImpl createSEIModel(WSDLPort wsdlPort, Class<?> implType, @NotNull QName serviceName, @NotNull QName portName, WSBinding binding, SDDocumentSource primaryWsdl) {
      DatabindingFactory fac = DatabindingFactory.newInstance();
      DatabindingConfig config = new DatabindingConfig();
      config.setEndpointClass(implType);
      config.getMappingInfo().setServiceName(serviceName);
      config.setWsdlPort(wsdlPort);
      config.setWSBinding(binding);
      config.setClassLoader(implType.getClassLoader());
      config.getMappingInfo().setPortName(portName);
      if (primaryWsdl != null) {
         config.setWsdlURL(primaryWsdl.getSystemId());
      }

      config.setMetadataReader(getExternalMetadatReader(implType, binding));
      DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
      return (AbstractSEIModelImpl)rt.getModel();
   }

   public static MetadataReader getExternalMetadatReader(Class<?> implType, WSBinding binding) {
      ExternalMetadataFeature ef = (ExternalMetadataFeature)binding.getFeature(ExternalMetadataFeature.class);
      return ef != null ? ef.getMetadataReader(implType.getClassLoader(), false) : null;
   }

   @NotNull
   public static QName getDefaultServiceName(Class<?> implType) {
      return getDefaultServiceName(implType, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultServiceName(Class<?> implType, MetadataReader metadataReader) {
      return getDefaultServiceName(implType, true, metadataReader);
   }

   @NotNull
   public static QName getDefaultServiceName(Class<?> implType, boolean isStandard) {
      return getDefaultServiceName(implType, isStandard, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultServiceName(Class<?> implType, boolean isStandard, MetadataReader metadataReader) {
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      WebServiceProvider wsProvider = (WebServiceProvider)((MetadataReader)metadataReader).getAnnotation(WebServiceProvider.class, implType);
      QName serviceName;
      if (wsProvider != null) {
         String tns = wsProvider.targetNamespace();
         String local = wsProvider.serviceName();
         serviceName = new QName(tns, local);
      } else {
         serviceName = RuntimeModeler.getServiceName(implType, (MetadataReader)metadataReader, isStandard);
      }

      assert serviceName != null;

      return serviceName;
   }

   @NotNull
   public static QName getDefaultPortName(QName serviceName, Class<?> implType) {
      return getDefaultPortName(serviceName, implType, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultPortName(QName serviceName, Class<?> implType, MetadataReader metadataReader) {
      return getDefaultPortName(serviceName, implType, true, metadataReader);
   }

   @NotNull
   public static QName getDefaultPortName(QName serviceName, Class<?> implType, boolean isStandard) {
      return getDefaultPortName(serviceName, implType, isStandard, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultPortName(QName serviceName, Class<?> implType, boolean isStandard, MetadataReader metadataReader) {
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      WebServiceProvider wsProvider = (WebServiceProvider)((MetadataReader)metadataReader).getAnnotation(WebServiceProvider.class, implType);
      QName portName;
      if (wsProvider != null) {
         String tns = wsProvider.targetNamespace();
         String local = wsProvider.portName();
         portName = new QName(tns, local);
      } else {
         portName = RuntimeModeler.getPortName(implType, (MetadataReader)metadataReader, serviceName.getNamespaceURI(), isStandard);
      }

      assert portName != null;

      return portName;
   }

   @Nullable
   public static String getWsdlLocation(Class<?> implType) {
      return getWsdlLocation(implType, new ReflectAnnotationReader());
   }

   @Nullable
   public static String getWsdlLocation(Class<?> implType, MetadataReader metadataReader) {
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      WebService ws = (WebService)((MetadataReader)metadataReader).getAnnotation(WebService.class, implType);
      if (ws != null) {
         return nullIfEmpty(ws.wsdlLocation());
      } else {
         WebServiceProvider wsProvider = (WebServiceProvider)implType.getAnnotation(WebServiceProvider.class);

         assert wsProvider != null;

         return nullIfEmpty(wsProvider.wsdlLocation());
      }
   }

   private static String nullIfEmpty(String string) {
      if (string.length() < 1) {
         string = null;
      }

      return string;
   }

   private static SDDocumentImpl generateWSDL(WSBinding binding, AbstractSEIModelImpl seiModel, List<SDDocumentImpl> docs, Container container, Class implType) {
      BindingID bindingId = binding.getBindingId();
      if (!bindingId.canGenerateWSDL()) {
         throw new ServerRtException("can.not.generate.wsdl", new Object[]{bindingId});
      } else {
         if (bindingId.toString().equals("http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
            String msg = ServerMessages.GENERATE_NON_STANDARD_WSDL();
            logger.warning(msg);
         }

         WSDLGenResolver wsdlResolver = new WSDLGenResolver(docs, seiModel.getServiceQName(), seiModel.getPortTypeName());
         WSDLGenInfo wsdlGenInfo = new WSDLGenInfo();
         wsdlGenInfo.setWsdlResolver(wsdlResolver);
         wsdlGenInfo.setContainer(container);
         wsdlGenInfo.setExtensions((WSDLGeneratorExtension[])ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
         wsdlGenInfo.setInlineSchemas(false);
         wsdlGenInfo.setSecureXmlProcessingDisabled(isSecureXmlProcessingDisabled(binding.getFeatures()));
         seiModel.getDatabinding().generateWSDL(wsdlGenInfo);
         return wsdlResolver.updateDocs();
      }
   }

   private static boolean isSecureXmlProcessingDisabled(WSFeatureList featureList) {
      return false;
   }

   private static List<SDDocumentImpl> categoriseMetadata(List<SDDocumentSource> src, QName serviceName, QName portTypeName) {
      List<SDDocumentImpl> r = new ArrayList(src.size());
      Iterator var4 = src.iterator();

      while(var4.hasNext()) {
         SDDocumentSource doc = (SDDocumentSource)var4.next();
         r.add(SDDocumentImpl.create(doc, serviceName, portTypeName));
      }

      return r;
   }

   private static void verifyPrimaryWSDL(@NotNull SDDocumentSource primaryWsdl, @NotNull QName serviceName) {
      SDDocumentImpl primaryDoc = SDDocumentImpl.create(primaryWsdl, serviceName, (QName)null);
      if (!(primaryDoc instanceof SDDocument.WSDL)) {
         throw new WebServiceException(primaryWsdl.getSystemId() + " is not a WSDL. But it is passed as a primary WSDL");
      } else {
         SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)primaryDoc;
         if (!wsdlDoc.hasService()) {
            if (wsdlDoc.getAllServices().isEmpty()) {
               throw new WebServiceException("Not a primary WSDL=" + primaryWsdl.getSystemId() + " since it doesn't have Service " + serviceName);
            } else {
               throw new WebServiceException("WSDL " + primaryDoc.getSystemId() + " has the following services " + wsdlDoc.getAllServices() + " but not " + serviceName + ". Maybe you forgot to specify a serviceName and/or targetNamespace in @WebService/@WebServiceProvider?");
            }
         }
      }
   }

   @Nullable
   private static SDDocumentImpl findPrimary(@NotNull List<SDDocumentImpl> docList) {
      SDDocumentImpl primaryDoc = null;
      boolean foundConcrete = false;
      boolean foundAbstract = false;
      Iterator var4 = docList.iterator();

      while(var4.hasNext()) {
         SDDocumentImpl doc = (SDDocumentImpl)var4.next();
         if (doc instanceof SDDocument.WSDL) {
            SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)doc;
            if (wsdlDoc.hasService()) {
               primaryDoc = doc;
               if (foundConcrete) {
                  throw new ServerRtException("duplicate.primary.wsdl", new Object[]{doc.getSystemId()});
               }

               foundConcrete = true;
            }

            if (wsdlDoc.hasPortType()) {
               if (foundAbstract) {
                  throw new ServerRtException("duplicate.abstract.wsdl", new Object[]{doc.getSystemId()});
               }

               foundAbstract = true;
            }
         }
      }

      return primaryDoc;
   }

   @NotNull
   private static WSDLPort getWSDLPort(SDDocumentSource primaryWsdl, List<? extends SDDocumentSource> metadata, @NotNull QName serviceName, @NotNull QName portName, Container container, EntityResolver resolver) {
      URL wsdlUrl = primaryWsdl.getSystemId();

      try {
         WSDLModel wsdlDoc = RuntimeWSDLParser.parse(new XMLEntityResolver.Parser(primaryWsdl), new EndpointFactory.EntityResolverImpl(metadata, resolver), false, container, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
         if (wsdlDoc.getServices().size() == 0) {
            throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(wsdlUrl));
         } else {
            WSDLService wsdlService = wsdlDoc.getService(serviceName);
            if (wsdlService == null) {
               throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(serviceName, wsdlUrl));
            } else {
               WSDLPort wsdlPort = wsdlService.get(portName);
               if (wsdlPort == null) {
                  throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(serviceName, portName, wsdlUrl));
               } else {
                  return wsdlPort;
               }
            }
         }
      } catch (IOException var10) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{wsdlUrl, var10});
      } catch (XMLStreamException var11) {
         throw new ServerRtException("runtime.saxparser.exception", new Object[]{var11.getMessage(), var11.getLocation(), var11});
      } catch (SAXException var12) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{wsdlUrl, var12});
      } catch (ServiceConfigurationError var13) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{wsdlUrl, var13});
      }
   }

   private static final class EntityResolverImpl implements XMLEntityResolver {
      private Map<String, SDDocumentSource> metadata = new HashMap();
      private EntityResolver resolver;

      public EntityResolverImpl(List<? extends SDDocumentSource> metadata, EntityResolver resolver) {
         Iterator var3 = metadata.iterator();

         while(var3.hasNext()) {
            SDDocumentSource doc = (SDDocumentSource)var3.next();
            this.metadata.put(doc.getSystemId().toExternalForm(), doc);
         }

         this.resolver = resolver;
      }

      public XMLEntityResolver.Parser resolveEntity(String publicId, String systemId) throws IOException, XMLStreamException {
         if (systemId != null) {
            SDDocumentSource doc = (SDDocumentSource)this.metadata.get(systemId);
            if (doc != null) {
               return new XMLEntityResolver.Parser(doc);
            }
         }

         if (this.resolver != null) {
            try {
               InputSource source = this.resolver.resolveEntity(publicId, systemId);
               if (source != null) {
                  XMLEntityResolver.Parser p = new XMLEntityResolver.Parser((URL)null, XMLStreamReaderFactory.create(source, true));
                  return p;
               }
            } catch (SAXException var5) {
               throw new XMLStreamException(var5);
            }
         }

         return null;
      }
   }
}
