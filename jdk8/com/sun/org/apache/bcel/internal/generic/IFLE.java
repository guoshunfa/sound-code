package com.sun.org.apache.bcel.internal.generic;

public class IFLE extends IfInstruction {
   IFLE() {
   }

   public IFLE(InstructionHandle target) {
      super((short)158, target);
   }

   public IfInstruction negate() {
      return new IFGT(this.target);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitBranchInstruction(this);
      v.visitIfInstruction(this);
      v.visitIFLE(this);
   }
}
