package com.sun.org.apache.xalan.internal.xsltc.dom;

public final class EmptyFilter implements Filter {
   public boolean test(int node) {
      return true;
   }
}
