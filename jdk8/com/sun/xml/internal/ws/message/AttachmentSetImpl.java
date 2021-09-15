package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

public final class AttachmentSetImpl implements AttachmentSet {
   private final ArrayList<Attachment> attList = new ArrayList();

   public AttachmentSetImpl() {
   }

   public AttachmentSetImpl(Iterable<Attachment> base) {
      Iterator var2 = base.iterator();

      while(var2.hasNext()) {
         Attachment a = (Attachment)var2.next();
         this.add(a);
      }

   }

   public Attachment get(String contentId) {
      for(int i = this.attList.size() - 1; i >= 0; --i) {
         Attachment a = (Attachment)this.attList.get(i);
         if (a.getContentId().equals(contentId)) {
            return a;
         }
      }

      return null;
   }

   public boolean isEmpty() {
      return this.attList.isEmpty();
   }

   public void add(Attachment att) {
      this.attList.add(att);
   }

   public Iterator<Attachment> iterator() {
      return this.attList.iterator();
   }
}
