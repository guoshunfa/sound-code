package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkEnd extends MarkerInstruction {
   public static final Instruction OUTLINEABLECHUNKEND = new OutlineableChunkEnd();

   private OutlineableChunkEnd() {
   }

   public String getName() {
      return OutlineableChunkEnd.class.getName();
   }

   public String toString() {
      return this.getName();
   }

   public String toString(boolean verbose) {
      return this.getName();
   }
}
