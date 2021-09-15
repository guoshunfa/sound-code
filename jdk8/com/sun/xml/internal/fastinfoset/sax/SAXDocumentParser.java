package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.EncodingConstants;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmState;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentParser extends Decoder implements FastInfosetReader {
   private static final Logger logger = Logger.getLogger(SAXDocumentParser.class.getName());
   protected boolean _namespacePrefixesFeature = false;
   protected EntityResolver _entityResolver;
   protected DTDHandler _dtdHandler;
   protected ContentHandler _contentHandler;
   protected ErrorHandler _errorHandler;
   protected LexicalHandler _lexicalHandler;
   protected DeclHandler _declHandler;
   protected EncodingAlgorithmContentHandler _algorithmHandler;
   protected PrimitiveTypeContentHandler _primitiveHandler;
   protected BuiltInEncodingAlgorithmState builtInAlgorithmState = new BuiltInEncodingAlgorithmState();
   protected AttributesHolder _attributes;
   protected int[] _namespacePrefixes = new int[16];
   protected int _namespacePrefixesIndex;
   protected boolean _clearAttributes = false;

   public SAXDocumentParser() {
      DefaultHandler handler = new DefaultHandler();
      this._attributes = new AttributesHolder(this._registeredEncodingAlgorithms);
      this._entityResolver = handler;
      this._dtdHandler = handler;
      this._contentHandler = handler;
      this._errorHandler = handler;
      this._lexicalHandler = new SAXDocumentParser.LexicalHandlerImpl();
      this._declHandler = new SAXDocumentParser.DeclHandlerImpl();
   }

   protected void resetOnError() {
      this._clearAttributes = false;
      this._attributes.clear();
      this._namespacePrefixesIndex = 0;
      if (this._v != null) {
         this._v.prefix.clearCompletely();
      }

      this._duplicateAttributeVerifier.clear();
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         return true;
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         return this._namespacePrefixesFeature;
      } else if (!name.equals("http://xml.org/sax/features/string-interning") && !name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
         throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
      } else {
         return this.getStringInterning();
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         if (!value) {
            throw new SAXNotSupportedException(name + ":" + value);
         }
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         this._namespacePrefixesFeature = value;
      } else {
         if (!name.equals("http://xml.org/sax/features/string-interning") && !name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
            throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
         }

         this.setStringInterning(value);
      }

   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
         return this.getLexicalHandler();
      } else if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
         return this.getDeclHandler();
      } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
         return this.getExternalVocabularies();
      } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
         return this.getRegisteredEncodingAlgorithms();
      } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
         return this.getEncodingAlgorithmContentHandler();
      } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
         return this.getPrimitiveTypeContentHandler();
      } else {
         throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[]{name}));
      }
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
         if (!(value instanceof LexicalHandler)) {
            throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
         }

         this.setLexicalHandler((LexicalHandler)value);
      } else if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
         if (!(value instanceof DeclHandler)) {
            throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
         }

         this.setDeclHandler((DeclHandler)value);
      } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
         if (!(value instanceof Map)) {
            throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies");
         }

         this.setExternalVocabularies((Map)value);
      } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
         if (!(value instanceof Map)) {
            throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms");
         }

         this.setRegisteredEncodingAlgorithms((Map)value);
      } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
         if (!(value instanceof EncodingAlgorithmContentHandler)) {
            throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler");
         }

         this.setEncodingAlgorithmContentHandler((EncodingAlgorithmContentHandler)value);
      } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
         if (!(value instanceof PrimitiveTypeContentHandler)) {
            throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler");
         }

         this.setPrimitiveTypeContentHandler((PrimitiveTypeContentHandler)value);
      } else {
         if (!name.equals("http://jvnet.org/fastinfoset/parser/properties/buffer-size")) {
            throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[]{name}));
         }

         if (!(value instanceof Integer)) {
            throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/buffer-size");
         }

         this.setBufferSize((Integer)value);
      }

   }

   public void setEntityResolver(EntityResolver resolver) {
      this._entityResolver = resolver;
   }

   public EntityResolver getEntityResolver() {
      return this._entityResolver;
   }

   public void setDTDHandler(DTDHandler handler) {
      this._dtdHandler = handler;
   }

   public DTDHandler getDTDHandler() {
      return this._dtdHandler;
   }

   public void setContentHandler(ContentHandler handler) {
      this._contentHandler = handler;
   }

   public ContentHandler getContentHandler() {
      return this._contentHandler;
   }

   public void setErrorHandler(ErrorHandler handler) {
      this._errorHandler = handler;
   }

   public ErrorHandler getErrorHandler() {
      return this._errorHandler;
   }

   public void parse(InputSource input) throws IOException, SAXException {
      try {
         InputStream s = input.getByteStream();
         if (s == null) {
            String systemId = input.getSystemId();
            if (systemId == null) {
               throw new SAXException(CommonResourceBundle.getInstance().getString("message.inputSource"));
            }

            this.parse(systemId);
         } else {
            this.parse(s);
         }

      } catch (FastInfosetException var4) {
         logger.log(Level.FINE, (String)"parsing error", (Throwable)var4);
         throw new SAXException(var4);
      }
   }

   public void parse(String systemId) throws IOException, SAXException {
      try {
         systemId = SystemIdResolver.getAbsoluteURI(systemId);
         this.parse((new URL(systemId)).openStream());
      } catch (FastInfosetException var3) {
         logger.log(Level.FINE, (String)"parsing error", (Throwable)var3);
         throw new SAXException(var3);
      }
   }

   public final void parse(InputStream s) throws IOException, FastInfosetException, SAXException {
      this.setInputStream(s);
      this.parse();
   }

   public void setLexicalHandler(LexicalHandler handler) {
      this._lexicalHandler = handler;
   }

   public LexicalHandler getLexicalHandler() {
      return this._lexicalHandler;
   }

   public void setDeclHandler(DeclHandler handler) {
      this._declHandler = handler;
   }

   public DeclHandler getDeclHandler() {
      return this._declHandler;
   }

   public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler handler) {
      this._algorithmHandler = handler;
   }

   public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler() {
      return this._algorithmHandler;
   }

   public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler handler) {
      this._primitiveHandler = handler;
   }

   public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler() {
      return this._primitiveHandler;
   }

   public final void parse() throws FastInfosetException, IOException {
      if (this._octetBuffer.length < this._bufferSize) {
         this._octetBuffer = new byte[this._bufferSize];
      }

      try {
         this.reset();
         this.decodeHeader();
         if (this._parseFragments) {
            this.processDIIFragment();
         } else {
            this.processDII();
         }

      } catch (RuntimeException var6) {
         RuntimeException e = var6;

         try {
            this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), (Locator)null, e));
         } catch (Exception var4) {
         }

         this.resetOnError();
         throw new FastInfosetException(var6);
      } catch (FastInfosetException var7) {
         FastInfosetException e = var7;

         try {
            this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), (Locator)null, e));
         } catch (Exception var5) {
         }

         this.resetOnError();
         throw var7;
      } catch (IOException var8) {
         IOException e = var8;

         try {
            this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), (Locator)null, e));
         } catch (Exception var3) {
         }

         this.resetOnError();
         throw var8;
      }
   }

   protected final void processDII() throws FastInfosetException, IOException {
      try {
         this._contentHandler.startDocument();
      } catch (SAXException var6) {
         throw new FastInfosetException("processDII", var6);
      }

      this._b = this.read();
      if (this._b > 0) {
         this.processDIIOptionalProperties();
      }

      boolean firstElementHasOccured = false;
      boolean documentTypeDeclarationOccured = false;

      while(!this._terminate || !firstElementHasOccured) {
         this._b = this.read();
         switch(DecoderStateTables.DII(this._b)) {
         case 0:
            this.processEII(this._elementNameTable._array[this._b], false);
            firstElementHasOccured = true;
            break;
         case 1:
            this.processEII(this._elementNameTable._array[this._b & 31], true);
            firstElementHasOccured = true;
            break;
         case 2:
            this.processEII(this.decodeEIIIndexMedium(), (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 3:
            this.processEII(this.decodeEIIIndexLarge(), (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 4:
            this.processEIIWithNamespaces();
            firstElementHasOccured = true;
            break;
         case 5:
            QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
            this._elementNameTable.add(qn);
            this.processEII(qn, (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 21:
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
         case 18:
            this.processCommentII();
            break;
         case 19:
            this.processProcessingII();
            break;
         case 20:
            if (documentTypeDeclarationOccured) {
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
            }

            documentTypeDeclarationOccured = true;
            String var10000;
            if ((this._b & 2) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = "";
            }

            if ((this._b & 1) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = "";
            }

            this._b = this.read();

            while(this._b == 225) {
               switch(this.decodeNonIdentifyingStringOnFirstBit()) {
               case 0:
                  if (this._addToTable) {
                     this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                  }
               case 1:
               case 3:
               default:
                  this._b = this.read();
                  break;
               case 2:
                  throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
               }
            }

            if ((this._b & 240) != 240) {
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
            }

            if (this._b == 255) {
               this._terminate = true;
            }

            if (this._notations != null) {
               this._notations.clear();
            }

            if (this._unparsedEntities != null) {
               this._unparsedEntities.clear();
            }
            break;
         case 23:
            this._doubleTerminate = true;
         case 22:
            this._terminate = true;
         }
      }

      while(!this._terminate) {
         this._b = this.read();
         switch(DecoderStateTables.DII(this._b)) {
         case 18:
            this.processCommentII();
            break;
         case 19:
            this.processProcessingII();
            break;
         case 20:
         case 21:
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
         case 23:
            this._doubleTerminate = true;
         case 22:
            this._terminate = true;
         }
      }

      try {
         this._contentHandler.endDocument();
      } catch (SAXException var5) {
         throw new FastInfosetException("processDII", var5);
      }
   }

   protected final void processDIIFragment() throws FastInfosetException, IOException {
      try {
         this._contentHandler.startDocument();
      } catch (SAXException var15) {
         throw new FastInfosetException("processDII", var15);
      }

      this._b = this.read();
      if (this._b > 0) {
         this.processDIIOptionalProperties();
      }

      while(!this._terminate) {
         this._b = this.read();
         int index;
         boolean addToTable;
         switch(DecoderStateTables.EII(this._b)) {
         case 0:
            this.processEII(this._elementNameTable._array[this._b], false);
            break;
         case 1:
            this.processEII(this._elementNameTable._array[this._b & 31], true);
            break;
         case 2:
            this.processEII(this.decodeEIIIndexMedium(), (this._b & 64) > 0);
            break;
         case 3:
            this.processEII(this.decodeEIIIndexLarge(), (this._b & 64) > 0);
            break;
         case 4:
            this.processEIIWithNamespaces();
            break;
         case 5:
            QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
            this._elementNameTable.add(qn);
            this.processEII(qn, (this._b & 64) > 0);
            break;
         case 6:
            this._octetBufferLength = (this._b & 1) + 1;
            this.processUtf8CharacterString();
            break;
         case 7:
            this._octetBufferLength = this.read() + 3;
            this.processUtf8CharacterString();
            break;
         case 8:
            this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
            this.processUtf8CharacterString();
            break;
         case 9:
            this._octetBufferLength = (this._b & 1) + 1;
            this.decodeUtf16StringAsCharBuffer();
            if ((this._b & 16) > 0) {
               this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }

            try {
               this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
               break;
            } catch (SAXException var14) {
               throw new FastInfosetException("processCII", var14);
            }
         case 10:
            this._octetBufferLength = this.read() + 3;
            this.decodeUtf16StringAsCharBuffer();
            if ((this._b & 16) > 0) {
               this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }

            try {
               this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
               break;
            } catch (SAXException var13) {
               throw new FastInfosetException("processCII", var13);
            }
         case 11:
            this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
            this.decodeUtf16StringAsCharBuffer();
            if ((this._b & 16) > 0) {
               this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }

            try {
               this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
               break;
            } catch (SAXException var12) {
               throw new FastInfosetException("processCII", var12);
            }
         case 12:
            addToTable = (this._b & 16) > 0;
            this._identifier = (this._b & 2) << 6;
            this._b = this.read();
            this._identifier |= (this._b & 252) >> 2;
            this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
            this.decodeRestrictedAlphabetAsCharBuffer();
            if (addToTable) {
               this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }

            try {
               this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
               break;
            } catch (SAXException var11) {
               throw new FastInfosetException("processCII", var11);
            }
         case 13:
            addToTable = (this._b & 16) > 0;
            this._identifier = (this._b & 2) << 6;
            this._b = this.read();
            this._identifier |= (this._b & 252) >> 2;
            this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
            this.processCIIEncodingAlgorithm(addToTable);
            break;
         case 14:
            index = this._b & 15;

            try {
               this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
               break;
            } catch (SAXException var10) {
               throw new FastInfosetException("processCII", var10);
            }
         case 15:
            index = ((this._b & 3) << 8 | this.read()) + 16;

            try {
               this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
               break;
            } catch (SAXException var9) {
               throw new FastInfosetException("processCII", var9);
            }
         case 16:
            index = ((this._b & 3) << 16 | this.read() << 8 | this.read()) + 1040;

            try {
               this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
               break;
            } catch (SAXException var8) {
               throw new FastInfosetException("processCII", var8);
            }
         case 17:
            index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;

            try {
               this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
               break;
            } catch (SAXException var7) {
               throw new FastInfosetException("processCII", var7);
            }
         case 18:
            this.processCommentII();
            break;
         case 19:
            this.processProcessingII();
            break;
         case 20:
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
         case 21:
            String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
            String var10000;
            if ((this._b & 2) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = "";
            }

            if ((this._b & 1) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = "";
            }

            try {
               this._contentHandler.skippedEntity(entity_reference_name);
               break;
            } catch (SAXException var6) {
               throw new FastInfosetException("processUnexpandedEntityReferenceII", var6);
            }
         case 23:
            this._doubleTerminate = true;
         case 22:
            this._terminate = true;
         }
      }

      try {
         this._contentHandler.endDocument();
      } catch (SAXException var5) {
         throw new FastInfosetException("processDII", var5);
      }
   }

   protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {
      if (this._b == 32) {
         this.decodeInitialVocabulary();
      } else {
         if ((this._b & 64) > 0) {
            this.decodeAdditionalData();
         }

         if ((this._b & 32) > 0) {
            this.decodeInitialVocabulary();
         }

         if ((this._b & 16) > 0) {
            this.decodeNotations();
         }

         if ((this._b & 8) > 0) {
            this.decodeUnparsedEntities();
         }

         if ((this._b & 4) > 0) {
            this.decodeCharacterEncodingScheme();
         }

         if ((this._b & 2) > 0) {
            this.read();
         }

         if ((this._b & 1) > 0) {
            this.decodeVersion();
         }

      }
   }

   protected final void processEII(QualifiedName name, boolean hasAttributes) throws FastInfosetException, IOException {
      if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameOfEIINotInScope"));
      } else {
         if (hasAttributes) {
            this.processAIIs();
         }

         try {
            this._contentHandler.startElement(name.namespaceName, name.localName, name.qName, this._attributes);
         } catch (SAXException var17) {
            logger.log(Level.FINE, (String)"processEII error", (Throwable)var17);
            throw new FastInfosetException("processEII", var17);
         }

         if (this._clearAttributes) {
            this._attributes.clear();
            this._clearAttributes = false;
         }

         while(!this._terminate) {
            this._b = this.read();
            int index;
            boolean addToTable;
            switch(DecoderStateTables.EII(this._b)) {
            case 0:
               this.processEII(this._elementNameTable._array[this._b], false);
               break;
            case 1:
               this.processEII(this._elementNameTable._array[this._b & 31], true);
               break;
            case 2:
               this.processEII(this.decodeEIIIndexMedium(), (this._b & 64) > 0);
               break;
            case 3:
               this.processEII(this.decodeEIIIndexLarge(), (this._b & 64) > 0);
               break;
            case 4:
               this.processEIIWithNamespaces();
               break;
            case 5:
               QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
               this._elementNameTable.add(qn);
               this.processEII(qn, (this._b & 64) > 0);
               break;
            case 6:
               this._octetBufferLength = (this._b & 1) + 1;
               this.processUtf8CharacterString();
               break;
            case 7:
               this._octetBufferLength = this.read() + 3;
               this.processUtf8CharacterString();
               break;
            case 8:
               this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
               this.processUtf8CharacterString();
               break;
            case 9:
               this._octetBufferLength = (this._b & 1) + 1;
               this.decodeUtf16StringAsCharBuffer();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               try {
                  this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                  break;
               } catch (SAXException var16) {
                  throw new FastInfosetException("processCII", var16);
               }
            case 10:
               this._octetBufferLength = this.read() + 3;
               this.decodeUtf16StringAsCharBuffer();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               try {
                  this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                  break;
               } catch (SAXException var15) {
                  throw new FastInfosetException("processCII", var15);
               }
            case 11:
               this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
               this.decodeUtf16StringAsCharBuffer();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               try {
                  this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                  break;
               } catch (SAXException var14) {
                  throw new FastInfosetException("processCII", var14);
               }
            case 12:
               addToTable = (this._b & 16) > 0;
               this._identifier = (this._b & 2) << 6;
               this._b = this.read();
               this._identifier |= (this._b & 252) >> 2;
               this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
               this.decodeRestrictedAlphabetAsCharBuffer();
               if (addToTable) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               try {
                  this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                  break;
               } catch (SAXException var13) {
                  throw new FastInfosetException("processCII", var13);
               }
            case 13:
               addToTable = (this._b & 16) > 0;
               this._identifier = (this._b & 2) << 6;
               this._b = this.read();
               this._identifier |= (this._b & 252) >> 2;
               this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
               this.processCIIEncodingAlgorithm(addToTable);
               break;
            case 14:
               index = this._b & 15;

               try {
                  this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                  break;
               } catch (SAXException var12) {
                  throw new FastInfosetException("processCII", var12);
               }
            case 15:
               index = ((this._b & 3) << 8 | this.read()) + 16;

               try {
                  this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                  break;
               } catch (SAXException var11) {
                  throw new FastInfosetException("processCII", var11);
               }
            case 16:
               index = ((this._b & 3) << 16 | this.read() << 8 | this.read()) + 1040;

               try {
                  this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                  break;
               } catch (SAXException var10) {
                  throw new FastInfosetException("processCII", var10);
               }
            case 17:
               index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;

               try {
                  this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                  break;
               } catch (SAXException var9) {
                  throw new FastInfosetException("processCII", var9);
               }
            case 18:
               this.processCommentII();
               break;
            case 19:
               this.processProcessingII();
               break;
            case 20:
            default:
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
            case 21:
               String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
               String var10000;
               if ((this._b & 2) > 0) {
                  this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
               } else {
                  var10000 = "";
               }

               if ((this._b & 1) > 0) {
                  this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
               } else {
                  var10000 = "";
               }

               try {
                  this._contentHandler.skippedEntity(entity_reference_name);
                  break;
               } catch (SAXException var8) {
                  throw new FastInfosetException("processUnexpandedEntityReferenceII", var8);
               }
            case 23:
               this._doubleTerminate = true;
            case 22:
               this._terminate = true;
            }
         }

         this._terminate = this._doubleTerminate;
         this._doubleTerminate = false;

         try {
            this._contentHandler.endElement(name.namespaceName, name.localName, name.qName);
         } catch (SAXException var7) {
            throw new FastInfosetException("processEII", var7);
         }
      }
   }

   private final void processUtf8CharacterString() throws FastInfosetException, IOException {
      if ((this._b & 16) > 0) {
         this._characterContentChunkTable.ensureSize(this._octetBufferLength);
         int charactersOffset = this._characterContentChunkTable._arrayIndex;
         this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
         this._characterContentChunkTable.add(this._charBufferLength);

         try {
            this._contentHandler.characters(this._characterContentChunkTable._array, charactersOffset, this._charBufferLength);
         } catch (SAXException var4) {
            throw new FastInfosetException("processCII", var4);
         }
      } else {
         this.decodeUtf8StringAsCharBuffer();

         try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
         } catch (SAXException var3) {
            throw new FastInfosetException("processCII", var3);
         }
      }

   }

   protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
      boolean hasAttributes = (this._b & 64) > 0;
      this._clearAttributes = this._namespacePrefixesFeature;
      if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
         this._prefixTable.clearDeclarationIds();
      }

      String prefix = "";
      String namespaceName = "";
      int start = this._namespacePrefixesIndex;

      int b;
      for(b = this.read(); (b & 252) == 204; b = this.read()) {
         if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
            int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
            System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
            this._namespacePrefixes = namespaceAIIs;
         }

         switch(b & 3) {
         case 0:
            namespaceName = "";
            prefix = "";
            this._namespaceNameIndex = this._prefixIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
            break;
         case 1:
            prefix = "";
            namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
            this._prefixIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
            break;
         case 2:
            prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
            namespaceName = "";
            this._namespaceNameIndex = -1;
            this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
            break;
         case 3:
            prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
            namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
            this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
         }

         this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
         if (this._namespacePrefixesFeature) {
            if (prefix != "") {
               this._attributes.addAttribute(new QualifiedName("xmlns", "http://www.w3.org/2000/xmlns/", prefix), namespaceName);
            } else {
               this._attributes.addAttribute(EncodingConstants.DEFAULT_NAMESPACE_DECLARATION, namespaceName);
            }
         }

         try {
            this._contentHandler.startPrefixMapping(prefix, namespaceName);
         } catch (SAXException var10) {
            throw new IOException("processStartNamespaceAII");
         }
      }

      if (b != 240) {
         throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
      } else {
         int end = this._namespacePrefixesIndex;
         this._b = this.read();
         switch(DecoderStateTables.EII(this._b)) {
         case 0:
            this.processEII(this._elementNameTable._array[this._b], hasAttributes);
            break;
         case 1:
         case 4:
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
         case 2:
            this.processEII(this.decodeEIIIndexMedium(), hasAttributes);
            break;
         case 3:
            this.processEII(this.decodeEIIIndexLarge(), hasAttributes);
            break;
         case 5:
            QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
            this._elementNameTable.add(qn);
            this.processEII(qn, hasAttributes);
         }

         try {
            for(int i = end - 1; i >= start; --i) {
               int prefixIndex = this._namespacePrefixes[i];
               this._prefixTable.popScope(prefixIndex);
               prefix = prefixIndex > 0 ? this._prefixTable.get(prefixIndex - 1) : (prefixIndex == -1 ? "" : "xml");
               this._contentHandler.endPrefixMapping(prefix);
            }

            this._namespacePrefixesIndex = start;
         } catch (SAXException var9) {
            throw new IOException("processStartNamespaceAII");
         }
      }
   }

   protected final void processAIIs() throws FastInfosetException, IOException {
      this._clearAttributes = true;
      if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
         this._duplicateAttributeVerifier.clear();
      }

      do {
         int b = this.read();
         QualifiedName name;
         int index;
         switch(DecoderStateTables.AII(b)) {
         case 0:
            name = this._attributeNameTable._array[b];
            break;
         case 1:
            index = ((b & 31) << 8 | this.read()) + 64;
            name = this._attributeNameTable._array[index];
            break;
         case 2:
            index = ((b & 15) << 16 | this.read() << 8 | this.read()) + 8256;
            name = this._attributeNameTable._array[index];
            break;
         case 3:
            name = this.decodeLiteralQualifiedName(b & 3, this._attributeNameTable.getNext());
            DuplicateAttributeVerifier var10001 = this._duplicateAttributeVerifier;
            name.createAttributeValues(256);
            this._attributeNameTable.add(name);
            break;
         case 5:
            this._doubleTerminate = true;
         case 4:
            this._terminate = true;
            continue;
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
         }

         if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
         }

         this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
         b = this.read();
         String value;
         boolean addToTable;
         switch(DecoderStateTables.NISTRING(b)) {
         case 0:
            this._octetBufferLength = (b & 7) + 1;
            value = this.decodeUtf8StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 1:
            this._octetBufferLength = this.read() + 9;
            value = this.decodeUtf8StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 2:
            this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
            value = this.decodeUtf8StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 3:
            this._octetBufferLength = (b & 7) + 1;
            value = this.decodeUtf16StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 4:
            this._octetBufferLength = this.read() + 9;
            value = this.decodeUtf16StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 5:
            this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
            value = this.decodeUtf16StringAsString();
            if ((b & 64) > 0) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 6:
            addToTable = (b & 64) > 0;
            this._identifier = (b & 15) << 4;
            b = this.read();
            this._identifier |= (b & 240) >> 4;
            this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
            value = this.decodeRestrictedAlphabetAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            this._attributes.addAttribute(name, value);
            break;
         case 7:
            addToTable = (b & 64) > 0;
            this._identifier = (b & 15) << 4;
            b = this.read();
            this._identifier |= (b & 240) >> 4;
            this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
            this.processAIIEncodingAlgorithm(name, addToTable);
            break;
         case 8:
            this._attributes.addAttribute(name, this._attributeValueTable._array[b & 63]);
            break;
         case 9:
            index = ((b & 31) << 8 | this.read()) + 64;
            this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
            break;
         case 10:
            index = ((b & 15) << 16 | this.read() << 8 | this.read()) + 8256;
            this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
            break;
         case 11:
            this._attributes.addAttribute(name, "");
            break;
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
         }
      } while(!this._terminate);

      this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
      this._terminate = this._doubleTerminate;
      this._doubleTerminate = false;
   }

   protected final void processCommentII() throws FastInfosetException, IOException {
      switch(this.decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
         if (this._addToTable) {
            this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
         }

         try {
            this._lexicalHandler.comment(this._charBuffer, 0, this._charBufferLength);
            break;
         } catch (SAXException var5) {
            throw new FastInfosetException("processCommentII", var5);
         }
      case 1:
         CharArray ca = this._v.otherString.get(this._integer);

         try {
            this._lexicalHandler.comment(ca.ch, ca.start, ca.length);
            break;
         } catch (SAXException var4) {
            throw new FastInfosetException("processCommentII", var4);
         }
      case 2:
         throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
      case 3:
         try {
            this._lexicalHandler.comment(this._charBuffer, 0, 0);
         } catch (SAXException var3) {
            throw new FastInfosetException("processCommentII", var3);
         }
      }

   }

   protected final void processProcessingII() throws FastInfosetException, IOException {
      String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
      switch(this.decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
         String data = new String(this._charBuffer, 0, this._charBufferLength);
         if (this._addToTable) {
            this._v.otherString.add(new CharArrayString(data));
         }

         try {
            this._contentHandler.processingInstruction(target, data);
            break;
         } catch (SAXException var6) {
            throw new FastInfosetException("processProcessingII", var6);
         }
      case 1:
         try {
            this._contentHandler.processingInstruction(target, this._v.otherString.get(this._integer).toString());
            break;
         } catch (SAXException var5) {
            throw new FastInfosetException("processProcessingII", var5);
         }
      case 2:
         throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
      case 3:
         try {
            this._contentHandler.processingInstruction(target, "");
         } catch (SAXException var4) {
            throw new FastInfosetException("processProcessingII", var4);
         }
      }

   }

   protected final void processCIIEncodingAlgorithm(boolean addToTable) throws FastInfosetException, IOException {
      if (this._identifier < 9) {
         StringBuffer buffer;
         if (this._primitiveHandler != null) {
            this.processCIIBuiltInEncodingAlgorithmAsPrimitive();
         } else if (this._algorithmHandler != null) {
            Object array = this.processBuiltInEncodingAlgorithmAsObject();

            try {
               this._algorithmHandler.object((String)null, this._identifier, array);
            } catch (SAXException var10) {
               throw new FastInfosetException(var10);
            }
         } else {
            buffer = new StringBuffer();
            this.processBuiltInEncodingAlgorithmAsCharacters(buffer);

            try {
               this._contentHandler.characters(buffer.toString().toCharArray(), 0, buffer.length());
            } catch (SAXException var9) {
               throw new FastInfosetException(var9);
            }
         }

         if (addToTable) {
            buffer = new StringBuffer();
            this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
            this._characterContentChunkTable.add(buffer.toString().toCharArray(), buffer.length());
         }
      } else if (this._identifier == 9) {
         this._octetBufferOffset -= this._octetBufferLength;
         this.decodeUtf8StringIntoCharBuffer();

         try {
            this._lexicalHandler.startCDATA();
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            this._lexicalHandler.endCDATA();
         } catch (SAXException var8) {
            throw new FastInfosetException(var8);
         }

         if (addToTable) {
            this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
         }
      } else {
         if (this._identifier < 32 || this._algorithmHandler == null) {
            if (this._identifier >= 32) {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            } else {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
         }

         String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
         if (URI == null) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
         }

         EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI);
         if (ea != null) {
            Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);

            try {
               this._algorithmHandler.object(URI, this._identifier, data);
            } catch (SAXException var7) {
               throw new FastInfosetException(var7);
            }
         } else {
            try {
               this._algorithmHandler.octets(URI, this._identifier, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            } catch (SAXException var6) {
               throw new FastInfosetException(var6);
            }
         }

         if (addToTable) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.addToTableNotSupported"));
         }
      }

   }

   protected final void processCIIBuiltInEncodingAlgorithmAsPrimitive() throws FastInfosetException, IOException {
      try {
         int length;
         long[] array;
         switch(this._identifier) {
         case 0:
         case 1:
            this._primitiveHandler.bytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            break;
         case 2:
            length = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.shortArray.length) {
               short[] array = new short[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.shortArray, 0, array, 0, this.builtInAlgorithmState.shortArray.length);
               this.builtInAlgorithmState.shortArray = array;
            }

            BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.decodeFromBytesToShortArray(this.builtInAlgorithmState.shortArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.shorts(this.builtInAlgorithmState.shortArray, 0, length);
            break;
         case 3:
            length = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.intArray.length) {
               int[] array = new int[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.intArray, 0, array, 0, this.builtInAlgorithmState.intArray.length);
               this.builtInAlgorithmState.intArray = array;
            }

            BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.decodeFromBytesToIntArray(this.builtInAlgorithmState.intArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.ints(this.builtInAlgorithmState.intArray, 0, length);
            break;
         case 4:
            length = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.longArray.length) {
               array = new long[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.longArray, 0, array, 0, this.builtInAlgorithmState.longArray.length);
               this.builtInAlgorithmState.longArray = array;
            }

            BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.longs(this.builtInAlgorithmState.longArray, 0, length);
            break;
         case 5:
            length = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength, this._octetBuffer[this._octetBufferStart] & 255);
            if (length > this.builtInAlgorithmState.booleanArray.length) {
               boolean[] array = new boolean[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.booleanArray, 0, array, 0, this.builtInAlgorithmState.booleanArray.length);
               this.builtInAlgorithmState.booleanArray = array;
            }

            BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.decodeFromBytesToBooleanArray(this.builtInAlgorithmState.booleanArray, 0, length, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.booleans(this.builtInAlgorithmState.booleanArray, 0, length);
            break;
         case 6:
            length = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.floatArray.length) {
               float[] array = new float[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.floatArray, 0, array, 0, this.builtInAlgorithmState.floatArray.length);
               this.builtInAlgorithmState.floatArray = array;
            }

            BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.decodeFromBytesToFloatArray(this.builtInAlgorithmState.floatArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.floats(this.builtInAlgorithmState.floatArray, 0, length);
            break;
         case 7:
            length = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.doubleArray.length) {
               double[] array = new double[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.doubleArray, 0, array, 0, this.builtInAlgorithmState.doubleArray.length);
               this.builtInAlgorithmState.doubleArray = array;
            }

            BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.decodeFromBytesToDoubleArray(this.builtInAlgorithmState.doubleArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.doubles(this.builtInAlgorithmState.doubleArray, 0, length);
            break;
         case 8:
            length = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
            if (length > this.builtInAlgorithmState.longArray.length) {
               array = new long[length * 3 / 2 + 1];
               System.arraycopy(this.builtInAlgorithmState.longArray, 0, array, 0, this.builtInAlgorithmState.longArray.length);
               this.builtInAlgorithmState.longArray = array;
            }

            BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._primitiveHandler.uuids(this.builtInAlgorithmState.longArray, 0, length);
            break;
         case 9:
            throw new UnsupportedOperationException("CDATA");
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unsupportedAlgorithm", new Object[]{this._identifier}));
         }

      } catch (SAXException var3) {
         throw new FastInfosetException(var3);
      }
   }

   protected final void processAIIEncodingAlgorithm(QualifiedName name, boolean addToTable) throws FastInfosetException, IOException {
      if (this._identifier < 9) {
         if (this._primitiveHandler == null && this._algorithmHandler == null) {
            StringBuffer buffer = new StringBuffer();
            this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
            this._attributes.addAttribute(name, buffer.toString());
         } else {
            Object data = this.processBuiltInEncodingAlgorithmAsObject();
            this._attributes.addAttributeWithAlgorithmData(name, (String)null, this._identifier, data);
         }
      } else {
         if (this._identifier < 32 || this._algorithmHandler == null) {
            if (this._identifier >= 32) {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            } else if (this._identifier == 9) {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            } else {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
         }

         String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
         if (URI == null) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
         }

         EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI);
         if (ea != null) {
            Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            this._attributes.addAttributeWithAlgorithmData(name, URI, this._identifier, data);
         } else {
            byte[] data = new byte[this._octetBufferLength];
            System.arraycopy(this._octetBuffer, this._octetBufferStart, data, 0, this._octetBufferLength);
            this._attributes.addAttributeWithAlgorithmData(name, URI, this._identifier, data);
         }
      }

      if (addToTable) {
         this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(name.qName)));
      }

   }

   protected final void processBuiltInEncodingAlgorithmAsCharacters(StringBuffer buffer) throws FastInfosetException, IOException {
      Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
      BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
   }

   protected final Object processBuiltInEncodingAlgorithmAsObject() throws FastInfosetException, IOException {
      return BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
   }

   private static final class DeclHandlerImpl implements DeclHandler {
      private DeclHandlerImpl() {
      }

      public void elementDecl(String name, String model) throws SAXException {
      }

      public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
      }

      public void internalEntityDecl(String name, String value) throws SAXException {
      }

      public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
      }

      // $FF: synthetic method
      DeclHandlerImpl(Object x0) {
         this();
      }
   }

   private static final class LexicalHandlerImpl implements LexicalHandler {
      private LexicalHandlerImpl() {
      }

      public void comment(char[] ch, int start, int end) {
      }

      public void startDTD(String name, String publicId, String systemId) {
      }

      public void endDTD() {
      }

      public void startEntity(String name) {
      }

      public void endEntity(String name) {
      }

      public void startCDATA() {
      }

      public void endCDATA() {
      }

      // $FF: synthetic method
      LexicalHandlerImpl(Object x0) {
         this();
      }
   }
}
