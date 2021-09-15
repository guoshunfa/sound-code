package java.util.concurrent;

public class ExecutorCompletionService<V> implements CompletionService<V> {
   private final Executor executor;
   private final AbstractExecutorService aes;
   private final BlockingQueue<Future<V>> completionQueue;

   private RunnableFuture<V> newTaskFor(Callable<V> var1) {
      return (RunnableFuture)(this.aes == null ? new FutureTask(var1) : this.aes.newTaskFor(var1));
   }

   private RunnableFuture<V> newTaskFor(Runnable var1, V var2) {
      return (RunnableFuture)(this.aes == null ? new FutureTask(var1, var2) : this.aes.newTaskFor(var1, var2));
   }

   public ExecutorCompletionService(Executor var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.executor = var1;
         this.aes = var1 instanceof AbstractExecutorService ? (AbstractExecutorService)var1 : null;
         this.completionQueue = new LinkedBlockingQueue();
      }
   }

   public ExecutorCompletionService(Executor var1, BlockingQueue<Future<V>> var2) {
      if (var1 != null && var2 != null) {
         this.executor = var1;
         this.aes = var1 instanceof AbstractExecutorService ? (AbstractExecutorService)var1 : null;
         this.completionQueue = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public Future<V> submit(Callable<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RunnableFuture var2 = this.newTaskFor(var1);
         this.executor.execute(new ExecutorCompletionService.QueueingFuture(var2));
         return var2;
      }
   }

   public Future<V> submit(Runnable var1, V var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RunnableFuture var3 = this.newTaskFor(var1, var2);
         this.executor.execute(new ExecutorCompletionService.QueueingFuture(var3));
         return var3;
      }
   }

   public Future<V> take() throws InterruptedException {
      return (Future)this.completionQueue.take();
   }

   public Future<V> poll() {
      return (Future)this.completionQueue.poll();
   }

   public Future<V> poll(long var1, TimeUnit var3) throws InterruptedException {
      return (Future)this.completionQueue.poll(var1, var3);
   }

   private class QueueingFuture extends FutureTask<Void> {
      private final Future<V> task;

      QueueingFuture(RunnableFuture<V> var2) {
         super(var2, (Object)null);
         this.task = var2;
      }

      protected void done() {
         ExecutorCompletionService.this.completionQueue.add(this.task);
      }
   }
}
