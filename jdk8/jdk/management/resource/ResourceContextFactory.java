package jdk.management.resource;

import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import jdk.management.resource.internal.ResourceNatives;
import jdk.management.resource.internal.SimpleResourceContext;
import jdk.management.resource.internal.TotalResourceContext;
import jdk.management.resource.internal.UnassignedContext;
import jdk.management.resource.internal.WrapInstrumentation;

public final class ResourceContextFactory {
   private static final ResourceContextFactory instance = new ResourceContextFactory();
   private final ResourceContext unassigned = ResourceNatives.isEnabled() ? UnassignedContext.getUnassignedContext() : null;
   private static Set<ResourceType> supportedResourceTypes = null;
   private volatile boolean initialized = false;

   private ResourceContextFactory() {
   }

   public static boolean isEnabled() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("jdk.management.resource.getResourceContextFactory"));
      }

      return ResourceNatives.isEnabled();
   }

   public static ResourceContextFactory getInstance() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("jdk.management.resource.getResourceContextFactory"));
      }

      if (!ResourceNatives.isEnabled()) {
         throw new UnsupportedOperationException("Resource management is not enabled");
      } else {
         instance.initInstrumentation();
         return instance;
      }
   }

   private synchronized void initInstrumentation() throws InternalError {
      if (!this.initialized) {
         ThreadLocalRandom.current();
         new SecureRandom();

         try {
            Class var1 = Class.forName("jdk.management.resource.internal.inst.InitInstrumentation");
            Runnable var2 = (Runnable)var1.newInstance();
            var2.run();
         } catch (ClassNotFoundException var3) {
         } catch (InstantiationException | IllegalAccessException var4) {
            throw new InternalError("Resource management instrumentation failed", var4);
         }

         if (!(new WrapInstrumentation()).wrapComplete()) {
            throw new InternalError("Resource management instrumentation failed");
         }

         this.initPreBoundThreads();
         this.initialized = true;
      }

   }

   private void initPreBoundThreads() {
      AccessController.doPrivileged(() -> {
         ThreadGroup var0;
         for(var0 = Thread.currentThread().getThreadGroup(); var0.getParent() != null; var0 = var0.getParent()) {
         }

         Thread[] var1 = new Thread[var0.activeCount() * 2];
         int var2 = var0.enumerate(var1, true);

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var1[var3] != null) {
               UnassignedContext var4 = var1[var3].getThreadGroup().equals(var0) ? UnassignedContext.getSystemContext() : UnassignedContext.getUnassignedContext();
               var4.bindThreadContext(var1[var3]);
            }
         }

         return null;
      });
   }

   public ResourceContext create(String var1) {
      return SimpleResourceContext.create(var1);
   }

   public ResourceContext lookup(String var1) {
      return SimpleResourceContext.get(var1);
   }

   public ResourceContext getThreadContext() {
      return this.getThreadContext(Thread.currentThread());
   }

   public ResourceContext getThreadContext(Thread var1) {
      return SimpleResourceContext.getThreadContext(var1);
   }

   public ResourceRequest getResourceRequest(ResourceType var1) {
      return this.getThreadContext().getResourceRequest(var1);
   }

   public ResourceContext getUnassignedContext() {
      return this.unassigned;
   }

   public ResourceContext getTotalsContext() {
      return TotalResourceContext.getTotalContext();
   }

   public Stream<ResourceContext> contexts() {
      return SimpleResourceContext.contexts();
   }

   public Set<ResourceType> supportedResourceTypes() {
      synchronized(this) {
         if (supportedResourceTypes == null) {
            Set var2 = ResourceType.builtinTypes();
            if (!ResourceNatives.isHeapRetainedEnabled()) {
               var2.remove(ResourceType.HEAP_RETAINED);
            }

            supportedResourceTypes = Collections.unmodifiableSet(var2);
         }

         return supportedResourceTypes;
      }
   }
}
