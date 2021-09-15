package javax.management.openmbean;

import java.util.Arrays;
import javax.management.Descriptor;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;

public class OpenMBeanConstructorInfoSupport extends MBeanConstructorInfo implements OpenMBeanConstructorInfo {
   static final long serialVersionUID = -4400441579007477003L;
   private transient Integer myHashCode;
   private transient String myToString;

   public OpenMBeanConstructorInfoSupport(String var1, String var2, OpenMBeanParameterInfo[] var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public OpenMBeanConstructorInfoSupport(String var1, String var2, OpenMBeanParameterInfo[] var3, Descriptor var4) {
      super(var1, var2, arrayCopyCast(var3), var4);
      this.myHashCode = null;
      this.myToString = null;
      if (var1 != null && !var1.trim().equals("")) {
         if (var2 == null || var2.trim().equals("")) {
            throw new IllegalArgumentException("Argument description cannot be null or empty");
         }
      } else {
         throw new IllegalArgumentException("Argument name cannot be null or empty");
      }
   }

   private static MBeanParameterInfo[] arrayCopyCast(OpenMBeanParameterInfo[] var0) {
      if (var0 == null) {
         return null;
      } else {
         MBeanParameterInfo[] var1 = new MBeanParameterInfo[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         OpenMBeanConstructorInfo var2;
         try {
            var2 = (OpenMBeanConstructorInfo)var1;
         } catch (ClassCastException var4) {
            return false;
         }

         if (!this.getName().equals(var2.getName())) {
            return false;
         } else {
            return Arrays.equals((Object[])this.getSignature(), (Object[])var2.getSignature());
         }
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         byte var1 = 0;
         int var2 = var1 + this.getName().hashCode();
         var2 += Arrays.asList(this.getSignature()).hashCode();
         this.myHashCode = var2;
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = this.getClass().getName() + "(name=" + this.getName() + ",signature=" + Arrays.asList(this.getSignature()).toString() + ",descriptor=" + this.getDescriptor() + ")";
      }

      return this.myToString;
   }
}
