package com.sun.xml.internal.txw2.output;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter extends XMLFilterImpl implements LexicalHandler {
   private final HashMap locallyDeclaredPrefix;
   private final Attributes EMPTY_ATTS;
   private boolean inCDATA;
   private int elementLevel;
   private Writer output;
   private String encoding;
   private boolean writeXmlDecl;
   private String header;
   private final CharacterEscapeHandler escapeHandler;
   private boolean startTagIsClosed;

   public XMLWriter(Writer writer, String encoding, CharacterEscapeHandler _escapeHandler) {
      this.locallyDeclaredPrefix = new HashMap();
      this.EMPTY_ATTS = new AttributesImpl();
      this.inCDATA = false;
      this.elementLevel = 0;
      this.writeXmlDecl = true;
      this.header = null;
      this.startTagIsClosed = true;
      this.init(writer, encoding);
      this.escapeHandler = _escapeHandler;
   }

   public XMLWriter(Writer writer, String encoding) {
      this(writer, encoding, DumbEscapeHandler.theInstance);
   }

   private void init(Writer writer, String encoding) {
      this.setOutput(writer, encoding);
   }

   public void reset() {
      this.elementLevel = 0;
      this.startTagIsClosed = true;
   }

   public void flush() throws IOException {
      this.output.flush();
   }

   public void setOutput(Writer writer, String _encoding) {
      if (writer == null) {
         this.output = new OutputStreamWriter(System.out);
      } else {
         this.output = writer;
      }

      this.encoding = _encoding;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setXmlDecl(boolean _writeXmlDecl) {
      this.writeXmlDecl = _writeXmlDecl;
   }

   public void setHeader(String _header) {
      this.header = _header;
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      this.locallyDeclaredPrefix.put(prefix, uri);
   }

   public void startDocument() throws SAXException {
      try {
         this.reset();
         if (this.writeXmlDecl) {
            String e = "";
            if (this.encoding != null) {
               e = " encoding=\"" + this.encoding + "\"";
            }

            this.write("<?xml version=\"1.0\"" + e + " standalone=\"yes\"?>\n");
         }

         if (this.header != null) {
            this.write(this.header);
         }

         super.startDocument();
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void endDocument() throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write("/>");
            this.startTagIsClosed = true;
         }

         this.write('\n');
         super.endDocument();

         try {
            this.flush();
         } catch (IOException var2) {
            throw new SAXException(var2);
         }
      } catch (IOException var3) {
         throw new SAXException(var3);
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write(">");
         }

         ++this.elementLevel;
         this.write('<');
         this.writeName(uri, localName, qName, true);
         this.writeAttributes(atts);
         if (!this.locallyDeclaredPrefix.isEmpty()) {
            Iterator itr = this.locallyDeclaredPrefix.entrySet().iterator();

            while(itr.hasNext()) {
               Map.Entry e = (Map.Entry)itr.next();
               String p = (String)e.getKey();
               String u = (String)e.getValue();
               if (u == null) {
                  u = "";
               }

               this.write(' ');
               if ("".equals(p)) {
                  this.write("xmlns=\"");
               } else {
                  this.write("xmlns:");
                  this.write(p);
                  this.write("=\"");
               }

               char[] ch = u.toCharArray();
               this.writeEsc(ch, 0, ch.length, true);
               this.write('"');
            }

            this.locallyDeclaredPrefix.clear();
         }

         super.startElement(uri, localName, qName, atts);
         this.startTagIsClosed = false;
      } catch (IOException var10) {
         throw new SAXException(var10);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      try {
         if (this.startTagIsClosed) {
            this.write("</");
            this.writeName(uri, localName, qName, true);
            this.write('>');
         } else {
            this.write("/>");
            this.startTagIsClosed = true;
         }

         if (this.elementLevel == 1) {
            this.write('\n');
         }

         super.endElement(uri, localName, qName);
         --this.elementLevel;
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void characters(char[] ch, int start, int len) throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write('>');
            this.startTagIsClosed = true;
         }

         if (this.inCDATA) {
            this.output.write(ch, start, len);
         } else {
            this.writeEsc(ch, start, len, false);
         }

         super.characters(ch, start, len);
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      try {
         this.writeEsc(ch, start, length, false);
         super.ignorableWhitespace(ch, start, length);
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write('>');
            this.startTagIsClosed = true;
         }

         this.write("<?");
         this.write(target);
         this.write(' ');
         this.write(data);
         this.write("?>");
         if (this.elementLevel < 1) {
            this.write('\n');
         }

         super.processingInstruction(target, data);
      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public void startElement(String uri, String localName) throws SAXException {
      this.startElement(uri, localName, "", this.EMPTY_ATTS);
   }

   public void startElement(String localName) throws SAXException {
      this.startElement("", localName, "", this.EMPTY_ATTS);
   }

   public void endElement(String uri, String localName) throws SAXException {
      this.endElement(uri, localName, "");
   }

   public void endElement(String localName) throws SAXException {
      this.endElement("", localName, "");
   }

   public void dataElement(String uri, String localName, String qName, Attributes atts, String content) throws SAXException {
      this.startElement(uri, localName, qName, atts);
      this.characters(content);
      this.endElement(uri, localName, qName);
   }

   public void dataElement(String uri, String localName, String content) throws SAXException {
      this.dataElement(uri, localName, "", this.EMPTY_ATTS, content);
   }

   public void dataElement(String localName, String content) throws SAXException {
      this.dataElement("", localName, "", this.EMPTY_ATTS, content);
   }

   public void characters(String data) throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write('>');
            this.startTagIsClosed = true;
         }

         char[] ch = data.toCharArray();
         this.characters(ch, 0, ch.length);
      } catch (IOException var3) {
         throw new SAXException(var3);
      }
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
      try {
         if (!this.startTagIsClosed) {
            this.write('>');
            this.startTagIsClosed = true;
         }

         this.write("<![CDATA[");
         this.inCDATA = true;
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void endCDATA() throws SAXException {
      try {
         this.inCDATA = false;
         this.write("]]>");
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      try {
         this.output.write("<!--");
         this.output.write(ch, start, length);
         this.output.write("-->");
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   private void write(char c) throws IOException {
      this.output.write(c);
   }

   private void write(String s) throws IOException {
      this.output.write(s);
   }

   private void writeAttributes(Attributes atts) throws IOException, SAXException {
      int len = atts.getLength();

      for(int i = 0; i < len; ++i) {
         char[] ch = atts.getValue(i).toCharArray();
         this.write(' ');
         this.writeName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), false);
         this.write("=\"");
         this.writeEsc(ch, 0, ch.length, true);
         this.write('"');
      }

   }

   private void writeEsc(char[] ch, int start, int length, boolean isAttVal) throws SAXException, IOException {
      this.escapeHandler.escape(ch, start, length, isAttVal, this.output);
   }

   private void writeName(String uri, String localName, String qName, boolean isElement) throws IOException {
      this.write(qName);
   }
}
