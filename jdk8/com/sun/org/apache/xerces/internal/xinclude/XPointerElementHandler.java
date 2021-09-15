package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;

public class XPointerElementHandler implements XPointerSchema {
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected static final String XPOINTER_SCHEMA = "http://apache.org/xml/properties/xpointer-schema";
   private static final String[] RECOGNIZED_FEATURES = new String[0];
   private static final Boolean[] FEATURE_DEFAULTS = new Boolean[0];
   private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/xpointer-schema"};
   private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null};
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDocumentSource fDocumentSource;
   protected XIncludeHandler fParentXIncludeHandler;
   protected XMLLocator fDocLocation;
   protected XIncludeNamespaceSupport fNamespaceContext;
   protected XMLErrorReporter fErrorReporter;
   protected XMLGrammarPool fGrammarPool;
   protected XMLGrammarDescription fGrammarDesc;
   protected DTDGrammar fDTDGrammar;
   protected XMLEntityResolver fEntityResolver;
   protected ParserConfigurationSettings fSettings;
   protected StringBuffer fPointer;
   private int elemCount = 0;
   private int fDepth = 0;
   private int fRootDepth = 0;
   private static final int INITIAL_SIZE = 8;
   private boolean[] fSawInclude = new boolean[8];
   private boolean[] fSawFallback = new boolean[8];
   private int[] fState = new int[8];
   QName foundElement = null;
   boolean skip = false;
   String fSchemaName;
   String fSchemaPointer;
   boolean fSubResourceIdentified;
   Stack fPointerToken = new Stack();
   int fCurrentTokenint = 0;
   String fCurrentTokenString = null;
   int fCurrentTokenType = 0;
   Stack ftempCurrentElement = new Stack();
   int fElementCount = 0;
   int fCurrentToken;
   boolean includeElement;

   public XPointerElementHandler() {
      this.fSawFallback[this.fDepth] = false;
      this.fSawInclude[this.fDepth] = false;
      this.fSchemaName = "element";
   }

   public void reset() {
      this.elemCount = 0;
      this.fPointerToken = null;
      this.fCurrentTokenint = 0;
      this.fCurrentTokenString = null;
      this.fCurrentTokenType = 0;
      this.fElementCount = 0;
      this.fCurrentToken = 0;
      this.includeElement = false;
      this.foundElement = null;
      this.skip = false;
      this.fSubResourceIdentified = false;
   }

   public void reset(XMLComponentManager componentManager) throws XNIException {
      this.fNamespaceContext = null;
      this.elemCount = 0;
      this.fDepth = 0;
      this.fRootDepth = 0;
      this.fPointerToken = null;
      this.fCurrentTokenint = 0;
      this.fCurrentTokenString = null;
      this.fCurrentTokenType = 0;
      this.foundElement = null;
      this.includeElement = false;
      this.skip = false;
      this.fSubResourceIdentified = false;

      try {
         this.setErrorReporter((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
      } catch (XMLConfigurationException var8) {
         this.fErrorReporter = null;
      }

      try {
         this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
      } catch (XMLConfigurationException var7) {
         this.fGrammarPool = null;
      }

      try {
         this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      } catch (XMLConfigurationException var6) {
         this.fEntityResolver = null;
      }

      this.fSettings = new ParserConfigurationSettings();
      Enumeration xercesFeatures = Constants.getXercesFeatures();

      while(xercesFeatures.hasMoreElements()) {
         String featureId = (String)xercesFeatures.nextElement();
         this.fSettings.addRecognizedFeatures(new String[]{featureId});

         try {
            this.fSettings.setFeature(featureId, componentManager.getFeature(featureId));
         } catch (XMLConfigurationException var5) {
         }
      }

   }

   public String[] getRecognizedFeatures() {
      return RECOGNIZED_FEATURES;
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      if (this.fSettings != null) {
         this.fSettings.setFeature(featureId, state);
      }

   }

   public String[] getRecognizedProperties() {
      return RECOGNIZED_PROPERTIES;
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
         this.setErrorReporter((XMLErrorReporter)value);
      }

      if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
         this.fGrammarPool = (XMLGrammarPool)value;
      }

      if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
         this.fEntityResolver = (XMLEntityResolver)value;
      }

   }

   public Boolean getFeatureDefault(String featureId) {
      for(int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
         if (RECOGNIZED_FEATURES[i].equals(featureId)) {
            return FEATURE_DEFAULTS[i];
         }
      }

      return null;
   }

   public Object getPropertyDefault(String propertyId) {
      for(int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
         if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
            return PROPERTY_DEFAULTS[i];
         }
      }

      return null;
   }

   private void setErrorReporter(XMLErrorReporter reporter) {
      this.fErrorReporter = reporter;
      if (this.fErrorReporter != null) {
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", new XIncludeMessageFormatter());
      }

   }

   public void setDocumentHandler(XMLDocumentHandler handler) {
      this.fDocumentHandler = handler;
   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fDocumentHandler;
   }

   public void setXPointerSchemaName(String schemaName) {
      this.fSchemaName = schemaName;
   }

   public String getXpointerSchemaName() {
      return this.fSchemaName;
   }

   public void setParent(Object parent) {
      this.fParentXIncludeHandler = (XIncludeHandler)parent;
   }

   public Object getParent() {
      return this.fParentXIncludeHandler;
   }

   public void setXPointerSchemaPointer(String content) {
      this.fSchemaPointer = content;
   }

   public String getXPointerSchemaPointer() {
      return this.fSchemaPointer;
   }

   public boolean isSubResourceIndentified() {
      return this.fSubResourceIdentified;
   }

   public void getTokens() {
      this.fSchemaPointer = this.fSchemaPointer.substring(this.fSchemaPointer.indexOf("(") + 1, this.fSchemaPointer.length());
      StringTokenizer st = new StringTokenizer(this.fSchemaPointer, "/");
      Integer integerToken = null;
      Stack tempPointerToken = new Stack();
      if (this.fPointerToken == null) {
         this.fPointerToken = new Stack();
      }

      while(st.hasMoreTokens()) {
         String tempToken = st.nextToken();

         try {
            integerToken = Integer.valueOf(tempToken);
            tempPointerToken.push(integerToken);
         } catch (NumberFormatException var6) {
            tempPointerToken.push(tempToken);
         }
      }

      while(!tempPointerToken.empty()) {
         this.fPointerToken.push(tempPointerToken.pop());
      }

   }

   public boolean hasMoreToken() {
      return !this.fPointerToken.isEmpty();
   }

   public boolean getNextToken() {
      if (!this.fPointerToken.isEmpty()) {
         Object currentToken = this.fPointerToken.pop();
         if (currentToken instanceof Integer) {
            this.fCurrentTokenint = (Integer)currentToken;
            this.fCurrentTokenType = 1;
         } else {
            this.fCurrentTokenString = ((String)currentToken).toString();
            this.fCurrentTokenType = 2;
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isIdAttribute(XMLAttributes attributes, Augmentations augs, int index) {
      Object o = augs.getItem("ID_ATTRIBUTE");
      return o instanceof Boolean ? (Boolean)o : "ID".equals(attributes.getType(index));
   }

   public boolean checkStringToken(QName element, XMLAttributes attributes) {
      QName cacheQName = null;
      String id = null;
      String rawname = null;
      QName attrName = new QName();
      String attrType = null;
      String attrValue = null;
      int attrCount = attributes.getLength();

      for(int i = 0; i < attrCount; ++i) {
         Augmentations aaugs = attributes.getAugmentations(i);
         attributes.getName(i, attrName);
         attrType = attributes.getType(i);
         attrValue = attributes.getValue(i);
         if (attrType != null && attrValue != null && this.isIdAttribute(attributes, aaugs, i) && attrValue.equals(this.fCurrentTokenString)) {
            if (this.hasMoreToken()) {
               this.fCurrentTokenType = 0;
               this.fCurrentTokenString = null;
               return true;
            }

            this.foundElement = element;
            this.includeElement = true;
            this.fCurrentTokenType = 0;
            this.fCurrentTokenString = null;
            this.fSubResourceIdentified = true;
            return true;
         }
      }

      return false;
   }

   public boolean checkIntegerToken(QName element) {
      if (!this.skip) {
         ++this.fElementCount;
         if (this.fCurrentTokenint == this.fElementCount) {
            if (this.hasMoreToken()) {
               this.fElementCount = 0;
               this.fCurrentTokenType = 0;
               return true;
            } else {
               this.foundElement = element;
               this.includeElement = true;
               this.fCurrentTokenType = 0;
               this.fElementCount = 0;
               this.fSubResourceIdentified = true;
               return true;
            }
         } else {
            this.addQName(element);
            this.skip = true;
            return false;
         }
      } else {
         return false;
      }
   }

   public void addQName(QName element) {
      QName cacheQName = new QName(element);
      this.ftempCurrentElement.push(cacheQName);
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.getTokens();
   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.comment(text, augs);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.processingInstruction(target, data, augs);
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      boolean requiredToken = false;
      if (this.fCurrentTokenType == 0) {
         this.getNextToken();
      }

      if (this.fCurrentTokenType == 1) {
         requiredToken = this.checkIntegerToken(element);
      } else if (this.fCurrentTokenType == 2) {
         requiredToken = this.checkStringToken(element, attributes);
      }

      if (requiredToken && this.hasMoreToken()) {
         this.getNextToken();
      }

      if (this.fDocumentHandler != null && this.includeElement) {
         ++this.elemCount;
         this.fDocumentHandler.startElement(element, attributes, augs);
      }

   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.includeElement && this.foundElement != null) {
         if (this.elemCount > 0) {
            --this.elemCount;
         }

         this.fDocumentHandler.endElement(element, augs);
         if (this.elemCount == 0) {
            this.includeElement = false;
         }
      } else if (!this.ftempCurrentElement.empty()) {
         QName name = (QName)this.ftempCurrentElement.peek();
         if (name.equals(element)) {
            this.ftempCurrentElement.pop();
            this.skip = false;
         }
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.emptyElement(element, attributes, augs);
      }

   }

   public void startGeneralEntity(String name, XMLResourceIdentifier resId, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
      }

   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.textDecl(version, encoding, augs);
      }

   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.endGeneralEntity(name, augs);
      }

   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.characters(text, augs);
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.ignorableWhitespace(text, augs);
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.startCDATA(augs);
      }

   }

   public void endCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.includeElement) {
         this.fDocumentHandler.endCDATA(augs);
      }

   }

   public void endDocument(Augmentations augs) throws XNIException {
   }

   public void setDocumentSource(XMLDocumentSource source) {
      this.fDocumentSource = source;
   }

   public XMLDocumentSource getDocumentSource() {
      return this.fDocumentSource;
   }

   protected void reportFatalError(String key) {
      this.reportFatalError(key, (Object[])null);
   }

   protected void reportFatalError(String key, Object[] args) {
      if (this.fErrorReporter != null) {
         this.fErrorReporter.reportError(this.fDocLocation, "http://www.w3.org/TR/xinclude", key, args, (short)2);
      }

   }

   protected boolean isRootDocument() {
      return this.fParentXIncludeHandler == null;
   }
}
