package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MonitorInfoCompositeData;

public class MonitorInfo extends LockInfo {
   private int stackDepth;
   private StackTraceElement stackFrame;

   public MonitorInfo(String var1, int var2, int var3, StackTraceElement var4) {
      super(var1, var2);
      if (var3 >= 0 && var4 == null) {
         throw new IllegalArgumentException("Parameter stackDepth is " + var3 + " but stackFrame is null");
      } else if (var3 < 0 && var4 != null) {
         throw new IllegalArgumentException("Parameter stackDepth is " + var3 + " but stackFrame is not null");
      } else {
         this.stackDepth = var3;
         this.stackFrame = var4;
      }
   }

   public int getLockedStackDepth() {
      return this.stackDepth;
   }

   public StackTraceElement getLockedStackFrame() {
      return this.stackFrame;
   }

   public static MonitorInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof MonitorInfoCompositeData) {
         return ((MonitorInfoCompositeData)var0).getMonitorInfo();
      } else {
         MonitorInfoCompositeData.validateCompositeData(var0);
         String var1 = MonitorInfoCompositeData.getClassName(var0);
         int var2 = MonitorInfoCompositeData.getIdentityHashCode(var0);
         int var3 = MonitorInfoCompositeData.getLockedStackDepth(var0);
         StackTraceElement var4 = MonitorInfoCompositeData.getLockedStackFrame(var0);
         return new MonitorInfo(var1, var2, var3, var4);
      }
   }
}
