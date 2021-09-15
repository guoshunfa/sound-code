package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Vector;

final class NotCall extends FunctionCall {
   public NotCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      this.argument().translate(classGen, methodGen);
      il.append(ICONST_1);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)IXOR);
   }

   public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList il = methodGen.getInstructionList();
      Expression exp = this.argument();
      exp.translateDesynthesized(classGen, methodGen);
      BranchHandle gotoh = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      this._trueList = exp._falseList;
      this._falseList = exp._trueList;
      this._falseList.add(gotoh);
   }
}
