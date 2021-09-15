package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Vector;

final class CallTemplate extends Instruction {
   private QName _name;
   private SyntaxTreeNode[] _parameters = null;
   private Template _calleeTemplate = null;

   public void display(int indent) {
      this.indent(indent);
      System.out.print("CallTemplate");
      Util.println(" name " + this._name);
      this.displayContents(indent + 4);
   }

   public boolean hasWithParams() {
      return this.elementCount() > 0;
   }

   public void parseContents(Parser parser) {
      String name = this.getAttribute("name");
      if (name.length() > 0) {
         if (!XML11Char.isXML11ValidQName(name)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
            parser.reportError(3, err);
         }

         this._name = parser.getQNameIgnoreDefaultNs(name);
      } else {
         this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
      }

      this.parseChildren(parser);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      Template template = stable.lookupTemplate(this._name);
      if (template != null) {
         this.typeCheckContents(stable);
         return Type.Void;
      } else {
         ErrorMsg err = new ErrorMsg("TEMPLATE_UNDEF_ERR", this._name, this);
         throw new TypeCheckError(err);
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      Stylesheet stylesheet = classGen.getStylesheet();
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      if (stylesheet.hasLocalParams() || this.hasContents()) {
         this._calleeTemplate = this.getCalleeTemplate();
         if (this._calleeTemplate != null) {
            this.buildParameterList();
         } else {
            int push = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
            il.append(classGen.loadTranslet());
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(push)));
            this.translateContents(classGen, methodGen);
         }
      }

      String className = stylesheet.getClassName();
      String methodName = Util.escape(this._name.toString());
      il.append(classGen.loadTranslet());
      il.append(methodGen.loadDOM());
      il.append(methodGen.loadIterator());
      il.append(methodGen.loadHandler());
      il.append(methodGen.loadCurrentNode());
      StringBuffer methodSig = new StringBuffer("(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I");
      int i;
      if (this._calleeTemplate != null) {
         i = this._parameters.length;

         for(int i = 0; i < i; ++i) {
            SyntaxTreeNode node = this._parameters[i];
            methodSig.append("Ljava/lang/Object;");
            if (node instanceof Param) {
               il.append(ACONST_NULL);
            } else {
               node.translate(classGen, methodGen);
            }
         }
      }

      methodSig.append(")V");
      il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(cpg.addMethodref(className, methodName, methodSig.toString()))));
      if (this._parameters != null) {
         for(i = 0; i < this._parameters.length; ++i) {
            if (this._parameters[i] instanceof WithParam) {
               ((WithParam)this._parameters[i]).releaseResultTree(classGen, methodGen);
            }
         }
      }

      if (this._calleeTemplate == null && (stylesheet.hasLocalParams() || this.hasContents())) {
         i = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
         il.append(classGen.loadTranslet());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(i)));
      }

   }

   public Template getCalleeTemplate() {
      Template foundTemplate = this.getXSLTC().getParser().getSymbolTable().lookupTemplate(this._name);
      return foundTemplate.isSimpleNamedTemplate() ? foundTemplate : null;
   }

   private void buildParameterList() {
      Vector<Param> defaultParams = this._calleeTemplate.getParameters();
      int numParams = defaultParams.size();
      this._parameters = new SyntaxTreeNode[numParams];

      int count;
      for(count = 0; count < numParams; ++count) {
         this._parameters[count] = (SyntaxTreeNode)defaultParams.elementAt(count);
      }

      count = this.elementCount();

      for(int i = 0; i < count; ++i) {
         Object node = this.elementAt(i);
         if (node instanceof WithParam) {
            WithParam withParam = (WithParam)node;
            QName name = withParam.getName();

            for(int k = 0; k < numParams; ++k) {
               SyntaxTreeNode parm = this._parameters[k];
               if (parm instanceof Param && ((Param)parm).getName().equals(name)) {
                  withParam.setDoParameterOptimization(true);
                  this._parameters[k] = withParam;
                  break;
               }

               if (parm instanceof WithParam && ((WithParam)parm).getName().equals(name)) {
                  withParam.setDoParameterOptimization(true);
                  this._parameters[k] = withParam;
                  break;
               }
            }
         }
      }

   }
}
