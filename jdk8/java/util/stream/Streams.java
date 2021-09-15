package java.util.stream;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

final class Streams {
   static final Object NONE = new Object();

   private Streams() {
      throw new Error("no instances");
   }

   static Runnable composeWithExceptions(final Runnable var0, final Runnable var1) {
      return new Runnable() {
         public void run() {
            try {
               var0.run();
            } catch (Throwable var6) {
               Throwable var1x = var6;

               try {
                  var1.run();
               } catch (Throwable var5) {
                  Throwable var2 = var5;

                  try {
                     var1x.addSuppressed(var2);
                  } catch (Throwable var4) {
                  }
               }

               throw var6;
            }

            var1.run();
         }
      };
   }

   static Runnable composedClose(final BaseStream<?, ?> var0, final BaseStream<?, ?> var1) {
      return new Runnable() {
         public void run() {
            try {
               var0.close();
            } catch (Throwable var6) {
               Throwable var1x = var6;

               try {
                  var1.close();
               } catch (Throwable var5) {
                  Throwable var2 = var5;

                  try {
                     var1x.addSuppressed(var2);
                  } catch (Throwable var4) {
                  }
               }

               throw var6;
            }

            var1.close();
         }
      };
   }

   abstract static class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
      protected final T_SPLITR aSpliterator;
      protected final T_SPLITR bSpliterator;
      boolean beforeSplit;
      final boolean unsized;

      public ConcatSpliterator(T_SPLITR var1, T_SPLITR var2) {
         this.aSpliterator = var1;
         this.bSpliterator = var2;
         this.beforeSplit = true;
         this.unsized = var1.estimateSize() + var2.estimateSize() < 0L;
      }

      public T_SPLITR trySplit() {
         Spliterator var1 = this.beforeSplit ? this.aSpliterator : this.bSpliterator.trySplit();
         this.beforeSplit = false;
         return var1;
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         boolean var2;
         if (this.beforeSplit) {
            var2 = this.aSpliterator.tryAdvance(var1);
            if (!var2) {
               this.beforeSplit = false;
               var2 = this.bSpliterator.tryAdvance(var1);
            }
         } else {
            var2 = this.bSpliterator.tryAdvance(var1);
         }

         return var2;
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         if (this.beforeSplit) {
            this.aSpliterator.forEachRemaining(var1);
         }

         this.bSpliterator.forEachRemaining(var1);
      }

      public long estimateSize() {
         if (this.beforeSplit) {
            long var1 = this.aSpliterator.estimateSize() + this.bSpliterator.estimateSize();
            return var1 >= 0L ? var1 : Long.MAX_VALUE;
         } else {
            return this.bSpliterator.estimateSize();
         }
      }

      public int characteristics() {
         return this.beforeSplit ? this.aSpliterator.characteristics() & this.bSpliterator.characteristics() & ~(5 | (this.unsized ? 16448 : 0)) : this.bSpliterator.characteristics();
      }

      public Comparator<? super T> getComparator() {
         if (this.beforeSplit) {
            throw new IllegalStateException();
         } else {
            return this.bSpliterator.getComparator();
         }
      }

      static class OfDouble extends Streams.ConcatSpliterator.OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
         OfDouble(Spliterator.OfDouble var1, Spliterator.OfDouble var2) {
            super(var1, var2, null);
         }
      }

      static class OfLong extends Streams.ConcatSpliterator.OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
         OfLong(Spliterator.OfLong var1, Spliterator.OfLong var2) {
            super(var1, var2, null);
         }
      }

      static class OfInt extends Streams.ConcatSpliterator.OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
         OfInt(Spliterator.OfInt var1, Spliterator.OfInt var2) {
            super(var1, var2, null);
         }
      }

      private abstract static class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends Streams.ConcatSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
         private OfPrimitive(T_SPLITR var1, T_SPLITR var2) {
            super(var1, var2);
         }

         public boolean tryAdvance(T_CONS var1) {
            boolean var2;
            if (this.beforeSplit) {
               var2 = ((Spliterator.OfPrimitive)this.aSpliterator).tryAdvance(var1);
               if (!var2) {
                  this.beforeSplit = false;
                  var2 = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(var1);
               }
            } else {
               var2 = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(var1);
            }

            return var2;
         }

         public void forEachRemaining(T_CONS var1) {
            if (this.beforeSplit) {
               ((Spliterator.OfPrimitive)this.aSpliterator).forEachRemaining(var1);
            }

            ((Spliterator.OfPrimitive)this.bSpliterator).forEachRemaining(var1);
         }

         // $FF: synthetic method
         OfPrimitive(Spliterator.OfPrimitive var1, Spliterator.OfPrimitive var2, Object var3) {
            this(var1, var2);
         }
      }

      static class OfRef<T> extends Streams.ConcatSpliterator<T, Spliterator<T>> {
         OfRef(Spliterator<T> var1, Spliterator<T> var2) {
            super(var1, var2);
         }
      }
   }

   static final class DoubleStreamBuilderImpl extends Streams.AbstractStreamBuilderImpl<Double, Spliterator.OfDouble> implements DoubleStream.Builder, Spliterator.OfDouble {
      double first;
      SpinedBuffer.OfDouble buffer;

      DoubleStreamBuilderImpl() {
         super(null);
      }

      DoubleStreamBuilderImpl(double var1) {
         super(null);
         this.first = var1;
         this.count = -2;
      }

      public void accept(double var1) {
         if (this.count == 0) {
            this.first = var1;
            ++this.count;
         } else {
            if (this.count <= 0) {
               throw new IllegalStateException();
            }

            if (this.buffer == null) {
               this.buffer = new SpinedBuffer.OfDouble();
               this.buffer.accept(this.first);
               ++this.count;
            }

            this.buffer.accept(var1);
         }

      }

      public DoubleStream build() {
         int var1 = this.count;
         if (var1 >= 0) {
            this.count = -this.count - 1;
            return var1 < 2 ? StreamSupport.doubleStream(this, false) : StreamSupport.doubleStream(this.buffer.spliterator(), false);
         } else {
            throw new IllegalStateException();
         }
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(DoubleConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
         }

      }
   }

   static final class LongStreamBuilderImpl extends Streams.AbstractStreamBuilderImpl<Long, Spliterator.OfLong> implements LongStream.Builder, Spliterator.OfLong {
      long first;
      SpinedBuffer.OfLong buffer;

      LongStreamBuilderImpl() {
         super(null);
      }

      LongStreamBuilderImpl(long var1) {
         super(null);
         this.first = var1;
         this.count = -2;
      }

      public void accept(long var1) {
         if (this.count == 0) {
            this.first = var1;
            ++this.count;
         } else {
            if (this.count <= 0) {
               throw new IllegalStateException();
            }

            if (this.buffer == null) {
               this.buffer = new SpinedBuffer.OfLong();
               this.buffer.accept(this.first);
               ++this.count;
            }

            this.buffer.accept(var1);
         }

      }

      public LongStream build() {
         int var1 = this.count;
         if (var1 >= 0) {
            this.count = -this.count - 1;
            return var1 < 2 ? StreamSupport.longStream(this, false) : StreamSupport.longStream(this.buffer.spliterator(), false);
         } else {
            throw new IllegalStateException();
         }
      }

      public boolean tryAdvance(LongConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(LongConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
         }

      }
   }

   static final class IntStreamBuilderImpl extends Streams.AbstractStreamBuilderImpl<Integer, Spliterator.OfInt> implements IntStream.Builder, Spliterator.OfInt {
      int first;
      SpinedBuffer.OfInt buffer;

      IntStreamBuilderImpl() {
         super(null);
      }

      IntStreamBuilderImpl(int var1) {
         super(null);
         this.first = var1;
         this.count = -2;
      }

      public void accept(int var1) {
         if (this.count == 0) {
            this.first = var1;
            ++this.count;
         } else {
            if (this.count <= 0) {
               throw new IllegalStateException();
            }

            if (this.buffer == null) {
               this.buffer = new SpinedBuffer.OfInt();
               this.buffer.accept(this.first);
               ++this.count;
            }

            this.buffer.accept(var1);
         }

      }

      public IntStream build() {
         int var1 = this.count;
         if (var1 >= 0) {
            this.count = -this.count - 1;
            return var1 < 2 ? StreamSupport.intStream(this, false) : StreamSupport.intStream(this.buffer.spliterator(), false);
         } else {
            throw new IllegalStateException();
         }
      }

      public boolean tryAdvance(IntConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(IntConsumer var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
         }

      }
   }

   static final class StreamBuilderImpl<T> extends Streams.AbstractStreamBuilderImpl<T, Spliterator<T>> implements Stream.Builder<T> {
      T first;
      SpinedBuffer<T> buffer;

      StreamBuilderImpl() {
         super(null);
      }

      StreamBuilderImpl(T var1) {
         super(null);
         this.first = var1;
         this.count = -2;
      }

      public void accept(T var1) {
         if (this.count == 0) {
            this.first = var1;
            ++this.count;
         } else {
            if (this.count <= 0) {
               throw new IllegalStateException();
            }

            if (this.buffer == null) {
               this.buffer = new SpinedBuffer();
               this.buffer.accept(this.first);
               ++this.count;
            }

            this.buffer.accept(var1);
         }

      }

      public Stream.Builder<T> add(T var1) {
         this.accept(var1);
         return this;
      }

      public Stream<T> build() {
         int var1 = this.count;
         if (var1 >= 0) {
            this.count = -this.count - 1;
            return var1 < 2 ? StreamSupport.stream(this, false) : StreamSupport.stream(this.buffer.spliterator(), false);
         } else {
            throw new IllegalStateException();
         }
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         Objects.requireNonNull(var1);
         if (this.count == -2) {
            var1.accept(this.first);
            this.count = -1;
         }

      }
   }

   private abstract static class AbstractStreamBuilderImpl<T, S extends Spliterator<T>> implements Spliterator<T> {
      int count;

      private AbstractStreamBuilderImpl() {
      }

      public S trySplit() {
         return null;
      }

      public long estimateSize() {
         return (long)(-this.count - 1);
      }

      public int characteristics() {
         return 17488;
      }

      // $FF: synthetic method
      AbstractStreamBuilderImpl(Object var1) {
         this();
      }
   }

   static final class RangeLongSpliterator implements Spliterator.OfLong {
      private long from;
      private final long upTo;
      private int last;
      private static final long BALANCED_SPLIT_THRESHOLD = 16777216L;
      private static final long RIGHT_BALANCED_SPLIT_RATIO = 8L;

      RangeLongSpliterator(long var1, long var3, boolean var5) {
         this(var1, var3, var5 ? 1 : 0);
      }

      private RangeLongSpliterator(long var1, long var3, int var5) {
         assert var3 - var1 + (long)var5 > 0L;

         this.from = var1;
         this.upTo = var3;
         this.last = var5;
      }

      public boolean tryAdvance(LongConsumer var1) {
         Objects.requireNonNull(var1);
         long var2 = this.from;
         if (var2 < this.upTo) {
            ++this.from;
            var1.accept(var2);
            return true;
         } else if (this.last > 0) {
            this.last = 0;
            var1.accept(var2);
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(LongConsumer var1) {
         Objects.requireNonNull(var1);
         long var2 = this.from;
         long var4 = this.upTo;
         int var6 = this.last;
         this.from = this.upTo;
         this.last = 0;

         while(var2 < var4) {
            var1.accept(var2++);
         }

         if (var6 > 0) {
            var1.accept(var2);
         }

      }

      public long estimateSize() {
         return this.upTo - this.from + (long)this.last;
      }

      public int characteristics() {
         return 17749;
      }

      public Comparator<? super Long> getComparator() {
         return null;
      }

      public Spliterator.OfLong trySplit() {
         long var1 = this.estimateSize();
         return var1 <= 1L ? null : new Streams.RangeLongSpliterator(this.from, this.from += this.splitPoint(var1), 0);
      }

      private long splitPoint(long var1) {
         long var3 = var1 < 16777216L ? 2L : 8L;
         return var1 / var3;
      }
   }

   static final class RangeIntSpliterator implements Spliterator.OfInt {
      private int from;
      private final int upTo;
      private int last;
      private static final int BALANCED_SPLIT_THRESHOLD = 16777216;
      private static final int RIGHT_BALANCED_SPLIT_RATIO = 8;

      RangeIntSpliterator(int var1, int var2, boolean var3) {
         this(var1, var2, var3 ? 1 : 0);
      }

      private RangeIntSpliterator(int var1, int var2, int var3) {
         this.from = var1;
         this.upTo = var2;
         this.last = var3;
      }

      public boolean tryAdvance(IntConsumer var1) {
         Objects.requireNonNull(var1);
         int var2 = this.from;
         if (var2 < this.upTo) {
            ++this.from;
            var1.accept(var2);
            return true;
         } else if (this.last > 0) {
            this.last = 0;
            var1.accept(var2);
            return true;
         } else {
            return false;
         }
      }

      public void forEachRemaining(IntConsumer var1) {
         Objects.requireNonNull(var1);
         int var2 = this.from;
         int var3 = this.upTo;
         int var4 = this.last;
         this.from = this.upTo;
         this.last = 0;

         while(var2 < var3) {
            var1.accept(var2++);
         }

         if (var4 > 0) {
            var1.accept(var2);
         }

      }

      public long estimateSize() {
         return (long)this.upTo - (long)this.from + (long)this.last;
      }

      public int characteristics() {
         return 17749;
      }

      public Comparator<? super Integer> getComparator() {
         return null;
      }

      public Spliterator.OfInt trySplit() {
         long var1 = this.estimateSize();
         return var1 <= 1L ? null : new Streams.RangeIntSpliterator(this.from, this.from += this.splitPoint(var1), 0);
      }

      private int splitPoint(long var1) {
         int var3 = var1 < 16777216L ? 2 : 8;
         return (int)(var1 / (long)var3);
      }
   }
}
