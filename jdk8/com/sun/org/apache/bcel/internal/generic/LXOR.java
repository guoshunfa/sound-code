package com.sun.org.apache.bcel.internal.generic;

public class LXOR extends ArithmeticInstruction {
   public LXOR() {
      super((short)131);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitLXOR(this);
   }
}
