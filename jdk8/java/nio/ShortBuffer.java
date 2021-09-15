package java.nio;

public abstract class ShortBuffer extends Buffer implements Comparable<ShortBuffer> {
   final short[] hb;
   final int offset;
   boolean isReadOnly;

   ShortBuffer(int var1, int var2, int var3, int var4, short[] var5, int var6) {
      super(var1, var2, var3, var4);
      this.hb = var5;
      this.offset = var6;
   }

   ShortBuffer(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (short[])null, 0);
   }

   public static ShortBuffer allocate(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException();
      } else {
         return new HeapShortBuffer(var0, var0);
      }
   }

   public static ShortBuffer wrap(short[] var0, int var1, int var2) {
      try {
         return new HeapShortBuffer(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public static ShortBuffer wrap(short[] var0) {
      return wrap(var0, 0, var0.length);
   }

   public abstract ShortBuffer slice();

   public abstract ShortBuffer duplicate();

   public abstract ShortBuffer asReadOnlyBuffer();

   public abstract short get();

   public abstract ShortBuffer put(short var1);

   public abstract short get(int var1);

   public abstract ShortBuffer put(int var1, short var2);

   public ShortBuffer get(short[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferUnderflowException();
      } else {
         int var4 = var2 + var3;

         for(int var5 = var2; var5 < var4; ++var5) {
            var1[var5] = this.get();
         }

         return this;
      }
   }

   public ShortBuffer get(short[] var1) {
      return this.get(var1, 0, var1.length);
   }

   public ShortBuffer put(ShortBuffer var1) {
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else if (this.isReadOnly()) {
         throw new ReadOnlyBufferException();
      } else {
         int var2 = var1.remaining();
         if (var2 > this.remaining()) {
            throw new BufferOverflowException();
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               this.put(var1.get());
            }

            return this;
         }
      }
   }

   public ShortBuffer put(short[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         int var4 = var2 + var3;

         for(int var5 = var2; var5 < var4; ++var5) {
            this.put(var1[var5]);
         }

         return this;
      }
   }

   public final ShortBuffer put(short[] var1) {
      return this.put(var1, 0, var1.length);
   }

   public final boolean hasArray() {
      return this.hb != null && !this.isReadOnly;
   }

   public final short[] array() {
      if (this.hb == null) {
         throw new UnsupportedOperationException();
      } else if (this.isReadOnly) {
         throw new ReadOnlyBufferException();
      } else {
         return this.hb;
      }
   }

   public final int arrayOffset() {
      if (this.hb == null) {
         throw new UnsupportedOperationException();
      } else if (this.isReadOnly) {
         throw new ReadOnlyBufferException();
      } else {
         return this.offset;
      }
   }

   public abstract ShortBuffer compact();

   public abstract boolean isDirect();

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getClass().getName());
      var1.append("[pos=");
      var1.append(this.position());
      var1.append(" lim=");
      var1.append(this.limit());
      var1.append(" cap=");
      var1.append(this.capacity());
      var1.append("]");
      return var1.toString();
   }

   public int hashCode() {
      int var1 = 1;
      int var2 = this.position();

      for(int var3 = this.limit() - 1; var3 >= var2; --var3) {
         var1 = 31 * var1 + this.get(var3);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ShortBuffer)) {
         return false;
      } else {
         ShortBuffer var2 = (ShortBuffer)var1;
         if (this.remaining() != var2.remaining()) {
            return false;
         } else {
            int var3 = this.position();
            int var4 = this.limit() - 1;

            for(int var5 = var2.limit() - 1; var4 >= var3; --var5) {
               if (!equals(this.get(var4), var2.get(var5))) {
                  return false;
               }

               --var4;
            }

            return true;
         }
      }
   }

   private static boolean equals(short var0, short var1) {
      return var0 == var1;
   }

   public int compareTo(ShortBuffer var1) {
      int var2 = this.position() + Math.min(this.remaining(), var1.remaining());
      int var3 = this.position();

      for(int var4 = var1.position(); var3 < var2; ++var4) {
         int var5 = compare(this.get(var3), var1.get(var4));
         if (var5 != 0) {
            return var5;
         }

         ++var3;
      }

      return this.remaining() - var1.remaining();
   }

   private static int compare(short var0, short var1) {
      return Short.compare(var0, var1);
   }

   public abstract ByteOrder order();
}
