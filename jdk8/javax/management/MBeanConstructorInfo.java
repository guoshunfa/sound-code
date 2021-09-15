package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public class MBeanConstructorInfo extends MBeanFeatureInfo implements Cloneable {
   static final long serialVersionUID = 4433990064191844427L;
   static final MBeanConstructorInfo[] NO_CONSTRUCTORS = new MBeanConstructorInfo[0];
   private final transient boolean arrayGettersSafe;
   private final MBeanParameterInfo[] signature;

   public MBeanConstructorInfo(String var1, Constructor<?> var2) {
      this(var2.getName(), var1, constructorSignature(var2), Introspector.descriptorForElement(var2));
   }

   public MBeanConstructorInfo(String var1, String var2, MBeanParameterInfo[] var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public MBeanConstructorInfo(String var1, String var2, MBeanParameterInfo[] var3, Descriptor var4) {
      super(var1, var2, var4);
      if (var3 != null && var3.length != 0) {
         var3 = (MBeanParameterInfo[])var3.clone();
      } else {
         var3 = MBeanParameterInfo.NO_PARAMS;
      }

      this.signature = var3;
      this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanConstructorInfo.class);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public MBeanParameterInfo[] getSignature() {
      return this.signature.length == 0 ? this.signature : (MBeanParameterInfo[])this.signature.clone();
   }

   private MBeanParameterInfo[] fastGetSignature() {
      return this.arrayGettersSafe ? this.signature : this.getSignature();
   }

   public String toString() {
      return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", signature=" + Arrays.asList(this.fastGetSignature()) + ", descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanConstructorInfo)) {
         return false;
      } else {
         MBeanConstructorInfo var2 = (MBeanConstructorInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getDescription(), this.getDescription()) && Arrays.equals((Object[])var2.fastGetSignature(), (Object[])this.fastGetSignature()) && Objects.equals(var2.getDescriptor(), this.getDescriptor());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getName()) ^ Arrays.hashCode((Object[])this.fastGetSignature());
   }

   private static MBeanParameterInfo[] constructorSignature(Constructor<?> var0) {
      Class[] var1 = var0.getParameterTypes();
      Annotation[][] var2 = var0.getParameterAnnotations();
      return MBeanOperationInfo.parameters(var1, var2);
   }
}
