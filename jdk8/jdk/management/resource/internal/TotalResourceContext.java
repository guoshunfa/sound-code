package jdk.management.resource.internal;

import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

public class TotalResourceContext implements ResourceContext {
   private static final TotalResourceContext totalContext = new TotalResourceContext("Total");
   final ConcurrentHashMap<ResourceType, TotalResourceContext.TotalMeter> totalMeters = new ConcurrentHashMap();
   private final String name;

   private TotalResourceContext(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public static TotalResourceContext getTotalContext() {
      return totalContext;
   }

   public void close() {
   }

   public ResourceRequest getResourceRequest(ResourceType var1) {
      return null;
   }

   public TotalResourceContext.TotalMeter getMeter(ResourceType var1) {
      return (TotalResourceContext.TotalMeter)this.totalMeters.get(var1);
   }

   static void validateMeter(ResourceType var0) {
      totalContext.totalMeters.computeIfAbsent(var0, (var0x) -> {
         return new TotalResourceContext.TotalMeter(var0x, 0L, 0L);
      });
   }

   public Stream<ResourceMeter> meters() {
      return this.totalMeters.entrySet().stream().map((var0) -> {
         return (TotalResourceContext.TotalMeter)var0.getValue();
      });
   }

   public String toString() {
      StringJoiner var1 = new StringJoiner("; ", this.name + "[", "]");
      this.meters().forEach((var1x) -> {
         var1.add(var1x.toString());
      });
      return var1.toString();
   }

   static class TotalMeter implements ResourceMeter {
      private final ResourceType type;
      private long value;
      private long allocated;

      TotalMeter(ResourceType var1, long var2, long var4) {
         this.type = var1;
         this.value = var2;
         this.allocated = var4;
      }

      synchronized void addValue(long var1) {
         this.value += var1;
      }

      synchronized void addAllocated(long var1) {
         this.allocated += var1;
      }

      public long getValue() {
         long var1 = 0L;
         synchronized(this) {
            var1 += this.value;
         }

         var1 += SimpleResourceContext.getContexts().reduceValuesToLong(1000L, (var1x) -> {
            ResourceMeter var2 = var1x.getMeter(this.type);
            long var3 = var2 == null ? 0L : var2.getValue();
            return var3;
         }, 0L, (var0, var2) -> {
            return var0 + var2;
         });
         return var1;
      }

      public long getAllocated() {
         long var1 = 0L;
         synchronized(this) {
            var1 += this.allocated;
         }

         var1 += SimpleResourceContext.getContexts().reduceValuesToLong(1000L, (var1x) -> {
            ResourceMeter var2 = var1x.getMeter(this.type);
            return var2 == null ? 0L : var2.getAllocated();
         }, 0L, (var0, var2) -> {
            return var0 + var2;
         });
         return var1;
      }

      public ResourceType getType() {
         return this.type;
      }

      public String toString() {
         return this.type.toString() + ": " + Long.toString(this.getValue()) + "/" + Long.toString(this.getAllocated());
      }
   }
}
