package sun.management;

import com.sun.management.GcInfo;
import java.io.InvalidObjectException;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

public class GcInfoCompositeData extends LazyCompositeData {
   private final GcInfo info;
   private final GcInfoBuilder builder;
   private final Object[] gcExtItemValues;
   private static final String ID = "id";
   private static final String START_TIME = "startTime";
   private static final String END_TIME = "endTime";
   private static final String DURATION = "duration";
   private static final String MEMORY_USAGE_BEFORE_GC = "memoryUsageBeforeGc";
   private static final String MEMORY_USAGE_AFTER_GC = "memoryUsageAfterGc";
   private static final String[] baseGcInfoItemNames = new String[]{"id", "startTime", "endTime", "duration", "memoryUsageBeforeGc", "memoryUsageAfterGc"};
   private static MappedMXBeanType memoryUsageMapType;
   private static OpenType[] baseGcInfoItemTypes;
   private static CompositeType baseGcInfoCompositeType;
   private static final long serialVersionUID = -5716428894085882742L;

   public GcInfoCompositeData(GcInfo var1, GcInfoBuilder var2, Object[] var3) {
      this.info = var1;
      this.builder = var2;
      this.gcExtItemValues = var3;
   }

   public GcInfo getGcInfo() {
      return this.info;
   }

   public static CompositeData toCompositeData(final GcInfo var0) {
      GcInfoBuilder var1 = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction<GcInfoBuilder>() {
         public GcInfoBuilder run() {
            try {
               Class var1 = Class.forName("com.sun.management.GcInfo");
               Field var2 = var1.getDeclaredField("builder");
               var2.setAccessible(true);
               return (GcInfoBuilder)var2.get(var0);
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException var3) {
               return null;
            }
         }
      });
      Object[] var2 = (Object[])AccessController.doPrivileged(new PrivilegedAction<Object[]>() {
         public Object[] run() {
            try {
               Class var1 = Class.forName("com.sun.management.GcInfo");
               Field var2 = var1.getDeclaredField("extAttributes");
               var2.setAccessible(true);
               return (Object[])((Object[])var2.get(var0));
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException var3) {
               return null;
            }
         }
      });
      GcInfoCompositeData var3 = new GcInfoCompositeData(var0, var1, var2);
      return var3.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      Object[] var1;
      try {
         var1 = new Object[]{new Long(this.info.getId()), new Long(this.info.getStartTime()), new Long(this.info.getEndTime()), new Long(this.info.getDuration()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageBeforeGc()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageAfterGc())};
      } catch (OpenDataException var6) {
         throw new AssertionError(var6);
      }

      int var2 = this.builder.getGcExtItemCount();
      if (var2 == 0 && this.gcExtItemValues != null && this.gcExtItemValues.length != 0) {
         throw new AssertionError("Unexpected Gc Extension Item Values");
      } else if (var2 > 0 && (this.gcExtItemValues == null || var2 != this.gcExtItemValues.length)) {
         throw new AssertionError("Unmatched Gc Extension Item Values");
      } else {
         Object[] var3 = new Object[var1.length + var2];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         if (var2 > 0) {
            System.arraycopy(this.gcExtItemValues, 0, var3, var1.length, var2);
         }

         try {
            return new CompositeDataSupport(this.builder.getGcInfoCompositeType(), this.builder.getItemNames(), var3);
         } catch (OpenDataException var5) {
            throw new AssertionError(var5);
         }
      }
   }

   static String[] getBaseGcInfoItemNames() {
      return baseGcInfoItemNames;
   }

   static synchronized OpenType[] getBaseGcInfoItemTypes() {
      if (baseGcInfoItemTypes == null) {
         OpenType var0 = memoryUsageMapType.getOpenType();
         baseGcInfoItemTypes = new OpenType[]{SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, var0, var0};
      }

      return baseGcInfoItemTypes;
   }

   public static long getId(CompositeData var0) {
      return getLong(var0, "id");
   }

   public static long getStartTime(CompositeData var0) {
      return getLong(var0, "startTime");
   }

   public static long getEndTime(CompositeData var0) {
      return getLong(var0, "endTime");
   }

   public static Map<String, MemoryUsage> getMemoryUsageBeforeGc(CompositeData var0) {
      try {
         TabularData var1 = (TabularData)var0.get("memoryUsageBeforeGc");
         return cast(memoryUsageMapType.toJavaTypeData(var1));
      } catch (OpenDataException | InvalidObjectException var2) {
         throw new AssertionError(var2);
      }
   }

   public static Map<String, MemoryUsage> cast(Object var0) {
      return (Map)var0;
   }

   public static Map<String, MemoryUsage> getMemoryUsageAfterGc(CompositeData var0) {
      try {
         TabularData var1 = (TabularData)var0.get("memoryUsageAfterGc");
         return cast(memoryUsageMapType.toJavaTypeData(var1));
      } catch (OpenDataException | InvalidObjectException var2) {
         throw new AssertionError(var2);
      }
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(getBaseGcInfoCompositeType(), var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for GcInfo");
      }
   }

   static synchronized CompositeType getBaseGcInfoCompositeType() {
      if (baseGcInfoCompositeType == null) {
         try {
            baseGcInfoCompositeType = new CompositeType("sun.management.BaseGcInfoCompositeType", "CompositeType for Base GcInfo", getBaseGcInfoItemNames(), getBaseGcInfoItemNames(), getBaseGcInfoItemTypes());
         } catch (OpenDataException var1) {
            throw Util.newException(var1);
         }
      }

      return baseGcInfoCompositeType;
   }

   static {
      try {
         Method var0 = GcInfo.class.getMethod("getMemoryUsageBeforeGc");
         memoryUsageMapType = MappedMXBeanType.getMappedType(var0.getGenericReturnType());
      } catch (OpenDataException | NoSuchMethodException var1) {
         throw new AssertionError(var1);
      }

      baseGcInfoItemTypes = null;
      baseGcInfoCompositeType = null;
   }
}
