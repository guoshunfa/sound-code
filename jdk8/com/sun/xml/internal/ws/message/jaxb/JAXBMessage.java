package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class JAXBMessage extends AbstractMessageImpl implements StreamingSOAP {
   private MessageHeaders headers;
   private final Object jaxbObject;
   private final XMLBridge bridge;
   private final JAXBContext rawContext;
   private String nsUri;
   private String localName;
   private XMLStreamBuffer infoset;

   public static Message create(BindingContext context, Object jaxbObject, SOAPVersion soapVersion, MessageHeaders headers, AttachmentSet attachments) {
      if (!context.hasSwaRef()) {
         return new JAXBMessage(context, jaxbObject, soapVersion, headers, attachments);
      } else {
         try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            Marshaller m = context.createMarshaller();
            AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            m.setAttachmentMarshaller(am);
            am.cleanup();
            m.marshal(jaxbObject, xsb.createFromXMLStreamWriter());
            return new StreamMessage(headers, attachments, xsb.readAsXMLStreamReader(), soapVersion);
         } catch (JAXBException var8) {
            throw new WebServiceException(var8);
         } catch (XMLStreamException var9) {
            throw new WebServiceException(var9);
         }
      }
   }

   public static Message create(BindingContext context, Object jaxbObject, SOAPVersion soapVersion) {
      return create(context, jaxbObject, soapVersion, (MessageHeaders)null, (AttachmentSet)null);
   }

   /** @deprecated */
   public static Message create(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
      return create(BindingContextFactory.create(context), jaxbObject, soapVersion, (MessageHeaders)null, (AttachmentSet)null);
   }

   /** @deprecated */
   public static Message createRaw(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
      return new JAXBMessage(context, jaxbObject, soapVersion, (MessageHeaders)null, (AttachmentSet)null);
   }

   private JAXBMessage(BindingContext context, Object jaxbObject, SOAPVersion soapVer, MessageHeaders headers, AttachmentSet attachments) {
      super(soapVer);
      this.bridge = context.createFragmentBridge();
      this.rawContext = null;
      this.jaxbObject = jaxbObject;
      this.headers = headers;
      this.attachmentSet = attachments;
   }

   private JAXBMessage(JAXBContext rawContext, Object jaxbObject, SOAPVersion soapVer, MessageHeaders headers, AttachmentSet attachments) {
      super(soapVer);
      this.rawContext = rawContext;
      this.bridge = null;
      this.jaxbObject = jaxbObject;
      this.headers = headers;
      this.attachmentSet = attachments;
   }

   public static Message create(XMLBridge bridge, Object jaxbObject, SOAPVersion soapVer) {
      if (!bridge.context().hasSwaRef()) {
         return new JAXBMessage(bridge, jaxbObject, soapVer);
      } else {
         try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            AttachmentSetImpl attachments = new AttachmentSetImpl();
            AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            bridge.marshal(jaxbObject, (XMLStreamWriter)xsb.createFromXMLStreamWriter(), am);
            am.cleanup();
            return new StreamMessage((MessageHeaders)null, attachments, xsb.readAsXMLStreamReader(), soapVer);
         } catch (JAXBException var6) {
            throw new WebServiceException(var6);
         } catch (XMLStreamException var7) {
            throw new WebServiceException(var7);
         }
      }
   }

   private JAXBMessage(XMLBridge bridge, Object jaxbObject, SOAPVersion soapVer) {
      super(soapVer);
      this.bridge = bridge;
      this.rawContext = null;
      this.jaxbObject = jaxbObject;
      QName tagName = bridge.getTypeInfo().tagName;
      this.nsUri = tagName.getNamespaceURI();
      this.localName = tagName.getLocalPart();
      this.attachmentSet = new AttachmentSetImpl();
   }

   public JAXBMessage(JAXBMessage that) {
      super((AbstractMessageImpl)that);
      this.headers = that.headers;
      if (this.headers != null) {
         this.headers = new HeaderList(this.headers);
      }

      this.attachmentSet = that.attachmentSet;
      this.jaxbObject = that.jaxbObject;
      this.bridge = that.bridge;
      this.rawContext = that.rawContext;
   }

   public boolean hasHeaders() {
      return this.headers != null && this.headers.hasHeaders();
   }

   public MessageHeaders getHeaders() {
      if (this.headers == null) {
         this.headers = new HeaderList(this.getSOAPVersion());
      }

      return this.headers;
   }

   public String getPayloadLocalPart() {
      if (this.localName == null) {
         this.sniff();
      }

      return this.localName;
   }

   public String getPayloadNamespaceURI() {
      if (this.nsUri == null) {
         this.sniff();
      }

      return this.nsUri;
   }

   public boolean hasPayload() {
      return true;
   }

   private void sniff() {
      RootElementSniffer sniffer = new RootElementSniffer(false);

      try {
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.TRUE);
            m.marshal(this.jaxbObject, (ContentHandler)sniffer);
         } else {
            this.bridge.marshal(this.jaxbObject, (ContentHandler)sniffer, (AttachmentMarshaller)null);
         }
      } catch (JAXBException var3) {
         this.nsUri = sniffer.getNsUri();
         this.localName = sniffer.getLocalName();
      }

   }

   public Source readPayloadAsSource() {
      return new JAXBBridgeSource(this.bridge, this.jaxbObject);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      JAXBResult out = new JAXBResult(unmarshaller);

      try {
         out.getHandler().startDocument();
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.TRUE);
            m.marshal(this.jaxbObject, (Result)out);
         } else {
            this.bridge.marshal(this.jaxbObject, (Result)out);
         }

         out.getHandler().endDocument();
      } catch (SAXException var4) {
         throw new JAXBException(var4);
      }

      return out.getResult();
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      try {
         if (this.infoset == null) {
            if (this.rawContext != null) {
               XMLStreamBufferResult sbr = new XMLStreamBufferResult();
               Marshaller m = this.rawContext.createMarshaller();
               m.setProperty("jaxb.fragment", Boolean.TRUE);
               m.marshal(this.jaxbObject, (Result)sbr);
               this.infoset = sbr.getXMLStreamBuffer();
            } else {
               MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
               this.writePayloadTo(buffer.createFromXMLStreamWriter());
               this.infoset = buffer;
            }
         }

         XMLStreamReader reader = this.infoset.readAsXMLStreamReader();
         if (reader.getEventType() == 7) {
            XMLStreamReaderUtil.nextElementContent(reader);
         }

         return reader;
      } catch (JAXBException var3) {
         throw new WebServiceException(var3);
      }
   }

   protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      try {
         if (fragment) {
            contentHandler = new FragmentContentHandler((ContentHandler)contentHandler);
         }

         AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(this.attachmentSet);
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.TRUE);
            m.setAttachmentMarshaller(am);
            m.marshal(this.jaxbObject, (ContentHandler)contentHandler);
         } else {
            this.bridge.marshal(this.jaxbObject, (ContentHandler)contentHandler, am);
         }

         am.cleanup();
      } catch (JAXBException var6) {
         throw new WebServiceException(var6.getMessage(), var6);
      }
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      try {
         AttachmentMarshaller am = sw instanceof MtomStreamWriter ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
         String encoding = XMLStreamWriterUtil.getEncoding(sw);
         OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.TRUE);
            m.setAttachmentMarshaller((AttachmentMarshaller)am);
            if (os != null) {
               m.marshal(this.jaxbObject, os);
            } else {
               m.marshal(this.jaxbObject, sw);
            }
         } else if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
            this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), (AttachmentMarshaller)am);
         } else {
            this.bridge.marshal(this.jaxbObject, (XMLStreamWriter)sw, (AttachmentMarshaller)am);
         }

      } catch (JAXBException var6) {
         throw new WebServiceException(var6);
      }
   }

   public Message copy() {
      return new JAXBMessage(this);
   }

   public XMLStreamReader readEnvelope() {
      int base = this.soapVersion.ordinal() * 3;
      this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(base);
      this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(base + 2);
      List<XMLStreamReader> hReaders = new ArrayList();
      XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, (XMLReaderComposite.ElemInfo)null);
      XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
      Iterator var5 = this.getHeaders().asList().iterator();

      while(var5.hasNext()) {
         Header h = (Header)var5.next();

         try {
            hReaders.add(h.readHeader());
         } catch (XMLStreamException var10) {
            throw new RuntimeException(var10);
         }
      }

      XMLStreamReader soapHeader = null;
      if (hReaders.size() > 0) {
         this.headerTag = (TagInfoset)DEFAULT_TAGS.get(base + 1);
         XMLReaderComposite.ElemInfo hdrElem = new XMLReaderComposite.ElemInfo(this.headerTag, envElem);
         soapHeader = new XMLReaderComposite(hdrElem, (XMLStreamReader[])hReaders.toArray(new XMLStreamReader[hReaders.size()]));
      }

      try {
         XMLStreamReader payload = this.readPayload();
         XMLStreamReader soapBody = new XMLReaderComposite(bdyElem, new XMLStreamReader[]{payload});
         XMLStreamReader[] soapContent = soapHeader != null ? new XMLStreamReader[]{soapHeader, soapBody} : new XMLStreamReader[]{soapBody};
         return new XMLReaderComposite(envElem, soapContent);
      } catch (XMLStreamException var9) {
         throw new RuntimeException(var9);
      }
   }
}
