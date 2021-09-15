package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ElementAvailableCall extends FunctionCall {
   public ElementAvailableCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this.argument() instanceof LiteralExpr) {
         return this._type = Type.Boolean;
      } else {
         ErrorMsg err = new ErrorMsg("NEED_LITERAL_ERR", "element-available", this);
         throw new TypeCheckError(err);
      }
   }

   public Object evaluateAtCompileTime() {
      return this.getResult() ? Boolean.TRUE : Boolean.FALSE;
   }

   public boolean getResult() {
      try {
         LiteralExpr arg = (LiteralExpr)this.argument();
         String qname = arg.getValue();
         int index = qname.indexOf(58);
         String localName = index > 0 ? qname.substring(index + 1) : qname;
         return this.getParser().elementSupported(arg.getNamespace(), localName);
      } catch (ClassCastException var5) {
         return false;
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      boolean result = this.getResult();
      methodGen.getInstructionList().append((CompoundInstruction)(new PUSH(cpg, result)));
   }
}
