package com.sun.xml.internal.ws.encoding;

import com.oracle.webservices.internal.impl.encoding.StreamDecoderImpl;
import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class StreamSOAPCodec implements com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec, RootOnlyCodec {
   private static final String SOAP_ENVELOPE = "Envelope";
   private static final String SOAP_HEADER = "Header";
   private static final String SOAP_BODY = "Body";
   private final SOAPVersion soapVersion;
   protected final SerializationFeature serializationFeature;
   private final StreamDecoder streamDecoder;
   private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";

   StreamSOAPCodec(SOAPVersion soapVersion) {
      this(soapVersion, (SerializationFeature)null);
   }

   StreamSOAPCodec(WSBinding binding) {
      this(binding.getSOAPVersion(), (SerializationFeature)binding.getFeature(SerializationFeature.class));
   }

   StreamSOAPCodec(WSFeatureList features) {
      this(WebServiceFeatureList.getSoapVersion(features), (SerializationFeature)features.get(SerializationFeature.class));
   }

   private StreamSOAPCodec(SOAPVersion soapVersion, @Nullable SerializationFeature sf) {
      this.soapVersion = soapVersion;
      this.serializationFeature = sf;
      this.streamDecoder = this.selectStreamDecoder();
   }

   private StreamDecoder selectStreamDecoder() {
      Iterator var1 = ServiceFinder.find(StreamDecoder.class).iterator();
      if (var1.hasNext()) {
         StreamDecoder sd = (StreamDecoder)var1.next();
         return sd;
      } else {
         return new StreamDecoderImpl();
      }
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet packet) {
      return this.getContentType(packet);
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, OutputStream out) {
      if (packet.getMessage() != null) {
         String encoding = this.getPacketEncoding(packet);
         packet.invocationProperties.remove("decodedMessageCharset");
         XMLStreamWriter writer = XMLStreamWriterFactory.create(out, encoding);

         try {
            packet.getMessage().writeTo(writer);
            writer.flush();
         } catch (XMLStreamException var6) {
            throw new WebServiceException(var6);
         }

         XMLStreamWriterFactory.recycle(writer);
      }

      return this.getContentType(packet);
   }

   protected abstract com.sun.xml.internal.ws.api.pipe.ContentType getContentType(Packet var1);

   protected abstract String getDefaultContentType();

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   protected abstract List<String> getExpectedContentTypes();

   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
      this.decode((InputStream)in, contentType, packet, new AttachmentSetImpl());
   }

   private static boolean isContentTypeSupported(String ct, List<String> expected) {
      Iterator var2 = expected.iterator();

      String contentType;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         contentType = (String)var2.next();
      } while(!ct.contains(contentType));

      return true;
   }

   @NotNull
   public final Message decode(@NotNull XMLStreamReader reader) {
      return this.decode(reader, new AttachmentSetImpl());
   }

   public final Message decode(XMLStreamReader reader, @NotNull AttachmentSet attachmentSet) {
      return decode(this.soapVersion, reader, attachmentSet);
   }

   public static final Message decode(SOAPVersion soapVersion, XMLStreamReader reader, @NotNull AttachmentSet attachmentSet) {
      if (reader.getEventType() != 1) {
         XMLStreamReaderUtil.nextElementContent(reader);
      }

      XMLStreamReaderUtil.verifyReaderState(reader, 1);
      if ("Envelope".equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
         throw new VersionMismatchException(soapVersion, new Object[]{soapVersion.nsUri, reader.getNamespaceURI()});
      } else {
         XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Envelope");
         return new StreamMessage(soapVersion, reader, attachmentSet);
      }
   }

   public void decode(ReadableByteChannel in, String contentType, Packet packet) {
      throw new UnsupportedOperationException();
   }

   public final StreamSOAPCodec copy() {
      return this;
   }

   public void decode(InputStream in, String contentType, Packet packet, AttachmentSet att) throws IOException {
      List<String> expectedContentTypes = this.getExpectedContentTypes();
      if (contentType != null && !isContentTypeSupported(contentType, expectedContentTypes)) {
         throw new UnsupportedMediaException(contentType, expectedContentTypes);
      } else {
         com.oracle.webservices.internal.api.message.ContentType pct = packet.getInternalContentType();
         ContentTypeImpl cti = pct != null && pct instanceof ContentTypeImpl ? (ContentTypeImpl)pct : new ContentTypeImpl(contentType);
         String charset = cti.getCharSet();
         if (charset != null && !Charset.isSupported(charset)) {
            throw new UnsupportedMediaException(charset);
         } else {
            if (charset != null) {
               packet.invocationProperties.put("decodedMessageCharset", charset);
            } else {
               packet.invocationProperties.remove("decodedMessageCharset");
            }

            packet.setMessage(this.streamDecoder.decode(in, charset, att, this.soapVersion));
         }
      }
   }

   public void decode(ReadableByteChannel in, String contentType, Packet response, AttachmentSet att) {
      throw new UnsupportedOperationException();
   }

   public static StreamSOAPCodec create(SOAPVersion version) {
      if (version == null) {
         throw new IllegalArgumentException();
      } else {
         switch(version) {
         case SOAP_11:
            return new StreamSOAP11Codec();
         case SOAP_12:
            return new StreamSOAP12Codec();
         default:
            throw new AssertionError();
         }
      }
   }

   public static StreamSOAPCodec create(WSFeatureList features) {
      SOAPVersion version = WebServiceFeatureList.getSoapVersion(features);
      if (version == null) {
         throw new IllegalArgumentException();
      } else {
         switch(version) {
         case SOAP_11:
            return new StreamSOAP11Codec(features);
         case SOAP_12:
            return new StreamSOAP12Codec(features);
         default:
            throw new AssertionError();
         }
      }
   }

   /** @deprecated */
   public static StreamSOAPCodec create(WSBinding binding) {
      SOAPVersion version = binding.getSOAPVersion();
      if (version == null) {
         throw new IllegalArgumentException();
      } else {
         switch(version) {
         case SOAP_11:
            return new StreamSOAP11Codec(binding);
         case SOAP_12:
            return new StreamSOAP12Codec(binding);
         default:
            throw new AssertionError();
         }
      }
   }

   private String getPacketEncoding(Packet packet) {
      if (this.serializationFeature != null && this.serializationFeature.getEncoding() != null) {
         return this.serializationFeature.getEncoding().equals("") ? "utf-8" : this.serializationFeature.getEncoding();
      } else if (packet != null && packet.endpoint != null) {
         String charset = (String)packet.invocationProperties.get("decodedMessageCharset");
         return charset == null ? "utf-8" : charset;
      } else {
         return "utf-8";
      }
   }

   protected ContentTypeImpl.Builder getContenTypeBuilder(Packet packet) {
      ContentTypeImpl.Builder b = new ContentTypeImpl.Builder();
      String encoding = this.getPacketEncoding(packet);
      if ("utf-8".equalsIgnoreCase(encoding)) {
         b.contentType = this.getDefaultContentType();
         b.charset = "utf-8";
         return b;
      } else {
         b.contentType = this.getMimeType() + " ;charset=" + encoding;
         b.charset = encoding;
         return b;
      }
   }
}
