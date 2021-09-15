package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.parsers.XMLGrammarPreparser;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMNormalizer implements XMLDocumentHandler {
   protected static final boolean DEBUG_ND = false;
   protected static final boolean DEBUG = false;
   protected static final boolean DEBUG_EVENTS = false;
   protected static final String PREFIX = "NS";
   protected DOMConfigurationImpl fConfiguration = null;
   protected CoreDocumentImpl fDocument = null;
   protected final DOMNormalizer.XMLAttributesProxy fAttrProxy = new DOMNormalizer.XMLAttributesProxy();
   protected final QName fQName = new QName();
   protected RevalidationHandler fValidationHandler;
   protected SymbolTable fSymbolTable;
   protected DOMErrorHandler fErrorHandler;
   private final DOMErrorImpl fError = new DOMErrorImpl();
   protected boolean fNamespaceValidation = false;
   protected boolean fPSVI = false;
   protected final NamespaceContext fNamespaceContext = new NamespaceSupport();
   protected final NamespaceContext fLocalNSBinder = new NamespaceSupport();
   protected final ArrayList fAttributeList = new ArrayList(5);
   protected final DOMLocatorImpl fLocator = new DOMLocatorImpl();
   protected Node fCurrentNode = null;
   private QName fAttrQName = new QName();
   final XMLString fNormalizedValue = new XMLString(new char[16], 0, 0);
   private XMLDTDValidator fDTDValidator;
   private boolean allWhitespace = false;

   protected void normalizeDocument(CoreDocumentImpl document, DOMConfigurationImpl config) {
      this.fDocument = document;
      this.fConfiguration = config;
      this.fSymbolTable = (SymbolTable)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fNamespaceContext.reset();
      this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
      if ((this.fConfiguration.features & 64) != 0) {
         String schemaLang = (String)this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
         if (schemaLang != null && schemaLang.equals(Constants.NS_XMLSCHEMA)) {
            this.fValidationHandler = CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/2001/XMLSchema");
            this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
            this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            this.fNamespaceValidation = true;
            this.fPSVI = (this.fConfiguration.features & 128) != 0;
         }

         this.fConfiguration.setFeature("http://xml.org/sax/features/validation", true);
         this.fDocument.clearIdentifiers();
         if (this.fValidationHandler != null) {
            ((XMLComponent)this.fValidationHandler).reset(this.fConfiguration);
         }
      }

      this.fErrorHandler = (DOMErrorHandler)this.fConfiguration.getParameter("error-handler");
      if (this.fValidationHandler != null) {
         this.fValidationHandler.setDocumentHandler(this);
         this.fValidationHandler.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, (Augmentations)null);
      }

      try {
         Node next;
         for(Node kid = this.fDocument.getFirstChild(); kid != null; kid = next) {
            next = kid.getNextSibling();
            kid = this.normalizeNode(kid);
            if (kid != null) {
               next = kid;
            }
         }

         if (this.fValidationHandler != null) {
            this.fValidationHandler.endDocument((Augmentations)null);
            CoreDOMImplementationImpl.singleton.releaseValidator("http://www.w3.org/2001/XMLSchema", this.fValidationHandler);
            this.fValidationHandler = null;
         }

      } catch (AbortException var5) {
      } catch (RuntimeException var6) {
         throw var6;
      }
   }

   protected Node normalizeNode(Node node) {
      int type = ((Node)node).getNodeType();
      this.fLocator.fRelatedNode = (Node)node;
      boolean wellformed;
      Node parent;
      Node kid;
      Node prevSibling;
      String value;
      switch(type) {
      case 1:
         if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0 && this.fDocument.isXMLVersionChanged()) {
            if (this.fNamespaceValidation) {
               wellformed = CoreDocumentImpl.isValidQName(((Node)node).getPrefix(), ((Node)node).getLocalName(), this.fDocument.isXML11Version());
            } else {
               wellformed = CoreDocumentImpl.isXMLName(((Node)node).getNodeName(), this.fDocument.isXML11Version());
            }

            if (!wellformed) {
               value = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Element", ((Node)node).getNodeName()});
               reportDOMError(this.fErrorHandler, this.fError, this.fLocator, value, (short)2, "wf-invalid-character-in-node-name");
            }
         }

         this.fNamespaceContext.pushContext();
         this.fLocalNSBinder.reset();
         ElementImpl elem = (ElementImpl)node;
         if (elem.needsSyncChildren()) {
            elem.synchronizeChildren();
         }

         AttributeMap attributes = elem.hasAttributes() ? (AttributeMap)elem.getAttributes() : null;
         Attr att;
         int i;
         if ((this.fConfiguration.features & 1) == 0) {
            if (attributes != null) {
               for(i = 0; i < attributes.getLength(); ++i) {
                  att = (Attr)attributes.item(i);
                  att.normalize();
                  if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0) {
                     isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, (AttrImpl)att, att.getValue(), this.fDocument.isXML11Version());
                     if (this.fDocument.isXMLVersionChanged()) {
                        wellformed = CoreDocumentImpl.isXMLName(((Node)node).getNodeName(), this.fDocument.isXML11Version());
                        if (!wellformed) {
                           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Attr", ((Node)node).getNodeName()});
                           reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "wf-invalid-character-in-node-name");
                        }
                     }
                  }
               }
            }
         } else {
            this.namespaceFixUp(elem, attributes);
            if ((this.fConfiguration.features & 512) == 0 && attributes != null) {
               for(i = 0; i < attributes.getLength(); ++i) {
                  att = (Attr)attributes.getItem(i);
                  if (XMLSymbols.PREFIX_XMLNS.equals(att.getPrefix()) || XMLSymbols.PREFIX_XMLNS.equals(att.getName())) {
                     elem.removeAttributeNode(att);
                     --i;
                  }
               }
            }
         }

         if (this.fValidationHandler != null) {
            this.fAttrProxy.setAttributes(attributes, this.fDocument, elem);
            this.updateQName(elem, this.fQName);
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fValidationHandler.startElement(this.fQName, this.fAttrProxy, (Augmentations)null);
         }

         if (this.fDTDValidator != null) {
            this.fAttrProxy.setAttributes(attributes, this.fDocument, elem);
            this.updateQName(elem, this.fQName);
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fDTDValidator.startElement(this.fQName, this.fAttrProxy, (Augmentations)null);
         }

         Node next;
         for(kid = elem.getFirstChild(); kid != null; kid = next) {
            next = kid.getNextSibling();
            kid = this.normalizeNode(kid);
            if (kid != null) {
               next = kid;
            }
         }

         if (this.fValidationHandler != null) {
            this.updateQName(elem, this.fQName);
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fValidationHandler.endElement(this.fQName, (Augmentations)null);
         }

         if (this.fDTDValidator != null) {
            this.updateQName(elem, this.fQName);
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fDTDValidator.endElement(this.fQName, (Augmentations)null);
         }

         this.fNamespaceContext.popContext();
      case 2:
      case 6:
      case 9:
      default:
         break;
      case 3:
         prevSibling = ((Node)node).getNextSibling();
         if (prevSibling != null && prevSibling.getNodeType() == 3) {
            ((Text)node).appendData(prevSibling.getNodeValue());
            ((Node)node).getParentNode().removeChild(prevSibling);
            return (Node)node;
         }

         if (((Node)node).getNodeValue().length() == 0) {
            ((Node)node).getParentNode().removeChild((Node)node);
         } else {
            short nextType = prevSibling != null ? prevSibling.getNodeType() : -1;
            if (nextType == -1 || ((this.fConfiguration.features & 4) != 0 || nextType != 6) && ((this.fConfiguration.features & 32) != 0 || nextType != 8) && ((this.fConfiguration.features & 8) != 0 || nextType != 4)) {
               if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0) {
                  isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, ((Node)node).getNodeValue(), this.fDocument.isXML11Version());
               }

               if (this.fValidationHandler != null) {
                  this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
                  this.fCurrentNode = (Node)node;
                  this.fValidationHandler.characterData(((Node)node).getNodeValue(), (Augmentations)null);
               }

               if (this.fDTDValidator != null) {
                  this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
                  this.fCurrentNode = (Node)node;
                  this.fDTDValidator.characterData(((Node)node).getNodeValue(), (Augmentations)null);
                  if (this.allWhitespace) {
                     this.allWhitespace = false;
                     ((TextImpl)node).setIgnorableWhitespace(true);
                  }
               }
            }
         }
         break;
      case 4:
         if ((this.fConfiguration.features & 8) == 0) {
            prevSibling = ((Node)node).getPreviousSibling();
            if (prevSibling != null && prevSibling.getNodeType() == 3) {
               ((Text)prevSibling).appendData(((Node)node).getNodeValue());
               ((Node)node).getParentNode().removeChild((Node)node);
               return prevSibling;
            }

            Text text = this.fDocument.createTextNode(((Node)node).getNodeValue());
            kid = ((Node)node).getParentNode();
            kid.replaceChild(text, (Node)node);
            return text;
         }

         if (this.fValidationHandler != null) {
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fValidationHandler.startCDATA((Augmentations)null);
            this.fValidationHandler.characterData(((Node)node).getNodeValue(), (Augmentations)null);
            this.fValidationHandler.endCDATA((Augmentations)null);
         }

         if (this.fDTDValidator != null) {
            this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = (Node)node;
            this.fCurrentNode = (Node)node;
            this.fDTDValidator.startCDATA((Augmentations)null);
            this.fDTDValidator.characterData(((Node)node).getNodeValue(), (Augmentations)null);
            this.fDTDValidator.endCDATA((Augmentations)null);
         }

         value = ((Node)node).getNodeValue();
         if ((this.fConfiguration.features & 16) != 0) {
            kid = ((Node)node).getParentNode();
            if (this.fDocument.errorChecking) {
               isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, ((Node)node).getNodeValue(), this.fDocument.isXML11Version());
            }

            int index;
            while((index = value.indexOf("]]>")) >= 0) {
               ((Node)node).setNodeValue(value.substring(0, index + 2));
               value = value.substring(index + 2);
               Node firstSplitNode = node;
               Node newChild = this.fDocument.createCDATASection(value);
               kid.insertBefore(newChild, ((Node)node).getNextSibling());
               node = newChild;
               this.fLocator.fRelatedNode = (Node)firstSplitNode;
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "cdata-sections-splitted", (Object[])null);
               reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)1, "cdata-sections-splitted");
            }

            return null;
         } else {
            if (this.fDocument.errorChecking) {
               isCDataWF(this.fErrorHandler, this.fError, this.fLocator, value, this.fDocument.isXML11Version());
            }
            break;
         }
      case 5:
         if ((this.fConfiguration.features & 4) == 0) {
            prevSibling = ((Node)node).getPreviousSibling();
            parent = ((Node)node).getParentNode();
            ((EntityReferenceImpl)node).setReadOnly(false, true);
            this.expandEntityRef(parent, (Node)node);
            parent.removeChild((Node)node);
            kid = prevSibling != null ? prevSibling.getNextSibling() : parent.getFirstChild();
            if (prevSibling != null && kid != null && prevSibling.getNodeType() == 3 && kid.getNodeType() == 3) {
               return prevSibling;
            }

            return kid;
         }

         if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0 && this.fDocument.isXMLVersionChanged()) {
            CoreDocumentImpl.isXMLName(((Node)node).getNodeName(), this.fDocument.isXML11Version());
         }
         break;
      case 7:
         if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0) {
            ProcessingInstruction pinode = (ProcessingInstruction)node;
            String target = pinode.getTarget();
            if (this.fDocument.isXML11Version()) {
               wellformed = XML11Char.isXML11ValidName(target);
            } else {
               wellformed = XMLChar.isValidName(target);
            }

            if (!wellformed) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Element", ((Node)node).getNodeName()});
               reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "wf-invalid-character-in-node-name");
            }

            isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, pinode.getData(), this.fDocument.isXML11Version());
         }
         break;
      case 8:
         if ((this.fConfiguration.features & 32) == 0) {
            prevSibling = ((Node)node).getPreviousSibling();
            parent = ((Node)node).getParentNode();
            parent.removeChild((Node)node);
            if (prevSibling != null && prevSibling.getNodeType() == 3) {
               kid = prevSibling.getNextSibling();
               if (kid != null && kid.getNodeType() == 3) {
                  ((TextImpl)kid).insertData(0, prevSibling.getNodeValue());
                  parent.removeChild(prevSibling);
                  return kid;
               }
            }
         } else if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0) {
            value = ((Comment)node).getData();
            isCommentWF(this.fErrorHandler, this.fError, this.fLocator, value, this.fDocument.isXML11Version());
         }
         break;
      case 10:
         DocumentTypeImpl docType = (DocumentTypeImpl)node;
         this.fDTDValidator = (XMLDTDValidator)CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/TR/REC-xml");
         this.fDTDValidator.setDocumentHandler(this);
         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.createGrammarPool(docType));
         this.fDTDValidator.reset(this.fConfiguration);
         this.fDTDValidator.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, (Augmentations)null);
         this.fDTDValidator.doctypeDecl(docType.getName(), docType.getPublicId(), docType.getSystemId(), (Augmentations)null);
      }

      return null;
   }

   private XMLGrammarPool createGrammarPool(DocumentTypeImpl docType) {
      XMLGrammarPoolImpl pool = new XMLGrammarPoolImpl();
      XMLGrammarPreparser preParser = new XMLGrammarPreparser(this.fSymbolTable);
      preParser.registerPreparser("http://www.w3.org/TR/REC-xml", (XMLGrammarLoader)null);
      preParser.setFeature("http://apache.org/xml/features/namespaces", true);
      preParser.setFeature("http://apache.org/xml/features/validation", true);
      preParser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", pool);
      String internalSubset = docType.getInternalSubset();
      XMLInputSource is = new XMLInputSource(docType.getPublicId(), docType.getSystemId(), (String)null);
      if (internalSubset != null) {
         is.setCharacterStream(new StringReader(internalSubset));
      }

      try {
         DTDGrammar g = (DTDGrammar)preParser.preparseGrammar("http://www.w3.org/TR/REC-xml", is);
         ((XMLDTDDescription)g.getGrammarDescription()).setRootName(docType.getName());
         is.setCharacterStream((Reader)null);
         g = (DTDGrammar)preParser.preparseGrammar("http://www.w3.org/TR/REC-xml", is);
         ((XMLDTDDescription)g.getGrammarDescription()).setRootName(docType.getName());
      } catch (XNIException var7) {
      } catch (IOException var8) {
      }

      return pool;
   }

   protected final void expandEntityRef(Node parent, Node reference) {
      Node next;
      for(Node kid = reference.getFirstChild(); kid != null; kid = next) {
         next = kid.getNextSibling();
         parent.insertBefore(kid, reference);
      }

   }

   protected final void namespaceFixUp(ElementImpl element, AttributeMap attributes) {
      String value;
      String uri;
      String prefix;
      int i;
      Attr attr;
      String declaredURI;
      if (attributes != null) {
         for(i = 0; i < attributes.getLength(); ++i) {
            attr = (Attr)attributes.getItem(i);
            if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0 && this.fDocument.isXMLVersionChanged()) {
               this.fDocument.checkQName(attr.getPrefix(), attr.getLocalName());
            }

            uri = attr.getNamespaceURI();
            if (uri != null && uri.equals(NamespaceContext.XMLNS_URI) && (this.fConfiguration.features & 512) != 0) {
               value = attr.getNodeValue();
               if (value == null) {
                  value = XMLSymbols.EMPTY_STRING;
               }

               if (this.fDocument.errorChecking && value.equals(NamespaceContext.XMLNS_URI)) {
                  this.fLocator.fRelatedNode = attr;
                  declaredURI = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", (Object[])null);
                  reportDOMError(this.fErrorHandler, this.fError, this.fLocator, declaredURI, (short)2, "CantBindXMLNS");
               } else {
                  prefix = attr.getPrefix();
                  prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
                  declaredURI = this.fSymbolTable.addSymbol(attr.getLocalName());
                  if (prefix == XMLSymbols.PREFIX_XMLNS) {
                     value = this.fSymbolTable.addSymbol(value);
                     if (value.length() != 0) {
                        this.fNamespaceContext.declarePrefix(declaredURI, value);
                     }
                  } else {
                     value = this.fSymbolTable.addSymbol(value);
                     this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, value);
                  }
               }
            }
         }
      }

      uri = element.getNamespaceURI();
      prefix = element.getPrefix();
      if ((this.fConfiguration.features & 512) == 0) {
         uri = null;
      } else if (uri != null) {
         uri = this.fSymbolTable.addSymbol(uri);
         prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
         if (this.fNamespaceContext.getURI(prefix) != uri) {
            this.addNamespaceDecl(prefix, uri, element);
            this.fLocalNSBinder.declarePrefix(prefix, uri);
            this.fNamespaceContext.declarePrefix(prefix, uri);
         }
      } else if (element.getLocalName() == null) {
         String msg;
         if (this.fNamespaceValidation) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[]{element.getNodeName()});
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "NullLocalElementName");
         } else {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[]{element.getNodeName()});
            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "NullLocalElementName");
         }
      } else {
         uri = this.fNamespaceContext.getURI(XMLSymbols.EMPTY_STRING);
         if (uri != null && uri.length() > 0) {
            this.addNamespaceDecl(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING, element);
            this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
            this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
         }
      }

      if (attributes != null) {
         attributes.cloneMap(this.fAttributeList);

         for(i = 0; i < this.fAttributeList.size(); ++i) {
            attr = (Attr)this.fAttributeList.get(i);
            this.fLocator.fRelatedNode = attr;
            attr.normalize();
            value = attr.getValue();
            String name = attr.getNodeName();
            uri = attr.getNamespaceURI();
            if (value == null) {
               value = XMLSymbols.EMPTY_STRING;
            }

            if (uri == null) {
               ((AttrImpl)attr).setIdAttribute(false);
               if (attr.getLocalName() == null) {
                  if (this.fNamespaceValidation) {
                     declaredURI = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[]{attr.getNodeName()});
                     reportDOMError(this.fErrorHandler, this.fError, this.fLocator, declaredURI, (short)3, "NullLocalAttrName");
                  } else {
                     declaredURI = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[]{attr.getNodeName()});
                     reportDOMError(this.fErrorHandler, this.fError, this.fLocator, declaredURI, (short)2, "NullLocalAttrName");
                  }
               }
            } else {
               prefix = attr.getPrefix();
               prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
               this.fSymbolTable.addSymbol(attr.getLocalName());
               if (uri == null || !uri.equals(NamespaceContext.XMLNS_URI)) {
                  String declaredPrefix;
                  if (this.fDocument.errorChecking && (this.fConfiguration.features & 256) != 0) {
                     isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, (AttrImpl)attr, attr.getValue(), this.fDocument.isXML11Version());
                     if (this.fDocument.isXMLVersionChanged()) {
                        boolean wellformed = CoreDocumentImpl.isXMLName(attr.getNodeName(), this.fDocument.isXML11Version());
                        if (!wellformed) {
                           declaredPrefix = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[]{"Attribute", attr.getNodeName()});
                           reportDOMError(this.fErrorHandler, this.fError, this.fLocator, declaredPrefix, (short)2, "wf-invalid-character-in-node-name");
                        }
                     }
                  }

                  ((AttrImpl)attr).setIdAttribute(false);
                  uri = this.fSymbolTable.addSymbol(uri);
                  declaredURI = this.fNamespaceContext.getURI(prefix);
                  if (prefix == XMLSymbols.EMPTY_STRING || declaredURI != uri) {
                     name = attr.getNodeName();
                     declaredPrefix = this.fNamespaceContext.getPrefix(uri);
                     if (declaredPrefix != null && declaredPrefix != XMLSymbols.EMPTY_STRING) {
                        prefix = declaredPrefix;
                     } else {
                        if (prefix == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(prefix) != null) {
                           int counter = 1;
                           SymbolTable var10000 = this.fSymbolTable;
                           StringBuilder var10001 = (new StringBuilder()).append("NS");
                           int var14 = counter + 1;

                           for(prefix = var10000.addSymbol(var10001.append((int)counter).toString()); this.fLocalNSBinder.getURI(prefix) != null; prefix = this.fSymbolTable.addSymbol("NS" + var14++)) {
                           }
                        }

                        this.addNamespaceDecl(prefix, uri, element);
                        value = this.fSymbolTable.addSymbol(value);
                        this.fLocalNSBinder.declarePrefix(prefix, value);
                        this.fNamespaceContext.declarePrefix(prefix, uri);
                     }

                     attr.setPrefix(prefix);
                  }
               }
            }
         }
      }

   }

   protected final void addNamespaceDecl(String prefix, String uri, ElementImpl element) {
      if (prefix == XMLSymbols.EMPTY_STRING) {
         element.setAttributeNS(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS, uri);
      } else {
         element.setAttributeNS(NamespaceContext.XMLNS_URI, "xmlns:" + prefix, uri);
      }

   }

   public static final void isCDataWF(DOMErrorHandler errorHandler, DOMErrorImpl error, DOMLocatorImpl locator, String datavalue, boolean isXML11Version) {
      if (datavalue != null && datavalue.length() != 0) {
         char[] dataarray = datavalue.toCharArray();
         int datalength = dataarray.length;
         int i;
         char c;
         int count;
         String msg;
         char c2;
         String msg;
         if (isXML11Version) {
            i = 0;

            while(true) {
               label112:
               do {
                  while(i < datalength) {
                     c = dataarray[i++];
                     if (XML11Char.isXML11Invalid(c)) {
                        if (!XMLChar.isHighSurrogate(c) || i >= datalength) {
                           break label112;
                        }

                        c2 = dataarray[i++];
                        continue label112;
                     }

                     if (c == ']') {
                        count = i;
                        if (i < datalength && dataarray[i] == ']') {
                           do {
                              ++count;
                           } while(count < datalength && dataarray[count] == ']');

                           if (count < datalength && dataarray[count] == '>') {
                              msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", (Object[])null);
                              reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                           }
                        }
                     }
                  }

                  return;
               } while(XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[]{Integer.toString(c, 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         } else {
            i = 0;

            while(true) {
               label80:
               do {
                  while(i < datalength) {
                     c = dataarray[i++];
                     if (XMLChar.isInvalid(c)) {
                        if (!XMLChar.isHighSurrogate(c) || i >= datalength) {
                           break label80;
                        }

                        c2 = dataarray[i++];
                        continue label80;
                     }

                     if (c == ']') {
                        count = i;
                        if (i < datalength && dataarray[i] == ']') {
                           do {
                              ++count;
                           } while(count < datalength && dataarray[count] == ']');

                           if (count < datalength && dataarray[count] == '>') {
                              msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", (Object[])null);
                              reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                           }
                        }
                     }
                  }

                  return;
               } while(XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[]{Integer.toString(c, 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         }
      }
   }

   public static final void isXMLCharWF(DOMErrorHandler errorHandler, DOMErrorImpl error, DOMLocatorImpl locator, String datavalue, boolean isXML11Version) {
      if (datavalue != null && datavalue.length() != 0) {
         char[] dataarray = datavalue.toCharArray();
         int datalength = dataarray.length;
         int i;
         char ch;
         char ch2;
         String msg;
         if (isXML11Version) {
            i = 0;

            while(true) {
               do {
                  do {
                     if (i >= datalength) {
                        return;
                     }
                  } while(!XML11Char.isXML11Invalid(dataarray[i++]));

                  ch = dataarray[i - 1];
                  if (!XMLChar.isHighSurrogate(ch) || i >= datalength) {
                     break;
                  }

                  ch2 = dataarray[i++];
               } while(XMLChar.isLowSurrogate(ch2) && XMLChar.isSupplemental(XMLChar.supplemental(ch, ch2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[]{Integer.toString(dataarray[i - 1], 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         } else {
            i = 0;

            while(true) {
               do {
                  do {
                     if (i >= datalength) {
                        return;
                     }
                  } while(!XMLChar.isInvalid(dataarray[i++]));

                  ch = dataarray[i - 1];
                  if (!XMLChar.isHighSurrogate(ch) || i >= datalength) {
                     break;
                  }

                  ch2 = dataarray[i++];
               } while(XMLChar.isLowSurrogate(ch2) && XMLChar.isSupplemental(XMLChar.supplemental(ch, ch2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[]{Integer.toString(dataarray[i - 1], 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         }
      }
   }

   public static final void isCommentWF(DOMErrorHandler errorHandler, DOMErrorImpl error, DOMLocatorImpl locator, String datavalue, boolean isXML11Version) {
      if (datavalue != null && datavalue.length() != 0) {
         char[] dataarray = datavalue.toCharArray();
         int datalength = dataarray.length;
         int i;
         char c;
         String msg;
         char c2;
         if (isXML11Version) {
            i = 0;

            while(true) {
               label80:
               do {
                  while(i < datalength) {
                     c = dataarray[i++];
                     if (XML11Char.isXML11Invalid(c)) {
                        if (!XMLChar.isHighSurrogate(c) || i >= datalength) {
                           break label80;
                        }

                        c2 = dataarray[i++];
                        continue label80;
                     }

                     if (c == '-' && i < datalength && dataarray[i] == '-') {
                        msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", (Object[])null);
                        reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                     }
                  }

                  return;
               } while(XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[]{Integer.toString(dataarray[i - 1], 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         } else {
            i = 0;

            while(true) {
               label60:
               do {
                  while(i < datalength) {
                     c = dataarray[i++];
                     if (XMLChar.isInvalid(c)) {
                        if (!XMLChar.isHighSurrogate(c) || i >= datalength) {
                           break label60;
                        }

                        c2 = dataarray[i++];
                        continue label60;
                     }

                     if (c == '-' && i < datalength && dataarray[i] == '-') {
                        msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", (Object[])null);
                        reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                     }
                  }

                  return;
               } while(XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2)));

               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[]{Integer.toString(dataarray[i - 1], 16)});
               reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
            }
         }
      }
   }

   public static final void isAttrValueWF(DOMErrorHandler errorHandler, DOMErrorImpl error, DOMLocatorImpl locator, NamedNodeMap attributes, Attr a, String value, boolean xml11Version) {
      if (a instanceof AttrImpl && ((AttrImpl)a).hasStringValue()) {
         isXMLCharWF(errorHandler, error, locator, value, xml11Version);
      } else {
         NodeList children = a.getChildNodes();

         for(int j = 0; j < children.getLength(); ++j) {
            Node child = children.item(j);
            if (child.getNodeType() == 5) {
               Document owner = a.getOwnerDocument();
               Entity ent = null;
               if (owner != null) {
                  DocumentType docType = owner.getDoctype();
                  if (docType != null) {
                     NamedNodeMap entities = docType.getEntities();
                     ent = (Entity)entities.getNamedItemNS("*", child.getNodeName());
                  }
               }

               if (ent == null) {
                  String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UndeclaredEntRefInAttrValue", new Object[]{a.getNodeName()});
                  reportDOMError(errorHandler, error, locator, msg, (short)2, "UndeclaredEntRefInAttrValue");
               }
            } else {
               isXMLCharWF(errorHandler, error, locator, child.getNodeValue(), xml11Version);
            }
         }
      }

   }

   public static final void reportDOMError(DOMErrorHandler errorHandler, DOMErrorImpl error, DOMLocatorImpl locator, String message, short severity, String type) {
      if (errorHandler != null) {
         error.reset();
         error.fMessage = message;
         error.fSeverity = severity;
         error.fLocator = locator;
         error.fType = type;
         error.fRelatedData = locator.fRelatedNode;
         if (!errorHandler.handleError(error)) {
            throw new AbortException();
         }
      }

      if (severity == 3) {
         throw new AbortException();
      }
   }

   protected final void updateQName(Node node, QName qname) {
      String prefix = node.getPrefix();
      String namespace = node.getNamespaceURI();
      String localName = node.getLocalName();
      qname.prefix = prefix != null && prefix.length() != 0 ? this.fSymbolTable.addSymbol(prefix) : null;
      qname.localpart = localName != null ? this.fSymbolTable.addSymbol(localName) : null;
      qname.rawname = this.fSymbolTable.addSymbol(node.getNodeName());
      qname.uri = namespace != null ? this.fSymbolTable.addSymbol(namespace) : null;
   }

   final String normalizeAttributeValue(String value, Attr attr) {
      if (!attr.getSpecified()) {
         return value;
      } else {
         int end = value.length();
         if (this.fNormalizedValue.ch.length < end) {
            this.fNormalizedValue.ch = new char[end];
         }

         this.fNormalizedValue.length = 0;
         boolean normalized = false;

         for(int i = 0; i < end; ++i) {
            char c = value.charAt(i);
            if (c != '\t' && c != '\n') {
               if (c == '\r') {
                  normalized = true;
                  this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
                  int next = i + 1;
                  if (next < end && value.charAt(next) == '\n') {
                     i = next;
                  }
               } else {
                  this.fNormalizedValue.ch[this.fNormalizedValue.length++] = c;
               }
            } else {
               this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
               normalized = true;
            }
         }

         if (normalized) {
            value = this.fNormalizedValue.toString();
            attr.setValue(value);
         }

         return value;
      }
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      Element currentElement = (Element)this.fCurrentNode;
      int attrCount = attributes.getLength();

      for(int i = 0; i < attrCount; ++i) {
         attributes.getName(i, this.fAttrQName);
         Attr attr = null;
         attr = currentElement.getAttributeNodeNS(this.fAttrQName.uri, this.fAttrQName.localpart);
         AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
         if (attrPSVI != null) {
            XSTypeDefinition decl = attrPSVI.getMemberTypeDefinition();
            boolean id = false;
            if (decl != null) {
               id = ((XSSimpleType)decl).isIDType();
            } else {
               XSTypeDefinition decl = attrPSVI.getTypeDefinition();
               if (decl != null) {
                  id = ((XSSimpleType)decl).isIDType();
               }
            }

            if (id) {
               ((ElementImpl)currentElement).setIdAttributeNode(attr, true);
            }

            if (this.fPSVI) {
               ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
            }

            if ((this.fConfiguration.features & 2) != 0) {
               boolean specified = attr.getSpecified();
               attr.setValue(attrPSVI.getSchemaNormalizedValue());
               if (!specified) {
                  ((AttrImpl)attr).setSpecified(specified);
               }
            }
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
   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      this.allWhitespace = true;
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (augs != null) {
         ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
         if (elementPSVI != null) {
            ElementImpl elementNode = (ElementImpl)this.fCurrentNode;
            if (this.fPSVI) {
               ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
            }

            String normalizedValue = elementPSVI.getSchemaNormalizedValue();
            if ((this.fConfiguration.features & 2) != 0) {
               if (normalizedValue != null) {
                  elementNode.setTextContent(normalizedValue);
               }
            } else {
               String text = elementNode.getTextContent();
               if (text.length() == 0 && normalizedValue != null) {
                  elementNode.setTextContent(normalizedValue);
               }
            }
         }
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
   }

   public void endCDATA(Augmentations augs) throws XNIException {
   }

   public void endDocument(Augmentations augs) throws XNIException {
   }

   public void setDocumentSource(XMLDocumentSource source) {
   }

   public XMLDocumentSource getDocumentSource() {
      return null;
   }

   protected final class XMLAttributesProxy implements XMLAttributes {
      protected AttributeMap fAttributes;
      protected CoreDocumentImpl fDocument;
      protected ElementImpl fElement;
      protected final Vector fAugmentations = new Vector(5);

      public void setAttributes(AttributeMap attributes, CoreDocumentImpl doc, ElementImpl elem) {
         this.fDocument = doc;
         this.fAttributes = attributes;
         this.fElement = elem;
         if (attributes != null) {
            int length = attributes.getLength();
            this.fAugmentations.setSize(length);

            for(int i = 0; i < length; ++i) {
               this.fAugmentations.setElementAt(new AugmentationsImpl(), i);
            }
         } else {
            this.fAugmentations.setSize(0);
         }

      }

      public int addAttribute(QName qname, String attrType, String attrValue) {
         int index = this.fElement.getXercesAttribute(qname.uri, qname.localpart);
         if (index < 0) {
            AttrImpl attr = (AttrImpl)((CoreDocumentImpl)this.fElement.getOwnerDocument()).createAttributeNS(qname.uri, qname.rawname, qname.localpart);
            attr.setNodeValue(attrValue);
            index = this.fElement.setXercesAttributeNode(attr);
            this.fAugmentations.insertElementAt(new AugmentationsImpl(), index);
            attr.setSpecified(false);
         }

         return index;
      }

      public void removeAllAttributes() {
      }

      public void removeAttributeAt(int attrIndex) {
      }

      public int getLength() {
         return this.fAttributes != null ? this.fAttributes.getLength() : 0;
      }

      public int getIndex(String qName) {
         return -1;
      }

      public int getIndex(String uri, String localPart) {
         return -1;
      }

      public void setName(int attrIndex, QName attrName) {
      }

      public void getName(int attrIndex, QName attrName) {
         if (this.fAttributes != null) {
            DOMNormalizer.this.updateQName((Node)this.fAttributes.getItem(attrIndex), attrName);
         }

      }

      public String getPrefix(int index) {
         return null;
      }

      public String getURI(int index) {
         return null;
      }

      public String getLocalName(int index) {
         return null;
      }

      public String getQName(int index) {
         return null;
      }

      public QName getQualifiedName(int index) {
         return null;
      }

      public void setType(int attrIndex, String attrType) {
      }

      public String getType(int index) {
         return "CDATA";
      }

      public String getType(String qName) {
         return "CDATA";
      }

      public String getType(String uri, String localName) {
         return "CDATA";
      }

      public void setValue(int attrIndex, String attrValue) {
         if (this.fAttributes != null) {
            AttrImpl attr = (AttrImpl)this.fAttributes.getItem(attrIndex);
            boolean specified = attr.getSpecified();
            attr.setValue(attrValue);
            attr.setSpecified(specified);
         }

      }

      public void setValue(int attrIndex, String attrValue, XMLString value) {
         this.setValue(attrIndex, value.toString());
      }

      public String getValue(int index) {
         return this.fAttributes != null ? this.fAttributes.item(index).getNodeValue() : "";
      }

      public String getValue(String qName) {
         return null;
      }

      public String getValue(String uri, String localName) {
         if (this.fAttributes != null) {
            Node node = this.fAttributes.getNamedItemNS(uri, localName);
            return node != null ? node.getNodeValue() : null;
         } else {
            return null;
         }
      }

      public void setNonNormalizedValue(int attrIndex, String attrValue) {
      }

      public String getNonNormalizedValue(int attrIndex) {
         return null;
      }

      public void setSpecified(int attrIndex, boolean specified) {
         AttrImpl attr = (AttrImpl)this.fAttributes.getItem(attrIndex);
         attr.setSpecified(specified);
      }

      public boolean isSpecified(int attrIndex) {
         return ((Attr)this.fAttributes.getItem(attrIndex)).getSpecified();
      }

      public Augmentations getAugmentations(int attributeIndex) {
         return (Augmentations)this.fAugmentations.elementAt(attributeIndex);
      }

      public Augmentations getAugmentations(String uri, String localPart) {
         return null;
      }

      public Augmentations getAugmentations(String qName) {
         return null;
      }

      public void setAugmentations(int attrIndex, Augmentations augs) {
         this.fAugmentations.setElementAt(augs, attrIndex);
      }
   }
}
