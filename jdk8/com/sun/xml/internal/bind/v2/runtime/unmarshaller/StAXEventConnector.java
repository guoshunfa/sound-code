package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class StAXEventConnector extends StAXConnector {
   private final XMLEventReader staxEventReader;
   private XMLEvent event;
   private final AttributesImpl attrs = new AttributesImpl();
   private final StringBuilder buffer = new StringBuilder();
   private boolean seenText;

   public StAXEventConnector(XMLEventReader staxCore, XmlVisitor visitor) {
      super(visitor);
      this.staxEventReader = staxCore;
   }

   public void bridge() throws XMLStreamException {
      try {
         int depth = 0;
         this.event = this.staxEventReader.peek();
         if (!this.event.isStartDocument() && !this.event.isStartElement()) {
            throw new IllegalStateException();
         } else {
            do {
               this.event = this.staxEventReader.nextEvent();
            } while(!this.event.isStartElement());

            this.handleStartDocument(this.event.asStartElement().getNamespaceContext());

            while(true) {
               switch(this.event.getEventType()) {
               case 1:
                  this.handleStartElement(this.event.asStartElement());
                  ++depth;
                  break;
               case 2:
                  --depth;
                  this.handleEndElement(this.event.asEndElement());
                  if (depth == 0) {
                     this.handleEndDocument();
                     this.event = null;
                     return;
                  }
               case 3:
               case 5:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               default:
                  break;
               case 4:
               case 6:
               case 12:
                  this.handleCharacters(this.event.asCharacters());
               }

               this.event = this.staxEventReader.nextEvent();
            }
         }
      } catch (SAXException var2) {
         throw new XMLStreamException(var2);
      }
   }

   protected Location getCurrentLocation() {
      return this.event.getLocation();
   }

   protected String getCurrentQName() {
      QName qName;
      if (this.event.isEndElement()) {
         qName = this.event.asEndElement().getName();
      } else {
         qName = this.event.asStartElement().getName();
      }

      return this.getQName(qName.getPrefix(), qName.getLocalPart());
   }

   private void handleCharacters(Characters event) throws SAXException, XMLStreamException {
      if (this.predictor.expectText()) {
         this.seenText = true;

         while(true) {
            XMLEvent next = this.staxEventReader.peek();
            if (!this.isIgnorable(next)) {
               if (this.isTag(next)) {
                  this.visitor.text(event.getData());
                  return;
               } else {
                  this.buffer.append(event.getData());

                  while(true) {
                     while(true) {
                        next = this.staxEventReader.peek();
                        if (!this.isIgnorable(next)) {
                           if (this.isTag(next)) {
                              this.visitor.text(this.buffer);
                              this.buffer.setLength(0);
                              return;
                           }

                           this.buffer.append(next.asCharacters().getData());
                           this.staxEventReader.nextEvent();
                        } else {
                           this.staxEventReader.nextEvent();
                        }
                     }
                  }
               }
            }

            this.staxEventReader.nextEvent();
         }
      }
   }

   private boolean isTag(XMLEvent event) {
      int eventType = event.getEventType();
      return eventType == 1 || eventType == 2;
   }

   private boolean isIgnorable(XMLEvent event) {
      int eventType = event.getEventType();
      return eventType == 5 || eventType == 3;
   }

   private void handleEndElement(EndElement event) throws SAXException {
      if (!this.seenText && this.predictor.expectText()) {
         this.visitor.text("");
      }

      QName qName = event.getName();
      this.tagName.uri = fixNull(qName.getNamespaceURI());
      this.tagName.local = qName.getLocalPart();
      this.visitor.endElement(this.tagName);
      Iterator i = event.getNamespaces();

      while(i.hasNext()) {
         String prefix = fixNull(((Namespace)i.next()).getPrefix());
         this.visitor.endPrefixMapping(prefix);
      }

      this.seenText = false;
   }

   private void handleStartElement(StartElement event) throws SAXException {
      Iterator i = event.getNamespaces();

      while(i.hasNext()) {
         Namespace ns = (Namespace)i.next();
         this.visitor.startPrefixMapping(fixNull(ns.getPrefix()), fixNull(ns.getNamespaceURI()));
      }

      QName qName = event.getName();
      this.tagName.uri = fixNull(qName.getNamespaceURI());
      String localName = qName.getLocalPart();
      this.tagName.uri = fixNull(qName.getNamespaceURI());
      this.tagName.local = localName;
      this.tagName.atts = this.getAttributes(event);
      this.visitor.startElement(this.tagName);
      this.seenText = false;
   }

   private Attributes getAttributes(StartElement event) {
      this.attrs.clear();
      Iterator i = event.getAttributes();

      while(i.hasNext()) {
         Attribute staxAttr = (Attribute)i.next();
         QName name = staxAttr.getName();
         String uri = fixNull(name.getNamespaceURI());
         String localName = name.getLocalPart();
         String prefix = name.getPrefix();
         String qName;
         if (prefix != null && prefix.length() != 0) {
            qName = prefix + ':' + localName;
         } else {
            qName = localName;
         }

         String type = staxAttr.getDTDType();
         String value = staxAttr.getValue();
         this.attrs.addAttribute(uri, localName, qName, type, value);
      }

      return this.attrs;
   }
}
