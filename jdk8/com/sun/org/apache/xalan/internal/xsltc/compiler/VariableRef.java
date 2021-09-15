package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;

final class VariableRef extends VariableRefBase {
   public VariableRef(Variable variable) {
      super(variable);
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (!this._type.implementedAsMethod()) {
         String name = this._variable.getEscapedName();
         String signature = this._type.toSignature();
         if (this._variable.isLocal()) {
            if (classGen.isExternal()) {
               Closure variableClosure;
               for(variableClosure = this._closure; variableClosure != null && !variableClosure.inInnerClass(); variableClosure = variableClosure.getParentClosure()) {
               }

               if (variableClosure != null) {
                  il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
                  il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref(variableClosure.getInnerClassName(), name, signature))));
               } else {
                  il.append(this._variable.loadInstruction());
               }
            } else {
               il.append(this._variable.loadInstruction());
            }
         } else {
            String className = classGen.getClassName();
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass(className))));
            }

            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref(className, name, signature))));
         }

         if (this._variable.getType() instanceof NodeSetType) {
            int clone = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "cloneIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(clone, 1)));
         }

      }
   }
}
