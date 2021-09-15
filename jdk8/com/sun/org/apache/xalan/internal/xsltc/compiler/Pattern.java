package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

public abstract class Pattern extends Expression {
   public abstract Type typeCheck(SymbolTable var1) throws TypeCheckError;

   public abstract void translate(ClassGenerator var1, MethodGenerator var2);

   public abstract double getPriority();
}
