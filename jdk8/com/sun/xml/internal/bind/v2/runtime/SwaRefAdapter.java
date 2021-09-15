package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class SwaRefAdapter extends XmlAdapter<String, DataHandler> {
   public DataHandler unmarshal(String cid) {
      AttachmentUnmarshaller au = UnmarshallingContext.getInstance().parent.getAttachmentUnmarshaller();
      return au.getAttachmentAsDataHandler(cid);
   }

   public String marshal(DataHandler data) {
      if (data == null) {
         return null;
      } else {
         AttachmentMarshaller am = XMLSerializer.getInstance().attachmentMarshaller;
         return am.addSwaRefAttachment(data);
      }
   }
}
