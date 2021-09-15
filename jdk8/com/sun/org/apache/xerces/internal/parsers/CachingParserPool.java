package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.ShadowedSymbolTable;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class CachingParserPool {
   public static final boolean DEFAULT_SHADOW_SYMBOL_TABLE = false;
   public static final boolean DEFAULT_SHADOW_GRAMMAR_POOL = false;
   protected SymbolTable fSynchronizedSymbolTable;
   protected XMLGrammarPool fSynchronizedGrammarPool;
   protected boolean fShadowSymbolTable;
   protected boolean fShadowGrammarPool;

   public CachingParserPool() {
      this(new SymbolTable(), new XMLGrammarPoolImpl());
   }

   public CachingParserPool(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this.fShadowSymbolTable = false;
      this.fShadowGrammarPool = false;
      this.fSynchronizedSymbolTable = new SynchronizedSymbolTable(symbolTable);
      this.fSynchronizedGrammarPool = new CachingParserPool.SynchronizedGrammarPool(grammarPool);
   }

   public SymbolTable getSymbolTable() {
      return this.fSynchronizedSymbolTable;
   }

   public XMLGrammarPool getXMLGrammarPool() {
      return this.fSynchronizedGrammarPool;
   }

   public void setShadowSymbolTable(boolean shadow) {
      this.fShadowSymbolTable = shadow;
   }

   public DOMParser createDOMParser() {
      SymbolTable symbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
      XMLGrammarPool grammarPool = this.fShadowGrammarPool ? new CachingParserPool.ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
      return new DOMParser((SymbolTable)symbolTable, (XMLGrammarPool)grammarPool);
   }

   public SAXParser createSAXParser() {
      SymbolTable symbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
      XMLGrammarPool grammarPool = this.fShadowGrammarPool ? new CachingParserPool.ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
      return new SAXParser((SymbolTable)symbolTable, (XMLGrammarPool)grammarPool);
   }

   public static final class ShadowedGrammarPool extends XMLGrammarPoolImpl {
      private XMLGrammarPool fGrammarPool;

      public ShadowedGrammarPool(XMLGrammarPool grammarPool) {
         this.fGrammarPool = grammarPool;
      }

      public Grammar[] retrieveInitialGrammarSet(String grammarType) {
         Grammar[] grammars = super.retrieveInitialGrammarSet(grammarType);
         return grammars != null ? grammars : this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
      }

      public Grammar retrieveGrammar(XMLGrammarDescription gDesc) {
         Grammar g = super.retrieveGrammar(gDesc);
         return g != null ? g : this.fGrammarPool.retrieveGrammar(gDesc);
      }

      public void cacheGrammars(String grammarType, Grammar[] grammars) {
         super.cacheGrammars(grammarType, grammars);
         this.fGrammarPool.cacheGrammars(grammarType, grammars);
      }

      public Grammar getGrammar(XMLGrammarDescription desc) {
         return super.containsGrammar(desc) ? super.getGrammar(desc) : null;
      }

      public boolean containsGrammar(XMLGrammarDescription desc) {
         return super.containsGrammar(desc);
      }
   }

   public static final class SynchronizedGrammarPool implements XMLGrammarPool {
      private XMLGrammarPool fGrammarPool;

      public SynchronizedGrammarPool(XMLGrammarPool grammarPool) {
         this.fGrammarPool = grammarPool;
      }

      public Grammar[] retrieveInitialGrammarSet(String grammarType) {
         synchronized(this.fGrammarPool) {
            return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
         }
      }

      public Grammar retrieveGrammar(XMLGrammarDescription gDesc) {
         synchronized(this.fGrammarPool) {
            return this.fGrammarPool.retrieveGrammar(gDesc);
         }
      }

      public void cacheGrammars(String grammarType, Grammar[] grammars) {
         synchronized(this.fGrammarPool) {
            this.fGrammarPool.cacheGrammars(grammarType, grammars);
         }
      }

      public void lockPool() {
         synchronized(this.fGrammarPool) {
            this.fGrammarPool.lockPool();
         }
      }

      public void clear() {
         synchronized(this.fGrammarPool) {
            this.fGrammarPool.clear();
         }
      }

      public void unlockPool() {
         synchronized(this.fGrammarPool) {
            this.fGrammarPool.unlockPool();
         }
      }
   }
}
