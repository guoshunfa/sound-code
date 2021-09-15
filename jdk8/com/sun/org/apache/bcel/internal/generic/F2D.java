package com.sun.org.apache.bcel.internal.generic;

public class F2D extends ConversionInstruction {
   public F2D() {
      super((short)141);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitConversionInstruction(this);
      v.visitF2D(this);
   }
}
