package com.sun.xml.internal.bind.marshaller;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2DOMEx implements ContentHandler {
   private Node node;
   private boolean isConsolidate;
   protected final Stack<Node> nodeStack;
   private final FinalArrayList<String> unprocessedNamespaces;
   protected final Document document;

   public SAX2DOMEx(Node node) {
      this(node, false);
   }

   public SAX2DOMEx(Node node, boolean isConsolidate) {
      this.node = null;
      this.nodeStack = new Stack();
      this.unprocessedNamespaces = new FinalArrayList();
      this.node = node;
      this.isConsolidate = isConsolidate;
      this.nodeStack.push(this.node);
      if (node instanceof Document) {
         this.document = (Document)node;
      } else {
         this.document = node.getOwnerDocument();
      }

   }

   public SAX2DOMEx(DocumentBuilderFactory f) throws ParserConfigurationException {
      this.node = null;
      this.nodeStack = new Stack();
      this.unprocessedNamespaces = new FinalArrayList();
      f.setValidating(false);
      this.document = f.newDocumentBuilder().newDocument();
      this.node = this.document;
      this.nodeStack.push(this.document);
   }

   /** @deprecated */
   public SAX2DOMEx() throws ParserConfigurationException {
      this.node = null;
      this.nodeStack = new Stack();
      this.unprocessedNamespaces = new FinalArrayList();
      DocumentBuilderFactory factory = XmlFactory.createDocumentBuilderFactory(false);
      factory.setValidating(false);
      this.document = factory.newDocumentBuilder().newDocument();
      this.node = this.document;
      this.nodeStack.push(this.document);
   }

   public final Element getCurrentElement() {
      return (Element)this.nodeStack.peek();
   }

   public Node getDOM() {
      return this.node;
   }

   public void startDocument() {
   }

   public void endDocument() {
   }

   protected void namespace(Element element, String prefix, String uri) {
      String qname;
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

   public void startElement(String namespace, String localName, String qName, Attributes attrs) {
      Node parent = (Node)this.nodeStack.peek();
      Element element = this.document.createElementNS(namespace, qName);
      if (element == null) {
         throw new AssertionError(Messages.format("SAX2DOMEx.DomImplDoesntSupportCreateElementNs", this.document.getClass().getName(), Which.which(this.document.getClass())));
      } else {
         int length;
         String namespaceuri;
         for(length = 0; length < this.unprocessedNamespaces.size(); length += 2) {
            String prefix = (String)this.unprocessedNamespaces.get(length);
            namespaceuri = (String)this.unprocessedNamespaces.get(length + 1);
            this.namespace(element, prefix, namespaceuri);
         }

         this.unprocessedNamespaces.clear();
         if (attrs != null) {
            length = attrs.getLength();

            for(int i = 0; i < length; ++i) {
               namespaceuri = attrs.getURI(i);
               String value = attrs.getValue(i);
               String qname = attrs.getQName(i);
               element.setAttributeNS(namespaceuri, qname, value);
            }
         }

         parent.appendChild(element);
         this.nodeStack.push(element);
      }
   }

   public void endElement(String namespace, String localName, String qName) {
      this.nodeStack.pop();
   }

   public void characters(char[] ch, int start, int length) {
      this.characters(new String(ch, start, length));
   }

   protected Text characters(String s) {
      Node parent = (Node)this.nodeStack.peek();
      Node lastChild = parent.getLastChild();
      Text text;
      if (this.isConsolidate && lastChild != null && lastChild.getNodeType() == 3) {
         text = (Text)lastChild;
         text.appendData(s);
      } else {
         text = this.document.createTextNode(s);
         parent.appendChild(text);
      }

      return text;
   }

   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   public void processingInstruction(String target, String data) throws SAXException {
      Node parent = (Node)this.nodeStack.peek();
      Node n = this.document.createProcessingInstruction(target, data);
      parent.appendChild(n);
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
}
