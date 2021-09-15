package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class LDC extends CPInstruction implements PushInstruction, ExceptionThrower, TypedInstruction {
   LDC() {
   }

   public LDC(int index) {
      super((short)19, index);
      this.setSize();
   }

   protected final void setSize() {
      if (this.index <= 255) {
         this.opcode = 18;
         this.length = 2;
      } else {
         this.opcode = 19;
         this.length = 3;
      }

   }

   public void dump(DataOutputStream out) throws IOException {
      out.writeByte(this.opcode);
      if (this.length == 2) {
         out.writeByte(this.index);
      } else {
         out.writeShort(this.index);
      }

   }

   public final void setIndex(int index) {
      super.setIndex(index);
      this.setSize();
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.length = 2;
      this.index = bytes.readUnsignedByte();
   }

   public Object getValue(ConstantPoolGen cpg) {
      Constant c = cpg.getConstantPool().getConstant(this.index);
      switch(c.getTag()) {
      case 3:
         return new Integer(((ConstantInteger)c).getBytes());
      case 4:
         return new Float(((ConstantFloat)c).getBytes());
      case 8:
         int i = ((ConstantString)c).getStringIndex();
         c = cpg.getConstantPool().getConstant(i);
         return ((ConstantUtf8)c).getBytes();
      default:
         throw new RuntimeException("Unknown or invalid constant type at " + this.index);
      }
   }

   public Type getType(ConstantPoolGen cpg) {
      switch(cpg.getConstantPool().getConstant(this.index).getTag()) {
      case 3:
         return Type.INT;
      case 4:
         return Type.FLOAT;
      case 8:
         return Type.STRING;
      default:
         throw new RuntimeException("Unknown or invalid constant type at " + this.index);
      }
   }

   public Class[] getExceptions() {
      return ExceptionConstants.EXCS_STRING_RESOLUTION;
   }

   public void accept(Visitor v) {
      v.visitStackProducer(this);
      v.visitPushInstruction(this);
      v.visitExceptionThrower(this);
      v.visitTypedInstruction(this);
      v.visitCPInstruction(this);
      v.visitLDC(this);
   }
}
