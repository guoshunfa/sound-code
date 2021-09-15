package com.sun.org.apache.bcel.internal.generic;

public class L2D extends ConversionInstruction {
   public L2D() {
      super((short)138);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitConversionInstruction(this);
      v.visitL2D(this);
   }
}
