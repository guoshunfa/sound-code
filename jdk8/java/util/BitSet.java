package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class BitSet implements Cloneable, Serializable {
   private static final int ADDRESS_BITS_PER_WORD = 6;
   private static final int BITS_PER_WORD = 64;
   private static final int BIT_INDEX_MASK = 63;
   private static final long WORD_MASK = -1L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("bits", long[].class)};
   private long[] words;
   private transient int wordsInUse = 0;
   private transient boolean sizeIsSticky = false;
   private static final long serialVersionUID = 7997698588986878753L;

   private static int wordIndex(int var0) {
      return var0 >> 6;
   }

   private void checkInvariants() {
      assert this.wordsInUse == 0 || this.words[this.wordsInUse - 1] != 0L;

      assert this.wordsInUse >= 0 && this.wordsInUse <= this.words.length;

      assert this.wordsInUse == this.words.length || this.words[this.wordsInUse] == 0L;

   }

   private void recalculateWordsInUse() {
      int var1;
      for(var1 = this.wordsInUse - 1; var1 >= 0 && this.words[var1] == 0L; --var1) {
      }

      this.wordsInUse = var1 + 1;
   }

   public BitSet() {
      this.initWords(64);
      this.sizeIsSticky = false;
   }

   public BitSet(int var1) {
      if (var1 < 0) {
         throw new NegativeArraySizeException("nbits < 0: " + var1);
      } else {
         this.initWords(var1);
         this.sizeIsSticky = true;
      }
   }

   private void initWords(int var1) {
      this.words = new long[wordIndex(var1 - 1) + 1];
   }

   private BitSet(long[] var1) {
      this.words = var1;
      this.wordsInUse = var1.length;
      this.checkInvariants();
   }

   public static BitSet valueOf(long[] var0) {
      int var1;
      for(var1 = var0.length; var1 > 0 && var0[var1 - 1] == 0L; --var1) {
      }

      return new BitSet(Arrays.copyOf(var0, var1));
   }

   public static BitSet valueOf(LongBuffer var0) {
      var0 = var0.slice();

      int var1;
      for(var1 = var0.remaining(); var1 > 0 && var0.get(var1 - 1) == 0L; --var1) {
      }

      long[] var2 = new long[var1];
      var0.get(var2);
      return new BitSet(var2);
   }

   public static BitSet valueOf(byte[] var0) {
      return valueOf(ByteBuffer.wrap(var0));
   }

   public static BitSet valueOf(ByteBuffer var0) {
      var0 = var0.slice().order(ByteOrder.LITTLE_ENDIAN);

      int var1;
      for(var1 = var0.remaining(); var1 > 0 && var0.get(var1 - 1) == 0; --var1) {
      }

      long[] var2 = new long[(var1 + 7) / 8];
      var0.limit(var1);

      int var3;
      for(var3 = 0; var0.remaining() >= 8; var2[var3++] = var0.getLong()) {
      }

      int var4 = var0.remaining();

      for(int var5 = 0; var5 < var4; ++var5) {
         var2[var3] |= ((long)var0.get() & 255L) << 8 * var5;
      }

      return new BitSet(var2);
   }

   public byte[] toByteArray() {
      int var1 = this.wordsInUse;
      if (var1 == 0) {
         return new byte[0];
      } else {
         int var2 = 8 * (var1 - 1);

         for(long var3 = this.words[var1 - 1]; var3 != 0L; var3 >>>= 8) {
            ++var2;
         }

         byte[] var7 = new byte[var2];
         ByteBuffer var4 = ByteBuffer.wrap(var7).order(ByteOrder.LITTLE_ENDIAN);

         for(int var5 = 0; var5 < var1 - 1; ++var5) {
            var4.putLong(this.words[var5]);
         }

         for(long var8 = this.words[var1 - 1]; var8 != 0L; var8 >>>= 8) {
            var4.put((byte)((int)(var8 & 255L)));
         }

         return var7;
      }
   }

   public long[] toLongArray() {
      return Arrays.copyOf(this.words, this.wordsInUse);
   }

   private void ensureCapacity(int var1) {
      if (this.words.length < var1) {
         int var2 = Math.max(2 * this.words.length, var1);
         this.words = Arrays.copyOf(this.words, var2);
         this.sizeIsSticky = false;
      }

   }

   private void expandTo(int var1) {
      int var2 = var1 + 1;
      if (this.wordsInUse < var2) {
         this.ensureCapacity(var2);
         this.wordsInUse = var2;
      }

   }

   private static void checkRange(int var0, int var1) {
      if (var0 < 0) {
         throw new IndexOutOfBoundsException("fromIndex < 0: " + var0);
      } else if (var1 < 0) {
         throw new IndexOutOfBoundsException("toIndex < 0: " + var1);
      } else if (var0 > var1) {
         throw new IndexOutOfBoundsException("fromIndex: " + var0 + " > toIndex: " + var1);
      }
   }

   public void flip(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("bitIndex < 0: " + var1);
      } else {
         int var2 = wordIndex(var1);
         this.expandTo(var2);
         long[] var10000 = this.words;
         var10000[var2] ^= 1L << var1;
         this.recalculateWordsInUse();
         this.checkInvariants();
      }
   }

   public void flip(int var1, int var2) {
      checkRange(var1, var2);
      if (var1 != var2) {
         int var3 = wordIndex(var1);
         int var4 = wordIndex(var2 - 1);
         this.expandTo(var4);
         long var5 = -1L << var1;
         long var7 = -1L >>> -var2;
         long[] var10000;
         if (var3 == var4) {
            var10000 = this.words;
            var10000[var3] ^= var5 & var7;
         } else {
            var10000 = this.words;
            var10000[var3] ^= var5;

            for(int var9 = var3 + 1; var9 < var4; ++var9) {
               var10000 = this.words;
               var10000[var9] = ~var10000[var9];
            }

            var10000 = this.words;
            var10000[var4] ^= var7;
         }

         this.recalculateWordsInUse();
         this.checkInvariants();
      }
   }

   public void set(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("bitIndex < 0: " + var1);
      } else {
         int var2 = wordIndex(var1);
         this.expandTo(var2);
         long[] var10000 = this.words;
         var10000[var2] |= 1L << var1;
         this.checkInvariants();
      }
   }

   public void set(int var1, boolean var2) {
      if (var2) {
         this.set(var1);
      } else {
         this.clear(var1);
      }

   }

   public void set(int var1, int var2) {
      checkRange(var1, var2);
      if (var1 != var2) {
         int var3 = wordIndex(var1);
         int var4 = wordIndex(var2 - 1);
         this.expandTo(var4);
         long var5 = -1L << var1;
         long var7 = -1L >>> -var2;
         long[] var10000;
         if (var3 == var4) {
            var10000 = this.words;
            var10000[var3] |= var5 & var7;
         } else {
            var10000 = this.words;
            var10000[var3] |= var5;

            for(int var9 = var3 + 1; var9 < var4; ++var9) {
               this.words[var9] = -1L;
            }

            var10000 = this.words;
            var10000[var4] |= var7;
         }

         this.checkInvariants();
      }
   }

   public void set(int var1, int var2, boolean var3) {
      if (var3) {
         this.set(var1, var2);
      } else {
         this.clear(var1, var2);
      }

   }

   public void clear(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("bitIndex < 0: " + var1);
      } else {
         int var2 = wordIndex(var1);
         if (var2 < this.wordsInUse) {
            long[] var10000 = this.words;
            var10000[var2] &= ~(1L << var1);
            this.recalculateWordsInUse();
            this.checkInvariants();
         }
      }
   }

   public void clear(int var1, int var2) {
      checkRange(var1, var2);
      if (var1 != var2) {
         int var3 = wordIndex(var1);
         if (var3 < this.wordsInUse) {
            int var4 = wordIndex(var2 - 1);
            if (var4 >= this.wordsInUse) {
               var2 = this.length();
               var4 = this.wordsInUse - 1;
            }

            long var5 = -1L << var1;
            long var7 = -1L >>> -var2;
            long[] var10000;
            if (var3 == var4) {
               var10000 = this.words;
               var10000[var3] &= ~(var5 & var7);
            } else {
               var10000 = this.words;
               var10000[var3] &= ~var5;

               for(int var9 = var3 + 1; var9 < var4; ++var9) {
                  this.words[var9] = 0L;
               }

               var10000 = this.words;
               var10000[var4] &= ~var7;
            }

            this.recalculateWordsInUse();
            this.checkInvariants();
         }
      }
   }

   public void clear() {
      while(this.wordsInUse > 0) {
         this.words[--this.wordsInUse] = 0L;
      }

   }

   public boolean get(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("bitIndex < 0: " + var1);
      } else {
         this.checkInvariants();
         int var2 = wordIndex(var1);
         return var2 < this.wordsInUse && (this.words[var2] & 1L << var1) != 0L;
      }
   }

   public BitSet get(int var1, int var2) {
      checkRange(var1, var2);
      this.checkInvariants();
      int var3 = this.length();
      if (var3 > var1 && var1 != var2) {
         if (var2 > var3) {
            var2 = var3;
         }

         BitSet var4 = new BitSet(var2 - var1);
         int var5 = wordIndex(var2 - var1 - 1) + 1;
         int var6 = wordIndex(var1);
         boolean var7 = (var1 & 63) == 0;

         for(int var8 = 0; var8 < var5 - 1; ++var6) {
            var4.words[var8] = var7 ? this.words[var6] : this.words[var6] >>> var1 | this.words[var6 + 1] << -var1;
            ++var8;
         }

         long var10 = -1L >>> -var2;
         var4.words[var5 - 1] = (var2 - 1 & 63) < (var1 & 63) ? this.words[var6] >>> var1 | (this.words[var6 + 1] & var10) << -var1 : (this.words[var6] & var10) >>> var1;
         var4.wordsInUse = var5;
         var4.recalculateWordsInUse();
         var4.checkInvariants();
         return var4;
      } else {
         return new BitSet(0);
      }
   }

   public int nextSetBit(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("fromIndex < 0: " + var1);
      } else {
         this.checkInvariants();
         int var2 = wordIndex(var1);
         if (var2 >= this.wordsInUse) {
            return -1;
         } else {
            long var3;
            for(var3 = this.words[var2] & -1L << var1; var3 == 0L; var3 = this.words[var2]) {
               ++var2;
               if (var2 == this.wordsInUse) {
                  return -1;
               }
            }

            return var2 * 64 + Long.numberOfTrailingZeros(var3);
         }
      }
   }

   public int nextClearBit(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("fromIndex < 0: " + var1);
      } else {
         this.checkInvariants();
         int var2 = wordIndex(var1);
         if (var2 >= this.wordsInUse) {
            return var1;
         } else {
            long var3;
            for(var3 = ~this.words[var2] & -1L << var1; var3 == 0L; var3 = ~this.words[var2]) {
               ++var2;
               if (var2 == this.wordsInUse) {
                  return this.wordsInUse * 64;
               }
            }

            return var2 * 64 + Long.numberOfTrailingZeros(var3);
         }
      }
   }

   public int previousSetBit(int var1) {
      if (var1 < 0) {
         if (var1 == -1) {
            return -1;
         } else {
            throw new IndexOutOfBoundsException("fromIndex < -1: " + var1);
         }
      } else {
         this.checkInvariants();
         int var2 = wordIndex(var1);
         if (var2 >= this.wordsInUse) {
            return this.length() - 1;
         } else {
            long var3;
            for(var3 = this.words[var2] & -1L >>> -(var1 + 1); var3 == 0L; var3 = this.words[var2]) {
               if (var2-- == 0) {
                  return -1;
               }
            }

            return (var2 + 1) * 64 - 1 - Long.numberOfLeadingZeros(var3);
         }
      }
   }

   public int previousClearBit(int var1) {
      if (var1 < 0) {
         if (var1 == -1) {
            return -1;
         } else {
            throw new IndexOutOfBoundsException("fromIndex < -1: " + var1);
         }
      } else {
         this.checkInvariants();
         int var2 = wordIndex(var1);
         if (var2 >= this.wordsInUse) {
            return var1;
         } else {
            long var3;
            for(var3 = ~this.words[var2] & -1L >>> -(var1 + 1); var3 == 0L; var3 = ~this.words[var2]) {
               if (var2-- == 0) {
                  return -1;
               }
            }

            return (var2 + 1) * 64 - 1 - Long.numberOfLeadingZeros(var3);
         }
      }
   }

   public int length() {
      return this.wordsInUse == 0 ? 0 : 64 * (this.wordsInUse - 1) + (64 - Long.numberOfLeadingZeros(this.words[this.wordsInUse - 1]));
   }

   public boolean isEmpty() {
      return this.wordsInUse == 0;
   }

   public boolean intersects(BitSet var1) {
      for(int var2 = Math.min(this.wordsInUse, var1.wordsInUse) - 1; var2 >= 0; --var2) {
         if ((this.words[var2] & var1.words[var2]) != 0L) {
            return true;
         }
      }

      return false;
   }

   public int cardinality() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.wordsInUse; ++var2) {
         var1 += Long.bitCount(this.words[var2]);
      }

      return var1;
   }

   public void and(BitSet var1) {
      if (this != var1) {
         while(this.wordsInUse > var1.wordsInUse) {
            this.words[--this.wordsInUse] = 0L;
         }

         for(int var2 = 0; var2 < this.wordsInUse; ++var2) {
            long[] var10000 = this.words;
            var10000[var2] &= var1.words[var2];
         }

         this.recalculateWordsInUse();
         this.checkInvariants();
      }
   }

   public void or(BitSet var1) {
      if (this != var1) {
         int var2 = Math.min(this.wordsInUse, var1.wordsInUse);
         if (this.wordsInUse < var1.wordsInUse) {
            this.ensureCapacity(var1.wordsInUse);
            this.wordsInUse = var1.wordsInUse;
         }

         for(int var3 = 0; var3 < var2; ++var3) {
            long[] var10000 = this.words;
            var10000[var3] |= var1.words[var3];
         }

         if (var2 < var1.wordsInUse) {
            System.arraycopy(var1.words, var2, this.words, var2, this.wordsInUse - var2);
         }

         this.checkInvariants();
      }
   }

   public void xor(BitSet var1) {
      int var2 = Math.min(this.wordsInUse, var1.wordsInUse);
      if (this.wordsInUse < var1.wordsInUse) {
         this.ensureCapacity(var1.wordsInUse);
         this.wordsInUse = var1.wordsInUse;
      }

      for(int var3 = 0; var3 < var2; ++var3) {
         long[] var10000 = this.words;
         var10000[var3] ^= var1.words[var3];
      }

      if (var2 < var1.wordsInUse) {
         System.arraycopy(var1.words, var2, this.words, var2, var1.wordsInUse - var2);
      }

      this.recalculateWordsInUse();
      this.checkInvariants();
   }

   public void andNot(BitSet var1) {
      for(int var2 = Math.min(this.wordsInUse, var1.wordsInUse) - 1; var2 >= 0; --var2) {
         long[] var10000 = this.words;
         var10000[var2] &= ~var1.words[var2];
      }

      this.recalculateWordsInUse();
      this.checkInvariants();
   }

   public int hashCode() {
      long var1 = 1234L;
      int var3 = this.wordsInUse;

      while(true) {
         --var3;
         if (var3 < 0) {
            return (int)(var1 >> 32 ^ var1);
         }

         var1 ^= this.words[var3] * (long)(var3 + 1);
      }
   }

   public int size() {
      return this.words.length * 64;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BitSet)) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         BitSet var2 = (BitSet)var1;
         this.checkInvariants();
         var2.checkInvariants();
         if (this.wordsInUse != var2.wordsInUse) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.wordsInUse; ++var3) {
               if (this.words[var3] != var2.words[var3]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public Object clone() {
      if (!this.sizeIsSticky) {
         this.trimToSize();
      }

      try {
         BitSet var1 = (BitSet)super.clone();
         var1.words = (long[])this.words.clone();
         var1.checkInvariants();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   private void trimToSize() {
      if (this.wordsInUse != this.words.length) {
         this.words = Arrays.copyOf(this.words, this.wordsInUse);
         this.checkInvariants();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.checkInvariants();
      if (!this.sizeIsSticky) {
         this.trimToSize();
      }

      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("bits", this.words);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.words = (long[])((long[])var2.get("bits", (Object)null));
      this.wordsInUse = this.words.length;
      this.recalculateWordsInUse();
      this.sizeIsSticky = this.words.length > 0 && this.words[this.words.length - 1] == 0L;
      this.checkInvariants();
   }

   public String toString() {
      this.checkInvariants();
      int var1 = this.wordsInUse > 128 ? this.cardinality() : this.wordsInUse * 64;
      StringBuilder var2 = new StringBuilder(6 * var1 + 2);
      var2.append('{');
      int var3 = this.nextSetBit(0);
      if (var3 != -1) {
         var2.append(var3);

         while(true) {
            ++var3;
            if (var3 < 0 || (var3 = this.nextSetBit(var3)) < 0) {
               break;
            }

            int var4 = this.nextClearBit(var3);

            while(true) {
               var2.append(", ").append(var3);
               ++var3;
               if (var3 == var4) {
                  break;
               }
            }
         }
      }

      var2.append('}');
      return var2.toString();
   }

   public IntStream stream() {
      return StreamSupport.intStream(() -> {
         class BitSetIterator implements PrimitiveIterator.OfInt {
            int next = BitSet.this.nextSetBit(0);

            public boolean hasNext() {
               return this.next != -1;
            }

            public int nextInt() {
               if (this.next != -1) {
                  int var1 = this.next;
                  this.next = BitSet.this.nextSetBit(this.next + 1);
                  return var1;
               } else {
                  throw new NoSuchElementException();
               }
            }
         }

         return Spliterators.spliterator((PrimitiveIterator.OfInt)(new BitSetIterator()), (long)this.cardinality(), 21);
      }, 16469, false);
   }
}
