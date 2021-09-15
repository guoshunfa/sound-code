package com.sun.org.apache.bcel.internal.generic;

public class IAND extends ArithmeticInstruction {
   public IAND() {
      super((short)126);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitIAND(this);
   }
}
