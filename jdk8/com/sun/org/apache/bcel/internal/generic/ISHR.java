package com.sun.org.apache.bcel.internal.generic;

public class ISHR extends ArithmeticInstruction {
   public ISHR() {
      super((short)122);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitArithmeticInstruction(this);
      v.visitISHR(this);
   }
}
