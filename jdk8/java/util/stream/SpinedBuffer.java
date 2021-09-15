package java.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

class SpinedBuffer<E> extends AbstractSpinedBuffer implements Consumer<E>, Iterable<E> {
   protected E[] curChunk;
   protected E[][] spine;
   private static final int SPLITERATOR_CHARACTERISTICS = 16464;

   SpinedBuffer(int var1) {
      super(var1);
      this.curChunk = (Object[])(new Object[1 << this.initialChunkPower]);
   }

   SpinedBuffer() {
      this.curChunk = (Object[])(new Object[1 << this.initialChunkPower]);
   }

   protected long capacity() {
      return this.spineIndex == 0 ? (long)this.curChunk.length : this.priorElementCount[this.spineIndex] + (long)this.spine[this.spineIndex].length;
   }

   private void inflateSpine() {
      if (this.spine == null) {
         this.spine = (Object[][])(new Object[8][]);
         this.priorElementCount = new long[8];
         this.spine[0] = this.curChunk;
      }

   }

   protected final void ensureCapacity(long var1) {
      long var3 = this.capacity();
      if (var1 > var3) {
         this.inflateSpine();

         for(int var5 = this.spineIndex + 1; var1 > var3; ++var5) {
            int var6;
            if (var5 >= this.spine.length) {
               var6 = this.spine.length * 2;
               this.spine = (Object[][])Arrays.copyOf((Object[])this.spine, var6);
               this.priorElementCount = Arrays.copyOf(this.priorElementCount, var6);
            }

            var6 = this.chunkSize(var5);
            this.spine[var5] = (Object[])(new Object[var6]);
            this.priorElementCount[var5] = this.priorElementCount[var5 - 1] + (long)this.spine[var5 - 1].length;
            var3 += (long)var6;
         }
      }

   }

   protected void increaseCapacity() {
      this.ensureCapacity(this.capacity() + 1L);
   }

   public E get(long var1) {
      if (this.spineIndex == 0) {
         if (var1 < (long)this.elementIndex) {
            return this.curChunk[(int)var1];
         } else {
            throw new IndexOutOfBoundsException(Long.toString(var1));
         }
      } else if (var1 >= this.count()) {
         throw new IndexOutOfBoundsException(Long.toString(var1));
      } else {
         for(int var3 = 0; var3 <= this.spineIndex; ++var3) {
            if (var1 < this.priorElementCount[var3] + (long)this.spine[var3].length) {
               return this.spine[var3][(int)(var1 - this.priorElementCount[var3])];
            }
         }

         throw new IndexOutOfBoundsException(Long.toString(var1));
      }
   }

   public void copyInto(E[] var1, int var2) {
      long var3 = (long)var2 + this.count();
      if (var3 <= (long)var1.length && var3 >= (long)var2) {
         if (this.spineIndex == 0) {
            System.arraycopy(this.curChunk, 0, var1, var2, this.elementIndex);
         } else {
            for(int var5 = 0; var5 < this.spineIndex; ++var5) {
               System.arraycopy(this.spine[var5], 0, var1, var2, this.spine[var5].length);
               var2 += this.spine[var5].length;
            }

            if (this.elementIndex > 0) {
               System.arraycopy(this.curChunk, 0, var1, var2, this.elementIndex);
            }
         }

      } else {
         throw new IndexOutOfBoundsException("does not fit");
      }
   }

   public E[] asArray(IntFunction<E[]> var1) {
      long var2 = this.count();
      if (var2 >= 2147483639L) {
         throw new IllegalArgumentException("Stream size exceeds max array size");
      } else {
         Object[] var4 = (Object[])var1.apply((int)var2);
         this.copyInto(var4, 0);
         return var4;
      }
   }

   public void clear() {
      int var1;
      if (this.spine != null) {
         this.curChunk = this.spine[0];

         for(var1 = 0; var1 < this.curChunk.length; ++var1) {
            this.curChunk[var1] = null;
         }

         this.spine = (Object[][])null;
         this.priorElementCount = null;
      } else {
         for(var1 = 0; var1 < this.elementIndex; ++var1) {
            this.curChunk[var1] = null;
         }
      }

      this.elementIndex = 0;
      this.spineIndex = 0;
   }

   public Iterator<E> iterator() {
      return Spliterators.iterator(this.spliterator());
   }

   public void forEach(Consumer<? super E> var1) {
      int var2;
      for(var2 = 0; var2 < this.spineIndex; ++var2) {
         Object[] var3 = this.spine[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object var6 = var3[var5];
            var1.accept(var6);
         }
      }

      for(var2 = 0; var2 < this.elementIndex; ++var2) {
         var1.accept(this.curChunk[var2]);
      }

   }

   public void accept(E var1) {
      if (this.elementIndex == this.curChunk.length) {
         this.inflateSpine();
         if (this.spineIndex + 1 >= this.spine.length || this.spine[this.spineIndex + 1] == null) {
            this.increaseCapacity();
         }

         this.elementIndex = 0;
         ++this.spineIndex;
         this.curChunk = this.spine[this.spineIndex];
      }

      this.curChunk[this.elementIndex++] = var1;
   }

   public String toString() {
      ArrayList var1 = new ArrayList();
      this.forEach(var1::add);
      return "SpinedBuffer:" + var1.toString();
   }

   public Spliterator<E> spliterator() {
      class Splitr implements Spliterator<E> {
         int splSpineIndex;
         final int lastSpineIndex;
         int splElementIndex;
         final int lastSpineElementFence;
         E[] splChunk;

         Splitr(int var2, int var3, int var4, int var5) {
            this.splSpineIndex = var2;
            this.lastSpineIndex = var3;
            this.splElementIndex = var4;
            this.lastSpineElementFence = var5;

            assert SpinedBuffer.this.spine != null || var2 == 0 && var3 == 0;

            this.splChunk = SpinedBuffer.this.spine == null ? SpinedBuffer.this.curChunk : SpinedBuffer.this.spine[var2];
         }

         public long estimateSize() {
            return this.splSpineIndex == this.lastSpineIndex ? (long)this.lastSpineElementFence - (long)this.splElementIndex : SpinedBuffer.this.priorElementCount[this.lastSpineIndex] + (long)this.lastSpineElementFence - SpinedBuffer.this.priorElementCount[this.splSpineIndex] - (long)this.splElementIndex;
         }

         public int characteristics() {
            return 16464;
         }

         public boolean tryAdvance(Consumer<? super E> var1) {
            Objects.requireNonNull(var1);
            if (this.splSpineIndex >= this.lastSpineIndex && (this.splSpineIndex != this.lastSpineIndex || this.splElementIndex >= this.lastSpineElementFence)) {
               return false;
            } else {
               var1.accept(this.splChunk[this.splElementIndex++]);
               if (this.splElementIndex == this.splChunk.length) {
                  this.splElementIndex = 0;
                  ++this.splSpineIndex;
                  if (SpinedBuffer.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                     this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
                  }
               }

               return true;
            }
         }

         public void forEachRemaining(Consumer<? super E> var1) {
            Objects.requireNonNull(var1);
            if (this.splSpineIndex < this.lastSpineIndex || this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence) {
               int var2 = this.splElementIndex;

               for(int var3 = this.splSpineIndex; var3 < this.lastSpineIndex; ++var3) {
                  for(Object[] var4 = SpinedBuffer.this.spine[var3]; var2 < var4.length; ++var2) {
                     var1.accept(var4[var2]);
                  }

                  var2 = 0;
               }

               Object[] var5 = this.splSpineIndex == this.lastSpineIndex ? this.splChunk : SpinedBuffer.this.spine[this.lastSpineIndex];

               for(int var6 = this.lastSpineElementFence; var2 < var6; ++var2) {
                  var1.accept(var5[var2]);
               }

               this.splSpineIndex = this.lastSpineIndex;
               this.splElementIndex = this.lastSpineElementFence;
            }

         }

         public Spliterator<E> trySplit() {
            if (this.splSpineIndex < this.lastSpineIndex) {
               Splitr var3 = new Splitr(this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, SpinedBuffer.this.spine[this.lastSpineIndex - 1].length);
               this.splSpineIndex = this.lastSpineIndex;
               this.splElementIndex = 0;
               this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
               return var3;
            } else if (this.splSpineIndex == this.lastSpineIndex) {
               int var1 = (this.lastSpineElementFence - this.splElementIndex) / 2;
               if (var1 == 0) {
                  return null;
               } else {
                  Spliterator var2 = Arrays.spliterator(this.splChunk, this.splElementIndex, this.splElementIndex + var1);
                  this.splElementIndex += var1;
                  return var2;
               }
            } else {
               return null;
            }
         }
      }

      return new Splitr(0, this.spineIndex, 0, this.elementIndex);
   }

   static class OfDouble extends SpinedBuffer.OfPrimitive<Double, double[], DoubleConsumer> implements DoubleConsumer {
      OfDouble() {
      }

      OfDouble(int var1) {
         super(var1);
      }

      public void forEach(Consumer<? super Double> var1) {
         if (var1 instanceof DoubleConsumer) {
            this.forEach((DoubleConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            }

            this.spliterator().forEachRemaining(var1);
         }

      }

      protected double[][] newArrayArray(int var1) {
         return new double[var1][];
      }

      public double[] newArray(int var1) {
         return new double[var1];
      }

      protected int arrayLength(double[] var1) {
         return var1.length;
      }

      protected void arrayForEach(double[] var1, int var2, int var3, DoubleConsumer var4) {
         for(int var5 = var2; var5 < var3; ++var5) {
            var4.accept(var1[var5]);
         }

      }

      public void accept(double var1) {
         this.preAccept();
         ((double[])this.curChunk)[this.elementIndex++] = var1;
      }

      public double get(long var1) {
         int var3 = this.chunkFor(var1);
         return this.spineIndex == 0 && var3 == 0 ? ((double[])this.curChunk)[(int)var1] : ((double[][])this.spine)[var3][(int)(var1 - this.priorElementCount[var3])];
      }

      public PrimitiveIterator.OfDouble iterator() {
         return Spliterators.iterator(this.spliterator());
      }

      public Spliterator.OfDouble spliterator() {
         class Splitr extends SpinedBuffer.OfPrimitive<Double, double[], DoubleConsumer>.BaseSpliterator<Spliterator.OfDouble> implements Spliterator.OfDouble {
            Splitr(int var2, int var3, int var4, int var5) {
               super(var2, var3, var4, var5);
            }

            Splitr newSpliterator(int var1, int var2, int var3, int var4) {
               return new Splitr(var1, var2, var3, var4);
            }

            void arrayForOne(double[] var1, int var2, DoubleConsumer var3) {
               var3.accept(var1[var2]);
            }

            Spliterator.OfDouble arraySpliterator(double[] var1, int var2, int var3) {
               return Arrays.spliterator(var1, var2, var2 + var3);
            }
         }

         return new Splitr(0, this.spineIndex, 0, this.elementIndex);
      }

      public String toString() {
         double[] var1 = (double[])this.asPrimitiveArray();
         if (var1.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var1));
         } else {
            double[] var2 = Arrays.copyOf((double[])var1, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var2));
         }
      }
   }

   static class OfLong extends SpinedBuffer.OfPrimitive<Long, long[], LongConsumer> implements LongConsumer {
      OfLong() {
      }

      OfLong(int var1) {
         super(var1);
      }

      public void forEach(Consumer<? super Long> var1) {
         if (var1 instanceof LongConsumer) {
            this.forEach((LongConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            }

            this.spliterator().forEachRemaining(var1);
         }

      }

      protected long[][] newArrayArray(int var1) {
         return new long[var1][];
      }

      public long[] newArray(int var1) {
         return new long[var1];
      }

      protected int arrayLength(long[] var1) {
         return var1.length;
      }

      protected void arrayForEach(long[] var1, int var2, int var3, LongConsumer var4) {
         for(int var5 = var2; var5 < var3; ++var5) {
            var4.accept(var1[var5]);
         }

      }

      public void accept(long var1) {
         this.preAccept();
         ((long[])this.curChunk)[this.elementIndex++] = var1;
      }

      public long get(long var1) {
         int var3 = this.chunkFor(var1);
         return this.spineIndex == 0 && var3 == 0 ? ((long[])this.curChunk)[(int)var1] : ((long[][])this.spine)[var3][(int)(var1 - this.priorElementCount[var3])];
      }

      public PrimitiveIterator.OfLong iterator() {
         return Spliterators.iterator(this.spliterator());
      }

      public Spliterator.OfLong spliterator() {
         class Splitr extends SpinedBuffer.OfPrimitive<Long, long[], LongConsumer>.BaseSpliterator<Spliterator.OfLong> implements Spliterator.OfLong {
            Splitr(int var2, int var3, int var4, int var5) {
               super(var2, var3, var4, var5);
            }

            Splitr newSpliterator(int var1, int var2, int var3, int var4) {
               return new Splitr(var1, var2, var3, var4);
            }

            void arrayForOne(long[] var1, int var2, LongConsumer var3) {
               var3.accept(var1[var2]);
            }

            Spliterator.OfLong arraySpliterator(long[] var1, int var2, int var3) {
               return Arrays.spliterator(var1, var2, var2 + var3);
            }
         }

         return new Splitr(0, this.spineIndex, 0, this.elementIndex);
      }

      public String toString() {
         long[] var1 = (long[])this.asPrimitiveArray();
         if (var1.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var1));
         } else {
            long[] var2 = Arrays.copyOf((long[])var1, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var2));
         }
      }
   }

   static class OfInt extends SpinedBuffer.OfPrimitive<Integer, int[], IntConsumer> implements IntConsumer {
      OfInt() {
      }

      OfInt(int var1) {
         super(var1);
      }

      public void forEach(Consumer<? super Integer> var1) {
         if (var1 instanceof IntConsumer) {
            this.forEach((IntConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            }

            this.spliterator().forEachRemaining(var1);
         }

      }

      protected int[][] newArrayArray(int var1) {
         return new int[var1][];
      }

      public int[] newArray(int var1) {
         return new int[var1];
      }

      protected int arrayLength(int[] var1) {
         return var1.length;
      }

      protected void arrayForEach(int[] var1, int var2, int var3, IntConsumer var4) {
         for(int var5 = var2; var5 < var3; ++var5) {
            var4.accept(var1[var5]);
         }

      }

      public void accept(int var1) {
         this.preAccept();
         ((int[])this.curChunk)[this.elementIndex++] = var1;
      }

      public int get(long var1) {
         int var3 = this.chunkFor(var1);
         return this.spineIndex == 0 && var3 == 0 ? ((int[])this.curChunk)[(int)var1] : ((int[][])this.spine)[var3][(int)(var1 - this.priorElementCount[var3])];
      }

      public PrimitiveIterator.OfInt iterator() {
         return Spliterators.iterator(this.spliterator());
      }

      public Spliterator.OfInt spliterator() {
         class Splitr extends SpinedBuffer.OfPrimitive<Integer, int[], IntConsumer>.BaseSpliterator<Spliterator.OfInt> implements Spliterator.OfInt {
            Splitr(int var2, int var3, int var4, int var5) {
               super(var2, var3, var4, var5);
            }

            Splitr newSpliterator(int var1, int var2, int var3, int var4) {
               return new Splitr(var1, var2, var3, var4);
            }

            void arrayForOne(int[] var1, int var2, IntConsumer var3) {
               var3.accept(var1[var2]);
            }

            Spliterator.OfInt arraySpliterator(int[] var1, int var2, int var3) {
               return Arrays.spliterator(var1, var2, var2 + var3);
            }
         }

         return new Splitr(0, this.spineIndex, 0, this.elementIndex);
      }

      public String toString() {
         int[] var1 = (int[])this.asPrimitiveArray();
         if (var1.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var1));
         } else {
            int[] var2 = Arrays.copyOf((int[])var1, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", this.getClass().getSimpleName(), var1.length, this.spineIndex, Arrays.toString(var2));
         }
      }
   }

   abstract static class OfPrimitive<E, T_ARR, T_CONS> extends AbstractSpinedBuffer implements Iterable<E> {
      T_ARR curChunk;
      T_ARR[] spine;

      OfPrimitive(int var1) {
         super(var1);
         this.curChunk = this.newArray(1 << this.initialChunkPower);
      }

      OfPrimitive() {
         this.curChunk = this.newArray(1 << this.initialChunkPower);
      }

      public abstract Iterator<E> iterator();

      public abstract void forEach(Consumer<? super E> var1);

      protected abstract T_ARR[] newArrayArray(int var1);

      public abstract T_ARR newArray(int var1);

      protected abstract int arrayLength(T_ARR var1);

      protected abstract void arrayForEach(T_ARR var1, int var2, int var3, T_CONS var4);

      protected long capacity() {
         return this.spineIndex == 0 ? (long)this.arrayLength(this.curChunk) : this.priorElementCount[this.spineIndex] + (long)this.arrayLength(this.spine[this.spineIndex]);
      }

      private void inflateSpine() {
         if (this.spine == null) {
            this.spine = this.newArrayArray(8);
            this.priorElementCount = new long[8];
            this.spine[0] = this.curChunk;
         }

      }

      protected final void ensureCapacity(long var1) {
         long var3 = this.capacity();
         if (var1 > var3) {
            this.inflateSpine();

            for(int var5 = this.spineIndex + 1; var1 > var3; ++var5) {
               int var6;
               if (var5 >= this.spine.length) {
                  var6 = this.spine.length * 2;
                  this.spine = Arrays.copyOf(this.spine, var6);
                  this.priorElementCount = Arrays.copyOf(this.priorElementCount, var6);
               }

               var6 = this.chunkSize(var5);
               this.spine[var5] = this.newArray(var6);
               this.priorElementCount[var5] = this.priorElementCount[var5 - 1] + (long)this.arrayLength(this.spine[var5 - 1]);
               var3 += (long)var6;
            }
         }

      }

      protected void increaseCapacity() {
         this.ensureCapacity(this.capacity() + 1L);
      }

      protected int chunkFor(long var1) {
         if (this.spineIndex == 0) {
            if (var1 < (long)this.elementIndex) {
               return 0;
            } else {
               throw new IndexOutOfBoundsException(Long.toString(var1));
            }
         } else if (var1 >= this.count()) {
            throw new IndexOutOfBoundsException(Long.toString(var1));
         } else {
            for(int var3 = 0; var3 <= this.spineIndex; ++var3) {
               if (var1 < this.priorElementCount[var3] + (long)this.arrayLength(this.spine[var3])) {
                  return var3;
               }
            }

            throw new IndexOutOfBoundsException(Long.toString(var1));
         }
      }

      public void copyInto(T_ARR var1, int var2) {
         long var3 = (long)var2 + this.count();
         if (var3 <= (long)this.arrayLength(var1) && var3 >= (long)var2) {
            if (this.spineIndex == 0) {
               System.arraycopy(this.curChunk, 0, var1, var2, this.elementIndex);
            } else {
               for(int var5 = 0; var5 < this.spineIndex; ++var5) {
                  System.arraycopy(this.spine[var5], 0, var1, var2, this.arrayLength(this.spine[var5]));
                  var2 += this.arrayLength(this.spine[var5]);
               }

               if (this.elementIndex > 0) {
                  System.arraycopy(this.curChunk, 0, var1, var2, this.elementIndex);
               }
            }

         } else {
            throw new IndexOutOfBoundsException("does not fit");
         }
      }

      public T_ARR asPrimitiveArray() {
         long var1 = this.count();
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            Object var3 = this.newArray((int)var1);
            this.copyInto(var3, 0);
            return var3;
         }
      }

      protected void preAccept() {
         if (this.elementIndex == this.arrayLength(this.curChunk)) {
            this.inflateSpine();
            if (this.spineIndex + 1 >= this.spine.length || this.spine[this.spineIndex + 1] == null) {
               this.increaseCapacity();
            }

            this.elementIndex = 0;
            ++this.spineIndex;
            this.curChunk = this.spine[this.spineIndex];
         }

      }

      public void clear() {
         if (this.spine != null) {
            this.curChunk = this.spine[0];
            this.spine = null;
            this.priorElementCount = null;
         }

         this.elementIndex = 0;
         this.spineIndex = 0;
      }

      public void forEach(T_CONS var1) {
         for(int var2 = 0; var2 < this.spineIndex; ++var2) {
            this.arrayForEach(this.spine[var2], 0, this.arrayLength(this.spine[var2]), var1);
         }

         this.arrayForEach(this.curChunk, 0, this.elementIndex, var1);
      }

      abstract class BaseSpliterator<T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>> implements Spliterator.OfPrimitive<E, T_CONS, T_SPLITR> {
         int splSpineIndex;
         final int lastSpineIndex;
         int splElementIndex;
         final int lastSpineElementFence;
         T_ARR splChunk;

         BaseSpliterator(int var2, int var3, int var4, int var5) {
            this.splSpineIndex = var2;
            this.lastSpineIndex = var3;
            this.splElementIndex = var4;
            this.lastSpineElementFence = var5;

            assert OfPrimitive.this.spine != null || var2 == 0 && var3 == 0;

            this.splChunk = OfPrimitive.this.spine == null ? OfPrimitive.this.curChunk : OfPrimitive.this.spine[var2];
         }

         abstract T_SPLITR newSpliterator(int var1, int var2, int var3, int var4);

         abstract void arrayForOne(T_ARR var1, int var2, T_CONS var3);

         abstract T_SPLITR arraySpliterator(T_ARR var1, int var2, int var3);

         public long estimateSize() {
            return this.splSpineIndex == this.lastSpineIndex ? (long)this.lastSpineElementFence - (long)this.splElementIndex : OfPrimitive.this.priorElementCount[this.lastSpineIndex] + (long)this.lastSpineElementFence - OfPrimitive.this.priorElementCount[this.splSpineIndex] - (long)this.splElementIndex;
         }

         public int characteristics() {
            return 16464;
         }

         public boolean tryAdvance(T_CONS var1) {
            Objects.requireNonNull(var1);
            if (this.splSpineIndex >= this.lastSpineIndex && (this.splSpineIndex != this.lastSpineIndex || this.splElementIndex >= this.lastSpineElementFence)) {
               return false;
            } else {
               this.arrayForOne(this.splChunk, this.splElementIndex++, var1);
               if (this.splElementIndex == OfPrimitive.this.arrayLength(this.splChunk)) {
                  this.splElementIndex = 0;
                  ++this.splSpineIndex;
                  if (OfPrimitive.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                     this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
                  }
               }

               return true;
            }
         }

         public void forEachRemaining(T_CONS var1) {
            Objects.requireNonNull(var1);
            if (this.splSpineIndex < this.lastSpineIndex || this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence) {
               int var2 = this.splElementIndex;

               for(int var3 = this.splSpineIndex; var3 < this.lastSpineIndex; ++var3) {
                  Object var4 = OfPrimitive.this.spine[var3];
                  OfPrimitive.this.arrayForEach(var4, var2, OfPrimitive.this.arrayLength(var4), var1);
                  var2 = 0;
               }

               Object var5 = this.splSpineIndex == this.lastSpineIndex ? this.splChunk : OfPrimitive.this.spine[this.lastSpineIndex];
               OfPrimitive.this.arrayForEach(var5, var2, this.lastSpineElementFence, var1);
               this.splSpineIndex = this.lastSpineIndex;
               this.splElementIndex = this.lastSpineElementFence;
            }

         }

         public T_SPLITR trySplit() {
            if (this.splSpineIndex < this.lastSpineIndex) {
               Spliterator.OfPrimitive var3 = this.newSpliterator(this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, OfPrimitive.this.arrayLength(OfPrimitive.this.spine[this.lastSpineIndex - 1]));
               this.splSpineIndex = this.lastSpineIndex;
               this.splElementIndex = 0;
               this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
               return var3;
            } else if (this.splSpineIndex == this.lastSpineIndex) {
               int var1 = (this.lastSpineElementFence - this.splElementIndex) / 2;
               if (var1 == 0) {
                  return null;
               } else {
                  Spliterator.OfPrimitive var2 = this.arraySpliterator(this.splChunk, this.splElementIndex, var1);
                  this.splElementIndex += var1;
                  return var2;
               }
            } else {
               return null;
            }
         }
      }
   }
}
