package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.xml.sax.SAXException;

public class XML11Serializer extends XMLSerializer {
   protected static final boolean DEBUG = false;
   protected NamespaceSupport fNSBinder;
   protected NamespaceSupport fLocalNSBinder;
   protected SymbolTable fSymbolTable;
   protected boolean fDOML1 = false;
   protected int fNamespaceCounter = 1;
   protected static final String PREFIX = "NS";
   protected boolean fNamespaces = false;
   private boolean fPreserveSpace;

   public XML11Serializer() {
      this._format.setVersion("1.1");
   }

   public XML11Serializer(OutputFormat format) {
      super(format);
      this._format.setVersion("1.1");
   }

   public XML11Serializer(Writer writer, OutputFormat format) {
      super(writer, format);
      this._format.setVersion("1.1");
   }

   public XML11Serializer(OutputStream output, OutputFormat format) {
      super(output, format != null ? format : new OutputFormat("xml", (String)null, false));
      this._format.setVersion("1.1");
   }

   public void characters(char[] chars, int start, int length) throws SAXException {
      try {
         ElementState state = this.content();
         int saveIndent;
         if (!state.inCData && !state.doCData) {
            if (state.preserveSpace) {
               saveIndent = this._printer.getNextIndent();
               this._printer.setNextIndent(0);
               this.printText(chars, start, length, true, state.unescaped);
               this._printer.setNextIndent(saveIndent);
            } else {
               this.printText(chars, start, length, false, state.unescaped);
            }
         } else {
            if (!state.inCData) {
               this._printer.printText("<![CDATA[");
               state.inCData = true;
            }

            saveIndent = this._printer.getNextIndent();
            this._printer.setNextIndent(0);
            int end = start + length;

            for(int index = start; index < end; ++index) {
               char ch = chars[index];
               if (ch == ']' && index + 2 < end && chars[index + 1] == ']' && chars[index + 2] == '>') {
                  this._printer.printText("]]]]><![CDATA[>");
                  index += 2;
               } else if (!XML11Char.isXML11Valid(ch)) {
                  ++index;
                  if (index < end) {
                     this.surrogates(ch, chars[index]);
                  } else {
                     this.fatalError("The character '" + ch + "' is an invalid XML character");
                  }
               } else if (this._encodingInfo.isPrintable(ch) && XML11Char.isXML11ValidLiteral(ch)) {
                  this._printer.printText(ch);
               } else {
                  this._printer.printText("]]>&#x");
                  this._printer.printText(Integer.toHexString(ch));
                  this._printer.printText(";<![CDATA[");
               }
            }

            this._printer.setNextIndent(saveIndent);
         }

      } catch (IOException var9) {
         throw new SAXException(var9);
      }
   }

   protected void printEscaped(String source) throws IOException {
      int length = source.length();

      for(int i = 0; i < length; ++i) {
         int ch = source.charAt(i);
         if (!XML11Char.isXML11Valid(ch)) {
            ++i;
            if (i < length) {
               this.surrogates(ch, source.charAt(i));
            } else {
               this.fatalError("The character '" + (char)ch + "' is an invalid XML character");
            }
         } else if (ch != '\n' && ch != '\r' && ch != '\t' && ch != 133 && ch != 8232) {
            if (ch == '<') {
               this._printer.printText("&lt;");
            } else if (ch == '&') {
               this._printer.printText("&amp;");
            } else if (ch == '"') {
               this._printer.printText("&quot;");
            } else if (ch >= ' ' && this._encodingInfo.isPrintable((char)ch)) {
               this._printer.printText((char)ch);
            } else {
               this.printHex(ch);
            }
         } else {
            this.printHex(ch);
         }
      }

   }

   protected final void printCDATAText(String text) throws IOException {
      int length = text.length();

      for(int index = 0; index < length; ++index) {
         char ch = text.charAt(index);
         if (ch == ']' && index + 2 < length && text.charAt(index + 1) == ']' && text.charAt(index + 2) == '>') {
            if (this.fDOMErrorHandler != null) {
               String msg;
               if ((this.features & 16) == 0 && (this.features & 2) == 0) {
                  msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", (Object[])null);
                  this.modifyDOMError(msg, (short)3, (String)null, this.fCurrentNode);
                  boolean continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                  if (!continueProcess) {
                     throw new IOException();
                  }
               } else {
                  msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", (Object[])null);
                  this.modifyDOMError(msg, (short)1, (String)null, this.fCurrentNode);
                  this.fDOMErrorHandler.handleError(this.fDOMError);
               }
            }

            this._printer.printText("]]]]><![CDATA[>");
            index += 2;
         } else if (!XML11Char.isXML11Valid(ch)) {
            ++index;
            if (index < length) {
               this.surrogates(ch, text.charAt(index));
            } else {
               this.fatalError("The character '" + ch + "' is an invalid XML character");
            }
         } else if (this._encodingInfo.isPrintable(ch) && XML11Char.isXML11ValidLiteral(ch)) {
            this._printer.printText(ch);
         } else {
            this._printer.printText("]]>&#x");
            this._printer.printText(Integer.toHexString(ch));
            this._printer.printText(";<![CDATA[");
         }
      }

   }

   protected final void printXMLChar(int ch) throws IOException {
      if (ch != 13 && ch != 133 && ch != 8232) {
         if (ch == 60) {
            this._printer.printText("&lt;");
         } else if (ch == 38) {
            this._printer.printText("&amp;");
         } else if (ch == 62) {
            this._printer.printText("&gt;");
         } else if (this._encodingInfo.isPrintable((char)ch) && XML11Char.isXML11ValidLiteral(ch)) {
            this._printer.printText((char)ch);
         } else {
            this.printHex(ch);
         }
      } else {
         this.printHex(ch);
      }

   }

   protected final void surrogates(int high, int low) throws IOException {
      if (XMLChar.isHighSurrogate(high)) {
         if (!XMLChar.isLowSurrogate(low)) {
            this.fatalError("The character '" + (char)low + "' is an invalid XML character");
         } else {
            int supplemental = XMLChar.supplemental((char)high, (char)low);
            if (!XML11Char.isXML11Valid(supplemental)) {
               this.fatalError("The character '" + (char)supplemental + "' is an invalid XML character");
            } else if (this.content().inCData) {
               this._printer.printText("]]>&#x");
               this._printer.printText(Integer.toHexString(supplemental));
               this._printer.printText(";<![CDATA[");
            } else {
               this.printHex(supplemental);
            }
         }
      } else {
         this.fatalError("The character '" + (char)high + "' is an invalid XML character");
      }

   }

   protected void printText(String text, boolean preserveSpace, boolean unescaped) throws IOException {
      int length = text.length();
      int index;
      char ch;
      if (preserveSpace) {
         for(index = 0; index < length; ++index) {
            ch = text.charAt(index);
            if (!XML11Char.isXML11Valid(ch)) {
               ++index;
               if (index < length) {
                  this.surrogates(ch, text.charAt(index));
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      } else {
         for(index = 0; index < length; ++index) {
            ch = text.charAt(index);
            if (!XML11Char.isXML11Valid(ch)) {
               ++index;
               if (index < length) {
                  this.surrogates(ch, text.charAt(index));
               } else {
                  this.fatalError("The character '" + ch + "' is an invalid XML character");
               }
            } else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
               this._printer.printText(ch);
            } else {
               this.printXMLChar(ch);
            }
         }
      }

   }

   protected void printText(char[] chars, int start, int length, boolean preserveSpace, boolean unescaped) throws IOException {
      char ch;
      if (preserveSpace) {
         while(true) {
            while(length-- > 0) {
               ch = chars[start++];
               if (!XML11Char.isXML11Valid(ch)) {
                  if (length-- > 0) {
                     this.surrogates(ch, chars[start++]);
                  } else {
                     this.fatalError("The character '" + ch + "' is an invalid XML character");
                  }
               } else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                  this._printer.printText(ch);
               } else {
                  this.printXMLChar(ch);
               }
            }

            return;
         }
      } else {
         while(true) {
            while(length-- > 0) {
               ch = chars[start++];
               if (!XML11Char.isXML11Valid(ch)) {
                  if (length-- > 0) {
                     this.surrogates(ch, chars[start++]);
                  } else {
                     this.fatalError("The character '" + ch + "' is an invalid XML character");
                  }
               } else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                  this._printer.printText(ch);
               } else {
                  this.printXMLChar(ch);
               }
            }

            return;
         }
      }
   }

   public boolean reset() {
      super.reset();
      return true;
   }
}
