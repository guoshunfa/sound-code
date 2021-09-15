package com.sun.org.apache.bcel.internal.generic;

public class LSUB extends ArithmeticInstruction {
   public LSUB() {
      super((short)101);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitLSUB(this);
   }
}
