package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class RequestProcessingPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 3;
   private static RequestProcessingPolicyValue[] __array;
   public static final int _USE_ACTIVE_OBJECT_MAP_ONLY = 0;
   public static final RequestProcessingPolicyValue USE_ACTIVE_OBJECT_MAP_ONLY;
   public static final int _USE_DEFAULT_SERVANT = 1;
   public static final RequestProcessingPolicyValue USE_DEFAULT_SERVANT;
   public static final int _USE_SERVANT_MANAGER = 2;
   public static final RequestProcessingPolicyValue USE_SERVANT_MANAGER;

   public int value() {
      return this.__value;
   }

   public static RequestProcessingPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected RequestProcessingPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new RequestProcessingPolicyValue[__size];
      USE_ACTIVE_OBJECT_MAP_ONLY = new RequestProcessingPolicyValue(0);
      USE_DEFAULT_SERVANT = new RequestProcessingPolicyValue(1);
      USE_SERVANT_MANAGER = new RequestProcessingPolicyValue(2);
   }
}
