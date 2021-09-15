package com.sun.xml.internal.ws.api.message.saaj;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

public class SaajStaxWriter implements XMLStreamWriter {
   protected SOAPMessage soap;
   protected String envURI;
   protected SOAPElement currentElement;
   protected SaajStaxWriter.DeferredElement deferredElement;
   protected static final String Envelope = "Envelope";
   protected static final String Header = "Header";
   protected static final String Body = "Body";
   protected static final String xmlns = "xmlns";

   public SaajStaxWriter(SOAPMessage msg) throws SOAPException {
      this.soap = msg;
      this.currentElement = this.soap.getSOAPPart().getEnvelope();
      this.envURI = this.currentElement.getNamespaceURI();
      this.deferredElement = new SaajStaxWriter.DeferredElement();
   }

   public SOAPMessage getSOAPMessage() {
      return this.soap;
   }

   public void writeStartElement(String localName) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      this.deferredElement.setLocalName(localName);
   }

   public void writeStartElement(String ns, String ln) throws XMLStreamException {
      this.writeStartElement((String)null, ln, ns);
   }

   public void writeStartElement(String prefix, String ln, String ns) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      if (this.envURI.equals(ns)) {
         try {
            if ("Envelope".equals(ln)) {
               this.currentElement = this.soap.getSOAPPart().getEnvelope();
               this.fixPrefix(prefix);
               return;
            }

            if ("Header".equals(ln)) {
               this.currentElement = this.soap.getSOAPHeader();
               this.fixPrefix(prefix);
               return;
            }

            if ("Body".equals(ln)) {
               this.currentElement = this.soap.getSOAPBody();
               this.fixPrefix(prefix);
               return;
            }
         } catch (SOAPException var5) {
            throw new XMLStreamException(var5);
         }
      }

      this.deferredElement.setLocalName(ln);
      this.deferredElement.setNamespaceUri(ns);
      this.deferredElement.setPrefix(prefix);
   }

   private void fixPrefix(String prfx) throws XMLStreamException {
      String oldPrfx = this.currentElement.getPrefix();
      if (prfx != null && !prfx.equals(oldPrfx)) {
         this.currentElement.setPrefix(prfx);
      }

   }

   public void writeEmptyElement(String uri, String ln) throws XMLStreamException {
      this.writeStartElement((String)null, ln, uri);
   }

   public void writeEmptyElement(String prefix, String ln, String uri) throws XMLStreamException {
      this.writeStartElement(prefix, ln, uri);
   }

   public void writeEmptyElement(String ln) throws XMLStreamException {
      this.writeStartElement((String)null, ln, (String)null);
   }

   public void writeEndElement() throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      if (this.currentElement != null) {
         this.currentElement = this.currentElement.getParentElement();
      }

   }

   public void writeEndDocument() throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
   }

   public void close() throws XMLStreamException {
   }

   public void flush() throws XMLStreamException {
   }

   public void writeAttribute(String ln, String val) throws XMLStreamException {
      this.writeAttribute((String)null, (String)null, ln, val);
   }

   public void writeAttribute(String prefix, String ns, String ln, String value) throws XMLStreamException {
      if (ns == null && prefix == null && "xmlns".equals(ln)) {
         this.writeNamespace("", value);
      } else if (this.deferredElement.isInitialized()) {
         this.deferredElement.addAttribute(prefix, ns, ln, value);
      } else {
         addAttibuteToElement(this.currentElement, prefix, ns, ln, value);
      }

   }

   public void writeAttribute(String ns, String ln, String val) throws XMLStreamException {
      this.writeAttribute((String)null, ns, ln, val);
   }

   public void writeNamespace(String prefix, String uri) throws XMLStreamException {
      String thePrefix = prefix != null && !"xmlns".equals(prefix) ? prefix : "";
      if (this.deferredElement.isInitialized()) {
         this.deferredElement.addNamespaceDeclaration(thePrefix, uri);
      } else {
         try {
            this.currentElement.addNamespaceDeclaration(thePrefix, uri);
         } catch (SOAPException var5) {
            throw new XMLStreamException(var5);
         }
      }

   }

   public void writeDefaultNamespace(String uri) throws XMLStreamException {
      this.writeNamespace("", uri);
   }

   public void writeComment(String data) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      Comment c = this.soap.getSOAPPart().createComment(data);
      this.currentElement.appendChild(c);
   }

   public void writeProcessingInstruction(String target) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      Node n = this.soap.getSOAPPart().createProcessingInstruction(target, "");
      this.currentElement.appendChild(n);
   }

   public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      Node n = this.soap.getSOAPPart().createProcessingInstruction(target, data);
      this.currentElement.appendChild(n);
   }

   public void writeCData(String data) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      Node n = this.soap.getSOAPPart().createCDATASection(data);
      this.currentElement.appendChild(n);
   }

   public void writeDTD(String dtd) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
   }

   public void writeEntityRef(String name) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      Node n = this.soap.getSOAPPart().createEntityReference(name);
      this.currentElement.appendChild(n);
   }

   public void writeStartDocument() throws XMLStreamException {
   }

   public void writeStartDocument(String version) throws XMLStreamException {
      if (version != null) {
         this.soap.getSOAPPart().setXmlVersion(version);
      }

   }

   public void writeStartDocument(String encoding, String version) throws XMLStreamException {
      if (version != null) {
         this.soap.getSOAPPart().setXmlVersion(version);
      }

      if (encoding != null) {
         try {
            this.soap.setProperty("javax.xml.soap.character-set-encoding", encoding);
         } catch (SOAPException var4) {
            throw new XMLStreamException(var4);
         }
      }

   }

   public void writeCharacters(String text) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);

      try {
         this.currentElement.addTextNode(text);
      } catch (SOAPException var3) {
         throw new XMLStreamException(var3);
      }
   }

   public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
      this.currentElement = this.deferredElement.flushTo(this.currentElement);
      char[] chr = start == 0 && len == text.length ? text : Arrays.copyOfRange(text, start, start + len);

      try {
         this.currentElement.addTextNode(new String(chr));
      } catch (SOAPException var6) {
         throw new XMLStreamException(var6);
      }
   }

   public String getPrefix(String uri) throws XMLStreamException {
      return this.currentElement.lookupPrefix(uri);
   }

   public void setPrefix(String prefix, String uri) throws XMLStreamException {
      if (this.deferredElement.isInitialized()) {
         this.deferredElement.addNamespaceDeclaration(prefix, uri);
      } else {
         throw new XMLStreamException("Namespace not associated with any element");
      }
   }

   public void setDefaultNamespace(String uri) throws XMLStreamException {
      this.setPrefix("", uri);
   }

   public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return "javax.xml.stream.isRepairingNamespaces".equals(name) ? Boolean.FALSE : null;
   }

   public NamespaceContext getNamespaceContext() {
      return new NamespaceContext() {
         public String getNamespaceURI(String prefix) {
            return SaajStaxWriter.this.currentElement.getNamespaceURI(prefix);
         }

         public String getPrefix(String namespaceURI) {
            return SaajStaxWriter.this.currentElement.lookupPrefix(namespaceURI);
         }

         public Iterator getPrefixes(final String namespaceURI) {
            return new Iterator<String>() {
               String prefix = getPrefix(namespaceURI);

               public boolean hasNext() {
                  return this.prefix != null;
               }

               public String next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     String next = this.prefix;
                     this.prefix = null;
                     return next;
                  }
               }

               public void remove() {
               }
            };
         }
      };
   }

   static void addAttibuteToElement(SOAPElement element, String prefix, String ns, String ln, String value) throws XMLStreamException {
      try {
         if (ns == null) {
            element.setAttributeNS("", ln, value);
         } else {
            QName name = prefix == null ? new QName(ns, ln) : new QName(ns, ln, prefix);
            element.addAttribute(name, value);
         }

      } catch (SOAPException var6) {
         throw new XMLStreamException(var6);
      }
   }

   static class AttributeDeclaration {
      final String prefix;
      final String namespaceUri;
      final String localName;
      final String value;

      AttributeDeclaration(String prefix, String namespaceUri, String localName, String value) {
         this.prefix = prefix;
         this.namespaceUri = namespaceUri;
         this.localName = localName;
         this.value = value;
      }
   }

   static class NamespaceDeclaration {
      final String prefix;
      final String namespaceUri;

      NamespaceDeclaration(String prefix, String namespaceUri) {
         this.prefix = prefix;
         this.namespaceUri = namespaceUri;
      }
   }

   static class DeferredElement {
      private String prefix;
      private String localName;
      private String namespaceUri;
      private final List<SaajStaxWriter.NamespaceDeclaration> namespaceDeclarations = new LinkedList();
      private final List<SaajStaxWriter.AttributeDeclaration> attributeDeclarations = new LinkedList();

      DeferredElement() {
         this.reset();
      }

      public void setPrefix(String prefix) {
         this.prefix = prefix;
      }

      public void setLocalName(String localName) {
         if (localName == null) {
            throw new IllegalArgumentException("localName can not be null");
         } else {
            this.localName = localName;
         }
      }

      public void setNamespaceUri(String namespaceUri) {
         this.namespaceUri = namespaceUri;
      }

      public void addNamespaceDeclaration(String prefix, String namespaceUri) {
         if (null == this.namespaceUri && null != namespaceUri && prefix.equals(emptyIfNull(this.prefix))) {
            this.namespaceUri = namespaceUri;
         }

         this.namespaceDeclarations.add(new SaajStaxWriter.NamespaceDeclaration(prefix, namespaceUri));
      }

      public void addAttribute(String prefix, String ns, String ln, String value) {
         if (ns == null && prefix == null && "xmlns".equals(ln)) {
            this.addNamespaceDeclaration(prefix, value);
         } else {
            this.attributeDeclarations.add(new SaajStaxWriter.AttributeDeclaration(prefix, ns, ln, value));
         }

      }

      public SOAPElement flushTo(SOAPElement target) throws XMLStreamException {
         try {
            if (this.localName == null) {
               return target;
            } else {
               SOAPElement newElement;
               if (this.namespaceUri == null) {
                  newElement = target.addChildElement(this.localName);
               } else if (this.prefix == null) {
                  newElement = target.addChildElement(new QName(this.namespaceUri, this.localName));
               } else {
                  newElement = target.addChildElement(this.localName, this.prefix, this.namespaceUri);
               }

               Iterator var3 = this.namespaceDeclarations.iterator();

               while(var3.hasNext()) {
                  SaajStaxWriter.NamespaceDeclaration namespace = (SaajStaxWriter.NamespaceDeclaration)var3.next();
                  newElement.addNamespaceDeclaration(namespace.prefix, namespace.namespaceUri);
               }

               var3 = this.attributeDeclarations.iterator();

               while(var3.hasNext()) {
                  SaajStaxWriter.AttributeDeclaration attribute = (SaajStaxWriter.AttributeDeclaration)var3.next();
                  SaajStaxWriter.addAttibuteToElement(newElement, attribute.prefix, attribute.namespaceUri, attribute.localName, attribute.value);
               }

               this.reset();
               return newElement;
            }
         } catch (SOAPException var5) {
            throw new XMLStreamException(var5);
         }
      }

      public boolean isInitialized() {
         return this.localName != null;
      }

      private void reset() {
         this.localName = null;
         this.prefix = null;
         this.namespaceUri = null;
         this.namespaceDeclarations.clear();
         this.attributeDeclarations.clear();
      }

      private static String emptyIfNull(String s) {
         return s == null ? "" : s;
      }
   }
}
