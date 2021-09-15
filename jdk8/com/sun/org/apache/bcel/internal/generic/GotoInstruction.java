package com.sun.org.apache.bcel.internal.generic;

public abstract class GotoInstruction extends BranchInstruction implements UnconditionalBranch {
   GotoInstruction(short opcode, InstructionHandle target) {
      super(opcode, target);
   }

   GotoInstruction() {
   }
}
