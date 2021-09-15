package javax.management;

import java.util.Objects;

public class MBeanParameterInfo extends MBeanFeatureInfo implements Cloneable {
   static final long serialVersionUID = 7432616882776782338L;
   static final MBeanParameterInfo[] NO_PARAMS = new MBeanParameterInfo[0];
   private final String type;

   public MBeanParameterInfo(String var1, String var2, String var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public MBeanParameterInfo(String var1, String var2, String var3, Descriptor var4) {
      super(var1, var3, var4);
      this.type = var2;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String getType() {
      return this.type;
   }

   public String toString() {
      return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", type=" + this.getType() + ", descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanParameterInfo)) {
         return false;
      } else {
         MBeanParameterInfo var2 = (MBeanParameterInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getType(), this.getType()) && Objects.equals(var2.getDescription(), this.getDescription()) && Objects.equals(var2.getDescriptor(), this.getDescriptor());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getName(), this.getType());
   }
}
