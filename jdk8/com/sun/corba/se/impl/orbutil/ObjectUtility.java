package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Array;

public final class ObjectUtility {
   private ObjectUtility() {
   }

   public static Object concatenateArrays(Object var0, Object var1) {
      Class var2 = var0.getClass().getComponentType();
      Class var3 = var1.getClass().getComponentType();
      int var4 = Array.getLength(var0);
      int var5 = Array.getLength(var1);
      if (var2 != null && var3 != null) {
         if (!var2.equals(var3)) {
            throw new IllegalStateException("Arguments must be arrays with the same component type");
         } else {
            Object var6 = Array.newInstance(var2, var4 + var5);
            int var7 = 0;

            int var8;
            for(var8 = 0; var8 < var4; ++var8) {
               Array.set(var6, var7++, Array.get(var0, var8));
            }

            for(var8 = 0; var8 < var5; ++var8) {
               Array.set(var6, var7++, Array.get(var1, var8));
            }

            return var6;
         }
      } else {
         throw new IllegalStateException("Arguments must be arrays");
      }
   }
}
