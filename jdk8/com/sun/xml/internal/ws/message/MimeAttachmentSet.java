package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

public final class MimeAttachmentSet implements AttachmentSet {
   private final MimeMultipartParser mpp;
   private Map<String, Attachment> atts = new HashMap();

   public MimeAttachmentSet(MimeMultipartParser mpp) {
      this.mpp = mpp;
   }

   @Nullable
   public Attachment get(String contentId) {
      Attachment att = (Attachment)this.atts.get(contentId);
      if (att != null) {
         return att;
      } else {
         try {
            att = this.mpp.getAttachmentPart(contentId);
            if (att != null) {
               this.atts.put(contentId, att);
            }

            return att;
         } catch (IOException var4) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(contentId), var4);
         }
      }
   }

   public boolean isEmpty() {
      return this.atts.size() <= 0 && this.mpp.getAttachmentParts().isEmpty();
   }

   public void add(Attachment att) {
      this.atts.put(att.getContentId(), att);
   }

   public Iterator<Attachment> iterator() {
      Map<String, Attachment> attachments = this.mpp.getAttachmentParts();
      Iterator var2 = attachments.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Attachment> att = (Map.Entry)var2.next();
         if (this.atts.get(att.getKey()) == null) {
            this.atts.put(att.getKey(), att.getValue());
         }
      }

      return this.atts.values().iterator();
   }
}
