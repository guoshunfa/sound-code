package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGE extends IfInstruction {
   IF_ICMPGE() {
   }

   public IF_ICMPGE(InstructionHandle target) {
      super((short)162, target);
   }

   public IfInstruction negate() {
      return new IF_ICMPLT(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIF_ICMPGE(this);
   }
}
