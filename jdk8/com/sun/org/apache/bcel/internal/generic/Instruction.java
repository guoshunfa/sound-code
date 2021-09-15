package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class Instruction implements Cloneable, Serializable {
   protected short length = 1;
   protected short opcode = -1;
   private static InstructionComparator cmp;

   Instruction() {
   }

   public Instruction(short opcode, short length) {
      this.length = length;
      this.opcode = opcode;
   }

   public void dump(DataOutputStream out) throws IOException {
      out.writeByte(this.opcode);
   }

   public String getName() {
      return Constants.OPCODE_NAMES[this.opcode];
   }

   public String toString(boolean verbose) {
      return verbose ? this.getName() + "[" + this.opcode + "](" + this.length + ")" : this.getName();
   }

   public String toString() {
      return this.toString(true);
   }

   public String toString(ConstantPool cp) {
      return this.toString(false);
   }

   public Instruction copy() {
      Instruction i = null;
      if (InstructionConstants.INSTRUCTIONS[this.getOpcode()] != null) {
         i = this;
      } else {
         try {
            i = (Instruction)this.clone();
         } catch (CloneNotSupportedException var3) {
            System.err.println((Object)var3);
         }
      }

      return i;
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
   }

   public static final Instruction readInstruction(ByteSequence bytes) throws IOException {
      boolean wide = false;
      short opcode = (short)bytes.readUnsignedByte();
      Instruction obj = null;
      if (opcode == 196) {
         wide = true;
         opcode = (short)bytes.readUnsignedByte();
      }

      if (InstructionConstants.INSTRUCTIONS[opcode] != null) {
         return InstructionConstants.INSTRUCTIONS[opcode];
      } else {
         Class clazz;
         try {
            clazz = Class.forName(className(opcode));
         } catch (ClassNotFoundException var7) {
            throw new ClassGenException("Illegal opcode detected.");
         }

         try {
            obj = (Instruction)clazz.newInstance();
            if (wide && !(obj instanceof LocalVariableInstruction) && !(obj instanceof IINC) && !(obj instanceof RET)) {
               throw new Exception("Illegal opcode after wide: " + opcode);
            } else {
               obj.setOpcode(opcode);
               obj.initFromFile(bytes, wide);
               return obj;
            }
         } catch (Exception var6) {
            throw new ClassGenException(var6.toString());
         }
      }
   }

   private static final String className(short opcode) {
      String name = Constants.OPCODE_NAMES[opcode].toUpperCase();

      try {
         int len = name.length();
         char ch1 = name.charAt(len - 2);
         char ch2 = name.charAt(len - 1);
         if (ch1 == '_' && ch2 >= '0' && ch2 <= '5') {
            name = name.substring(0, len - 2);
         }

         if (name.equals("ICONST_M1")) {
            name = "ICONST";
         }
      } catch (StringIndexOutOfBoundsException var5) {
         System.err.println((Object)var5);
      }

      return "com.sun.org.apache.bcel.internal.generic." + name;
   }

   public int consumeStack(ConstantPoolGen cpg) {
      return Constants.CONSUME_STACK[this.opcode];
   }

   public int produceStack(ConstantPoolGen cpg) {
      return Constants.PRODUCE_STACK[this.opcode];
   }

   public short getOpcode() {
      return this.opcode;
   }

   public int getLength() {
      return this.length;
   }

   private void setOpcode(short opcode) {
      this.opcode = opcode;
   }

   void dispose() {
   }

   public abstract void accept(Visitor var1);

   public static InstructionComparator getComparator() {
      return cmp;
   }

   public static void setComparator(InstructionComparator c) {
      cmp = c;
   }

   public boolean equals(Object that) {
      return that instanceof Instruction ? cmp.equals(this, (Instruction)that) : false;
   }

   static {
      cmp = InstructionComparator.DEFAULT;
   }
}
