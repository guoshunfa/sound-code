package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
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

final class AncestorPattern extends RelativePathPattern {
   private final Pattern _left;
   private final RelativePathPattern _right;
   private InstructionHandle _loop;

   public AncestorPattern(RelativePathPattern right) {
      this((Pattern)null, right);
   }

   public AncestorPattern(Pattern left, RelativePathPattern right) {
      this._left = left;
      (this._right = right).setParent(this);
      if (left != null) {
         left.setParent(this);
      }

   }

   public InstructionHandle getLoopHandle() {
      return this._loop;
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      if (this._left != null) {
         this._left.setParser(parser);
      }

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
      if (this._left != null) {
         this._left.typeCheck(stable);
      }

      return this._right.typeCheck(stable);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      LocalVariableGen local = methodGen.addLocalVariable2("app", Util.getJCRefType("I"), il.getEnd());
      com.sun.org.apache.bcel.internal.generic.Instruction loadLocal = new ILOAD(local.getIndex());
      com.sun.org.apache.bcel.internal.generic.Instruction storeLocal = new ISTORE(local.getIndex());
      if (this._right instanceof StepPattern) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)storeLocal);
         this._right.translate(classGen, methodGen);
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal);
      } else {
         this._right.translate(classGen, methodGen);
         if (this._right instanceof AncestorPattern) {
            il.append(methodGen.loadDOM());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         }
      }

      if (this._left != null) {
         int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
         InstructionHandle parent = il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getParent, 2)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)storeLocal);
         this._falseList.add(il.append((BranchInstruction)(new IFLT((InstructionHandle)null))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal);
         this._left.translate(classGen, methodGen);
         SyntaxTreeNode p = this.getParent();
         if (p != null && !(p instanceof Instruction) && !(p instanceof TopLevelElement)) {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal);
         }

         BranchHandle exit = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
         this._loop = il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)loadLocal);
         local.setEnd(this._loop);
         il.append((BranchInstruction)(new GOTO(parent)));
         exit.setTarget(il.append(NOP));
         this._left.backPatchFalseList(this._loop);
         this._trueList.append(this._left._trueList);
      } else {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)POP2);
      }

      if (this._right instanceof AncestorPattern) {
         AncestorPattern ancestor = (AncestorPattern)this._right;
         this._falseList.backPatch(ancestor.getLoopHandle());
      }

      this._trueList.append(this._right._trueList);
      this._falseList.append(this._right._falseList);
   }

   public String toString() {
      return "AncestorPattern(" + this._left + ", " + this._right + ')';
   }
}
