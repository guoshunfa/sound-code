package com.sun.org.apache.bcel.internal.generic;

public class IFLT extends IfInstruction {
   IFLT() {
   }

   public IFLT(InstructionHandle target) {
      super((short)155, target);
   }

   public IfInstruction negate() {
      return new IFGE(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFLT(this);
   }
}
