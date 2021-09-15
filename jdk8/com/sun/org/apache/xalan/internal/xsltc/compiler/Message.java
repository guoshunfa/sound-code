package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Message extends Instruction {
   private boolean _terminate = false;

   public void parseContents(Parser parser) {
      String termstr = this.getAttribute("terminate");
      if (termstr != null) {
         this._terminate = termstr.equals("yes");
      }

      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this.typeCheckContents(stable);
      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append(classGen.loadTranslet());
      switch(this.elementCount()) {
      case 0:
         il.append((CompoundInstruction)(new PUSH(cpg, "")));
         break;
      case 1:
         SyntaxTreeNode child = this.elementAt(0);
         if (child instanceof Text) {
            il.append((CompoundInstruction)(new PUSH(cpg, ((Text)child).getText())));
            break;
         }
      default:
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xml.internal.serializer.ToXMLStream"))));
         il.append(methodGen.storeHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("java.io.StringWriter"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(cpg.addMethodref("java.io.StringWriter", "<init>", "()V"))));
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(cpg.addMethodref("com.sun.org.apache.xml.internal.serializer.ToXMLStream", "<init>", "()V"))));
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setWriter", "(Ljava/io/Writer;)V"), 2)));
         il.append(methodGen.loadHandler());
         il.append((CompoundInstruction)(new PUSH(cpg, "UTF-8")));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setEncoding", "(Ljava/lang/String;)V"), 2)));
         il.append(methodGen.loadHandler());
         il.append(ICONST_1);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "setOmitXMLDeclaration", "(Z)V"), 2)));
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V"), 1)));
         this.translateContents(classGen, methodGen);
         il.append(methodGen.loadHandler());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V"), 1)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("java.io.StringWriter", "toString", "()Ljava/lang/String;"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)SWAP);
         il.append(methodGen.storeHandler());
      }

      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "displayMessage", "(Ljava/lang/String;)V"))));
      if (this._terminate) {
         int einit = cpg.addMethodref("java.lang.RuntimeException", "<init>", "(Ljava/lang/String;)V");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("java.lang.RuntimeException"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((CompoundInstruction)(new PUSH(cpg, "Termination forced by an xsl:message instruction")));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(einit)));
         il.append(ATHROW);
      }

   }
}
