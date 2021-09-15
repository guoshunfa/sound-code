package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxErrorReporter;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import com.sun.xml.internal.stream.events.EntityDeclarationImpl;
import com.sun.xml.internal.stream.events.NotationDeclarationImpl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderImpl implements XMLStreamReader {
   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String READER_IN_DEFINED_STATE = "http://java.sun.com/xml/stream/properties/reader-in-defined-state";
   private SymbolTable fSymbolTable = new SymbolTable();
   protected XMLDocumentScannerImpl fScanner = new XMLNSDocumentScannerImpl();
   protected NamespaceContextWrapper fNamespaceContextWrapper;
   protected XMLEntityManager fEntityManager;
   protected StaxErrorReporter fErrorReporter;
   protected XMLEntityScanner fEntityScanner;
   protected XMLInputSource fInputSource;
   protected PropertyManager fPropertyManager;
   private int fEventType;
   static final boolean DEBUG = false;
   private boolean fReuse;
   private boolean fReaderInDefinedState;
   private boolean fBindNamespaces;
   private String fDTDDecl;
   private String versionStr;

   public XMLStreamReaderImpl(InputStream inputStream, PropertyManager props) throws XMLStreamException {
      this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
      this.fEntityManager = new XMLEntityManager();
      this.fErrorReporter = new StaxErrorReporter();
      this.fEntityScanner = null;
      this.fInputSource = null;
      this.fPropertyManager = null;
      this.fReuse = true;
      this.fReaderInDefinedState = true;
      this.fBindNamespaces = true;
      this.fDTDDecl = null;
      this.versionStr = null;
      this.init(props);
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, inputStream, (String)null);
      this.setInputSource(inputSource);
   }

   public XMLDocumentScannerImpl getScanner() {
      System.out.println("returning scanner");
      return this.fScanner;
   }

   public XMLStreamReaderImpl(String systemid, PropertyManager props) throws XMLStreamException {
      this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
      this.fEntityManager = new XMLEntityManager();
      this.fErrorReporter = new StaxErrorReporter();
      this.fEntityScanner = null;
      this.fInputSource = null;
      this.fPropertyManager = null;
      this.fReuse = true;
      this.fReaderInDefinedState = true;
      this.fBindNamespaces = true;
      this.fDTDDecl = null;
      this.versionStr = null;
      this.init(props);
      XMLInputSource inputSource = new XMLInputSource((String)null, systemid, (String)null);
      this.setInputSource(inputSource);
   }

   public XMLStreamReaderImpl(InputStream inputStream, String encoding, PropertyManager props) throws XMLStreamException {
      this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
      this.fEntityManager = new XMLEntityManager();
      this.fErrorReporter = new StaxErrorReporter();
      this.fEntityScanner = null;
      this.fInputSource = null;
      this.fPropertyManager = null;
      this.fReuse = true;
      this.fReaderInDefinedState = true;
      this.fBindNamespaces = true;
      this.fDTDDecl = null;
      this.versionStr = null;
      this.init(props);
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, new BufferedInputStream(inputStream), encoding);
      this.setInputSource(inputSource);
   }

   public XMLStreamReaderImpl(Reader reader, PropertyManager props) throws XMLStreamException {
      this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
      this.fEntityManager = new XMLEntityManager();
      this.fErrorReporter = new StaxErrorReporter();
      this.fEntityScanner = null;
      this.fInputSource = null;
      this.fPropertyManager = null;
      this.fReuse = true;
      this.fReaderInDefinedState = true;
      this.fBindNamespaces = true;
      this.fDTDDecl = null;
      this.versionStr = null;
      this.init(props);
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, new BufferedReader(reader), (String)null);
      this.setInputSource(inputSource);
   }

   public XMLStreamReaderImpl(XMLInputSource inputSource, PropertyManager props) throws XMLStreamException {
      this.fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)this.fScanner.getNamespaceContext());
      this.fEntityManager = new XMLEntityManager();
      this.fErrorReporter = new StaxErrorReporter();
      this.fEntityScanner = null;
      this.fInputSource = null;
      this.fPropertyManager = null;
      this.fReuse = true;
      this.fReaderInDefinedState = true;
      this.fBindNamespaces = true;
      this.fDTDDecl = null;
      this.versionStr = null;
      this.init(props);
      this.setInputSource(inputSource);
   }

   public void setInputSource(XMLInputSource inputSource) throws XMLStreamException {
      this.fReuse = false;

      try {
         this.fScanner.setInputSource(inputSource);
         if (this.fReaderInDefinedState) {
            this.fEventType = this.fScanner.next();
            if (this.versionStr == null) {
               this.versionStr = this.getVersion();
            }

            if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1")) {
               this.switchToXML11Scanner();
            }
         }

      } catch (IOException var3) {
         throw new XMLStreamException(var3);
      } catch (XNIException var4) {
         throw new XMLStreamException(var4.getMessage(), this.getLocation(), var4.getException());
      }
   }

   void init(PropertyManager propertyManager) throws XMLStreamException {
      this.fPropertyManager = propertyManager;
      propertyManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
      propertyManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      propertyManager.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
      this.reset();
   }

   public boolean canReuse() {
      return this.fReuse;
   }

   public void reset() {
      this.fReuse = true;
      this.fEventType = 0;
      this.fEntityManager.reset(this.fPropertyManager);
      this.fScanner.reset(this.fPropertyManager);
      this.fDTDDecl = null;
      this.fEntityScanner = this.fEntityManager.getEntityScanner();
      this.fReaderInDefinedState = (Boolean)this.fPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/reader-in-defined-state");
      this.fBindNamespaces = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isNamespaceAware");
      this.versionStr = null;
   }

   public void close() throws XMLStreamException {
      this.fReuse = true;
   }

   public String getCharacterEncodingScheme() {
      return this.fScanner.getCharacterEncodingScheme();
   }

   public int getColumnNumber() {
      return this.fEntityScanner.getColumnNumber();
   }

   public String getEncoding() {
      return this.fEntityScanner.getEncoding();
   }

   public int getEventType() {
      return this.fEventType;
   }

   public int getLineNumber() {
      return this.fEntityScanner.getLineNumber();
   }

   public String getLocalName() {
      if (this.fEventType != 1 && this.fEventType != 2) {
         if (this.fEventType == 9) {
            return this.fScanner.getEntityName();
         } else {
            throw new IllegalStateException("Method getLocalName() cannot be called for " + getEventTypeString(this.fEventType) + " event.");
         }
      } else {
         return this.fScanner.getElementQName().localpart;
      }
   }

   public String getNamespaceURI() {
      return this.fEventType != 1 && this.fEventType != 2 ? null : this.fScanner.getElementQName().uri;
   }

   public String getPIData() {
      if (this.fEventType == 3) {
         return this.fScanner.getPIData().toString();
      } else {
         throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + 3);
      }
   }

   public String getPITarget() {
      if (this.fEventType == 3) {
         return this.fScanner.getPITarget();
      } else {
         throw new IllegalStateException("Current state of the parser is " + getEventTypeString(this.fEventType) + " But Expected state is " + 3);
      }
   }

   public String getPrefix() {
      if (this.fEventType != 1 && this.fEventType != 2) {
         return null;
      } else {
         String prefix = this.fScanner.getElementQName().prefix;
         return prefix == null ? "" : prefix;
      }
   }

   public char[] getTextCharacters() {
      if (this.fEventType != 4 && this.fEventType != 5 && this.fEventType != 12 && this.fEventType != 6) {
         throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextCharacters() ");
      } else {
         return this.fScanner.getCharacterData().ch;
      }
   }

   public int getTextLength() {
      if (this.fEventType != 4 && this.fEventType != 5 && this.fEventType != 12 && this.fEventType != 6) {
         throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextLength() ");
      } else {
         return this.fScanner.getCharacterData().length;
      }
   }

   public int getTextStart() {
      if (this.fEventType != 4 && this.fEventType != 5 && this.fEventType != 12 && this.fEventType != 6) {
         throw new IllegalStateException("Current state = " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextStart() ");
      } else {
         return this.fScanner.getCharacterData().offset;
      }
   }

   public String getValue() {
      if (this.fEventType == 3) {
         return this.fScanner.getPIData().toString();
      } else if (this.fEventType == 5) {
         return this.fScanner.getComment();
      } else if (this.fEventType != 1 && this.fEventType != 2) {
         return this.fEventType == 4 ? this.fScanner.getCharacterData().toString() : null;
      } else {
         return this.fScanner.getElementQName().localpart;
      }
   }

   public String getVersion() {
      String version = this.fEntityScanner.getXMLVersion();
      return "1.0".equals(version) && !this.fEntityScanner.xmlVersionSetExplicitly ? null : version;
   }

   public boolean hasAttributes() {
      return this.fScanner.getAttributeIterator().getLength() > 0;
   }

   public boolean hasName() {
      return this.fEventType == 1 || this.fEventType == 2;
   }

   public boolean hasNext() throws XMLStreamException {
      if (this.fEventType == -1) {
         return false;
      } else {
         return this.fEventType != 8;
      }
   }

   public boolean hasValue() {
      return this.fEventType == 1 || this.fEventType == 2 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 4;
   }

   public boolean isEndElement() {
      return this.fEventType == 2;
   }

   public boolean isStandalone() {
      return this.fScanner.isStandAlone();
   }

   public boolean isStartElement() {
      return this.fEventType == 1;
   }

   public boolean isWhiteSpace() {
      if (!this.isCharacters() && this.fEventType != 12) {
         return false;
      } else {
         char[] ch = this.getTextCharacters();
         int start = this.getTextStart();
         int end = start + this.getTextLength();

         for(int i = start; i < end; ++i) {
            if (!XMLChar.isSpace(ch[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public int next() throws XMLStreamException {
      if (!this.hasNext()) {
         if (this.fEventType != -1) {
            throw new NoSuchElementException("END_DOCUMENT reached: no more elements on the stream.");
         } else {
            throw new XMLStreamException("Error processing input source. The input stream is not complete.");
         }
      } else {
         try {
            this.fEventType = this.fScanner.next();
            if (this.versionStr == null) {
               this.versionStr = this.getVersion();
            }

            if (this.fEventType == 7 && this.versionStr != null && this.versionStr.equals("1.1")) {
               this.switchToXML11Scanner();
            }

            if (this.fEventType == 4 || this.fEventType == 9 || this.fEventType == 3 || this.fEventType == 5 || this.fEventType == 12) {
               this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
            }

            return this.fEventType;
         } catch (IOException var3) {
            XMLDocumentScannerImpl var10001 = this.fScanner;
            if (this.fScanner.fScannerState == 46) {
               Boolean isValidating = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isValidating");
               if (isValidating != null && !isValidating) {
                  this.fEventType = 11;
                  var10001 = this.fScanner;
                  this.fScanner.setScannerState(43);
                  this.fScanner.setDriver(this.fScanner.fPrologDriver);
                  if (this.fDTDDecl == null || this.fDTDDecl.length() == 0) {
                     this.fDTDDecl = "<!-- Exception scanning External DTD Subset.  True contents of DTD cannot be determined.  Processing will continue as XMLInputFactory.IS_VALIDATING == false. -->";
                  }

                  return 11;
               }
            }

            throw new XMLStreamException(var3.getMessage(), this.getLocation(), var3);
         } catch (XNIException var4) {
            throw new XMLStreamException(var4.getMessage(), this.getLocation(), var4.getException());
         }
      }
   }

   private void switchToXML11Scanner() throws IOException {
      int oldEntityDepth = this.fScanner.fEntityDepth;
      NamespaceContext oldNamespaceContext = this.fScanner.fNamespaceContext;
      this.fScanner = new XML11NSDocumentScannerImpl();
      this.fScanner.reset(this.fPropertyManager);
      this.fScanner.setPropertyManager(this.fPropertyManager);
      this.fEntityScanner = this.fEntityManager.getEntityScanner();
      this.fEntityManager.fCurrentEntity.mayReadChunks = true;
      this.fScanner.setScannerState(7);
      this.fScanner.fEntityDepth = oldEntityDepth;
      this.fScanner.fNamespaceContext = oldNamespaceContext;
      this.fEventType = this.fScanner.next();
   }

   static final String getEventTypeString(int eventType) {
      switch(eventType) {
      case 1:
         return "START_ELEMENT";
      case 2:
         return "END_ELEMENT";
      case 3:
         return "PROCESSING_INSTRUCTION";
      case 4:
         return "CHARACTERS";
      case 5:
         return "COMMENT";
      case 6:
         return "SPACE";
      case 7:
         return "START_DOCUMENT";
      case 8:
         return "END_DOCUMENT";
      case 9:
         return "ENTITY_REFERENCE";
      case 10:
         return "ATTRIBUTE";
      case 11:
         return "DTD";
      case 12:
         return "CDATA";
      default:
         return "UNKNOWN_EVENT_TYPE, " + String.valueOf(eventType);
      }
   }

   public int getAttributeCount() {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeCount()");
      } else {
         return this.fScanner.getAttributeIterator().getLength();
      }
   }

   public QName getAttributeName(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeName()");
      } else {
         return this.convertXNIQNametoJavaxQName(this.fScanner.getAttributeIterator().getQualifiedName(index));
      }
   }

   public String getAttributeLocalName(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException();
      } else {
         return this.fScanner.getAttributeIterator().getLocalName(index);
      }
   }

   public String getAttributeNamespace(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeNamespace()");
      } else {
         return this.fScanner.getAttributeIterator().getURI(index);
      }
   }

   public String getAttributePrefix(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributePrefix()");
      } else {
         return this.fScanner.getAttributeIterator().getPrefix(index);
      }
   }

   public QName getAttributeQName(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeQName()");
      } else {
         String localName = this.fScanner.getAttributeIterator().getLocalName(index);
         String uri = this.fScanner.getAttributeIterator().getURI(index);
         return new QName(uri, localName);
      }
   }

   public String getAttributeType(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeType()");
      } else {
         return this.fScanner.getAttributeIterator().getType(index);
      }
   }

   public String getAttributeValue(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
      } else {
         return this.fScanner.getAttributeIterator().getValue(index);
      }
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
      } else {
         XMLAttributesImpl attributes = this.fScanner.getAttributeIterator();
         return namespaceURI == null ? attributes.getValue(attributes.getIndexByLocalName(localName)) : this.fScanner.getAttributeIterator().getValue(namespaceURI.length() == 0 ? null : namespaceURI, localName);
      }
   }

   public String getElementText() throws XMLStreamException {
      if (this.getEventType() != 1) {
         throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.getLocation());
      } else {
         int eventType = this.next();

         StringBuffer content;
         for(content = new StringBuffer(); eventType != 2; eventType = this.next()) {
            if (eventType != 4 && eventType != 12 && eventType != 6 && eventType != 9) {
               if (eventType != 3 && eventType != 5) {
                  if (eventType == 8) {
                     throw new XMLStreamException("unexpected end of document when reading element text content");
                  }

                  if (eventType == 1) {
                     throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", this.getLocation());
                  }

                  throw new XMLStreamException("Unexpected event type " + eventType, this.getLocation());
               }
            } else {
               content.append(this.getText());
            }
         }

         return content.toString();
      }
   }

   public Location getLocation() {
      return new Location() {
         String _systemId;
         String _publicId;
         int _offset;
         int _columnNumber;
         int _lineNumber;

         {
            this._systemId = XMLStreamReaderImpl.this.fEntityScanner.getExpandedSystemId();
            this._publicId = XMLStreamReaderImpl.this.fEntityScanner.getPublicId();
            this._offset = XMLStreamReaderImpl.this.fEntityScanner.getCharacterOffset();
            this._columnNumber = XMLStreamReaderImpl.this.fEntityScanner.getColumnNumber();
            this._lineNumber = XMLStreamReaderImpl.this.fEntityScanner.getLineNumber();
         }

         public String getLocationURI() {
            return this._systemId;
         }

         public int getCharacterOffset() {
            return this._offset;
         }

         public int getColumnNumber() {
            return this._columnNumber;
         }

         public int getLineNumber() {
            return this._lineNumber;
         }

         public String getPublicId() {
            return this._publicId;
         }

         public String getSystemId() {
            return this._systemId;
         }

         public String toString() {
            StringBuffer sbuffer = new StringBuffer();
            sbuffer.append("Line number = " + this.getLineNumber());
            sbuffer.append("\n");
            sbuffer.append("Column number = " + this.getColumnNumber());
            sbuffer.append("\n");
            sbuffer.append("System Id = " + this.getSystemId());
            sbuffer.append("\n");
            sbuffer.append("Public Id = " + this.getPublicId());
            sbuffer.append("\n");
            sbuffer.append("Location Uri= " + this.getLocationURI());
            sbuffer.append("\n");
            sbuffer.append("CharacterOffset = " + this.getCharacterOffset());
            sbuffer.append("\n");
            return sbuffer.toString();
         }
      };
   }

   public QName getName() {
      if (this.fEventType != 1 && this.fEventType != 2) {
         throw new IllegalStateException("Illegal to call getName() when event type is " + getEventTypeString(this.fEventType) + ". Valid states are " + getEventTypeString(1) + ", " + getEventTypeString(2));
      } else {
         return this.convertXNIQNametoJavaxQName(this.fScanner.getElementQName());
      }
   }

   public javax.xml.namespace.NamespaceContext getNamespaceContext() {
      return this.fNamespaceContextWrapper;
   }

   public int getNamespaceCount() {
      if (this.fEventType != 1 && this.fEventType != 2 && this.fEventType != 13) {
         throw new IllegalStateException("Current event state is " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceCount().");
      } else {
         return this.fScanner.getNamespaceContext().getDeclaredPrefixCount();
      }
   }

   public String getNamespacePrefix(int index) {
      if (this.fEventType != 1 && this.fEventType != 2 && this.fEventType != 13) {
         throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespacePrefix().");
      } else {
         String prefix = this.fScanner.getNamespaceContext().getDeclaredPrefixAt(index);
         return prefix.equals("") ? null : prefix;
      }
   }

   public String getNamespaceURI(int index) {
      if (this.fEventType != 1 && this.fEventType != 2 && this.fEventType != 13) {
         throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceURI().");
      } else {
         return this.fScanner.getNamespaceContext().getURI(this.fScanner.getNamespaceContext().getDeclaredPrefixAt(index));
      }
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      if (name == null) {
         throw new IllegalArgumentException();
      } else if (this.fPropertyManager != null) {
         PropertyManager var10001 = this.fPropertyManager;
         if (name.equals("javax.xml.stream.notations")) {
            return this.getNotationDecls();
         } else {
            var10001 = this.fPropertyManager;
            return name.equals("javax.xml.stream.entities") ? this.getEntityDecls() : this.fPropertyManager.getProperty(name);
         }
      } else {
         return null;
      }
   }

   public String getText() {
      if (this.fEventType != 4 && this.fEventType != 5 && this.fEventType != 12 && this.fEventType != 6) {
         if (this.fEventType == 9) {
            String name = this.fScanner.getEntityName();
            if (name != null) {
               if (this.fScanner.foundBuiltInRefs) {
                  return this.fScanner.getCharacterData().toString();
               } else {
                  XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
                  Entity en = entityStore.getEntity(name);
                  if (en == null) {
                     return null;
                  } else {
                     return en.isExternal() ? ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId() : ((Entity.InternalEntity)en).text;
                  }
               }
            } else {
               return null;
            }
         } else if (this.fEventType == 11) {
            if (this.fDTDDecl != null) {
               return this.fDTDDecl;
            } else {
               XMLStringBuffer tmpBuffer = this.fScanner.getDTDDecl();
               this.fDTDDecl = tmpBuffer.toString();
               return this.fDTDDecl;
            }
         } else {
            throw new IllegalStateException("Current state " + getEventTypeString(this.fEventType) + " is not among the states" + getEventTypeString(4) + ", " + getEventTypeString(5) + ", " + getEventTypeString(12) + ", " + getEventTypeString(6) + ", " + getEventTypeString(9) + ", " + getEventTypeString(11) + " valid for getText() ");
         }
      } else {
         return this.fScanner.getCharacterData().toString();
      }
   }

   public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      if (type != this.fEventType) {
         throw new XMLStreamException("Event type " + getEventTypeString(type) + " specified did not match with current parser event " + getEventTypeString(this.fEventType));
      } else if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
         throw new XMLStreamException("Namespace URI " + namespaceURI + " specified did not match with current namespace URI");
      } else if (localName != null && !localName.equals(this.getLocalName())) {
         throw new XMLStreamException("LocalName " + localName + " specified did not match with current local name");
      }
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
      if (target == null) {
         throw new NullPointerException("target char array can't be null");
      } else if (targetStart >= 0 && length >= 0 && sourceStart >= 0 && targetStart < target.length && targetStart + length <= target.length) {
         int copiedLength = false;
         int available = this.getTextLength() - sourceStart;
         if (available < 0) {
            throw new IndexOutOfBoundsException("sourceStart is greater thannumber of characters associated with this event");
         } else {
            int copiedLength;
            if (available < length) {
               copiedLength = available;
            } else {
               copiedLength = length;
            }

            System.arraycopy(this.getTextCharacters(), this.getTextStart() + sourceStart, target, targetStart, copiedLength);
            return copiedLength;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public boolean hasText() {
      if (this.fEventType != 4 && this.fEventType != 5 && this.fEventType != 12) {
         if (this.fEventType == 9) {
            String name = this.fScanner.getEntityName();
            if (name != null) {
               if (this.fScanner.foundBuiltInRefs) {
                  return true;
               } else {
                  XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
                  Entity en = entityStore.getEntity(name);
                  if (en == null) {
                     return false;
                  } else if (en.isExternal()) {
                     return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId() != null;
                  } else {
                     return ((Entity.InternalEntity)en).text != null;
                  }
               }
            } else {
               return false;
            }
         } else {
            return this.fEventType == 11 ? this.fScanner.fSeenDoctypeDecl : false;
         }
      } else {
         return this.fScanner.getCharacterData().length > 0;
      }
   }

   public boolean isAttributeSpecified(int index) {
      if (this.fEventType != 1 && this.fEventType != 10) {
         throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for isAttributeSpecified()");
      } else {
         return this.fScanner.getAttributeIterator().isSpecified(index);
      }
   }

   public boolean isCharacters() {
      return this.fEventType == 4;
   }

   public int nextTag() throws XMLStreamException {
      int eventType;
      for(eventType = this.next(); eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {
      }

      if (eventType != 1 && eventType != 2) {
         throw new XMLStreamException("found: " + getEventTypeString(eventType) + ", expected " + getEventTypeString(1) + " or " + getEventTypeString(2), this.getLocation());
      } else {
         return eventType;
      }
   }

   public boolean standaloneSet() {
      return this.fScanner.standaloneSet();
   }

   public QName convertXNIQNametoJavaxQName(com.sun.org.apache.xerces.internal.xni.QName qname) {
      if (qname == null) {
         return null;
      } else {
         return qname.prefix == null ? new QName(qname.uri, qname.localpart) : new QName(qname.uri, qname.localpart, qname.prefix);
      }
   }

   public String getNamespaceURI(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("prefix cannot be null.");
      } else {
         return this.fScanner.getNamespaceContext().getURI(this.fSymbolTable.addSymbol(prefix));
      }
   }

   protected void setPropertyManager(PropertyManager propertyManager) {
      this.fPropertyManager = propertyManager;
      this.fScanner.setProperty("stax-properties", propertyManager);
      this.fScanner.setPropertyManager(propertyManager);
   }

   protected PropertyManager getPropertyManager() {
      return this.fPropertyManager;
   }

   static void pr(String str) {
      System.out.println(str);
   }

   protected List getEntityDecls() {
      if (this.fEventType != 11) {
         return null;
      } else {
         XMLEntityStorage entityStore = this.fEntityManager.getEntityStore();
         ArrayList list = null;
         if (entityStore.hasEntities()) {
            EntityDeclarationImpl decl = null;
            list = new ArrayList(entityStore.getEntitySize());

            for(Enumeration enu = entityStore.getEntityKeys(); enu.hasMoreElements(); list.add(decl)) {
               String key = (String)enu.nextElement();
               Entity en = entityStore.getEntity(key);
               decl = new EntityDeclarationImpl();
               decl.setEntityName(key);
               if (en.isExternal()) {
                  decl.setXMLResourceIdentifier(((Entity.ExternalEntity)en).entityLocation);
                  decl.setNotationName(((Entity.ExternalEntity)en).notation);
               } else {
                  decl.setEntityReplacementText(((Entity.InternalEntity)en).text);
               }
            }
         }

         return list;
      }
   }

   protected List getNotationDecls() {
      if (this.fEventType == 11) {
         if (this.fScanner.fDTDScanner == null) {
            return null;
         } else {
            DTDGrammar grammar = ((XMLDTDScannerImpl)((XMLDTDScannerImpl)this.fScanner.fDTDScanner)).getGrammar();
            if (grammar == null) {
               return null;
            } else {
               List notations = grammar.getNotationDecls();
               Iterator it = notations.iterator();
               ArrayList list = new ArrayList();

               while(it.hasNext()) {
                  XMLNotationDecl ni = (XMLNotationDecl)it.next();
                  if (ni != null) {
                     list.add(new NotationDeclarationImpl(ni));
                  }
               }

               return list;
            }
         }
      } else {
         return null;
      }
   }
}
