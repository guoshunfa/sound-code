package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Copy extends Instruction {
   private UseAttributeSets _useSets;

   public void parseContents(Parser parser) {
      String useSets = this.getAttribute("use-attribute-sets");
      if (useSets.length() > 0) {
         if (!Util.isValidQNames(useSets)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", useSets, this);
            parser.reportError(3, err);
         }

         this._useSets = new UseAttributeSets(useSets, parser);
      }

      this.parseChildren(parser);
   }

   public void display(int indent) {
      this.indent(indent);
      Util.println("Copy");
      this.indent(indent + 4);
      this.displayContents(indent + 4);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._useSets != null) {
         this._useSets.typeCheck(stable);
      }

      this.typeCheckContents(stable);
      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      LocalVariableGen name = methodGen.addLocalVariable2("name", Util.getJCRefType("Ljava/lang/String;"), (InstructionHandle)null);
      LocalVariableGen length = methodGen.addLocalVariable2("length", Util.getJCRefType("I"), (InstructionHandle)null);
      il.append(methodGen.loadDOM());
      il.append(methodGen.loadCurrentNode());
      il.append(methodGen.loadHandler());
      int cpy = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "shallowCopy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)Ljava/lang/String;");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpy, 3)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      name.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(name.getIndex()))));
      BranchHandle ifBlock1 = il.append((BranchInstruction)(new IFNULL((InstructionHandle)null)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(name.getIndex())));
      int lengthMethod = cpg.addMethodref("java.lang.String", "length", "()I");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(lengthMethod)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      length.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ISTORE(length.getIndex()))));
      BranchHandle ifBlock4 = il.append((BranchInstruction)(new IFEQ((InstructionHandle)null)));
      if (this._useSets != null) {
         SyntaxTreeNode parent = this.getParent();
         if (!(parent instanceof LiteralElement) && !(parent instanceof LiteralElement)) {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(length.getIndex())));
            BranchHandle ifBlock2 = il.append((BranchInstruction)(new IFEQ((InstructionHandle)null)));
            this._useSets.translate(classGen, methodGen);
            ifBlock2.setTarget(il.append(NOP));
         } else {
            this._useSets.translate(classGen, methodGen);
         }
      }

      ifBlock4.setTarget(il.append(NOP));
      this.translateContents(classGen, methodGen);
      length.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(length.getIndex()))));
      BranchHandle ifBlock3 = il.append((BranchInstruction)(new IFEQ((InstructionHandle)null)));
      il.append(methodGen.loadHandler());
      name.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(name.getIndex()))));
      il.append(methodGen.endElement());
      InstructionHandle end = il.append(NOP);
      ifBlock1.setTarget(end);
      ifBlock3.setTarget(end);
      methodGen.removeLocalVariable(name);
      methodGen.removeLocalVariable(length);
   }
}
