package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdAssignmentPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static IdAssignmentPolicyValue[] __array;
   public static final int _USER_ID = 0;
   public static final IdAssignmentPolicyValue USER_ID;
   public static final int _SYSTEM_ID = 1;
   public static final IdAssignmentPolicyValue SYSTEM_ID;

   public int value() {
      return this.__value;
   }

   public static IdAssignmentPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected IdAssignmentPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new IdAssignmentPolicyValue[__size];
      USER_ID = new IdAssignmentPolicyValue(0);
      SYSTEM_ID = new IdAssignmentPolicyValue(1);
   }
}
