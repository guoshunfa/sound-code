package com.sun.jmx.mbeanserver;

import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

public abstract class MBeanSupport<M> implements DynamicMBean2, MBeanRegistration {
   private final MBeanInfo mbeanInfo;
   private final Object resource;
   private final PerInterface<M> perInterface;

   <T> MBeanSupport(T var1, Class<T> var2) throws NotCompliantMBeanException {
      if (var2 == null) {
         throw new NotCompliantMBeanException("Null MBean interface");
      } else if (!var2.isInstance(var1)) {
         String var4 = "Resource class " + var1.getClass().getName() + " is not an instance of " + var2.getName();
         throw new NotCompliantMBeanException(var4);
      } else {
         ReflectUtil.checkPackageAccess(var2);
         this.resource = var1;
         MBeanIntrospector var3 = this.getMBeanIntrospector();
         this.perInterface = var3.getPerInterface(var2);
         this.mbeanInfo = var3.getMBeanInfo(var1, this.perInterface);
      }
   }

   abstract MBeanIntrospector<M> getMBeanIntrospector();

   abstract Object getCookie();

   public final boolean isMXBean() {
      return this.perInterface.isMXBean();
   }

   public abstract void register(MBeanServer var1, ObjectName var2) throws Exception;

   public abstract void unregister();

   public final ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      if (this.resource instanceof MBeanRegistration) {
         var2 = ((MBeanRegistration)this.resource).preRegister(var1, var2);
      }

      return var2;
   }

   public final void preRegister2(MBeanServer var1, ObjectName var2) throws Exception {
      this.register(var1, var2);
   }

   public final void registerFailed() {
      this.unregister();
   }

   public final void postRegister(Boolean var1) {
      if (this.resource instanceof MBeanRegistration) {
         ((MBeanRegistration)this.resource).postRegister(var1);
      }

   }

   public final void preDeregister() throws Exception {
      if (this.resource instanceof MBeanRegistration) {
         ((MBeanRegistration)this.resource).preDeregister();
      }

   }

   public final void postDeregister() {
      try {
         this.unregister();
      } finally {
         if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).postDeregister();
         }

      }

   }

   public final Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException {
      return this.perInterface.getAttribute(this.resource, var1, this.getCookie());
   }

   public final AttributeList getAttributes(String[] var1) {
      AttributeList var2 = new AttributeList(var1.length);
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];

         try {
            Object var7 = this.getAttribute(var6);
            var2.add(new Attribute(var6, var7));
         } catch (Exception var8) {
         }
      }

      return var2;
   }

   public final void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      String var2 = var1.getName();
      Object var3 = var1.getValue();
      this.perInterface.setAttribute(this.resource, var2, var3, this.getCookie());
   }

   public final AttributeList setAttributes(AttributeList var1) {
      AttributeList var2 = new AttributeList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         Attribute var5 = (Attribute)var4;

         try {
            this.setAttribute(var5);
            var2.add(new Attribute(var5.getName(), var5.getValue()));
         } catch (Exception var7) {
         }
      }

      return var2;
   }

   public final Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException {
      return this.perInterface.invoke(this.resource, var1, var2, var3, this.getCookie());
   }

   public MBeanInfo getMBeanInfo() {
      return this.mbeanInfo;
   }

   public final String getClassName() {
      return this.resource.getClass().getName();
   }

   public final Object getResource() {
      return this.resource;
   }

   public final Class<?> getMBeanInterface() {
      return this.perInterface.getMBeanInterface();
   }
}
