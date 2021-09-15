package jdk.management.resource;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

public class ResourceType {
   private static final WeakHashMap<String, ResourceType> types = new WeakHashMap(32);
   public static final ResourceType FILE_OPEN = ofBuiltin("file.open");
   public static final ResourceType FILE_READ = ofBuiltin("file.read");
   public static final ResourceType FILE_WRITE = ofBuiltin("file.write");
   public static final ResourceType STDERR_WRITE = ofBuiltin("stderr.write");
   public static final ResourceType STDIN_READ = ofBuiltin("stdin.read");
   public static final ResourceType STDOUT_WRITE = ofBuiltin("stdout.write");
   public static final ResourceType SOCKET_OPEN = ofBuiltin("socket.open");
   public static final ResourceType SOCKET_READ = ofBuiltin("socket.read");
   public static final ResourceType SOCKET_WRITE = ofBuiltin("socket.write");
   public static final ResourceType DATAGRAM_OPEN = ofBuiltin("datagram.open");
   public static final ResourceType DATAGRAM_RECEIVED = ofBuiltin("datagram.received");
   public static final ResourceType DATAGRAM_SENT = ofBuiltin("datagram.sent");
   public static final ResourceType DATAGRAM_READ = ofBuiltin("datagram.read");
   public static final ResourceType DATAGRAM_WRITE = ofBuiltin("datagram.write");
   public static final ResourceType THREAD_CREATED = ofBuiltin("thread.created");
   public static final ResourceType THREAD_CPU = ofBuiltin("thread.cpu");
   public static final ResourceType HEAP_RETAINED = ofBuiltin("heap.retained");
   public static final ResourceType HEAP_ALLOCATED = ofBuiltin("heap.allocated");
   public static final ResourceType FILEDESCRIPTOR_OPEN = ofBuiltin("filedescriptor.open");
   private final String name;
   private final boolean builtin;

   public static ResourceType of(String var0) {
      synchronized(types) {
         return (ResourceType)types.computeIfAbsent(var0, (var0x) -> {
            return new ResourceType(var0x, false);
         });
      }
   }

   static ResourceType ofBuiltin(String var0) {
      synchronized(types) {
         return (ResourceType)types.computeIfAbsent(var0, (var0x) -> {
            return new ResourceType(var0x, true);
         });
      }
   }

   private boolean isBuiltin() {
      return this.builtin;
   }

   static Set<ResourceType> builtinTypes() {
      synchronized(types) {
         HashSet var1 = new HashSet(types.values());
         var1.removeIf((var0) -> {
            return !var0.isBuiltin();
         });
         return var1;
      }
   }

   private ResourceType(String var1, boolean var2) {
      this.name = (String)Objects.requireNonNull(var1, (String)"name");
      this.builtin = var2;
      if (var1.length() == 0) {
         throw new IllegalArgumentException("name must not be empty");
      }
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   public int hashCode() {
      byte var1 = 5;
      int var2 = 17 * var1 + Objects.hashCode(this.name);
      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ResourceType var2 = (ResourceType)var1;
         return Objects.equals(this.name, var2.name);
      }
   }
}
