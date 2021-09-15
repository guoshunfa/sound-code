package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.NamespaceContextIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ElementImpl extends ElementNSImpl implements SOAPElement, SOAPBodyElement {
   public static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
   public static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
   public static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
   private ElementImpl.AttributeManager encodingStyleAttribute = new ElementImpl.AttributeManager();
   protected QName elementQName;
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
   public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
   public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();

   public ElementImpl(SOAPDocumentImpl ownerDoc, Name name) {
      super(ownerDoc, name.getURI(), name.getQualifiedName(), name.getLocalName());
      this.elementQName = NameImpl.convertToQName(name);
   }

   public ElementImpl(SOAPDocumentImpl ownerDoc, QName name) {
      super(ownerDoc, name.getNamespaceURI(), getQualifiedName(name), name.getLocalPart());
      this.elementQName = name;
   }

   public ElementImpl(SOAPDocumentImpl ownerDoc, String uri, String qualifiedName) {
      super(ownerDoc, uri, qualifiedName);
      this.elementQName = new QName(uri, getLocalPart(qualifiedName), getPrefix(qualifiedName));
   }

   public void ensureNamespaceIsDeclared(String prefix, String uri) {
      String alreadyDeclaredUri = this.getNamespaceURI(prefix);
      if (alreadyDeclaredUri == null || !alreadyDeclaredUri.equals(uri)) {
         try {
            this.addNamespaceDeclaration(prefix, uri);
         } catch (SOAPException var5) {
         }
      }

   }

   public Document getOwnerDocument() {
      Document doc = super.getOwnerDocument();
      return (Document)(doc instanceof SOAPDocument ? ((SOAPDocument)doc).getDocument() : doc);
   }

   public SOAPElement addChildElement(Name name) throws SOAPException {
      return this.addElement(name);
   }

   public SOAPElement addChildElement(QName qname) throws SOAPException {
      return this.addElement(qname);
   }

   public SOAPElement addChildElement(String localName) throws SOAPException {
      String nsUri = this.getNamespaceURI("");
      Name name = nsUri != null && !nsUri.isEmpty() ? NameImpl.createFromQualifiedName(localName, nsUri) : NameImpl.createFromUnqualifiedName(localName);
      return this.addChildElement((Name)name);
   }

   public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
      String uri = this.getNamespaceURI(prefix);
      if (uri == null) {
         log.log(Level.SEVERE, (String)"SAAJ0101.impl.parent.of.body.elem.mustbe.body", (Object[])(new String[]{prefix}));
         throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
      } else {
         return this.addChildElement(localName, prefix, uri);
      }
   }

   public String getNamespaceURI(String prefix) {
      if ("xmlns".equals(prefix)) {
         return XMLNS_URI;
      } else if ("xml".equals(prefix)) {
         return XML_URI;
      } else {
         Object currentAncestor;
         if ("".equals(prefix)) {
            for(currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = ((Node)currentAncestor).getParentNode()) {
               if (currentAncestor instanceof ElementImpl) {
                  QName name = ((ElementImpl)currentAncestor).getElementQName();
                  if (((Element)currentAncestor).hasAttributeNS(XMLNS_URI, "xmlns")) {
                     String uri = ((Element)currentAncestor).getAttributeNS(XMLNS_URI, "xmlns");
                     if ("".equals(uri)) {
                        return null;
                     }

                     return uri;
                  }
               }
            }
         } else if (prefix != null) {
            for(currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = ((Node)currentAncestor).getParentNode()) {
               if (((Element)currentAncestor).hasAttributeNS(XMLNS_URI, prefix)) {
                  return ((Element)currentAncestor).getAttributeNS(XMLNS_URI, prefix);
               }
            }
         }

         return null;
      }
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      ElementImpl copy = new ElementImpl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
      return replaceElementWithSOAPElement(this, copy);
   }

   public QName createQName(String localName, String prefix) throws SOAPException {
      String uri = this.getNamespaceURI(prefix);
      if (uri == null) {
         log.log(Level.SEVERE, "SAAJ0102.impl.cannot.locate.ns", new Object[]{prefix});
         throw new SOAPException("Unable to locate namespace for prefix " + prefix);
      } else {
         return new QName(uri, localName, prefix);
      }
   }

   public String getNamespacePrefix(String uri) {
      NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();

      while(eachNamespace.hasNext()) {
         Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
         if (namespaceDecl.getNodeValue().equals(uri)) {
            String candidatePrefix = namespaceDecl.getLocalName();
            if ("xmlns".equals(candidatePrefix)) {
               return "";
            }

            return candidatePrefix;
         }
      }

      for(Object currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = ((Node)currentAncestor).getParentNode()) {
         if (uri.equals(((Node)currentAncestor).getNamespaceURI())) {
            return ((Node)currentAncestor).getPrefix();
         }
      }

      return null;
   }

   protected Attr getNamespaceAttr(String prefix) {
      NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();
      if (!"".equals(prefix)) {
         prefix = ":" + prefix;
      }

      while(eachNamespace.hasNext()) {
         Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
         if (!"".equals(prefix)) {
            if (namespaceDecl.getNodeName().endsWith(prefix)) {
               return namespaceDecl;
            }
         } else if (namespaceDecl.getNodeName().equals("xmlns")) {
            return namespaceDecl;
         }
      }

      return null;
   }

   public NamespaceContextIterator getNamespaceContextNodes() {
      return this.getNamespaceContextNodes(true);
   }

   public NamespaceContextIterator getNamespaceContextNodes(boolean traverseStack) {
      return new NamespaceContextIterator(this, traverseStack);
   }

   public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
      SOAPElement newElement = this.createElement((Name)NameImpl.create(localName, prefix, uri));
      this.addNode(newElement);
      return this.convertToSoapElement(newElement);
   }

   public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
      String elementURI = element.getElementName().getURI();
      String localName = element.getLocalName();
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(elementURI) || "http://www.w3.org/2003/05/soap-envelope".equals(elementURI)) {
         if ("Envelope".equalsIgnoreCase(localName) || "Header".equalsIgnoreCase(localName) || "Body".equalsIgnoreCase(localName)) {
            log.severe("SAAJ0103.impl.cannot.add.fragements");
            throw new SOAPExceptionImpl("Cannot add fragments which contain elements which are in the SOAP namespace");
         }

         if ("Fault".equalsIgnoreCase(localName) && !"Body".equalsIgnoreCase(this.getLocalName())) {
            log.severe("SAAJ0154.impl.adding.fault.to.nonbody");
            throw new SOAPExceptionImpl("Cannot add a SOAPFault as a child of " + this.getLocalName());
         }

         if ("Detail".equalsIgnoreCase(localName) && !"Fault".equalsIgnoreCase(this.getLocalName())) {
            log.severe("SAAJ0155.impl.adding.detail.nonfault");
            throw new SOAPExceptionImpl("Cannot add a Detail as a child of " + this.getLocalName());
         }

         if ("Fault".equalsIgnoreCase(localName)) {
            if (!elementURI.equals(this.getElementName().getURI())) {
               log.severe("SAAJ0158.impl.version.mismatch.fault");
               throw new SOAPExceptionImpl("SOAP Version mismatch encountered when trying to add SOAPFault to SOAPBody");
            }

            Iterator it = this.getChildElements();
            if (it.hasNext()) {
               log.severe("SAAJ0156.impl.adding.fault.error");
               throw new SOAPExceptionImpl("Cannot add SOAPFault as a child of a non-Empty SOAPBody");
            }
         }
      }

      String encodingStyle = element.getEncodingStyle();
      ElementImpl importedElement = (ElementImpl)this.importElement(element);
      this.addNode(importedElement);
      if (encodingStyle != null) {
         importedElement.setEncodingStyle(encodingStyle);
      }

      return this.convertToSoapElement(importedElement);
   }

   protected Element importElement(Element element) {
      Document document = this.getOwnerDocument();
      Document oldDocument = element.getOwnerDocument();
      return !oldDocument.equals(document) ? (Element)document.importNode(element, true) : element;
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      SOAPElement newElement = this.createElement(name);
      this.addNode(newElement);
      return newElement;
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      SOAPElement newElement = this.createElement(name);
      this.addNode(newElement);
      return newElement;
   }

   protected SOAPElement createElement(Name name) {
      return this.isNamespaceQualified(name) ? (SOAPElement)this.getOwnerDocument().createElementNS(name.getURI(), name.getQualifiedName()) : (SOAPElement)this.getOwnerDocument().createElement(name.getQualifiedName());
   }

   protected SOAPElement createElement(QName name) {
      return this.isNamespaceQualified(name) ? (SOAPElement)this.getOwnerDocument().createElementNS(name.getNamespaceURI(), getQualifiedName(name)) : (SOAPElement)this.getOwnerDocument().createElement(getQualifiedName(name));
   }

   protected void addNode(Node newElement) throws SOAPException {
      this.insertBefore(newElement, (Node)null);
      if (!(this.getOwnerDocument() instanceof DocumentFragment)) {
         if (newElement instanceof ElementImpl) {
            ElementImpl element = (ElementImpl)newElement;
            QName elementName = element.getElementQName();
            if (!"".equals(elementName.getNamespaceURI())) {
               element.ensureNamespaceIsDeclared(elementName.getPrefix(), elementName.getNamespaceURI());
            }
         }

      }
   }

   protected SOAPElement findChild(NameImpl name) {
      Iterator eachChild = this.getChildElementNodes();

      SOAPElement child;
      do {
         if (!eachChild.hasNext()) {
            return null;
         }

         child = (SOAPElement)eachChild.next();
      } while(!child.getElementName().equals(name));

      return child;
   }

   public SOAPElement addTextNode(String text) throws SOAPException {
      return !text.startsWith("<![CDATA[") && !text.startsWith("<![cdata[") ? this.addText(text) : this.addCDATA(text.substring("<![CDATA[".length(), text.length() - 3));
   }

   protected SOAPElement addCDATA(String text) throws SOAPException {
      Text cdata = this.getOwnerDocument().createCDATASection(text);
      this.addNode(cdata);
      return this;
   }

   protected SOAPElement addText(String text) throws SOAPException {
      Text textNode = this.getOwnerDocument().createTextNode(text);
      this.addNode(textNode);
      return this;
   }

   public SOAPElement addAttribute(Name name, String value) throws SOAPException {
      this.addAttributeBare(name, value);
      if (!"".equals(name.getURI())) {
         this.ensureNamespaceIsDeclared(name.getPrefix(), name.getURI());
      }

      return this;
   }

   public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
      this.addAttributeBare(qname, value);
      if (!"".equals(qname.getNamespaceURI())) {
         this.ensureNamespaceIsDeclared(qname.getPrefix(), qname.getNamespaceURI());
      }

      return this;
   }

   private void addAttributeBare(Name name, String value) {
      this.addAttributeBare(name.getURI(), name.getPrefix(), name.getQualifiedName(), value);
   }

   private void addAttributeBare(QName name, String value) {
      this.addAttributeBare(name.getNamespaceURI(), name.getPrefix(), getQualifiedName(name), value);
   }

   private void addAttributeBare(String uri, String prefix, String qualifiedName, String value) {
      uri = uri.length() == 0 ? null : uri;
      if (qualifiedName.equals("xmlns")) {
         uri = XMLNS_URI;
      }

      if (uri == null) {
         this.setAttribute(qualifiedName, value);
      } else {
         this.setAttributeNS(uri, qualifiedName, value);
      }

   }

   public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
      if (prefix.length() > 0) {
         this.setAttributeNS(XMLNS_URI, "xmlns:" + prefix, uri);
      } else {
         this.setAttributeNS(XMLNS_URI, "xmlns", uri);
      }

      return this;
   }

   public String getAttributeValue(Name name) {
      return getAttributeValueFrom(this, name);
   }

   public String getAttributeValue(QName qname) {
      return getAttributeValueFrom(this, qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), getQualifiedName(qname));
   }

   public Iterator getAllAttributes() {
      Iterator i = getAllAttributesFrom(this);
      ArrayList list = new ArrayList();

      while(i.hasNext()) {
         Name name = (Name)i.next();
         if (!"xmlns".equalsIgnoreCase(name.getPrefix())) {
            list.add(name);
         }
      }

      return list.iterator();
   }

   public Iterator getAllAttributesAsQNames() {
      Iterator i = getAllAttributesFrom(this);
      ArrayList list = new ArrayList();

      while(i.hasNext()) {
         Name name = (Name)i.next();
         if (!"xmlns".equalsIgnoreCase(name.getPrefix())) {
            list.add(NameImpl.convertToQName(name));
         }
      }

      return list.iterator();
   }

   public Iterator getNamespacePrefixes() {
      return this.doGetNamespacePrefixes(false);
   }

   public Iterator getVisibleNamespacePrefixes() {
      return this.doGetNamespacePrefixes(true);
   }

   protected Iterator doGetNamespacePrefixes(final boolean deep) {
      return new Iterator() {
         String next = null;
         String last = null;
         NamespaceContextIterator eachNamespace = ElementImpl.this.getNamespaceContextNodes(deep);

         void findNext() {
            while(this.next == null && this.eachNamespace.hasNext()) {
               String attributeKey = this.eachNamespace.nextNamespaceAttr().getNodeName();
               if (attributeKey.startsWith("xmlns:")) {
                  this.next = attributeKey.substring("xmlns:".length());
               }
            }

         }

         public boolean hasNext() {
            this.findNext();
            return this.next != null;
         }

         public Object next() {
            this.findNext();
            if (this.next == null) {
               throw new NoSuchElementException();
            } else {
               this.last = this.next;
               this.next = null;
               return this.last;
            }
         }

         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               this.eachNamespace.remove();
               this.next = null;
               this.last = null;
            }
         }
      };
   }

   public Name getElementName() {
      return NameImpl.convertToName(this.elementQName);
   }

   public QName getElementQName() {
      return this.elementQName;
   }

   public boolean removeAttribute(Name name) {
      return this.removeAttribute(name.getURI(), name.getLocalName());
   }

   public boolean removeAttribute(QName name) {
      return this.removeAttribute(name.getNamespaceURI(), name.getLocalPart());
   }

   private boolean removeAttribute(String uri, String localName) {
      String nonzeroLengthUri = uri != null && uri.length() != 0 ? uri : null;
      Attr attribute = this.getAttributeNodeNS(nonzeroLengthUri, localName);
      if (attribute == null) {
         return false;
      } else {
         this.removeAttributeNode(attribute);
         return true;
      }
   }

   public boolean removeNamespaceDeclaration(String prefix) {
      Attr declaration = this.getNamespaceAttr(prefix);
      if (declaration == null) {
         return false;
      } else {
         try {
            this.removeAttributeNode(declaration);
         } catch (DOMException var4) {
         }

         return true;
      }
   }

   public Iterator getChildElements() {
      return getChildElementsFrom(this);
   }

   protected SOAPElement convertToSoapElement(Element element) {
      return element instanceof SOAPElement ? (SOAPElement)element : replaceElementWithSOAPElement(element, (ElementImpl)this.createElement(NameImpl.copyElementName(element)));
   }

   protected static SOAPElement replaceElementWithSOAPElement(Element element, ElementImpl copy) {
      Iterator eachAttribute = getAllAttributesFrom(element);

      while(eachAttribute.hasNext()) {
         Name name = (Name)eachAttribute.next();
         copy.addAttributeBare(name, getAttributeValueFrom(element, name));
      }

      Iterator eachChild = getChildElementsFrom(element);

      Node parent;
      while(eachChild.hasNext()) {
         parent = (Node)eachChild.next();
         copy.insertBefore(parent, (Node)null);
      }

      parent = element.getParentNode();
      if (parent != null) {
         parent.replaceChild(copy, element);
      }

      return copy;
   }

   protected Iterator getChildElementNodes() {
      return new Iterator() {
         Iterator eachNode = ElementImpl.this.getChildElements();
         Node next = null;
         Node last = null;

         public boolean hasNext() {
            if (this.next == null) {
               while(this.eachNode.hasNext()) {
                  Node node = (Node)this.eachNode.next();
                  if (node instanceof SOAPElement) {
                     this.next = node;
                     break;
                  }
               }
            }

            return this.next != null;
         }

         public Object next() {
            if (this.hasNext()) {
               this.last = this.next;
               this.next = null;
               return this.last;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               Node target = this.last;
               this.last = null;
               ElementImpl.this.removeChild(target);
            }
         }
      };
   }

   public Iterator getChildElements(Name name) {
      return this.getChildElements(name.getURI(), name.getLocalName());
   }

   public Iterator getChildElements(QName qname) {
      return this.getChildElements(qname.getNamespaceURI(), qname.getLocalPart());
   }

   private Iterator getChildElements(final String nameUri, final String nameLocal) {
      return new Iterator() {
         Iterator eachElement = ElementImpl.this.getChildElementNodes();
         Node next = null;
         Node last = null;

         public boolean hasNext() {
            if (this.next == null) {
               while(this.eachElement.hasNext()) {
                  Node element = (Node)this.eachElement.next();
                  String elementUri = element.getNamespaceURI();
                  elementUri = elementUri == null ? "" : elementUri;
                  String elementName = element.getLocalName();
                  if (elementUri.equals(nameUri) && elementName.equals(nameLocal)) {
                     this.next = element;
                     break;
                  }
               }
            }

            return this.next != null;
         }

         public Object next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.last = this.next;
               this.next = null;
               return this.last;
            }
         }

         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               Node target = this.last;
               this.last = null;
               ElementImpl.this.removeChild(target);
            }
         }
      };
   }

   public void removeContents() {
      Node temp;
      for(Node currentChild = this.getFirstChild(); currentChild != null; currentChild = temp) {
         temp = currentChild.getNextSibling();
         if (currentChild instanceof javax.xml.soap.Node) {
            ((javax.xml.soap.Node)currentChild).detachNode();
         } else {
            Node parent = currentChild.getParentNode();
            if (parent != null) {
               parent.removeChild(currentChild);
            }
         }
      }

   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      if (!"".equals(encodingStyle)) {
         try {
            new URI(encodingStyle);
         } catch (URISyntaxException var3) {
            log.log(Level.SEVERE, (String)"SAAJ0105.impl.encoding.style.mustbe.valid.URI", (Object[])(new String[]{encodingStyle}));
            throw new IllegalArgumentException("Encoding style (" + encodingStyle + ") should be a valid URI");
         }
      }

      this.encodingStyleAttribute.setValue(encodingStyle);
      this.tryToFindEncodingStyleAttributeName();
   }

   public String getEncodingStyle() {
      String encodingStyle = this.encodingStyleAttribute.getValue();
      if (encodingStyle != null) {
         return encodingStyle;
      } else {
         String soapNamespace = this.getSOAPNamespace();
         if (soapNamespace != null) {
            Attr attr = this.getAttributeNodeNS(soapNamespace, "encodingStyle");
            if (attr != null) {
               encodingStyle = attr.getValue();

               try {
                  this.setEncodingStyle(encodingStyle);
               } catch (SOAPException var5) {
               }

               return encodingStyle;
            }
         }

         return null;
      }
   }

   public String getValue() {
      javax.xml.soap.Node valueNode = this.getValueNode();
      return valueNode == null ? null : valueNode.getValue();
   }

   public void setValue(String value) {
      Node valueNode = this.getValueNodeStrict();
      if (valueNode != null) {
         valueNode.setNodeValue(value);
      } else {
         try {
            this.addTextNode(value);
         } catch (SOAPException var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }

   }

   protected Node getValueNodeStrict() {
      Node node = this.getFirstChild();
      if (node != null) {
         if (node.getNextSibling() == null && node.getNodeType() == 3) {
            return node;
         } else {
            log.severe("SAAJ0107.impl.elem.child.not.single.text");
            throw new IllegalStateException();
         }
      } else {
         return null;
      }
   }

   protected javax.xml.soap.Node getValueNode() {
      Iterator i = this.getChildElements();

      javax.xml.soap.Node n;
      do {
         if (!i.hasNext()) {
            return null;
         }

         n = (javax.xml.soap.Node)i.next();
      } while(n.getNodeType() != 3 && n.getNodeType() != 4);

      this.normalize();
      return n;
   }

   public void setParentElement(SOAPElement element) throws SOAPException {
      if (element == null) {
         log.severe("SAAJ0106.impl.no.null.to.parent.elem");
         throw new SOAPException("Cannot pass NULL to setParentElement");
      } else {
         element.addChildElement((SOAPElement)this);
         this.findEncodingStyleAttributeName();
      }
   }

   protected void findEncodingStyleAttributeName() throws SOAPException {
      String soapNamespace = this.getSOAPNamespace();
      if (soapNamespace != null) {
         String soapNamespacePrefix = this.getNamespacePrefix(soapNamespace);
         if (soapNamespacePrefix != null) {
            this.setEncodingStyleNamespace(soapNamespace, soapNamespacePrefix);
         }
      }

   }

   protected void setEncodingStyleNamespace(String soapNamespace, String soapNamespacePrefix) throws SOAPException {
      Name encodingStyleAttributeName = NameImpl.create("encodingStyle", soapNamespacePrefix, soapNamespace);
      this.encodingStyleAttribute.setName(encodingStyleAttributeName);
   }

   public SOAPElement getParentElement() {
      Node parentNode = this.getParentNode();
      return parentNode instanceof SOAPDocument ? null : (SOAPElement)parentNode;
   }

   protected String getSOAPNamespace() {
      String soapNamespace = null;

      for(Object antecedent = this; antecedent != null; antecedent = ((SOAPElement)antecedent).getParentElement()) {
         Name antecedentName = ((SOAPElement)antecedent).getElementName();
         String antecedentNamespace = antecedentName.getURI();
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(antecedentNamespace) || "http://www.w3.org/2003/05/soap-envelope".equals(antecedentNamespace)) {
            soapNamespace = antecedentNamespace;
            break;
         }
      }

      return soapNamespace;
   }

   public void detachNode() {
      Node parent = this.getParentNode();
      if (parent != null) {
         parent.removeChild(this);
      }

      this.encodingStyleAttribute.clearNameAndValue();
   }

   public void tryToFindEncodingStyleAttributeName() {
      try {
         this.findEncodingStyleAttributeName();
      } catch (SOAPException var2) {
      }

   }

   public void recycleNode() {
      this.detachNode();
   }

   protected static Attr getNamespaceAttrFrom(Element element, String prefix) {
      NamespaceContextIterator eachNamespace = new NamespaceContextIterator(element);

      Attr namespaceDecl;
      String declaredPrefix;
      do {
         if (!eachNamespace.hasNext()) {
            return null;
         }

         namespaceDecl = eachNamespace.nextNamespaceAttr();
         declaredPrefix = NameImpl.getLocalNameFromTagName(namespaceDecl.getNodeName());
      } while(!declaredPrefix.equals(prefix));

      return namespaceDecl;
   }

   protected static Iterator getAllAttributesFrom(Element element) {
      final NamedNodeMap attributes = element.getAttributes();
      return new Iterator() {
         int attributesLength = attributes.getLength();
         int attributeIndex = 0;
         String currentName;

         public boolean hasNext() {
            return this.attributeIndex < this.attributesLength;
         }

         public Object next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Node current = attributes.item(this.attributeIndex++);
               this.currentName = current.getNodeName();
               String prefix = NameImpl.getPrefixFromTagName(this.currentName);
               if (prefix.length() == 0) {
                  return NameImpl.createFromUnqualifiedName(this.currentName);
               } else {
                  Name attributeName = NameImpl.createFromQualifiedName(this.currentName, current.getNamespaceURI());
                  return attributeName;
               }
            }
         }

         public void remove() {
            if (this.currentName == null) {
               throw new IllegalStateException();
            } else {
               attributes.removeNamedItem(this.currentName);
            }
         }
      };
   }

   protected static String getAttributeValueFrom(Element element, Name name) {
      return getAttributeValueFrom(element, name.getURI(), name.getLocalName(), name.getPrefix(), name.getQualifiedName());
   }

   private static String getAttributeValueFrom(Element element, String uri, String localName, String prefix, String qualifiedName) {
      String nonzeroLengthUri = uri != null && uri.length() != 0 ? uri : null;
      boolean mustUseGetAttributeNodeNS = nonzeroLengthUri != null;
      if (mustUseGetAttributeNodeNS) {
         if (!element.hasAttributeNS(uri, localName)) {
            return null;
         } else {
            String attrValue = element.getAttributeNS(nonzeroLengthUri, localName);
            return attrValue;
         }
      } else {
         Attr attribute = null;
         attribute = element.getAttributeNode(qualifiedName);
         return attribute == null ? null : attribute.getValue();
      }
   }

   protected static Iterator getChildElementsFrom(final Element element) {
      return new Iterator() {
         Node next = element.getFirstChild();
         Node nextNext = null;
         Node last = null;

         public boolean hasNext() {
            if (this.next != null) {
               return true;
            } else {
               if (this.next == null && this.nextNext != null) {
                  this.next = this.nextNext;
               }

               return this.next != null;
            }
         }

         public Object next() {
            if (this.hasNext()) {
               this.last = this.next;
               this.next = null;
               if (element instanceof ElementImpl && this.last instanceof Element) {
                  this.last = ((ElementImpl)element).convertToSoapElement((Element)this.last);
               }

               this.nextNext = this.last.getNextSibling();
               return this.last;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               Node target = this.last;
               this.last = null;
               element.removeChild(target);
            }
         }
      };
   }

   public static String getQualifiedName(QName name) {
      String prefix = name.getPrefix();
      String localName = name.getLocalPart();
      String qualifiedName = null;
      if (prefix != null && prefix.length() > 0) {
         qualifiedName = prefix + ":" + localName;
      } else {
         qualifiedName = localName;
      }

      return qualifiedName;
   }

   public static String getLocalPart(String qualifiedName) {
      if (qualifiedName == null) {
         throw new IllegalArgumentException("Cannot get local name for a \"null\" qualified name");
      } else {
         int index = qualifiedName.indexOf(58);
         return index < 0 ? qualifiedName : qualifiedName.substring(index + 1);
      }
   }

   public static String getPrefix(String qualifiedName) {
      if (qualifiedName == null) {
         throw new IllegalArgumentException("Cannot get prefix for a  \"null\" qualified name");
      } else {
         int index = qualifiedName.indexOf(58);
         return index < 0 ? "" : qualifiedName.substring(0, index);
      }
   }

   protected boolean isNamespaceQualified(Name name) {
      return !"".equals(name.getURI());
   }

   protected boolean isNamespaceQualified(QName name) {
      return !"".equals(name.getNamespaceURI());
   }

   public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
      int index = qualifiedName.indexOf(58);
      String localName;
      if (index < 0) {
         localName = qualifiedName;
      } else {
         localName = qualifiedName.substring(index + 1);
      }

      super.setAttributeNS(namespaceURI, qualifiedName, value);
      String tmpURI = this.getNamespaceURI();
      boolean isIDNS = false;
      if (tmpURI != null && (tmpURI.equals(DSIG_NS) || tmpURI.equals(XENC_NS))) {
         isIDNS = true;
      }

      if (localName.equals("Id")) {
         if (namespaceURI != null && !namespaceURI.equals("")) {
            if (isIDNS || WSU_NS.equals(namespaceURI)) {
               this.setIdAttributeNS(namespaceURI, localName, true);
            }
         } else {
            this.setIdAttribute(localName, true);
         }
      }

   }

   class AttributeManager {
      Name attributeName = null;
      String attributeValue = null;

      public void setName(Name newName) throws SOAPException {
         this.clearAttribute();
         this.attributeName = newName;
         this.reconcileAttribute();
      }

      public void clearName() {
         this.clearAttribute();
         this.attributeName = null;
      }

      public void setValue(String value) throws SOAPException {
         this.attributeValue = value;
         this.reconcileAttribute();
      }

      public Name getName() {
         return this.attributeName;
      }

      public String getValue() {
         return this.attributeValue;
      }

      public void clearNameAndValue() {
         this.attributeName = null;
         this.attributeValue = null;
      }

      private void reconcileAttribute() throws SOAPException {
         if (this.attributeName != null) {
            ElementImpl.this.removeAttribute(this.attributeName);
            if (this.attributeValue != null) {
               ElementImpl.this.addAttribute(this.attributeName, this.attributeValue);
            }
         }

      }

      private void clearAttribute() {
         if (this.attributeName != null) {
            ElementImpl.this.removeAttribute(this.attributeName);
         }

      }
   }
}
