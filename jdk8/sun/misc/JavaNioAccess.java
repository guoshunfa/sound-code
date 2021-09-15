package sun.misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public interface JavaNioAccess {
   JavaNioAccess.BufferPool getDirectBufferPool();

   ByteBuffer newDirectByteBuffer(long var1, int var3, Object var4);

   void truncate(Buffer var1);

   public interface BufferPool {
      String getName();

      long getCount();

      long getTotalCapacity();

      long getMemoryUsed();
   }
}
