package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class FilteredAbsoluteLocationPath extends Expression {
   private Expression _path;

   public FilteredAbsoluteLocationPath() {
      this._path = null;
   }

   public FilteredAbsoluteLocationPath(Expression path) {
      this._path = path;
      if (path != null) {
         this._path.setParent(this);
      }

   }

   public void setParser(Parser parser) {
      super.setParser(parser);
      if (this._path != null) {
         this._path.setParser(parser);
      }

   }

   public Expression getPath() {
      return this._path;
   }

   public String toString() {
      return "FilteredAbsoluteLocationPath(" + (this._path != null ? this._path.toString() : "null") + ')';
   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      if (this._path != null) {
         Type ptype = this._path.typeCheck(stable);
         if (ptype instanceof NodeType) {
            this._path = new CastExpr(this._path, Type.NodeSet);
         }
      }

      return this._type = Type.NodeSet;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
      ConstantPoolGen cpg = classGen.getConstantPool();
      InstructionList il = methodGen.getInstructionList();
      int initDFI;
      if (this._path != null) {
         initDFI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.DupFilterIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
         LocalVariableGen pathTemp = methodGen.addLocalVariable("filtered_absolute_location_path_tmp", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), (InstructionHandle)null, (InstructionHandle)null);
         this._path.translate(classGen, methodGen);
         pathTemp.setStart(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ASTORE(pathTemp.getIndex()))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.DupFilterIterator"))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)DUP);
         pathTemp.setEnd(il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new ALOAD(pathTemp.getIndex()))));
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKESPECIAL(initDFI)));
      } else {
         initDFI = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
         il.append(methodGen.loadDOM());
         il.append((com.sun.org.apache.bcel.internal.generic.Instruction)(new INVOKEINTERFACE(initDFI, 1)));
      }

   }
}
