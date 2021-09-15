package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class LDIV extends ArithmeticInstruction implements ExceptionThrower {
   public LDIV() {
      super((short)109);
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
      v.visitLDIV(this);
   }
}
