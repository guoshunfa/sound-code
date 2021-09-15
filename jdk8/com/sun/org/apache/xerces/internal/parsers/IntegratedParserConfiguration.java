package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;

public class IntegratedParserConfiguration extends StandardParserConfiguration {
   protected XMLNSDocumentScannerImpl fNamespaceScanner;
   protected XMLDocumentScannerImpl fNonNSScanner;
   protected XMLDTDValidator fNonNSDTDValidator;

   public IntegratedParserConfiguration() {
      this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public IntegratedParserConfiguration(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public IntegratedParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLComponentManager)null);
   }

   public IntegratedParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
      super(symbolTable, grammarPool, parentSettings);
      this.fNonNSScanner = new XMLDocumentScannerImpl();
      this.fNonNSDTDValidator = new XMLDTDValidator();
      this.addComponent(this.fNonNSScanner);
      this.addComponent(this.fNonNSDTDValidator);
   }

   protected void configurePipeline() {
      this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
      this.configureDTDPipeline();
      if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
         this.fProperties.put("http://apache.org/xml/properties/internal/namespace-binder", this.fNamespaceBinder);
         this.fScanner = this.fNamespaceScanner;
         this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
         if (this.fDTDValidator != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
            this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
            this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
            this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
            this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
               this.fDocumentHandler.setDocumentSource(this.fDTDValidator);
            }

            this.fLastComponent = this.fDTDValidator;
         } else {
            this.fNamespaceScanner.setDocumentHandler(this.fDocumentHandler);
            this.fNamespaceScanner.setDTDValidator((XMLDTDValidatorFilter)null);
            if (this.fDocumentHandler != null) {
               this.fDocumentHandler.setDocumentSource(this.fNamespaceScanner);
            }

            this.fLastComponent = this.fNamespaceScanner;
         }
      } else {
         this.fScanner = this.fNonNSScanner;
         this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
         if (this.fNonNSDTDValidator != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
            this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
            this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
            this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
               this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator);
            }

            this.fLastComponent = this.fNonNSDTDValidator;
         } else {
            this.fScanner.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
               this.fDocumentHandler.setDocumentSource(this.fScanner);
            }

            this.fLastComponent = this.fScanner;
         }
      }

      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
         if (this.fSchemaValidator == null) {
            this.fSchemaValidator = new XMLSchemaValidator();
            this.fProperties.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
            this.addComponent(this.fSchemaValidator);
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
               XSMessageFormatter xmft = new XSMessageFormatter();
               this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
            }
         }

         this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
         this.fSchemaValidator.setDocumentSource(this.fLastComponent);
         this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fSchemaValidator);
         }

         this.fLastComponent = this.fSchemaValidator;
      }

   }

   protected XMLDocumentScanner createDocumentScanner() {
      this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
      return this.fNamespaceScanner;
   }

   protected XMLDTDValidator createDTDValidator() {
      return new XMLNSDTDValidator();
   }
}
