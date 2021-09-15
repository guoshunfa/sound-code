package com.sun.org.apache.bcel.internal.generic;

public abstract class IfInstruction extends BranchInstruction implements StackConsumer {
   IfInstruction() {
   }

   protected IfInstruction(short opcode, InstructionHandle target) {
      super(opcode, target);
   }

   public abstract IfInstruction negate();
}
