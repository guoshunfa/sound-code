package org.omg.CosNaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class BindingType implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static BindingType[] __array;
   public static final int _nobject = 0;
   public static final BindingType nobject;
   public static final int _ncontext = 1;
   public static final BindingType ncontext;

   public int value() {
      return this.__value;
   }

   public static BindingType from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected BindingType(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new BindingType[__size];
      nobject = new BindingType(0);
      ncontext = new BindingType(1);
   }
}
