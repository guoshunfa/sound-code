package com.sun.org.apache.regexp.internal;

import java.io.PrintWriter;
import java.util.Hashtable;

public class REDebugCompiler extends RECompiler {
   static Hashtable hashOpcode = new Hashtable();

   String opcodeToString(char opcode) {
      String ret = (String)hashOpcode.get(new Integer(opcode));
      if (ret == null) {
         ret = "OP_????";
      }

      return ret;
   }

   String charToString(char c) {
      return c >= ' ' && c <= 127 ? String.valueOf(c) : "\\" + c;
   }

   String nodeToString(int node) {
      char opcode = this.instruction[node + 0];
      int opdata = this.instruction[node + 1];
      return this.opcodeToString(opcode) + ", opdata = " + opdata;
   }

   public void dumpProgram(PrintWriter p) {
      for(int i = 0; i < this.lenInstruction; p.println("")) {
         char opcode = this.instruction[i + 0];
         char opdata = this.instruction[i + 1];
         short next = (short)this.instruction[i + 2];
         p.print(i + ". " + this.nodeToString(i) + ", next = ");
         if (next == 0) {
            p.print("none");
         } else {
            p.print(i + next);
         }

         i += 3;
         if (opcode == '[') {
            p.print(", [");
            int rangeCount = opdata;

            for(int r = 0; r < rangeCount; ++r) {
               char charFirst = this.instruction[i++];
               char charLast = this.instruction[i++];
               if (charFirst == charLast) {
                  p.print(this.charToString(charFirst));
               } else {
                  p.print(this.charToString(charFirst) + "-" + this.charToString(charLast));
               }
            }

            p.print("]");
         }

         if (opcode == 'A') {
            p.print(", \"");
            int var10 = opdata;

            while(var10-- != 0) {
               p.print(this.charToString(this.instruction[i++]));
            }

            p.print("\"");
         }
      }

   }

   static {
      hashOpcode.put(new Integer(56), "OP_RELUCTANTSTAR");
      hashOpcode.put(new Integer(61), "OP_RELUCTANTPLUS");
      hashOpcode.put(new Integer(47), "OP_RELUCTANTMAYBE");
      hashOpcode.put(new Integer(69), "OP_END");
      hashOpcode.put(new Integer(94), "OP_BOL");
      hashOpcode.put(new Integer(36), "OP_EOL");
      hashOpcode.put(new Integer(46), "OP_ANY");
      hashOpcode.put(new Integer(91), "OP_ANYOF");
      hashOpcode.put(new Integer(124), "OP_BRANCH");
      hashOpcode.put(new Integer(65), "OP_ATOM");
      hashOpcode.put(new Integer(42), "OP_STAR");
      hashOpcode.put(new Integer(43), "OP_PLUS");
      hashOpcode.put(new Integer(63), "OP_MAYBE");
      hashOpcode.put(new Integer(78), "OP_NOTHING");
      hashOpcode.put(new Integer(71), "OP_GOTO");
      hashOpcode.put(new Integer(92), "OP_ESCAPE");
      hashOpcode.put(new Integer(40), "OP_OPEN");
      hashOpcode.put(new Integer(41), "OP_CLOSE");
      hashOpcode.put(new Integer(35), "OP_BACKREF");
      hashOpcode.put(new Integer(80), "OP_POSIXCLASS");
      hashOpcode.put(new Integer(60), "OP_OPEN_CLUSTER");
      hashOpcode.put(new Integer(62), "OP_CLOSE_CLUSTER");
   }
}
