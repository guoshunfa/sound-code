package sun.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

class HotspotThread implements HotspotThreadMBean {
   private VMManagement jvm;
   private static final String JAVA_THREADS = "java.threads.";
   private static final String COM_SUN_THREADS = "com.sun.threads.";
   private static final String SUN_THREADS = "sun.threads.";
   private static final String THREADS_COUNTER_NAME_PATTERN = "java.threads.|com.sun.threads.|sun.threads.";

   HotspotThread(VMManagement var1) {
      this.jvm = var1;
   }

   public native int getInternalThreadCount();

   public Map<String, Long> getInternalThreadCpuTimes() {
      int var1 = this.getInternalThreadCount();
      if (var1 == 0) {
         return Collections.emptyMap();
      } else {
         String[] var2 = new String[var1];
         long[] var3 = new long[var1];
         int var4 = this.getInternalThreadTimes0(var2, var3);
         HashMap var5 = new HashMap(var4);

         for(int var6 = 0; var6 < var4; ++var6) {
            var5.put(var2[var6], new Long(var3[var6]));
         }

         return var5;
      }
   }

   public native int getInternalThreadTimes0(String[] var1, long[] var2);

   public List<Counter> getInternalThreadingCounters() {
      return this.jvm.getInternalCounters("java.threads.|com.sun.threads.|sun.threads.");
   }
}
