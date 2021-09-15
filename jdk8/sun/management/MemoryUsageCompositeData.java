package sun.management;

import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MemoryUsageCompositeData extends LazyCompositeData {
   private final MemoryUsage usage;
   private static final CompositeType memoryUsageCompositeType;
   private static final String INIT = "init";
   private static final String USED = "used";
   private static final String COMMITTED = "committed";
   private static final String MAX = "max";
   private static final String[] memoryUsageItemNames;
   private static final long serialVersionUID = -8504291541083874143L;

   private MemoryUsageCompositeData(MemoryUsage var1) {
      this.usage = var1;
   }

   public MemoryUsage getMemoryUsage() {
      return this.usage;
   }

   public static CompositeData toCompositeData(MemoryUsage var0) {
      MemoryUsageCompositeData var1 = new MemoryUsageCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{new Long(this.usage.getInit()), new Long(this.usage.getUsed()), new Long(this.usage.getCommitted()), new Long(this.usage.getMax())};

      try {
         return new CompositeDataSupport(memoryUsageCompositeType, memoryUsageItemNames, var1);
      } catch (OpenDataException var3) {
         throw new AssertionError(var3);
      }
   }

   static CompositeType getMemoryUsageCompositeType() {
      return memoryUsageCompositeType;
   }

   public static long getInit(CompositeData var0) {
      return getLong(var0, "init");
   }

   public static long getUsed(CompositeData var0) {
      return getLong(var0, "used");
   }

   public static long getCommitted(CompositeData var0) {
      return getLong(var0, "committed");
   }

   public static long getMax(CompositeData var0) {
      return getLong(var0, "max");
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(memoryUsageCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for MemoryUsage");
      }
   }

   static {
      try {
         memoryUsageCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryUsage.class);
      } catch (OpenDataException var1) {
         throw new AssertionError(var1);
      }

      memoryUsageItemNames = new String[]{"init", "used", "committed", "max"};
   }
}
