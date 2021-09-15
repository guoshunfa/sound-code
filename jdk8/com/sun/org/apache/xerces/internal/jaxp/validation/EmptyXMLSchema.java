package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class EmptyXMLSchema extends AbstractXMLSchema implements XMLGrammarPool {
   private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];

   public EmptyXMLSchema() {
   }

   public Grammar[] retrieveInitialGrammarSet(String grammarType) {
      return ZERO_LENGTH_GRAMMAR_ARRAY;
   }

   public void cacheGrammars(String grammarType, Grammar[] grammars) {
   }

   public Grammar retrieveGrammar(XMLGrammarDescription desc) {
      return null;
   }

   public void lockPool() {
   }

   public void unlockPool() {
   }

   public void clear() {
   }

   public XMLGrammarPool getGrammarPool() {
      return this;
   }

   public boolean isFullyComposed() {
      return true;
   }
}
