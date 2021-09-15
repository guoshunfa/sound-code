package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.Entity;
import java.io.IOException;

public class XML11EntityScanner extends XMLEntityScanner {
   public int peekChar() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (!this.fCurrentEntity.isExternal()) {
         return c;
      } else {
         return c != '\r' && c != 133 && c != 8232 ? c : 10;
      }
   }

   protected int scanChar(XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
      boolean external = false;
      if (c == '\n' || (c == '\r' || c == 133 || c == 8232) && (external = this.fCurrentEntity.isExternal())) {
         ++this.fCurrentEntity.lineNumber;
         this.fCurrentEntity.columnNumber = 1;
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)c;
            this.load(1, false, false);
            offset = 0;
         }

         if (c == '\r' && external) {
            int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (cc != '\n' && cc != 133) {
               --this.fCurrentEntity.position;
            }
         }

         c = '\n';
      }

      ++this.fCurrentEntity.columnNumber;
      if (!this.detectingVersion) {
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
      }

      return c;
   }

   protected String scanNmtoken() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;

      while(true) {
         char ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         int length;
         char[] tmp;
         if (XML11Char.isXML11Name(ch)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         } else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
               break;
            }

            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  --this.fCurrentEntity.startPosition;
                  --this.fCurrentEntity.position;
                  break;
               }
            }

            char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) {
               --this.fCurrentEntity.position;
               break;
            }

            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               int length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         }
      }

      int length = this.fCurrentEntity.position - offset;
      Entity.ScannedEntity var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      String symbol = null;
      if (length > 0) {
         symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
      }

      return symbol;
   }

   protected String scanName(XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      char ch = this.fCurrentEntity.ch[offset];
      Entity.ScannedEntity var10000;
      String symbol;
      if (XML11Char.isXML11NameStart(ch)) {
         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               ++this.fCurrentEntity.columnNumber;
               String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
               return symbol;
            }
         }
      } else {
         if (!XML11Char.isXML11NameHighSurrogate(ch)) {
            return null;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               --this.fCurrentEntity.position;
               --this.fCurrentEntity.startPosition;
               return null;
            }
         }

         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NameStart(XMLChar.supplemental(ch, ch2))) {
            --this.fCurrentEntity.position;
            return null;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(2);
            this.fCurrentEntity.ch[0] = ch;
            this.fCurrentEntity.ch[1] = ch2;
            offset = 0;
            if (this.load(2, false, false)) {
               var10000 = this.fCurrentEntity;
               var10000.columnNumber += 2;
               symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
               return symbol;
            }
         }
      }

      boolean var6 = false;

      int length;
      while(true) {
         ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (XML11Char.isXML11Name(ch)) {
            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) > 0) {
               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         } else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
               break;
            }

            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) > 0) {
               offset = 0;
               if (this.load(length, false, false)) {
                  --this.fCurrentEntity.position;
                  --this.fCurrentEntity.startPosition;
                  break;
               }
            }

            char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) {
               --this.fCurrentEntity.position;
               break;
            }

            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) > 0) {
               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      symbol = null;
      if (length > 0) {
         this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
         symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
      }

      return symbol;
   }

   protected String scanNCName() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      char ch = this.fCurrentEntity.ch[offset];
      Entity.ScannedEntity var10000;
      char ch2;
      String symbol;
      if (XML11Char.isXML11NCNameStart(ch)) {
         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               ++this.fCurrentEntity.columnNumber;
               String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
               return symbol;
            }
         }
      } else {
         if (!XML11Char.isXML11NameHighSurrogate(ch)) {
            return null;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               --this.fCurrentEntity.position;
               --this.fCurrentEntity.startPosition;
               return null;
            }
         }

         ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))) {
            --this.fCurrentEntity.position;
            return null;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(2);
            this.fCurrentEntity.ch[0] = ch;
            this.fCurrentEntity.ch[1] = ch2;
            offset = 0;
            if (this.load(2, false, false)) {
               var10000 = this.fCurrentEntity;
               var10000.columnNumber += 2;
               symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
               return symbol;
            }
         }
      }

      int length;
      while(true) {
         ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         char[] tmp;
         if (XML11Char.isXML11NCName(ch)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         } else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
               break;
            }

            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  --this.fCurrentEntity.startPosition;
                  --this.fCurrentEntity.position;
                  break;
               }
            }

            ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCName(XMLChar.supplemental(ch, ch2))) {
               --this.fCurrentEntity.position;
               break;
            }

            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
               int length = this.fCurrentEntity.position - offset;
               this.invokeListeners(length);
               if (length == this.fCurrentEntity.ch.length) {
                  char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                  System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                  this.fCurrentEntity.ch = tmp;
               } else {
                  System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      symbol = null;
      if (length > 0) {
         symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
      }

      return symbol;
   }

   protected boolean scanQName(QName qname, XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      char ch = this.fCurrentEntity.ch[offset];
      Entity.ScannedEntity var10000;
      if (XML11Char.isXML11NCNameStart(ch)) {
         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               ++this.fCurrentEntity.columnNumber;
               String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
               qname.setValues((String)null, name, name, (String)null);
               this.checkEntityLimit(nt, this.fCurrentEntity, 0, 1);
               return true;
            }
         }
      } else {
         if (!XML11Char.isXML11NameHighSurrogate(ch)) {
            return false;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = ch;
            offset = 0;
            if (this.load(1, false, false)) {
               --this.fCurrentEntity.startPosition;
               --this.fCurrentEntity.position;
               return false;
            }
         }

         char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))) {
            --this.fCurrentEntity.position;
            return false;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(2);
            this.fCurrentEntity.ch[0] = ch;
            this.fCurrentEntity.ch[1] = ch2;
            offset = 0;
            if (this.load(2, false, false)) {
               var10000 = this.fCurrentEntity;
               var10000.columnNumber += 2;
               String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
               qname.setValues((String)null, name, name, (String)null);
               this.checkEntityLimit(nt, this.fCurrentEntity, 0, 2);
               return true;
            }
         }
      }

      int index = -1;
      int length = false;
      boolean sawIncompleteSurrogatePair = false;

      int length;
      while(true) {
         ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (XML11Char.isXML11Name(ch)) {
            if (ch == ':') {
               if (index != -1) {
                  break;
               }

               index = this.fCurrentEntity.position;
               this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, index - offset);
            }

            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, index)) > 0) {
               if (index != -1) {
                  index -= offset;
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         } else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
               break;
            }

            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, index)) > 0) {
               if (index != -1) {
                  index -= offset;
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  sawIncompleteSurrogatePair = true;
                  --this.fCurrentEntity.startPosition;
                  --this.fCurrentEntity.position;
                  break;
               }
            }

            char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) {
               sawIncompleteSurrogatePair = true;
               --this.fCurrentEntity.position;
               break;
            }

            if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, index)) > 0) {
               if (index != -1) {
                  index -= offset;
               }

               offset = 0;
               if (this.load(length, false, false)) {
                  break;
               }
            }
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      if (length <= 0) {
         return false;
      } else {
         String prefix = null;
         String localpart = null;
         String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
         if (index != -1) {
            int prefixLength = index - offset;
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, prefixLength);
            prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
            int len = length - prefixLength - 1;
            int startLocal = index + 1;
            if (!XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[startLocal]) && (!XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[startLocal]) || sawIncompleteSurrogatePair)) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", (Object[])null, (short)2);
            }

            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, index + 1, len);
            localpart = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, index + 1, len);
         } else {
            localpart = rawname;
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
         }

         qname.setValues(prefix, localpart, rawname, (String)null);
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
         return true;
      }
   }

   protected int scanContent(XMLString content) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
         this.invokeListeners(1);
         this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
         this.load(1, false, false);
         this.fCurrentEntity.position = 0;
         this.fCurrentEntity.startPosition = 0;
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[offset];
      int newlines = 0;
      boolean counted = false;
      boolean external = this.fCurrentEntity.isExternal();
      Entity.ScannedEntity var10000;
      int length;
      if (c == '\n' || (c == '\r' || c == 133 || c == 8232) && external) {
         do {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c == '\r' && external) {
               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, newlines);
                  offset = 0;
                  var10000 = this.fCurrentEntity;
                  var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                  this.fCurrentEntity.position = newlines;
                  this.fCurrentEntity.startPosition = newlines;
                  if (this.load(newlines, false, true)) {
                     counted = true;
                     break;
                  }
               }

               int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
               if (cc != '\n' && cc != 133) {
                  ++newlines;
               } else {
                  ++this.fCurrentEntity.position;
                  ++offset;
               }
            } else {
               if (c != '\n' && (c != 133 && c != 8232 || !external)) {
                  --this.fCurrentEntity.position;
                  break;
               }

               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, newlines);
                  offset = 0;
                  var10000 = this.fCurrentEntity;
                  var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                  this.fCurrentEntity.position = newlines;
                  this.fCurrentEntity.startPosition = newlines;
                  if (this.load(newlines, false, true)) {
                     counted = true;
                     break;
                  }
               }
            }
         } while(this.fCurrentEntity.position < this.fCurrentEntity.count - 1);

         for(length = offset; length < this.fCurrentEntity.position; ++length) {
            this.fCurrentEntity.ch[length] = '\n';
         }

         length = this.fCurrentEntity.position - offset;
         if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, length);
            content.setValues(this.fCurrentEntity.ch, offset, length);
            return -1;
         }
      }

      if (!external) {
         while(this.fCurrentEntity.position < this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (!XML11Char.isXML11InternalEntityContent(c)) {
               --this.fCurrentEntity.position;
               break;
            }
         }
      } else {
         label140: {
            do {
               if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                  break label140;
               }

               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            } while(XML11Char.isXML11Content(c) && c != 133 && c != 8232);

            --this.fCurrentEntity.position;
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length - newlines;
      if (!counted) {
         this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, length);
      }

      content.setValues(this.fCurrentEntity.ch, offset, length);
      int c;
      if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
         c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if ((c == 13 || c == 133 || c == 8232) && external) {
            c = 10;
         }
      } else {
         c = -1;
      }

      return c;
   }

   protected int scanLiteral(int quote, XMLString content, boolean isNSURI) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
         this.invokeListeners(1);
         this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
         this.load(1, false, false);
         this.fCurrentEntity.startPosition = 0;
         this.fCurrentEntity.position = 0;
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[offset];
      int newlines = 0;
      boolean external = this.fCurrentEntity.isExternal();
      Entity.ScannedEntity var10000;
      int length;
      if (c == '\n' || (c == '\r' || c == 133 || c == 8232) && external) {
         do {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c == '\r' && external) {
               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  offset = 0;
                  var10000 = this.fCurrentEntity;
                  var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                  this.fCurrentEntity.position = newlines;
                  this.fCurrentEntity.startPosition = newlines;
                  if (this.load(newlines, false, true)) {
                     break;
                  }
               }

               int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
               if (cc != '\n' && cc != 133) {
                  ++newlines;
               } else {
                  ++this.fCurrentEntity.position;
                  ++offset;
               }
            } else {
               if (c != '\n' && (c != 133 && c != 8232 || !external)) {
                  --this.fCurrentEntity.position;
                  break;
               }

               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  offset = 0;
                  var10000 = this.fCurrentEntity;
                  var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                  this.fCurrentEntity.position = newlines;
                  this.fCurrentEntity.startPosition = newlines;
                  if (this.load(newlines, false, true)) {
                     break;
                  }
               }
            }
         } while(this.fCurrentEntity.position < this.fCurrentEntity.count - 1);

         for(length = offset; length < this.fCurrentEntity.position; ++length) {
            this.fCurrentEntity.ch[length] = '\n';
         }

         length = this.fCurrentEntity.position - offset;
         if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            content.setValues(this.fCurrentEntity.ch, offset, length);
            return -1;
         }
      }

      if (external) {
         label83: {
            do {
               if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                  break label83;
               }

               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            } while(c != quote && c != '%' && XML11Char.isXML11Content(c) && c != 133 && c != 8232);

            --this.fCurrentEntity.position;
         }
      } else {
         label146: {
            do {
               if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                  break label146;
               }

               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            } while((c != quote || this.fCurrentEntity.literal) && c != '%' && XML11Char.isXML11InternalEntityContent(c));

            --this.fCurrentEntity.position;
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length - newlines;
      this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, length);
      if (isNSURI) {
         this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
      }

      content.setValues(this.fCurrentEntity.ch, offset, length);
      int c;
      if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
         c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (c == quote && this.fCurrentEntity.literal) {
            c = -1;
         }
      } else {
         c = -1;
      }

      return c;
   }

   protected boolean scanData(String delimiter, XMLStringBuffer buffer) throws IOException {
      boolean done = false;
      int delimLen = delimiter.length();
      char charAt0 = delimiter.charAt(0);
      boolean external = this.fCurrentEntity.isExternal();

      do {
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, false);
         }

         for(boolean bNextEntity = false; this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen && !bNextEntity; this.fCurrentEntity.startPosition = 0) {
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            bNextEntity = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
            this.fCurrentEntity.position = 0;
         }

         int offset;
         Entity.ScannedEntity var10000;
         if (this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen) {
            offset = this.fCurrentEntity.count - this.fCurrentEntity.position;
            this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, this.fCurrentEntity.position, offset);
            buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, offset);
            var10000 = this.fCurrentEntity;
            var10000.columnNumber += this.fCurrentEntity.count;
            var10000 = this.fCurrentEntity;
            var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = this.fCurrentEntity.count;
            this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
            this.load(0, true, false);
            return false;
         }

         offset = this.fCurrentEntity.position;
         int c = this.fCurrentEntity.ch[offset];
         int newlines = 0;
         int length;
         if (c == '\n' || (c == '\r' || c == 133 || c == 8232) && external) {
            do {
               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
               if (c == '\r' && external) {
                  ++newlines;
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                     offset = 0;
                     var10000 = this.fCurrentEntity;
                     var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                     this.fCurrentEntity.position = newlines;
                     this.fCurrentEntity.startPosition = newlines;
                     if (this.load(newlines, false, true)) {
                        break;
                     }
                  }

                  int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                  if (cc != '\n' && cc != 133) {
                     ++newlines;
                  } else {
                     ++this.fCurrentEntity.position;
                     ++offset;
                  }
               } else {
                  if (c != '\n' && (c != 133 && c != 8232 || !external)) {
                     --this.fCurrentEntity.position;
                     break;
                  }

                  ++newlines;
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                     offset = 0;
                     var10000 = this.fCurrentEntity;
                     var10000.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                     this.fCurrentEntity.position = newlines;
                     this.fCurrentEntity.startPosition = newlines;
                     this.fCurrentEntity.count = newlines;
                     if (this.load(newlines, false, true)) {
                        break;
                     }
                  }
               }
            } while(this.fCurrentEntity.position < this.fCurrentEntity.count - 1);

            for(length = offset; length < this.fCurrentEntity.position; ++length) {
               this.fCurrentEntity.ch[length] = '\n';
            }

            length = this.fCurrentEntity.position - offset;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
               this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length);
               buffer.append(this.fCurrentEntity.ch, offset, length);
               return true;
            }
         }

         int i;
         if (external) {
            label178:
            while(true) {
               while(true) {
                  if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                     break label178;
                  }

                  c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                  if (c != charAt0) {
                     if (c == '\n' || c == '\r' || c == 133 || c == 8232) {
                        --this.fCurrentEntity.position;
                        break label178;
                     }

                     if (!XML11Char.isXML11ValidLiteral(c)) {
                        --this.fCurrentEntity.position;
                        length = this.fCurrentEntity.position - offset;
                        var10000 = this.fCurrentEntity;
                        var10000.columnNumber += length - newlines;
                        this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length);
                        buffer.append(this.fCurrentEntity.ch, offset, length);
                        return true;
                     }
                  } else {
                     length = this.fCurrentEntity.position - 1;

                     for(i = 1; i < delimLen; ++i) {
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                           var10000 = this.fCurrentEntity;
                           var10000.position -= i;
                           break label178;
                        }

                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (delimiter.charAt(i) != c) {
                           --this.fCurrentEntity.position;
                           break;
                        }
                     }

                     if (this.fCurrentEntity.position == length + delimLen) {
                        done = true;
                        break label178;
                     }
                  }
               }
            }
         } else {
            label197:
            while(true) {
               while(true) {
                  if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                     break label197;
                  }

                  c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                  if (c == charAt0) {
                     length = this.fCurrentEntity.position - 1;

                     for(i = 1; i < delimLen; ++i) {
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                           var10000 = this.fCurrentEntity;
                           var10000.position -= i;
                           break label197;
                        }

                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (delimiter.charAt(i) != c) {
                           --this.fCurrentEntity.position;
                           break;
                        }
                     }

                     if (this.fCurrentEntity.position == length + delimLen) {
                        done = true;
                        break label197;
                     }
                  } else {
                     if (c == '\n') {
                        --this.fCurrentEntity.position;
                        break label197;
                     }

                     if (!XML11Char.isXML11Valid(c)) {
                        --this.fCurrentEntity.position;
                        length = this.fCurrentEntity.position - offset;
                        var10000 = this.fCurrentEntity;
                        var10000.columnNumber += length - newlines;
                        this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length);
                        buffer.append(this.fCurrentEntity.ch, offset, length);
                        return true;
                     }
                  }
               }
            }
         }

         length = this.fCurrentEntity.position - offset;
         var10000 = this.fCurrentEntity;
         var10000.columnNumber += length - newlines;
         this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length);
         if (done) {
            length -= delimLen;
         }

         buffer.append(this.fCurrentEntity.ch, offset, length);
      } while(!done);

      return !done;
   }

   protected boolean skipChar(int c, XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (cc == c) {
         ++this.fCurrentEntity.position;
         if (c == 10) {
            ++this.fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
         } else {
            ++this.fCurrentEntity.columnNumber;
         }

         this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
         return true;
      } else if (c == 10 && (cc == 8232 || cc == 133) && this.fCurrentEntity.isExternal()) {
         ++this.fCurrentEntity.position;
         ++this.fCurrentEntity.lineNumber;
         this.fCurrentEntity.columnNumber = 1;
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
         return true;
      } else if (c == 10 && cc == '\r' && this.fCurrentEntity.isExternal()) {
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)cc;
            this.load(1, false, false);
         }

         int ccc = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
         if (ccc == '\n' || ccc == 133) {
            ++this.fCurrentEntity.position;
         }

         ++this.fCurrentEntity.lineNumber;
         this.fCurrentEntity.columnNumber = 1;
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
         return true;
      } else {
         return false;
      }
   }

   protected boolean skipSpaces() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      if (this.fCurrentEntity == null) {
         return false;
      } else {
         int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         int offset = this.fCurrentEntity.position - 1;
         boolean entityChanged;
         if (this.fCurrentEntity.isExternal()) {
            if (XML11Char.isXML11Space(c)) {
               do {
                  entityChanged = false;
                  if (c != '\n' && c != '\r' && c != 133 && c != 8232) {
                     ++this.fCurrentEntity.columnNumber;
                  } else {
                     ++this.fCurrentEntity.lineNumber;
                     this.fCurrentEntity.columnNumber = 1;
                     if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.invokeListeners(1);
                        this.fCurrentEntity.ch[0] = (char)c;
                        entityChanged = this.load(1, true, false);
                        if (!entityChanged) {
                           this.fCurrentEntity.startPosition = 0;
                           this.fCurrentEntity.position = 0;
                        } else if (this.fCurrentEntity == null) {
                           return true;
                        }
                     }

                     if (c == '\r') {
                        int cc = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
                        if (cc != '\n' && cc != 133) {
                           --this.fCurrentEntity.position;
                        }
                     }
                  }

                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
                  offset = this.fCurrentEntity.position;
                  if (!entityChanged) {
                     ++this.fCurrentEntity.position;
                  }

                  if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                     this.load(0, true, true);
                     if (this.fCurrentEntity == null) {
                        return true;
                     }
                  }
               } while(XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));

               return true;
            }
         } else if (XMLChar.isSpace(c)) {
            do {
               entityChanged = false;
               if (c == '\n') {
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                     this.invokeListeners(1);
                     this.fCurrentEntity.ch[0] = (char)c;
                     entityChanged = this.load(1, true, false);
                     if (!entityChanged) {
                        this.fCurrentEntity.startPosition = 0;
                        this.fCurrentEntity.position = 0;
                     } else if (this.fCurrentEntity == null) {
                        return true;
                     }
                  }
               } else {
                  ++this.fCurrentEntity.columnNumber;
               }

               this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
               offset = this.fCurrentEntity.position;
               if (!entityChanged) {
                  ++this.fCurrentEntity.position;
               }

               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  this.load(0, true, true);
                  if (this.fCurrentEntity == null) {
                     return true;
                  }
               }
            } while(XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));

            return true;
         }

         return false;
      }
   }

   protected boolean skipString(String s) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int length = s.length();
      int beforeSkip = this.fCurrentEntity.position;

      Entity.ScannedEntity var10000;
      for(int i = 0; i < length; ++i) {
         char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
         if (c != s.charAt(i)) {
            var10000 = this.fCurrentEntity;
            var10000.position -= i + 1;
            return false;
         }

         if (i < length - 1 && this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(0);
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
            if (this.load(i + 1, false, false)) {
               var10000 = this.fCurrentEntity;
               var10000.startPosition -= i + 1;
               var10000 = this.fCurrentEntity;
               var10000.position -= i + 1;
               return false;
            }
         }
      }

      var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      if (!this.detectingVersion) {
         this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, beforeSkip, length);
      }

      return true;
   }
}
