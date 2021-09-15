package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface GatheringByteChannel extends WritableByteChannel {
   long write(ByteBuffer[] var1, int var2, int var3) throws IOException;

   long write(ByteBuffer[] var1) throws IOException;
}
