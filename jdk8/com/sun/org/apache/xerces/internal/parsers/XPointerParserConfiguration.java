package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;

public class XPointerParserConfiguration extends XML11Configuration {
   private XPointerHandler fXPointerHandler;
   private XIncludeHandler fXIncludeHandler;
   protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
   protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
   protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
   protected static final String XPOINTER_HANDLER = "http://apache.org/xml/properties/internal/xpointer-handler";
   protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
   protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";

   public XPointerParserConfiguration() {
      this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public XPointerParserConfiguration(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public XPointerParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLComponentManager)null);
   }

   public XPointerParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
      super(symbolTable, grammarPool, parentSettings);
      this.fXIncludeHandler = new XIncludeHandler();
      this.addCommonComponent(this.fXIncludeHandler);
      this.fXPointerHandler = new XPointerHandler();
      this.addCommonComponent(this.fXPointerHandler);
      String[] recognizedFeatures = new String[]{"http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language"};
      this.addRecognizedFeatures(recognizedFeatures);
      String[] recognizedProperties = new String[]{"http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/xpointer-handler", "http://apache.org/xml/properties/internal/namespace-context"};
      this.addRecognizedProperties(recognizedProperties);
      this.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
      this.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
      this.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
      this.setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
      this.setProperty("http://apache.org/xml/properties/internal/xpointer-handler", this.fXPointerHandler);
      this.setProperty("http://apache.org/xml/properties/internal/namespace-context", new XIncludeNamespaceSupport());
   }

   protected void configurePipeline() {
      super.configurePipeline();
      this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
      this.fDTDProcessor.setDTDSource(this.fDTDScanner);
      this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fXPointerHandler);
      this.fXPointerHandler.setDTDSource(this.fXIncludeHandler);
      this.fXPointerHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.setDTDSource(this.fXPointerHandler);
      }

      XMLDocumentSource prev = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
         prev = this.fSchemaValidator.getDocumentSource();
      } else {
         prev = this.fLastComponent;
         this.fLastComponent = this.fXPointerHandler;
      }

      XMLDocumentHandler next = prev.getDocumentHandler();
      prev.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(prev);
      if (next != null) {
         this.fXIncludeHandler.setDocumentHandler(next);
         next.setDocumentSource(this.fXIncludeHandler);
      }

      this.fXIncludeHandler.setDocumentHandler(this.fXPointerHandler);
      this.fXPointerHandler.setDocumentSource(this.fXIncludeHandler);
   }

   protected void configureXML11Pipeline() {
      super.configureXML11Pipeline();
      this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
      this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
      this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fXPointerHandler);
      this.fXPointerHandler.setDTDSource(this.fXIncludeHandler);
      this.fXPointerHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.setDTDSource(this.fXPointerHandler);
      }

      XMLDocumentSource prev = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
         prev = this.fSchemaValidator.getDocumentSource();
      } else {
         prev = this.fLastComponent;
         this.fLastComponent = this.fXPointerHandler;
      }

      XMLDocumentHandler next = prev.getDocumentHandler();
      prev.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(prev);
      if (next != null) {
         this.fXIncludeHandler.setDocumentHandler(next);
         next.setDocumentSource(this.fXIncludeHandler);
      }

      this.fXIncludeHandler.setDocumentHandler(this.fXPointerHandler);
      this.fXPointerHandler.setDocumentSource(this.fXIncludeHandler);
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      super.setProperty(propertyId, value);
   }
}
