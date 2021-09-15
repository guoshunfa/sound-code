package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool implements XMLGrammarPool {
   private final XMLGrammarPool core;

   public ReadOnlyGrammarPool(XMLGrammarPool pool) {
      this.core = pool;
   }

   public void cacheGrammars(String grammarType, Grammar[] grammars) {
   }

   public void clear() {
   }

   public void lockPool() {
   }

   public Grammar retrieveGrammar(XMLGrammarDescription desc) {
      return this.core.retrieveGrammar(desc);
   }

   public Grammar[] retrieveInitialGrammarSet(String grammarType) {
      return this.core.retrieveInitialGrammarSet(grammarType);
   }

   public void unlockPool() {
   }
}
