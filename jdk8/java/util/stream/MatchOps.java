package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class MatchOps {
   private MatchOps() {
   }

   public static <T> TerminalOp<T, Boolean> makeRef(Predicate<? super T> var0, MatchOps.MatchKind var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return new MatchOps.MatchOp(StreamShape.REFERENCE, var1, () -> {
         class MatchSink extends MatchOps.BooleanTerminalSink<T> {
            MatchSink() {
               super(var0);
            }

            public void accept(T var1x) {
               if (!this.stop && var1.test(var1x) == var0.stopOnPredicateMatches) {
                  this.stop = true;
                  this.value = var0.shortCircuitResult;
               }

            }
         }

         return new MatchSink();
      });
   }

   public static TerminalOp<Integer, Boolean> makeInt(IntPredicate var0, MatchOps.MatchKind var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return new MatchOps.MatchOp(StreamShape.INT_VALUE, var1, () -> {
         class MatchSink extends MatchOps.BooleanTerminalSink<Integer> implements Sink.OfInt {
            MatchSink() {
               super(var0);
            }

            public void accept(int var1x) {
               if (!this.stop && var1.test(var1x) == var0.stopOnPredicateMatches) {
                  this.stop = true;
                  this.value = var0.shortCircuitResult;
               }

            }
         }

         return new MatchSink();
      });
   }

   public static TerminalOp<Long, Boolean> makeLong(LongPredicate var0, MatchOps.MatchKind var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return new MatchOps.MatchOp(StreamShape.LONG_VALUE, var1, () -> {
         class MatchSink extends MatchOps.BooleanTerminalSink<Long> implements Sink.OfLong {
            MatchSink() {
               super(var0);
            }

            public void accept(long var1x) {
               if (!this.stop && var1.test(var1x) == var0.stopOnPredicateMatches) {
                  this.stop = true;
                  this.value = var0.shortCircuitResult;
               }

            }
         }

         return new MatchSink();
      });
   }

   public static TerminalOp<Double, Boolean> makeDouble(DoublePredicate var0, MatchOps.MatchKind var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return new MatchOps.MatchOp(StreamShape.DOUBLE_VALUE, var1, () -> {
         class MatchSink extends MatchOps.BooleanTerminalSink<Double> implements Sink.OfDouble {
            MatchSink() {
               super(var0);
            }

            public void accept(double var1x) {
               if (!this.stop && var1.test(var1x) == var0.stopOnPredicateMatches) {
                  this.stop = true;
                  this.value = var0.shortCircuitResult;
               }

            }
         }

         return new MatchSink();
      });
   }

   private static final class MatchTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Boolean, MatchOps.MatchTask<P_IN, P_OUT>> {
      private final MatchOps.MatchOp<P_OUT> op;

      MatchTask(MatchOps.MatchOp<P_OUT> var1, PipelineHelper<P_OUT> var2, Spliterator<P_IN> var3) {
         super(var2, var3);
         this.op = var1;
      }

      MatchTask(MatchOps.MatchTask<P_IN, P_OUT> var1, Spliterator<P_IN> var2) {
         super((AbstractShortCircuitTask)var1, var2);
         this.op = var1.op;
      }

      protected MatchOps.MatchTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> var1) {
         return new MatchOps.MatchTask(this, var1);
      }

      protected Boolean doLeaf() {
         boolean var1 = ((MatchOps.BooleanTerminalSink)this.helper.wrapAndCopyInto((Sink)this.op.sinkSupplier.get(), this.spliterator)).getAndClearState();
         if (var1 == this.op.matchKind.shortCircuitResult) {
            this.shortCircuit(var1);
         }

         return null;
      }

      protected Boolean getEmptyResult() {
         return !this.op.matchKind.shortCircuitResult;
      }
   }

   private abstract static class BooleanTerminalSink<T> implements Sink<T> {
      boolean stop;
      boolean value;

      BooleanTerminalSink(MatchOps.MatchKind var1) {
         this.value = !var1.shortCircuitResult;
      }

      public boolean getAndClearState() {
         return this.value;
      }

      public boolean cancellationRequested() {
         return this.stop;
      }
   }

   private static final class MatchOp<T> implements TerminalOp<T, Boolean> {
      private final StreamShape inputShape;
      final MatchOps.MatchKind matchKind;
      final Supplier<MatchOps.BooleanTerminalSink<T>> sinkSupplier;

      MatchOp(StreamShape var1, MatchOps.MatchKind var2, Supplier<MatchOps.BooleanTerminalSink<T>> var3) {
         this.inputShape = var1;
         this.matchKind = var2;
         this.sinkSupplier = var3;
      }

      public int getOpFlags() {
         return StreamOpFlag.IS_SHORT_CIRCUIT | StreamOpFlag.NOT_ORDERED;
      }

      public StreamShape inputShape() {
         return this.inputShape;
      }

      public <S> Boolean evaluateSequential(PipelineHelper<T> var1, Spliterator<S> var2) {
         return ((MatchOps.BooleanTerminalSink)var1.wrapAndCopyInto((Sink)this.sinkSupplier.get(), var2)).getAndClearState();
      }

      public <S> Boolean evaluateParallel(PipelineHelper<T> var1, Spliterator<S> var2) {
         return (Boolean)(new MatchOps.MatchTask(this, var1, var2)).invoke();
      }
   }

   static enum MatchKind {
      ANY(true, true),
      ALL(false, false),
      NONE(true, false);

      private final boolean stopOnPredicateMatches;
      private final boolean shortCircuitResult;

      private MatchKind(boolean var3, boolean var4) {
         this.stopOnPredicateMatches = var3;
         this.shortCircuitResult = var4;
      }
   }
}
