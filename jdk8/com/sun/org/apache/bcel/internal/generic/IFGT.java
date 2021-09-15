package com.sun.org.apache.bcel.internal.generic;

public class IFGT extends IfInstruction {
   IFGT() {
   }

   public IFGT(InstructionHandle target) {
      super((short)157, target);
   }

   public IfInstruction negate() {
      return new IFLE(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFGT(this);
   }
}
