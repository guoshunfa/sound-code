package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.stream.XMLStreamReader;

public interface StreamSOAPCodec extends Codec {
   @NotNull
   Message decode(@NotNull XMLStreamReader var1);

   @NotNull
   Message decode(@NotNull XMLStreamReader var1, @NotNull AttachmentSet var2);
}
