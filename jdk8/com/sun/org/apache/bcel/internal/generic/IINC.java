package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class IINC extends LocalVariableInstruction {
   private boolean wide;
   private int c;

   IINC() {
   }

   public IINC(int n, int c) {
      this.opcode = 132;
      this.length = 3;
      this.setIndex(n);
      this.setIncrement(c);
   }

   public void dump(DataOutputStream out) throws IOException {
      if (this.wide) {
         out.writeByte(196);
      }

      out.writeByte(this.opcode);
      if (this.wide) {
         out.writeShort(this.n);
         out.writeShort(this.c);
      } else {
         out.writeByte(this.n);
         out.writeByte(this.c);
      }

   }

   private final void setWide() {
      if (this.wide = this.n > 65535 || Math.abs(this.c) > 127) {
         this.length = 6;
      } else {
         this.length = 3;
      }

   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.wide = wide;
      if (wide) {
         this.length = 6;
         this.n = bytes.readUnsignedShort();
         this.c = bytes.readShort();
      } else {
         this.length = 3;
         this.n = bytes.readUnsignedByte();
         this.c = bytes.readByte();
      }

   }

   public String toString(boolean verbose) {
      return super.toString(verbose) + " " + this.c;
   }

   public final void setIndex(int n) {
      if (n < 0) {
         throw new ClassGenException("Negative index value: " + n);
      } else {
         this.n = n;
         this.setWide();
      }
   }

   public final int getIncrement() {
      return this.c;
   }

   public final void setIncrement(int c) {
      this.c = c;
      this.setWide();
   }

   public Type getType(ConstantPoolGen cp) {
      return Type.INT;
   }

   public void accept(Visitor v) {
      v.visitLocalVariableInstruction(this);
      v.visitIINC(this);
   }
}
