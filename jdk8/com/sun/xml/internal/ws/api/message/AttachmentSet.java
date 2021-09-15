package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;

public interface AttachmentSet extends Iterable<Attachment> {
   @Nullable
   Attachment get(String var1);

   boolean isEmpty();

   void add(Attachment var1);
}
