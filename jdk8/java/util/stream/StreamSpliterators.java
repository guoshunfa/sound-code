package java.util.stream;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

class StreamSpliterators {
   abstract static class ArrayBuffer {
      int index;

      void reset() {
         this.index = 0;
      }

      static final class OfDouble extends StreamSpliterators.ArrayBuffer.OfPrimitive<DoubleConsumer> implements DoubleConsumer {
         final double[] array;

         OfDouble(int var1) {
            this.array = new double[var1];
         }

         public void accept(double var1) {
            this.array[this.index++] = var1;
         }

         void forEach(DoubleConsumer var1, long var2) {
            for(int var4 = 0; (long)var4 < var2; ++var4) {
               var1.accept(this.array[var4]);
            }

         }
      }

      static final class OfLong extends StreamSpliterators.ArrayBuffer.OfPrimitive<LongConsumer> implements LongConsumer {
         final long[] array;

         OfLong(int var1) {
            this.array = new long[var1];
         }

         public void accept(long var1) {
            this.array[this.index++] = var1;
         }

         public void forEach(LongConsumer var1, long var2) {
            for(int var4 = 0; (long)var4 < var2; ++var4) {
               var1.accept(this.array[var4]);
            }

         }
      }

      static final class OfInt extends StreamSpliterators.ArrayBuffer.OfPrimitive<IntConsumer> implements IntConsumer {
         final int[] array;

         OfInt(int var1) {
            this.array = new int[var1];
         }

         public void accept(int var1) {
            this.array[this.index++] = var1;
         }

         public void forEach(IntConsumer var1, long var2) {
            for(int var4 = 0; (long)var4 < var2; ++var4) {
               var1.accept(this.array[var4]);
            }

         }
      }

      abstract static class OfPrimitive<T_CONS> extends StreamSpliterators.ArrayBuffer {
         int index;

         void reset() {
            this.index = 0;
         }

         abstract void forEach(T_CONS var1, long var2);
      }

      static final class OfRef<T> extends StreamSpliterators.ArrayBuffer implements Consumer<T> {
         final Object[] array;

         OfRef(int var1) {
            this.array = new Object[var1];
         }

         public void accept(T var1) {
            this.array[this.index++] = var1;
         }

         public void forEach(Consumer<? super T> var1, long var2) {
            for(int var4 = 0; (long)var4 < var2; ++var4) {
               Object var5 = this.array[var4];
               var1.accept(var5);
            }

         }
      }
   }

   abstract static class InfiniteSupplyingSpliterator<T> implements Spliterator<T> {
      long estimate;

      protected InfiniteSupplyingSpliterator(long var1) {
         this.estimate = var1;
      }

      public long estimateSize() {
         return this.estimate;
      }

      public int characteristics() {
         return 1024;
      }

      static final class OfDouble extends StreamSpliterators.InfiniteSupplyingSpliterator<Double> implements Spliterator.OfDouble {
         final DoubleSupplier s;

         OfDouble(long var1, DoubleSupplier var3) {
            super(var1);
            this.s = var3;
         }

         public boolean tryAdvance(DoubleConsumer var1) {
            Objects.requireNonNull(var1);
            var1.accept(this.s.getAsDouble());
            return true;
         }

         public Spliterator.OfDouble trySplit() {
            return this.estimate == 0L ? null : new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(this.estimate >>>= 1, this.s);
         }
      }

      static final class OfLong extends StreamSpliterators.InfiniteSupplyingSpliterator<Long> implements Spliterator.OfLong {
         final LongSupplier s;

         OfLong(long var1, LongSupplier var3) {
            super(var1);
            this.s = var3;
         }

         public boolean tryAdvance(LongConsumer var1) {
            Objects.requireNonNull(var1);
            var1.accept(this.s.getAsLong());
            return true;
         }

         public Spliterator.OfLong trySplit() {
            return this.estimate == 0L ? null : new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(this.estimate >>>= 1, this.s);
         }
      }

      static final class OfInt extends StreamSpliterators.InfiniteSupplyingSpliterator<Integer> implements Spliterator.OfInt {
         final IntSupplier s;

         OfInt(long var1, IntSupplier var3) {
            super(var1);
            this.s = var3;
         }

         public boolean tryAdvance(IntConsumer var1) {
            Objects.requireNonNull(var1);
            var1.accept(this.s.getAsInt());
            return true;
         }

         public Spliterator.OfInt trySplit() {
            return this.estimate == 0L ? null : new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(this.estimate >>>= 1, this.s);
         }
      }

      static final class OfRef<T> extends StreamSpliterators.InfiniteSupplyingSpliterator<T> {
         final Supplier<T> s;

         OfRef(long var1, Supplier<T> var3) {
            super(var1);
            this.s = var3;
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);
            var1.accept(this.s.get());
            return true;
         }

         public Spliterator<T> trySplit() {
            return this.estimate == 0L ? null : new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef(this.estimate >>>= 1, this.s);
         }
      }
   }

   static final class DistinctSpliterator<T> implements Spliterator<T>, Consumer<T> {
      private static final Object NULL_VALUE = new Object();
      private final Spliterator<T> s;
      private final ConcurrentHashMap<T, Boolean> seen;
      private T tmpSlot;

      DistinctSpliterator(Spliterator<T> var1) {
         this(var1, new ConcurrentHashMap());
      }

      private DistinctSpliterator(Spliterator<T> var1, ConcurrentHashMap<T, Boolean> var2) {
         this.s = var1;
         this.seen = var2;
      }

      public void accept(T var1) {
         this.tmpSlot = var1;
      }

      private T mapNull(T var1) {
         return var1 != null ? var1 : NULL_VALUE;
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         while(true) {
            if (this.s.tryAdvance(this)) {
               if (this.seen.putIfAbsent(this.mapNull(this.tmpSlot), Boolean.TRUE) != null) {
                  continue;
               }

               var1.accept(this.tmpSlot);
               this.tmpSlot = null;
               return true;
            }

            return false;
         }
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         this.s.forEachRemaining((var2) -> {
            if (this.seen.putIfAbsent(this.mapNull(var2), Boolean.TRUE) == null) {
               var1.accept(var2);
            }

         });
      }

      public Spliterator<T> trySplit() {
         Spliterator var1 = this.s.trySplit();
         return var1 != null ? new StreamSpliterators.DistinctSpliterator(var1, this.seen) : null;
      }

      public long estimateSize() {
         return this.s.estimateSize();
      }

      public int characteristics() {
         return this.s.characteristics() & -16469 | 1;
      }

      public Comparator<? super T> getComparator() {
         return this.s.getComparator();
      }
   }

   abstract static class UnorderedSliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
      static final int CHUNK_SIZE = 128;
      protected final T_SPLITR s;
      protected final boolean unlimited;
      private final long skipThreshold;
      private final AtomicLong permits;

      UnorderedSliceSpliterator(T_SPLITR var1, long var2, long var4) {
         this.s = var1;
         this.unlimited = var4 < 0L;
         this.skipThreshold = var4 >= 0L ? var4 : 0L;
         this.permits = new AtomicLong(var4 >= 0L ? var2 + var4 : var2);
      }

      UnorderedSliceSpliterator(T_SPLITR var1, StreamSpliterators.UnorderedSliceSpliterator<T, T_SPLITR> var2) {
         this.s = var1;
         this.unlimited = var2.unlimited;
         this.permits = var2.permits;
         this.skipThreshold = var2.skipThreshold;
      }

      protected final long acquirePermits(long var1) {
         assert var1 > 0L;

         long var3;
         long var5;
         do {
            var3 = this.permits.get();
            if (var3 == 0L) {
               return this.unlimited ? var1 : 0L;
            }

            var5 = Math.min(var3, var1);
         } while(var5 > 0L && !this.permits.compareAndSet(var3, var3 - var5));

         if (this.unlimited) {
            return Math.max(var1 - var5, 0L);
         } else {
            return var3 > this.skipThreshold ? Math.max(var5 - (var3 - this.skipThreshold), 0L) : var5;
         }
      }

      protected final StreamSpliterators.UnorderedSliceSpliterator.PermitStatus permitStatus() {
         if (this.permits.get() > 0L) {
            return StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.MAYBE_MORE;
         } else {
            return this.unlimited ? StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.UNLIMITED : StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE;
         }
      }

      public final T_SPLITR trySplit() {
         if (this.permits.get() == 0L) {
            return null;
         } else {
            Spliterator var1 = this.s.trySplit();
            return var1 == null ? null : this.makeSpliterator(var1);
         }
      }

      protected abstract T_SPLITR makeSpliterator(T_SPLITR var1);

      public final long estimateSize() {
         return this.s.estimateSize();
      }

      public final int characteristics() {
         return this.s.characteristics() & -16465;
      }

      static final class OfDouble extends StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive<Double, DoubleConsumer, StreamSpliterators.ArrayBuffer.OfDouble, Spliterator.OfDouble> implements Spliterator.OfDouble, DoubleConsumer {
         double tmpValue;

         OfDouble(Spliterator.OfDouble var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfDouble(Spliterator.OfDouble var1, StreamSpliterators.UnorderedSliceSpliterator.OfDouble var2) {
            super(var1, var2);
         }

         public void accept(double var1) {
            this.tmpValue = var1;
         }

         protected void acceptConsumed(DoubleConsumer var1) {
            var1.accept(this.tmpValue);
         }

         protected StreamSpliterators.ArrayBuffer.OfDouble bufferCreate(int var1) {
            return new StreamSpliterators.ArrayBuffer.OfDouble(var1);
         }

         protected Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble var1) {
            return new StreamSpliterators.UnorderedSliceSpliterator.OfDouble(var1, this);
         }
      }

      static final class OfLong extends StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive<Long, LongConsumer, StreamSpliterators.ArrayBuffer.OfLong, Spliterator.OfLong> implements Spliterator.OfLong, LongConsumer {
         long tmpValue;

         OfLong(Spliterator.OfLong var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfLong(Spliterator.OfLong var1, StreamSpliterators.UnorderedSliceSpliterator.OfLong var2) {
            super(var1, var2);
         }

         public void accept(long var1) {
            this.tmpValue = var1;
         }

         protected void acceptConsumed(LongConsumer var1) {
            var1.accept(this.tmpValue);
         }

         protected StreamSpliterators.ArrayBuffer.OfLong bufferCreate(int var1) {
            return new StreamSpliterators.ArrayBuffer.OfLong(var1);
         }

         protected Spliterator.OfLong makeSpliterator(Spliterator.OfLong var1) {
            return new StreamSpliterators.UnorderedSliceSpliterator.OfLong(var1, this);
         }
      }

      static final class OfInt extends StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive<Integer, IntConsumer, StreamSpliterators.ArrayBuffer.OfInt, Spliterator.OfInt> implements Spliterator.OfInt, IntConsumer {
         int tmpValue;

         OfInt(Spliterator.OfInt var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfInt(Spliterator.OfInt var1, StreamSpliterators.UnorderedSliceSpliterator.OfInt var2) {
            super(var1, var2);
         }

         public void accept(int var1) {
            this.tmpValue = var1;
         }

         protected void acceptConsumed(IntConsumer var1) {
            var1.accept(this.tmpValue);
         }

         protected StreamSpliterators.ArrayBuffer.OfInt bufferCreate(int var1) {
            return new StreamSpliterators.ArrayBuffer.OfInt(var1);
         }

         protected Spliterator.OfInt makeSpliterator(Spliterator.OfInt var1) {
            return new StreamSpliterators.UnorderedSliceSpliterator.OfInt(var1, this);
         }
      }

      abstract static class OfPrimitive<T, T_CONS, T_BUFF extends StreamSpliterators.ArrayBuffer.OfPrimitive<T_CONS>, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends StreamSpliterators.UnorderedSliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
         OfPrimitive(T_SPLITR var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfPrimitive(T_SPLITR var1, StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive<T, T_CONS, T_BUFF, T_SPLITR> var2) {
            super(var1, var2);
         }

         public boolean tryAdvance(T_CONS var1) {
            Objects.requireNonNull(var1);
            StreamSpliterators.UnorderedSliceSpliterator.OfPrimitive var2 = this;

            do {
               if (this.permitStatus() == StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
                  return false;
               }

               if (!((Spliterator.OfPrimitive)this.s).tryAdvance(var2)) {
                  return false;
               }
            } while(this.acquirePermits(1L) != 1L);

            this.acceptConsumed(var1);
            return true;
         }

         protected abstract void acceptConsumed(T_CONS var1);

         public void forEachRemaining(T_CONS var1) {
            Objects.requireNonNull(var1);
            StreamSpliterators.ArrayBuffer.OfPrimitive var2 = null;

            StreamSpliterators.UnorderedSliceSpliterator.PermitStatus var3;
            while((var3 = this.permitStatus()) != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
               if (var3 != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.MAYBE_MORE) {
                  ((Spliterator.OfPrimitive)this.s).forEachRemaining(var1);
                  return;
               }

               if (var2 == null) {
                  var2 = this.bufferCreate(128);
               } else {
                  var2.reset();
               }

               StreamSpliterators.ArrayBuffer.OfPrimitive var4 = var2;
               long var5 = 0L;

               while(((Spliterator.OfPrimitive)this.s).tryAdvance(var4) && ++var5 < 128L) {
               }

               if (var5 == 0L) {
                  return;
               }

               var2.forEach(var1, this.acquirePermits(var5));
            }

         }

         protected abstract T_BUFF bufferCreate(int var1);
      }

      static final class OfRef<T> extends StreamSpliterators.UnorderedSliceSpliterator<T, Spliterator<T>> implements Spliterator<T>, Consumer<T> {
         T tmpSlot;

         OfRef(Spliterator<T> var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfRef(Spliterator<T> var1, StreamSpliterators.UnorderedSliceSpliterator.OfRef<T> var2) {
            super(var1, var2);
         }

         public final void accept(T var1) {
            this.tmpSlot = var1;
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);

            do {
               if (this.permitStatus() == StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
                  return false;
               }

               if (!this.s.tryAdvance(this)) {
                  return false;
               }
            } while(this.acquirePermits(1L) != 1L);

            var1.accept(this.tmpSlot);
            this.tmpSlot = null;
            return true;
         }

         public void forEachRemaining(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);
            StreamSpliterators.ArrayBuffer.OfRef var2 = null;

            StreamSpliterators.UnorderedSliceSpliterator.PermitStatus var3;
            while((var3 = this.permitStatus()) != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
               if (var3 != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.MAYBE_MORE) {
                  this.s.forEachRemaining(var1);
                  return;
               }

               if (var2 == null) {
                  var2 = new StreamSpliterators.ArrayBuffer.OfRef(128);
               } else {
                  var2.reset();
               }

               long var4 = 0L;

               while(this.s.tryAdvance(var2) && ++var4 < 128L) {
               }

               if (var4 == 0L) {
                  return;
               }

               var2.forEach(var1, this.acquirePermits(var4));
            }

         }

         protected Spliterator<T> makeSpliterator(Spliterator<T> var1) {
            return new StreamSpliterators.UnorderedSliceSpliterator.OfRef(var1, this);
         }
      }

      static enum PermitStatus {
         NO_MORE,
         MAYBE_MORE,
         UNLIMITED;
      }
   }

   abstract static class SliceSpliterator<T, T_SPLITR extends Spliterator<T>> {
      final long sliceOrigin;
      final long sliceFence;
      T_SPLITR s;
      long index;
      long fence;

      SliceSpliterator(T_SPLITR var1, long var2, long var4, long var6, long var8) {
         assert var1.hasCharacteristics(16384);

         this.s = var1;
         this.sliceOrigin = var2;
         this.sliceFence = var4;
         this.index = var6;
         this.fence = var8;
      }

      protected abstract T_SPLITR makeSpliterator(T_SPLITR var1, long var2, long var4, long var6, long var8);

      public T_SPLITR trySplit() {
         if (this.sliceOrigin >= this.fence) {
            return null;
         } else if (this.index >= this.fence) {
            return null;
         } else {
            while(true) {
               Spliterator var1 = this.s.trySplit();
               if (var1 == null) {
                  return null;
               }

               long var2 = this.index + var1.estimateSize();
               long var4 = Math.min(var2, this.sliceFence);
               if (this.sliceOrigin >= var4) {
                  this.index = var4;
               } else {
                  if (var4 < this.sliceFence) {
                     if (this.index >= this.sliceOrigin && var2 <= this.sliceFence) {
                        this.index = var4;
                        return var1;
                     }

                     return this.makeSpliterator(var1, this.sliceOrigin, this.sliceFence, this.index, this.index = var4);
                  }

                  this.s = var1;
                  this.fence = var4;
               }
            }
         }
      }

      public long estimateSize() {
         return this.sliceOrigin < this.fence ? this.fence - Math.max(this.sliceOrigin, this.index) : 0L;
      }

      public int characteristics() {
         return this.s.characteristics();
      }

      static final class OfDouble extends StreamSpliterators.SliceSpliterator.OfPrimitive<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
         OfDouble(Spliterator.OfDouble var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfDouble(Spliterator.OfDouble var1, long var2, long var4, long var6, long var8) {
            super(var1, var2, var4, var6, var8, null);
         }

         protected Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble var1, long var2, long var4, long var6, long var8) {
            return new StreamSpliterators.SliceSpliterator.OfDouble(var1, var2, var4, var6, var8);
         }

         protected DoubleConsumer emptyConsumer() {
            return (var0) -> {
            };
         }
      }

      static final class OfLong extends StreamSpliterators.SliceSpliterator.OfPrimitive<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
         OfLong(Spliterator.OfLong var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfLong(Spliterator.OfLong var1, long var2, long var4, long var6, long var8) {
            super(var1, var2, var4, var6, var8, null);
         }

         protected Spliterator.OfLong makeSpliterator(Spliterator.OfLong var1, long var2, long var4, long var6, long var8) {
            return new StreamSpliterators.SliceSpliterator.OfLong(var1, var2, var4, var6, var8);
         }

         protected LongConsumer emptyConsumer() {
            return (var0) -> {
            };
         }
      }

      static final class OfInt extends StreamSpliterators.SliceSpliterator.OfPrimitive<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
         OfInt(Spliterator.OfInt var1, long var2, long var4) {
            super(var1, var2, var4);
         }

         OfInt(Spliterator.OfInt var1, long var2, long var4, long var6, long var8) {
            super(var1, var2, var4, var6, var8, null);
         }

         protected Spliterator.OfInt makeSpliterator(Spliterator.OfInt var1, long var2, long var4, long var6, long var8) {
            return new StreamSpliterators.SliceSpliterator.OfInt(var1, var2, var4, var6, var8);
         }

         protected IntConsumer emptyConsumer() {
            return (var0) -> {
            };
         }
      }

      abstract static class OfPrimitive<T, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> extends StreamSpliterators.SliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
         OfPrimitive(T_SPLITR var1, long var2, long var4) {
            this(var1, var2, var4, 0L, Math.min(var1.estimateSize(), var4));
         }

         private OfPrimitive(T_SPLITR var1, long var2, long var4, long var6, long var8) {
            super(var1, var2, var4, var6, var8);
         }

         public boolean tryAdvance(T_CONS var1) {
            Objects.requireNonNull(var1);
            if (this.sliceOrigin >= this.fence) {
               return false;
            } else {
               while(this.sliceOrigin > this.index) {
                  ((Spliterator.OfPrimitive)this.s).tryAdvance(this.emptyConsumer());
                  ++this.index;
               }

               if (this.index >= this.fence) {
                  return false;
               } else {
                  ++this.index;
                  return ((Spliterator.OfPrimitive)this.s).tryAdvance(var1);
               }
            }
         }

         public void forEachRemaining(T_CONS var1) {
            Objects.requireNonNull(var1);
            if (this.sliceOrigin < this.fence) {
               if (this.index < this.fence) {
                  if (this.index >= this.sliceOrigin && this.index + ((Spliterator.OfPrimitive)this.s).estimateSize() <= this.sliceFence) {
                     ((Spliterator.OfPrimitive)this.s).forEachRemaining(var1);
                     this.index = this.fence;
                  } else {
                     while(this.sliceOrigin > this.index) {
                        ((Spliterator.OfPrimitive)this.s).tryAdvance(this.emptyConsumer());
                        ++this.index;
                     }

                     while(this.index < this.fence) {
                        ((Spliterator.OfPrimitive)this.s).tryAdvance(var1);
                        ++this.index;
                     }
                  }

               }
            }
         }

         protected abstract T_CONS emptyConsumer();

         // $FF: synthetic method
         OfPrimitive(Spliterator.OfPrimitive var1, long var2, long var4, long var6, long var8, Object var10) {
            this(var1, var2, var4, var6, var8);
         }
      }

      static final class OfRef<T> extends StreamSpliterators.SliceSpliterator<T, Spliterator<T>> implements Spliterator<T> {
         OfRef(Spliterator<T> var1, long var2, long var4) {
            this(var1, var2, var4, 0L, Math.min(var1.estimateSize(), var4));
         }

         private OfRef(Spliterator<T> var1, long var2, long var4, long var6, long var8) {
            super(var1, var2, var4, var6, var8);
         }

         protected Spliterator<T> makeSpliterator(Spliterator<T> var1, long var2, long var4, long var6, long var8) {
            return new StreamSpliterators.SliceSpliterator.OfRef(var1, var2, var4, var6, var8);
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);
            if (this.sliceOrigin >= this.fence) {
               return false;
            } else {
               while(this.sliceOrigin > this.index) {
                  this.s.tryAdvance((var0) -> {
                  });
                  ++this.index;
               }

               if (this.index >= this.fence) {
                  return false;
               } else {
                  ++this.index;
                  return this.s.tryAdvance(var1);
               }
            }
         }

         public void forEachRemaining(Consumer<? super T> var1) {
            Objects.requireNonNull(var1);
            if (this.sliceOrigin < this.fence) {
               if (this.index < this.fence) {
                  if (this.index >= this.sliceOrigin && this.index + this.s.estimateSize() <= this.sliceFence) {
                     this.s.forEachRemaining(var1);
                     this.index = this.fence;
                  } else {
                     while(this.sliceOrigin > this.index) {
                        this.s.tryAdvance((var0) -> {
                        });
                        ++this.index;
                     }

                     while(this.index < this.fence) {
                        this.s.tryAdvance(var1);
                        ++this.index;
                     }
                  }

               }
            }
         }
      }
   }

   static class DelegatingSpliterator<T, T_SPLITR extends Spliterator<T>> implements Spliterator<T> {
      private final Supplier<? extends T_SPLITR> supplier;
      private T_SPLITR s;

      DelegatingSpliterator(Supplier<? extends T_SPLITR> var1) {
         this.supplier = var1;
      }

      T_SPLITR get() {
         if (this.s == null) {
            this.s = (Spliterator)this.supplier.get();
         }

         return this.s;
      }

      public T_SPLITR trySplit() {
         return this.get().trySplit();
      }

      public boolean tryAdvance(Consumer<? super T> var1) {
         return this.get().tryAdvance(var1);
      }

      public void forEachRemaining(Consumer<? super T> var1) {
         this.get().forEachRemaining(var1);
      }

      public long estimateSize() {
         return this.get().estimateSize();
      }

      public int characteristics() {
         return this.get().characteristics();
      }

      public Comparator<? super T> getComparator() {
         return this.get().getComparator();
      }

      public long getExactSizeIfKnown() {
         return this.get().getExactSizeIfKnown();
      }

      public String toString() {
         return this.getClass().getName() + "[" + this.get() + "]";
      }

      static final class OfDouble extends StreamSpliterators.DelegatingSpliterator.OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
         OfDouble(Supplier<Spliterator.OfDouble> var1) {
            super(var1);
         }
      }

      static final class OfLong extends StreamSpliterators.DelegatingSpliterator.OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
         OfLong(Supplier<Spliterator.OfLong> var1) {
            super(var1);
         }
      }

      static final class OfInt extends StreamSpliterators.DelegatingSpliterator.OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
         OfInt(Supplier<Spliterator.OfInt> var1) {
            super(var1);
         }
      }

      static class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends StreamSpliterators.DelegatingSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
         OfPrimitive(Supplier<? extends T_SPLITR> var1) {
            super(var1);
         }

         public boolean tryAdvance(T_CONS var1) {
            return ((Spliterator.OfPrimitive)this.get()).tryAdvance(var1);
         }

         public void forEachRemaining(T_CONS var1) {
            ((Spliterator.OfPrimitive)this.get()).forEachRemaining(var1);
         }
      }
   }

   static final class DoubleWrappingSpliterator<P_IN> extends StreamSpliterators.AbstractWrappingSpliterator<P_IN, Double, SpinedBuffer.OfDouble> implements Spliterator.OfDouble {
      DoubleWrappingSpliterator(PipelineHelper<Double> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
         super(var1, var2, var3);
      }

      DoubleWrappingSpliterator(PipelineHelper<Double> var1, Spliterator<P_IN> var2, boolean var3) {
         super(var1, var2, var3);
      }

      StreamSpliterators.AbstractWrappingSpliterator<P_IN, Double, ?> wrap(Spliterator<P_IN> var1) {
         return new StreamSpliterators.DoubleWrappingSpliterator(this.ph, var1, this.isParallel);
      }

      void initPartialTraversalState() {
         SpinedBuffer.OfDouble var1 = new SpinedBuffer.OfDouble();
         this.buffer = var1;
         PipelineHelper var10001 = this.ph;
         var1.getClass();
         this.bufferSink = var10001.wrapSink(var1::accept);
         this.pusher = () -> {
            return this.spliterator.tryAdvance(this.bufferSink);
         };
      }

      public Spliterator.OfDouble trySplit() {
         return (Spliterator.OfDouble)super.trySplit();
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         Objects.requireNonNull(var1);
         boolean var2 = this.doAdvance();
         if (var2) {
            var1.accept(((SpinedBuffer.OfDouble)this.buffer).get(this.nextToConsume));
         }

         return var2;
      }

      public void forEachRemaining(DoubleConsumer var1) {
         if (this.buffer == null && !this.finished) {
            Objects.requireNonNull(var1);
            this.init();
            this.ph.wrapAndCopyInto(var1::accept, this.spliterator);
            this.finished = true;
         } else {
            while(this.tryAdvance(var1)) {
            }
         }

      }
   }

   static final class LongWrappingSpliterator<P_IN> extends StreamSpliterators.AbstractWrappingSpliterator<P_IN, Long, SpinedBuffer.OfLong> implements Spliterator.OfLong {
      LongWrappingSpliterator(PipelineHelper<Long> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
         super(var1, var2, var3);
      }

      LongWrappingSpliterator(PipelineHelper<Long> var1, Spliterator<P_IN> var2, boolean var3) {
         super(var1, var2, var3);
      }

      StreamSpliterators.AbstractWrappingSpliterator<P_IN, Long, ?> wrap(Spliterator<P_IN> var1) {
         return new StreamSpliterators.LongWrappingSpliterator(this.ph, var1, this.isParallel);
      }

      void initPartialTraversalState() {
         SpinedBuffer.OfLong var1 = new SpinedBuffer.OfLong();
         this.buffer = var1;
         PipelineHelper var10001 = this.ph;
         var1.getClass();
         this.bufferSink = var10001.wrapSink(var1::accept);
         this.pusher = () -> {
            return this.spliterator.tryAdvance(this.bufferSink);
         };
      }

      public Spliterator.OfLong trySplit() {
         return (Spliterator.OfLong)super.trySplit();
      }

      public boolean tryAdvance(LongConsumer var1) {
         Objects.requireNonNull(var1);
         boolean var2 = this.doAdvance();
         if (var2) {
            var1.accept(((SpinedBuffer.OfLong)this.buffer).get(this.nextToConsume));
         }

         return var2;
      }

      public void forEachRemaining(LongConsumer var1) {
         if (this.buffer == null && !this.finished) {
            Objects.requireNonNull(var1);
            this.init();
            this.ph.wrapAndCopyInto(var1::accept, this.spliterator);
            this.finished = true;
         } else {
            while(this.tryAdvance(var1)) {
            }
         }

      }
   }

   static final class IntWrappingSpliterator<P_IN> extends StreamSpliterators.AbstractWrappingSpliterator<P_IN, Integer, SpinedBuffer.OfInt> implements Spliterator.OfInt {
      IntWrappingSpliterator(PipelineHelper<Integer> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
         super(var1, var2, var3);
      }

      IntWrappingSpliterator(PipelineHelper<Integer> var1, Spliterator<P_IN> var2, boolean var3) {
         super(var1, var2, var3);
      }

      StreamSpliterators.AbstractWrappingSpliterator<P_IN, Integer, ?> wrap(Spliterator<P_IN> var1) {
         return new StreamSpliterators.IntWrappingSpliterator(this.ph, var1, this.isParallel);
      }

      void initPartialTraversalState() {
         SpinedBuffer.OfInt var1 = new SpinedBuffer.OfInt();
         this.buffer = var1;
         PipelineHelper var10001 = this.ph;
         var1.getClass();
         this.bufferSink = var10001.wrapSink(var1::accept);
         this.pusher = () -> {
            return this.spliterator.tryAdvance(this.bufferSink);
         };
      }

      public Spliterator.OfInt trySplit() {
         return (Spliterator.OfInt)super.trySplit();
      }

      public boolean tryAdvance(IntConsumer var1) {
         Objects.requireNonNull(var1);
         boolean var2 = this.doAdvance();
         if (var2) {
            var1.accept(((SpinedBuffer.OfInt)this.buffer).get(this.nextToConsume));
         }

         return var2;
      }

      public void forEachRemaining(IntConsumer var1) {
         if (this.buffer == null && !this.finished) {
            Objects.requireNonNull(var1);
            this.init();
            this.ph.wrapAndCopyInto(var1::accept, this.spliterator);
            this.finished = true;
         } else {
            while(this.tryAdvance(var1)) {
            }
         }

      }
   }

   static final class WrappingSpliterator<P_IN, P_OUT> extends StreamSpliterators.AbstractWrappingSpliterator<P_IN, P_OUT, SpinedBuffer<P_OUT>> {
      WrappingSpliterator(PipelineHelper<P_OUT> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
         super(var1, var2, var3);
      }

      WrappingSpliterator(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2, boolean var3) {
         super(var1, var2, var3);
      }

      StreamSpliterators.WrappingSpliterator<P_IN, P_OUT> wrap(Spliterator<P_IN> var1) {
         return new StreamSpliterators.WrappingSpliterator(this.ph, var1, this.isParallel);
      }

      void initPartialTraversalState() {
         SpinedBuffer var1 = new SpinedBuffer();
         this.buffer = var1;
         PipelineHelper var10001 = this.ph;
         var1.getClass();
         this.bufferSink = var10001.wrapSink(var1::accept);
         this.pusher = () -> {
            return this.spliterator.tryAdvance(this.bufferSink);
         };
      }

      public boolean tryAdvance(Consumer<? super P_OUT> var1) {
         Objects.requireNonNull(var1);
         boolean var2 = this.doAdvance();
         if (var2) {
            var1.accept(((SpinedBuffer)this.buffer).get(this.nextToConsume));
         }

         return var2;
      }

      public void forEachRemaining(Consumer<? super P_OUT> var1) {
         if (this.buffer == null && !this.finished) {
            Objects.requireNonNull(var1);
            this.init();
            this.ph.wrapAndCopyInto(var1::accept, this.spliterator);
            this.finished = true;
         } else {
            while(this.tryAdvance(var1)) {
            }
         }

      }
   }

   private abstract static class AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends AbstractSpinedBuffer> implements Spliterator<P_OUT> {
      final boolean isParallel;
      final PipelineHelper<P_OUT> ph;
      private Supplier<Spliterator<P_IN>> spliteratorSupplier;
      Spliterator<P_IN> spliterator;
      Sink<P_IN> bufferSink;
      BooleanSupplier pusher;
      long nextToConsume;
      T_BUFFER buffer;
      boolean finished;

      AbstractWrappingSpliterator(PipelineHelper<P_OUT> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
         this.ph = var1;
         this.spliteratorSupplier = var2;
         this.spliterator = null;
         this.isParallel = var3;
      }

      AbstractWrappingSpliterator(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2, boolean var3) {
         this.ph = var1;
         this.spliteratorSupplier = null;
         this.spliterator = var2;
         this.isParallel = var3;
      }

      final void init() {
         if (this.spliterator == null) {
            this.spliterator = (Spliterator)this.spliteratorSupplier.get();
            this.spliteratorSupplier = null;
         }

      }

      final boolean doAdvance() {
         if (this.buffer == null) {
            if (this.finished) {
               return false;
            } else {
               this.init();
               this.initPartialTraversalState();
               this.nextToConsume = 0L;
               this.bufferSink.begin(this.spliterator.getExactSizeIfKnown());
               return this.fillBuffer();
            }
         } else {
            ++this.nextToConsume;
            boolean var1 = this.nextToConsume < this.buffer.count();
            if (!var1) {
               this.nextToConsume = 0L;
               this.buffer.clear();
               var1 = this.fillBuffer();
            }

            return var1;
         }
      }

      abstract StreamSpliterators.AbstractWrappingSpliterator<P_IN, P_OUT, ?> wrap(Spliterator<P_IN> var1);

      abstract void initPartialTraversalState();

      public Spliterator<P_OUT> trySplit() {
         if (this.isParallel && !this.finished) {
            this.init();
            Spliterator var1 = this.spliterator.trySplit();
            return var1 == null ? null : this.wrap(var1);
         } else {
            return null;
         }
      }

      private boolean fillBuffer() {
         while(this.buffer.count() == 0L) {
            if (this.bufferSink.cancellationRequested() || !this.pusher.getAsBoolean()) {
               if (this.finished) {
                  return false;
               }

               this.bufferSink.end();
               this.finished = true;
            }
         }

         return true;
      }

      public final long estimateSize() {
         this.init();
         return this.spliterator.estimateSize();
      }

      public final long getExactSizeIfKnown() {
         this.init();
         return StreamOpFlag.SIZED.isKnown(this.ph.getStreamAndOpFlags()) ? this.spliterator.getExactSizeIfKnown() : -1L;
      }

      public final int characteristics() {
         this.init();
         int var1 = StreamOpFlag.toCharacteristics(StreamOpFlag.toStreamFlags(this.ph.getStreamAndOpFlags()));
         if ((var1 & 64) != 0) {
            var1 &= -16449;
            var1 |= this.spliterator.characteristics() & 16448;
         }

         return var1;
      }

      public Comparator<? super P_OUT> getComparator() {
         if (!this.hasCharacteristics(4)) {
            throw new IllegalStateException();
         } else {
            return null;
         }
      }

      public final String toString() {
         return String.format("%s[%s]", this.getClass().getName(), this.spliterator);
      }
   }
}
