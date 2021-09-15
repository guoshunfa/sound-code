package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public interface RootOnlyCodec extends Codec {
   void decode(@NotNull InputStream var1, @NotNull String var2, @NotNull Packet var3, @NotNull AttachmentSet var4) throws IOException;

   void decode(@NotNull ReadableByteChannel var1, @NotNull String var2, @NotNull Packet var3, @NotNull AttachmentSet var4);
}
