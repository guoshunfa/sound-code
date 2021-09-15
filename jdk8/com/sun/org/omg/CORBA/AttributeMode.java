package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class AttributeMode implements IDLEntity {
   private int __value;
   private static int __size = 2;
   private static AttributeMode[] __array;
   public static final int _ATTR_NORMAL = 0;
   public static final AttributeMode ATTR_NORMAL;
   public static final int _ATTR_READONLY = 1;
   public static final AttributeMode ATTR_READONLY;

   public int value() {
      return this.__value;
   }

   public static AttributeMode from_int(int var0) {
      if (var0 >= 0 && var0 < __size) {
         return __array[var0];
      } else {
         throw new BAD_PARAM();
      }
   }

   protected AttributeMode(int var1) {
      this.__value = var1;
      __array[this.__value] = this;
   }

   static {
      __array = new AttributeMode[__size];
      ATTR_NORMAL = new AttributeMode(0);
      ATTR_READONLY = new AttributeMode(1);
   }
}
