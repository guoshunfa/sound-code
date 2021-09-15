package jdk.management.resource;

public class ThrottledMeter extends NotifyingMeter {
   private volatile long ratePerSec;
   private final Object mutex;
   private long availableBytes;
   private long availableTimestamp;

   public static ThrottledMeter create(ResourceType var0, long var1, ResourceApprover var3) {
      return new ThrottledMeter(var0, var1, (ResourceRequest)null, var3);
   }

   public static ThrottledMeter create(ResourceType var0, ResourceRequest var1, ResourceApprover var2) {
      return new ThrottledMeter(var0, Long.MAX_VALUE, var1, var2);
   }

   public static ThrottledMeter create(ResourceType var0, long var1, ResourceRequest var3, ResourceApprover var4) {
      return new ThrottledMeter(var0, var1, var3, var4);
   }

   ThrottledMeter(ResourceType var1, long var2, ResourceRequest var4, ResourceApprover var5) {
      super(var1, var4, var5);
      if (var2 <= 0L) {
         throw new IllegalArgumentException("ratePerSec must be greater than zero");
      } else {
         this.ratePerSec = var2;
         this.mutex = new Object();
         this.availableBytes = 0L;
         this.availableTimestamp = 0L;
      }
   }

   public long validate(long var1, long var3, ResourceId var5) {
      long var6 = super.validate(var1, var3, var5);
      if (var6 <= 0L) {
         return var6;
      } else {
         synchronized(this.mutex) {
            while(this.availableBytes - var3 < 0L) {
               long var9 = this.ratePerSec;
               long var11 = this.availableBytes;
               long var13 = System.currentTimeMillis();
               long var15 = Math.max(var13 - this.availableTimestamp, 0L);
               long var17 = var9 * var15 / 1000L;
               this.availableBytes = Math.min(this.availableBytes + var17, var9);
               this.availableTimestamp = var13;
               if (this.availableBytes - var3 >= 0L || var3 > var9 && var11 > 0L) {
                  break;
               }

               long var19 = Math.min(var3 - this.availableBytes, var9);
               var15 = var19 * 1000L / var9;

               try {
                  this.mutex.wait(Math.max(var15, 10L));
               } catch (InterruptedException var23) {
                  return 0L;
               }
            }

            this.availableBytes -= var3;
            return var3;
         }
      }
   }

   public final long getCurrentRate() {
      synchronized(this.mutex) {
         long var2 = this.ratePerSec;
         long var4 = System.currentTimeMillis();
         long var6 = var4 - this.availableTimestamp;
         long var8 = var2 * var6 / 1000L;
         this.availableBytes = Math.min(this.availableBytes + var8, var2);
         this.availableTimestamp = var4;
         long var10 = var2 - this.availableBytes;
         return var10;
      }
   }

   public final synchronized long getRatePerSec() {
      return this.ratePerSec;
   }

   public final synchronized long setRatePerSec(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("ratePerSec must be greater than zero");
      } else {
         this.ratePerSec = var1;
         return var1;
      }
   }

   public String toString() {
      return super.toString() + "; ratePerSec: " + Long.toString(this.ratePerSec) + "; currentRate: " + Long.toString(this.getCurrentRate());
   }
}
