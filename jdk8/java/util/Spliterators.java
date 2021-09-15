package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class Spliterators {
   private static final Spliterator<Object> EMPTY_SPLITERATOR = new Spliterators.EmptySpliterator.OfRef();
   private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new Spliterators.EmptySpliterator.OfInt();
   private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new Spliterators.EmptySpliterator.OfLong();
   private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new Spliterators.EmptySpliterator.OfDouble();

   private Spliterators() {
   }

   public static <T> Spliterator<T> emptySpliterator() {
      return EMPTY_SPLITERATOR;
   }

   public static Spliterator.OfInt emptyIntSpliterator() {
      return EMPTY_INT_SPLITERATOR;
   }

   public static Spliterator.OfLong emptyLongSpliterator() {
      return EMPTY_LONG_SPLITERATOR;
   }

   public static Spliterator.OfDouble emptyDoubleSpliterator() {
      return EMPTY_DOUBLE_SPLITERATOR;
   }

   public static <T> Spliterator<T> spliterator(Object[] var0, int var1) {
      return new Spliterators.ArraySpliterator((Object[])Objects.requireNonNull(var0), var1);
   }

   public static <T> Spliterator<T> spliterator(Object[] var0, int var1, int var2, int var3) {
      checkFromToBounds(((Object[])Objects.requireNonNull(var0)).length, var1, var2);
      return new Spliterators.ArraySpliterator(var0, var1, var2, var3);
   }

   public static Spliterator.OfInt spliterator(int[] var0, int var1) {
      return new Spliterators.IntArraySpliterator((int[])Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfInt spliterator(int[] var0, int var1, int var2, int var3) {
      checkFromToBounds(((int[])Objects.requireNonNull(var0)).length, var1, var2);
      return new Spliterators.IntArraySpliterator(var0, var1, var2, var3);
   }

   public static Spliterator.OfLong spliterator(long[] var0, int var1) {
      return new Spliterators.LongArraySpliterator((long[])Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfLong spliterator(long[] var0, int var1, int var2, int var3) {
      checkFromToBounds(((long[])Objects.requireNonNull(var0)).length, var1, var2);
      return new Spliterators.LongArraySpliterator(var0, var1, var2, var3);
   }

   public static Spliterator.OfDouble spliterator(double[] var0, int var1) {
      return new Spliterators.DoubleArraySpliterator((double[])Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfDouble spliterator(double[] var0, int var1, int var2, int var3) {
      checkFromToBounds(((double[])Objects.requireNonNull(var0)).length, var1, var2);
      return new Spliterators.DoubleArraySpliterator(var0, var1, var2, var3);
   }

   private static void checkFromToBounds(int var0, int var1, int var2) {
      if (var1 > var2) {
         throw new ArrayIndexOutOfBoundsException("origin(" + var1 + ") > fence(" + var2 + ")");
      } else if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var2 > var0) {
         throw new ArrayIndexOutOfBoundsException(var2);
      }
   }

   public static <T> Spliterator<T> spliterator(Collection<? extends T> var0, int var1) {
      return new Spliterators.IteratorSpliterator((Collection)Objects.requireNonNull(var0), var1);
   }

   public static <T> Spliterator<T> spliterator(Iterator<? extends T> var0, long var1, int var3) {
      return new Spliterators.IteratorSpliterator((Iterator)Objects.requireNonNull(var0), var1, var3);
   }

   public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> var0, int var1) {
      return new Spliterators.IteratorSpliterator((Iterator)Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt var0, long var1, int var3) {
      return new Spliterators.IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(var0), var1, var3);
   }

   public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt var0, int var1) {
      return new Spliterators.IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong var0, long var1, int var3) {
      return new Spliterators.LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(var0), var1, var3);
   }

   public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong var0, int var1) {
      return new Spliterators.LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(var0), var1);
   }

   public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble var0, long var1, int var3) {
      return new Spliterators.DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(var0), var1, var3);
   }

   public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble var0, int var1) {
      return new Spliterators.DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(var0), var1);
   }

   public static <T> Iterator<T> iterator(final Spliterator<? extends T> var0) {
      Objects.requireNonNull(var0);

      class Adapter implements Iterator<T>, Consumer<T> {
         boolean valueReady = false;
         T nextElement;

         public void accept(T var1) {
            this.valueReady = true;
            this.nextElement = var1;
         }

         public boolean hasNext() {
            if (!this.valueReady) {
               var0.tryAdvance(this);
            }

            return this.valueReady;
         }

         public T next() {
            if (!this.valueReady && !this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.valueReady = false;
               return this.nextElement;
            }
         }
      }

      return new Adapter();
   }

   public static PrimitiveIterator.OfInt iterator(final Spliterator.OfInt var0) {
      Objects.requireNonNull(var0);

      class Adapter implements PrimitiveIterator.OfInt, IntConsumer {
         boolean valueReady = false;
         int nextElement;

         public void accept(int var1) {
            this.valueReady = true;
            this.nextElement = var1;
         }

         public boolean hasNext() {
            if (!this.valueReady) {
               var0.tryAdvance((IntConsumer)this);
            }

            return this.valueReady;
         }

         public int nextInt() {
            if (!this.valueReady && !this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.valueReady = false;
               return this.nextElement;
            }
         }
      }

      return new Adapter();
   }

   public static PrimitiveIterator.OfLong iterator(final Spliterator.OfLong var0) {
      Objects.requireNonNull(var0);

      class Adapter implements PrimitiveIterator.OfLong, LongConsumer {
         boolean valueReady = false;
         long nextElement;

         public void accept(long var1) {
            this.valueReady = true;
            this.nextElement = var1;
         }

         public boolean hasNext() {
            if (!this.valueReady) {
               var0.tryAdvance((LongConsumer)this);
            }

            return this.valueReady;
         }

         public long nextLong() {
            if (!this.valueReady && !this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.valueReady = false;
               return this.nextElement;
            }
         }
      }

      return new Adapter();
   }

   public static PrimitiveIterator.OfDouble iterator(final Spliterator.OfDouble var0) {
      Objects.requireNonNull(var0);

      class Adapter implements PrimitiveIterator.OfDouble, DoubleConsumer {
         boolean valueReady = false;
         double nextElement;

         public void accept(double var1) {
            this.valueReady = true;
            this.nextElement = var1;
         }

         public boolean hasNext() {
            if (!this.valueReady) {
               var0.tryAdvance((DoubleConsumer)this);
            }

            return this.valueReady;
         }

         public double nextDouble() {
            if (!this.valueReady && !this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.valueReady = false;
               return this.nextElement;
            }
         }
      }

      return new Adapter();
   }

   static final class DoubleIteratorSpliterator implements Spliterator.OfDouble {
      static final int BATCH_UNIT = 1024;
      static final int MAX_BATCH = 33554432;
      private PrimitiveIterator.OfDouble it;
      private final int characteristics;
      private long est;
      private int batch;

      public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble var1, long var2, int var4) {
         this.it = var1;
         this.est = var2;
         this.characteristics = (var4 & 4096) == 0 ? var4 | 64 | 16384 : var4;
      }

      public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble var1, int var2) {
         this.it = var1;
         this.est = Long.MAX_VALUE;
         this.characteristics = var2 & -16449;
      }

      public Spliterator.OfDouble trySplit() {
         PrimitiveIterator.OfDouble var1 = this.it;
         long var2 = this.est;
         if (var2 > 1L && var1.hasNext()) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            double[] var5 = new double[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.nextDouble();
               ++var6;
            } while(var6 < var4 && var1.hasNext());

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.DoubleArraySpliterator(var5, 0, var6, this.characteristics);
         } else {
            return null;
         }
      }

      public void forEachRemaining(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.it.forEachRemaining(var1);
         }
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.it.hasNext()) {
            var1.accept(this.it.nextDouble());
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Double> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static final class LongIteratorSpliterator implements Spliterator.OfLong {
      static final int BATCH_UNIT = 1024;
      static final int MAX_BATCH = 33554432;
      private PrimitiveIterator.OfLong it;
      private final int characteristics;
      private long est;
      private int batch;

      public LongIteratorSpliterator(PrimitiveIterator.OfLong var1, long var2, int var4) {
         this.it = var1;
         this.est = var2;
         this.characteristics = (var4 & 4096) == 0 ? var4 | 64 | 16384 : var4;
      }

      public LongIteratorSpliterator(PrimitiveIterator.OfLong var1, int var2) {
         this.it = var1;
         this.est = Long.MAX_VALUE;
         this.characteristics = var2 & -16449;
      }

      public Spliterator.OfLong trySplit() {
         PrimitiveIterator.OfLong var1 = this.it;
         long var2 = this.est;
         if (var2 > 1L && var1.hasNext()) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            long[] var5 = new long[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.nextLong();
               ++var6;
            } while(var6 < var4 && var1.hasNext());

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.LongArraySpliterator(var5, 0, var6, this.characteristics);
         } else {
            return null;
         }
      }

      public void forEachRemaining(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.it.forEachRemaining(var1);
         }
      }

      public boolean tryAdvance(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.it.hasNext()) {
            var1.accept(this.it.nextLong());
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Long> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static final class IntIteratorSpliterator implements Spliterator.OfInt {
      static final int BATCH_UNIT = 1024;
      static final int MAX_BATCH = 33554432;
      private PrimitiveIterator.OfInt it;
      private final int characteristics;
      private long est;
      private int batch;

      public IntIteratorSpliterator(PrimitiveIterator.OfInt var1, long var2, int var4) {
         this.it = var1;
         this.est = var2;
         this.characteristics = (var4 & 4096) == 0 ? var4 | 64 | 16384 : var4;
      }

      public IntIteratorSpliterator(PrimitiveIterator.OfInt var1, int var2) {
         this.it = var1;
         this.est = Long.MAX_VALUE;
         this.characteristics = var2 & -16449;
      }

      public Spliterator.OfInt trySplit() {
         PrimitiveIterator.OfInt var1 = this.it;
         long var2 = this.est;
         if (var2 > 1L && var1.hasNext()) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            int[] var5 = new int[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.nextInt();
               ++var6;
            } while(var6 < var4 && var1.hasNext());

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.IntArraySpliterator(var5, 0, var6, this.characteristics);
         } else {
            return null;
         }
      }

      public void forEachRemaining(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.it.forEachRemaining(var1);
         }
      }

      public boolean tryAdvance(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.it.hasNext()) {
            var1.accept(this.it.nextInt());
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Integer> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static class IteratorSpliterator<T> implements Spliterator<T> {
      static final int BATCH_UNIT = 1024;
      static final int MAX_BATCH = 33554432;
      private final Collection<? extends T> collection;
      private Iterator<? extends T> it;
      private final int characteristics;
      private long est;
      private int batch;

      public IteratorSpliterator(Collection<? extends T> var1, int var2) {
         this.collection = var1;
         this.it = null;
         this.characteristics = (var2 & 4096) == 0 ? var2 | 64 | 16384 : var2;
      }

      public IteratorSpliterator(Iterator<? extends T> var1, long var2, int var4) {
         this.collection = null;
         this.it = var1;
         this.est = var2;
         this.characteristics = (var4 & 4096) == 0 ? var4 | 64 | 16384 : var4;
      }

      public IteratorSpliterator(Iterator<? extends T> var1, int var2) {
         this.collection = null;
         this.it = var1;
         this.est = Long.MAX_VALUE;
         this.characteristics = var2 & -16449;
      }

      public Spliterator<T> trySplit() {
         Iterator var1;
         long var2;
         if ((var1 = this.it) == null) {
            var1 = this.it = this.collection.iterator();
            var2 = this.est = (long)this.collection.size();
         } else {
            var2 = this.est;
         }

         if (var2 > 1L && var1.hasNext()) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            Object[] var5 = new Object[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.next();
               ++var6;
            } while(var6 < var4 && var1.hasNext());

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.ArraySpliterator(var5, 0, var6, this.characteristics);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Iterator var2;
            if ((var2 = this.it) == null) {
               var2 = this.it = this.collection.iterator();
               this.est = (long)this.collection.size();
            }

            var2.forEachRemaining(var1);
         }
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            if (this.it == null) {
               this.it = this.collection.iterator();
               this.est = (long)this.collection.size();
            }

            if (this.it.hasNext()) {
               var1.accept(this.it.next());
               return true;
            } else {
               return false;
            }
         }
      }

      public long estimateSize() {
         if (this.it == null) {
            this.it = this.collection.iterator();
            return this.est = (long)this.collection.size();
         } else {
            return this.est;
         }
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super T> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   public abstract static class AbstractDoubleSpliterator implements Spliterator.OfDouble {
      static final int MAX_BATCH = 33554432;
      static final int BATCH_UNIT = 1024;
      private final int characteristics;
      private long est;
      private int batch;

      protected AbstractDoubleSpliterator(long var1, int var3) {
         this.est = var1;
         this.characteristics = (var3 & 64) != 0 ? var3 | 16384 : var3;
      }

      public Spliterator.OfDouble trySplit() {
         Spliterators.AbstractDoubleSpliterator.HoldingDoubleConsumer var1 = new Spliterators.AbstractDoubleSpliterator.HoldingDoubleConsumer();
         long var2 = this.est;
         if (var2 > 1L && this.tryAdvance(var1)) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            double[] var5 = new double[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.value;
               ++var6;
            } while(var6 < var4 && this.tryAdvance(var1));

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.DoubleArraySpliterator(var5, 0, var6, this.characteristics());
         } else {
            return null;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      static final class HoldingDoubleConsumer implements DoubleConsumer {
         double value;

         public void accept(double var1) {
            this.value = var1;
         }
      }
   }

   public abstract static class AbstractLongSpliterator implements Spliterator.OfLong {
      static final int MAX_BATCH = 33554432;
      static final int BATCH_UNIT = 1024;
      private final int characteristics;
      private long est;
      private int batch;

      protected AbstractLongSpliterator(long var1, int var3) {
         this.est = var1;
         this.characteristics = (var3 & 64) != 0 ? var3 | 16384 : var3;
      }

      public Spliterator.OfLong trySplit() {
         Spliterators.AbstractLongSpliterator.HoldingLongConsumer var1 = new Spliterators.AbstractLongSpliterator.HoldingLongConsumer();
         long var2 = this.est;
         if (var2 > 1L && this.tryAdvance(var1)) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            long[] var5 = new long[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.value;
               ++var6;
            } while(var6 < var4 && this.tryAdvance(var1));

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.LongArraySpliterator(var5, 0, var6, this.characteristics());
         } else {
            return null;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      static final class HoldingLongConsumer implements LongConsumer {
         long value;

         public void accept(long var1) {
            this.value = var1;
         }
      }
   }

   public abstract static class AbstractIntSpliterator implements Spliterator.OfInt {
      static final int MAX_BATCH = 33554432;
      static final int BATCH_UNIT = 1024;
      private final int characteristics;
      private long est;
      private int batch;

      protected AbstractIntSpliterator(long var1, int var3) {
         this.est = var1;
         this.characteristics = (var3 & 64) != 0 ? var3 | 16384 : var3;
      }

      public Spliterator.OfInt trySplit() {
         Spliterators.AbstractIntSpliterator.HoldingIntConsumer var1 = new Spliterators.AbstractIntSpliterator.HoldingIntConsumer();
         long var2 = this.est;
         if (var2 > 1L && this.tryAdvance(var1)) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            int[] var5 = new int[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.value;
               ++var6;
            } while(var6 < var4 && this.tryAdvance(var1));

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.IntArraySpliterator(var5, 0, var6, this.characteristics());
         } else {
            return null;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      static final class HoldingIntConsumer implements IntConsumer {
         int value;

         public void accept(int var1) {
            this.value = var1;
         }
      }
   }

   public abstract static class AbstractSpliterator<T> implements Spliterator<T> {
      static final int BATCH_UNIT = 1024;
      static final int MAX_BATCH = 33554432;
      private final int characteristics;
      private long est;
      private int batch;

      protected AbstractSpliterator(long var1, int var3) {
         this.est = var1;
         this.characteristics = (var3 & 64) != 0 ? var3 | 16384 : var3;
      }

      public Spliterator<T> trySplit() {
         Spliterators.AbstractSpliterator.HoldingConsumer var1 = new Spliterators.AbstractSpliterator.HoldingConsumer();
         long var2 = this.est;
         if (var2 > 1L && this.tryAdvance(var1)) {
            int var4 = this.batch + 1024;
            if ((long)var4 > var2) {
               var4 = (int)var2;
            }

            if (var4 > 33554432) {
               var4 = 33554432;
            }

            Object[] var5 = new Object[var4];
            int var6 = 0;

            do {
               var5[var6] = var1.value;
               ++var6;
            } while(var6 < var4 && this.tryAdvance(var1));

            this.batch = var6;
            if (this.est != Long.MAX_VALUE) {
               this.est -= (long)var6;
            }

            return new Spliterators.ArraySpliterator(var5, 0, var6, this.characteristics());
         } else {
            return null;
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return this.characteristics;
      }

      static final class HoldingConsumer<T> implements Consumer<T> {
         Object value;

         public void accept(T var1) {
            this.value = var1;
         }
      }
   }

   static final class DoubleArraySpliterator implements Spliterator.OfDouble {
      private final double[] array;
      private int index;
      private final int fence;
      private final int characteristics;

      public DoubleArraySpliterator(double[] var1, int var2) {
         this(var1, 0, var1.length, var2);
      }

      public DoubleArraySpliterator(double[] var1, int var2, int var3, int var4) {
         this.array = var1;
         this.index = var2;
         this.fence = var3;
         this.characteristics = var4 | 64 | 16384;
      }

      public Spliterator.OfDouble trySplit() {
         int var1 = this.index;
         int var2 = var1 + this.fence >>> 1;
         return var1 >= var2 ? null : new Spliterators.DoubleArraySpliterator(this.array, var1, this.index = var2, this.characteristics);
      }

      public void forEachRemaining(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            double[] var2;
            int var3;
            int var4;
            if ((var2 = this.array).length >= (var4 = this.fence) && (var3 = this.index) >= 0 && var3 < (this.index = var4)) {
               do {
                  var1.accept(var2[var3]);
                  ++var3;
               } while(var3 < var4);
            }

         }
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.index >= 0 && this.index < this.fence) {
            var1.accept(this.array[this.index++]);
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return (long)(this.fence - this.index);
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Double> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static final class LongArraySpliterator implements Spliterator.OfLong {
      private final long[] array;
      private int index;
      private final int fence;
      private final int characteristics;

      public LongArraySpliterator(long[] var1, int var2) {
         this(var1, 0, var1.length, var2);
      }

      public LongArraySpliterator(long[] var1, int var2, int var3, int var4) {
         this.array = var1;
         this.index = var2;
         this.fence = var3;
         this.characteristics = var4 | 64 | 16384;
      }

      public Spliterator.OfLong trySplit() {
         int var1 = this.index;
         int var2 = var1 + this.fence >>> 1;
         return var1 >= var2 ? null : new Spliterators.LongArraySpliterator(this.array, var1, this.index = var2, this.characteristics);
      }

      public void forEachRemaining(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long[] var2;
            int var3;
            int var4;
            if ((var2 = this.array).length >= (var4 = this.fence) && (var3 = this.index) >= 0 && var3 < (this.index = var4)) {
               do {
                  var1.accept(var2[var3]);
                  ++var3;
               } while(var3 < var4);
            }

         }
      }

      public boolean tryAdvance(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.index >= 0 && this.index < this.fence) {
            var1.accept(this.array[this.index++]);
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return (long)(this.fence - this.index);
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Long> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static final class IntArraySpliterator implements Spliterator.OfInt {
      private final int[] array;
      private int index;
      private final int fence;
      private final int characteristics;

      public IntArraySpliterator(int[] var1, int var2) {
         this(var1, 0, var1.length, var2);
      }

      public IntArraySpliterator(int[] var1, int var2, int var3, int var4) {
         this.array = var1;
         this.index = var2;
         this.fence = var3;
         this.characteristics = var4 | 64 | 16384;
      }

      public Spliterator.OfInt trySplit() {
         int var1 = this.index;
         int var2 = var1 + this.fence >>> 1;
         return var1 >= var2 ? null : new Spliterators.IntArraySpliterator(this.array, var1, this.index = var2, this.characteristics);
      }

      public void forEachRemaining(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int[] var2;
            int var3;
            int var4;
            if ((var2 = this.array).length >= (var4 = this.fence) && (var3 = this.index) >= 0 && var3 < (this.index = var4)) {
               do {
                  var1.accept(var2[var3]);
                  ++var3;
               } while(var3 < var4);
            }

         }
      }

      public boolean tryAdvance(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.index >= 0 && this.index < this.fence) {
            var1.accept(this.array[this.index++]);
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return (long)(this.fence - this.index);
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super Integer> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   static final class ArraySpliterator<T> implements Spliterator<T> {
      private final Object[] array;
      private int index;
      private final int fence;
      private final int characteristics;

      public ArraySpliterator(Object[] var1, int var2) {
         this(var1, 0, var1.length, var2);
      }

      public ArraySpliterator(Object[] var1, int var2, int var3, int var4) {
         this.array = var1;
         this.index = var2;
         this.fence = var3;
         this.characteristics = var4 | 64 | 16384;
      }

      public Spliterator<T> trySplit() {
         int var1 = this.index;
         int var2 = var1 + this.fence >>> 1;
         return var1 >= var2 ? null : new Spliterators.ArraySpliterator(this.array, var1, this.index = var2, this.characteristics);
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2;
            int var3;
            int var4;
            if ((var2 = this.array).length >= (var4 = this.fence) && (var3 = this.index) >= 0 && var3 < (this.index = var4)) {
               do {
                  var1.accept(var2[var3]);
                  ++var3;
               } while(var3 < var4);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.index >= 0 && this.index < this.fence) {
            Object var2 = this.array[this.index++];
            var1.accept(var2);
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return (long)(this.fence - this.index);
      }

      public int characteristics() {
         return this.characteristics;
      }

      public Comparator<? super T> getComparator() {
         if (this.hasCharacteristics(4)) {
            return null;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   private abstract static class EmptySpliterator<T, S extends Spliterator<T>, C> {
      EmptySpliterator() {
      }

      public S trySplit() {
         return null;
      }

      public boolean tryAdvance(C var1) {
         Objects.requireNonNull(var1);
         return false;
      }

      public void forEachRemaining(C var1) {
         Objects.requireNonNull(var1);
      }

      public long estimateSize() {
         return 0L;
      }

      public int characteristics() {
         return 16448;
      }

      private static final class OfDouble extends Spliterators.EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
         OfDouble() {
         }
      }

      private static final class OfLong extends Spliterators.EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
         OfLong() {
         }
      }

      private static final class OfInt extends Spliterators.EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
         OfInt() {
         }
      }

      private static final class OfRef<T> extends Spliterators.EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {
         OfRef() {
         }
      }
   }
}
