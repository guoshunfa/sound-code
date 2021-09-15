package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

class Dom2SaxAdapter implements ContentHandler, LexicalHandler {
   private final Node _node;
   private final Stack _nodeStk = new Stack();
   private boolean inCDATA;
   private final Document _document;
   private ArrayList unprocessedNamespaces = new ArrayList();

   public final Element getCurrentElement() {
      return (Element)this._nodeStk.peek();
   }

   public Dom2SaxAdapter(Node node) {
      this._node = node;
      this._nodeStk.push(this._node);
      if (node instanceof Document) {
         this._document = (Document)node;
      } else {
         this._document = node.getOwnerDocument();
      }

   }

   public Dom2SaxAdapter() throws ParserConfigurationException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      this._document = factory.newDocumentBuilder().newDocument();
      this._node = this._document;
      this._nodeStk.push(this._document);
   }

   public Node getDOM() {
      return this._node;
   }

   public void startDocument() {
   }

   public void endDocument() {
   }

   public void startElement(String namespace, String localName, String qName, Attributes attrs) {
      Element element = this._document.createElementNS(namespace, qName);
      if (element == null) {
         throw new TxwException("Your DOM provider doesn't support the createElementNS method properly");
      } else {
         int length;
         String uri;
         String qname;
         for(length = 0; length < this.unprocessedNamespaces.size(); length += 2) {
            String prefix = (String)this.unprocessedNamespaces.get(length + 0);
            uri = (String)this.unprocessedNamespaces.get(length + 1);
            if (!"".equals(prefix) && prefix != null) {
               qname = "xmlns:" + prefix;
            } else {
               qname = "xmlns";
            }

            if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", qname)) {
               element.removeAttributeNS("http://www.w3.org/2000/xmlns/", qname);
            }

            element.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, uri);
         }

         this.unprocessedNamespaces.clear();
         length = attrs.getLength();

         for(int i = 0; i < length; ++i) {
            uri = attrs.getURI(i);
            qname = attrs.getValue(i);
            String qname = attrs.getQName(i);
            element.setAttributeNS(uri, qname, qname);
         }

         this.getParent().appendChild(element);
         this._nodeStk.push(element);
      }
   }

   private final Node getParent() {
      return (Node)this._nodeStk.peek();
   }

   public void endElement(String namespace, String localName, String qName) {
      this._nodeStk.pop();
   }

   public void characters(char[] ch, int start, int length) {
      Object text;
      if (this.inCDATA) {
         text = this._document.createCDATASection(new String(ch, start, length));
      } else {
         text = this._document.createTextNode(new String(ch, start, length));
      }

      this.getParent().appendChild((Node)text);
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      this.getParent().appendChild(this._document.createComment(new String(ch, start, length)));
   }

   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   public void processingInstruction(String target, String data) throws SAXException {
      Node node = this._document.createProcessingInstruction(target, data);
      this.getParent().appendChild(node);
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void skippedEntity(String name) {
   }

   public void startPrefixMapping(String prefix, String uri) {
      this.unprocessedNamespaces.add(prefix);
      this.unprocessedNamespaces.add(uri);
   }

   public void endPrefixMapping(String prefix) {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
      this.inCDATA = true;
   }

   public void endCDATA() throws SAXException {
      this.inCDATA = false;
   }
}
