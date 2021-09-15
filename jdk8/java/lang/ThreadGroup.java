package java.lang;

import java.io.PrintStream;
import java.util.Arrays;
import sun.misc.VM;

public class ThreadGroup implements Thread.UncaughtExceptionHandler {
   private final ThreadGroup parent;
   String name;
   int maxPriority;
   boolean destroyed;
   boolean daemon;
   boolean vmAllowSuspension;
   int nUnstartedThreads;
   int nthreads;
   Thread[] threads;
   int ngroups;
   ThreadGroup[] groups;

   private ThreadGroup() {
      this.nUnstartedThreads = 0;
      this.name = "system";
      this.maxPriority = 10;
      this.parent = null;
   }

   public ThreadGroup(String var1) {
      this(Thread.currentThread().getThreadGroup(), var1);
   }

   public ThreadGroup(ThreadGroup var1, String var2) {
      this(checkParentAccess(var1), var1, var2);
   }

   private ThreadGroup(Void var1, ThreadGroup var2, String var3) {
      this.nUnstartedThreads = 0;
      this.name = var3;
      this.maxPriority = var2.maxPriority;
      this.daemon = var2.daemon;
      this.vmAllowSuspension = var2.vmAllowSuspension;
      this.parent = var2;
      var2.add(this);
   }

   private static Void checkParentAccess(ThreadGroup var0) {
      var0.checkAccess();
      return null;
   }

   public final String getName() {
      return this.name;
   }

   public final ThreadGroup getParent() {
      if (this.parent != null) {
         this.parent.checkAccess();
      }

      return this.parent;
   }

   public final int getMaxPriority() {
      return this.maxPriority;
   }

   public final boolean isDaemon() {
      return this.daemon;
   }

   public synchronized boolean isDestroyed() {
      return this.destroyed;
   }

   public final void setDaemon(boolean var1) {
      this.checkAccess();
      this.daemon = var1;
   }

   public final void setMaxPriority(int var1) {
      int var2;
      ThreadGroup[] var3;
      synchronized(this) {
         this.checkAccess();
         if (var1 < 1 || var1 > 10) {
            return;
         }

         this.maxPriority = this.parent != null ? Math.min(var1, this.parent.maxPriority) : var1;
         var2 = this.ngroups;
         if (this.groups != null) {
            var3 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var2);
         } else {
            var3 = null;
         }
      }

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4].setMaxPriority(var1);
      }

   }

   public final boolean parentOf(ThreadGroup var1) {
      while(var1 != null) {
         if (var1 == this) {
            return true;
         }

         var1 = var1.parent;
      }

      return false;
   }

   public final void checkAccess() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkAccess(this);
      }

   }

   public int activeCount() {
      int var1;
      int var2;
      ThreadGroup[] var3;
      synchronized(this) {
         if (this.destroyed) {
            return 0;
         }

         var1 = this.nthreads;
         var2 = this.ngroups;
         if (this.groups != null) {
            var3 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var2);
         } else {
            var3 = null;
         }
      }

      for(int var4 = 0; var4 < var2; ++var4) {
         var1 += var3[var4].activeCount();
      }

      return var1;
   }

   public int enumerate(Thread[] var1) {
      this.checkAccess();
      return this.enumerate((Thread[])var1, 0, true);
   }

   public int enumerate(Thread[] var1, boolean var2) {
      this.checkAccess();
      return this.enumerate((Thread[])var1, 0, var2);
   }

   private int enumerate(Thread[] var1, int var2, boolean var3) {
      int var4 = 0;
      ThreadGroup[] var5 = null;
      synchronized(this) {
         if (this.destroyed) {
            return 0;
         }

         int var7 = this.nthreads;
         if (var7 > var1.length - var2) {
            var7 = var1.length - var2;
         }

         for(int var8 = 0; var8 < var7; ++var8) {
            if (this.threads[var8].isAlive()) {
               var1[var2++] = this.threads[var8];
            }
         }

         if (var3) {
            var4 = this.ngroups;
            if (this.groups != null) {
               var5 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var4);
            } else {
               var5 = null;
            }
         }
      }

      if (var3) {
         for(int var6 = 0; var6 < var4; ++var6) {
            var2 = var5[var6].enumerate(var1, var2, true);
         }
      }

      return var2;
   }

   public int activeGroupCount() {
      int var1;
      ThreadGroup[] var2;
      synchronized(this) {
         if (this.destroyed) {
            return 0;
         }

         var1 = this.ngroups;
         if (this.groups != null) {
            var2 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var1);
         } else {
            var2 = null;
         }
      }

      int var3 = var1;

      for(int var4 = 0; var4 < var1; ++var4) {
         var3 += var2[var4].activeGroupCount();
      }

      return var3;
   }

   public int enumerate(ThreadGroup[] var1) {
      this.checkAccess();
      return this.enumerate((ThreadGroup[])var1, 0, true);
   }

   public int enumerate(ThreadGroup[] var1, boolean var2) {
      this.checkAccess();
      return this.enumerate((ThreadGroup[])var1, 0, var2);
   }

   private int enumerate(ThreadGroup[] var1, int var2, boolean var3) {
      int var4 = 0;
      ThreadGroup[] var5 = null;
      synchronized(this) {
         if (this.destroyed) {
            return 0;
         }

         int var7 = this.ngroups;
         if (var7 > var1.length - var2) {
            var7 = var1.length - var2;
         }

         if (var7 > 0) {
            System.arraycopy(this.groups, 0, var1, var2, var7);
            var2 += var7;
         }

         if (var3) {
            var4 = this.ngroups;
            if (this.groups != null) {
               var5 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var4);
            } else {
               var5 = null;
            }
         }
      }

      if (var3) {
         for(int var6 = 0; var6 < var4; ++var6) {
            var2 = var5[var6].enumerate(var1, var2, true);
         }
      }

      return var2;
   }

   /** @deprecated */
   @Deprecated
   public final void stop() {
      if (this.stopOrSuspend(false)) {
         Thread.currentThread().stop();
      }

   }

   public final void interrupt() {
      int var1;
      ThreadGroup[] var2;
      synchronized(this) {
         this.checkAccess();
         int var4 = 0;

         while(true) {
            if (var4 >= this.nthreads) {
               var1 = this.ngroups;
               if (this.groups != null) {
                  var2 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var1);
               } else {
                  var2 = null;
               }
               break;
            }

            this.threads[var4].interrupt();
            ++var4;
         }
      }

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3].interrupt();
      }

   }

   /** @deprecated */
   @Deprecated
   public final void suspend() {
      if (this.stopOrSuspend(true)) {
         Thread.currentThread().suspend();
      }

   }

   private boolean stopOrSuspend(boolean var1) {
      boolean var2 = false;
      Thread var3 = Thread.currentThread();
      ThreadGroup[] var5 = null;
      int var4;
      synchronized(this) {
         this.checkAccess();
         int var7 = 0;

         while(true) {
            if (var7 >= this.nthreads) {
               var4 = this.ngroups;
               if (this.groups != null) {
                  var5 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var4);
               }
               break;
            }

            if (this.threads[var7] == var3) {
               var2 = true;
            } else if (var1) {
               this.threads[var7].suspend();
            } else {
               this.threads[var7].stop();
            }

            ++var7;
         }
      }

      for(int var6 = 0; var6 < var4; ++var6) {
         var2 = var5[var6].stopOrSuspend(var1) || var2;
      }

      return var2;
   }

   /** @deprecated */
   @Deprecated
   public final void resume() {
      int var1;
      ThreadGroup[] var2;
      synchronized(this) {
         this.checkAccess();
         int var4 = 0;

         while(true) {
            if (var4 >= this.nthreads) {
               var1 = this.ngroups;
               if (this.groups != null) {
                  var2 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var1);
               } else {
                  var2 = null;
               }
               break;
            }

            this.threads[var4].resume();
            ++var4;
         }
      }

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3].resume();
      }

   }

   public final void destroy() {
      int var1;
      ThreadGroup[] var2;
      synchronized(this) {
         this.checkAccess();
         if (this.destroyed || this.nthreads > 0) {
            throw new IllegalThreadStateException();
         }

         var1 = this.ngroups;
         if (this.groups != null) {
            var2 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var1);
         } else {
            var2 = null;
         }

         if (this.parent != null) {
            this.destroyed = true;
            this.ngroups = 0;
            this.groups = null;
            this.nthreads = 0;
            this.threads = null;
         }
      }

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3].destroy();
      }

      if (this.parent != null) {
         this.parent.remove(this);
      }

   }

   private final void add(ThreadGroup var1) {
      synchronized(this) {
         if (this.destroyed) {
            throw new IllegalThreadStateException();
         } else {
            if (this.groups == null) {
               this.groups = new ThreadGroup[4];
            } else if (this.ngroups == this.groups.length) {
               this.groups = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, this.ngroups * 2);
            }

            this.groups[this.ngroups] = var1;
            ++this.ngroups;
         }
      }
   }

   private void remove(ThreadGroup var1) {
      synchronized(this) {
         if (!this.destroyed) {
            for(int var3 = 0; var3 < this.ngroups; ++var3) {
               if (this.groups[var3] == var1) {
                  --this.ngroups;
                  System.arraycopy(this.groups, var3 + 1, this.groups, var3, this.ngroups - var3);
                  this.groups[this.ngroups] = null;
                  break;
               }
            }

            if (this.nthreads == 0) {
               this.notifyAll();
            }

            if (this.daemon && this.nthreads == 0 && this.nUnstartedThreads == 0 && this.ngroups == 0) {
               this.destroy();
            }

         }
      }
   }

   void addUnstarted() {
      synchronized(this) {
         if (this.destroyed) {
            throw new IllegalThreadStateException();
         } else {
            ++this.nUnstartedThreads;
         }
      }
   }

   void add(Thread var1) {
      synchronized(this) {
         if (this.destroyed) {
            throw new IllegalThreadStateException();
         } else {
            if (this.threads == null) {
               this.threads = new Thread[4];
            } else if (this.nthreads == this.threads.length) {
               this.threads = (Thread[])Arrays.copyOf((Object[])this.threads, this.nthreads * 2);
            }

            this.threads[this.nthreads] = var1;
            ++this.nthreads;
            --this.nUnstartedThreads;
         }
      }
   }

   void threadStartFailed(Thread var1) {
      synchronized(this) {
         this.remove(var1);
         ++this.nUnstartedThreads;
      }
   }

   void threadTerminated(Thread var1) {
      synchronized(this) {
         this.remove(var1);
         if (this.nthreads == 0) {
            this.notifyAll();
         }

         if (this.daemon && this.nthreads == 0 && this.nUnstartedThreads == 0 && this.ngroups == 0) {
            this.destroy();
         }

      }
   }

   private void remove(Thread var1) {
      synchronized(this) {
         if (!this.destroyed) {
            for(int var3 = 0; var3 < this.nthreads; ++var3) {
               if (this.threads[var3] == var1) {
                  System.arraycopy(this.threads, var3 + 1, this.threads, var3, --this.nthreads - var3);
                  this.threads[this.nthreads] = null;
                  break;
               }
            }

         }
      }
   }

   public void list() {
      this.list(System.out, 0);
   }

   void list(PrintStream var1, int var2) {
      int var3;
      ThreadGroup[] var4;
      synchronized(this) {
         int var6 = 0;

         while(true) {
            if (var6 >= var2) {
               var1.println((Object)this);
               var2 += 4;

               for(var6 = 0; var6 < this.nthreads; ++var6) {
                  for(int var7 = 0; var7 < var2; ++var7) {
                     var1.print(" ");
                  }

                  var1.println((Object)this.threads[var6]);
               }

               var3 = this.ngroups;
               if (this.groups != null) {
                  var4 = (ThreadGroup[])Arrays.copyOf((Object[])this.groups, var3);
               } else {
                  var4 = null;
               }
               break;
            }

            var1.print(" ");
            ++var6;
         }
      }

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5].list(var1, var2);
      }

   }

   public void uncaughtException(Thread var1, Throwable var2) {
      if (this.parent != null) {
         this.parent.uncaughtException(var1, var2);
      } else {
         Thread.UncaughtExceptionHandler var3 = Thread.getDefaultUncaughtExceptionHandler();
         if (var3 != null) {
            var3.uncaughtException(var1, var2);
         } else if (!(var2 instanceof ThreadDeath)) {
            System.err.print("Exception in thread \"" + var1.getName() + "\" ");
            var2.printStackTrace(System.err);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean allowThreadSuspension(boolean var1) {
      this.vmAllowSuspension = var1;
      if (!var1) {
         VM.unsuspendSomeThreads();
      }

      return true;
   }

   public String toString() {
      return this.getClass().getName() + "[name=" + this.getName() + ",maxpri=" + this.maxPriority + "]";
   }
}
