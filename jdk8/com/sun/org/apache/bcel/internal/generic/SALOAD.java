package com.sun.org.apache.bcel.internal.generic;

public class SALOAD extends ArrayInstruction implements StackProducer {
   public SALOAD() {
      super((short)53);
   }

   public void accept(Visitor v) {
      v.visitStackProducer(this);
      v.visitExceptionThrower(this);
      v.visitTypedInstruction(this);
      v.visitArrayInstruction(this);
      v.visitSALOAD(this);
   }
}
