package com.sun.org.apache.bcel.internal.generic;

public class DNEG extends ArithmeticInstruction {
   public DNEG() {
      super((short)119);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitDNEG(this);
   }
}
