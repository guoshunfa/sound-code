package com.sun.org.apache.bcel.internal.generic;

public class LADD extends ArithmeticInstruction {
   public LADD() {
      super((short)97);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitLADD(this);
   }
}
