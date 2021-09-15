package com.sun.org.apache.bcel.internal.generic;

public class IUSHR extends ArithmeticInstruction {
   public IUSHR() {
      super((short)124);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitIUSHR(this);
   }
}
