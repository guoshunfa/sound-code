package com.sun.xml.internal.ws.encoding.xml;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.encoding.ContentType;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class XMLMessage {
   private static final int PLAIN_XML_FLAG = 1;
   private static final int MIME_MULTIPART_FLAG = 2;
   private static final int FI_ENCODED_FLAG = 16;

   public static Message create(String ct, InputStream in, WSFeatureList f) {
      try {
         in = StreamUtils.hasSomeData(in);
         if (in == null) {
            return Messages.createEmpty(SOAPVersion.SOAP_11);
         } else {
            Object data;
            if (ct != null) {
               ContentType contentType = new ContentType(ct);
               int contentTypeId = identifyContentType(contentType);
               if ((contentTypeId & 2) != 0) {
                  data = new XMLMessage.XMLMultiPart(ct, in, f);
               } else if ((contentTypeId & 1) != 0) {
                  data = new XMLMessage.XmlContent(ct, in, f);
               } else {
                  data = new XMLMessage.UnknownContent(ct, in);
               }
            } else {
               data = new XMLMessage.UnknownContent("application/octet-stream", in);
            }

            return (Message)data;
         }
      } catch (Exception var6) {
         throw new WebServiceException(var6);
      }
   }

   public static Message create(Source source) {
      return source == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
   }

   public static Message create(DataSource ds, WSFeatureList f) {
      try {
         return ds == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : create(ds.getContentType(), ds.getInputStream(), f);
      } catch (IOException var3) {
         throw new WebServiceException(var3);
      }
   }

   public static Message create(Exception e) {
      return new XMLMessage.FaultMessage(SOAPVersion.SOAP_11);
   }

   private static int getContentId(String ct) {
      try {
         ContentType contentType = new ContentType(ct);
         return identifyContentType(contentType);
      } catch (Exception var2) {
         throw new WebServiceException(var2);
      }
   }

   public static boolean isFastInfoset(String ct) {
      return (getContentId(ct) & 16) != 0;
   }

   public static int identifyContentType(ContentType contentType) {
      String primary = contentType.getPrimaryType();
      String sub = contentType.getSubType();
      if (primary.equalsIgnoreCase("multipart") && sub.equalsIgnoreCase("related")) {
         String type = contentType.getParameter("type");
         if (type != null) {
            if (isXMLType(type)) {
               return 3;
            }

            if (isFastInfosetType(type)) {
               return 18;
            }
         }

         return 0;
      } else if (isXMLType(primary, sub)) {
         return 1;
      } else {
         return isFastInfosetType(primary, sub) ? 16 : 0;
      }
   }

   protected static boolean isXMLType(@NotNull String primary, @NotNull String sub) {
      return primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml") || primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("xml") || primary.equalsIgnoreCase("application") && sub.toLowerCase().endsWith("+xml");
   }

   protected static boolean isXMLType(String type) {
      String lowerType = type.toLowerCase();
      return lowerType.startsWith("text/xml") || lowerType.startsWith("application/xml") || lowerType.startsWith("application/") && lowerType.indexOf("+xml") != -1;
   }

   protected static boolean isFastInfosetType(String primary, String sub) {
      return primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("fastinfoset");
   }

   protected static boolean isFastInfosetType(String type) {
      return type.toLowerCase().startsWith("application/fastinfoset");
   }

   public static DataSource getDataSource(Message msg, WSFeatureList f) {
      if (msg == null) {
         return null;
      } else if (msg instanceof XMLMessage.MessageDataSource) {
         return ((XMLMessage.MessageDataSource)msg).getDataSource();
      } else {
         AttachmentSet atts = msg.getAttachments();
         ByteArrayBuffer bos;
         if (atts != null && !atts.isEmpty()) {
            bos = new ByteArrayBuffer();

            try {
               Codec codec = new XMLHTTPBindingCodec(f);
               Packet packet = new Packet(msg);
               com.sun.xml.internal.ws.api.pipe.ContentType ct = codec.getStaticContentType(packet);
               codec.encode(packet, (OutputStream)bos);
               return createDataSource(ct.getContentType(), bos.newInputStream());
            } catch (IOException var7) {
               throw new WebServiceException(var7);
            }
         } else {
            bos = new ByteArrayBuffer();
            XMLStreamWriter writer = XMLStreamWriterFactory.create(bos);

            try {
               msg.writePayloadTo(writer);
               writer.flush();
            } catch (XMLStreamException var8) {
               throw new WebServiceException(var8);
            }

            return createDataSource("text/xml", bos.newInputStream());
         }
      }
   }

   public static DataSource createDataSource(String contentType, InputStream is) {
      return new XMLMessage.XmlDataSource(contentType, is);
   }

   private static class XmlDataSource implements DataSource {
      private final String contentType;
      private final InputStream is;
      private boolean consumed;

      XmlDataSource(String contentType, InputStream is) {
         this.contentType = contentType;
         this.is = is;
      }

      public boolean consumed() {
         return this.consumed;
      }

      public InputStream getInputStream() {
         this.consumed = !this.consumed;
         return this.is;
      }

      public OutputStream getOutputStream() {
         return null;
      }

      public String getContentType() {
         return this.contentType;
      }

      public String getName() {
         return "";
      }
   }

   public static class UnknownContent extends AbstractMessageImpl implements XMLMessage.MessageDataSource {
      private final DataSource ds;
      private final HeaderList headerList;

      public UnknownContent(String ct, InputStream in) {
         this(XMLMessage.createDataSource(ct, in));
      }

      public UnknownContent(DataSource ds) {
         super(SOAPVersion.SOAP_11);
         this.ds = ds;
         this.headerList = new HeaderList(SOAPVersion.SOAP_11);
      }

      private UnknownContent(XMLMessage.UnknownContent that) {
         super(that.soapVersion);
         this.ds = that.ds;
         this.headerList = HeaderList.copy(that.headerList);
      }

      public boolean hasUnconsumedDataSource() {
         return true;
      }

      public DataSource getDataSource() {
         assert this.ds != null;

         return this.ds;
      }

      protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
         throw new UnsupportedOperationException();
      }

      public boolean hasHeaders() {
         return false;
      }

      public boolean isFault() {
         return false;
      }

      public MessageHeaders getHeaders() {
         return this.headerList;
      }

      public String getPayloadLocalPart() {
         throw new UnsupportedOperationException();
      }

      public String getPayloadNamespaceURI() {
         throw new UnsupportedOperationException();
      }

      public boolean hasPayload() {
         return false;
      }

      public Source readPayloadAsSource() {
         return null;
      }

      public XMLStreamReader readPayload() throws XMLStreamException {
         throw new WebServiceException("There isn't XML payload. Shouldn't come here.");
      }

      public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      }

      public Message copy() {
         return new XMLMessage.UnknownContent(this);
      }
   }

   private static class FaultMessage extends EmptyMessageImpl {
      public FaultMessage(SOAPVersion version) {
         super(version);
      }

      public boolean isFault() {
         return true;
      }
   }

   public static final class XMLMultiPart extends AbstractMessageImpl implements XMLMessage.MessageDataSource {
      private final DataSource dataSource;
      private final StreamingAttachmentFeature feature;
      private Message delegate;
      private HeaderList headerList;
      private final WSFeatureList features;

      public XMLMultiPart(String contentType, InputStream is, WSFeatureList f) {
         super(SOAPVersion.SOAP_11);
         this.headerList = new HeaderList(SOAPVersion.SOAP_11);
         this.dataSource = XMLMessage.createDataSource(contentType, is);
         this.feature = (StreamingAttachmentFeature)f.get(StreamingAttachmentFeature.class);
         this.features = f;
      }

      private Message getMessage() {
         if (this.delegate == null) {
            MimeMultipartParser mpp;
            try {
               mpp = new MimeMultipartParser(this.dataSource.getInputStream(), this.dataSource.getContentType(), this.feature);
            } catch (IOException var3) {
               throw new WebServiceException(var3);
            }

            InputStream in = mpp.getRootPart().asInputStream();

            assert in != null;

            this.delegate = new PayloadSourceMessage(this.headerList, new StreamSource(in), new MimeAttachmentSet(mpp), SOAPVersion.SOAP_11);
         }

         return this.delegate;
      }

      public boolean hasUnconsumedDataSource() {
         return this.delegate == null;
      }

      public DataSource getDataSource() {
         return this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features);
      }

      public boolean hasHeaders() {
         return false;
      }

      @NotNull
      public MessageHeaders getHeaders() {
         return this.headerList;
      }

      public String getPayloadLocalPart() {
         return this.getMessage().getPayloadLocalPart();
      }

      public String getPayloadNamespaceURI() {
         return this.getMessage().getPayloadNamespaceURI();
      }

      public boolean hasPayload() {
         return true;
      }

      public boolean isFault() {
         return false;
      }

      public Source readEnvelopeAsSource() {
         return this.getMessage().readEnvelopeAsSource();
      }

      public Source readPayloadAsSource() {
         return this.getMessage().readPayloadAsSource();
      }

      public SOAPMessage readAsSOAPMessage() throws SOAPException {
         return this.getMessage().readAsSOAPMessage();
      }

      public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
         return this.getMessage().readAsSOAPMessage(packet, inbound);
      }

      public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
         return this.getMessage().readPayloadAsJAXB(unmarshaller);
      }

      public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
         return this.getMessage().readPayloadAsJAXB(bridge);
      }

      public XMLStreamReader readPayload() throws XMLStreamException {
         return this.getMessage().readPayload();
      }

      public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
         this.getMessage().writePayloadTo(sw);
      }

      public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
         this.getMessage().writeTo(sw);
      }

      public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
         this.getMessage().writeTo(contentHandler, errorHandler);
      }

      public Message copy() {
         return this.getMessage().copy();
      }

      protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
         throw new UnsupportedOperationException();
      }

      public boolean isOneWay(@NotNull WSDLPort port) {
         return false;
      }

      @NotNull
      public AttachmentSet getAttachments() {
         return this.getMessage().getAttachments();
      }
   }

   private static class XmlContent extends AbstractMessageImpl implements XMLMessage.MessageDataSource {
      private final XMLMessage.XmlDataSource dataSource;
      private boolean consumed;
      private Message delegate;
      private final HeaderList headerList;
      private WSFeatureList features;

      public XmlContent(String ct, InputStream in, WSFeatureList f) {
         super(SOAPVersion.SOAP_11);
         this.dataSource = new XMLMessage.XmlDataSource(ct, in);
         this.headerList = new HeaderList(SOAPVersion.SOAP_11);
         this.features = f;
      }

      private Message getMessage() {
         if (this.delegate == null) {
            InputStream in = this.dataSource.getInputStream();

            assert in != null;

            this.delegate = Messages.createUsingPayload((Source)(new StreamSource(in)), SOAPVersion.SOAP_11);
            this.consumed = true;
         }

         return this.delegate;
      }

      public boolean hasUnconsumedDataSource() {
         return !this.dataSource.consumed() && !this.consumed;
      }

      public DataSource getDataSource() {
         return (DataSource)(this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features));
      }

      public boolean hasHeaders() {
         return false;
      }

      @NotNull
      public MessageHeaders getHeaders() {
         return this.headerList;
      }

      public String getPayloadLocalPart() {
         return this.getMessage().getPayloadLocalPart();
      }

      public String getPayloadNamespaceURI() {
         return this.getMessage().getPayloadNamespaceURI();
      }

      public boolean hasPayload() {
         return true;
      }

      public boolean isFault() {
         return false;
      }

      public Source readEnvelopeAsSource() {
         return this.getMessage().readEnvelopeAsSource();
      }

      public Source readPayloadAsSource() {
         return this.getMessage().readPayloadAsSource();
      }

      public SOAPMessage readAsSOAPMessage() throws SOAPException {
         return this.getMessage().readAsSOAPMessage();
      }

      public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
         return this.getMessage().readAsSOAPMessage(packet, inbound);
      }

      public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
         return this.getMessage().readPayloadAsJAXB(unmarshaller);
      }

      /** @deprecated */
      public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
         return this.getMessage().readPayloadAsJAXB(bridge);
      }

      public XMLStreamReader readPayload() throws XMLStreamException {
         return this.getMessage().readPayload();
      }

      public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
         this.getMessage().writePayloadTo(sw);
      }

      public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
         this.getMessage().writeTo(sw);
      }

      public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
         this.getMessage().writeTo(contentHandler, errorHandler);
      }

      public Message copy() {
         return this.getMessage().copy();
      }

      protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
         throw new UnsupportedOperationException();
      }
   }

   public interface MessageDataSource {
      boolean hasUnconsumedDataSource();

      DataSource getDataSource();
   }
}
