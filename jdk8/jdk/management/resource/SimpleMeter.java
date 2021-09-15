package jdk.management.resource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import jdk.management.resource.internal.ResourceIdImpl;

public class SimpleMeter implements ResourceMeter, ResourceRequest {
   private final ResourceType type;
   private final AtomicLong value;
   private final AtomicLong allocated;
   private final ResourceRequest parent;

   public static SimpleMeter create(ResourceType var0) {
      return new SimpleMeter(var0, (ResourceRequest)null);
   }

   public static SimpleMeter create(ResourceType var0, ResourceRequest var1) {
      return new SimpleMeter(var0, var1);
   }

   protected SimpleMeter(ResourceType var1, ResourceRequest var2) {
      this.type = (ResourceType)Objects.requireNonNull(var1, (String)"type");
      this.parent = var2;
      this.value = new AtomicLong();
      this.allocated = new AtomicLong();
   }

   public final long getValue() {
      return this.value.get();
   }

   public final long getAllocated() {
      return this.allocated.get();
   }

   public final ResourceType getType() {
      return this.type;
   }

   public final ResourceRequest getParent() {
      return this.parent;
   }

   public final long request(long var1, ResourceId var3) {
      if (var1 == 0L) {
         Object var4 = null;
         if (var3 == null || !(var3 instanceof ResourceIdImpl) || !((ResourceIdImpl)var3).isForcedUpdate()) {
            return 0L;
         }
      }

      long var22 = 0L;
      long var6;
      if (var1 > 0L) {
         boolean var19 = false;

         try {
            var19 = true;
            var6 = this.value.getAndAdd(var1);
            var22 = this.validate(var6, var1, var3);
            var19 = false;
         } finally {
            if (var19) {
               long var9 = var1 - var22;
               if (var9 != 0L) {
                  this.value.getAndAdd(-var9);
               }

            }
         }

         var6 = var1 - var22;
         if (var6 != 0L) {
            this.value.getAndAdd(-var6);
         }
      } else {
         var6 = this.getValue();
         var22 = this.validate(var6, var1, var3);
         this.value.getAndAdd(var22);
      }

      if (this.parent != null) {
         var6 = var22;
         var22 = 0L;
         boolean var16 = false;

         try {
            var16 = true;
            var22 = this.parent.request(var6, var3);
            var16 = false;
         } finally {
            if (var16) {
               long var12 = var6 - var22;
               if (var12 != 0L) {
                  this.value.getAndAdd(-var12);
               }

            }
         }

         long var8 = var6 - var22;
         if (var8 != 0L) {
            this.value.getAndAdd(-var8);
         }
      }

      if (var22 > 0L) {
         this.allocated.getAndAdd(var22);
      }

      return var22;
   }

   protected long validate(long var1, long var3, ResourceId var5) throws ResourceRequestDeniedException {
      return var3;
   }

   public String toString() {
      long var1 = this.value.get();
      long var3 = this.allocated.get();
      return this.type.toString() + ": " + Long.toString(var1) + "/" + var3;
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final boolean equals(Object var1) {
      return super.equals(var1);
   }
}
