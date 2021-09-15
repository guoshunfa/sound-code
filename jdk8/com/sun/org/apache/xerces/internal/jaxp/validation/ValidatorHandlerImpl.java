package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.EntityResolver2;

final class ValidatorHandlerImpl extends ValidatorHandler implements DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler {
   private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
   protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   private XMLErrorReporter fErrorReporter;
   private NamespaceContext fNamespaceContext;
   private XMLSchemaValidator fSchemaValidator;
   private SymbolTable fSymbolTable;
   private ValidationManager fValidationManager;
   private XMLSchemaValidatorComponentManager fComponentManager;
   private final SAXLocatorWrapper fSAXLocatorWrapper;
   private boolean fNeedPushNSContext;
   private HashMap fUnparsedEntities;
   private boolean fStringsInternalized;
   private final QName fElementQName;
   private final QName fAttributeQName;
   private final XMLAttributesImpl fAttributes;
   private final AttributesProxy fAttrAdapter;
   private final XMLString fTempString;
   private ContentHandler fContentHandler;
   private final ValidatorHandlerImpl.XMLSchemaTypeInfoProvider fTypeInfoProvider;
   private final ValidatorHandlerImpl.ResolutionForwarder fResolutionForwarder;

   public ValidatorHandlerImpl(XSGrammarPoolContainer grammarContainer) {
      this(new XMLSchemaValidatorComponentManager(grammarContainer));
      this.fComponentManager.addRecognizedFeatures(new String[]{"http://xml.org/sax/features/namespace-prefixes"});
      this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      this.setErrorHandler((ErrorHandler)null);
      this.setResourceResolver((LSResourceResolver)null);
   }

   public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager componentManager) {
      this.fSAXLocatorWrapper = new SAXLocatorWrapper();
      this.fNeedPushNSContext = true;
      this.fUnparsedEntities = null;
      this.fStringsInternalized = false;
      this.fElementQName = new QName();
      this.fAttributeQName = new QName();
      this.fAttributes = new XMLAttributesImpl();
      this.fAttrAdapter = new AttributesProxy(this.fAttributes);
      this.fTempString = new XMLString();
      this.fContentHandler = null;
      this.fTypeInfoProvider = new ValidatorHandlerImpl.XMLSchemaTypeInfoProvider();
      this.fResolutionForwarder = new ValidatorHandlerImpl.ResolutionForwarder((LSResourceResolver)null);
      this.fComponentManager = componentManager;
      this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fNamespaceContext = (NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
      this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
      this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
   }

   public void setContentHandler(ContentHandler receiver) {
      this.fContentHandler = receiver;
   }

   public ContentHandler getContentHandler() {
      return this.fContentHandler;
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.fComponentManager.setErrorHandler(errorHandler);
   }

   public ErrorHandler getErrorHandler() {
      return this.fComponentManager.getErrorHandler();
   }

   public void setResourceResolver(LSResourceResolver resourceResolver) {
      this.fComponentManager.setResourceResolver(resourceResolver);
   }

   public LSResourceResolver getResourceResolver() {
      return this.fComponentManager.getResourceResolver();
   }

   public TypeInfoProvider getTypeInfoProvider() {
      return this.fTypeInfoProvider;
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            return this.fComponentManager.getFeature(name);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            String key = var5.getType() == Status.NOT_RECOGNIZED ? "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            this.fComponentManager.setFeature(name, value);
         } catch (XMLConfigurationException var6) {
            String identifier = var6.getIdentifier();
            if (var6.getType() == Status.NOT_ALLOWED) {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", (Object[])null));
            } else {
               String key;
               if (var6.getType() == Status.NOT_RECOGNIZED) {
                  key = "feature-not-recognized";
               } else {
                  key = "feature-not-supported";
               }

               throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
            }
         }
      }
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            return this.fComponentManager.getProperty(name);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            String key = var5.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }
      }
   }

   public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            this.fComponentManager.setProperty(name, object);
         } catch (XMLConfigurationException var6) {
            String identifier = var6.getIdentifier();
            String key = var6.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }
      }
   }

   public boolean isEntityDeclared(String name) {
      return false;
   }

   public boolean isEntityUnparsed(String name) {
      return this.fUnparsedEntities != null ? this.fUnparsedEntities.containsKey(name) : false;
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fContentHandler.startDocument();
         } catch (SAXException var6) {
            throw new XNIException(var6);
         }
      }

   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fContentHandler.processingInstruction(target, data.toString());
         } catch (SAXException var5) {
            throw new XNIException(var5);
         }
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fTypeInfoProvider.beginStartElement(augs, attributes);
            this.fContentHandler.startElement(element.uri != null ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname, this.fAttrAdapter);
         } catch (SAXException var8) {
            throw new XNIException(var8);
         } finally {
            this.fTypeInfoProvider.finishStartElement();
         }
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      this.startElement(element, attributes, augs);
      this.endElement(element, augs);
   }

   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         if (text.length == 0) {
            return;
         }

         try {
            this.fContentHandler.characters(text.ch, text.offset, text.length);
         } catch (SAXException var4) {
            throw new XNIException(var4);
         }
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
         } catch (SAXException var4) {
            throw new XNIException(var4);
         }
      }

   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fTypeInfoProvider.beginEndElement(augs);
            this.fContentHandler.endElement(element.uri != null ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname);
         } catch (SAXException var7) {
            throw new XNIException(var7);
         } finally {
            this.fTypeInfoProvider.finishEndElement();
         }
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
   }

   public void endCDATA(Augmentations augs) throws XNIException {
   }

   public void endDocument(Augmentations augs) throws XNIException {
      if (this.fContentHandler != null) {
         try {
            this.fContentHandler.endDocument();
         } catch (SAXException var3) {
            throw new XNIException(var3);
         }
      }

   }

   public void setDocumentSource(XMLDocumentSource source) {
   }

   public XMLDocumentSource getDocumentSource() {
      return this.fSchemaValidator;
   }

   public void setDocumentLocator(Locator locator) {
      this.fSAXLocatorWrapper.setLocator(locator);
      if (this.fContentHandler != null) {
         this.fContentHandler.setDocumentLocator(locator);
      }

   }

   public void startDocument() throws SAXException {
      this.fComponentManager.reset();
      this.fSchemaValidator.setDocumentHandler(this);
      this.fValidationManager.setEntityState(this);
      this.fTypeInfoProvider.finishStartElement();
      this.fNeedPushNSContext = true;
      if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
         this.fUnparsedEntities.clear();
      }

      this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);

      try {
         this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, (Augmentations)null);
      } catch (XMLParseException var2) {
         throw Util.toSAXParseException(var2);
      } catch (XNIException var3) {
         throw Util.toSAXException(var3);
      }
   }

   public void endDocument() throws SAXException {
      this.fSAXLocatorWrapper.setLocator((Locator)null);

      try {
         this.fSchemaValidator.endDocument((Augmentations)null);
      } catch (XMLParseException var2) {
         throw Util.toSAXParseException(var2);
      } catch (XNIException var3) {
         throw Util.toSAXException(var3);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      String prefixSymbol;
      String uriSymbol;
      if (!this.fStringsInternalized) {
         prefixSymbol = prefix != null ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
         uriSymbol = uri != null && uri.length() > 0 ? this.fSymbolTable.addSymbol(uri) : null;
      } else {
         prefixSymbol = prefix != null ? prefix : XMLSymbols.EMPTY_STRING;
         uriSymbol = uri != null && uri.length() > 0 ? uri : null;
      }

      if (this.fNeedPushNSContext) {
         this.fNeedPushNSContext = false;
         this.fNamespaceContext.pushContext();
      }

      this.fNamespaceContext.declarePrefix(prefixSymbol, uriSymbol);
      if (this.fContentHandler != null) {
         this.fContentHandler.startPrefixMapping(prefix, uri);
      }

   }

   public void endPrefixMapping(String prefix) throws SAXException {
      if (this.fContentHandler != null) {
         this.fContentHandler.endPrefixMapping(prefix);
      }

   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if (this.fNeedPushNSContext) {
         this.fNamespaceContext.pushContext();
      }

      this.fNeedPushNSContext = true;
      this.fillQName(this.fElementQName, uri, localName, qName);
      if (atts instanceof Attributes2) {
         this.fillXMLAttributes2((Attributes2)atts);
      } else {
         this.fillXMLAttributes(atts);
      }

      try {
         this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, (Augmentations)null);
      } catch (XMLParseException var6) {
         throw Util.toSAXParseException(var6);
      } catch (XNIException var7) {
         throw Util.toSAXException(var7);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.fillQName(this.fElementQName, uri, localName, qName);

      try {
         this.fSchemaValidator.endElement(this.fElementQName, (Augmentations)null);
      } catch (XMLParseException var9) {
         throw Util.toSAXParseException(var9);
      } catch (XNIException var10) {
         throw Util.toSAXException(var10);
      } finally {
         this.fNamespaceContext.popContext();
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         this.fTempString.setValues(ch, start, length);
         this.fSchemaValidator.characters(this.fTempString, (Augmentations)null);
      } catch (XMLParseException var5) {
         throw Util.toSAXParseException(var5);
      } catch (XNIException var6) {
         throw Util.toSAXException(var6);
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      try {
         this.fTempString.setValues(ch, start, length);
         this.fSchemaValidator.ignorableWhitespace(this.fTempString, (Augmentations)null);
      } catch (XMLParseException var5) {
         throw Util.toSAXParseException(var5);
      } catch (XNIException var6) {
         throw Util.toSAXException(var6);
      }
   }

   public void processingInstruction(String target, String data) throws SAXException {
      if (this.fContentHandler != null) {
         this.fContentHandler.processingInstruction(target, data);
      }

   }

   public void skippedEntity(String name) throws SAXException {
      if (this.fContentHandler != null) {
         this.fContentHandler.skippedEntity(name);
      }

   }

   public void notationDecl(String name, String publicId, String systemId) throws SAXException {
   }

   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
      if (this.fUnparsedEntities == null) {
         this.fUnparsedEntities = new HashMap();
      }

      this.fUnparsedEntities.put(name, name);
   }

   public void validate(Source source, Result result) throws SAXException, IOException {
      if (!(result instanceof SAXResult) && result != null) {
         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
      } else {
         SAXSource saxSource = (SAXSource)source;
         SAXResult saxResult = (SAXResult)result;
         if (result != null) {
            this.setContentHandler(saxResult.getHandler());
         }

         try {
            XMLReader reader = saxSource.getXMLReader();
            if (reader == null) {
               reader = JdkXmlUtils.getXMLReader(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"), this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"));

               try {
                  if (reader instanceof SAXParser) {
                     XMLSecurityManager securityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
                     if (securityManager != null) {
                        try {
                           reader.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
                        } catch (SAXException var16) {
                        }
                     }

                     try {
                        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
                        reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD));
                     } catch (SAXException var15) {
                        XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", var15);
                     }
                  }
               } catch (Exception var17) {
                  throw new FactoryConfigurationError(var17);
               }
            }

            try {
               this.fStringsInternalized = reader.getFeature("http://xml.org/sax/features/string-interning");
            } catch (SAXException var14) {
               this.fStringsInternalized = false;
            }

            ErrorHandler errorHandler = this.fComponentManager.getErrorHandler();
            reader.setErrorHandler((ErrorHandler)(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance()));
            reader.setEntityResolver(this.fResolutionForwarder);
            this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            InputSource is = saxSource.getInputSource();
            reader.parse(is);
         } finally {
            this.setContentHandler((ContentHandler)null);
         }

      }
   }

   public ElementPSVI getElementPSVI() {
      return this.fTypeInfoProvider.getElementPSVI();
   }

   public AttributePSVI getAttributePSVI(int index) {
      return this.fTypeInfoProvider.getAttributePSVI(index);
   }

   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
      return this.fTypeInfoProvider.getAttributePSVIByName(uri, localname);
   }

   private void fillQName(QName toFill, String uri, String localpart, String raw) {
      if (!this.fStringsInternalized) {
         uri = uri != null && uri.length() > 0 ? this.fSymbolTable.addSymbol(uri) : null;
         localpart = localpart != null ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
         raw = raw != null ? this.fSymbolTable.addSymbol(raw) : XMLSymbols.EMPTY_STRING;
      } else {
         if (uri != null && uri.length() == 0) {
            uri = null;
         }

         if (localpart == null) {
            localpart = XMLSymbols.EMPTY_STRING;
         }

         if (raw == null) {
            raw = XMLSymbols.EMPTY_STRING;
         }
      }

      String prefix = XMLSymbols.EMPTY_STRING;
      int prefixIdx = raw.indexOf(58);
      if (prefixIdx != -1) {
         prefix = this.fSymbolTable.addSymbol(raw.substring(0, prefixIdx));
      }

      toFill.setValues(prefix, localpart, raw, uri);
   }

   private void fillXMLAttributes(Attributes att) {
      this.fAttributes.removeAllAttributes();
      int len = att.getLength();

      for(int i = 0; i < len; ++i) {
         this.fillXMLAttribute(att, i);
         this.fAttributes.setSpecified(i, true);
      }

   }

   private void fillXMLAttributes2(Attributes2 att) {
      this.fAttributes.removeAllAttributes();
      int len = att.getLength();

      for(int i = 0; i < len; ++i) {
         this.fillXMLAttribute(att, i);
         this.fAttributes.setSpecified(i, att.isSpecified(i));
         if (att.isDeclared(i)) {
            this.fAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
         }
      }

   }

   private void fillXMLAttribute(Attributes att, int index) {
      this.fillQName(this.fAttributeQName, att.getURI(index), att.getLocalName(index), att.getQName(index));
      String type = att.getType(index);
      this.fAttributes.addAttributeNS(this.fAttributeQName, type != null ? type : XMLSymbols.fCDATASymbol, att.getValue(index));
   }

   static final class ResolutionForwarder implements EntityResolver2 {
      private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
      protected LSResourceResolver fEntityResolver;

      public ResolutionForwarder() {
      }

      public ResolutionForwarder(LSResourceResolver entityResolver) {
         this.setEntityResolver(entityResolver);
      }

      public void setEntityResolver(LSResourceResolver entityResolver) {
         this.fEntityResolver = entityResolver;
      }

      public LSResourceResolver getEntityResolver() {
         return this.fEntityResolver;
      }

      public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
         return null;
      }

      public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
         if (this.fEntityResolver != null) {
            LSInput lsInput = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", (String)null, publicId, systemId, baseURI);
            if (lsInput != null) {
               String pubId = lsInput.getPublicId();
               String sysId = lsInput.getSystemId();
               String baseSystemId = lsInput.getBaseURI();
               Reader charStream = lsInput.getCharacterStream();
               InputStream byteStream = lsInput.getByteStream();
               String data = lsInput.getStringData();
               String encoding = lsInput.getEncoding();
               InputSource inputSource = new InputSource();
               inputSource.setPublicId(pubId);
               inputSource.setSystemId(baseSystemId != null ? this.resolveSystemId(systemId, baseSystemId) : systemId);
               if (charStream != null) {
                  inputSource.setCharacterStream(charStream);
               } else if (byteStream != null) {
                  inputSource.setByteStream(byteStream);
               } else if (data != null && data.length() != 0) {
                  inputSource.setCharacterStream(new StringReader(data));
               }

               inputSource.setEncoding(encoding);
               return inputSource;
            }
         }

         return null;
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         return this.resolveEntity((String)null, publicId, (String)null, systemId);
      }

      private String resolveSystemId(String systemId, String baseURI) {
         try {
            return XMLEntityManager.expandSystemId(systemId, baseURI, false);
         } catch (URI.MalformedURIException var4) {
            return systemId;
         }
      }
   }

   private class XMLSchemaTypeInfoProvider extends TypeInfoProvider {
      private Augmentations fElementAugs;
      private XMLAttributes fAttributes;
      private boolean fInStartElement;
      private boolean fInEndElement;

      private XMLSchemaTypeInfoProvider() {
         this.fInStartElement = false;
         this.fInEndElement = false;
      }

      void beginStartElement(Augmentations elementAugs, XMLAttributes attributes) {
         this.fInStartElement = true;
         this.fElementAugs = elementAugs;
         this.fAttributes = attributes;
      }

      void finishStartElement() {
         this.fInStartElement = false;
         this.fElementAugs = null;
         this.fAttributes = null;
      }

      void beginEndElement(Augmentations elementAugs) {
         this.fInEndElement = true;
         this.fElementAugs = elementAugs;
      }

      void finishEndElement() {
         this.fInEndElement = false;
         this.fElementAugs = null;
      }

      private void checkState(boolean forElementInfo) {
         if (!this.fInStartElement && (!this.fInEndElement || !forElementInfo)) {
            throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalState", (Object[])null));
         }
      }

      public TypeInfo getAttributeTypeInfo(int index) {
         this.checkState(false);
         return this.getAttributeType(index);
      }

      private TypeInfo getAttributeType(int index) {
         this.checkState(false);
         if (index >= 0 && this.fAttributes.getLength() > index) {
            Augmentations augs = this.fAttributes.getAugmentations(index);
            if (augs == null) {
               return null;
            } else {
               AttributePSVI psvi = (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
               return this.getTypeInfoFromPSVI(psvi);
            }
         } else {
            throw new IndexOutOfBoundsException(Integer.toString(index));
         }
      }

      public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
         this.checkState(false);
         return this.getAttributeTypeInfo(this.fAttributes.getIndex(attributeUri, attributeLocalName));
      }

      public TypeInfo getAttributeTypeInfo(String attributeQName) {
         this.checkState(false);
         return this.getAttributeTypeInfo(this.fAttributes.getIndex(attributeQName));
      }

      public TypeInfo getElementTypeInfo() {
         this.checkState(true);
         if (this.fElementAugs == null) {
            return null;
         } else {
            ElementPSVI psvi = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
            return this.getTypeInfoFromPSVI(psvi);
         }
      }

      private TypeInfo getTypeInfoFromPSVI(ItemPSVI psvi) {
         if (psvi == null) {
            return null;
         } else {
            if (psvi.getValidity() == 2) {
               XSTypeDefinition tx = psvi.getMemberTypeDefinition();
               if (tx != null) {
                  return tx instanceof TypeInfo ? (TypeInfo)tx : null;
               }
            }

            XSTypeDefinition t = psvi.getTypeDefinition();
            if (t != null) {
               return t instanceof TypeInfo ? (TypeInfo)t : null;
            } else {
               return null;
            }
         }
      }

      public boolean isIdAttribute(int index) {
         this.checkState(false);
         XSSimpleType type = (XSSimpleType)this.getAttributeType(index);
         return type == null ? false : type.isIDType();
      }

      public boolean isSpecified(int index) {
         this.checkState(false);
         return this.fAttributes.isSpecified(index);
      }

      ElementPSVI getElementPSVI() {
         return this.fElementAugs != null ? (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI") : null;
      }

      AttributePSVI getAttributePSVI(int index) {
         if (this.fAttributes != null) {
            Augmentations augs = this.fAttributes.getAugmentations(index);
            if (augs != null) {
               return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
            }
         }

         return null;
      }

      AttributePSVI getAttributePSVIByName(String uri, String localname) {
         if (this.fAttributes != null) {
            Augmentations augs = this.fAttributes.getAugmentations(uri, localname);
            if (augs != null) {
               return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
            }
         }

         return null;
      }

      // $FF: synthetic method
      XMLSchemaTypeInfoProvider(Object x1) {
         this();
      }
   }
}
