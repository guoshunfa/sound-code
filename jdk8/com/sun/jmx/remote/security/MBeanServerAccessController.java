package com.sun.jmx.remote.security;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
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
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;
import javax.management.remote.MBeanServerForwarder;

public abstract class MBeanServerAccessController implements MBeanServerForwarder {
   private MBeanServer mbs;

   public MBeanServer getMBeanServer() {
      return this.mbs;
   }

   public void setMBeanServer(MBeanServer var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null MBeanServer");
      } else if (this.mbs != null) {
         throw new IllegalArgumentException("MBeanServer object already initialized");
      } else {
         this.mbs = var1;
      }
   }

   protected abstract void checkRead();

   protected abstract void checkWrite();

   protected void checkCreate(String var1) {
      this.checkWrite();
   }

   protected void checkUnregister(ObjectName var1) {
      this.checkWrite();
   }

   public void addNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.checkRead();
      this.getMBeanServer().addNotificationListener(var1, var2, var3, var4);
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.checkRead();
      this.getMBeanServer().addNotificationListener(var1, var2, var3, var4);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      this.checkCreate(var1);
      SecurityManager var3 = System.getSecurityManager();
      if (var3 == null) {
         Object var4 = this.getMBeanServer().instantiate(var1);
         this.checkClassLoader(var4);
         return this.getMBeanServer().registerMBean(var4, var2);
      } else {
         return this.getMBeanServer().createMBean(var1, var2);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      this.checkCreate(var1);
      SecurityManager var5 = System.getSecurityManager();
      if (var5 == null) {
         Object var6 = this.getMBeanServer().instantiate(var1, var3, var4);
         this.checkClassLoader(var6);
         return this.getMBeanServer().registerMBean(var6, var2);
      } else {
         return this.getMBeanServer().createMBean(var1, var2, var3, var4);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      this.checkCreate(var1);
      SecurityManager var4 = System.getSecurityManager();
      if (var4 == null) {
         Object var5 = this.getMBeanServer().instantiate(var1, var3);
         this.checkClassLoader(var5);
         return this.getMBeanServer().registerMBean(var5, var2);
      } else {
         return this.getMBeanServer().createMBean(var1, var2, var3);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Object[] var4, String[] var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      this.checkCreate(var1);
      SecurityManager var6 = System.getSecurityManager();
      if (var6 == null) {
         Object var7 = this.getMBeanServer().instantiate(var1, var3, var4, var5);
         this.checkClassLoader(var7);
         return this.getMBeanServer().registerMBean(var7, var2);
      } else {
         return this.getMBeanServer().createMBean(var1, var2, var3, var4, var5);
      }
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(ObjectName var1, byte[] var2) throws InstanceNotFoundException, OperationsException {
      this.checkRead();
      return this.getMBeanServer().deserialize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, byte[] var2) throws OperationsException, ReflectionException {
      this.checkRead();
      return this.getMBeanServer().deserialize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3) throws InstanceNotFoundException, OperationsException, ReflectionException {
      this.checkRead();
      return this.getMBeanServer().deserialize(var1, var2, var3);
   }

   public Object getAttribute(ObjectName var1, String var2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      this.checkRead();
      return this.getMBeanServer().getAttribute(var1, var2);
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2) throws InstanceNotFoundException, ReflectionException {
      this.checkRead();
      return this.getMBeanServer().getAttributes(var1, var2);
   }

   public ClassLoader getClassLoader(ObjectName var1) throws InstanceNotFoundException {
      this.checkRead();
      return this.getMBeanServer().getClassLoader(var1);
   }

   public ClassLoader getClassLoaderFor(ObjectName var1) throws InstanceNotFoundException {
      this.checkRead();
      return this.getMBeanServer().getClassLoaderFor(var1);
   }

   public ClassLoaderRepository getClassLoaderRepository() {
      this.checkRead();
      return this.getMBeanServer().getClassLoaderRepository();
   }

   public String getDefaultDomain() {
      this.checkRead();
      return this.getMBeanServer().getDefaultDomain();
   }

   public String[] getDomains() {
      this.checkRead();
      return this.getMBeanServer().getDomains();
   }

   public Integer getMBeanCount() {
      this.checkRead();
      return this.getMBeanServer().getMBeanCount();
   }

   public MBeanInfo getMBeanInfo(ObjectName var1) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
      this.checkRead();
      return this.getMBeanServer().getMBeanInfo(var1);
   }

   public ObjectInstance getObjectInstance(ObjectName var1) throws InstanceNotFoundException {
      this.checkRead();
      return this.getMBeanServer().getObjectInstance(var1);
   }

   public Object instantiate(String var1) throws ReflectionException, MBeanException {
      this.checkCreate(var1);
      return this.getMBeanServer().instantiate(var1);
   }

   public Object instantiate(String var1, Object[] var2, String[] var3) throws ReflectionException, MBeanException {
      this.checkCreate(var1);
      return this.getMBeanServer().instantiate(var1, var2, var3);
   }

   public Object instantiate(String var1, ObjectName var2) throws ReflectionException, MBeanException, InstanceNotFoundException {
      this.checkCreate(var1);
      return this.getMBeanServer().instantiate(var1, var2);
   }

   public Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, MBeanException, InstanceNotFoundException {
      this.checkCreate(var1);
      return this.getMBeanServer().instantiate(var1, var2, var3, var4);
   }

   public Object invoke(ObjectName var1, String var2, Object[] var3, String[] var4) throws InstanceNotFoundException, MBeanException, ReflectionException {
      this.checkWrite();
      this.checkMLetMethods(var1, var2);
      return this.getMBeanServer().invoke(var1, var2, var3, var4);
   }

   public boolean isInstanceOf(ObjectName var1, String var2) throws InstanceNotFoundException {
      this.checkRead();
      return this.getMBeanServer().isInstanceOf(var1, var2);
   }

   public boolean isRegistered(ObjectName var1) {
      this.checkRead();
      return this.getMBeanServer().isRegistered(var1);
   }

   public Set<ObjectInstance> queryMBeans(ObjectName var1, QueryExp var2) {
      this.checkRead();
      return this.getMBeanServer().queryMBeans(var1, var2);
   }

   public Set<ObjectName> queryNames(ObjectName var1, QueryExp var2) {
      this.checkRead();
      return this.getMBeanServer().queryNames(var1, var2);
   }

   public ObjectInstance registerMBean(Object var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      this.checkWrite();
      return this.getMBeanServer().registerMBean(var1, var2);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.checkRead();
      this.getMBeanServer().removeNotificationListener(var1, var2);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.checkRead();
      this.getMBeanServer().removeNotificationListener(var1, var2, var3, var4);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.checkRead();
      this.getMBeanServer().removeNotificationListener(var1, var2);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.checkRead();
      this.getMBeanServer().removeNotificationListener(var1, var2, var3, var4);
   }

   public void setAttribute(ObjectName var1, Attribute var2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      this.checkWrite();
      this.getMBeanServer().setAttribute(var1, var2);
   }

   public AttributeList setAttributes(ObjectName var1, AttributeList var2) throws InstanceNotFoundException, ReflectionException {
      this.checkWrite();
      return this.getMBeanServer().setAttributes(var1, var2);
   }

   public void unregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException {
      this.checkUnregister(var1);
      this.getMBeanServer().unregisterMBean(var1);
   }

   private void checkClassLoader(Object var1) {
      if (var1 instanceof ClassLoader) {
         throw new SecurityException("Access denied! Creating an MBean that is a ClassLoader is forbidden unless a security manager is installed.");
      }
   }

   private void checkMLetMethods(ObjectName var1, String var2) throws InstanceNotFoundException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 == null) {
         if (var2.equals("addURL") || var2.equals("getMBeansFromURL")) {
            if (this.getMBeanServer().isInstanceOf(var1, "javax.management.loading.MLet")) {
               if (var2.equals("addURL")) {
                  throw new SecurityException("Access denied! MLet method addURL cannot be invoked unless a security manager is installed.");
               } else {
                  GetPropertyAction var5 = new GetPropertyAction("jmx.remote.x.mlet.allow.getMBeansFromURL");
                  String var6 = (String)AccessController.doPrivileged((PrivilegedAction)var5);
                  boolean var7 = "true".equalsIgnoreCase(var6);
                  if (!var7) {
                     throw new SecurityException("Access denied! MLet method getMBeansFromURL cannot be invoked unless a security manager is installed or the system property -Djmx.remote.x.mlet.allow.getMBeansFromURL=true is specified.");
                  }
               }
            }
         }
      }
   }
}
