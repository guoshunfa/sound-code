package com.sun.jndi.ldap.pool;

public final class PoolCleaner extends Thread {
   private final Pool[] pools;
   private final long period;

   public PoolCleaner(long var1, Pool[] var3) {
      this.period = var1;
      this.pools = (Pool[])var3.clone();
      this.setDaemon(true);
   }

   public void run() {
      while(true) {
         synchronized(this) {
            try {
               this.wait(this.period);
            } catch (InterruptedException var6) {
            }

            long var1 = System.currentTimeMillis() - this.period;

            for(int var4 = 0; var4 < this.pools.length; ++var4) {
               if (this.pools[var4] != null) {
                  this.pools[var4].expire(var1);
               }
            }
         }
      }
   }
}
