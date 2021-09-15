package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class NumberCall extends FunctionCall {
   public NumberCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this.argumentCount() > 0) {
         this.argument().typeCheck(stable);
      }

      return this._type = Type.Real;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      Type targ;
      if (this.argumentCount() == 0) {
         il.append(methodGen.loadContextNode());
         targ = Type.Node;
      } else {
         Expression arg = this.argument();
         arg.translate(classGen, methodGen);
         arg.startIterator(classGen, methodGen);
         targ = arg.getType();
      }

      if (!targ.identicalTo(Type.Real)) {
         targ.translateTo(classGen, methodGen, Type.Real);
      }

   }
}
