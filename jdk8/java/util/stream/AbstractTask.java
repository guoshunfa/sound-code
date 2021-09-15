package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
   static final int LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
   protected final PipelineHelper<P_OUT> helper;
   protected Spliterator<P_IN> spliterator;
   protected long targetSize;
   protected K leftChild;
   protected K rightChild;
   private R localResult;

   protected AbstractTask(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2) {
      super((CountedCompleter)null);
      this.helper = var1;
      this.spliterator = var2;
      this.targetSize = 0L;
   }

   protected AbstractTask(K var1, Spliterator<P_IN> var2) {
      super(var1);
      this.spliterator = var2;
      this.helper = var1.helper;
      this.targetSize = var1.targetSize;
   }

   protected abstract K makeChild(Spliterator<P_IN> var1);

   protected abstract R doLeaf();

   public static long suggestTargetSize(long var0) {
      long var2 = var0 / (long)LEAF_TARGET;
      return var2 > 0L ? var2 : 1L;
   }

   protected final long getTargetSize(long var1) {
      long var3;
      return (var3 = this.targetSize) != 0L ? var3 : (this.targetSize = suggestTargetSize(var1));
   }

   public R getRawResult() {
      return this.localResult;
   }

   protected void setRawResult(R var1) {
      if (var1 != null) {
         throw new IllegalStateException();
      }
   }

   protected R getLocalResult() {
      return this.localResult;
   }

   protected void setLocalResult(R var1) {
      this.localResult = var1;
   }

   protected boolean isLeaf() {
      return this.leftChild == null;
   }

   protected boolean isRoot() {
      return this.getParent() == null;
   }

   protected K getParent() {
      return (AbstractTask)this.getCompleter();
   }

   public void compute() {
      Spliterator var1 = this.spliterator;
      long var3 = var1.estimateSize();
      long var5 = this.getTargetSize(var3);
      boolean var7 = false;

      Spliterator var2;
      AbstractTask var8;
      for(var8 = this; var3 > var5 && (var2 = var1.trySplit()) != null; var3 = var1.estimateSize()) {
         AbstractTask var9;
         var8.leftChild = var9 = var8.makeChild(var2);
         AbstractTask var10;
         var8.rightChild = var10 = var8.makeChild(var1);
         var8.setPendingCount(1);
         AbstractTask var11;
         if (var7) {
            var7 = false;
            var1 = var2;
            var8 = var9;
            var11 = var10;
         } else {
            var7 = true;
            var8 = var10;
            var11 = var9;
         }

         var11.fork();
      }

      var8.setLocalResult(var8.doLeaf());
      var8.tryComplete();
   }

   public void onCompletion(CountedCompleter<?> var1) {
      this.spliterator = null;
      this.leftChild = this.rightChild = null;
   }

   protected boolean isLeftmostNode() {
      AbstractTask var2;
      for(AbstractTask var1 = this; var1 != null; var1 = var2) {
         var2 = var1.getParent();
         if (var2 != null && var2.leftChild != var1) {
            return false;
         }
      }

      return true;
   }
}
