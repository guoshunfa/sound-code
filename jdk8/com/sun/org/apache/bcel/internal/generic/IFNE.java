package com.sun.org.apache.bcel.internal.generic;

public class IFNE extends IfInstruction {
   IFNE() {
   }

   public IFNE(InstructionHandle target) {
      super((short)154, target);
   }

   public IfInstruction negate() {
      return new IFEQ(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFNE(this);
   }
}
