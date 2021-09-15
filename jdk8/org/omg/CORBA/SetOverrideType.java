package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class SetOverrideType implements IDLEntity {
   public static final int _SET_OVERRIDE = 0;
   public static final int _ADD_OVERRIDE = 1;
   public static final SetOverrideType SET_OVERRIDE = new SetOverrideType(0);
   public static final SetOverrideType ADD_OVERRIDE = new SetOverrideType(1);
   private int _value;

   public int value() {
      return this._value;
   }

   public static SetOverrideType from_int(int var0) {
      switch(var0) {
      case 0:
         return SET_OVERRIDE;
      case 1:
         return ADD_OVERRIDE;
      default:
         throw new BAD_PARAM();
      }
   }

   protected SetOverrideType(int var1) {
      this._value = var1;
   }
}
