package sun.management;

import java.lang.management.MonitorInfo;
import java.util.Set;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MonitorInfoCompositeData extends LazyCompositeData {
   private final MonitorInfo lock;
   private static final CompositeType monitorInfoCompositeType;
   private static final String[] monitorInfoItemNames;
   private static final String CLASS_NAME = "className";
   private static final String IDENTITY_HASH_CODE = "identityHashCode";
   private static final String LOCKED_STACK_FRAME = "lockedStackFrame";
   private static final String LOCKED_STACK_DEPTH = "lockedStackDepth";
   private static final long serialVersionUID = -5825215591822908529L;

   private MonitorInfoCompositeData(MonitorInfo var1) {
      this.lock = var1;
   }

   public MonitorInfo getMonitorInfo() {
      return this.lock;
   }

   public static CompositeData toCompositeData(MonitorInfo var0) {
      MonitorInfoCompositeData var1 = new MonitorInfoCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      int var1 = monitorInfoItemNames.length;
      Object[] var2 = new Object[var1];
      CompositeData var3 = LockInfoCompositeData.toCompositeData(this.lock);

      for(int var4 = 0; var4 < var1; ++var4) {
         String var5 = monitorInfoItemNames[var4];
         if (var5.equals("lockedStackFrame")) {
            StackTraceElement var6 = this.lock.getLockedStackFrame();
            var2[var4] = var6 != null ? StackTraceElementCompositeData.toCompositeData(var6) : null;
         } else if (var5.equals("lockedStackDepth")) {
            var2[var4] = new Integer(this.lock.getLockedStackDepth());
         } else {
            var2[var4] = var3.get(var5);
         }
      }

      try {
         return new CompositeDataSupport(monitorInfoCompositeType, monitorInfoItemNames, var2);
      } catch (OpenDataException var7) {
         throw new AssertionError(var7);
      }
   }

   static CompositeType getMonitorInfoCompositeType() {
      return monitorInfoCompositeType;
   }

   public static String getClassName(CompositeData var0) {
      return getString(var0, "className");
   }

   public static int getIdentityHashCode(CompositeData var0) {
      return getInt(var0, "identityHashCode");
   }

   public static StackTraceElement getLockedStackFrame(CompositeData var0) {
      CompositeData var1 = (CompositeData)var0.get("lockedStackFrame");
      return var1 != null ? StackTraceElementCompositeData.from(var1) : null;
   }

   public static int getLockedStackDepth(CompositeData var0) {
      return getInt(var0, "lockedStackDepth");
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(monitorInfoCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for MonitorInfo");
      }
   }

   static {
      try {
         monitorInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MonitorInfo.class);
         Set var0 = monitorInfoCompositeType.keySet();
         monitorInfoItemNames = (String[])var0.toArray(new String[0]);
      } catch (OpenDataException var1) {
         throw new AssertionError(var1);
      }
   }
}
