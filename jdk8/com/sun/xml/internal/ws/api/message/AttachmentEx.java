package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import java.util.Iterator;

public interface AttachmentEx extends Attachment {
   @NotNull
   Iterator<AttachmentEx.MimeHeader> getMimeHeaders();

   public interface MimeHeader {
      String getName();

      String getValue();
   }
}
