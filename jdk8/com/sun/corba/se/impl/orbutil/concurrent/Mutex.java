package com.sun.corba.se.impl.orbutil.concurrent;

public class Mutex implements Sync {
   protected boolean inuse_ = false;

   public void acquire() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            try {
               while(this.inuse_) {
                  this.wait();
               }

               this.inuse_ = true;
            } catch (InterruptedException var4) {
               this.notify();
               throw var4;
            }

         }
      }
   }

   public synchronized void release() {
      this.inuse_ = false;
      this.notify();
   }

   public boolean attempt(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         synchronized(this) {
            if (!this.inuse_) {
               this.inuse_ = true;
               return true;
            } else if (var1 <= 0L) {
               return false;
            } else {
               long var4 = var1;
               long var6 = System.currentTimeMillis();

               try {
                  boolean var10000;
                  do {
                     this.wait(var4);
                     if (!this.inuse_) {
                        this.inuse_ = true;
                        var10000 = true;
                        return var10000;
                     }

                     var4 = var1 - (System.currentTimeMillis() - var6);
                  } while(var4 > 0L);

                  var10000 = false;
                  return var10000;
               } catch (InterruptedException var10) {
                  this.notify();
                  throw var10;
               }
            }
         }
      }
   }
}
