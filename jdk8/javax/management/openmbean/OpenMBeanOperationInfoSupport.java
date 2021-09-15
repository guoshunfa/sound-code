package javax.management.openmbean;

import java.util.Arrays;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

public class OpenMBeanOperationInfoSupport extends MBeanOperationInfo implements OpenMBeanOperationInfo {
   static final long serialVersionUID = 4996859732565369366L;
   private OpenType<?> returnOpenType;
   private transient Integer myHashCode;
   private transient String myToString;

   public OpenMBeanOperationInfoSupport(String var1, String var2, OpenMBeanParameterInfo[] var3, OpenType<?> var4, int var5) {
      this(var1, var2, var3, var4, var5, (Descriptor)null);
   }

   public OpenMBeanOperationInfoSupport(String var1, String var2, OpenMBeanParameterInfo[] var3, OpenType<?> var4, int var5, Descriptor var6) {
      super(var1, var2, arrayCopyCast(var3), var4 == null ? null : var4.getClassName(), var5, ImmutableDescriptor.union(var6, var4 == null ? null : var4.getDescriptor()));
      this.myHashCode = null;
      this.myToString = null;
      if (var1 != null && !var1.trim().equals("")) {
         if (var2 != null && !var2.trim().equals("")) {
            if (var4 == null) {
               throw new IllegalArgumentException("Argument returnOpenType cannot be null");
            } else if (var5 != 1 && var5 != 2 && var5 != 0 && var5 != 3) {
               throw new IllegalArgumentException("Argument impact can only be one of ACTION, ACTION_INFO, INFO, or UNKNOWN: " + var5);
            } else {
               this.returnOpenType = var4;
            }
         } else {
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

   private static OpenMBeanParameterInfo[] arrayCopyCast(MBeanParameterInfo[] var0) {
      if (var0 == null) {
         return null;
      } else {
         OpenMBeanParameterInfo[] var1 = new OpenMBeanParameterInfo[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   public OpenType<?> getReturnOpenType() {
      return this.returnOpenType;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         OpenMBeanOperationInfo var2;
         try {
            var2 = (OpenMBeanOperationInfo)var1;
         } catch (ClassCastException var4) {
            return false;
         }

         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (!Arrays.equals((Object[])this.getSignature(), (Object[])var2.getSignature())) {
            return false;
         } else if (!this.getReturnOpenType().equals(var2.getReturnOpenType())) {
            return false;
         } else {
            return this.getImpact() == var2.getImpact();
         }
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         byte var1 = 0;
         int var2 = var1 + this.getName().hashCode();
         var2 += Arrays.asList(this.getSignature()).hashCode();
         var2 += this.getReturnOpenType().hashCode();
         var2 += this.getImpact();
         this.myHashCode = var2;
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = this.getClass().getName() + "(name=" + this.getName() + ",signature=" + Arrays.asList(this.getSignature()).toString() + ",return=" + this.getReturnOpenType().toString() + ",impact=" + this.getImpact() + ",descriptor=" + this.getDescriptor() + ")";
      }

      return this.myToString;
   }

   private Object readResolve() {
      return this.getDescriptor().getFieldNames().length == 0 ? new OpenMBeanOperationInfoSupport(this.name, this.description, arrayCopyCast(this.getSignature()), this.returnOpenType, this.getImpact()) : this;
   }
}
