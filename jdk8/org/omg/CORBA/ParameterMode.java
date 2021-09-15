package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class ParameterMode implements IDLEntity {
   private int __value;
   private static int __size = 3;
   private static ParameterMode[] __array;
   public static final int _PARAM_IN = 0;
   public static final ParameterMode PARAM_IN;
   public static final int _PARAM_OUT = 1;
   public static final ParameterMode PARAM_OUT;
   public static final int _PARAM_INOUT = 2;
   public static final ParameterMode PARAM_INOUT;

   public int value() {
      return this.__value;
   }

   public static ParameterMode from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected ParameterMode(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new ParameterMode[__size];
      PARAM_IN = new ParameterMode(0);
      PARAM_OUT = new ParameterMode(1);
      PARAM_INOUT = new ParameterMode(2);
   }
}
