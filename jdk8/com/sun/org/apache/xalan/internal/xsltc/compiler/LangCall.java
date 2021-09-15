package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class LangCall extends FunctionCall {
   private Expression _lang = this.argument(0);
   private Type _langType;

   public LangCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._langType = this._lang.typeCheck(stable);
      if (!(this._langType instanceof StringType)) {
         this._lang = new CastExpr(this._lang, Type.String);
      }

      return Type.Boolean;
   }

   public Type getType() {
      return Type.Boolean;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int tst = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "testLanguage", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Z");
      this._lang.translate(classGen, methodGen);
      il.append(methodGen.loadDOM());
      if (classGen instanceof FilterGenerator) {
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ILOAD(1)));
      } else {
         il.append(methodGen.loadContextNode());
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(tst)));
   }
}
