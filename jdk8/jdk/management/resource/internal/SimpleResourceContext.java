package jdk.management.resource.internal;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

public class SimpleResourceContext implements ResourceContext {
   private final ConcurrentHashMap<ResourceType, ResourceMeter> meters;
   private static final WeakKeyConcurrentHashMap<Thread, ResourceContext> currContext = new WeakKeyConcurrentHashMap();
   private static final ConcurrentHashMap<String, SimpleResourceContext> contexts = new ConcurrentHashMap();
   private final String name;
   private final int contextId;
   private volatile boolean closed;

   SimpleResourceContext(String var1) {
      this((String)Objects.requireNonNull(var1, (String)"name"), ResourceNatives.createResourceContext(var1));
   }

   SimpleResourceContext(String var1, int var2) {
      this.meters = new ConcurrentHashMap();
      this.name = (String)Objects.requireNonNull(var1, (String)"name");
      this.contextId = var2;
      this.closed = false;
   }

   public String getName() {
      return this.name;
   }

   public static ResourceContext create(String var0) {
      if (!contexts.containsKey(var0)) {
         SimpleResourceContext var1 = new SimpleResourceContext(var0);
         ResourceContext var2 = (ResourceContext)contexts.putIfAbsent(var0, var1);
         if (var2 == null) {
            return var1;
         }

         ResourceNatives.destroyResourceContext(var1.contextId, 0);
      }

      throw new IllegalArgumentException("ResourceContext already exists for name: " + var0);
   }

   public static ResourceContext get(String var0) {
      Objects.requireNonNull(var0, (String)"name");
      return (ResourceContext)contexts.get(var0);
   }

   public void close() {
      synchronized(this) {
         if (!this.closed) {
            this.closed = true;
            contexts.remove(this.getName());
            UnassignedContext var2 = UnassignedContext.getUnassignedContext();
            ResourceNatives.destroyResourceContext(this.contextId, var2.nativeThreadContext());
            this.boundThreads().forEach((var1) -> {
               var2.bindThreadContext(var1);
            });
            this.meters.forEach((var1, var2x) -> {
               this.removeResourceMeter(var2x);
               ApproverGroup var3 = ApproverGroup.getGroup(var1);
               if (var3 != null) {
                  var3.purgeResourceContext(this);
               }

            });
         }

      }
   }

   static ConcurrentHashMap<String, SimpleResourceContext> getContexts() {
      return contexts;
   }

   public static Stream<ResourceContext> contexts() {
      return getContexts().values().stream().map((var0) -> {
         return var0;
      });
   }

   public static ResourceContext getThreadContext(Thread var0) {
      Object var1 = (ResourceContext)currContext.get(var0);
      if (var1 == null) {
         var1 = UnassignedContext.getUnassignedContext();
      }

      return (ResourceContext)var1;
   }

   public ResourceContext bindThreadContext() {
      this.illegalStateIfClosed();
      Thread var1 = Thread.currentThread();
      Object var2 = (ResourceContext)currContext.put(var1, this);
      if (var2 == null) {
         var2 = UnassignedContext.getUnassignedContext();
      }

      ThreadMetrics.updateCurrentThreadMetrics((ResourceContext)var2);
      ResourceNatives.setThreadResourceContext(this.contextId);
      return (ResourceContext)var2;
   }

   public ResourceContext bindThreadContext(Thread var1) {
      this.illegalStateIfClosed();
      Object var2 = var1.isAlive() ? (ResourceContext)currContext.put(var1, this) : (ResourceContext)currContext.remove(var1);
      if (var2 == null) {
         var2 = UnassignedContext.getUnassignedContext();
      }

      try {
         ThreadMetrics.updateThreadMetrics((ResourceContext)var2, var1);
         ResourceNatives.setThreadResourceContext(var1.getId(), this.contextId);
      } catch (IllegalArgumentException var4) {
         currContext.remove(var1);
      }

      return (ResourceContext)var2;
   }

   public void bindNewThreadContext(Thread var1) {
      currContext.put(var1, this);
   }

   public static void removeThreadContext() {
      currContext.remove(Thread.currentThread());
   }

   public static ResourceContext unbindThreadContext() {
      return UnassignedContext.getUnassignedContext().bindThreadContext();
   }

   int nativeThreadContext() {
      return this.contextId;
   }

   public Stream<Thread> boundThreads() {
      return currContext.keysForValue(this).filter((var0) -> {
         return var0.isAlive();
      });
   }

   public ResourceRequest getResourceRequest(ResourceType var1) {
      ResourceMeter var2 = (ResourceMeter)this.meters.get(var1);
      return var2 instanceof ResourceRequest ? (ResourceRequest)var2 : null;
   }

   public void addResourceMeter(ResourceMeter var1) {
      this.illegalStateIfClosed();
      if (var1.getType().equals(ResourceType.HEAP_RETAINED) && !ResourceNatives.isHeapRetainedEnabled()) {
         throw new UnsupportedOperationException("ResourceType not supported by the current garbage collector: " + ResourceType.HEAP_RETAINED);
      } else {
         ResourceMeter var2 = (ResourceMeter)this.meters.putIfAbsent(var1.getType(), var1);
         if (var2 != null) {
            throw new IllegalArgumentException("ResourceType already added to meter: " + var1.getType().getName());
         } else {
            if (var1.getType().equals(ResourceType.THREAD_CPU) || var1.getType().equals(ResourceType.HEAP_ALLOCATED)) {
               ThreadMetrics.init();
            }

            if (var1.getType().equals(ResourceType.HEAP_RETAINED)) {
               HeapMetrics.init();
            }

            TotalResourceContext.validateMeter(var1.getType());
         }
      }
   }

   public boolean removeResourceMeter(ResourceMeter var1) {
      ResourceMeter var2 = (ResourceMeter)this.meters.remove(var1.getType());
      if (var2 != null) {
         TotalResourceContext var3 = TotalResourceContext.getTotalContext();
         TotalResourceContext.TotalMeter var4 = var3.getMeter(var2.getType());
         var4.addValue(var2.getValue());
         var4.addAllocated(var2.getAllocated());
         return true;
      } else {
         return false;
      }
   }

   public ResourceMeter getMeter(ResourceType var1) {
      return (ResourceMeter)this.meters.get(var1);
   }

   public Stream<ResourceMeter> meters() {
      return this.meters.entrySet().stream().map((var0) -> {
         return (ResourceMeter)var0.getValue();
      });
   }

   public void requestAccurateUpdate(ResourceAccuracy var1) {
      Objects.requireNonNull(var1, (String)"accuracy");
      if (!ResourceNatives.isHeapRetainedEnabled()) {
         throw new UnsupportedOperationException("ResourceType not supported by the current garbage collector: " + ResourceType.HEAP_RETAINED);
      } else {
         int[] var2 = new int[]{this.contextId};
         ResourceNatives.computeRetainedMemory(var2, var1.ordinal());
      }
   }

   private synchronized void illegalStateIfClosed() {
      if (this.closed) {
         throw new IllegalStateException("ResourceContext is closed: " + this.getName());
      }
   }

   public String toString() {
      StringJoiner var1 = new StringJoiner("; ", this.name + "[", "]");
      this.meters.forEach((var1x, var2) -> {
         var1.add(var2.toString());
      });
      return var1.toString();
   }
}
