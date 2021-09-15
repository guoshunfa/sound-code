package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class UnparsedEntityUriCall extends FunctionCall {
   private Expression _entity = this.argument();

   public UnparsedEntityUriCall(QName fname, Vector arguments) {
      super(fname, arguments);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type entity = this._entity.typeCheck(stable);
      if (!(entity instanceof StringType)) {
         this._entity = new CastExpr(this._entity, Type.String);
      }

      return this._type = Type.String;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append(methodGen.loadDOM());
      this._entity.translate(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getUnparsedEntityURI", "(Ljava/lang/String;)Ljava/lang/String;"), 2)));
   }
}
