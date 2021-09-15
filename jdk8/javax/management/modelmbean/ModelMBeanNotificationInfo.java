package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanNotificationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanNotificationInfo extends MBeanNotificationInfo implements DescriptorAccess {
   private static final long oldSerialVersionUID = -5211564525059047097L;
   private static final long newSerialVersionUID = -7445681389570207141L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("notificationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("notificationDescriptor", Descriptor.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private Descriptor notificationDescriptor;
   private static final String currClass = "ModelMBeanNotificationInfo";

   public ModelMBeanNotificationInfo(String[] var1, String var2, String var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public ModelMBeanNotificationInfo(String[] var1, String var2, String var3, Descriptor var4) {
      super(var1, var2, var3);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "ModelMBeanNotificationInfo", "Entry");
      }

      this.notificationDescriptor = this.validDescriptor(var4);
   }

   public ModelMBeanNotificationInfo(ModelMBeanNotificationInfo var1) {
      this(var1.getNotifTypes(), var1.getName(), var1.getDescription(), var1.getDescriptor());
   }

   public Object clone() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "clone()", "Entry");
      }

      return new ModelMBeanNotificationInfo(this);
   }

   public Descriptor getDescriptor() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Entry");
      }

      if (this.notificationDescriptor == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Descriptor value is null, setting descriptor to default values");
         }

         this.notificationDescriptor = this.validDescriptor((Descriptor)null);
      }

      return (Descriptor)this.notificationDescriptor.clone();
   }

   public void setDescriptor(Descriptor var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
      }

      this.notificationDescriptor = this.validDescriptor(var1);
   }

   public String toString() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "toString()", "Entry");
      }

      StringBuilder var1 = new StringBuilder();
      var1.append("ModelMBeanNotificationInfo: ").append(this.getName());
      var1.append(" ; Description: ").append(this.getDescription());
      var1.append(" ; Descriptor: ").append((Object)this.getDescriptor());
      var1.append(" ; Types: ");
      String[] var2 = this.getNotifTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(var2[var3]);
      }

      return var1.toString();
   }

   private Descriptor validDescriptor(Descriptor var1) throws RuntimeOperationsException {
      boolean var3 = var1 == null;
      Object var2;
      if (var3) {
         var2 = new DescriptorSupport();
         JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
      } else {
         var2 = (Descriptor)var1.clone();
      }

      if (var3 && ((Descriptor)var2).getFieldValue("name") == null) {
         ((Descriptor)var2).setField("name", this.getName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + this.getName());
      }

      if (var3 && ((Descriptor)var2).getFieldValue("descriptorType") == null) {
         ((Descriptor)var2).setField("descriptorType", "notification");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"notification\"");
      }

      if (((Descriptor)var2).getFieldValue("displayName") == null) {
         ((Descriptor)var2).setField("displayName", this.getName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
      }

      if (((Descriptor)var2).getFieldValue("severity") == null) {
         ((Descriptor)var2).setField("severity", "6");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor severity field to 6");
      }

      if (!((Descriptor)var2).isValid()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + var2.toString());
      } else if (!this.getName().equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("name"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + ((Descriptor)var2).getFieldValue("name"));
      } else if (!"notification".equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("descriptorType"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"notification\" , was: " + ((Descriptor)var2).getFieldValue("descriptorType"));
      } else {
         return (Descriptor)var2;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("notificationDescriptor", this.notificationDescriptor);
         var2.put("currClass", "ModelMBeanNotificationInfo");
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = -5211564525059047097L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -7445681389570207141L;
      }

   }
}
