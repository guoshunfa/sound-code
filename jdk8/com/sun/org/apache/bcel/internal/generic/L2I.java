package com.sun.org.apache.bcel.internal.generic;

public class L2I extends ConversionInstruction {
   public L2I() {
      super((short)136);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitConversionInstruction(this);
      v.visitL2I(this);
   }
}
