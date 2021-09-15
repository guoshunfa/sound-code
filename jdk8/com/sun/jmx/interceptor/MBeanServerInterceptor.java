package com.sun.jmx.interceptor;

import java.io.ObjectInputStream;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

public interface MBeanServerInterceptor extends MBeanServer {
   Object instantiate(String var1) throws ReflectionException, MBeanException;

   Object instantiate(String var1, ObjectName var2) throws ReflectionException, MBeanException, InstanceNotFoundException;

   Object instantiate(String var1, Object[] var2, String[] var3) throws ReflectionException, MBeanException;

   Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4) throws ReflectionException, MBeanException, InstanceNotFoundException;

   /** @deprecated */
   @Deprecated
   ObjectInputStream deserialize(ObjectName var1, byte[] var2) throws InstanceNotFoundException, OperationsException;

   /** @deprecated */
   @Deprecated
   ObjectInputStream deserialize(String var1, byte[] var2) throws OperationsException, ReflectionException;

   /** @deprecated */
   @Deprecated
   ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3) throws InstanceNotFoundException, OperationsException, ReflectionException;

   ClassLoaderRepository getClassLoaderRepository();
}
