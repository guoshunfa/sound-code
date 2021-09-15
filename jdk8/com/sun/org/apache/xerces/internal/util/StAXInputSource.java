package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class StAXInputSource extends XMLInputSource {
   private final XMLStreamReader fStreamReader;
   private final XMLEventReader fEventReader;
   private final boolean fConsumeRemainingContent;

   public StAXInputSource(XMLStreamReader source) {
      this(source, false);
   }

   public StAXInputSource(XMLStreamReader source, boolean consumeRemainingContent) {
      super((String)null, source.getLocation().getSystemId(), (String)null);
      if (source == null) {
         throw new IllegalArgumentException("XMLStreamReader parameter cannot be null.");
      } else {
         this.fStreamReader = source;
         this.fEventReader = null;
         this.fConsumeRemainingContent = consumeRemainingContent;
      }
   }

   public StAXInputSource(XMLEventReader source) {
      this(source, false);
   }

   public StAXInputSource(XMLEventReader source, boolean consumeRemainingContent) {
      super((String)null, getEventReaderSystemId(source), (String)null);
      if (source == null) {
         throw new IllegalArgumentException("XMLEventReader parameter cannot be null.");
      } else {
         this.fStreamReader = null;
         this.fEventReader = source;
         this.fConsumeRemainingContent = consumeRemainingContent;
      }
   }

   public XMLStreamReader getXMLStreamReader() {
      return this.fStreamReader;
   }

   public XMLEventReader getXMLEventReader() {
      return this.fEventReader;
   }

   public boolean shouldConsumeRemainingContent() {
      return this.fConsumeRemainingContent;
   }

   public void setSystemId(String systemId) {
      throw new UnsupportedOperationException("Cannot set the system ID on a StAXInputSource");
   }

   private static String getEventReaderSystemId(XMLEventReader reader) {
      try {
         if (reader != null) {
            return reader.peek().getLocation().getSystemId();
         }
      } catch (XMLStreamException var2) {
      }

      return null;
   }
}
