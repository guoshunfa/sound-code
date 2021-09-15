package javax.management.openmbean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

public class OpenMBeanInfoSupport extends MBeanInfo implements OpenMBeanInfo {
   static final long serialVersionUID = 4349395935420511492L;
   private transient Integer myHashCode;
   private transient String myToString;

   public OpenMBeanInfoSupport(String var1, String var2, OpenMBeanAttributeInfo[] var3, OpenMBeanConstructorInfo[] var4, OpenMBeanOperationInfo[] var5, MBeanNotificationInfo[] var6) {
      this(var1, var2, var3, var4, var5, var6, (Descriptor)null);
   }

   public OpenMBeanInfoSupport(String var1, String var2, OpenMBeanAttributeInfo[] var3, OpenMBeanConstructorInfo[] var4, OpenMBeanOperationInfo[] var5, MBeanNotificationInfo[] var6, Descriptor var7) {
      super(var1, var2, attributeArray(var3), constructorArray(var4), operationArray(var5), var6 == null ? null : (MBeanNotificationInfo[])var6.clone(), var7);
      this.myHashCode = null;
      this.myToString = null;
   }

   private static MBeanAttributeInfo[] attributeArray(OpenMBeanAttributeInfo[] var0) {
      if (var0 == null) {
         return null;
      } else {
         MBeanAttributeInfo[] var1 = new MBeanAttributeInfo[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   private static MBeanConstructorInfo[] constructorArray(OpenMBeanConstructorInfo[] var0) {
      if (var0 == null) {
         return null;
      } else {
         MBeanConstructorInfo[] var1 = new MBeanConstructorInfo[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   private static MBeanOperationInfo[] operationArray(OpenMBeanOperationInfo[] var0) {
      if (var0 == null) {
         return null;
      } else {
         MBeanOperationInfo[] var1 = new MBeanOperationInfo[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         OpenMBeanInfo var2;
         try {
            var2 = (OpenMBeanInfo)var1;
         } catch (ClassCastException var4) {
            return false;
         }

         if (!Objects.equals(this.getClassName(), var2.getClassName())) {
            return false;
         } else if (!sameArrayContents(this.getAttributes(), var2.getAttributes())) {
            return false;
         } else if (!sameArrayContents(this.getConstructors(), var2.getConstructors())) {
            return false;
         } else if (!sameArrayContents(this.getOperations(), var2.getOperations())) {
            return false;
         } else {
            return sameArrayContents(this.getNotifications(), var2.getNotifications());
         }
      }
   }

   private static <T> boolean sameArrayContents(T[] var0, T[] var1) {
      return (new HashSet(Arrays.asList(var0))).equals(new HashSet(Arrays.asList(var1)));
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         int var1 = 0;
         if (this.getClassName() != null) {
            var1 += this.getClassName().hashCode();
         }

         var1 += arraySetHash(this.getAttributes());
         var1 += arraySetHash(this.getConstructors());
         var1 += arraySetHash(this.getOperations());
         var1 += arraySetHash(this.getNotifications());
         this.myHashCode = var1;
      }

      return this.myHashCode;
   }

   private static <T> int arraySetHash(T[] var0) {
      return (new HashSet(Arrays.asList(var0))).hashCode();
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = this.getClass().getName() + "(mbean_class_name=" + this.getClassName() + ",attributes=" + Arrays.asList(this.getAttributes()).toString() + ",constructors=" + Arrays.asList(this.getConstructors()).toString() + ",operations=" + Arrays.asList(this.getOperations()).toString() + ",notifications=" + Arrays.asList(this.getNotifications()).toString() + ",descriptor=" + this.getDescriptor() + ")";
      }

      return this.myToString;
   }
}
