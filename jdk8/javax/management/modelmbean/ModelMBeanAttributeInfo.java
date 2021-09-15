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
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanAttributeInfo extends MBeanAttributeInfo implements DescriptorAccess {
   private static final long oldSerialVersionUID = 7098036920755973145L;
   private static final long newSerialVersionUID = 6181543027787327345L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("attrDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("attrDescriptor", Descriptor.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private Descriptor attrDescriptor = this.validDescriptor((Descriptor)null);
   private static final String currClass = "ModelMBeanAttributeInfo";

   public ModelMBeanAttributeInfo(String var1, String var2, Method var3, Method var4) throws IntrospectionException {
      super(var1, var2, var3, var4);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method)", (String)"Entry", (Object)var1);
      }

      this.attrDescriptor = this.validDescriptor((Descriptor)null);
   }

   public ModelMBeanAttributeInfo(String var1, String var2, Method var3, Method var4, Descriptor var5) throws IntrospectionException {
      super(var1, var2, var3, var4);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method,Descriptor)", (String)"Entry", (Object)var1);
      }

      this.attrDescriptor = this.validDescriptor(var5);
   }

   public ModelMBeanAttributeInfo(String var1, String var2, String var3, boolean var4, boolean var5, boolean var6) {
      super(var1, var2, var3, var4, var5, var6);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean)", (String)"Entry", (Object)var1);
      }

      this.attrDescriptor = this.validDescriptor((Descriptor)null);
   }

   public ModelMBeanAttributeInfo(String var1, String var2, String var3, boolean var4, boolean var5, boolean var6, Descriptor var7) {
      super(var1, var2, var3, var4, var5, var6);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean,Descriptor)", (String)"Entry", (Object)var1);
      }

      this.attrDescriptor = this.validDescriptor(var7);
   }

   public ModelMBeanAttributeInfo(ModelMBeanAttributeInfo var1) {
      super(var1.getName(), var1.getType(), var1.getDescription(), var1.isReadable(), var1.isWritable(), var1.isIs());
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(ModelMBeanAttributeInfo)", "Entry");
      }

      Descriptor var2 = var1.getDescriptor();
      this.attrDescriptor = this.validDescriptor(var2);
   }

   public Descriptor getDescriptor() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "getDescriptor()", "Entry");
      }

      if (this.attrDescriptor == null) {
         this.attrDescriptor = this.validDescriptor((Descriptor)null);
      }

      return (Descriptor)this.attrDescriptor.clone();
   }

   public void setDescriptor(Descriptor var1) {
      this.attrDescriptor = this.validDescriptor(var1);
   }

   public Object clone() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "clone()", "Entry");
      }

      return new ModelMBeanAttributeInfo(this);
   }

   public String toString() {
      return "ModelMBeanAttributeInfo: " + this.getName() + " ; Description: " + this.getDescription() + " ; Types: " + this.getType() + " ; isReadable: " + this.isReadable() + " ; isWritable: " + this.isWritable() + " ; Descriptor: " + this.getDescriptor();
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
         ((Descriptor)var2).setField("descriptorType", "attribute");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"attribute\"");
      }

      if (((Descriptor)var2).getFieldValue("displayName") == null) {
         ((Descriptor)var2).setField("displayName", this.getName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
      }

      if (!((Descriptor)var2).isValid()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + var2.toString());
      } else if (!this.getName().equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("name"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + ((Descriptor)var2).getFieldValue("name"));
      } else if (!"attribute".equalsIgnoreCase((String)((Descriptor)var2).getFieldValue("descriptorType"))) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"attribute\" , was: " + ((Descriptor)var2).getFieldValue("descriptorType"));
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
         var2.put("attrDescriptor", this.attrDescriptor);
         var2.put("currClass", "ModelMBeanAttributeInfo");
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
         serialVersionUID = 7098036920755973145L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 6181543027787327345L;
      }

   }
}
