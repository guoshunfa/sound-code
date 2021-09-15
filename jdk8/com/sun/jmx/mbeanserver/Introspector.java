package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeData;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class Introspector {
   public static final boolean ALLOW_NONPUBLIC_MBEAN;

   private Introspector() {
   }

   public static final boolean isDynamic(Class<?> var0) {
      return DynamicMBean.class.isAssignableFrom(var0);
   }

   public static void testCreation(Class<?> var0) throws NotCompliantMBeanException {
      int var1 = var0.getModifiers();
      if (!Modifier.isAbstract(var1) && !Modifier.isInterface(var1)) {
         Constructor[] var2 = var0.getConstructors();
         if (var2.length == 0) {
            throw new NotCompliantMBeanException("MBean class must have public constructor");
         }
      } else {
         throw new NotCompliantMBeanException("MBean class must be concrete");
      }
   }

   public static void checkCompliance(Class<?> var0) throws NotCompliantMBeanException {
      if (!DynamicMBean.class.isAssignableFrom(var0)) {
         try {
            getStandardMBeanInterface(var0);
         } catch (NotCompliantMBeanException var5) {
            try {
               getMXBeanInterface(var0);
            } catch (NotCompliantMBeanException var4) {
               String var3 = "MBean class " + var0.getName() + " does not implement DynamicMBean, and neither follows the Standard MBean conventions (" + var5.toString() + ") nor the MXBean conventions (" + var4.toString() + ")";
               throw new NotCompliantMBeanException(var3);
            }
         }
      }
   }

   public static <T> DynamicMBean makeDynamicMBean(T var0) throws NotCompliantMBeanException {
      if (var0 instanceof DynamicMBean) {
         return (DynamicMBean)var0;
      } else {
         Class var1 = var0.getClass();
         Class var2 = null;

         try {
            var2 = (Class)Util.cast(getStandardMBeanInterface(var1));
         } catch (NotCompliantMBeanException var5) {
         }

         if (var2 != null) {
            return new StandardMBeanSupport(var0, var2);
         } else {
            try {
               var2 = (Class)Util.cast(getMXBeanInterface(var1));
            } catch (NotCompliantMBeanException var4) {
            }

            if (var2 != null) {
               return new MXBeanSupport(var0, var2);
            } else {
               checkCompliance(var1);
               throw new NotCompliantMBeanException("Not compliant");
            }
         }
      }
   }

   public static MBeanInfo testCompliance(Class<?> var0) throws NotCompliantMBeanException {
      return isDynamic(var0) ? null : testCompliance(var0, (Class)null);
   }

   public static void testComplianceMXBeanInterface(Class<?> var0) throws NotCompliantMBeanException {
      MXBeanIntrospector.getInstance().getAnalyzer(var0);
   }

   public static void testComplianceMBeanInterface(Class<?> var0) throws NotCompliantMBeanException {
      StandardMBeanIntrospector.getInstance().getAnalyzer(var0);
   }

   public static synchronized MBeanInfo testCompliance(Class<?> var0, Class<?> var1) throws NotCompliantMBeanException {
      if (var1 == null) {
         var1 = getStandardMBeanInterface(var0);
      }

      ReflectUtil.checkPackageAccess(var1);
      StandardMBeanIntrospector var2 = StandardMBeanIntrospector.getInstance();
      return getClassMBeanInfo(var2, var0, var1);
   }

   private static <M> MBeanInfo getClassMBeanInfo(MBeanIntrospector<M> var0, Class<?> var1, Class<?> var2) throws NotCompliantMBeanException {
      PerInterface var3 = var0.getPerInterface(var2);
      return var0.getClassMBeanInfo(var1, var3);
   }

   public static Class<?> getMBeanInterface(Class<?> var0) {
      if (isDynamic(var0)) {
         return null;
      } else {
         try {
            return getStandardMBeanInterface(var0);
         } catch (NotCompliantMBeanException var2) {
            return null;
         }
      }
   }

   public static <T> Class<? super T> getStandardMBeanInterface(Class<T> var0) throws NotCompliantMBeanException {
      Class var1 = var0;

      Class var2;
      for(var2 = null; var1 != null; var1 = var1.getSuperclass()) {
         var2 = findMBeanInterface(var1, var1.getName());
         if (var2 != null) {
            break;
         }
      }

      if (var2 != null) {
         return var2;
      } else {
         String var3 = "Class " + var0.getName() + " is not a JMX compliant Standard MBean";
         throw new NotCompliantMBeanException(var3);
      }
   }

   public static <T> Class<? super T> getMXBeanInterface(Class<T> var0) throws NotCompliantMBeanException {
      try {
         return MXBeanSupport.findMXBeanInterface(var0);
      } catch (Exception var2) {
         throw throwException(var0, var2);
      }
   }

   private static <T> Class<? super T> findMBeanInterface(Class<T> var0, String var1) {
      for(Class var2 = var0; var2 != null; var2 = var2.getSuperclass()) {
         Class[] var3 = var2.getInterfaces();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class var6 = (Class)Util.cast(var3[var5]);
            var6 = implementsMBean(var6, var1);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   public static Descriptor descriptorForElement(AnnotatedElement var0) {
      if (var0 == null) {
         return ImmutableDescriptor.EMPTY_DESCRIPTOR;
      } else {
         Annotation[] var1 = var0.getAnnotations();
         return descriptorForAnnotations(var1);
      }
   }

   public static Descriptor descriptorForAnnotations(Annotation[] var0) {
      if (var0.length == 0) {
         return ImmutableDescriptor.EMPTY_DESCRIPTOR;
      } else {
         HashMap var1 = new HashMap();
         Annotation[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Annotation var5 = var2[var4];
            Class var6 = var5.annotationType();
            Method[] var7 = var6.getMethods();
            boolean var8 = false;
            Method[] var9 = var7;
            int var10 = var7.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               Method var12 = var9[var11];
               DescriptorKey var13 = (DescriptorKey)var12.getAnnotation(DescriptorKey.class);
               if (var13 != null) {
                  String var14 = var13.value();

                  Object var15;
                  try {
                     if (!var8) {
                        ReflectUtil.checkPackageAccess(var6);
                        var8 = true;
                     }

                     var15 = MethodUtil.invoke(var12, var5, (Object[])null);
                  } catch (RuntimeException var18) {
                     throw var18;
                  } catch (Exception var19) {
                     throw new UndeclaredThrowableException(var19);
                  }

                  var15 = annotationToField(var15);
                  Object var16 = var1.put(var14, var15);
                  if (var16 != null && !equals(var16, var15)) {
                     String var17 = "Inconsistent values for descriptor field " + var14 + " from annotations: " + var15 + " :: " + var16;
                     throw new IllegalArgumentException(var17);
                  }
               }
            }
         }

         if (var1.isEmpty()) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
         } else {
            return new ImmutableDescriptor(var1);
         }
      }
   }

   static NotCompliantMBeanException throwException(Class<?> var0, Throwable var1) throws NotCompliantMBeanException, SecurityException {
      if (var1 instanceof SecurityException) {
         throw (SecurityException)var1;
      } else if (var1 instanceof NotCompliantMBeanException) {
         throw (NotCompliantMBeanException)var1;
      } else {
         String var2 = var0 == null ? "null class" : var0.getName();
         String var3 = var1 == null ? "Not compliant" : var1.getMessage();
         NotCompliantMBeanException var4 = new NotCompliantMBeanException(var2 + ": " + var3);
         var4.initCause(var1);
         throw var4;
      }
   }

   private static Object annotationToField(Object var0) {
      if (var0 == null) {
         return null;
      } else if (!(var0 instanceof Number) && !(var0 instanceof String) && !(var0 instanceof Character) && !(var0 instanceof Boolean) && !(var0 instanceof String[])) {
         Class var1 = var0.getClass();
         if (!var1.isArray()) {
            if (var0 instanceof Class) {
               return ((Class)var0).getName();
            } else if (var0 instanceof Enum) {
               return ((Enum)var0).name();
            } else {
               if (Proxy.isProxyClass(var1)) {
                  var1 = var1.getInterfaces()[0];
               }

               throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + var1.getName());
            }
         } else if (var1.getComponentType().isPrimitive()) {
            return var0;
         } else {
            Object[] var2 = (Object[])((Object[])var0);
            String[] var3 = new String[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
               var3[var4] = (String)annotationToField(var2[var4]);
            }

            return var3;
         }
      } else {
         return var0;
      }
   }

   private static boolean equals(Object var0, Object var1) {
      return Arrays.deepEquals(new Object[]{var0}, new Object[]{var1});
   }

   private static <T> Class<? super T> implementsMBean(Class<T> var0, String var1) {
      String var2 = var1 + "MBean";
      if (var0.getName().equals(var2)) {
         return var0;
      } else {
         Class[] var3 = var0.getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4].getName().equals(var2) && (Modifier.isPublic(var3[var4].getModifiers()) || ALLOW_NONPUBLIC_MBEAN)) {
               return (Class)Util.cast(var3[var4]);
            }
         }

         return null;
      }
   }

   public static Object elementFromComplex(Object var0, String var1) throws AttributeNotFoundException {
      try {
         if (var0.getClass().isArray() && var1.equals("length")) {
            return Array.getLength(var0);
         } else if (var0 instanceof CompositeData) {
            return ((CompositeData)var0).get(var1);
         } else {
            Class var2 = var0.getClass();
            Method var3 = null;
            if (Introspector.BeansHelper.isAvailable()) {
               Object var4 = Introspector.BeansHelper.getBeanInfo(var2);
               Object[] var5 = Introspector.BeansHelper.getPropertyDescriptors(var4);
               Object[] var6 = var5;
               int var7 = var5.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Object var9 = var6[var8];
                  if (Introspector.BeansHelper.getPropertyName(var9).equals(var1)) {
                     var3 = Introspector.BeansHelper.getReadMethod(var9);
                     break;
                  }
               }
            } else {
               var3 = Introspector.SimpleIntrospector.getReadMethod(var2, var1);
            }

            if (var3 != null) {
               ReflectUtil.checkPackageAccess(var3.getDeclaringClass());
               return MethodUtil.invoke(var3, var0, new Class[0]);
            } else {
               throw new AttributeNotFoundException("Could not find the getter method for the property " + var1 + " using the Java Beans introspector");
            }
         }
      } catch (InvocationTargetException var10) {
         throw new IllegalArgumentException(var10);
      } catch (AttributeNotFoundException var11) {
         throw var11;
      } catch (Exception var12) {
         throw (AttributeNotFoundException)EnvHelp.initCause(new AttributeNotFoundException(var12.getMessage()), var12);
      }
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.jmx.mbeans.allowNonPublic")));
      ALLOW_NONPUBLIC_MBEAN = Boolean.parseBoolean(var0);
   }

   private static class BeansHelper {
      private static final Class<?> introspectorClass = getClass("java.beans.Introspector");
      private static final Class<?> beanInfoClass;
      private static final Class<?> getPropertyDescriptorClass;
      private static final Method getBeanInfo;
      private static final Method getPropertyDescriptors;
      private static final Method getPropertyName;
      private static final Method getReadMethod;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, (ClassLoader)null);
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         if (var0 != null) {
            try {
               return var0.getMethod(var1, var2);
            } catch (NoSuchMethodException var4) {
               throw new AssertionError(var4);
            }
         } else {
            return null;
         }
      }

      static boolean isAvailable() {
         return introspectorClass != null;
      }

      static Object getBeanInfo(Class<?> var0) throws Exception {
         try {
            return getBeanInfo.invoke((Object)null, var0);
         } catch (InvocationTargetException var3) {
            Throwable var2 = var3.getCause();
            if (var2 instanceof Exception) {
               throw (Exception)var2;
            } else {
               throw new AssertionError(var3);
            }
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      static Object[] getPropertyDescriptors(Object var0) {
         try {
            return (Object[])((Object[])getPropertyDescriptors.invoke(var0));
         } catch (InvocationTargetException var3) {
            Throwable var2 = var3.getCause();
            if (var2 instanceof RuntimeException) {
               throw (RuntimeException)var2;
            } else {
               throw new AssertionError(var3);
            }
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      static String getPropertyName(Object var0) {
         try {
            return (String)getPropertyName.invoke(var0);
         } catch (InvocationTargetException var3) {
            Throwable var2 = var3.getCause();
            if (var2 instanceof RuntimeException) {
               throw (RuntimeException)var2;
            } else {
               throw new AssertionError(var3);
            }
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      static Method getReadMethod(Object var0) {
         try {
            return (Method)getReadMethod.invoke(var0);
         } catch (InvocationTargetException var3) {
            Throwable var2 = var3.getCause();
            if (var2 instanceof RuntimeException) {
               throw (RuntimeException)var2;
            } else {
               throw new AssertionError(var3);
            }
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }

      static {
         beanInfoClass = introspectorClass == null ? null : getClass("java.beans.BeanInfo");
         getPropertyDescriptorClass = beanInfoClass == null ? null : getClass("java.beans.PropertyDescriptor");
         getBeanInfo = getMethod(introspectorClass, "getBeanInfo", Class.class);
         getPropertyDescriptors = getMethod(beanInfoClass, "getPropertyDescriptors");
         getPropertyName = getMethod(getPropertyDescriptorClass, "getName");
         getReadMethod = getMethod(getPropertyDescriptorClass, "getReadMethod");
      }
   }

   private static class SimpleIntrospector {
      private static final String GET_METHOD_PREFIX = "get";
      private static final String IS_METHOD_PREFIX = "is";
      private static final Map<Class<?>, SoftReference<List<Method>>> cache = Collections.synchronizedMap(new WeakHashMap());

      private static List<Method> getCachedMethods(Class<?> var0) {
         SoftReference var1 = (SoftReference)cache.get(var0);
         if (var1 != null) {
            List var2 = (List)var1.get();
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      }

      static boolean isReadMethod(Method var0) {
         int var1 = var0.getModifiers();
         if (Modifier.isStatic(var1)) {
            return false;
         } else {
            String var2 = var0.getName();
            Class[] var3 = var0.getParameterTypes();
            int var4 = var3.length;
            if (var4 == 0 && var2.length() > 2) {
               if (var2.startsWith("is")) {
                  return var0.getReturnType() == Boolean.TYPE;
               }

               if (var2.length() > 3 && var2.startsWith("get")) {
                  return var0.getReturnType() != Void.TYPE;
               }
            }

            return false;
         }
      }

      static List<Method> getReadMethods(Class<?> var0) {
         List var1 = getCachedMethods(var0);
         if (var1 != null) {
            return var1;
         } else {
            List var2 = StandardMBeanIntrospector.getInstance().getMethods(var0);
            var2 = MBeanAnalyzer.eliminateCovariantMethods(var2);
            LinkedList var3 = new LinkedList();
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Method var5 = (Method)var4.next();
               if (isReadMethod(var5)) {
                  if (var5.getName().startsWith("is")) {
                     var3.add(0, var5);
                  } else {
                     var3.add(var5);
                  }
               }
            }

            cache.put(var0, new SoftReference(var3));
            return var3;
         }
      }

      static Method getReadMethod(Class<?> var0, String var1) {
         var1 = var1.substring(0, 1).toUpperCase(Locale.ENGLISH) + var1.substring(1);
         String var2 = "get" + var1;
         String var3 = "is" + var1;
         Iterator var4 = getReadMethods(var0).iterator();

         Method var5;
         String var6;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (Method)var4.next();
            var6 = var5.getName();
         } while(!var6.equals(var3) && !var6.equals(var2));

         return var5;
      }
   }
}
