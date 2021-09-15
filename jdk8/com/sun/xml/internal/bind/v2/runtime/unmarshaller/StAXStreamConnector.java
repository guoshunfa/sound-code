package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.lang.reflect.Constructor;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class StAXStreamConnector extends StAXConnector {
   private final XMLStreamReader staxStreamReader;
   protected final StringBuilder buffer = new StringBuilder();
   protected boolean textReported = false;
   private final Attributes attributes = new Attributes() {
      public int getLength() {
         return StAXStreamConnector.this.staxStreamReader.getAttributeCount();
      }

      public String getURI(int index) {
         String uri = StAXStreamConnector.this.staxStreamReader.getAttributeNamespace(index);
         return uri == null ? "" : uri;
      }

      public String getLocalName(int index) {
         return StAXStreamConnector.this.staxStreamReader.getAttributeLocalName(index);
      }

      public String getQName(int index) {
         String prefix = StAXStreamConnector.this.staxStreamReader.getAttributePrefix(index);
         return prefix != null && prefix.length() != 0 ? prefix + ':' + this.getLocalName(index) : this.getLocalName(index);
      }

      public String getType(int index) {
         return StAXStreamConnector.this.staxStreamReader.getAttributeType(index);
      }

      public String getValue(int index) {
         return StAXStreamConnector.this.staxStreamReader.getAttributeValue(index);
      }

      public int getIndex(String uri, String localName) {
         for(int i = this.getLength() - 1; i >= 0; --i) {
            if (localName.equals(this.getLocalName(i)) && uri.equals(this.getURI(i))) {
               return i;
            }
         }

         return -1;
      }

      public int getIndex(String qName) {
         for(int i = this.getLength() - 1; i >= 0; --i) {
            if (qName.equals(this.getQName(i))) {
               return i;
            }
         }

         return -1;
      }

      public String getType(String uri, String localName) {
         int index = this.getIndex(uri, localName);
         return index < 0 ? null : this.getType(index);
      }

      public String getType(String qName) {
         int index = this.getIndex(qName);
         return index < 0 ? null : this.getType(index);
      }

      public String getValue(String uri, String localName) {
         int index = this.getIndex(uri, localName);
         return index < 0 ? null : this.getValue(index);
      }

      public String getValue(String qName) {
         int index = this.getIndex(qName);
         return index < 0 ? null : this.getValue(index);
      }
   };
   private static final Class FI_STAX_READER_CLASS = initFIStAXReaderClass();
   private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();
   private static final Class STAX_EX_READER_CLASS = initStAXExReader();
   private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR = initStAXExConnector();

   public static StAXConnector create(XMLStreamReader reader, XmlVisitor visitor) {
      Class readerClass = reader.getClass();
      if (FI_STAX_READER_CLASS != null && FI_STAX_READER_CLASS.isAssignableFrom(readerClass) && FI_CONNECTOR_CTOR != null) {
         try {
            return (StAXConnector)FI_CONNECTOR_CTOR.newInstance(reader, visitor);
         } catch (Exception var6) {
         }
      }

      boolean isZephyr = readerClass.getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
      if ((!getBoolProp(reader, "org.codehaus.stax2.internNames") || !getBoolProp(reader, "org.codehaus.stax2.internNsUris")) && !isZephyr && !checkImplementaionNameOfSjsxp(reader)) {
         visitor = new InterningXmlVisitor((XmlVisitor)visitor);
      }

      if (STAX_EX_READER_CLASS != null && STAX_EX_READER_CLASS.isAssignableFrom(readerClass)) {
         try {
            return (StAXConnector)STAX_EX_CONNECTOR_CTOR.newInstance(reader, visitor);
         } catch (Exception var5) {
         }
      }

      return new StAXStreamConnector(reader, (XmlVisitor)visitor);
   }

   private static boolean checkImplementaionNameOfSjsxp(XMLStreamReader reader) {
      try {
         Object name = reader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
         return name != null && name.equals("sjsxp");
      } catch (Exception var2) {
         return false;
      }
   }

   private static boolean getBoolProp(XMLStreamReader r, String n) {
      try {
         Object o = r.getProperty(n);
         return o instanceof Boolean ? (Boolean)o : false;
      } catch (Exception var3) {
         return false;
      }
   }

   protected StAXStreamConnector(XMLStreamReader staxStreamReader, XmlVisitor visitor) {
      super(visitor);
      this.staxStreamReader = staxStreamReader;
   }

   public void bridge() throws XMLStreamException {
      try {
         int depth = 0;
         int event = this.staxStreamReader.getEventType();
         if (event == 7) {
            while(!this.staxStreamReader.isStartElement()) {
               event = this.staxStreamReader.next();
            }
         }

         if (event != 1) {
            throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
         } else {
            this.handleStartDocument(this.staxStreamReader.getNamespaceContext());

            while(true) {
               switch(event) {
               case 1:
                  this.handleStartElement();
                  ++depth;
                  break;
               case 2:
                  --depth;
                  this.handleEndElement();
                  if (depth == 0) {
                     this.staxStreamReader.next();
                     this.handleEndDocument();
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
                  this.handleCharacters();
               }

               event = this.staxStreamReader.next();
            }
         }
      } catch (SAXException var3) {
         throw new XMLStreamException(var3);
      }
   }

   protected Location getCurrentLocation() {
      return this.staxStreamReader.getLocation();
   }

   protected String getCurrentQName() {
      return this.getQName(this.staxStreamReader.getPrefix(), this.staxStreamReader.getLocalName());
   }

   private void handleEndElement() throws SAXException {
      this.processText(false);
      this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
      this.tagName.local = this.staxStreamReader.getLocalName();
      this.visitor.endElement(this.tagName);
      int nsCount = this.staxStreamReader.getNamespaceCount();

      for(int i = nsCount - 1; i >= 0; --i) {
         this.visitor.endPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(i)));
      }

   }

   private void handleStartElement() throws SAXException {
      this.processText(true);
      int nsCount = this.staxStreamReader.getNamespaceCount();

      for(int i = 0; i < nsCount; ++i) {
         this.visitor.startPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(i)), fixNull(this.staxStreamReader.getNamespaceURI(i)));
      }

      this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
      this.tagName.local = this.staxStreamReader.getLocalName();
      this.tagName.atts = this.attributes;
      this.visitor.startElement(this.tagName);
   }

   protected void handleCharacters() throws XMLStreamException, SAXException {
      if (this.predictor.expectText()) {
         this.buffer.append(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength());
      }

   }

   private void processText(boolean ignorable) throws SAXException {
      if (this.predictor.expectText() && (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer) || this.context.getCurrentState().isMixed())) {
         if (this.textReported) {
            this.textReported = false;
         } else {
            this.visitor.text(this.buffer);
         }
      }

      this.buffer.setLength(0);
   }

   private static Class initFIStAXReaderClass() {
      try {
         Class<?> fisr = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader");
         Class<?> sdp = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
         return fisr.isAssignableFrom(sdp) ? sdp : null;
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass() {
      try {
         if (FI_STAX_READER_CLASS == null) {
            return null;
         } else {
            Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.FastInfosetConnector");
            return c.getConstructor(FI_STAX_READER_CLASS, XmlVisitor.class);
         }
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Class initStAXExReader() {
      try {
         return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx");
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Constructor<? extends StAXConnector> initStAXExConnector() {
      try {
         Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.StAXExConnector");
         return c.getConstructor(STAX_EX_READER_CLASS, XmlVisitor.class);
      } catch (Throwable var1) {
         return null;
      }
   }
}
