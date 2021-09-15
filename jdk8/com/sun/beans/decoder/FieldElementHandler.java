package com.sun.beans.decoder;

import com.sun.beans.finder.FieldFinder;
import java.lang.reflect.Field;

final class FieldElementHandler extends AccessorElementHandler {
   private Class<?> type;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("class")) {
         this.type = this.getOwner().findClass(var2);
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected boolean isArgument() {
      return super.isArgument() && this.type != null;
   }

   protected Object getContextBean() {
      return this.type != null ? this.type : super.getContextBean();
   }

   protected Object getValue(String var1) {
      try {
         return getFieldValue(this.getContextBean(), var1);
      } catch (Exception var3) {
         this.getOwner().handleException(var3);
         return null;
      }
   }

   protected void setValue(String var1, Object var2) {
      try {
         setFieldValue(this.getContextBean(), var1, var2);
      } catch (Exception var4) {
         this.getOwner().handleException(var4);
      }

   }

   static Object getFieldValue(Object var0, String var1) throws IllegalAccessException, NoSuchFieldException {
      return findField(var0, var1).get(var0);
   }

   private static void setFieldValue(Object var0, String var1, Object var2) throws IllegalAccessException, NoSuchFieldException {
      findField(var0, var1).set(var0, var2);
   }

   private static Field findField(Object var0, String var1) throws NoSuchFieldException {
      return var0 instanceof Class ? FieldFinder.findStaticField((Class)var0, var1) : FieldFinder.findField(var0.getClass(), var1);
   }
}
