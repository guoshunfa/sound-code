package com.sun.org.apache.regexp.internal;

import java.io.Serializable;

public class REProgram implements Serializable {
   static final int OPT_HASBACKREFS = 1;
   char[] instruction;
   int lenInstruction;
   char[] prefix;
   int flags;
   int maxParens;

   public REProgram(char[] instruction) {
      this(instruction, instruction.length);
   }

   public REProgram(int parens, char[] instruction) {
      this(instruction, instruction.length);
      this.maxParens = parens;
   }

   public REProgram(char[] instruction, int lenInstruction) {
      this.maxParens = -1;
      this.setInstructions(instruction, lenInstruction);
   }

   public char[] getInstructions() {
      if (this.lenInstruction != 0) {
         char[] ret = new char[this.lenInstruction];
         System.arraycopy(this.instruction, 0, ret, 0, this.lenInstruction);
         return ret;
      } else {
         return null;
      }
   }

   public void setInstructions(char[] instruction, int lenInstruction) {
      this.instruction = instruction;
      this.lenInstruction = lenInstruction;
      this.flags = 0;
      this.prefix = null;
      if (instruction != null && lenInstruction != 0) {
         if (lenInstruction >= 3 && instruction[0] == '|') {
            int next = instruction[2];
            if (instruction[next + 0] == 'E' && lenInstruction >= 6 && instruction[3] == 'A') {
               int lenAtom = instruction[4];
               this.prefix = new char[lenAtom];
               System.arraycopy(instruction, 6, this.prefix, 0, lenAtom);
            }
         }

         for(int i = 0; i < lenInstruction; i += 3) {
            switch(instruction[i + 0]) {
            case '#':
               this.flags |= 1;
               return;
            case 'A':
               i += instruction[i + 1];
               break;
            case '[':
               i += instruction[i + 1] * 2;
            }
         }
      }

   }
}
