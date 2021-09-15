package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;

final class LastCall extends FunctionCall {
   public LastCall(QName fname) {
      super(fname);
   }

   public boolean hasPositionCall() {
      return true;
   }

   public boolean hasLastCall() {
      return true;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      if (methodGen instanceof CompareGenerator) {
         il.append(((CompareGenerator)methodGen).loadLastNode());
      } else if (methodGen instanceof TestGenerator) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(3)));
      } else {
         ConstantPoolGen cpg = classGen.getConstantPool();
         int getLast = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "getLast", "()I");
         il.append(methodGen.loadIterator());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getLast, 1)));
      }

   }
}
