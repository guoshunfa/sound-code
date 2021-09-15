package com.sun.org.apache.bcel.internal.generic;

public class BASTORE extends ArrayInstruction implements StackConsumer {
   public BASTORE() {
      super((short)84);
   }

   public void accept(Visitor v) {
      v.visitStackConsumer(this);
      v.visitExceptionThrower(this);
      v.visitTypedInstruction(this);
      v.visitArrayInstruction(this);
      v.visitBASTORE(this);
   }
}
