package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Objects;

public class MBeanNotificationInfo extends MBeanFeatureInfo implements Cloneable {
   static final long serialVersionUID = -3888371564530107064L;
   private static final String[] NO_TYPES = new String[0];
   static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
   private String[] types;
   private final transient boolean arrayGettersSafe;

   public MBeanNotificationInfo(String[] var1, String var2, String var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public MBeanNotificationInfo(String[] var1, String var2, String var3, Descriptor var4) {
      super(var2, var3, var4);
      this.types = var1 != null && var1.length > 0 ? (String[])var1.clone() : NO_TYPES;
      this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanNotificationInfo.class);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String[] getNotifTypes() {
      return this.types.length == 0 ? NO_TYPES : (String[])this.types.clone();
   }

   private String[] fastGetNotifTypes() {
      return this.arrayGettersSafe ? this.types : this.getNotifTypes();
   }

   public String toString() {
      return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", notifTypes=" + Arrays.asList(this.fastGetNotifTypes()) + ", descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanNotificationInfo)) {
         return false;
      } else {
         MBeanNotificationInfo var2 = (MBeanNotificationInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getDescription(), this.getDescription()) && Objects.equals(var2.getDescriptor(), this.getDescriptor()) && Arrays.equals((Object[])var2.fastGetNotifTypes(), (Object[])this.fastGetNotifTypes());
      }
   }

   public int hashCode() {
      int var1 = this.getName().hashCode();

      for(int var2 = 0; var2 < this.types.length; ++var2) {
         var1 ^= this.types[var2].hashCode();
      }

      return var1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String[] var3 = (String[])((String[])var2.get("types", (Object)null));
      this.types = var3 != null && var3.length != 0 ? (String[])var3.clone() : NO_TYPES;
   }
}
