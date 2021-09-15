package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Comment extends Instruction {
   public void parseContents(Parser parser) {
      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this.typeCheckContents(stable);
      return Type.String;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      Text rawText = null;
      if (this.elementCount() == 1) {
         Object content = this.elementAt(0);
         if (content instanceof Text) {
            rawText = (Text)content;
         }
      }

      int comment;
      if (rawText != null) {
         il.append(methodGen.loadHandler());
         if (rawText.canLoadAsArrayOffsetLength()) {
            rawText.loadAsArrayOffsetLength(classGen, methodGen);
            comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "([CII)V");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(comment, 4)));
         } else {
            il.append((CompoundInstruction)(new PUSH(cpg, rawText.getText())));
            comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(comment, 2)));
         }
      } else {
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append(classGen.loadTranslet());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append(methodGen.storeHandler());
         this.translateContents(classGen, methodGen);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;"))));
         comment = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(comment, 2)));
         il.append(methodGen.storeHandler());
      }

   }
}
