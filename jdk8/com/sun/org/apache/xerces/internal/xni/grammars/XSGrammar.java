package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xs.XSModel;

public interface XSGrammar extends Grammar {
   XSModel toXSModel();

   XSModel toXSModel(XSGrammar[] var1);
}
