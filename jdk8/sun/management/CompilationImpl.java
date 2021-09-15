package sun.management;

import java.lang.management.CompilationMXBean;
import javax.management.ObjectName;

class CompilationImpl implements CompilationMXBean {
   private final VMManagement jvm;
   private final String name;

   CompilationImpl(VMManagement var1) {
      this.jvm = var1;
      this.name = this.jvm.getCompilerName();
      if (this.name == null) {
         throw new AssertionError("Null compiler name");
      }
   }

   public String getName() {
      return this.name;
   }

   public boolean isCompilationTimeMonitoringSupported() {
      return this.jvm.isCompilationTimeMonitoringSupported();
   }

   public long getTotalCompilationTime() {
      if (!this.isCompilationTimeMonitoringSupported()) {
         throw new UnsupportedOperationException("Compilation time monitoring is not supported.");
      } else {
         return this.jvm.getTotalCompileTime();
      }
   }

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=Compilation");
   }
}
