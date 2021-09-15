package com.sun.org.apache.bcel.internal.generic;

public class LOR extends ArithmeticInstruction {
   public LOR() {
      super((short)129);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitLOR(this);
   }
}
