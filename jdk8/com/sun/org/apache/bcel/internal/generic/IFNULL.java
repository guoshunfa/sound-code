package com.sun.org.apache.bcel.internal.generic;

public class IFNULL extends IfInstruction {
   IFNULL() {
   }

   public IFNULL(InstructionHandle target) {
      super((short)198, target);
   }

   public IfInstruction negate() {
      return new IFNONNULL(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFNULL(this);
   }
}
