package java.nio;

import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class CharBuffer extends Buffer implements Comparable<CharBuffer>, Appendable, CharSequence, Readable {
   final char[] hb;
   final int offset;
   boolean isReadOnly;

   CharBuffer(int var1, int var2, int var3, int var4, char[] var5, int var6) {
      super(var1, var2, var3, var4);
      this.hb = var5;
      this.offset = var6;
   }

   CharBuffer(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (char[])null, 0);
   }

   public static CharBuffer allocate(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException();
      } else {
         return new HeapCharBuffer(var0, var0);
      }
   }

   public static CharBuffer wrap(char[] var0, int var1, int var2) {
      try {
         return new HeapCharBuffer(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public static CharBuffer wrap(char[] var0) {
      return wrap((char[])var0, 0, var0.length);
   }

   public int read(CharBuffer var1) throws IOException {
      int var2 = var1.remaining();
      int var3 = this.remaining();
      if (var3 == 0) {
         return -1;
      } else {
         int var4 = Math.min(var3, var2);
         int var5 = this.limit();
         if (var2 < var3) {
            this.limit(this.position() + var4);
         }

         try {
            if (var4 > 0) {
               var1.put(this);
            }
         } finally {
            this.limit(var5);
         }

         return var4;
      }
   }

   public static CharBuffer wrap(CharSequence var0, int var1, int var2) {
      try {
         return new StringCharBuffer(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public static CharBuffer wrap(CharSequence var0) {
      return wrap((CharSequence)var0, 0, var0.length());
   }

   public abstract CharBuffer slice();

   public abstract CharBuffer duplicate();

   public abstract CharBuffer asReadOnlyBuffer();

   public abstract char get();

   public abstract CharBuffer put(char var1);

   public abstract char get(int var1);

   abstract char getUnchecked(int var1);

   public abstract CharBuffer put(int var1, char var2);

   public CharBuffer get(char[] var1, int var2, int var3) {
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

   public CharBuffer get(char[] var1) {
      return this.get(var1, 0, var1.length);
   }

   public CharBuffer put(CharBuffer var1) {
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

   public CharBuffer put(char[] var1, int var2, int var3) {
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

   public final CharBuffer put(char[] var1) {
      return this.put((char[])var1, 0, var1.length);
   }

   public CharBuffer put(String var1, int var2, int var3) {
      checkBounds(var2, var3 - var2, var1.length());
      if (this.isReadOnly()) {
         throw new ReadOnlyBufferException();
      } else if (var3 - var2 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         for(int var4 = var2; var4 < var3; ++var4) {
            this.put(var1.charAt(var4));
         }

         return this;
      }
   }

   public final CharBuffer put(String var1) {
      return this.put((String)var1, 0, var1.length());
   }

   public final boolean hasArray() {
      return this.hb != null && !this.isReadOnly;
   }

   public final char[] array() {
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

   public abstract CharBuffer compact();

   public abstract boolean isDirect();

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
      } else if (!(var1 instanceof CharBuffer)) {
         return false;
      } else {
         CharBuffer var2 = (CharBuffer)var1;
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

   private static boolean equals(char var0, char var1) {
      return var0 == var1;
   }

   public int compareTo(CharBuffer var1) {
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

   private static int compare(char var0, char var1) {
      return Character.compare(var0, var1);
   }

   public String toString() {
      return this.toString(this.position(), this.limit());
   }

   abstract String toString(int var1, int var2);

   public final int length() {
      return this.remaining();
   }

   public final char charAt(int var1) {
      return this.get(this.position() + this.checkIndex(var1, 1));
   }

   public abstract CharBuffer subSequence(int var1, int var2);

   public CharBuffer append(CharSequence var1) {
      return var1 == null ? this.put("null") : this.put(var1.toString());
   }

   public CharBuffer append(CharSequence var1, int var2, int var3) {
      Object var4 = var1 == null ? "null" : var1;
      return this.put(((CharSequence)var4).subSequence(var2, var3).toString());
   }

   public CharBuffer append(char var1) {
      return this.put(var1);
   }

   public abstract ByteOrder order();

   public IntStream chars() {
      return StreamSupport.intStream(() -> {
         return new CharBufferSpliterator(this);
      }, 16464, false);
   }
}
