package com.sun.org.apache.bcel.internal.generic;

public class FNEG extends ArithmeticInstruction {
   public FNEG() {
      super((short)118);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitFNEG(this);
   }
}
