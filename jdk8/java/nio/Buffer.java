package java.nio;

public abstract class Buffer {
   static final int SPLITERATOR_CHARACTERISTICS = 16464;
   private int mark = -1;
   private int position = 0;
   private int limit;
   private int capacity;
   long address;

   Buffer(int var1, int var2, int var3, int var4) {
      if (var4 < 0) {
         throw new IllegalArgumentException("Negative capacity: " + var4);
      } else {
         this.capacity = var4;
         this.limit(var3);
         this.position(var2);
         if (var1 >= 0) {
            if (var1 > var2) {
               throw new IllegalArgumentException("mark > position: (" + var1 + " > " + var2 + ")");
            }

            this.mark = var1;
         }

      }
   }

   public final int capacity() {
      return this.capacity;
   }

   public final int position() {
      return this.position;
   }

   public final Buffer position(int var1) {
      if (var1 <= this.limit && var1 >= 0) {
         this.position = var1;
         if (this.mark > this.position) {
            this.mark = -1;
         }

         return this;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int limit() {
      return this.limit;
   }

   public final Buffer limit(int var1) {
      if (var1 <= this.capacity && var1 >= 0) {
         this.limit = var1;
         if (this.position > this.limit) {
            this.position = this.limit;
         }

         if (this.mark > this.limit) {
            this.mark = -1;
         }

         return this;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final Buffer mark() {
      this.mark = this.position;
      return this;
   }

   public final Buffer reset() {
      int var1 = this.mark;
      if (var1 < 0) {
         throw new InvalidMarkException();
      } else {
         this.position = var1;
         return this;
      }
   }

   public final Buffer clear() {
      this.position = 0;
      this.limit = this.capacity;
      this.mark = -1;
      return this;
   }

   public final Buffer flip() {
      this.limit = this.position;
      this.position = 0;
      this.mark = -1;
      return this;
   }

   public final Buffer rewind() {
      this.position = 0;
      this.mark = -1;
      return this;
   }

   public final int remaining() {
      return this.limit - this.position;
   }

   public final boolean hasRemaining() {
      return this.position < this.limit;
   }

   public abstract boolean isReadOnly();

   public abstract boolean hasArray();

   public abstract Object array();

   public abstract int arrayOffset();

   public abstract boolean isDirect();

   final int nextGetIndex() {
      if (this.position >= this.limit) {
         throw new BufferUnderflowException();
      } else {
         return this.position++;
      }
   }

   final int nextGetIndex(int var1) {
      if (this.limit - this.position < var1) {
         throw new BufferUnderflowException();
      } else {
         int var2 = this.position;
         this.position += var1;
         return var2;
      }
   }

   final int nextPutIndex() {
      if (this.position >= this.limit) {
         throw new BufferOverflowException();
      } else {
         return this.position++;
      }
   }

   final int nextPutIndex(int var1) {
      if (this.limit - this.position < var1) {
         throw new BufferOverflowException();
      } else {
         int var2 = this.position;
         this.position += var1;
         return var2;
      }
   }

   final int checkIndex(int var1) {
      if (var1 >= 0 && var1 < this.limit) {
         return var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   final int checkIndex(int var1, int var2) {
      if (var1 >= 0 && var2 <= this.limit - var1) {
         return var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   final int markValue() {
      return this.mark;
   }

   final void truncate() {
      this.mark = -1;
      this.position = 0;
      this.limit = 0;
      this.capacity = 0;
   }

   final void discardMark() {
      this.mark = -1;
   }

   static void checkBounds(int var0, int var1, int var2) {
      if ((var0 | var1 | var0 + var1 | var2 - (var0 + var1)) < 0) {
         throw new IndexOutOfBoundsException();
      }
   }
}
