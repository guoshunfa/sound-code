package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

public abstract class NumberType extends Type {
   public boolean isNumber() {
      return true;
   }

   public boolean isSimple() {
      return true;
   }
}
