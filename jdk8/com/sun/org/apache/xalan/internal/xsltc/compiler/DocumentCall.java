package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class DocumentCall extends FunctionCall {
   private Expression _arg1 = null;
   private Expression _arg2 = null;
   private Type _arg1Type;

   public DocumentCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      int ac = this.argumentCount();
      ErrorMsg msg;
      if (ac >= 1 && ac <= 2) {
         if (this.getStylesheet() == null) {
            msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(msg);
         } else {
            this._arg1 = this.argument(0);
            if (this._arg1 == null) {
               msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
               throw new TypeCheckError(msg);
            } else {
               this._arg1Type = this._arg1.typeCheck(stable);
               if (this._arg1Type != Type.NodeSet && this._arg1Type != Type.String) {
                  this._arg1 = new CastExpr(this._arg1, Type.String);
               }

               if (ac == 2) {
                  this._arg2 = this.argument(1);
                  if (this._arg2 == null) {
                     msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                     throw new TypeCheckError(msg);
                  }

                  Type arg2Type = this._arg2.typeCheck(stable);
                  if (arg2Type.identicalTo(Type.Node)) {
                     this._arg2 = new CastExpr(this._arg2, Type.NodeSet);
                  } else if (!arg2Type.identicalTo(Type.NodeSet)) {
                     ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                     throw new TypeCheckError(msg);
                  }
               }

               return this._type = Type.NodeSet;
            }
         }
      } else {
         msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
         throw new TypeCheckError(msg);
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int ac = this.argumentCount();
      int domField = cpg.addFieldref(classGen.getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
      String docParamList = null;
      if (ac == 1) {
         docParamList = "(Ljava/lang/Object;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
      } else {
         docParamList = "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
      }

      int docIdx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument", "documentF", docParamList);
      this._arg1.translate(classGen, methodGen);
      if (this._arg1Type == Type.NodeSet) {
         this._arg1.startIterator(classGen, methodGen);
      }

      if (ac == 2) {
         this._arg2.translate(classGen, methodGen);
         this._arg2.startIterator(classGen, methodGen);
      }

      il.append((CompoundInstruction)(new PUSH(cpg, this.getStylesheet().getSystemId())));
      il.append(classGen.loadTranslet());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(domField)));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(docIdx)));
   }
}
