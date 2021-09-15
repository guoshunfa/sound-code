package com.sun.xml.internal.stream.writers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.helpers.NamespaceSupport;

public class XMLDOMWriterImpl implements XMLStreamWriter {
   private Document ownerDoc = null;
   private Node currentNode = null;
   private Node node = null;
   private NamespaceSupport namespaceContext = null;
   private Method mXmlVersion = null;
   private boolean[] needContextPop = null;
   private StringBuffer stringBuffer = null;
   private int resizeValue = 20;
   private int depth = 0;

   public XMLDOMWriterImpl(DOMResult result) {
      this.node = result.getNode();
      if (this.node.getNodeType() == 9) {
         this.ownerDoc = (Document)this.node;
         this.currentNode = this.ownerDoc;
      } else {
         this.ownerDoc = this.node.getOwnerDocument();
         this.currentNode = this.node;
      }

      this.getDLThreeMethods();
      this.stringBuffer = new StringBuffer();
      this.needContextPop = new boolean[this.resizeValue];
      this.namespaceContext = new NamespaceSupport();
   }

   private void getDLThreeMethods() {
      try {
         this.mXmlVersion = this.ownerDoc.getClass().getMethod("setXmlVersion", String.class);
      } catch (NoSuchMethodException var2) {
         this.mXmlVersion = null;
      } catch (SecurityException var3) {
         this.mXmlVersion = null;
      }

   }

   public void close() throws XMLStreamException {
   }

   public void flush() throws XMLStreamException {
   }

   public NamespaceContext getNamespaceContext() {
      return null;
   }

   public String getPrefix(String namespaceURI) throws XMLStreamException {
      String prefix = null;
      if (this.namespaceContext != null) {
         prefix = this.namespaceContext.getPrefix(namespaceURI);
      }

      return prefix;
   }

   public Object getProperty(String str) throws IllegalArgumentException {
      throw new UnsupportedOperationException();
   }

   public void setDefaultNamespace(String uri) throws XMLStreamException {
      this.namespaceContext.declarePrefix("", uri);
      if (!this.needContextPop[this.depth]) {
         this.needContextPop[this.depth] = true;
      }

   }

   public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public void setPrefix(String prefix, String uri) throws XMLStreamException {
      if (prefix == null) {
         throw new XMLStreamException("Prefix cannot be null");
      } else {
         this.namespaceContext.declarePrefix(prefix, uri);
         if (!this.needContextPop[this.depth]) {
            this.needContextPop[this.depth] = true;
         }

      }
   }

   public void writeAttribute(String localName, String value) throws XMLStreamException {
      if (this.currentNode.getNodeType() == 1) {
         Attr attr = this.ownerDoc.createAttribute(localName);
         attr.setValue(value);
         ((Element)this.currentNode).setAttributeNode(attr);
      } else {
         throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
      }
   }

   public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
      if (this.currentNode.getNodeType() == 1) {
         String prefix = null;
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         } else {
            if (this.namespaceContext != null) {
               prefix = this.namespaceContext.getPrefix(namespaceURI);
            }

            if (prefix == null) {
               throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
            } else {
               String qualifiedName = null;
               if (prefix.equals("")) {
                  qualifiedName = localName;
               } else {
                  qualifiedName = this.getQName(prefix, localName);
               }

               Attr attr = this.ownerDoc.createAttributeNS(namespaceURI, qualifiedName);
               attr.setValue(value);
               ((Element)this.currentNode).setAttributeNode(attr);
            }
         }
      } else {
         throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
      }
   }

   public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
      if (this.currentNode.getNodeType() == 1) {
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         } else if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         } else if (prefix == null) {
            throw new XMLStreamException("prefix cannot be null");
         } else {
            String qualifiedName = null;
            if (prefix.equals("")) {
               qualifiedName = localName;
            } else {
               qualifiedName = this.getQName(prefix, localName);
            }

            Attr attr = this.ownerDoc.createAttributeNS(namespaceURI, qualifiedName);
            attr.setValue(value);
            ((Element)this.currentNode).setAttributeNodeNS(attr);
         }
      } else {
         throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
      }
   }

   public void writeCData(String data) throws XMLStreamException {
      if (data == null) {
         throw new XMLStreamException("CDATA cannot be null");
      } else {
         CDATASection cdata = this.ownerDoc.createCDATASection(data);
         this.getNode().appendChild(cdata);
      }
   }

   public void writeCharacters(String charData) throws XMLStreamException {
      Text text = this.ownerDoc.createTextNode(charData);
      this.currentNode.appendChild(text);
   }

   public void writeCharacters(char[] values, int param, int param2) throws XMLStreamException {
      Text text = this.ownerDoc.createTextNode(new String(values, param, param2));
      this.currentNode.appendChild(text);
   }

   public void writeComment(String str) throws XMLStreamException {
      Comment comment = this.ownerDoc.createComment(str);
      this.getNode().appendChild(comment);
   }

   public void writeDTD(String str) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
      if (this.currentNode.getNodeType() == 1) {
         String qname = "xmlns";
         ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", qname, namespaceURI);
      } else {
         throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
      }
   }

   public void writeEmptyElement(String localName) throws XMLStreamException {
      if (this.ownerDoc != null) {
         Element element = this.ownerDoc.createElement(localName);
         if (this.currentNode != null) {
            this.currentNode.appendChild(element);
         } else {
            this.ownerDoc.appendChild(element);
         }
      }

   }

   public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
      if (this.ownerDoc != null) {
         String qualifiedName = null;
         String prefix = null;
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         }

         if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         }

         if (this.namespaceContext != null) {
            prefix = this.namespaceContext.getPrefix(namespaceURI);
         }

         if (prefix == null) {
            throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
         }

         if ("".equals(prefix)) {
            qualifiedName = localName;
         } else {
            qualifiedName = this.getQName(prefix, localName);
         }

         Element element = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
         if (this.currentNode != null) {
            this.currentNode.appendChild(element);
         } else {
            this.ownerDoc.appendChild(element);
         }
      }

   }

   public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      if (this.ownerDoc != null) {
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         }

         if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         }

         if (prefix == null) {
            throw new XMLStreamException("Prefix cannot be null");
         }

         String qualifiedName = null;
         if ("".equals(prefix)) {
            qualifiedName = localName;
         } else {
            qualifiedName = this.getQName(prefix, localName);
         }

         Element el = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
         if (this.currentNode != null) {
            this.currentNode.appendChild(el);
         } else {
            this.ownerDoc.appendChild(el);
         }
      }

   }

   public void writeEndDocument() throws XMLStreamException {
      this.currentNode = null;

      for(int i = 0; i < this.depth; ++i) {
         if (this.needContextPop[this.depth]) {
            this.needContextPop[this.depth] = false;
            this.namespaceContext.popContext();
         }

         --this.depth;
      }

      this.depth = 0;
   }

   public void writeEndElement() throws XMLStreamException {
      Node node = this.currentNode.getParentNode();
      if (this.currentNode.getNodeType() == 9) {
         this.currentNode = null;
      } else {
         this.currentNode = node;
      }

      if (this.needContextPop[this.depth]) {
         this.needContextPop[this.depth] = false;
         this.namespaceContext.popContext();
      }

      --this.depth;
   }

   public void writeEntityRef(String name) throws XMLStreamException {
      EntityReference er = this.ownerDoc.createEntityReference(name);
      this.currentNode.appendChild(er);
   }

   public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
      if (prefix == null) {
         throw new XMLStreamException("prefix cannot be null");
      } else if (namespaceURI == null) {
         throw new XMLStreamException("NamespaceURI cannot be null");
      } else {
         String qname = null;
         if (prefix.equals("")) {
            qname = "xmlns";
         } else {
            qname = this.getQName("xmlns", prefix);
         }

         ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", qname, namespaceURI);
      }
   }

   public void writeProcessingInstruction(String target) throws XMLStreamException {
      if (target == null) {
         throw new XMLStreamException("Target cannot be null");
      } else {
         ProcessingInstruction pi = this.ownerDoc.createProcessingInstruction(target, "");
         this.currentNode.appendChild(pi);
      }
   }

   public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
      if (target == null) {
         throw new XMLStreamException("Target cannot be null");
      } else {
         ProcessingInstruction pi = this.ownerDoc.createProcessingInstruction(target, data);
         this.currentNode.appendChild(pi);
      }
   }

   public void writeStartDocument() throws XMLStreamException {
      try {
         if (this.mXmlVersion != null) {
            this.mXmlVersion.invoke(this.ownerDoc, "1.0");
         }

      } catch (IllegalAccessException var2) {
         throw new XMLStreamException(var2);
      } catch (InvocationTargetException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeStartDocument(String version) throws XMLStreamException {
      try {
         if (this.mXmlVersion != null) {
            this.mXmlVersion.invoke(this.ownerDoc, version);
         }

      } catch (IllegalAccessException var3) {
         throw new XMLStreamException(var3);
      } catch (InvocationTargetException var4) {
         throw new XMLStreamException(var4);
      }
   }

   public void writeStartDocument(String encoding, String version) throws XMLStreamException {
      try {
         if (this.mXmlVersion != null) {
            this.mXmlVersion.invoke(this.ownerDoc, version);
         }

      } catch (IllegalAccessException var4) {
         throw new XMLStreamException(var4);
      } catch (InvocationTargetException var5) {
         throw new XMLStreamException(var5);
      }
   }

   public void writeStartElement(String localName) throws XMLStreamException {
      if (this.ownerDoc != null) {
         Element element = this.ownerDoc.createElement(localName);
         if (this.currentNode != null) {
            this.currentNode.appendChild(element);
         } else {
            this.ownerDoc.appendChild(element);
         }

         this.currentNode = element;
      }

      if (this.needContextPop[this.depth]) {
         this.namespaceContext.pushContext();
      }

      this.incDepth();
   }

   public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
      if (this.ownerDoc != null) {
         String qualifiedName = null;
         String prefix = null;
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         }

         if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         }

         if (this.namespaceContext != null) {
            prefix = this.namespaceContext.getPrefix(namespaceURI);
         }

         if (prefix == null) {
            throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
         }

         if ("".equals(prefix)) {
            qualifiedName = localName;
         } else {
            qualifiedName = this.getQName(prefix, localName);
         }

         Element element = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
         if (this.currentNode != null) {
            this.currentNode.appendChild(element);
         } else {
            this.ownerDoc.appendChild(element);
         }

         this.currentNode = element;
      }

      if (this.needContextPop[this.depth]) {
         this.namespaceContext.pushContext();
      }

      this.incDepth();
   }

   public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      if (this.ownerDoc != null) {
         String qname = null;
         if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
         }

         if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
         }

         if (prefix == null) {
            throw new XMLStreamException("Prefix cannot be null");
         }

         if (prefix.equals("")) {
            qname = localName;
         } else {
            qname = this.getQName(prefix, localName);
         }

         Element el = this.ownerDoc.createElementNS(namespaceURI, qname);
         if (this.currentNode != null) {
            this.currentNode.appendChild(el);
         } else {
            this.ownerDoc.appendChild(el);
         }

         this.currentNode = el;
         if (this.needContextPop[this.depth]) {
            this.namespaceContext.pushContext();
         }

         this.incDepth();
      }

   }

   private String getQName(String prefix, String localName) {
      this.stringBuffer.setLength(0);
      this.stringBuffer.append(prefix);
      this.stringBuffer.append(":");
      this.stringBuffer.append(localName);
      return this.stringBuffer.toString();
   }

   private Node getNode() {
      return (Node)(this.currentNode == null ? this.ownerDoc : this.currentNode);
   }

   private void incDepth() {
      ++this.depth;
      if (this.depth == this.needContextPop.length) {
         boolean[] array = new boolean[this.depth + this.resizeValue];
         System.arraycopy(this.needContextPop, 0, array, 0, this.depth);
         this.needContextPop = array;
      }

   }
}
