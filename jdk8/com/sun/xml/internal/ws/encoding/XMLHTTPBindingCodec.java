package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.encoding.xml.XMLCodec;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.WritableByteChannel;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.xml.ws.WebServiceException;

public final class XMLHTTPBindingCodec extends MimeCodec {
   private static final String BASE_ACCEPT_VALUE = "*";
   private static final String APPLICATION_FAST_INFOSET_MIME_TYPE = "application/fastinfoset";
   private boolean useFastInfosetForEncoding;
   private final Codec xmlCodec;
   private final Codec fiCodec;
   private static final String xmlAccept = null;
   private static final String fiXmlAccept = "application/fastinfoset, *";

   private ContentTypeImpl setAcceptHeader(Packet p, com.sun.xml.internal.ws.api.pipe.ContentType c) {
      ContentTypeImpl ctImpl = (ContentTypeImpl)c;
      if (p.contentNegotiation != ContentNegotiation.optimistic && p.contentNegotiation != ContentNegotiation.pessimistic) {
         ctImpl.setAcceptHeader(xmlAccept);
      } else {
         ctImpl.setAcceptHeader("application/fastinfoset, *");
      }

      p.setContentType(ctImpl);
      return ctImpl;
   }

   public XMLHTTPBindingCodec(WSFeatureList f) {
      super(SOAPVersion.SOAP_11, f);
      this.xmlCodec = new XMLCodec(f);
      this.fiCodec = getFICodec();
   }

   public String getMimeType() {
      return null;
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet packet) {
      com.sun.xml.internal.ws.api.pipe.ContentType ct;
      if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
         XMLMessage.MessageDataSource mds = (XMLMessage.MessageDataSource)packet.getInternalMessage();
         if (mds.hasUnconsumedDataSource()) {
            ct = this.getStaticContentType(mds);
            return ct != null ? this.setAcceptHeader(packet, ct) : null;
         }
      }

      ct = super.getStaticContentType(packet);
      return ct != null ? this.setAcceptHeader(packet, ct) : null;
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, OutputStream out) throws IOException {
      if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
         XMLMessage.MessageDataSource mds = (XMLMessage.MessageDataSource)packet.getInternalMessage();
         if (mds.hasUnconsumedDataSource()) {
            return this.setAcceptHeader(packet, this.encode(mds, out));
         }
      }

      return this.setAcceptHeader(packet, super.encode(packet, out));
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
      if (packet.contentNegotiation == null) {
         this.useFastInfosetForEncoding = false;
      }

      if (contentType == null) {
         this.xmlCodec.decode(in, contentType, packet);
      } else if (this.isMultipartRelated(contentType)) {
         packet.setMessage(new XMLMessage.XMLMultiPart(contentType, in, this.features));
      } else if (this.isFastInfoset(contentType)) {
         if (this.fiCodec == null) {
            throw new RuntimeException(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION());
         }

         this.useFastInfosetForEncoding = true;
         this.fiCodec.decode(in, contentType, packet);
      } else if (this.isXml(contentType)) {
         this.xmlCodec.decode(in, contentType, packet);
      } else {
         packet.setMessage(new XMLMessage.UnknownContent(contentType, in));
      }

      if (!this.useFastInfosetForEncoding) {
         this.useFastInfosetForEncoding = this.isFastInfosetAcceptable(packet.acceptableMimeTypes);
      }

   }

   protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
   }

   public MimeCodec copy() {
      return new XMLHTTPBindingCodec(this.features);
   }

   private boolean isMultipartRelated(String contentType) {
      return this.compareStrings(contentType, "multipart/related");
   }

   private boolean isXml(String contentType) {
      return this.compareStrings(contentType, "application/xml") || this.compareStrings(contentType, "text/xml") || this.compareStrings(contentType, "application/") && contentType.toLowerCase().indexOf("+xml") != -1;
   }

   private boolean isFastInfoset(String contentType) {
      return this.compareStrings(contentType, "application/fastinfoset");
   }

   private boolean compareStrings(String a, String b) {
      return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
   }

   private boolean isFastInfosetAcceptable(String accept) {
      if (accept == null) {
         return false;
      } else {
         StringTokenizer st = new StringTokenizer(accept, ",");

         String token;
         do {
            if (!st.hasMoreTokens()) {
               return false;
            }

            token = st.nextToken().trim();
         } while(!token.equalsIgnoreCase("application/fastinfoset"));

         return true;
      }
   }

   private com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(XMLMessage.MessageDataSource mds) {
      String contentType = mds.getDataSource().getContentType();
      boolean isFastInfoset = XMLMessage.isFastInfoset(contentType);
      return !requiresTransformationOfDataSource(isFastInfoset, this.useFastInfosetForEncoding) ? new ContentTypeImpl(contentType) : null;
   }

   private com.sun.xml.internal.ws.api.pipe.ContentType encode(XMLMessage.MessageDataSource mds, OutputStream out) {
      try {
         boolean isFastInfoset = XMLMessage.isFastInfoset(mds.getDataSource().getContentType());
         DataSource ds = transformDataSource(mds.getDataSource(), isFastInfoset, this.useFastInfosetForEncoding, this.features);
         InputStream is = ds.getInputStream();
         byte[] buf = new byte[1024];

         int count;
         while((count = is.read(buf)) != -1) {
            out.write(buf, 0, count);
         }

         return new ContentTypeImpl(ds.getContentType());
      } catch (IOException var8) {
         throw new WebServiceException(var8);
      }
   }

   protected Codec getMimeRootCodec(Packet p) {
      if (p.contentNegotiation == ContentNegotiation.none) {
         this.useFastInfosetForEncoding = false;
      } else if (p.contentNegotiation == ContentNegotiation.optimistic) {
         this.useFastInfosetForEncoding = true;
      }

      return this.useFastInfosetForEncoding && this.fiCodec != null ? this.fiCodec : this.xmlCodec;
   }

   public static boolean requiresTransformationOfDataSource(boolean isFastInfoset, boolean useFastInfoset) {
      return isFastInfoset && !useFastInfoset || !isFastInfoset && useFastInfoset;
   }

   public static DataSource transformDataSource(DataSource in, boolean isFastInfoset, boolean useFastInfoset, WSFeatureList f) {
      try {
         XMLHTTPBindingCodec codec;
         Packet p;
         ByteArrayBuffer bos;
         com.sun.xml.internal.ws.api.pipe.ContentType ct;
         if (isFastInfoset && !useFastInfoset) {
            codec = new XMLHTTPBindingCodec(f);
            p = new Packet();
            codec.decode(in.getInputStream(), in.getContentType(), p);
            p.getMessage().getAttachments();
            codec.getStaticContentType(p);
            bos = new ByteArrayBuffer();
            ct = codec.encode(p, (OutputStream)bos);
            return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
         } else if (!isFastInfoset && useFastInfoset) {
            codec = new XMLHTTPBindingCodec(f);
            p = new Packet();
            codec.decode(in.getInputStream(), in.getContentType(), p);
            p.contentNegotiation = ContentNegotiation.optimistic;
            p.getMessage().getAttachments();
            codec.getStaticContentType(p);
            bos = new ByteArrayBuffer();
            ct = codec.encode(p, (OutputStream)bos);
            return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
         } else {
            return in;
         }
      } catch (Exception var8) {
         throw new WebServiceException(var8);
      }
   }

   private static Codec getFICodec() {
      try {
         Class c = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetCodec");
         Method m = c.getMethod("create");
         return (Codec)m.invoke((Object)null);
      } catch (Exception var2) {
         return null;
      }
   }
}
