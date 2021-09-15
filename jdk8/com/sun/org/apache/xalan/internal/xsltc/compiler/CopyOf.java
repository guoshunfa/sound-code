package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class CopyOf extends Instruction {
   private Expression _select;

   public void display(int indent) {
      this.indent(indent);
      Util.println("CopyOf");
      this.indent(indent + 4);
      Util.println("select " + this._select.toString());
   }

   public void parseContents(Parser parser) {
      this._select = parser.parseExpression(this, "select", (String)null);
      if (this._select.isDummy()) {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
      }
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type tselect = this._select.typeCheck(stable);
      if (!(tselect instanceof NodeType) && !(tselect instanceof NodeSetType) && !(tselect instanceof ReferenceType) && !(tselect instanceof ResultTreeType)) {
         this._select = new CastExpr(this._select, Type.String);
      }

      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      Type tselect = this._select.getType();
      String CPY1_SIG = "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
      int cpy1 = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      String CPY2_SIG = "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
      int cpy2 = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
      String getDoc_SIG = "()I";
      int getDoc = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getDocument", "()I");
      if (tselect instanceof NodeSetType) {
         il.append(methodGen.loadDOM());
         this._select.translate(classGen, methodGen);
         this._select.startIterator(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpy1, 3)));
      } else if (tselect instanceof NodeType) {
         il.append(methodGen.loadDOM());
         this._select.translate(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpy2, 3)));
      } else if (tselect instanceof ResultTreeType) {
         this._select.translate(classGen, methodGen);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(getDoc, 1)));
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpy2, 3)));
      } else if (tselect instanceof ReferenceType) {
         this._select.translate(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append(methodGen.loadCurrentNode());
         il.append(methodGen.loadDOM());
         int copy = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "copy", "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(copy)));
      } else {
         il.append(classGen.loadTranslet());
         this._select.translate(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V"))));
      }

   }
}
