package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class ProcessingInstruction extends Instruction {
   private AttributeValue _name;
   private boolean _isLiteral = false;

   public void parseContents(Parser parser) {
      String name = this.getAttribute("name");
      if (name.length() > 0) {
         this._isLiteral = Util.isLiteral(name);
         if (this._isLiteral && !XML11Char.isXML11ValidNCName(name)) {
            ErrorMsg err = new ErrorMsg("INVALID_NCNAME_ERR", name, this);
            parser.reportError(3, err);
         }

         this._name = AttributeValue.create(this, name, parser);
      } else {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
      }

      if (name.equals("xml")) {
         this.reportError(this, parser, "ILLEGAL_PI_ERR", "xml");
      }

      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._name.typeCheck(stable);
      this.typeCheckContents(stable);
      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (!this._isLiteral) {
         LocalVariableGen nameValue = methodGen.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), (InstructionHandle)null);
         this._name.translate(classGen, methodGen);
         nameValue.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(nameValue.getIndex()))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(nameValue.getIndex())));
         int check = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkNCName", "(Ljava/lang/String;)V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(check)));
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         nameValue.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(nameValue.getIndex()))));
      } else {
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         this._name.translate(classGen, methodGen);
      }

      il.append(classGen.loadTranslet());
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"))));
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
      il.append(methodGen.storeHandler());
      this.translateContents(classGen, methodGen);
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValueOfPI", "()Ljava/lang/String;"))));
      int processingInstruction = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "processingInstruction", "(Ljava/lang/String;Ljava/lang/String;)V");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(processingInstruction, 3)));
      il.append(methodGen.storeHandler());
   }
}
