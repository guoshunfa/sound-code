package javax.xml.transform.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;

public class StAXSource implements Source {
   public static final String FEATURE = "http://javax.xml.transform.stax.StAXSource/feature";
   private XMLEventReader xmlEventReader = null;
   private XMLStreamReader xmlStreamReader = null;
   private String systemId = null;

   public StAXSource(XMLEventReader xmlEventReader) throws XMLStreamException {
      if (xmlEventReader == null) {
         throw new IllegalArgumentException("StAXSource(XMLEventReader) with XMLEventReader == null");
      } else {
         XMLEvent event = xmlEventReader.peek();
         int eventType = event.getEventType();
         if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException("StAXSource(XMLEventReader) with XMLEventReader not in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state");
         } else {
            this.xmlEventReader = xmlEventReader;
            this.systemId = event.getLocation().getSystemId();
         }
      }
   }

   public StAXSource(XMLStreamReader xmlStreamReader) {
      if (xmlStreamReader == null) {
         throw new IllegalArgumentException("StAXSource(XMLStreamReader) with XMLStreamReader == null");
      } else {
         int eventType = xmlStreamReader.getEventType();
         if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException("StAXSource(XMLStreamReader) with XMLStreamReadernot in XMLStreamConstants.START_DOCUMENT or XMLStreamConstants.START_ELEMENT state");
         } else {
            this.xmlStreamReader = xmlStreamReader;
            this.systemId = xmlStreamReader.getLocation().getSystemId();
         }
      }
   }

   public XMLEventReader getXMLEventReader() {
      return this.xmlEventReader;
   }

   public XMLStreamReader getXMLStreamReader() {
      return this.xmlStreamReader;
   }

   public void setSystemId(String systemId) {
      throw new UnsupportedOperationException("StAXSource#setSystemId(systemId) cannot set the system identifier for a StAXSource");
   }

   public String getSystemId() {
      return this.systemId;
   }
}
