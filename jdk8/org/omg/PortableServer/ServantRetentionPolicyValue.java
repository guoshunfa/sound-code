package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ServantRetentionPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static ServantRetentionPolicyValue[] __array;
   public static final int _RETAIN = 0;
   public static final ServantRetentionPolicyValue RETAIN;
   public static final int _NON_RETAIN = 1;
   public static final ServantRetentionPolicyValue NON_RETAIN;

   public int value() {
      return this.__value;
   }

   public static ServantRetentionPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected ServantRetentionPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new ServantRetentionPolicyValue[__size];
      RETAIN = new ServantRetentionPolicyValue(0);
      NON_RETAIN = new ServantRetentionPolicyValue(1);
   }
}
