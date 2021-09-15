package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.protocol.soap.MessageCreationException;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class SOAPBindingCodec extends MimeCodec implements com.sun.xml.internal.ws.api.pipe.SOAPBindingCodec {
   public static final String UTF8_ENCODING = "utf-8";
   public static final String DEFAULT_ENCODING = "utf-8";
   private boolean isFastInfosetDisabled;
   private boolean useFastInfosetForEncoding;
   private boolean ignoreContentNegotiationProperty;
   private final com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec xmlSoapCodec;
   private final Codec fiSoapCodec;
   private final MimeCodec xmlMtomCodec;
   private final MimeCodec xmlSwaCodec;
   private final MimeCodec fiSwaCodec;
   private final String xmlMimeType;
   private final String fiMimeType;
   private final String xmlAccept;
   private final String connegXmlAccept;

   public com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec getXMLCodec() {
      return this.xmlSoapCodec;
   }

   private ContentTypeImpl setAcceptHeader(Packet p, ContentTypeImpl c) {
      String _accept;
      if (!this.ignoreContentNegotiationProperty && p.contentNegotiation != ContentNegotiation.none) {
         _accept = this.connegXmlAccept;
      } else {
         _accept = this.xmlAccept;
      }

      c.setAcceptHeader(_accept);
      return c;
   }

   public SOAPBindingCodec(WSFeatureList features) {
      this(features, Codecs.createSOAPEnvelopeXmlCodec(features));
   }

   public SOAPBindingCodec(WSFeatureList features, com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec xmlSoapCodec) {
      super(WebServiceFeatureList.getSoapVersion(features), features);
      this.xmlSoapCodec = xmlSoapCodec;
      this.xmlMimeType = xmlSoapCodec.getMimeType();
      this.xmlMtomCodec = new MtomCodec(this.version, xmlSoapCodec, features);
      this.xmlSwaCodec = new SwACodec(this.version, features, xmlSoapCodec);
      String clientAcceptedContentTypes = xmlSoapCodec.getMimeType() + ", " + this.xmlMtomCodec.getMimeType();
      WebServiceFeature fi = features.get(FastInfosetFeature.class);
      this.isFastInfosetDisabled = fi != null && !fi.isEnabled();
      if (!this.isFastInfosetDisabled) {
         this.fiSoapCodec = getFICodec(xmlSoapCodec, this.version);
         if (this.fiSoapCodec != null) {
            this.fiMimeType = this.fiSoapCodec.getMimeType();
            this.fiSwaCodec = new SwACodec(this.version, features, this.fiSoapCodec);
            this.connegXmlAccept = this.fiMimeType + ", " + clientAcceptedContentTypes;
            WebServiceFeature select = features.get(SelectOptimalEncodingFeature.class);
            if (select != null) {
               this.ignoreContentNegotiationProperty = true;
               if (select.isEnabled()) {
                  if (fi != null) {
                     this.useFastInfosetForEncoding = true;
                  }

                  clientAcceptedContentTypes = this.connegXmlAccept;
               } else {
                  this.isFastInfosetDisabled = true;
               }
            }
         } else {
            this.isFastInfosetDisabled = true;
            this.fiSwaCodec = null;
            this.fiMimeType = "";
            this.connegXmlAccept = clientAcceptedContentTypes;
            this.ignoreContentNegotiationProperty = true;
         }
      } else {
         this.fiSoapCodec = this.fiSwaCodec = null;
         this.fiMimeType = "";
         this.connegXmlAccept = clientAcceptedContentTypes;
         this.ignoreContentNegotiationProperty = true;
      }

      this.xmlAccept = clientAcceptedContentTypes;
      if (WebServiceFeatureList.getSoapVersion(features) == null) {
         throw new WebServiceException("Expecting a SOAP binding but found ");
      }
   }

   public String getMimeType() {
      return null;
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet packet) {
      com.sun.xml.internal.ws.api.pipe.ContentType toAdapt = this.getEncoder(packet).getStaticContentType(packet);
      return this.setAcceptHeader(packet, (ContentTypeImpl)toAdapt);
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, OutputStream out) throws IOException {
      this.preEncode(packet);
      com.sun.xml.internal.ws.api.pipe.ContentType ct = this.getEncoder(packet).encode(packet, out);
      com.sun.xml.internal.ws.api.pipe.ContentType ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
      this.postEncode();
      return ct;
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, WritableByteChannel buffer) {
      this.preEncode(packet);
      com.sun.xml.internal.ws.api.pipe.ContentType ct = this.getEncoder(packet).encode(packet, buffer);
      com.sun.xml.internal.ws.api.pipe.ContentType ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
      this.postEncode();
      return ct;
   }

   private void preEncode(Packet p) {
   }

   private void postEncode() {
   }

   private void preDecode(Packet p) {
      if (p.contentNegotiation == null) {
         this.useFastInfosetForEncoding = false;
      }

   }

   private void postDecode(Packet p) {
      p.setFastInfosetDisabled(this.isFastInfosetDisabled);
      if (this.features.isEnabled(MTOMFeature.class)) {
         p.checkMtomAcceptable();
      }

      MTOMFeature mtomFeature = (MTOMFeature)this.features.get(MTOMFeature.class);
      if (mtomFeature != null) {
         p.setMtomFeature(mtomFeature);
      }

      if (!this.useFastInfosetForEncoding) {
         this.useFastInfosetForEncoding = p.getFastInfosetAcceptable(this.fiMimeType);
      }

   }

   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
      if (contentType == null) {
         contentType = this.xmlMimeType;
      }

      packet.setContentType(new ContentTypeImpl(contentType));
      this.preDecode(packet);

      try {
         if (this.isMultipartRelated(contentType)) {
            super.decode(in, contentType, packet);
         } else if (this.isFastInfoset(contentType)) {
            if (!this.ignoreContentNegotiationProperty && packet.contentNegotiation == ContentNegotiation.none) {
               throw this.noFastInfosetForDecoding();
            }

            this.useFastInfosetForEncoding = true;
            this.fiSoapCodec.decode(in, contentType, packet);
         } else {
            this.xmlSoapCodec.decode(in, contentType, packet);
         }
      } catch (RuntimeException var5) {
         if (!(var5 instanceof ExceptionHasMessage) && !(var5 instanceof UnsupportedMediaException)) {
            throw new MessageCreationException(this.version, new Object[]{var5});
         }

         throw var5;
      }

      this.postDecode(packet);
   }

   public void decode(ReadableByteChannel in, String contentType, Packet packet) {
      if (contentType == null) {
         throw new UnsupportedMediaException();
      } else {
         this.preDecode(packet);

         try {
            if (this.isMultipartRelated(contentType)) {
               super.decode(in, contentType, packet);
            } else if (this.isFastInfoset(contentType)) {
               if (packet.contentNegotiation == ContentNegotiation.none) {
                  throw this.noFastInfosetForDecoding();
               }

               this.useFastInfosetForEncoding = true;
               this.fiSoapCodec.decode(in, contentType, packet);
            } else {
               this.xmlSoapCodec.decode(in, contentType, packet);
            }
         } catch (RuntimeException var5) {
            if (!(var5 instanceof ExceptionHasMessage) && !(var5 instanceof UnsupportedMediaException)) {
               throw new MessageCreationException(this.version, new Object[]{var5});
            }

            throw var5;
         }

         this.postDecode(packet);
      }
   }

   public SOAPBindingCodec copy() {
      return new SOAPBindingCodec(this.features, (com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec)this.xmlSoapCodec.copy());
   }

   protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
      String rootContentType = mpp.getRootPart().getContentType();
      boolean isMTOM = this.isApplicationXopXml(rootContentType);
      packet.setMtomRequest(isMTOM);
      if (isMTOM) {
         this.xmlMtomCodec.decode(mpp, packet);
      } else if (this.isFastInfoset(rootContentType)) {
         if (packet.contentNegotiation == ContentNegotiation.none) {
            throw this.noFastInfosetForDecoding();
         }

         this.useFastInfosetForEncoding = true;
         this.fiSwaCodec.decode(mpp, packet);
      } else {
         if (!this.isXml(rootContentType)) {
            throw new IOException("");
         }

         this.xmlSwaCodec.decode(mpp, packet);
      }

   }

   private boolean isMultipartRelated(String contentType) {
      return this.compareStrings(contentType, "multipart/related");
   }

   private boolean isApplicationXopXml(String contentType) {
      return this.compareStrings(contentType, "application/xop+xml");
   }

   private boolean isXml(String contentType) {
      return this.compareStrings(contentType, this.xmlMimeType);
   }

   private boolean isFastInfoset(String contentType) {
      return this.isFastInfosetDisabled ? false : this.compareStrings(contentType, this.fiMimeType);
   }

   private boolean compareStrings(String a, String b) {
      return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
   }

   private Codec getEncoder(Packet p) {
      if (!this.ignoreContentNegotiationProperty) {
         if (p.contentNegotiation == ContentNegotiation.none) {
            this.useFastInfosetForEncoding = false;
         } else if (p.contentNegotiation == ContentNegotiation.optimistic) {
            this.useFastInfosetForEncoding = true;
         }
      }

      Message m;
      if (this.useFastInfosetForEncoding) {
         m = p.getMessage();
         return (Codec)(m != null && !m.getAttachments().isEmpty() && !this.features.isEnabled(MTOMFeature.class) ? this.fiSwaCodec : this.fiSoapCodec);
      } else {
         if (p.getBinding() == null && this.features != null) {
            p.setMtomFeature((MTOMFeature)this.features.get(MTOMFeature.class));
         }

         if (p.shouldUseMtom()) {
            return this.xmlMtomCodec;
         } else {
            m = p.getMessage();
            return (Codec)(m != null && !m.getAttachments().isEmpty() ? this.xmlSwaCodec : this.xmlSoapCodec);
         }
      }
   }

   private RuntimeException noFastInfosetForDecoding() {
      return new RuntimeException(StreamingMessages.FASTINFOSET_DECODING_NOT_ACCEPTED());
   }

   private static Codec getFICodec(com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec soapCodec, SOAPVersion version) {
      try {
         Class c = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec");
         Method m = c.getMethod("create", com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec.class, SOAPVersion.class);
         return (Codec)m.invoke((Object)null, soapCodec, version);
      } catch (Exception var4) {
         return null;
      }
   }
}
