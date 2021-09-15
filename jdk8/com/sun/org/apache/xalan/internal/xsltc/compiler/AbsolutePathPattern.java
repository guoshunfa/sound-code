package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class AbsolutePathPattern extends LocationPathPattern {
   private final RelativePathPattern _left;

   public AbsolutePathPattern(RelativePathPattern left) {
      this._left = left;
      if (left != null) {
         left.setParent(this);
      }

   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      if (this._left != null) {
         this._left.setParser(parser);
      }

   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      return this._left == null ? Type.Root : this._left.typeCheck(stable);
   }

   public boolean isWildcard() {
      return false;
   }

   public StepPattern getKernelPattern() {
      return this._left != null ? this._left.getKernelPattern() : null;
   }

   public void reduceKernelPattern() {
      this._left.reduceKernelPattern();
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._left != null) {
         if (this._left instanceof StepPattern) {
            LocalVariableGen local = methodGen.addLocalVariable2("apptmp", Util.getJCRefType("I"), (InstructionHandle)null);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            local.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ISTORE(local.getIndex()))));
            this._left.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            local.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(local.getIndex()))));
            methodGen.removeLocalVariable(local);
         } else {
            this._left.translate(classGen, methodGen);
         }
      }

      int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
      int getType = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      InstructionHandle begin = il.append(methodGen.loadDOM());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getParent, 2)));
      if (this._left instanceof AncestorPattern) {
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getType, 2)));
      il.append((CompoundInstruction)(new PUSH(cpg, 9)));
      BranchHandle skip = il.append((BranchInstruction)(new IF_ICMPEQ((InstructionHandle)null)));
      this._falseList.add(il.append((BranchInstruction)(new GOTO_W((InstructionHandle)null))));
      skip.setTarget(il.append(NOP));
      if (this._left != null) {
         this._left.backPatchTrueList(begin);
         if (this._left instanceof AncestorPattern) {
            AncestorPattern ancestor = (AncestorPattern)this._left;
            this._falseList.backPatch(ancestor.getLoopHandle());
         }

         this._falseList.append(this._left._falseList);
      }

   }

   public String toString() {
      return "absolutePathPattern(" + (this._left != null ? this._left.toString() : ")");
   }
}
