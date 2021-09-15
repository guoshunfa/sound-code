package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

final class SoftReferenceGrammarPool implements XMLGrammarPool {
   protected static final int TABLE_SIZE = 11;
   protected static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
   protected SoftReferenceGrammarPool.Entry[] fGrammars = null;
   protected boolean fPoolIsLocked;
   protected int fGrammarCount = 0;
   protected final ReferenceQueue fReferenceQueue = new ReferenceQueue();

   public SoftReferenceGrammarPool() {
      this.fGrammars = new SoftReferenceGrammarPool.Entry[11];
      this.fPoolIsLocked = false;
   }

   public SoftReferenceGrammarPool(int initialCapacity) {
      this.fGrammars = new SoftReferenceGrammarPool.Entry[initialCapacity];
      this.fPoolIsLocked = false;
   }

   public Grammar[] retrieveInitialGrammarSet(String grammarType) {
      synchronized(this.fGrammars) {
         this.clean();
         return ZERO_LENGTH_GRAMMAR_ARRAY;
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
            this.clean();
            XMLGrammarDescription desc = grammar.getGrammarDescription();
            int hash = this.hashCode(desc);
            int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

            SoftReferenceGrammarPool.Entry entry;
            for(entry = this.fGrammars[index]; entry != null; entry = entry.next) {
               if (entry.hash == hash && this.equals(entry.desc, desc)) {
                  if (entry.grammar.get() != grammar) {
                     entry.grammar = new SoftReferenceGrammarPool.SoftGrammarReference(entry, grammar, this.fReferenceQueue);
                  }

                  return;
               }
            }

            entry = new SoftReferenceGrammarPool.Entry(hash, index, desc, grammar, this.fGrammars[index], this.fReferenceQueue);
            this.fGrammars[index] = entry;
            ++this.fGrammarCount;
         }
      }

   }

   public Grammar getGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         this.clean();
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

         for(SoftReferenceGrammarPool.Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
            Grammar tempGrammar = (Grammar)entry.grammar.get();
            if (tempGrammar == null) {
               this.removeEntry(entry);
            } else if (entry.hash == hash && this.equals(entry.desc, desc)) {
               return tempGrammar;
            }
         }

         return null;
      }
   }

   public Grammar removeGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         this.clean();
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

         for(SoftReferenceGrammarPool.Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
            if (entry.hash == hash && this.equals(entry.desc, desc)) {
               return this.removeEntry(entry);
            }
         }

         return null;
      }
   }

   public boolean containsGrammar(XMLGrammarDescription desc) {
      synchronized(this.fGrammars) {
         this.clean();
         int hash = this.hashCode(desc);
         int index = (hash & Integer.MAX_VALUE) % this.fGrammars.length;

         for(SoftReferenceGrammarPool.Entry entry = this.fGrammars[index]; entry != null; entry = entry.next) {
            Grammar tempGrammar = (Grammar)entry.grammar.get();
            if (tempGrammar == null) {
               this.removeEntry(entry);
            } else if (entry.hash == hash && this.equals(entry.desc, desc)) {
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
      if (desc1 instanceof XMLSchemaDescription) {
         if (!(desc2 instanceof XMLSchemaDescription)) {
            return false;
         } else {
            XMLSchemaDescription sd1 = (XMLSchemaDescription)desc1;
            XMLSchemaDescription sd2 = (XMLSchemaDescription)desc2;
            String targetNamespace = sd1.getTargetNamespace();
            if (targetNamespace != null) {
               if (!targetNamespace.equals(sd2.getTargetNamespace())) {
                  return false;
               }
            } else if (sd2.getTargetNamespace() != null) {
               return false;
            }

            String expandedSystemId = sd1.getExpandedSystemId();
            if (expandedSystemId != null) {
               if (!expandedSystemId.equals(sd2.getExpandedSystemId())) {
                  return false;
               }
            } else if (sd2.getExpandedSystemId() != null) {
               return false;
            }

            return true;
         }
      } else {
         return desc1.equals(desc2);
      }
   }

   public int hashCode(XMLGrammarDescription desc) {
      if (desc instanceof XMLSchemaDescription) {
         XMLSchemaDescription sd = (XMLSchemaDescription)desc;
         String targetNamespace = sd.getTargetNamespace();
         String expandedSystemId = sd.getExpandedSystemId();
         int hash = targetNamespace != null ? targetNamespace.hashCode() : 0;
         hash ^= expandedSystemId != null ? expandedSystemId.hashCode() : 0;
         return hash;
      } else {
         return desc.hashCode();
      }
   }

   private Grammar removeEntry(SoftReferenceGrammarPool.Entry entry) {
      if (entry.prev != null) {
         entry.prev.next = entry.next;
      } else {
         this.fGrammars[entry.bucket] = entry.next;
      }

      if (entry.next != null) {
         entry.next.prev = entry.prev;
      }

      --this.fGrammarCount;
      entry.grammar.entry = null;
      return (Grammar)entry.grammar.get();
   }

   private void clean() {
      for(Reference ref = this.fReferenceQueue.poll(); ref != null; ref = this.fReferenceQueue.poll()) {
         SoftReferenceGrammarPool.Entry entry = ((SoftReferenceGrammarPool.SoftGrammarReference)ref).entry;
         if (entry != null) {
            this.removeEntry(entry);
         }
      }

   }

   static final class SoftGrammarReference extends SoftReference {
      public SoftReferenceGrammarPool.Entry entry;

      protected SoftGrammarReference(SoftReferenceGrammarPool.Entry entry, Grammar grammar, ReferenceQueue queue) {
         super(grammar, queue);
         this.entry = entry;
      }
   }

   static final class Entry {
      public int hash;
      public int bucket;
      public SoftReferenceGrammarPool.Entry prev;
      public SoftReferenceGrammarPool.Entry next;
      public XMLGrammarDescription desc;
      public SoftReferenceGrammarPool.SoftGrammarReference grammar;

      protected Entry(int hash, int bucket, XMLGrammarDescription desc, Grammar grammar, SoftReferenceGrammarPool.Entry next, ReferenceQueue queue) {
         this.hash = hash;
         this.bucket = bucket;
         this.prev = null;
         this.next = next;
         if (next != null) {
            next.prev = this;
         }

         this.desc = desc;
         this.grammar = new SoftReferenceGrammarPool.SoftGrammarReference(this, grammar, queue);
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
