package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import com.sun.xml.internal.ws.util.xml.NamespaceContextExAdaper;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

public class MtomCodec extends MimeCodec {
   public static final String XOP_XML_MIME_TYPE = "application/xop+xml";
   public static final String XOP_LOCALNAME = "Include";
   public static final String XOP_NAMESPACEURI = "http://www.w3.org/2004/08/xop/include";
   private final com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec codec;
   private final MTOMFeature mtomFeature;
   private final SerializationFeature sf;
   private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";

   MtomCodec(SOAPVersion version, com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec codec, WSFeatureList features) {
      super(version, features);
      this.codec = codec;
      this.sf = (SerializationFeature)features.get(SerializationFeature.class);
      MTOMFeature mtom = (MTOMFeature)features.get(MTOMFeature.class);
      if (mtom == null) {
         this.mtomFeature = new MTOMFeature();
      } else {
         this.mtomFeature = mtom;
      }

   }

   public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet packet) {
      return getStaticContentTypeStatic(packet, this.version);
   }

   public static com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentTypeStatic(Packet packet, SOAPVersion version) {
      com.sun.xml.internal.ws.api.pipe.ContentType ct = (com.sun.xml.internal.ws.api.pipe.ContentType)packet.getInternalContentType();
      if (ct != null) {
         return ct;
      } else {
         String uuid = UUID.randomUUID().toString();
         String boundary = "uuid:" + uuid;
         String rootId = "<rootpart*" + uuid + "@example.jaxws.sun.com>";
         String soapActionParameter = SOAPVersion.SOAP_11.equals(version) ? null : createActionParameter(packet);
         String boundaryParameter = "boundary=\"" + boundary + "\"";
         String messageContentType = "multipart/related;start=\"" + rootId + "\";type=\"" + "application/xop+xml" + "\";" + boundaryParameter + ";start-info=\"" + version.contentType + (soapActionParameter == null ? "" : soapActionParameter) + "\"";
         ContentTypeImpl ctImpl = SOAPVersion.SOAP_11.equals(version) ? new ContentTypeImpl(messageContentType, packet.soapAction == null ? "" : packet.soapAction, (String)null) : new ContentTypeImpl(messageContentType, (String)null, (String)null);
         ctImpl.setBoundary(boundary);
         ctImpl.setRootId(rootId);
         packet.setContentType(ctImpl);
         return ctImpl;
      }
   }

   private static String createActionParameter(Packet packet) {
      return packet.soapAction != null ? ";action=\\\"" + packet.soapAction + "\\\"" : "";
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, OutputStream out) throws IOException {
      ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
      String boundary = ctImpl.getBoundary();
      String rootId = ctImpl.getRootId();
      if (packet.getMessage() != null) {
         try {
            String encoding = this.getPacketEncoding(packet);
            packet.invocationProperties.remove("decodedMessageCharset");
            String actionParameter = getActionParameter(packet, this.version);
            String soapXopContentType = getSOAPXopContentType(encoding, this.version, actionParameter);
            writeln("--" + boundary, out);
            writeMimeHeaders(soapXopContentType, rootId, out);
            List<MtomCodec.ByteArrayBuffer> mtomAttachments = new ArrayList();
            MtomCodec.MtomStreamWriterImpl writer = new MtomCodec.MtomStreamWriterImpl(XMLStreamWriterFactory.create(out, encoding), mtomAttachments, boundary, this.mtomFeature);
            packet.getMessage().writeTo(writer);
            XMLStreamWriterFactory.recycle(writer);
            writeln(out);
            Iterator var11 = mtomAttachments.iterator();

            while(var11.hasNext()) {
               MtomCodec.ByteArrayBuffer bos = (MtomCodec.ByteArrayBuffer)var11.next();
               bos.write(out);
            }

            this.writeNonMtomAttachments(packet.getMessage().getAttachments(), out, boundary);
            writeAsAscii("--" + boundary, out);
            writeAsAscii("--", out);
         } catch (XMLStreamException var13) {
            throw new WebServiceException(var13);
         }
      }

      return ctImpl;
   }

   public static String getSOAPXopContentType(String encoding, SOAPVersion version, String actionParameter) {
      return "application/xop+xml;charset=" + encoding + ";type=\"" + version.contentType + actionParameter + "\"";
   }

   public static String getActionParameter(Packet packet, SOAPVersion version) {
      return version == SOAPVersion.SOAP_11 ? "" : createActionParameter(packet);
   }

   public static void writeMimeHeaders(String contentType, String contentId, OutputStream out) throws IOException {
      String cid = contentId;
      if (contentId != null && contentId.length() > 0 && contentId.charAt(0) != '<') {
         cid = '<' + contentId + '>';
      }

      writeln("Content-Id: " + cid, out);
      writeln("Content-Type: " + contentType, out);
      writeln("Content-Transfer-Encoding: binary", out);
      writeln(out);
   }

   private void writeNonMtomAttachments(AttachmentSet attachments, OutputStream out, String boundary) throws IOException {
      Iterator var4 = attachments.iterator();

      while(true) {
         Attachment att;
         StreamingDataHandler sdh;
         do {
            if (!var4.hasNext()) {
               return;
            }

            att = (Attachment)var4.next();
            DataHandler dh = att.asDataHandler();
            if (!(dh instanceof StreamingDataHandler)) {
               break;
            }

            sdh = (StreamingDataHandler)dh;
         } while(sdh.getHrefCid() != null);

         writeln("--" + boundary, out);
         writeMimeHeaders(att.getContentType(), att.getContentId(), out);
         att.writeTo(out);
         writeln(out);
      }
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   public MtomCodec copy() {
      return new MtomCodec(this.version, (com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec)this.codec.copy(), this.features);
   }

   private static String encodeCid() {
      String cid = "example.jaxws.sun.com";
      String name = UUID.randomUUID() + "@";
      return name + cid;
   }

   protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
      String charset = null;
      String ct = mpp.getRootPart().getContentType();
      if (ct != null) {
         charset = (new ContentTypeImpl(ct)).getCharSet();
      }

      if (charset != null && !Charset.isSupported(charset)) {
         throw new UnsupportedMediaException(charset);
      } else {
         if (charset != null) {
            packet.invocationProperties.put("decodedMessageCharset", charset);
         } else {
            packet.invocationProperties.remove("decodedMessageCharset");
         }

         XMLStreamReader mtomReader = new MtomCodec.MtomXMLStreamReaderEx(mpp, XMLStreamReaderFactory.create((String)null, mpp.getRootPart().asInputStream(), charset, true));
         packet.setMessage(this.codec.decode(mtomReader, new MimeAttachmentSet(mpp)));
         packet.setMtomFeature(this.mtomFeature);
         packet.setContentType(mpp.getContentType());
      }
   }

   private String getPacketEncoding(Packet packet) {
      if (this.sf != null && this.sf.getEncoding() != null) {
         return this.sf.getEncoding().equals("") ? "utf-8" : this.sf.getEncoding();
      } else {
         return determinePacketEncoding(packet);
      }
   }

   public static String determinePacketEncoding(Packet packet) {
      if (packet != null && packet.endpoint != null) {
         String charset = (String)packet.invocationProperties.get("decodedMessageCharset");
         return charset == null ? "utf-8" : charset;
      } else {
         return "utf-8";
      }
   }

   public static class MtomXMLStreamReaderEx extends XMLStreamReaderFilter implements XMLStreamReaderEx {
      private final MimeMultipartParser mimeMP;
      private boolean xopReferencePresent = false;
      private Base64Data base64AttData;
      private char[] base64EncodedText;
      private String xopHref;

      public MtomXMLStreamReaderEx(MimeMultipartParser mimeMP, XMLStreamReader reader) {
         super(reader);
         this.mimeMP = mimeMP;
      }

      public CharSequence getPCDATA() throws XMLStreamException {
         return (CharSequence)(this.xopReferencePresent ? this.base64AttData : this.reader.getText());
      }

      public NamespaceContextEx getNamespaceContext() {
         return new NamespaceContextExAdaper(this.reader.getNamespaceContext());
      }

      public String getElementTextTrim() throws XMLStreamException {
         throw new UnsupportedOperationException();
      }

      public int getTextLength() {
         return this.xopReferencePresent ? this.base64AttData.length() : this.reader.getTextLength();
      }

      public int getTextStart() {
         return this.xopReferencePresent ? 0 : this.reader.getTextStart();
      }

      public int getEventType() {
         return this.xopReferencePresent ? 4 : super.getEventType();
      }

      public int next() throws XMLStreamException {
         int event = this.reader.next();
         if (event == 1 && this.reader.getLocalName().equals("Include") && this.reader.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
            String href = this.reader.getAttributeValue((String)null, "href");

            try {
               this.xopHref = href;
               Attachment att = this.getAttachment(href);
               if (att != null) {
                  DataHandler dh = att.asDataHandler();
                  if (dh instanceof StreamingDataHandler) {
                     ((StreamingDataHandler)dh).setHrefCid(att.getContentId());
                  }

                  this.base64AttData = new Base64Data();
                  this.base64AttData.set(dh);
               }

               this.xopReferencePresent = true;
            } catch (IOException var5) {
               throw new WebServiceException(var5);
            }

            XMLStreamReaderUtil.nextElementContent(this.reader);
            return 4;
         } else {
            if (this.xopReferencePresent) {
               this.xopReferencePresent = false;
               this.base64EncodedText = null;
               this.xopHref = null;
            }

            return event;
         }
      }

      private String decodeCid(String cid) {
         try {
            cid = URLDecoder.decode(cid, "utf-8");
         } catch (UnsupportedEncodingException var3) {
         }

         return cid;
      }

      private Attachment getAttachment(String cid) throws IOException {
         if (cid.startsWith("cid:")) {
            cid = cid.substring(4, cid.length());
         }

         if (cid.indexOf(37) != -1) {
            cid = this.decodeCid(cid);
            return this.mimeMP.getAttachmentPart(cid);
         } else {
            return this.mimeMP.getAttachmentPart(cid);
         }
      }

      public char[] getTextCharacters() {
         if (this.xopReferencePresent) {
            char[] chars = new char[this.base64AttData.length()];
            this.base64AttData.writeTo(chars, 0);
            return chars;
         } else {
            return this.reader.getTextCharacters();
         }
      }

      public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
         if (this.xopReferencePresent) {
            if (target == null) {
               throw new NullPointerException("target char array can't be null");
            } else if (targetStart >= 0 && length >= 0 && sourceStart >= 0 && targetStart < target.length && targetStart + length <= target.length) {
               int textLength = this.base64AttData.length();
               if (sourceStart > textLength) {
                  throw new IndexOutOfBoundsException();
               } else {
                  if (this.base64EncodedText == null) {
                     this.base64EncodedText = new char[this.base64AttData.length()];
                     this.base64AttData.writeTo(this.base64EncodedText, 0);
                  }

                  int copiedLength = Math.min(textLength - sourceStart, length);
                  System.arraycopy(this.base64EncodedText, sourceStart, target, targetStart, copiedLength);
                  return copiedLength;
               }
            } else {
               throw new IndexOutOfBoundsException();
            }
         } else {
            return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
         }
      }

      public String getText() {
         return this.xopReferencePresent ? this.base64AttData.toString() : this.reader.getText();
      }

      protected boolean isXopReference() throws XMLStreamException {
         return this.xopReferencePresent;
      }

      protected String getXopHref() {
         return this.xopHref;
      }

      public MimeMultipartParser getMimeMultipartParser() {
         return this.mimeMP;
      }
   }

   public static class MtomStreamWriterImpl extends XMLStreamWriterFilter implements XMLStreamWriterEx, MtomStreamWriter, HasEncoding {
      private final List<MtomCodec.ByteArrayBuffer> mtomAttachments;
      private final String boundary;
      private final MTOMFeature myMtomFeature;

      public MtomStreamWriterImpl(XMLStreamWriter w, List<MtomCodec.ByteArrayBuffer> mtomAttachments, String b, MTOMFeature myMtomFeature) {
         super(w);
         this.mtomAttachments = mtomAttachments;
         this.boundary = b;
         this.myMtomFeature = myMtomFeature;
      }

      public void writeBinary(byte[] data, int start, int len, String contentType) throws XMLStreamException {
         if (this.myMtomFeature.getThreshold() > len) {
            this.writeCharacters(DatatypeConverterImpl._printBase64Binary(data, start, len));
         } else {
            MtomCodec.ByteArrayBuffer bab = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(data, start, len, contentType)), this.boundary);
            this.writeBinary(bab);
         }
      }

      public void writeBinary(DataHandler dataHandler) throws XMLStreamException {
         this.writeBinary(new MtomCodec.ByteArrayBuffer(dataHandler, this.boundary));
      }

      public OutputStream writeBinary(String contentType) throws XMLStreamException {
         throw new UnsupportedOperationException();
      }

      public void writePCDATA(CharSequence data) throws XMLStreamException {
         if (data != null) {
            if (data instanceof Base64Data) {
               Base64Data binaryData = (Base64Data)data;
               this.writeBinary(binaryData.getDataHandler());
            } else {
               this.writeCharacters(data.toString());
            }
         }
      }

      private void writeBinary(MtomCodec.ByteArrayBuffer bab) {
         try {
            this.mtomAttachments.add(bab);
            String prefix = this.writer.getPrefix("http://www.w3.org/2004/08/xop/include");
            if (prefix == null || !prefix.equals("xop")) {
               this.writer.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
               this.writer.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
            }

            this.writer.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
            this.writer.writeAttribute("href", "cid:" + bab.contentId);
            this.writer.writeEndElement();
            this.writer.flush();
         } catch (XMLStreamException var3) {
            throw new WebServiceException(var3);
         }
      }

      public Object getProperty(String name) throws IllegalArgumentException {
         if (name.equals("sjsxp-outputstream") && this.writer instanceof Map) {
            Object obj = ((Map)this.writer).get("sjsxp-outputstream");
            if (obj != null) {
               return obj;
            }
         }

         return super.getProperty(name);
      }

      public AttachmentMarshaller getAttachmentMarshaller() {
         return new AttachmentMarshaller() {
            public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
               MtomCodec.ByteArrayBuffer bab = new MtomCodec.ByteArrayBuffer(data, MtomStreamWriterImpl.this.boundary);
               MtomStreamWriterImpl.this.mtomAttachments.add(bab);
               return "cid:" + bab.contentId;
            }

            public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName) {
               if (MtomStreamWriterImpl.this.myMtomFeature.getThreshold() > length) {
                  return null;
               } else {
                  MtomCodec.ByteArrayBuffer bab = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(data, offset, length, mimeType)), MtomStreamWriterImpl.this.boundary);
                  MtomStreamWriterImpl.this.mtomAttachments.add(bab);
                  return "cid:" + bab.contentId;
               }
            }

            public String addSwaRefAttachment(DataHandler data) {
               MtomCodec.ByteArrayBuffer bab = new MtomCodec.ByteArrayBuffer(data, MtomStreamWriterImpl.this.boundary);
               MtomStreamWriterImpl.this.mtomAttachments.add(bab);
               return "cid:" + bab.contentId;
            }

            public boolean isXOPPackage() {
               return true;
            }
         };
      }

      public List<MtomCodec.ByteArrayBuffer> getMtomAttachments() {
         return this.mtomAttachments;
      }

      public String getEncoding() {
         return XMLStreamWriterUtil.getEncoding(this.writer);
      }

      public NamespaceContextEx getNamespaceContext() {
         NamespaceContext nsContext = this.writer.getNamespaceContext();
         return new MtomCodec.MtomStreamWriterImpl.MtomNamespaceContextEx(nsContext);
      }

      private static class MtomNamespaceContextEx implements NamespaceContextEx {
         private final NamespaceContext nsContext;

         public MtomNamespaceContextEx(NamespaceContext nsContext) {
            this.nsContext = nsContext;
         }

         public Iterator<NamespaceContextEx.Binding> iterator() {
            throw new UnsupportedOperationException();
         }

         public String getNamespaceURI(String prefix) {
            return this.nsContext.getNamespaceURI(prefix);
         }

         public String getPrefix(String namespaceURI) {
            return this.nsContext.getPrefix(namespaceURI);
         }

         public Iterator getPrefixes(String namespaceURI) {
            return this.nsContext.getPrefixes(namespaceURI);
         }
      }
   }

   public static class ByteArrayBuffer {
      final String contentId;
      private final DataHandler dh;
      private final String boundary;

      ByteArrayBuffer(@NotNull DataHandler dh, String b) {
         this.dh = dh;
         String cid = null;
         if (dh instanceof StreamingDataHandler) {
            StreamingDataHandler sdh = (StreamingDataHandler)dh;
            if (sdh.getHrefCid() != null) {
               cid = sdh.getHrefCid();
            }
         }

         this.contentId = cid != null ? cid : MtomCodec.encodeCid();
         this.boundary = b;
      }

      public void write(OutputStream os) throws IOException {
         MimeCodec.writeln("--" + this.boundary, os);
         MtomCodec.writeMimeHeaders(this.dh.getContentType(), this.contentId, os);
         this.dh.writeTo(os);
         MimeCodec.writeln(os);
      }
   }
}
