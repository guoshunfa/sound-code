package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class ARRAYLENGTH extends Instruction implements ExceptionThrower, StackProducer {
   public ARRAYLENGTH() {
      super((short)190, (short)1);
   }

   public Class[] getExceptions() {
      return new Class[]{ExceptionConstants.NULL_POINTER_EXCEPTION};
   }

   public void accept(Visitor v) {
      v.visitExceptionThrower(this);
      v.visitStackProducer(this);
      v.visitARRAYLENGTH(this);
   }
}
