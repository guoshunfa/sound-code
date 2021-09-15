package com.sun.org.apache.bcel.internal.generic;

public class BREAKPOINT extends Instruction {
   public BREAKPOINT() {
      super((short)202, (short)1);
   }

   public void accept(Visitor v) {
      v.visitBREAKPOINT(this);
   }
}
