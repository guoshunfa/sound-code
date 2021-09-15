package jdk.management.resource;

public class BoundedMeter extends NotifyingMeter implements ResourceMeter, ResourceRequest {
   private volatile long bound;

   public static BoundedMeter create(ResourceType var0, long var1) {
      return create(var0, var1, (ResourceRequest)null, (ResourceApprover)null);
   }

   public static BoundedMeter create(ResourceType var0, long var1, ResourceRequest var3) {
      return create(var0, var1, var3, (ResourceApprover)null);
   }

   public static BoundedMeter create(ResourceType var0, long var1, ResourceRequest var3, ResourceApprover var4) {
      return new BoundedMeter(var0, var1, var3, var4);
   }

   public static BoundedMeter create(ResourceType var0, long var1, ResourceApprover var3) {
      return create(var0, var1, (ResourceRequest)null, var3);
   }

   protected BoundedMeter(ResourceType var1, long var2, ResourceRequest var4, ResourceApprover var5) {
      super(var1, var4, var5);
      if (var2 < 0L) {
         throw new IllegalArgumentException("bound must be zero or greater");
      } else {
         this.bound = var2;
      }
   }

   protected long validate(long var1, long var3, ResourceId var5) {
      ResourceApprover var6 = this.getApprover();
      long var7 = var3;
      if (var6 != null) {
         long var9 = this.getGranularity();
         long var11 = var1 + var3;
         long var13 = Math.floorDiv(var1, var9);
         long var15 = Math.floorDiv(var11, var9);
         if (var13 != var15 || this.bound - var11 < 0L) {
            var7 = var6.request(this, var1, var3, var5);
            if (var7 != var3 && var7 != 0L) {
               var7 = var3;
            }
         }
      }

      if (this.bound - (var1 + var7) < 0L) {
         var7 = 0L;
      }

      return var7;
   }

   public final synchronized long getBound() {
      return this.bound;
   }

   public final synchronized long setBound(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("bound must be zero or greater");
      } else {
         long var3 = this.bound;
         this.bound = var1;
         return var3;
      }
   }

   synchronized long setGranularityInternal(long var1) {
      long var3 = super.setGranularityInternal(var1);
      return var3;
   }

   public String toString() {
      return super.toString() + "; bound: " + Long.toString(this.bound);
   }
}
