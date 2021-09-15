package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultXMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.LocatorProxy;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

final class JAXPValidatorComponent extends TeeXMLDocumentFilterImpl implements XMLComponent {
   private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   private final ValidatorHandler validator;
   private final JAXPValidatorComponent.XNI2SAX xni2sax = new JAXPValidatorComponent.XNI2SAX();
   private final JAXPValidatorComponent.SAX2XNI sax2xni = new JAXPValidatorComponent.SAX2XNI();
   private final TypeInfoProvider typeInfoProvider;
   private Augmentations fCurrentAug;
   private XMLAttributes fCurrentAttributes;
   private SymbolTable fSymbolTable;
   private XMLErrorReporter fErrorReporter;
   private XMLEntityResolver fEntityResolver;
   private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider() {
      public TypeInfo getElementTypeInfo() {
         return null;
      }

      public TypeInfo getAttributeTypeInfo(int index) {
         return null;
      }

      public TypeInfo getAttributeTypeInfo(String attributeQName) {
         return null;
      }

      public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
         return null;
      }

      public boolean isIdAttribute(int index) {
         return false;
      }

      public boolean isSpecified(int index) {
         return false;
      }
   };

   public JAXPValidatorComponent(ValidatorHandler validatorHandler) {
      this.validator = validatorHandler;
      TypeInfoProvider tip = validatorHandler.getTypeInfoProvider();
      if (tip == null) {
         tip = noInfoProvider;
      }

      this.typeInfoProvider = tip;
      this.xni2sax.setContentHandler(this.validator);
      this.validator.setContentHandler(this.sax2xni);
      this.setSide(this.xni2sax);
      this.validator.setErrorHandler(new ErrorHandlerProxy() {
         protected XMLErrorHandler getErrorHandler() {
            XMLErrorHandler handler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
            return (XMLErrorHandler)(handler != null ? handler : new ErrorHandlerWrapper(JAXPValidatorComponent.DraconianErrorHandler.getInstance()));
         }
      });
      this.validator.setResourceResolver(new LSResourceResolver() {
         public LSInput resolveResource(String type, String ns, String publicId, String systemId, String baseUri) {
            if (JAXPValidatorComponent.this.fEntityResolver == null) {
               return null;
            } else {
               try {
                  XMLInputSource is = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(publicId, systemId, baseUri, (String)null));
                  if (is == null) {
                     return null;
                  } else {
                     LSInput di = new DOMInputImpl();
                     di.setBaseURI(is.getBaseSystemId());
                     di.setByteStream(is.getByteStream());
                     di.setCharacterStream(is.getCharacterStream());
                     di.setEncoding(is.getEncoding());
                     di.setPublicId(is.getPublicId());
                     di.setSystemId(is.getSystemId());
                     return di;
                  }
               } catch (IOException var8) {
                  throw new XNIException(var8);
               }
            }
         }
      });
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      this.fCurrentAttributes = attributes;
      this.fCurrentAug = augs;
      this.xni2sax.startElement(element, attributes, (Augmentations)null);
      this.fCurrentAttributes = null;
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      this.fCurrentAug = augs;
      this.xni2sax.endElement(element, (Augmentations)null);
   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      this.startElement(element, attributes, augs);
      this.endElement(element, augs);
   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      this.fCurrentAug = augs;
      this.xni2sax.characters(text, (Augmentations)null);
   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      this.fCurrentAug = augs;
      this.xni2sax.ignorableWhitespace(text, (Augmentations)null);
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");

      try {
         this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
      } catch (XMLConfigurationException var3) {
         this.fEntityResolver = null;
      }

   }

   private void updateAttributes(Attributes atts) {
      int len = atts.getLength();

      for(int i = 0; i < len; ++i) {
         String aqn = atts.getQName(i);
         int j = this.fCurrentAttributes.getIndex(aqn);
         String av = atts.getValue(i);
         if (j == -1) {
            int idx = aqn.indexOf(58);
            String prefix;
            if (idx < 0) {
               prefix = null;
            } else {
               prefix = this.symbolize(aqn.substring(0, idx));
            }

            j = this.fCurrentAttributes.addAttribute(new QName(prefix, this.symbolize(atts.getLocalName(i)), this.symbolize(aqn), this.symbolize(atts.getURI(i))), atts.getType(i), av);
         } else if (!av.equals(this.fCurrentAttributes.getValue(j))) {
            this.fCurrentAttributes.setValue(j, av);
         }
      }

   }

   private String symbolize(String s) {
      return this.fSymbolTable.addSymbol(s);
   }

   public String[] getRecognizedFeatures() {
      return null;
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
   }

   public String[] getRecognizedProperties() {
      return new String[]{"http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/symbol-table"};
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
   }

   public Boolean getFeatureDefault(String featureId) {
      return null;
   }

   public Object getPropertyDefault(String propertyId) {
      return null;
   }

   private static final class DraconianErrorHandler implements ErrorHandler {
      private static final JAXPValidatorComponent.DraconianErrorHandler ERROR_HANDLER_INSTANCE = new JAXPValidatorComponent.DraconianErrorHandler();

      public static JAXPValidatorComponent.DraconianErrorHandler getInstance() {
         return ERROR_HANDLER_INSTANCE;
      }

      public void warning(SAXParseException e) throws SAXException {
      }

      public void error(SAXParseException e) throws SAXException {
         throw e;
      }

      public void fatalError(SAXParseException e) throws SAXException {
         throw e;
      }
   }

   private final class XNI2SAX extends DefaultXMLDocumentHandler {
      private ContentHandler fContentHandler;
      private String fVersion;
      protected NamespaceContext fNamespaceContext;
      private final AttributesProxy fAttributesProxy;

      private XNI2SAX() {
         this.fAttributesProxy = new AttributesProxy((XMLAttributes)null);
      }

      public void setContentHandler(ContentHandler handler) {
         this.fContentHandler = handler;
      }

      public ContentHandler getContentHandler() {
         return this.fContentHandler;
      }

      public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
         this.fVersion = version;
      }

      public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
         this.fNamespaceContext = namespaceContext;
         this.fContentHandler.setDocumentLocator(new LocatorProxy(locator));

         try {
            this.fContentHandler.startDocument();
         } catch (SAXException var6) {
            throw new XNIException(var6);
         }
      }

      public void endDocument(Augmentations augs) throws XNIException {
         try {
            this.fContentHandler.endDocument();
         } catch (SAXException var3) {
            throw new XNIException(var3);
         }
      }

      public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
         try {
            this.fContentHandler.processingInstruction(target, data.toString());
         } catch (SAXException var5) {
            throw new XNIException(var5);
         }
      }

      public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
         try {
            int count = this.fNamespaceContext.getDeclaredPrefixCount();
            String prefix;
            String uri;
            if (count > 0) {
               prefix = null;
               uri = null;

               for(int i = 0; i < count; ++i) {
                  prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                  uri = this.fNamespaceContext.getURI(prefix);
                  this.fContentHandler.startPrefixMapping(prefix, uri == null ? "" : uri);
               }
            }

            prefix = element.uri != null ? element.uri : "";
            uri = element.localpart;
            this.fAttributesProxy.setAttributes(attributes);
            this.fContentHandler.startElement(prefix, uri, element.rawname, this.fAttributesProxy);
         } catch (SAXException var8) {
            throw new XNIException(var8);
         }
      }

      public void endElement(QName element, Augmentations augs) throws XNIException {
         try {
            String uri = element.uri != null ? element.uri : "";
            String localpart = element.localpart;
            this.fContentHandler.endElement(uri, localpart, element.rawname);
            int count = this.fNamespaceContext.getDeclaredPrefixCount();
            if (count > 0) {
               for(int i = 0; i < count; ++i) {
                  this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
               }
            }

         } catch (SAXException var7) {
            throw new XNIException(var7);
         }
      }

      public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
         this.startElement(element, attributes, augs);
         this.endElement(element, augs);
      }

      public void characters(XMLString text, Augmentations augs) throws XNIException {
         try {
            this.fContentHandler.characters(text.ch, text.offset, text.length);
         } catch (SAXException var4) {
            throw new XNIException(var4);
         }
      }

      public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
         try {
            this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
         } catch (SAXException var4) {
            throw new XNIException(var4);
         }
      }

      // $FF: synthetic method
      XNI2SAX(Object x1) {
         this();
      }
   }

   private final class SAX2XNI extends DefaultHandler {
      private final Augmentations fAugmentations;
      private final QName fQName;

      private SAX2XNI() {
         this.fAugmentations = new AugmentationsImpl();
         this.fQName = new QName();
      }

      public void characters(char[] ch, int start, int len) throws SAXException {
         try {
            this.handler().characters(new XMLString(ch, start, len), this.aug());
         } catch (XNIException var5) {
            throw this.toSAXException(var5);
         }
      }

      public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException {
         try {
            this.handler().ignorableWhitespace(new XMLString(ch, start, len), this.aug());
         } catch (XNIException var5) {
            throw this.toSAXException(var5);
         }
      }

      public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
         try {
            JAXPValidatorComponent.this.updateAttributes(atts);
            this.handler().startElement(this.toQName(uri, localName, qname), JAXPValidatorComponent.this.fCurrentAttributes, this.elementAug());
         } catch (XNIException var6) {
            throw this.toSAXException(var6);
         }
      }

      public void endElement(String uri, String localName, String qname) throws SAXException {
         try {
            this.handler().endElement(this.toQName(uri, localName, qname), this.aug());
         } catch (XNIException var5) {
            throw this.toSAXException(var5);
         }
      }

      private Augmentations elementAug() {
         Augmentations aug = this.aug();
         return aug;
      }

      private Augmentations aug() {
         if (JAXPValidatorComponent.this.fCurrentAug != null) {
            Augmentations r = JAXPValidatorComponent.this.fCurrentAug;
            JAXPValidatorComponent.this.fCurrentAug = null;
            return r;
         } else {
            this.fAugmentations.removeAllItems();
            return this.fAugmentations;
         }
      }

      private XMLDocumentHandler handler() {
         return JAXPValidatorComponent.this.getDocumentHandler();
      }

      private SAXException toSAXException(XNIException xe) {
         Exception e = xe.getException();
         if (e == null) {
            e = xe;
         }

         return e instanceof SAXException ? (SAXException)e : new SAXException((Exception)e);
      }

      private QName toQName(String uri, String localName, String qname) {
         String prefix = null;
         int idx = qname.indexOf(58);
         if (idx > 0) {
            prefix = JAXPValidatorComponent.this.symbolize(qname.substring(0, idx));
         }

         localName = JAXPValidatorComponent.this.symbolize(localName);
         qname = JAXPValidatorComponent.this.symbolize(qname);
         uri = JAXPValidatorComponent.this.symbolize(uri);
         this.fQName.setValues(prefix, localName, qname, uri);
         return this.fQName;
      }

      // $FF: synthetic method
      SAX2XNI(Object x1) {
         this();
      }
   }
}
