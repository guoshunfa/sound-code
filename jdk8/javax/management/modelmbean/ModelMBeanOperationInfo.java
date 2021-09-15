package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanOperationInfo extends MBeanOperationInfo implements DescriptorAccess {
   private static final long oldSerialVersionUID = 9087646304346171239L;
   private static final long newSerialVersionUID = 6532732096650090465L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("operationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("operationDescriptor", Descriptor.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private Descriptor operationDescriptor = this.validDescriptor((Descriptor)null);
   private static final String currClass = "ModelMBeanOperationInfo";

   public ModelMBeanOperationInfo(String var1, Method var2) {
      super(var1, var2);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method)", "Entry");
      }

      this.operationDescriptor = this.validDescriptor((Descriptor)null);
   }

   public ModelMBeanOperationInfo(String var1, Method var2, Descriptor var3) {
      super(var1, var2);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method,Descriptor)", "Entry");
      }

      this.operationDescriptor = this.validDescriptor(var3);
   }

   public ModelMBeanOperationInfo(String var1, String var2, MBeanParameterInfo[] var3, String var4, int var5) {
      super(var1, var2, var3, var4, var5);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int)", "Entry");
      }

      this.operationDescriptor = this.validDescriptor((Descriptor)null);
   }

   public ModelMBeanOperationInfo(String var1, String var2, MBeanParameterInfo[] var3, String var4, int var5, Descriptor var6) {
      super(var1, var2, var3, var4, var5);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int,Descriptor)", "Entry");
      }

      this.operationDescriptor = this.validDescriptor(var6);
   }

   public ModelMBeanOperationInfo(ModelMBeanOperationInfo var1) {
      super(var1.getName(), var1.getDescription(), var1.getSignature(), var1.getReturnType(), var1.getImpact());
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(ModelMBeanOperationInfo)", "Entry");
      }

      Descriptor var2 = var1.getDescriptor();
      this.operationDescriptor = this.validDescriptor(var2);
   }

   public Object clone() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "clone()", "Entry");
      }

      return new ModelMBeanOperationInfo(this);
   }

   public Descriptor getDescriptor() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "getDescriptor()", "Entry");
      }

      if (this.operationDescriptor == null) {
         this.operationDescriptor = this.validDescriptor((Descriptor)null);
      }

      return (Descriptor)this.operationDescriptor.clone();
   }

   public void setDescriptor(Descriptor var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
      }

      this.operationDescriptor = this.validDescriptor(var1);
   }

   public String toString() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "toString()", "Entry");
      }

      String var1 = "ModelMBeanOperationInfo: " + this.getName() + " ; Description: " + this.getDescription() + " ; Descriptor: " + this.getDescriptor() + " ; ReturnType: " + this.getReturnType() + " ; Signature: ";
      MBeanParameterInfo[] var2 = this.getSignature();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1 = var1.concat(var2[var3].getType() + ", ");
      }

      return var1;
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
         ((Descriptor)var2).setField("descriptorType", "operation");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"operation\"");
      }

      if (((Descriptor)var2).getFieldValue("displayName") == null) {
         ((Descriptor)var2).setField("displayName", this.getName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
      }

      if (((Descriptor)var2).getFieldValue("role") == null) {
         ((Descriptor)var2).setField("role", "operation");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"operation\"");
      }

      if (!((Descriptor)var2).isValid()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + var2.toString());
      } else if (!this.getName().equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("name"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + ((Descriptor)var2).getFieldValue("name"));
      } else if (!"operation".equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("descriptorType"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + ((Descriptor)var2).getFieldValue("descriptorType"));
      } else {
         String var4 = (String)((Descriptor)var2).getFieldValue("role");
         if (!var4.equalsIgnoreCase("operation") && !var4.equalsIgnoreCase("setter") && !var4.equalsIgnoreCase("getter")) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"operation\", \"setter\", or \"getter\" , was: " + ((Descriptor)var2).getFieldValue("role"));
         } else {
            Object var5 = ((Descriptor)var2).getFieldValue("targetType");
            if (var5 != null && !(var5 instanceof String)) {
               throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor field \"targetValue\" is invalid class.  Expected: java.lang.String,  was: " + var5.getClass().getName());
            } else {
               return (Descriptor)var2;
            }
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("operationDescriptor", this.operationDescriptor);
         var2.put("currClass", "ModelMBeanOperationInfo");
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
         serialVersionUID = 9087646304346171239L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 6532732096650090465L;
      }

   }
}
