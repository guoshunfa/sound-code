package com.sun.org.apache.bcel.internal.generic;

public class FCONST extends Instruction implements ConstantPushInstruction, TypedInstruction {
   private float value;

   FCONST() {
   }

   public FCONST(float f) {
      super((short)11, (short)1);
      if ((double)f == 0.0D) {
         this.opcode = 11;
      } else if ((double)f == 1.0D) {
         this.opcode = 12;
      } else {
         if ((double)f != 2.0D) {
            throw new ClassGenException("FCONST can be used only for 0.0, 1.0 and 2.0: " + f);
         }

         this.opcode = 13;
      }

      this.value = f;
   }

   public Number getValue() {
      return new Float(this.value);
   }

   public Type getType(ConstantPoolGen cp) {
      return Type.FLOAT;
   }

   public void accept(Visitor v) {
      v.visitPushInstruction(this);
      v.visitStackProducer(this);
      v.visitTypedInstruction(this);
      v.visitConstantPushInstruction(this);
      v.visitFCONST(this);
   }
}
