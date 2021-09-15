package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public final class VoidType extends Type {
   protected VoidType() {
   }

   public String toString() {
      return "void";
   }

   public boolean identicalTo(Type other) {
      return this == other;
   }

   public String toSignature() {
      return "V";
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return null;
   }

   public Instruction POP() {
      return NOP;
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      InstructionList il = methodGen.getInstructionList();
      il.append((CompoundInstruction)(new PUSH(classGen.getConstantPool(), "")));
   }

   public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      if (!clazz.getName().equals("void")) {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
         classGen.getParser().reportError(2, err);
      }

   }
}
