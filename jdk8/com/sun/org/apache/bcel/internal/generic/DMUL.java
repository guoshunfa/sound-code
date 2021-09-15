package com.sun.org.apache.bcel.internal.generic;

public class DMUL extends ArithmeticInstruction {
   public DMUL() {
      super((short)107);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitDMUL(this);
   }
}
