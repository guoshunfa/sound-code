package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import sun.misc.Unsafe;

public class ForkJoinWorkerThread extends Thread {
   final ForkJoinPool pool;
   final ForkJoinPool.WorkQueue workQueue;
   private static final Unsafe U;
   private static final long THREADLOCALS;
   private static final long INHERITABLETHREADLOCALS;
   private static final long INHERITEDACCESSCONTROLCONTEXT;

   protected ForkJoinWorkerThread(ForkJoinPool var1) {
      super("aForkJoinWorkerThread");
      this.pool = var1;
      this.workQueue = var1.registerWorker(this);
   }

   ForkJoinWorkerThread(ForkJoinPool var1, ThreadGroup var2, AccessControlContext var3) {
      super(var2, (Runnable)null, "aForkJoinWorkerThread");
      U.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, var3);
      this.eraseThreadLocals();
      this.pool = var1;
      this.workQueue = var1.registerWorker(this);
   }

   public ForkJoinPool getPool() {
      return this.pool;
   }

   public int getPoolIndex() {
      return this.workQueue.getPoolIndex();
   }

   protected void onStart() {
   }

   protected void onTermination(Throwable var1) {
   }

   public void run() {
      if (this.workQueue.array == null) {
         Throwable var1 = null;

         try {
            this.onStart();
            this.pool.runWorker(this.workQueue);
         } catch (Throwable var40) {
            var1 = var40;
         } finally {
            try {
               this.onTermination(var1);
            } catch (Throwable var41) {
               if (var1 == null) {
                  var1 = var41;
               }
            } finally {
               this.pool.deregisterWorker(this, var1);
            }

         }
      }

   }

   final void eraseThreadLocals() {
      U.putObject(this, THREADLOCALS, (Object)null);
      U.putObject(this, INHERITABLETHREADLOCALS, (Object)null);
   }

   void afterTopLevelExec() {
   }

   static {
      try {
         U = Unsafe.getUnsafe();
         Class var0 = Thread.class;
         THREADLOCALS = U.objectFieldOffset(var0.getDeclaredField("threadLocals"));
         INHERITABLETHREADLOCALS = U.objectFieldOffset(var0.getDeclaredField("inheritableThreadLocals"));
         INHERITEDACCESSCONTROLCONTEXT = U.objectFieldOffset(var0.getDeclaredField("inheritedAccessControlContext"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class InnocuousForkJoinWorkerThread extends ForkJoinWorkerThread {
      private static final ThreadGroup innocuousThreadGroup = createThreadGroup();
      private static final AccessControlContext INNOCUOUS_ACC = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, (PermissionCollection)null)});

      InnocuousForkJoinWorkerThread(ForkJoinPool var1) {
         super(var1, innocuousThreadGroup, INNOCUOUS_ACC);
      }

      void afterTopLevelExec() {
         this.eraseThreadLocals();
      }

      public ClassLoader getContextClassLoader() {
         return ClassLoader.getSystemClassLoader();
      }

      public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler var1) {
      }

      public void setContextClassLoader(ClassLoader var1) {
         throw new SecurityException("setContextClassLoader");
      }

      private static ThreadGroup createThreadGroup() {
         try {
            Unsafe var0 = Unsafe.getUnsafe();
            Class var1 = Thread.class;
            Class var2 = ThreadGroup.class;
            long var3 = var0.objectFieldOffset(var1.getDeclaredField("group"));
            long var5 = var0.objectFieldOffset(var2.getDeclaredField("parent"));

            ThreadGroup var8;
            for(ThreadGroup var7 = (ThreadGroup)var0.getObject(Thread.currentThread(), var3); var7 != null; var7 = var8) {
               var8 = (ThreadGroup)var0.getObject(var7, var5);
               if (var8 == null) {
                  return new ThreadGroup(var7, "InnocuousForkJoinWorkerThreadGroup");
               }
            }
         } catch (Exception var9) {
            throw new Error(var9);
         }

         throw new Error("Cannot create ThreadGroup");
      }
   }
}
