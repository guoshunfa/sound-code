package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public class XMLGrammarCachingConfiguration extends XIncludeAwareParserConfiguration {
   public static final int BIG_PRIME = 2039;
   protected static final SynchronizedSymbolTable fStaticSymbolTable = new SynchronizedSymbolTable(2039);
   protected static final XMLGrammarPoolImpl fStaticGrammarPool = new XMLGrammarPoolImpl();
   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   protected XMLSchemaLoader fSchemaLoader;
   protected XMLDTDLoader fDTDLoader;

   public XMLGrammarCachingConfiguration() {
      this(fStaticSymbolTable, fStaticGrammarPool, (XMLComponentManager)null);
   }

   public XMLGrammarCachingConfiguration(SymbolTable symbolTable) {
      this(symbolTable, fStaticGrammarPool, (XMLComponentManager)null);
   }

   public XMLGrammarCachingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLComponentManager)null);
   }

   public XMLGrammarCachingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
      super(symbolTable, grammarPool, parentSettings);
      this.fSchemaLoader = new XMLSchemaLoader(this.fSymbolTable);
      this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
      this.fDTDLoader = new XMLDTDLoader(this.fSymbolTable, this.fGrammarPool);
   }

   public void lockGrammarPool() {
      this.fGrammarPool.lockPool();
   }

   public void clearGrammarPool() {
      this.fGrammarPool.clear();
   }

   public void unlockGrammarPool() {
      this.fGrammarPool.unlockPool();
   }

   public Grammar parseGrammar(String type, String uri) throws XNIException, IOException {
      XMLInputSource source = new XMLInputSource((String)null, uri, (String)null);
      return this.parseGrammar(type, source);
   }

   public Grammar parseGrammar(String type, XMLInputSource is) throws XNIException, IOException {
      if (type.equals("http://www.w3.org/2001/XMLSchema")) {
         return this.parseXMLSchema(is);
      } else {
         return type.equals("http://www.w3.org/TR/REC-xml") ? this.parseDTD(is) : null;
      }
   }

   SchemaGrammar parseXMLSchema(XMLInputSource is) throws IOException {
      XMLEntityResolver resolver = this.getEntityResolver();
      if (resolver != null) {
         this.fSchemaLoader.setEntityResolver(resolver);
      }

      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
      }

      this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      String propPrefix = "http://apache.org/xml/properties/";
      String propName = propPrefix + "schema/external-schemaLocation";
      this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
      propName = propPrefix + "schema/external-noNamespaceSchemaLocation";
      this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
      propName = "http://java.sun.com/xml/jaxp/properties/schemaSource";
      this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
      this.fSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", this.getFeature("http://apache.org/xml/features/validation/schema-full-checking"));
      SchemaGrammar grammar = (SchemaGrammar)this.fSchemaLoader.loadGrammar(is);
      if (grammar != null) {
         this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", new Grammar[]{grammar});
      }

      return grammar;
   }

   DTDGrammar parseDTD(XMLInputSource is) throws IOException {
      XMLEntityResolver resolver = this.getEntityResolver();
      if (resolver != null) {
         this.fDTDLoader.setEntityResolver(resolver);
      }

      this.fDTDLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      DTDGrammar grammar = (DTDGrammar)this.fDTDLoader.loadGrammar(is);
      if (grammar != null) {
         this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[]{grammar});
      }

      return grammar;
   }
}
