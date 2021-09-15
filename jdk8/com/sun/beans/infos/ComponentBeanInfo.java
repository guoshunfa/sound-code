package com.sun.beans.infos;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ComponentBeanInfo extends SimpleBeanInfo {
   private static final Class<Component> beanClass = Component.class;

   public PropertyDescriptor[] getPropertyDescriptors() {
      try {
         PropertyDescriptor var1 = new PropertyDescriptor("name", beanClass);
         PropertyDescriptor var2 = new PropertyDescriptor("background", beanClass);
         PropertyDescriptor var3 = new PropertyDescriptor("foreground", beanClass);
         PropertyDescriptor var4 = new PropertyDescriptor("font", beanClass);
         PropertyDescriptor var5 = new PropertyDescriptor("enabled", beanClass);
         PropertyDescriptor var6 = new PropertyDescriptor("visible", beanClass);
         PropertyDescriptor var7 = new PropertyDescriptor("focusable", beanClass);
         var5.setExpert(true);
         var6.setHidden(true);
         var2.setBound(true);
         var3.setBound(true);
         var4.setBound(true);
         var7.setBound(true);
         PropertyDescriptor[] var8 = new PropertyDescriptor[]{var1, var2, var3, var4, var5, var6, var7};
         return var8;
      } catch (IntrospectionException var9) {
         throw new Error(var9.toString());
      }
   }
}
