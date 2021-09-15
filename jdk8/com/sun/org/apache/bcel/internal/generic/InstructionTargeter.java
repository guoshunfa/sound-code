package com.sun.org.apache.bcel.internal.generic;

public interface InstructionTargeter {
   boolean containsTarget(InstructionHandle var1);

   void updateTarget(InstructionHandle var1, InstructionHandle var2);
}
