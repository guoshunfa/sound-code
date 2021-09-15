package jdk.management.resource.internal;

import java.util.concurrent.ConcurrentHashMap;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.ResourceType;

public final class ApproverGroup {
   private static final ConcurrentHashMap<ResourceType, ApproverGroup> approverGroups = new ConcurrentHashMap();
   private final WeakKeyConcurrentHashMap<Object, ResourceContext> approvers;
   private final ResourceType type;
   private final boolean isBoundToContext;
   private static final ResourceRequest fallback = (var0, var2) -> {
      return var0;
   };
   public static final ApproverGroup FILE_OPEN_GROUP;
   public static final ApproverGroup FILE_READ_GROUP;
   public static final ApproverGroup FILE_WRITE_GROUP;
   public static final ApproverGroup STDERR_WRITE_GROUP;
   public static final ApproverGroup STDIN_READ_GROUP;
   public static final ApproverGroup STDOUT_WRITE_GROUP;
   public static final ApproverGroup SOCKET_OPEN_GROUP;
   public static final ApproverGroup SOCKET_READ_GROUP;
   public static final ApproverGroup SOCKET_WRITE_GROUP;
   public static final ApproverGroup DATAGRAM_OPEN_GROUP;
   public static final ApproverGroup DATAGRAM_RECEIVED_GROUP;
   public static final ApproverGroup DATAGRAM_SENT_GROUP;
   public static final ApproverGroup DATAGRAM_READ_GROUP;
   public static final ApproverGroup DATAGRAM_WRITE_GROUP;
   public static final ApproverGroup THREAD_CREATED_GROUP;
   public static final ApproverGroup THREAD_CPU_GROUP;
   public static final ApproverGroup FILEDESCRIPTOR_OPEN_GROUP;

   public static ApproverGroup getGroup(ResourceType var0) {
      return (ApproverGroup)approverGroups.get(var0);
   }

   public static ApproverGroup create(ResourceType var0, boolean var1) {
      return (ApproverGroup)approverGroups.computeIfAbsent(var0, (var1x) -> {
         return new ApproverGroup(var1x, var1);
      });
   }

   private ApproverGroup(ResourceType var1, boolean var2) {
      this.type = var1;
      this.isBoundToContext = var2;
      this.approvers = new WeakKeyConcurrentHashMap();
   }

   public final ResourceType getId() {
      return this.type;
   }

   public final ResourceRequest getApprover(Object var1) {
      ResourceContext var2;
      if (this.isBoundToContext) {
         if (var1 == null) {
            throw new ResourceRequestDeniedException("null resource instance for ResourceType: " + this.type);
         }

         var2 = (ResourceContext)this.approvers.computeIfAbsent(var1, (var0) -> {
            return SimpleResourceContext.getThreadContext(Thread.currentThread());
         });
      } else {
         var2 = SimpleResourceContext.getThreadContext(Thread.currentThread());
      }

      ResourceRequest var3 = var2.getResourceRequest(this.type);
      return var3 != null ? var3 : fallback;
   }

   public void purgeResourceContext(ResourceContext var1) {
      this.approvers.purgeValue(var1);
   }

   static {
      FILE_OPEN_GROUP = create(ResourceType.FILE_OPEN, true);
      FILE_READ_GROUP = create(ResourceType.FILE_READ, false);
      FILE_WRITE_GROUP = create(ResourceType.FILE_WRITE, false);
      STDERR_WRITE_GROUP = create(ResourceType.STDERR_WRITE, false);
      STDIN_READ_GROUP = create(ResourceType.STDIN_READ, false);
      STDOUT_WRITE_GROUP = create(ResourceType.STDOUT_WRITE, false);
      SOCKET_OPEN_GROUP = create(ResourceType.SOCKET_OPEN, true);
      SOCKET_READ_GROUP = create(ResourceType.SOCKET_READ, false);
      SOCKET_WRITE_GROUP = create(ResourceType.SOCKET_WRITE, false);
      DATAGRAM_OPEN_GROUP = create(ResourceType.DATAGRAM_OPEN, true);
      DATAGRAM_RECEIVED_GROUP = create(ResourceType.DATAGRAM_RECEIVED, false);
      DATAGRAM_SENT_GROUP = create(ResourceType.DATAGRAM_SENT, false);
      DATAGRAM_READ_GROUP = create(ResourceType.DATAGRAM_READ, false);
      DATAGRAM_WRITE_GROUP = create(ResourceType.DATAGRAM_WRITE, false);
      THREAD_CREATED_GROUP = create(ResourceType.THREAD_CREATED, true);
      THREAD_CPU_GROUP = create(ResourceType.THREAD_CPU, false);
      FILEDESCRIPTOR_OPEN_GROUP = create(ResourceType.FILEDESCRIPTOR_OPEN, true);
   }
}
