package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class FilterParentPath extends Expression {
   private Expression _filterExpr;
   private Expression _path;
   private boolean _hasDescendantAxis = false;

   public FilterParentPath(Expression filterExpr, Expression path) {
      (this._path = path).setParent(this);
      (this._filterExpr = filterExpr).setParent(this);
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      this._filterExpr.setParser(parser);
      this._path.setParser(parser);
   }

   public String toString() {
      return "FilterParentPath(" + this._filterExpr + ", " + this._path + ')';
   }

   public void setDescendantAxis() {
      this._hasDescendantAxis = true;
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type ftype = this._filterExpr.typeCheck(stable);
      if (!(ftype instanceof NodeSetType)) {
         if (ftype instanceof ReferenceType) {
            this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
         } else {
            if (!(ftype instanceof NodeType)) {
               throw new TypeCheckError(this);
            }

            this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
         }
      }

      Type ptype = this._path.typeCheck(stable);
      if (!(ptype instanceof NodeSetType)) {
         this._path = new CastExpr(this._path, Type.NodeSet);
      }

      return this._type = Type.NodeSet;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int initSI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
      this._filterExpr.translate(classGen, methodGen);
      LocalVariableGen filterTemp = methodGen.addLocalVariable("filter_parent_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
      filterTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(filterTemp.getIndex()))));
      this._path.translate(classGen, methodGen);
      LocalVariableGen pathTemp = methodGen.addLocalVariable("filter_parent_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
      pathTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(pathTemp.getIndex()))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator"))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      filterTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(filterTemp.getIndex()))));
      pathTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(pathTemp.getIndex()))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(initSI)));
      if (this._hasDescendantAxis) {
         int incl = cpg.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(incl)));
      }

      SyntaxTreeNode parent = this.getParent();
      boolean parentAlreadyOrdered = parent instanceof RelativeLocationPath || parent instanceof FilterParentPath || parent instanceof KeyCall || parent instanceof CurrentCall || parent instanceof DocumentCall;
      if (!parentAlreadyOrdered) {
         int order = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         il.append(methodGen.loadContextNode());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(order, 3)));
      }

   }
}
