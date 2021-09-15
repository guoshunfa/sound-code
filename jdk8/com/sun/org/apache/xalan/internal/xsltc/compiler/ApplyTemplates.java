package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Iterator;
import java.util.Vector;

final class ApplyTemplates extends Instruction {
   private Expression _select;
   private Type _type = null;
   private QName _modeName;
   private String _functionName;

   public void display(int indent) {
      this.indent(indent);
      Util.println("ApplyTemplates");
      this.indent(indent + 4);
      Util.println("select " + this._select.toString());
      if (this._modeName != null) {
         this.indent(indent + 4);
         Util.println("mode " + this._modeName);
      }

   }

   public boolean hasWithParams() {
      return this.hasContents();
   }

   public void parseContents(Parser parser) {
      String select = this.getAttribute("select");
      String mode = this.getAttribute("mode");
      if (select.length() > 0) {
         this._select = parser.parseExpression(this, "select", (String)null);
      }

      if (mode.length() > 0) {
         if (!XML11Char.isXML11ValidQName(mode)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", mode, this);
            parser.reportError(3, err);
         }

         this._modeName = parser.getQNameIgnoreDefaultNs(mode);
      }

      this._functionName = parser.getTopLevelStylesheet().getMode(this._modeName).functionName();
      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._select == null) {
         this.typeCheckContents(stable);
         return Type.Void;
      } else {
         this._type = this._select.typeCheck(stable);
         if (this._type instanceof NodeType || this._type instanceof ReferenceType) {
            this._select = new CastExpr(this._select, Type.NodeSet);
            this._type = Type.NodeSet;
         }

         if (!(this._type instanceof NodeSetType) && !(this._type instanceof ResultTreeType)) {
            throw new TypeCheckError(this);
         } else {
            this.typeCheckContents(stable);
            return Type.Void;
         }
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      boolean setStartNodeCalled = false;
      Stylesheet stylesheet = classGen.getStylesheet();
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int current = methodGen.getLocalIndex("current");
      Vector<Sort> sortObjects = new Vector();
      Iterator var9 = this.getContents().iterator();

      while(var9.hasNext()) {
         SyntaxTreeNode child = (SyntaxTreeNode)var9.next();
         if (child instanceof Sort) {
            sortObjects.addElement((Sort)child);
         }
      }

      int setStartNode;
      if (stylesheet.hasLocalParams() || this.hasContents()) {
         il.append(classGen.loadTranslet());
         setStartNode = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(setStartNode)));
         this.translateContents(classGen, methodGen);
      }

      il.append(classGen.loadTranslet());
      if (this._type != null && this._type instanceof ResultTreeType) {
         if (sortObjects.size() > 0) {
            ErrorMsg err = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
            this.getParser().reportError(4, err);
         }

         this._select.translate(classGen, methodGen);
         this._type.translateTo(classGen, methodGen, Type.NodeSet);
      } else {
         il.append(methodGen.loadDOM());
         if (sortObjects.size() > 0) {
            Sort.translateSortIterator(classGen, methodGen, this._select, sortObjects);
            setStartNode = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadCurrentNode());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(setStartNode, 2)));
            setStartNodeCalled = true;
         } else if (this._select == null) {
            Mode.compileGetChildren(classGen, methodGen, current);
         } else {
            this._select.translate(classGen, methodGen);
         }
      }

      if (this._select != null && !setStartNodeCalled) {
         this._select.startIterator(classGen, methodGen);
      }

      String className = classGen.getStylesheet().getClassName();
      il.append(methodGen.loadHandler());
      String applyTemplatesSig = classGen.getApplyTemplatesSig();
      int applyTemplates = cpg.addMethodref(className, this._functionName, applyTemplatesSig);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(applyTemplates)));
      Iterator var12 = this.getContents().iterator();

      while(var12.hasNext()) {
         SyntaxTreeNode child = (SyntaxTreeNode)var12.next();
         if (child instanceof WithParam) {
            ((WithParam)child).releaseResultTree(classGen, methodGen);
         }
      }

      if (stylesheet.hasLocalParams() || this.hasContents()) {
         il.append(classGen.loadTranslet());
         int popFrame = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(popFrame)));
      }

   }
}
