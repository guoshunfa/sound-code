package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GarbageCollectionNotifInfoCompositeData extends LazyCompositeData {
   private final GarbageCollectionNotificationInfo gcNotifInfo;
   private static final String GC_NAME = "gcName";
   private static final String GC_ACTION = "gcAction";
   private static final String GC_CAUSE = "gcCause";
   private static final String GC_INFO = "gcInfo";
   private static final String[] gcNotifInfoItemNames = new String[]{"gcName", "gcAction", "gcCause", "gcInfo"};
   private static HashMap<GcInfoBuilder, CompositeType> compositeTypeByBuilder = new HashMap();
   private static CompositeType baseGcNotifInfoCompositeType = null;
   private static final long serialVersionUID = -1805123446483771292L;

   public GarbageCollectionNotifInfoCompositeData(GarbageCollectionNotificationInfo var1) {
      this.gcNotifInfo = var1;
   }

   public GarbageCollectionNotificationInfo getGarbageCollectionNotifInfo() {
      return this.gcNotifInfo;
   }

   public static CompositeData toCompositeData(GarbageCollectionNotificationInfo var0) {
      GarbageCollectionNotifInfoCompositeData var1 = new GarbageCollectionNotifInfoCompositeData(var0);
      return var1.getCompositeData();
   }

   private CompositeType getCompositeTypeByBuilder() {
      GcInfoBuilder var1 = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction<GcInfoBuilder>() {
         public GcInfoBuilder run() {
            try {
               Class var1 = Class.forName("com.sun.management.GcInfo");
               Field var2 = var1.getDeclaredField("builder");
               var2.setAccessible(true);
               return (GcInfoBuilder)var2.get(GarbageCollectionNotifInfoCompositeData.this.gcNotifInfo.getGcInfo());
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException var3) {
               return null;
            }
         }
      });
      CompositeType var2 = null;
      synchronized(compositeTypeByBuilder) {
         var2 = (CompositeType)compositeTypeByBuilder.get(var1);
         if (var2 == null) {
            OpenType[] var4 = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, var1.getGcInfoCompositeType()};

            try {
               var2 = new CompositeType("sun.management.GarbageCollectionNotifInfoCompositeType", "CompositeType for GC notification info", gcNotifInfoItemNames, gcNotifInfoItemNames, var4);
               compositeTypeByBuilder.put(var1, var2);
            } catch (OpenDataException var7) {
               throw Util.newException(var7);
            }
         }

         return var2;
      }
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{this.gcNotifInfo.getGcName(), this.gcNotifInfo.getGcAction(), this.gcNotifInfo.getGcCause(), GcInfoCompositeData.toCompositeData(this.gcNotifInfo.getGcInfo())};
      CompositeType var2 = this.getCompositeTypeByBuilder();

      try {
         return new CompositeDataSupport(var2, gcNotifInfoItemNames, var1);
      } catch (OpenDataException var4) {
         throw new AssertionError(var4);
      }
   }

   public static String getGcName(CompositeData var0) {
      String var1 = getString(var0, "gcName");
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid composite data: Attribute gcName has null value");
      } else {
         return var1;
      }
   }

   public static String getGcAction(CompositeData var0) {
      String var1 = getString(var0, "gcAction");
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid composite data: Attribute gcAction has null value");
      } else {
         return var1;
      }
   }

   public static String getGcCause(CompositeData var0) {
      String var1 = getString(var0, "gcCause");
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid composite data: Attribute gcCause has null value");
      } else {
         return var1;
      }
   }

   public static GcInfo getGcInfo(CompositeData var0) {
      CompositeData var1 = (CompositeData)var0.get("gcInfo");
      return GcInfo.from(var1);
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(getBaseGcNotifInfoCompositeType(), var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for GarbageCollectionNotificationInfo");
      }
   }

   private static synchronized CompositeType getBaseGcNotifInfoCompositeType() {
      if (baseGcNotifInfoCompositeType == null) {
         try {
            OpenType[] var0 = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, GcInfoCompositeData.getBaseGcInfoCompositeType()};
            baseGcNotifInfoCompositeType = new CompositeType("sun.management.BaseGarbageCollectionNotifInfoCompositeType", "CompositeType for Base GarbageCollectionNotificationInfo", gcNotifInfoItemNames, gcNotifInfoItemNames, var0);
         } catch (OpenDataException var1) {
            throw Util.newException(var1);
         }
      }

      return baseGcNotifInfoCompositeType;
   }
}
