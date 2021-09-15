package java.util.stream;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class FindOps {
   private FindOps() {
   }

   public static <T> TerminalOp<T, Optional<T>> makeRef(boolean var0) {
      return new FindOps.FindOp(var0, StreamShape.REFERENCE, Optional.empty(), Optional::isPresent, FindOps.FindSink.OfRef::new);
   }

   public static TerminalOp<Integer, OptionalInt> makeInt(boolean var0) {
      return new FindOps.FindOp(var0, StreamShape.INT_VALUE, OptionalInt.empty(), OptionalInt::isPresent, FindOps.FindSink.OfInt::new);
   }

   public static TerminalOp<Long, OptionalLong> makeLong(boolean var0) {
      return new FindOps.FindOp(var0, StreamShape.LONG_VALUE, OptionalLong.empty(), OptionalLong::isPresent, FindOps.FindSink.OfLong::new);
   }

   public static TerminalOp<Double, OptionalDouble> makeDouble(boolean var0) {
      return new FindOps.FindOp(var0, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), OptionalDouble::isPresent, FindOps.FindSink.OfDouble::new);
   }

   private static final class FindTask<P_IN, P_OUT, O> extends AbstractShortCircuitTask<P_IN, P_OUT, O, FindOps.FindTask<P_IN, P_OUT, O>> {
      private final FindOps.FindOp<P_OUT, O> op;

      FindTask(FindOps.FindOp<P_OUT, O> var1, PipelineHelper<P_OUT> var2, Spliterator<P_IN> var3) {
         super(var2, var3);
         this.op = var1;
      }

      FindTask(FindOps.FindTask<P_IN, P_OUT, O> var1, Spliterator<P_IN> var2) {
         super((AbstractShortCircuitTask)var1, var2);
         this.op = var1.op;
      }

      protected FindOps.FindTask<P_IN, P_OUT, O> makeChild(Spliterator<P_IN> var1) {
         return new FindOps.FindTask(this, var1);
      }

      protected O getEmptyResult() {
         return this.op.emptyValue;
      }

      private void foundResult(O var1) {
         if (this.isLeftmostNode()) {
            this.shortCircuit(var1);
         } else {
            this.cancelLaterNodes();
         }

      }

      protected O doLeaf() {
         Object var1 = ((TerminalSink)this.helper.wrapAndCopyInto((Sink)this.op.sinkSupplier.get(), this.spliterator)).get();
         if (!this.op.mustFindFirst) {
            if (var1 != null) {
               this.shortCircuit(var1);
            }

            return null;
         } else if (var1 != null) {
            this.foundResult(var1);
            return var1;
         } else {
            return null;
         }
      }

      public void onCompletion(CountedCompleter<?> var1) {
         if (this.op.mustFindFirst) {
            FindOps.FindTask var2 = (FindOps.FindTask)this.leftChild;

            for(FindOps.FindTask var3 = null; var2 != var3; var2 = (FindOps.FindTask)this.rightChild) {
               Object var4 = var2.getLocalResult();
               if (var4 != null && this.op.presentPredicate.test(var4)) {
                  this.setLocalResult(var4);
                  this.foundResult(var4);
                  break;
               }

               var3 = var2;
            }
         }

         super.onCompletion(var1);
      }
   }

   private abstract static class FindSink<T, O> implements TerminalSink<T, O> {
      boolean hasValue;
      T value;

      FindSink() {
      }

      public void accept(T var1) {
         if (!this.hasValue) {
            this.hasValue = true;
            this.value = var1;
         }

      }

      public boolean cancellationRequested() {
         return this.hasValue;
      }

      static final class OfDouble extends FindOps.FindSink<Double, OptionalDouble> implements Sink.OfDouble {
         public void accept(double var1) {
            this.accept(var1);
         }

         public OptionalDouble get() {
            return this.hasValue ? OptionalDouble.of((Double)this.value) : null;
         }
      }

      static final class OfLong extends FindOps.FindSink<Long, OptionalLong> implements Sink.OfLong {
         public void accept(long var1) {
            this.accept(var1);
         }

         public OptionalLong get() {
            return this.hasValue ? OptionalLong.of((Long)this.value) : null;
         }
      }

      static final class OfInt extends FindOps.FindSink<Integer, OptionalInt> implements Sink.OfInt {
         public void accept(int var1) {
            this.accept(var1);
         }

         public OptionalInt get() {
            return this.hasValue ? OptionalInt.of((Integer)this.value) : null;
         }
      }

      static final class OfRef<T> extends FindOps.FindSink<T, Optional<T>> {
         public Optional<T> get() {
            return this.hasValue ? Optional.of(this.value) : null;
         }
      }
   }

   private static final class FindOp<T, O> implements TerminalOp<T, O> {
      private final StreamShape shape;
      final boolean mustFindFirst;
      final O emptyValue;
      final Predicate<O> presentPredicate;
      final Supplier<TerminalSink<T, O>> sinkSupplier;

      FindOp(boolean var1, StreamShape var2, O var3, Predicate<O> var4, Supplier<TerminalSink<T, O>> var5) {
         this.mustFindFirst = var1;
         this.shape = var2;
         this.emptyValue = var3;
         this.presentPredicate = var4;
         this.sinkSupplier = var5;
      }

      public int getOpFlags() {
         return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED);
      }

      public StreamShape inputShape() {
         return this.shape;
      }

      public <S> O evaluateSequential(PipelineHelper<T> var1, Spliterator<S> var2) {
         Object var3 = ((TerminalSink)var1.wrapAndCopyInto((Sink)this.sinkSupplier.get(), var2)).get();
         return var3 != null ? var3 : this.emptyValue;
      }

      public <P_IN> O evaluateParallel(PipelineHelper<T> var1, Spliterator<P_IN> var2) {
         return (new FindOps.FindTask(this, var1, var2)).invoke();
      }
   }
}
