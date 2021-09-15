package sun.nio.ch;

import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import sun.misc.InnocuousThread;
import sun.security.action.GetIntegerAction;

class Invoker {
   private static final int maxHandlerInvokeCount = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack", 16)));
   private static final ThreadLocal<Invoker.GroupAndInvokeCount> myGroupAndInvokeCount = new ThreadLocal<Invoker.GroupAndInvokeCount>() {
      protected Invoker.GroupAndInvokeCount initialValue() {
         return null;
      }
   };

   private Invoker() {
   }

   static void bindToGroup(AsynchronousChannelGroupImpl var0) {
      myGroupAndInvokeCount.set(new Invoker.GroupAndInvokeCount(var0));
   }

   static Invoker.GroupAndInvokeCount getGroupAndInvokeCount() {
      return (Invoker.GroupAndInvokeCount)myGroupAndInvokeCount.get();
   }

   static boolean isBoundToAnyGroup() {
      return myGroupAndInvokeCount.get() != null;
   }

   static boolean mayInvokeDirect(Invoker.GroupAndInvokeCount var0, AsynchronousChannelGroupImpl var1) {
      return var0 != null && var0.group() == var1 && var0.invokeCount() < maxHandlerInvokeCount;
   }

   static <V, A> void invokeUnchecked(CompletionHandler<V, ? super A> var0, A var1, V var2, Throwable var3) {
      if (var3 == null) {
         var0.completed(var2, var1);
      } else {
         var0.failed(var3, var1);
      }

      Thread.interrupted();
      if (System.getSecurityManager() != null) {
         Thread var4 = Thread.currentThread();
         if (var4 instanceof InnocuousThread) {
            Invoker.GroupAndInvokeCount var5 = (Invoker.GroupAndInvokeCount)myGroupAndInvokeCount.get();
            ((InnocuousThread)var4).eraseThreadLocals();
            if (var5 != null) {
               myGroupAndInvokeCount.set(var5);
            }
         }
      }

   }

   static <V, A> void invokeDirect(Invoker.GroupAndInvokeCount var0, CompletionHandler<V, ? super A> var1, A var2, V var3, Throwable var4) {
      var0.incrementInvokeCount();
      invokeUnchecked(var1, var2, var3, var4);
   }

   static <V, A> void invoke(AsynchronousChannel var0, CompletionHandler<V, ? super A> var1, A var2, V var3, Throwable var4) {
      boolean var5 = false;
      boolean var6 = false;
      Invoker.GroupAndInvokeCount var7 = (Invoker.GroupAndInvokeCount)myGroupAndInvokeCount.get();
      if (var7 != null) {
         if (var7.group() == ((Groupable)var0).group()) {
            var6 = true;
         }

         if (var6 && var7.invokeCount() < maxHandlerInvokeCount) {
            var5 = true;
         }
      }

      if (var5) {
         invokeDirect(var7, var1, var2, var3, var4);
      } else {
         try {
            invokeIndirectly(var0, var1, var2, var3, var4);
         } catch (RejectedExecutionException var9) {
            if (!var6) {
               throw new ShutdownChannelGroupException();
            }

            invokeDirect(var7, var1, var2, var3, var4);
         }
      }

   }

   static <V, A> void invokeIndirectly(AsynchronousChannel var0, final CompletionHandler<V, ? super A> var1, final A var2, final V var3, final Throwable var4) {
      try {
         ((Groupable)var0).group().executeOnPooledThread(new Runnable() {
            public void run() {
               Invoker.GroupAndInvokeCount var1x = (Invoker.GroupAndInvokeCount)Invoker.myGroupAndInvokeCount.get();
               if (var1x != null) {
                  var1x.setInvokeCount(1);
               }

               Invoker.invokeUnchecked(var1, var2, var3, var4);
            }
         });
      } catch (RejectedExecutionException var6) {
         throw new ShutdownChannelGroupException();
      }
   }

   static <V, A> void invokeIndirectly(final CompletionHandler<V, ? super A> var0, final A var1, final V var2, final Throwable var3, Executor var4) {
      try {
         var4.execute(new Runnable() {
            public void run() {
               Invoker.invokeUnchecked(var0, var1, var2, var3);
            }
         });
      } catch (RejectedExecutionException var6) {
         throw new ShutdownChannelGroupException();
      }
   }

   static void invokeOnThreadInThreadPool(Groupable var0, Runnable var1) {
      Invoker.GroupAndInvokeCount var3 = (Invoker.GroupAndInvokeCount)myGroupAndInvokeCount.get();
      AsynchronousChannelGroupImpl var4 = var0.group();
      boolean var2;
      if (var3 == null) {
         var2 = false;
      } else {
         var2 = var3.group == var4;
      }

      try {
         if (var2) {
            var1.run();
         } else {
            var4.executeOnPooledThread(var1);
         }

      } catch (RejectedExecutionException var6) {
         throw new ShutdownChannelGroupException();
      }
   }

   static <V, A> void invokeUnchecked(PendingFuture<V, A> var0) {
      assert var0.isDone();

      CompletionHandler var1 = var0.handler();
      if (var1 != null) {
         invokeUnchecked(var1, var0.attachment(), var0.value(), var0.exception());
      }

   }

   static <V, A> void invoke(PendingFuture<V, A> var0) {
      assert var0.isDone();

      CompletionHandler var1 = var0.handler();
      if (var1 != null) {
         invoke(var0.channel(), var1, var0.attachment(), var0.value(), var0.exception());
      }

   }

   static <V, A> void invokeIndirectly(PendingFuture<V, A> var0) {
      assert var0.isDone();

      CompletionHandler var1 = var0.handler();
      if (var1 != null) {
         invokeIndirectly(var0.channel(), var1, var0.attachment(), var0.value(), var0.exception());
      }

   }

   static class GroupAndInvokeCount {
      private final AsynchronousChannelGroupImpl group;
      private int handlerInvokeCount;

      GroupAndInvokeCount(AsynchronousChannelGroupImpl var1) {
         this.group = var1;
      }

      AsynchronousChannelGroupImpl group() {
         return this.group;
      }

      int invokeCount() {
         return this.handlerInvokeCount;
      }

      void setInvokeCount(int var1) {
         this.handlerInvokeCount = var1;
      }

      void resetInvokeCount() {
         this.handlerInvokeCount = 0;
      }

      void incrementInvokeCount() {
         ++this.handlerInvokeCount;
      }
   }
}
