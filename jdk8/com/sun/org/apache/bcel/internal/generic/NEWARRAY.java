package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class NEWARRAY extends Instruction implements AllocationInstruction, ExceptionThrower, StackProducer {
   private byte type;

   NEWARRAY() {
   }

   public NEWARRAY(byte type) {
      super((short)188, (short)2);
      this.type = type;
   }

   public NEWARRAY(BasicType type) {
      this(type.getType());
   }

   public void dump(DataOutputStream out) throws IOException {
      out.writeByte(this.opcode);
      out.writeByte(this.type);
   }

   public final byte getTypecode() {
      return this.type;
   }

   public final Type getType() {
      return new ArrayType(BasicType.getType(this.type), 1);
   }

   public String toString(boolean verbose) {
      return super.toString(verbose) + " " + Constants.TYPE_NAMES[this.type];
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.type = bytes.readByte();
      this.length = 2;
   }

   public Class[] getExceptions() {
      return new Class[]{ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION};
   }

   public void accept(Visitor v) {
      v.visitAllocationInstruction(this);
      v.visitExceptionThrower(this);
      v.visitStackProducer(this);
      v.visitNEWARRAY(this);
   }
}
