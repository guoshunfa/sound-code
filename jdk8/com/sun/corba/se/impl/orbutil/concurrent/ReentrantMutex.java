package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.INTERNAL;

public class ReentrantMutex implements Sync {
   protected Thread holder_;
   protected int counter_;
   protected boolean debug;

   public ReentrantMutex() {
      this(false);
   }

   public ReentrantMutex(boolean var1) {
      this.holder_ = null;
      this.counter_ = 0;
      this.debug = false;
      this.debug = var1;
   }

   public void acquire() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            try {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "acquire enter: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

               Thread var2 = Thread.currentThread();
               if (this.holder_ != var2) {
                  try {
                     while(true) {
                        if (this.counter_ <= 0) {
                           if (this.counter_ != 0) {
                              throw new INTERNAL("counter not 0 when first acquiring mutex");
                           }

                           this.holder_ = var2;
                           break;
                        }

                        this.wait();
                     }
                  } catch (InterruptedException var9) {
                     this.notify();
                     throw var9;
                  }
               }

               ++this.counter_;
            } finally {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "acquire exit: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

            }

         }
      }
   }

   void acquireAll(int var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            try {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "acquireAll enter: count=" + var1 + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

               Thread var3 = Thread.currentThread();
               if (this.holder_ == var3) {
                  throw new INTERNAL("Cannot acquireAll while holding the mutex");
               }

               try {
                  while(true) {
                     if (this.counter_ <= 0) {
                        if (this.counter_ != 0) {
                           throw new INTERNAL("counter not 0 when first acquiring mutex");
                        }

                        this.holder_ = var3;
                        break;
                     }

                     this.wait();
                  }
               } catch (InterruptedException var10) {
                  this.notify();
                  throw var10;
               }

               this.counter_ = var1;
            } finally {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "acquireAll exit: count=" + var1 + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

            }

         }
      }
   }

   public synchronized void release() {
      try {
         if (this.debug) {
            ORBUtility.dprintTrace(this, "release enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
         }

         Thread var1 = Thread.currentThread();
         if (var1 != this.holder_) {
            throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex");
         }

         --this.counter_;
         if (this.counter_ == 0) {
            this.holder_ = null;
            this.notify();
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprintTrace(this, "release exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
         }

      }

   }

   synchronized int releaseAll() {
      int var3;
      try {
         if (this.debug) {
            ORBUtility.dprintTrace(this, "releaseAll enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
         }

         Thread var1 = Thread.currentThread();
         if (var1 != this.holder_) {
            throw new INTERNAL("Attempt to releaseAll Mutex by thread not holding the Mutex");
         }

         int var2 = this.counter_;
         this.counter_ = 0;
         this.holder_ = null;
         this.notify();
         var3 = var2;
      } finally {
         if (this.debug) {
            ORBUtility.dprintTrace(this, "releaseAll exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
         }

      }

      return var3;
   }

   public boolean attempt(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            try {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "attempt enter: msecs=" + var1 + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

               Thread var4 = Thread.currentThread();
               boolean var18;
               if (this.counter_ == 0) {
                  this.holder_ = var4;
                  this.counter_ = 1;
                  var18 = true;
                  return var18;
               } else if (var1 <= 0L) {
                  var18 = false;
                  return var18;
               } else {
                  long var5 = var1;
                  long var7 = System.currentTimeMillis();

                  try {
                     boolean var9;
                     do {
                        this.wait(var5);
                        if (this.counter_ == 0) {
                           this.holder_ = var4;
                           this.counter_ = 1;
                           var9 = true;
                           return var9;
                        }

                        var5 = var1 - (System.currentTimeMillis() - var7);
                     } while(var5 > 0L);

                     var9 = false;
                     return var9;
                  } catch (InterruptedException var15) {
                     this.notify();
                     throw var15;
                  }
               }
            } finally {
               if (this.debug) {
                  ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
               }

            }
         }
      }
   }
}
