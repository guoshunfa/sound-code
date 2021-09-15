package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class ReferenceType extends Type {
   protected ReferenceType() {
   }

   public String toString() {
      return "reference";
   }

   public boolean identicalTo(Type other) {
      return this == other;
   }

   public String toSignature() {
      return "Ljava/lang/Object;";
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return com.sun.org.apache.bcel.internal.generic.Type.OBJECT;
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else if (type == Type.Real) {
         this.translateTo(classGen, methodGen, (RealType)type);
      } else if (type == Type.Boolean) {
         this.translateTo(classGen, methodGen, (BooleanType)type);
      } else if (type == Type.NodeSet) {
         this.translateTo(classGen, methodGen, (NodeSetType)type);
      } else if (type == Type.Node) {
         this.translateTo(classGen, methodGen, (NodeType)type);
      } else if (type == Type.ResultTree) {
         this.translateTo(classGen, methodGen, (ResultTreeType)type);
      } else if (type == Type.Object) {
         this.translateTo(classGen, methodGen, (ObjectType)type);
      } else if (type != Type.Reference) {
         ErrorMsg err = new ErrorMsg("INTERNAL_ERR", type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      int current = methodGen.getLocalIndex("current");
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (current < 0) {
         il.append((CompoundInstruction)(new PUSH(cpg, 0)));
      } else {
         il.append((Instruction)(new ILOAD(current)));
      }

      il.append(methodGen.loadDOM());
      int stringF = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringF", "(Ljava/lang/Object;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
      il.append((Instruction)(new INVOKESTATIC(stringF)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append(methodGen.loadDOM());
      int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "numberF", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)D");
      il.append((Instruction)(new INVOKESTATIC(index)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "booleanF", "(Ljava/lang/Object;)Z");
      il.append((Instruction)(new INVOKESTATIC(index)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeSetType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      il.append((Instruction)(new INVOKESTATIC(index)));
      index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      il.append((Instruction)(new INVOKEINTERFACE(index, 1)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeType type) {
      this.translateTo(classGen, methodGen, Type.NodeSet);
      Type.NodeSet.translateTo(classGen, methodGen, (Type)type);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ResultTreeType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToResultTree", "(Ljava/lang/Object;)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      il.append((Instruction)(new INVOKESTATIC(index)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
      methodGen.getInstructionList().append(NOP);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int referenceToLong = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToLong", "(Ljava/lang/Object;)J");
      int referenceToDouble = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToDouble", "(Ljava/lang/Object;)D");
      int referenceToBoolean = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToBoolean", "(Ljava/lang/Object;)Z");
      if (clazz.getName().equals("java.lang.Object")) {
         il.append(NOP);
      } else if (clazz == Double.TYPE) {
         il.append((Instruction)(new INVOKESTATIC(referenceToDouble)));
      } else if (clazz.getName().equals("java.lang.Double")) {
         il.append((Instruction)(new INVOKESTATIC(referenceToDouble)));
         Type.Real.translateTo(classGen, methodGen, Type.Reference);
      } else if (clazz == Float.TYPE) {
         il.append((Instruction)(new INVOKESTATIC(referenceToDouble)));
         il.append((Instruction)D2F);
      } else {
         int index;
         if (clazz.getName().equals("java.lang.String")) {
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToString", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
            il.append(methodGen.loadDOM());
            il.append((Instruction)(new INVOKESTATIC(index)));
         } else if (clazz.getName().equals("org.w3c.dom.Node")) {
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNode", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/Node;");
            il.append(methodGen.loadDOM());
            il.append((Instruction)(new INVOKESTATIC(index)));
         } else if (clazz.getName().equals("org.w3c.dom.NodeList")) {
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeList", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/NodeList;");
            il.append(methodGen.loadDOM());
            il.append((Instruction)(new INVOKESTATIC(index)));
         } else if (clazz.getName().equals("com.sun.org.apache.xalan.internal.xsltc.DOM")) {
            this.translateTo(classGen, methodGen, Type.ResultTree);
         } else if (clazz == Long.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToLong)));
         } else if (clazz == Integer.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToLong)));
            il.append((Instruction)L2I);
         } else if (clazz == Short.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToLong)));
            il.append((Instruction)L2I);
            il.append((Instruction)I2S);
         } else if (clazz == Byte.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToLong)));
            il.append((Instruction)L2I);
            il.append((Instruction)I2B);
         } else if (clazz == Character.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToLong)));
            il.append((Instruction)L2I);
            il.append((Instruction)I2C);
         } else if (clazz == java.lang.Boolean.TYPE) {
            il.append((Instruction)(new INVOKESTATIC(referenceToBoolean)));
         } else if (clazz.getName().equals("java.lang.Boolean")) {
            il.append((Instruction)(new INVOKESTATIC(referenceToBoolean)));
            Type.Boolean.translateTo(classGen, methodGen, Type.Reference);
         } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
         }
      }

   }

   public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      if (clazz.getName().equals("java.lang.Object")) {
         methodGen.getInstructionList().append(NOP);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
         classGen.getParser().reportError(2, err);
      }

   }

   public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      InstructionList il = methodGen.getInstructionList();
      this.translateTo(classGen, methodGen, type);
      return new FlowList(il.append((BranchInstruction)(new IFEQ((InstructionHandle)null))));
   }

   public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
   }

   public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
   }

   public Instruction LOAD(int slot) {
      return new ALOAD(slot);
   }

   public Instruction STORE(int slot) {
      return new ASTORE(slot);
   }
}
