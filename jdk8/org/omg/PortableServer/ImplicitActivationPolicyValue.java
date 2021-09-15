package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ImplicitActivationPolicyValue implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static ImplicitActivationPolicyValue[] __array;
   public static final int _IMPLICIT_ACTIVATION = 0;
   public static final ImplicitActivationPolicyValue IMPLICIT_ACTIVATION;
   public static final int _NO_IMPLICIT_ACTIVATION = 1;
   public static final ImplicitActivationPolicyValue NO_IMPLICIT_ACTIVATION;

   public int value() {
      return this.__value;
   }

   public static ImplicitActivationPolicyValue from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected ImplicitActivationPolicyValue(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new ImplicitActivationPolicyValue[__size];
      IMPLICIT_ACTIVATION = new ImplicitActivationPolicyValue(0);
      NO_IMPLICIT_ACTIVATION = new ImplicitActivationPolicyValue(1);
   }
}
