package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.UUID;

abstract class MimeCodec implements Codec {
   public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
   protected Codec mimeRootCodec;
   protected final SOAPVersion version;
   protected final WSFeatureList features;

   protected MimeCodec(SOAPVersion version, WSFeatureList f) {
      this.version = version;
      this.features = f;
   }

   public String getMimeType() {
      return "multipart/related";
   }

   protected Codec getMimeRootCodec(Packet packet) {
      return this.mimeRootCodec;
   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, OutputStream out) throws IOException {
      Message msg = packet.getMessage();
      if (msg == null) {
         return null;
      } else {
         ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
         String boundary = ctImpl.getBoundary();
         boolean hasAttachments = boundary != null;
         Codec rootCodec = this.getMimeRootCodec(packet);
         com.sun.xml.internal.ws.api.pipe.ContentType primaryCt;
         if (hasAttachments) {
            writeln("--" + boundary, out);
            primaryCt = rootCodec.getStaticContentType(packet);
            String ctStr = primaryCt != null ? primaryCt.getContentType() : rootCodec.getMimeType();
            writeln("Content-Type: " + ctStr, out);
            writeln(out);
         }

         primaryCt = rootCodec.encode(packet, out);
         if (hasAttachments) {
            writeln(out);
            Iterator var12 = msg.getAttachments().iterator();

            while(var12.hasNext()) {
               Attachment att = (Attachment)var12.next();
               writeln("--" + boundary, out);
               String cid = att.getContentId();
               if (cid != null && cid.length() > 0 && cid.charAt(0) != '<') {
                  cid = '<' + cid + '>';
               }

               writeln("Content-Id:" + cid, out);
               writeln("Content-Type: " + att.getContentType(), out);
               this.writeCustomMimeHeaders(att, out);
               writeln("Content-Transfer-Encoding: binary", out);
               writeln(out);
               att.writeTo(out);
               writeln(out);
            }

            writeAsAscii("--" + boundary, out);
            writeAsAscii("--", out);
         }

         return (com.sun.xml.internal.ws.api.pipe.ContentType)(hasAttachments ? ctImpl : primaryCt);
      }
   }

   private void writeCustomMimeHeaders(Attachment att, OutputStream out) throws IOException {
      if (att instanceof AttachmentEx) {
         Iterator allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();

         while(allMimeHeaders.hasNext()) {
            AttachmentEx.MimeHeader mh = (AttachmentEx.MimeHeader)allMimeHeaders.next();
            String name = mh.getName();
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Id".equalsIgnoreCase(name)) {
               writeln(name + ": " + mh.getValue(), out);
            }
         }
      }

   }

   public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet packet) {
      com.sun.xml.internal.ws.api.pipe.ContentType ct = (com.sun.xml.internal.ws.api.pipe.ContentType)packet.getInternalContentType();
      if (ct != null) {
         return ct;
      } else {
         Message msg = packet.getMessage();
         boolean hasAttachments = !msg.getAttachments().isEmpty();
         Codec rootCodec = this.getMimeRootCodec(packet);
         if (hasAttachments) {
            String boundary = "uuid:" + UUID.randomUUID().toString();
            String boundaryParameter = "boundary=\"" + boundary + "\"";
            String messageContentType = "multipart/related; type=\"" + rootCodec.getMimeType() + "\"; " + boundaryParameter;
            ContentTypeImpl impl = new ContentTypeImpl(messageContentType, packet.soapAction, (String)null);
            impl.setBoundary(boundary);
            impl.setBoundaryParameter(boundaryParameter);
            packet.setContentType(impl);
            return impl;
         } else {
            ct = rootCodec.getStaticContentType(packet);
            packet.setContentType(ct);
            return ct;
         }
      }
   }

   protected MimeCodec(MimeCodec that) {
      this.version = that.version;
      this.features = that.features;
   }

   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
      MimeMultipartParser parser = new MimeMultipartParser(in, contentType, (StreamingAttachmentFeature)this.features.get(StreamingAttachmentFeature.class));
      this.decode(parser, packet);
   }

   public void decode(ReadableByteChannel in, String contentType, Packet packet) {
      throw new UnsupportedOperationException();
   }

   protected abstract void decode(MimeMultipartParser var1, Packet var2) throws IOException;

   public abstract MimeCodec copy();

   public static void writeln(String s, OutputStream out) throws IOException {
      writeAsAscii(s, out);
      writeln(out);
   }

   public static void writeAsAscii(String s, OutputStream out) throws IOException {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         out.write((byte)s.charAt(i));
      }

   }

   public static void writeln(OutputStream out) throws IOException {
      out.write(13);
      out.write(10);
   }
}
