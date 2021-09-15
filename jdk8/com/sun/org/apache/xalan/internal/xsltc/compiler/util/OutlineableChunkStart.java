package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkStart extends MarkerInstruction {
   public static final Instruction OUTLINEABLECHUNKSTART = new OutlineableChunkStart();

   private OutlineableChunkStart() {
   }

   public String getName() {
      return OutlineableChunkStart.class.getName();
   }

   public String toString() {
      return this.getName();
   }

   public String toString(boolean verbose) {
      return this.getName();
   }
}
