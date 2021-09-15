package sun.management;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;

class ManagementFactory {
   private ManagementFactory() {
   }

   private static MemoryPoolMXBean createMemoryPool(String var0, boolean var1, long var2, long var4) {
      return new MemoryPoolImpl(var0, var1, var2, var4);
   }

   private static MemoryManagerMXBean createMemoryManager(String var0) {
      return new MemoryManagerImpl(var0);
   }

   private static GarbageCollectorMXBean createGarbageCollector(String var0, String var1) {
      return new GarbageCollectorImpl(var0);
   }
}
