package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;

final class LiteralAttribute extends Instruction {
   private final String _name;
   private final AttributeValue _value;

   public LiteralAttribute(String name, String value, Parser parser, SyntaxTreeNode parent) {
      this._name = name;
      this.setParent(parent);
      this._value = AttributeValue.create(this, value, parser);
   }

   public void display(int indent) {
      this.indent(indent);
      Util.println("LiteralAttribute name=" + this._name + " value=" + this._value);
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      this._value.typeCheck(stable);
      this.typeCheckContents(stable);
      return Type.Void;
   }

   protected boolean contextDependent() {
      return this._value.contextDependent();
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      il.append(methodGen.loadHandler());
      il.append((CompoundInstruction)(new PUSH(cpg, this._name)));
      this._value.translate(classGen, methodGen);
      SyntaxTreeNode parent = this.getParent();
      if (parent instanceof LiteralElement && ((LiteralElement)parent).allAttributesUnique()) {
         int flags = 0;
         boolean isHTMLAttrEmpty = false;
         ElemDesc elemDesc = ((LiteralElement)parent).getElemDesc();
         if (elemDesc != null) {
            if (elemDesc.isAttrFlagSet(this._name, 4)) {
               flags |= 2;
               isHTMLAttrEmpty = true;
            } else if (elemDesc.isAttrFlagSet(this._name, 2)) {
               flags |= 4;
            }
         }

         if (this._value instanceof SimpleAttributeValue) {
            String attrValue = ((SimpleAttributeValue)this._value).toString();
            if (!this.hasBadChars(attrValue) && !isHTMLAttrEmpty) {
               flags |= 1;
            }
         }

         il.append((CompoundInstruction)(new PUSH(cpg, flags)));
         il.append(methodGen.uniqueAttribute());
      } else {
         il.append(methodGen.attribute());
      }

   }

   private boolean hasBadChars(String value) {
      char[] chars = value.toCharArray();
      int size = chars.length;

      for(int i = 0; i < size; ++i) {
         char ch = chars[i];
         if (ch < ' ' || '~' < ch || ch == '<' || ch == '>' || ch == '&' || ch == '"') {
            return true;
         }
      }

      return false;
   }

   public String getName() {
      return this._name;
   }

   public AttributeValue getValue() {
      return this._value;
   }
}
