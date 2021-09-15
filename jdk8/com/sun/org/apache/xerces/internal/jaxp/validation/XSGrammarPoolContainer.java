package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public interface XSGrammarPoolContainer {
   XMLGrammarPool getGrammarPool();

   boolean isFullyComposed();

   Boolean getFeature(String var1);

   void setFeature(String var1, boolean var2);

   Object getProperty(String var1);

   void setProperty(String var1, Object var2);
}
