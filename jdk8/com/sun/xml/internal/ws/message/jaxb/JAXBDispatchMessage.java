package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.PayloadElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class JAXBDispatchMessage extends AbstractMessageImpl {
   private final Object jaxbObject;
   private final XMLBridge bridge;
   private final JAXBContext rawContext;
   private QName payloadQName;

   private JAXBDispatchMessage(JAXBDispatchMessage that) {
      super((AbstractMessageImpl)that);
      this.jaxbObject = that.jaxbObject;
      this.rawContext = that.rawContext;
      this.bridge = that.bridge;
   }

   public JAXBDispatchMessage(JAXBContext rawContext, Object jaxbObject, SOAPVersion soapVersion) {
      super(soapVersion);
      this.bridge = null;
      this.rawContext = rawContext;
      this.jaxbObject = jaxbObject;
   }

   public JAXBDispatchMessage(BindingContext context, Object jaxbObject, SOAPVersion soapVersion) {
      super(soapVersion);
      this.bridge = context.createFragmentBridge();
      this.rawContext = null;
      this.jaxbObject = jaxbObject;
   }

   protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      throw new UnsupportedOperationException();
   }

   public boolean hasHeaders() {
      return false;
   }

   public MessageHeaders getHeaders() {
      return null;
   }

   public String getPayloadLocalPart() {
      if (this.payloadQName == null) {
         this.readPayloadElement();
      }

      return this.payloadQName.getLocalPart();
   }

   public String getPayloadNamespaceURI() {
      if (this.payloadQName == null) {
         this.readPayloadElement();
      }

      return this.payloadQName.getNamespaceURI();
   }

   private void readPayloadElement() {
      PayloadElementSniffer sniffer = new PayloadElementSniffer();

      try {
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.FALSE);
            m.marshal(this.jaxbObject, (ContentHandler)sniffer);
         } else {
            this.bridge.marshal(this.jaxbObject, (ContentHandler)sniffer, (AttachmentMarshaller)null);
         }
      } catch (JAXBException var3) {
         this.payloadQName = sniffer.getPayloadQName();
      }

   }

   public boolean hasPayload() {
      return true;
   }

   public Source readPayloadAsSource() {
      throw new UnsupportedOperationException();
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public Message copy() {
      return new JAXBDispatchMessage(this);
   }

   public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
      try {
         AttachmentMarshaller am = sw instanceof MtomStreamWriter ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
         String encoding = XMLStreamWriterUtil.getEncoding(sw);
         OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
         if (this.rawContext != null) {
            Marshaller m = this.rawContext.createMarshaller();
            m.setProperty("jaxb.fragment", Boolean.FALSE);
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
}
