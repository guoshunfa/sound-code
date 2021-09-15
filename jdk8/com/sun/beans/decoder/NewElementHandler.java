package com.sun.beans.decoder;

import com.sun.beans.finder.ConstructorFinder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class NewElementHandler extends ElementHandler {
   private List<Object> arguments = new ArrayList();
   private ValueObject value;
   private Class<?> type;

   NewElementHandler() {
      this.value = ValueObjectImpl.VOID;
   }

   public void addAttribute(String var1, String var2) {
      if (var1.equals("class")) {
         this.type = this.getOwner().findClass(var2);
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected final void addArgument(Object var1) {
      if (this.arguments == null) {
         throw new IllegalStateException("Could not add argument to evaluated element");
      } else {
         this.arguments.add(var1);
      }
   }

   protected final Object getContextBean() {
      return this.type != null ? this.type : super.getContextBean();
   }

   protected final ValueObject getValueObject() {
      if (this.arguments != null) {
         try {
            this.value = this.getValueObject(this.type, this.arguments.toArray());
         } catch (Exception var5) {
            this.getOwner().handleException(var5);
         } finally {
            this.arguments = null;
         }
      }

      return this.value;
   }

   ValueObject getValueObject(Class<?> var1, Object[] var2) throws Exception {
      if (var1 == null) {
         throw new IllegalArgumentException("Class name is not set");
      } else {
         Class[] var3 = getArgumentTypes(var2);
         Constructor var4 = ConstructorFinder.findConstructor(var1, var3);
         if (var4.isVarArgs()) {
            var2 = getArguments(var2, var4.getParameterTypes());
         }

         return ValueObjectImpl.create(var4.newInstance(var2));
      }
   }

   static Class<?>[] getArgumentTypes(Object[] var0) {
      Class[] var1 = new Class[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2] != null) {
            var1[var2] = var0[var2].getClass();
         }
      }

      return var1;
   }

   static Object[] getArguments(Object[] var0, Class<?>[] var1) {
      int var2 = var1.length - 1;
      Class var4;
      if (var1.length == var0.length) {
         Object var3 = var0[var2];
         if (var3 == null) {
            return var0;
         }

         var4 = var1[var2];
         if (var4.isAssignableFrom(var3.getClass())) {
            return var0;
         }
      }

      int var7 = var0.length - var2;
      var4 = var1[var2].getComponentType();
      Object var5 = Array.newInstance(var4, var7);
      System.arraycopy(var0, var2, var5, 0, var7);
      Object[] var6 = new Object[var1.length];
      System.arraycopy(var0, 0, var6, 0, var2);
      var6[var2] = var5;
      return var6;
   }
}
