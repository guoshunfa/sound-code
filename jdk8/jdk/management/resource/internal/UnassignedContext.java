package jdk.management.resource.internal;

import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;
import jdk.management.resource.SimpleMeter;

public class UnassignedContext extends SimpleResourceContext {
   private static final UnassignedContext unassignedContext = new UnassignedContext("Unassigned");
   private static final UnassignedContext systemContext = new UnassignedContext("System", 0);

   private UnassignedContext(String var1) {
      super(var1);
   }

   private UnassignedContext(String var1, int var2) {
      super(var1, var2);
   }

   public static UnassignedContext getSystemContext() {
      return systemContext;
   }

   public static UnassignedContext getUnassignedContext() {
      return unassignedContext;
   }

   public void close() {
   }

   public ResourceRequest getResourceRequest(ResourceType var1) {
      ResourceRequest var2 = super.getResourceRequest(var1);
      if (var2 == null) {
         try {
            this.addResourceMeter(SimpleMeter.create(var1));
         } catch (IllegalArgumentException var4) {
         }

         var2 = super.getResourceRequest(var1);
      }

      return var2;
   }
}
