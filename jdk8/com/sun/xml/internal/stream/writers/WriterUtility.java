package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class WriterUtility {
   public static final String START_COMMENT = "<!--";
   public static final String END_COMMENT = "-->";
   public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
   public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
   public static final String DEFAULT_XML_VERSION = "1.0";
   public static final char CLOSE_START_TAG = '>';
   public static final char OPEN_START_TAG = '<';
   public static final String OPEN_END_TAG = "</";
   public static final char CLOSE_END_TAG = '>';
   public static final String START_CDATA = "<![CDATA[";
   public static final String END_CDATA = "]]>";
   public static final String CLOSE_EMPTY_ELEMENT = "/>";
   public static final String SPACE = " ";
   public static final String UTF_8 = "utf-8";
   static final boolean DEBUG_XML_CONTENT = false;
   boolean fEscapeCharacters = true;
   Writer fWriter = null;
   CharsetEncoder fEncoder;

   public WriterUtility() {
      this.fEncoder = this.getDefaultEncoder();
   }

   public WriterUtility(Writer writer) {
      this.fWriter = writer;
      String charset;
      if (writer instanceof OutputStreamWriter) {
         charset = ((OutputStreamWriter)writer).getEncoding();
         if (charset != null) {
            this.fEncoder = Charset.forName(charset).newEncoder();
         }
      } else if (writer instanceof FileWriter) {
         charset = ((FileWriter)writer).getEncoding();
         if (charset != null) {
            this.fEncoder = Charset.forName(charset).newEncoder();
         }
      } else {
         this.fEncoder = this.getDefaultEncoder();
      }

   }

   public void setWriter(Writer writer) {
      this.fWriter = writer;
   }

   public void setEscapeCharacters(boolean escape) {
      this.fEscapeCharacters = escape;
   }

   public boolean getEscapeCharacters() {
      return this.fEscapeCharacters;
   }

   public void writeXMLContent(char[] content, int start, int length) throws IOException {
      this.writeXMLContent(content, start, length, this.getEscapeCharacters());
   }

   private void writeXMLContent(char[] content, int start, int length, boolean escapeCharacter) throws IOException {
      int end = start + length;
      int startWritePos = start;

      for(int index = start; index < end; ++index) {
         char ch = content[index];
         if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
            this.fWriter.write(content, startWritePos, index - startWritePos);
            this.fWriter.write("&#x");
            this.fWriter.write(Integer.toHexString(ch));
            this.fWriter.write(59);
            startWritePos = index + 1;
         }

         switch(ch) {
         case '&':
            if (escapeCharacter) {
               this.fWriter.write(content, startWritePos, index - startWritePos);
               this.fWriter.write("&amp;");
               startWritePos = index + 1;
            }
            break;
         case '<':
            if (escapeCharacter) {
               this.fWriter.write(content, startWritePos, index - startWritePos);
               this.fWriter.write("&lt;");
               startWritePos = index + 1;
            }
            break;
         case '>':
            if (escapeCharacter) {
               this.fWriter.write(content, startWritePos, index - startWritePos);
               this.fWriter.write("&gt;");
               startWritePos = index + 1;
            }
         }
      }

      this.fWriter.write(content, startWritePos, end - startWritePos);
   }

   public void writeXMLContent(String content) throws IOException {
      if (content != null && content.length() != 0) {
         this.writeXMLContent(content.toCharArray(), 0, content.length());
      }
   }

   public void writeXMLAttributeValue(String value) throws IOException {
      this.writeXMLContent(value.toCharArray(), 0, value.length(), true);
   }

   private CharsetEncoder getDefaultEncoder() {
      try {
         String encoding = SecuritySupport.getSystemProperty("file.encoding");
         if (encoding != null) {
            return Charset.forName(encoding).newEncoder();
         }
      } catch (Exception var2) {
      }

      return null;
   }
}
