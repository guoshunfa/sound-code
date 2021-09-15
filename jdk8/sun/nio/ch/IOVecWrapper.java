package sun.nio.ch;

import java.nio.ByteBuffer;
import sun.misc.Cleaner;

class IOVecWrapper {
   private static final int BASE_OFFSET = 0;
   private static final int LEN_OFFSET;
   private static final int SIZE_IOVEC;
   private final AllocatedNativeObject vecArray;
   private final int size;
   private final ByteBuffer[] buf;
   private final int[] position;
   private final int[] remaining;
   private final ByteBuffer[] shadow;
   final long address;
   static int addressSize = Util.unsafe().addressSize();
   private static final ThreadLocal<IOVecWrapper> cached = new ThreadLocal();

   private IOVecWrapper(int var1) {
      this.size = var1;
      this.buf = new ByteBuffer[var1];
      this.position = new int[var1];
      this.remaining = new int[var1];
      this.shadow = new ByteBuffer[var1];
      this.vecArray = new AllocatedNativeObject(var1 * SIZE_IOVEC, false);
      this.address = this.vecArray.address();
   }

   static IOVecWrapper get(int var0) {
      IOVecWrapper var1 = (IOVecWrapper)cached.get();
      if (var1 != null && var1.size < var0) {
         var1.vecArray.free();
         var1 = null;
      }

      if (var1 == null) {
         var1 = new IOVecWrapper(var0);
         Cleaner.create(var1, new IOVecWrapper.Deallocator(var1.vecArray));
         cached.set(var1);
      }

      return var1;
   }

   void setBuffer(int var1, ByteBuffer var2, int var3, int var4) {
      this.buf[var1] = var2;
      this.position[var1] = var3;
      this.remaining[var1] = var4;
   }

   void setShadow(int var1, ByteBuffer var2) {
      this.shadow[var1] = var2;
   }

   ByteBuffer getBuffer(int var1) {
      return this.buf[var1];
   }

   int getPosition(int var1) {
      return this.position[var1];
   }

   int getRemaining(int var1) {
      return this.remaining[var1];
   }

   ByteBuffer getShadow(int var1) {
      return this.shadow[var1];
   }

   void clearRefs(int var1) {
      this.buf[var1] = null;
      this.shadow[var1] = null;
   }

   void putBase(int var1, long var2) {
      int var4 = SIZE_IOVEC * var1 + 0;
      if (addressSize == 4) {
         this.vecArray.putInt(var4, (int)var2);
      } else {
         this.vecArray.putLong(var4, var2);
      }

   }

   void putLen(int var1, long var2) {
      int var4 = SIZE_IOVEC * var1 + LEN_OFFSET;
      if (addressSize == 4) {
         this.vecArray.putInt(var4, (int)var2);
      } else {
         this.vecArray.putLong(var4, var2);
      }

   }

   static {
      LEN_OFFSET = addressSize;
      SIZE_IOVEC = (short)(addressSize * 2);
   }

   private static class Deallocator implements Runnable {
      private final AllocatedNativeObject obj;

      Deallocator(AllocatedNativeObject var1) {
         this.obj = var1;
      }

      public void run() {
         this.obj.free();
      }
   }
}
