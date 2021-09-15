package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;

final class CurrentCall extends FunctionCall {
   public CurrentCall(QName fname) {
      super(fname);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      methodGen.getInstructionList().append(methodGen.loadCurrentNode());
   }
}
