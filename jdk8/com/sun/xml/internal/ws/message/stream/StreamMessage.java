package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.StAXSource;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;

public class StreamMessage extends AbstractMessageImpl implements StreamingSOAP {
   @NotNull
   private XMLStreamReader reader;
   @Nullable
   private MessageHeaders headers;
   private String bodyPrologue;
   private String bodyEpilogue;
   private String payloadLocalName;
   private String payloadNamespaceURI;
   private Throwable consumedAt;
   private XMLStreamReader envelopeReader;
   private static final String SOAP_ENVELOPE = "Envelope";
   private static final String SOAP_HEADER = "Header";
   private static final String SOAP_BODY = "Body";
   static final StreamMessage.StreamHeaderDecoder SOAP12StreamHeaderDecoder = new StreamMessage.StreamHeaderDecoder() {
      public Header decodeHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
         return new StreamHeader12(reader, mark);
      }
   };
   static final StreamMessage.StreamHeaderDecoder SOAP11StreamHeaderDecoder = new StreamMessage.StreamHeaderDecoder() {
      public Header decodeHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
         return new StreamHeader11(reader, mark);
      }
   };

   public StreamMessage(SOAPVersion v) {
      super(v);
      this.bodyPrologue = null;
      this.bodyEpilogue = null;
      this.payloadLocalName = null;
      this.payloadNamespaceURI = null;
   }

   public StreamMessage(SOAPVersion v, @NotNull XMLStreamReader envelope, @NotNull AttachmentSet attachments) {
      super(v);
      this.bodyPrologue = null;
      this.bodyEpilogue = null;
      this.envelopeReader = envelope;
      this.attachmentSet = attachments;
   }

   public XMLStreamReader readEnvelope() {
      if (this.envelopeReader != null) {
         return this.envelopeReader;
      } else {
         List<XMLStreamReader> hReaders = new ArrayList();
         XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, (XMLReaderComposite.ElemInfo)null);
         XMLReaderComposite.ElemInfo hdrElem = this.headerTag != null ? new XMLReaderComposite.ElemInfo(this.headerTag, envElem) : null;
         XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
         Iterator var5 = this.getHeaders().asList().iterator();

         while(var5.hasNext()) {
            Header h = (Header)var5.next();

            try {
               hReaders.add(h.readHeader());
            } catch (XMLStreamException var9) {
               throw new RuntimeException(var9);
            }
         }

         XMLStreamReader soapHeader = hdrElem != null ? new XMLReaderComposite(hdrElem, (XMLStreamReader[])hReaders.toArray(new XMLStreamReader[hReaders.size()])) : null;
         XMLStreamReader[] payload = new XMLStreamReader[]{this.readPayload()};
         XMLStreamReader soapBody = new XMLReaderComposite(bdyElem, payload);
         XMLStreamReader[] soapContent = soapHeader != null ? new XMLStreamReader[]{soapHeader, soapBody} : new XMLStreamReader[]{soapBody};
         return new XMLReaderComposite(envElem, soapContent);
      }
   }

   public StreamMessage(@Nullable MessageHeaders headers, @NotNull AttachmentSet attachmentSet, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
      super(soapVersion);
      this.bodyPrologue = null;
      this.bodyEpilogue = null;
      this.init(headers, attachmentSet, reader, soapVersion);
   }

   private void init(@Nullable MessageHeaders headers, @NotNull AttachmentSet attachmentSet, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
      this.headers = headers;
      this.attachmentSet = attachmentSet;
      this.reader = reader;
      if (reader.getEventType() == 7) {
         XMLStreamReaderUtil.nextElementContent(reader);
      }

      if (reader.getEventType() == 2) {
         String body = reader.getLocalName();
         String nsUri = reader.getNamespaceURI();

         assert body != null;

         assert nsUri != null;

         if (!body.equals("Body") || !nsUri.equals(soapVersion.nsUri)) {
            throw new WebServiceException("Malformed stream: {" + nsUri + "}" + body);
         }

         this.payloadLocalName = null;
         this.payloadNamespaceURI = null;
      } else {
         this.payloadLocalName = reader.getLocalName();
         this.payloadNamespaceURI = reader.getNamespaceURI();
      }

      int base = soapVersion.ordinal() * 3;
      this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(base);
      this.headerTag = (TagInfoset)DEFAULT_TAGS.get(base + 1);
      this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(base + 2);
   }

   public StreamMessage(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @NotNull TagInfoset bodyTag, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
      this(envelopeTag, headerTag, attachmentSet, headers, (String)null, bodyTag, (String)null, reader, soapVersion);
   }

   public StreamMessage(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @Nullable String bodyPrologue, @NotNull TagInfoset bodyTag, @Nullable String bodyEpilogue, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
      super(soapVersion);
      this.bodyPrologue = null;
      this.bodyEpilogue = null;
      this.init(envelopeTag, headerTag, attachmentSet, headers, bodyPrologue, bodyTag, bodyEpilogue, reader, soapVersion);
   }

   private void init(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @Nullable String bodyPrologue, @NotNull TagInfoset bodyTag, @Nullable String bodyEpilogue, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
      this.init(headers, attachmentSet, reader, soapVersion);
      if (envelopeTag == null) {
         throw new IllegalArgumentException("EnvelopeTag TagInfoset cannot be null");
      } else if (bodyTag == null) {
         throw new IllegalArgumentException("BodyTag TagInfoset cannot be null");
      } else {
         this.envelopeTag = envelopeTag;
         this.headerTag = headerTag;
         this.bodyTag = bodyTag;
         this.bodyPrologue = bodyPrologue;
         this.bodyEpilogue = bodyEpilogue;
      }
   }

   public boolean hasHeaders() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.headers != null && this.headers.hasHeaders();
   }

   public MessageHeaders getHeaders() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      if (this.headers == null) {
         this.headers = new HeaderList(this.getSOAPVersion());
      }

      return this.headers;
   }

   public String getPayloadLocalPart() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.payloadLocalName;
   }

   public String getPayloadNamespaceURI() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.payloadNamespaceURI;
   }

   public boolean hasPayload() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.payloadLocalName != null;
   }

   public Source readPayloadAsSource() {
      if (this.hasPayload()) {
         assert this.unconsumed();

         return new StAXSource(this.reader, true, this.getInscopeNamespaces());
      } else {
         return null;
      }
   }

   private String[] getInscopeNamespaces() {
      NamespaceSupport nss = new NamespaceSupport();
      nss.pushContext();

      int i;
      for(i = 0; i < this.envelopeTag.ns.length; i += 2) {
         nss.declarePrefix(this.envelopeTag.ns[i], this.envelopeTag.ns[i + 1]);
      }

      nss.pushContext();

      for(i = 0; i < this.bodyTag.ns.length; i += 2) {
         nss.declarePrefix(this.bodyTag.ns[i], this.bodyTag.ns[i + 1]);
      }

      List<String> inscope = new ArrayList();
      Enumeration en = nss.getPrefixes();

      while(en.hasMoreElements()) {
         String prefix = (String)en.nextElement();
         inscope.add(prefix);
         inscope.add(nss.getURI(prefix));
      }

      return (String[])inscope.toArray(new String[inscope.size()]);
   }

   public Object readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      if (!this.hasPayload()) {
         return null;
      } else {
         assert this.unconsumed();

         if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
         }

         Object var2;
         try {
            var2 = unmarshaller.unmarshal(this.reader);
         } finally {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)null);
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
         }

         return var2;
      }
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      if (!this.hasPayload()) {
         return null;
      } else {
         assert this.unconsumed();

         T r = bridge.unmarshal((XMLStreamReader)this.reader, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
         XMLStreamReaderUtil.readRest(this.reader);
         XMLStreamReaderUtil.close(this.reader);
         XMLStreamReaderFactory.recycle(this.reader);
         return r;
      }
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      if (!this.hasPayload()) {
         return null;
      } else {
         assert this.unconsumed();

         T r = bridge.unmarshal((XMLStreamReader)this.reader, this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
         XMLStreamReaderUtil.readRest(this.reader);
         XMLStreamReaderUtil.close(this.reader);
         XMLStreamReaderFactory.recycle(this.reader);
         return r;
      }
   }

   public void consume() {
      assert this.unconsumed();

      XMLStreamReaderUtil.readRest(this.reader);
      XMLStreamReaderUtil.close(this.reader);
      XMLStreamReaderFactory.recycle(this.reader);
   }

   public XMLStreamReader readPayload() {
      if (!this.hasPayload()) {
         return null;
      } else {
         assert this.unconsumed();

         return this.reader;
      }
   }

   public void writePayloadTo(XMLStreamWriter writer) throws XMLStreamException {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      assert this.unconsumed();

      if (this.payloadLocalName != null) {
         if (this.bodyPrologue != null) {
            writer.writeCharacters(this.bodyPrologue);
         }

         XMLStreamReaderToXMLStreamWriter conv = new XMLStreamReaderToXMLStreamWriter();

         while(this.reader.getEventType() != 8) {
            String name = this.reader.getLocalName();
            String nsUri = this.reader.getNamespaceURI();
            if (this.reader.getEventType() == 2) {
               if (this.isBodyElement(name, nsUri)) {
                  break;
               }

               String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
               if (whiteSpaces != null) {
                  this.bodyEpilogue = whiteSpaces;
                  writer.writeCharacters(whiteSpaces);
               }
            } else {
               conv.bridge(this.reader, writer);
            }
         }

         XMLStreamReaderUtil.readRest(this.reader);
         XMLStreamReaderUtil.close(this.reader);
         XMLStreamReaderFactory.recycle(this.reader);
      }
   }

   private boolean isBodyElement(String name, String nsUri) {
      return name.equals("Body") && nsUri.equals(this.soapVersion.nsUri);
   }

   public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      this.writeEnvelope(sw);
   }

   private void writeEnvelope(XMLStreamWriter writer) throws XMLStreamException {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      writer.writeStartDocument();
      this.envelopeTag.writeStart(writer);
      MessageHeaders hl = this.getHeaders();
      if (hl.hasHeaders() && this.headerTag == null) {
         this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, EMPTY_ATTS, new String[0]);
      }

      if (this.headerTag != null) {
         this.headerTag.writeStart(writer);
         if (hl.hasHeaders()) {
            Iterator var3 = hl.asList().iterator();

            while(var3.hasNext()) {
               Header h = (Header)var3.next();
               h.writeTo(writer);
            }
         }

         writer.writeEndElement();
      }

      this.bodyTag.writeStart(writer);
      if (this.hasPayload()) {
         this.writePayloadTo(writer);
      }

      writer.writeEndElement();
      writer.writeEndElement();
      writer.writeEndDocument();
   }

   public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      assert this.unconsumed();

      try {
         if (this.payloadLocalName == null) {
            return;
         }

         if (this.bodyPrologue != null) {
            char[] chars = this.bodyPrologue.toCharArray();
            contentHandler.characters(chars, 0, chars.length);
         }

         XMLStreamReaderToContentHandler conv = new XMLStreamReaderToContentHandler(this.reader, contentHandler, true, fragment, this.getInscopeNamespaces());

         while(this.reader.getEventType() != 8) {
            String name = this.reader.getLocalName();
            String nsUri = this.reader.getNamespaceURI();
            if (this.reader.getEventType() == 2) {
               if (this.isBodyElement(name, nsUri)) {
                  break;
               }

               String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
               if (whiteSpaces != null) {
                  this.bodyEpilogue = whiteSpaces;
                  char[] chars = whiteSpaces.toCharArray();
                  contentHandler.characters(chars, 0, chars.length);
               }
            } else {
               conv.bridge();
            }
         }

         XMLStreamReaderUtil.readRest(this.reader);
         XMLStreamReaderUtil.close(this.reader);
         XMLStreamReaderFactory.recycle(this.reader);
      } catch (XMLStreamException var9) {
         Location loc = var9.getLocation();
         if (loc == null) {
            loc = DummyLocation.INSTANCE;
         }

         SAXParseException x = new SAXParseException(var9.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), var9);
         errorHandler.error(x);
      }

   }

   public Message copy() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      try {
         assert this.unconsumed();

         this.consumedAt = null;
         MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
         StreamReaderBufferCreator c = new StreamReaderBufferCreator(xsb);
         c.storeElement(this.envelopeTag.nsUri, this.envelopeTag.localName, this.envelopeTag.prefix, this.envelopeTag.ns);
         c.storeElement(this.bodyTag.nsUri, this.bodyTag.localName, this.bodyTag.prefix, this.bodyTag.ns);
         if (this.hasPayload()) {
            while(this.reader.getEventType() != 8) {
               String name = this.reader.getLocalName();
               String nsUri = this.reader.getNamespaceURI();
               if (this.isBodyElement(name, nsUri) || this.reader.getEventType() == 8) {
                  break;
               }

               c.create(this.reader);
               if (this.reader.isWhiteSpace()) {
                  this.bodyEpilogue = XMLStreamReaderUtil.currentWhiteSpaceContent(this.reader);
               } else {
                  this.bodyEpilogue = null;
               }
            }
         }

         c.storeEndElement();
         c.storeEndElement();
         c.storeEndElement();
         XMLStreamReaderUtil.readRest(this.reader);
         XMLStreamReaderUtil.close(this.reader);
         XMLStreamReaderFactory.recycle(this.reader);
         this.reader = xsb.readAsXMLStreamReader();
         XMLStreamReader clone = xsb.readAsXMLStreamReader();
         this.proceedToRootElement(this.reader);
         this.proceedToRootElement(clone);
         return new StreamMessage(this.envelopeTag, this.headerTag, this.attachmentSet, HeaderList.copy(this.headers), this.bodyPrologue, this.bodyTag, this.bodyEpilogue, clone, this.soapVersion);
      } catch (XMLStreamException var5) {
         throw new WebServiceException("Failed to copy a message", var5);
      }
   }

   private void proceedToRootElement(XMLStreamReader xsr) throws XMLStreamException {
      assert xsr.getEventType() == 7;

      xsr.nextTag();
      xsr.nextTag();
      xsr.nextTag();

      assert xsr.getEventType() == 1 || xsr.getEventType() == 2;

   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      contentHandler.setDocumentLocator(NULL_LOCATOR);
      contentHandler.startDocument();
      this.envelopeTag.writeStart(contentHandler);
      if (this.hasHeaders() && this.headerTag == null) {
         this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, EMPTY_ATTS, new String[0]);
      }

      if (this.headerTag != null) {
         this.headerTag.writeStart(contentHandler);
         if (this.hasHeaders()) {
            MessageHeaders headers = this.getHeaders();
            Iterator var4 = headers.asList().iterator();

            while(var4.hasNext()) {
               Header h = (Header)var4.next();
               h.writeTo(contentHandler, errorHandler);
            }
         }

         this.headerTag.writeEnd(contentHandler);
      }

      this.bodyTag.writeStart(contentHandler);
      this.writePayloadTo(contentHandler, errorHandler, true);
      this.bodyTag.writeEnd(contentHandler);
      this.envelopeTag.writeEnd(contentHandler);
      contentHandler.endDocument();
   }

   private boolean unconsumed() {
      if (this.payloadLocalName == null) {
         return true;
      } else if (this.reader.getEventType() != 1) {
         AssertionError error = new AssertionError("StreamMessage has been already consumed. See the nested exception for where it's consumed");
         error.initCause(this.consumedAt);
         throw error;
      } else {
         this.consumedAt = (new Exception()).fillInStackTrace();
         return true;
      }
   }

   public String getBodyPrologue() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.bodyPrologue;
   }

   public String getBodyEpilogue() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      return this.bodyEpilogue;
   }

   public XMLStreamReader getReader() {
      if (this.envelopeReader != null) {
         readEnvelope(this);
      }

      assert this.unconsumed();

      return this.reader;
   }

   private static void readEnvelope(StreamMessage message) {
      if (message.envelopeReader != null) {
         XMLStreamReader reader = message.envelopeReader;
         message.envelopeReader = null;
         SOAPVersion soapVersion = message.soapVersion;
         if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
         }

         XMLStreamReaderUtil.verifyReaderState(reader, 1);
         if ("Envelope".equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
            throw new VersionMismatchException(soapVersion, new Object[]{soapVersion.nsUri, reader.getNamespaceURI()});
         } else {
            XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Envelope");
            TagInfoset envelopeTag = new TagInfoset(reader);
            Map<String, String> namespaces = new HashMap();

            for(int i = 0; i < reader.getNamespaceCount(); ++i) {
               namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }

            XMLStreamReaderUtil.nextElementContent(reader);
            XMLStreamReaderUtil.verifyReaderState(reader, 1);
            HeaderList headers = null;
            TagInfoset headerTag = null;
            if (reader.getLocalName().equals("Header") && reader.getNamespaceURI().equals(soapVersion.nsUri)) {
               headerTag = new TagInfoset(reader);

               for(int i = 0; i < reader.getNamespaceCount(); ++i) {
                  namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
               }

               XMLStreamReaderUtil.nextElementContent(reader);
               if (reader.getEventType() == 1) {
                  headers = new HeaderList(soapVersion);

                  try {
                     StreamMessage.StreamHeaderDecoder headerDecoder = SOAPVersion.SOAP_11.equals(soapVersion) ? SOAP11StreamHeaderDecoder : SOAP12StreamHeaderDecoder;
                     cacheHeaders(reader, namespaces, headers, headerDecoder);
                  } catch (XMLStreamException var9) {
                     throw new WebServiceException(var9);
                  }
               }

               XMLStreamReaderUtil.nextElementContent(reader);
            }

            XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Body");
            TagInfoset bodyTag = new TagInfoset(reader);
            String bodyPrologue = XMLStreamReaderUtil.nextWhiteSpaceContent(reader);
            message.init(envelopeTag, headerTag, message.attachmentSet, headers, bodyPrologue, bodyTag, (String)null, reader, soapVersion);
         }
      }
   }

   private static XMLStreamBuffer cacheHeaders(XMLStreamReader reader, Map<String, String> namespaces, HeaderList headers, StreamMessage.StreamHeaderDecoder headerDecoder) throws XMLStreamException {
      MutableXMLStreamBuffer buffer = createXMLStreamBuffer();
      StreamReaderBufferCreator creator = new StreamReaderBufferCreator();
      creator.setXMLStreamBuffer(buffer);

      while(reader.getEventType() == 1) {
         Map<String, String> headerBlockNamespaces = namespaces;
         if (reader.getNamespaceCount() > 0) {
            headerBlockNamespaces = new HashMap(namespaces);

            for(int i = 0; i < reader.getNamespaceCount(); ++i) {
               ((Map)headerBlockNamespaces).put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
         }

         XMLStreamBuffer mark = new XMLStreamBufferMark((Map)headerBlockNamespaces, creator);
         headers.add(headerDecoder.decodeHeader(reader, mark));
         creator.createElementFragment(reader, false);
         if (reader.getEventType() != 1 && reader.getEventType() != 2) {
            XMLStreamReaderUtil.nextElementContent(reader);
         }
      }

      return buffer;
   }

   private static MutableXMLStreamBuffer createXMLStreamBuffer() {
      return new MutableXMLStreamBuffer();
   }

   protected interface StreamHeaderDecoder {
      Header decodeHeader(XMLStreamReader var1, XMLStreamBuffer var2);
   }
}
