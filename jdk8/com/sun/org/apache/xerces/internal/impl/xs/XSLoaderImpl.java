package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;

public final class XSLoaderImpl implements XSLoader, DOMConfiguration {
   private final XSGrammarPool fGrammarPool = new XSLoaderImpl.XSGrammarMerger();
   private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader();

   public XSLoaderImpl() {
      this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
   }

   public DOMConfiguration getConfig() {
      return this;
   }

   public XSModel loadURIList(StringList uriList) {
      int length = uriList.getLength();

      try {
         this.fGrammarPool.clear();

         for(int i = 0; i < length; ++i) {
            this.fSchemaLoader.loadGrammar(new XMLInputSource((String)null, uriList.item(i), (String)null));
         }

         return this.fGrammarPool.toXSModel();
      } catch (Exception var4) {
         this.fSchemaLoader.reportDOMFatalError(var4);
         return null;
      }
   }

   public XSModel loadInputList(LSInputList is) {
      int length = is.getLength();

      try {
         this.fGrammarPool.clear();

         for(int i = 0; i < length; ++i) {
            this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(is.item(i)));
         }

         return this.fGrammarPool.toXSModel();
      } catch (Exception var4) {
         this.fSchemaLoader.reportDOMFatalError(var4);
         return null;
      }
   }

   public XSModel loadURI(String uri) {
      try {
         this.fGrammarPool.clear();
         return ((XSGrammar)this.fSchemaLoader.loadGrammar(new XMLInputSource((String)null, uri, (String)null))).toXSModel();
      } catch (Exception var3) {
         this.fSchemaLoader.reportDOMFatalError(var3);
         return null;
      }
   }

   public XSModel load(LSInput is) {
      try {
         this.fGrammarPool.clear();
         return ((XSGrammar)this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(is))).toXSModel();
      } catch (Exception var3) {
         this.fSchemaLoader.reportDOMFatalError(var3);
         return null;
      }
   }

   public void setParameter(String name, Object value) throws DOMException {
      this.fSchemaLoader.setParameter(name, value);
   }

   public Object getParameter(String name) throws DOMException {
      return this.fSchemaLoader.getParameter(name);
   }

   public boolean canSetParameter(String name, Object value) {
      return this.fSchemaLoader.canSetParameter(name, value);
   }

   public DOMStringList getParameterNames() {
      return this.fSchemaLoader.getParameterNames();
   }

   private static final class XSGrammarMerger extends XSGrammarPool {
      public XSGrammarMerger() {
      }

      public void putGrammar(Grammar grammar) {
         SchemaGrammar cachedGrammar = this.toSchemaGrammar(super.getGrammar(grammar.getGrammarDescription()));
         if (cachedGrammar != null) {
            SchemaGrammar newGrammar = this.toSchemaGrammar(grammar);
            if (newGrammar != null) {
               this.mergeSchemaGrammars(cachedGrammar, newGrammar);
            }
         } else {
            super.putGrammar(grammar);
         }

      }

      private SchemaGrammar toSchemaGrammar(Grammar grammar) {
         return grammar instanceof SchemaGrammar ? (SchemaGrammar)grammar : null;
      }

      private void mergeSchemaGrammars(SchemaGrammar cachedGrammar, SchemaGrammar newGrammar) {
         XSNamedMap map = newGrammar.getComponents((short)2);
         int length = map.getLength();

         int i;
         for(i = 0; i < length; ++i) {
            XSElementDecl decl = (XSElementDecl)map.item(i);
            if (cachedGrammar.getGlobalElementDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalElementDecl(decl);
            }
         }

         map = newGrammar.getComponents((short)1);
         length = map.getLength();

         for(i = 0; i < length; ++i) {
            XSAttributeDecl decl = (XSAttributeDecl)map.item(i);
            if (cachedGrammar.getGlobalAttributeDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalAttributeDecl(decl);
            }
         }

         map = newGrammar.getComponents((short)3);
         length = map.getLength();

         for(i = 0; i < length; ++i) {
            XSTypeDefinition decl = (XSTypeDefinition)map.item(i);
            if (cachedGrammar.getGlobalTypeDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalTypeDecl(decl);
            }
         }

         map = newGrammar.getComponents((short)5);
         length = map.getLength();

         for(i = 0; i < length; ++i) {
            XSAttributeGroupDecl decl = (XSAttributeGroupDecl)map.item(i);
            if (cachedGrammar.getGlobalAttributeGroupDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalAttributeGroupDecl(decl);
            }
         }

         map = newGrammar.getComponents((short)7);
         length = map.getLength();

         for(i = 0; i < length; ++i) {
            XSGroupDecl decl = (XSGroupDecl)map.item(i);
            if (cachedGrammar.getGlobalGroupDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalGroupDecl(decl);
            }
         }

         map = newGrammar.getComponents((short)11);
         length = map.getLength();

         for(i = 0; i < length; ++i) {
            XSNotationDecl decl = (XSNotationDecl)map.item(i);
            if (cachedGrammar.getGlobalNotationDecl(decl.getName()) == null) {
               cachedGrammar.addGlobalNotationDecl(decl);
            }
         }

         XSObjectList annotations = newGrammar.getAnnotations();
         length = annotations.getLength();

         for(int i = 0; i < length; ++i) {
            cachedGrammar.addAnnotation((XSAnnotationImpl)annotations.item(i));
         }

      }

      public boolean containsGrammar(XMLGrammarDescription desc) {
         return false;
      }

      public Grammar getGrammar(XMLGrammarDescription desc) {
         return null;
      }

      public Grammar retrieveGrammar(XMLGrammarDescription desc) {
         return null;
      }

      public Grammar[] retrieveInitialGrammarSet(String grammarType) {
         return new Grammar[0];
      }
   }
}
