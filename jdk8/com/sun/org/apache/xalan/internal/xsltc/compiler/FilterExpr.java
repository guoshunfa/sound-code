package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class FilterExpr extends Expression {
   private Expression _primary;
   private final Vector _predicates;

   public FilterExpr(Expression primary, Vector predicates) {
      this._primary = primary;
      this._predicates = predicates;
      primary.setParent(this);
   }

   protected Expression getExpr() {
      return this._primary instanceof CastExpr ? ((CastExpr)this._primary).getExpr() : this._primary;
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      this._primary.setParser(parser);
      if (this._predicates != null) {
         int n = this._predicates.size();

         for(int i = 0; i < n; ++i) {
            Expression exp = (Expression)this._predicates.elementAt(i);
            exp.setParser(parser);
            exp.setParent(this);
         }
      }

   }

   public String toString() {
      return "filter-expr(" + this._primary + ", " + this._predicates + ")";
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type ptype = this._primary.typeCheck(stable);
      boolean canOptimize = this._primary instanceof KeyCall;
      if (!(ptype instanceof NodeSetType)) {
         if (!(ptype instanceof ReferenceType)) {
            throw new TypeCheckError(this);
         }

         this._primary = new CastExpr(this._primary, Type.NodeSet);
      }

      int n = this._predicates.size();

      for(int i = 0; i < n; ++i) {
         Predicate pred = (Predicate)this._predicates.elementAt(i);
         if (!canOptimize) {
            pred.dontOptimize();
         }

         pred.typeCheck(stable);
      }

      return this._type = Type.NodeSet;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateFilterExpr(classGen, methodGen, this._predicates == null ? -1 : this._predicates.size() - 1);
   }

   private void translateFilterExpr(ClassGenerator classGen, MethodGenerator methodGen, int predicateIndex) {
      if (predicateIndex >= 0) {
         this.translatePredicates(classGen, methodGen, predicateIndex);
      } else {
         this._primary.translate(classGen, methodGen);
      }

   }

   public void translatePredicates(ClassGenerator classGen, MethodGenerator methodGen, int predicateIndex) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (predicateIndex < 0) {
         this.translateFilterExpr(classGen, methodGen, predicateIndex);
      } else {
         Predicate predicate = (Predicate)this._predicates.get(predicateIndex--);
         this.translatePredicates(classGen, methodGen, predicateIndex);
         int nthIteratorIdx;
         LocalVariableGen iteratorTemp;
         LocalVariableGen predicateValueTemp;
         if (predicate.isNthPositionFilter()) {
            nthIteratorIdx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
            iteratorTemp = methodGen.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
            iteratorTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(iteratorTemp.getIndex()))));
            predicate.translate(classGen, methodGen);
            predicateValueTemp = methodGen.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("I"), (InstructionHandle)null, (InstructionHandle)null);
            predicateValueTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ISTORE(predicateValueTemp.getIndex()))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            iteratorTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(iteratorTemp.getIndex()))));
            predicateValueTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(predicateValueTemp.getIndex()))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(nthIteratorIdx)));
         } else {
            nthIteratorIdx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ZLcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
            iteratorTemp = methodGen.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
            iteratorTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(iteratorTemp.getIndex()))));
            predicate.translate(classGen, methodGen);
            predicateValueTemp = methodGen.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), (InstructionHandle)null, (InstructionHandle)null);
            predicateValueTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(predicateValueTemp.getIndex()))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator"))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
            iteratorTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(iteratorTemp.getIndex()))));
            il.append(ICONST_1);
            predicateValueTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(predicateValueTemp.getIndex()))));
            il.append(methodGen.loadCurrentNode());
            il.append(classGen.loadTranslet());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(nthIteratorIdx)));
         }
      }

   }
}
