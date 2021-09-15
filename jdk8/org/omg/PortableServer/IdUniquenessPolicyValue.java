package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdUniquenessPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static IdUniquenessPolicyValue[] __array;
   public static final int _UNIQUE_ID = 0;
   public static final IdUniquenessPolicyValue UNIQUE_ID;
   public static final int _MULTIPLE_ID = 1;
   public static final IdUniquenessPolicyValue MULTIPLE_ID;

   public int value() {
      return this.__value;
   }

   public static IdUniquenessPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected IdUniquenessPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new IdUniquenessPolicyValue[__size];
      UNIQUE_ID = new IdUniquenessPolicyValue(0);
      MULTIPLE_ID = new IdUniquenessPolicyValue(1);
   }
}
