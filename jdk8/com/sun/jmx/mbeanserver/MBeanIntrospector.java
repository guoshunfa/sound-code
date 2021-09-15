package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

abstract class MBeanIntrospector<M> {
   abstract MBeanIntrospector.PerInterfaceMap<M> getPerInterfaceMap();

   abstract MBeanIntrospector.MBeanInfoMap getMBeanInfoMap();

   abstract MBeanAnalyzer<M> getAnalyzer(Class<?> var1) throws NotCompliantMBeanException;

   abstract boolean isMXBean();

   abstract M mFrom(Method var1);

   abstract String getName(M var1);

   abstract Type getGenericReturnType(M var1);

   abstract Type[] getGenericParameterTypes(M var1);

   abstract String[] getSignature(M var1);

   abstract void checkMethod(M var1);

   abstract Object invokeM2(M var1, Object var2, Object[] var3, Object var4) throws InvocationTargetException, IllegalAccessException, MBeanException;

   abstract boolean validParameter(M var1, Object var2, int var3, Object var4);

   abstract MBeanAttributeInfo getMBeanAttributeInfo(String var1, M var2, M var3);

   abstract MBeanOperationInfo getMBeanOperationInfo(String var1, M var2);

   abstract Descriptor getBasicMBeanDescriptor();

   abstract Descriptor getMBeanDescriptor(Class<?> var1);

   final List<Method> getMethods(Class<?> var1) {
      ReflectUtil.checkPackageAccess(var1);
      return Arrays.asList(var1.getMethods());
   }

   final PerInterface<M> getPerInterface(Class<?> var1) throws NotCompliantMBeanException {
      MBeanIntrospector.PerInterfaceMap var2 = this.getPerInterfaceMap();
      synchronized(var2) {
         WeakReference var4 = (WeakReference)var2.get(var1);
         PerInterface var5 = var4 == null ? null : (PerInterface)var4.get();
         if (var5 == null) {
            try {
               MBeanAnalyzer var6 = this.getAnalyzer(var1);
               MBeanInfo var7 = this.makeInterfaceMBeanInfo(var1, var6);
               var5 = new PerInterface(var1, this, var6, var7);
               var4 = new WeakReference(var5);
               var2.put(var1, var4);
            } catch (Exception var9) {
               throw Introspector.throwException(var1, var9);
            }
         }

         return var5;
      }
   }

   private MBeanInfo makeInterfaceMBeanInfo(Class<?> var1, MBeanAnalyzer<M> var2) {
      MBeanIntrospector.MBeanInfoMaker var3 = new MBeanIntrospector.MBeanInfoMaker();
      var2.visit(var3);
      return var3.makeMBeanInfo(var1, "Information on the management interface of the MBean");
   }

   final boolean consistent(M var1, M var2) {
      return var1 == null || var2 == null || this.getGenericReturnType(var1).equals(this.getGenericParameterTypes(var2)[0]);
   }

   final Object invokeM(M var1, Object var2, Object[] var3, Object var4) throws MBeanException, ReflectionException {
      try {
         return this.invokeM2(var1, var2, var3, var4);
      } catch (InvocationTargetException var6) {
         unwrapInvocationTargetException(var6);
         throw new RuntimeException(var6);
      } catch (IllegalAccessException var7) {
         throw new ReflectionException(var7, var7.toString());
      }
   }

   final void invokeSetter(String var1, M var2, Object var3, Object var4, Object var5) throws MBeanException, ReflectionException, InvalidAttributeValueException {
      try {
         this.invokeM2(var2, var3, new Object[]{var4}, var5);
      } catch (IllegalAccessException var7) {
         throw new ReflectionException(var7, var7.toString());
      } catch (RuntimeException var8) {
         this.maybeInvalidParameter(var1, var2, var4, var5);
         throw var8;
      } catch (InvocationTargetException var9) {
         this.maybeInvalidParameter(var1, var2, var4, var5);
         unwrapInvocationTargetException(var9);
      }

   }

   private void maybeInvalidParameter(String var1, M var2, Object var3, Object var4) throws InvalidAttributeValueException {
      if (!this.validParameter(var2, var3, 0, var4)) {
         String var5 = "Invalid value for attribute " + var1 + ": " + var3;
         throw new InvalidAttributeValueException(var5);
      }
   }

   static boolean isValidParameter(Method var0, Object var1, int var2) {
      Class var3 = var0.getParameterTypes()[var2];

      try {
         Object var4 = Array.newInstance(var3, 1);
         Array.set(var4, 0, var1);
         return true;
      } catch (IllegalArgumentException var5) {
         return false;
      }
   }

   private static void unwrapInvocationTargetException(InvocationTargetException var0) throws MBeanException {
      Throwable var1 = var0.getCause();
      if (var1 instanceof RuntimeException) {
         throw (RuntimeException)var1;
      } else if (var1 instanceof Error) {
         throw (Error)var1;
      } else {
         throw new MBeanException((Exception)var1, var1 == null ? null : var1.toString());
      }
   }

   final MBeanInfo getMBeanInfo(Object var1, PerInterface<M> var2) {
      MBeanInfo var3 = this.getClassMBeanInfo(var1.getClass(), var2);
      MBeanNotificationInfo[] var4 = findNotifications(var1);
      return var4 != null && var4.length != 0 ? new MBeanInfo(var3.getClassName(), var3.getDescription(), var3.getAttributes(), var3.getConstructors(), var3.getOperations(), var4, var3.getDescriptor()) : var3;
   }

   final MBeanInfo getClassMBeanInfo(Class<?> var1, PerInterface<M> var2) {
      MBeanIntrospector.MBeanInfoMap var3 = this.getMBeanInfoMap();
      synchronized(var3) {
         WeakHashMap var5 = (WeakHashMap)var3.get(var1);
         if (var5 == null) {
            var5 = new WeakHashMap();
            var3.put(var1, var5);
         }

         Class var6 = var2.getMBeanInterface();
         MBeanInfo var7 = (MBeanInfo)var5.get(var6);
         if (var7 == null) {
            MBeanInfo var8 = var2.getMBeanInfo();
            ImmutableDescriptor var9 = ImmutableDescriptor.union(var8.getDescriptor(), this.getMBeanDescriptor(var1));
            var7 = new MBeanInfo(var1.getName(), var8.getDescription(), var8.getAttributes(), findConstructors(var1), var8.getOperations(), (MBeanNotificationInfo[])null, var9);
            var5.put(var6, var7);
         }

         return var7;
      }
   }

   static MBeanNotificationInfo[] findNotifications(Object var0) {
      if (!(var0 instanceof NotificationBroadcaster)) {
         return null;
      } else {
         MBeanNotificationInfo[] var1 = ((NotificationBroadcaster)var0).getNotificationInfo();
         if (var1 == null) {
            return null;
         } else {
            MBeanNotificationInfo[] var2 = new MBeanNotificationInfo[var1.length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
               MBeanNotificationInfo var4 = var1[var3];
               if (var4.getClass() != MBeanNotificationInfo.class) {
                  var4 = (MBeanNotificationInfo)var4.clone();
               }

               var2[var3] = var4;
            }

            return var2;
         }
      }
   }

   private static MBeanConstructorInfo[] findConstructors(Class<?> var0) {
      Constructor[] var1 = var0.getConstructors();
      MBeanConstructorInfo[] var2 = new MBeanConstructorInfo[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = new MBeanConstructorInfo("Public constructor of the MBean", var1[var3]);
      }

      return var2;
   }

   static class MBeanInfoMap extends WeakHashMap<Class<?>, WeakHashMap<Class<?>, MBeanInfo>> {
   }

   private class MBeanInfoMaker implements MBeanAnalyzer.MBeanVisitor<M> {
      private final List<MBeanAttributeInfo> attrs;
      private final List<MBeanOperationInfo> ops;

      private MBeanInfoMaker() {
         this.attrs = Util.newList();
         this.ops = Util.newList();
      }

      public void visitAttribute(String var1, M var2, M var3) {
         MBeanAttributeInfo var4 = MBeanIntrospector.this.getMBeanAttributeInfo(var1, var2, var3);
         this.attrs.add(var4);
      }

      public void visitOperation(String var1, M var2) {
         MBeanOperationInfo var3 = MBeanIntrospector.this.getMBeanOperationInfo(var1, var2);
         this.ops.add(var3);
      }

      MBeanInfo makeMBeanInfo(Class<?> var1, String var2) {
         MBeanAttributeInfo[] var3 = (MBeanAttributeInfo[])this.attrs.toArray(new MBeanAttributeInfo[0]);
         MBeanOperationInfo[] var4 = (MBeanOperationInfo[])this.ops.toArray(new MBeanOperationInfo[0]);
         String var5 = "interfaceClassName=" + var1.getName();
         ImmutableDescriptor var6 = new ImmutableDescriptor(new String[]{var5});
         Descriptor var7 = MBeanIntrospector.this.getBasicMBeanDescriptor();
         Descriptor var8 = Introspector.descriptorForElement(var1);
         ImmutableDescriptor var9 = DescriptorCache.getInstance().union(var6, var7, var8);
         return new MBeanInfo(var1.getName(), var2, var3, (MBeanConstructorInfo[])null, var4, (MBeanNotificationInfo[])null, var9);
      }

      // $FF: synthetic method
      MBeanInfoMaker(Object var2) {
         this();
      }
   }

   static final class PerInterfaceMap<M> extends WeakHashMap<Class<?>, WeakReference<PerInterface<M>>> {
   }
}
