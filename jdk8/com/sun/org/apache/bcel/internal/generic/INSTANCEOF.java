package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class INSTANCEOF extends CPInstruction implements LoadClass, ExceptionThrower, StackProducer, StackConsumer {
   INSTANCEOF() {
   }

   public INSTANCEOF(int index) {
      super((short)193, index);
   }

   public Class[] getExceptions() {
      return ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION;
   }

   public ObjectType getLoadClassType(ConstantPoolGen cpg) {
      Type t = this.getType(cpg);
      if (t instanceof ArrayType) {
         t = ((ArrayType)t).getBasicType();
      }

      return t instanceof ObjectType ? (ObjectType)t : null;
   }

   public void accept(Visitor v) {
      v.visitLoadClass(this);
      v.visitExceptionThrower(this);
      v.visitStackProducer(this);
      v.visitStackConsumer(this);
      v.visitTypedInstruction(this);
      v.visitCPInstruction(this);
      v.visitINSTANCEOF(this);
   }
}
