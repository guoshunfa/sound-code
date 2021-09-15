package com.sun.beans.decoder;

import com.sun.beans.finder.MethodFinder;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

final class PropertyElementHandler extends AccessorElementHandler {
   static final String GETTER = "get";
   static final String SETTER = "set";
   private Integer index;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("index")) {
         this.index = Integer.valueOf(var2);
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected boolean isArgument() {
      return false;
   }

   protected Object getValue(String var1) {
      try {
         return getPropertyValue(this.getContextBean(), var1, this.index);
      } catch (Exception var3) {
         this.getOwner().handleException(var3);
         return null;
      }
   }

   protected void setValue(String var1, Object var2) {
      try {
         setPropertyValue(this.getContextBean(), var1, this.index, var2);
      } catch (Exception var4) {
         this.getOwner().handleException(var4);
      }

   }

   private static Object getPropertyValue(Object var0, String var1, Integer var2) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
      Class var3 = var0.getClass();
      if (var2 == null) {
         return MethodUtil.invoke(findGetter(var3, var1), var0, new Object[0]);
      } else {
         return var3.isArray() && var1 == null ? Array.get(var0, var2) : MethodUtil.invoke(findGetter(var3, var1, Integer.TYPE), var0, new Object[]{var2});
      }
   }

   private static void setPropertyValue(Object var0, String var1, Integer var2, Object var3) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
      Class var4 = var0.getClass();
      Class var5 = var3 != null ? var3.getClass() : null;
      if (var2 == null) {
         MethodUtil.invoke(findSetter(var4, var1, var5), var0, new Object[]{var3});
      } else if (var4.isArray() && var1 == null) {
         Array.set(var0, var2, var3);
      } else {
         MethodUtil.invoke(findSetter(var4, var1, Integer.TYPE, var5), var0, new Object[]{var2, var3});
      }

   }

   private static Method findGetter(Class<?> var0, String var1, Class<?>... var2) throws IntrospectionException, NoSuchMethodException {
      if (var1 == null) {
         return MethodFinder.findInstanceMethod(var0, "get", var2);
      } else {
         PropertyDescriptor var3 = getProperty(var0, var1);
         if (var2.length == 0) {
            Method var4 = var3.getReadMethod();
            if (var4 != null) {
               return var4;
            }
         } else if (var3 instanceof IndexedPropertyDescriptor) {
            IndexedPropertyDescriptor var6 = (IndexedPropertyDescriptor)var3;
            Method var5 = var6.getIndexedReadMethod();
            if (var5 != null) {
               return var5;
            }
         }

         throw new IntrospectionException("Could not find getter for the " + var1 + " property");
      }
   }

   private static Method findSetter(Class<?> var0, String var1, Class<?>... var2) throws IntrospectionException, NoSuchMethodException {
      if (var1 == null) {
         return MethodFinder.findInstanceMethod(var0, "set", var2);
      } else {
         PropertyDescriptor var3 = getProperty(var0, var1);
         if (var2.length == 1) {
            Method var4 = var3.getWriteMethod();
            if (var4 != null) {
               return var4;
            }
         } else if (var3 instanceof IndexedPropertyDescriptor) {
            IndexedPropertyDescriptor var6 = (IndexedPropertyDescriptor)var3;
            Method var5 = var6.getIndexedWriteMethod();
            if (var5 != null) {
               return var5;
            }
         }

         throw new IntrospectionException("Could not find setter for the " + var1 + " property");
      }
   }

   private static PropertyDescriptor getProperty(Class<?> var0, String var1) throws IntrospectionException {
      PropertyDescriptor[] var2 = Introspector.getBeanInfo(var0).getPropertyDescriptors();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PropertyDescriptor var5 = var2[var4];
         if (var1.equals(var5.getName())) {
            return var5;
         }
      }

      throw new IntrospectionException("Could not find the " + var1 + " property descriptor");
   }
}
