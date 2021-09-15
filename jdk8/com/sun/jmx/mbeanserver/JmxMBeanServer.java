package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public final class JmxMBeanServer implements SunJmxMBeanServer {
   public static final boolean DEFAULT_FAIR_LOCK_POLICY = true;
   private final MBeanInstantiator instantiator;
   private final SecureClassLoaderRepository secureClr;
   private final boolean interceptorsEnabled;
   private final MBeanServer outerShell;
   private volatile MBeanServer mbsInterceptor;
   private final MBeanServerDelegate mBeanServerDelegateObject;

   JmxMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3) {
      this(var1, var2, var3, (MBeanInstantiator)null, false);
   }

   JmxMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3, boolean var4) {
      this(var1, var2, var3, (MBeanInstantiator)null, false);
   }

   JmxMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3, MBeanInstantiator var4, boolean var5) {
      this(var1, var2, var3, var4, var5, true);
   }

   JmxMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3, final MBeanInstantiator var4, boolean var5, boolean var6) {
      this.mbsInterceptor = null;
      if (var4 == null) {
         ClassLoaderRepositorySupport var7 = new ClassLoaderRepositorySupport();
         var4 = new MBeanInstantiator(var7);
      }

      this.secureClr = new SecureClassLoaderRepository((ClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderRepository>() {
         public ClassLoaderRepository run() {
            return var4.getClassLoaderRepository();
         }
      }));
      if (var3 == null) {
         var3 = new MBeanServerDelegateImpl();
      }

      if (var2 == null) {
         var2 = this;
      }

      this.instantiator = var4;
      this.mBeanServerDelegateObject = (MBeanServerDelegate)var3;
      this.outerShell = (MBeanServer)var2;
      Repository var8 = new Repository(var1);
      this.mbsInterceptor = new DefaultMBeanServerInterceptor((MBeanServer)var2, (MBeanServerDelegate)var3, var4, var8);
      this.interceptorsEnabled = var5;
      this.initialize();
   }

   public boolean interceptorsEnabled() {
      return this.interceptorsEnabled;
   }

   public MBeanInstantiator getMBeanInstantiator() {
      if (this.interceptorsEnabled) {
         return this.instantiator;
      } else {
         throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return this.mbsInterceptor.createMBean(var1, this.cloneObjectName(var2), (Object[])null, (String[])null);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.mbsInterceptor.createMBean(var1, this.cloneObjectName(var2), var3, (Object[])null, (String[])null);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return this.mbsInterceptor.createMBean(var1, this.cloneObjectName(var2), var3, var4);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Object[] var4, String[] var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.mbsInterceptor.createMBean(var1, this.cloneObjectName(var2), var3, var4, var5);
   }

   public ObjectInstance registerMBean(Object var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      return this.mbsInterceptor.registerMBean(var1, this.cloneObjectName(var2));
   }

   public void unregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException {
      this.mbsInterceptor.unregisterMBean(this.cloneObjectName(var1));
   }

   public ObjectInstance getObjectInstance(ObjectName var1) throws InstanceNotFoundException {
      return this.mbsInterceptor.getObjectInstance(this.cloneObjectName(var1));
   }

   public Set<ObjectInstance> queryMBeans(ObjectName var1, QueryExp var2) {
      return this.mbsInterceptor.queryMBeans(this.cloneObjectName(var1), var2);
   }

   public Set<ObjectName> queryNames(ObjectName var1, QueryExp var2) {
      return this.mbsInterceptor.queryNames(this.cloneObjectName(var1), var2);
   }

   public boolean isRegistered(ObjectName var1) {
      return this.mbsInterceptor.isRegistered(var1);
   }

   public Integer getMBeanCount() {
      return this.mbsInterceptor.getMBeanCount();
   }

   public Object getAttribute(ObjectName var1, String var2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      return this.mbsInterceptor.getAttribute(this.cloneObjectName(var1), var2);
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2) throws InstanceNotFoundException, ReflectionException {
      return this.mbsInterceptor.getAttributes(this.cloneObjectName(var1), var2);
   }

   public void setAttribute(ObjectName var1, Attribute var2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      this.mbsInterceptor.setAttribute(this.cloneObjectName(var1), this.cloneAttribute(var2));
   }

   public AttributeList setAttributes(ObjectName var1, AttributeList var2) throws InstanceNotFoundException, ReflectionException {
      return this.mbsInterceptor.setAttributes(this.cloneObjectName(var1), this.cloneAttributeList(var2));
   }

   public Object invoke(ObjectName var1, String var2, Object[] var3, String[] var4) throws InstanceNotFoundException, MBeanException, ReflectionException {
      return this.mbsInterceptor.invoke(this.cloneObjectName(var1), var2, var3, var4);
   }

   public String getDefaultDomain() {
      return this.mbsInterceptor.getDefaultDomain();
   }

   public String[] getDomains() {
      return this.mbsInterceptor.getDomains();
   }

   public void addNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.mbsInterceptor.addNotificationListener(this.cloneObjectName(var1), var2, var3, var4);
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.mbsInterceptor.addNotificationListener(this.cloneObjectName(var1), var2, var3, var4);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(var1), var2);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(var1), var2, var3, var4);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(var1), var2);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(var1), var2, var3, var4);
   }

   public MBeanInfo getMBeanInfo(ObjectName var1) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
      return this.mbsInterceptor.getMBeanInfo(this.cloneObjectName(var1));
   }

   public Object instantiate(String var1) throws ReflectionException, MBeanException {
      checkMBeanPermission(var1, (String)null, (ObjectName)null, "instantiate");
      return this.instantiator.instantiate(var1);
   }

   public Object instantiate(String var1, ObjectName var2) throws ReflectionException, MBeanException, InstanceNotFoundException {
      checkMBeanPermission(var1, (String)null, (ObjectName)null, "instantiate");
      ClassLoader var3 = this.outerShell.getClass().getClassLoader();
      return this.instantiator.instantiate(var1, var2, var3);
   }

   public Object instantiate(String var1, Object[] var2, String[] var3) throws ReflectionException, MBeanException {
      checkMBeanPermission(var1, (String)null, (ObjectName)null, "instantiate");
      ClassLoader var4 = this.outerShell.getClass().getClassLoader();
      return this.instantiator.instantiate(var1, var2, var3, var4);
   }

   public Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, MBeanException, InstanceNotFoundException {
      checkMBeanPermission(var1, (String)null, (ObjectName)null, "instantiate");
      ClassLoader var5 = this.outerShell.getClass().getClassLoader();
      return this.instantiator.instantiate(var1, var2, var3, var4, var5);
   }

   public boolean isInstanceOf(ObjectName var1, String var2) throws InstanceNotFoundException {
      return this.mbsInterceptor.isInstanceOf(this.cloneObjectName(var1), var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(ObjectName var1, byte[] var2) throws InstanceNotFoundException, OperationsException {
      ClassLoader var3 = this.getClassLoaderFor(var1);
      return this.instantiator.deserialize(var3, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, byte[] var2) throws OperationsException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
      } else {
         ClassLoaderRepository var3 = this.getClassLoaderRepository();

         Class var4;
         try {
            if (var3 == null) {
               throw new ClassNotFoundException(var1);
            }

            var4 = var3.loadClass(var1);
         } catch (ClassNotFoundException var6) {
            throw new ReflectionException(var6, "The given class could not be loaded by the default loader repository");
         }

         return this.instantiator.deserialize(var4.getClassLoader(), var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3) throws InstanceNotFoundException, OperationsException, ReflectionException {
      var2 = this.cloneObjectName(var2);

      try {
         this.getClassLoader(var2);
      } catch (SecurityException var5) {
         throw var5;
      } catch (Exception var6) {
      }

      ClassLoader var4 = this.outerShell.getClass().getClassLoader();
      return this.instantiator.deserialize(var1, var2, var3, var4);
   }

   private void initialize() {
      if (this.instantiator == null) {
         throw new IllegalStateException("instantiator must not be null.");
      } else {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  JmxMBeanServer.this.mbsInterceptor.registerMBean(JmxMBeanServer.this.mBeanServerDelegateObject, MBeanServerDelegate.DELEGATE_NAME);
                  return null;
               }
            });
         } catch (SecurityException var4) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", (String)"Unexpected security exception occurred", (Throwable)var4);
            }

            throw var4;
         } catch (Exception var5) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", (String)"Unexpected exception occurred", (Throwable)var5);
            }

            throw new IllegalStateException("Can't register delegate.", var5);
         }

         ClassLoader var1 = this.outerShell.getClass().getClassLoader();
         ModifiableClassLoaderRepository var2 = (ModifiableClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ModifiableClassLoaderRepository>() {
            public ModifiableClassLoaderRepository run() {
               return JmxMBeanServer.this.instantiator.getClassLoaderRepository();
            }
         });
         if (var2 != null) {
            var2.addClassLoader(var1);
            ClassLoader var3 = ClassLoader.getSystemClassLoader();
            if (var3 != var1) {
               var2.addClassLoader(var3);
            }
         }

      }
   }

   public synchronized MBeanServer getMBeanServerInterceptor() {
      if (this.interceptorsEnabled) {
         return this.mbsInterceptor;
      } else {
         throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
      }
   }

   public synchronized void setMBeanServerInterceptor(MBeanServer var1) {
      if (!this.interceptorsEnabled) {
         throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
      } else if (var1 == null) {
         throw new IllegalArgumentException("MBeanServerInterceptor is null");
      } else {
         this.mbsInterceptor = var1;
      }
   }

   public ClassLoader getClassLoaderFor(ObjectName var1) throws InstanceNotFoundException {
      return this.mbsInterceptor.getClassLoaderFor(this.cloneObjectName(var1));
   }

   public ClassLoader getClassLoader(ObjectName var1) throws InstanceNotFoundException {
      return this.mbsInterceptor.getClassLoader(this.cloneObjectName(var1));
   }

   public ClassLoaderRepository getClassLoaderRepository() {
      checkMBeanPermission((String)null, (String)null, (ObjectName)null, "getClassLoaderRepository");
      return this.secureClr;
   }

   public MBeanServerDelegate getMBeanServerDelegate() {
      if (!this.interceptorsEnabled) {
         throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
      } else {
         return this.mBeanServerDelegateObject;
      }
   }

   public static MBeanServerDelegate newMBeanServerDelegate() {
      return new MBeanServerDelegateImpl();
   }

   public static MBeanServer newMBeanServer(String var0, MBeanServer var1, MBeanServerDelegate var2, boolean var3) {
      checkNewMBeanServerPermission();
      return new JmxMBeanServer(var0, var1, var2, (MBeanInstantiator)null, var3, true);
   }

   private ObjectName cloneObjectName(ObjectName var1) {
      return var1 != null ? ObjectName.getInstance(var1) : var1;
   }

   private Attribute cloneAttribute(Attribute var1) {
      return var1 != null && !var1.getClass().equals(Attribute.class) ? new Attribute(var1.getName(), var1.getValue()) : var1;
   }

   private AttributeList cloneAttributeList(AttributeList var1) {
      if (var1 == null) {
         return var1;
      } else {
         List var2 = var1.asList();
         if (var1.getClass().equals(AttributeList.class)) {
            for(int var6 = 0; var6 < var2.size(); ++var6) {
               Attribute var7 = (Attribute)var2.get(var6);
               if (!var7.getClass().equals(Attribute.class)) {
                  var1.set(var6, this.cloneAttribute(var7));
               }
            }

            return var1;
         } else {
            AttributeList var3 = new AttributeList(var2.size());
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Attribute var5 = (Attribute)var4.next();
               var3.add(this.cloneAttribute(var5));
            }

            return var3;
         }
      }
   }

   private static void checkMBeanPermission(String var0, String var1, ObjectName var2, String var3) throws SecurityException {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         MBeanPermission var5 = new MBeanPermission(var0, var1, var2, var3);
         var4.checkPermission(var5);
      }

   }

   private static void checkNewMBeanServerPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         MBeanServerPermission var1 = new MBeanServerPermission("newMBeanServer");
         var0.checkPermission(var1);
      }

   }
}
