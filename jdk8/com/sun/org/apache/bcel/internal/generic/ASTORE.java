package com.sun.org.apache.bcel.internal.generic;

public class ASTORE extends StoreInstruction {
   ASTORE() {
      super((short)58, (short)75);
   }

   public ASTORE(int n) {
      super((short)58, (short)75, n);
   }

   public void accept(Visitor v) {
      super.accept(v);
      v.visitASTORE(this);
   }
}
