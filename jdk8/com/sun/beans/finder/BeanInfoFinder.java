package com.sun.beans.finder;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public final class BeanInfoFinder extends InstanceFinder<BeanInfo> {
   private static final String DEFAULT = "sun.beans.infos";
   private static final String DEFAULT_NEW = "com.sun.beans.infos";

   public BeanInfoFinder() {
      super(BeanInfo.class, true, "BeanInfo", "sun.beans.infos");
   }

   private static boolean isValid(Class<?> var0, Method var1) {
      return var1 != null && var1.getDeclaringClass().isAssignableFrom(var0);
   }

   protected BeanInfo instantiate(Class<?> var1, String var2, String var3) {
      if ("sun.beans.infos".equals(var2)) {
         var2 = "com.sun.beans.infos";
      }

      BeanInfo var4 = "com.sun.beans.infos".equals(var2) && !"ComponentBeanInfo".equals(var3) ? null : (BeanInfo)super.instantiate(var1, var2, var3);
      if (var4 != null) {
         BeanDescriptor var5 = var4.getBeanDescriptor();
         if (var5 != null) {
            if (var1.equals(var5.getBeanClass())) {
               return var4;
            }
         } else {
            PropertyDescriptor[] var6 = var4.getPropertyDescriptors();
            int var9;
            if (var6 != null) {
               PropertyDescriptor[] var7 = var6;
               int var8 = var6.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  PropertyDescriptor var10 = var7[var9];
                  Method var11 = var10.getReadMethod();
                  if (var11 == null) {
                     var11 = var10.getWriteMethod();
                  }

                  if (isValid(var1, var11)) {
                     return var4;
                  }
               }
            } else {
               MethodDescriptor[] var12 = var4.getMethodDescriptors();
               if (var12 != null) {
                  MethodDescriptor[] var13 = var12;
                  var9 = var12.length;

                  for(int var14 = 0; var14 < var9; ++var14) {
                     MethodDescriptor var15 = var13[var14];
                     if (isValid(var1, var15.getMethod())) {
                        return var4;
                     }
                  }
               }
            }
         }
      }

      return null;
   }
}
