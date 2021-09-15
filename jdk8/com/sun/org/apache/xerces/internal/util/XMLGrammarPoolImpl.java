package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class XMLGrammarPoolImpl implements XMLGrammarPool {
   protected static final int TABLE_SIZE = 11;
   protected XMLGrammarPoolImpl.Entry[] fGrammars = null;
   protected boolean fPoolIsLocked;
   protected int fGrammarCount = 0;
   private static final boolean DEBUG = false;

   public XMLGrammarPoolImpl() {
      this.fGrammars = new XMLGrammarPoolImpl.Entry[11];
      this.fPoolIsLocked = false;
   }

   public XMLGrammarPoolImpl(int initialCapacity) {
      this.fGrammars = new XMLGrammarPoolImpl.Entry[initialCapacity];
      this.fPoolIsLocked = false;
   }

   public Grammar[] retrieveInitialGrammarSet(String grammarType) {
      synchronized(this.fGrammars) {
         int grammarSize = this.fGrammars.length;
         Grammar[] tempGrammars = new Grammar[this.fGrammarCount];
         int pos = 0;

         for(int i = 0; i < grammarSize; ++i) {
            for(XMLGrammarPoolImpl.Entry e = this.fGrammars[i]; e != null; e = e.next) {
               if (e.desc.getGrammarType().equals(grammarType)) {
                  tempGrammars[pos++] = e.grammar;
               }
            }
         }

         Grammar[] toReturn = new Grammar[pos];
         System.arraycopy(tempGrammars, 0, toReturn, 0, pos);
         return toReturn;
      }
   }

   public void cacheGrammars(String grammarType, Grammar[] grammars) {
      if (!this.fPoolIsLocked) {
         for(int i = 0; i < grammars.length; ++i) {
            this.putGrammar(grammars[i]);
         }
      }

   }

   public Grammar retrieveGrammar(XMLGrammarDescription desc) {
      return this.getGrammar(desc);
   }

   public void putGrammar(Grammar grammar) {
      if (!this.fPoolIsLocked) {
         synchronized(this.fGrammars) {
            XMLGrammarDescription desc = grammar.getGrammarDescription();
            int hash = this.hashCode(desc);
            int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

            XMLGrammarPoolImpl.Entry entry;
            for(entry = this.fGrammars[index]; entry != null; entry = entry.next) {
               if (entry.hash == hash && this.equals(entry.desc, desc)) {
                  entry.grammar = grammar;
                  return;
               }
            }

            entry = new XMLGrammarPoolImpl.Entry(hash, desc, grammar, this.fGrammars[index]);
            this.fGrammars[index] = entry;
            ++this.fGrammarCount;
         }
      }

   }

   public Grammar getGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

         for(XMLGrammarPoolImpl.Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
            if (entry.hash == hash && this.equals(entry.desc, desc)) {
               return entry.grammar;
            }
         }

         return null;
      }
   }

   public Grammar removeGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;
         XMLGrammarPoolImpl.Entry entry = this.fGrammars[index];

         for(XMLGrammarPoolImpl.Entry prev = null; entry != null; entry = entry.next) {
            if (entry.hash == hash && this.equals(entry.desc, desc)) {
               if (prev != null) {
                  prev.next = entry.next;
               } else {
                  this.fGrammars[index] = entry.next;
               }

               Grammar tempGrammar = entry.grammar;
               entry.grammar = null;
               --this.fGrammarCount;
               return tempGrammar;
            }

            prev = entry;
         }

         return null;
      }
   }

   public boolean containsGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

         for(XMLGrammarPoolImpl.Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
            if (entry.hash == hash && this.equals(entry.desc, desc)) {
               return true;
            }
         }

         return false;
      }
   }

   public void lockPool() {
      this.fPoolIsLocked = true;
   }

   public void unlockPool() {
      this.fPoolIsLocked = false;
   }

   public void clear() {
      for(int i = 0; i < this.fGrammars.length; ++i) {
         if (this.fGrammars[i] != null) {
            this.fGrammars[i].clear();
            this.fGrammars[i] = null;
         }
      }

      this.fGrammarCount = 0;
   }

   public boolean equals(XMLGrammarDescription desc1, XMLGrammarDescription desc2) {
      return desc1.equals(desc2);
   }

   public int hashCode(XMLGrammarDescription desc) {
      return desc.hashCode();
   }

   protected static final class Entry {
      public int hash;
      public XMLGrammarDescription desc;
      public Grammar grammar;
      public XMLGrammarPoolImpl.Entry next;

      protected Entry(int hash, XMLGrammarDescription desc, Grammar grammar, XMLGrammarPoolImpl.Entry next) {
         this.hash = hash;
         this.desc = desc;
         this.grammar = grammar;
         this.next = next;
      }

      protected void clear() {
         this.desc = null;
         this.grammar = null;
         if (this.next != null) {
            this.next.clear();
            this.next = null;
         }

      }
   }
}
