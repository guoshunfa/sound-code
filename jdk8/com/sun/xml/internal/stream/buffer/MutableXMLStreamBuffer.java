package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MutableXMLStreamBuffer extends XMLStreamBuffer {
   public static final int DEFAULT_ARRAY_SIZE = 512;

   public MutableXMLStreamBuffer() {
      this(512);
   }

   public void setSystemId(String systemId) {
      this.systemId = systemId;
   }

   public MutableXMLStreamBuffer(int size) {
      this._structure = new FragmentedArray(new byte[size]);
      this._structureStrings = new FragmentedArray(new String[size]);
      this._contentCharactersBuffer = new FragmentedArray(new char[4096]);
      this._contentObjects = new FragmentedArray(new Object[size]);
      ((byte[])this._structure.getArray())[0] = -112;
   }

   public void createFromXMLStreamReader(XMLStreamReader reader) throws XMLStreamException {
      this.reset();
      StreamReaderBufferCreator c = new StreamReaderBufferCreator(this);
      c.create(reader);
   }

   public XMLStreamWriter createFromXMLStreamWriter() {
      this.reset();
      return new StreamWriterBufferCreator(this);
   }

   public SAXBufferCreator createFromSAXBufferCreator() {
      this.reset();
      SAXBufferCreator c = new SAXBufferCreator();
      c.setBuffer(this);
      return c;
   }

   public void createFromXMLReader(XMLReader reader, InputStream in) throws SAXException, IOException {
      this.createFromXMLReader(reader, in, (String)null);
   }

   public void createFromXMLReader(XMLReader reader, InputStream in, String systemId) throws SAXException, IOException {
      this.reset();
      SAXBufferCreator c = new SAXBufferCreator(this);
      reader.setContentHandler(c);
      reader.setDTDHandler(c);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", c);
      c.create(reader, in, systemId);
   }

   public void reset() {
      this._structurePtr = this._structureStringsPtr = this._contentCharactersBufferPtr = this._contentObjectsPtr = 0;
      ((byte[])this._structure.getArray())[0] = -112;
      this._contentObjects.setNext((FragmentedArray)null);
      Object[] o = (Object[])this._contentObjects.getArray();

      for(int i = 0; i < o.length && o[i] != null; ++i) {
         o[i] = null;
      }

      this.treeCount = 0;
   }

   protected void setHasInternedStrings(boolean hasInternedStrings) {
      this._hasInternedStrings = hasInternedStrings;
   }
}
