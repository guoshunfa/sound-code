package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.CDATAImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.CommentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.TextImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class SOAPDocumentImpl extends DocumentImpl implements SOAPDocument {
   private static final String XMLNS = "xmlns".intern();
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   SOAPPartImpl enclosingSOAPPart;

   public SOAPDocumentImpl(SOAPPartImpl enclosingDocument) {
      this.enclosingSOAPPart = enclosingDocument;
   }

   public SOAPPartImpl getSOAPPart() {
      if (this.enclosingSOAPPart == null) {
         log.severe("SAAJ0541.soap.fragment.not.bound.to.part");
         throw new RuntimeException("Could not complete operation. Fragment not bound to SOAP part.");
      } else {
         return this.enclosingSOAPPart;
      }
   }

   public SOAPDocumentImpl getDocument() {
      return this;
   }

   public DocumentType getDoctype() {
      return null;
   }

   public DOMImplementation getImplementation() {
      return super.getImplementation();
   }

   public Element getDocumentElement() {
      this.getSOAPPart().doGetDocumentElement();
      return this.doGetDocumentElement();
   }

   protected Element doGetDocumentElement() {
      return super.getDocumentElement();
   }

   public Element createElement(String tagName) throws DOMException {
      return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(tagName), NameImpl.getPrefixFromTagName(tagName), (String)null);
   }

   public DocumentFragment createDocumentFragment() {
      return new SOAPDocumentFragment(this);
   }

   public Text createTextNode(String data) {
      return new TextImpl(this, data);
   }

   public Comment createComment(String data) {
      return new CommentImpl(this, data);
   }

   public CDATASection createCDATASection(String data) throws DOMException {
      return new CDATAImpl(this, data);
   }

   public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
      log.severe("SAAJ0542.soap.proc.instructions.not.allowed.in.docs");
      throw new UnsupportedOperationException("Processing Instructions are not allowed in SOAP documents");
   }

   public Attr createAttribute(String name) throws DOMException {
      boolean isQualifiedName = name.indexOf(":") > 0;
      if (isQualifiedName) {
         String nsUri = null;
         String prefix = name.substring(0, name.indexOf(":"));
         if (XMLNS.equals(prefix)) {
            nsUri = ElementImpl.XMLNS_URI;
            return this.createAttributeNS(nsUri, name);
         }
      }

      return super.createAttribute(name);
   }

   public EntityReference createEntityReference(String name) throws DOMException {
      log.severe("SAAJ0543.soap.entity.refs.not.allowed.in.docs");
      throw new UnsupportedOperationException("Entity References are not allowed in SOAP documents");
   }

   public NodeList getElementsByTagName(String tagname) {
      return super.getElementsByTagName(tagname);
   }

   public Node importNode(Node importedNode, boolean deep) throws DOMException {
      return super.importNode(importedNode, deep);
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
      return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(qualifiedName), NameImpl.getPrefixFromTagName(qualifiedName), namespaceURI);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
      return super.createAttributeNS(namespaceURI, qualifiedName);
   }

   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return super.getElementsByTagNameNS(namespaceURI, localName);
   }

   public Element getElementById(String elementId) {
      return super.getElementById(elementId);
   }

   public Node cloneNode(boolean deep) {
      SOAPPartImpl newSoapPart = this.getSOAPPart().doCloneNode();
      super.cloneNode(newSoapPart.getDocument(), deep);
      return newSoapPart;
   }

   public void cloneNode(SOAPDocumentImpl newdoc, boolean deep) {
      super.cloneNode(newdoc, deep);
   }
}
