package com.sun.org.apache.xerces.internal.dom;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.ls.LSOutput;

public class DOMOutputImpl implements LSOutput {
   protected Writer fCharStream = null;
   protected OutputStream fByteStream = null;
   protected String fSystemId = null;
   protected String fEncoding = null;

   public Writer getCharacterStream() {
      return this.fCharStream;
   }

   public void setCharacterStream(Writer characterStream) {
      this.fCharStream = characterStream;
   }

   public OutputStream getByteStream() {
      return this.fByteStream;
   }

   public void setByteStream(OutputStream byteStream) {
      this.fByteStream = byteStream;
   }

   public String getSystemId() {
      return this.fSystemId;
   }

   public void setSystemId(String systemId) {
      this.fSystemId = systemId;
   }

   public String getEncoding() {
      return this.fEncoding;
   }

   public void setEncoding(String encoding) {
      this.fEncoding = encoding;
   }
}
