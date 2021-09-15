package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ParentPattern extends RelativePathPattern {
   private final Pattern _left;
   private final RelativePathPattern _right;

   public ParentPattern(Pattern left, RelativePathPattern right) {
      (this._left = left).setParent(this);
      (this._right = right).setParent(this);
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      this._left.setParser(parser);
      this._right.setParser(parser);
   }

   public boolean isWildcard() {
      return false;
   }

   public StepPattern getKernelPattern() {
      return this._right.getKernelPattern();
   }

   public void reduceKernelPattern() {
      this._right.reduceKernelPattern();
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._left.typeCheck(stable);
      return this._right.typeCheck(stable);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      LocalVariableGen local = methodGen.addLocalVariable2("ppt", Util.getJCRefType("I"), (InstructionHandle)null);
      com.sun.org.apache.bcel.internal.generic.Instruction loadLocal = new ILOAD(local.getIndex());
      com.sun.org.apache.bcel.internal.generic.Instruction storeLocal = new ISTORE(local.getIndex());
      if (this._right.isWildcard()) {
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
      } else if (this._right instanceof StepPattern) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         local.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)storeLocal));
         this._right.translate(classGen, methodGen);
         il.append(methodGen.loadDOM());
         local.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal));
      } else {
         this._right.translate(classGen, methodGen);
         if (this._right instanceof AncestorPattern) {
            il.append(methodGen.loadDOM());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         }
      }

      int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getParent, 2)));
      SyntaxTreeNode p = this.getParent();
      if (p != null && !(p instanceof Instruction) && !(p instanceof TopLevelElement)) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         InstructionHandle storeInst = il.append((com.sun.org.apache.bcel.internal.generic.Instruction)storeLocal);
         if (local.getStart() == null) {
            local.setStart(storeInst);
         }

         this._left.translate(classGen, methodGen);
         il.append(methodGen.loadDOM());
         local.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal));
      } else {
         this._left.translate(classGen, methodGen);
      }

      methodGen.removeLocalVariable(local);
      if (this._right instanceof AncestorPattern) {
         AncestorPattern ancestor = (AncestorPattern)this._right;
         this._left.backPatchFalseList(ancestor.getLoopHandle());
      }

      this._trueList.append(this._right._trueList.append(this._left._trueList));
      this._falseList.append(this._right._falseList.append(this._left._falseList));
   }

   public String toString() {
      return "Parent(" + this._left + ", " + this._right + ')';
   }
}
