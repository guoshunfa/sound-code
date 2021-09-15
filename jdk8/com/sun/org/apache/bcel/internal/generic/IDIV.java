package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class IDIV extends ArithmeticInstruction implements ExceptionThrower {
   public IDIV() {
      super((short)108);
   }

   public Class[] getExceptions() {
      return new Class[]{ExceptionConstants.ARITHMETIC_EXCEPTION};
   }

   public void accept(Visitor v) {
      v.visitExceptionThrower(this);
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitIDIV(this);
   }
}
