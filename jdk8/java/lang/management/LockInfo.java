package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.LockInfoCompositeData;

public class LockInfo {
   private String className;
   private int identityHashCode;

   public LockInfo(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("Parameter className cannot be null");
      } else {
         this.className = var1;
         this.identityHashCode = var2;
      }
   }

   LockInfo(Object var1) {
      this.className = var1.getClass().getName();
      this.identityHashCode = System.identityHashCode(var1);
   }

   public String getClassName() {
      return this.className;
   }

   public int getIdentityHashCode() {
      return this.identityHashCode;
   }

   public static LockInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof LockInfoCompositeData ? ((LockInfoCompositeData)var0).getLockInfo() : LockInfoCompositeData.toLockInfo(var0);
      }
   }

   public String toString() {
      return this.className + '@' + Integer.toHexString(this.identityHashCode);
   }
}
