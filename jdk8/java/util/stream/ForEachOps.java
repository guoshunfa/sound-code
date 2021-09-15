package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

final class ForEachOps {
   private ForEachOps() {
   }

   public static <T> TerminalOp<T, Void> makeRef(Consumer<? super T> var0, boolean var1) {
      Objects.requireNonNull(var0);
      return new ForEachOps.ForEachOp.OfRef(var0, var1);
   }

   public static TerminalOp<Integer, Void> makeInt(IntConsumer var0, boolean var1) {
      Objects.requireNonNull(var0);
      return new ForEachOps.ForEachOp.OfInt(var0, var1);
   }

   public static TerminalOp<Long, Void> makeLong(LongConsumer var0, boolean var1) {
      Objects.requireNonNull(var0);
      return new ForEachOps.ForEachOp.OfLong(var0, var1);
   }

   public static TerminalOp<Double, Void> makeDouble(DoubleConsumer var0, boolean var1) {
      Objects.requireNonNull(var0);
      return new ForEachOps.ForEachOp.OfDouble(var0, var1);
   }

   static final class ForEachOrderedTask<S, T> extends CountedCompleter<Void> {
      private final PipelineHelper<T> helper;
      private Spliterator<S> spliterator;
      private final long targetSize;
      private final ConcurrentHashMap<ForEachOps.ForEachOrderedTask<S, T>, ForEachOps.ForEachOrderedTask<S, T>> completionMap;
      private final Sink<T> action;
      private final ForEachOps.ForEachOrderedTask<S, T> leftPredecessor;
      private Node<T> node;

      protected ForEachOrderedTask(PipelineHelper<T> var1, Spliterator<S> var2, Sink<T> var3) {
         super((CountedCompleter)null);
         this.helper = var1;
         this.spliterator = var2;
         this.targetSize = AbstractTask.suggestTargetSize(var2.estimateSize());
         this.completionMap = new ConcurrentHashMap(Math.max(16, AbstractTask.LEAF_TARGET << 1));
         this.action = var3;
         this.leftPredecessor = null;
      }

      ForEachOrderedTask(ForEachOps.ForEachOrderedTask<S, T> var1, Spliterator<S> var2, ForEachOps.ForEachOrderedTask<S, T> var3) {
         super(var1);
         this.helper = var1.helper;
         this.spliterator = var2;
         this.targetSize = var1.targetSize;
         this.completionMap = var1.completionMap;
         this.action = var1.action;
         this.leftPredecessor = var3;
      }

      public final void compute() {
         doCompute(this);
      }

      private static <S, T> void doCompute(ForEachOps.ForEachOrderedTask<S, T> var0) {
         Spliterator var1 = var0.spliterator;
         long var3 = var0.targetSize;

         Spliterator var2;
         ForEachOps.ForEachOrderedTask var8;
         for(boolean var5 = false; var1.estimateSize() > var3 && (var2 = var1.trySplit()) != null; var8.fork()) {
            ForEachOps.ForEachOrderedTask var6 = new ForEachOps.ForEachOrderedTask(var0, var2, var0.leftPredecessor);
            ForEachOps.ForEachOrderedTask var7 = new ForEachOps.ForEachOrderedTask(var0, var1, var6);
            var0.addToPendingCount(1);
            var7.addToPendingCount(1);
            var0.completionMap.put(var6, var7);
            if (var0.leftPredecessor != null) {
               var6.addToPendingCount(1);
               if (var0.completionMap.replace(var0.leftPredecessor, var0, var6)) {
                  var0.addToPendingCount(-1);
               } else {
                  var6.addToPendingCount(-1);
               }
            }

            if (var5) {
               var5 = false;
               var1 = var2;
               var0 = var6;
               var8 = var7;
            } else {
               var5 = true;
               var0 = var7;
               var8 = var6;
            }
         }

         if (var0.getPendingCount() > 0) {
            IntFunction var9 = (var0x) -> {
               return (Object[])(new Object[var0x]);
            };
            Node.Builder var10 = var0.helper.makeNodeBuilder(var0.helper.exactOutputSizeIfKnown(var1), var9);
            var0.node = ((Node.Builder)var0.helper.wrapAndCopyInto(var10, var1)).build();
            var0.spliterator = null;
         }

         var0.tryComplete();
      }

      public void onCompletion(CountedCompleter<?> var1) {
         if (this.node != null) {
            this.node.forEach(this.action);
            this.node = null;
         } else if (this.spliterator != null) {
            this.helper.wrapAndCopyInto(this.action, this.spliterator);
            this.spliterator = null;
         }

         ForEachOps.ForEachOrderedTask var2 = (ForEachOps.ForEachOrderedTask)this.completionMap.remove(this);
         if (var2 != null) {
            var2.tryComplete();
         }

      }
   }

   static final class ForEachTask<S, T> extends CountedCompleter<Void> {
      private Spliterator<S> spliterator;
      private final Sink<S> sink;
      private final PipelineHelper<T> helper;
      private long targetSize;

      ForEachTask(PipelineHelper<T> var1, Spliterator<S> var2, Sink<S> var3) {
         super((CountedCompleter)null);
         this.sink = var3;
         this.helper = var1;
         this.spliterator = var2;
         this.targetSize = 0L;
      }

      ForEachTask(ForEachOps.ForEachTask<S, T> var1, Spliterator<S> var2) {
         super(var1);
         this.spliterator = var2;
         this.sink = var1.sink;
         this.targetSize = var1.targetSize;
         this.helper = var1.helper;
      }

      public void compute() {
         Spliterator var1 = this.spliterator;
         long var3 = var1.estimateSize();
         long var5;
         if ((var5 = this.targetSize) == 0L) {
            this.targetSize = var5 = AbstractTask.suggestTargetSize(var3);
         }

         boolean var7 = StreamOpFlag.SHORT_CIRCUIT.isKnown(this.helper.getStreamAndOpFlags());
         boolean var8 = false;
         Sink var9 = this.sink;

         ForEachOps.ForEachTask var10;
         for(var10 = this; !var7 || !var9.cancellationRequested(); var3 = var1.estimateSize()) {
            Spliterator var2;
            if (var3 <= var5 || (var2 = var1.trySplit()) == null) {
               var10.helper.copyInto(var9, var1);
               break;
            }

            ForEachOps.ForEachTask var11 = new ForEachOps.ForEachTask(var10, var2);
            var10.addToPendingCount(1);
            ForEachOps.ForEachTask var12;
            if (var8) {
               var8 = false;
               var1 = var2;
               var12 = var10;
               var10 = var11;
            } else {
               var8 = true;
               var12 = var11;
            }

            var12.fork();
         }

         var10.spliterator = null;
         var10.propagateCompletion();
      }
   }

   abstract static class ForEachOp<T> implements TerminalOp<T, Void>, TerminalSink<T, Void> {
      private final boolean ordered;

      protected ForEachOp(boolean var1) {
         this.ordered = var1;
      }

      public int getOpFlags() {
         return this.ordered ? 0 : StreamOpFlag.NOT_ORDERED;
      }

      public <S> Void evaluateSequential(PipelineHelper<T> var1, Spliterator<S> var2) {
         return ((ForEachOps.ForEachOp)var1.wrapAndCopyInto(this, var2)).get();
      }

      public <S> Void evaluateParallel(PipelineHelper<T> var1, Spliterator<S> var2) {
         if (this.ordered) {
            (new ForEachOps.ForEachOrderedTask(var1, var2, this)).invoke();
         } else {
            (new ForEachOps.ForEachTask(var1, var2, var1.wrapSink(this))).invoke();
         }

         return null;
      }

      public Void get() {
         return null;
      }

      static final class OfDouble extends ForEachOps.ForEachOp<Double> implements Sink.OfDouble {
         final DoubleConsumer consumer;

         OfDouble(DoubleConsumer var1, boolean var2) {
            super(var2);
            this.consumer = var1;
         }

         public StreamShape inputShape() {
            return StreamShape.DOUBLE_VALUE;
         }

         public void accept(double var1) {
            this.consumer.accept(var1);
         }
      }

      static final class OfLong extends ForEachOps.ForEachOp<Long> implements Sink.OfLong {
         final LongConsumer consumer;

         OfLong(LongConsumer var1, boolean var2) {
            super(var2);
            this.consumer = var1;
         }

         public StreamShape inputShape() {
            return StreamShape.LONG_VALUE;
         }

         public void accept(long var1) {
            this.consumer.accept(var1);
         }
      }

      static final class OfInt extends ForEachOps.ForEachOp<Integer> implements Sink.OfInt {
         final IntConsumer consumer;

         OfInt(IntConsumer var1, boolean var2) {
            super(var2);
            this.consumer = var1;
         }

         public StreamShape inputShape() {
            return StreamShape.INT_VALUE;
         }

         public void accept(int var1) {
            this.consumer.accept(var1);
         }
      }

      static final class OfRef<T> extends ForEachOps.ForEachOp<T> {
         final Consumer<? super T> consumer;

         OfRef(Consumer<? super T> var1, boolean var2) {
            super(var2);
            this.consumer = var1;
         }

         public void accept(T var1) {
            this.consumer.accept(var1);
         }
      }
   }
}
