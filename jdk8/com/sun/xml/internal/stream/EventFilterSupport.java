package com.sun.xml.internal.stream;

import java.util.NoSuchElementException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

public class EventFilterSupport extends EventReaderDelegate {
   EventFilter fEventFilter;

   public EventFilterSupport(XMLEventReader eventReader, EventFilter eventFilter) {
      this.setParent(eventReader);
      this.fEventFilter = eventFilter;
   }

   public Object next() {
      try {
         return this.nextEvent();
      } catch (XMLStreamException var2) {
         throw new NoSuchElementException();
      }
   }

   public boolean hasNext() {
      try {
         return this.peek() != null;
      } catch (XMLStreamException var2) {
         return false;
      }
   }

   public XMLEvent nextEvent() throws XMLStreamException {
      if (super.hasNext()) {
         XMLEvent event = super.nextEvent();
         return this.fEventFilter.accept(event) ? event : this.nextEvent();
      } else {
         throw new NoSuchElementException();
      }
   }

   public XMLEvent nextTag() throws XMLStreamException {
      if (super.hasNext()) {
         XMLEvent event = super.nextTag();
         return this.fEventFilter.accept(event) ? event : this.nextTag();
      } else {
         throw new NoSuchElementException();
      }
   }

   public XMLEvent peek() throws XMLStreamException {
      while(true) {
         XMLEvent event = super.peek();
         if (event == null) {
            return null;
         }

         if (this.fEventFilter.accept(event)) {
            return event;
         }

         super.next();
      }
   }
}
