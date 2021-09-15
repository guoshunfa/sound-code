package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ScatteringByteChannel extends ReadableByteChannel {
   long read(ByteBuffer[] var1, int var2, int var3) throws IOException;

   long read(ByteBuffer[] var1) throws IOException;
}
