package java.util.stream;

abstract class AbstractSpinedBuffer {
   public static final int MIN_CHUNK_POWER = 4;
   public static final int MIN_CHUNK_SIZE = 16;
   public static final int MAX_CHUNK_POWER = 30;
   public static final int MIN_SPINE_SIZE = 8;
   protected final int initialChunkPower;
   protected int elementIndex;
   protected int spineIndex;
   protected long[] priorElementCount;

   protected AbstractSpinedBuffer() {
      this.initialChunkPower = 4;
   }

   protected AbstractSpinedBuffer(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else {
         this.initialChunkPower = Math.max(4, 32 - Integer.numberOfLeadingZeros(var1 - 1));
      }
   }

   public boolean isEmpty() {
      return this.spineIndex == 0 && this.elementIndex == 0;
   }

   public long count() {
      return this.spineIndex == 0 ? (long)this.elementIndex : this.priorElementCount[this.spineIndex] + (long)this.elementIndex;
   }

   protected int chunkSize(int var1) {
      int var2 = var1 != 0 && var1 != 1 ? Math.min(this.initialChunkPower + var1 - 1, 30) : this.initialChunkPower;
      return 1 << var2;
   }

   public abstract void clear();
}
