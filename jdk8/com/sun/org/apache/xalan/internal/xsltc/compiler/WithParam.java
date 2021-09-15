package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class WithParam extends Instruction {
   private QName _name;
   protected String _escapedName;
   private Expression _select;
   private LocalVariableGen _domAdapter;
   private boolean _doParameterOptimization = false;

   public void display(int indent) {
      this.indent(indent);
      Util.println("with-param " + this._name);
      if (this._select != null) {
         this.indent(indent + 4);
         Util.println("select " + this._select.toString());
      }

      this.displayContents(indent + 4);
   }

   public String getEscapedName() {
      return this._escapedName;
   }

   public QName getName() {
      return this._name;
   }

   public void setName(QName name) {
      this._name = name;
      this._escapedName = Util.escape(name.getStringRep());
   }

   public void setDoParameterOptimization(boolean flag) {
      this._doParameterOptimization = flag;
   }

   public void parseContents(Parser parser) {
      String name = this.getAttribute("name");
      if (name.length() > 0) {
         if (!XML11Char.isXML11ValidQName(name)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
            parser.reportError(3, err);
         }

         this.setName(parser.getQNameIgnoreDefaultNs(name));
      } else {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
      }

      String select = this.getAttribute("select");
      if (select.length() > 0) {
         this._select = parser.parseExpression(this, "select", (String)null);
      }

      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._select != null) {
         Type tselect = this._select.typeCheck(stable);
         if (!(tselect instanceof ReferenceType)) {
            this._select = new CastExpr(this._select, Type.Reference);
         }
      } else {
         this.typeCheckContents(stable);
      }

      return Type.Void;
   }

   public void translateValue(ClassGenerator classGen, MethodGenerator methodGen) {
      if (this._select != null) {
         this._select.translate(classGen, methodGen);
         this._select.startIterator(classGen, methodGen);
      } else if (this.hasContents()) {
         InstructionList il = methodGen.getInstructionList();
         this.compileResultTree(classGen, methodGen);
         this._domAdapter = methodGen.addLocalVariable2("@" + this._escapedName, Type.ResultTree.toJCType(), il.getEnd());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(this._domAdapter.getIndex())));
      } else {
         ConstantPoolGen cpg = classGen.getConstantPool();
         InstructionList il = methodGen.getInstructionList();
         il.append((CompoundInstruction)(new PUSH(cpg, "")));
      }

   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._doParameterOptimization) {
         this.translateValue(classGen, methodGen);
      } else {
         String name = Util.escape(this.getEscapedName());
         il.append(classGen.loadTranslet());
         il.append((CompoundInstruction)(new PUSH(cpg, name)));
         this.translateValue(classGen, methodGen);
         il.append((CompoundInstruction)(new PUSH(cpg, false)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)POP);
      }
   }

   public void releaseResultTree(ClassGenerator classGen, MethodGenerator methodGen) {
      if (this._domAdapter != null) {
         ConstantPoolGen cpg = classGen.getConstantPool();
         InstructionList il = methodGen.getInstructionList();
         int release;
         if (classGen.getStylesheet().callsNodeset() && classGen.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")) {
            release = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
            il.append(methodGen.loadDOM());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(this._domAdapter.getIndex())));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(release)));
         }

         release = cpg.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(this._domAdapter.getIndex())));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(release, 1)));
         this._domAdapter.setEnd(il.getEnd());
         methodGen.removeLocalVariable(this._domAdapter);
         this._domAdapter = null;
      }

   }
}
