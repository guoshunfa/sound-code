package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

public class SAX2DOM implements ContentHandler, LexicalHandler, Constants {
   private Node _root;
   private Document _document;
   private Node _nextSibling;
   private Stack _nodeStk;
   private Vector _namespaceDecls;
   private Node _lastSibling;
   private Locator locator;
   private boolean needToSetDocumentInfo;
   private StringBuilder _textBuffer;
   private Node _nextSiblingCache;
   private DocumentBuilderFactory _factory;
   private boolean _internal;

   public SAX2DOM(boolean overrideDefaultParser) throws ParserConfigurationException {
      this._root = null;
      this._document = null;
      this._nextSibling = null;
      this._nodeStk = new Stack();
      this._namespaceDecls = null;
      this._lastSibling = null;
      this.locator = null;
      this.needToSetDocumentInfo = true;
      this._textBuffer = new StringBuilder();
      this._nextSiblingCache = null;
      this._internal = true;
      this._document = this.createDocument(overrideDefaultParser);
      this._root = this._document;
   }

   public SAX2DOM(Node root, Node nextSibling, boolean overrideDefaultParser) throws ParserConfigurationException {
      this._root = null;
      this._document = null;
      this._nextSibling = null;
      this._nodeStk = new Stack();
      this._namespaceDecls = null;
      this._lastSibling = null;
      this.locator = null;
      this.needToSetDocumentInfo = true;
      this._textBuffer = new StringBuilder();
      this._nextSiblingCache = null;
      this._internal = true;
      this._root = root;
      if (root instanceof Document) {
         this._document = (Document)root;
      } else if (root != null) {
         this._document = root.getOwnerDocument();
      } else {
         this._document = this.createDocument(overrideDefaultParser);
         this._root = this._document;
      }

      this._nextSibling = nextSibling;
   }

   public SAX2DOM(Node root, boolean overrideDefaultParser) throws ParserConfigurationException {
      this(root, (Node)null, overrideDefaultParser);
   }

   public Node getDOM() {
      return this._root;
   }

   public void characters(char[] ch, int start, int length) {
      if (length != 0) {
         Node last = (Node)this._nodeStk.peek();
         if (last != this._document) {
            this._nextSiblingCache = this._nextSibling;
            this._textBuffer.append(ch, start, length);
         }

      }
   }

   private void appendTextNode() {
      if (this._textBuffer.length() > 0) {
         Node last = (Node)this._nodeStk.peek();
         if (last == this._root && this._nextSiblingCache != null) {
            this._lastSibling = last.insertBefore(this._document.createTextNode(this._textBuffer.toString()), this._nextSiblingCache);
         } else {
            this._lastSibling = last.appendChild(this._document.createTextNode(this._textBuffer.toString()));
         }

         this._textBuffer.setLength(0);
      }

   }

   public void startDocument() {
      this._nodeStk.push(this._root);
   }

   public void endDocument() {
      this._nodeStk.pop();
   }

   private void setDocumentInfo() {
      if (this.locator != null) {
         try {
            this._document.setXmlVersion(((Locator2)this.locator).getXMLVersion());
         } catch (ClassCastException var2) {
         }

      }
   }

   public void startElement(String namespace, String localName, String qName, Attributes attrs) {
      this.appendTextNode();
      if (this.needToSetDocumentInfo) {
         this.setDocumentInfo();
         this.needToSetDocumentInfo = false;
      }

      Element tmp = this._document.createElementNS(namespace, qName);
      int nattrs;
      int i;
      String attQName;
      if (this._namespaceDecls != null) {
         nattrs = this._namespaceDecls.size();

         for(i = 0; i < nattrs; ++i) {
            attQName = (String)this._namespaceDecls.elementAt(i++);
            if (attQName != null && !attQName.equals("")) {
               tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + attQName, (String)this._namespaceDecls.elementAt(i));
            } else {
               tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", (String)this._namespaceDecls.elementAt(i));
            }
         }

         this._namespaceDecls.clear();
      }

      nattrs = attrs.getLength();

      for(i = 0; i < nattrs; ++i) {
         attQName = attrs.getQName(i);
         String attURI = attrs.getURI(i);
         if (attrs.getLocalName(i).equals("")) {
            tmp.setAttribute(attQName, attrs.getValue(i));
            if (attrs.getType(i).equals("ID")) {
               tmp.setIdAttribute(attQName, true);
            }
         } else {
            tmp.setAttributeNS(attURI, attQName, attrs.getValue(i));
            if (attrs.getType(i).equals("ID")) {
               tmp.setIdAttributeNS(attURI, attrs.getLocalName(i), true);
            }
         }
      }

      Node last = (Node)this._nodeStk.peek();
      if (last == this._root && this._nextSibling != null) {
         last.insertBefore(tmp, this._nextSibling);
      } else {
         last.appendChild(tmp);
      }

      this._nodeStk.push(tmp);
      this._lastSibling = null;
   }

   public void endElement(String namespace, String localName, String qName) {
      this.appendTextNode();
      this._nodeStk.pop();
      this._lastSibling = null;
   }

   public void startPrefixMapping(String prefix, String uri) {
      if (this._namespaceDecls == null) {
         this._namespaceDecls = new Vector(2);
      }

      this._namespaceDecls.addElement(prefix);
      this._namespaceDecls.addElement(uri);
   }

   public void endPrefixMapping(String prefix) {
   }

   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   public void processingInstruction(String target, String data) {
      this.appendTextNode();
      Node last = (Node)this._nodeStk.peek();
      ProcessingInstruction pi = this._document.createProcessingInstruction(target, data);
      if (pi != null) {
         if (last == this._root && this._nextSibling != null) {
            last.insertBefore(pi, this._nextSibling);
         } else {
            last.appendChild(pi);
         }

         this._lastSibling = pi;
      }

   }

   public void setDocumentLocator(Locator locator) {
      this.locator = locator;
   }

   public void skippedEntity(String name) {
   }

   public void comment(char[] ch, int start, int length) {
      this.appendTextNode();
      Node last = (Node)this._nodeStk.peek();
      Comment comment = this._document.createComment(new String(ch, start, length));
      if (comment != null) {
         if (last == this._root && this._nextSibling != null) {
            last.insertBefore(comment, this._nextSibling);
         } else {
            last.appendChild(comment);
         }

         this._lastSibling = comment;
      }

   }

   public void startCDATA() {
   }

   public void endCDATA() {
   }

   public void startEntity(String name) {
   }

   public void endDTD() {
   }

   public void endEntity(String name) {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   private Document createDocument(boolean overrideDefaultParser) throws ParserConfigurationException {
      if (this._factory == null) {
         this._factory = JdkXmlUtils.getDOMFactory(overrideDefaultParser);
         this._internal = true;
         if (!(this._factory instanceof DocumentBuilderFactoryImpl)) {
            this._internal = false;
         }
      }

      Document doc;
      if (this._internal) {
         doc = this._factory.newDocumentBuilder().newDocument();
      } else {
         Class var3 = SAX2DOM.class;
         synchronized(SAX2DOM.class) {
            doc = this._factory.newDocumentBuilder().newDocument();
         }
      }

      return doc;
   }
}
