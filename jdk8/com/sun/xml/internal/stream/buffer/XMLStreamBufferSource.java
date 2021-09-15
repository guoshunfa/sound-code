package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import java.io.ByteArrayInputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XMLStreamBufferSource extends SAXSource {
   protected XMLStreamBuffer _buffer;
   protected SAXBufferProcessor _bufferProcessor;

   public XMLStreamBufferSource(XMLStreamBuffer buffer) {
      super(new InputSource(new ByteArrayInputStream(new byte[0])));
      this.setXMLStreamBuffer(buffer);
   }

   public XMLStreamBuffer getXMLStreamBuffer() {
      return this._buffer;
   }

   public void setXMLStreamBuffer(XMLStreamBuffer buffer) {
      if (buffer == null) {
         throw new NullPointerException("buffer cannot be null");
      } else {
         this._buffer = buffer;
         if (this._bufferProcessor != null) {
            this._bufferProcessor.setBuffer(this._buffer, false);
         }

      }
   }

   public XMLReader getXMLReader() {
      if (this._bufferProcessor == null) {
         this._bufferProcessor = new SAXBufferProcessor(this._buffer, false);
         this.setXMLReader(this._bufferProcessor);
      } else if (super.getXMLReader() == null) {
         this.setXMLReader(this._bufferProcessor);
      }

      return this._bufferProcessor;
   }
}
