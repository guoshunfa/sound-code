package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class Step extends RelativeLocationPath {
   private int _axis;
   private Vector _predicates;
   private boolean _hadPredicates = false;
   private int _nodeType;

   public Step(int axis, int nodeType, Vector predicates) {
      this._axis = axis;
      this._nodeType = nodeType;
      this._predicates = predicates;
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      if (this._predicates != null) {
         int n = this._predicates.size();

         for(int i = 0; i < n; ++i) {
            Predicate exp = (Predicate)this._predicates.elementAt(i);
            exp.setParser(parser);
            exp.setParent(this);
         }
      }

   }

   public int getAxis() {
      return this._axis;
   }

   public void setAxis(int axis) {
      this._axis = axis;
   }

   public int getNodeType() {
      return this._nodeType;
   }

   public Vector getPredicates() {
      return this._predicates;
   }

   public void addPredicates(Vector predicates) {
      if (this._predicates == null) {
         this._predicates = predicates;
      } else {
         this._predicates.addAll(predicates);
      }

   }

   private boolean hasParentPattern() {
      SyntaxTreeNode parent = this.getParent();
      return parent instanceof ParentPattern || parent instanceof ParentLocationPath || parent instanceof UnionPathExpr || parent instanceof FilterParentPath;
   }

   private boolean hasParentLocationPath() {
      return this.getParent() instanceof ParentLocationPath;
   }

   private boolean hasPredicates() {
      return this._predicates != null && this._predicates.size() > 0;
   }

   private boolean isPredicate() {
      Object parent = this;

      do {
         if (parent == null) {
            return false;
         }

         parent = ((SyntaxTreeNode)parent).getParent();
      } while(!(parent instanceof Predicate));

      return true;
   }

   public boolean isAbbreviatedDot() {
      return this._nodeType == -1 && this._axis == 13;
   }

   public boolean isAbbreviatedDDot() {
      return this._nodeType == -1 && this._axis == 10;
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._hadPredicates = this.hasPredicates();
      if (this.isAbbreviatedDot()) {
         this._type = !this.hasParentPattern() && !this.hasPredicates() && !this.hasParentLocationPath() ? Type.Node : Type.NodeSet;
      } else {
         this._type = Type.NodeSet;
      }

      if (this._predicates != null) {
         int n = this._predicates.size();

         for(int i = 0; i < n; ++i) {
            Expression pred = (Expression)this._predicates.elementAt(i);
            pred.typeCheck(stable);
         }
      }

      return this._type;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      this.translateStep(classGen, methodGen, this.hasPredicates() ? this._predicates.size() - 1 : -1);
   }

   private void translateStep(ClassGenerator classGen, MethodGenerator methodGen, int predicateIndex) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (predicateIndex >= 0) {
         this.translatePredicates(classGen, methodGen, predicateIndex);
      } else {
         int star = 0;
         String name = null;
         XSLTC xsltc = this.getParser().getXSLTC();
         if (this._nodeType >= 14) {
            Vector ni = xsltc.getNamesIndex();
            name = (String)ni.elementAt(this._nodeType - 14);
            star = name.lastIndexOf(42);
         }

         if (this._axis == 2 && this._nodeType != 2 && this._nodeType != -1 && !this.hasParentPattern() && star == 0) {
            int iter = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append((CompoundInstruction)(new PUSH(cpg, 2)));
            il.append((CompoundInstruction)(new PUSH(cpg, this._nodeType)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(iter, 3)));
            return;
         }

         SyntaxTreeNode parent = this.getParent();
         int git;
         if (this.isAbbreviatedDot()) {
            if (this._type == Type.Node) {
               il.append(methodGen.loadContextNode());
            } else if (parent instanceof ParentLocationPath) {
               git = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator"))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
               il.append(methodGen.loadContextNode());
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(git)));
            } else {
               git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
               il.append(methodGen.loadDOM());
               il.append((CompoundInstruction)(new PUSH(cpg, this._axis)));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(git, 2)));
            }

            return;
         }

         if (parent instanceof ParentLocationPath && parent.getParent() instanceof ParentLocationPath && this._nodeType == 1 && !this._hadPredicates) {
            this._nodeType = -1;
         }

         switch(this._nodeType) {
         case 0:
         default:
            if (star > 1) {
               String namespace;
               if (this._axis == 2) {
                  namespace = name.substring(0, star - 2);
               } else {
                  namespace = name.substring(0, star - 1);
               }

               int nsType = xsltc.registerNamespace(namespace);
               int ns = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
               il.append(methodGen.loadDOM());
               il.append((CompoundInstruction)(new PUSH(cpg, this._axis)));
               il.append((CompoundInstruction)(new PUSH(cpg, nsType)));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(ns, 3)));
               break;
            }
         case 1:
            int ty = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append((CompoundInstruction)(new PUSH(cpg, this._axis)));
            il.append((CompoundInstruction)(new PUSH(cpg, this._nodeType)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(ty, 3)));
            break;
         case 2:
            this._axis = 2;
         case -1:
            git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append((CompoundInstruction)(new PUSH(cpg, this._axis)));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(git, 2)));
         }
      }

   }

   public void translatePredicates(ClassGenerator classGen, MethodGenerator methodGen, int predicateIndex) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int idx = false;
      if (predicateIndex < 0) {
         this.translateStep(classGen, methodGen, predicateIndex);
      } else {
         Predicate predicate = (Predicate)this._predicates.get(predicateIndex--);
         int idx;
         if (predicate.isNodeValueTest()) {
            Step step = predicate.getStep();
            il.append(methodGen.loadDOM());
            if (step.isAbbreviatedDot()) {
               this.translateStep(classGen, methodGen, predicateIndex);
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(0)));
            } else {
               ParentLocationPath path = new ParentLocationPath(this, step);
               this._parent = step._parent = path;

               try {
                  path.typeCheck(this.getParser().getSymbolTable());
               } catch (TypeCheckError var11) {
               }

               this.translateStep(classGen, methodGen, predicateIndex);
               path.translateStep(classGen, methodGen);
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(1)));
            }

            predicate.translate(classGen, methodGen);
            idx = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeValueIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ILjava/lang/String;Z)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(idx, 5)));
         } else if (predicate.isNthDescendant()) {
            il.append(methodGen.loadDOM());
            il.append((CompoundInstruction)(new PUSH(cpg, predicate.getPosType())));
            predicate.translate(classGen, methodGen);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ICONST(0)));
            idx = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNthDescendant", "(IIZ)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(idx, 4)));
         } else {
            LocalVariableGen iteratorTemp;
            LocalVariableGen predicateValueTemp;
            if (predicate.isNthPositionFilter()) {
               idx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
               this.translatePredicates(classGen, methodGen, predicateIndex);
               iteratorTemp = methodGen.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
               iteratorTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(iteratorTemp.getIndex()))));
               predicate.translate(classGen, methodGen);
               predicateValueTemp = methodGen.addLocalVariable("step_tmp2", Util.getJCRefType("I"), (InstructionHandle)null, (InstructionHandle)null);
               predicateValueTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ISTORE(predicateValueTemp.getIndex()))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator"))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
               iteratorTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(iteratorTemp.getIndex()))));
               predicateValueTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(predicateValueTemp.getIndex()))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(idx)));
            } else {
               idx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
               this.translatePredicates(classGen, methodGen, predicateIndex);
               iteratorTemp = methodGen.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
               iteratorTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(iteratorTemp.getIndex()))));
               predicate.translateFilter(classGen, methodGen);
               predicateValueTemp = methodGen.addLocalVariable("step_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), (InstructionHandle)null, (InstructionHandle)null);
               predicateValueTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(predicateValueTemp.getIndex()))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator"))));
               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
               iteratorTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(iteratorTemp.getIndex()))));
               predicateValueTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(predicateValueTemp.getIndex()))));
               il.append(methodGen.loadCurrentNode());
               il.append(classGen.loadTranslet());
               if (classGen.isExternal()) {
                  String className = classGen.getClassName();
                  il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass(className))));
               }

               il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(idx)));
            }
         }
      }

   }

   public String toString() {
      StringBuffer buffer = new StringBuffer("step(\"");
      buffer.append(Axis.getNames(this._axis)).append("\", ").append(this._nodeType);
      if (this._predicates != null) {
         int n = this._predicates.size();

         for(int i = 0; i < n; ++i) {
            Predicate pred = (Predicate)this._predicates.elementAt(i);
            buffer.append(", ").append(pred.toString());
         }
      }

      return buffer.append(')').toString();
   }
}
