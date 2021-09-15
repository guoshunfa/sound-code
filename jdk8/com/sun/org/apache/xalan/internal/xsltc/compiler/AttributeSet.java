package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.AttributeSetMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Iterator;
import java.util.List;

final class AttributeSet extends TopLevelElement {
   private static final String AttributeSetPrefix = "$as$";
   private QName _name;
   private UseAttributeSets _useSets;
   private AttributeSet _mergeSet;
   private String _method;
   private boolean _ignore = false;

   public QName getName() {
      return this._name;
   }

   public String getMethodName() {
      return this._method;
   }

   public void ignore() {
      this._ignore = true;
   }

   public void parseContents(Parser parser) {
      String name = this.getAttribute("name");
      ErrorMsg msg;
      if (!XML11Char.isXML11ValidQName(name)) {
         msg = new ErrorMsg("INVALID_QNAME_ERR", name, this);
         parser.reportError(3, msg);
      }

      this._name = parser.getQNameIgnoreDefaultNs(name);
      if (this._name == null || this._name.equals("")) {
         msg = new ErrorMsg("UNNAMED_ATTRIBSET_ERR", this);
         parser.reportError(3, msg);
      }

      String useSets = this.getAttribute("use-attribute-sets");
      if (useSets.length() > 0) {
         if (!Util.isValidQNames(useSets)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", useSets, this);
            parser.reportError(3, err);
         }

         this._useSets = new UseAttributeSets(useSets, parser);
      }

      List<SyntaxTreeNode> contents = this.getContents();
      int count = contents.size();

      for(int i = 0; i < count; ++i) {
         SyntaxTreeNode child = (SyntaxTreeNode)contents.get(i);
         if (child instanceof XslAttribute) {
            parser.getSymbolTable().setCurrentNode(child);
            child.parseContents(parser);
         } else if (!(child instanceof Text)) {
            ErrorMsg msg = new ErrorMsg("ILLEGAL_CHILD_ERR", this);
            parser.reportError(3, msg);
         }
      }

      parser.getSymbolTable().setCurrentNode(this);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._ignore) {
         return Type.Void;
      } else {
         this._mergeSet = stable.addAttributeSet(this);
         this._method = "$as$" + this.getXSLTC().nextAttributeSetSerial();
         if (this._useSets != null) {
            this._useSets.typeCheck(stable);
         }

         this.typeCheckContents(stable);
         return Type.Void;
      }
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      if (!this._ignore) {
         MethodGenerator methodGen = new AttributeSetMethodGenerator(this._method, classGen);
         InstructionList il;
         if (this._mergeSet != null) {
            ConstantPoolGen cpg = classGen.getConstantPool();
            il = methodGen.getInstructionList();
            String methodName = this._mergeSet.getMethodName();
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadIterator());
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadCurrentNode());
            int method = cpg.addMethodref(classGen.getClassName(), methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(method)));
         }

         if (this._useSets != null) {
            this._useSets.translate(classGen, methodGen);
         }

         Iterator attributes = this.elements();

         while(attributes.hasNext()) {
            SyntaxTreeNode element = (SyntaxTreeNode)attributes.next();
            if (element instanceof XslAttribute) {
               XslAttribute attribute = (XslAttribute)element;
               attribute.translate(classGen, methodGen);
            }
         }

         il = methodGen.getInstructionList();
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)RETURN);
         classGen.addMethod(methodGen);
      }
   }

   public String toString() {
      StringBuffer buf = new StringBuffer("attribute-set: ");
      Iterator attributes = this.elements();

      while(attributes.hasNext()) {
         XslAttribute attribute = (XslAttribute)attributes.next();
         buf.append((Object)attribute);
      }

      return buf.toString();
   }
}
