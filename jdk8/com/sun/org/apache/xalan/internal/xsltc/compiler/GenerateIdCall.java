package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class GenerateIdCall extends FunctionCall {
   public GenerateIdCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      if (this.argumentCount() == 0) {
         il.append(methodGen.loadContextNode());
      } else {
         this.argument().translate(classGen, methodGen);
      }

      ConstantPoolGen cpg = classGen.getConstantPool();
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "generate_idF", "(I)Ljava/lang/String;"))));
   }
}
