package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXEventWriter extends SAX2StAXBaseWriter {
   private XMLEventWriter writer;
   private XMLEventFactory eventFactory;
   private List namespaceStack = new ArrayList();
   private boolean needToCallStartDocument = false;

   public SAX2StAXEventWriter() {
      this.eventFactory = XMLEventFactory.newInstance();
   }

   public SAX2StAXEventWriter(XMLEventWriter writer) {
      this.writer = writer;
      this.eventFactory = XMLEventFactory.newInstance();
   }

   public SAX2StAXEventWriter(XMLEventWriter writer, XMLEventFactory factory) {
      this.writer = writer;
      if (factory != null) {
         this.eventFactory = factory;
      } else {
         this.eventFactory = XMLEventFactory.newInstance();
      }

   }

   public XMLEventWriter getEventWriter() {
      return this.writer;
   }

   public void setEventWriter(XMLEventWriter writer) {
      this.writer = writer;
   }

   public XMLEventFactory getEventFactory() {
      return this.eventFactory;
   }

   public void setEventFactory(XMLEventFactory factory) {
      this.eventFactory = factory;
   }

   public void startDocument() throws SAXException {
      super.startDocument();
      this.namespaceStack.clear();
      this.eventFactory.setLocation(this.getCurrentLocation());
      this.needToCallStartDocument = true;
   }

   private void writeStartDocument() throws SAXException {
      try {
         if (this.docLocator == null) {
            this.writer.add((XMLEvent)this.eventFactory.createStartDocument());
         } else {
            try {
               this.writer.add((XMLEvent)this.eventFactory.createStartDocument(((Locator2)this.docLocator).getEncoding(), ((Locator2)this.docLocator).getXMLVersion()));
            } catch (ClassCastException var2) {
               this.writer.add((XMLEvent)this.eventFactory.createStartDocument());
            }
         }
      } catch (XMLStreamException var3) {
         throw new SAXException(var3);
      }

      this.needToCallStartDocument = false;
   }

   public void endDocument() throws SAXException {
      this.eventFactory.setLocation(this.getCurrentLocation());

      try {
         this.writer.add((XMLEvent)this.eventFactory.createEndDocument());
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }

      super.endDocument();
      this.namespaceStack.clear();
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this.needToCallStartDocument) {
         this.writeStartDocument();
      }

      this.eventFactory.setLocation(this.getCurrentLocation());
      Collection[] events = new Collection[]{null, null};
      this.createStartEvents(attributes, events);
      this.namespaceStack.add(events[0]);

      try {
         String[] qname = new String[]{null, null};
         parseQName(qName, qname);
         this.writer.add((XMLEvent)this.eventFactory.createStartElement(qname[0], uri, qname[1], events[1].iterator(), events[0].iterator()));
      } catch (XMLStreamException var10) {
         throw new SAXException(var10);
      } finally {
         super.startElement(uri, localName, qName, attributes);
      }

   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      super.endElement(uri, localName, qName);
      this.eventFactory.setLocation(this.getCurrentLocation());
      String[] qname = new String[]{null, null};
      parseQName(qName, qname);
      Collection nsList = (Collection)this.namespaceStack.remove(this.namespaceStack.size() - 1);
      Iterator nsIter = nsList.iterator();

      try {
         this.writer.add((XMLEvent)this.eventFactory.createEndElement(qname[0], uri, qname[1], nsIter));
      } catch (XMLStreamException var8) {
         throw new SAXException(var8);
      }
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      if (this.needToCallStartDocument) {
         this.writeStartDocument();
      }

      super.comment(ch, start, length);
      this.eventFactory.setLocation(this.getCurrentLocation());

      try {
         this.writer.add((XMLEvent)this.eventFactory.createComment(new String(ch, start, length)));
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      super.characters(ch, start, length);

      try {
         if (!this.isCDATA) {
            this.eventFactory.setLocation(this.getCurrentLocation());
            this.writer.add((XMLEvent)this.eventFactory.createCharacters(new String(ch, start, length)));
         }

      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      super.ignorableWhitespace(ch, start, length);
      this.characters(ch, start, length);
   }

   public void processingInstruction(String target, String data) throws SAXException {
      if (this.needToCallStartDocument) {
         this.writeStartDocument();
      }

      super.processingInstruction(target, data);

      try {
         this.writer.add((XMLEvent)this.eventFactory.createProcessingInstruction(target, data));
      } catch (XMLStreamException var4) {
         throw new SAXException(var4);
      }
   }

   public void endCDATA() throws SAXException {
      this.eventFactory.setLocation(this.getCurrentLocation());

      try {
         this.writer.add((XMLEvent)this.eventFactory.createCData(this.CDATABuffer.toString()));
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }

      super.endCDATA();
   }

   protected void createStartEvents(Attributes attributes, Collection[] events) {
      Map nsMap = null;
      List attrs = null;
      int i;
      String attrPrefix;
      if (this.namespaces != null) {
         int nDecls = this.namespaces.size();

         for(i = 0; i < nDecls; ++i) {
            String prefix = (String)this.namespaces.elementAt(i++);
            attrPrefix = (String)this.namespaces.elementAt(i);
            Namespace ns = this.createNamespace(prefix, attrPrefix);
            if (nsMap == null) {
               nsMap = new HashMap();
            }

            nsMap.put(prefix, ns);
         }
      }

      String[] qname = new String[]{null, null};
      i = 0;

      for(int s = attributes.getLength(); i < s; ++i) {
         parseQName(attributes.getQName(i), qname);
         attrPrefix = qname[0];
         String attrLocal = qname[1];
         String attrQName = attributes.getQName(i);
         String attrValue = attributes.getValue(i);
         String attrURI = attributes.getURI(i);
         if (!"xmlns".equals(attrQName) && !"xmlns".equals(attrPrefix)) {
            Attribute attribute;
            if (attrPrefix.length() > 0) {
               attribute = this.eventFactory.createAttribute(attrPrefix, attrURI, attrLocal, attrValue);
            } else {
               attribute = this.eventFactory.createAttribute(attrLocal, attrValue);
            }

            if (attrs == null) {
               attrs = new ArrayList();
            }

            attrs.add(attribute);
         } else {
            if (nsMap == null) {
               nsMap = new HashMap();
            }

            if (!nsMap.containsKey(attrLocal)) {
               Namespace ns = this.createNamespace(attrLocal, attrValue);
               nsMap.put(attrLocal, ns);
            }
         }
      }

      events[0] = (Collection)(nsMap == null ? Collections.EMPTY_LIST : nsMap.values());
      events[1] = (Collection)(attrs == null ? Collections.EMPTY_LIST : attrs);
   }

   protected Namespace createNamespace(String prefix, String uri) {
      return prefix != null && prefix.length() != 0 ? this.eventFactory.createNamespace(prefix, uri) : this.eventFactory.createNamespace(uri);
   }
}
