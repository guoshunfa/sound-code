package com.sun.org.apache.bcel.internal.generic;

public class F2I extends ConversionInstruction {
   public F2I() {
      super((short)139);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitConversionInstruction(this);
      v.visitF2I(this);
   }
}
