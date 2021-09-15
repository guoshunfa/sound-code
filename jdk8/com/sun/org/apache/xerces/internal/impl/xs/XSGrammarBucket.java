package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class XSGrammarBucket {
   Map<String, SchemaGrammar> fGrammarRegistry = new HashMap();
   SchemaGrammar fNoNSGrammar = null;

   public SchemaGrammar getGrammar(String namespace) {
      return namespace == null ? this.fNoNSGrammar : (SchemaGrammar)this.fGrammarRegistry.get(namespace);
   }

   public void putGrammar(SchemaGrammar grammar) {
      if (grammar.getTargetNamespace() == null) {
         this.fNoNSGrammar = grammar;
      } else {
         this.fGrammarRegistry.put(grammar.getTargetNamespace(), grammar);
      }

   }

   public boolean putGrammar(SchemaGrammar grammar, boolean deep) {
      SchemaGrammar sg = this.getGrammar(grammar.fTargetNamespace);
      if (sg != null) {
         return sg == grammar;
      } else if (!deep) {
         this.putGrammar(grammar);
         return true;
      } else {
         Vector currGrammars = grammar.getImportedGrammars();
         if (currGrammars == null) {
            this.putGrammar(grammar);
            return true;
         } else {
            Vector grammars = (Vector)currGrammars.clone();

            int i;
            for(i = 0; i < grammars.size(); ++i) {
               SchemaGrammar sg1 = (SchemaGrammar)grammars.elementAt(i);
               SchemaGrammar sg2 = this.getGrammar(sg1.fTargetNamespace);
               if (sg2 == null) {
                  Vector gs = sg1.getImportedGrammars();
                  if (gs != null) {
                     for(int j = gs.size() - 1; j >= 0; --j) {
                        sg2 = (SchemaGrammar)gs.elementAt(j);
                        if (!grammars.contains(sg2)) {
                           grammars.addElement(sg2);
                        }
                     }
                  }
               } else if (sg2 != sg1) {
                  return false;
               }
            }

            this.putGrammar(grammar);

            for(i = grammars.size() - 1; i >= 0; --i) {
               this.putGrammar((SchemaGrammar)grammars.elementAt(i));
            }

            return true;
         }
      }
   }

   public boolean putGrammar(SchemaGrammar grammar, boolean deep, boolean ignoreConflict) {
      if (!ignoreConflict) {
         return this.putGrammar(grammar, deep);
      } else {
         SchemaGrammar sg = this.getGrammar(grammar.fTargetNamespace);
         if (sg == null) {
            this.putGrammar(grammar);
         }

         if (!deep) {
            return true;
         } else {
            Vector currGrammars = grammar.getImportedGrammars();
            if (currGrammars == null) {
               return true;
            } else {
               Vector grammars = (Vector)currGrammars.clone();

               int i;
               for(i = 0; i < grammars.size(); ++i) {
                  SchemaGrammar sg1 = (SchemaGrammar)grammars.elementAt(i);
                  SchemaGrammar sg2 = this.getGrammar(sg1.fTargetNamespace);
                  if (sg2 == null) {
                     Vector gs = sg1.getImportedGrammars();
                     if (gs != null) {
                        for(int j = gs.size() - 1; j >= 0; --j) {
                           sg2 = (SchemaGrammar)gs.elementAt(j);
                           if (!grammars.contains(sg2)) {
                              grammars.addElement(sg2);
                           }
                        }
                     }
                  } else {
                     grammars.remove(sg1);
                  }
               }

               for(i = grammars.size() - 1; i >= 0; --i) {
                  this.putGrammar((SchemaGrammar)grammars.elementAt(i));
               }

               return true;
            }
         }
      }
   }

   public SchemaGrammar[] getGrammars() {
      int count = this.fGrammarRegistry.size() + (this.fNoNSGrammar == null ? 0 : 1);
      SchemaGrammar[] grammars = new SchemaGrammar[count];
      int i = 0;

      Map.Entry entry;
      for(Iterator var4 = this.fGrammarRegistry.entrySet().iterator(); var4.hasNext(); grammars[i++] = (SchemaGrammar)entry.getValue()) {
         entry = (Map.Entry)var4.next();
      }

      if (this.fNoNSGrammar != null) {
         grammars[count - 1] = this.fNoNSGrammar;
      }

      return grammars;
   }

   public void reset() {
      this.fNoNSGrammar = null;
      this.fGrammarRegistry.clear();
   }
}
