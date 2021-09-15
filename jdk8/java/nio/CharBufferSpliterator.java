package java.nio;

import java.util.Spliterator;
import java.util.function.IntConsumer;

class CharBufferSpliterator implements Spliterator.OfInt {
   private final CharBuffer buffer;
   private int index;
   private final int limit;

   CharBufferSpliterator(CharBuffer var1) {
      this(var1, var1.position(), var1.limit());
   }

   CharBufferSpliterator(CharBuffer var1, int var2, int var3) {
      assert var2 <= var3;

      this.buffer = var1;
      this.index = var2 <= var3 ? var2 : var3;
      this.limit = var3;
   }

   public Spliterator.OfInt trySplit() {
      int var1 = this.index;
      int var2 = var1 + this.limit >>> 1;
      return var1 >= var2 ? null : new CharBufferSpliterator(this.buffer, var1, this.index = var2);
   }

   public void forEachRemaining(IntConsumer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         CharBuffer var2 = this.buffer;
         int var3 = this.index;
         int var4 = this.limit;
         this.index = var4;

         while(var3 < var4) {
            var1.accept(var2.getUnchecked(var3++));
         }

      }
   }

   public boolean tryAdvance(IntConsumer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.index >= 0 && this.index < this.limit) {
         var1.accept(this.buffer.getUnchecked(this.index++));
         return true;
      } else {
         return false;
      }
   }

   public long estimateSize() {
      return (long)(this.limit - this.index);
   }

   public int characteristics() {
      return 16464;
   }
}
