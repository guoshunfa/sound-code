package jdk.management.resource;

import jdk.management.resource.internal.ResourceIdImpl;

public class NotifyingMeter extends SimpleMeter {
   private final ResourceApprover approver;
   private long granularity;

   public static NotifyingMeter create(ResourceType var0, ResourceApprover var1) {
      return new NotifyingMeter(var0, (ResourceRequest)null, var1);
   }

   public static NotifyingMeter create(ResourceType var0, ResourceRequest var1, ResourceApprover var2) {
      return new NotifyingMeter(var0, var1, var2);
   }

   protected NotifyingMeter(ResourceType var1, ResourceRequest var2, ResourceApprover var3) {
      super(var1, var2);
      this.approver = var3;
      this.granularity = 1L;
   }

   protected long validate(long var1, long var3, ResourceId var5) {
      long var6 = var3;
      if (this.approver != null) {
         long var8 = Math.floorDiv(var1, this.granularity);
         long var10 = Math.floorDiv(var1 + var3, this.granularity);
         if (var8 != var10 || var3 == 0L && var5 != null && var5 instanceof ResourceIdImpl && ((ResourceIdImpl)var5).isForcedUpdate()) {
            var6 = this.approver.request(this, var1, var3, var5);
            if (var6 != var3 && var6 != 0L) {
               var6 = var3;
            }
         }
      }

      return var6;
   }

   public final synchronized long getGranularity() {
      return this.granularity;
   }

   public final long setGranularity(long var1) {
      return this.setGranularityInternal(var1);
   }

   synchronized long setGranularityInternal(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("granularity must be greater than zero");
      } else {
         long var3 = this.granularity;
         this.granularity = var1;
         return var3;
      }
   }

   public final ResourceApprover getApprover() {
      return this.approver;
   }
}
