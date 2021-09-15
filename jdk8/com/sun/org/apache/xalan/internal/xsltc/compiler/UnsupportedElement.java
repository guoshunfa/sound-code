package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.List;
import java.util.Vector;

final class UnsupportedElement extends SyntaxTreeNode {
   private Vector _fallbacks = null;
   private ErrorMsg _message = null;
   private boolean _isExtension = false;

   public UnsupportedElement(String uri, String prefix, String local, boolean isExtension) {
      super(uri, prefix, local);
      this._isExtension = isExtension;
   }

   public void setErrorMessage(ErrorMsg message) {
      this._message = message;
   }

   public void display(int indent) {
      this.indent(indent);
      Util.println("Unsupported element = " + this._qname.getNamespace() + ":" + this._qname.getLocalPart());
      this.displayContents(indent + 4);
   }

   private void processFallbacks(Parser parser) {
      List<SyntaxTreeNode> children = this.getContents();
      if (children != null) {
         int count = children.size();

         for(int i = 0; i < count; ++i) {
            SyntaxTreeNode child = (SyntaxTreeNode)children.get(i);
            if (child instanceof Fallback) {
               Fallback fallback = (Fallback)child;
               fallback.activate();
               fallback.parseContents(parser);
               if (this._fallbacks == null) {
                  this._fallbacks = new Vector();
               }

               this._fallbacks.addElement(child);
            }
         }
      }

   }

   public void parseContents(Parser parser) {
      this.processFallbacks(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._fallbacks != null) {
         int count = this._fallbacks.size();

         for(int i = 0; i < count; ++i) {
            Fallback fallback = (Fallback)this._fallbacks.elementAt(i);
            fallback.typeCheck(stable);
         }
      }

      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      if (this._fallbacks != null) {
         int count = this._fallbacks.size();

         for(int i = 0; i < count; ++i) {
            Fallback fallback = (Fallback)this._fallbacks.elementAt(i);
            fallback.translate(classGen, methodGen);
         }
      } else {
         ConstantPoolGen cpg = classGen.getConstantPool();
         InstructionList il = methodGen.getInstructionList();
         int unsupportedElem = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unsupported_ElementF", "(Ljava/lang/String;Z)V");
         il.append((CompoundInstruction)(new PUSH(cpg, this.getQName().toString())));
         il.append((CompoundInstruction)(new PUSH(cpg, this._isExtension)));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(unsupportedElem)));
      }

   }
}
