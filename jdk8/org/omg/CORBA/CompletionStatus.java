package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class CompletionStatus implements IDLEntity {
   public static final int _COMPLETED_YES = 0;
   public static final int _COMPLETED_NO = 1;
   public static final int _COMPLETED_MAYBE = 2;
   public static final CompletionStatus COMPLETED_YES = new CompletionStatus(0);
   public static final CompletionStatus COMPLETED_NO = new CompletionStatus(1);
   public static final CompletionStatus COMPLETED_MAYBE = new CompletionStatus(2);
   private int _value;

   public int value() {
      return this._value;
   }

   public static CompletionStatus from_int(int var0) {
      switch(var0) {
      case 0:
         return COMPLETED_YES;
      case 1:
         return COMPLETED_NO;
      case 2:
         return COMPLETED_MAYBE;
      default:
         throw new BAD_PARAM();
      }
   }

   private CompletionStatus(int var1) {
      this._value = var1;
   }
}
