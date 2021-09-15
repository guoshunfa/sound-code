package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;

public class CondVar {
   protected boolean debug_;
   protected final Sync mutex_;
   protected final ReentrantMutex remutex_;

   private int releaseMutex() {
      int var1 = 1;
      if (this.remutex_ != null) {
         var1 = this.remutex_.releaseAll();
      } else {
         this.mutex_.release();
      }

      return var1;
   }

   private void acquireMutex(int var1) throws InterruptedException {
      if (this.remutex_ != null) {
         this.remutex_.acquireAll(var1);
      } else {
         this.mutex_.acquire();
      }

   }

   public CondVar(Sync var1, boolean var2) {
      this.debug_ = var2;
      this.mutex_ = var1;
      if (var1 instanceof ReentrantMutex) {
         this.remutex_ = (ReentrantMutex)var1;
      } else {
         this.remutex_ = null;
      }

   }

   public CondVar(Sync var1) {
      this(var1, false);
   }

   public void await() throws InterruptedException {
      int var1 = 0;
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         boolean var13 = false;

         try {
            var13 = true;
            if (this.debug_) {
               ORBUtility.dprintTrace(this, "await enter");
            }

            synchronized(this) {
               var1 = this.releaseMutex();

               try {
                  this.wait();
               } catch (InterruptedException var14) {
                  this.notify();
                  throw var14;
               }

               var13 = false;
            }
         } finally {
            if (var13) {
               boolean var6 = false;

               while(true) {
                  try {
                     this.acquireMutex(var1);
                     break;
                  } catch (InterruptedException var16) {
                     var6 = true;
                  }
               }

               if (var6) {
                  Thread.currentThread().interrupt();
               }

               if (this.debug_) {
                  ORBUtility.dprintTrace(this, "await exit");
               }

            }
         }

         boolean var2 = false;

         while(true) {
            try {
               this.acquireMutex(var1);
               break;
            } catch (InterruptedException var17) {
               var2 = true;
            }
         }

         if (var2) {
            Thread.currentThread().interrupt();
         }

         if (this.debug_) {
            ORBUtility.dprintTrace(this, "await exit");
         }

      }
   }

   public boolean timedwait(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         boolean var3 = false;
         int var4 = 0;
         boolean var17 = false;

         try {
            var17 = true;
            if (this.debug_) {
               ORBUtility.dprintTrace(this, "timedwait enter");
            }

            synchronized(this) {
               var4 = this.releaseMutex();

               try {
                  if (var1 > 0L) {
                     long var6 = System.currentTimeMillis();
                     this.wait(var1);
                     var3 = System.currentTimeMillis() - var6 <= var1;
                  }
               } catch (InterruptedException var20) {
                  this.notify();
                  throw var20;
               }

               var17 = false;
            }
         } finally {
            if (var17) {
               boolean var10 = false;

               while(true) {
                  try {
                     this.acquireMutex(var4);
                     break;
                  } catch (InterruptedException var18) {
                     var10 = true;
                  }
               }

               if (var10) {
                  Thread.currentThread().interrupt();
               }

               if (this.debug_) {
                  ORBUtility.dprintTrace(this, "timedwait exit");
               }

            }
         }

         boolean var5 = false;

         while(true) {
            try {
               this.acquireMutex(var4);
               break;
            } catch (InterruptedException var19) {
               var5 = true;
            }
         }

         if (var5) {
            Thread.currentThread().interrupt();
         }

         if (this.debug_) {
            ORBUtility.dprintTrace(this, "timedwait exit");
         }

         return var3;
      }
   }

   public synchronized void signal() {
      this.notify();
   }

   public synchronized void broadcast() {
      this.notifyAll();
   }
}
