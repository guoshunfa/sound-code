package sun.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import sun.management.counter.Counter;
import sun.management.counter.LongCounter;
import sun.management.counter.StringCounter;

class HotspotCompilation implements HotspotCompilationMBean {
   private VMManagement jvm;
   private static final String JAVA_CI = "java.ci.";
   private static final String COM_SUN_CI = "com.sun.ci.";
   private static final String SUN_CI = "sun.ci.";
   private static final String CI_COUNTER_NAME_PATTERN = "java.ci.|com.sun.ci.|sun.ci.";
   private LongCounter compilerThreads;
   private LongCounter totalCompiles;
   private LongCounter totalBailouts;
   private LongCounter totalInvalidates;
   private LongCounter nmethodCodeSize;
   private LongCounter nmethodSize;
   private StringCounter lastMethod;
   private LongCounter lastSize;
   private LongCounter lastType;
   private StringCounter lastFailedMethod;
   private LongCounter lastFailedType;
   private StringCounter lastInvalidatedMethod;
   private LongCounter lastInvalidatedType;
   private HotspotCompilation.CompilerThreadInfo[] threads;
   private int numActiveThreads;
   private Map<String, Counter> counters;

   HotspotCompilation(VMManagement var1) {
      this.jvm = var1;
      this.initCompilerCounters();
   }

   private Counter lookup(String var1) {
      Counter var2 = null;
      if ((var2 = (Counter)this.counters.get("sun.ci." + var1)) != null) {
         return var2;
      } else if ((var2 = (Counter)this.counters.get("com.sun.ci." + var1)) != null) {
         return var2;
      } else if ((var2 = (Counter)this.counters.get("java.ci." + var1)) != null) {
         return var2;
      } else {
         throw new AssertionError("Counter " + var1 + " does not exist");
      }
   }

   private void initCompilerCounters() {
      this.counters = new TreeMap();
      Iterator var1 = this.getInternalCompilerCounters().iterator();

      while(var1.hasNext()) {
         Counter var2 = (Counter)var1.next();
         this.counters.put(var2.getName(), var2);
      }

      this.compilerThreads = (LongCounter)this.lookup("threads");
      this.totalCompiles = (LongCounter)this.lookup("totalCompiles");
      this.totalBailouts = (LongCounter)this.lookup("totalBailouts");
      this.totalInvalidates = (LongCounter)this.lookup("totalInvalidates");
      this.nmethodCodeSize = (LongCounter)this.lookup("nmethodCodeSize");
      this.nmethodSize = (LongCounter)this.lookup("nmethodSize");
      this.lastMethod = (StringCounter)this.lookup("lastMethod");
      this.lastSize = (LongCounter)this.lookup("lastSize");
      this.lastType = (LongCounter)this.lookup("lastType");
      this.lastFailedMethod = (StringCounter)this.lookup("lastFailedMethod");
      this.lastFailedType = (LongCounter)this.lookup("lastFailedType");
      this.lastInvalidatedMethod = (StringCounter)this.lookup("lastInvalidatedMethod");
      this.lastInvalidatedType = (LongCounter)this.lookup("lastInvalidatedType");
      this.numActiveThreads = (int)this.compilerThreads.longValue();
      this.threads = new HotspotCompilation.CompilerThreadInfo[this.numActiveThreads + 1];
      if (this.counters.containsKey("sun.ci.adapterThread.compiles")) {
         this.threads[0] = new HotspotCompilation.CompilerThreadInfo("adapterThread", 0);
         ++this.numActiveThreads;
      } else {
         this.threads[0] = null;
      }

      for(int var3 = 1; var3 < this.threads.length; ++var3) {
         this.threads[var3] = new HotspotCompilation.CompilerThreadInfo("compilerThread", var3 - 1);
      }

   }

   public int getCompilerThreadCount() {
      return this.numActiveThreads;
   }

   public long getTotalCompileCount() {
      return this.totalCompiles.longValue();
   }

   public long getBailoutCompileCount() {
      return this.totalBailouts.longValue();
   }

   public long getInvalidatedCompileCount() {
      return this.totalInvalidates.longValue();
   }

   public long getCompiledMethodCodeSize() {
      return this.nmethodCodeSize.longValue();
   }

   public long getCompiledMethodSize() {
      return this.nmethodSize.longValue();
   }

   public List<CompilerThreadStat> getCompilerThreadStats() {
      ArrayList var1 = new ArrayList(this.threads.length);
      int var2 = 0;
      if (this.threads[0] == null) {
         var2 = 1;
      }

      while(var2 < this.threads.length) {
         var1.add(this.threads[var2].getCompilerThreadStat());
         ++var2;
      }

      return var1;
   }

   public MethodInfo getLastCompile() {
      return new MethodInfo(this.lastMethod.stringValue(), (long)((int)this.lastType.longValue()), (int)this.lastSize.longValue());
   }

   public MethodInfo getFailedCompile() {
      return new MethodInfo(this.lastFailedMethod.stringValue(), (long)((int)this.lastFailedType.longValue()), -1);
   }

   public MethodInfo getInvalidatedCompile() {
      return new MethodInfo(this.lastInvalidatedMethod.stringValue(), (long)((int)this.lastInvalidatedType.longValue()), -1);
   }

   public List<Counter> getInternalCompilerCounters() {
      return this.jvm.getInternalCounters("java.ci.|com.sun.ci.|sun.ci.");
   }

   private class CompilerThreadInfo {
      int index;
      String name;
      StringCounter method;
      LongCounter type;
      LongCounter compiles;
      LongCounter time;

      CompilerThreadInfo(String var2, int var3) {
         String var4 = var2 + "." + var3 + ".";
         this.name = var2 + "-" + var3;
         this.method = (StringCounter)HotspotCompilation.this.lookup(var4 + "method");
         this.type = (LongCounter)HotspotCompilation.this.lookup(var4 + "type");
         this.compiles = (LongCounter)HotspotCompilation.this.lookup(var4 + "compiles");
         this.time = (LongCounter)HotspotCompilation.this.lookup(var4 + "time");
      }

      CompilerThreadInfo(String var2) {
         String var3 = var2 + ".";
         this.name = var2;
         this.method = (StringCounter)HotspotCompilation.this.lookup(var3 + "method");
         this.type = (LongCounter)HotspotCompilation.this.lookup(var3 + "type");
         this.compiles = (LongCounter)HotspotCompilation.this.lookup(var3 + "compiles");
         this.time = (LongCounter)HotspotCompilation.this.lookup(var3 + "time");
      }

      CompilerThreadStat getCompilerThreadStat() {
         MethodInfo var1 = new MethodInfo(this.method.stringValue(), (long)((int)this.type.longValue()), -1);
         return new CompilerThreadStat(this.name, this.compiles.longValue(), this.time.longValue(), var1);
      }
   }
}
