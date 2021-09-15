package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class LocalNameCall extends NameBase {
   public LocalNameCall(QName fname) {
      super(fname);
   }

   public LocalNameCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int getNodeName = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
      int getLocalName = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "getLocalName", "(Ljava/lang/String;)Ljava/lang/String;");
      super.translate(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getNodeName, 2)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(getLocalName)));
   }
}
