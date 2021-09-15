package com.sun.jmx.mbeanserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import sun.reflect.misc.MethodUtil;

class StandardMBeanIntrospector extends MBeanIntrospector<Method> {
   private static final StandardMBeanIntrospector instance = new StandardMBeanIntrospector();
   private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable = new WeakHashMap();
   private static final MBeanIntrospector.PerInterfaceMap<Method> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
   private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();

   static StandardMBeanIntrospector getInstance() {
      return instance;
   }

   MBeanIntrospector.PerInterfaceMap<Method> getPerInterfaceMap() {
      return perInterfaceMap;
   }

   MBeanIntrospector.MBeanInfoMap getMBeanInfoMap() {
      return mbeanInfoMap;
   }

   MBeanAnalyzer<Method> getAnalyzer(Class<?> var1) throws NotCompliantMBeanException {
      return MBeanAnalyzer.analyzer(var1, this);
   }

   boolean isMXBean() {
      return false;
   }

   Method mFrom(Method var1) {
      return var1;
   }

   String getName(Method var1) {
      return var1.getName();
   }

   Type getGenericReturnType(Method var1) {
      return var1.getGenericReturnType();
   }

   Type[] getGenericParameterTypes(Method var1) {
      return var1.getGenericParameterTypes();
   }

   String[] getSignature(Method var1) {
      Class[] var2 = var1.getParameterTypes();
      String[] var3 = new String[var2.length];

      for(int var4 = 0; var4 < var2.length; ++var4) {
         var3[var4] = var2[var4].getName();
      }

      return var3;
   }

   void checkMethod(Method var1) {
   }

   Object invokeM2(Method var1, Object var2, Object[] var3, Object var4) throws InvocationTargetException, IllegalAccessException, MBeanException {
      return MethodUtil.invoke(var1, var2, var3);
   }

   boolean validParameter(Method var1, Object var2, int var3, Object var4) {
      return isValidParameter(var1, var2, var3);
   }

   MBeanAttributeInfo getMBeanAttributeInfo(String var1, Method var2, Method var3) {
      try {
         return new MBeanAttributeInfo(var1, "Attribute exposed for management", var2, var3);
      } catch (IntrospectionException var6) {
         throw new RuntimeException(var6);
      }
   }

   MBeanOperationInfo getMBeanOperationInfo(String var1, Method var2) {
      return new MBeanOperationInfo("Operation exposed for management", var2);
   }

   Descriptor getBasicMBeanDescriptor() {
      return ImmutableDescriptor.EMPTY_DESCRIPTOR;
   }

   Descriptor getMBeanDescriptor(Class<?> var1) {
      boolean var2 = isDefinitelyImmutableInfo(var1);
      return new ImmutableDescriptor(new String[]{"mxbean=false", "immutableInfo=" + var2});
   }

   static boolean isDefinitelyImmutableInfo(Class<?> var0) {
      if (!NotificationBroadcaster.class.isAssignableFrom(var0)) {
         return true;
      } else {
         synchronized(definitelyImmutable) {
            Boolean var2 = (Boolean)definitelyImmutable.get(var0);
            if (var2 == null) {
               Class var3 = NotificationBroadcasterSupport.class;
               if (var3.isAssignableFrom(var0)) {
                  try {
                     Method var4 = var0.getMethod("getNotificationInfo");
                     var2 = var4.getDeclaringClass() == var3;
                  } catch (Exception var6) {
                     return false;
                  }
               } else {
                  var2 = false;
               }

               definitelyImmutable.put(var0, var2);
            }

            return var2;
         }
      }
   }
}
