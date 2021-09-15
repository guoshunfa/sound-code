package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.DataInputStream;
import java.io.IOException;

public final class Field extends FieldOrMethod {
   public Field(Field c) {
      super(c);
   }

   Field(DataInputStream file, ConstantPool constant_pool) throws IOException, ClassFormatException {
      super(file, constant_pool);
   }

   public Field(int access_flags, int name_index, int signature_index, Attribute[] attributes, ConstantPool constant_pool) {
      super(access_flags, name_index, signature_index, attributes, constant_pool);
   }

   public void accept(Visitor v) {
      v.visitField(this);
   }

   public final ConstantValue getConstantValue() {
      for(int i = 0; i < this.attributes_count; ++i) {
         if (this.attributes[i].getTag() == 1) {
            return (ConstantValue)this.attributes[i];
         }
      }

      return null;
   }

   public final String toString() {
      String access = Utility.accessToString(this.access_flags);
      access = access.equals("") ? "" : access + " ";
      String signature = Utility.signatureToString(this.getSignature());
      String name = this.getName();
      StringBuffer buf = new StringBuffer(access + signature + " " + name);
      ConstantValue cv = this.getConstantValue();
      if (cv != null) {
         buf.append(" = " + cv);
      }

      for(int i = 0; i < this.attributes_count; ++i) {
         Attribute a = this.attributes[i];
         if (!(a instanceof ConstantValue)) {
            buf.append(" [" + a.toString() + "]");
         }
      }

      return buf.toString();
   }

   public final Field copy(ConstantPool constant_pool) {
      return (Field)this.copy_(constant_pool);
   }

   public Type getType() {
      return Type.getReturnType(this.getSignature());
   }
}
