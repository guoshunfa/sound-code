package jdk.internal.util.xml.impl;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import jdk.internal.util.xml.XMLStreamException;
import jdk.internal.util.xml.XMLStreamWriter;

public class XMLStreamWriterImpl implements XMLStreamWriter {
   static final int STATE_XML_DECL = 1;
   static final int STATE_PROLOG = 2;
   static final int STATE_DTD_DECL = 3;
   static final int STATE_ELEMENT = 4;
   static final int ELEMENT_STARTTAG_OPEN = 10;
   static final int ELEMENT_STARTTAG_CLOSE = 11;
   static final int ELEMENT_ENDTAG_OPEN = 12;
   static final int ELEMENT_ENDTAG_CLOSE = 13;
   public static final char CLOSE_START_TAG = '>';
   public static final char OPEN_START_TAG = '<';
   public static final String OPEN_END_TAG = "</";
   public static final char CLOSE_END_TAG = '>';
   public static final String START_CDATA = "<![CDATA[";
   public static final String END_CDATA = "]]>";
   public static final String CLOSE_EMPTY_ELEMENT = "/>";
   public static final String ENCODING_PREFIX = "&#x";
   public static final char SPACE = ' ';
   public static final char AMPERSAND = '&';
   public static final char DOUBLEQUOT = '"';
   public static final char SEMICOLON = ';';
   private int _state;
   private XMLStreamWriterImpl.Element _currentEle;
   private XMLWriter _writer;
   private String _encoding;
   boolean _escapeCharacters;
   private boolean _doIndent;
   private char[] _lineSep;

   public XMLStreamWriterImpl(OutputStream var1) throws XMLStreamException {
      this(var1, "UTF-8");
   }

   public XMLStreamWriterImpl(OutputStream var1, String var2) throws XMLStreamException {
      this._state = 0;
      this._escapeCharacters = true;
      this._doIndent = true;
      this._lineSep = System.getProperty("line.separator").toCharArray();
      Charset var3 = null;
      if (var2 == null) {
         this._encoding = "UTF-8";
      } else {
         try {
            var3 = this.getCharset(var2);
         } catch (UnsupportedEncodingException var5) {
            throw new XMLStreamException(var5);
         }

         this._encoding = var2;
      }

      this._writer = new XMLWriter(var1, var2, var3);
   }

   public void writeStartDocument() throws XMLStreamException {
      this.writeStartDocument(this._encoding, "1.0");
   }

   public void writeStartDocument(String var1) throws XMLStreamException {
      this.writeStartDocument(this._encoding, var1, (String)null);
   }

   public void writeStartDocument(String var1, String var2) throws XMLStreamException {
      this.writeStartDocument(var1, var2, (String)null);
   }

   public void writeStartDocument(String var1, String var2, String var3) throws XMLStreamException {
      if (this._state > 0) {
         throw new XMLStreamException("XML declaration must be as the first line in the XML document.");
      } else {
         this._state = 1;
         String var4 = var1;
         if (var1 == null) {
            var4 = this._encoding;
         } else {
            try {
               this.getCharset(var1);
            } catch (UnsupportedEncodingException var6) {
               throw new XMLStreamException(var6);
            }
         }

         if (var2 == null) {
            var2 = "1.0";
         }

         this._writer.write("<?xml version=\"");
         this._writer.write(var2);
         this._writer.write(34);
         if (var4 != null) {
            this._writer.write(" encoding=\"");
            this._writer.write(var4);
            this._writer.write(34);
         }

         if (var3 != null) {
            this._writer.write(" standalone=\"");
            this._writer.write(var3);
            this._writer.write(34);
         }

         this._writer.write("?>");
         this.writeLineSeparator();
      }
   }

   public void writeDTD(String var1) throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      this._writer.write(var1);
      this.writeLineSeparator();
   }

   public void writeStartElement(String var1) throws XMLStreamException {
      if (var1 != null && var1.length() != 0) {
         this._state = 4;
         if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
         }

         this._currentEle = new XMLStreamWriterImpl.Element(this._currentEle, var1, false);
         this.openStartTag();
         this._writer.write(var1);
      } else {
         throw new XMLStreamException("Local Name cannot be null or empty");
      }
   }

   public void writeEmptyElement(String var1) throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      this._currentEle = new XMLStreamWriterImpl.Element(this._currentEle, var1, true);
      this.openStartTag();
      this._writer.write(var1);
   }

   public void writeAttribute(String var1, String var2) throws XMLStreamException {
      if (this._currentEle.getState() != 10) {
         throw new XMLStreamException("Attribute not associated with any element");
      } else {
         this._writer.write(32);
         this._writer.write(var1);
         this._writer.write("=\"");
         this.writeXMLContent(var2, true, true);
         this._writer.write(34);
      }
   }

   public void writeEndDocument() throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      for(; this._currentEle != null; this._currentEle = this._currentEle.getParent()) {
         if (!this._currentEle.isEmpty()) {
            this._writer.write("</");
            this._writer.write(this._currentEle.getLocalName());
            this._writer.write(62);
         }
      }

   }

   public void writeEndElement() throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      if (this._currentEle == null) {
         throw new XMLStreamException("No element was found to write");
      } else if (!this._currentEle.isEmpty()) {
         this._writer.write("</");
         this._writer.write(this._currentEle.getLocalName());
         this._writer.write(62);
         this.writeLineSeparator();
         this._currentEle = this._currentEle.getParent();
      }
   }

   public void writeCData(String var1) throws XMLStreamException {
      if (var1 == null) {
         throw new XMLStreamException("cdata cannot be null");
      } else {
         if (this._currentEle != null && this._currentEle.getState() == 10) {
            this.closeStartTag();
         }

         this._writer.write("<![CDATA[");
         this._writer.write(var1);
         this._writer.write("]]>");
      }
   }

   public void writeCharacters(String var1) throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      this.writeXMLContent(var1);
   }

   public void writeCharacters(char[] var1, int var2, int var3) throws XMLStreamException {
      if (this._currentEle != null && this._currentEle.getState() == 10) {
         this.closeStartTag();
      }

      this.writeXMLContent(var1, var2, var3, this._escapeCharacters);
   }

   public void close() throws XMLStreamException {
      if (this._writer != null) {
         this._writer.close();
      }

      this._writer = null;
      this._currentEle = null;
      this._state = 0;
   }

   public void flush() throws XMLStreamException {
      if (this._writer != null) {
         this._writer.flush();
      }

   }

   public void setDoIndent(boolean var1) {
      this._doIndent = var1;
   }

   private void writeXMLContent(char[] var1, int var2, int var3, boolean var4) throws XMLStreamException {
      if (!var4) {
         this._writer.write(var1, var2, var3);
      } else {
         int var5 = var2;
         int var6 = var2 + var3;

         for(int var7 = var2; var7 < var6; ++var7) {
            char var8 = var1[var7];
            if (!this._writer.canEncode(var8)) {
               this._writer.write(var1, var5, var7 - var5);
               this._writer.write("&#x");
               this._writer.write(Integer.toHexString(var8));
               this._writer.write(59);
               var5 = var7 + 1;
            } else {
               switch(var8) {
               case '&':
                  this._writer.write(var1, var5, var7 - var5);
                  this._writer.write("&amp;");
                  var5 = var7 + 1;
                  break;
               case '<':
                  this._writer.write(var1, var5, var7 - var5);
                  this._writer.write("&lt;");
                  var5 = var7 + 1;
                  break;
               case '>':
                  this._writer.write(var1, var5, var7 - var5);
                  this._writer.write("&gt;");
                  var5 = var7 + 1;
               }
            }
         }

         this._writer.write(var1, var5, var6 - var5);
      }
   }

   private void writeXMLContent(String var1) throws XMLStreamException {
      if (var1 != null && var1.length() > 0) {
         this.writeXMLContent(var1, this._escapeCharacters, false);
      }

   }

   private void writeXMLContent(String var1, boolean var2, boolean var3) throws XMLStreamException {
      if (!var2) {
         this._writer.write(var1);
      } else {
         int var4 = 0;
         int var5 = var1.length();

         for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = var1.charAt(var6);
            if (!this._writer.canEncode(var7)) {
               this._writer.write(var1, var4, var6 - var4);
               this._writer.write("&#x");
               this._writer.write(Integer.toHexString(var7));
               this._writer.write(59);
               var4 = var6 + 1;
            } else {
               switch(var7) {
               case '"':
                  this._writer.write(var1, var4, var6 - var4);
                  if (var3) {
                     this._writer.write("&quot;");
                  } else {
                     this._writer.write(34);
                  }

                  var4 = var6 + 1;
                  break;
               case '&':
                  this._writer.write(var1, var4, var6 - var4);
                  this._writer.write("&amp;");
                  var4 = var6 + 1;
                  break;
               case '<':
                  this._writer.write(var1, var4, var6 - var4);
                  this._writer.write("&lt;");
                  var4 = var6 + 1;
                  break;
               case '>':
                  this._writer.write(var1, var4, var6 - var4);
                  this._writer.write("&gt;");
                  var4 = var6 + 1;
               }
            }
         }

         this._writer.write(var1, var4, var5 - var4);
      }
   }

   private void openStartTag() throws XMLStreamException {
      this._currentEle.setState(10);
      this._writer.write(60);
   }

   private void closeStartTag() throws XMLStreamException {
      if (this._currentEle.isEmpty()) {
         this._writer.write("/>");
      } else {
         this._writer.write(62);
      }

      if (this._currentEle.getParent() == null) {
         this.writeLineSeparator();
      }

      this._currentEle.setState(11);
   }

   private void writeLineSeparator() throws XMLStreamException {
      if (this._doIndent) {
         this._writer.write((char[])this._lineSep, 0, this._lineSep.length);
      }

   }

   private Charset getCharset(String var1) throws UnsupportedEncodingException {
      if (var1.equalsIgnoreCase("UTF-32")) {
         throw new UnsupportedEncodingException("The basic XMLWriter does not support " + var1);
      } else {
         try {
            Charset var2 = Charset.forName(var1);
            return var2;
         } catch (UnsupportedCharsetException | IllegalCharsetNameException var4) {
            throw new UnsupportedEncodingException(var1);
         }
      }
   }

   protected class Element {
      protected XMLStreamWriterImpl.Element _parent;
      protected short _Depth;
      boolean _isEmptyElement = false;
      String _localpart;
      int _state;

      public Element() {
      }

      public Element(XMLStreamWriterImpl.Element var2, String var3, boolean var4) {
         this._parent = var2;
         this._localpart = var3;
         this._isEmptyElement = var4;
      }

      public XMLStreamWriterImpl.Element getParent() {
         return this._parent;
      }

      public String getLocalName() {
         return this._localpart;
      }

      public int getState() {
         return this._state;
      }

      public void setState(int var1) {
         this._state = var1;
      }

      public boolean isEmpty() {
         return this._isEmptyElement;
      }
   }
}
