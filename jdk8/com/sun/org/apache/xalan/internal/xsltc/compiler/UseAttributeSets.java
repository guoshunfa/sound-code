package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.StringTokenizer;
import java.util.Vector;

final class UseAttributeSets extends Instruction {
   private static final String ATTR_SET_NOT_FOUND = "";
   private final Vector _sets = new Vector(2);

   public UseAttributeSets(String setNames, Parser parser) {
      this.setParser(parser);
      this.addAttributeSets(setNames);
   }

   public void addAttributeSets(String setNames) {
      if (setNames != null && !setNames.equals("")) {
         StringTokenizer tokens = new StringTokenizer(setNames);

         while(tokens.hasMoreTokens()) {
            QName qname = this.getParser().getQNameIgnoreDefaultNs(tokens.nextToken());
            this._sets.add(qname);
         }
      }

   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      SymbolTable symbolTable = this.getParser().getSymbolTable();

      for(int i = 0; i < this._sets.size(); ++i) {
         QName name = (QName)this._sets.elementAt(i);
         AttributeSet attrs = symbolTable.lookupAttributeSet(name);
         if (attrs != null) {
            String methodName = attrs.getMethodName();
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadIterator());
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadCurrentNode());
            int method = cpg.addMethodref(classGen.getClassName(), methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
            il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(method)));
         } else {
            Parser parser = this.getParser();
            String atrs = name.toString();
            this.reportError(this, parser, "ATTRIBSET_UNDEF_ERR", atrs);
         }
      }

   }
}
