package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class StAXFilteredEvent implements XMLEventReader {
   private XMLEventReader eventReader;
   private EventFilter _filter;

   public StAXFilteredEvent() {
   }

   public StAXFilteredEvent(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
      this.eventReader = reader;
      this._filter = filter;
   }

   public void setEventReader(XMLEventReader reader) {
      this.eventReader = reader;
   }

   public void setFilter(EventFilter filter) {
      this._filter = filter;
   }

   public Object next() {
      try {
         return this.nextEvent();
      } catch (XMLStreamException var2) {
         return null;
      }
   }

   public XMLEvent nextEvent() throws XMLStreamException {
      return this.hasNext() ? this.eventReader.nextEvent() : null;
   }

   public String getElementText() throws XMLStreamException {
      StringBuffer buffer = new StringBuffer();
      XMLEvent e = this.nextEvent();
      if (!e.isStartElement()) {
         throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT"));
      } else {
         do {
            if (!this.hasNext()) {
               throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
            }

            e = this.nextEvent();
            if (e.isStartElement()) {
               throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"));
            }

            if (e.isCharacters()) {
               buffer.append(((Characters)e).getData());
            }
         } while(!e.isEndElement());

         return buffer.toString();
      }
   }

   public XMLEvent nextTag() throws XMLStreamException {
      while(true) {
         if (this.hasNext()) {
            XMLEvent e = this.nextEvent();
            if (!e.isStartElement() && !e.isEndElement()) {
               continue;
            }

            return e;
         }

         throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
      }
   }

   public boolean hasNext() {
      try {
         while(this.eventReader.hasNext()) {
            if (this._filter.accept(this.eventReader.peek())) {
               return true;
            }

            this.eventReader.nextEvent();
         }

         return false;
      } catch (XMLStreamException var2) {
         return false;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public XMLEvent peek() throws XMLStreamException {
      return this.hasNext() ? this.eventReader.peek() : null;
   }

   public void close() throws XMLStreamException {
      this.eventReader.close();
   }

   public Object getProperty(String name) {
      return this.eventReader.getProperty(name);
   }
}
