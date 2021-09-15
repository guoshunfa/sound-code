package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class XslElement extends Instruction {
   private String _prefix;
   private boolean _ignore = false;
   private boolean _isLiteralName = true;
   private AttributeValueTemplate _name;
   private AttributeValueTemplate _namespace;

   public void display(int indent) {
      this.indent(indent);
      Util.println("Element " + this._name);
      this.displayContents(indent + 4);
   }

   public void parseContents(Parser parser) {
      SymbolTable stable = parser.getSymbolTable();
      String name = this.getAttribute("name");
      if (name == "") {
         ErrorMsg msg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", name, this);
         parser.reportError(4, msg);
         this.parseChildren(parser);
         this._ignore = true;
      } else {
         String namespace = this.getAttribute("namespace");
         this._isLiteralName = Util.isLiteral(name);
         if (this._isLiteralName) {
            if (!XML11Char.isXML11ValidQName(name)) {
               ErrorMsg msg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", name, this);
               parser.reportError(4, msg);
               this.parseChildren(parser);
               this._ignore = true;
               return;
            }

            QName qname = parser.getQNameSafe(name);
            String prefix = qname.getPrefix();
            String local = qname.getLocalPart();
            if (prefix == null) {
               prefix = "";
            }

            if (!this.hasAttribute("namespace")) {
               namespace = this.lookupNamespace(prefix);
               if (namespace == null) {
                  ErrorMsg err = new ErrorMsg("NAMESPACE_UNDEF_ERR", prefix, this);
                  parser.reportError(4, err);
                  this.parseChildren(parser);
                  this._ignore = true;
                  return;
               }

               this._prefix = prefix;
               this._namespace = new AttributeValueTemplate(namespace, parser, this);
            } else {
               if (prefix == "") {
                  if (Util.isLiteral(namespace)) {
                     prefix = this.lookupPrefix(namespace);
                     if (prefix == null) {
                        prefix = stable.generateNamespacePrefix();
                     }
                  }

                  StringBuffer newName = new StringBuffer(prefix);
                  if (prefix != "") {
                     newName.append(':');
                  }

                  name = newName.append(local).toString();
               }

               this._prefix = prefix;
               this._namespace = new AttributeValueTemplate(namespace, parser, this);
            }
         } else {
            this._namespace = namespace == "" ? null : new AttributeValueTemplate(namespace, parser, this);
         }

         this._name = new AttributeValueTemplate(name, parser, this);
         String useSets = this.getAttribute("use-attribute-sets");
         if (useSets.length() > 0) {
            if (!Util.isValidQNames(useSets)) {
               ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", useSets, this);
               parser.reportError(3, err);
            }

            this.setFirstElement(new UseAttributeSets(useSets, parser));
         }

         this.parseChildren(parser);
      }
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (!this._ignore) {
         this._name.typeCheck(stable);
         if (this._namespace != null) {
            this._namespace.typeCheck(stable);
         }
      }

      this.typeCheckContents(stable);
      return Type.Void;
   }

   public void translateLiteral(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (!this._ignore) {
         il.append(methodGen.loadHandler());
         this._name.translate(classGen, methodGen);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP2);
         il.append(methodGen.startElement());
         if (this._namespace != null) {
            il.append(methodGen.loadHandler());
            il.append((CompoundInstruction)(new PUSH(cpg, this._prefix)));
            this._namespace.translate(classGen, methodGen);
            il.append(methodGen.namespace());
         }
      }

      this.translateContents(classGen, methodGen);
      if (!this._ignore) {
         il.append(methodGen.endElement());
      }

   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (this._isLiteralName) {
         this.translateLiteral(classGen, methodGen);
      } else {
         if (!this._ignore) {
            LocalVariableGen nameValue = methodGen.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), (InstructionHandle)null);
            this._name.translate(classGen, methodGen);
            nameValue.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(nameValue.getIndex()))));
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(nameValue.getIndex())));
            int check = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkQName", "(Ljava/lang/String;)V");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(check)));
            il.append(methodGen.loadHandler());
            nameValue.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(nameValue.getIndex()))));
            if (this._namespace != null) {
               this._namespace.translate(classGen, methodGen);
            } else {
               il.append(ACONST_NULL);
            }

            il.append(methodGen.loadHandler());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "startXslElement", "(Ljava/lang/String;Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Ljava/lang/String;"))));
         }

         this.translateContents(classGen, methodGen);
         if (!this._ignore) {
            il.append(methodGen.endElement());
         }

      }
   }

   public void translateContents(ClassGenerator classGen, MethodGenerator methodGen) {
      int n = this.elementCount();

      for(int i = 0; i < n; ++i) {
         SyntaxTreeNode item = (SyntaxTreeNode)this.getContents().get(i);
         if (!this._ignore || !(item instanceof XslAttribute)) {
            item.translate(classGen, methodGen);
         }
      }

   }
}
