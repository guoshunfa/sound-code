package com.sun.org.apache.bcel.internal.generic;

public class POP2 extends StackInstruction implements PopInstruction {
   public POP2() {
      super((short)88);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitPopInstruction(this);
      v.visitStackInstruction(this);
      v.visitPOP2(this);
   }
}
