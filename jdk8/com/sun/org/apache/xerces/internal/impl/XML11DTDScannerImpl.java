package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public class XML11DTDScannerImpl extends XMLDTDScannerImpl {
   private XMLStringBuffer fStringBuffer = new XMLStringBuffer();

   public XML11DTDScannerImpl() {
   }

   public XML11DTDScannerImpl(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
      super(symbolTable, errorReporter, entityManager);
   }

   protected boolean scanPubidLiteral(XMLString literal) throws IOException, XNIException {
      int quote = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
      if (quote != 39 && quote != 34) {
         this.reportFatalError("QuoteRequiredInPublicID", (Object[])null);
         return false;
      } else {
         this.fStringBuffer.clear();
         boolean skipSpace = true;
         boolean dataok = true;

         while(true) {
            while(true) {
               int c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               if (c != 32 && c != 10 && c != 13 && c != 133 && c != 8232) {
                  if (c == quote) {
                     if (skipSpace) {
                        --this.fStringBuffer.length;
                     }

                     literal.setValues(this.fStringBuffer);
                     return dataok;
                  }

                  if (XMLChar.isPubid(c)) {
                     this.fStringBuffer.append((char)c);
                     skipSpace = false;
                  } else {
                     if (c == -1) {
                        this.reportFatalError("PublicIDUnterminated", (Object[])null);
                        return false;
                     }

                     dataok = false;
                     this.reportFatalError("InvalidCharInPublicID", new Object[]{Integer.toHexString(c)});
                  }
               } else if (!skipSpace) {
                  this.fStringBuffer.append(' ');
                  skipSpace = true;
               }
            }
         }
      }
   }

   protected void normalizeWhitespace(XMLString value) {
      int end = value.offset + value.length;

      for(int i = value.offset; i < end; ++i) {
         int c = value.ch[i];
         if (XMLChar.isSpace(c)) {
            value.ch[i] = ' ';
         }
      }

   }

   protected void normalizeWhitespace(XMLString value, int fromIndex) {
      int end = value.offset + value.length;

      for(int i = value.offset + fromIndex; i < end; ++i) {
         int c = value.ch[i];
         if (XMLChar.isSpace(c)) {
            value.ch[i] = ' ';
         }
      }

   }

   protected int isUnchangedByNormalization(XMLString value) {
      int end = value.offset + value.length;

      for(int i = value.offset; i < end; ++i) {
         int c = value.ch[i];
         if (XMLChar.isSpace(c)) {
            return i - value.offset;
         }
      }

      return -1;
   }

   protected boolean isInvalid(int value) {
      return !XML11Char.isXML11Valid(value);
   }

   protected boolean isInvalidLiteral(int value) {
      return !XML11Char.isXML11ValidLiteral(value);
   }

   protected boolean isValidNameChar(int value) {
      return XML11Char.isXML11Name(value);
   }

   protected boolean isValidNameStartChar(int value) {
      return XML11Char.isXML11NameStart(value);
   }

   protected boolean isValidNCName(int value) {
      return XML11Char.isXML11NCName(value);
   }

   protected boolean isValidNameStartHighSurrogate(int value) {
      return XML11Char.isXML11NameHighSurrogate(value);
   }

   protected boolean versionSupported(String version) {
      return version.equals("1.1") || version.equals("1.0");
   }

   protected String getVersionNotSupportedKey() {
      return "VersionNotSupported11";
   }
}
