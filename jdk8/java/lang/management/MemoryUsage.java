package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MemoryUsageCompositeData;

public class MemoryUsage {
   private final long init;
   private final long used;
   private final long committed;
   private final long max;

   public MemoryUsage(long var1, long var3, long var5, long var7) {
      if (var1 < -1L) {
         throw new IllegalArgumentException("init parameter = " + var1 + " is negative but not -1.");
      } else if (var7 < -1L) {
         throw new IllegalArgumentException("max parameter = " + var7 + " is negative but not -1.");
      } else if (var3 < 0L) {
         throw new IllegalArgumentException("used parameter = " + var3 + " is negative.");
      } else if (var5 < 0L) {
         throw new IllegalArgumentException("committed parameter = " + var5 + " is negative.");
      } else if (var3 > var5) {
         throw new IllegalArgumentException("used = " + var3 + " should be <= committed = " + var5);
      } else if (var7 >= 0L && var5 > var7) {
         throw new IllegalArgumentException("committed = " + var5 + " should be < max = " + var7);
      } else {
         this.init = var1;
         this.used = var3;
         this.committed = var5;
         this.max = var7;
      }
   }

   private MemoryUsage(CompositeData var1) {
      MemoryUsageCompositeData.validateCompositeData(var1);
      this.init = MemoryUsageCompositeData.getInit(var1);
      this.used = MemoryUsageCompositeData.getUsed(var1);
      this.committed = MemoryUsageCompositeData.getCommitted(var1);
      this.max = MemoryUsageCompositeData.getMax(var1);
   }

   public long getInit() {
      return this.init;
   }

   public long getUsed() {
      return this.used;
   }

   public long getCommitted() {
      return this.committed;
   }

   public long getMax() {
      return this.max;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("init = " + this.init + "(" + (this.init >> 10) + "K) ");
      var1.append("used = " + this.used + "(" + (this.used >> 10) + "K) ");
      var1.append("committed = " + this.committed + "(" + (this.committed >> 10) + "K) ");
      var1.append("max = " + this.max + "(" + (this.max >> 10) + "K)");
      return var1.toString();
   }

   public static MemoryUsage from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof MemoryUsageCompositeData ? ((MemoryUsageCompositeData)var0).getMemoryUsage() : new MemoryUsage(var0);
      }
   }
}
