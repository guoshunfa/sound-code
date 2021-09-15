package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ConstantValue extends Attribute {
   private int constantvalue_index;

   public ConstantValue(ConstantValue c) {
      this(c.getNameIndex(), c.getLength(), c.getConstantValueIndex(), c.getConstantPool());
   }

   ConstantValue(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
      this(name_index, length, file.readUnsignedShort(), constant_pool);
   }

   public ConstantValue(int name_index, int length, int constantvalue_index, ConstantPool constant_pool) {
      super((byte)1, name_index, length, constant_pool);
      this.constantvalue_index = constantvalue_index;
   }

   public void accept(Visitor v) {
      v.visitConstantValue(this);
   }

   public final void dump(DataOutputStream file) throws IOException {
      super.dump(file);
      file.writeShort(this.constantvalue_index);
   }

   public final int getConstantValueIndex() {
      return this.constantvalue_index;
   }

   public final void setConstantValueIndex(int constantvalue_index) {
      this.constantvalue_index = constantvalue_index;
   }

   public final String toString() {
      Constant c = this.constant_pool.getConstant(this.constantvalue_index);
      String buf;
      switch(c.getTag()) {
      case 3:
         buf = "" + ((ConstantInteger)c).getBytes();
         break;
      case 4:
         buf = "" + ((ConstantFloat)c).getBytes();
         break;
      case 5:
         buf = "" + ((ConstantLong)c).getBytes();
         break;
      case 6:
         buf = "" + ((ConstantDouble)c).getBytes();
         break;
      case 7:
      default:
         throw new IllegalStateException("Type of ConstValue invalid: " + c);
      case 8:
         int i = ((ConstantString)c).getStringIndex();
         c = this.constant_pool.getConstant(i, (byte)1);
         buf = "\"" + Utility.convertString(((ConstantUtf8)c).getBytes()) + "\"";
      }

      return buf;
   }

   public Attribute copy(ConstantPool constant_pool) {
      ConstantValue c = (ConstantValue)this.clone();
      c.constant_pool = constant_pool;
      return c;
   }
}
