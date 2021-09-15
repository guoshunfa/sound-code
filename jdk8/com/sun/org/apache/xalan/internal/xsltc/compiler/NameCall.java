package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class NameCall extends NameBase {
   public NameCall(QName fname) {
      super(fname);
   }

   public NameCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int getName = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeNameX", "(I)Ljava/lang/String;");
      super.translate(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getName, 2)));
   }
}
