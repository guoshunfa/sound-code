package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Objects;

public class MBeanFeatureInfo implements Serializable, DescriptorRead {
   static final long serialVersionUID = 3952882688968447265L;
   protected String name;
   protected String description;
   private transient Descriptor descriptor;

   public MBeanFeatureInfo(String var1, String var2) {
      this(var1, var2, (Descriptor)null);
   }

   public MBeanFeatureInfo(String var1, String var2, Descriptor var3) {
      this.name = var1;
      this.description = var2;
      this.descriptor = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Descriptor getDescriptor() {
      return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanFeatureInfo)) {
         return false;
      } else {
         MBeanFeatureInfo var2 = (MBeanFeatureInfo)var1;
         return Objects.equals(var2.getName(), this.getName()) && Objects.equals(var2.getDescription(), this.getDescription()) && Objects.equals(var2.getDescriptor(), this.getDescriptor());
      }
   }

   public int hashCode() {
      return this.getName().hashCode() ^ this.getDescription().hashCode() ^ this.getDescriptor().hashCode();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.descriptor != null && this.descriptor.getClass() == ImmutableDescriptor.class) {
         var1.write(1);
         String[] var2 = this.descriptor.getFieldNames();
         var1.writeObject(var2);
         var1.writeObject(this.descriptor.getFieldValues(var2));
      } else {
         var1.write(0);
         var1.writeObject(this.descriptor);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      switch(var1.read()) {
      case -1:
         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
         break;
      case 0:
         this.descriptor = (Descriptor)var1.readObject();
         if (this.descriptor == null) {
            this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
         }
         break;
      case 1:
         String[] var2 = (String[])((String[])var1.readObject());
         Object[] var3 = (Object[])((Object[])var1.readObject());
         this.descriptor = var2.length == 0 ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(var2, var3);
         break;
      default:
         throw new StreamCorruptedException("Got unexpected byte.");
      }

   }
}
