package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelImpl;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.util.ArrayList;

public class XSGrammarPool extends XMLGrammarPoolImpl {
   public XSModel toXSModel() {
      return this.toXSModel((short)1);
   }

   public XSModel toXSModel(short schemaVersion) {
      ArrayList list = new ArrayList();

      int size;
      for(size = 0; size < this.fGrammars.length; ++size) {
         for(XMLGrammarPoolImpl.Entry entry = this.fGrammars[size]; entry != null; entry = entry.next) {
            if (entry.desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema")) {
               list.add(entry.grammar);
            }
         }
      }

      size = list.size();
      if (size == 0) {
         return this.toXSModel(new SchemaGrammar[0], schemaVersion);
      } else {
         SchemaGrammar[] gs = (SchemaGrammar[])((SchemaGrammar[])list.toArray(new SchemaGrammar[size]));
         return this.toXSModel(gs, schemaVersion);
      }
   }

   protected XSModel toXSModel(SchemaGrammar[] grammars, short schemaVersion) {
      return new XSModelImpl(grammars, schemaVersion);
   }
}
