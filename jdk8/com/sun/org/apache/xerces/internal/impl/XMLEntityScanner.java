package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

public class XMLEntityScanner implements XMLLocator {
   protected Entity.ScannedEntity fCurrentEntity = null;
   protected int fBufferSize = 8192;
   protected XMLEntityManager fEntityManager;
   protected XMLSecurityManager fSecurityManager = null;
   protected XMLLimitAnalyzer fLimitAnalyzer = null;
   private static final boolean DEBUG_ENCODINGS = false;
   private ArrayList<XMLBufferListener> listeners = new ArrayList();
   private static final boolean[] VALID_NAMES = new boolean[127];
   private static final boolean DEBUG_BUFFER = false;
   private static final boolean DEBUG_SKIP_STRING = false;
   private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException() {
      private static final long serialVersionUID = 980337771224675268L;

      public Throwable fillInStackTrace() {
         return this;
      }
   };
   protected SymbolTable fSymbolTable = null;
   protected XMLErrorReporter fErrorReporter = null;
   int[] whiteSpaceLookup = new int[100];
   int whiteSpaceLen = 0;
   boolean whiteSpaceInfoNeeded = true;
   protected boolean fAllowJavaEncodings;
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
   protected PropertyManager fPropertyManager = null;
   boolean isExternal = false;
   protected boolean xmlVersionSetExplicitly = false;
   boolean detectingVersion = false;

   public XMLEntityScanner() {
   }

   public XMLEntityScanner(PropertyManager propertyManager, XMLEntityManager entityManager) {
      this.fEntityManager = entityManager;
      this.reset(propertyManager);
   }

   public final void setBufferSize(int size) {
      this.fBufferSize = size;
   }

   public void reset(PropertyManager propertyManager) {
      this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.resetCommon();
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
      this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.resetCommon();
   }

   public final void reset(SymbolTable symbolTable, XMLEntityManager entityManager, XMLErrorReporter reporter) {
      this.fCurrentEntity = null;
      this.fSymbolTable = symbolTable;
      this.fEntityManager = entityManager;
      this.fErrorReporter = reporter;
      this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
      this.fSecurityManager = this.fEntityManager.fSecurityManager;
   }

   private void resetCommon() {
      this.fCurrentEntity = null;
      this.whiteSpaceLen = 0;
      this.whiteSpaceInfoNeeded = true;
      this.listeners.clear();
      this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
      this.fSecurityManager = this.fEntityManager.fSecurityManager;
   }

   public final String getXMLVersion() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.xmlVersion : null;
   }

   public final void setXMLVersion(String xmlVersion) {
      this.xmlVersionSetExplicitly = true;
      this.fCurrentEntity.xmlVersion = xmlVersion;
   }

   public final void setCurrentEntity(Entity.ScannedEntity scannedEntity) {
      this.fCurrentEntity = scannedEntity;
      if (this.fCurrentEntity != null) {
         this.isExternal = this.fCurrentEntity.isExternal();
      }

   }

   public Entity.ScannedEntity getCurrentEntity() {
      return this.fCurrentEntity;
   }

   public final String getBaseSystemId() {
      return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
   }

   public void setBaseSystemId(String systemId) {
   }

   public final int getLineNumber() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.lineNumber : -1;
   }

   public void setLineNumber(int line) {
   }

   public final int getColumnNumber() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.columnNumber : -1;
   }

   public void setColumnNumber(int col) {
   }

   public final int getCharacterOffset() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position : -1;
   }

   public final String getExpandedSystemId() {
      return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
   }

   public void setExpandedSystemId(String systemId) {
   }

   public final String getLiteralSystemId() {
      return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null;
   }

   public void setLiteralSystemId(String systemId) {
   }

   public final String getPublicId() {
      return this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null ? this.fCurrentEntity.entityLocation.getPublicId() : null;
   }

   public void setPublicId(String publicId) {
   }

   public void setVersion(String version) {
      this.fCurrentEntity.version = version;
   }

   public String getVersion() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.version : null;
   }

   public final String getEncoding() {
      return this.fCurrentEntity != null ? this.fCurrentEntity.encoding : null;
   }

   public final void setEncoding(String encoding) throws IOException {
      if (this.fCurrentEntity.stream != null && (this.fCurrentEntity.encoding == null || !this.fCurrentEntity.encoding.equals(encoding))) {
         if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
            String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
            if (ENCODING.equals("UTF-16")) {
               return;
            }

            if (ENCODING.equals("ISO-10646-UCS-4")) {
               if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                  this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
               } else {
                  this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
               }

               return;
            }

            if (ENCODING.equals("ISO-10646-UCS-2")) {
               if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                  this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
               } else {
                  this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
               }

               return;
            }
         }

         this.fCurrentEntity.reader = this.createReader(this.fCurrentEntity.stream, encoding, (Boolean)null);
         this.fCurrentEntity.encoding = encoding;
      }

   }

   public final boolean isExternal() {
      return this.fCurrentEntity.isExternal();
   }

   public int getChar(int relative) throws IOException {
      return this.arrangeCapacity(relative + 1, false) ? this.fCurrentEntity.ch[this.fCurrentEntity.position + relative] : -1;
   }

   public int peekChar() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (this.isExternal) {
         return c != '\r' ? c : 10;
      } else {
         return c;
      }
   }

   protected int scanChar(XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
      if (c == '\n' || c == '\r' && this.isExternal) {
         ++this.fCurrentEntity.lineNumber;
         this.fCurrentEntity.columnNumber = 1;
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)c;
            this.load(1, false, false);
            offset = 0;
         }

         if (c == '\r' && this.isExternal) {
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n') {
               --this.fCurrentEntity.position;
            }

            c = '\n';
         }
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
      boolean vc = false;

      Entity.ScannedEntity var10000;
      int length;
      while(true) {
         char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (c < 127) {
            vc = VALID_NAMES[c];
         } else {
            vc = XMLChar.isName(c);
         }

         if (!vc) {
            break;
         }

         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            length = this.fCurrentEntity.position - offset;
            this.invokeListeners(length);
            if (length == this.fCurrentEntity.fBufferSize) {
               char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
               System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
               this.fCurrentEntity.ch = tmp;
               var10000 = this.fCurrentEntity;
               var10000.fBufferSize *= 2;
            } else {
               System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
            }

            offset = 0;
            if (this.load(length, false, false)) {
               break;
            }
         }
      }

      length = this.fCurrentEntity.position - offset;
      var10000 = this.fCurrentEntity;
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
      int length;
      String symbol;
      if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
            offset = 0;
            if (this.load(1, false, false)) {
               ++this.fCurrentEntity.columnNumber;
               symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
               return symbol;
            }
         }

         boolean vc = false;

         while(true) {
            char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c < 127) {
               vc = VALID_NAMES[c];
            } else {
               vc = XMLChar.isName(c);
            }

            if (!vc) {
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
      Entity.ScannedEntity var10000 = this.fCurrentEntity;
      var10000.columnNumber += length;
      if (length > 0) {
         this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
         this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
         symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
      } else {
         symbol = null;
      }

      return symbol;
   }

   protected boolean scanQName(QName qname, XMLScanner.NameType nt) throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, true);
      }

      int offset = this.fCurrentEntity.position;
      if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
            offset = 0;
            if (this.load(1, false, false)) {
               ++this.fCurrentEntity.columnNumber;
               String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
               qname.setValues((String)null, name, name, (String)null);
               this.checkEntityLimit(nt, this.fCurrentEntity, 0, 1);
               return true;
            }
         }

         int index = -1;
         boolean vc = false;

         int length;
         while(true) {
            char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c < 127) {
               vc = VALID_NAMES[c];
            } else {
               vc = XMLChar.isName(c);
            }

            if (!vc) {
               break;
            }

            if (c == ':') {
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
         }

         length = this.fCurrentEntity.position - offset;
         Entity.ScannedEntity var10000 = this.fCurrentEntity;
         var10000.columnNumber += length;
         if (length > 0) {
            String prefix = null;
            String localpart = null;
            String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
            if (index != -1) {
               int prefixLength = index - offset;
               this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, prefixLength);
               prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
               int len = length - prefixLength - 1;
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

      return false;
   }

   protected int checkBeforeLoad(Entity.ScannedEntity entity, int offset, int nameOffset) throws IOException {
      int length = 0;
      if (++entity.position == entity.count) {
         length = entity.position - offset;
         int nameLength = length;
         if (nameOffset != -1) {
            nameOffset -= offset;
            nameLength = length - nameOffset;
         } else {
            nameOffset = offset;
         }

         this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, entity, nameOffset, nameLength);
         this.invokeListeners(length);
         if (length == entity.ch.length) {
            char[] tmp = new char[entity.fBufferSize * 2];
            System.arraycopy(entity.ch, offset, tmp, 0, length);
            entity.ch = tmp;
            entity.fBufferSize *= 2;
         } else {
            System.arraycopy(entity.ch, offset, entity.ch, 0, length);
         }
      }

      return length;
   }

   protected void checkEntityLimit(XMLScanner.NameType nt, Entity.ScannedEntity entity, int offset, int length) {
      if (entity != null && entity.isGE) {
         if (nt != XMLScanner.NameType.REFERENCE) {
            this.checkLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entity, offset, length);
         }

         if (nt == XMLScanner.NameType.ELEMENTSTART || nt == XMLScanner.NameType.ATTRIBUTENAME) {
            this.checkNodeCount(entity);
         }

      }
   }

   protected void checkNodeCount(Entity.ScannedEntity entity) {
      if (entity != null && entity.isGE) {
         this.checkLimit(XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT, entity, 0, 1);
      }

   }

   protected void checkLimit(XMLSecurityManager.Limit limit, Entity.ScannedEntity entity, int offset, int length) {
      this.fLimitAnalyzer.addValue(limit, entity.name, length);
      if (this.fSecurityManager.isOverLimit(limit, this.fLimitAnalyzer)) {
         this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
         Object[] e = limit == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT ? new Object[]{this.fLimitAnalyzer.getValue(limit), this.fSecurityManager.getLimit(limit), this.fSecurityManager.getStateLiteral(limit)} : new Object[]{entity.name, this.fLimitAnalyzer.getValue(limit), this.fSecurityManager.getLimit(limit), this.fSecurityManager.getStateLiteral(limit)};
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", limit.key(), e, (short)2);
      }

      if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
         this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimit", new Object[]{this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)}, (short)2);
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
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[offset];
      int newlines = 0;
      boolean counted = false;
      int length;
      if (c == '\n' || c == '\r' && this.isExternal) {
         do {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c == '\r' && this.isExternal) {
               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, newlines);
                  offset = 0;
                  this.fCurrentEntity.position = newlines;
                  if (this.load(newlines, false, true)) {
                     counted = true;
                     break;
                  }
               }

               if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                  ++this.fCurrentEntity.position;
                  ++offset;
               } else {
                  ++newlines;
               }
            } else {
               if (c != '\n') {
                  --this.fCurrentEntity.position;
                  break;
               }

               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, newlines);
                  offset = 0;
                  this.fCurrentEntity.position = newlines;
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

      while(this.fCurrentEntity.position < this.fCurrentEntity.count) {
         c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
         if (!XMLChar.isContent(c)) {
            --this.fCurrentEntity.position;
            break;
         }
      }

      length = this.fCurrentEntity.position - offset;
      Entity.ScannedEntity var10000 = this.fCurrentEntity;
      var10000.columnNumber += length - newlines;
      if (!counted) {
         this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, length);
      }

      content.setValues(this.fCurrentEntity.ch, offset, length);
      int c;
      if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
         c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (c == 13 && this.isExternal) {
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
         this.fCurrentEntity.position = 0;
      }

      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[offset];
      int newlines = 0;
      if (this.whiteSpaceInfoNeeded) {
         this.whiteSpaceLen = 0;
      }

      int i;
      if (c == '\n' || c == '\r' && this.isExternal) {
         do {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c == '\r' && this.isExternal) {
               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  offset = 0;
                  this.fCurrentEntity.position = newlines;
                  if (this.load(newlines, false, true)) {
                     break;
                  }
               }

               if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                  ++this.fCurrentEntity.position;
                  ++offset;
               } else {
                  ++newlines;
               }
            } else {
               if (c != '\n') {
                  --this.fCurrentEntity.position;
                  break;
               }

               ++newlines;
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                  offset = 0;
                  this.fCurrentEntity.position = newlines;
                  if (this.load(newlines, false, true)) {
                     break;
                  }
               }
            }
         } while(this.fCurrentEntity.position < this.fCurrentEntity.count - 1);

         int i = false;

         for(i = offset; i < this.fCurrentEntity.position; ++i) {
            this.fCurrentEntity.ch[i] = '\n';
            this.storeWhiteSpace(i);
         }

         int length = this.fCurrentEntity.position - offset;
         if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            content.setValues(this.fCurrentEntity.ch, offset, length);
            return -1;
         }
      }

      for(; this.fCurrentEntity.position < this.fCurrentEntity.count; ++this.fCurrentEntity.position) {
         c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
         if (c == quote && (!this.fCurrentEntity.literal || this.isExternal) || c == '%' || !XMLChar.isContent(c)) {
            break;
         }

         if (this.whiteSpaceInfoNeeded && c == '\t') {
            this.storeWhiteSpace(this.fCurrentEntity.position);
         }
      }

      i = this.fCurrentEntity.position - offset;
      Entity.ScannedEntity var10000 = this.fCurrentEntity;
      var10000.columnNumber += i - newlines;
      this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, offset, i);
      if (isNSURI) {
         this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, i);
      }

      content.setValues(this.fCurrentEntity.ch, offset, i);
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

   private void storeWhiteSpace(int whiteSpacePos) {
      if (this.whiteSpaceLen >= this.whiteSpaceLookup.length) {
         int[] tmp = new int[this.whiteSpaceLookup.length + 100];
         System.arraycopy(this.whiteSpaceLookup, 0, tmp, 0, this.whiteSpaceLookup.length);
         this.whiteSpaceLookup = tmp;
      }

      this.whiteSpaceLookup[this.whiteSpaceLen++] = whiteSpacePos;
   }

   protected boolean scanData(String delimiter, XMLStringBuffer buffer) throws IOException {
      boolean done = false;
      int delimLen = delimiter.length();
      char charAt0 = delimiter.charAt(0);

      do {
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, false);
         }

         for(boolean bNextEntity = false; this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen && !bNextEntity; this.fCurrentEntity.startPosition = 0) {
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            bNextEntity = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
            this.fCurrentEntity.position = 0;
         }

         int offset;
         Entity.ScannedEntity var10000;
         if (this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen) {
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
         if (c == '\n' || c == '\r' && this.isExternal) {
            do {
               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
               if (c == '\r' && this.isExternal) {
                  ++newlines;
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                     offset = 0;
                     this.fCurrentEntity.position = newlines;
                     if (this.load(newlines, false, true)) {
                        break;
                     }
                  }

                  if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                     ++this.fCurrentEntity.position;
                     ++offset;
                  } else {
                     ++newlines;
                  }
               } else {
                  if (c != '\n') {
                     --this.fCurrentEntity.position;
                     break;
                  }

                  ++newlines;
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                     offset = 0;
                     this.fCurrentEntity.position = newlines;
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

         label133:
         while(true) {
            while(true) {
               if (this.fCurrentEntity.position >= this.fCurrentEntity.count) {
                  break label133;
               }

               c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
               if (c != charAt0) {
                  if (c == '\n' || this.isExternal && c == '\r') {
                     --this.fCurrentEntity.position;
                     break label133;
                  }

                  if (XMLChar.isInvalid(c)) {
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

                  for(int i = 1; i < delimLen; ++i) {
                     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        var10000 = this.fCurrentEntity;
                        var10000.position -= i;
                        break label133;
                     }

                     c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                     if (delimiter.charAt(i) != c) {
                        var10000 = this.fCurrentEntity;
                        var10000.position -= i;
                        break;
                     }
                  }

                  if (this.fCurrentEntity.position == length + delimLen) {
                     done = true;
                     break label133;
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
      } else if (c == 10 && cc == '\r' && this.isExternal) {
         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = (char)cc;
            this.load(1, false, false);
         }

         ++this.fCurrentEntity.position;
         if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
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

   public boolean isSpace(char ch) {
      return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
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
         if (!XMLChar.isSpace(c)) {
            return false;
         } else {
            do {
               boolean entityChanged = false;
               if (c == '\n' || this.isExternal && c == '\r') {
                  ++this.fCurrentEntity.lineNumber;
                  this.fCurrentEntity.columnNumber = 1;
                  if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                     this.invokeListeners(1);
                     this.fCurrentEntity.ch[0] = (char)c;
                     entityChanged = this.load(1, true, false);
                     if (!entityChanged) {
                        this.fCurrentEntity.position = 0;
                     } else if (this.fCurrentEntity == null) {
                        return true;
                     }
                  }

                  if (c == '\r' && this.isExternal && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                     --this.fCurrentEntity.position;
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
      }
   }

   public boolean arrangeCapacity(int length) throws IOException {
      return this.arrangeCapacity(length, false);
   }

   public boolean arrangeCapacity(int length, boolean changeEntity) throws IOException {
      if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length) {
         return true;
      } else {
         boolean entityChanged = false;

         while(this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
            if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < length) {
               this.invokeListeners(0);
               System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
               this.fCurrentEntity.count -= this.fCurrentEntity.position;
               this.fCurrentEntity.position = 0;
            }

            if (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
               int pos = this.fCurrentEntity.position;
               this.invokeListeners(pos);
               entityChanged = this.load(this.fCurrentEntity.count, changeEntity, false);
               this.fCurrentEntity.position = pos;
               if (entityChanged) {
                  break;
               }
            }
         }

         return this.fCurrentEntity.count - this.fCurrentEntity.position >= length;
      }
   }

   protected boolean skipString(String s) throws IOException {
      int length = s.length();
      if (this.arrangeCapacity(length, false)) {
         int beforeSkip = this.fCurrentEntity.position;
         int afterSkip = this.fCurrentEntity.position + length - 1;
         int var5 = length - 1;

         while(s.charAt(var5--) == this.fCurrentEntity.ch[afterSkip]) {
            if (afterSkip-- == beforeSkip) {
               this.fCurrentEntity.position += length;
               Entity.ScannedEntity var6 = this.fCurrentEntity;
               var6.columnNumber += length;
               if (!this.detectingVersion) {
                  this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, beforeSkip, length);
               }

               return true;
            }
         }
      }

      return false;
   }

   protected boolean skipString(char[] s) throws IOException {
      int length = s.length;
      if (this.arrangeCapacity(length, false)) {
         int beforeSkip = this.fCurrentEntity.position;

         for(int i = 0; i < length; ++i) {
            if (this.fCurrentEntity.ch[beforeSkip++] != s[i]) {
               return false;
            }
         }

         this.fCurrentEntity.position += length;
         Entity.ScannedEntity var10000 = this.fCurrentEntity;
         var10000.columnNumber += length;
         if (!this.detectingVersion) {
            this.checkEntityLimit((XMLScanner.NameType)null, this.fCurrentEntity, beforeSkip, length);
         }

         return true;
      } else {
         return false;
      }
   }

   final boolean load(int offset, boolean changeEntity, boolean notify) throws IOException {
      if (notify) {
         this.invokeListeners(offset);
      }

      this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
      int length = this.fCurrentEntity.ch.length - offset;
      if (!this.fCurrentEntity.mayReadChunks && length > 64) {
         length = 64;
      }

      int count = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, offset, length);
      boolean entityChanged = false;
      if (count != -1) {
         if (count != 0) {
            this.fCurrentEntity.fLastCount = count;
            this.fCurrentEntity.count = count + offset;
            this.fCurrentEntity.position = offset;
         }
      } else {
         this.fCurrentEntity.count = offset;
         this.fCurrentEntity.position = offset;
         entityChanged = true;
         if (changeEntity) {
            this.fEntityManager.endEntity();
            if (this.fCurrentEntity == null) {
               throw END_OF_DOCUMENT_ENTITY;
            }

            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
               this.load(0, true, false);
            }
         }
      }

      return entityChanged;
   }

   protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian) throws IOException {
      if (encoding == null) {
         encoding = "UTF-8";
      }

      String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
      if (ENCODING.equals("UTF-8")) {
         return new UTF8Reader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
      } else if (ENCODING.equals("US-ASCII")) {
         return new ASCIIReader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
      } else {
         boolean isBE;
         if (ENCODING.equals("ISO-10646-UCS-4")) {
            if (isBigEndian != null) {
               isBE = isBigEndian;
               if (isBE) {
                  return new UCSReader(inputStream, (short)8);
               }

               return new UCSReader(inputStream, (short)4);
            }

            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[]{encoding}, (short)2);
         }

         if (ENCODING.equals("ISO-10646-UCS-2")) {
            if (isBigEndian != null) {
               isBE = isBigEndian;
               if (isBE) {
                  return new UCSReader(inputStream, (short)2);
               }

               return new UCSReader(inputStream, (short)1);
            }

            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[]{encoding}, (short)2);
         }

         isBE = XMLChar.isValidIANAEncoding(encoding);
         boolean validJava = XMLChar.isValidJavaEncoding(encoding);
         if (!isBE || this.fAllowJavaEncodings && !validJava) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[]{encoding}, (short)2);
            encoding = "ISO-8859-1";
         }

         String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
         if (javaEncoding == null) {
            if (this.fAllowJavaEncodings) {
               javaEncoding = encoding;
            } else {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[]{encoding}, (short)2);
               javaEncoding = "ISO8859_1";
            }
         } else if (javaEncoding.equals("ASCII")) {
            return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
         }

         return new InputStreamReader(inputStream, javaEncoding);
      }
   }

   protected Object[] getEncodingName(byte[] b4, int count) {
      if (count < 2) {
         return new Object[]{"UTF-8", null};
      } else {
         int b0 = b4[0] & 255;
         int b1 = b4[1] & 255;
         if (b0 == 254 && b1 == 255) {
            return new Object[]{"UTF-16BE", new Boolean(true)};
         } else if (b0 == 255 && b1 == 254) {
            return new Object[]{"UTF-16LE", new Boolean(false)};
         } else if (count < 3) {
            return new Object[]{"UTF-8", null};
         } else {
            int b2 = b4[2] & 255;
            if (b0 == 239 && b1 == 187 && b2 == 191) {
               return new Object[]{"UTF-8", null};
            } else if (count < 4) {
               return new Object[]{"UTF-8", null};
            } else {
               int b3 = b4[3] & 255;
               if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60) {
                  return new Object[]{"ISO-10646-UCS-4", new Boolean(true)};
               } else if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0) {
                  return new Object[]{"ISO-10646-UCS-4", new Boolean(false)};
               } else if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0) {
                  return new Object[]{"ISO-10646-UCS-4", null};
               } else if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0) {
                  return new Object[]{"ISO-10646-UCS-4", null};
               } else if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
                  return new Object[]{"UTF-16BE", new Boolean(true)};
               } else if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
                  return new Object[]{"UTF-16LE", new Boolean(false)};
               } else {
                  return b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148 ? new Object[]{"CP037", null} : new Object[]{"UTF-8", null};
               }
            }
         }
      }
   }

   final void print() {
   }

   public void registerListener(XMLBufferListener listener) {
      if (!this.listeners.contains(listener)) {
         this.listeners.add(listener);
      }

   }

   public void invokeListeners(int loadPos) {
      for(int i = 0; i < this.listeners.size(); ++i) {
         ((XMLBufferListener)this.listeners.get(i)).refresh(loadPos);
      }

   }

   protected final boolean skipDeclSpaces() throws IOException {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
         this.load(0, true, false);
      }

      int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (!XMLChar.isSpace(c)) {
         return false;
      } else {
         boolean external = this.fCurrentEntity.isExternal();

         do {
            boolean entityChanged = false;
            if (c != '\n' && (!external || c != '\r')) {
               ++this.fCurrentEntity.columnNumber;
            } else {
               ++this.fCurrentEntity.lineNumber;
               this.fCurrentEntity.columnNumber = 1;
               if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                  this.fCurrentEntity.ch[0] = (char)c;
                  entityChanged = this.load(1, true, false);
                  if (!entityChanged) {
                     this.fCurrentEntity.position = 0;
                  }
               }

               if (c == '\r' && external && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                  --this.fCurrentEntity.position;
               }
            }

            if (!entityChanged) {
               ++this.fCurrentEntity.position;
            }

            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
               this.load(0, true, false);
            }
         } while(XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));

         return true;
      }
   }

   static {
      int i;
      for(i = 65; i <= 90; ++i) {
         VALID_NAMES[i] = true;
      }

      for(i = 97; i <= 122; ++i) {
         VALID_NAMES[i] = true;
      }

      for(i = 48; i <= 57; ++i) {
         VALID_NAMES[i] = true;
      }

      VALID_NAMES[45] = true;
      VALID_NAMES[46] = true;
      VALID_NAMES[58] = true;
      VALID_NAMES[95] = true;
   }
}
