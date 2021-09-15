package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class NotFoundReason implements IDLEntity {
   private int __value;
   private static int __size = 3;
   private static NotFoundReason[] __array;
   public static final int _missing_node = 0;
   public static final NotFoundReason missing_node;
   public static final int _not_context = 1;
   public static final NotFoundReason not_context;
   public static final int _not_object = 2;
   public static final NotFoundReason not_object;

   public int value() {
      return this.__value;
   }

   public static NotFoundReason from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected NotFoundReason(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new NotFoundReason[__size];
      missing_node = new NotFoundReason(0);
      not_context = new NotFoundReason(1);
      not_object = new NotFoundReason(2);
   }
}
