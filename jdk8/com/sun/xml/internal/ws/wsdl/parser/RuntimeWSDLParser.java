package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.BindingIDFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSDLLocator;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.PolicyWSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLInputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLMessageImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOutputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartDescriptorImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLServiceImpl;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsdlmodelMessages;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class RuntimeWSDLParser {
   private final EditableWSDLModel wsdlDoc;
   private String targetNamespace;
   private final Set<String> importedWSDLs = new HashSet();
   private final XMLEntityResolver resolver;
   private final PolicyResolver policyResolver;
   private final WSDLParserExtension extensionFacade;
   private final WSDLParserExtensionContextImpl context;
   List<WSDLParserExtension> extensions;
   Map<String, String> wsdldef_nsdecl = new HashMap();
   Map<String, String> service_nsdecl = new HashMap();
   Map<String, String> port_nsdecl = new HashMap();
   private static final Logger LOGGER = Logger.getLogger(RuntimeWSDLParser.class.getName());

   public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, PolicyResolverFactory.create(), extensions);
   }

   public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, PolicyResolverFactory.create(), extensions);
   }

   public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, @NotNull PolicyResolver policyResolver, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, policyResolver, extensions);
   }

   public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, @NotNull PolicyResolver policyResolver, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, policyResolver, false, extensions);
   }

   public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, @NotNull PolicyResolver policyResolver, boolean isUseStreamFromEntityResolverWrapper, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      assert resolver != null;

      RuntimeWSDLParser wsdlParser = new RuntimeWSDLParser(wsdlSource.getSystemId(), new EntityResolverWrapper(resolver, isUseStreamFromEntityResolverWrapper), isClientSide, container, policyResolver, extensions);

      XMLEntityResolver.Parser parser;
      try {
         parser = wsdlParser.resolveWSDL(wsdlLoc, wsdlSource, serviceClass);
         if (!hasWSDLDefinitions(parser.parser)) {
            throw new XMLStreamException(ClientMessages.RUNTIME_WSDLPARSER_INVALID_WSDL(parser.systemId, WSDLConstants.QNAME_DEFINITIONS, parser.parser.getName(), parser.parser.getLocation()));
         }
      } catch (XMLStreamException var12) {
         if (wsdlLoc == null) {
            throw var12;
         }

         return tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, var12, serviceClass, policyResolver, extensions);
      } catch (IOException var13) {
         if (wsdlLoc == null) {
            throw var13;
         }

         return tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, var13, serviceClass, policyResolver, extensions);
      }

      wsdlParser.extensionFacade.start(wsdlParser.context);
      wsdlParser.parseWSDL(parser, false);
      wsdlParser.wsdlDoc.freeze();
      wsdlParser.extensionFacade.finished(wsdlParser.context);
      wsdlParser.extensionFacade.postFinished(wsdlParser.context);
      if (wsdlParser.wsdlDoc.getServices().isEmpty()) {
         throw new WebServiceException(ClientMessages.WSDL_CONTAINS_NO_SERVICE(wsdlLoc));
      } else {
         return wsdlParser.wsdlDoc;
      }
   }

   private static WSDLModel tryWithMex(@NotNull RuntimeWSDLParser wsdlParser, @NotNull URL wsdlLoc, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Throwable e, Class serviceClass, PolicyResolver policyResolver, WSDLParserExtension... extensions) throws SAXException, XMLStreamException {
      ArrayList exceptions = new ArrayList();

      try {
         WSDLModel wsdlModel = wsdlParser.parseUsingMex(wsdlLoc, resolver, isClientSide, container, serviceClass, policyResolver, extensions);
         if (wsdlModel == null) {
            throw new WebServiceException(ClientMessages.FAILED_TO_PARSE(wsdlLoc.toExternalForm(), e.getMessage()), e);
         }

         return wsdlModel;
      } catch (URISyntaxException var11) {
         exceptions.add(e);
         exceptions.add(var11);
      } catch (IOException var12) {
         exceptions.add(e);
         exceptions.add(var12);
      }

      throw new InaccessibleWSDLException(exceptions);
   }

   private WSDLModel parseUsingMex(@NotNull URL wsdlLoc, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, PolicyResolver policyResolver, WSDLParserExtension[] extensions) throws IOException, SAXException, XMLStreamException, URISyntaxException {
      MetaDataResolver mdResolver = null;
      ServiceDescriptor serviceDescriptor = null;
      RuntimeWSDLParser wsdlParser = null;
      Iterator var11 = ServiceFinder.find(MetadataResolverFactory.class).iterator();

      while(var11.hasNext()) {
         MetadataResolverFactory resolverFactory = (MetadataResolverFactory)var11.next();
         mdResolver = resolverFactory.metadataResolver(resolver);
         serviceDescriptor = mdResolver.resolve(wsdlLoc.toURI());
         if (serviceDescriptor != null) {
            break;
         }
      }

      if (serviceDescriptor != null) {
         List<? extends Source> wsdls = serviceDescriptor.getWSDLs();
         wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new MexEntityResolver(wsdls), isClientSide, container, policyResolver, extensions);
         wsdlParser.extensionFacade.start(wsdlParser.context);
         Iterator var18 = wsdls.iterator();

         while(var18.hasNext()) {
            Source src = (Source)var18.next();
            String systemId = src.getSystemId();
            XMLEntityResolver.Parser parser = wsdlParser.resolver.resolveEntity((String)null, systemId);
            wsdlParser.parseWSDL(parser, false);
         }
      }

      if ((mdResolver == null || serviceDescriptor == null) && (wsdlLoc.getProtocol().equals("http") || wsdlLoc.getProtocol().equals("https")) && wsdlLoc.getQuery() == null) {
         String urlString = wsdlLoc.toExternalForm();
         urlString = urlString + "?wsdl";
         wsdlLoc = new URL(urlString);
         wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new EntityResolverWrapper(resolver), isClientSide, container, policyResolver, extensions);
         wsdlParser.extensionFacade.start(wsdlParser.context);
         XMLEntityResolver.Parser parser = this.resolveWSDL(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), serviceClass);
         wsdlParser.parseWSDL(parser, false);
      }

      if (wsdlParser == null) {
         return null;
      } else {
         wsdlParser.wsdlDoc.freeze();
         wsdlParser.extensionFacade.finished(wsdlParser.context);
         wsdlParser.extensionFacade.postFinished(wsdlParser.context);
         return wsdlParser.wsdlDoc;
      }
   }

   private static boolean hasWSDLDefinitions(XMLStreamReader reader) {
      XMLStreamReaderUtil.nextElementContent(reader);
      return reader.getName().equals(WSDLConstants.QNAME_DEFINITIONS);
   }

   public static WSDLModel parse(XMLEntityResolver.Parser wsdl, XMLEntityResolver resolver, boolean isClientSide, Container container, PolicyResolver policyResolver, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      assert resolver != null;

      RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, policyResolver, extensions);
      parser.extensionFacade.start(parser.context);
      parser.parseWSDL(wsdl, false);
      parser.wsdlDoc.freeze();
      parser.extensionFacade.finished(parser.context);
      parser.extensionFacade.postFinished(parser.context);
      return parser.wsdlDoc;
   }

   public static WSDLModel parse(XMLEntityResolver.Parser wsdl, XMLEntityResolver resolver, boolean isClientSide, Container container, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
      assert resolver != null;

      RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
      parser.extensionFacade.start(parser.context);
      parser.parseWSDL(wsdl, false);
      parser.wsdlDoc.freeze();
      parser.extensionFacade.finished(parser.context);
      parser.extensionFacade.postFinished(parser.context);
      return parser.wsdlDoc;
   }

   private RuntimeWSDLParser(@NotNull String sourceLocation, XMLEntityResolver resolver, boolean isClientSide, Container container, PolicyResolver policyResolver, WSDLParserExtension... extensions) {
      this.wsdlDoc = sourceLocation != null ? new WSDLModelImpl(sourceLocation) : new WSDLModelImpl();
      this.resolver = resolver;
      this.policyResolver = policyResolver;
      this.extensions = new ArrayList();
      this.context = new WSDLParserExtensionContextImpl(this.wsdlDoc, isClientSide, container, policyResolver);
      boolean isPolicyExtensionFound = false;
      WSDLParserExtension[] var8 = extensions;
      int var9 = extensions.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         WSDLParserExtension e = var8[var10];
         if (e instanceof PolicyWSDLParserExtension) {
            isPolicyExtensionFound = true;
         }

         this.register(e);
      }

      if (!isPolicyExtensionFound) {
         this.register(new com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLParserExtension());
      }

      this.register(new MemberSubmissionAddressingWSDLParserExtension());
      this.register(new W3CAddressingWSDLParserExtension());
      this.register(new W3CAddressingMetadataWSDLParserExtension());
      this.extensionFacade = new WSDLParserExtensionFacade((WSDLParserExtension[])this.extensions.toArray(new WSDLParserExtension[0]));
   }

   private XMLEntityResolver.Parser resolveWSDL(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, Class serviceClass) throws IOException, SAXException, XMLStreamException {
      String systemId = wsdlSource.getSystemId();
      XMLEntityResolver.Parser parser = this.resolver.resolveEntity((String)null, systemId);
      if (parser == null && wsdlLoc != null) {
         String exForm = wsdlLoc.toExternalForm();
         parser = this.resolver.resolveEntity((String)null, exForm);
         if (parser == null && serviceClass != null) {
            URL ru = serviceClass.getResource(".");
            if (ru != null) {
               String ruExForm = ru.toExternalForm();
               if (exForm.startsWith(ruExForm)) {
                  parser = this.resolver.resolveEntity((String)null, exForm.substring(ruExForm.length()));
               }
            }
         }
      }

      if (parser == null) {
         if (this.isKnownReadableSource(wsdlSource)) {
            parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
         } else if (wsdlLoc != null) {
            parser = new XMLEntityResolver.Parser(wsdlLoc, createReader(wsdlLoc, serviceClass));
         }

         if (parser == null) {
            parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
         }
      }

      return parser;
   }

   private boolean isKnownReadableSource(Source wsdlSource) {
      if (!(wsdlSource instanceof StreamSource)) {
         return false;
      } else {
         return ((StreamSource)wsdlSource).getInputStream() != null || ((StreamSource)wsdlSource).getReader() != null;
      }
   }

   private XMLStreamReader createReader(@NotNull Source src) throws XMLStreamException {
      return new TidyXMLStreamReader(SourceReaderFactory.createSourceReader(src, true), (Closeable)null);
   }

   private void parseImport(@NotNull URL wsdlLoc) throws XMLStreamException, IOException, SAXException {
      String systemId = wsdlLoc.toExternalForm();
      XMLEntityResolver.Parser parser = this.resolver.resolveEntity((String)null, systemId);
      if (parser == null) {
         parser = new XMLEntityResolver.Parser(wsdlLoc, createReader(wsdlLoc));
      }

      this.parseWSDL(parser, true);
   }

   private void parseWSDL(XMLEntityResolver.Parser parser, boolean imported) throws XMLStreamException, IOException, SAXException {
      XMLStreamReader reader = parser.parser;

      try {
         if (parser.systemId != null && !this.importedWSDLs.add(parser.systemId.toExternalForm())) {
            return;
         }

         if (reader.getEventType() == 7) {
            XMLStreamReaderUtil.nextElementContent(reader);
         }

         if (WSDLConstants.QNAME_DEFINITIONS.equals(reader.getName())) {
            readNSDecl(this.wsdldef_nsdecl, reader);
         }

         if (reader.getEventType() == 8 || !reader.getName().equals(WSDLConstants.QNAME_SCHEMA) || !imported) {
            String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
            String oldTargetNamespace = this.targetNamespace;
            this.targetNamespace = tns;

            while(XMLStreamReaderUtil.nextElementContent(reader) != 2 && reader.getEventType() != 8) {
               QName name = reader.getName();
               if (WSDLConstants.QNAME_IMPORT.equals(name)) {
                  this.parseImport(parser.systemId, reader);
               } else if (WSDLConstants.QNAME_MESSAGE.equals(name)) {
                  this.parseMessage(reader);
               } else if (WSDLConstants.QNAME_PORT_TYPE.equals(name)) {
                  this.parsePortType(reader);
               } else if (WSDLConstants.QNAME_BINDING.equals(name)) {
                  this.parseBinding(reader);
               } else if (WSDLConstants.QNAME_SERVICE.equals(name)) {
                  this.parseService(reader);
               } else {
                  this.extensionFacade.definitionsElements(reader);
               }
            }

            this.targetNamespace = oldTargetNamespace;
            return;
         }

         LOGGER.warning(WsdlmodelMessages.WSDL_IMPORT_SHOULD_BE_WSDL(parser.systemId));
      } finally {
         this.wsdldef_nsdecl = new HashMap();
         reader.close();
      }

   }

   private void parseService(XMLStreamReader reader) {
      this.service_nsdecl.putAll(this.wsdldef_nsdecl);
      readNSDecl(this.service_nsdecl, reader);
      String serviceName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      EditableWSDLService service = new WSDLServiceImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, serviceName));
      this.extensionFacade.serviceAttributes(service, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         QName name = reader.getName();
         if (WSDLConstants.QNAME_PORT.equals(name)) {
            this.parsePort(reader, service);
            if (reader.getEventType() != 2) {
               XMLStreamReaderUtil.next(reader);
            }
         } else {
            this.extensionFacade.serviceElements(service, reader);
         }
      }

      this.wsdlDoc.addService(service);
      this.service_nsdecl = new HashMap();
   }

   private void parsePort(XMLStreamReader reader, EditableWSDLService service) {
      this.port_nsdecl.putAll(this.service_nsdecl);
      readNSDecl(this.port_nsdecl, reader);
      String portName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      String binding = ParserUtil.getMandatoryNonEmptyAttribute(reader, "binding");
      QName bindingName = ParserUtil.getQName(reader, binding);
      QName portQName = new QName(service.getName().getNamespaceURI(), portName);
      EditableWSDLPort port = new WSDLPortImpl(reader, service, portQName, bindingName);
      this.extensionFacade.portAttributes(port, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         QName name = reader.getName();
         if (!SOAPConstants.QNAME_ADDRESS.equals(name) && !SOAPConstants.QNAME_SOAP12ADDRESS.equals(name)) {
            if (AddressingVersion.W3C.nsUri.equals(name.getNamespaceURI()) && "EndpointReference".equals(name.getLocalPart())) {
               try {
                  StreamReaderBufferCreator creator = new StreamReaderBufferCreator(new MutableXMLStreamBuffer());
                  XMLStreamBuffer eprbuffer = new XMLStreamBufferMark(this.port_nsdecl, creator);
                  creator.createElementFragment(reader, false);
                  WSEndpointReference wsepr = new WSEndpointReference(eprbuffer, AddressingVersion.W3C);
                  port.setEPR(wsepr);
                  if (reader.getEventType() == 2 && reader.getName().equals(WSDLConstants.QNAME_PORT)) {
                     break;
                  }
               } catch (XMLStreamException var15) {
                  throw new WebServiceException(var15);
               }
            } else {
               this.extensionFacade.portElements(port, reader);
            }
         } else {
            String location = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
            if (location != null) {
               try {
                  port.setAddress(new EndpointAddress(location));
               } catch (URISyntaxException var14) {
               }
            }

            XMLStreamReaderUtil.next(reader);
         }
      }

      if (port.getAddress() == null) {
         try {
            port.setAddress(new EndpointAddress(""));
         } catch (URISyntaxException var13) {
         }
      }

      service.put(portQName, port);
      this.port_nsdecl = new HashMap();
   }

   private void parseBinding(XMLStreamReader reader) {
      String bindingName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "type");
      if (bindingName != null && portTypeName != null) {
         EditableWSDLBoundPortType binding = new WSDLBoundPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, bindingName), ParserUtil.getQName(reader, portTypeName));
         this.extensionFacade.bindingAttributes(binding, reader);

         while(true) {
            while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
               QName name = reader.getName();
               String transport;
               String style;
               if (WSDLConstants.NS_SOAP_BINDING.equals(name)) {
                  transport = reader.getAttributeValue((String)null, "transport");
                  binding.setBindingId(createBindingId(transport, SOAPVersion.SOAP_11));
                  style = reader.getAttributeValue((String)null, "style");
                  if (style != null && style.equals("rpc")) {
                     binding.setStyle(SOAPBinding.Style.RPC);
                  } else {
                     binding.setStyle(SOAPBinding.Style.DOCUMENT);
                  }

                  goToEnd(reader);
               } else if (!WSDLConstants.NS_SOAP12_BINDING.equals(name)) {
                  if (WSDLConstants.QNAME_OPERATION.equals(name)) {
                     this.parseBindingOperation(reader, binding);
                  } else {
                     this.extensionFacade.bindingElements(binding, reader);
                  }
               } else {
                  transport = reader.getAttributeValue((String)null, "transport");
                  binding.setBindingId(createBindingId(transport, SOAPVersion.SOAP_12));
                  style = reader.getAttributeValue((String)null, "style");
                  if (style != null && style.equals("rpc")) {
                     binding.setStyle(SOAPBinding.Style.RPC);
                  } else {
                     binding.setStyle(SOAPBinding.Style.DOCUMENT);
                  }

                  goToEnd(reader);
               }
            }

            return;
         }
      } else {
         XMLStreamReaderUtil.skipElement(reader);
      }
   }

   private static BindingID createBindingId(String transport, SOAPVersion soapVersion) {
      if (!transport.equals("http://schemas.xmlsoap.org/soap/http")) {
         Iterator var2 = ServiceFinder.find(BindingIDFactory.class).iterator();

         while(var2.hasNext()) {
            BindingIDFactory f = (BindingIDFactory)var2.next();
            BindingID bindingId = f.create(transport, soapVersion);
            if (bindingId != null) {
               return bindingId;
            }
         }
      }

      return soapVersion.equals(SOAPVersion.SOAP_11) ? BindingID.SOAP11_HTTP : BindingID.SOAP12_HTTP;
   }

   private void parseBindingOperation(XMLStreamReader reader, EditableWSDLBoundPortType binding) {
      String bindingOpName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      if (bindingOpName == null) {
         XMLStreamReaderUtil.skipElement(reader);
      } else {
         QName opName = new QName(binding.getPortTypeName().getNamespaceURI(), bindingOpName);
         EditableWSDLBoundOperation bindingOp = new WSDLBoundOperationImpl(reader, binding, opName);
         binding.put(opName, bindingOp);
         this.extensionFacade.bindingOperationAttributes(bindingOp, reader);

         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            String style = null;
            if (WSDLConstants.QNAME_INPUT.equals(name)) {
               this.parseInputBinding(reader, bindingOp);
            } else if (WSDLConstants.QNAME_OUTPUT.equals(name)) {
               this.parseOutputBinding(reader, bindingOp);
            } else if (WSDLConstants.QNAME_FAULT.equals(name)) {
               this.parseFaultBinding(reader, bindingOp);
            } else if (!SOAPConstants.QNAME_OPERATION.equals(name) && !SOAPConstants.QNAME_SOAP12OPERATION.equals(name)) {
               this.extensionFacade.bindingOperationElements(bindingOp, reader);
            } else {
               style = reader.getAttributeValue((String)null, "style");
               String soapAction = reader.getAttributeValue((String)null, "soapAction");
               if (soapAction != null) {
                  bindingOp.setSoapAction(soapAction);
               }

               goToEnd(reader);
            }

            if (style != null) {
               if (style.equals("rpc")) {
                  bindingOp.setStyle(SOAPBinding.Style.RPC);
               } else {
                  bindingOp.setStyle(SOAPBinding.Style.DOCUMENT);
               }
            } else {
               bindingOp.setStyle(binding.getStyle());
            }
         }

      }
   }

   private void parseInputBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
      boolean bodyFound = false;
      this.extensionFacade.bindingOperationInputAttributes(bindingOp, reader);

      while(true) {
         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
               bodyFound = true;
               bindingOp.setInputExplicitBodyParts(parseSOAPBodyBinding(reader, bindingOp, RuntimeWSDLParser.BindingMode.INPUT));
               goToEnd(reader);
            } else if (!SOAPConstants.QNAME_HEADER.equals(name) && !SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
               if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                  parseMimeMultipartBinding(reader, bindingOp, RuntimeWSDLParser.BindingMode.INPUT);
               } else {
                  this.extensionFacade.bindingOperationInputElements(bindingOp, reader);
               }
            } else {
               parseSOAPHeaderBinding(reader, bindingOp.getInputParts());
            }
         }

         return;
      }
   }

   private void parseOutputBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
      boolean bodyFound = false;
      this.extensionFacade.bindingOperationOutputAttributes(bindingOp, reader);

      while(true) {
         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
               bodyFound = true;
               bindingOp.setOutputExplicitBodyParts(parseSOAPBodyBinding(reader, bindingOp, RuntimeWSDLParser.BindingMode.OUTPUT));
               goToEnd(reader);
            } else if (!SOAPConstants.QNAME_HEADER.equals(name) && !SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
               if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                  parseMimeMultipartBinding(reader, bindingOp, RuntimeWSDLParser.BindingMode.OUTPUT);
               } else {
                  this.extensionFacade.bindingOperationOutputElements(bindingOp, reader);
               }
            } else {
               parseSOAPHeaderBinding(reader, bindingOp.getOutputParts());
            }
         }

         return;
      }
   }

   private void parseFaultBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
      String faultName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      EditableWSDLBoundFault wsdlBoundFault = new WSDLBoundFaultImpl(reader, faultName, bindingOp);
      bindingOp.addFault(wsdlBoundFault);
      this.extensionFacade.bindingOperationFaultAttributes(wsdlBoundFault, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         this.extensionFacade.bindingOperationFaultElements(wsdlBoundFault, reader);
      }

   }

   private static boolean parseSOAPBodyBinding(XMLStreamReader reader, EditableWSDLBoundOperation op, RuntimeWSDLParser.BindingMode mode) {
      String namespace = reader.getAttributeValue((String)null, "namespace");
      if (mode == RuntimeWSDLParser.BindingMode.INPUT) {
         op.setRequestNamespace(namespace);
         return parseSOAPBodyBinding(reader, op.getInputParts());
      } else {
         op.setResponseNamespace(namespace);
         return parseSOAPBodyBinding(reader, op.getOutputParts());
      }
   }

   private static boolean parseSOAPBodyBinding(XMLStreamReader reader, Map<String, ParameterBinding> parts) {
      String partsString = reader.getAttributeValue((String)null, "parts");
      if (partsString == null) {
         return false;
      } else {
         List<String> partsList = XmlUtil.parseTokenList(partsString);
         if (partsList.isEmpty()) {
            parts.put(" ", ParameterBinding.BODY);
         } else {
            Iterator var4 = partsList.iterator();

            while(var4.hasNext()) {
               String part = (String)var4.next();
               parts.put(part, ParameterBinding.BODY);
            }
         }

         return true;
      }
   }

   private static void parseSOAPHeaderBinding(XMLStreamReader reader, Map<String, ParameterBinding> parts) {
      String part = reader.getAttributeValue((String)null, "part");
      if (part != null && !part.equals("")) {
         parts.put(part, ParameterBinding.HEADER);
         goToEnd(reader);
      }
   }

   private static void parseMimeMultipartBinding(XMLStreamReader reader, EditableWSDLBoundOperation op, RuntimeWSDLParser.BindingMode mode) {
      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         QName name = reader.getName();
         if (MIMEConstants.QNAME_PART.equals(name)) {
            parseMIMEPart(reader, op, mode);
         } else {
            XMLStreamReaderUtil.skipElement(reader);
         }
      }

   }

   private static void parseMIMEPart(XMLStreamReader reader, EditableWSDLBoundOperation op, RuntimeWSDLParser.BindingMode mode) {
      boolean bodyFound = false;
      Map<String, ParameterBinding> parts = null;
      if (mode == RuntimeWSDLParser.BindingMode.INPUT) {
         parts = op.getInputParts();
      } else if (mode == RuntimeWSDLParser.BindingMode.OUTPUT) {
         parts = op.getOutputParts();
      } else if (mode == RuntimeWSDLParser.BindingMode.FAULT) {
         parts = op.getFaultParts();
      }

      while(true) {
         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (SOAPConstants.QNAME_BODY.equals(name) && !bodyFound) {
               bodyFound = true;
               parseSOAPBodyBinding(reader, op, mode);
               XMLStreamReaderUtil.next(reader);
            } else if (SOAPConstants.QNAME_HEADER.equals(name)) {
               bodyFound = true;
               parseSOAPHeaderBinding(reader, parts);
               XMLStreamReaderUtil.next(reader);
            } else if (MIMEConstants.QNAME_CONTENT.equals(name)) {
               String part = reader.getAttributeValue((String)null, "part");
               String type = reader.getAttributeValue((String)null, "type");
               if (part != null && type != null) {
                  ParameterBinding sb = ParameterBinding.createAttachment(type);
                  if (parts != null && sb != null && part != null) {
                     parts.put(part, sb);
                  }

                  XMLStreamReaderUtil.next(reader);
               } else {
                  XMLStreamReaderUtil.skipElement(reader);
               }
            } else {
               XMLStreamReaderUtil.skipElement(reader);
            }
         }

         return;
      }
   }

   protected void parseImport(@Nullable URL baseURL, XMLStreamReader reader) throws IOException, SAXException, XMLStreamException {
      String importLocation = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
      URL importURL;
      if (baseURL != null) {
         importURL = new URL(baseURL, importLocation);
      } else {
         importURL = new URL(importLocation);
      }

      this.parseImport(importURL);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         XMLStreamReaderUtil.skipElement(reader);
      }

   }

   private void parsePortType(XMLStreamReader reader) {
      String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      if (portTypeName == null) {
         XMLStreamReaderUtil.skipElement(reader);
      } else {
         EditableWSDLPortType portType = new WSDLPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, portTypeName));
         this.extensionFacade.portTypeAttributes(portType, reader);
         this.wsdlDoc.addPortType(portType);

         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (WSDLConstants.QNAME_OPERATION.equals(name)) {
               this.parsePortTypeOperation(reader, portType);
            } else {
               this.extensionFacade.portTypeElements(portType, reader);
            }
         }

      }
   }

   private void parsePortTypeOperation(XMLStreamReader reader, EditableWSDLPortType portType) {
      String operationName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      if (operationName == null) {
         XMLStreamReaderUtil.skipElement(reader);
      } else {
         QName operationQName = new QName(portType.getName().getNamespaceURI(), operationName);
         EditableWSDLOperation operation = new WSDLOperationImpl(reader, portType, operationQName);
         this.extensionFacade.portTypeOperationAttributes(operation, reader);
         String parameterOrder = ParserUtil.getAttribute(reader, "parameterOrder");
         operation.setParameterOrder(parameterOrder);
         portType.put(operationName, operation);

         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (name.equals(WSDLConstants.QNAME_INPUT)) {
               this.parsePortTypeOperationInput(reader, operation);
            } else if (name.equals(WSDLConstants.QNAME_OUTPUT)) {
               this.parsePortTypeOperationOutput(reader, operation);
            } else if (name.equals(WSDLConstants.QNAME_FAULT)) {
               this.parsePortTypeOperationFault(reader, operation);
            } else {
               this.extensionFacade.portTypeOperationElements(operation, reader);
            }
         }

      }
   }

   private void parsePortTypeOperationFault(XMLStreamReader reader, EditableWSDLOperation operation) {
      String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
      QName msgName = ParserUtil.getQName(reader, msg);
      String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      EditableWSDLFault fault = new WSDLFaultImpl(reader, name, msgName, operation);
      operation.addFault(fault);
      this.extensionFacade.portTypeOperationFaultAttributes(fault, reader);
      this.extensionFacade.portTypeOperationFault(operation, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         this.extensionFacade.portTypeOperationFaultElements(fault, reader);
      }

   }

   private void parsePortTypeOperationInput(XMLStreamReader reader, EditableWSDLOperation operation) {
      String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
      QName msgName = ParserUtil.getQName(reader, msg);
      String name = ParserUtil.getAttribute(reader, "name");
      EditableWSDLInput input = new WSDLInputImpl(reader, name, msgName, operation);
      operation.setInput(input);
      this.extensionFacade.portTypeOperationInputAttributes(input, reader);
      this.extensionFacade.portTypeOperationInput(operation, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         this.extensionFacade.portTypeOperationInputElements(input, reader);
      }

   }

   private void parsePortTypeOperationOutput(XMLStreamReader reader, EditableWSDLOperation operation) {
      String msg = ParserUtil.getAttribute(reader, "message");
      QName msgName = ParserUtil.getQName(reader, msg);
      String name = ParserUtil.getAttribute(reader, "name");
      EditableWSDLOutput output = new WSDLOutputImpl(reader, name, msgName, operation);
      operation.setOutput(output);
      this.extensionFacade.portTypeOperationOutputAttributes(output, reader);
      this.extensionFacade.portTypeOperationOutput(operation, reader);

      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         this.extensionFacade.portTypeOperationOutputElements(output, reader);
      }

   }

   private void parseMessage(XMLStreamReader reader) {
      String msgName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
      EditableWSDLMessage msg = new WSDLMessageImpl(reader, new QName(this.targetNamespace, msgName));
      this.extensionFacade.messageAttributes(msg, reader);
      byte partIndex = 0;

      while(true) {
         while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (WSDLConstants.QNAME_PART.equals(name)) {
               String part = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
               String desc = null;
               int index = reader.getAttributeCount();
               WSDLDescriptorKind kind = WSDLDescriptorKind.ELEMENT;

               for(int i = 0; i < index; ++i) {
                  QName descName = reader.getAttributeName(i);
                  if (descName.getLocalPart().equals("element")) {
                     kind = WSDLDescriptorKind.ELEMENT;
                  } else if (descName.getLocalPart().equals("type")) {
                     kind = WSDLDescriptorKind.TYPE;
                  }

                  if (descName.getLocalPart().equals("element") || descName.getLocalPart().equals("type")) {
                     desc = reader.getAttributeValue(i);
                     break;
                  }
               }

               if (desc != null) {
                  EditableWSDLPart wsdlPart = new WSDLPartImpl(reader, part, partIndex, new WSDLPartDescriptorImpl(reader, ParserUtil.getQName(reader, desc), kind));
                  msg.add(wsdlPart);
               }

               if (reader.getEventType() != 2) {
                  goToEnd(reader);
               }
            } else {
               this.extensionFacade.messageElements(msg, reader);
            }
         }

         this.wsdlDoc.addMessage(msg);
         if (reader.getEventType() != 2) {
            goToEnd(reader);
         }

         return;
      }
   }

   private static void goToEnd(XMLStreamReader reader) {
      while(XMLStreamReaderUtil.nextElementContent(reader) != 2) {
         XMLStreamReaderUtil.skipElement(reader);
      }

   }

   private static XMLStreamReader createReader(URL wsdlLoc) throws IOException, XMLStreamException {
      return createReader(wsdlLoc, (Class)null);
   }

   private static XMLStreamReader createReader(URL wsdlLoc, Class<Service> serviceClass) throws IOException, XMLStreamException {
      Object stream;
      try {
         stream = wsdlLoc.openStream();
      } catch (IOException var9) {
         if (serviceClass != null) {
            WSDLLocator locator = (WSDLLocator)ContainerResolver.getInstance().getContainer().getSPI(WSDLLocator.class);
            if (locator != null) {
               String exForm = wsdlLoc.toExternalForm();
               URL ru = serviceClass.getResource(".");
               String loc = wsdlLoc.getPath();
               if (ru != null) {
                  String ruExForm = ru.toExternalForm();
                  if (exForm.startsWith(ruExForm)) {
                     loc = exForm.substring(ruExForm.length());
                  }
               }

               wsdlLoc = locator.locateWSDL(serviceClass, loc);
               if (wsdlLoc != null) {
                  stream = new FilterInputStream(wsdlLoc.openStream()) {
                     boolean closed;

                     public void close() throws IOException {
                        if (!this.closed) {
                           this.closed = true;
                           byte[] buf = new byte[8192];

                           while(true) {
                              if (this.read(buf) == -1) {
                                 super.close();
                                 break;
                              }
                           }
                        }

                     }
                  };
                  return new TidyXMLStreamReader(XMLStreamReaderFactory.create(wsdlLoc.toExternalForm(), (InputStream)stream, false), (Closeable)stream);
               }
            }
         }

         throw var9;
      }

      return new TidyXMLStreamReader(XMLStreamReaderFactory.create(wsdlLoc.toExternalForm(), (InputStream)stream, false), (Closeable)stream);
   }

   private void register(WSDLParserExtension e) {
      this.extensions.add(new FoolProofParserExtension(e));
   }

   private static void readNSDecl(Map<String, String> ns_map, XMLStreamReader reader) {
      if (reader.getNamespaceCount() > 0) {
         for(int i = 0; i < reader.getNamespaceCount(); ++i) {
            ns_map.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
         }
      }

   }

   private static enum BindingMode {
      INPUT,
      OUTPUT,
      FAULT;
   }
}
