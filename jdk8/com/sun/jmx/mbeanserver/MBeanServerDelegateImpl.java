package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

final class MBeanServerDelegateImpl extends MBeanServerDelegate implements DynamicMBean, MBeanRegistration {
   private static final String[] attributeNames = new String[]{"MBeanServerId", "SpecificationName", "SpecificationVersion", "SpecificationVendor", "ImplementationName", "ImplementationVersion", "ImplementationVendor"};
   private static final MBeanAttributeInfo[] attributeInfos = new MBeanAttributeInfo[]{new MBeanAttributeInfo("MBeanServerId", "java.lang.String", "The MBean server agent identification", true, false, false), new MBeanAttributeInfo("SpecificationName", "java.lang.String", "The full name of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVersion", "java.lang.String", "The version of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVendor", "java.lang.String", "The vendor of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("ImplementationName", "java.lang.String", "The JMX implementation name (the name of this product)", true, false, false), new MBeanAttributeInfo("ImplementationVersion", "java.lang.String", "The JMX implementation version (the version of this product).", true, false, false), new MBeanAttributeInfo("ImplementationVendor", "java.lang.String", "the JMX implementation vendor (the vendor of this product).", true, false, false)};
   private final MBeanInfo delegateInfo;

   public MBeanServerDelegateImpl() {
      this.delegateInfo = new MBeanInfo("javax.management.MBeanServerDelegate", "Represents  the MBean server from the management point of view.", attributeInfos, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])null, this.getNotificationInfo());
   }

   public final ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      return var2 == null ? DELEGATE_NAME : var2;
   }

   public final void postRegister(Boolean var1) {
   }

   public final void preDeregister() throws Exception {
      throw new IllegalArgumentException("The MBeanServerDelegate MBean cannot be unregistered");
   }

   public final void postDeregister() {
   }

   public Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException {
      try {
         if (var1 == null) {
            throw new AttributeNotFoundException("null");
         } else if (var1.equals("MBeanServerId")) {
            return this.getMBeanServerId();
         } else if (var1.equals("SpecificationName")) {
            return this.getSpecificationName();
         } else if (var1.equals("SpecificationVersion")) {
            return this.getSpecificationVersion();
         } else if (var1.equals("SpecificationVendor")) {
            return this.getSpecificationVendor();
         } else if (var1.equals("ImplementationName")) {
            return this.getImplementationName();
         } else if (var1.equals("ImplementationVersion")) {
            return this.getImplementationVersion();
         } else if (var1.equals("ImplementationVendor")) {
            return this.getImplementationVendor();
         } else {
            throw new AttributeNotFoundException("null");
         }
      } catch (AttributeNotFoundException var3) {
         throw var3;
      } catch (JMRuntimeException var4) {
         throw var4;
      } catch (SecurityException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new MBeanException(var6, "Failed to get " + var1);
      }
   }

   public void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      String var2 = var1 == null ? null : var1.getName();
      if (var2 == null) {
         IllegalArgumentException var3 = new IllegalArgumentException("Attribute name cannot be null");
         throw new RuntimeOperationsException(var3, "Exception occurred trying to invoke the setter on the MBean");
      } else {
         this.getAttribute(var2);
         throw new AttributeNotFoundException(var2 + " not accessible");
      }
   }

   public AttributeList getAttributes(String[] var1) {
      String[] var2 = var1 == null ? attributeNames : var1;
      int var3 = var2.length;
      AttributeList var4 = new AttributeList(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         try {
            Attribute var6 = new Attribute(var2[var5], this.getAttribute(var2[var5]));
            var4.add(var6);
         } catch (Exception var7) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerDelegateImpl.class.getName(), "getAttributes", "Attribute " + var2[var5] + " not found");
            }
         }
      }

      return var4;
   }

   public AttributeList setAttributes(AttributeList var1) {
      return new AttributeList(0);
   }

   public Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException {
      if (var1 == null) {
         IllegalArgumentException var4 = new IllegalArgumentException("Operation name  cannot be null");
         throw new RuntimeOperationsException(var4, "Exception occurred trying to invoke the operation on the MBean");
      } else {
         throw new ReflectionException(new NoSuchMethodException(var1), "The operation with name " + var1 + " could not be found");
      }
   }

   public MBeanInfo getMBeanInfo() {
      return this.delegateInfo;
   }
}
