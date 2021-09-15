package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class DummyEvent implements XMLEvent {
   private static DummyEvent.DummyLocation nowhere = new DummyEvent.DummyLocation();
   private int fEventType;
   protected Location fLocation;

   public DummyEvent() {
      this.fLocation = nowhere;
   }

   public DummyEvent(int i) {
      this.fLocation = nowhere;
      this.fEventType = i;
   }

   public int getEventType() {
      return this.fEventType;
   }

   protected void setEventType(int eventType) {
      this.fEventType = eventType;
   }

   public boolean isStartElement() {
      return this.fEventType == 1;
   }

   public boolean isEndElement() {
      return this.fEventType == 2;
   }

   public boolean isEntityReference() {
      return this.fEventType == 9;
   }

   public boolean isProcessingInstruction() {
      return this.fEventType == 3;
   }

   public boolean isCharacterData() {
      return this.fEventType == 4;
   }

   public boolean isStartDocument() {
      return this.fEventType == 7;
   }

   public boolean isEndDocument() {
      return this.fEventType == 8;
   }

   public Location getLocation() {
      return this.fLocation;
   }

   void setLocation(Location loc) {
      if (loc == null) {
         this.fLocation = nowhere;
      } else {
         this.fLocation = loc;
      }

   }

   public Characters asCharacters() {
      return (Characters)this;
   }

   public EndElement asEndElement() {
      return (EndElement)this;
   }

   public StartElement asStartElement() {
      return (StartElement)this;
   }

   public QName getSchemaType() {
      return null;
   }

   public boolean isAttribute() {
      return this.fEventType == 10;
   }

   public boolean isCharacters() {
      return this.fEventType == 4;
   }

   public boolean isNamespace() {
      return this.fEventType == 13;
   }

   public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
      try {
         this.writeAsEncodedUnicodeEx(writer);
      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      }
   }

   protected abstract void writeAsEncodedUnicodeEx(Writer var1) throws IOException, XMLStreamException;

   protected void charEncode(Writer writer, String data) throws IOException {
      if (data != null && data != "") {
         int i = 0;
         int start = 0;

         int len;
         for(len = data.length(); i < len; ++i) {
            switch(data.charAt(i)) {
            case '"':
               writer.write(data, start, i - start);
               writer.write("&quot;");
               start = i + 1;
               break;
            case '&':
               writer.write(data, start, i - start);
               writer.write("&amp;");
               start = i + 1;
               break;
            case '<':
               writer.write(data, start, i - start);
               writer.write("&lt;");
               start = i + 1;
               break;
            case '>':
               writer.write(data, start, i - start);
               writer.write("&gt;");
               start = i + 1;
            }
         }

         writer.write(data, start, len - start);
      }
   }

   static class DummyLocation implements Location {
      public DummyLocation() {
      }

      public int getCharacterOffset() {
         return -1;
      }

      public int getColumnNumber() {
         return -1;
      }

      public int getLineNumber() {
         return -1;
      }

      public String getPublicId() {
         return null;
      }

      public String getSystemId() {
         return null;
      }
   }
}
