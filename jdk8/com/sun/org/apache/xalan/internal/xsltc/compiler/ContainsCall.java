package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ContainsCall extends FunctionCall {
   private Expression _base = null;
   private Expression _token = null;

   public ContainsCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public boolean isBoolean() {
      return true;
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this.argumentCount() != 2) {
         throw new TypeCheckError("ILLEGAL_ARG_ERR", this.getName(), this);
      } else {
         this._base = this.argument(0);
         Type baseType = this._base.typeCheck(stable);
         if (baseType != Type.String) {
            this._base = new CastExpr(this._base, Type.String);
         }

         this._token = this.argument(1);
         Type tokenType = this._token.typeCheck(stable);
         if (tokenType != Type.String) {
            this._token = new CastExpr(this._token, Type.String);
         }

         return this._type = Type.Boolean;
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateDesynthesized(classGen, methodGen);
      this.synthesize(classGen, methodGen);
   }

   public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      this._base.translate(classGen, methodGen);
      this._token.translate(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.String", "indexOf", "(Ljava/lang/String;)I"))));
      this._falseList.add(il.append((BranchInstruction)(new IFLT((InstructionHandle)null))));
   }
}
