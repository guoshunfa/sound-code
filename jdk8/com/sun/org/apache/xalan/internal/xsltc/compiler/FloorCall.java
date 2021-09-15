package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class FloorCall extends FunctionCall {
   public FloorCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      this.argument().translate(classGen, methodGen);
      methodGen.getInstructionList().append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(classGen.getConstantPool().addMethodref("java.lang.Math", "floor", "(D)D"))));
   }
}
