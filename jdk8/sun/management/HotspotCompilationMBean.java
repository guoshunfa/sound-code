package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface HotspotCompilationMBean {
   int getCompilerThreadCount();

   List<CompilerThreadStat> getCompilerThreadStats();

   long getTotalCompileCount();

   long getBailoutCompileCount();

   long getInvalidatedCompileCount();

   MethodInfo getLastCompile();

   MethodInfo getFailedCompile();

   MethodInfo getInvalidatedCompile();

   long getCompiledMethodCodeSize();

   long getCompiledMethodSize();

   List<Counter> getInternalCompilerCounters();
}
