package com.sun.org.apache.bcel.internal.generic;

public class IFGE extends IfInstruction {
   IFGE() {
   }

   public IFGE(InstructionHandle target) {
      super((short)156, target);
   }

   public IfInstruction negate() {
      return new IFLT(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFGE(this);
   }
}
