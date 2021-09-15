package com.sun.beans.finder;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.HashMap;

abstract class AbstractFinder<T extends Executable> {
   private final Class<?>[] args;

   protected AbstractFinder(Class<?>[] var1) {
      this.args = var1;
   }

   protected boolean isValid(T var1) {
      return Modifier.isPublic(var1.getModifiers());
   }

   final T find(T[] var1) throws NoSuchMethodException {
      HashMap var2 = new HashMap();
      Executable var3 = null;
      Class[] var4 = null;
      boolean var5 = false;
      Executable[] var6 = var1;
      int var7 = var1.length;

      int var8;
      Executable var9;
      Class[] var10;
      boolean var11;
      boolean var12;
      for(var8 = 0; var8 < var7; ++var8) {
         var9 = var6[var8];
         if (this.isValid(var9)) {
            var10 = var9.getParameterTypes();
            if (var10.length == this.args.length) {
               PrimitiveWrapperMap.replacePrimitivesWithWrappers(var10);
               if (this.isAssignable(var10, this.args)) {
                  if (var3 == null) {
                     var3 = var9;
                     var4 = var10;
                  } else {
                     var11 = this.isAssignable(var4, var10);
                     var12 = this.isAssignable(var10, var4);
                     if (var12 && var11) {
                        var11 = !var9.isSynthetic();
                        var12 = !var3.isSynthetic();
                     }

                     if (var12 == var11) {
                        var5 = true;
                     } else if (var11) {
                        var3 = var9;
                        var4 = var10;
                        var5 = false;
                     }
                  }
               }
            }

            if (var9.isVarArgs()) {
               int var15 = var10.length - 1;
               if (var15 <= this.args.length) {
                  Class[] var16 = new Class[this.args.length];
                  System.arraycopy(var10, 0, var16, 0, var15);
                  if (var15 < this.args.length) {
                     Class var13 = var10[var15].getComponentType();
                     if (var13.isPrimitive()) {
                        var13 = PrimitiveWrapperMap.getType(var13.getName());
                     }

                     for(int var14 = var15; var14 < this.args.length; ++var14) {
                        var16[var14] = var13;
                     }
                  }

                  var2.put(var9, var16);
               }
            }
         }
      }

      var6 = var1;
      var7 = var1.length;

      for(var8 = 0; var8 < var7; ++var8) {
         var9 = var6[var8];
         var10 = (Class[])var2.get(var9);
         if (var10 != null && this.isAssignable(var10, this.args)) {
            if (var3 == null) {
               var3 = var9;
               var4 = var10;
            } else {
               var11 = this.isAssignable(var4, var10);
               var12 = this.isAssignable(var10, var4);
               if (var12 && var11) {
                  var11 = !var9.isSynthetic();
                  var12 = !var3.isSynthetic();
               }

               if (var12 == var11) {
                  if (var4 == var2.get(var3)) {
                     var5 = true;
                  }
               } else if (var11) {
                  var3 = var9;
                  var4 = var10;
                  var5 = false;
               }
            }
         }
      }

      if (var5) {
         throw new NoSuchMethodException("Ambiguous methods are found");
      } else if (var3 == null) {
         throw new NoSuchMethodException("Method is not found");
      } else {
         return var3;
      }
   }

   private boolean isAssignable(Class<?>[] var1, Class<?>[] var2) {
      for(int var3 = 0; var3 < this.args.length; ++var3) {
         if (null != this.args[var3] && !var1[var3].isAssignableFrom(var2[var3])) {
            return false;
         }
      }

      return true;
   }
}
