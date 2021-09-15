package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class InterningXmlVisitor implements XmlVisitor {
   private final XmlVisitor next;
   private final InterningXmlVisitor.AttributesImpl attributes = new InterningXmlVisitor.AttributesImpl();

   public InterningXmlVisitor(XmlVisitor next) {
      this.next = next;
   }

   public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException {
      this.next.startDocument(locator, nsContext);
   }

   public void endDocument() throws SAXException {
      this.next.endDocument();
   }

   public void startElement(TagName tagName) throws SAXException {
      this.attributes.setAttributes(tagName.atts);
      tagName.atts = this.attributes;
      tagName.uri = intern(tagName.uri);
      tagName.local = intern(tagName.local);
      this.next.startElement(tagName);
   }

   public void endElement(TagName tagName) throws SAXException {
      tagName.uri = intern(tagName.uri);
      tagName.local = intern(tagName.local);
      this.next.endElement(tagName);
   }

   public void startPrefixMapping(String prefix, String nsUri) throws SAXException {
      this.next.startPrefixMapping(intern(prefix), intern(nsUri));
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      this.next.endPrefixMapping(intern(prefix));
   }

   public void text(CharSequence pcdata) throws SAXException {
      this.next.text(pcdata);
   }

   public UnmarshallingContext getContext() {
      return this.next.getContext();
   }

   public XmlVisitor.TextPredictor getPredictor() {
      return this.next.getPredictor();
   }

   private static String intern(String s) {
      return s == null ? null : s.intern();
   }

   private static class AttributesImpl implements Attributes {
      private Attributes core;

      private AttributesImpl() {
      }

      void setAttributes(Attributes att) {
         this.core = att;
      }

      public int getIndex(String qName) {
         return this.core.getIndex(qName);
      }

      public int getIndex(String uri, String localName) {
         return this.core.getIndex(uri, localName);
      }

      public int getLength() {
         return this.core.getLength();
      }

      public String getLocalName(int index) {
         return InterningXmlVisitor.intern(this.core.getLocalName(index));
      }

      public String getQName(int index) {
         return InterningXmlVisitor.intern(this.core.getQName(index));
      }

      public String getType(int index) {
         return InterningXmlVisitor.intern(this.core.getType(index));
      }

      public String getType(String qName) {
         return InterningXmlVisitor.intern(this.core.getType(qName));
      }

      public String getType(String uri, String localName) {
         return InterningXmlVisitor.intern(this.core.getType(uri, localName));
      }

      public String getURI(int index) {
         return InterningXmlVisitor.intern(this.core.getURI(index));
      }

      public String getValue(int index) {
         return this.core.getValue(index);
      }

      public String getValue(String qName) {
         return this.core.getValue(qName);
      }

      public String getValue(String uri, String localName) {
         return this.core.getValue(uri, localName);
      }

      // $FF: synthetic method
      AttributesImpl(Object x0) {
         this();
      }
   }
}
