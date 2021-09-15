package com.sun.org.apache.bcel.internal.generic;

public class ISUB extends ArithmeticInstruction {
   public ISUB() {
      super((short)100);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitISUB(this);
   }
}
