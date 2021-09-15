package java.util.stream;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

final class ReduceOps {
   private ReduceOps() {
   }

   public static <T, U> TerminalOp<T, U> makeRef(final U var0, final BiFunction<U, ? super T, U> var1, final BinaryOperator<U> var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<T, U, ReducingSink>(StreamShape.REFERENCE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1, var2);
         }
      };

      class ReducingSink extends ReduceOps.Box<U> implements ReduceOps.AccumulatingSink<T, U, ReducingSink> {
         // $FF: synthetic field
         final BiFunction val$reducer;
         // $FF: synthetic field
         final BinaryOperator val$combiner;

         ReducingSink(BiFunction var2, BinaryOperator var3) {
            this.val$reducer = var2;
            this.val$combiner = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this;
         }

         public void accept(T var1) {
            this.state = this.val$reducer.apply(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.state = this.val$combiner.apply(this.state, var1.state);
         }
      }

   }

   public static <T> TerminalOp<T, Optional<T>> makeRef(final BinaryOperator<T> var0) {
      Objects.requireNonNull(var0);
      return new ReduceOps.ReduceOp<T, Optional<T>, ReducingSink>(StreamShape.REFERENCE) {
         public ReducingSink makeSink() {
            return new ReducingSink();
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<T, Optional<T>, ReducingSink> {
         private boolean empty;
         private T state;

         public void begin(long var1) {
            this.empty = true;
            this.state = null;
         }

         public void accept(T var1) {
            if (this.empty) {
               this.empty = false;
               this.state = var1;
            } else {
               this.state = ReduceOps.this.apply(this.state, var1);
            }

         }

         public Optional<T> get() {
            return this.empty ? Optional.empty() : Optional.of(this.state);
         }

         public void combine(ReducingSink var1) {
            if (!var1.empty) {
               this.accept(var1.state);
            }

         }
      }

   }

   public static <T, I> TerminalOp<T, I> makeRef(final Collector<? super T, I, ?> var0) {
      final Supplier var1 = ((Collector)Objects.requireNonNull(var0)).supplier();
      final BiConsumer var2 = var0.accumulator();
      final BinaryOperator var3 = var0.combiner();
      return new ReduceOps.ReduceOp<T, I, ReducingSink>(StreamShape.REFERENCE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var2, var3);
         }

         public int getOpFlags() {
            return var0.characteristics().contains(Collector.Characteristics.UNORDERED) ? StreamOpFlag.NOT_ORDERED : 0;
         }
      };

      class ReducingSink extends ReduceOps.Box<I> implements ReduceOps.AccumulatingSink<T, I, ReducingSink> {
         // $FF: synthetic field
         final BiConsumer val$accumulator;
         // $FF: synthetic field
         final BinaryOperator val$combiner;

         ReducingSink(BiConsumer var2, BinaryOperator var3) {
            this.val$accumulator = var2;
            this.val$combiner = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this.get();
         }

         public void accept(T var1) {
            this.val$accumulator.accept(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.state = this.val$combiner.apply(this.state, var1.state);
         }
      }

   }

   public static <T, R> TerminalOp<T, R> makeRef(final Supplier<R> var0, final BiConsumer<R, ? super T> var1, final BiConsumer<R, R> var2) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<T, R, ReducingSink>(StreamShape.REFERENCE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1, var2);
         }
      };

      class ReducingSink extends ReduceOps.Box<R> implements ReduceOps.AccumulatingSink<T, R, ReducingSink> {
         // $FF: synthetic field
         final BiConsumer val$accumulator;
         // $FF: synthetic field
         final BiConsumer val$reducer;

         ReducingSink(BiConsumer var2, BiConsumer var3) {
            this.val$accumulator = var2;
            this.val$reducer = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this.get();
         }

         public void accept(T var1) {
            this.val$accumulator.accept(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.val$reducer.accept(this.state, var1.state);
         }
      }

   }

   public static TerminalOp<Integer, Integer> makeInt(final int var0, final IntBinaryOperator var1) {
      Objects.requireNonNull(var1);
      return new ReduceOps.ReduceOp<Integer, Integer, ReducingSink>(StreamShape.INT_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1);
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Integer, Integer, ReducingSink>, Sink.OfInt {
         private int state;
         // $FF: synthetic field
         final IntBinaryOperator val$operator;

         ReducingSink(IntBinaryOperator var2) {
            this.val$operator = var2;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this;
         }

         public void accept(int var1) {
            this.state = this.val$operator.applyAsInt(this.state, var1);
         }

         public Integer get() {
            return this.state;
         }

         public void combine(ReducingSink var1) {
            this.accept(var1.state);
         }
      }

   }

   public static TerminalOp<Integer, OptionalInt> makeInt(final IntBinaryOperator var0) {
      Objects.requireNonNull(var0);
      return new ReduceOps.ReduceOp<Integer, OptionalInt, ReducingSink>(StreamShape.INT_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink();
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Integer, OptionalInt, ReducingSink>, Sink.OfInt {
         private boolean empty;
         private int state;

         public void begin(long var1) {
            this.empty = true;
            this.state = 0;
         }

         public void accept(int var1) {
            if (this.empty) {
               this.empty = false;
               this.state = var1;
            } else {
               this.state = ReduceOps.this.applyAsInt(this.state, var1);
            }

         }

         public OptionalInt get() {
            return this.empty ? OptionalInt.empty() : OptionalInt.of(this.state);
         }

         public void combine(ReducingSink var1) {
            if (!var1.empty) {
               this.accept(var1.state);
            }

         }
      }

   }

   public static <R> TerminalOp<Integer, R> makeInt(final Supplier<R> var0, final ObjIntConsumer<R> var1, final BinaryOperator<R> var2) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<Integer, R, ReducingSink>(StreamShape.INT_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1, var2);
         }
      };

      class ReducingSink extends ReduceOps.Box<R> implements ReduceOps.AccumulatingSink<Integer, R, ReducingSink>, Sink.OfInt {
         // $FF: synthetic field
         final ObjIntConsumer val$accumulator;
         // $FF: synthetic field
         final BinaryOperator val$combiner;

         ReducingSink(ObjIntConsumer var2, BinaryOperator var3) {
            this.val$accumulator = var2;
            this.val$combiner = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this.get();
         }

         public void accept(int var1) {
            this.val$accumulator.accept(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.state = this.val$combiner.apply(this.state, var1.state);
         }
      }

   }

   public static TerminalOp<Long, Long> makeLong(final long var0, final LongBinaryOperator var2) {
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<Long, Long, ReducingSink>(StreamShape.LONG_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var2);
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Long, Long, ReducingSink>, Sink.OfLong {
         private long state;
         // $FF: synthetic field
         final LongBinaryOperator val$operator;

         ReducingSink(LongBinaryOperator var3) {
            this.val$operator = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this;
         }

         public void accept(long var1) {
            this.state = this.val$operator.applyAsLong(this.state, var1);
         }

         public Long get() {
            return this.state;
         }

         public void combine(ReducingSink var1) {
            this.accept(var1.state);
         }
      }

   }

   public static TerminalOp<Long, OptionalLong> makeLong(final LongBinaryOperator var0) {
      Objects.requireNonNull(var0);
      return new ReduceOps.ReduceOp<Long, OptionalLong, ReducingSink>(StreamShape.LONG_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink();
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Long, OptionalLong, ReducingSink>, Sink.OfLong {
         private boolean empty;
         private long state;

         public void begin(long var1) {
            this.empty = true;
            this.state = 0L;
         }

         public void accept(long var1) {
            if (this.empty) {
               this.empty = false;
               this.state = var1;
            } else {
               this.state = ReduceOps.this.applyAsLong(this.state, var1);
            }

         }

         public OptionalLong get() {
            return this.empty ? OptionalLong.empty() : OptionalLong.of(this.state);
         }

         public void combine(ReducingSink var1) {
            if (!var1.empty) {
               this.accept(var1.state);
            }

         }
      }

   }

   public static <R> TerminalOp<Long, R> makeLong(final Supplier<R> var0, final ObjLongConsumer<R> var1, final BinaryOperator<R> var2) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<Long, R, ReducingSink>(StreamShape.LONG_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1, var2);
         }
      };

      class ReducingSink extends ReduceOps.Box<R> implements ReduceOps.AccumulatingSink<Long, R, ReducingSink>, Sink.OfLong {
         // $FF: synthetic field
         final ObjLongConsumer val$accumulator;
         // $FF: synthetic field
         final BinaryOperator val$combiner;

         ReducingSink(ObjLongConsumer var2, BinaryOperator var3) {
            this.val$accumulator = var2;
            this.val$combiner = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this.get();
         }

         public void accept(long var1) {
            this.val$accumulator.accept(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.state = this.val$combiner.apply(this.state, var1.state);
         }
      }

   }

   public static TerminalOp<Double, Double> makeDouble(final double var0, final DoubleBinaryOperator var2) {
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<Double, Double, ReducingSink>(StreamShape.DOUBLE_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var2);
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Double, Double, ReducingSink>, Sink.OfDouble {
         private double state;
         // $FF: synthetic field
         final DoubleBinaryOperator val$operator;

         ReducingSink(DoubleBinaryOperator var3) {
            this.val$operator = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this;
         }

         public void accept(double var1) {
            this.state = this.val$operator.applyAsDouble(this.state, var1);
         }

         public Double get() {
            return this.state;
         }

         public void combine(ReducingSink var1) {
            this.accept(var1.state);
         }
      }

   }

   public static TerminalOp<Double, OptionalDouble> makeDouble(final DoubleBinaryOperator var0) {
      Objects.requireNonNull(var0);
      return new ReduceOps.ReduceOp<Double, OptionalDouble, ReducingSink>(StreamShape.DOUBLE_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink();
         }
      };

      class ReducingSink implements ReduceOps.AccumulatingSink<Double, OptionalDouble, ReducingSink>, Sink.OfDouble {
         private boolean empty;
         private double state;

         public void begin(long var1) {
            this.empty = true;
            this.state = 0.0D;
         }

         public void accept(double var1) {
            if (this.empty) {
               this.empty = false;
               this.state = var1;
            } else {
               this.state = ReduceOps.this.applyAsDouble(this.state, var1);
            }

         }

         public OptionalDouble get() {
            return this.empty ? OptionalDouble.empty() : OptionalDouble.of(this.state);
         }

         public void combine(ReducingSink var1) {
            if (!var1.empty) {
               this.accept(var1.state);
            }

         }
      }

   }

   public static <R> TerminalOp<Double, R> makeDouble(final Supplier<R> var0, final ObjDoubleConsumer<R> var1, final BinaryOperator<R> var2) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return new ReduceOps.ReduceOp<Double, R, ReducingSink>(StreamShape.DOUBLE_VALUE) {
         public ReducingSink makeSink() {
            return new ReducingSink(var1, var2);
         }
      };

      class ReducingSink extends ReduceOps.Box<R> implements ReduceOps.AccumulatingSink<Double, R, ReducingSink>, Sink.OfDouble {
         // $FF: synthetic field
         final ObjDoubleConsumer val$accumulator;
         // $FF: synthetic field
         final BinaryOperator val$combiner;

         ReducingSink(ObjDoubleConsumer var2, BinaryOperator var3) {
            this.val$accumulator = var2;
            this.val$combiner = var3;
         }

         public void begin(long var1) {
            this.state = ReduceOps.this.get();
         }

         public void accept(double var1) {
            this.val$accumulator.accept(this.state, var1);
         }

         public void combine(ReducingSink var1) {
            this.state = this.val$combiner.apply(this.state, var1.state);
         }
      }

   }

   private static final class ReduceTask<P_IN, P_OUT, R, S extends ReduceOps.AccumulatingSink<P_OUT, R, S>> extends AbstractTask<P_IN, P_OUT, S, ReduceOps.ReduceTask<P_IN, P_OUT, R, S>> {
      private final ReduceOps.ReduceOp<P_OUT, R, S> op;

      ReduceTask(ReduceOps.ReduceOp<P_OUT, R, S> var1, PipelineHelper<P_OUT> var2, Spliterator<P_IN> var3) {
         super(var2, var3);
         this.op = var1;
      }

      ReduceTask(ReduceOps.ReduceTask<P_IN, P_OUT, R, S> var1, Spliterator<P_IN> var2) {
         super((AbstractTask)var1, var2);
         this.op = var1.op;
      }

      protected ReduceOps.ReduceTask<P_IN, P_OUT, R, S> makeChild(Spliterator<P_IN> var1) {
         return new ReduceOps.ReduceTask(this, var1);
      }

      protected S doLeaf() {
         return (ReduceOps.AccumulatingSink)this.helper.wrapAndCopyInto(this.op.makeSink(), this.spliterator);
      }

      public void onCompletion(CountedCompleter<?> var1) {
         if (!this.isLeaf()) {
            ReduceOps.AccumulatingSink var2 = (ReduceOps.AccumulatingSink)((ReduceOps.ReduceTask)this.leftChild).getLocalResult();
            var2.combine((ReduceOps.AccumulatingSink)((ReduceOps.ReduceTask)this.rightChild).getLocalResult());
            this.setLocalResult(var2);
         }

         super.onCompletion(var1);
      }
   }

   private abstract static class ReduceOp<T, R, S extends ReduceOps.AccumulatingSink<T, R, S>> implements TerminalOp<T, R> {
      private final StreamShape inputShape;

      ReduceOp(StreamShape var1) {
         this.inputShape = var1;
      }

      public abstract S makeSink();

      public StreamShape inputShape() {
         return this.inputShape;
      }

      public <P_IN> R evaluateSequential(PipelineHelper<T> var1, Spliterator<P_IN> var2) {
         return ((ReduceOps.AccumulatingSink)var1.wrapAndCopyInto(this.makeSink(), var2)).get();
      }

      public <P_IN> R evaluateParallel(PipelineHelper<T> var1, Spliterator<P_IN> var2) {
         return ((ReduceOps.AccumulatingSink)(new ReduceOps.ReduceTask(this, var1, var2)).invoke()).get();
      }
   }

   private abstract static class Box<U> {
      U state;

      Box() {
      }

      public U get() {
         return this.state;
      }
   }

   private interface AccumulatingSink<T, R, K extends ReduceOps.AccumulatingSink<T, R, K>> extends TerminalSink<T, R> {
      void combine(K var1);
   }
}
