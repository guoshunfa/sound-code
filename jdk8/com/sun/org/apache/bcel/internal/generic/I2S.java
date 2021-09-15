package com.sun.org.apache.bcel.internal.generic;

public class I2S extends ConversionInstruction {
   public I2S() {
      super((short)147);
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitConversionInstruction(this);
      v.visitI2S(this);
   }
}
