package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Map;

public final class SwACodec extends MimeCodec {
   public SwACodec(SOAPVersion version, WSFeatureList f, Codec rootCodec) {
      super(version, f);
      this.mimeRootCodec = rootCodec;
   }

   private SwACodec(SwACodec that) {
      super(that);
      this.mimeRootCodec = that.mimeRootCodec.copy();
   }

   protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
      Attachment root = mpp.getRootPart();
      Codec rootCodec = this.getMimeRootCodec(packet);
      if (rootCodec instanceof RootOnlyCodec) {
         ((RootOnlyCodec)rootCodec).decode((InputStream)root.asInputStream(), root.getContentType(), packet, new MimeAttachmentSet(mpp));
      } else {
         rootCodec.decode(root.asInputStream(), root.getContentType(), packet);
         Map<String, Attachment> atts = mpp.getAttachmentParts();
         Iterator var6 = atts.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<String, Attachment> att = (Map.Entry)var6.next();
            packet.getMessage().getAttachments().add((Attachment)att.getValue());
         }
      }

   }

   public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet packet, WritableByteChannel buffer) {
      throw new UnsupportedOperationException();
   }

   public SwACodec copy() {
      return new SwACodec(this);
   }
}
