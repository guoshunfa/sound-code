package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

abstract class IdKeyPattern extends LocationPathPattern {
   protected RelativePathPattern _left = null;
   private String _index = null;
   private String _value = null;

   public IdKeyPattern(String index, String value) {
      this._index = index;
      this._value = value;
   }

   public String getIndexName() {
      return this._index;
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      return Type.NodeSet;
   }

   public boolean isWildcard() {
      return false;
   }

   public void setLeft(RelativePathPattern left) {
      this._left = left;
   }

   public StepPattern getKernelPattern() {
      return null;
   }

   public void reduceKernelPattern() {
   }

   public String toString() {
      return "id/keyPattern(" + this._index + ", " + this._value + ')';
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int getKeyIndex = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
      int lookupId = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsID", "(ILjava/lang/Object;)I");
      int lookupKey = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsKey", "(ILjava/lang/Object;)I");
      int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
      il.append(classGen.loadTranslet());
      il.append((CompoundInstruction)(new PUSH(cpg, this._index)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(getKeyIndex)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
      il.append((CompoundInstruction)(new PUSH(cpg, this._value)));
      if (this instanceof IdPattern) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(lookupId)));
      } else {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(lookupKey)));
      }

      this._trueList.add(il.append((BranchInstruction)(new IFNE((InstructionHandle)null))));
      this._falseList.add(il.append((BranchInstruction)(new GOTO((InstructionHandle)null))));
   }
}
