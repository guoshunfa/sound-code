package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface Codec {
   String getMimeType();

   ContentType getStaticContentType(Packet var1);

   ContentType encode(Packet var1, OutputStream var2) throws IOException;

   ContentType encode(Packet var1, WritableByteChannel var2);

   Codec copy();

   void decode(InputStream var1, String var2, Packet var3) throws IOException;

   void decode(ReadableByteChannel var1, String var2, Packet var3);
}
