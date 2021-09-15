package com.sun.management.jmx;

import java.io.ObjectInputStream;
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
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

/** @deprecated */
@Deprecated
public class MBeanServerImpl implements MBeanServer {
   private final MBeanServer server;

   public MBeanServerImpl() {
      this((String)null);
   }

   public MBeanServerImpl(String var1) {
      MBeanServerBuilder var2 = new MBeanServerBuilder();
      MBeanServerDelegate var3 = var2.newMBeanServerDelegate();
      this.server = var2.newMBeanServer(var1, (MBeanServer)null, var3);
   }

   public Object instantiate(String var1) throws ReflectionException, MBeanException {
      return this.server.instantiate(var1);
   }

   public Object instantiate(String var1, ObjectName var2) throws ReflectionException, MBeanException, InstanceNotFoundException {
      return this.server.instantiate(var1, var2);
   }

   public Object instantiate(String var1, Object[] var2, String[] var3) throws ReflectionException, MBeanException {
      return this.server.instantiate(var1, var2, var3);
   }

   public Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, MBeanException, InstanceNotFoundException {
      return this.server.instantiate(var1, var2, var3, var4);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return this.server.createMBean(var1, var2);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.server.createMBean(var1, var2, var3);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
      return this.server.createMBean(var1, var2, var3, var4);
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Object[] var4, String[] var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
      return this.server.createMBean(var1, var2, var3, var4, var5);
   }

   public ObjectInstance registerMBean(Object var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      return this.server.registerMBean(var1, var2);
   }

   public void unregisterMBean(ObjectName var1) throws InstanceNotFoundException, MBeanRegistrationException {
      this.server.unregisterMBean(var1);
   }

   public ObjectInstance getObjectInstance(ObjectName var1) throws InstanceNotFoundException {
      return this.server.getObjectInstance(var1);
   }

   public Set<ObjectInstance> queryMBeans(ObjectName var1, QueryExp var2) {
      return this.server.queryMBeans(var1, var2);
   }

   public Set<ObjectName> queryNames(ObjectName var1, QueryExp var2) {
      return this.server.queryNames(var1, var2);
   }

   public boolean isRegistered(ObjectName var1) {
      return this.server.isRegistered(var1);
   }

   public Integer getMBeanCount() {
      return this.server.getMBeanCount();
   }

   public Object getAttribute(ObjectName var1, String var2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
      return this.server.getAttribute(var1, var2);
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2) throws InstanceNotFoundException, ReflectionException {
      return this.server.getAttributes(var1, var2);
   }

   public void setAttribute(ObjectName var1, Attribute var2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      this.server.setAttribute(var1, var2);
   }

   public AttributeList setAttributes(ObjectName var1, AttributeList var2) throws InstanceNotFoundException, ReflectionException {
      return this.server.setAttributes(var1, var2);
   }

   public Object invoke(ObjectName var1, String var2, Object[] var3, String[] var4) throws InstanceNotFoundException, MBeanException, ReflectionException {
      return this.server.invoke(var1, var2, var3, var4);
   }

   public String getDefaultDomain() {
      return this.server.getDefaultDomain();
   }

   public String[] getDomains() {
      return this.server.getDomains();
   }

   public void addNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.server.addNotificationListener(var1, var2, var3, var4);
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException {
      this.server.addNotificationListener(var1, var2, var3, var4);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.server.removeNotificationListener(var1, var2);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2) throws InstanceNotFoundException, ListenerNotFoundException {
      this.server.removeNotificationListener(var1, var2);
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.server.removeNotificationListener(var1, var2, var3, var4);
   }

   public void removeNotificationListener(ObjectName var1, NotificationListener var2, NotificationFilter var3, Object var4) throws InstanceNotFoundException, ListenerNotFoundException {
      this.server.removeNotificationListener(var1, var2, var3, var4);
   }

   public MBeanInfo getMBeanInfo(ObjectName var1) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
      return this.server.getMBeanInfo(var1);
   }

   public boolean isInstanceOf(ObjectName var1, String var2) throws InstanceNotFoundException {
      return this.server.isInstanceOf(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(ObjectName var1, byte[] var2) throws InstanceNotFoundException, OperationsException {
      return this.server.deserialize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, byte[] var2) throws OperationsException, ReflectionException {
      return this.server.deserialize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3) throws InstanceNotFoundException, OperationsException, ReflectionException {
      return this.server.deserialize(var1, var2, var3);
   }

   public ClassLoader getClassLoaderFor(ObjectName var1) throws InstanceNotFoundException {
      return this.server.getClassLoaderFor(var1);
   }

   public ClassLoader getClassLoader(ObjectName var1) throws InstanceNotFoundException {
      return this.server.getClassLoader(var1);
   }

   public ClassLoaderRepository getClassLoaderRepository() {
      return this.server.getClassLoaderRepository();
   }
}
