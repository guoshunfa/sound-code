package com.sun.corba.se.impl.orbutil.concurrent;

import org.omg.CORBA.INTERNAL;

public class DebugMutex implements Sync {
   protected boolean inuse_ = false;
   protected Thread holder_ = null;

   public void acquire() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            Thread var2 = Thread.currentThread();
            if (this.holder_ == var2) {
               throw new INTERNAL("Attempt to acquire Mutex by thread holding the Mutex");
            } else {
               try {
                  while(this.inuse_) {
                     this.wait();
                  }

                  this.inuse_ = true;
                  this.holder_ = Thread.currentThread();
               } catch (InterruptedException var5) {
                  this.notify();
                  throw var5;
               }

            }
         }
      }
   }

   public synchronized void release() {
      Thread var1 = Thread.currentThread();
      if (var1 != this.holder_) {
         throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex");
      } else {
         this.holder_ = null;
         this.inuse_ = false;
         this.notify();
      }
   }

   public boolean attempt(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            Thread var4 = Thread.currentThread();
            if (!this.inuse_) {
               this.inuse_ = true;
               this.holder_ = var4;
               return true;
            } else if (var1 <= 0L) {
               return false;
            } else {
               long var5 = var1;
               long var7 = System.currentTimeMillis();

               try {
                  boolean var10000;
                  do {
                     this.wait(var5);
                     if (!this.inuse_) {
                        this.inuse_ = true;
                        this.holder_ = var4;
                        var10000 = true;
                        return var10000;
                     }

                     var5 = var1 - (System.currentTimeMillis() - var7);
                  } while(var5 > 0L);

                  var10000 = false;
                  return var10000;
               } catch (InterruptedException var11) {
                  this.notify();
                  throw var11;
               }
            }
         }
      }
   }
}
