package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ValueOf extends Instruction {
   private Expression _select;
   private boolean _escaping = true;
   private boolean _isString = false;

   public void display(int indent) {
      this.indent(indent);
      Util.println("ValueOf");
      this.indent(indent + 4);
      Util.println("select " + this._select.toString());
   }

   public void parseContents(Parser parser) {
      this._select = parser.parseExpression(this, "select", (String)null);
      if (this._select.isDummy()) {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
      } else {
         String str = this.getAttribute("disable-output-escaping");
         if (str != null && str.equals("yes")) {
            this._escaping = false;
         }

      }
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Type type = this._select.typeCheck(stable);
      if (type != null && !type.identicalTo(Type.Node)) {
         if (type.identicalTo(Type.NodeSet)) {
            this._select = new CastExpr(this._select, Type.Node);
         } else {
            this._isString = true;
            if (!type.identicalTo(Type.String)) {
               this._select = new CastExpr(this._select, Type.String);
            }

            this._isString = true;
         }
      }

      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int setEscaping = cpg.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
      if (!this._escaping) {
         il.append(methodGen.loadHandler());
         il.append((CompoundInstruction)(new PUSH(cpg, false)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(setEscaping, 2)));
      }

      int characters;
      if (this._isString) {
         characters = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
         il.append(classGen.loadTranslet());
         this._select.translate(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(characters)));
      } else {
         characters = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
         il.append(methodGen.loadDOM());
         this._select.translate(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(characters, 3)));
      }

      if (!this._escaping) {
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(setEscaping, 2)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)POP);
      }

   }
}
