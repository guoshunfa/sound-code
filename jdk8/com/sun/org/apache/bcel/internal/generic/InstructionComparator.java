package com.sun.org.apache.bcel.internal.generic;

public interface InstructionComparator {
   InstructionComparator DEFAULT = new InstructionComparator() {
      public boolean equals(Instruction i1, Instruction i2) {
         if (i1.opcode == i2.opcode) {
            if (!(i1 instanceof Select)) {
               if (i1 instanceof BranchInstruction) {
                  return ((BranchInstruction)i1).target == ((BranchInstruction)i2).target;
               }

               if (i1 instanceof ConstantPushInstruction) {
                  return ((ConstantPushInstruction)i1).getValue().equals(((ConstantPushInstruction)i2).getValue());
               }

               if (i1 instanceof IndexedInstruction) {
                  return ((IndexedInstruction)i1).getIndex() == ((IndexedInstruction)i2).getIndex();
               }

               if (i1 instanceof NEWARRAY) {
                  return ((NEWARRAY)i1).getTypecode() == ((NEWARRAY)i2).getTypecode();
               }

               return true;
            }

            InstructionHandle[] t1 = ((Select)i1).getTargets();
            InstructionHandle[] t2 = ((Select)i2).getTargets();
            if (t1.length == t2.length) {
               for(int i = 0; i < t1.length; ++i) {
                  if (t1[i] != t2[i]) {
                     return false;
                  }
               }

               return true;
            }
         }

         return false;
      }
   };

   boolean equals(Instruction var1, Instruction var2);
}
