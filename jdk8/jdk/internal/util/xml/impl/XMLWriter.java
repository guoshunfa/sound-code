package jdk.internal.util.xml.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import jdk.internal.util.xml.XMLStreamException;

public class XMLWriter {
   private Writer _writer;
   private CharsetEncoder _encoder = null;

   public XMLWriter(OutputStream var1, String var2, Charset var3) throws XMLStreamException {
      this._encoder = var3.newEncoder();

      try {
         this._writer = this.getWriter(var1, var2, var3);
      } catch (UnsupportedEncodingException var5) {
         throw new XMLStreamException(var5);
      }
   }

   public boolean canEncode(char var1) {
      return this._encoder == null ? false : this._encoder.canEncode(var1);
   }

   public void write(String var1) throws XMLStreamException {
      try {
         this._writer.write(var1.toCharArray());
      } catch (IOException var3) {
         throw new XMLStreamException("I/O error", var3);
      }
   }

   public void write(String var1, int var2, int var3) throws XMLStreamException {
      try {
         this._writer.write(var1, var2, var3);
      } catch (IOException var5) {
         throw new XMLStreamException("I/O error", var5);
      }
   }

   public void write(char[] var1, int var2, int var3) throws XMLStreamException {
      try {
         this._writer.write(var1, var2, var3);
      } catch (IOException var5) {
         throw new XMLStreamException("I/O error", var5);
      }
   }

   void write(int var1) throws XMLStreamException {
      try {
         this._writer.write(var1);
      } catch (IOException var3) {
         throw new XMLStreamException("I/O error", var3);
      }
   }

   void flush() throws XMLStreamException {
      try {
         this._writer.flush();
      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      }
   }

   void close() throws XMLStreamException {
      try {
         this._writer.close();
      } catch (IOException var2) {
         throw new XMLStreamException(var2);
      }
   }

   private void nl() throws XMLStreamException {
      String var1 = System.getProperty("line.separator");

      try {
         this._writer.write(var1);
      } catch (IOException var3) {
         throw new XMLStreamException("I/O error", var3);
      }
   }

   private Writer getWriter(OutputStream var1, String var2, Charset var3) throws XMLStreamException, UnsupportedEncodingException {
      return var3 != null ? new OutputStreamWriter(new BufferedOutputStream(var1), var3) : new OutputStreamWriter(new BufferedOutputStream(var1), var2);
   }
}
