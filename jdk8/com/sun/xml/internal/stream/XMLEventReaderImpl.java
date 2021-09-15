package com.sun.xml.internal.stream;

import com.sun.xml.internal.stream.events.XMLEventAllocatorImpl;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class XMLEventReaderImpl implements XMLEventReader {
   protected XMLStreamReader fXMLReader;
   protected XMLEventAllocator fXMLEventAllocator;
   private XMLEvent fPeekedEvent;
   private XMLEvent fLastEvent;

   public XMLEventReaderImpl(XMLStreamReader reader) throws XMLStreamException {
      this.fXMLReader = reader;
      this.fXMLEventAllocator = (XMLEventAllocator)reader.getProperty("javax.xml.stream.allocator");
      if (this.fXMLEventAllocator == null) {
         this.fXMLEventAllocator = new XMLEventAllocatorImpl();
      }

      this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
   }

   public boolean hasNext() {
      if (this.fPeekedEvent != null) {
         return true;
      } else {
         boolean next = false;

         try {
            next = this.fXMLReader.hasNext();
            return next;
         } catch (XMLStreamException var3) {
            return false;
         }
      }
   }

   public XMLEvent nextEvent() throws XMLStreamException {
      if (this.fPeekedEvent != null) {
         this.fLastEvent = this.fPeekedEvent;
         this.fPeekedEvent = null;
         return this.fLastEvent;
      } else if (this.fXMLReader.hasNext()) {
         this.fXMLReader.next();
         return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
      } else {
         this.fLastEvent = null;
         throw new NoSuchElementException();
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public void close() throws XMLStreamException {
      this.fXMLReader.close();
   }

   public String getElementText() throws XMLStreamException {
      if (this.fLastEvent.getEventType() != 1) {
         throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.fLastEvent.getLocation());
      } else {
         String data = null;
         if (this.fPeekedEvent == null) {
            data = this.fXMLReader.getElementText();
            this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
            return data;
         } else {
            XMLEvent event = this.fPeekedEvent;
            this.fPeekedEvent = null;
            int type = event.getEventType();
            if (type != 4 && type != 6 && type != 12) {
               if (type == 9) {
                  data = ((EntityReference)event).getDeclaration().getReplacementText();
               } else if (type != 5 && type != 3) {
                  if (type == 1) {
                     throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
                  }

                  if (type == 2) {
                     return "";
                  }
               }
            } else {
               data = event.asCharacters().getData();
            }

            StringBuffer buffer = new StringBuffer();
            if (data != null && data.length() > 0) {
               buffer.append(data);
            }

            for(event = this.nextEvent(); event.getEventType() != 2; event = this.nextEvent()) {
               if (type != 4 && type != 6 && type != 12) {
                  if (type == 9) {
                     data = ((EntityReference)event).getDeclaration().getReplacementText();
                  } else if (type != 5 && type != 3) {
                     if (type == 8) {
                        throw new XMLStreamException("unexpected end of document when reading element text content");
                     }

                     if (type == 1) {
                        throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
                     }

                     throw new XMLStreamException("Unexpected event type " + type, event.getLocation());
                  }
               } else {
                  data = event.asCharacters().getData();
               }

               if (data != null && data.length() > 0) {
                  buffer.append(data);
               }
            }

            return buffer.toString();
         }
      }
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return this.fXMLReader.getProperty(name);
   }

   public XMLEvent nextTag() throws XMLStreamException {
      if (this.fPeekedEvent == null) {
         this.fXMLReader.nextTag();
         return this.fLastEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
      } else {
         XMLEvent event = this.fPeekedEvent;
         this.fPeekedEvent = null;
         int eventType = event.getEventType();
         if (event.isCharacters() && event.asCharacters().isWhiteSpace() || eventType == 3 || eventType == 5 || eventType == 7) {
            event = this.nextEvent();
            eventType = event.getEventType();
         }

         while(event.isCharacters() && event.asCharacters().isWhiteSpace() || eventType == 3 || eventType == 5) {
            event = this.nextEvent();
            eventType = event.getEventType();
         }

         if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("expected start or end tag", event.getLocation());
         } else {
            return event;
         }
      }
   }

   public Object next() {
      XMLEvent object = null;

      try {
         object = this.nextEvent();
         return object;
      } catch (XMLStreamException var4) {
         this.fLastEvent = null;
         NoSuchElementException e = new NoSuchElementException(var4.getMessage());
         e.initCause(var4.getCause());
         throw e;
      }
   }

   public XMLEvent peek() throws XMLStreamException {
      if (this.fPeekedEvent != null) {
         return this.fPeekedEvent;
      } else if (this.hasNext()) {
         this.fXMLReader.next();
         this.fPeekedEvent = this.fXMLEventAllocator.allocate(this.fXMLReader);
         return this.fPeekedEvent;
      } else {
         return null;
      }
   }
}
