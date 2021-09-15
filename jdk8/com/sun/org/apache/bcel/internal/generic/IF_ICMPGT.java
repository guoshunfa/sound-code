package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGT extends IfInstruction {
   IF_ICMPGT() {
   }

   public IF_ICMPGT(InstructionHandle target) {
      super((short)163, target);
   }

   public IfInstruction negate() {
      return new IF_ICMPLE(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIF_ICMPGT(this);
   }
}
