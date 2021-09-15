package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CoreDocumentImpl extends ParentNode implements Document {
   static final long serialVersionUID = 0L;
   protected DocumentTypeImpl docType;
   protected ElementImpl docElement;
   transient NodeListCache fFreeNLCache;
   protected String encoding;
   protected String actualEncoding;
   protected String version;
   protected boolean standalone;
   protected String fDocumentURI;
   private Map<Node, Map<String, ParentNode.UserDataRecord>> nodeUserData;
   protected Map<String, Node> identifiers;
   transient DOMNormalizer domNormalizer;
   transient DOMConfigurationImpl fConfiguration;
   transient Object fXPathEvaluator;
   private static final int[] kidOK = new int[13];
   protected int changes;
   protected boolean allowGrammarAccess;
   protected boolean errorChecking;
   protected boolean ancestorChecking;
   protected boolean xmlVersionChanged;
   private int documentNumber;
   private int nodeCounter;
   private Map<Node, Integer> nodeTable;
   private boolean xml11Version;
   private static final ObjectStreamField[] serialPersistentFields;

   public CoreDocumentImpl() {
      this(false);
   }

   public CoreDocumentImpl(boolean grammarAccess) {
      super((CoreDocumentImpl)null);
      this.domNormalizer = null;
      this.fConfiguration = null;
      this.fXPathEvaluator = null;
      this.changes = 0;
      this.errorChecking = true;
      this.ancestorChecking = true;
      this.xmlVersionChanged = false;
      this.documentNumber = 0;
      this.nodeCounter = 0;
      this.xml11Version = false;
      this.ownerDocument = this;
      this.allowGrammarAccess = grammarAccess;
      String systemProp = SecuritySupport.getSystemProperty("http://java.sun.com/xml/dom/properties/ancestor-check");
      if (systemProp != null && systemProp.equalsIgnoreCase("false")) {
         this.ancestorChecking = false;
      }

   }

   public CoreDocumentImpl(DocumentType doctype) {
      this(doctype, false);
   }

   public CoreDocumentImpl(DocumentType doctype, boolean grammarAccess) {
      this(grammarAccess);
      if (doctype != null) {
         DocumentTypeImpl doctypeImpl;
         try {
            doctypeImpl = (DocumentTypeImpl)doctype;
         } catch (ClassCastException var6) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }

         doctypeImpl.ownerDocument = this;
         this.appendChild(doctype);
      }

   }

   public final Document getOwnerDocument() {
      return null;
   }

   public short getNodeType() {
      return 9;
   }

   public String getNodeName() {
      return "#document";
   }

   public Node cloneNode(boolean deep) {
      CoreDocumentImpl newdoc = new CoreDocumentImpl();
      this.callUserDataHandlers(this, newdoc, (short)1);
      this.cloneNode(newdoc, deep);
      return newdoc;
   }

   protected void cloneNode(CoreDocumentImpl newdoc, boolean deep) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      if (deep) {
         Map<Node, String> reversedIdentifiers = null;
         if (this.identifiers != null) {
            reversedIdentifiers = new HashMap(this.identifiers.size());
            Iterator var4 = this.identifiers.keySet().iterator();

            while(var4.hasNext()) {
               String elementId = (String)var4.next();
               reversedIdentifiers.put(this.identifiers.get(elementId), elementId);
            }
         }

         for(ChildNode kid = this.firstChild; kid != null; kid = kid.nextSibling) {
            newdoc.appendChild(newdoc.importNode(kid, true, true, reversedIdentifiers));
         }
      }

      newdoc.allowGrammarAccess = this.allowGrammarAccess;
      newdoc.errorChecking = this.errorChecking;
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException {
      int type = newChild.getNodeType();
      if (!this.errorChecking || (type != 1 || this.docElement == null) && (type != 10 || this.docType == null)) {
         if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)newChild).ownerDocument = this;
         }

         super.insertBefore(newChild, refChild);
         if (type == 1) {
            this.docElement = (ElementImpl)newChild;
         } else if (type == 10) {
            this.docType = (DocumentTypeImpl)newChild;
         }

         return newChild;
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
         throw new DOMException((short)3, msg);
      }
   }

   public Node removeChild(Node oldChild) throws DOMException {
      super.removeChild(oldChild);
      int type = oldChild.getNodeType();
      if (type == 1) {
         this.docElement = null;
      } else if (type == 10) {
         this.docType = null;
      }

      return oldChild;
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
      if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
         ((DocumentTypeImpl)newChild).ownerDocument = this;
      }

      if (this.errorChecking && (this.docType != null && oldChild.getNodeType() != 10 && newChild.getNodeType() == 10 || this.docElement != null && oldChild.getNodeType() != 1 && newChild.getNodeType() == 1)) {
         throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
      } else {
         super.replaceChild(newChild, oldChild);
         int type = oldChild.getNodeType();
         if (type == 1) {
            this.docElement = (ElementImpl)newChild;
         } else if (type == 10) {
            this.docType = (DocumentTypeImpl)newChild;
         }

         return oldChild;
      }
   }

   public String getTextContent() throws DOMException {
      return null;
   }

   public void setTextContent(String textContent) throws DOMException {
   }

   public Object getFeature(String feature, String version) {
      boolean anyVersion = version == null || version.length() == 0;
      if (!feature.equalsIgnoreCase("+XPath") || !anyVersion && !version.equals("3.0")) {
         return super.getFeature(feature, version);
      } else if (this.fXPathEvaluator != null) {
         return this.fXPathEvaluator;
      } else {
         try {
            Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
            Constructor xpathClassConstr = xpathClass.getConstructor(Document.class);
            Class[] interfaces = xpathClass.getInterfaces();

            for(int i = 0; i < interfaces.length; ++i) {
               if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                  this.fXPathEvaluator = xpathClassConstr.newInstance(this);
                  return this.fXPathEvaluator;
               }
            }

            return null;
         } catch (Exception var8) {
            return null;
         }
      }
   }

   public Attr createAttribute(String name) throws DOMException {
      if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new AttrImpl(this, name);
      }
   }

   public CDATASection createCDATASection(String data) throws DOMException {
      return new CDATASectionImpl(this, data);
   }

   public Comment createComment(String data) {
      return new CommentImpl(this, data);
   }

   public DocumentFragment createDocumentFragment() {
      return new DocumentFragmentImpl(this);
   }

   public Element createElement(String tagName) throws DOMException {
      if (this.errorChecking && !isXMLName(tagName, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new ElementImpl(this, tagName);
      }
   }

   public EntityReference createEntityReference(String name) throws DOMException {
      if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new EntityReferenceImpl(this, name);
      }
   }

   public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
      if (this.errorChecking && !isXMLName(target, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new ProcessingInstructionImpl(this, target, data);
      }
   }

   public Text createTextNode(String data) {
      return new TextImpl(this, data);
   }

   public DocumentType getDoctype() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.docType;
   }

   public Element getDocumentElement() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.docElement;
   }

   public NodeList getElementsByTagName(String tagname) {
      return new DeepNodeListImpl(this, tagname);
   }

   public DOMImplementation getImplementation() {
      return CoreDOMImplementationImpl.getDOMImplementation();
   }

   public void setErrorChecking(boolean check) {
      this.errorChecking = check;
   }

   public void setStrictErrorChecking(boolean check) {
      this.errorChecking = check;
   }

   public boolean getErrorChecking() {
      return this.errorChecking;
   }

   public boolean getStrictErrorChecking() {
      return this.errorChecking;
   }

   public String getInputEncoding() {
      return this.actualEncoding;
   }

   public void setInputEncoding(String value) {
      this.actualEncoding = value;
   }

   public void setXmlEncoding(String value) {
      this.encoding = value;
   }

   /** @deprecated */
   public void setEncoding(String value) {
      this.setXmlEncoding(value);
   }

   public String getXmlEncoding() {
      return this.encoding;
   }

   /** @deprecated */
   public String getEncoding() {
      return this.getXmlEncoding();
   }

   public void setXmlVersion(String value) {
      if (!value.equals("1.0") && !value.equals("1.1")) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      } else {
         if (!this.getXmlVersion().equals(value)) {
            this.xmlVersionChanged = true;
            this.isNormalized(false);
            this.version = value;
         }

         if (this.getXmlVersion().equals("1.1")) {
            this.xml11Version = true;
         } else {
            this.xml11Version = false;
         }

      }
   }

   /** @deprecated */
   public void setVersion(String value) {
      this.setXmlVersion(value);
   }

   public String getXmlVersion() {
      return this.version == null ? "1.0" : this.version;
   }

   /** @deprecated */
   public String getVersion() {
      return this.getXmlVersion();
   }

   public void setXmlStandalone(boolean value) throws DOMException {
      this.standalone = value;
   }

   /** @deprecated */
   public void setStandalone(boolean value) {
      this.setXmlStandalone(value);
   }

   public boolean getXmlStandalone() {
      return this.standalone;
   }

   /** @deprecated */
   public boolean getStandalone() {
      return this.getXmlStandalone();
   }

   public String getDocumentURI() {
      return this.fDocumentURI;
   }

   public Node renameNode(Node n, String namespaceURI, String name) throws DOMException {
      String msg;
      if (this.errorChecking && n.getOwnerDocument() != this && n != this) {
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
         throw new DOMException((short)4, msg);
      } else {
         Object at;
         Node nextSib;
         switch(n.getNodeType()) {
         case 1:
            at = (ElementImpl)n;
            if (at instanceof ElementNSImpl) {
               ((ElementNSImpl)at).rename(namespaceURI, name);
               this.callUserDataHandlers((Node)at, (Node)null, (short)4);
            } else if (namespaceURI == null) {
               if (this.errorChecking) {
                  int colon1 = name.indexOf(58);
                  String msg;
                  if (colon1 != -1) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
                     throw new DOMException((short)14, msg);
                  }

                  if (!isXMLName(name, this.xml11Version)) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
                     throw new DOMException((short)5, msg);
                  }
               }

               ((ElementImpl)at).rename(name);
               this.callUserDataHandlers((Node)at, (Node)null, (short)4);
            } else {
               ElementNSImpl nel = new ElementNSImpl(this, namespaceURI, name);
               this.copyEventListeners((NodeImpl)at, nel);
               Map<String, ParentNode.UserDataRecord> data = this.removeUserDataTable((Node)at);
               Node parent = ((ElementImpl)at).getParentNode();
               nextSib = ((ElementImpl)at).getNextSibling();
               if (parent != null) {
                  parent.removeChild((Node)at);
               }

               for(Node child = ((ElementImpl)at).getFirstChild(); child != null; child = ((ElementImpl)at).getFirstChild()) {
                  ((ElementImpl)at).removeChild(child);
                  nel.appendChild(child);
               }

               nel.moveSpecifiedAttributes((ElementImpl)at);
               this.setUserDataTable(nel, data);
               this.callUserDataHandlers((Node)at, nel, (short)4);
               if (parent != null) {
                  parent.insertBefore(nel, nextSib);
               }

               at = nel;
            }

            this.renamedElement((Element)n, (Element)at);
            return (Node)at;
         case 2:
            at = (AttrImpl)n;
            Element el = ((AttrImpl)at).getOwnerElement();
            if (el != null) {
               el.removeAttributeNode((Attr)at);
            }

            if (n instanceof AttrNSImpl) {
               ((AttrNSImpl)at).rename(namespaceURI, name);
               if (el != null) {
                  el.setAttributeNodeNS((Attr)at);
               }

               this.callUserDataHandlers((Node)at, (Node)null, (short)4);
            } else if (namespaceURI == null) {
               ((AttrImpl)at).rename(name);
               if (el != null) {
                  el.setAttributeNode((Attr)at);
               }

               this.callUserDataHandlers((Node)at, (Node)null, (short)4);
            } else {
               AttrNSImpl nat = new AttrNSImpl(this, namespaceURI, name);
               this.copyEventListeners((NodeImpl)at, nat);
               Map<String, ParentNode.UserDataRecord> data = this.removeUserDataTable((Node)at);

               for(nextSib = ((AttrImpl)at).getFirstChild(); nextSib != null; nextSib = ((AttrImpl)at).getFirstChild()) {
                  ((AttrImpl)at).removeChild(nextSib);
                  nat.appendChild(nextSib);
               }

               this.setUserDataTable(nat, data);
               this.callUserDataHandlers((Node)at, nat, (short)4);
               if (el != null) {
                  el.setAttributeNode(nat);
               }

               at = nat;
            }

            this.renamedAttrNode((Attr)n, (Attr)at);
            return (Node)at;
         default:
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
            throw new DOMException((short)9, msg);
         }
      }
   }

   public void normalizeDocument() {
      if (!this.isNormalized() || this.isNormalizeDocRequired()) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         if (this.domNormalizer == null) {
            this.domNormalizer = new DOMNormalizer();
         }

         if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
         } else {
            this.fConfiguration.reset();
         }

         this.domNormalizer.normalizeDocument(this, this.fConfiguration);
         this.isNormalized(true);
         this.xmlVersionChanged = false;
      }
   }

   public DOMConfiguration getDomConfig() {
      if (this.fConfiguration == null) {
         this.fConfiguration = new DOMConfigurationImpl();
      }

      return this.fConfiguration;
   }

   public String getBaseURI() {
      if (this.fDocumentURI != null && this.fDocumentURI.length() != 0) {
         try {
            return (new URI(this.fDocumentURI)).toString();
         } catch (URI.MalformedURIException var2) {
            return null;
         }
      } else {
         return this.fDocumentURI;
      }
   }

   public void setDocumentURI(String documentURI) {
      this.fDocumentURI = documentURI;
   }

   public boolean getAsync() {
      return false;
   }

   public void setAsync(boolean async) {
      if (async) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      }
   }

   public void abort() {
   }

   public boolean load(String uri) {
      return false;
   }

   public boolean loadXML(String source) {
      return false;
   }

   public String saveXML(Node node) throws DOMException {
      if (this.errorChecking && node != null && this != ((Node)node).getOwnerDocument()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
         throw new DOMException((short)4, msg);
      } else {
         DOMImplementationLS domImplLS = (DOMImplementationLS)DOMImplementationImpl.getDOMImplementation();
         LSSerializer xmlWriter = domImplLS.createLSSerializer();
         if (node == null) {
            node = this;
         }

         return xmlWriter.writeToString((Node)node);
      }
   }

   void setMutationEvents(boolean set) {
   }

   boolean getMutationEvents() {
      return false;
   }

   public DocumentType createDocumentType(String qualifiedName, String publicID, String systemID) throws DOMException {
      return new DocumentTypeImpl(this, qualifiedName, publicID, systemID);
   }

   public Entity createEntity(String name) throws DOMException {
      if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new EntityImpl(this, name);
      }
   }

   public Notation createNotation(String name) throws DOMException {
      if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new NotationImpl(this, name);
      }
   }

   public ElementDefinitionImpl createElementDefinition(String name) throws DOMException {
      if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
         throw new DOMException((short)5, msg);
      } else {
         return new ElementDefinitionImpl(this, name);
      }
   }

   protected int getNodeNumber() {
      if (this.documentNumber == 0) {
         CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
         this.documentNumber = cd.assignDocumentNumber();
      }

      return this.documentNumber;
   }

   protected int getNodeNumber(Node node) {
      int num;
      if (this.nodeTable == null) {
         this.nodeTable = new HashMap();
         num = --this.nodeCounter;
         this.nodeTable.put(node, new Integer(num));
      } else {
         Integer n = (Integer)this.nodeTable.get(node);
         if (n == null) {
            num = --this.nodeCounter;
            this.nodeTable.put(node, num);
         } else {
            num = n;
         }
      }

      return num;
   }

   public Node importNode(Node source, boolean deep) throws DOMException {
      return this.importNode(source, deep, false, (Map)null);
   }

   private Node importNode(Node source, boolean deep, boolean cloningDoc, Map<Node, String> reversedIdentifiers) throws DOMException {
      Node newnode = null;
      Map<String, ParentNode.UserDataRecord> userData = null;
      if (source instanceof NodeImpl) {
         userData = ((NodeImpl)source).getUserDataRecord();
      }

      int type = source.getNodeType();
      NamedNodeMap smap;
      int i;
      String msg;
      switch(type) {
      case 1:
         boolean domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
         Element newElement;
         if (domLevel20 && source.getLocalName() != null) {
            newElement = this.createElementNS(source.getNamespaceURI(), source.getNodeName());
         } else {
            newElement = this.createElement(source.getNodeName());
         }

         smap = source.getAttributes();
         if (smap != null) {
            int length = smap.getLength();

            for(i = 0; i < length; ++i) {
               Attr attr = (Attr)smap.item(i);
               if (attr.getSpecified() || cloningDoc) {
                  Attr newAttr = (Attr)this.importNode(attr, true, cloningDoc, reversedIdentifiers);
                  if (domLevel20 && attr.getLocalName() != null) {
                     newElement.setAttributeNodeNS(newAttr);
                  } else {
                     newElement.setAttributeNode(newAttr);
                  }
               }
            }
         }

         if (reversedIdentifiers != null) {
            String elementId = (String)reversedIdentifiers.get(source);
            if (elementId != null) {
               if (this.identifiers == null) {
                  this.identifiers = new HashMap();
               }

               this.identifiers.put(elementId, newElement);
            }
         }

         newnode = newElement;
         break;
      case 2:
         if (source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0")) {
            if (source.getLocalName() == null) {
               newnode = this.createAttribute(source.getNodeName());
            } else {
               newnode = this.createAttributeNS(source.getNamespaceURI(), source.getNodeName());
            }
         } else {
            newnode = this.createAttribute(source.getNodeName());
         }

         if (source instanceof AttrImpl) {
            AttrImpl attr = (AttrImpl)source;
            if (attr.hasStringValue()) {
               AttrImpl newattr = (AttrImpl)newnode;
               newattr.setValue(attr.getValue());
               deep = false;
            } else {
               deep = true;
            }
         } else if (source.getFirstChild() == null) {
            ((Node)newnode).setNodeValue(source.getNodeValue());
            deep = false;
         } else {
            deep = true;
         }
         break;
      case 3:
         newnode = this.createTextNode(source.getNodeValue());
         break;
      case 4:
         newnode = this.createCDATASection(source.getNodeValue());
         break;
      case 5:
         newnode = this.createEntityReference(source.getNodeName());
         deep = false;
         break;
      case 6:
         Entity srcentity = (Entity)source;
         EntityImpl newentity = (EntityImpl)this.createEntity(source.getNodeName());
         newentity.setPublicId(srcentity.getPublicId());
         newentity.setSystemId(srcentity.getSystemId());
         newentity.setNotationName(srcentity.getNotationName());
         newentity.isReadOnly(false);
         newnode = newentity;
         break;
      case 7:
         newnode = this.createProcessingInstruction(source.getNodeName(), source.getNodeValue());
         break;
      case 8:
         newnode = this.createComment(source.getNodeValue());
         break;
      case 9:
      default:
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      case 10:
         if (!cloningDoc) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
            throw new DOMException((short)9, msg);
         }

         DocumentType srcdoctype = (DocumentType)source;
         DocumentTypeImpl newdoctype = (DocumentTypeImpl)this.createDocumentType(srcdoctype.getNodeName(), srcdoctype.getPublicId(), srcdoctype.getSystemId());
         smap = srcdoctype.getEntities();
         NamedNodeMap tmap = newdoctype.getEntities();
         if (smap != null) {
            for(i = 0; i < smap.getLength(); ++i) {
               tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
            }
         }

         smap = srcdoctype.getNotations();
         tmap = newdoctype.getNotations();
         if (smap != null) {
            for(i = 0; i < smap.getLength(); ++i) {
               tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
            }
         }

         newnode = newdoctype;
         break;
      case 11:
         newnode = this.createDocumentFragment();
         break;
      case 12:
         Notation srcnotation = (Notation)source;
         NotationImpl newnotation = (NotationImpl)this.createNotation(source.getNodeName());
         newnotation.setPublicId(srcnotation.getPublicId());
         newnotation.setSystemId(srcnotation.getSystemId());
         newnode = newnotation;
      }

      if (userData != null) {
         this.callUserDataHandlers(source, (Node)newnode, (short)2, userData);
      }

      if (deep) {
         for(Node srckid = source.getFirstChild(); srckid != null; srckid = srckid.getNextSibling()) {
            ((Node)newnode).appendChild(this.importNode(srckid, true, cloningDoc, reversedIdentifiers));
         }
      }

      if (((Node)newnode).getNodeType() == 6) {
         ((NodeImpl)newnode).setReadOnly(true, true);
      }

      return (Node)newnode;
   }

   public Node adoptNode(Node source) {
      NodeImpl node;
      try {
         node = (NodeImpl)source;
      } catch (ClassCastException var9) {
         return null;
      }

      if (source == null) {
         return null;
      } else {
         if (source.getOwnerDocument() != null) {
            DOMImplementation thisImpl = this.getImplementation();
            DOMImplementation otherImpl = source.getOwnerDocument().getImplementation();
            if (thisImpl != otherImpl) {
               if (thisImpl instanceof DOMImplementationImpl && otherImpl instanceof DeferredDOMImplementationImpl) {
                  this.undeferChildren(node);
               } else if (!(thisImpl instanceof DeferredDOMImplementationImpl) || !(otherImpl instanceof DOMImplementationImpl)) {
                  return null;
               }
            }
         }

         Map userData;
         String msg;
         Node parent;
         switch(node.getNodeType()) {
         case 1:
            userData = node.getUserDataRecord();
            parent = node.getParentNode();
            if (parent != null) {
               parent.removeChild(source);
            }

            node.setOwnerDocument(this);
            if (userData != null) {
               this.setUserDataTable(node, userData);
            }

            ((ElementImpl)node).reconcileDefaultAttributes();
            break;
         case 2:
            AttrImpl attr = (AttrImpl)node;
            if (attr.getOwnerElement() != null) {
               attr.getOwnerElement().removeAttributeNode(attr);
            }

            attr.isSpecified(true);
            userData = node.getUserDataRecord();
            attr.setOwnerDocument(this);
            if (userData != null) {
               this.setUserDataTable(node, userData);
            }
            break;
         case 3:
         case 4:
         case 7:
         case 8:
         case 11:
         default:
            userData = node.getUserDataRecord();
            parent = node.getParentNode();
            if (parent != null) {
               parent.removeChild(source);
            }

            node.setOwnerDocument(this);
            if (userData != null) {
               this.setUserDataTable(node, userData);
            }
            break;
         case 5:
            userData = node.getUserDataRecord();
            parent = node.getParentNode();
            if (parent != null) {
               parent.removeChild(source);
            }

            Node child;
            while((child = node.getFirstChild()) != null) {
               node.removeChild(child);
            }

            node.setOwnerDocument(this);
            if (userData != null) {
               this.setUserDataTable(node, userData);
            }

            if (this.docType != null) {
               NamedNodeMap entities = this.docType.getEntities();
               Node entityNode = entities.getNamedItem(node.getNodeName());
               if (entityNode != null) {
                  for(child = entityNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                     Node childClone = child.cloneNode(true);
                     node.appendChild(childClone);
                  }
               }
            }
            break;
         case 6:
         case 12:
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         case 9:
         case 10:
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
            throw new DOMException((short)9, msg);
         }

         if (userData != null) {
            this.callUserDataHandlers(source, (Node)null, (short)5, userData);
         }

         return node;
      }
   }

   protected void undeferChildren(Node node) {
      Node nextNode;
      label51:
      for(Node top = node; null != node; node = nextNode) {
         if (((NodeImpl)node).needsSyncData()) {
            ((NodeImpl)node).synchronizeData();
         }

         NamedNodeMap attributes = node.getAttributes();
         if (attributes != null) {
            int length = attributes.getLength();

            for(int i = 0; i < length; ++i) {
               this.undeferChildren(attributes.item(i));
            }
         }

         nextNode = null;
         nextNode = node.getFirstChild();

         do {
            do {
               if (null != nextNode || top.equals(node)) {
                  continue label51;
               }

               nextNode = node.getNextSibling();
            } while(null != nextNode);

            node = node.getParentNode();
         } while(null != node && !top.equals(node));

         nextNode = null;
      }

   }

   public Element getElementById(String elementId) {
      return this.getIdentifier(elementId);
   }

   protected final void clearIdentifiers() {
      if (this.identifiers != null) {
         this.identifiers.clear();
      }

   }

   public void putIdentifier(String idName, Element element) {
      if (element == null) {
         this.removeIdentifier(idName);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (this.identifiers == null) {
            this.identifiers = new HashMap();
         }

         this.identifiers.put(idName, element);
      }
   }

   public Element getIdentifier(String idName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.identifiers == null) {
         return null;
      } else {
         Element elem = (Element)this.identifiers.get(idName);
         if (elem != null) {
            for(Node parent = elem.getParentNode(); parent != null; parent = parent.getParentNode()) {
               if (parent == this) {
                  return elem;
               }
            }
         }

         return null;
      }
   }

   public void removeIdentifier(String idName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.identifiers != null) {
         this.identifiers.remove(idName);
      }
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
      return new ElementNSImpl(this, namespaceURI, qualifiedName);
   }

   public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
      return new ElementNSImpl(this, namespaceURI, qualifiedName, localpart);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
      return new AttrNSImpl(this, namespaceURI, qualifiedName);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
      return new AttrNSImpl(this, namespaceURI, qualifiedName, localpart);
   }

   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return new DeepNodeListImpl(this, namespaceURI, localName);
   }

   public Object clone() throws CloneNotSupportedException {
      CoreDocumentImpl newdoc = (CoreDocumentImpl)super.clone();
      newdoc.docType = null;
      newdoc.docElement = null;
      return newdoc;
   }

   public static final boolean isXMLName(String s, boolean xml11Version) {
      if (s == null) {
         return false;
      } else {
         return !xml11Version ? XMLChar.isValidName(s) : XML11Char.isXML11ValidName(s);
      }
   }

   public static final boolean isValidQName(String prefix, String local, boolean xml11Version) {
      if (local == null) {
         return false;
      } else {
         boolean validNCName = false;
         if (!xml11Version) {
            validNCName = (prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local);
         } else {
            validNCName = (prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local);
         }

         return validNCName;
      }
   }

   protected boolean isKidOK(Node parent, Node child) {
      if (this.allowGrammarAccess && parent.getNodeType() == 10) {
         return child.getNodeType() == 1;
      } else {
         return 0 != (kidOK[parent.getNodeType()] & 1 << child.getNodeType());
      }
   }

   protected void changed() {
      ++this.changes;
   }

   protected int changes() {
      return this.changes;
   }

   NodeListCache getNodeListCache(ParentNode owner) {
      if (this.fFreeNLCache == null) {
         return new NodeListCache(owner);
      } else {
         NodeListCache c = this.fFreeNLCache;
         this.fFreeNLCache = this.fFreeNLCache.next;
         c.fChild = null;
         c.fChildIndex = -1;
         c.fLength = -1;
         if (c.fOwner != null) {
            c.fOwner.fNodeListCache = null;
         }

         c.fOwner = owner;
         return c;
      }
   }

   void freeNodeListCache(NodeListCache c) {
      c.next = this.fFreeNLCache;
      this.fFreeNLCache = c;
   }

   public Object setUserData(Node n, String key, Object data, UserDataHandler handler) {
      ParentNode.UserDataRecord r;
      if (data == null) {
         if (this.nodeUserData != null) {
            Map<String, ParentNode.UserDataRecord> t = (Map)this.nodeUserData.get(n);
            if (t != null) {
               r = (ParentNode.UserDataRecord)t.remove(key);
               if (r != null) {
                  return r.fData;
               }
            }
         }

         return null;
      } else {
         Object t;
         if (this.nodeUserData == null) {
            this.nodeUserData = new HashMap();
            t = new HashMap();
            this.nodeUserData.put(n, t);
         } else {
            t = (Map)this.nodeUserData.get(n);
            if (t == null) {
               t = new HashMap();
               this.nodeUserData.put(n, t);
            }
         }

         r = (ParentNode.UserDataRecord)((Map)t).put(key, new ParentNode.UserDataRecord(data, handler));
         return r != null ? r.fData : null;
      }
   }

   public Object getUserData(Node n, String key) {
      if (this.nodeUserData == null) {
         return null;
      } else {
         Map<String, ParentNode.UserDataRecord> t = (Map)this.nodeUserData.get(n);
         if (t == null) {
            return null;
         } else {
            ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)t.get(key);
            return r != null ? r.fData : null;
         }
      }
   }

   protected Map<String, ParentNode.UserDataRecord> getUserDataRecord(Node n) {
      if (this.nodeUserData == null) {
         return null;
      } else {
         Map<String, ParentNode.UserDataRecord> t = (Map)this.nodeUserData.get(n);
         return t == null ? null : t;
      }
   }

   Map<String, ParentNode.UserDataRecord> removeUserDataTable(Node n) {
      return this.nodeUserData == null ? null : (Map)this.nodeUserData.get(n);
   }

   void setUserDataTable(Node n, Map<String, ParentNode.UserDataRecord> data) {
      if (this.nodeUserData == null) {
         this.nodeUserData = new HashMap();
      }

      if (data != null) {
         this.nodeUserData.put(n, data);
      }

   }

   void callUserDataHandlers(Node n, Node c, short operation) {
      if (this.nodeUserData != null) {
         if (n instanceof NodeImpl) {
            Map<String, ParentNode.UserDataRecord> t = ((NodeImpl)n).getUserDataRecord();
            if (t == null || t.isEmpty()) {
               return;
            }

            this.callUserDataHandlers(n, c, operation, t);
         }

      }
   }

   void callUserDataHandlers(Node n, Node c, short operation, Map<String, ParentNode.UserDataRecord> userData) {
      if (userData != null && !userData.isEmpty()) {
         Iterator var5 = userData.keySet().iterator();

         while(var5.hasNext()) {
            String key = (String)var5.next();
            ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)userData.get(key);
            if (r.fHandler != null) {
               r.fHandler.handle(operation, key, r.fData, n, c);
            }
         }

      }
   }

   protected final void checkNamespaceWF(String qname, int colon1, int colon2) {
      if (this.errorChecking) {
         if (colon1 == 0 || colon1 == qname.length() - 1 || colon2 != colon1) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
            throw new DOMException((short)14, msg);
         }
      }
   }

   protected final void checkDOMNSErr(String prefix, String namespace) {
      if (this.errorChecking) {
         String msg;
         if (namespace == null) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
            throw new DOMException((short)14, msg);
         }

         if (prefix.equals("xml") && !namespace.equals(NamespaceContext.XML_URI)) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
            throw new DOMException((short)14, msg);
         }

         if (prefix.equals("xmlns") && !namespace.equals(NamespaceContext.XMLNS_URI) || !prefix.equals("xmlns") && namespace.equals(NamespaceContext.XMLNS_URI)) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
            throw new DOMException((short)14, msg);
         }
      }

   }

   protected final void checkQName(String prefix, String local) {
      if (this.errorChecking) {
         boolean validNCName = false;
         if (!this.xml11Version) {
            validNCName = (prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local);
         } else {
            validNCName = (prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local);
         }

         if (!validNCName) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
            throw new DOMException((short)5, msg);
         }
      }
   }

   boolean isXML11Version() {
      return this.xml11Version;
   }

   boolean isNormalizeDocRequired() {
      return true;
   }

   boolean isXMLVersionChanged() {
      return this.xmlVersionChanged;
   }

   protected void setUserData(NodeImpl n, Object data) {
      this.setUserData(n, "XERCES1DOMUSERDATA", data, (UserDataHandler)null);
   }

   protected Object getUserData(NodeImpl n) {
      return this.getUserData(n, "XERCES1DOMUSERDATA");
   }

   protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
   }

   protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
   }

   protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {
   }

   protected boolean dispatchEvent(NodeImpl node, Event event) {
      return false;
   }

   void replacedText(NodeImpl node) {
   }

   void deletedText(NodeImpl node, int offset, int count) {
   }

   void insertedText(NodeImpl node, int offset, int count) {
   }

   void modifyingCharacterData(NodeImpl node, boolean replace) {
   }

   void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
   }

   void insertingNode(NodeImpl node, boolean replace) {
   }

   void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
   }

   void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
   }

   void removedNode(NodeImpl node, boolean replace) {
   }

   void replacingNode(NodeImpl node) {
   }

   void replacedNode(NodeImpl node) {
   }

   void replacingData(NodeImpl node) {
   }

   void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
   }

   void modifiedAttrValue(AttrImpl attr, String oldvalue) {
   }

   void setAttrNode(AttrImpl attr, AttrImpl previous) {
   }

   void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
   }

   void renamedAttrNode(Attr oldAt, Attr newAt) {
   }

   void renamedElement(Element oldEl, Element newEl) {
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      Hashtable<Node, Hashtable<String, ParentNode.UserDataRecord>> nud = null;
      if (this.nodeUserData != null) {
         nud = new Hashtable();
         Iterator var3 = this.nodeUserData.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<Node, Map<String, ParentNode.UserDataRecord>> e = (Map.Entry)var3.next();
            nud.put(e.getKey(), new Hashtable((Map)e.getValue()));
         }
      }

      Hashtable<String, Node> ids = this.identifiers == null ? null : new Hashtable(this.identifiers);
      Hashtable<Node, Integer> nt = this.nodeTable == null ? null : new Hashtable(this.nodeTable);
      ObjectOutputStream.PutField pf = out.putFields();
      pf.put("docType", this.docType);
      pf.put("docElement", this.docElement);
      pf.put("fFreeNLCache", this.fFreeNLCache);
      pf.put("encoding", this.encoding);
      pf.put("actualEncoding", this.actualEncoding);
      pf.put("version", this.version);
      pf.put("standalone", this.standalone);
      pf.put("fDocumentURI", this.fDocumentURI);
      pf.put("userData", nud);
      pf.put("identifiers", ids);
      pf.put("changes", this.changes);
      pf.put("allowGrammarAccess", this.allowGrammarAccess);
      pf.put("errorChecking", this.errorChecking);
      pf.put("ancestorChecking", this.ancestorChecking);
      pf.put("xmlVersionChanged", this.xmlVersionChanged);
      pf.put("documentNumber", this.documentNumber);
      pf.put("nodeCounter", this.nodeCounter);
      pf.put("nodeTable", nt);
      pf.put("xml11Version", this.xml11Version);
      out.writeFields();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField gf = in.readFields();
      this.docType = (DocumentTypeImpl)gf.get("docType", (Object)null);
      this.docElement = (ElementImpl)gf.get("docElement", (Object)null);
      this.fFreeNLCache = (NodeListCache)gf.get("fFreeNLCache", (Object)null);
      this.encoding = (String)gf.get("encoding", (Object)null);
      this.actualEncoding = (String)gf.get("actualEncoding", (Object)null);
      this.version = (String)gf.get("version", (Object)null);
      this.standalone = gf.get("standalone", false);
      this.fDocumentURI = (String)gf.get("fDocumentURI", (Object)null);
      Hashtable<Node, Hashtable<String, ParentNode.UserDataRecord>> nud = (Hashtable)gf.get("userData", (Object)null);
      Hashtable<String, Node> ids = (Hashtable)gf.get("identifiers", (Object)null);
      this.changes = gf.get("changes", (int)0);
      this.allowGrammarAccess = gf.get("allowGrammarAccess", false);
      this.errorChecking = gf.get("errorChecking", true);
      this.ancestorChecking = gf.get("ancestorChecking", true);
      this.xmlVersionChanged = gf.get("xmlVersionChanged", false);
      this.documentNumber = gf.get("documentNumber", (int)0);
      this.nodeCounter = gf.get("nodeCounter", (int)0);
      Hashtable<Node, Integer> nt = (Hashtable)gf.get("nodeTable", (Object)null);
      this.xml11Version = gf.get("xml11Version", false);
      if (nud != null) {
         this.nodeUserData = new HashMap();
         Iterator var6 = nud.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<Node, Hashtable<String, ParentNode.UserDataRecord>> e = (Map.Entry)var6.next();
            this.nodeUserData.put(e.getKey(), new HashMap((Map)e.getValue()));
         }
      }

      if (ids != null) {
         this.identifiers = new HashMap(ids);
      }

      if (nt != null) {
         this.nodeTable = new HashMap(nt);
      }

   }

   static {
      kidOK[9] = 1410;
      kidOK[11] = kidOK[6] = kidOK[5] = kidOK[1] = 442;
      kidOK[2] = 40;
      kidOK[10] = kidOK[7] = kidOK[8] = kidOK[3] = kidOK[4] = kidOK[12] = 0;
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("docType", DocumentTypeImpl.class), new ObjectStreamField("docElement", ElementImpl.class), new ObjectStreamField("fFreeNLCache", NodeListCache.class), new ObjectStreamField("encoding", String.class), new ObjectStreamField("actualEncoding", String.class), new ObjectStreamField("version", String.class), new ObjectStreamField("standalone", Boolean.TYPE), new ObjectStreamField("fDocumentURI", String.class), new ObjectStreamField("userData", Hashtable.class), new ObjectStreamField("identifiers", Hashtable.class), new ObjectStreamField("changes", Integer.TYPE), new ObjectStreamField("allowGrammarAccess", Boolean.TYPE), new ObjectStreamField("errorChecking", Boolean.TYPE), new ObjectStreamField("ancestorChecking", Boolean.TYPE), new ObjectStreamField("xmlVersionChanged", Boolean.TYPE), new ObjectStreamField("documentNumber", Integer.TYPE), new ObjectStreamField("nodeCounter", Integer.TYPE), new ObjectStreamField("nodeTable", Hashtable.class), new ObjectStreamField("xml11Version", Boolean.TYPE)};
   }
}
