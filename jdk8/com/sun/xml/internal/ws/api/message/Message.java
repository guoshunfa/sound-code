package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class Message {
   protected AttachmentSet attachmentSet;
   private WSDLBoundOperation operation = null;
   private WSDLOperationMapping wsdlOperationMapping = null;
   private MessageMetadata messageMetadata = null;
   private Boolean isOneWay;

   public abstract boolean hasHeaders();

   @NotNull
   public abstract MessageHeaders getHeaders();

   @NotNull
   public AttachmentSet getAttachments() {
      if (this.attachmentSet == null) {
         this.attachmentSet = new AttachmentSetImpl();
      }

      return this.attachmentSet;
   }

   protected boolean hasAttachments() {
      return this.attachmentSet != null;
   }

   public void setMessageMedadata(MessageMetadata metadata) {
      this.messageMetadata = metadata;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public final WSDLBoundOperation getOperation(@NotNull WSDLBoundPortType boundPortType) {
      if (this.operation == null && this.messageMetadata != null) {
         if (this.wsdlOperationMapping == null) {
            this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
         }

         if (this.wsdlOperationMapping != null) {
            this.operation = this.wsdlOperationMapping.getWSDLBoundOperation();
         }
      }

      if (this.operation == null) {
         this.operation = boundPortType.getOperation(this.getPayloadNamespaceURI(), this.getPayloadLocalPart());
      }

      return this.operation;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public final WSDLBoundOperation getOperation(@NotNull WSDLPort port) {
      return this.getOperation(port.getBinding());
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public final JavaMethod getMethod(@NotNull SEIModel seiModel) {
      if (this.wsdlOperationMapping == null && this.messageMetadata != null) {
         this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
      }

      if (this.wsdlOperationMapping != null) {
         return this.wsdlOperationMapping.getJavaMethod();
      } else {
         String localPart = this.getPayloadLocalPart();
         String nsUri;
         if (localPart == null) {
            localPart = "";
            nsUri = "";
         } else {
            nsUri = this.getPayloadNamespaceURI();
         }

         QName name = new QName(nsUri, localPart);
         return seiModel.getJavaMethod(name);
      }
   }

   public boolean isOneWay(@NotNull WSDLPort port) {
      if (this.isOneWay == null) {
         WSDLBoundOperation op = this.getOperation(port);
         if (op != null) {
            this.isOneWay = op.getOperation().isOneWay();
         } else {
            this.isOneWay = false;
         }
      }

      return this.isOneWay;
   }

   public final void assertOneWay(boolean value) {
      assert this.isOneWay == null || this.isOneWay == value;

      this.isOneWay = value;
   }

   @Nullable
   public abstract String getPayloadLocalPart();

   public abstract String getPayloadNamespaceURI();

   public abstract boolean hasPayload();

   public boolean isFault() {
      String localPart = this.getPayloadLocalPart();
      if (localPart != null && localPart.equals("Fault")) {
         String nsUri = this.getPayloadNamespaceURI();
         return nsUri.equals(SOAPVersion.SOAP_11.nsUri) || nsUri.equals(SOAPVersion.SOAP_12.nsUri);
      } else {
         return false;
      }
   }

   @Nullable
   public QName getFirstDetailEntryName() {
      assert this.isFault();

      Message msg = this.copy();

      try {
         SOAPFaultBuilder fault = SOAPFaultBuilder.create(msg);
         return fault.getFirstDetailEntryName();
      } catch (JAXBException var3) {
         throw new WebServiceException(var3);
      }
   }

   public abstract Source readEnvelopeAsSource();

   public abstract Source readPayloadAsSource();

   public abstract SOAPMessage readAsSOAPMessage() throws SOAPException;

   public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
      return this.readAsSOAPMessage();
   }

   public static Map<String, List<String>> getTransportHeaders(Packet packet) {
      return getTransportHeaders(packet, packet.getState().isInbound());
   }

   public static Map<String, List<String>> getTransportHeaders(Packet packet, boolean inbound) {
      Map<String, List<String>> headers = null;
      String key = inbound ? "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers" : "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
      if (packet.supports(key)) {
         headers = (Map)packet.get(key);
      }

      return headers;
   }

   public static void addSOAPMimeHeaders(MimeHeaders mh, Map<String, List<String>> headers) {
      Iterator var2 = headers.entrySet().iterator();

      while(true) {
         Map.Entry e;
         do {
            if (!var2.hasNext()) {
               return;
            }

            e = (Map.Entry)var2.next();
         } while(((String)e.getKey()).equalsIgnoreCase("Content-Type"));

         Iterator var4 = ((List)e.getValue()).iterator();

         while(var4.hasNext()) {
            String value = (String)var4.next();
            mh.addHeader((String)e.getKey(), value);
         }
      }
   }

   public abstract <T> T readPayloadAsJAXB(Unmarshaller var1) throws JAXBException;

   /** @deprecated */
   public abstract <T> T readPayloadAsJAXB(Bridge<T> var1) throws JAXBException;

   public abstract <T> T readPayloadAsJAXB(XMLBridge<T> var1) throws JAXBException;

   public abstract XMLStreamReader readPayload() throws XMLStreamException;

   public void consume() {
   }

   public abstract void writePayloadTo(XMLStreamWriter var1) throws XMLStreamException;

   public abstract void writeTo(XMLStreamWriter var1) throws XMLStreamException;

   public abstract void writeTo(ContentHandler var1, ErrorHandler var2) throws SAXException;

   public abstract Message copy();

   /** @deprecated */
   @NotNull
   public String getID(@NotNull WSBinding binding) {
      return this.getID(binding.getAddressingVersion(), binding.getSOAPVersion());
   }

   /** @deprecated */
   @NotNull
   public String getID(AddressingVersion av, SOAPVersion sv) {
      String uuid = null;
      if (av != null) {
         uuid = AddressingUtils.getMessageID(this.getHeaders(), av, sv);
      }

      if (uuid == null) {
         uuid = generateMessageID();
         this.getHeaders().add(new StringHeader(av.messageIDTag, uuid));
      }

      return uuid;
   }

   public static String generateMessageID() {
      return "uuid:" + UUID.randomUUID().toString();
   }

   public SOAPVersion getSOAPVersion() {
      return null;
   }
}
