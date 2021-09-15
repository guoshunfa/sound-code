package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import javax.management.loading.ClassLoaderRepository;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class RequiredModelMBean implements ModelMBean, MBeanRegistration, NotificationEmitter {
   ModelMBeanInfo modelMBeanInfo;
   private NotificationBroadcasterSupport generalBroadcaster = null;
   private NotificationBroadcasterSupport attributeBroadcaster = null;
   private Object managedResource = null;
   private boolean registered = false;
   private transient MBeanServer server = null;
   private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
   private final AccessControlContext acc = AccessController.getContext();
   private static final Class<?>[] primitiveClasses;
   private static final Map<String, Class<?>> primitiveClassMap;
   private static Set<String> rmmbMethodNames;
   private static final String[] primitiveTypes;
   private static final String[] primitiveWrappers;

   public RequiredModelMBean() throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Entry");
      }

      this.modelMBeanInfo = this.createDefaultModelMBeanInfo();
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Exit");
      }

   }

   public RequiredModelMBean(ModelMBeanInfo var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Entry");
      }

      this.setModelMBeanInfo(var1);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Exit");
      }

   }

   public void setModelMBeanInfo(ModelMBeanInfo var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Entry");
      }

      if (var1 == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo is null: Raising exception.");
         }

         IllegalArgumentException var4 = new IllegalArgumentException("ModelMBeanInfo must not be null");
         throw new RuntimeOperationsException(var4, "Exception occurred trying to initialize the ModelMBeanInfo of the RequiredModelMBean");
      } else if (this.registered) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "RequiredMBean is registered: Raising exception.");
         }

         IllegalStateException var3 = new IllegalStateException("cannot call setModelMBeanInfo while ModelMBean is registered");
         throw new RuntimeOperationsException(var3, "Exception occurred trying to set the ModelMBeanInfo of the RequiredModelMBean");
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Setting ModelMBeanInfo to " + this.printModelMBeanInfo(var1));
            int var2 = 0;
            if (var1.getNotifications() != null) {
               var2 = var1.getNotifications().length;
            }

            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo notifications has " + var2 + " elements");
         }

         this.modelMBeanInfo = (ModelMBeanInfo)var1.clone();
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "set mbeanInfo to: " + this.printModelMBeanInfo(this.modelMBeanInfo));
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Exit");
         }

      }
   }

   public void setManagedResource(Object var1, String var2) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Entry");
      }

      if (var2 != null && var2.equalsIgnoreCase("objectReference")) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource is valid");
         }

         this.managedResource = var1;
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object, String)", "Exit");
         }

      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource Type is not supported: " + var2);
         }

         throw new InvalidTargetObjectTypeException(var2);
      }
   }

   public void load() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
      ServiceNotFoundException var1 = new ServiceNotFoundException("Persistence not supported for this MBean");
      throw new MBeanException(var1, var1.getMessage());
   }

   public void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
      ServiceNotFoundException var1 = new ServiceNotFoundException("Persistence not supported for this MBean");
      throw new MBeanException(var1, var1.getMessage());
   }

   private Object resolveForCacheValue(Descriptor var1) throws MBeanException, RuntimeOperationsException {
      boolean var2 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
      if (var2) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Entry");
      }

      Object var4 = null;
      boolean var5 = false;
      boolean var6 = true;
      long var7 = 0L;
      if (var1 == null) {
         if (var2) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Input Descriptor is null");
         }

         return var4;
      } else {
         if (var2) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "descriptor is " + var1);
         }

         Descriptor var9 = this.modelMBeanInfo.getMBeanDescriptor();
         if (var9 == null && var2) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "MBean Descriptor is null");
         }

         Object var10 = var1.getFieldValue("currencyTimeLimit");
         String var11;
         if (var10 != null) {
            var11 = var10.toString();
         } else {
            var11 = null;
         }

         if (var11 == null && var9 != null) {
            var10 = var9.getFieldValue("currencyTimeLimit");
            if (var10 != null) {
               var11 = var10.toString();
            } else {
               var11 = null;
            }
         }

         if (var11 != null) {
            if (var2) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyTimeLimit: " + var11);
            }

            var7 = new Long(var11) * 1000L;
            Object var12;
            if (var7 < 0L) {
               var6 = false;
               var5 = true;
               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", var7 + ": never Cached");
               }
            } else if (var7 == 0L) {
               var6 = true;
               var5 = false;
               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "always valid Cache");
               }
            } else {
               var12 = var1.getFieldValue("lastUpdatedTimeStamp");
               String var13;
               if (var12 != null) {
                  var13 = var12.toString();
               } else {
                  var13 = null;
               }

               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "lastUpdatedTimeStamp: " + var13);
               }

               if (var13 == null) {
                  var13 = "0";
               }

               long var14 = new Long(var13);
               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyPeriod:" + var7 + " lastUpdatedTimeStamp:" + var14);
               }

               long var16 = (new Date()).getTime();
               if (var16 < var14 + var7) {
                  var6 = true;
                  var5 = false;
                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", " timed valid Cache for " + var16 + " < " + (var14 + var7));
                  }
               } else {
                  var6 = false;
                  var5 = true;
                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "timed expired cache for " + var16 + " > " + (var14 + var7));
                  }
               }
            }

            if (var2) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "returnCachedValue:" + var6 + " resetValue: " + var5);
            }

            if (var6) {
               var12 = var1.getFieldValue("value");
               if (var12 != null) {
                  var4 = var12;
                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "valid Cache value: " + var12);
                  }
               } else {
                  var4 = null;
                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "no Cached value");
                  }
               }
            }

            if (var5) {
               var1.removeField("lastUpdatedTimeStamp");
               var1.removeField("value");
               var4 = null;
               this.modelMBeanInfo.setDescriptor(var1, (String)null);
               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "reset cached value to null");
               }
            }
         }

         if (var2) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Exit");
         }

         return var4;
      }
   }

   public MBeanInfo getMBeanInfo() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "Entry");
      }

      if (this.modelMBeanInfo == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "modelMBeanInfo is null");
         }

         this.modelMBeanInfo = this.createDefaultModelMBeanInfo();
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "ModelMBeanInfo is " + this.modelMBeanInfo.getClassName() + " for " + this.modelMBeanInfo.getDescription());
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", this.printModelMBeanInfo(this.modelMBeanInfo));
      }

      return (MBeanInfo)this.modelMBeanInfo.clone();
   }

   private String printModelMBeanInfo(ModelMBeanInfo var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1 == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "printModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo to print is null, printing local ModelMBeanInfo");
         }

         var1 = this.modelMBeanInfo;
      }

      var2.append("\nMBeanInfo for ModelMBean is:");
      var2.append("\nCLASSNAME: \t" + var1.getClassName());
      var2.append("\nDESCRIPTION: \t" + var1.getDescription());

      try {
         var2.append("\nMBEAN DESCRIPTOR: \t" + var1.getMBeanDescriptor());
      } catch (Exception var9) {
         var2.append("\nMBEAN DESCRIPTOR: \t is invalid");
      }

      var2.append("\nATTRIBUTES");
      MBeanAttributeInfo[] var3 = var1.getAttributes();
      if (var3 != null && var3.length > 0) {
         for(int var4 = 0; var4 < var3.length; ++var4) {
            ModelMBeanAttributeInfo var5 = (ModelMBeanAttributeInfo)var3[var4];
            var2.append(" ** NAME: \t" + var5.getName());
            var2.append("    DESCR: \t" + var5.getDescription());
            var2.append("    TYPE: \t" + var5.getType() + "    READ: \t" + var5.isReadable() + "    WRITE: \t" + var5.isWritable());
            var2.append("    DESCRIPTOR: " + var5.getDescriptor().toString());
         }
      } else {
         var2.append(" ** No attributes **");
      }

      var2.append("\nCONSTRUCTORS");
      MBeanConstructorInfo[] var10 = var1.getConstructors();
      if (var10 != null && var10.length > 0) {
         for(int var11 = 0; var11 < var10.length; ++var11) {
            ModelMBeanConstructorInfo var6 = (ModelMBeanConstructorInfo)var10[var11];
            var2.append(" ** NAME: \t" + var6.getName());
            var2.append("    DESCR: \t" + var6.getDescription());
            var2.append("    PARAM: \t" + var6.getSignature().length + " parameter(s)");
            var2.append("    DESCRIPTOR: " + var6.getDescriptor().toString());
         }
      } else {
         var2.append(" ** No Constructors **");
      }

      var2.append("\nOPERATIONS");
      MBeanOperationInfo[] var12 = var1.getOperations();
      if (var12 != null && var12.length > 0) {
         for(int var13 = 0; var13 < var12.length; ++var13) {
            ModelMBeanOperationInfo var7 = (ModelMBeanOperationInfo)var12[var13];
            var2.append(" ** NAME: \t" + var7.getName());
            var2.append("    DESCR: \t" + var7.getDescription());
            var2.append("    PARAM: \t" + var7.getSignature().length + " parameter(s)");
            var2.append("    DESCRIPTOR: " + var7.getDescriptor().toString());
         }
      } else {
         var2.append(" ** No operations ** ");
      }

      var2.append("\nNOTIFICATIONS");
      MBeanNotificationInfo[] var14 = var1.getNotifications();
      if (var14 != null && var14.length > 0) {
         for(int var15 = 0; var15 < var14.length; ++var15) {
            ModelMBeanNotificationInfo var8 = (ModelMBeanNotificationInfo)var14[var15];
            var2.append(" ** NAME: \t" + var8.getName());
            var2.append("    DESCR: \t" + var8.getDescription());
            var2.append("    DESCRIPTOR: " + var8.getDescriptor().toString());
         }
      } else {
         var2.append(" ** No notifications **");
      }

      var2.append(" ** ModelMBean: End of MBeanInfo ** ");
      return var2.toString();
   }

   public Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException {
      boolean var4 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
      if (var4) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Entry");
      }

      if (var1 == null) {
         IllegalArgumentException var22 = new IllegalArgumentException("Method name must not be null");
         throw new RuntimeOperationsException(var22, "An exception occurred while trying to invoke a method on a RequiredModelMBean");
      } else {
         final String var6 = null;
         int var8 = var1.lastIndexOf(".");
         String var7;
         if (var8 > 0) {
            var6 = var1.substring(0, var8);
            var7 = var1.substring(var8 + 1);
         } else {
            var7 = var1;
         }

         var8 = var7.indexOf("(");
         if (var8 > 0) {
            var7 = var7.substring(0, var8);
         }

         if (var4) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Finding operation " + var1 + " as " + var7);
         }

         ModelMBeanOperationInfo var9 = this.modelMBeanInfo.getOperation(var7);
         if (var9 == null) {
            String var23 = "Operation " + var1 + " not in ModelMBeanInfo";
            throw new MBeanException(new ServiceNotFoundException(var23), var23);
         } else {
            Descriptor var10 = var9.getDescriptor();
            if (var10 == null) {
               throw new MBeanException(new ServiceNotFoundException("Operation descriptor null"), "Operation descriptor null");
            } else {
               Object var11 = this.resolveForCacheValue(var10);
               if (var11 != null) {
                  if (var4) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Returning cached value");
                  }

                  return var11;
               } else {
                  if (var6 == null) {
                     var6 = (String)var10.getFieldValue("class");
                  }

                  var7 = (String)var10.getFieldValue("name");
                  if (var7 == null) {
                     throw new MBeanException(new ServiceNotFoundException("Method descriptor must include `name' field"), "Method descriptor must include `name' field");
                  } else {
                     String var12 = (String)var10.getFieldValue("targetType");
                     if (var12 != null && !var12.equalsIgnoreCase("objectReference")) {
                        String var24 = "Target type must be objectReference: " + var12;
                        throw new MBeanException(new InvalidTargetObjectTypeException(var24), var24);
                     } else {
                        Object var13 = var10.getFieldValue("targetObject");
                        if (var4 && var13 != null) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Found target object in descriptor");
                        }

                        Method var14 = this.findRMMBMethod(var7, var13, var6, var3);
                        final Object var15;
                        if (var14 != null) {
                           var15 = this;
                        } else {
                           if (var4) {
                              JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in managedResource class");
                           }

                           if (var13 != null) {
                              var15 = var13;
                           } else {
                              var15 = this.managedResource;
                              if (var15 == null) {
                                 String var26 = "managedResource for invoke " + var1 + " is null";
                                 ServiceNotFoundException var27 = new ServiceNotFoundException(var26);
                                 throw new MBeanException(var27);
                              }
                           }

                           Class var16;
                           if (var6 != null) {
                              try {
                                 AccessControlContext var17 = AccessController.getContext();
                                 final ClassNotFoundException[] var20 = new ClassNotFoundException[1];
                                 var16 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
                                    public Class<?> run() {
                                       try {
                                          ReflectUtil.checkPackageAccess(var6);
                                          ClassLoader var1 = var15.getClass().getClassLoader();
                                          return Class.forName(var6, false, var1);
                                       } catch (ClassNotFoundException var2) {
                                          var20[0] = var2;
                                          return null;
                                       }
                                    }
                                 }, var17, this.acc);
                                 if (var20[0] != null) {
                                    throw var20[0];
                                 }
                              } catch (ClassNotFoundException var21) {
                                 String var18 = "class for invoke " + var1 + " not found";
                                 throw new ReflectionException(var21, var18);
                              }
                           } else {
                              var16 = var15.getClass();
                           }

                           var14 = this.resolveMethod(var16, var7, var3);
                        }

                        if (var4) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "found " + var7 + ", now invoking");
                        }

                        Object var25 = this.invokeMethod(var1, var14, var15, var2);
                        if (var4) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "successfully invoked method");
                        }

                        if (var25 != null) {
                           this.cacheResult(var9, var10, var25);
                        }

                        return var25;
                     }
                  }
               }
            }
         }
      }
   }

   private Method resolveMethod(Class<?> var1, String var2, final String[] var3) throws ReflectionException {
      final boolean var4 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
      if (var4) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolving " + var1.getName() + "." + var2);
      }

      final Class[] var5;
      if (var3 == null) {
         var5 = null;
      } else {
         AccessControlContext var6 = AccessController.getContext();
         final ReflectionException[] var7 = new ReflectionException[1];
         final ClassLoader var8 = var1.getClassLoader();
         var5 = new Class[var3.length];
         javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
               for(int var1 = 0; var1 < var3.length; ++var1) {
                  if (var4) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolve type " + var3[var1]);
                  }

                  var5[var1] = (Class)RequiredModelMBean.primitiveClassMap.get(var3[var1]);
                  if (var5[var1] == null) {
                     try {
                        ReflectUtil.checkPackageAccess(var3[var1]);
                        var5[var1] = Class.forName(var3[var1], false, var8);
                     } catch (ClassNotFoundException var4x) {
                        if (var4) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "class not found");
                        }

                        var7[0] = new ReflectionException(var4x, "Parameter class not found");
                     }
                  }
               }

               return null;
            }
         }, var6, this.acc);
         if (var7[0] != null) {
            throw var7[0];
         }
      }

      try {
         return var1.getMethod(var2, var5);
      } catch (NoSuchMethodException var9) {
         String var10 = "Target method not found: " + var1.getName() + "." + var2;
         throw new ReflectionException(var9, var10);
      }
   }

   private Method findRMMBMethod(String var1, Object var2, final String var3, String[] var4) {
      boolean var5 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
      if (var5) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in RequiredModelMBean class");
      }

      if (!isRMMBMethodName(var1)) {
         return null;
      } else if (var2 != null) {
         return null;
      } else {
         final Class var6 = RequiredModelMBean.class;
         Class var7;
         if (var3 == null) {
            var7 = var6;
         } else {
            AccessControlContext var8 = AccessController.getContext();
            var7 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
               public Class<?> run() {
                  try {
                     ReflectUtil.checkPackageAccess(var3);
                     ClassLoader var1 = var6.getClassLoader();
                     Class var2 = Class.forName(var3, false, var1);
                     return !var6.isAssignableFrom(var2) ? null : var2;
                  } catch (ClassNotFoundException var3x) {
                     return null;
                  }
               }
            }, var8, this.acc);
         }

         try {
            return var7 != null ? this.resolveMethod(var7, var1, var4) : null;
         } catch (ReflectionException var10) {
            return null;
         }
      }
   }

   private Object invokeMethod(String var1, final Method var2, final Object var3, final Object[] var4) throws MBeanException, ReflectionException {
      try {
         final Throwable[] var5 = new Throwable[1];
         AccessControlContext var14 = AccessController.getContext();
         Object var7 = javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Object>() {
            public Object run() {
               try {
                  ReflectUtil.checkPackageAccess(var2.getDeclaringClass());
                  return MethodUtil.invoke(var2, var3, var4);
               } catch (InvocationTargetException var2x) {
                  var5[0] = var2x;
               } catch (IllegalAccessException var3x) {
                  var5[0] = var3x;
               }

               return null;
            }
         }, var14, this.acc);
         if (var5[0] != null) {
            if (var5[0] instanceof Exception) {
               throw (Exception)var5[0];
            }

            if (var5[0] instanceof Error) {
               throw (Error)var5[0];
            }
         }

         return var7;
      } catch (RuntimeErrorException var8) {
         throw new RuntimeOperationsException(var8, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + var1);
      } catch (RuntimeException var9) {
         throw new RuntimeOperationsException(var9, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + var1);
      } catch (IllegalAccessException var10) {
         throw new ReflectionException(var10, "IllegalAccessException occurred in RequiredModelMBean while trying to invoke operation " + var1);
      } catch (InvocationTargetException var11) {
         Throwable var6 = var11.getTargetException();
         if (var6 instanceof RuntimeException) {
            throw new MBeanException((RuntimeException)var6, "RuntimeException thrown in RequiredModelMBean while trying to invoke operation " + var1);
         } else if (var6 instanceof Error) {
            throw new RuntimeErrorException((Error)var6, "Error occurred in RequiredModelMBean while trying to invoke operation " + var1);
         } else if (var6 instanceof ReflectionException) {
            throw (ReflectionException)var6;
         } else {
            throw new MBeanException((Exception)var6, "Exception thrown in RequiredModelMBean while trying to invoke operation " + var1);
         }
      } catch (Error var12) {
         throw new RuntimeErrorException(var12, "Error occurred in RequiredModelMBean while trying to invoke operation " + var1);
      } catch (Exception var13) {
         throw new ReflectionException(var13, "Exception occurred in RequiredModelMBean while trying to invoke operation " + var1);
      }
   }

   private void cacheResult(ModelMBeanOperationInfo var1, Descriptor var2, Object var3) throws MBeanException {
      Descriptor var4 = this.modelMBeanInfo.getMBeanDescriptor();
      Object var5 = var2.getFieldValue("currencyTimeLimit");
      String var6;
      if (var5 != null) {
         var6 = var5.toString();
      } else {
         var6 = null;
      }

      if (var6 == null && var4 != null) {
         var5 = var4.getFieldValue("currencyTimeLimit");
         if (var5 != null) {
            var6 = var5.toString();
         } else {
            var6 = null;
         }
      }

      if (var6 != null && !var6.equals("-1")) {
         var2.setField("value", var3);
         var2.setField("lastUpdatedTimeStamp", String.valueOf((new Date()).getTime()));
         this.modelMBeanInfo.setDescriptor(var2, "operation");
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String,Object[],Object[])", "new descriptor is " + var2);
         }
      }

   }

   private static synchronized boolean isRMMBMethodName(String var0) {
      if (rmmbMethodNames == null) {
         try {
            HashSet var1 = new HashSet();
            Method[] var2 = RequiredModelMBean.class.getMethods();

            for(int var3 = 0; var3 < var2.length; ++var3) {
               var1.add(var2[var3].getName());
            }

            rmmbMethodNames = var1;
         } catch (Exception var4) {
            return true;
         }
      }

      return rmmbMethodNames.contains(var0);
   }

   public Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a RequiredModelMBean");
      } else {
         boolean var3 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
         if (var3) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Entry with " + var1);
         }

         Object var4;
         try {
            if (this.modelMBeanInfo == null) {
               throw new AttributeNotFoundException("getAttribute failed: ModelMBeanInfo not found for " + var1);
            }

            ModelMBeanAttributeInfo var5 = this.modelMBeanInfo.getAttribute(var1);
            Descriptor var6 = this.modelMBeanInfo.getMBeanDescriptor();
            if (var5 == null) {
               throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + var1);
            }

            Descriptor var7 = var5.getDescriptor();
            if (var7 == null) {
               if (var3) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed " + var1 + " not in attributeDescriptor\n");
               }

               throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
            }

            if (!var5.isReadable()) {
               throw new AttributeNotFoundException("getAttribute failed: " + var1 + " is not readable ");
            }

            var4 = this.resolveForCacheValue(var7);
            if (var3) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "*** cached value is " + var4);
            }

            final String var8;
            String var22;
            if (var4 == null) {
               if (var3) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "**** cached value is null - getting getMethod");
               }

               var8 = (String)((String)var7.getFieldValue("getMethod"));
               if (var8 != null) {
                  if (var3) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "invoking a getMethod for " + var1);
                  }

                  Object var9 = this.invoke(var8, new Object[0], new String[0]);
                  if (var9 != null) {
                     if (var3) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a non-null response from getMethod\n");
                     }

                     var4 = var9;
                     Object var10 = var7.getFieldValue("currencyTimeLimit");
                     String var11;
                     if (var10 != null) {
                        var11 = var10.toString();
                     } else {
                        var11 = null;
                     }

                     if (var11 == null && var6 != null) {
                        var10 = var6.getFieldValue("currencyTimeLimit");
                        if (var10 != null) {
                           var11 = var10.toString();
                        } else {
                           var11 = null;
                        }
                     }

                     if (var11 != null && !var11.equals("-1")) {
                        if (var3) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor");
                        }

                        var7.setField("value", var9);
                        String var12 = String.valueOf((new Date()).getTime());
                        var7.setField("lastUpdatedTimeStamp", var12);
                        var5.setDescriptor(var7);
                        this.modelMBeanInfo.setDescriptor(var7, "attribute");
                        if (var3) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "new descriptor is " + var7);
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "AttributeInfo descriptor is " + var5.getDescriptor());
                           String var13 = this.modelMBeanInfo.getDescriptor(var1, "attribute").toString();
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "modelMBeanInfo: AttributeInfo descriptor is " + var13);
                        }
                     }
                  } else {
                     if (var3) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a null response from getMethod\n");
                     }

                     var4 = null;
                  }
               } else {
                  var22 = "";
                  var4 = var7.getFieldValue("value");
                  if (var4 == null) {
                     var22 = "default ";
                     var4 = var7.getFieldValue("default");
                  }

                  if (var3) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "could not find getMethod for " + var1 + ", returning descriptor " + var22 + "value");
                  }
               }
            }

            var8 = var5.getType();
            if (var4 != null) {
               var22 = var4.getClass().getName();
               if (!var8.equals(var22)) {
                  boolean var23 = false;
                  boolean var25 = false;
                  boolean var24 = false;

                  for(int var26 = 0; var26 < primitiveTypes.length; ++var26) {
                     if (var8.equals(primitiveTypes[var26])) {
                        var25 = true;
                        if (var22.equals(primitiveWrappers[var26])) {
                           var24 = true;
                        }
                        break;
                     }
                  }

                  if (var25) {
                     if (!var24) {
                        var23 = true;
                     }
                  } else {
                     boolean var27;
                     try {
                        final Class var14 = var4.getClass();
                        final Exception[] var15 = new Exception[1];
                        AccessControlContext var16 = AccessController.getContext();
                        Class var17 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
                           public Class<?> run() {
                              try {
                                 ReflectUtil.checkPackageAccess(var8);
                                 ClassLoader var1 = var14.getClassLoader();
                                 return Class.forName(var8, true, var1);
                              } catch (Exception var2) {
                                 var15[0] = var2;
                                 return null;
                              }
                           }
                        }, var16, this.acc);
                        if (var15[0] != null) {
                           throw var15[0];
                        }

                        var27 = var17.isInstance(var4);
                     } catch (Exception var18) {
                        var27 = false;
                        if (var3) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", (String)"Exception: ", (Throwable)var18);
                        }
                     }

                     if (!var27) {
                        var23 = true;
                     }
                  }

                  if (var23) {
                     if (var3) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Wrong response type '" + var8 + "'");
                     }

                     throw new MBeanException(new InvalidAttributeValueException("Wrong value type received for get attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
                  }
               }
            }
         } catch (MBeanException var19) {
            throw var19;
         } catch (AttributeNotFoundException var20) {
            throw var20;
         } catch (Exception var21) {
            if (var3) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed with " + var21.getMessage() + " exception type " + var21.getClass().toString());
            }

            throw new MBeanException(var21, "An exception occurred while trying to get an attribute value: " + var21.getMessage());
         }

         if (var3) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exit");
         }

         return var4;
      }
   }

   public AttributeList getAttributes(String[] var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a RequiredModelMBean");
      } else {
         AttributeList var2 = new AttributeList();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               var2.add(new Attribute(var1[var3], this.getAttribute(var1[var3])));
            } catch (Exception var5) {
               if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", (String)("Failed to get \"" + var1[var3] + "\": "), (Throwable)var5);
               }
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Exit");
         }

         return var2;
      }
   }

   public void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      boolean var2 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
      if (var2) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute()", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a RequiredModelMBean");
      } else {
         String var3 = var1.getName();
         Object var4 = var1.getValue();
         boolean var5 = false;
         ModelMBeanAttributeInfo var6 = this.modelMBeanInfo.getAttribute(var3);
         if (var6 == null) {
            throw new AttributeNotFoundException("setAttribute failed: " + var3 + " is not found ");
         } else {
            Descriptor var7 = this.modelMBeanInfo.getMBeanDescriptor();
            Descriptor var8 = var6.getDescriptor();
            if (var8 == null) {
               if (var2) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setMethod failed " + var3 + " not in attributeDescriptor\n");
               }

               throw new InvalidAttributeValueException("Unable to resolve attribute value, no defined in descriptor for attribute");
            } else if (!var6.isWritable()) {
               throw new AttributeNotFoundException("setAttribute failed: " + var3 + " is not writable ");
            } else {
               String var9 = (String)((String)var8.getFieldValue("setMethod"));
               String var10 = (String)((String)var8.getFieldValue("getMethod"));
               String var11 = var6.getType();
               Object var12 = "Unknown";

               try {
                  var12 = this.getAttribute(var3);
               } catch (Throwable var18) {
               }

               Attribute var13 = new Attribute(var3, var12);
               if (var9 == null) {
                  if (var4 != null) {
                     try {
                        Class var14 = this.loadClass(var11);
                        if (!var14.isInstance(var4)) {
                           throw new InvalidAttributeValueException(var14.getName() + " expected, " + var4.getClass().getName() + " received.");
                        }
                     } catch (ClassNotFoundException var19) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", (String)("Class " + var11 + " for attribute " + var3 + " not found: "), (Throwable)var19);
                        }
                     }
                  }

                  var5 = true;
               } else {
                  this.invoke(var9, new Object[]{var4}, new String[]{var11});
               }

               Object var20 = var8.getFieldValue("currencyTimeLimit");
               String var15;
               if (var20 != null) {
                  var15 = var20.toString();
               } else {
                  var15 = null;
               }

               if (var15 == null && var7 != null) {
                  var20 = var7.getFieldValue("currencyTimeLimit");
                  if (var20 != null) {
                     var15 = var20.toString();
                  } else {
                     var15 = null;
                  }
               }

               boolean var16 = var15 != null && !var15.equals("-1");
               if (var9 == null && !var16 && var10 != null) {
                  throw new MBeanException(new ServiceNotFoundException("No setMethod field is defined in the descriptor for " + var3 + " attribute and caching is not enabled for it"));
               } else {
                  if (var16 || var5) {
                     if (var2) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setting cached value of " + var3 + " to " + var4);
                     }

                     var8.setField("value", var4);
                     if (var16) {
                        String var17 = String.valueOf((new Date()).getTime());
                        var8.setField("lastUpdatedTimeStamp", var17);
                     }

                     var6.setDescriptor(var8);
                     this.modelMBeanInfo.setDescriptor(var8, "attribute");
                     if (var2) {
                        StringBuilder var21 = (new StringBuilder()).append("new descriptor is ").append((Object)var8).append(". AttributeInfo descriptor is ").append((Object)var6.getDescriptor()).append(". AttributeInfo descriptor is ").append((Object)this.modelMBeanInfo.getDescriptor(var3, "attribute"));
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", var21.toString());
                     }
                  }

                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "sending sendAttributeNotification");
                  }

                  this.sendAttributeChangeNotification(var13, var1);
                  if (var2) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Exit");
                  }

               }
            }
         }
      }
   }

   public AttributeList setAttributes(AttributeList var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a RequiredModelMBean");
      } else {
         AttributeList var2 = new AttributeList();
         Iterator var3 = var1.asList().iterator();

         while(var3.hasNext()) {
            Attribute var4 = (Attribute)var3.next();

            try {
               this.setAttribute(var4);
               var2.add(var4);
            } catch (Exception var6) {
               var2.remove(var4);
            }
         }

         return var2;
      }
   }

   private ModelMBeanInfo createDefaultModelMBeanInfo() {
      return new ModelMBeanInfoSupport(this.getClass().getName(), "Default ModelMBean", (ModelMBeanAttributeInfo[])null, (ModelMBeanConstructorInfo[])null, (ModelMBeanOperationInfo[])null, (ModelMBeanNotificationInfo[])null);
   }

   private synchronized void writeToLog(String var1, String var2) throws Exception {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Notification Logging to " + var1 + ": " + var2);
      }

      if (var1 != null && var2 != null) {
         FileOutputStream var3 = new FileOutputStream(var1, true);

         try {
            PrintStream var4 = new PrintStream(var3);
            var4.println(var2);
            var4.close();
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Successfully opened log " + var1);
            }
         } catch (Exception var8) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Exception " + var8.toString() + " trying to write to the Notification log file " + var1);
            }

            throw var8;
         } finally {
            var3.close();
         }

      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Bad input parameters, will not log this entry.");
         }

      }
   }

   public void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws IllegalArgumentException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
      }

      if (var1 == null) {
         throw new IllegalArgumentException("notification listener must not be null");
      } else {
         if (this.generalBroadcaster == null) {
            this.generalBroadcaster = new NotificationBroadcasterSupport();
         }

         this.generalBroadcaster.addNotificationListener(var1, var2, var3);
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "NotificationListener added");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
         }

      }
   }

   public void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      if (var1 == null) {
         throw new ListenerNotFoundException("Notification listener is null");
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Entry");
         }

         if (this.generalBroadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
         } else {
            this.generalBroadcaster.removeNotificationListener(var1);
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Exit");
            }

         }
      }
   }

   public void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      if (var1 == null) {
         throw new ListenerNotFoundException("Notification listener is null");
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
         }

         if (this.generalBroadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
         } else {
            this.generalBroadcaster.removeNotificationListener(var1, var2, var3);
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
            }

         }
      }
   }

   public void sendNotification(Notification var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("notification object must not be null"), "Exception occurred trying to send a notification from a RequiredModelMBean");
      } else {
         Descriptor var2 = this.modelMBeanInfo.getDescriptor(var1.getType(), "notification");
         Descriptor var3 = this.modelMBeanInfo.getMBeanDescriptor();
         if (var2 != null) {
            String var4 = (String)var2.getFieldValue("log");
            if (var4 == null && var3 != null) {
               var4 = (String)var3.getFieldValue("log");
            }

            if (var4 != null && (var4.equalsIgnoreCase("t") || var4.equalsIgnoreCase("true"))) {
               String var5 = (String)var2.getFieldValue("logfile");
               if (var5 == null && var3 != null) {
                  var5 = (String)var3.getFieldValue("logfile");
               }

               if (var5 != null) {
                  try {
                     this.writeToLog(var5, "LogMsg: " + (new Date(var1.getTimeStamp())).toString() + " " + var1.getType() + " " + var1.getMessage() + " Severity = " + (String)var2.getFieldValue("severity"));
                  } catch (Exception var7) {
                     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendNotification(Notification)", (String)("Failed to log " + var1.getType() + " notification: "), (Throwable)var7);
                     }
                  }
               }
            }
         }

         if (this.generalBroadcaster != null) {
            this.generalBroadcaster.sendNotification(var1);
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "sendNotification sent provided notification object");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", " Exit");
         }

      }
   }

   public void sendNotification(String var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("notification message must not be null"), "Exception occurred trying to send a text notification from a ModelMBean");
      } else {
         Notification var2 = new Notification("jmx.modelmbean.generic", this, 1L, var1);
         this.sendNotification(var2);
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Notification sent");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Exit");
         }

      }
   }

   private static final boolean hasNotification(ModelMBeanInfo var0, String var1) {
      try {
         if (var0 == null) {
            return false;
         } else {
            return var0.getNotification(var1) != null;
         }
      } catch (MBeanException var3) {
         return false;
      } catch (RuntimeOperationsException var4) {
         return false;
      }
   }

   private static final ModelMBeanNotificationInfo makeGenericInfo() {
      DescriptorSupport var0 = new DescriptorSupport(new String[]{"name=GENERIC", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.modelmbean.generic"});
      return new ModelMBeanNotificationInfo(new String[]{"jmx.modelmbean.generic"}, "GENERIC", "A text notification has been issued by the managed resource", var0);
   }

   private static final ModelMBeanNotificationInfo makeAttributeChangeInfo() {
      DescriptorSupport var0 = new DescriptorSupport(new String[]{"name=ATTRIBUTE_CHANGE", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.attribute.change"});
      return new ModelMBeanNotificationInfo(new String[]{"jmx.attribute.change"}, "ATTRIBUTE_CHANGE", "Signifies that an observed MBean attribute value has changed", var0);
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Entry");
      }

      boolean var1 = hasNotification(this.modelMBeanInfo, "GENERIC");
      boolean var2 = hasNotification(this.modelMBeanInfo, "ATTRIBUTE_CHANGE");
      ModelMBeanNotificationInfo[] var3 = (ModelMBeanNotificationInfo[])((ModelMBeanNotificationInfo[])this.modelMBeanInfo.getNotifications());
      int var4 = (var3 == null ? 0 : var3.length) + (var1 ? 0 : 1) + (var2 ? 0 : 1);
      ModelMBeanNotificationInfo[] var5 = new ModelMBeanNotificationInfo[var4];
      int var6 = 0;
      if (!var1) {
         var5[var6++] = makeGenericInfo();
      }

      if (!var2) {
         var5[var6++] = makeAttributeChangeInfo();
      }

      int var7 = var3.length;
      int var8 = var6;

      for(int var9 = 0; var9 < var7; ++var9) {
         var5[var8 + var9] = var3[var9];
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Exit");
      }

      return var5;
   }

   public void addAttributeChangeNotificationListener(NotificationListener var1, String var2, Object var3) throws MBeanException, RuntimeOperationsException, IllegalArgumentException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Entry");
      }

      if (var1 == null) {
         throw new IllegalArgumentException("Listener to be registered must not be null");
      } else {
         if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new NotificationBroadcasterSupport();
         }

         AttributeChangeNotificationFilter var5 = new AttributeChangeNotificationFilter();
         MBeanAttributeInfo[] var6 = this.modelMBeanInfo.getAttributes();
         boolean var7 = false;
         int var8;
         if (var2 == null) {
            if (var6 != null && var6.length > 0) {
               for(var8 = 0; var8 < var6.length; ++var8) {
                  var5.enableAttribute(var6[var8].getName());
               }
            }
         } else {
            if (var6 != null && var6.length > 0) {
               for(var8 = 0; var8 < var6.length; ++var8) {
                  if (var2.equals(var6[var8].getName())) {
                     var7 = true;
                     var5.enableAttribute(var2);
                     break;
                  }
               }
            }

            if (!var7) {
               throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name does not exist"), "Exception occurred trying to add an AttributeChangeNotification listener");
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            Vector var10 = var5.getEnabledAttributes();
            String var9 = var10.size() > 1 ? "[" + (String)var10.firstElement() + ", ...]" : var10.toString();
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Set attribute change filter to " + var9);
         }

         this.attributeBroadcaster.addNotificationListener(var1, var5, var3);
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Notification listener added for " + var2);
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Exit");
         }

      }
   }

   public void removeAttributeChangeNotificationListener(NotificationListener var1, String var2) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException {
      if (var1 == null) {
         throw new ListenerNotFoundException("Notification listener is null");
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Entry");
         }

         if (this.attributeBroadcaster == null) {
            throw new ListenerNotFoundException("No attribute change notification listeners registered");
         } else {
            MBeanAttributeInfo[] var4 = this.modelMBeanInfo.getAttributes();
            boolean var5 = false;
            if (var4 != null && var4.length > 0) {
               for(int var6 = 0; var6 < var4.length; ++var6) {
                  if (var4[var6].getName().equals(var2)) {
                     var5 = true;
                     break;
                  }
               }
            }

            if (!var5 && var2 != null) {
               throw new RuntimeOperationsException(new IllegalArgumentException("Invalid attribute name"), "Exception occurred trying to remove attribute change notification listener");
            } else {
               this.attributeBroadcaster.removeNotificationListener(var1);
               if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Exit");
               }

            }
         }
      }
   }

   public void sendAttributeChangeNotification(AttributeChangeNotification var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Entry");
      }

      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("attribute change notification object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
      } else {
         Object var3 = var1.getOldValue();
         Object var4 = var1.getNewValue();
         if (var3 == null) {
            var3 = "null";
         }

         if (var4 == null) {
            var4 = "null";
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Sending AttributeChangeNotification with " + var1.getAttributeName() + var1.getAttributeType() + var1.getNewValue() + var1.getOldValue());
         }

         Descriptor var5 = this.modelMBeanInfo.getDescriptor(var1.getType(), "notification");
         Descriptor var6 = this.modelMBeanInfo.getMBeanDescriptor();
         String var7;
         String var8;
         if (var5 != null) {
            var7 = (String)var5.getFieldValue("log");
            if (var7 == null && var6 != null) {
               var7 = (String)var6.getFieldValue("log");
            }

            if (var7 != null && (var7.equalsIgnoreCase("t") || var7.equalsIgnoreCase("true"))) {
               var8 = (String)var5.getFieldValue("logfile");
               if (var8 == null && var6 != null) {
                  var8 = (String)var6.getFieldValue("logfile");
               }

               if (var8 != null) {
                  try {
                     this.writeToLog(var8, "LogMsg: " + (new Date(var1.getTimeStamp())).toString() + " " + var1.getType() + " " + var1.getMessage() + " Name = " + var1.getAttributeName() + " Old value = " + var3 + " New value = " + var4);
                  } catch (Exception var11) {
                     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", (String)("Failed to log " + var1.getType() + " notification: "), (Throwable)var11);
                     }
                  }
               }
            }
         } else if (var6 != null) {
            var7 = (String)var6.getFieldValue("log");
            if (var7 != null && (var7.equalsIgnoreCase("t") || var7.equalsIgnoreCase("true"))) {
               var8 = (String)var6.getFieldValue("logfile");
               if (var8 != null) {
                  try {
                     this.writeToLog(var8, "LogMsg: " + (new Date(var1.getTimeStamp())).toString() + " " + var1.getType() + " " + var1.getMessage() + " Name = " + var1.getAttributeName() + " Old value = " + var3 + " New value = " + var4);
                  } catch (Exception var10) {
                     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", (String)("Failed to log " + var1.getType() + " notification: "), (Throwable)var10);
                     }
                  }
               }
            }
         }

         if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.sendNotification(var1);
         }

         if (this.generalBroadcaster != null) {
            this.generalBroadcaster.sendNotification(var1);
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "sent notification");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Exit");
         }

      }
   }

   public void sendAttributeChangeNotification(Attribute var1, Attribute var2) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Entry");
      }

      if (var1 != null && var2 != null) {
         if (!var1.getName().equals(var2.getName())) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute names are not the same"), "Exception occurred trying to send attribute change notification of a ModelMBean");
         } else {
            Object var4 = var2.getValue();
            Object var5 = var1.getValue();
            String var6 = "unknown";
            if (var4 != null) {
               var6 = var4.getClass().getName();
            }

            if (var5 != null) {
               var6 = var5.getClass().getName();
            }

            AttributeChangeNotification var7 = new AttributeChangeNotification(this, 1L, (new Date()).getTime(), "AttributeChangeDetected", var1.getName(), var6, var1.getValue(), var2.getValue());
            this.sendAttributeChangeNotification(var7);
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Exit");
            }

         }
      } else {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attribute object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
      }
   }

   protected ClassLoaderRepository getClassLoaderRepository() {
      return MBeanServerFactory.getClassLoaderRepository(this.server);
   }

   private Class<?> loadClass(final String var1) throws ClassNotFoundException {
      AccessControlContext var2 = AccessController.getContext();
      final ClassNotFoundException[] var3 = new ClassNotFoundException[1];
      Class var4 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Class<?>>() {
         public Class<?> run() {
            try {
               ReflectUtil.checkPackageAccess(var1);
               return Class.forName(var1);
            } catch (ClassNotFoundException var5) {
               ClassLoaderRepository var2 = RequiredModelMBean.this.getClassLoaderRepository();

               try {
                  if (var2 == null) {
                     throw new ClassNotFoundException(var1);
                  } else {
                     return var2.loadClass(var1);
                  }
               } catch (ClassNotFoundException var4) {
                  var3[0] = var4;
                  return null;
               }
            }
         }
      }, var2, this.acc);
      if (var3[0] != null) {
         throw var3[0];
      } else {
         return var4;
      }
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      if (var2 == null) {
         throw new NullPointerException("name of RequiredModelMBean to registered is null");
      } else {
         this.server = var1;
         return var2;
      }
   }

   public void postRegister(Boolean var1) {
      this.registered = var1;
   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
      this.registered = false;
      this.server = null;
   }

   static {
      primitiveClasses = new Class[]{Integer.TYPE, Long.TYPE, Boolean.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Byte.TYPE, Character.TYPE};
      primitiveClassMap = new HashMap();

      for(int var0 = 0; var0 < primitiveClasses.length; ++var0) {
         Class var1 = primitiveClasses[var0];
         primitiveClassMap.put(var1.getName(), var1);
      }

      primitiveTypes = new String[]{Boolean.TYPE.getName(), Byte.TYPE.getName(), Character.TYPE.getName(), Short.TYPE.getName(), Integer.TYPE.getName(), Long.TYPE.getName(), Float.TYPE.getName(), Double.TYPE.getName(), Void.TYPE.getName()};
      primitiveWrappers = new String[]{Boolean.class.getName(), Byte.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Void.class.getName()};
   }
}
