package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class BooleanExpr extends Expression {
   private boolean _value;

   public BooleanExpr(boolean value) {
      this._value = value;
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._type = Type.Boolean;
      return this._type;
   }

   public String toString() {
      return this._value ? "true()" : "false()";
   }

   public boolean getValue() {
      return this._value;
   }

   public boolean contextDependent() {
      return false;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((CompoundInstruction)(new PUSH(cpg, this._value)));
   }

   public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      if (this._value) {
         il.append(NOP);
      } else {
         this._falseList.add(il.append((BranchInstruction)(new GOTO((InstructionHandle)null))));
      }

   }
}
