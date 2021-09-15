package com.sun.org.apache.bcel.internal.generic;

public class LCMP extends Instruction implements TypedInstruction, StackProducer, StackConsumer {
   public LCMP() {
      super((short)148, (short)1);
   }

   public Type getType(ConstantPoolGen cp) {
      return Type.LONG;
   }

   public void accept(Visitor v) {
      v.visitTypedInstruction(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitLCMP(this);
   }
}
