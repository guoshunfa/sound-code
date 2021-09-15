package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class LifespanPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static LifespanPolicyValue[] __array;
   public static final int _TRANSIENT = 0;
   public static final LifespanPolicyValue TRANSIENT;
   public static final int _PERSISTENT = 1;
   public static final LifespanPolicyValue PERSISTENT;

   public int value() {
      return this.__value;
   }

   public static LifespanPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected LifespanPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new LifespanPolicyValue[__size];
      TRANSIENT = new LifespanPolicyValue(0);
      PERSISTENT = new LifespanPolicyValue(1);
   }
}
