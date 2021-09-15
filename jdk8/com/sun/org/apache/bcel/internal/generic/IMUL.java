package com.sun.org.apache.bcel.internal.generic;

public class IMUL extends ArithmeticInstruction {
   public IMUL() {
      super((short)104);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitIMUL(this);
   }
}
