package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class TopLevelElement extends SyntaxTreeNode {
   protected Vector _dependencies = null;

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      return this.typeCheckContents(stable);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ErrorMsg msg = new ErrorMsg("NOT_IMPLEMENTED_ERR", this.getClass(), this);
      this.getParser().reportError(2, msg);
   }

   public InstructionList compile(ClassGenerator classGen, MethodGenerator methodGen) {
      InstructionList save = methodGen.getInstructionList();
      InstructionList result;
      methodGen.setInstructionList(result = new InstructionList());
      this.translate(classGen, methodGen);
      methodGen.setInstructionList(save);
      return result;
   }

   public void display(int indent) {
      this.indent(indent);
      Util.println("TopLevelElement");
      this.displayContents(indent + 4);
   }

   public void addDependency(TopLevelElement other) {
      if (this._dependencies == null) {
         this._dependencies = new Vector();
      }

      if (!this._dependencies.contains(other)) {
         this._dependencies.addElement(other);
      }

   }

   public Vector getDependencies() {
      return this._dependencies;
   }
}
