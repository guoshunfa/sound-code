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
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanInfoSupport extends MBeanInfo implements ModelMBeanInfo {
   private static final long oldSerialVersionUID = -3944083498453227709L;
   private static final long newSerialVersionUID = -1935722590756516193L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("mmbAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("mmbConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("mmbNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("mmbOperations", MBeanOperationInfo[].class), new ObjectStreamField("currClass", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("modelMBeanAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("modelMBeanConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("modelMBeanNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("modelMBeanOperations", MBeanOperationInfo[].class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private Descriptor modelMBeanDescriptor;
   private MBeanAttributeInfo[] modelMBeanAttributes;
   private MBeanConstructorInfo[] modelMBeanConstructors;
   private MBeanNotificationInfo[] modelMBeanNotifications;
   private MBeanOperationInfo[] modelMBeanOperations;
   private static final String ATTR = "attribute";
   private static final String OPER = "operation";
   private static final String NOTF = "notification";
   private static final String CONS = "constructor";
   private static final String MMB = "mbean";
   private static final String ALL = "all";
   private static final String currClass = "ModelMBeanInfoSupport";
   private static final ModelMBeanAttributeInfo[] NO_ATTRIBUTES;
   private static final ModelMBeanConstructorInfo[] NO_CONSTRUCTORS;
   private static final ModelMBeanNotificationInfo[] NO_NOTIFICATIONS;
   private static final ModelMBeanOperationInfo[] NO_OPERATIONS;

   public ModelMBeanInfoSupport(ModelMBeanInfo var1) {
      super(var1.getClassName(), var1.getDescription(), var1.getAttributes(), var1.getConstructors(), var1.getOperations(), var1.getNotifications());
      this.modelMBeanDescriptor = null;
      this.modelMBeanAttributes = var1.getAttributes();
      this.modelMBeanConstructors = var1.getConstructors();
      this.modelMBeanOperations = var1.getOperations();
      this.modelMBeanNotifications = var1.getNotifications();

      try {
         Descriptor var2 = var1.getMBeanDescriptor();
         this.modelMBeanDescriptor = this.validDescriptor(var2);
      } catch (MBeanException var3) {
         this.modelMBeanDescriptor = this.validDescriptor((Descriptor)null);
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Could not get a valid modelMBeanDescriptor, setting a default Descriptor");
         }
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Exit");
      }

   }

   public ModelMBeanInfoSupport(String var1, String var2, ModelMBeanAttributeInfo[] var3, ModelMBeanConstructorInfo[] var4, ModelMBeanOperationInfo[] var5, ModelMBeanNotificationInfo[] var6) {
      this(var1, var2, var3, var4, var5, var6, (Descriptor)null);
   }

   public ModelMBeanInfoSupport(String var1, String var2, ModelMBeanAttributeInfo[] var3, ModelMBeanConstructorInfo[] var4, ModelMBeanOperationInfo[] var5, ModelMBeanNotificationInfo[] var6, Descriptor var7) {
      super(var1, var2, var3 != null ? var3 : NO_ATTRIBUTES, var4 != null ? var4 : NO_CONSTRUCTORS, var5 != null ? var5 : NO_OPERATIONS, var6 != null ? var6 : NO_NOTIFICATIONS);
      this.modelMBeanDescriptor = null;
      this.modelMBeanAttributes = var3;
      this.modelMBeanConstructors = var4;
      this.modelMBeanOperations = var5;
      this.modelMBeanNotifications = var6;
      this.modelMBeanDescriptor = this.validDescriptor(var7);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfoSupport(String,String,ModelMBeanAttributeInfo[],ModelMBeanConstructorInfo[],ModelMBeanOperationInfo[],ModelMBeanNotificationInfo[],Descriptor)", "Exit");
      }

   }

   public Object clone() {
      return new ModelMBeanInfoSupport(this);
   }

   public Descriptor[] getDescriptors(String var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Entry");
      }

      if (var1 == null || var1.equals("")) {
         var1 = "all";
      }

      Descriptor[] var2;
      if (var1.equalsIgnoreCase("mbean")) {
         var2 = new Descriptor[]{this.modelMBeanDescriptor};
      } else {
         MBeanAttributeInfo[] var3;
         int var4;
         int var5;
         if (var1.equalsIgnoreCase("attribute")) {
            var3 = this.modelMBeanAttributes;
            var4 = 0;
            if (var3 != null) {
               var4 = var3.length;
            }

            var2 = new Descriptor[var4];

            for(var5 = 0; var5 < var4; ++var5) {
               var2[var5] = ((ModelMBeanAttributeInfo)var3[var5]).getDescriptor();
            }
         } else if (var1.equalsIgnoreCase("operation")) {
            MBeanOperationInfo[] var14 = this.modelMBeanOperations;
            var4 = 0;
            if (var14 != null) {
               var4 = var14.length;
            }

            var2 = new Descriptor[var4];

            for(var5 = 0; var5 < var4; ++var5) {
               var2[var5] = ((ModelMBeanOperationInfo)var14[var5]).getDescriptor();
            }
         } else if (var1.equalsIgnoreCase("constructor")) {
            MBeanConstructorInfo[] var15 = this.modelMBeanConstructors;
            var4 = 0;
            if (var15 != null) {
               var4 = var15.length;
            }

            var2 = new Descriptor[var4];

            for(var5 = 0; var5 < var4; ++var5) {
               var2[var5] = ((ModelMBeanConstructorInfo)var15[var5]).getDescriptor();
            }
         } else if (var1.equalsIgnoreCase("notification")) {
            MBeanNotificationInfo[] var16 = this.modelMBeanNotifications;
            var4 = 0;
            if (var16 != null) {
               var4 = var16.length;
            }

            var2 = new Descriptor[var4];

            for(var5 = 0; var5 < var4; ++var5) {
               var2[var5] = ((ModelMBeanNotificationInfo)var16[var5]).getDescriptor();
            }
         } else {
            if (!var1.equalsIgnoreCase("all")) {
               IllegalArgumentException var17 = new IllegalArgumentException("Descriptor Type is invalid");
               throw new RuntimeOperationsException(var17, "Exception occurred trying to find the descriptors of the MBean");
            }

            var3 = this.modelMBeanAttributes;
            var4 = 0;
            if (var3 != null) {
               var4 = var3.length;
            }

            MBeanOperationInfo[] var18 = this.modelMBeanOperations;
            int var6 = 0;
            if (var18 != null) {
               var6 = var18.length;
            }

            MBeanConstructorInfo[] var7 = this.modelMBeanConstructors;
            int var8 = 0;
            if (var7 != null) {
               var8 = var7.length;
            }

            MBeanNotificationInfo[] var9 = this.modelMBeanNotifications;
            int var10 = 0;
            if (var9 != null) {
               var10 = var9.length;
            }

            int var11 = var4 + var8 + var6 + var10 + 1;
            var2 = new Descriptor[var11];
            var2[var11 - 1] = this.modelMBeanDescriptor;
            int var12 = 0;

            int var13;
            for(var13 = 0; var13 < var4; ++var13) {
               var2[var12] = ((ModelMBeanAttributeInfo)var3[var13]).getDescriptor();
               ++var12;
            }

            for(var13 = 0; var13 < var8; ++var13) {
               var2[var12] = ((ModelMBeanConstructorInfo)var7[var13]).getDescriptor();
               ++var12;
            }

            for(var13 = 0; var13 < var6; ++var13) {
               var2[var12] = ((ModelMBeanOperationInfo)var18[var13]).getDescriptor();
               ++var12;
            }

            for(var13 = 0; var13 < var10; ++var13) {
               var2[var12] = ((ModelMBeanNotificationInfo)var9[var13]).getDescriptor();
               ++var12;
            }
         }
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Exit");
      }

      return var2;
   }

   public void setDescriptors(Descriptor[] var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor list is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
      } else if (var1.length != 0) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.setDescriptor(var1[var2], (String)null);
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Exit");
         }

      }
   }

   public Descriptor getDescriptor(String var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptor(String)", "Entry");
      }

      return this.getDescriptor(var1, (String)null);
   }

   public Descriptor getDescriptor(String var1, String var2) throws MBeanException, RuntimeOperationsException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
      } else if ("mbean".equalsIgnoreCase(var2)) {
         return (Descriptor)this.modelMBeanDescriptor.clone();
      } else {
         if ("attribute".equalsIgnoreCase(var2) || var2 == null) {
            ModelMBeanAttributeInfo var3 = this.getAttribute(var1);
            if (var3 != null) {
               return var3.getDescriptor();
            }

            if (var2 != null) {
               return null;
            }
         }

         if ("operation".equalsIgnoreCase(var2) || var2 == null) {
            ModelMBeanOperationInfo var4 = this.getOperation(var1);
            if (var4 != null) {
               return var4.getDescriptor();
            }

            if (var2 != null) {
               return null;
            }
         }

         if ("constructor".equalsIgnoreCase(var2) || var2 == null) {
            ModelMBeanConstructorInfo var5 = this.getConstructor(var1);
            if (var5 != null) {
               return var5.getDescriptor();
            }

            if (var2 != null) {
               return null;
            }
         }

         if ("notification".equalsIgnoreCase(var2) || var2 == null) {
            ModelMBeanNotificationInfo var6 = this.getNotification(var1);
            if (var6 != null) {
               return var6.getDescriptor();
            }

            if (var2 != null) {
               return null;
            }
         }

         if (var2 == null) {
            return null;
         } else {
            throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor Type is invalid"), "Exception occurred trying to find the descriptors of the MBean");
         }
      }
   }

   public void setDescriptor(Descriptor var1, String var2) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Entry");
      }

      if (var1 == null) {
         var1 = new DescriptorSupport();
      }

      if (var2 == null || var2.equals("")) {
         var2 = (String)((Descriptor)var1).getFieldValue("descriptorType");
         if (var2 == null) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptorType null in both String parameter and Descriptor, defaulting to mbean");
            var2 = "mbean";
         }
      }

      String var4 = (String)((Descriptor)var1).getFieldValue("name");
      if (var4 == null) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptor name null, defaulting to " + this.getClassName());
         var4 = this.getClassName();
      }

      boolean var5 = false;
      IllegalArgumentException var14;
      if (var2.equalsIgnoreCase("mbean")) {
         this.setMBeanDescriptor((Descriptor)var1);
         var5 = true;
      } else {
         int var7;
         int var8;
         if (var2.equalsIgnoreCase("attribute")) {
            MBeanAttributeInfo[] var6 = this.modelMBeanAttributes;
            var7 = 0;
            if (var6 != null) {
               var7 = var6.length;
            }

            for(var8 = 0; var8 < var7; ++var8) {
               if (var4.equals(var6[var8].getName())) {
                  var5 = true;
                  ModelMBeanAttributeInfo var9 = (ModelMBeanAttributeInfo)var6[var8];
                  var9.setDescriptor((Descriptor)var1);
                  if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                     StringBuilder var10 = (new StringBuilder()).append("Setting descriptor to ").append(var1).append("\t\n local: AttributeInfo descriptor is ").append((Object)var9.getDescriptor()).append("\t\n modelMBeanInfo: AttributeInfo descriptor is ").append((Object)this.getDescriptor(var4, "attribute"));
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", var10.toString());
                  }
               }
            }
         } else if (var2.equalsIgnoreCase("operation")) {
            MBeanOperationInfo[] var11 = this.modelMBeanOperations;
            var7 = 0;
            if (var11 != null) {
               var7 = var11.length;
            }

            for(var8 = 0; var8 < var7; ++var8) {
               if (var4.equals(var11[var8].getName())) {
                  var5 = true;
                  ModelMBeanOperationInfo var15 = (ModelMBeanOperationInfo)var11[var8];
                  var15.setDescriptor((Descriptor)var1);
               }
            }
         } else if (var2.equalsIgnoreCase("constructor")) {
            MBeanConstructorInfo[] var12 = this.modelMBeanConstructors;
            var7 = 0;
            if (var12 != null) {
               var7 = var12.length;
            }

            for(var8 = 0; var8 < var7; ++var8) {
               if (var4.equals(var12[var8].getName())) {
                  var5 = true;
                  ModelMBeanConstructorInfo var16 = (ModelMBeanConstructorInfo)var12[var8];
                  var16.setDescriptor((Descriptor)var1);
               }
            }
         } else {
            if (!var2.equalsIgnoreCase("notification")) {
               var14 = new IllegalArgumentException("Invalid descriptor type: " + var2);
               throw new RuntimeOperationsException(var14, "Exception occurred trying to set the descriptors of the MBean");
            }

            MBeanNotificationInfo[] var13 = this.modelMBeanNotifications;
            var7 = 0;
            if (var13 != null) {
               var7 = var13.length;
            }

            for(var8 = 0; var8 < var7; ++var8) {
               if (var4.equals(var13[var8].getName())) {
                  var5 = true;
                  ModelMBeanNotificationInfo var17 = (ModelMBeanNotificationInfo)var13[var8];
                  var17.setDescriptor((Descriptor)var1);
               }
            }
         }
      }

      if (!var5) {
         var14 = new IllegalArgumentException("Descriptor name is invalid: type=" + var2 + "; name=" + var4);
         throw new RuntimeOperationsException(var14, "Exception occurred trying to set the descriptors of the MBean");
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Exit");
         }

      }
   }

   public ModelMBeanAttributeInfo getAttribute(String var1) throws MBeanException, RuntimeOperationsException {
      ModelMBeanAttributeInfo var2 = null;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attribute Name is null"), "Exception occurred trying to get the ModelMBeanAttributeInfo of the MBean");
      } else {
         MBeanAttributeInfo[] var3 = this.modelMBeanAttributes;
         int var4 = 0;
         if (var3 != null) {
            var4 = var3.length;
         }

         for(int var5 = 0; var5 < var4 && var2 == null; ++var5) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               StringBuilder var6 = (new StringBuilder()).append("\t\n this.getAttributes() MBeanAttributeInfo Array ").append(var5).append(":").append((Object)((ModelMBeanAttributeInfo)var3[var5]).getDescriptor()).append("\t\n this.modelMBeanAttributes MBeanAttributeInfo Array ").append(var5).append(":").append((Object)((ModelMBeanAttributeInfo)this.modelMBeanAttributes[var5]).getDescriptor());
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", var6.toString());
            }

            if (var1.equals(var3[var5].getName())) {
               var2 = (ModelMBeanAttributeInfo)var3[var5].clone();
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Exit");
         }

         return var2;
      }
   }

   public ModelMBeanOperationInfo getOperation(String var1) throws MBeanException, RuntimeOperationsException {
      ModelMBeanOperationInfo var2 = null;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("inName is null"), "Exception occurred trying to get the ModelMBeanOperationInfo of the MBean");
      } else {
         MBeanOperationInfo[] var3 = this.modelMBeanOperations;
         int var4 = 0;
         if (var3 != null) {
            var4 = var3.length;
         }

         for(int var5 = 0; var5 < var4 && var2 == null; ++var5) {
            if (var1.equals(var3[var5].getName())) {
               var2 = (ModelMBeanOperationInfo)var3[var5].clone();
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Exit");
         }

         return var2;
      }
   }

   public ModelMBeanConstructorInfo getConstructor(String var1) throws MBeanException, RuntimeOperationsException {
      ModelMBeanConstructorInfo var2 = null;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Constructor name is null"), "Exception occurred trying to get the ModelMBeanConstructorInfo of the MBean");
      } else {
         MBeanConstructorInfo[] var3 = this.modelMBeanConstructors;
         int var4 = 0;
         if (var3 != null) {
            var4 = var3.length;
         }

         for(int var5 = 0; var5 < var4 && var2 == null; ++var5) {
            if (var1.equals(var3[var5].getName())) {
               var2 = (ModelMBeanConstructorInfo)var3[var5].clone();
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Exit");
         }

         return var2;
      }
   }

   public ModelMBeanNotificationInfo getNotification(String var1) throws MBeanException, RuntimeOperationsException {
      ModelMBeanNotificationInfo var2 = null;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Notification name is null"), "Exception occurred trying to get the ModelMBeanNotificationInfo of the MBean");
      } else {
         MBeanNotificationInfo[] var3 = this.modelMBeanNotifications;
         int var4 = 0;
         if (var3 != null) {
            var4 = var3.length;
         }

         for(int var5 = 0; var5 < var4 && var2 == null; ++var5) {
            if (var1.equals(var3[var5].getName())) {
               var2 = (ModelMBeanNotificationInfo)var3[var5].clone();
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Exit");
         }

         return var2;
      }
   }

   public Descriptor getDescriptor() {
      return this.getMBeanDescriptorNoException();
   }

   public Descriptor getMBeanDescriptor() throws MBeanException {
      return this.getMBeanDescriptorNoException();
   }

   private Descriptor getMBeanDescriptorNoException() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Entry");
      }

      if (this.modelMBeanDescriptor == null) {
         this.modelMBeanDescriptor = this.validDescriptor((Descriptor)null);
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Exit, returning: " + this.modelMBeanDescriptor);
      }

      return (Descriptor)this.modelMBeanDescriptor.clone();
   }

   public void setMBeanDescriptor(Descriptor var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setMBeanDescriptor(Descriptor)", "Entry");
      }

      this.modelMBeanDescriptor = this.validDescriptor(var1);
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
         ((Descriptor)var2).setField("name", this.getClassName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + this.getClassName());
      }

      if (var3 && ((Descriptor)var2).getFieldValue("descriptorType") == null) {
         ((Descriptor)var2).setField("descriptorType", "mbean");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"mbean\"");
      }

      if (((Descriptor)var2).getFieldValue("displayName") == null) {
         ((Descriptor)var2).setField("displayName", this.getClassName());
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getClassName());
      }

      if (((Descriptor)var2).getFieldValue("persistPolicy") == null) {
         ((Descriptor)var2).setField("persistPolicy", "never");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor persistPolicy to \"never\"");
      }

      if (((Descriptor)var2).getFieldValue("log") == null) {
         ((Descriptor)var2).setField("log", "F");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor \"log\" field to \"F\"");
      }

      if (((Descriptor)var2).getFieldValue("visibility") == null) {
         ((Descriptor)var2).setField("visibility", "1");
         JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor visibility to 1");
      }

      if (!((Descriptor)var2).isValid()) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + var2.toString());
      } else if (!((String)((Descriptor)var2).getFieldValue("descriptorType")).equalsIgnoreCase("mbean")) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: mbean , was: " + ((Descriptor)var2).getFieldValue("descriptorType"));
      } else {
         return (Descriptor)var2;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.modelMBeanDescriptor = (Descriptor)var2.get("modelMBeanDescriptor", (Object)null);
         if (var2.defaulted("modelMBeanDescriptor")) {
            throw new NullPointerException("modelMBeanDescriptor");
         }

         this.modelMBeanAttributes = (MBeanAttributeInfo[])((MBeanAttributeInfo[])var2.get("mmbAttributes", (Object)null));
         if (var2.defaulted("mmbAttributes")) {
            throw new NullPointerException("mmbAttributes");
         }

         this.modelMBeanConstructors = (MBeanConstructorInfo[])((MBeanConstructorInfo[])var2.get("mmbConstructors", (Object)null));
         if (var2.defaulted("mmbConstructors")) {
            throw new NullPointerException("mmbConstructors");
         }

         this.modelMBeanNotifications = (MBeanNotificationInfo[])((MBeanNotificationInfo[])var2.get("mmbNotifications", (Object)null));
         if (var2.defaulted("mmbNotifications")) {
            throw new NullPointerException("mmbNotifications");
         }

         this.modelMBeanOperations = (MBeanOperationInfo[])((MBeanOperationInfo[])var2.get("mmbOperations", (Object)null));
         if (var2.defaulted("mmbOperations")) {
            throw new NullPointerException("mmbOperations");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("modelMBeanDescriptor", this.modelMBeanDescriptor);
         var2.put("mmbAttributes", this.modelMBeanAttributes);
         var2.put("mmbConstructors", this.modelMBeanConstructors);
         var2.put("mmbNotifications", this.modelMBeanNotifications);
         var2.put("mmbOperations", this.modelMBeanOperations);
         var2.put("currClass", "ModelMBeanInfoSupport");
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
         serialVersionUID = -3944083498453227709L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -1935722590756516193L;
      }

      NO_ATTRIBUTES = new ModelMBeanAttributeInfo[0];
      NO_CONSTRUCTORS = new ModelMBeanConstructorInfo[0];
      NO_NOTIFICATIONS = new ModelMBeanNotificationInfo[0];
      NO_OPERATIONS = new ModelMBeanOperationInfo[0];
   }
}
