package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;

public final class ResultTreeType extends Type {
   private final String _methodName;

   protected ResultTreeType() {
      this._methodName = null;
   }

   public ResultTreeType(String methodName) {
      this._methodName = methodName;
   }

   public String toString() {
      return "result-tree";
   }

   public boolean identicalTo(Type other) {
      return other instanceof ResultTreeType;
   }

   public String toSignature() {
      return "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
   }

   public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
      return Util.getJCRefType(this.toSignature());
   }

   public String getMethodName() {
      return this._methodName;
   }

   public boolean implementedAsMethod() {
      return this._methodName != null;
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
      if (type == Type.String) {
         this.translateTo(classGen, methodGen, (StringType)type);
      } else if (type == Type.Boolean) {
         this.translateTo(classGen, methodGen, (BooleanType)type);
      } else if (type == Type.Real) {
         this.translateTo(classGen, methodGen, (RealType)type);
      } else if (type == Type.NodeSet) {
         this.translateTo(classGen, methodGen, (NodeSetType)type);
      } else if (type == Type.Reference) {
         this.translateTo(classGen, methodGen, (ReferenceType)type);
      } else if (type == Type.Object) {
         this.translateTo(classGen, methodGen, (ObjectType)type);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)POP);
      il.append(ICONST_1);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._methodName == null) {
         int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValue", "()Ljava/lang/String;");
         il.append((Instruction)(new INVOKEINTERFACE(index, 1)));
      } else {
         String className = classGen.getClassName();
         int current = methodGen.getLocalIndex("current");
         il.append(classGen.loadTranslet());
         if (classGen.isExternal()) {
            il.append((Instruction)(new CHECKCAST(cpg.addClass(className))));
         }

         il.append((Instruction)DUP);
         il.append((Instruction)(new GETFIELD(cpg.addFieldref(className, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"))));
         int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "<init>", "()V");
         il.append((Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler"))));
         il.append((Instruction)DUP);
         il.append((Instruction)DUP);
         il.append((Instruction)(new INVOKESPECIAL(index)));
         LocalVariableGen handler = methodGen.addLocalVariable("rt_to_string_handler", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"), (InstructionHandle)null, (InstructionHandle)null);
         handler.setStart(il.append((Instruction)(new ASTORE(handler.getIndex()))));
         index = cpg.addMethodref(className, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
         il.append((Instruction)(new INVOKEVIRTUAL(index)));
         handler.setEnd(il.append((Instruction)(new ALOAD(handler.getIndex()))));
         index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;");
         il.append((Instruction)(new INVOKEVIRTUAL(index)));
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
      this.translateTo(classGen, methodGen, Type.String);
      Type.String.translateTo(classGen, methodGen, Type.Real);
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._methodName == null) {
         il.append(NOP);
      } else {
         String className = classGen.getClassName();
         int current = methodGen.getLocalIndex("current");
         il.append(classGen.loadTranslet());
         if (classGen.isExternal()) {
            il.append((Instruction)(new CHECKCAST(cpg.addClass(className))));
         }

         il.append(methodGen.loadDOM());
         il.append(methodGen.loadDOM());
         int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
         il.append((CompoundInstruction)(new PUSH(cpg, 32)));
         il.append((CompoundInstruction)(new PUSH(cpg, false)));
         il.append((Instruction)(new INVOKEINTERFACE(index, 3)));
         il.append((Instruction)DUP);
         LocalVariableGen newDom = methodGen.addLocalVariable("rt_to_reference_dom", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), (InstructionHandle)null, (InstructionHandle)null);
         il.append((Instruction)(new CHECKCAST(cpg.addClass("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"))));
         newDom.setStart(il.append((Instruction)(new ASTORE(newDom.getIndex()))));
         index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
         il.append((Instruction)(new INVOKEINTERFACE(index, 1)));
         il.append((Instruction)DUP);
         il.append((Instruction)DUP);
         LocalVariableGen domBuilder = methodGen.addLocalVariable("rt_to_reference_handler", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), (InstructionHandle)null, (InstructionHandle)null);
         domBuilder.setStart(il.append((Instruction)(new ASTORE(domBuilder.getIndex()))));
         index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
         il.append((Instruction)(new INVOKEINTERFACE(index, 1)));
         index = cpg.addMethodref(className, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
         il.append((Instruction)(new INVOKEVIRTUAL(index)));
         domBuilder.setEnd(il.append((Instruction)(new ALOAD(domBuilder.getIndex()))));
         index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
         il.append((Instruction)(new INVOKEINTERFACE(index, 1)));
         newDom.setEnd(il.append((Instruction)(new ALOAD(newDom.getIndex()))));
      }

   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, NodeSetType type) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append((Instruction)DUP);
      il.append(classGen.loadTranslet());
      il.append((Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;"))));
      il.append(classGen.loadTranslet());
      il.append((Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;"))));
      il.append(classGen.loadTranslet());
      il.append((Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I"))));
      il.append(classGen.loadTranslet());
      il.append((Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;"))));
      int mapping = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setupMapping", "([Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
      il.append((Instruction)(new INVOKEINTERFACE(mapping, 5)));
      il.append((Instruction)DUP);
      int iter = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      il.append((Instruction)(new INVOKEINTERFACE(iter, 1)));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ObjectType type) {
      methodGen.getInstructionList().append(NOP);
   }

   public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
      InstructionList il = methodGen.getInstructionList();
      this.translateTo(classGen, methodGen, Type.Boolean);
      return new FlowList(il.append((BranchInstruction)(new IFEQ((InstructionHandle)null))));
   }

   public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
      String className = clazz.getName();
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int index;
      if (className.equals("org.w3c.dom.Node")) {
         this.translateTo(classGen, methodGen, Type.NodeSet);
         index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
         il.append((Instruction)(new INVOKEINTERFACE(index, 2)));
      } else if (className.equals("org.w3c.dom.NodeList")) {
         this.translateTo(classGen, methodGen, Type.NodeSet);
         index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
         il.append((Instruction)(new INVOKEINTERFACE(index, 2)));
      } else if (className.equals("java.lang.Object")) {
         il.append(NOP);
      } else if (className.equals("java.lang.String")) {
         this.translateTo(classGen, methodGen, Type.String);
      } else {
         ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), className);
         classGen.getParser().reportError(2, err);
      }

   }

   public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateTo(classGen, methodGen, Type.Reference);
   }

   public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
      methodGen.getInstructionList().append(NOP);
   }

   public String getClassName() {
      return "com.sun.org.apache.xalan.internal.xsltc.DOM";
   }

   public Instruction LOAD(int slot) {
      return new ALOAD(slot);
   }

   public Instruction STORE(int slot) {
      return new ASTORE(slot);
   }
}
