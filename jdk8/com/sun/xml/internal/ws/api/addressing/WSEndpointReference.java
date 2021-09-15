package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.EndpointReferenceUtil;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.spi.ProviderImpl;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class WSEndpointReference implements WSDLExtension {
   private final XMLStreamBuffer infoset;
   private final AddressingVersion version;
   @NotNull
   private Header[] referenceParameters;
   @NotNull
   private String address;
   @NotNull
   private QName rootElement;
   private static final OutboundReferenceParameterHeader[] EMPTY_ARRAY = new OutboundReferenceParameterHeader[0];
   private Map<QName, WSEndpointReference.EPRExtension> rootEprExtensions;

   public WSEndpointReference(EndpointReference epr, AddressingVersion version) {
      try {
         MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
         epr.writeTo(new XMLStreamBufferResult(xsb));
         this.infoset = xsb;
         this.version = version;
         this.rootElement = new QName("EndpointReference", version.nsUri);
         this.parse();
      } catch (XMLStreamException var4) {
         throw new WebServiceException(ClientMessages.FAILED_TO_PARSE_EPR(epr), var4);
      }
   }

   public WSEndpointReference(EndpointReference epr) {
      this(epr, AddressingVersion.fromSpecClass(epr.getClass()));
   }

   public WSEndpointReference(XMLStreamBuffer infoset, AddressingVersion version) {
      try {
         this.infoset = infoset;
         this.version = version;
         this.rootElement = new QName("EndpointReference", version.nsUri);
         this.parse();
      } catch (XMLStreamException var4) {
         throw new AssertionError(var4);
      }
   }

   public WSEndpointReference(InputStream infoset, AddressingVersion version) throws XMLStreamException {
      this(XMLStreamReaderFactory.create((String)null, (InputStream)infoset, false), version);
   }

   public WSEndpointReference(XMLStreamReader in, AddressingVersion version) throws XMLStreamException {
      this(XMLStreamBuffer.createNewBufferFromXMLStreamReader(in), version);
   }

   public WSEndpointReference(URL address, AddressingVersion version) {
      this(address.toExternalForm(), version);
   }

   public WSEndpointReference(URI address, AddressingVersion version) {
      this(address.toString(), version);
   }

   public WSEndpointReference(String address, AddressingVersion version) {
      this.infoset = createBufferFromAddress(address, version);
      this.version = version;
      this.address = address;
      this.rootElement = new QName("EndpointReference", version.nsUri);
      this.referenceParameters = EMPTY_ARRAY;
   }

   private static XMLStreamBuffer createBufferFromAddress(String address, AddressingVersion version) {
      try {
         MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
         StreamWriterBufferCreator w = new StreamWriterBufferCreator(xsb);
         w.writeStartDocument();
         w.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
         w.writeNamespace(version.getPrefix(), version.nsUri);
         w.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
         w.writeCharacters(address);
         w.writeEndElement();
         w.writeEndElement();
         w.writeEndDocument();
         w.close();
         return xsb;
      } catch (XMLStreamException var4) {
         throw new AssertionError(var4);
      }
   }

   public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable List<Element> referenceParameters) {
      this(version, address, service, port, portType, metadata, wsdlAddress, (String)null, referenceParameters, (List)null, (Map)null);
   }

   public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable List<Element> referenceParameters, @Nullable Collection<WSEndpointReference.EPRExtension> extns, @Nullable Map<QName, String> attributes) {
      this(createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, (String)null, (Collection)extns, attributes), version);
   }

   public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable String wsdlTargetNamepsace, @Nullable List<Element> referenceParameters, @Nullable List<Element> elements, @Nullable Map<QName, String> attributes) {
      this(createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamepsace, elements, attributes), version);
   }

   private static XMLStreamBuffer createBufferFromData(AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable List<Element> elements, @Nullable Map<QName, String> attributes) {
      StreamWriterBufferCreator writer = new StreamWriterBufferCreator();

      try {
         writer.writeStartDocument();
         writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
         writer.writeNamespace(version.getPrefix(), version.nsUri);
         writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
         if (elements != null) {
            Iterator var12 = elements.iterator();

            while(var12.hasNext()) {
               Element e = (Element)var12.next();
               DOMUtil.serializeNode(e, writer);
            }
         }

         writer.writeEndElement();
         writer.writeEndDocument();
         writer.flush();
         return writer.getXMLStreamBuffer();
      } catch (XMLStreamException var14) {
         throw new WebServiceException(var14);
      }
   }

   private static XMLStreamBuffer createBufferFromData(AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable Collection<WSEndpointReference.EPRExtension> extns, @Nullable Map<QName, String> attributes) {
      StreamWriterBufferCreator writer = new StreamWriterBufferCreator();

      try {
         writer.writeStartDocument();
         writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
         writer.writeNamespace(version.getPrefix(), version.nsUri);
         writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
         if (extns != null) {
            Iterator var12 = extns.iterator();

            while(var12.hasNext()) {
               WSEndpointReference.EPRExtension e = (WSEndpointReference.EPRExtension)var12.next();
               XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
               XMLStreamReader r = e.readAsXMLStreamReader();
               c.bridge(r, writer);
               XMLStreamReaderFactory.recycle(r);
            }
         }

         writer.writeEndElement();
         writer.writeEndDocument();
         writer.flush();
         return writer.getXMLStreamBuffer();
      } catch (XMLStreamException var16) {
         throw new WebServiceException(var16);
      }
   }

   private static void writePartialEPRInfoset(StreamWriterBufferCreator writer, AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable Map<QName, String> attributes) throws XMLStreamException {
      Iterator var11;
      if (attributes != null) {
         var11 = attributes.entrySet().iterator();

         while(var11.hasNext()) {
            Map.Entry<QName, String> entry = (Map.Entry)var11.next();
            QName qname = (QName)entry.getKey();
            writer.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), (String)entry.getValue());
         }
      }

      writer.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
      writer.writeCharacters(address);
      writer.writeEndElement();
      if (referenceParameters != null && referenceParameters.size() > 0) {
         writer.writeStartElement(version.getPrefix(), version.eprType.referenceParameters, version.nsUri);
         var11 = referenceParameters.iterator();

         while(var11.hasNext()) {
            Element e = (Element)var11.next();
            DOMUtil.serializeNode(e, writer);
         }

         writer.writeEndElement();
      }

      switch(version) {
      case W3C:
         writeW3CMetaData(writer, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace);
         break;
      case MEMBER:
         writeMSMetaData(writer, service, port, portType, metadata);
         if (wsdlAddress != null) {
            writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI());
            writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI());
            writer.writeAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
            writeWsdl(writer, service, wsdlAddress);
            writer.writeEndElement();
            writer.writeEndElement();
         }
      }

   }

   private static boolean isEmty(QName qname) {
      return qname == null || qname.toString().trim().length() == 0;
   }

   private static void writeW3CMetaData(StreamWriterBufferCreator writer, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace) throws XMLStreamException {
      if (!isEmty(service) || !isEmty(port) || !isEmty(portType) || metadata != null) {
         writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart(), AddressingVersion.W3C.nsUri);
         writer.writeNamespace(AddressingVersion.W3C.getWsdlPrefix(), AddressingVersion.W3C.wsdlNsUri);
         if (wsdlAddress != null) {
            writeWsdliLocation(writer, service, wsdlAddress, wsdlTargetNamespace);
         }

         String servicePrefix;
         if (portType != null) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.portTypeName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            servicePrefix = portType.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
               servicePrefix = "wsns";
            }

            writer.writeNamespace(servicePrefix, portType.getNamespaceURI());
            writer.writeCharacters(servicePrefix + ":" + portType.getLocalPart());
            writer.writeEndElement();
         }

         if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.serviceName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            servicePrefix = service.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
               servicePrefix = "wsns";
            }

            writer.writeNamespace(servicePrefix, service.getNamespaceURI());
            if (port != null) {
               writer.writeAttribute(AddressingVersion.W3C.eprType.portName, port.getLocalPart());
            }

            writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
            writer.writeEndElement();
         }

         if (metadata != null) {
            Iterator var9 = metadata.iterator();

            while(var9.hasNext()) {
               Element e = (Element)var9.next();
               DOMUtil.serializeNode(e, writer);
            }
         }

         writer.writeEndElement();
      }
   }

   private static void writeWsdliLocation(StreamWriterBufferCreator writer, QName service, String wsdlAddress, String wsdlTargetNamespace) throws XMLStreamException {
      String wsdliLocation = "";
      if (wsdlTargetNamespace != null) {
         wsdliLocation = wsdlTargetNamespace + " ";
      } else {
         if (service == null) {
            throw new WebServiceException("WSDL target Namespace cannot be resolved");
         }

         wsdliLocation = service.getNamespaceURI() + " ";
      }

      wsdliLocation = wsdliLocation + wsdlAddress;
      writer.writeNamespace("wsdli", "http://www.w3.org/ns/wsdl-instance");
      writer.writeAttribute("wsdli", "http://www.w3.org/ns/wsdl-instance", "wsdlLocation", wsdliLocation);
   }

   private static void writeMSMetaData(StreamWriterBufferCreator writer, QName service, QName port, QName portType, List<Element> metadata) throws XMLStreamException {
      String servicePrefix;
      if (portType != null) {
         writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.portTypeName, AddressingVersion.MEMBER.nsUri);
         servicePrefix = portType.getPrefix();
         if (servicePrefix == null || servicePrefix.equals("")) {
            servicePrefix = "wsns";
         }

         writer.writeNamespace(servicePrefix, portType.getNamespaceURI());
         writer.writeCharacters(servicePrefix + ":" + portType.getLocalPart());
         writer.writeEndElement();
      }

      if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
         writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.serviceName, AddressingVersion.MEMBER.nsUri);
         servicePrefix = service.getPrefix();
         if (servicePrefix == null || servicePrefix.equals("")) {
            servicePrefix = "wsns";
         }

         writer.writeNamespace(servicePrefix, service.getNamespaceURI());
         if (port != null) {
            writer.writeAttribute(AddressingVersion.MEMBER.eprType.portName, port.getLocalPart());
         }

         writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
         writer.writeEndElement();
      }

   }

   private static void writeWsdl(StreamWriterBufferCreator writer, QName service, String wsdlAddress) throws XMLStreamException {
      writer.writeStartElement("wsdl", WSDLConstants.QNAME_DEFINITIONS.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
      writer.writeNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
      writer.writeStartElement("wsdl", WSDLConstants.QNAME_IMPORT.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
      writer.writeAttribute("namespace", service.getNamespaceURI());
      writer.writeAttribute("location", wsdlAddress);
      writer.writeEndElement();
      writer.writeEndElement();
   }

   @Nullable
   public static WSEndpointReference create(@Nullable EndpointReference epr) {
      return epr != null ? new WSEndpointReference(epr) : null;
   }

   @NotNull
   public WSEndpointReference createWithAddress(@NotNull URI newAddress) {
      return this.createWithAddress(newAddress.toString());
   }

   @NotNull
   public WSEndpointReference createWithAddress(@NotNull URL newAddress) {
      return this.createWithAddress(newAddress.toString());
   }

   @NotNull
   public WSEndpointReference createWithAddress(@NotNull final String newAddress) {
      MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
      XMLFilterImpl filter = new XMLFilterImpl() {
         private boolean inAddress = false;

         public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (localName.equals("Address") && uri.equals(WSEndpointReference.this.version.nsUri)) {
               this.inAddress = true;
            }

            super.startElement(uri, localName, qName, atts);
         }

         public void characters(char[] ch, int start, int length) throws SAXException {
            if (!this.inAddress) {
               super.characters(ch, start, length);
            }

         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            if (this.inAddress) {
               super.characters(newAddress.toCharArray(), 0, newAddress.length());
            }

            this.inAddress = false;
            super.endElement(uri, localName, qName);
         }
      };
      filter.setContentHandler(xsb.createFromSAXBufferCreator());

      try {
         this.infoset.writeTo(filter, false);
      } catch (SAXException var5) {
         throw new AssertionError(var5);
      }

      return new WSEndpointReference(xsb, this.version);
   }

   @NotNull
   public EndpointReference toSpec() {
      return ProviderImpl.INSTANCE.readEndpointReference(this.asSource("EndpointReference"));
   }

   @NotNull
   public <T extends EndpointReference> T toSpec(Class<T> clazz) {
      return EndpointReferenceUtil.transform(clazz, this.toSpec());
   }

   @NotNull
   public <T> T getPort(@NotNull Service jaxwsService, @NotNull Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return jaxwsService.getPort(this.toSpec(), serviceEndpointInterface, features);
   }

   @NotNull
   public <T> Dispatch<T> createDispatch(@NotNull Service jaxwsService, @NotNull Class<T> type, @NotNull Service.Mode mode, WebServiceFeature... features) {
      return jaxwsService.createDispatch(this.toSpec(), type, mode, features);
   }

   @NotNull
   public Dispatch<Object> createDispatch(@NotNull Service jaxwsService, @NotNull JAXBContext context, @NotNull Service.Mode mode, WebServiceFeature... features) {
      return jaxwsService.createDispatch(this.toSpec(), context, mode, features);
   }

   @NotNull
   public AddressingVersion getVersion() {
      return this.version;
   }

   @NotNull
   public String getAddress() {
      return this.address;
   }

   public boolean isAnonymous() {
      return this.address.equals(this.version.anonymousUri);
   }

   public boolean isNone() {
      return this.address.equals(this.version.noneUri);
   }

   private void parse() throws XMLStreamException {
      StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
      if (xsr.getEventType() == 7) {
         xsr.nextTag();
      }

      assert xsr.getEventType() == 1;

      String rootLocalName = xsr.getLocalName();
      if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
         throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
      } else {
         this.rootElement = new QName(xsr.getNamespaceURI(), rootLocalName);
         ArrayList marks = null;

         while(true) {
            while(xsr.nextTag() == 1) {
               String localName = xsr.getLocalName();
               XMLStreamBuffer mark;
               if (this.version.isReferenceParameter(localName)) {
                  while((mark = xsr.nextTagAndMark()) != null) {
                     if (marks == null) {
                        marks = new ArrayList();
                     }

                     marks.add(this.version.createReferenceParameterHeader(mark, xsr.getNamespaceURI(), xsr.getLocalName()));
                     XMLStreamReaderUtil.skipElement(xsr);
                  }
               } else if (localName.equals("Address")) {
                  if (this.address != null) {
                     throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), AddressingVersion.fault_duplicateAddressInEpr);
                  }

                  this.address = xsr.getElementText().trim();
               } else {
                  XMLStreamReaderUtil.skipElement(xsr);
               }
            }

            if (marks == null) {
               this.referenceParameters = EMPTY_ARRAY;
            } else {
               this.referenceParameters = (Header[])marks.toArray(new Header[marks.size()]);
            }

            if (this.address == null) {
               throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), this.version.fault_missingAddressInEpr);
            }

            return;
         }
      }
   }

   public XMLStreamReader read(@NotNull final String localName) throws XMLStreamException {
      return new StreamReaderBufferProcessor(this.infoset) {
         protected void processElement(String prefix, String uri, String _localName, boolean inScope) {
            if (this._depth == 0) {
               _localName = localName;
            }

            super.processElement(prefix, uri, _localName, WSEndpointReference.this.isInscope(WSEndpointReference.this.infoset, this._depth));
         }
      };
   }

   private boolean isInscope(XMLStreamBuffer buffer, int depth) {
      return buffer.getInscopeNamespaces().size() > 0 && depth == 0;
   }

   public Source asSource(@NotNull String localName) {
      return new SAXSource(new WSEndpointReference.SAXBufferProcessorImpl(localName), new InputSource());
   }

   public void writeTo(@NotNull String localName, ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      WSEndpointReference.SAXBufferProcessorImpl p = new WSEndpointReference.SAXBufferProcessorImpl(localName);
      p.setContentHandler(contentHandler);
      p.setErrorHandler(errorHandler);
      p.process(this.infoset, fragment);
   }

   public void writeTo(@NotNull final String localName, @NotNull XMLStreamWriter w) throws XMLStreamException {
      this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(w) {
         private boolean root = true;

         public void writeStartDocument() throws XMLStreamException {
         }

         public void writeStartDocument(String encoding, String version) throws XMLStreamException {
         }

         public void writeStartDocument(String version) throws XMLStreamException {
         }

         public void writeEndDocument() throws XMLStreamException {
         }

         private String override(String ln) {
            if (this.root) {
               this.root = false;
               return localName;
            } else {
               return ln;
            }
         }

         public void writeStartElement(String localNamex) throws XMLStreamException {
            super.writeStartElement(this.override(localNamex));
         }

         public void writeStartElement(String namespaceURI, String localNamex) throws XMLStreamException {
            super.writeStartElement(namespaceURI, this.override(localNamex));
         }

         public void writeStartElement(String prefix, String localNamex, String namespaceURI) throws XMLStreamException {
            super.writeStartElement(prefix, this.override(localNamex), namespaceURI);
         }
      }, true);
   }

   public Header createHeader(QName rootTagName) {
      return new EPRHeader(rootTagName, this);
   }

   /** @deprecated */
   public void addReferenceParametersToList(HeaderList outbound) {
      Header[] var2 = this.referenceParameters;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Header header = var2[var4];
         outbound.add(header);
      }

   }

   public void addReferenceParametersToList(MessageHeaders outbound) {
      Header[] var2 = this.referenceParameters;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Header header = var2[var4];
         outbound.add(header);
      }

   }

   public void addReferenceParameters(HeaderList headers) {
      if (headers != null) {
         Header[] hs = new Header[this.referenceParameters.length + headers.size()];
         System.arraycopy(this.referenceParameters, 0, hs, 0, this.referenceParameters.length);
         int i = this.referenceParameters.length;

         Header h;
         for(Iterator var4 = headers.iterator(); var4.hasNext(); hs[i++] = h) {
            h = (Header)var4.next();
         }

         this.referenceParameters = hs;
      }

   }

   public String toString() {
      try {
         StringWriter sw = new StringWriter();
         XmlUtil.newTransformer().transform(this.asSource("EndpointReference"), new StreamResult(sw));
         return sw.toString();
      } catch (TransformerException var2) {
         return var2.toString();
      }
   }

   public QName getName() {
      return this.rootElement;
   }

   @Nullable
   public WSEndpointReference.EPRExtension getEPRExtension(QName extnQName) throws XMLStreamException {
      if (this.rootEprExtensions == null) {
         this.parseEPRExtensions();
      }

      return (WSEndpointReference.EPRExtension)this.rootEprExtensions.get(extnQName);
   }

   @NotNull
   public Collection<WSEndpointReference.EPRExtension> getEPRExtensions() throws XMLStreamException {
      if (this.rootEprExtensions == null) {
         this.parseEPRExtensions();
      }

      return this.rootEprExtensions.values();
   }

   private void parseEPRExtensions() throws XMLStreamException {
      this.rootEprExtensions = new HashMap();
      StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
      if (xsr.getEventType() == 7) {
         xsr.nextTag();
      }

      assert xsr.getEventType() == 1;

      if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
         throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
      } else {
         XMLStreamBuffer mark;
         while((mark = xsr.nextTagAndMark()) != null) {
            String localName = xsr.getLocalName();
            String ns = xsr.getNamespaceURI();
            if (this.version.nsUri.equals(ns)) {
               XMLStreamReaderUtil.skipElement(xsr);
            } else {
               QName qn = new QName(ns, localName);
               this.rootEprExtensions.put(qn, new WSEPRExtension(mark, qn));
               XMLStreamReaderUtil.skipElement(xsr);
            }
         }

      }
   }

   @NotNull
   public WSEndpointReference.Metadata getMetaData() {
      return new WSEndpointReference.Metadata();
   }

   public class Metadata {
      @Nullable
      private QName serviceName;
      @Nullable
      private QName portName;
      @Nullable
      private QName portTypeName;
      @Nullable
      private Source wsdlSource;
      @Nullable
      private String wsdliLocation;

      @Nullable
      public QName getServiceName() {
         return this.serviceName;
      }

      @Nullable
      public QName getPortName() {
         return this.portName;
      }

      @Nullable
      public QName getPortTypeName() {
         return this.portTypeName;
      }

      @Nullable
      public Source getWsdlSource() {
         return this.wsdlSource;
      }

      @Nullable
      public String getWsdliLocation() {
         return this.wsdliLocation;
      }

      private Metadata() {
         try {
            this.parseMetaData();
         } catch (XMLStreamException var3) {
            throw new WebServiceException(var3);
         }
      }

      private void parseMetaData() throws XMLStreamException {
         StreamReaderBufferProcessor xsr = WSEndpointReference.this.infoset.readAsXMLStreamReader();
         if (xsr.getEventType() == 7) {
            xsr.nextTag();
         }

         assert xsr.getEventType() == 1;

         String rootElement = xsr.getLocalName();
         if (!xsr.getNamespaceURI().equals(WSEndpointReference.this.version.nsUri)) {
            throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(WSEndpointReference.this.version.nsUri, xsr.getNamespaceURI()));
         } else {
            String localName;
            String ns;
            String wsdlLocation;
            if (WSEndpointReference.this.version != AddressingVersion.W3C) {
               if (WSEndpointReference.this.version == AddressingVersion.MEMBER) {
                  do {
                     localName = xsr.getLocalName();
                     ns = xsr.getNamespaceURI();
                     if (localName.equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getLocalPart()) && ns.equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getNamespaceURI())) {
                        while(xsr.nextTag() == 1) {
                           while(true) {
                              while(true) {
                                 XMLStreamBuffer markx;
                                 if ((markx = xsr.nextTagAndMark()) != null) {
                                    localName = xsr.getLocalName();
                                    ns = xsr.getNamespaceURI();
                                    if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                       this.wsdlSource = new XMLStreamBufferSource(markx);
                                    } else {
                                       XMLStreamReaderUtil.skipElement(xsr);
                                    }
                                 }
                              }
                           }
                        }
                     } else if (localName.equals(WSEndpointReference.this.version.eprType.serviceName)) {
                        wsdlLocation = xsr.getAttributeValue((String)null, WSEndpointReference.this.version.eprType.portName);
                        this.serviceName = this.getElementTextAsQName(xsr);
                        if (this.serviceName != null && wsdlLocation != null) {
                           this.portName = new QName(this.serviceName.getNamespaceURI(), wsdlLocation);
                        }
                     } else if (localName.equals(WSEndpointReference.this.version.eprType.portTypeName)) {
                        this.portTypeName = this.getElementTextAsQName(xsr);
                     } else if (!xsr.getLocalName().equals(rootElement)) {
                        XMLStreamReaderUtil.skipElement(xsr);
                     }
                  } while(XMLStreamReaderUtil.nextElementContent(xsr) == 1);
               }
            } else {
               while(true) {
                  if (!xsr.getLocalName().equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getLocalPart())) {
                     if (!xsr.getLocalName().equals(rootElement)) {
                        XMLStreamReaderUtil.skipElement(xsr);
                     }
                  } else {
                     wsdlLocation = xsr.getAttributeValue("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                     if (wsdlLocation != null) {
                        this.wsdliLocation = wsdlLocation.trim();
                     }

                     label116:
                     while(true) {
                        while(true) {
                           XMLStreamBuffer mark;
                           if ((mark = xsr.nextTagAndMark()) == null) {
                              break label116;
                           }

                           localName = xsr.getLocalName();
                           ns = xsr.getNamespaceURI();
                           if (localName.equals(WSEndpointReference.this.version.eprType.serviceName)) {
                              String portStr = xsr.getAttributeValue((String)null, WSEndpointReference.this.version.eprType.portName);
                              if (this.serviceName != null) {
                                 throw new RuntimeException("More than one " + WSEndpointReference.this.version.eprType.serviceName + " element in EPR Metadata");
                              }

                              this.serviceName = this.getElementTextAsQName(xsr);
                              if (this.serviceName != null && portStr != null) {
                                 this.portName = new QName(this.serviceName.getNamespaceURI(), portStr);
                              }
                           } else if (localName.equals(WSEndpointReference.this.version.eprType.portTypeName)) {
                              if (this.portTypeName != null) {
                                 throw new RuntimeException("More than one " + WSEndpointReference.this.version.eprType.portTypeName + " element in EPR Metadata");
                              }

                              this.portTypeName = this.getElementTextAsQName(xsr);
                           } else if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                              this.wsdlSource = new XMLStreamBufferSource(mark);
                           } else {
                              XMLStreamReaderUtil.skipElement(xsr);
                           }
                        }
                     }
                  }

                  if (XMLStreamReaderUtil.nextElementContent(xsr) != 1) {
                     if (this.wsdliLocation != null) {
                        wsdlLocation = this.wsdliLocation.trim();
                        wsdlLocation = wsdlLocation.substring(this.wsdliLocation.lastIndexOf(" "));
                        this.wsdlSource = new StreamSource(wsdlLocation);
                     }
                     break;
                  }
               }
            }

         }
      }

      private QName getElementTextAsQName(StreamReaderBufferProcessor xsr) throws XMLStreamException {
         String text = xsr.getElementText().trim();
         String prefix = XmlUtil.getPrefix(text);
         String name = XmlUtil.getLocalPart(text);
         if (name != null) {
            if (prefix == null) {
               return new QName((String)null, name);
            }

            String ns = xsr.getNamespaceURI(prefix);
            if (ns != null) {
               return new QName(ns, name, prefix);
            }
         }

         return null;
      }

      // $FF: synthetic method
      Metadata(Object x1) {
         this();
      }
   }

   public abstract static class EPRExtension {
      public abstract XMLStreamReader readAsXMLStreamReader() throws XMLStreamException;

      public abstract QName getQName();
   }

   class SAXBufferProcessorImpl extends SAXBufferProcessor {
      private final String rootLocalName;
      private boolean root = true;

      public SAXBufferProcessorImpl(String rootLocalName) {
         super(WSEndpointReference.this.infoset, false);
         this.rootLocalName = rootLocalName;
      }

      protected void processElement(String uri, String localName, String qName, boolean inscope) throws SAXException {
         if (this.root) {
            this.root = false;
            if (qName.equals(localName)) {
               qName = localName = this.rootLocalName;
            } else {
               localName = this.rootLocalName;
               int idx = qName.indexOf(58);
               qName = qName.substring(0, idx + 1) + this.rootLocalName;
            }
         }

         super.processElement(uri, localName, qName, inscope);
      }
   }
}
