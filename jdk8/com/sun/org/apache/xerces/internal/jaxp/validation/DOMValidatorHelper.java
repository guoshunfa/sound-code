package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.IOException;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

final class DOMValidatorHelper implements ValidatorHelper, EntityState {
   private static final int CHUNK_SIZE = 1024;
   private static final int CHUNK_MASK = 1023;
   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   private XMLErrorReporter fErrorReporter;
   private NamespaceSupport fNamespaceContext;
   private DOMValidatorHelper.DOMNamespaceContext fDOMNamespaceContext = new DOMValidatorHelper.DOMNamespaceContext();
   private XMLSchemaValidator fSchemaValidator;
   private SymbolTable fSymbolTable;
   private ValidationManager fValidationManager;
   private XMLSchemaValidatorComponentManager fComponentManager;
   private final SimpleLocator fXMLLocator = new SimpleLocator((String)null, (String)null, -1, -1, -1);
   private DOMDocumentHandler fDOMValidatorHandler;
   private final DOMResultAugmentor fDOMResultAugmentor = new DOMResultAugmentor(this);
   private final DOMResultBuilder fDOMResultBuilder = new DOMResultBuilder();
   private NamedNodeMap fEntities = null;
   private char[] fCharBuffer = new char[1024];
   private Node fRoot;
   private Node fCurrentElement;
   final QName fElementQName = new QName();
   final QName fAttributeQName = new QName();
   final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
   final XMLString fTempString = new XMLString();

   public DOMValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
      this.fComponentManager = componentManager;
      this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fNamespaceContext = (NamespaceSupport)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
      this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
      this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
   }

   public void validate(Source source, Result result) throws SAXException, IOException {
      if (!(result instanceof DOMResult) && result != null) {
         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
      } else {
         DOMSource domSource = (DOMSource)source;
         DOMResult domResult = (DOMResult)result;
         Node node = domSource.getNode();
         this.fRoot = node;
         if (node != null) {
            this.fComponentManager.reset();
            this.fValidationManager.setEntityState(this);
            this.fDOMNamespaceContext.reset();
            String systemId = domSource.getSystemId();
            this.fXMLLocator.setLiteralSystemId(systemId);
            this.fXMLLocator.setExpandedSystemId(systemId);
            this.fErrorReporter.setDocumentLocator(this.fXMLLocator);

            try {
               this.setupEntityMap(node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument());
               this.setupDOMResultHandler(domSource, domResult);
               this.fSchemaValidator.startDocument(this.fXMLLocator, (String)null, this.fDOMNamespaceContext, (Augmentations)null);
               this.validate(node);
               this.fSchemaValidator.endDocument((Augmentations)null);
            } catch (XMLParseException var12) {
               throw Util.toSAXParseException(var12);
            } catch (XNIException var13) {
               throw Util.toSAXException(var13);
            } finally {
               this.fRoot = null;
               this.fEntities = null;
               if (this.fDOMValidatorHandler != null) {
                  this.fDOMValidatorHandler.setDOMResult((DOMResult)null);
               }

            }
         }

      }
   }

   public boolean isEntityDeclared(String name) {
      return false;
   }

   public boolean isEntityUnparsed(String name) {
      if (this.fEntities != null) {
         Entity entity = (Entity)this.fEntities.getNamedItem(name);
         if (entity != null) {
            return entity.getNotationName() != null;
         }
      }

      return false;
   }

   private void validate(Node node) {
      Node next;
      label39:
      for(Node top = node; node != null; node = next) {
         this.beginNode(node);
         next = node.getFirstChild();

         do {
            do {
               if (next != null) {
                  continue label39;
               }

               this.finishNode(node);
               if (top == node) {
                  continue label39;
               }

               next = node.getNextSibling();
            } while(next != null);

            node = node.getParentNode();
         } while(node != null && top != node);

         if (node != null) {
            this.finishNode(node);
         }

         next = null;
      }

   }

   private void beginNode(Node node) {
      switch(node.getNodeType()) {
      case 1:
         this.fCurrentElement = node;
         this.fNamespaceContext.pushContext();
         this.fillQName(this.fElementQName, node);
         this.processAttributes(node.getAttributes());
         this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, (Augmentations)null);
      case 2:
      case 5:
      case 6:
      case 9:
      default:
         break;
      case 3:
         if (this.fDOMValidatorHandler != null) {
            this.fDOMValidatorHandler.setIgnoringCharacters(true);
            this.sendCharactersToValidator(node.getNodeValue());
            this.fDOMValidatorHandler.setIgnoringCharacters(false);
            this.fDOMValidatorHandler.characters((Text)node);
         } else {
            this.sendCharactersToValidator(node.getNodeValue());
         }
         break;
      case 4:
         if (this.fDOMValidatorHandler != null) {
            this.fDOMValidatorHandler.setIgnoringCharacters(true);
            this.fSchemaValidator.startCDATA((Augmentations)null);
            this.sendCharactersToValidator(node.getNodeValue());
            this.fSchemaValidator.endCDATA((Augmentations)null);
            this.fDOMValidatorHandler.setIgnoringCharacters(false);
            this.fDOMValidatorHandler.cdata((CDATASection)node);
         } else {
            this.fSchemaValidator.startCDATA((Augmentations)null);
            this.sendCharactersToValidator(node.getNodeValue());
            this.fSchemaValidator.endCDATA((Augmentations)null);
         }
         break;
      case 7:
         if (this.fDOMValidatorHandler != null) {
            this.fDOMValidatorHandler.processingInstruction((ProcessingInstruction)node);
         }
         break;
      case 8:
         if (this.fDOMValidatorHandler != null) {
            this.fDOMValidatorHandler.comment((Comment)node);
         }
         break;
      case 10:
         if (this.fDOMValidatorHandler != null) {
            this.fDOMValidatorHandler.doctypeDecl((DocumentType)node);
         }
      }

   }

   private void finishNode(Node node) {
      if (node.getNodeType() == 1) {
         this.fCurrentElement = node;
         this.fillQName(this.fElementQName, node);
         this.fSchemaValidator.endElement(this.fElementQName, (Augmentations)null);
         this.fNamespaceContext.popContext();
      }

   }

   private void setupEntityMap(Document doc) {
      if (doc != null) {
         DocumentType docType = doc.getDoctype();
         if (docType != null) {
            this.fEntities = docType.getEntities();
            return;
         }
      }

      this.fEntities = null;
   }

   private void setupDOMResultHandler(DOMSource source, DOMResult result) throws SAXException {
      if (result == null) {
         this.fDOMValidatorHandler = null;
         this.fSchemaValidator.setDocumentHandler((XMLDocumentHandler)null);
      } else {
         Node nodeResult = result.getNode();
         if (source.getNode() == nodeResult) {
            this.fDOMValidatorHandler = this.fDOMResultAugmentor;
            this.fDOMResultAugmentor.setDOMResult(result);
            this.fSchemaValidator.setDocumentHandler(this.fDOMResultAugmentor);
         } else {
            if (result.getNode() == null) {
               try {
                  DocumentBuilderFactory factory = JdkXmlUtils.getDOMFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
                  DocumentBuilder builder = factory.newDocumentBuilder();
                  result.setNode(builder.newDocument());
               } catch (ParserConfigurationException var6) {
                  throw new SAXException(var6);
               }
            }

            this.fDOMValidatorHandler = this.fDOMResultBuilder;
            this.fDOMResultBuilder.setDOMResult(result);
            this.fSchemaValidator.setDocumentHandler(this.fDOMResultBuilder);
         }
      }
   }

   private void fillQName(QName toFill, Node node) {
      String prefix = node.getPrefix();
      String localName = node.getLocalName();
      String rawName = node.getNodeName();
      String namespace = node.getNamespaceURI();
      toFill.uri = namespace != null && namespace.length() > 0 ? this.fSymbolTable.addSymbol(namespace) : null;
      toFill.rawname = rawName != null ? this.fSymbolTable.addSymbol(rawName) : XMLSymbols.EMPTY_STRING;
      if (localName == null) {
         int k = rawName.indexOf(58);
         if (k > 0) {
            toFill.prefix = this.fSymbolTable.addSymbol(rawName.substring(0, k));
            toFill.localpart = this.fSymbolTable.addSymbol(rawName.substring(k + 1));
         } else {
            toFill.prefix = XMLSymbols.EMPTY_STRING;
            toFill.localpart = toFill.rawname;
         }
      } else {
         toFill.prefix = prefix != null ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
         toFill.localpart = localName != null ? this.fSymbolTable.addSymbol(localName) : XMLSymbols.EMPTY_STRING;
      }

   }

   private void processAttributes(NamedNodeMap attrMap) {
      int attrCount = attrMap.getLength();
      this.fAttributes.removeAllAttributes();

      for(int i = 0; i < attrCount; ++i) {
         Attr attr = (Attr)attrMap.item(i);
         String value = attr.getValue();
         if (value == null) {
            value = XMLSymbols.EMPTY_STRING;
         }

         this.fillQName(this.fAttributeQName, attr);
         this.fAttributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, value);
         this.fAttributes.setSpecified(i, attr.getSpecified());
         if (this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
            if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
               this.fNamespaceContext.declarePrefix(this.fAttributeQName.localpart, value.length() != 0 ? this.fSymbolTable.addSymbol(value) : null);
            } else {
               this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, value.length() != 0 ? this.fSymbolTable.addSymbol(value) : null);
            }
         }
      }

   }

   private void sendCharactersToValidator(String str) {
      if (str != null) {
         int length = str.length();
         int remainder = length & 1023;
         if (remainder > 0) {
            str.getChars(0, remainder, this.fCharBuffer, 0);
            this.fTempString.setValues(this.fCharBuffer, 0, remainder);
            this.fSchemaValidator.characters(this.fTempString, (Augmentations)null);
         }

         int i = remainder;

         while(i < length) {
            int var10001 = i;
            i += 1024;
            str.getChars(var10001, i, this.fCharBuffer, 0);
            this.fTempString.setValues(this.fCharBuffer, 0, 1024);
            this.fSchemaValidator.characters(this.fTempString, (Augmentations)null);
         }
      }

   }

   Node getCurrentElement() {
      return this.fCurrentElement;
   }

   final class DOMNamespaceContext implements NamespaceContext {
      protected String[] fNamespace = new String[32];
      protected int fNamespaceSize = 0;
      protected boolean fDOMContextBuilt = false;

      public void pushContext() {
         DOMValidatorHelper.this.fNamespaceContext.pushContext();
      }

      public void popContext() {
         DOMValidatorHelper.this.fNamespaceContext.popContext();
      }

      public boolean declarePrefix(String prefix, String uri) {
         return DOMValidatorHelper.this.fNamespaceContext.declarePrefix(prefix, uri);
      }

      public String getURI(String prefix) {
         String uri = DOMValidatorHelper.this.fNamespaceContext.getURI(prefix);
         if (uri == null) {
            if (!this.fDOMContextBuilt) {
               this.fillNamespaceContext();
               this.fDOMContextBuilt = true;
            }

            if (this.fNamespaceSize > 0 && !DOMValidatorHelper.this.fNamespaceContext.containsPrefix(prefix)) {
               uri = this.getURI0(prefix);
            }
         }

         return uri;
      }

      public String getPrefix(String uri) {
         return DOMValidatorHelper.this.fNamespaceContext.getPrefix(uri);
      }

      public int getDeclaredPrefixCount() {
         return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixCount();
      }

      public String getDeclaredPrefixAt(int index) {
         return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixAt(index);
      }

      public Enumeration getAllPrefixes() {
         return DOMValidatorHelper.this.fNamespaceContext.getAllPrefixes();
      }

      public void reset() {
         this.fDOMContextBuilt = false;
         this.fNamespaceSize = 0;
      }

      private void fillNamespaceContext() {
         if (DOMValidatorHelper.this.fRoot != null) {
            for(Node currentNode = DOMValidatorHelper.this.fRoot.getParentNode(); currentNode != null; currentNode = currentNode.getParentNode()) {
               if (1 == currentNode.getNodeType()) {
                  NamedNodeMap attributes = currentNode.getAttributes();
                  int attrCount = attributes.getLength();

                  for(int i = 0; i < attrCount; ++i) {
                     Attr attr = (Attr)attributes.item(i);
                     String value = attr.getValue();
                     if (value == null) {
                        value = XMLSymbols.EMPTY_STRING;
                     }

                     DOMValidatorHelper.this.fillQName(DOMValidatorHelper.this.fAttributeQName, attr);
                     if (DOMValidatorHelper.this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                        if (DOMValidatorHelper.this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                           this.declarePrefix0(DOMValidatorHelper.this.fAttributeQName.localpart, value.length() != 0 ? DOMValidatorHelper.this.fSymbolTable.addSymbol(value) : null);
                        } else {
                           this.declarePrefix0(XMLSymbols.EMPTY_STRING, value.length() != 0 ? DOMValidatorHelper.this.fSymbolTable.addSymbol(value) : null);
                        }
                     }
                  }
               }
            }
         }

      }

      private void declarePrefix0(String prefix, String uri) {
         if (this.fNamespaceSize == this.fNamespace.length) {
            String[] namespacearray = new String[this.fNamespaceSize * 2];
            System.arraycopy(this.fNamespace, 0, namespacearray, 0, this.fNamespaceSize);
            this.fNamespace = namespacearray;
         }

         this.fNamespace[this.fNamespaceSize++] = prefix;
         this.fNamespace[this.fNamespaceSize++] = uri;
      }

      private String getURI0(String prefix) {
         for(int i = 0; i < this.fNamespaceSize; i += 2) {
            if (this.fNamespace[i] == prefix) {
               return this.fNamespace[i + 1];
            }
         }

         return null;
      }
   }
}
