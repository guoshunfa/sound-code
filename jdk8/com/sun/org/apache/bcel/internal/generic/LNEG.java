package com.sun.org.apache.bcel.internal.generic;

public class LNEG extends ArithmeticInstruction {
   public LNEG() {
      super((short)117);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitLNEG(this);
   }
}
