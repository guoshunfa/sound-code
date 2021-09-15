package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class XMLGrammarPreparser {
   private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   private static final Map<String, String> KNOWN_LOADERS;
   private static final String[] RECOGNIZED_PROPERTIES;
   protected SymbolTable fSymbolTable;
   protected XMLErrorReporter fErrorReporter;
   protected XMLEntityResolver fEntityResolver;
   protected XMLGrammarPool fGrammarPool;
   protected Locale fLocale;
   private Map<String, XMLGrammarLoader> fLoaders;

   public XMLGrammarPreparser() {
      this(new SymbolTable());
   }

   public XMLGrammarPreparser(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
      this.fLoaders = new HashMap();
      this.fErrorReporter = new XMLErrorReporter();
      this.setLocale(Locale.getDefault());
      this.fEntityResolver = new XMLEntityManager();
   }

   public boolean registerPreparser(String grammarType, XMLGrammarLoader loader) {
      if (loader == null) {
         if (KNOWN_LOADERS.containsKey(grammarType)) {
            String loaderName = (String)KNOWN_LOADERS.get(grammarType);

            try {
               XMLGrammarLoader gl = (XMLGrammarLoader)((XMLGrammarLoader)ObjectFactory.newInstance(loaderName, true));
               this.fLoaders.put(grammarType, gl);
               return true;
            } catch (Exception var5) {
               return false;
            }
         } else {
            return false;
         }
      } else {
         this.fLoaders.put(grammarType, loader);
         return true;
      }
   }

   public Grammar preparseGrammar(String type, XMLInputSource is) throws XNIException, IOException {
      if (this.fLoaders.containsKey(type)) {
         XMLGrammarLoader gl = (XMLGrammarLoader)this.fLoaders.get(type);
         gl.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
         gl.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
         gl.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
         if (this.fGrammarPool != null) {
            try {
               gl.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
            } catch (Exception var5) {
            }
         }

         return gl.loadGrammar(is);
      } else {
         return null;
      }
   }

   public void setLocale(Locale locale) {
      this.fLocale = locale;
      this.fErrorReporter.setLocale(locale);
   }

   public Locale getLocale() {
      return this.fLocale;
   }

   public void setErrorHandler(XMLErrorHandler errorHandler) {
      this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler);
   }

   public XMLErrorHandler getErrorHandler() {
      return this.fErrorReporter.getErrorHandler();
   }

   public void setEntityResolver(XMLEntityResolver entityResolver) {
      this.fEntityResolver = entityResolver;
   }

   public XMLEntityResolver getEntityResolver() {
      return this.fEntityResolver;
   }

   public void setGrammarPool(XMLGrammarPool grammarPool) {
      this.fGrammarPool = grammarPool;
   }

   public XMLGrammarPool getGrammarPool() {
      return this.fGrammarPool;
   }

   public XMLGrammarLoader getLoader(String type) {
      return (XMLGrammarLoader)this.fLoaders.get(type);
   }

   public void setFeature(String featureId, boolean value) {
      Iterator var3 = this.fLoaders.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();

         try {
            XMLGrammarLoader gl = (XMLGrammarLoader)entry.getValue();
            gl.setFeature(featureId, value);
         } catch (Exception var6) {
         }
      }

      if (featureId.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
         this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", value);
      }

   }

   public void setProperty(String propId, Object value) {
      Iterator var3 = this.fLoaders.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();

         try {
            XMLGrammarLoader gl = (XMLGrammarLoader)entry.getValue();
            gl.setProperty(propId, value);
         } catch (Exception var6) {
         }
      }

   }

   public boolean getFeature(String type, String featureId) {
      XMLGrammarLoader gl = (XMLGrammarLoader)this.fLoaders.get(type);
      return gl.getFeature(featureId);
   }

   public Object getProperty(String type, String propertyId) {
      XMLGrammarLoader gl = (XMLGrammarLoader)this.fLoaders.get(type);
      return gl.getProperty(propertyId);
   }

   static {
      Map<String, String> loaders = new HashMap();
      loaders.put("http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader");
      loaders.put("http://www.w3.org/TR/REC-xml", "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader");
      KNOWN_LOADERS = Collections.unmodifiableMap(loaders);
      RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool"};
   }
}
