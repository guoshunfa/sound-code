package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class MONITOREXIT extends Instruction implements ExceptionThrower, StackConsumer {
   public MONITOREXIT() {
      super((short)195, (short)1);
   }

   public Class[] getExceptions() {
      return new Class[]{ExceptionConstants.NULL_POINTER_EXCEPTION};
   }

   public void accept(Visitor v) {
      v.visitExceptionThrower(this);
      v.visitStackConsumer(this);
      v.visitMONITOREXIT(this);
   }
}
