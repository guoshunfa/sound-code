package com.sun.xml.internal.bind.api;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/** @deprecated */
public abstract class BridgeContext {
   protected BridgeContext() {
   }

   public abstract void setErrorHandler(ValidationEventHandler var1);

   public abstract void setAttachmentMarshaller(AttachmentMarshaller var1);

   public abstract void setAttachmentUnmarshaller(AttachmentUnmarshaller var1);

   public abstract AttachmentMarshaller getAttachmentMarshaller();

   public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}
