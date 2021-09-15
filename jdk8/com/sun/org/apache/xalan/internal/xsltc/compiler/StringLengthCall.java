package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class StringLengthCall extends FunctionCall {
   public StringLengthCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this.argumentCount() > 0) {
         this.argument().translate(classGen, methodGen);
      } else {
         il.append(methodGen.loadContextNode());
         Type.Node.translateTo(classGen, methodGen, Type.String);
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "getStringLength", "(Ljava/lang/String;)I"))));
   }
}
