package com.sun.org.apache.bcel.internal.generic;

public class FALOAD extends ArrayInstruction implements StackProducer {
   public FALOAD() {
      super((short)48);
   }

   public void accept(Visitor v) {
      v.visitStackProducer(this);
      v.visitExceptionThrower(this);
      v.visitTypedInstruction(this);
      v.visitArrayInstruction(this);
      v.visitFALOAD(this);
   }
}
