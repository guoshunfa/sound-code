package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class Key extends TopLevelElement {
   private QName _name;
   private Pattern _match;
   private Expression _use;
   private Type _useType;

   public void parseContents(Parser parser) {
      String name = this.getAttribute("name");
      if (!XML11Char.isXML11ValidQName(name)) {
         ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
         parser.reportError(3, err);
      }

      this._name = parser.getQNameIgnoreDefaultNs(name);
      this.getSymbolTable().addKey(this._name, this);
      this._match = parser.parsePattern(this, "match", (String)null);
      this._use = parser.parseExpression(this, "use", (String)null);
      if (this._name == null) {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
      } else if (this._match.isDummy()) {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "match");
      } else if (this._use.isDummy()) {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "use");
      }
   }

   public String getName() {
      return this._name.toString();
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._match.typeCheck(stable);
      this._useType = this._use.typeCheck(stable);
      if (!(this._useType instanceof StringType) && !(this._useType instanceof NodeSetType)) {
         this._use = new CastExpr(this._use, Type.String);
      }

      return Type.Void;
   }

   public void traverseNodeSet(ClassGenerator classGen, MethodGenerator methodGen, int buildKeyIndex) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int getNodeValue = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
      int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
      int keyDom = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      LocalVariableGen parentNode = methodGen.addLocalVariable("parentNode", Util.getJCRefType("I"), (InstructionHandle)null, (InstructionHandle)null);
      parentNode.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ISTORE(parentNode.getIndex()))));
      il.append(methodGen.loadCurrentNode());
      il.append(methodGen.loadIterator());
      this._use.translate(classGen, methodGen);
      this._use.startIterator(classGen, methodGen);
      il.append(methodGen.storeIterator());
      BranchHandle nextNode = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      InstructionHandle loop = il.append(NOP);
      il.append(classGen.loadTranslet());
      il.append((CompoundInstruction)(new PUSH(cpg, this._name.toString())));
      parentNode.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(parentNode.getIndex()))));
      il.append(methodGen.loadDOM());
      il.append(methodGen.loadCurrentNode());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getNodeValue, 2)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(buildKeyIndex)));
      il.append(classGen.loadTranslet());
      il.append((CompoundInstruction)(new PUSH(cpg, this.getName())));
      il.append(methodGen.loadDOM());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(keyDom)));
      nextNode.setTarget(il.append(methodGen.loadIterator()));
      il.append(methodGen.nextNode());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append(methodGen.storeCurrentNode());
      il.append((BranchInstruction)(new IFGE(loop)));
      il.append(methodGen.storeIterator());
      il.append(methodGen.storeCurrentNode());
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int current = methodGen.getLocalIndex("current");
      int key = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "buildKeyIndex", "(Ljava/lang/String;ILjava/lang/String;)V");
      int keyDom = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
      int git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      il.append(methodGen.loadCurrentNode());
      il.append(methodGen.loadIterator());
      il.append(methodGen.loadDOM());
      il.append((CompoundInstruction)(new PUSH(cpg, 4)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(git, 2)));
      il.append(methodGen.loadCurrentNode());
      il.append(methodGen.setStartNode());
      il.append(methodGen.storeIterator());
      BranchHandle nextNode = il.append((BranchInstruction)(new GOTO((InstructionHandle)null)));
      InstructionHandle loop = il.append(NOP);
      il.append(methodGen.loadCurrentNode());
      this._match.translate(classGen, methodGen);
      this._match.synthesize(classGen, methodGen);
      BranchHandle skipNode = il.append((BranchInstruction)(new IFEQ((InstructionHandle)null)));
      if (this._useType instanceof NodeSetType) {
         il.append(methodGen.loadCurrentNode());
         this.traverseNodeSet(classGen, methodGen, key);
      } else {
         il.append(classGen.loadTranslet());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((CompoundInstruction)(new PUSH(cpg, this._name.toString())));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP_X1);
         il.append(methodGen.loadCurrentNode());
         this._use.translate(classGen, methodGen);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(key)));
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(keyDom)));
      }

      InstructionHandle skip = il.append(NOP);
      il.append(methodGen.loadIterator());
      il.append(methodGen.nextNode());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append(methodGen.storeCurrentNode());
      il.append((BranchInstruction)(new IFGT(loop)));
      il.append(methodGen.storeIterator());
      il.append(methodGen.storeCurrentNode());
      nextNode.setTarget(skip);
      skipNode.setTarget(skip);
   }
}
