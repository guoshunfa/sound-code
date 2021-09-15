package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class OperationMode implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static OperationMode[] __array;
   public static final int _OP_NORMAL = 0;
   public static final OperationMode OP_NORMAL;
   public static final int _OP_ONEWAY = 1;
   public static final OperationMode OP_ONEWAY;

   public int value() {
      return this.__value;
   }

   public static OperationMode from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected OperationMode(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new OperationMode[__size];
      OP_NORMAL = new OperationMode(0);
      OP_ONEWAY = new OperationMode(1);
   }
}
