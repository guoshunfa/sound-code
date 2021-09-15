package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public class XML11DocumentScannerImpl extends XMLDocumentScannerImpl {
   private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
   private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
   private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();

   protected int scanContent(XMLStringBuffer content) throws IOException, XNIException {
      this.fTempString.length = 0;
      int c = this.fEntityScanner.scanContent(this.fTempString);
      content.append(this.fTempString);
      if (c == 13 || c == 133 || c == 8232) {
         this.fEntityScanner.scanChar((XMLScanner.NameType)null);
         content.append((char)c);
         c = -1;
      }

      if (c == 93) {
         content.append((char)this.fEntityScanner.scanChar((XMLScanner.NameType)null));
         this.fInScanContent = true;
         if (this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
            content.append(']');

            while(this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
               content.append(']');
            }

            if (this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
               this.reportFatalError("CDEndInContent", (Object[])null);
            }
         }

         this.fInScanContent = false;
         c = -1;
      }

      return c;
   }

   protected boolean scanAttributeValue(XMLString value, XMLString nonNormalizedValue, String atName, boolean checkEntities, String eleName, boolean isNSURI) throws IOException, XNIException {
      int quote = this.fEntityScanner.peekChar();
      if (quote != 39 && quote != 34) {
         this.reportFatalError("OpenQuoteExpected", new Object[]{eleName, atName});
      }

      this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      int entityDepth = this.fEntityDepth;
      int c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
      int fromIndex = 0;
      int ch;
      if (c == quote && (fromIndex = this.isUnchangedByNormalization(value)) == -1) {
         nonNormalizedValue.setValues(value);
         ch = this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
         if (ch != quote) {
            this.reportFatalError("CloseQuoteExpected", new Object[]{eleName, atName});
         }

         return true;
      } else {
         this.fStringBuffer2.clear();
         this.fStringBuffer2.append(value);
         this.normalizeWhitespace(value, fromIndex);
         if (c != quote) {
            this.fScanningAttribute = true;
            this.fStringBuffer.clear();

            do {
               this.fStringBuffer.append(value);
               if (c == 38) {
                  this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE);
                  if (entityDepth == this.fEntityDepth) {
                     this.fStringBuffer2.append('&');
                  }

                  if (this.fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE)) {
                     if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('#');
                     }

                     ch = this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2);
                     if (ch != -1) {
                     }
                  } else {
                     String entityName = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
                     if (entityName == null) {
                        this.reportFatalError("NameRequiredInReference", (Object[])null);
                     } else if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append(entityName);
                     }

                     if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
                        this.reportFatalError("SemicolonRequiredInReference", new Object[]{entityName});
                     } else if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append(';');
                     }

                     if (this.resolveCharacter(entityName, this.fStringBuffer)) {
                        this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
                     } else if (this.fEntityManager.isExternalEntity(entityName)) {
                        this.reportFatalError("ReferenceToExternalEntity", new Object[]{entityName});
                     } else {
                        if (!this.fEntityManager.isDeclaredEntity(entityName)) {
                           if (checkEntities) {
                              if (this.fValidation) {
                                 this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{entityName}, (short)1);
                              }
                           } else {
                              this.reportFatalError("EntityNotDeclared", new Object[]{entityName});
                           }
                        }

                        this.fEntityManager.startEntity(true, entityName, true);
                     }
                  }
               } else if (c == 60) {
                  this.reportFatalError("LessthanInAttValue", new Object[]{eleName, atName});
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  if (entityDepth == this.fEntityDepth) {
                     this.fStringBuffer2.append((char)c);
                  }
               } else if (c != 37 && c != 93) {
                  if (c != 10 && c != 13 && c != 133 && c != 8232) {
                     if (c != -1 && XMLChar.isHighSurrogate(c)) {
                        this.fStringBuffer3.clear();
                        if (this.scanSurrogates(this.fStringBuffer3)) {
                           this.fStringBuffer.append((XMLString)this.fStringBuffer3);
                           if (entityDepth == this.fEntityDepth) {
                              this.fStringBuffer2.append((XMLString)this.fStringBuffer3);
                           }
                        }
                     } else if (c != -1 && this.isInvalidLiteral(c)) {
                        this.reportFatalError("InvalidCharInAttValue", new Object[]{eleName, atName, Integer.toString(c, 16)});
                        this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                        if (entityDepth == this.fEntityDepth) {
                           this.fStringBuffer2.append((char)c);
                        }
                     }
                  } else {
                     this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                     this.fStringBuffer.append(' ');
                     if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('\n');
                     }
                  }
               } else {
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  this.fStringBuffer.append((char)c);
                  if (entityDepth == this.fEntityDepth) {
                     this.fStringBuffer2.append((char)c);
                  }
               }

               c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
               if (entityDepth == this.fEntityDepth) {
                  this.fStringBuffer2.append(value);
               }

               this.normalizeWhitespace(value);
            } while(c != quote || entityDepth != this.fEntityDepth);

            this.fStringBuffer.append(value);
            value.setValues(this.fStringBuffer);
            this.fScanningAttribute = false;
         }

         nonNormalizedValue.setValues(this.fStringBuffer2);
         ch = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
         if (ch != quote) {
            this.reportFatalError("CloseQuoteExpected", new Object[]{eleName, atName});
         }

         return nonNormalizedValue.equals(value.ch, value.offset, value.length);
      }
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
      return XML11Char.isXML11Invalid(value);
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
