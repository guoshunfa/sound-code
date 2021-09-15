package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class MBeanOperationInfo extends MBeanFeatureInfo implements Cloneable {
   static final long serialVersionUID = -6178860474881375330L;
   static final MBeanOperationInfo[] NO_OPERATIONS = new MBeanOperationInfo[0];
   public static final int INFO = 0;
   public static final int ACTION = 1;
   public static final int ACTION_INFO = 2;
   public static final int UNKNOWN = 3;
   private final String type;
   private final MBeanParameterInfo[] signature;
   private final int impact;
   private final transient boolean arrayGettersSafe;

   public MBeanOperationInfo(String var1, Method var2) {
      this(var2.getName(), var1, methodSignature(var2), var2.getReturnType().getName(), 3, Introspector.descriptorForElement(var2));
   }

   public MBeanOperationInfo(String var1, String var2, MBeanParameterInfo[] var3, String var4, int var5) {
      this(var1, var2, var3, var4, var5, (Descriptor)null);
   }

   public MBeanOperationInfo(String var1, String var2, MBeanParameterInfo[] var3, String var4, int var5, Descriptor var6) {
      super(var1, var2, var6);
      if (var3 != null && var3.length != 0) {
         var3 = (MBeanParameterInfo[])var3.clone();
      } else {
         var3 = MBeanParameterInfo.NO_PARAMS;
      }

      this.signature = var3;
      this.type = var4;
      this.impact = var5;
      this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanOperationInfo.class);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String getReturnType() {
      return this.type;
   }

   public MBeanParameterInfo[] getSignature() {
      if (this.signature == null) {
         return MBeanParameterInfo.NO_PARAMS;
      } else {
         return this.signature.length == 0 ? this.signature : (MBeanParameterInfo[])this.signature.clone();
      }
   }

   private MBeanParameterInfo[] fastGetSignature() {
      if (this.arrayGettersSafe) {
         return this.signature == null ? MBeanParameterInfo.NO_PARAMS : this.signature;
      } else {
         return this.getSignature();
      }
   }

   public int getImpact() {
      return this.impact;
   }

   public String toString() {
      String var1;
      switch(this.getImpact()) {
      case 0:
         var1 = "info";
         break;
      case 1:
         var1 = "action";
         break;
      case 2:
         var1 = "action/info";
         break;
      case 3:
         var1 = "unknown";
         break;
      default:
         var1 = "(" + this.getImpact() + ")";
      }

      return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", returnType=" + this.getReturnType() + ", signature=" + Arrays.asList(this.fastGetSignature()) + ", impact=" + var1 + ", descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanOperationInfo)) {
         return false;
      } else {
         MBeanOperationInfo var2 = (MBeanOperationInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getReturnType(), this.getReturnType()) && Objects.equals(var2.getDescription(), this.getDescription()) && var2.getImpact() == this.getImpact() && Arrays.equals((Object[])var2.fastGetSignature(), (Object[])this.fastGetSignature()) && Objects.equals(var2.getDescriptor(), this.getDescriptor());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getName(), this.getReturnType());
   }

   private static MBeanParameterInfo[] methodSignature(Method var0) {
      Class[] var1 = var0.getParameterTypes();
      Annotation[][] var2 = var0.getParameterAnnotations();
      return parameters(var1, var2);
   }

   static MBeanParameterInfo[] parameters(Class<?>[] var0, Annotation[][] var1) {
      MBeanParameterInfo[] var2 = new MBeanParameterInfo[var0.length];

      assert var0.length == var1.length;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         Descriptor var4 = Introspector.descriptorForAnnotations(var1[var3]);
         String var5 = "p" + (var3 + 1);
         var2[var3] = new MBeanParameterInfo(var5, var0[var3].getName(), "", var4);
      }

      return var2;
   }
}
