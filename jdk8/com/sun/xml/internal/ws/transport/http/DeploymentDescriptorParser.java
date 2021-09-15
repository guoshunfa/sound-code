package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.streaming.Attributes;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;

public class DeploymentDescriptorParser<A> {
   public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
   public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
   public static final QName QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
   public static final QName QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
   public static final QName QNAME_EXT_METADA = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "external-metadata");
   public static final String ATTR_FILE = "file";
   public static final String ATTR_RESOURCE = "resource";
   public static final String ATTR_VERSION = "version";
   public static final String ATTR_NAME = "name";
   public static final String ATTR_IMPLEMENTATION = "implementation";
   public static final String ATTR_WSDL = "wsdl";
   public static final String ATTR_SERVICE = "service";
   public static final String ATTR_PORT = "port";
   public static final String ATTR_URL_PATTERN = "url-pattern";
   public static final String ATTR_ENABLE_MTOM = "enable-mtom";
   public static final String ATTR_MTOM_THRESHOLD_VALUE = "mtom-threshold-value";
   public static final String ATTR_BINDING = "binding";
   public static final String ATTR_DATABINDING = "databinding";
   public static final List<String> ATTRVALUE_SUPPORTED_VERSIONS = Arrays.asList("2.0", "2.1");
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
   private final Container container;
   private final ClassLoader classLoader;
   private final ResourceLoader loader;
   private final DeploymentDescriptorParser.AdapterFactory<A> adapterFactory;
   private final Set<String> names = new HashSet();
   private final Map<String, SDDocumentSource> docs = new HashMap();

   public DeploymentDescriptorParser(ClassLoader cl, ResourceLoader loader, Container container, DeploymentDescriptorParser.AdapterFactory<A> adapterFactory) throws MalformedURLException {
      this.classLoader = cl;
      this.loader = loader;
      this.container = container;
      this.adapterFactory = adapterFactory;
      this.collectDocs("/WEB-INF/wsdl/");
      logger.log(Level.FINE, (String)"war metadata={0}", (Object)this.docs);
   }

   @NotNull
   public List<A> parse(String systemId, InputStream is) {
      TidyXMLStreamReader reader = null;

      List var4;
      try {
         reader = new TidyXMLStreamReader(XMLStreamReaderFactory.create(systemId, is, true), is);
         XMLStreamReaderUtil.nextElementContent(reader);
         var4 = this.parseAdapters(reader);
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (XMLStreamException var14) {
               throw new ServerRtException("runtime.parser.xmlReader", new Object[]{var14});
            }
         }

         try {
            is.close();
         } catch (IOException var13) {
         }

      }

      return var4;
   }

   @NotNull
   public List<A> parse(File f) throws IOException {
      FileInputStream in = new FileInputStream(f);

      List var3;
      try {
         var3 = this.parse(f.getPath(), in);
      } finally {
         in.close();
      }

      return var3;
   }

   private void collectDocs(String dirPath) throws MalformedURLException {
      Set<String> paths = this.loader.getResourcePaths(dirPath);
      if (paths != null) {
         Iterator var3 = paths.iterator();

         while(var3.hasNext()) {
            String path = (String)var3.next();
            if (path.endsWith("/")) {
               if (!path.endsWith("/CVS/") && !path.endsWith("/.svn/")) {
                  this.collectDocs(path);
               }
            } else {
               URL res = this.loader.getResource(path);
               this.docs.put(res.toString(), SDDocumentSource.create(res));
            }
         }
      }

   }

   private List<A> parseAdapters(XMLStreamReader reader) {
      if (!reader.getName().equals(QNAME_ENDPOINTS)) {
         failWithFullName("runtime.parser.invalidElement", reader);
      }

      List<A> adapters = new ArrayList();
      Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
      String version = this.getMandatoryNonEmptyAttribute(reader, attrs, "version");
      if (!ATTRVALUE_SUPPORTED_VERSIONS.contains(version)) {
         failWithLocalName("runtime.parser.invalidVersionNumber", reader, version);
      }

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         if (reader.getName().equals(QNAME_ENDPOINT)) {
            attrs = XMLStreamReaderUtil.getAttributes(reader);
            String name = this.getMandatoryNonEmptyAttribute(reader, attrs, "name");
            if (!this.names.add(name)) {
               logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_NAME());
            }

            String implementationName = this.getMandatoryNonEmptyAttribute(reader, attrs, "implementation");
            Class<?> implementorClass = this.getImplementorClass(implementationName, reader);
            MetadataReader metadataReader = null;
            ExternalMetadataFeature externalMetadataFeature = null;
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getEventType() != 2) {
               externalMetadataFeature = this.configureExternalMetadataReader(reader);
               if (externalMetadataFeature != null) {
                  metadataReader = externalMetadataFeature.getMetadataReader(implementorClass.getClassLoader(), false);
               }
            }

            QName serviceName = this.getQNameAttribute(attrs, "service");
            if (serviceName == null) {
               serviceName = EndpointFactory.getDefaultServiceName(implementorClass, metadataReader);
            }

            QName portName = this.getQNameAttribute(attrs, "port");
            if (portName == null) {
               portName = EndpointFactory.getDefaultPortName(serviceName, implementorClass, metadataReader);
            }

            String enable_mtom = this.getAttribute(attrs, "enable-mtom");
            String mtomThreshold = this.getAttribute(attrs, "mtom-threshold-value");
            String dbMode = this.getAttribute(attrs, "databinding");
            String bindingId = this.getAttribute(attrs, "binding");
            if (bindingId != null) {
               bindingId = getBindingIdForToken(bindingId);
            }

            WSBinding binding = createBinding(bindingId, implementorClass, enable_mtom, mtomThreshold, dbMode);
            if (externalMetadataFeature != null) {
               binding.getFeatures().mergeFeatures(new WebServiceFeature[]{externalMetadataFeature}, true);
            }

            String urlPattern = this.getMandatoryNonEmptyAttribute(reader, attrs, "url-pattern");
            boolean handlersSetInDD = this.setHandlersAndRoles(binding, reader, serviceName, portName);
            EndpointFactory.verifyImplementorClass(implementorClass, metadataReader);
            SDDocumentSource primaryWSDL = this.getPrimaryWSDL(reader, attrs, implementorClass, metadataReader);
            WSEndpoint<?> endpoint = WSEndpoint.create(implementorClass, !handlersSetInDD, (Invoker)null, serviceName, portName, this.container, binding, primaryWSDL, this.docs.values(), this.createEntityResolver(), false);
            adapters.add(this.adapterFactory.createAdapter(name, urlPattern, endpoint));
         } else {
            failWithLocalName("runtime.parser.invalidElement", reader);
         }
      }

      return adapters;
   }

   private static WSBinding createBinding(String ddBindingId, Class implClass, String mtomEnabled, String mtomThreshold, String dataBindingMode) {
      MTOMFeature mtomfeature = null;
      if (mtomEnabled != null) {
         if (mtomThreshold != null) {
            mtomfeature = new MTOMFeature(Boolean.valueOf(mtomEnabled), Integer.valueOf(mtomThreshold));
         } else {
            mtomfeature = new MTOMFeature(Boolean.valueOf(mtomEnabled));
         }
      }

      WebServiceFeatureList features;
      BindingID bindingID;
      if (ddBindingId != null) {
         bindingID = BindingID.parse(ddBindingId);
         features = bindingID.createBuiltinFeatureList();
         if (checkMtomConflict((MTOMFeature)features.get(MTOMFeature.class), mtomfeature)) {
            throw new ServerRtException(ServerMessages.DD_MTOM_CONFLICT(ddBindingId, mtomEnabled), new Object[0]);
         }
      } else {
         bindingID = BindingID.parse(implClass);
         features = new WebServiceFeatureList();
         if (mtomfeature != null) {
            features.add(mtomfeature);
         }

         features.addAll(bindingID.createBuiltinFeatureList());
      }

      if (dataBindingMode != null) {
         features.add(new DatabindingModeFeature(dataBindingMode));
      }

      return bindingID.createBinding(features.toArray());
   }

   private static boolean checkMtomConflict(MTOMFeature lhs, MTOMFeature rhs) {
      return lhs != null && rhs != null ? lhs.isEnabled() ^ rhs.isEnabled() : false;
   }

   @NotNull
   public static String getBindingIdForToken(@NotNull String lexical) {
      if (lexical.equals("##SOAP11_HTTP")) {
         return "http://schemas.xmlsoap.org/wsdl/soap/http";
      } else if (lexical.equals("##SOAP11_HTTP_MTOM")) {
         return "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
      } else if (lexical.equals("##SOAP12_HTTP")) {
         return "http://www.w3.org/2003/05/soap/bindings/HTTP/";
      } else if (lexical.equals("##SOAP12_HTTP_MTOM")) {
         return "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
      } else {
         return lexical.equals("##XML_HTTP") ? "http://www.w3.org/2004/08/wsdl/http" : lexical;
      }
   }

   private SDDocumentSource getPrimaryWSDL(XMLStreamReader xsr, Attributes attrs, Class<?> implementorClass, MetadataReader metadataReader) {
      String wsdlFile = this.getAttribute(attrs, "wsdl");
      if (wsdlFile == null) {
         wsdlFile = EndpointFactory.getWsdlLocation(implementorClass, metadataReader);
      }

      if (wsdlFile != null) {
         if (!wsdlFile.startsWith("WEB-INF/wsdl")) {
            logger.log(Level.WARNING, "Ignoring wrong wsdl={0}. It should start with {1}. Going to generate and publish a new WSDL.", new Object[]{wsdlFile, "WEB-INF/wsdl"});
            return null;
         } else {
            URL wsdl;
            try {
               wsdl = this.loader.getResource('/' + wsdlFile);
            } catch (MalformedURLException var8) {
               throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), var8, xsr);
            }

            if (wsdl == null) {
               throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), xsr);
            } else {
               SDDocumentSource docInfo = (SDDocumentSource)this.docs.get(wsdl.toExternalForm());

               assert docInfo != null;

               return docInfo;
            }
         }
      } else {
         return null;
      }
   }

   private EntityResolver createEntityResolver() {
      try {
         return XmlUtil.createEntityResolver(this.loader.getCatalogFile());
      } catch (MalformedURLException var2) {
         throw new WebServiceException(var2);
      }
   }

   protected String getAttribute(Attributes attrs, String name) {
      String value = attrs.getValue(name);
      if (value != null) {
         value = value.trim();
      }

      return value;
   }

   protected QName getQNameAttribute(Attributes attrs, String name) {
      String value = this.getAttribute(attrs, name);
      return value != null && !value.equals("") ? QName.valueOf(value) : null;
   }

   protected String getNonEmptyAttribute(XMLStreamReader reader, Attributes attrs, String name) {
      String value = this.getAttribute(attrs, name);
      if (value != null && value.equals("")) {
         failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
      }

      return value;
   }

   protected String getMandatoryAttribute(XMLStreamReader reader, Attributes attrs, String name) {
      String value = this.getAttribute(attrs, name);
      if (value == null) {
         failWithLocalName("runtime.parser.missing.attribute", reader, name);
      }

      return value;
   }

   protected String getMandatoryNonEmptyAttribute(XMLStreamReader reader, Attributes attributes, String name) {
      String value = this.getAttribute(attributes, name);
      if (value == null) {
         failWithLocalName("runtime.parser.missing.attribute", reader, name);
      } else if (value.equals("")) {
         failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
      }

      return value;
   }

   protected boolean setHandlersAndRoles(WSBinding binding, XMLStreamReader reader, QName serviceName, QName portName) {
      if (reader.getEventType() != 2 && reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAINS)) {
         HandlerAnnotationInfo handlerInfo = HandlerChainsModel.parseHandlerFile(reader, this.classLoader, serviceName, portName, binding);
         binding.setHandlerChain(handlerInfo.getHandlers());
         if (binding instanceof SOAPBinding) {
            ((SOAPBinding)binding).setRoles(handlerInfo.getRoles());
         }

         XMLStreamReaderUtil.nextContent(reader);
         return true;
      } else {
         return false;
      }
   }

   protected ExternalMetadataFeature configureExternalMetadataReader(XMLStreamReader reader) {
      ExternalMetadataFeature.Builder featureBuilder;
      for(featureBuilder = null; QNAME_EXT_METADA.equals(reader.getName()); XMLStreamReaderUtil.nextElementContent(reader)) {
         if (reader.getEventType() == 1) {
            Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
            String file = this.getAttribute(attrs, "file");
            if (file != null) {
               if (featureBuilder == null) {
                  featureBuilder = ExternalMetadataFeature.builder();
               }

               featureBuilder.addFiles(new File(file));
            }

            String res = this.getAttribute(attrs, "resource");
            if (res != null) {
               if (featureBuilder == null) {
                  featureBuilder = ExternalMetadataFeature.builder();
               }

               featureBuilder.addResources(res);
            }
         }
      }

      return this.buildFeature(featureBuilder);
   }

   private ExternalMetadataFeature buildFeature(ExternalMetadataFeature.Builder builder) {
      return builder != null ? builder.build() : null;
   }

   protected static void fail(String key, XMLStreamReader reader) {
      logger.log(Level.SEVERE, "{0}{1}", new Object[]{key, reader.getLocation().getLineNumber()});
      throw new ServerRtException(key, new Object[]{Integer.toString(reader.getLocation().getLineNumber())});
   }

   protected static void failWithFullName(String key, XMLStreamReader reader) {
      throw new ServerRtException(key, new Object[]{reader.getLocation().getLineNumber(), reader.getName()});
   }

   protected static void failWithLocalName(String key, XMLStreamReader reader) {
      throw new ServerRtException(key, new Object[]{reader.getLocation().getLineNumber(), reader.getLocalName()});
   }

   protected static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
      throw new ServerRtException(key, new Object[]{reader.getLocation().getLineNumber(), reader.getLocalName(), arg});
   }

   protected Class loadClass(String name) {
      try {
         return Class.forName(name, true, this.classLoader);
      } catch (ClassNotFoundException var3) {
         logger.log(Level.SEVERE, (String)var3.getMessage(), (Throwable)var3);
         throw new ServerRtException("runtime.parser.classNotFound", new Object[]{name});
      }
   }

   private Class getImplementorClass(String name, XMLStreamReader xsr) {
      try {
         return Class.forName(name, true, this.classLoader);
      } catch (ClassNotFoundException var4) {
         logger.log(Level.SEVERE, (String)var4.getMessage(), (Throwable)var4);
         throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_CLASS_NOT_FOUND(name), var4, xsr);
      }
   }

   public interface AdapterFactory<A> {
      A createAdapter(String var1, String var2, WSEndpoint<?> var3);
   }
}
