package com.sun.org.apache.bcel.internal.generic;

public class ILOAD extends LoadInstruction {
   ILOAD() {
      super((short)21, (short)26);
   }

   public ILOAD(int n) {
      super((short)21, (short)26, n);
   }

   public void accept(Visitor v) {
      super.accept(v);
      v.visitILOAD(this);
   }
}
