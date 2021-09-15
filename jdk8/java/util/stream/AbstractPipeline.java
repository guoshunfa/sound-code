package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

abstract class AbstractPipeline<E_IN, E_OUT, S extends BaseStream<E_OUT, S>> extends PipelineHelper<E_OUT> implements BaseStream<E_OUT, S> {
   private static final String MSG_STREAM_LINKED = "stream has already been operated upon or closed";
   private static final String MSG_CONSUMED = "source already consumed or closed";
   private final AbstractPipeline sourceStage;
   private final AbstractPipeline previousStage;
   protected final int sourceOrOpFlags;
   private AbstractPipeline nextStage;
   private int depth;
   private int combinedFlags;
   private Spliterator<?> sourceSpliterator;
   private Supplier<? extends Spliterator<?>> sourceSupplier;
   private boolean linkedOrConsumed;
   private boolean sourceAnyStateful;
   private Runnable sourceCloseAction;
   private boolean parallel;

   AbstractPipeline(Supplier<? extends Spliterator<?>> var1, int var2, boolean var3) {
      this.previousStage = null;
      this.sourceSupplier = var1;
      this.sourceStage = this;
      this.sourceOrOpFlags = var2 & StreamOpFlag.STREAM_MASK;
      this.combinedFlags = ~(this.sourceOrOpFlags << 1) & StreamOpFlag.INITIAL_OPS_VALUE;
      this.depth = 0;
      this.parallel = var3;
   }

   AbstractPipeline(Spliterator<?> var1, int var2, boolean var3) {
      this.previousStage = null;
      this.sourceSpliterator = var1;
      this.sourceStage = this;
      this.sourceOrOpFlags = var2 & StreamOpFlag.STREAM_MASK;
      this.combinedFlags = ~(this.sourceOrOpFlags << 1) & StreamOpFlag.INITIAL_OPS_VALUE;
      this.depth = 0;
      this.parallel = var3;
   }

   AbstractPipeline(AbstractPipeline<?, E_IN, ?> var1, int var2) {
      if (var1.linkedOrConsumed) {
         throw new IllegalStateException("stream has already been operated upon or closed");
      } else {
         var1.linkedOrConsumed = true;
         var1.nextStage = this;
         this.previousStage = var1;
         this.sourceOrOpFlags = var2 & StreamOpFlag.OP_MASK;
         this.combinedFlags = StreamOpFlag.combineOpFlags(var2, var1.combinedFlags);
         this.sourceStage = var1.sourceStage;
         if (this.opIsStateful()) {
            this.sourceStage.sourceAnyStateful = true;
         }

         this.depth = var1.depth + 1;
      }
   }

   final <R> R evaluate(TerminalOp<E_OUT, R> var1) {
      assert this.getOutputShape() == var1.inputShape();

      if (this.linkedOrConsumed) {
         throw new IllegalStateException("stream has already been operated upon or closed");
      } else {
         this.linkedOrConsumed = true;
         return this.isParallel() ? var1.evaluateParallel(this, this.sourceSpliterator(var1.getOpFlags())) : var1.evaluateSequential(this, this.sourceSpliterator(var1.getOpFlags()));
      }
   }

   final Node<E_OUT> evaluateToArrayNode(IntFunction<E_OUT[]> var1) {
      if (this.linkedOrConsumed) {
         throw new IllegalStateException("stream has already been operated upon or closed");
      } else {
         this.linkedOrConsumed = true;
         if (this.isParallel() && this.previousStage != null && this.opIsStateful()) {
            this.depth = 0;
            return this.opEvaluateParallel(this.previousStage, this.previousStage.sourceSpliterator(0), var1);
         } else {
            return this.evaluate(this.sourceSpliterator(0), true, var1);
         }
      }
   }

   final Spliterator<E_OUT> sourceStageSpliterator() {
      if (this != this.sourceStage) {
         throw new IllegalStateException();
      } else if (this.linkedOrConsumed) {
         throw new IllegalStateException("stream has already been operated upon or closed");
      } else {
         this.linkedOrConsumed = true;
         Spliterator var1;
         if (this.sourceStage.sourceSpliterator != null) {
            var1 = this.sourceStage.sourceSpliterator;
            this.sourceStage.sourceSpliterator = null;
            return var1;
         } else if (this.sourceStage.sourceSupplier != null) {
            var1 = (Spliterator)this.sourceStage.sourceSupplier.get();
            this.sourceStage.sourceSupplier = null;
            return var1;
         } else {
            throw new IllegalStateException("source already consumed or closed");
         }
      }
   }

   public final S sequential() {
      this.sourceStage.parallel = false;
      return this;
   }

   public final S parallel() {
      this.sourceStage.parallel = true;
      return this;
   }

   public void close() {
      this.linkedOrConsumed = true;
      this.sourceSupplier = null;
      this.sourceSpliterator = null;
      if (this.sourceStage.sourceCloseAction != null) {
         Runnable var1 = this.sourceStage.sourceCloseAction;
         this.sourceStage.sourceCloseAction = null;
         var1.run();
      }

   }

   public S onClose(Runnable var1) {
      Runnable var2 = this.sourceStage.sourceCloseAction;
      this.sourceStage.sourceCloseAction = var2 == null ? var1 : Streams.composeWithExceptions(var2, var1);
      return this;
   }

   public Spliterator<E_OUT> spliterator() {
      if (this.linkedOrConsumed) {
         throw new IllegalStateException("stream has already been operated upon or closed");
      } else {
         this.linkedOrConsumed = true;
         if (this == this.sourceStage) {
            if (this.sourceStage.sourceSpliterator != null) {
               Spliterator var2 = this.sourceStage.sourceSpliterator;
               this.sourceStage.sourceSpliterator = null;
               return var2;
            } else if (this.sourceStage.sourceSupplier != null) {
               Supplier var1 = this.sourceStage.sourceSupplier;
               this.sourceStage.sourceSupplier = null;
               return this.lazySpliterator(var1);
            } else {
               throw new IllegalStateException("source already consumed or closed");
            }
         } else {
            return this.wrap(this, () -> {
               return this.sourceSpliterator(0);
            }, this.isParallel());
         }
      }
   }

   public final boolean isParallel() {
      return this.sourceStage.parallel;
   }

   final int getStreamFlags() {
      return StreamOpFlag.toStreamFlags(this.combinedFlags);
   }

   private Spliterator<?> sourceSpliterator(int var1) {
      Spliterator var2 = null;
      if (this.sourceStage.sourceSpliterator != null) {
         var2 = this.sourceStage.sourceSpliterator;
         this.sourceStage.sourceSpliterator = null;
      } else {
         if (this.sourceStage.sourceSupplier == null) {
            throw new IllegalStateException("source already consumed or closed");
         }

         var2 = (Spliterator)this.sourceStage.sourceSupplier.get();
         this.sourceStage.sourceSupplier = null;
      }

      if (this.isParallel() && this.sourceStage.sourceAnyStateful) {
         int var3 = 1;
         AbstractPipeline var4 = this.sourceStage;
         AbstractPipeline var5 = this.sourceStage.nextStage;

         for(AbstractPipeline var6 = this; var4 != var6; var5 = var5.nextStage) {
            int var7 = var5.sourceOrOpFlags;
            if (var5.opIsStateful()) {
               var3 = 0;
               if (StreamOpFlag.SHORT_CIRCUIT.isKnown(var7)) {
                  var7 &= ~StreamOpFlag.IS_SHORT_CIRCUIT;
               }

               var2 = var5.opEvaluateParallelLazy(var4, var2);
               var7 = var2.hasCharacteristics(64) ? var7 & ~StreamOpFlag.NOT_SIZED | StreamOpFlag.IS_SIZED : var7 & ~StreamOpFlag.IS_SIZED | StreamOpFlag.NOT_SIZED;
            }

            var5.depth = var3++;
            var5.combinedFlags = StreamOpFlag.combineOpFlags(var7, var4.combinedFlags);
            var4 = var5;
         }
      }

      if (var1 != 0) {
         this.combinedFlags = StreamOpFlag.combineOpFlags(var1, this.combinedFlags);
      }

      return var2;
   }

   final StreamShape getSourceShape() {
      AbstractPipeline var1;
      for(var1 = this; var1.depth > 0; var1 = var1.previousStage) {
      }

      return var1.getOutputShape();
   }

   final <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> var1) {
      return StreamOpFlag.SIZED.isKnown(this.getStreamAndOpFlags()) ? var1.getExactSizeIfKnown() : -1L;
   }

   final <P_IN, S extends Sink<E_OUT>> S wrapAndCopyInto(S var1, Spliterator<P_IN> var2) {
      this.copyInto(this.wrapSink((Sink)Objects.requireNonNull(var1)), var2);
      return var1;
   }

   final <P_IN> void copyInto(Sink<P_IN> var1, Spliterator<P_IN> var2) {
      Objects.requireNonNull(var1);
      if (!StreamOpFlag.SHORT_CIRCUIT.isKnown(this.getStreamAndOpFlags())) {
         var1.begin(var2.getExactSizeIfKnown());
         var2.forEachRemaining(var1);
         var1.end();
      } else {
         this.copyIntoWithCancel(var1, var2);
      }

   }

   final <P_IN> void copyIntoWithCancel(Sink<P_IN> var1, Spliterator<P_IN> var2) {
      AbstractPipeline var3;
      for(var3 = this; var3.depth > 0; var3 = var3.previousStage) {
      }

      var1.begin(var2.getExactSizeIfKnown());
      var3.forEachWithCancel(var2, var1);
      var1.end();
   }

   final int getStreamAndOpFlags() {
      return this.combinedFlags;
   }

   final boolean isOrdered() {
      return StreamOpFlag.ORDERED.isKnown(this.combinedFlags);
   }

   final <P_IN> Sink<P_IN> wrapSink(Sink<E_OUT> var1) {
      Objects.requireNonNull(var1);

      for(AbstractPipeline var2 = this; var2.depth > 0; var2 = var2.previousStage) {
         var1 = var2.opWrapSink(var2.previousStage.combinedFlags, var1);
      }

      return var1;
   }

   final <P_IN> Spliterator<E_OUT> wrapSpliterator(Spliterator<P_IN> var1) {
      return this.depth == 0 ? var1 : this.wrap(this, () -> {
         return var1;
      }, this.isParallel());
   }

   final <P_IN> Node<E_OUT> evaluate(Spliterator<P_IN> var1, boolean var2, IntFunction<E_OUT[]> var3) {
      if (this.isParallel()) {
         return this.evaluateToNode(this, var1, var2, var3);
      } else {
         Node.Builder var4 = this.makeNodeBuilder(this.exactOutputSizeIfKnown(var1), var3);
         return ((Node.Builder)this.wrapAndCopyInto(var4, var1)).build();
      }
   }

   abstract StreamShape getOutputShape();

   abstract <P_IN> Node<E_OUT> evaluateToNode(PipelineHelper<E_OUT> var1, Spliterator<P_IN> var2, boolean var3, IntFunction<E_OUT[]> var4);

   abstract <P_IN> Spliterator<E_OUT> wrap(PipelineHelper<E_OUT> var1, Supplier<Spliterator<P_IN>> var2, boolean var3);

   abstract Spliterator<E_OUT> lazySpliterator(Supplier<? extends Spliterator<E_OUT>> var1);

   abstract void forEachWithCancel(Spliterator<E_OUT> var1, Sink<E_OUT> var2);

   abstract Node.Builder<E_OUT> makeNodeBuilder(long var1, IntFunction<E_OUT[]> var3);

   abstract boolean opIsStateful();

   abstract Sink<E_IN> opWrapSink(int var1, Sink<E_OUT> var2);

   <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> var1, Spliterator<P_IN> var2, IntFunction<E_OUT[]> var3) {
      throw new UnsupportedOperationException("Parallel evaluation is not supported");
   }

   <P_IN> Spliterator<E_OUT> opEvaluateParallelLazy(PipelineHelper<E_OUT> var1, Spliterator<P_IN> var2) {
      return this.opEvaluateParallel(var1, var2, (var0) -> {
         return (Object[])(new Object[var0]);
      }).spliterator();
   }
}
