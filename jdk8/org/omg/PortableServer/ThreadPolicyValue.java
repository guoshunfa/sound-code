package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ThreadPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static ThreadPolicyValue[] __array;
   public static final int _ORB_CTRL_MODEL = 0;
   public static final ThreadPolicyValue ORB_CTRL_MODEL;
   public static final int _SINGLE_THREAD_MODEL = 1;
   public static final ThreadPolicyValue SINGLE_THREAD_MODEL;

   public int value() {
      return this.__value;
   }

   public static ThreadPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected ThreadPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new ThreadPolicyValue[__size];
      ORB_CTRL_MODEL = new ThreadPolicyValue(0);
      SINGLE_THREAD_MODEL = new ThreadPolicyValue(1);
   }
}
