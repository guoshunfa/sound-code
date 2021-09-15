package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.Locale;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.LocatorImpl;

public abstract class AbstractSAXParser extends AbstractXMLDocumentParser implements PSVIProvider, Parser, XMLReader {
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
   protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
   protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
   private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/namespace-prefixes", "http://xml.org/sax/features/string-interning"};
   protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
   protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
   protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node"};
   protected boolean fNamespaces;
   protected boolean fNamespacePrefixes = false;
   protected boolean fLexicalHandlerParameterEntities = true;
   protected boolean fStandalone;
   protected boolean fResolveDTDURIs = true;
   protected boolean fUseEntityResolver2 = true;
   protected boolean fXMLNSURIs = false;
   protected ContentHandler fContentHandler;
   protected DocumentHandler fDocumentHandler;
   protected NamespaceContext fNamespaceContext;
   protected DTDHandler fDTDHandler;
   protected DeclHandler fDeclHandler;
   protected LexicalHandler fLexicalHandler;
   protected QName fQName = new QName();
   protected boolean fParseInProgress = false;
   protected String fVersion;
   private final AbstractSAXParser.AttributesProxy fAttributesProxy = new AbstractSAXParser.AttributesProxy();
   private Augmentations fAugmentations = null;
   private static final int BUFFER_SIZE = 20;
   private char[] fCharBuffer = new char[20];
   protected SymbolHash fDeclaredAttrs = null;

   protected AbstractSAXParser(XMLParserConfiguration config) {
      super(config);
      config.addRecognizedFeatures(RECOGNIZED_FEATURES);
      config.addRecognizedProperties(RECOGNIZED_PROPERTIES);

      try {
         config.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", false);
      } catch (XMLConfigurationException var3) {
      }

   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.fNamespaceContext = namespaceContext;

      try {
         if (this.fDocumentHandler != null) {
            if (locator != null) {
               this.fDocumentHandler.setDocumentLocator(new AbstractSAXParser.LocatorProxy(locator));
            }

            this.fDocumentHandler.startDocument();
         }

         if (this.fContentHandler != null) {
            if (locator != null) {
               this.fContentHandler.setDocumentLocator(new AbstractSAXParser.LocatorProxy(locator));
            }

            this.fContentHandler.startDocument();
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
      this.fVersion = version;
      this.fStandalone = "yes".equals(standalone);
   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
      this.fInDTD = true;

      try {
         if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startDTD(rootElement, publicId, systemId);
         }
      } catch (SAXException var6) {
         throw new XNIException(var6);
      }

      if (this.fDeclHandler != null) {
         this.fDeclaredAttrs = new SymbolHash();
      }

   }

   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      try {
         if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
            if (this.fContentHandler != null) {
               this.fContentHandler.skippedEntity(name);
            }
         } else if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startEntity(name);
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
      try {
         if ((augs == null || !Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null) {
            this.fLexicalHandler.endEntity(name);
         }

      } catch (SAXException var4) {
         throw new XNIException(var4);
      }
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      try {
         if (this.fDocumentHandler != null) {
            this.fAttributesProxy.setAttributes(attributes);
            this.fDocumentHandler.startElement(element.rawname, this.fAttributesProxy);
         }

         if (this.fContentHandler != null) {
            if (this.fNamespaces) {
               this.startNamespaceMapping();
               int len = attributes.getLength();
               int i;
               if (!this.fNamespacePrefixes) {
                  for(i = len - 1; i >= 0; --i) {
                     attributes.getName(i, this.fQName);
                     if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                        attributes.removeAttributeAt(i);
                     }
                  }
               } else if (!this.fXMLNSURIs) {
                  for(i = len - 1; i >= 0; --i) {
                     attributes.getName(i, this.fQName);
                     if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                        this.fQName.prefix = "";
                        this.fQName.uri = "";
                        this.fQName.localpart = "";
                        attributes.setName(i, this.fQName);
                     }
                  }
               }
            }

            this.fAugmentations = augs;
            String uri = element.uri != null ? element.uri : "";
            String localpart = this.fNamespaces ? element.localpart : "";
            this.fAttributesProxy.setAttributes(attributes);
            this.fContentHandler.startElement(uri, localpart, element.rawname, this.fAttributesProxy);
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (text.length != 0) {
         try {
            if (this.fDocumentHandler != null) {
               this.fDocumentHandler.characters(text.ch, text.offset, text.length);
            }

            if (this.fContentHandler != null) {
               this.fContentHandler.characters(text.ch, text.offset, text.length);
            }

         } catch (SAXException var4) {
            throw new XNIException(var4);
         }
      }
   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      try {
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
         }

         if (this.fContentHandler != null) {
            this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
         }

      } catch (SAXException var4) {
         throw new XNIException(var4);
      }
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      try {
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(element.rawname);
         }

         if (this.fContentHandler != null) {
            this.fAugmentations = augs;
            String uri = element.uri != null ? element.uri : "";
            String localpart = this.fNamespaces ? element.localpart : "";
            this.fContentHandler.endElement(uri, localpart, element.rawname);
            if (this.fNamespaces) {
               this.endNamespaceMapping();
            }
         }

      } catch (SAXException var5) {
         throw new XNIException(var5);
      }
   }

   public void startCDATA(Augmentations augs) throws XNIException {
      try {
         if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startCDATA();
         }

      } catch (SAXException var3) {
         throw new XNIException(var3);
      }
   }

   public void endCDATA(Augmentations augs) throws XNIException {
      try {
         if (this.fLexicalHandler != null) {
            this.fLexicalHandler.endCDATA();
         }

      } catch (SAXException var3) {
         throw new XNIException(var3);
      }
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      try {
         if (this.fLexicalHandler != null) {
            this.fLexicalHandler.comment(text.ch, 0, text.length);
         }

      } catch (SAXException var4) {
         throw new XNIException(var4);
      }
   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      try {
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data.toString());
         }

         if (this.fContentHandler != null) {
            this.fContentHandler.processingInstruction(target, data.toString());
         }

      } catch (SAXException var5) {
         throw new XNIException(var5);
      }
   }

   public void endDocument(Augmentations augs) throws XNIException {
      try {
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument();
         }

         if (this.fContentHandler != null) {
            this.fContentHandler.endDocument();
         }

      } catch (SAXException var3) {
         throw new XNIException(var3);
      }
   }

   public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
      this.startParameterEntity("[dtd]", (XMLResourceIdentifier)null, (String)null, augs);
   }

   public void endExternalSubset(Augmentations augs) throws XNIException {
      this.endParameterEntity("[dtd]", augs);
   }

   public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      try {
         if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
            if (this.fContentHandler != null) {
               this.fContentHandler.skippedEntity(name);
            }
         } else if (this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
            this.fLexicalHandler.startEntity(name);
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void endParameterEntity(String name, Augmentations augs) throws XNIException {
      try {
         if ((augs == null || !Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
            this.fLexicalHandler.endEntity(name);
         }

      } catch (SAXException var4) {
         throw new XNIException(var4);
      }
   }

   public void elementDecl(String name, String contentModel, Augmentations augs) throws XNIException {
      try {
         if (this.fDeclHandler != null) {
            this.fDeclHandler.elementDecl(name, contentModel);
         }

      } catch (SAXException var5) {
         throw new XNIException(var5);
      }
   }

   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {
      try {
         if (this.fDeclHandler != null) {
            String elemAttr = elementName + "<" + attributeName;
            if (this.fDeclaredAttrs.get(elemAttr) != null) {
               return;
            }

            this.fDeclaredAttrs.put(elemAttr, Boolean.TRUE);
            if (type.equals("NOTATION") || type.equals("ENUMERATION")) {
               StringBuffer str = new StringBuffer();
               if (type.equals("NOTATION")) {
                  str.append(type);
                  str.append(" (");
               } else {
                  str.append("(");
               }

               for(int i = 0; i < enumeration.length; ++i) {
                  str.append(enumeration[i]);
                  if (i < enumeration.length - 1) {
                     str.append('|');
                  }
               }

               str.append(')');
               type = str.toString();
            }

            String value = defaultValue == null ? null : defaultValue.toString();
            this.fDeclHandler.attributeDecl(elementName, attributeName, type, defaultType, value);
         }

      } catch (SAXException var12) {
         throw new XNIException(var12);
      }
   }

   public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augs) throws XNIException {
      try {
         if (this.fDeclHandler != null) {
            this.fDeclHandler.internalEntityDecl(name, text.toString());
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
      try {
         if (this.fDeclHandler != null) {
            String publicId = identifier.getPublicId();
            String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
            this.fDeclHandler.externalEntityDecl(name, publicId, systemId);
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augs) throws XNIException {
      try {
         if (this.fDTDHandler != null) {
            String publicId = identifier.getPublicId();
            String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
            this.fDTDHandler.unparsedEntityDecl(name, publicId, systemId, notation);
         }

      } catch (SAXException var7) {
         throw new XNIException(var7);
      }
   }

   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
      try {
         if (this.fDTDHandler != null) {
            String publicId = identifier.getPublicId();
            String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
            this.fDTDHandler.notationDecl(name, publicId, systemId);
         }

      } catch (SAXException var6) {
         throw new XNIException(var6);
      }
   }

   public void endDTD(Augmentations augs) throws XNIException {
      this.fInDTD = false;

      try {
         if (this.fLexicalHandler != null) {
            this.fLexicalHandler.endDTD();
         }
      } catch (SAXException var3) {
         throw new XNIException(var3);
      }

      if (this.fDeclaredAttrs != null) {
         this.fDeclaredAttrs.clear();
      }

   }

   public void parse(String systemId) throws SAXException, IOException {
      XMLInputSource source = new XMLInputSource((String)null, systemId, (String)null);

      Exception ex;
      try {
         this.parse((XMLInputSource)source);
      } catch (XMLParseException var6) {
         ex = var6.getException();
         if (ex == null) {
            LocatorImpl locatorImpl = new LocatorImpl() {
               public String getXMLVersion() {
                  return AbstractSAXParser.this.fVersion;
               }

               public String getEncoding() {
                  return null;
               }
            };
            locatorImpl.setPublicId(var6.getPublicId());
            locatorImpl.setSystemId(var6.getExpandedSystemId());
            locatorImpl.setLineNumber(var6.getLineNumber());
            locatorImpl.setColumnNumber(var6.getColumnNumber());
            throw new SAXParseException(var6.getMessage(), locatorImpl);
         } else if (ex instanceof SAXException) {
            throw (SAXException)ex;
         } else if (ex instanceof IOException) {
            throw (IOException)ex;
         } else {
            throw new SAXException(ex);
         }
      } catch (XNIException var7) {
         ex = var7.getException();
         if (ex == null) {
            throw new SAXException(var7.getMessage());
         } else if (ex instanceof SAXException) {
            throw (SAXException)ex;
         } else if (ex instanceof IOException) {
            throw (IOException)ex;
         } else {
            throw new SAXException(ex);
         }
      }
   }

   public void parse(InputSource inputSource) throws SAXException, IOException {
      Exception ex;
      try {
         XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), (String)null);
         xmlInputSource.setByteStream(inputSource.getByteStream());
         xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
         xmlInputSource.setEncoding(inputSource.getEncoding());
         this.parse((XMLInputSource)xmlInputSource);
      } catch (XMLParseException var5) {
         ex = var5.getException();
         if (ex == null) {
            LocatorImpl locatorImpl = new LocatorImpl() {
               public String getXMLVersion() {
                  return AbstractSAXParser.this.fVersion;
               }

               public String getEncoding() {
                  return null;
               }
            };
            locatorImpl.setPublicId(var5.getPublicId());
            locatorImpl.setSystemId(var5.getExpandedSystemId());
            locatorImpl.setLineNumber(var5.getLineNumber());
            locatorImpl.setColumnNumber(var5.getColumnNumber());
            throw new SAXParseException(var5.getMessage(), locatorImpl);
         } else if (ex instanceof SAXException) {
            throw (SAXException)ex;
         } else if (ex instanceof IOException) {
            throw (IOException)ex;
         } else {
            throw new SAXException(ex);
         }
      } catch (XNIException var6) {
         ex = var6.getException();
         if (ex == null) {
            throw new SAXException(var6.getMessage());
         } else if (ex instanceof SAXException) {
            throw (SAXException)ex;
         } else if (ex instanceof IOException) {
            throw (IOException)ex;
         } else {
            throw new SAXException(ex);
         }
      }
   }

   public void setEntityResolver(EntityResolver resolver) {
      try {
         XMLEntityResolver xer = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
         if (this.fUseEntityResolver2 && resolver instanceof EntityResolver2) {
            if (xer instanceof EntityResolver2Wrapper) {
               EntityResolver2Wrapper er2w = (EntityResolver2Wrapper)xer;
               er2w.setEntityResolver((EntityResolver2)resolver);
            } else {
               this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)resolver));
            }
         } else if (xer instanceof EntityResolverWrapper) {
            EntityResolverWrapper erw = (EntityResolverWrapper)xer;
            erw.setEntityResolver(resolver);
         } else {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(resolver));
         }
      } catch (XMLConfigurationException var4) {
      }

   }

   public EntityResolver getEntityResolver() {
      Object entityResolver = null;

      try {
         XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
         if (xmlEntityResolver != null) {
            if (xmlEntityResolver instanceof EntityResolverWrapper) {
               entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
            } else if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
               entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
            }
         }
      } catch (XMLConfigurationException var3) {
      }

      return (EntityResolver)entityResolver;
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      try {
         XMLErrorHandler xeh = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
         if (xeh instanceof ErrorHandlerWrapper) {
            ErrorHandlerWrapper ehw = (ErrorHandlerWrapper)xeh;
            ehw.setErrorHandler(errorHandler);
         } else {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
         }
      } catch (XMLConfigurationException var4) {
      }

   }

   public ErrorHandler getErrorHandler() {
      ErrorHandler errorHandler = null;

      try {
         XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
         if (xmlErrorHandler != null && xmlErrorHandler instanceof ErrorHandlerWrapper) {
            errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
         }
      } catch (XMLConfigurationException var3) {
      }

      return errorHandler;
   }

   public void setLocale(Locale locale) throws SAXException {
      this.fConfiguration.setLocale(locale);
   }

   public void setDTDHandler(DTDHandler dtdHandler) {
      this.fDTDHandler = dtdHandler;
   }

   public void setDocumentHandler(DocumentHandler documentHandler) {
      this.fDocumentHandler = documentHandler;
   }

   public void setContentHandler(ContentHandler contentHandler) {
      this.fContentHandler = contentHandler;
   }

   public ContentHandler getContentHandler() {
      return this.fContentHandler;
   }

   public DTDHandler getDTDHandler() {
      return this.fDTDHandler;
   }

   public void setFeature(String featureId, boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
         if (!featureId.startsWith("http://xml.org/sax/features/")) {
            if (featureId.equals("http://javax.xml.XMLConstants/feature/secure-processing") && state && this.fConfiguration.getProperty("http://apache.org/xml/properties/security-manager") == null) {
               this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
            }
         } else {
            int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
            if (suffixLength == "namespaces".length() && featureId.endsWith("namespaces")) {
               this.fConfiguration.setFeature(featureId, state);
               this.fNamespaces = state;
               return;
            }

            if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
               this.fConfiguration.setFeature(featureId, state);
               this.fNamespacePrefixes = state;
               return;
            }

            if (suffixLength == "string-interning".length() && featureId.endsWith("string-interning")) {
               if (!state) {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[]{featureId}));
               }

               return;
            }

            if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
               this.fLexicalHandlerParameterEntities = state;
               return;
            }

            if (suffixLength == "resolve-dtd-uris".length() && featureId.endsWith("resolve-dtd-uris")) {
               this.fResolveDTDURIs = state;
               return;
            }

            if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
               if (state) {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[]{featureId}));
               }

               return;
            }

            if (suffixLength == "xmlns-uris".length() && featureId.endsWith("xmlns-uris")) {
               this.fXMLNSURIs = state;
               return;
            }

            if (suffixLength == "use-entity-resolver2".length() && featureId.endsWith("use-entity-resolver2")) {
               if (state != this.fUseEntityResolver2) {
                  this.fUseEntityResolver2 = state;
                  this.setEntityResolver(this.getEntityResolver());
               }

               return;
            }

            if (suffixLength == "is-standalone".length() && featureId.endsWith("is-standalone") || suffixLength == "use-attributes2".length() && featureId.endsWith("use-attributes2") || suffixLength == "use-locator2".length() && featureId.endsWith("use-locator2") || suffixLength == "xml-1.1".length() && featureId.endsWith("xml-1.1")) {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[]{featureId}));
            }
         }

         this.fConfiguration.setFeature(featureId, state);
      } catch (XMLConfigurationException var5) {
         String identifier = var5.getIdentifier();
         if (var5.getType() == Status.NOT_RECOGNIZED) {
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{identifier}));
         } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{identifier}));
         }
      }
   }

   public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
         if (featureId.startsWith("http://xml.org/sax/features/")) {
            int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
            if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
               boolean state = this.fConfiguration.getFeature(featureId);
               return state;
            }

            if (suffixLength == "string-interning".length() && featureId.endsWith("string-interning")) {
               return true;
            }

            if (suffixLength == "is-standalone".length() && featureId.endsWith("is-standalone")) {
               return this.fStandalone;
            }

            if (suffixLength == "xml-1.1".length() && featureId.endsWith("xml-1.1")) {
               return this.fConfiguration instanceof XML11Configurable;
            }

            if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
               return this.fLexicalHandlerParameterEntities;
            }

            if (suffixLength == "resolve-dtd-uris".length() && featureId.endsWith("resolve-dtd-uris")) {
               return this.fResolveDTDURIs;
            }

            if (suffixLength == "xmlns-uris".length() && featureId.endsWith("xmlns-uris")) {
               return this.fXMLNSURIs;
            }

            if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
               return false;
            }

            if (suffixLength == "use-entity-resolver2".length() && featureId.endsWith("use-entity-resolver2")) {
               return this.fUseEntityResolver2;
            }

            if (suffixLength == "use-attributes2".length() && featureId.endsWith("use-attributes2") || suffixLength == "use-locator2".length() && featureId.endsWith("use-locator2")) {
               return true;
            }
         }

         return this.fConfiguration.getFeature(featureId);
      } catch (XMLConfigurationException var4) {
         String identifier = var4.getIdentifier();
         if (var4.getType() == Status.NOT_RECOGNIZED) {
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{identifier}));
         } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{identifier}));
         }
      }
   }

   public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
         if (propertyId.startsWith("http://xml.org/sax/properties/")) {
            int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
            if (suffixLength == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
               try {
                  this.setLexicalHandler((LexicalHandler)value);
                  return;
               } catch (ClassCastException var5) {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[]{propertyId, "org.xml.sax.ext.LexicalHandler"}));
               }
            }

            if (suffixLength == "declaration-handler".length() && propertyId.endsWith("declaration-handler")) {
               try {
                  this.setDeclHandler((DeclHandler)value);
                  return;
               } catch (ClassCastException var6) {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[]{propertyId, "org.xml.sax.ext.DeclHandler"}));
               }
            }

            if (suffixLength == "dom-node".length() && propertyId.endsWith("dom-node") || suffixLength == "document-xml-version".length() && propertyId.endsWith("document-xml-version")) {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[]{propertyId}));
            }
         }

         this.fConfiguration.setProperty(propertyId, value);
      } catch (XMLConfigurationException var7) {
         String identifier = var7.getIdentifier();
         if (var7.getType() == Status.NOT_RECOGNIZED) {
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{identifier}));
         } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{identifier}));
         }
      }
   }

   public Object getProperty(String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException {
      try {
         if (propertyId.startsWith("http://xml.org/sax/properties/")) {
            int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
            if (suffixLength == "document-xml-version".length() && propertyId.endsWith("document-xml-version")) {
               return this.fVersion;
            }

            if (suffixLength == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
               return this.getLexicalHandler();
            }

            if (suffixLength == "declaration-handler".length() && propertyId.endsWith("declaration-handler")) {
               return this.getDeclHandler();
            }

            if (suffixLength == "dom-node".length() && propertyId.endsWith("dom-node")) {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "dom-node-read-not-supported", (Object[])null));
            }
         }

         return this.fConfiguration.getProperty(propertyId);
      } catch (XMLConfigurationException var4) {
         String identifier = var4.getIdentifier();
         if (var4.getType() == Status.NOT_RECOGNIZED) {
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{identifier}));
         } else {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{identifier}));
         }
      }
   }

   protected void setDeclHandler(DeclHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (this.fParseInProgress) {
         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[]{"http://xml.org/sax/properties/declaration-handler"}));
      } else {
         this.fDeclHandler = handler;
      }
   }

   protected DeclHandler getDeclHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
      return this.fDeclHandler;
   }

   protected void setLexicalHandler(LexicalHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (this.fParseInProgress) {
         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[]{"http://xml.org/sax/properties/lexical-handler"}));
      } else {
         this.fLexicalHandler = handler;
      }
   }

   protected LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
      return this.fLexicalHandler;
   }

   protected final void startNamespaceMapping() throws SAXException {
      int count = this.fNamespaceContext.getDeclaredPrefixCount();
      if (count > 0) {
         String prefix = null;
         String uri = null;

         for(int i = 0; i < count; ++i) {
            prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
            uri = this.fNamespaceContext.getURI(prefix);
            this.fContentHandler.startPrefixMapping(prefix, uri == null ? "" : uri);
         }
      }

   }

   protected final void endNamespaceMapping() throws SAXException {
      int count = this.fNamespaceContext.getDeclaredPrefixCount();
      if (count > 0) {
         for(int i = 0; i < count; ++i) {
            this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
         }
      }

   }

   public void reset() throws XNIException {
      super.reset();
      this.fInDTD = false;
      this.fVersion = "1.0";
      this.fStandalone = false;
      this.fNamespaces = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
      this.fNamespacePrefixes = this.fConfiguration.getFeature("http://xml.org/sax/features/namespace-prefixes");
      this.fAugmentations = null;
      this.fDeclaredAttrs = null;
   }

   public ElementPSVI getElementPSVI() {
      return this.fAugmentations != null ? (ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI") : null;
   }

   public AttributePSVI getAttributePSVI(int index) {
      return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_PSVI");
   }

   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
      return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(uri, localname).getItem("ATTRIBUTE_PSVI");
   }

   protected static final class AttributesProxy implements AttributeList, Attributes2 {
      protected XMLAttributes fAttributes;

      public void setAttributes(XMLAttributes attributes) {
         this.fAttributes = attributes;
      }

      public int getLength() {
         return this.fAttributes.getLength();
      }

      public String getName(int i) {
         return this.fAttributes.getQName(i);
      }

      public String getQName(int index) {
         return this.fAttributes.getQName(index);
      }

      public String getURI(int index) {
         String uri = this.fAttributes.getURI(index);
         return uri != null ? uri : "";
      }

      public String getLocalName(int index) {
         return this.fAttributes.getLocalName(index);
      }

      public String getType(int i) {
         return this.fAttributes.getType(i);
      }

      public String getType(String name) {
         return this.fAttributes.getType(name);
      }

      public String getType(String uri, String localName) {
         return uri.equals("") ? this.fAttributes.getType((String)null, localName) : this.fAttributes.getType(uri, localName);
      }

      public String getValue(int i) {
         return this.fAttributes.getValue(i);
      }

      public String getValue(String name) {
         return this.fAttributes.getValue(name);
      }

      public String getValue(String uri, String localName) {
         return uri.equals("") ? this.fAttributes.getValue((String)null, localName) : this.fAttributes.getValue(uri, localName);
      }

      public int getIndex(String qName) {
         return this.fAttributes.getIndex(qName);
      }

      public int getIndex(String uri, String localPart) {
         return uri.equals("") ? this.fAttributes.getIndex((String)null, localPart) : this.fAttributes.getIndex(uri, localPart);
      }

      public boolean isDeclared(int index) {
         if (index >= 0 && index < this.fAttributes.getLength()) {
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
         } else {
            throw new ArrayIndexOutOfBoundsException(index);
         }
      }

      public boolean isDeclared(String qName) {
         int index = this.getIndex(qName);
         if (index == -1) {
            throw new IllegalArgumentException(qName);
         } else {
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
         }
      }

      public boolean isDeclared(String uri, String localName) {
         int index = this.getIndex(uri, localName);
         if (index == -1) {
            throw new IllegalArgumentException(localName);
         } else {
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
         }
      }

      public boolean isSpecified(int index) {
         if (index >= 0 && index < this.fAttributes.getLength()) {
            return this.fAttributes.isSpecified(index);
         } else {
            throw new ArrayIndexOutOfBoundsException(index);
         }
      }

      public boolean isSpecified(String qName) {
         int index = this.getIndex(qName);
         if (index == -1) {
            throw new IllegalArgumentException(qName);
         } else {
            return this.fAttributes.isSpecified(index);
         }
      }

      public boolean isSpecified(String uri, String localName) {
         int index = this.getIndex(uri, localName);
         if (index == -1) {
            throw new IllegalArgumentException(localName);
         } else {
            return this.fAttributes.isSpecified(index);
         }
      }
   }

   protected class LocatorProxy implements Locator2 {
      protected XMLLocator fLocator;

      public LocatorProxy(XMLLocator locator) {
         this.fLocator = locator;
      }

      public String getPublicId() {
         return this.fLocator.getPublicId();
      }

      public String getSystemId() {
         return this.fLocator.getExpandedSystemId();
      }

      public int getLineNumber() {
         return this.fLocator.getLineNumber();
      }

      public int getColumnNumber() {
         return this.fLocator.getColumnNumber();
      }

      public String getXMLVersion() {
         return this.fLocator.getXMLVersion();
      }

      public String getEncoding() {
         return this.fLocator.getEncoding();
      }
   }
}
