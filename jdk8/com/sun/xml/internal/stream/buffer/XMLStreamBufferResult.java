package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class XMLStreamBufferResult extends SAXResult {
   protected MutableXMLStreamBuffer _buffer;
   protected SAXBufferCreator _bufferCreator;

   public XMLStreamBufferResult() {
      this.setXMLStreamBuffer(new MutableXMLStreamBuffer());
   }

   public XMLStreamBufferResult(MutableXMLStreamBuffer buffer) {
      this.setXMLStreamBuffer(buffer);
   }

   public MutableXMLStreamBuffer getXMLStreamBuffer() {
      return this._buffer;
   }

   public void setXMLStreamBuffer(MutableXMLStreamBuffer buffer) {
      if (buffer == null) {
         throw new NullPointerException("buffer cannot be null");
      } else {
         this._buffer = buffer;
         this.setSystemId(this._buffer.getSystemId());
         if (this._bufferCreator != null) {
            this._bufferCreator.setXMLStreamBuffer(this._buffer);
         }

      }
   }

   public ContentHandler getHandler() {
      if (this._bufferCreator == null) {
         this._bufferCreator = new SAXBufferCreator(this._buffer);
         this.setHandler(this._bufferCreator);
      } else if (super.getHandler() == null) {
         this.setHandler(this._bufferCreator);
      }

      return this._bufferCreator;
   }

   public LexicalHandler getLexicalHandler() {
      return (LexicalHandler)this.getHandler();
   }
}
