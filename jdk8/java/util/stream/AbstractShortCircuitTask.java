package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractShortCircuitTask<P_IN, P_OUT, R, K extends AbstractShortCircuitTask<P_IN, P_OUT, R, K>> extends AbstractTask<P_IN, P_OUT, R, K> {
   protected final AtomicReference<R> sharedResult;
   protected volatile boolean canceled;

   protected AbstractShortCircuitTask(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2) {
      super(var1, var2);
      this.sharedResult = new AtomicReference((Object)null);
   }

   protected AbstractShortCircuitTask(K var1, Spliterator<P_IN> var2) {
      super((AbstractTask)var1, var2);
      this.sharedResult = var1.sharedResult;
   }

   protected abstract R getEmptyResult();

   public void compute() {
      Spliterator var1 = this.spliterator;
      long var3 = var1.estimateSize();
      long var5 = this.getTargetSize(var3);
      boolean var7 = false;
      AbstractShortCircuitTask var8 = this;

      Object var10;
      for(AtomicReference var9 = this.sharedResult; (var10 = var9.get()) == null; var3 = var1.estimateSize()) {
         if (var8.taskCanceled()) {
            var10 = var8.getEmptyResult();
            break;
         }

         Spliterator var2;
         if (var3 <= var5 || (var2 = var1.trySplit()) == null) {
            var10 = var8.doLeaf();
            break;
         }

         AbstractShortCircuitTask var11;
         var8.leftChild = var11 = (AbstractShortCircuitTask)var8.makeChild(var2);
         AbstractShortCircuitTask var12;
         var8.rightChild = var12 = (AbstractShortCircuitTask)var8.makeChild(var1);
         var8.setPendingCount(1);
         AbstractShortCircuitTask var13;
         if (var7) {
            var7 = false;
            var1 = var2;
            var8 = var11;
            var13 = var12;
         } else {
            var7 = true;
            var8 = var12;
            var13 = var11;
         }

         var13.fork();
      }

      var8.setLocalResult(var10);
      var8.tryComplete();
   }

   protected void shortCircuit(R var1) {
      if (var1 != null) {
         this.sharedResult.compareAndSet((Object)null, var1);
      }

   }

   protected void setLocalResult(R var1) {
      if (this.isRoot()) {
         if (var1 != null) {
            this.sharedResult.compareAndSet((Object)null, var1);
         }
      } else {
         super.setLocalResult(var1);
      }

   }

   public R getRawResult() {
      return this.getLocalResult();
   }

   public R getLocalResult() {
      if (this.isRoot()) {
         Object var1 = this.sharedResult.get();
         return var1 == null ? this.getEmptyResult() : var1;
      } else {
         return super.getLocalResult();
      }
   }

   protected void cancel() {
      this.canceled = true;
   }

   protected boolean taskCanceled() {
      boolean var1 = this.canceled;
      if (!var1) {
         for(AbstractShortCircuitTask var2 = (AbstractShortCircuitTask)this.getParent(); !var1 && var2 != null; var2 = (AbstractShortCircuitTask)var2.getParent()) {
            var1 = var2.canceled;
         }
      }

      return var1;
   }

   protected void cancelLaterNodes() {
      AbstractShortCircuitTask var1 = (AbstractShortCircuitTask)this.getParent();

      for(AbstractShortCircuitTask var2 = this; var1 != null; var1 = (AbstractShortCircuitTask)var1.getParent()) {
         if (var1.leftChild == var2) {
            AbstractShortCircuitTask var3 = (AbstractShortCircuitTask)var1.rightChild;
            if (!var3.canceled) {
               var3.cancel();
            }
         }

         var2 = var1;
      }

   }
}
