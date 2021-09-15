package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class BridgeContextImpl extends BridgeContext {
   public final UnmarshallerImpl unmarshaller;
   public final MarshallerImpl marshaller;

   BridgeContextImpl(JAXBContextImpl context) {
      this.unmarshaller = context.createUnmarshaller();
      this.marshaller = context.createMarshaller();
   }

   public void setErrorHandler(ValidationEventHandler handler) {
      try {
         this.unmarshaller.setEventHandler(handler);
         this.marshaller.setEventHandler(handler);
      } catch (JAXBException var3) {
         throw new Error(var3);
      }
   }

   public void setAttachmentMarshaller(AttachmentMarshaller m) {
      this.marshaller.setAttachmentMarshaller(m);
   }

   public void setAttachmentUnmarshaller(AttachmentUnmarshaller u) {
      this.unmarshaller.setAttachmentUnmarshaller(u);
   }

   public AttachmentMarshaller getAttachmentMarshaller() {
      return this.marshaller.getAttachmentMarshaller();
   }

   public AttachmentUnmarshaller getAttachmentUnmarshaller() {
      return this.unmarshaller.getAttachmentUnmarshaller();
   }
}
