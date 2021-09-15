package com.sun.jmx.interceptor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DynamicMBean2;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanInstantiator;
import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MBeanTrustPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public class DefaultMBeanServerInterceptor implements MBeanServerInterceptor {
   private final transient MBeanInstantiator instantiator;
   private transient MBeanServer server = null;
   private final transient MBeanServerDelegate delegate;
   private final transient Repository repository;
   private final transient WeakHashMap<DefaultMBeanServerInterceptor.ListenerWrapper, WeakReference<DefaultMBeanServerInterceptor.ListenerWrapper>> listenerWrappers = new WeakHashMap();
   private final String domain;
   private final Set<ObjectName> beingUnregistered = new HashSet();

   public DefaultMBeanServerInterceptor(MBeanServer var1, MBeanServerDelegate var2, MBeanInstantiator var3, Repository var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("outer MBeanServer cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("MBeanServerDelegate cannot be null");
      } else if (var3 == null) {
         throw new IllegalArgumentException("MBeanInstantiator cannot be null");
      } else if (var4 == null) {
         throw new IllegalArgumentException("Repository cannot be null");
      } else {
         this.server = var1;
         this.delegate = var2;
         this.instantiator = var3;
         this.repository = var4;
         this.domain = var4.getDefaultDomain();
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return this.createMBean(var1, var2, (Object[])null, (String[])null);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.createMBean(var1, var2, var3, (Object[])null, (String[])null);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      try {
         return this.createMBean(var1, var2, (ObjectName)null, true, var3, var4);
      } catch (InstanceNotFoundException var6) {
         throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException("Unexpected exception: " + var6), var6);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Object[] var4, String[] var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.createMBean(var1, var2, var3, false, var4, var5);
   }

   private ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, boolean var4, Object[] var5, String[] var6) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      IllegalArgumentException var10;
      if (var1 == null) {
         var10 = new IllegalArgumentException("The class name cannot be null");
         throw new RuntimeOperationsException(var10, "Exception occurred during MBean creation");
      } else {
         if (var2 != null) {
            if (var2.isPattern()) {
               var10 = new IllegalArgumentException("Invalid name->" + var2.toString());
               throw new RuntimeOperationsException(var10, "Exception occurred during MBean creation");
            }

            var2 = this.nonDefaultDomain(var2);
         }

         checkMBeanPermission((String)var1, (String)null, (ObjectName)null, "instantiate");
         checkMBeanPermission((String)var1, (String)null, var2, "registerMBean");
         Class var7;
         if (var4) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + var1 + ", ObjectName = " + var2);
            }

            var7 = this.instantiator.findClassWithDefaultLoaderRepository(var1);
         } else if (var3 == null) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + var1 + ", ObjectName = " + var2 + ", Loader name = null");
            }

            var7 = this.instantiator.findClass(var1, this.server.getClass().getClassLoader());
         } else {
            var3 = this.nonDefaultDomain(var3);
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + var1 + ", ObjectName = " + var2 + ", Loader name = " + var3);
            }

            var7 = this.instantiator.findClass(var1, var3);
         }

         checkMBeanTrustPermission(var7);
         Introspector.testCreation(var7);
         Introspector.checkCompliance(var7);
         Object var8 = this.instantiator.instantiate(var7, var5, var6, this.server.getClass().getClassLoader());
         String var9 = getNewMBeanClassName(var8);
         return this.registerObject(var9, var8, var2);
      }
   }

   public ObjectInstance registerMBean(Object var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      Class var3 = var1.getClass();
      Introspector.checkCompliance(var3);
      String var4 = getNewMBeanClassName(var1);
      checkMBeanPermission((String)var4, (String)null, var2, "registerMBean");
      checkMBeanTrustPermission(var3);
      return this.registerObject(var4, var1, var2);
   }

   private static String getNewMBeanClassName(Object var0) throws NotCompliantMBeanException {
      if (var0 instanceof DynamicMBean) {
         DynamicMBean var1 = (DynamicMBean)var0;

         String var2;
         try {
            var2 = var1.getMBeanInfo().getClassName();
         } catch (Exception var5) {
            NotCompliantMBeanException var4 = new NotCompliantMBeanException("Bad getMBeanInfo()");
            var4.initCause(var5);
            throw var4;
         }

         if (var2 == null) {
            throw new NotCompliantMBeanException("MBeanInfo has null class name");
         } else {
            return var2;
         }
      } else {
         return var0.getClass().getName();
      }
   }

   public void unregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException {
      if (var1 == null) {
         IllegalArgumentException var2 = new IllegalArgumentException("Object name cannot be null");
         throw new RuntimeOperationsException(var2, "Exception occurred trying to unregister the MBean");
      } else {
         var1 = this.nonDefaultDomain(var1);
         synchronized(this.beingUnregistered) {
            while(this.beingUnregistered.contains(var1)) {
               try {
                  this.beingUnregistered.wait();
               } catch (InterruptedException var18) {
                  throw new MBeanRegistrationException(var18, var18.toString());
               }
            }

            this.beingUnregistered.add(var1);
         }

         boolean var14 = false;

         try {
            var14 = true;
            this.exclusiveUnregisterMBean(var1);
            var14 = false;
         } finally {
            if (var14) {
               synchronized(this.beingUnregistered) {
                  this.beingUnregistered.remove(var1);
                  this.beingUnregistered.notifyAll();
               }
            }
         }

         synchronized(this.beingUnregistered) {
            this.beingUnregistered.remove(var1);
            this.beingUnregistered.notifyAll();
         }
      }
   }

   private void exclusiveUnregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException {
      DynamicMBean var2 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var2, (String)null, var1, "unregisterMBean");
      if (var2 instanceof MBeanRegistration) {
         preDeregisterInvoke((MBeanRegistration)var2);
      }

      Object var3 = getResource(var2);
      DefaultMBeanServerInterceptor.ResourceContext var4 = this.unregisterFromRepository(var3, var2, var1);

      try {
         if (var2 instanceof MBeanRegistration) {
            postDeregisterInvoke(var1, (MBeanRegistration)var2);
         }
      } finally {
         var4.done();
      }

   }

   public ObjectInstance getObjectInstance(ObjectName var1) throws InstanceNotFoundException {
      var1 = this.nonDefaultDomain(var1);
      DynamicMBean var2 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var2, (String)null, var1, "getObjectInstance");
      String var3 = getClassName(var2);
      return new ObjectInstance(var1, var3);
   }

   public Set<ObjectInstance> queryMBeans(ObjectName var1, QueryExp var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 == null) {
         return this.queryMBeansImpl(var1, var2);
      } else {
         checkMBeanPermission((String)((String)null), (String)null, (ObjectName)null, "queryMBeans");
         Set var4 = this.queryMBeansImpl(var1, (QueryExp)null);
         HashSet var5 = new HashSet(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            ObjectInstance var7 = (ObjectInstance)var6.next();

            try {
               checkMBeanPermission((String)var7.getClassName(), (String)null, var7.getObjectName(), "queryMBeans");
               var5.add(var7);
            } catch (SecurityException var9) {
            }
         }

         return this.filterListOfObjectInstances(var5, var2);
      }
   }

   private Set<ObjectInstance> queryMBeansImpl(ObjectName var1, QueryExp var2) {
      Set var3 = this.repository.query(var1, var2);
      return this.objectInstancesFromFilteredNamedObjects(var3, var2);
   }

   public Set<ObjectName> queryNames(ObjectName var1, QueryExp var2) {
      SecurityManager var4 = System.getSecurityManager();
      Object var3;
      if (var4 != null) {
         checkMBeanPermission((String)((String)null), (String)null, (ObjectName)null, "queryNames");
         Set var5 = this.queryMBeansImpl(var1, (QueryExp)null);
         HashSet var6 = new HashSet(var5.size());
         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            ObjectInstance var8 = (ObjectInstance)var7.next();

            try {
               checkMBeanPermission((String)var8.getClassName(), (String)null, var8.getObjectName(), "queryNames");
               var6.add(var8);
            } catch (SecurityException var10) {
            }
         }

         Set var11 = this.filterListOfObjectInstances(var6, var2);
         var3 = new HashSet(var11.size());
         Iterator var12 = var11.iterator();

         while(var12.hasNext()) {
            ObjectInstance var9 = (ObjectInstance)var12.next();
            ((Set)var3).add(var9.getObjectName());
         }
      } else {
         var3 = this.queryNamesImpl(var1, var2);
      }

      return (Set)var3;
   }

   private Set<ObjectName> queryNamesImpl(ObjectName var1, QueryExp var2) {
      Set var3 = this.repository.query(var1, var2);
      return this.objectNamesFromFilteredNamedObjects(var3, var2);
   }

   public boolean isRegistered(ObjectName var1) {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Object name cannot be null");
      } else {
         var1 = this.nonDefaultDomain(var1);
         return this.repository.contains(var1);
      }
   }

   public String[] getDomains() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 == null) {
         return this.repository.getDomains();
      } else {
         checkMBeanPermission((String)((String)null), (String)null, (ObjectName)null, "getDomains");
         String[] var2 = this.repository.getDomains();
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            try {
               ObjectName var5 = Util.newObjectName(var2[var4] + ":x=x");
               checkMBeanPermission((String)((String)null), (String)null, var5, "getDomains");
               var3.add(var2[var4]);
            } catch (SecurityException var6) {
            }
         }

         return (String[])var3.toArray(new String[var3.size()]);
      }
   }

   public Integer getMBeanCount() {
      return this.repository.getCount();
   }

   public Object getAttribute(ObjectName var1, String var2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
      } else if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
      } else {
         var1 = this.nonDefaultDomain(var1);
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttribute", "Attribute = " + var2 + ", ObjectName = " + var1);
         }

         DynamicMBean var3 = this.getMBean(var1);
         checkMBeanPermission(var3, var2, var1, "getAttribute");

         try {
            return var3.getAttribute(var2);
         } catch (AttributeNotFoundException var5) {
            throw var5;
         } catch (Throwable var6) {
            rethrowMaybeMBeanException(var6);
            throw new AssertionError();
         }
      }
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2) throws InstanceNotFoundException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
      } else if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attributes cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
      } else {
         var1 = this.nonDefaultDomain(var1);
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttributes", "ObjectName = " + var1);
         }

         DynamicMBean var3 = this.getMBean(var1);
         SecurityManager var5 = System.getSecurityManager();
         String[] var4;
         if (var5 == null) {
            var4 = var2;
         } else {
            String var6 = getClassName(var3);
            checkMBeanPermission((String)var6, (String)null, var1, "getAttribute");
            ArrayList var7 = new ArrayList(var2.length);
            String[] var8 = var2;
            int var9 = var2.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               String var11 = var8[var10];

               try {
                  checkMBeanPermission(var6, var11, var1, "getAttribute");
                  var7.add(var11);
               } catch (SecurityException var14) {
               }
            }

            var4 = (String[])var7.toArray(new String[var7.size()]);
         }

         try {
            return var3.getAttributes(var4);
         } catch (Throwable var13) {
            rethrow(var13);
            throw new AssertionError();
         }
      }
   }

   public void setAttribute(ObjectName var1, Attribute var2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
      } else if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
      } else {
         var1 = this.nonDefaultDomain(var1);
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "setAttribute", "ObjectName = " + var1 + ", Attribute = " + var2.getName());
         }

         DynamicMBean var3 = this.getMBean(var1);
         checkMBeanPermission(var3, var2.getName(), var1, "setAttribute");

         try {
            var3.setAttribute(var2);
         } catch (AttributeNotFoundException var5) {
            throw var5;
         } catch (InvalidAttributeValueException var6) {
            throw var6;
         } catch (Throwable var7) {
            rethrowMaybeMBeanException(var7);
            throw new AssertionError();
         }
      }
   }

   public AttributeList setAttributes(ObjectName var1, AttributeList var2) throws InstanceNotFoundException, ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
      } else if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList  cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
      } else {
         var1 = this.nonDefaultDomain(var1);
         DynamicMBean var3 = this.getMBean(var1);
         SecurityManager var5 = System.getSecurityManager();
         AttributeList var4;
         if (var5 == null) {
            var4 = var2;
         } else {
            String var6 = getClassName(var3);
            checkMBeanPermission((String)var6, (String)null, var1, "setAttribute");
            var4 = new AttributeList(var2.size());
            Iterator var7 = var2.asList().iterator();

            while(var7.hasNext()) {
               Attribute var8 = (Attribute)var7.next();

               try {
                  checkMBeanPermission(var6, var8.getName(), var1, "setAttribute");
                  var4.add(var8);
               } catch (SecurityException var11) {
               }
            }
         }

         try {
            return var3.setAttributes(var4);
         } catch (Throwable var10) {
            rethrow(var10);
            throw new AssertionError();
         }
      }
   }

   public Object invoke(ObjectName var1, String var2, Object[] var3, String[] var4) throws InstanceNotFoundException, MBeanException, ReflectionException {
      var1 = this.nonDefaultDomain(var1);
      DynamicMBean var5 = this.getMBean(var1);
      checkMBeanPermission(var5, var2, var1, "invoke");

      try {
         return var5.invoke(var2, var3, var4);
      } catch (Throwable var7) {
         rethrowMaybeMBeanException(var7);
         throw new AssertionError();
      }
   }

   private static void rethrow(Throwable var0) throws ReflectionException {
      try {
         throw var0;
      } catch (ReflectionException var2) {
         throw var2;
      } catch (RuntimeOperationsException var3) {
         throw var3;
      } catch (RuntimeErrorException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw new RuntimeMBeanException(var5, var5.toString());
      } catch (Error var6) {
         throw new RuntimeErrorException(var6, var6.toString());
      } catch (Throwable var7) {
         throw new RuntimeException("Unexpected exception", var7);
      }
   }

   private static void rethrowMaybeMBeanException(Throwable var0) throws ReflectionException, MBeanException {
      if (var0 instanceof MBeanException) {
         throw (MBeanException)var0;
      } else {
         rethrow(var0);
      }
   }

   private ObjectInstance registerObject(String var1, Object var2, ObjectName var3) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      if (var2 == null) {
         IllegalArgumentException var5 = new IllegalArgumentException("Cannot add null object");
         throw new RuntimeOperationsException(var5, "Exception occurred trying to register the MBean");
      } else {
         DynamicMBean var4 = Introspector.makeDynamicMBean(var2);
         return this.registerDynamicMBean(var1, var4, var3);
      }
   }

   private ObjectInstance registerDynamicMBean(String var1, DynamicMBean var2, ObjectName var3) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      var3 = this.nonDefaultDomain(var3);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "registerMBean", "ObjectName = " + var3);
      }

      ObjectName var4 = preRegister(var2, this.server, var3);
      boolean var5 = false;
      boolean var6 = false;
      DefaultMBeanServerInterceptor.ResourceContext var7 = null;

      try {
         if (var2 instanceof DynamicMBean2) {
            try {
               ((DynamicMBean2)var2).preRegister2(this.server, var4);
               var6 = true;
            } catch (Exception var25) {
               if (var25 instanceof RuntimeException) {
                  throw (RuntimeException)var25;
               }

               if (var25 instanceof InstanceAlreadyExistsException) {
                  throw (InstanceAlreadyExistsException)var25;
               }

               throw new RuntimeException(var25);
            }
         }

         if (var4 != var3 && var4 != null) {
            var4 = ObjectName.getInstance(this.nonDefaultDomain(var4));
         }

         checkMBeanPermission((String)var1, (String)null, var4, "registerMBean");
         if (var4 == null) {
            IllegalArgumentException var27 = new IllegalArgumentException("No object name specified");
            throw new RuntimeOperationsException(var27, "Exception occurred trying to register the MBean");
         }

         Object var8 = getResource(var2);
         var7 = this.registerWithRepository(var8, var2, var4);
         var6 = false;
         var5 = true;
      } finally {
         try {
            postRegister(var4, var2, var5, var6);
         } finally {
            if (var5 && var7 != null) {
               var7.done();
            }

         }
      }

      return new ObjectInstance(var4, var1);
   }

   private static void throwMBeanRegistrationException(Throwable var0, String var1) throws MBeanRegistrationException {
      if (var0 instanceof RuntimeException) {
         throw new RuntimeMBeanException((RuntimeException)var0, "RuntimeException thrown " + var1);
      } else if (var0 instanceof Error) {
         throw new RuntimeErrorException((Error)var0, "Error thrown " + var1);
      } else if (var0 instanceof MBeanRegistrationException) {
         throw (MBeanRegistrationException)var0;
      } else if (var0 instanceof Exception) {
         throw new MBeanRegistrationException((Exception)var0, "Exception thrown " + var1);
      } else {
         throw new RuntimeException(var0);
      }
   }

   private static ObjectName preRegister(DynamicMBean var0, MBeanServer var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException {
      ObjectName var3 = null;

      try {
         if (var0 instanceof MBeanRegistration) {
            var3 = ((MBeanRegistration)var0).preRegister(var1, var2);
         }
      } catch (Throwable var5) {
         throwMBeanRegistrationException(var5, "in preRegister method");
      }

      return var3 != null ? var3 : var2;
   }

   private static void postRegister(ObjectName var0, DynamicMBean var1, boolean var2, boolean var3) {
      if (var3 && var1 instanceof DynamicMBean2) {
         ((DynamicMBean2)var1).registerFailed();
      }

      try {
         if (var1 instanceof MBeanRegistration) {
            ((MBeanRegistration)var1).postRegister(var2);
         }

      } catch (RuntimeException var5) {
         JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + var0 + "]: Exception thrown by postRegister: rethrowing <" + var5 + ">, but keeping the MBean registered");
         throw new RuntimeMBeanException(var5, "RuntimeException thrown in postRegister method: rethrowing <" + var5 + ">, but keeping the MBean registered");
      } catch (Error var6) {
         JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + var0 + "]: Error thrown by postRegister: rethrowing <" + var6 + ">, but keeping the MBean registered");
         throw new RuntimeErrorException(var6, "Error thrown in postRegister method: rethrowing <" + var6 + ">, but keeping the MBean registered");
      }
   }

   private static void preDeregisterInvoke(MBeanRegistration var0) throws MBeanRegistrationException {
      try {
         var0.preDeregister();
      } catch (Throwable var2) {
         throwMBeanRegistrationException(var2, "in preDeregister method");
      }

   }

   private static void postDeregisterInvoke(ObjectName var0, MBeanRegistration var1) {
      try {
         var1.postDeregister();
      } catch (RuntimeException var3) {
         JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + var0 + "]: Exception thrown by postDeregister: rethrowing <" + var3 + ">, although the MBean is succesfully unregistered");
         throw new RuntimeMBeanException(var3, "RuntimeException thrown in postDeregister method: rethrowing <" + var3 + ">, although the MBean is sucessfully unregistered");
      } catch (Error var4) {
         JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + var0 + "]: Error thrown by postDeregister: rethrowing <" + var4 + ">, although the MBean is succesfully unregistered");
         throw new RuntimeErrorException(var4, "Error thrown in postDeregister method: rethrowing <" + var4 + ">, although the MBean is sucessfully unregistered");
      }
   }

   private DynamicMBean getMBean(ObjectName var1) throws InstanceNotFoundException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to get an MBean");
      } else {
         DynamicMBean var2 = this.repository.retrieve(var1);
         if (var2 == null) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getMBean", var1 + " : Found no object");
            }

            throw new InstanceNotFoundException(var1.toString());
         } else {
            return var2;
         }
      }
   }

   private static Object getResource(DynamicMBean var0) {
      return var0 instanceof DynamicMBean2 ? ((DynamicMBean2)var0).getResource() : var0;
   }

   private ObjectName nonDefaultDomain(ObjectName var1) {
      if (var1 != null && var1.getDomain().length() <= 0) {
         String var2 = this.domain + var1;
         return Util.newObjectName(var2);
      } else {
         return var1;
      }
   }

   public String getDefaultDomain() {
      return this.domain;
   }

   public void addNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + var1);
      }

      DynamicMBean var5 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var5, (String)null, var1, "addNotificationListener");
      NotificationBroadcaster var6 = getNotificationBroadcaster(var1, var5, NotificationBroadcaster.class);
      if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("Null listener"), "Null listener");
      } else {
         NotificationListener var7 = this.getListenerWrapper(var2, var1, var5, true);
         var6.addNotificationListener(var7, var3, var4);
      }
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      DynamicMBean var5 = this.getMBean(var2);
      Object var6 = getResource(var5);
      if (!(var6 instanceof NotificationListener)) {
         throw new RuntimeOperationsException(new IllegalArgumentException(var2.getCanonicalName()), "The MBean " + var2.getCanonicalName() + "does not implement the NotificationListener interface");
      } else {
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + var1 + ", Listener = " + var2);
         }

         this.server.addNotificationListener(var1, (NotificationListener)var6, var3, var4);
      }
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.removeNotificationListener(var1, var2, (NotificationFilter)null, (Object)null, true);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.removeNotificationListener(var1, var2, var3, var4, false);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2) throws InstanceNotFoundException, ListenerNotFoundException {
      NotificationListener var3 = this.getListener(var2);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + var1 + ", Listener = " + var2);
      }

      this.server.removeNotificationListener(var1, var3);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      NotificationListener var5 = this.getListener(var2);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + var1 + ", Listener = " + var2);
      }

      this.server.removeNotificationListener(var1, var5, var3, var4);
   }

   private NotificationListener getListener(ObjectName var1) throws ListenerNotFoundException {
      DynamicMBean var2;
      try {
         var2 = this.getMBean(var1);
      } catch (InstanceNotFoundException var6) {
         throw (ListenerNotFoundException)EnvHelp.initCause(new ListenerNotFoundException(var6.getMessage()), var6);
      }

      Object var3 = getResource(var2);
      if (!(var3 instanceof NotificationListener)) {
         IllegalArgumentException var4 = new IllegalArgumentException(var1.getCanonicalName());
         String var5 = "MBean " + var1.getCanonicalName() + " does not implement " + NotificationListener.class.getName();
         throw new RuntimeOperationsException(var4, var5);
      } else {
         return (NotificationListener)var3;
      }
   }

   private void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4, boolean var5) throws InstanceNotFoundException, ListenerNotFoundException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + var1);
      }

      DynamicMBean var6 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var6, (String)null, var1, "removeNotificationListener");
      Class var7 = var5 ? NotificationBroadcaster.class : NotificationEmitter.class;
      NotificationBroadcaster var8 = getNotificationBroadcaster(var1, var6, var7);
      NotificationListener var9 = this.getListenerWrapper(var2, var1, var6, false);
      if (var9 == null) {
         throw new ListenerNotFoundException("Unknown listener");
      } else {
         if (var5) {
            var8.removeNotificationListener(var9);
         } else {
            NotificationEmitter var10 = (NotificationEmitter)var8;
            var10.removeNotificationListener(var9, var3, var4);
         }

      }
   }

   private static <T extends NotificationBroadcaster> T getNotificationBroadcaster(ObjectName var0, Object var1, Class<T> var2) {
      if (var2.isInstance(var1)) {
         return (NotificationBroadcaster)var2.cast(var1);
      } else {
         if (var1 instanceof DynamicMBean2) {
            var1 = ((DynamicMBean2)var1).getResource();
         }

         if (var2.isInstance(var1)) {
            return (NotificationBroadcaster)var2.cast(var1);
         } else {
            IllegalArgumentException var3 = new IllegalArgumentException(var0.getCanonicalName());
            String var4 = "MBean " + var0.getCanonicalName() + " does not implement " + var2.getName();
            throw new RuntimeOperationsException(var3, var4);
         }
      }
   }

   public MBeanInfo getMBeanInfo(ObjectName var1) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
      DynamicMBean var2 = this.getMBean(var1);

      MBeanInfo var3;
      try {
         var3 = var2.getMBeanInfo();
      } catch (RuntimeMBeanException var5) {
         throw var5;
      } catch (RuntimeErrorException var6) {
         throw var6;
      } catch (RuntimeException var7) {
         throw new RuntimeMBeanException(var7, "getMBeanInfo threw RuntimeException");
      } catch (Error var8) {
         throw new RuntimeErrorException(var8, "getMBeanInfo threw Error");
      }

      if (var3 == null) {
         throw new JMRuntimeException("MBean " + var1 + "has no MBeanInfo");
      } else {
         checkMBeanPermission((String)var3.getClassName(), (String)null, var1, "getMBeanInfo");
         return var3;
      }
   }

   public boolean isInstanceOf(ObjectName var1, String var2) throws InstanceNotFoundException {
      DynamicMBean var3 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var3, (String)null, var1, "isInstanceOf");

      try {
         Object var4 = getResource(var3);
         String var5 = var4 instanceof DynamicMBean ? getClassName((DynamicMBean)var4) : var4.getClass().getName();
         if (var5.equals(var2)) {
            return true;
         } else {
            ClassLoader var6 = var4.getClass().getClassLoader();
            Class var7 = Class.forName(var2, false, var6);
            if (var7.isInstance(var4)) {
               return true;
            } else {
               Class var8 = Class.forName(var5, false, var6);
               return var7.isAssignableFrom(var8);
            }
         }
      } catch (Exception var9) {
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "isInstanceOf", (String)"Exception calling isInstanceOf", (Throwable)var9);
         }

         return false;
      }
   }

   public ClassLoader getClassLoaderFor(ObjectName var1) throws InstanceNotFoundException {
      DynamicMBean var2 = this.getMBean(var1);
      checkMBeanPermission((DynamicMBean)var2, (String)null, var1, "getClassLoaderFor");
      return getResource(var2).getClass().getClassLoader();
   }

   public ClassLoader getClassLoader(ObjectName var1) throws InstanceNotFoundException {
      if (var1 == null) {
         checkMBeanPermission((String)((String)null), (String)null, (ObjectName)null, "getClassLoader");
         return this.server.getClass().getClassLoader();
      } else {
         DynamicMBean var2 = this.getMBean(var1);
         checkMBeanPermission((DynamicMBean)var2, (String)null, var1, "getClassLoader");
         Object var3 = getResource(var2);
         if (!(var3 instanceof ClassLoader)) {
            throw new InstanceNotFoundException(var1.toString() + " is not a classloader");
         } else {
            return (ClassLoader)var3;
         }
      }
   }

   private void sendNotification(String var1, ObjectName var2) {
      MBeanServerNotification var3 = new MBeanServerNotification(var1, MBeanServerDelegate.DELEGATE_NAME, 0L, var2);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "sendNotification", var1 + " " + var2);
      }

      this.delegate.sendNotification(var3);
   }

   private Set<ObjectName> objectNamesFromFilteredNamedObjects(Set<NamedObject> var1, QueryExp var2) {
      HashSet var3 = new HashSet();
      if (var2 == null) {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            NamedObject var5 = (NamedObject)var4.next();
            var3.add(var5.getName());
         }
      } else {
         MBeanServer var14 = QueryEval.getMBeanServer();
         var2.setMBeanServer(this.server);

         try {
            Iterator var15 = var1.iterator();

            while(var15.hasNext()) {
               NamedObject var6 = (NamedObject)var15.next();

               boolean var7;
               try {
                  var7 = var2.apply(var6.getName());
               } catch (Exception var12) {
                  var7 = false;
               }

               if (var7) {
                  var3.add(var6.getName());
               }
            }
         } finally {
            var2.setMBeanServer(var14);
         }
      }

      return var3;
   }

   private Set<ObjectInstance> objectInstancesFromFilteredNamedObjects(Set<NamedObject> var1, QueryExp var2) {
      HashSet var3 = new HashSet();
      if (var2 == null) {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            NamedObject var5 = (NamedObject)var4.next();
            DynamicMBean var6 = var5.getObject();
            String var7 = safeGetClassName(var6);
            var3.add(new ObjectInstance(var5.getName(), var7));
         }
      } else {
         MBeanServer var15 = QueryEval.getMBeanServer();
         var2.setMBeanServer(this.server);

         try {
            Iterator var16 = var1.iterator();

            while(var16.hasNext()) {
               NamedObject var17 = (NamedObject)var16.next();
               DynamicMBean var18 = var17.getObject();

               boolean var8;
               try {
                  var8 = var2.apply(var17.getName());
               } catch (Exception var13) {
                  var8 = false;
               }

               if (var8) {
                  String var9 = safeGetClassName(var18);
                  var3.add(new ObjectInstance(var17.getName(), var9));
               }
            }
         } finally {
            var2.setMBeanServer(var15);
         }
      }

      return var3;
   }

   private static String safeGetClassName(DynamicMBean var0) {
      try {
         return getClassName(var0);
      } catch (Exception var2) {
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "safeGetClassName", (String)"Exception getting MBean class name", (Throwable)var2);
         }

         return null;
      }
   }

   private Set<ObjectInstance> filterListOfObjectInstances(Set<ObjectInstance> var1, QueryExp var2) {
      if (var2 == null) {
         return var1;
      } else {
         HashSet var3 = new HashSet();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            ObjectInstance var5 = (ObjectInstance)var4.next();
            boolean var6 = false;
            MBeanServer var7 = QueryEval.getMBeanServer();
            var2.setMBeanServer(this.server);

            try {
               var6 = var2.apply(var5.getObjectName());
            } catch (Exception var12) {
               var6 = false;
            } finally {
               var2.setMBeanServer(var7);
            }

            if (var6) {
               var3.add(var5);
            }
         }

         return var3;
      }
   }

   private NotificationListener getListenerWrapper(NotificationListener var1, ObjectName var2, DynamicMBean var3, boolean var4) {
      Object var5 = getResource(var3);
      DefaultMBeanServerInterceptor.ListenerWrapper var6 = new DefaultMBeanServerInterceptor.ListenerWrapper(var1, var2, var5);
      synchronized(this.listenerWrappers) {
         WeakReference var8 = (WeakReference)this.listenerWrappers.get(var6);
         if (var8 != null) {
            NotificationListener var9 = (NotificationListener)var8.get();
            if (var9 != null) {
               return var9;
            }
         }

         if (var4) {
            var8 = new WeakReference(var6);
            this.listenerWrappers.put(var6, var8);
            return var6;
         } else {
            return null;
         }
      }
   }

   public Object instantiate(String var1) throws ReflectionException, MBeanException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public Object instantiate(String var1, ObjectName var2) throws ReflectionException, MBeanException, InstanceNotFoundException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public Object instantiate(String var1, Object[] var2, String[] var3) throws ReflectionException, MBeanException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, MBeanException, InstanceNotFoundException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public ObjectInputStream deserialize(ObjectName var1, byte[] var2) throws InstanceNotFoundException, OperationsException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public ObjectInputStream deserialize(String var1, byte[] var2) throws OperationsException, ReflectionException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3) throws InstanceNotFoundException, OperationsException, ReflectionException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public ClassLoaderRepository getClassLoaderRepository() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   private static String getClassName(DynamicMBean var0) {
      return var0 instanceof DynamicMBean2 ? ((DynamicMBean2)var0).getClassName() : var0.getMBeanInfo().getClassName();
   }

   private static void checkMBeanPermission(DynamicMBean var0, String var1, ObjectName var2, String var3) {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         checkMBeanPermission(safeGetClassName(var0), var1, var2, var3);
      }

   }

   private static void checkMBeanPermission(String var0, String var1, ObjectName var2, String var3) {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         MBeanPermission var5 = new MBeanPermission(var0, var1, var2, var3);
         var4.checkPermission(var5);
      }

   }

   private static void checkMBeanTrustPermission(final Class<?> var0) throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         MBeanTrustPermission var2 = new MBeanTrustPermission("register");
         PrivilegedAction var3 = new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
               return var0.getProtectionDomain();
            }
         };
         ProtectionDomain var4 = (ProtectionDomain)AccessController.doPrivileged(var3);
         AccessControlContext var5 = new AccessControlContext(new ProtectionDomain[]{var4});
         var1.checkPermission(var2, var5);
      }

   }

   private DefaultMBeanServerInterceptor.ResourceContext registerWithRepository(Object var1, DynamicMBean var2, ObjectName var3) throws InstanceAlreadyExistsException, MBeanRegistrationException {
      DefaultMBeanServerInterceptor.ResourceContext var4 = this.makeResourceContextFor(var1, var3);
      this.repository.addMBean(var2, var3, var4);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addObject", "Send create notification of object " + var3.getCanonicalName());
      }

      this.sendNotification("JMX.mbean.registered", var3);
      return var4;
   }

   private DefaultMBeanServerInterceptor.ResourceContext unregisterFromRepository(Object var1, DynamicMBean var2, ObjectName var3) throws InstanceNotFoundException {
      DefaultMBeanServerInterceptor.ResourceContext var4 = this.makeResourceContextFor(var1, var3);
      this.repository.remove(var3, var4);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "unregisterMBean", "Send delete notification of object " + var3.getCanonicalName());
      }

      this.sendNotification("JMX.mbean.unregistered", var3);
      return var4;
   }

   private void addClassLoader(ClassLoader var1, ObjectName var2) {
      ModifiableClassLoaderRepository var3 = this.getInstantiatorCLR();
      if (var3 == null) {
         IllegalArgumentException var4 = new IllegalArgumentException("Dynamic addition of class loaders is not supported");
         throw new RuntimeOperationsException(var4, "Exception occurred trying to register the MBean as a class loader");
      } else {
         var3.addClassLoader(var2, var1);
      }
   }

   private void removeClassLoader(ClassLoader var1, ObjectName var2) {
      if (var1 != this.server.getClass().getClassLoader()) {
         ModifiableClassLoaderRepository var3 = this.getInstantiatorCLR();
         if (var3 != null) {
            var3.removeClassLoader(var2);
         }
      }

   }

   private DefaultMBeanServerInterceptor.ResourceContext createClassLoaderContext(final ClassLoader var1, final ObjectName var2) {
      return new DefaultMBeanServerInterceptor.ResourceContext() {
         public void registering() {
            DefaultMBeanServerInterceptor.this.addClassLoader(var1, var2);
         }

         public void unregistered() {
            DefaultMBeanServerInterceptor.this.removeClassLoader(var1, var2);
         }

         public void done() {
         }
      };
   }

   private DefaultMBeanServerInterceptor.ResourceContext makeResourceContextFor(Object var1, ObjectName var2) {
      return var1 instanceof ClassLoader ? this.createClassLoaderContext((ClassLoader)var1, var2) : DefaultMBeanServerInterceptor.ResourceContext.NONE;
   }

   private ModifiableClassLoaderRepository getInstantiatorCLR() {
      return (ModifiableClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ModifiableClassLoaderRepository>() {
         public ModifiableClassLoaderRepository run() {
            return DefaultMBeanServerInterceptor.this.instantiator != null ? DefaultMBeanServerInterceptor.this.instantiator.getClassLoaderRepository() : null;
         }
      });
   }

   private interface ResourceContext extends Repository.RegistrationContext {
      DefaultMBeanServerInterceptor.ResourceContext NONE = new DefaultMBeanServerInterceptor.ResourceContext() {
         public void done() {
         }

         public void registering() {
         }

         public void unregistered() {
         }
      };

      void done();
   }

   private static class ListenerWrapper implements NotificationListener {
      private NotificationListener listener;
      private ObjectName name;
      private Object mbean;

      ListenerWrapper(NotificationListener var1, ObjectName var2, Object var3) {
         this.listener = var1;
         this.name = var2;
         this.mbean = var3;
      }

      public void handleNotification(Notification var1, Object var2) {
         if (var1 != null && var1.getSource() == this.mbean) {
            var1.setSource(this.name);
         }

         this.listener.handleNotification(var1, var2);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof DefaultMBeanServerInterceptor.ListenerWrapper)) {
            return false;
         } else {
            DefaultMBeanServerInterceptor.ListenerWrapper var2 = (DefaultMBeanServerInterceptor.ListenerWrapper)var1;
            return var2.listener == this.listener && var2.mbean == this.mbean && var2.name.equals(this.name);
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.listener) ^ System.identityHashCode(this.mbean);
      }
   }
}
