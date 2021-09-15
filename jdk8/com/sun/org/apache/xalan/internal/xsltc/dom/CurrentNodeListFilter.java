package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public interface CurrentNodeListFilter {
   boolean test(int var1, int var2, int var3, int var4, AbstractTranslet var5, DTMAxisIterator var6);
}
