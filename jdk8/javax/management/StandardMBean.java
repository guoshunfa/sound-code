package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DescriptorCache;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanSupport;
import com.sun.jmx.mbeanserver.MXBeanSupport;
import com.sun.jmx.mbeanserver.StandardMBeanSupport;
import com.sun.jmx.mbeanserver.Util;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;

public class StandardMBean implements DynamicMBean, MBeanRegistration {
   private static final DescriptorCache descriptors;
   private volatile MBeanSupport<?> mbean;
   private volatile MBeanInfo cachedMBeanInfo;
   private static final Map<Class<?>, Boolean> mbeanInfoSafeMap;

   private <T> void construct(T var1, Class<T> var2, boolean var3, boolean var4) throws NotCompliantMBeanException {
      if (var1 == null) {
         if (!var3) {
            throw new IllegalArgumentException("implementation is null");
         }

         var1 = Util.cast(this);
      }

      if (var4) {
         if (var2 == null) {
            var2 = (Class)Util.cast(Introspector.getMXBeanInterface(var1.getClass()));
         }

         this.mbean = new MXBeanSupport(var1, var2);
      } else {
         if (var2 == null) {
            var2 = (Class)Util.cast(Introspector.getStandardMBeanInterface(var1.getClass()));
         }

         this.mbean = new StandardMBeanSupport(var1, var2);
      }

   }

   public <T> StandardMBean(T var1, Class<T> var2) throws NotCompliantMBeanException {
      this.construct(var1, var2, false, false);
   }

   protected StandardMBean(Class<?> var1) throws NotCompliantMBeanException {
      this.construct((Object)null, var1, true, false);
   }

   public <T> StandardMBean(T var1, Class<T> var2, boolean var3) {
      try {
         this.construct(var1, var2, false, var3);
      } catch (NotCompliantMBeanException var5) {
         throw new IllegalArgumentException(var5);
      }
   }

   protected StandardMBean(Class<?> var1, boolean var2) {
      try {
         this.construct((Object)null, var1, true, var2);
      } catch (NotCompliantMBeanException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public void setImplementation(Object var1) throws NotCompliantMBeanException {
      if (var1 == null) {
         throw new IllegalArgumentException("implementation is null");
      } else {
         if (this.isMXBean()) {
            this.mbean = new MXBeanSupport(var1, (Class)Util.cast(this.getMBeanInterface()));
         } else {
            this.mbean = new StandardMBeanSupport(var1, (Class)Util.cast(this.getMBeanInterface()));
         }

      }
   }

   public Object getImplementation() {
      return this.mbean.getResource();
   }

   public final Class<?> getMBeanInterface() {
      return this.mbean.getMBeanInterface();
   }

   public Class<?> getImplementationClass() {
      return this.mbean.getResource().getClass();
   }

   public Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException {
      return this.mbean.getAttribute(var1);
   }

   public void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      this.mbean.setAttribute(var1);
   }

   public AttributeList getAttributes(String[] var1) {
      return this.mbean.getAttributes(var1);
   }

   public AttributeList setAttributes(AttributeList var1) {
      return this.mbean.setAttributes(var1);
   }

   public Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException {
      return this.mbean.invoke(var1, var2, var3);
   }

   public MBeanInfo getMBeanInfo() {
      try {
         MBeanInfo var1 = this.getCachedMBeanInfo();
         if (var1 != null) {
            return var1;
         }
      } catch (RuntimeException var15) {
         if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", (String)"Failed to get cached MBeanInfo", (Throwable)var15);
         }
      }

      if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MISC_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "getMBeanInfo", "Building MBeanInfo for " + this.getImplementationClass().getName());
      }

      MBeanSupport var16 = this.mbean;
      MBeanInfo var2 = var16.getMBeanInfo();
      Object var3 = var16.getResource();
      boolean var4 = immutableInfo(this.getClass());
      String var5 = this.getClassName(var2);
      String var6 = this.getDescription(var2);
      MBeanConstructorInfo[] var7 = this.getConstructors(var2, var3);
      MBeanAttributeInfo[] var8 = this.getAttributes(var2);
      MBeanOperationInfo[] var9 = this.getOperations(var2);
      MBeanNotificationInfo[] var10 = this.getNotifications(var2);
      Descriptor var11 = this.getDescriptor(var2, var4);
      MBeanInfo var12 = new MBeanInfo(var5, var6, var8, var7, var9, var10, var11);

      try {
         this.cacheMBeanInfo(var12);
      } catch (RuntimeException var14) {
         if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", (String)"Failed to cache MBeanInfo", (Throwable)var14);
         }
      }

      return var12;
   }

   protected String getClassName(MBeanInfo var1) {
      return var1 == null ? this.getImplementationClass().getName() : var1.getClassName();
   }

   protected String getDescription(MBeanInfo var1) {
      return var1 == null ? null : var1.getDescription();
   }

   protected String getDescription(MBeanFeatureInfo var1) {
      return var1 == null ? null : var1.getDescription();
   }

   protected String getDescription(MBeanAttributeInfo var1) {
      return this.getDescription((MBeanFeatureInfo)var1);
   }

   protected String getDescription(MBeanConstructorInfo var1) {
      return this.getDescription((MBeanFeatureInfo)var1);
   }

   protected String getDescription(MBeanConstructorInfo var1, MBeanParameterInfo var2, int var3) {
      return var2 == null ? null : var2.getDescription();
   }

   protected String getParameterName(MBeanConstructorInfo var1, MBeanParameterInfo var2, int var3) {
      return var2 == null ? null : var2.getName();
   }

   protected String getDescription(MBeanOperationInfo var1) {
      return this.getDescription((MBeanFeatureInfo)var1);
   }

   protected int getImpact(MBeanOperationInfo var1) {
      return var1 == null ? 3 : var1.getImpact();
   }

   protected String getParameterName(MBeanOperationInfo var1, MBeanParameterInfo var2, int var3) {
      return var2 == null ? null : var2.getName();
   }

   protected String getDescription(MBeanOperationInfo var1, MBeanParameterInfo var2, int var3) {
      return var2 == null ? null : var2.getDescription();
   }

   protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] var1, Object var2) {
      if (var1 == null) {
         return null;
      } else {
         return var2 != null && var2 != this ? null : var1;
      }
   }

   MBeanNotificationInfo[] getNotifications(MBeanInfo var1) {
      return null;
   }

   Descriptor getDescriptor(MBeanInfo var1, boolean var2) {
      ImmutableDescriptor var3;
      if (var1 != null && var1.getDescriptor() != null && var1.getDescriptor().getFieldNames().length != 0) {
         Descriptor var10 = var1.getDescriptor();
         HashMap var11 = new HashMap();
         String[] var6 = var10.getFieldNames();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (var9.equals("immutableInfo")) {
               var11.put(var9, Boolean.toString(var2));
            } else {
               var11.put(var9, var10.getFieldValue(var9));
            }
         }

         var3 = new ImmutableDescriptor(var11);
      } else {
         String var4 = "interfaceClassName=" + this.getMBeanInterface().getName();
         String var5 = "immutableInfo=" + var2;
         var3 = new ImmutableDescriptor(new String[]{var4, var5});
         var3 = descriptors.get(var3);
      }

      return var3;
   }

   protected MBeanInfo getCachedMBeanInfo() {
      return this.cachedMBeanInfo;
   }

   protected void cacheMBeanInfo(MBeanInfo var1) {
      this.cachedMBeanInfo = var1;
   }

   private boolean isMXBean() {
      return this.mbean.isMXBean();
   }

   private static <T> boolean identicalArrays(T[] var0, T[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null && var0.length == var1.length) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var0[var2] != var1[var2]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static <T> boolean equal(T var0, T var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null ? var0.equals(var1) : false;
      }
   }

   private static MBeanParameterInfo customize(MBeanParameterInfo var0, String var1, String var2) {
      if (equal(var1, var0.getName()) && equal(var2, var0.getDescription())) {
         return var0;
      } else if (var0 instanceof OpenMBeanParameterInfo) {
         OpenMBeanParameterInfo var3 = (OpenMBeanParameterInfo)var0;
         return new OpenMBeanParameterInfoSupport(var1, var2, var3.getOpenType(), var0.getDescriptor());
      } else {
         return new MBeanParameterInfo(var1, var0.getType(), var2, var0.getDescriptor());
      }
   }

   private static MBeanConstructorInfo customize(MBeanConstructorInfo var0, String var1, MBeanParameterInfo[] var2) {
      if (equal(var1, var0.getDescription()) && identicalArrays(var2, var0.getSignature())) {
         return var0;
      } else if (var0 instanceof OpenMBeanConstructorInfo) {
         OpenMBeanParameterInfo[] var3 = paramsToOpenParams(var2);
         return new OpenMBeanConstructorInfoSupport(var0.getName(), var1, var3, var0.getDescriptor());
      } else {
         return new MBeanConstructorInfo(var0.getName(), var1, var2, var0.getDescriptor());
      }
   }

   private static MBeanOperationInfo customize(MBeanOperationInfo var0, String var1, MBeanParameterInfo[] var2, int var3) {
      if (equal(var1, var0.getDescription()) && identicalArrays(var2, var0.getSignature()) && var3 == var0.getImpact()) {
         return var0;
      } else if (var0 instanceof OpenMBeanOperationInfo) {
         OpenMBeanOperationInfo var4 = (OpenMBeanOperationInfo)var0;
         OpenMBeanParameterInfo[] var5 = paramsToOpenParams(var2);
         return new OpenMBeanOperationInfoSupport(var0.getName(), var1, var5, var4.getReturnOpenType(), var3, var0.getDescriptor());
      } else {
         return new MBeanOperationInfo(var0.getName(), var1, var2, var0.getReturnType(), var3, var0.getDescriptor());
      }
   }

   private static MBeanAttributeInfo customize(MBeanAttributeInfo var0, String var1) {
      if (equal(var1, var0.getDescription())) {
         return var0;
      } else if (var0 instanceof OpenMBeanAttributeInfo) {
         OpenMBeanAttributeInfo var2 = (OpenMBeanAttributeInfo)var0;
         return new OpenMBeanAttributeInfoSupport(var0.getName(), var1, var2.getOpenType(), var0.isReadable(), var0.isWritable(), var0.isIs(), var0.getDescriptor());
      } else {
         return new MBeanAttributeInfo(var0.getName(), var0.getType(), var1, var0.isReadable(), var0.isWritable(), var0.isIs(), var0.getDescriptor());
      }
   }

   private static OpenMBeanParameterInfo[] paramsToOpenParams(MBeanParameterInfo[] var0) {
      if (var0 instanceof OpenMBeanParameterInfo[]) {
         return (OpenMBeanParameterInfo[])((OpenMBeanParameterInfo[])var0);
      } else {
         OpenMBeanParameterInfoSupport[] var1 = new OpenMBeanParameterInfoSupport[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   private MBeanConstructorInfo[] getConstructors(MBeanInfo var1, Object var2) {
      MBeanConstructorInfo[] var3 = this.getConstructors(var1.getConstructors(), var2);
      if (var3 == null) {
         return null;
      } else {
         int var4 = var3.length;
         MBeanConstructorInfo[] var5 = new MBeanConstructorInfo[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            MBeanConstructorInfo var7 = var3[var6];
            MBeanParameterInfo[] var8 = var7.getSignature();
            MBeanParameterInfo[] var9;
            if (var8 != null) {
               int var10 = var8.length;
               var9 = new MBeanParameterInfo[var10];

               for(int var11 = 0; var11 < var10; ++var11) {
                  MBeanParameterInfo var12 = var8[var11];
                  var9[var11] = customize(var12, this.getParameterName(var7, var12, var11), this.getDescription(var7, var12, var11));
               }
            } else {
               var9 = null;
            }

            var5[var6] = customize(var7, this.getDescription(var7), var9);
         }

         return var5;
      }
   }

   private MBeanOperationInfo[] getOperations(MBeanInfo var1) {
      MBeanOperationInfo[] var2 = var1.getOperations();
      if (var2 == null) {
         return null;
      } else {
         int var3 = var2.length;
         MBeanOperationInfo[] var4 = new MBeanOperationInfo[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            MBeanOperationInfo var6 = var2[var5];
            MBeanParameterInfo[] var7 = var6.getSignature();
            MBeanParameterInfo[] var8;
            if (var7 != null) {
               int var9 = var7.length;
               var8 = new MBeanParameterInfo[var9];

               for(int var10 = 0; var10 < var9; ++var10) {
                  MBeanParameterInfo var11 = var7[var10];
                  var8[var10] = customize(var11, this.getParameterName(var6, var11, var10), this.getDescription(var6, var11, var10));
               }
            } else {
               var8 = null;
            }

            var4[var5] = customize(var6, this.getDescription(var6), var8, this.getImpact(var6));
         }

         return var4;
      }
   }

   private MBeanAttributeInfo[] getAttributes(MBeanInfo var1) {
      MBeanAttributeInfo[] var2 = var1.getAttributes();
      if (var2 == null) {
         return null;
      } else {
         int var4 = var2.length;
         MBeanAttributeInfo[] var3 = new MBeanAttributeInfo[var4];

         for(int var5 = 0; var5 < var4; ++var5) {
            MBeanAttributeInfo var6 = var2[var5];
            var3[var5] = customize(var6, this.getDescription(var6));
         }

         return var3;
      }
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.mbean.register(var1, var2);
      return var2;
   }

   public void postRegister(Boolean var1) {
      if (!var1) {
         this.mbean.unregister();
      }

   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
      this.mbean.unregister();
   }

   static boolean immutableInfo(Class<? extends StandardMBean> var0) {
      if (var0 != StandardMBean.class && var0 != StandardEmitterMBean.class) {
         synchronized(mbeanInfoSafeMap) {
            Boolean var2 = (Boolean)mbeanInfoSafeMap.get(var0);
            if (var2 == null) {
               try {
                  StandardMBean.MBeanInfoSafeAction var3 = new StandardMBean.MBeanInfoSafeAction(var0);
                  var2 = (Boolean)AccessController.doPrivileged((PrivilegedAction)var3);
               } catch (Exception var5) {
                  var2 = false;
               }

               mbeanInfoSafeMap.put(var0, var2);
            }

            return var2;
         }
      } else {
         return true;
      }
   }

   static boolean overrides(Class<?> var0, Class<?> var1, String var2, Class<?>... var3) {
      Class var4 = var0;

      while(var4 != var1) {
         try {
            var4.getDeclaredMethod(var2, var3);
            return true;
         } catch (NoSuchMethodException var6) {
            var4 = var4.getSuperclass();
         }
      }

      return false;
   }

   static {
      descriptors = DescriptorCache.getInstance(JMX.proof);
      mbeanInfoSafeMap = new WeakHashMap();
   }

   private static class MBeanInfoSafeAction implements PrivilegedAction<Boolean> {
      private final Class<?> subclass;

      MBeanInfoSafeAction(Class<?> var1) {
         this.subclass = var1;
      }

      public Boolean run() {
         if (StandardMBean.overrides(this.subclass, StandardMBean.class, "cacheMBeanInfo", MBeanInfo.class)) {
            return false;
         } else if (StandardMBean.overrides(this.subclass, StandardMBean.class, "getCachedMBeanInfo", (Class[])null)) {
            return false;
         } else if (StandardMBean.overrides(this.subclass, StandardMBean.class, "getMBeanInfo", (Class[])null)) {
            return false;
         } else {
            return StandardEmitterMBean.class.isAssignableFrom(this.subclass) && StandardMBean.overrides(this.subclass, StandardEmitterMBean.class, "getNotificationInfo", (Class[])null) ? false : true;
         }
      }
   }
}
