package java.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntFunction;

final class SortedOps {
   private SortedOps() {
   }

   static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> var0) {
      return new SortedOps.OfRef(var0);
   }

   static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> var0, Comparator<? super T> var1) {
      return new SortedOps.OfRef(var0, var1);
   }

   static <T> IntStream makeInt(AbstractPipeline<?, Integer, ?> var0) {
      return new SortedOps.OfInt(var0);
   }

   static <T> LongStream makeLong(AbstractPipeline<?, Long, ?> var0) {
      return new SortedOps.OfLong(var0);
   }

   static <T> DoubleStream makeDouble(AbstractPipeline<?, Double, ?> var0) {
      return new SortedOps.OfDouble(var0);
   }

   private static final class DoubleSortingSink extends SortedOps.AbstractDoubleSortingSink {
      private SpinedBuffer.OfDouble b;

      DoubleSortingSink(Sink<? super Double> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.b = var1 > 0L ? new SpinedBuffer.OfDouble((int)var1) : new SpinedBuffer.OfDouble();
         }
      }

      public void end() {
         double[] var1 = (double[])this.b.asPrimitiveArray();
         Arrays.sort(var1);
         this.downstream.begin((long)var1.length);
         double[] var2;
         int var3;
         int var4;
         double var5;
         if (!this.cancellationWasRequested) {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               this.downstream.accept(var5);
            }
         } else {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               if (this.downstream.cancellationRequested()) {
                  break;
               }

               this.downstream.accept(var5);
            }
         }

         this.downstream.end();
      }

      public void accept(double var1) {
         this.b.accept(var1);
      }
   }

   private static final class SizedDoubleSortingSink extends SortedOps.AbstractDoubleSortingSink {
      private double[] array;
      private int offset;

      SizedDoubleSortingSink(Sink<? super Double> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new double[(int)var1];
         }
      }

      public void end() {
         Arrays.sort((double[])this.array, 0, this.offset);
         this.downstream.begin((long)this.offset);
         int var1;
         if (!this.cancellationWasRequested) {
            for(var1 = 0; var1 < this.offset; ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         } else {
            for(var1 = 0; var1 < this.offset && !this.downstream.cancellationRequested(); ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         }

         this.downstream.end();
         this.array = null;
      }

      public void accept(double var1) {
         this.array[this.offset++] = var1;
      }
   }

   private abstract static class AbstractDoubleSortingSink extends Sink.ChainedDouble<Double> {
      protected boolean cancellationWasRequested;

      AbstractDoubleSortingSink(Sink<? super Double> var1) {
         super(var1);
      }

      public final boolean cancellationRequested() {
         this.cancellationWasRequested = true;
         return false;
      }
   }

   private static final class LongSortingSink extends SortedOps.AbstractLongSortingSink {
      private SpinedBuffer.OfLong b;

      LongSortingSink(Sink<? super Long> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.b = var1 > 0L ? new SpinedBuffer.OfLong((int)var1) : new SpinedBuffer.OfLong();
         }
      }

      public void end() {
         long[] var1 = (long[])this.b.asPrimitiveArray();
         Arrays.sort(var1);
         this.downstream.begin((long)var1.length);
         long[] var2;
         int var3;
         int var4;
         long var5;
         if (!this.cancellationWasRequested) {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               this.downstream.accept(var5);
            }
         } else {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               if (this.downstream.cancellationRequested()) {
                  break;
               }

               this.downstream.accept(var5);
            }
         }

         this.downstream.end();
      }

      public void accept(long var1) {
         this.b.accept(var1);
      }
   }

   private static final class SizedLongSortingSink extends SortedOps.AbstractLongSortingSink {
      private long[] array;
      private int offset;

      SizedLongSortingSink(Sink<? super Long> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new long[(int)var1];
         }
      }

      public void end() {
         Arrays.sort((long[])this.array, 0, this.offset);
         this.downstream.begin((long)this.offset);
         int var1;
         if (!this.cancellationWasRequested) {
            for(var1 = 0; var1 < this.offset; ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         } else {
            for(var1 = 0; var1 < this.offset && !this.downstream.cancellationRequested(); ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         }

         this.downstream.end();
         this.array = null;
      }

      public void accept(long var1) {
         this.array[this.offset++] = var1;
      }
   }

   private abstract static class AbstractLongSortingSink extends Sink.ChainedLong<Long> {
      protected boolean cancellationWasRequested;

      AbstractLongSortingSink(Sink<? super Long> var1) {
         super(var1);
      }

      public final boolean cancellationRequested() {
         this.cancellationWasRequested = true;
         return false;
      }
   }

   private static final class IntSortingSink extends SortedOps.AbstractIntSortingSink {
      private SpinedBuffer.OfInt b;

      IntSortingSink(Sink<? super Integer> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.b = var1 > 0L ? new SpinedBuffer.OfInt((int)var1) : new SpinedBuffer.OfInt();
         }
      }

      public void end() {
         int[] var1 = (int[])this.b.asPrimitiveArray();
         Arrays.sort(var1);
         this.downstream.begin((long)var1.length);
         int[] var2;
         int var3;
         int var4;
         int var5;
         if (!this.cancellationWasRequested) {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               this.downstream.accept(var5);
            }
         } else {
            var2 = var1;
            var3 = var1.length;

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var2[var4];
               if (this.downstream.cancellationRequested()) {
                  break;
               }

               this.downstream.accept(var5);
            }
         }

         this.downstream.end();
      }

      public void accept(int var1) {
         this.b.accept(var1);
      }
   }

   private static final class SizedIntSortingSink extends SortedOps.AbstractIntSortingSink {
      private int[] array;
      private int offset;

      SizedIntSortingSink(Sink<? super Integer> var1) {
         super(var1);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new int[(int)var1];
         }
      }

      public void end() {
         Arrays.sort((int[])this.array, 0, this.offset);
         this.downstream.begin((long)this.offset);
         int var1;
         if (!this.cancellationWasRequested) {
            for(var1 = 0; var1 < this.offset; ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         } else {
            for(var1 = 0; var1 < this.offset && !this.downstream.cancellationRequested(); ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         }

         this.downstream.end();
         this.array = null;
      }

      public void accept(int var1) {
         this.array[this.offset++] = var1;
      }
   }

   private abstract static class AbstractIntSortingSink extends Sink.ChainedInt<Integer> {
      protected boolean cancellationWasRequested;

      AbstractIntSortingSink(Sink<? super Integer> var1) {
         super(var1);
      }

      public final boolean cancellationRequested() {
         this.cancellationWasRequested = true;
         return false;
      }
   }

   private static final class RefSortingSink<T> extends SortedOps.AbstractRefSortingSink<T> {
      private ArrayList<T> list;

      RefSortingSink(Sink<? super T> var1, Comparator<? super T> var2) {
         super(var1, var2);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.list = var1 >= 0L ? new ArrayList((int)var1) : new ArrayList();
         }
      }

      public void end() {
         this.list.sort(this.comparator);
         this.downstream.begin((long)this.list.size());
         if (!this.cancellationWasRequested) {
            Sink var10001 = this.downstream;
            this.list.forEach(var10001::accept);
         } else {
            Iterator var1 = this.list.iterator();

            while(var1.hasNext()) {
               Object var2 = var1.next();
               if (this.downstream.cancellationRequested()) {
                  break;
               }

               this.downstream.accept(var2);
            }
         }

         this.downstream.end();
         this.list = null;
      }

      public void accept(T var1) {
         this.list.add(var1);
      }
   }

   private static final class SizedRefSortingSink<T> extends SortedOps.AbstractRefSortingSink<T> {
      private T[] array;
      private int offset;

      SizedRefSortingSink(Sink<? super T> var1, Comparator<? super T> var2) {
         super(var1, var2);
      }

      public void begin(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = (Object[])(new Object[(int)var1]);
         }
      }

      public void end() {
         Arrays.sort(this.array, 0, this.offset, this.comparator);
         this.downstream.begin((long)this.offset);
         int var1;
         if (!this.cancellationWasRequested) {
            for(var1 = 0; var1 < this.offset; ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         } else {
            for(var1 = 0; var1 < this.offset && !this.downstream.cancellationRequested(); ++var1) {
               this.downstream.accept(this.array[var1]);
            }
         }

         this.downstream.end();
         this.array = null;
      }

      public void accept(T var1) {
         this.array[this.offset++] = var1;
      }
   }

   private abstract static class AbstractRefSortingSink<T> extends Sink.ChainedReference<T, T> {
      protected final Comparator<? super T> comparator;
      protected boolean cancellationWasRequested;

      AbstractRefSortingSink(Sink<? super T> var1, Comparator<? super T> var2) {
         super(var1);
         this.comparator = var2;
      }

      public final boolean cancellationRequested() {
         this.cancellationWasRequested = true;
         return false;
      }
   }

   private static final class OfDouble extends DoublePipeline.StatefulOp<Double> {
      OfDouble(AbstractPipeline<?, Double, ?> var1) {
         super(var1, StreamShape.DOUBLE_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
      }

      public Sink<Double> opWrapSink(int var1, Sink<Double> var2) {
         Objects.requireNonNull(var2);
         if (StreamOpFlag.SORTED.isKnown(var1)) {
            return var2;
         } else {
            return (Sink)(StreamOpFlag.SIZED.isKnown(var1) ? new SortedOps.SizedDoubleSortingSink(var2) : new SortedOps.DoubleSortingSink(var2));
         }
      }

      public <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> var1, Spliterator<P_IN> var2, IntFunction<Double[]> var3) {
         if (StreamOpFlag.SORTED.isKnown(var1.getStreamAndOpFlags())) {
            return var1.evaluate(var2, false, var3);
         } else {
            Node.OfDouble var4 = (Node.OfDouble)var1.evaluate(var2, true, var3);
            double[] var5 = (double[])var4.asPrimitiveArray();
            Arrays.parallelSort(var5);
            return Nodes.node(var5);
         }
      }
   }

   private static final class OfLong extends LongPipeline.StatefulOp<Long> {
      OfLong(AbstractPipeline<?, Long, ?> var1) {
         super(var1, StreamShape.LONG_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
      }

      public Sink<Long> opWrapSink(int var1, Sink<Long> var2) {
         Objects.requireNonNull(var2);
         if (StreamOpFlag.SORTED.isKnown(var1)) {
            return var2;
         } else {
            return (Sink)(StreamOpFlag.SIZED.isKnown(var1) ? new SortedOps.SizedLongSortingSink(var2) : new SortedOps.LongSortingSink(var2));
         }
      }

      public <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> var1, Spliterator<P_IN> var2, IntFunction<Long[]> var3) {
         if (StreamOpFlag.SORTED.isKnown(var1.getStreamAndOpFlags())) {
            return var1.evaluate(var2, false, var3);
         } else {
            Node.OfLong var4 = (Node.OfLong)var1.evaluate(var2, true, var3);
            long[] var5 = (long[])var4.asPrimitiveArray();
            Arrays.parallelSort(var5);
            return Nodes.node(var5);
         }
      }
   }

   private static final class OfInt extends IntPipeline.StatefulOp<Integer> {
      OfInt(AbstractPipeline<?, Integer, ?> var1) {
         super(var1, StreamShape.INT_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
      }

      public Sink<Integer> opWrapSink(int var1, Sink<Integer> var2) {
         Objects.requireNonNull(var2);
         if (StreamOpFlag.SORTED.isKnown(var1)) {
            return var2;
         } else {
            return (Sink)(StreamOpFlag.SIZED.isKnown(var1) ? new SortedOps.SizedIntSortingSink(var2) : new SortedOps.IntSortingSink(var2));
         }
      }

      public <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> var1, Spliterator<P_IN> var2, IntFunction<Integer[]> var3) {
         if (StreamOpFlag.SORTED.isKnown(var1.getStreamAndOpFlags())) {
            return var1.evaluate(var2, false, var3);
         } else {
            Node.OfInt var4 = (Node.OfInt)var1.evaluate(var2, true, var3);
            int[] var5 = (int[])var4.asPrimitiveArray();
            Arrays.parallelSort(var5);
            return Nodes.node(var5);
         }
      }
   }

   private static final class OfRef<T> extends ReferencePipeline.StatefulOp<T, T> {
      private final boolean isNaturalSort;
      private final Comparator<? super T> comparator;

      OfRef(AbstractPipeline<?, T, ?> var1) {
         super(var1, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
         this.isNaturalSort = true;
         Comparator var2 = Comparator.naturalOrder();
         this.comparator = var2;
      }

      OfRef(AbstractPipeline<?, T, ?> var1, Comparator<? super T> var2) {
         super(var1, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.NOT_SORTED);
         this.isNaturalSort = false;
         this.comparator = (Comparator)Objects.requireNonNull(var2);
      }

      public Sink<T> opWrapSink(int var1, Sink<T> var2) {
         Objects.requireNonNull(var2);
         if (StreamOpFlag.SORTED.isKnown(var1) && this.isNaturalSort) {
            return var2;
         } else {
            return (Sink)(StreamOpFlag.SIZED.isKnown(var1) ? new SortedOps.SizedRefSortingSink(var2, this.comparator) : new SortedOps.RefSortingSink(var2, this.comparator));
         }
      }

      public <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> var1, Spliterator<P_IN> var2, IntFunction<T[]> var3) {
         if (StreamOpFlag.SORTED.isKnown(var1.getStreamAndOpFlags()) && this.isNaturalSort) {
            return var1.evaluate(var2, false, var3);
         } else {
            Object[] var4 = var1.evaluate(var2, true, var3).asArray(var3);
            Arrays.parallelSort(var4, this.comparator);
            return Nodes.node(var4);
         }
      }
   }
}
