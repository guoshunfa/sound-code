package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class NodeSetType extends Type {
   protected NodeSetType() {
   }

   public String toString() {
      return "node-set";
   }

   public boolean identicalTo(Type other) {
      return this == other;
   }

   public String toSignature() {
      return "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return new com.sun.org.apache.bcel.internal.generic.ObjectType("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator");
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else if (type == Type.Boolean) {
         this.translateTo(classGen, methodGen, (BooleanType)type);
      } else if (type == Type.Real) {
         this.translateTo(classGen, methodGen, (RealType)type);
      } else if (type == Type.Node) {
         this.translateTo(classGen, methodGen, (NodeType)type);
      } else if (type == Type.Reference) {
         this.translateTo(classGen, methodGen, (ReferenceType)type);
      } else if (type == Type.Object) {
         this.translateTo(classGen, methodGen, (ObjectType)type);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      InstructionList il = methodGen.getInstructionList();
      ConstantPoolGen cpg = classGen.getConstantPool();
      int convert;
      if (clazz.getName().equals("org.w3c.dom.NodeList")) {
         il.append(classGen.loadTranslet());
         il.append(methodGen.loadDOM());
         convert = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "nodeList2Iterator", "(Lorg/w3c/dom/NodeList;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
         il.append((Instruction)(new INVOKESTATIC(convert)));
      } else if (clazz.getName().equals("org.w3c.dom.Node")) {
         il.append(classGen.loadTranslet());
         il.append(methodGen.loadDOM());
         convert = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "node2Iterator", "(Lorg/w3c/dom/Node;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
         il.append((Instruction)(new INVOKESTATIC(convert)));
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      InstructionList il = methodGen.getInstructionList();
      FlowList falsel = this.translateToDesynthesized(classGen, methodGen, type);
      il.append(ICONST_1);
      BranchHandle truec = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      falsel.backPatch(il.append(ICONST_0));
      truec.setTarget(il.append(NOP));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      InstructionList il = methodGen.getInstructionList();
      this.getFirstNode(classGen, methodGen);
      il.append((Instruction)DUP);
      BranchHandle falsec = il.append((BranchInstruction)(new IFLT((InstructionHandle)null)));
      Type.Node.translateTo(classGen, methodGen, (Type)type);
      BranchHandle truec = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      falsec.setTarget(il.append((Instruction)POP));
      il.append((CompoundInstruction)(new PUSH(classGen.getConstantPool(), "")));
      truec.setTarget(il.append(NOP));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
      this.translateTo(classGen, methodGen, Type.String);
      Type.String.translateTo(classGen, methodGen, Type.Real);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeType type) {
      this.getFirstNode(classGen, methodGen);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
      methodGen.getInstructionList().append(NOP);
   }

   public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      InstructionList il = methodGen.getInstructionList();
      this.getFirstNode(classGen, methodGen);
      return new FlowList(il.append((BranchInstruction)(new IFLT((InstructionHandle)null))));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
      methodGen.getInstructionList().append(NOP);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      String className = clazz.getName();
      il.append(methodGen.loadDOM());
      il.append((Instruction)SWAP);
      int next;
      if (className.equals("org.w3c.dom.Node")) {
         next = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
         il.append((Instruction)(new INVOKEINTERFACE(next, 2)));
      } else if (!className.equals("org.w3c.dom.NodeList") && !className.equals("java.lang.Object")) {
         if (className.equals("java.lang.String")) {
            next = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
            int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
            il.append((Instruction)(new INVOKEINTERFACE(next, 1)));
            il.append((Instruction)(new INVOKEINTERFACE(index, 2)));
         } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), className);
            classGen.getParser().reportError(2, err);
         }
      } else {
         next = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
         il.append((Instruction)(new INVOKEINTERFACE(next, 2)));
      }

   }

   private void getFirstNode(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I"), 1)));
   }

   public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateTo(classGen, methodGen, Type.Reference);
   }

   public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
      methodGen.getInstructionList().append(NOP);
   }

   public String getClassName() {
      return "com.sun.org.apache.xml.internal.dtm.DTMAxisIterator";
   }

   public Instruction LOAD(int slot) {
      return new ALOAD(slot);
   }

   public Instruction STORE(int slot) {
      return new ASTORE(slot);
   }
}
