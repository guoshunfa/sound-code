package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

final class AttributeValueTemplate extends AttributeValue {
   static final int OUT_EXPR = 0;
   static final int IN_EXPR = 1;
   static final int IN_EXPR_SQUOTES = 2;
   static final int IN_EXPR_DQUOTES = 3;
   static final String DELIMITER = "\ufffe";

   public AttributeValueTemplate(String value, Parser parser, SyntaxTreeNode parent) {
      this.setParent(parent);
      this.setParser(parser);

      try {
         this.parseAVTemplate(value, parser);
      } catch (NoSuchElementException var5) {
         this.reportError(parent, parser, "ATTR_VAL_TEMPLATE_ERR", value);
      }

   }

   private void parseAVTemplate(String text, Parser parser) {
      StringTokenizer tokenizer = new StringTokenizer(text, "{}\"'", true);
      String t = null;
      String lookahead = null;
      StringBuffer buffer = new StringBuffer();
      byte state = 0;

      while(tokenizer.hasMoreTokens()) {
         if (lookahead != null) {
            t = lookahead;
            lookahead = null;
         } else {
            t = tokenizer.nextToken();
         }

         if (t.length() == 1) {
            switch(t.charAt(0)) {
            case '"':
               switch(state) {
               case 0:
               case 2:
               default:
                  break;
               case 1:
                  state = 3;
                  break;
               case 3:
                  state = 1;
               }

               buffer.append(t);
               break;
            case '\'':
               switch(state) {
               case 0:
               case 3:
               default:
                  break;
               case 1:
                  state = 2;
                  break;
               case 2:
                  state = 1;
               }

               buffer.append(t);
               break;
            case '{':
               switch(state) {
               case 0:
                  lookahead = tokenizer.nextToken();
                  if (lookahead.equals("{")) {
                     buffer.append(lookahead);
                     lookahead = null;
                  } else {
                     buffer.append("\ufffe");
                     state = 1;
                  }
                  continue;
               case 1:
               case 2:
               case 3:
                  this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
               default:
                  continue;
               }
            case '}':
               switch(state) {
               case 0:
                  lookahead = tokenizer.nextToken();
                  if (lookahead.equals("}")) {
                     buffer.append(lookahead);
                     lookahead = null;
                  } else {
                     this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
                  }
                  continue;
               case 1:
                  buffer.append("\ufffe");
                  state = 0;
                  continue;
               case 2:
               case 3:
                  buffer.append(t);
               default:
                  continue;
               }
            default:
               buffer.append(t);
            }
         } else {
            buffer.append(t);
         }
      }

      if (state != 0) {
         this.reportError(this.getParent(), parser, "ATTR_VAL_TEMPLATE_ERR", text);
      }

      tokenizer = new StringTokenizer(buffer.toString(), "\ufffe", true);

      while(tokenizer.hasMoreTokens()) {
         t = tokenizer.nextToken();
         if (t.equals("\ufffe")) {
            this.addElement(parser.parseExpression(this, tokenizer.nextToken()));
            tokenizer.nextToken();
         } else {
            this.addElement(new LiteralExpr(t));
         }
      }

   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      List<SyntaxTreeNode> contents = this.getContents();
      int n = contents.size();

      for(int i = 0; i < n; ++i) {
         Expression exp = (Expression)contents.get(i);
         if (!exp.typeCheck(stable).identicalTo(Type.String)) {
            contents.set(i, new CastExpr(exp, Type.String));
         }
      }

      return this._type = Type.String;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer("AVT:[");
      int count = this.elementCount();

      for(int i = 0; i < count; ++i) {
         buffer.append(this.elementAt(i).toString());
         if (i < count - 1) {
            buffer.append(' ');
         }
      }

      return buffer.append(']').toString();
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      if (this.elementCount() == 1) {
         Expression exp = (Expression)this.elementAt(0);
         exp.translate(classGen, methodGen);
      } else {
         ConstantPoolGen cpg = classGen.getConstantPool();
         InstructionList il = methodGen.getInstructionList();
         int initBuffer = cpg.addMethodref("java.lang.StringBuffer", "<init>", "()V");
         com.sun.org.apache.bcel.internal.generic.Instruction append = new INVOKEVIRTUAL(cpg.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
         int toString = cpg.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("java.lang.StringBuffer"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(initBuffer)));
         Iterator elements = this.elements();

         while(elements.hasNext()) {
            Expression exp = (Expression)elements.next();
            exp.translate(classGen, methodGen);
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)append);
         }

         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEVIRTUAL(toString)));
      }

   }
}
