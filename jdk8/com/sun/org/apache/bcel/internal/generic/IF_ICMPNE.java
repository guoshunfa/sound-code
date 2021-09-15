package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPNE extends IfInstruction {
   IF_ICMPNE() {
   }

   public IF_ICMPNE(InstructionHandle target) {
      super((short)160, target);
   }

   public IfInstruction negate() {
      return new IF_ICMPEQ(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIF_ICMPNE(this);
   }
}
