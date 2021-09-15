package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class KeyCall extends FunctionCall {
   private Expression _name;
   private Expression _value;
   private Type _valueType;
   private QName _resolvedQName = null;

   public KeyCall(QName fname, Vector arguments) {
      super(fname, arguments);
      switch(this.argumentCount()) {
      case 1:
         this._name = null;
         this._value = this.argument(0);
         break;
      case 2:
         this._name = this.argument(0);
         this._value = this.argument(1);
         break;
      default:
         this._name = this._value = null;
      }

   }

   public void addParentDependency() {
      if (this._resolvedQName != null) {
         Object node;
         for(node = this; node != null && !(node instanceof TopLevelElement); node = ((SyntaxTreeNode)node).getParent()) {
         }

         TopLevelElement parent = (TopLevelElement)node;
         if (parent != null) {
            parent.addDependency(this.getSymbolTable().getKey(this._resolvedQName));
         }

      }
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type returnType = super.typeCheck(stable);
      if (this._name != null) {
         Type nameType = this._name.typeCheck(stable);
         if (this._name instanceof LiteralExpr) {
            LiteralExpr literal = (LiteralExpr)this._name;
            this._resolvedQName = this.getParser().getQNameIgnoreDefaultNs(literal.getValue());
         } else if (!(nameType instanceof StringType)) {
            this._name = new CastExpr(this._name, Type.String);
         }
      }

      this._valueType = this._value.typeCheck(stable);
      if (this._valueType != Type.NodeSet && this._valueType != Type.Reference && this._valueType != Type.String) {
         this._value = new CastExpr(this._value, Type.String);
         this._valueType = this._value.typeCheck(stable);
      }

      this.addParentDependency();
      return returnType;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int getKeyIndex = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
      int keyDom = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "setDom", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)V");
      int getKeyIterator = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + this._valueType.toSignature() + "Z)" + "Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex$KeyIndexIterator;");
      il.append(classGen.loadTranslet());
      if (this._name == null) {
         il.append((CompoundInstruction)(new PUSH(cpg, "##id")));
      } else if (this._resolvedQName != null) {
         il.append((CompoundInstruction)(new PUSH(cpg, this._resolvedQName.toString())));
      } else {
         this._name.translate(classGen, methodGen);
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(getKeyIndex)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append(methodGen.loadDOM());
      il.append(methodGen.loadCurrentNode());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(keyDom)));
      this._value.translate(classGen, methodGen);
      il.append(this._name != null ? ICONST_1 : ICONST_0);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(getKeyIterator)));
   }
}
