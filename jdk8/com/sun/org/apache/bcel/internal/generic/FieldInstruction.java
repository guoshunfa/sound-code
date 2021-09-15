package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;

public abstract class FieldInstruction extends FieldOrMethod implements TypedInstruction {
   FieldInstruction() {
   }

   protected FieldInstruction(short opcode, int index) {
      super(opcode, index);
   }

   public String toString(ConstantPool cp) {
      return Constants.OPCODE_NAMES[this.opcode] + " " + cp.constantToString(this.index, (byte)9);
   }

   protected int getFieldSize(ConstantPoolGen cpg) {
      return this.getType(cpg).getSize();
   }

   public Type getType(ConstantPoolGen cpg) {
      return this.getFieldType(cpg);
   }

   public Type getFieldType(ConstantPoolGen cpg) {
      return Type.getType(this.getSignature(cpg));
   }

   public String getFieldName(ConstantPoolGen cpg) {
      return this.getName(cpg);
   }
}
