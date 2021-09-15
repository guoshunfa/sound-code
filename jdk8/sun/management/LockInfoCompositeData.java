package sun.management;

import java.lang.management.LockInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class LockInfoCompositeData extends LazyCompositeData {
   private final LockInfo lock;
   private static final CompositeType lockInfoCompositeType;
   private static final String CLASS_NAME = "className";
   private static final String IDENTITY_HASH_CODE = "identityHashCode";
   private static final String[] lockInfoItemNames;
   private static final long serialVersionUID = -6374759159749014052L;

   private LockInfoCompositeData(LockInfo var1) {
      this.lock = var1;
   }

   public LockInfo getLockInfo() {
      return this.lock;
   }

   public static CompositeData toCompositeData(LockInfo var0) {
      if (var0 == null) {
         return null;
      } else {
         LockInfoCompositeData var1 = new LockInfoCompositeData(var0);
         return var1.getCompositeData();
      }
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{new String(this.lock.getClassName()), new Integer(this.lock.getIdentityHashCode())};

      try {
         return new CompositeDataSupport(lockInfoCompositeType, lockInfoItemNames, var1);
      } catch (OpenDataException var3) {
         throw Util.newException(var3);
      }
   }

   static CompositeType getLockInfoCompositeType() {
      return lockInfoCompositeType;
   }

   public static LockInfo toLockInfo(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(lockInfoCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for LockInfo");
      } else {
         String var1 = getString(var0, "className");
         int var2 = getInt(var0, "identityHashCode");
         return new LockInfo(var1, var2);
      }
   }

   static {
      try {
         lockInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(LockInfo.class);
      } catch (OpenDataException var1) {
         throw Util.newException(var1);
      }

      lockInfoItemNames = new String[]{"className", "identityHashCode"};
   }
}
