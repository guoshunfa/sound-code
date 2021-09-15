package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import java.util.Hashtable;
import java.util.Vector;

public final class XPointerHandler extends XIncludeHandler implements XPointerProcessor {
   protected Vector fXPointerParts = null;
   protected XPointerPart fXPointerPart = null;
   protected boolean fFoundMatchingPtrPart = false;
   protected XMLErrorReporter fXPointerErrorReporter;
   protected XMLErrorHandler fErrorHandler;
   protected SymbolTable fSymbolTable = null;
   private final String ELEMENT_SCHEME_NAME = "element";
   protected boolean fIsXPointerResolved = false;
   protected boolean fFixupBase = false;
   protected boolean fFixupLang = false;

   public XPointerHandler() {
      this.fXPointerParts = new Vector();
      this.fSymbolTable = new SymbolTable();
   }

   public XPointerHandler(SymbolTable symbolTable, XMLErrorHandler errorHandler, XMLErrorReporter errorReporter) {
      this.fXPointerParts = new Vector();
      this.fSymbolTable = symbolTable;
      this.fErrorHandler = errorHandler;
      this.fXPointerErrorReporter = errorReporter;
   }

   public void parseXPointer(String xpointer) throws XNIException {
      this.init();
      XPointerHandler.Tokens tokens = new XPointerHandler.Tokens(this.fSymbolTable);
      XPointerHandler.Scanner scanner = new XPointerHandler.Scanner(this.fSymbolTable) {
         protected void addToken(XPointerHandler.Tokens tokens, int token) throws XNIException {
            if (token != 0 && token != 1 && token != 3 && token != 4 && token != 2) {
               XPointerHandler.this.reportError("InvalidXPointerToken", new Object[]{tokens.getTokenString(token)});
            } else {
               super.addToken(tokens, token);
            }
         }
      };
      int length = xpointer.length();
      boolean success = scanner.scanExpr(this.fSymbolTable, tokens, xpointer, 0, length);
      if (!success) {
         this.reportError("InvalidXPointerExpression", new Object[]{xpointer});
      }

      while(true) {
         while(tokens.hasMore()) {
            int token = tokens.nextToken();
            String shortHandPointerName;
            switch(token) {
            case 2:
               token = tokens.nextToken();
               shortHandPointerName = tokens.getTokenString(token);
               if (shortHandPointerName == null) {
                  this.reportError("InvalidXPointerExpression", new Object[]{xpointer});
               }

               XPointerPart shortHandPointer = new ShortHandPointer(this.fSymbolTable);
               shortHandPointer.setSchemeName(shortHandPointerName);
               this.fXPointerParts.add(shortHandPointer);
               break;
            case 3:
               token = tokens.nextToken();
               shortHandPointerName = tokens.getTokenString(token);
               token = tokens.nextToken();
               String localName = tokens.getTokenString(token);
               String schemeName = shortHandPointerName + localName;
               int openParenCount = 0;
               int closeParenCount = 0;
               token = tokens.nextToken();
               String openParen = tokens.getTokenString(token);
               if (openParen != "XPTRTOKEN_OPEN_PAREN") {
                  if (token == 2) {
                     this.reportError("MultipleShortHandPointers", new Object[]{xpointer});
                  } else {
                     this.reportError("InvalidXPointerExpression", new Object[]{xpointer});
                  }
               }

               int openParenCount = openParenCount + 1;

               String schemeData;
               for(schemeData = null; tokens.hasMore(); ++openParenCount) {
                  token = tokens.nextToken();
                  schemeData = tokens.getTokenString(token);
                  if (schemeData != "XPTRTOKEN_OPEN_PAREN") {
                     break;
                  }
               }

               token = tokens.nextToken();
               schemeData = tokens.getTokenString(token);
               token = tokens.nextToken();
               String closeParen = tokens.getTokenString(token);
               if (closeParen != "XPTRTOKEN_CLOSE_PAREN") {
                  this.reportError("SchemeDataNotFollowedByCloseParenthesis", new Object[]{xpointer});
               }

               int closeParenCount;
               for(closeParenCount = closeParenCount + 1; tokens.hasMore() && tokens.getTokenString(tokens.peekToken()) == "XPTRTOKEN_OPEN_PAREN"; ++closeParenCount) {
               }

               if (openParenCount != closeParenCount) {
                  this.reportError("UnbalancedParenthesisInXPointerExpression", new Object[]{xpointer, new Integer(openParenCount), new Integer(closeParenCount)});
               }

               if (schemeName.equals("element")) {
                  XPointerPart elementSchemePointer = new ElementSchemePointer(this.fSymbolTable, this.fErrorReporter);
                  elementSchemePointer.setSchemeName(schemeName);
                  elementSchemePointer.setSchemeData(schemeData);

                  try {
                     elementSchemePointer.parseXPointer(schemeData);
                     this.fXPointerParts.add(elementSchemePointer);
                  } catch (XNIException var17) {
                     throw new XNIException(var17);
                  }
               } else {
                  this.reportWarning("SchemeUnsupported", new Object[]{schemeName});
               }
               break;
            default:
               this.reportError("InvalidXPointerExpression", new Object[]{xpointer});
            }
         }

         return;
      }
   }

   public boolean resolveXPointer(QName element, XMLAttributes attributes, Augmentations augs, int event) throws XNIException {
      boolean resolved = false;
      if (!this.fFoundMatchingPtrPart) {
         for(int i = 0; i < this.fXPointerParts.size(); ++i) {
            this.fXPointerPart = (XPointerPart)this.fXPointerParts.get(i);
            if (this.fXPointerPart.resolveXPointer(element, attributes, augs, event)) {
               this.fFoundMatchingPtrPart = true;
               resolved = true;
            }
         }
      } else if (this.fXPointerPart.resolveXPointer(element, attributes, augs, event)) {
         resolved = true;
      }

      if (!this.fIsXPointerResolved) {
         this.fIsXPointerResolved = resolved;
      }

      return resolved;
   }

   public boolean isFragmentResolved() throws XNIException {
      boolean resolved = this.fXPointerPart != null ? this.fXPointerPart.isFragmentResolved() : false;
      if (!this.fIsXPointerResolved) {
         this.fIsXPointerResolved = resolved;
      }

      return resolved;
   }

   public boolean isChildFragmentResolved() throws XNIException {
      boolean resolved = this.fXPointerPart != null ? this.fXPointerPart.isChildFragmentResolved() : false;
      return resolved;
   }

   public boolean isXPointerResolved() throws XNIException {
      return this.fIsXPointerResolved;
   }

   public XPointerPart getXPointerPart() {
      return this.fXPointerPart;
   }

   private void reportError(String key, Object[] arguments) throws XNIException {
      throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), key, arguments));
   }

   private void reportWarning(String key, Object[] arguments) throws XNIException {
      this.fXPointerErrorReporter.reportError("http://www.w3.org/TR/XPTR", key, arguments, (short)0);
   }

   protected void initErrorReporter() {
      if (this.fXPointerErrorReporter == null) {
         this.fXPointerErrorReporter = new XMLErrorReporter();
      }

      if (this.fErrorHandler == null) {
         this.fErrorHandler = new XPointerErrorHandler();
      }

      this.fXPointerErrorReporter.putMessageFormatter("http://www.w3.org/TR/XPTR", new XPointerMessageFormatter());
   }

   protected void init() {
      this.fXPointerParts.clear();
      this.fXPointerPart = null;
      this.fFoundMatchingPtrPart = false;
      this.fIsXPointerResolved = false;
      this.initErrorReporter();
   }

   public Vector getPointerParts() {
      return this.fXPointerParts;
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.comment(text, augs);
      }
   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.processingInstruction(target, data, augs);
      }
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (!this.resolveXPointer(element, attributes, augs, 0)) {
         if (this.fFixupBase) {
            this.processXMLBaseAttributes(attributes);
         }

         if (this.fFixupLang) {
            this.processXMLLangAttributes(attributes);
         }

         this.fNamespaceContext.setContextInvalid();
      } else {
         super.startElement(element, attributes, augs);
      }
   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (!this.resolveXPointer(element, attributes, augs, 2)) {
         if (this.fFixupBase) {
            this.processXMLBaseAttributes(attributes);
         }

         if (this.fFixupLang) {
            this.processXMLLangAttributes(attributes);
         }

         this.fNamespaceContext.setContextInvalid();
      } else {
         super.emptyElement(element, attributes, augs);
      }
   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.characters(text, augs);
      }
   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.ignorableWhitespace(text, augs);
      }
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.resolveXPointer(element, (XMLAttributes)null, augs, 1)) {
         super.endElement(element, augs);
      }
   }

   public void startCDATA(Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.startCDATA(augs);
      }
   }

   public void endCDATA(Augmentations augs) throws XNIException {
      if (this.isChildFragmentResolved()) {
         super.endCDATA(augs);
      }
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId == "http://apache.org/xml/properties/internal/error-reporter") {
         if (value != null) {
            this.fXPointerErrorReporter = (XMLErrorReporter)value;
         } else {
            this.fXPointerErrorReporter = null;
         }
      }

      if (propertyId == "http://apache.org/xml/properties/internal/error-handler") {
         if (value != null) {
            this.fErrorHandler = (XMLErrorHandler)value;
         } else {
            this.fErrorHandler = null;
         }
      }

      if (propertyId == "http://apache.org/xml/features/xinclude/fixup-language") {
         if (value != null) {
            this.fFixupLang = (Boolean)value;
         } else {
            this.fFixupLang = false;
         }
      }

      if (propertyId == "http://apache.org/xml/features/xinclude/fixup-base-uris") {
         if (value != null) {
            this.fFixupBase = (Boolean)value;
         } else {
            this.fFixupBase = false;
         }
      }

      if (propertyId == "http://apache.org/xml/properties/internal/namespace-context") {
         this.fNamespaceContext = (XIncludeNamespaceSupport)value;
      }

      super.setProperty(propertyId, value);
   }

   private class Scanner {
      private static final byte CHARTYPE_INVALID = 0;
      private static final byte CHARTYPE_OTHER = 1;
      private static final byte CHARTYPE_WHITESPACE = 2;
      private static final byte CHARTYPE_CARRET = 3;
      private static final byte CHARTYPE_OPEN_PAREN = 4;
      private static final byte CHARTYPE_CLOSE_PAREN = 5;
      private static final byte CHARTYPE_MINUS = 6;
      private static final byte CHARTYPE_PERIOD = 7;
      private static final byte CHARTYPE_SLASH = 8;
      private static final byte CHARTYPE_DIGIT = 9;
      private static final byte CHARTYPE_COLON = 10;
      private static final byte CHARTYPE_EQUAL = 11;
      private static final byte CHARTYPE_LETTER = 12;
      private static final byte CHARTYPE_UNDERSCORE = 13;
      private static final byte CHARTYPE_NONASCII = 14;
      private final byte[] fASCIICharMap;
      private SymbolTable fSymbolTable;

      private Scanner(SymbolTable symbolTable) {
         this.fASCIICharMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 4, 5, 1, 1, 1, 6, 7, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 1, 1, 11, 1, 1, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 3, 13, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 1, 1};
         this.fSymbolTable = symbolTable;
      }

      private boolean scanExpr(SymbolTable symbolTable, XPointerHandler.Tokens tokens, String data, int currentOffset, int endOffset) throws XNIException {
         int openParen = 0;
         int closeParen = 0;
         boolean isQName = false;
         String name = null;
         String prefix = null;
         String schemeData = null;
         StringBuffer schemeDataBuff = new StringBuffer();

         while(true) {
            if (currentOffset != endOffset) {
               char ch;
               for(ch = data.charAt(currentOffset); ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r'; ch = data.charAt(currentOffset)) {
                  ++currentOffset;
                  if (currentOffset == endOffset) {
                     break;
                  }
               }

               if (currentOffset != endOffset) {
                  byte chartype = ch >= 128 ? 14 : this.fASCIICharMap[ch];
                  switch(chartype) {
                  case 1:
                  case 2:
                  case 3:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                     boolean var17;
                     if (openParen == 0) {
                        int nameOffset = currentOffset;
                        currentOffset = this.scanNCName(data, endOffset, currentOffset);
                        if (currentOffset == nameOffset) {
                           XPointerHandler.this.reportError("InvalidShortHandPointer", new Object[]{data});
                           return false;
                        }

                        int chx;
                        if (currentOffset < endOffset) {
                           chx = data.charAt(currentOffset);
                        } else {
                           chx = -1;
                        }

                        name = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        prefix = XMLSymbols.EMPTY_STRING;
                        if (chx == 58) {
                           ++currentOffset;
                           if (currentOffset == endOffset) {
                              return false;
                           }

                           data.charAt(currentOffset);
                           prefix = name;
                           nameOffset = currentOffset;
                           currentOffset = this.scanNCName(data, endOffset, currentOffset);
                           if (currentOffset == nameOffset) {
                              return false;
                           }

                           if (currentOffset < endOffset) {
                              data.charAt(currentOffset);
                           } else {
                              var17 = true;
                           }

                           isQName = true;
                           name = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        }

                        if (currentOffset != endOffset) {
                           this.addToken(tokens, 3);
                           tokens.addToken(prefix);
                           tokens.addToken(name);
                           isQName = false;
                        } else if (currentOffset == endOffset) {
                           this.addToken(tokens, 2);
                           tokens.addToken(name);
                           isQName = false;
                        }

                        closeParen = 0;
                        continue;
                     }

                     if (openParen > 0 && closeParen == 0 && name != null) {
                        int dataOffset = currentOffset;
                        currentOffset = this.scanData(data, schemeDataBuff, endOffset, currentOffset);
                        if (currentOffset == dataOffset) {
                           XPointerHandler.this.reportError("InvalidSchemeDataInXPointer", new Object[]{data});
                           return false;
                        }

                        if (currentOffset < endOffset) {
                           data.charAt(currentOffset);
                        } else {
                           var17 = true;
                        }

                        schemeData = symbolTable.addSymbol(schemeDataBuff.toString());
                        this.addToken(tokens, 4);
                        tokens.addToken(schemeData);
                        openParen = 0;
                        schemeDataBuff.delete(0, schemeDataBuff.length());
                        continue;
                     }

                     return false;
                  case 4:
                     this.addToken(tokens, 0);
                     ++openParen;
                     ++currentOffset;
                     continue;
                  case 5:
                     this.addToken(tokens, 1);
                     ++closeParen;
                     ++currentOffset;
                  default:
                     continue;
                  }
               }
            }

            return true;
         }
      }

      private int scanNCName(String data, int endOffset, int currentOffset) {
         int ch = data.charAt(currentOffset);
         byte chartype;
         if (ch >= 128) {
            if (!XMLChar.isNameStart(ch)) {
               return currentOffset;
            }
         } else {
            chartype = this.fASCIICharMap[ch];
            if (chartype != 12 && chartype != 13) {
               return currentOffset;
            }
         }

         while(true) {
            ++currentOffset;
            if (currentOffset >= endOffset) {
               break;
            }

            ch = data.charAt(currentOffset);
            if (ch >= 128) {
               if (!XMLChar.isName(ch)) {
                  break;
               }
            } else {
               chartype = this.fASCIICharMap[ch];
               if (chartype != 12 && chartype != 9 && chartype != 7 && chartype != 6 && chartype != 13) {
                  break;
               }
            }
         }

         return currentOffset;
      }

      private int scanData(String data, StringBuffer schemeData, int endOffset, int currentOffset) {
         while(true) {
            while(true) {
               while(true) {
                  if (currentOffset != endOffset) {
                     int ch = data.charAt(currentOffset);
                     byte chartype = ch >= 128 ? 14 : this.fASCIICharMap[ch];
                     if (chartype == 4) {
                        schemeData.append((int)ch);
                        ++currentOffset;
                        currentOffset = this.scanData(data, schemeData, endOffset, currentOffset);
                        if (currentOffset == endOffset) {
                           return currentOffset;
                        }

                        ch = data.charAt(currentOffset);
                        chartype = ch >= 128 ? 14 : this.fASCIICharMap[ch];
                        if (chartype != 5) {
                           return endOffset;
                        }

                        schemeData.append((char)ch);
                        ++currentOffset;
                        continue;
                     }

                     if (chartype == 5) {
                        return currentOffset;
                     }

                     if (chartype != 3) {
                        schemeData.append((char)ch);
                        ++currentOffset;
                        continue;
                     }

                     ++currentOffset;
                     ch = data.charAt(currentOffset);
                     chartype = ch >= 128 ? 14 : this.fASCIICharMap[ch];
                     if (chartype == 3 || chartype == 4 || chartype == 5) {
                        schemeData.append((char)ch);
                        ++currentOffset;
                        continue;
                     }
                  }

                  return currentOffset;
               }
            }
         }
      }

      protected void addToken(XPointerHandler.Tokens tokens, int token) throws XNIException {
         tokens.addToken(token);
      }

      // $FF: synthetic method
      Scanner(SymbolTable x1, Object x2) {
         this(x1);
      }
   }

   private final class Tokens {
      private static final int XPTRTOKEN_OPEN_PAREN = 0;
      private static final int XPTRTOKEN_CLOSE_PAREN = 1;
      private static final int XPTRTOKEN_SHORTHAND = 2;
      private static final int XPTRTOKEN_SCHEMENAME = 3;
      private static final int XPTRTOKEN_SCHEMEDATA = 4;
      private final String[] fgTokenNames;
      private static final int INITIAL_TOKEN_COUNT = 256;
      private int[] fTokens;
      private int fTokenCount;
      private int fCurrentTokenIndex;
      private SymbolTable fSymbolTable;
      private Hashtable fTokenNames;

      private Tokens(SymbolTable symbolTable) {
         this.fgTokenNames = new String[]{"XPTRTOKEN_OPEN_PAREN", "XPTRTOKEN_CLOSE_PAREN", "XPTRTOKEN_SHORTHAND", "XPTRTOKEN_SCHEMENAME", "XPTRTOKEN_SCHEMEDATA"};
         this.fTokens = new int[256];
         this.fTokenCount = 0;
         this.fTokenNames = new Hashtable();
         this.fSymbolTable = symbolTable;
         this.fTokenNames.put(new Integer(0), "XPTRTOKEN_OPEN_PAREN");
         this.fTokenNames.put(new Integer(1), "XPTRTOKEN_CLOSE_PAREN");
         this.fTokenNames.put(new Integer(2), "XPTRTOKEN_SHORTHAND");
         this.fTokenNames.put(new Integer(3), "XPTRTOKEN_SCHEMENAME");
         this.fTokenNames.put(new Integer(4), "XPTRTOKEN_SCHEMEDATA");
      }

      private String getTokenString(int token) {
         return (String)this.fTokenNames.get(new Integer(token));
      }

      private void addToken(String tokenStr) {
         Integer tokenInt = (Integer)this.fTokenNames.get(tokenStr);
         if (tokenInt == null) {
            tokenInt = new Integer(this.fTokenNames.size());
            this.fTokenNames.put(tokenInt, tokenStr);
         }

         this.addToken(tokenInt);
      }

      private void addToken(int token) {
         try {
            this.fTokens[this.fTokenCount] = token;
         } catch (ArrayIndexOutOfBoundsException var4) {
            int[] oldList = this.fTokens;
            this.fTokens = new int[this.fTokenCount << 1];
            System.arraycopy(oldList, 0, this.fTokens, 0, this.fTokenCount);
            this.fTokens[this.fTokenCount] = token;
         }

         ++this.fTokenCount;
      }

      private void rewind() {
         this.fCurrentTokenIndex = 0;
      }

      private boolean hasMore() {
         return this.fCurrentTokenIndex < this.fTokenCount;
      }

      private int nextToken() throws XNIException {
         if (this.fCurrentTokenIndex == this.fTokenCount) {
            XPointerHandler.this.reportError("XPointerProcessingError", (Object[])null);
         }

         return this.fTokens[this.fCurrentTokenIndex++];
      }

      private int peekToken() throws XNIException {
         if (this.fCurrentTokenIndex == this.fTokenCount) {
            XPointerHandler.this.reportError("XPointerProcessingError", (Object[])null);
         }

         return this.fTokens[this.fCurrentTokenIndex];
      }

      private String nextTokenAsString() throws XNIException {
         String tokenStrint = this.getTokenString(this.nextToken());
         if (tokenStrint == null) {
            XPointerHandler.this.reportError("XPointerProcessingError", (Object[])null);
         }

         return tokenStrint;
      }

      // $FF: synthetic method
      Tokens(SymbolTable x1, Object x2) {
         this(x1);
      }
   }
}
