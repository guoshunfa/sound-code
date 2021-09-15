package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.ASCIIUtility;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public class SAAJMessage extends Message {
   private boolean parsedMessage;
   private boolean accessedMessage;
   private final SOAPMessage sm;
   private MessageHeaders headers;
   private List<Element> bodyParts;
   private Element payload;
   private String payloadLocalName;
   private String payloadNamespace;
   private SOAPVersion soapVersion;
   private NamedNodeMap bodyAttrs;
   private NamedNodeMap headerAttrs;
   private NamedNodeMap envelopeAttrs;
   private static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
   private static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
   private XMLStreamReader soapBodyFirstChildReader;
   private SOAPElement soapBodyFirstChild;

   public SAAJMessage(SOAPMessage sm) {
      this.sm = sm;
   }

   private SAAJMessage(MessageHeaders headers, AttachmentSet as, SOAPMessage sm, SOAPVersion version) {
      this.sm = sm;
      this.parse();
      if (headers == null) {
         headers = new HeaderList(version);
      }

      this.headers = (MessageHeaders)headers;
      this.attachmentSet = as;
   }

   private void parse() {
      if (!this.parsedMessage) {
         try {
            this.access();
            if (this.headers == null) {
               this.headers = new HeaderList(this.getSOAPVersion());
            }

            SOAPHeader header = this.sm.getSOAPHeader();
            if (header != null) {
               this.headerAttrs = header.getAttributes();
               Iterator iter = header.examineAllHeaderElements();

               while(iter.hasNext()) {
                  this.headers.add(new SAAJHeader((SOAPHeaderElement)iter.next()));
               }
            }

            this.attachmentSet = new SAAJMessage.SAAJAttachmentSet(this.sm);
            this.parsedMessage = true;
         } catch (SOAPException var3) {
            throw new WebServiceException(var3);
         }
      }

   }

   protected void access() {
      if (!this.accessedMessage) {
         try {
            this.envelopeAttrs = this.sm.getSOAPPart().getEnvelope().getAttributes();
            Node body = this.sm.getSOAPBody();
            this.bodyAttrs = body.getAttributes();
            this.soapVersion = SOAPVersion.fromNsUri(body.getNamespaceURI());
            this.bodyParts = DOMUtil.getChildElements(body);
            this.payload = this.bodyParts.size() > 0 ? (Element)this.bodyParts.get(0) : null;
            if (this.payload != null) {
               this.payloadLocalName = this.payload.getLocalName();
               this.payloadNamespace = this.payload.getNamespaceURI();
            }

            this.accessedMessage = true;
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

   }

   public boolean hasHeaders() {
      this.parse();
      return this.headers.hasHeaders();
   }

   @NotNull
   public MessageHeaders getHeaders() {
      this.parse();
      return this.headers;
   }

   @NotNull
   public AttachmentSet getAttachments() {
      if (this.attachmentSet == null) {
         this.attachmentSet = new SAAJMessage.SAAJAttachmentSet(this.sm);
      }

      return this.attachmentSet;
   }

   protected boolean hasAttachments() {
      return !this.getAttachments().isEmpty();
   }

   @Nullable
   public String getPayloadLocalPart() {
      this.soapBodyFirstChild();
      return this.payloadLocalName;
   }

   public String getPayloadNamespaceURI() {
      this.soapBodyFirstChild();
      return this.payloadNamespace;
   }

   public boolean hasPayload() {
      return this.soapBodyFirstChild() != null;
   }

   private void addAttributes(Element e, NamedNodeMap attrs) {
      if (attrs != null) {
         String elPrefix = e.getPrefix();

         for(int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (!"xmlns".equals(a.getPrefix()) && !"xmlns".equals(a.getLocalName())) {
               e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
            } else if ((elPrefix != null || !a.getLocalName().equals("xmlns")) && (elPrefix == null || !"xmlns".equals(a.getPrefix()) || !elPrefix.equals(a.getLocalName()))) {
               e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
            }
         }

      }
   }

   public Source readEnvelopeAsSource() {
      try {
         if (!this.parsedMessage) {
            SOAPEnvelope se = this.sm.getSOAPPart().getEnvelope();
            return new DOMSource(se);
         } else {
            SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
            this.addAttributes(msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
            SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
            this.addAttributes(newBody, this.bodyAttrs);
            Iterator var3 = this.bodyParts.iterator();

            while(var3.hasNext()) {
               Element part = (Element)var3.next();
               Node n = newBody.getOwnerDocument().importNode(part, true);
               newBody.appendChild(n);
            }

            this.addAttributes(msg.getSOAPHeader(), this.headerAttrs);
            var3 = this.headers.asList().iterator();

            while(var3.hasNext()) {
               Header header = (Header)var3.next();
               header.writeTo(msg);
            }

            SOAPEnvelope se = msg.getSOAPPart().getEnvelope();
            return new DOMSource(se);
         }
      } catch (SOAPException var6) {
         throw new WebServiceException(var6);
      }
   }

   public SOAPMessage readAsSOAPMessage() throws SOAPException {
      if (!this.parsedMessage) {
         return this.sm;
      } else {
         SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
         this.addAttributes(msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
         SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
         this.addAttributes(newBody, this.bodyAttrs);
         Iterator var3 = this.bodyParts.iterator();

         while(var3.hasNext()) {
            Element part = (Element)var3.next();
            Node n = newBody.getOwnerDocument().importNode(part, true);
            newBody.appendChild(n);
         }

         this.addAttributes(msg.getSOAPHeader(), this.headerAttrs);
         var3 = this.headers.asList().iterator();

         while(var3.hasNext()) {
            Header header = (Header)var3.next();
            header.writeTo(msg);
         }

         var3 = this.getAttachments().iterator();

         while(var3.hasNext()) {
            Attachment att = (Attachment)var3.next();
            AttachmentPart part = msg.createAttachmentPart();
            part.setDataHandler(att.asDataHandler());
            part.setContentId('<' + att.getContentId() + '>');
            this.addCustomMimeHeaders(att, part);
            msg.addAttachmentPart(part);
         }

         msg.saveChanges();
         return msg;
      }
   }

   private void addCustomMimeHeaders(Attachment att, AttachmentPart part) {
      if (att instanceof AttachmentEx) {
         Iterator allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();

         while(allMimeHeaders.hasNext()) {
            AttachmentEx.MimeHeader mh = (AttachmentEx.MimeHeader)allMimeHeaders.next();
            String name = mh.getName();
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Id".equalsIgnoreCase(name)) {
               part.addMimeHeader(name, mh.getValue());
            }
         }
      }

   }

   public Source readPayloadAsSource() {
      this.access();
      return this.payload != null ? new DOMSource(this.payload) : null;
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      this.access();
      if (this.payload != null) {
         if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
         }

         return unmarshaller.unmarshal((Node)this.payload);
      } else {
         return null;
      }
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      this.access();
      return this.payload != null ? bridge.unmarshal((Node)this.payload, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null)) : null;
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      this.access();
      return this.payload != null ? bridge.unmarshal((Node)this.payload, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null) : null;
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      return this.soapBodyFirstChildReader();
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      this.access();

      try {
         Iterator var2 = this.bodyParts.iterator();

         while(var2.hasNext()) {
            Element part = (Element)var2.next();
            DOMUtil.serializeNode(part, sw);
         }

      } catch (XMLStreamException var4) {
         throw new WebServiceException(var4);
      }
   }

   public void writeTo(XMLStreamWriter writer) throws XMLStreamException {
      try {
         writer.writeStartDocument();
         if (!this.parsedMessage) {
            DOMUtil.serializeNode(this.sm.getSOAPPart().getEnvelope(), writer);
         } else {
            SOAPEnvelope env = this.sm.getSOAPPart().getEnvelope();
            DOMUtil.writeTagWithAttributes(env, writer);
            if (this.hasHeaders()) {
               if (env.getHeader() != null) {
                  DOMUtil.writeTagWithAttributes(env.getHeader(), writer);
               } else {
                  writer.writeStartElement(env.getPrefix(), "Header", env.getNamespaceURI());
               }

               Iterator var3 = this.headers.asList().iterator();

               while(var3.hasNext()) {
                  Header h = (Header)var3.next();
                  h.writeTo(writer);
               }

               writer.writeEndElement();
            }

            DOMUtil.serializeNode(this.sm.getSOAPBody(), writer);
            writer.writeEndElement();
         }

         writer.writeEndDocument();
         writer.flush();
      } catch (SOAPException var5) {
         throw new XMLStreamException2(var5);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      String soapNsUri = this.soapVersion.nsUri;
      if (!this.parsedMessage) {
         DOMScanner ds = new DOMScanner();
         ds.setContentHandler(contentHandler);
         ds.scan((Document)this.sm.getSOAPPart());
      } else {
         contentHandler.setDocumentLocator(NULL_LOCATOR);
         contentHandler.startDocument();
         contentHandler.startPrefixMapping("S", soapNsUri);
         this.startPrefixMapping(contentHandler, this.envelopeAttrs, "S");
         contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", this.getAttributes(this.envelopeAttrs));
         if (this.hasHeaders()) {
            this.startPrefixMapping(contentHandler, this.headerAttrs, "S");
            contentHandler.startElement(soapNsUri, "Header", "S:Header", this.getAttributes(this.headerAttrs));
            MessageHeaders headers = this.getHeaders();
            Iterator var5 = headers.asList().iterator();

            while(var5.hasNext()) {
               Header h = (Header)var5.next();
               h.writeTo(contentHandler, errorHandler);
            }

            this.endPrefixMapping(contentHandler, this.headerAttrs, "S");
            contentHandler.endElement(soapNsUri, "Header", "S:Header");
         }

         this.startPrefixMapping(contentHandler, this.bodyAttrs, "S");
         contentHandler.startElement(soapNsUri, "Body", "S:Body", this.getAttributes(this.bodyAttrs));
         this.writePayloadTo(contentHandler, errorHandler, true);
         this.endPrefixMapping(contentHandler, this.bodyAttrs, "S");
         contentHandler.endElement(soapNsUri, "Body", "S:Body");
         this.endPrefixMapping(contentHandler, this.envelopeAttrs, "S");
         contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
      }

   }

   private AttributesImpl getAttributes(NamedNodeMap attrs) {
      AttributesImpl atts = new AttributesImpl();
      if (attrs == null) {
         return EMPTY_ATTS;
      } else {
         for(int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (!"xmlns".equals(a.getPrefix()) && !"xmlns".equals(a.getLocalName())) {
               atts.addAttribute(fixNull(a.getNamespaceURI()), a.getLocalName(), a.getName(), a.getSchemaTypeInfo().getTypeName(), a.getValue());
            }
         }

         return atts;
      }
   }

   private void startPrefixMapping(ContentHandler contentHandler, NamedNodeMap attrs, String excludePrefix) throws SAXException {
      if (attrs != null) {
         for(int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) && !fixNull(a.getPrefix()).equals(excludePrefix)) {
               contentHandler.startPrefixMapping(fixNull(a.getPrefix()), a.getNamespaceURI());
            }
         }

      }
   }

   private void endPrefixMapping(ContentHandler contentHandler, NamedNodeMap attrs, String excludePrefix) throws SAXException {
      if (attrs != null) {
         for(int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) && !fixNull(a.getPrefix()).equals(excludePrefix)) {
               contentHandler.endPrefixMapping(fixNull(a.getPrefix()));
            }
         }

      }
   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   private void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      if (fragment) {
         contentHandler = new FragmentContentHandler((ContentHandler)contentHandler);
      }

      DOMScanner ds = new DOMScanner();
      ds.setContentHandler((ContentHandler)contentHandler);
      ds.scan(this.payload);
   }

   public Message copy() {
      try {
         if (!this.parsedMessage) {
            return new SAAJMessage(this.readAsSOAPMessage());
         } else {
            SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
            SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
            Iterator var3 = this.bodyParts.iterator();

            while(var3.hasNext()) {
               Element part = (Element)var3.next();
               Node n = newBody.getOwnerDocument().importNode(part, true);
               newBody.appendChild(n);
            }

            this.addAttributes(newBody, this.bodyAttrs);
            return new SAAJMessage(this.getHeaders(), this.getAttachments(), msg, this.soapVersion);
         }
      } catch (SOAPException var6) {
         throw new WebServiceException(var6);
      }
   }

   public SOAPVersion getSOAPVersion() {
      return this.soapVersion;
   }

   protected XMLStreamReader getXMLStreamReader(SOAPElement soapElement) {
      return null;
   }

   protected XMLStreamReader createXMLStreamReader(SOAPElement soapElement) {
      DOMStreamReader dss = new DOMStreamReader();
      dss.setCurrentNode(soapElement);
      return dss;
   }

   protected XMLStreamReader soapBodyFirstChildReader() {
      if (this.soapBodyFirstChildReader != null) {
         return this.soapBodyFirstChildReader;
      } else {
         this.soapBodyFirstChild();
         if (this.soapBodyFirstChild != null) {
            this.soapBodyFirstChildReader = this.getXMLStreamReader(this.soapBodyFirstChild);
            if (this.soapBodyFirstChildReader == null) {
               this.soapBodyFirstChildReader = this.createXMLStreamReader(this.soapBodyFirstChild);
            }

            if (this.soapBodyFirstChildReader.getEventType() == 7) {
               try {
                  while(this.soapBodyFirstChildReader.getEventType() != 1) {
                     this.soapBodyFirstChildReader.next();
                  }
               } catch (XMLStreamException var2) {
                  throw new RuntimeException(var2);
               }
            }

            return this.soapBodyFirstChildReader;
         } else {
            this.payloadLocalName = null;
            this.payloadNamespace = null;
            return null;
         }
      }
   }

   SOAPElement soapBodyFirstChild() {
      if (this.soapBodyFirstChild != null) {
         return this.soapBodyFirstChild;
      } else {
         try {
            boolean foundElement = false;

            for(Node n = this.sm.getSOAPBody().getFirstChild(); n != null && !foundElement; n = n.getNextSibling()) {
               if (n.getNodeType() == 1) {
                  foundElement = true;
                  if (n instanceof SOAPElement) {
                     this.soapBodyFirstChild = (SOAPElement)n;
                     this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
                     this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
                     return this.soapBodyFirstChild;
                  }
               }
            }

            if (!foundElement) {
               return this.soapBodyFirstChild;
            } else {
               Iterator i = this.sm.getSOAPBody().getChildElements();

               Object o;
               do {
                  if (!i.hasNext()) {
                     return this.soapBodyFirstChild;
                  }

                  o = i.next();
               } while(!(o instanceof SOAPElement));

               this.soapBodyFirstChild = (SOAPElement)o;
               this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
               this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
               return this.soapBodyFirstChild;
            }
         } catch (SOAPException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   protected static class SAAJAttachmentSet implements AttachmentSet {
      private Map<String, Attachment> attMap;
      private Iterator attIter;

      public SAAJAttachmentSet(SOAPMessage sm) {
         this.attIter = sm.getAttachments();
      }

      public Attachment get(String contentId) {
         if (this.attMap == null) {
            if (!this.attIter.hasNext()) {
               return null;
            }

            this.attMap = this.createAttachmentMap();
         }

         return contentId.charAt(0) != '<' ? (Attachment)this.attMap.get('<' + contentId + '>') : (Attachment)this.attMap.get(contentId);
      }

      public boolean isEmpty() {
         if (this.attMap != null) {
            return this.attMap.isEmpty();
         } else {
            return !this.attIter.hasNext();
         }
      }

      public Iterator<Attachment> iterator() {
         if (this.attMap == null) {
            this.attMap = this.createAttachmentMap();
         }

         return this.attMap.values().iterator();
      }

      private Map<String, Attachment> createAttachmentMap() {
         HashMap map = new HashMap();

         while(this.attIter.hasNext()) {
            AttachmentPart ap = (AttachmentPart)this.attIter.next();
            map.put(ap.getContentId(), new SAAJMessage.SAAJAttachment(ap));
         }

         return map;
      }

      public void add(Attachment att) {
         this.attMap.put('<' + att.getContentId() + '>', att);
      }
   }

   protected static class SAAJAttachment implements AttachmentEx {
      final AttachmentPart ap;
      String contentIdNoAngleBracket;

      public SAAJAttachment(AttachmentPart part) {
         this.ap = part;
      }

      public String getContentId() {
         if (this.contentIdNoAngleBracket == null) {
            this.contentIdNoAngleBracket = this.ap.getContentId();
            if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
               this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
            }
         }

         return this.contentIdNoAngleBracket;
      }

      public String getContentType() {
         return this.ap.getContentType();
      }

      public byte[] asByteArray() {
         try {
            return this.ap.getRawContentBytes();
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public DataHandler asDataHandler() {
         try {
            return this.ap.getDataHandler();
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public Source asSource() {
         try {
            return new StreamSource(this.ap.getRawContent());
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public InputStream asInputStream() {
         try {
            return this.ap.getRawContent();
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public void writeTo(OutputStream os) throws IOException {
         try {
            ASCIIUtility.copyStream(this.ap.getRawContent(), os);
         } catch (SOAPException var3) {
            throw new WebServiceException(var3);
         }
      }

      public void writeTo(SOAPMessage saaj) {
         saaj.addAttachmentPart(this.ap);
      }

      AttachmentPart asAttachmentPart() {
         return this.ap;
      }

      public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
         final Iterator it = this.ap.getAllMimeHeaders();
         return new Iterator<AttachmentEx.MimeHeader>() {
            public boolean hasNext() {
               return it.hasNext();
            }

            public AttachmentEx.MimeHeader next() {
               final MimeHeader mh = (MimeHeader)it.next();
               return new AttachmentEx.MimeHeader() {
                  public String getName() {
                     return mh.getName();
                  }

                  public String getValue() {
                     return mh.getValue();
                  }
               };
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }
}
