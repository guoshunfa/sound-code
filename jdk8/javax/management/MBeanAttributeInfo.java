package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

public class MBeanAttributeInfo extends MBeanFeatureInfo implements Cloneable {
   private static final long serialVersionUID;
   static final MBeanAttributeInfo[] NO_ATTRIBUTES;
   private final String attributeType;
   private final boolean isWrite;
   private final boolean isRead;
   private final boolean is;

   public MBeanAttributeInfo(String var1, String var2, String var3, boolean var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, var5, var6, (Descriptor)null);
   }

   public MBeanAttributeInfo(String var1, String var2, String var3, boolean var4, boolean var5, boolean var6, Descriptor var7) {
      super(var1, var3, var7);
      this.attributeType = var2;
      this.isRead = var4;
      this.isWrite = var5;
      if (var6 && !var4) {
         throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-readable attribute");
      } else if (var6 && !var2.equals("java.lang.Boolean") && !var2.equals("boolean")) {
         throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-boolean attribute");
      } else {
         this.is = var6;
      }
   }

   public MBeanAttributeInfo(String var1, String var2, Method var3, Method var4) throws IntrospectionException {
      this(var1, attributeType(var3, var4), var2, var3 != null, var4 != null, isIs(var3), ImmutableDescriptor.union(Introspector.descriptorForElement(var3), Introspector.descriptorForElement(var4)));
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String getType() {
      return this.attributeType;
   }

   public boolean isReadable() {
      return this.isRead;
   }

   public boolean isWritable() {
      return this.isWrite;
   }

   public boolean isIs() {
      return this.is;
   }

   public String toString() {
      String var1;
      if (this.isReadable()) {
         if (this.isWritable()) {
            var1 = "read/write";
         } else {
            var1 = "read-only";
         }
      } else if (this.isWritable()) {
         var1 = "write-only";
      } else {
         var1 = "no-access";
      }

      return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", type=" + this.getType() + ", " + var1 + ", " + (this.isIs() ? "isIs, " : "") + "descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanAttributeInfo)) {
         return false;
      } else {
         MBeanAttributeInfo var2 = (MBeanAttributeInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getType(), this.getType()) && Objects.equals(var2.getDescription(), this.getDescription()) && Objects.equals(var2.getDescriptor(), this.getDescriptor()) && var2.isReadable() == this.isReadable() && var2.isWritable() == this.isWritable() && var2.isIs() == this.isIs();
      }
   }

   public int hashCode() {
      return Objects.hash(this.getName(), this.getType());
   }

   private static boolean isIs(Method var0) {
      return var0 != null && var0.getName().startsWith("is") && (var0.getReturnType().equals(Boolean.TYPE) || var0.getReturnType().equals(Boolean.class));
   }

   private static String attributeType(Method var0, Method var1) throws IntrospectionException {
      Class var2 = null;
      if (var0 != null) {
         if (var0.getParameterTypes().length != 0) {
            throw new IntrospectionException("bad getter arg count");
         }

         var2 = var0.getReturnType();
         if (var2 == Void.TYPE) {
            throw new IntrospectionException("getter " + var0.getName() + " returns void");
         }
      }

      if (var1 != null) {
         Class[] var3 = var1.getParameterTypes();
         if (var3.length != 1) {
            throw new IntrospectionException("bad setter arg count");
         }

         if (var2 == null) {
            var2 = var3[0];
         } else if (var2 != var3[0]) {
            throw new IntrospectionException("type mismatch between getter and setter");
         }
      }

      if (var2 == null) {
         throw new IntrospectionException("getter and setter cannot both be null");
      } else {
         return var2.getName();
      }
   }

   static {
      long var0 = 8644704819898565848L;

      try {
         GetPropertyAction var2 = new GetPropertyAction("jmx.serial.form");
         String var3 = (String)AccessController.doPrivileged((PrivilegedAction)var2);
         if ("1.0".equals(var3)) {
            var0 = 7043855487133450673L;
         }
      } catch (Exception var4) {
      }

      serialVersionUID = var0;
      NO_ATTRIBUTES = new MBeanAttributeInfo[0];
   }
}
