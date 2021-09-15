package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.Visitor;
import java.io.DataOutputStream;
import java.io.IOException;

abstract class MarkerInstruction extends Instruction {
   public MarkerInstruction() {
      super((short)-1, (short)0);
   }

   public void accept(Visitor v) {
   }

   public final int consumeStack(ConstantPoolGen cpg) {
      return 0;
   }

   public final int produceStack(ConstantPoolGen cpg) {
      return 0;
   }

   public Instruction copy() {
      return this;
   }

   public final void dump(DataOutputStream out) throws IOException {
   }
}
