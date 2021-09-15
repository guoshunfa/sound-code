package com.sun.org.apache.bcel.internal.generic;

public class FREM extends ArithmeticInstruction {
   public FREM() {
      super((short)114);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitFREM(this);
   }
}
