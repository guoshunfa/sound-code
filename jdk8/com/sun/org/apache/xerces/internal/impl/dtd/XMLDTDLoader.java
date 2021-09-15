package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

public class XMLDTDLoader extends XMLDTDProcessor implements XMLGrammarLoader {
   protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
   protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
   private static final String[] LOADER_RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/validation/balance-syntax-trees"};
   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   public static final String LOCALE = "http://apache.org/xml/properties/locale";
   private static final String[] LOADER_RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/locale"};
   private boolean fStrictURI;
   private boolean fBalanceSyntaxTrees;
   protected XMLEntityResolver fEntityResolver;
   protected XMLDTDScannerImpl fDTDScanner;
   protected XMLEntityManager fEntityManager;
   protected Locale fLocale;

   public XMLDTDLoader() {
      this(new SymbolTable());
   }

   public XMLDTDLoader(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null);
   }

   public XMLDTDLoader(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLErrorReporter)null, new XMLEntityManager());
   }

   XMLDTDLoader(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLErrorReporter errorReporter, XMLEntityResolver entityResolver) {
      this.fStrictURI = false;
      this.fBalanceSyntaxTrees = false;
      this.fSymbolTable = symbolTable;
      this.fGrammarPool = grammarPool;
      if (errorReporter == null) {
         errorReporter = new XMLErrorReporter();
         errorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
      }

      this.fErrorReporter = errorReporter;
      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
         XMLMessageFormatter xmft = new XMLMessageFormatter();
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
      }

      this.fEntityResolver = entityResolver;
      if (this.fEntityResolver instanceof XMLEntityManager) {
         this.fEntityManager = (XMLEntityManager)this.fEntityResolver;
      } else {
         this.fEntityManager = new XMLEntityManager();
      }

      this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
      this.fDTDScanner = this.createDTDScanner(this.fSymbolTable, this.fErrorReporter, this.fEntityManager);
      this.fDTDScanner.setDTDHandler(this);
      this.fDTDScanner.setDTDContentModelHandler(this);
      this.reset();
   }

   public String[] getRecognizedFeatures() {
      return (String[])((String[])LOADER_RECOGNIZED_FEATURES.clone());
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      if (featureId.equals("http://xml.org/sax/features/validation")) {
         this.fValidation = state;
      } else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
         this.fWarnDuplicateAttdef = state;
      } else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
         this.fWarnOnUndeclaredElemdef = state;
      } else if (featureId.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
         this.fDTDScanner.setFeature(featureId, state);
      } else if (featureId.equals("http://apache.org/xml/features/standard-uri-conformant")) {
         this.fStrictURI = state;
      } else {
         if (!featureId.equals("http://apache.org/xml/features/validation/balance-syntax-trees")) {
            throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
         }

         this.fBalanceSyntaxTrees = state;
      }

   }

   public String[] getRecognizedProperties() {
      return (String[])((String[])LOADER_RECOGNIZED_PROPERTIES.clone());
   }

   public Object getProperty(String propertyId) throws XMLConfigurationException {
      if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
         return this.fSymbolTable;
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
         return this.fErrorReporter;
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/error-handler")) {
         return this.fErrorReporter.getErrorHandler();
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
         return this.fEntityResolver;
      } else if (propertyId.equals("http://apache.org/xml/properties/locale")) {
         return this.getLocale();
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
         return this.fGrammarPool;
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/validator/dtd")) {
         return this.fValidator;
      } else {
         throw new XMLConfigurationException(Status.NOT_RECOGNIZED, propertyId);
      }
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
         this.fSymbolTable = (SymbolTable)value;
         this.fDTDScanner.setProperty(propertyId, value);
         this.fEntityManager.setProperty(propertyId, value);
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
         this.fErrorReporter = (XMLErrorReporter)value;
         if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            XMLMessageFormatter xmft = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
         }

         this.fDTDScanner.setProperty(propertyId, value);
         this.fEntityManager.setProperty(propertyId, value);
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/error-handler")) {
         this.fErrorReporter.setProperty(propertyId, value);
      } else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
         this.fEntityResolver = (XMLEntityResolver)value;
         this.fEntityManager.setProperty(propertyId, value);
      } else if (propertyId.equals("http://apache.org/xml/properties/locale")) {
         this.setLocale((Locale)value);
      } else {
         if (!propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            throw new XMLConfigurationException(Status.NOT_RECOGNIZED, propertyId);
         }

         this.fGrammarPool = (XMLGrammarPool)value;
      }

   }

   public boolean getFeature(String featureId) throws XMLConfigurationException {
      if (featureId.equals("http://xml.org/sax/features/validation")) {
         return this.fValidation;
      } else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
         return this.fWarnDuplicateAttdef;
      } else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
         return this.fWarnOnUndeclaredElemdef;
      } else if (featureId.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
         return this.fDTDScanner.getFeature(featureId);
      } else if (featureId.equals("http://apache.org/xml/features/standard-uri-conformant")) {
         return this.fStrictURI;
      } else if (featureId.equals("http://apache.org/xml/features/validation/balance-syntax-trees")) {
         return this.fBalanceSyntaxTrees;
      } else {
         throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
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
      this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
   }

   public XMLEntityResolver getEntityResolver() {
      return this.fEntityResolver;
   }

   public Grammar loadGrammar(XMLInputSource source) throws IOException, XNIException {
      this.reset();
      String eid = XMLEntityManager.expandSystemId(source.getSystemId(), source.getBaseSystemId(), this.fStrictURI);
      XMLDTDDescription desc = new XMLDTDDescription(source.getPublicId(), source.getSystemId(), source.getBaseSystemId(), eid, (String)null);
      if (!this.fBalanceSyntaxTrees) {
         this.fDTDGrammar = new DTDGrammar(this.fSymbolTable, desc);
      } else {
         this.fDTDGrammar = new BalancedDTDGrammar(this.fSymbolTable, desc);
      }

      this.fGrammarBucket = new DTDGrammarBucket();
      this.fGrammarBucket.setStandalone(false);
      this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);

      try {
         this.fDTDScanner.setInputSource(source);
         this.fDTDScanner.scanDTDExternalSubset(true);
      } catch (EOFException var8) {
      } finally {
         this.fEntityManager.closeReaders();
      }

      if (this.fDTDGrammar != null && this.fGrammarPool != null) {
         this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[]{this.fDTDGrammar});
      }

      return this.fDTDGrammar;
   }

   public void loadGrammarWithContext(XMLDTDValidator validator, String rootName, String publicId, String systemId, String baseSystemId, String internalSubset) throws IOException, XNIException {
      DTDGrammarBucket grammarBucket = validator.getGrammarBucket();
      DTDGrammar activeGrammar = grammarBucket.getActiveGrammar();
      if (activeGrammar != null && !activeGrammar.isImmutable()) {
         this.fGrammarBucket = grammarBucket;
         this.fEntityManager.setScannerVersion(this.getScannerVersion());
         this.reset();

         try {
            XMLInputSource source;
            if (internalSubset != null) {
               StringBuffer buffer = new StringBuffer(internalSubset.length() + 2);
               buffer.append(internalSubset).append("]>");
               source = new XMLInputSource((String)null, baseSystemId, (String)null, new StringReader(buffer.toString()), (String)null);
               this.fEntityManager.startDocumentEntity(source);
               this.fDTDScanner.scanDTDInternalSubset(true, false, systemId != null);
            }

            if (systemId != null) {
               XMLDTDDescription desc = new XMLDTDDescription(publicId, systemId, baseSystemId, (String)null, rootName);
               source = this.fEntityManager.resolveEntity(desc);
               this.fDTDScanner.setInputSource(source);
               this.fDTDScanner.scanDTDExternalSubset(true);
            }
         } catch (EOFException var14) {
         } finally {
            this.fEntityManager.closeReaders();
         }
      }

   }

   protected void reset() {
      super.reset();
      this.fDTDScanner.reset();
      this.fEntityManager.reset();
      this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
   }

   protected XMLDTDScannerImpl createDTDScanner(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
      return new XMLDTDScannerImpl(symbolTable, errorReporter, entityManager);
   }

   protected short getScannerVersion() {
      return 1;
   }
}
