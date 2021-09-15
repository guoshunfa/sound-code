package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;

final class Predicate extends Expression implements Closure {
   private Expression _exp = null;
   private boolean _canOptimize = true;
   private boolean _nthPositionFilter = false;
   private boolean _nthDescendant = false;
   int _ptype = -1;
   private String _className = null;
   private ArrayList _closureVars = null;
   private Closure _parentClosure = null;
   private Expression _value = null;
   private Step _step = null;

   public Predicate(Expression exp) {
      this._exp = exp;
      this._exp.setParent(this);
   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      this._exp.setParser(parser);
   }

   public boolean isNthPositionFilter() {
      return this._nthPositionFilter;
   }

   public boolean isNthDescendant() {
      return this._nthDescendant;
   }

   public void dontOptimize() {
      this._canOptimize = false;
   }

   public boolean hasPositionCall() {
      return this._exp.hasPositionCall();
   }

   public boolean hasLastCall() {
      return this._exp.hasLastCall();
   }

   public boolean inInnerClass() {
      return this._className != null;
   }

   public Closure getParentClosure() {
      if (this._parentClosure == null) {
         SyntaxTreeNode node = this.getParent();

         do {
            if (node instanceof Closure) {
               this._parentClosure = (Closure)node;
               break;
            }

            if (node instanceof TopLevelElement) {
               break;
            }

            node = node.getParent();
         } while(node != null);
      }

      return this._parentClosure;
   }

   public String getInnerClassName() {
      return this._className;
   }

   public void addVariable(VariableRefBase variableRef) {
      if (this._closureVars == null) {
         this._closureVars = new ArrayList();
      }

      if (!this._closureVars.contains(variableRef)) {
         this._closureVars.add(variableRef);
         Closure parentClosure = this.getParentClosure();
         if (parentClosure != null) {
            parentClosure.addVariable(variableRef);
         }
      }

   }

   public int getPosType() {
      if (this._ptype == -1) {
         SyntaxTreeNode parent = this.getParent();
         if (parent instanceof StepPattern) {
            this._ptype = ((StepPattern)parent).getNodeType();
         } else if (parent instanceof AbsoluteLocationPath) {
            AbsoluteLocationPath path = (AbsoluteLocationPath)parent;
            Expression exp = path.getPath();
            if (exp instanceof Step) {
               this._ptype = ((Step)exp).getNodeType();
            }
         } else if (parent instanceof VariableRefBase) {
            VariableRefBase ref = (VariableRefBase)parent;
            VariableBase var = ref.getVariable();
            Expression exp = var.getExpression();
            if (exp instanceof Step) {
               this._ptype = ((Step)exp).getNodeType();
            }
         } else if (parent instanceof Step) {
            this._ptype = ((Step)parent).getNodeType();
         }
      }

      return this._ptype;
   }

   public boolean parentIsPattern() {
      return this.getParent() instanceof Pattern;
   }

   public Expression getExpr() {
      return this._exp;
   }

   public String toString() {
      return "pred(" + this._exp + ')';
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type texp = this._exp.typeCheck(stable);
      if (texp instanceof ReferenceType) {
         this._exp = new CastExpr(this._exp, texp = Type.Real);
      }

      if (texp instanceof ResultTreeType) {
         this._exp = new CastExpr(this._exp, Type.Boolean);
         this._exp = new CastExpr(this._exp, Type.Real);
         texp = this._exp.typeCheck(stable);
      }

      if (texp instanceof NumberType) {
         if (!(texp instanceof IntType)) {
            this._exp = new CastExpr(this._exp, Type.Int);
         }

         if (this._canOptimize) {
            this._nthPositionFilter = !this._exp.hasLastCall() && !this._exp.hasPositionCall();
            if (this._nthPositionFilter) {
               SyntaxTreeNode parent = this.getParent();
               this._nthDescendant = parent instanceof Step && parent.getParent() instanceof AbsoluteLocationPath;
               return this._type = Type.NodeSet;
            }
         }

         this._nthPositionFilter = this._nthDescendant = false;
         QName position = this.getParser().getQNameIgnoreDefaultNs("position");
         PositionCall positionCall = new PositionCall(position);
         positionCall.setParser(this.getParser());
         positionCall.setParent(this);
         this._exp = new EqualityExpr(0, positionCall, this._exp);
         if (this._exp.typeCheck(stable) != Type.Boolean) {
            this._exp = new CastExpr(this._exp, Type.Boolean);
         }

         return this._type = Type.Boolean;
      } else {
         if (!(texp instanceof BooleanType)) {
            this._exp = new CastExpr(this._exp, Type.Boolean);
         }

         return this._type = Type.Boolean;
      }
   }

   private void compileFilter(ClassGenerator classGen, MethodGenerator methodGen) {
      this._className = this.getXSLTC().getHelperClassName();
      FilterGenerator filterGen = new FilterGenerator(this._className, "java.lang.Object", this.toString(), 33, new String[]{"com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter"}, classGen.getStylesheet());
      ConstantPoolGen cpg = filterGen.getConstantPool();
      int length = this._closureVars == null ? 0 : this._closureVars.size();

      for(int i = 0; i < length; ++i) {
         VariableBase var = ((VariableRefBase)this._closureVars.get(i)).getVariable();
         filterGen.addField(new Field(1, cpg.addUtf8(var.getEscapedName()), cpg.addUtf8(var.getType().toSignature()), (com.sun.org.apache.bcel.internal.classfile.Attribute[])null, cpg.getConstantPool()));
      }

      InstructionList il = new InstructionList();
      TestGenerator testGen = new TestGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[]{com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;")}, new String[]{"node", "position", "last", "current", "translet", "iterator"}, "test", this._className, il, cpg);
      LocalVariableGen local = testGen.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), (InstructionHandle)null, (InstructionHandle)null);
      String className = classGen.getClassName();
      il.append(filterGen.loadTranslet());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass(className))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref(className, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"))));
      local.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(local.getIndex()))));
      testGen.setDomIndex(local.getIndex());
      this._exp.translate(filterGen, testGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)IRETURN);
      filterGen.addEmptyConstructor(1);
      filterGen.addMethod(testGen);
      this.getXSLTC().dumpClass(filterGen.getJavaClass());
   }

   public boolean isBooleanTest() {
      return this._exp instanceof BooleanExpr;
   }

   public boolean isNodeValueTest() {
      if (!this._canOptimize) {
         return false;
      } else {
         return this.getStep() != null && this.getCompareValue() != null;
      }
   }

   public Step getStep() {
      if (this._step != null) {
         return this._step;
      } else if (this._exp == null) {
         return null;
      } else {
         if (this._exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr)this._exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();
            if (left instanceof CastExpr) {
               left = ((CastExpr)left).getExpr();
            }

            if (left instanceof Step) {
               this._step = (Step)left;
            }

            if (right instanceof CastExpr) {
               right = ((CastExpr)right).getExpr();
            }

            if (right instanceof Step) {
               this._step = (Step)right;
            }
         }

         return this._step;
      }
   }

   public Expression getCompareValue() {
      if (this._value != null) {
         return this._value;
      } else if (this._exp == null) {
         return null;
      } else {
         if (this._exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr)this._exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();
            if (left instanceof LiteralExpr) {
               this._value = left;
               return this._value;
            }

            if (left instanceof VariableRefBase && left.getType() == Type.String) {
               this._value = left;
               return this._value;
            }

            if (right instanceof LiteralExpr) {
               this._value = right;
               return this._value;
            }

            if (right instanceof VariableRefBase && right.getType() == Type.String) {
               this._value = right;
               return this._value;
            }
         }

         return null;
      }
   }

   public void translateFilter(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      this.compileFilter(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass(this._className))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(cpg.addMethodref(this._className, "<init>", "()V"))));
      int length = this._closureVars == null ? 0 : this._closureVars.size();

      for(int i = 0; i < length; ++i) {
         VariableRefBase varRef = (VariableRefBase)this._closureVars.get(i);
         VariableBase var = varRef.getVariable();
         Type varType = var.getType();
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);

         Closure variableClosure;
         for(variableClosure = this._parentClosure; variableClosure != null && !variableClosure.inInnerClass(); variableClosure = variableClosure.getParentClosure()) {
         }

         if (variableClosure != null) {
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)ALOAD_0);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref(variableClosure.getInnerClassName(), var.getEscapedName(), varType.toSignature()))));
         } else {
            il.append(var.loadInstruction());
         }

         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new PUTFIELD(cpg.addFieldref(this._className, var.getEscapedName(), varType.toSignature()))));
      }

   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (!this._nthPositionFilter && !this._nthDescendant) {
         if (this.isNodeValueTest() && this.getParent() instanceof Step) {
            this._value.translate(classGen, methodGen);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new CHECKCAST(cpg.addClass("java.lang.String"))));
            il.append((CompoundInstruction)(new PUSH(cpg, ((EqualityExpr)this._exp).getOp())));
         } else {
            this.translateFilter(classGen, methodGen);
         }
      } else {
         this._exp.translate(classGen, methodGen);
      }

   }
}
