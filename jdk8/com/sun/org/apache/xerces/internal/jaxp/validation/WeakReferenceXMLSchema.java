package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import java.lang.ref.WeakReference;

final class WeakReferenceXMLSchema extends AbstractXMLSchema {
   private WeakReference fGrammarPool = new WeakReference((Object)null);

   public WeakReferenceXMLSchema() {
   }

   public synchronized XMLGrammarPool getGrammarPool() {
      XMLGrammarPool grammarPool = (XMLGrammarPool)this.fGrammarPool.get();
      if (grammarPool == null) {
         grammarPool = new SoftReferenceGrammarPool();
         this.fGrammarPool = new WeakReference(grammarPool);
      }

      return (XMLGrammarPool)grammarPool;
   }

   public boolean isFullyComposed() {
      return false;
   }
}
