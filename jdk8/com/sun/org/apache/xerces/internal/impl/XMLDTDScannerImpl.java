package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import java.io.EOFException;
import java.io.IOException;

public class XMLDTDScannerImpl extends XMLScanner implements XMLDTDScanner, XMLComponent, XMLEntityHandler {
   protected static final int SCANNER_STATE_END_OF_INPUT = 0;
   protected static final int SCANNER_STATE_TEXT_DECL = 1;
   protected static final int SCANNER_STATE_MARKUP_DECL = 2;
   private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-char-refs"};
   private static final Boolean[] FEATURE_DEFAULTS;
   private static final String[] RECOGNIZED_PROPERTIES;
   private static final Object[] PROPERTY_DEFAULTS;
   private static final boolean DEBUG_SCANNER_STATE = false;
   public XMLDTDHandler fDTDHandler = null;
   protected XMLDTDContentModelHandler fDTDContentModelHandler;
   protected int fScannerState;
   protected boolean fStandalone;
   protected boolean fSeenExternalDTD;
   protected boolean fSeenExternalPE;
   private boolean fStartDTDCalled;
   private XMLAttributesImpl fAttributes = new XMLAttributesImpl();
   private int[] fContentStack = new int[5];
   private int fContentDepth;
   private int[] fPEStack = new int[5];
   private boolean[] fPEReport = new boolean[5];
   private int fPEDepth;
   private int fMarkUpDepth;
   private int fExtEntityDepth;
   private int fIncludeSectDepth;
   private String[] fStrings = new String[3];
   private XMLString fString = new XMLString();
   private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
   private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
   private XMLString fLiteral = new XMLString();
   private XMLString fLiteral2 = new XMLString();
   private String[] fEnumeration = new String[5];
   private int fEnumerationCount;
   private XMLStringBuffer fIgnoreConditionalBuffer = new XMLStringBuffer(128);
   DTDGrammar nvGrammarInfo = null;
   boolean nonValidatingMode = false;

   public XMLDTDScannerImpl() {
   }

   public XMLDTDScannerImpl(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
      this.fSymbolTable = symbolTable;
      this.fErrorReporter = errorReporter;
      this.fEntityManager = entityManager;
      entityManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
   }

   public void setInputSource(XMLInputSource inputSource) throws IOException {
      if (inputSource == null) {
         if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD((XMLLocator)null, (Augmentations)null);
            this.fDTDHandler.endDTD((Augmentations)null);
         }

         if (this.nonValidatingMode) {
            this.nvGrammarInfo.startDTD((XMLLocator)null, (Augmentations)null);
            this.nvGrammarInfo.endDTD((Augmentations)null);
         }

      } else {
         this.fEntityManager.setEntityHandler(this);
         this.fEntityManager.startDTDEntity(inputSource);
      }
   }

   public void setLimitAnalyzer(XMLLimitAnalyzer limitAnalyzer) {
      this.fLimitAnalyzer = limitAnalyzer;
   }

   public boolean scanDTDExternalSubset(boolean complete) throws IOException, XNIException {
      this.fEntityManager.setEntityHandler(this);
      if (this.fScannerState == 1) {
         this.fSeenExternalDTD = true;
         boolean textDecl = this.scanTextDecl();
         if (this.fScannerState == 0) {
            return false;
         }

         this.setScannerState(2);
         if (textDecl && !complete) {
            return true;
         }
      }

      while(this.scanDecls(complete)) {
         if (!complete) {
            return true;
         }
      }

      return false;
   }

   public boolean scanDTDInternalSubset(boolean complete, boolean standalone, boolean hasExternalSubset) throws IOException, XNIException {
      this.fEntityScanner = this.fEntityManager.getEntityScanner();
      this.fEntityManager.setEntityHandler(this);
      this.fStandalone = standalone;
      if (this.fScannerState == 1) {
         if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(this.fEntityScanner, (Augmentations)null);
            this.fStartDTDCalled = true;
         }

         if (this.nonValidatingMode) {
            this.fStartDTDCalled = true;
            this.nvGrammarInfo.startDTD(this.fEntityScanner, (Augmentations)null);
         }

         this.setScannerState(2);
      }

      while(this.scanDecls(complete)) {
         if (!complete) {
            return true;
         }
      }

      if (this.fDTDHandler != null && !hasExternalSubset) {
         this.fDTDHandler.endDTD((Augmentations)null);
      }

      if (this.nonValidatingMode && !hasExternalSubset) {
         this.nvGrammarInfo.endDTD((Augmentations)null);
      }

      this.setScannerState(1);
      this.fLimitAnalyzer.reset(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT);
      this.fLimitAnalyzer.reset(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT);
      return false;
   }

   public boolean skipDTD(boolean supportDTD) throws IOException {
      if (supportDTD) {
         return false;
      } else {
         this.fStringBuffer.clear();

         while(this.fEntityScanner.scanData("]", this.fStringBuffer)) {
            int c = this.fEntityScanner.peekChar();
            if (c != -1) {
               if (XMLChar.isHighSurrogate(c)) {
                  this.scanSurrogates(this.fStringBuffer);
               }

               if (this.isInvalidLiteral(c)) {
                  this.reportFatalError("InvalidCharInDTD", new Object[]{Integer.toHexString(c)});
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               }
            }
         }

         --this.fEntityScanner.fCurrentEntity.position;
         return true;
      }
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      super.reset(componentManager);
      this.init();
   }

   public void reset() {
      super.reset();
      this.init();
   }

   public void reset(PropertyManager props) {
      this.setPropertyManager(props);
      super.reset(props);
      this.init();
      this.nonValidatingMode = true;
      this.nvGrammarInfo = new DTDGrammar(this.fSymbolTable);
   }

   public String[] getRecognizedFeatures() {
      return (String[])((String[])RECOGNIZED_FEATURES.clone());
   }

   public String[] getRecognizedProperties() {
      return (String[])((String[])RECOGNIZED_PROPERTIES.clone());
   }

   public Boolean getFeatureDefault(String featureId) {
      for(int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
         if (RECOGNIZED_FEATURES[i].equals(featureId)) {
            return FEATURE_DEFAULTS[i];
         }
      }

      return null;
   }

   public Object getPropertyDefault(String propertyId) {
      for(int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
         if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
            return PROPERTY_DEFAULTS[i];
         }
      }

      return null;
   }

   public void setDTDHandler(XMLDTDHandler dtdHandler) {
      this.fDTDHandler = dtdHandler;
   }

   public XMLDTDHandler getDTDHandler() {
      return this.fDTDHandler;
   }

   public void setDTDContentModelHandler(XMLDTDContentModelHandler dtdContentModelHandler) {
      this.fDTDContentModelHandler = dtdContentModelHandler;
   }

   public XMLDTDContentModelHandler getDTDContentModelHandler() {
      return this.fDTDContentModelHandler;
   }

   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      super.startEntity(name, identifier, encoding, augs);
      boolean dtdEntity = name.equals("[dtd]");
      if (dtdEntity) {
         if (this.fDTDHandler != null && !this.fStartDTDCalled) {
            this.fDTDHandler.startDTD(this.fEntityScanner, (Augmentations)null);
         }

         if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(identifier, (Augmentations)null);
         }

         this.fEntityManager.startExternalSubset();
         this.fEntityStore.startExternalSubset();
         ++this.fExtEntityDepth;
      } else if (name.charAt(0) == '%') {
         this.pushPEStack(this.fMarkUpDepth, this.fReportEntity);
         if (this.fEntityScanner.isExternal()) {
            ++this.fExtEntityDepth;
         }
      }

      if (this.fDTDHandler != null && !dtdEntity && this.fReportEntity) {
         this.fDTDHandler.startParameterEntity(name, identifier, encoding, (Augmentations)null);
      }

   }

   public void endEntity(String name, Augmentations augs) throws XNIException, IOException {
      super.endEntity(name, augs);
      if (this.fScannerState != 0) {
         boolean reportEntity = this.fReportEntity;
         if (name.startsWith("%")) {
            reportEntity = this.peekReportEntity();
            int startMarkUpDepth = this.popPEStack();
            if (startMarkUpDepth == 0 && startMarkUpDepth < this.fMarkUpDepth) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ILL_FORMED_PARAMETER_ENTITY_WHEN_USED_IN_DECL", new Object[]{this.fEntityManager.fCurrentEntity.name}, (short)2);
            }

            if (startMarkUpDepth != this.fMarkUpDepth) {
               reportEntity = false;
               if (this.fValidation) {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ImproperDeclarationNesting", new Object[]{name}, (short)1);
               }
            }

            if (this.fEntityScanner.isExternal()) {
               --this.fExtEntityDepth;
            }
         }

         boolean dtdEntity = name.equals("[dtd]");
         if (this.fDTDHandler != null && !dtdEntity && reportEntity) {
            this.fDTDHandler.endParameterEntity(name, (Augmentations)null);
         }

         if (dtdEntity) {
            if (this.fIncludeSectDepth != 0) {
               this.reportFatalError("IncludeSectUnterminated", (Object[])null);
            }

            this.fScannerState = 0;
            this.fEntityManager.endExternalSubset();
            this.fEntityStore.endExternalSubset();
            if (this.fDTDHandler != null) {
               this.fDTDHandler.endExternalSubset((Augmentations)null);
               this.fDTDHandler.endDTD((Augmentations)null);
            }

            --this.fExtEntityDepth;
         }

         if (augs != null && Boolean.TRUE.equals(augs.getItem("LAST_ENTITY")) && (this.fMarkUpDepth != 0 || this.fExtEntityDepth != 0 || this.fIncludeSectDepth != 0)) {
            throw new EOFException();
         }
      }
   }

   protected final void setScannerState(int state) {
      this.fScannerState = state;
   }

   private static String getScannerStateName(int state) {
      return "??? (" + state + ')';
   }

   protected final boolean scanningInternalSubset() {
      return this.fExtEntityDepth == 0;
   }

   protected void startPE(String name, boolean literal) throws IOException, XNIException {
      int depth = this.fPEDepth;
      String pName = "%" + name;
      if (this.fValidation && !this.fEntityStore.isDeclaredEntity(pName)) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{name}, (short)1);
      }

      this.fEntityManager.startEntity(false, this.fSymbolTable.addSymbol(pName), literal);
      if (depth != this.fPEDepth && this.fEntityScanner.isExternal()) {
         this.scanTextDecl();
      }

   }

   protected final boolean scanTextDecl() throws IOException, XNIException {
      boolean textDecl = false;
      if (this.fEntityScanner.skipString("<?xml")) {
         ++this.fMarkUpDepth;
         String target;
         if (this.isValidNameChar(this.fEntityScanner.peekChar())) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append("xml");

            while(this.isValidNameChar(this.fEntityScanner.peekChar())) {
               this.fStringBuffer.append((char)this.fEntityScanner.scanChar((XMLScanner.NameType)null));
            }

            target = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            this.scanPIData(target, this.fString);
         } else {
            target = null;
            String encoding = null;
            this.scanXMLDeclOrTextDecl(true, this.fStrings);
            textDecl = true;
            --this.fMarkUpDepth;
            target = this.fStrings[0];
            encoding = this.fStrings[1];
            this.fEntityScanner.setEncoding(encoding);
            if (this.fDTDHandler != null) {
               this.fDTDHandler.textDecl(target, encoding, (Augmentations)null);
            }
         }
      }

      this.fEntityManager.fCurrentEntity.mayReadChunks = true;
      return textDecl;
   }

   protected final void scanPIData(String target, XMLString data) throws IOException, XNIException {
      --this.fMarkUpDepth;
      if (this.fDTDHandler != null) {
         this.fDTDHandler.processingInstruction(target, data, (Augmentations)null);
      }

   }

   protected final void scanComment() throws IOException, XNIException {
      this.fReportEntity = false;
      this.scanComment(this.fStringBuffer);
      --this.fMarkUpDepth;
      if (this.fDTDHandler != null) {
         this.fDTDHandler.comment(this.fStringBuffer, (Augmentations)null);
      }

      this.fReportEntity = true;
   }

   protected final void scanElementDecl() throws IOException, XNIException {
      this.fReportEntity = false;
      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL", (Object[])null);
      }

      String name = this.fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
      if (name == null) {
         this.reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL", (Object[])null);
      }

      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL", new Object[]{name});
      }

      if (this.fDTDContentModelHandler != null) {
         this.fDTDContentModelHandler.startContentModel(name, (Augmentations)null);
      }

      String contentModel = null;
      this.fReportEntity = true;
      if (this.fEntityScanner.skipString("EMPTY")) {
         contentModel = "EMPTY";
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.empty((Augmentations)null);
         }
      } else if (this.fEntityScanner.skipString("ANY")) {
         contentModel = "ANY";
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.any((Augmentations)null);
         }
      } else {
         if (!this.fEntityScanner.skipChar(40, (XMLScanner.NameType)null)) {
            this.reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[]{name});
         }

         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startGroup((Augmentations)null);
         }

         this.fStringBuffer.clear();
         this.fStringBuffer.append('(');
         ++this.fMarkUpDepth;
         this.skipSeparator(false, !this.scanningInternalSubset());
         if (this.fEntityScanner.skipString("#PCDATA")) {
            this.scanMixed(name);
         } else {
            this.scanChildren(name);
         }

         contentModel = this.fStringBuffer.toString();
      }

      if (this.fDTDContentModelHandler != null) {
         this.fDTDContentModelHandler.endContentModel((Augmentations)null);
      }

      this.fReportEntity = false;
      this.skipSeparator(false, !this.scanningInternalSubset());
      if (!this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
         this.reportFatalError("ElementDeclUnterminated", new Object[]{name});
      }

      this.fReportEntity = true;
      --this.fMarkUpDepth;
      if (this.fDTDHandler != null) {
         this.fDTDHandler.elementDecl(name, contentModel, (Augmentations)null);
      }

      if (this.nonValidatingMode) {
         this.nvGrammarInfo.elementDecl(name, contentModel, (Augmentations)null);
      }

   }

   private final void scanMixed(String elName) throws IOException, XNIException {
      String childName = null;
      this.fStringBuffer.append("#PCDATA");
      if (this.fDTDContentModelHandler != null) {
         this.fDTDContentModelHandler.pcdata((Augmentations)null);
      }

      this.skipSeparator(false, !this.scanningInternalSubset());

      for(; this.fEntityScanner.skipChar(124, (XMLScanner.NameType)null); this.skipSeparator(false, !this.scanningInternalSubset())) {
         this.fStringBuffer.append('|');
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.separator((short)0, (Augmentations)null);
         }

         this.skipSeparator(false, !this.scanningInternalSubset());
         childName = this.fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
         if (childName == null) {
            this.reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT", new Object[]{elName});
         }

         this.fStringBuffer.append(childName);
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.element(childName, (Augmentations)null);
         }
      }

      if (this.fEntityScanner.skipString(")*")) {
         this.fStringBuffer.append(")*");
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endGroup((Augmentations)null);
            this.fDTDContentModelHandler.occurrence((short)3, (Augmentations)null);
         }
      } else if (childName != null) {
         this.reportFatalError("MixedContentUnterminated", new Object[]{elName});
      } else if (this.fEntityScanner.skipChar(41, (XMLScanner.NameType)null)) {
         this.fStringBuffer.append(')');
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endGroup((Augmentations)null);
         }
      } else {
         this.reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[]{elName});
      }

      --this.fMarkUpDepth;
   }

   private final void scanChildren(String elName) throws IOException, XNIException {
      this.fContentDepth = 0;
      this.pushContentStack(0);
      int currentOp = 0;

      while(true) {
         label116:
         for(; !this.fEntityScanner.skipChar(40, (XMLScanner.NameType)null); this.skipSeparator(false, !this.scanningInternalSubset())) {
            this.skipSeparator(false, !this.scanningInternalSubset());
            String childName = this.fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
            if (childName == null) {
               this.reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[]{elName});
               return;
            }

            if (this.fDTDContentModelHandler != null) {
               this.fDTDContentModelHandler.element(childName, (Augmentations)null);
            }

            this.fStringBuffer.append(childName);
            int c = this.fEntityScanner.peekChar();
            byte oc;
            if (c == 63 || c == 42 || c == 43) {
               if (this.fDTDContentModelHandler != null) {
                  if (c == 63) {
                     oc = 2;
                  } else if (c == 42) {
                     oc = 3;
                  } else {
                     oc = 4;
                  }

                  this.fDTDContentModelHandler.occurrence(oc, (Augmentations)null);
               }

               this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               this.fStringBuffer.append((char)c);
            }

            do {
               this.skipSeparator(false, !this.scanningInternalSubset());
               c = this.fEntityScanner.peekChar();
               if (c == 44 && currentOp != 124) {
                  currentOp = c;
                  if (this.fDTDContentModelHandler != null) {
                     this.fDTDContentModelHandler.separator((short)1, (Augmentations)null);
                  }

                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  this.fStringBuffer.append(',');
                  continue label116;
               }

               if (c == 124 && currentOp != 44) {
                  currentOp = c;
                  if (this.fDTDContentModelHandler != null) {
                     this.fDTDContentModelHandler.separator((short)0, (Augmentations)null);
                  }

                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  this.fStringBuffer.append('|');
                  continue label116;
               }

               if (c != 41) {
                  this.reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[]{elName});
               }

               if (this.fDTDContentModelHandler != null) {
                  this.fDTDContentModelHandler.endGroup((Augmentations)null);
               }

               currentOp = this.popContentStack();
               if (this.fEntityScanner.skipString(")?")) {
                  this.fStringBuffer.append(")?");
                  if (this.fDTDContentModelHandler != null) {
                     oc = 2;
                     this.fDTDContentModelHandler.occurrence(oc, (Augmentations)null);
                  }
               } else if (this.fEntityScanner.skipString(")+")) {
                  this.fStringBuffer.append(")+");
                  if (this.fDTDContentModelHandler != null) {
                     oc = 4;
                     this.fDTDContentModelHandler.occurrence(oc, (Augmentations)null);
                  }
               } else if (this.fEntityScanner.skipString(")*")) {
                  this.fStringBuffer.append(")*");
                  if (this.fDTDContentModelHandler != null) {
                     oc = 3;
                     this.fDTDContentModelHandler.occurrence(oc, (Augmentations)null);
                  }
               } else {
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  this.fStringBuffer.append(')');
               }

               --this.fMarkUpDepth;
            } while(this.fContentDepth != 0);

            return;
         }

         ++this.fMarkUpDepth;
         this.fStringBuffer.append('(');
         if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startGroup((Augmentations)null);
         }

         this.pushContentStack(currentOp);
         currentOp = 0;
         this.skipSeparator(false, !this.scanningInternalSubset());
      }
   }

   protected final void scanAttlistDecl() throws IOException, XNIException {
      this.fReportEntity = false;
      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL", (Object[])null);
      }

      String elName = this.fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
      if (elName == null) {
         this.reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL", (Object[])null);
      }

      if (this.fDTDHandler != null) {
         this.fDTDHandler.startAttlist(elName, (Augmentations)null);
      }

      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         if (this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
            if (this.fDTDHandler != null) {
               this.fDTDHandler.endAttlist((Augmentations)null);
            }

            --this.fMarkUpDepth;
            return;
         }

         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF", new Object[]{elName});
      }

      for(; !this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null); this.skipSeparator(false, !this.scanningInternalSubset())) {
         String name = this.fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
         if (name == null) {
            this.reportFatalError("AttNameRequiredInAttDef", new Object[]{elName});
         }

         if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF", new Object[]{elName, name});
         }

         String type = this.scanAttType(elName, name);
         if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF", new Object[]{elName, name});
         }

         String defaultType = this.scanAttDefaultDecl(elName, name, type, this.fLiteral, this.fLiteral2);
         String[] enumr = null;
         if ((this.fDTDHandler != null || this.nonValidatingMode) && this.fEnumerationCount != 0) {
            enumr = new String[this.fEnumerationCount];
            System.arraycopy(this.fEnumeration, 0, enumr, 0, this.fEnumerationCount);
         }

         if (defaultType != null && (defaultType.equals("#REQUIRED") || defaultType.equals("#IMPLIED"))) {
            if (this.fDTDHandler != null) {
               this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, (XMLString)null, (XMLString)null, (Augmentations)null);
            }

            if (this.nonValidatingMode) {
               this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, (XMLString)null, (XMLString)null, (Augmentations)null);
            }
         } else {
            if (this.fDTDHandler != null) {
               this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, (Augmentations)null);
            }

            if (this.nonValidatingMode) {
               this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, (Augmentations)null);
            }
         }
      }

      if (this.fDTDHandler != null) {
         this.fDTDHandler.endAttlist((Augmentations)null);
      }

      --this.fMarkUpDepth;
      this.fReportEntity = true;
   }

   private final String scanAttType(String elName, String atName) throws IOException, XNIException {
      String type = null;
      this.fEnumerationCount = 0;
      if (this.fEntityScanner.skipString("CDATA")) {
         type = "CDATA";
      } else if (this.fEntityScanner.skipString("IDREFS")) {
         type = "IDREFS";
      } else if (this.fEntityScanner.skipString("IDREF")) {
         type = "IDREF";
      } else if (this.fEntityScanner.skipString("ID")) {
         type = "ID";
      } else if (this.fEntityScanner.skipString("ENTITY")) {
         type = "ENTITY";
      } else if (this.fEntityScanner.skipString("ENTITIES")) {
         type = "ENTITIES";
      } else if (this.fEntityScanner.skipString("NMTOKENS")) {
         type = "NMTOKENS";
      } else if (this.fEntityScanner.skipString("NMTOKEN")) {
         type = "NMTOKEN";
      } else {
         int c;
         String aName;
         if (this.fEntityScanner.skipString("NOTATION")) {
            type = "NOTATION";
            if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
               this.reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE", new Object[]{elName, atName});
            }

            c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
            if (c != 40) {
               this.reportFatalError("MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE", new Object[]{elName, atName});
            }

            ++this.fMarkUpDepth;

            do {
               this.skipSeparator(false, !this.scanningInternalSubset());
               aName = this.fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
               if (aName == null) {
                  this.reportFatalError("MSG_NAME_REQUIRED_IN_NOTATIONTYPE", new Object[]{elName, atName});
               }

               this.ensureEnumerationSize(this.fEnumerationCount + 1);
               this.fEnumeration[this.fEnumerationCount++] = aName;
               this.skipSeparator(false, !this.scanningInternalSubset());
               c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
            } while(c == 124);

            if (c != 41) {
               this.reportFatalError("NotationTypeUnterminated", new Object[]{elName, atName});
            }

            --this.fMarkUpDepth;
         } else {
            type = "ENUMERATION";
            c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
            if (c != 40) {
               this.reportFatalError("AttTypeRequiredInAttDef", new Object[]{elName, atName});
            }

            ++this.fMarkUpDepth;

            do {
               this.skipSeparator(false, !this.scanningInternalSubset());
               aName = this.fEntityScanner.scanNmtoken();
               if (aName == null) {
                  this.reportFatalError("MSG_NMTOKEN_REQUIRED_IN_ENUMERATION", new Object[]{elName, atName});
               }

               this.ensureEnumerationSize(this.fEnumerationCount + 1);
               this.fEnumeration[this.fEnumerationCount++] = aName;
               this.skipSeparator(false, !this.scanningInternalSubset());
               c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
            } while(c == 124);

            if (c != 41) {
               this.reportFatalError("EnumerationUnterminated", new Object[]{elName, atName});
            }

            --this.fMarkUpDepth;
         }
      }

      return type;
   }

   protected final String scanAttDefaultDecl(String elName, String atName, String type, XMLString defaultVal, XMLString nonNormalizedDefaultVal) throws IOException, XNIException {
      String defaultType = null;
      this.fString.clear();
      defaultVal.clear();
      if (this.fEntityScanner.skipString("#REQUIRED")) {
         defaultType = "#REQUIRED";
      } else if (this.fEntityScanner.skipString("#IMPLIED")) {
         defaultType = "#IMPLIED";
      } else {
         if (this.fEntityScanner.skipString("#FIXED")) {
            defaultType = "#FIXED";
            if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
               this.reportFatalError("MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL", new Object[]{elName, atName});
            }
         }

         boolean isVC = !this.fStandalone && (this.fSeenExternalDTD || this.fSeenExternalPE);
         this.scanAttributeValue(defaultVal, nonNormalizedDefaultVal, atName, this.fAttributes, 0, isVC, elName, false);
      }

      return defaultType;
   }

   private final void scanEntityDecl() throws IOException, XNIException {
      boolean isPEDecl = false;
      boolean sawPERef = false;
      this.fReportEntity = false;
      if (this.fEntityScanner.skipSpaces()) {
         if (!this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
            isPEDecl = false;
         } else if (this.skipSeparator(true, !this.scanningInternalSubset())) {
            isPEDecl = true;
         } else if (this.scanningInternalSubset()) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", (Object[])null);
            isPEDecl = true;
         } else if (this.fEntityScanner.peekChar() == 37) {
            this.skipSeparator(false, !this.scanningInternalSubset());
            isPEDecl = true;
         } else {
            sawPERef = true;
         }
      } else if (!this.scanningInternalSubset() && this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
         if (this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL", (Object[])null);
            isPEDecl = false;
         } else {
            sawPERef = true;
         }
      } else {
         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", (Object[])null);
         isPEDecl = false;
      }

      String name;
      if (sawPERef) {
         while(true) {
            name = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
            if (name == null) {
               this.reportFatalError("NameRequiredInPEReference", (Object[])null);
            } else if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
               this.reportFatalError("SemicolonRequiredInPEReference", new Object[]{name});
            } else {
               this.startPE(name, false);
            }

            this.fEntityScanner.skipSpaces();
            if (!this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
               break;
            }

            if (!isPEDecl) {
               if (this.skipSeparator(true, !this.scanningInternalSubset())) {
                  isPEDecl = true;
                  break;
               }

               isPEDecl = this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE);
            }
         }
      }

      name = this.fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
      if (name == null) {
         this.reportFatalError("MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL", (Object[])null);
      }

      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL", new Object[]{name});
      }

      this.scanExternalID(this.fStrings, false);
      String systemId = this.fStrings[0];
      String publicId = this.fStrings[1];
      if (isPEDecl && systemId != null) {
         this.fSeenExternalPE = true;
      }

      String notation = null;
      boolean sawSpace = this.skipSeparator(true, !this.scanningInternalSubset());
      if (!isPEDecl && this.fEntityScanner.skipString("NDATA")) {
         if (!sawSpace) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL", new Object[]{name});
         }

         if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL", new Object[]{name});
         }

         notation = this.fEntityScanner.scanName(XMLScanner.NameType.NOTATION);
         if (notation == null) {
            this.reportFatalError("MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL", new Object[]{name});
         }
      }

      if (systemId == null) {
         this.scanEntityValue(name, isPEDecl, this.fLiteral, this.fLiteral2);
         this.fStringBuffer.clear();
         this.fStringBuffer2.clear();
         this.fStringBuffer.append(this.fLiteral.ch, this.fLiteral.offset, this.fLiteral.length);
         this.fStringBuffer2.append(this.fLiteral2.ch, this.fLiteral2.offset, this.fLiteral2.length);
      }

      this.skipSeparator(false, !this.scanningInternalSubset());
      if (!this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
         this.reportFatalError("EntityDeclUnterminated", new Object[]{name});
      }

      --this.fMarkUpDepth;
      if (isPEDecl) {
         name = "%" + name;
      }

      if (systemId != null) {
         String baseSystemId = this.fEntityScanner.getBaseSystemId();
         if (notation != null) {
            this.fEntityStore.addUnparsedEntity(name, publicId, systemId, baseSystemId, notation);
         } else {
            this.fEntityStore.addExternalEntity(name, publicId, systemId, baseSystemId);
         }

         if (this.fDTDHandler != null) {
            this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
            if (notation != null) {
               this.fDTDHandler.unparsedEntityDecl(name, this.fResourceIdentifier, notation, (Augmentations)null);
            } else {
               this.fDTDHandler.externalEntityDecl(name, this.fResourceIdentifier, (Augmentations)null);
            }
         }
      } else {
         this.fEntityStore.addInternalEntity(name, this.fStringBuffer.toString());
         if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(name, this.fStringBuffer, this.fStringBuffer2, (Augmentations)null);
         }
      }

      this.fReportEntity = true;
   }

   protected final void scanEntityValue(String entityName, boolean isPEDecl, XMLString value, XMLString nonNormalizedValue) throws IOException, XNIException {
      int quote = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
      if (quote != 39 && quote != 34) {
         this.reportFatalError("OpenQuoteMissingInDecl", (Object[])null);
      }

      int entityDepth = this.fEntityDepth;
      XMLString literal = this.fString;
      XMLString literal2 = this.fString;
      int countChar = false;
      if (this.fLimitAnalyzer == null) {
         this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
      }

      this.fLimitAnalyzer.startEntity(entityName);
      if (this.fEntityScanner.scanLiteral(quote, this.fString, false) != quote) {
         this.fStringBuffer.clear();
         this.fStringBuffer2.clear();

         do {
            int countChar = 0;
            int offset = this.fStringBuffer.length;
            this.fStringBuffer.append(this.fString);
            this.fStringBuffer2.append(this.fString);
            String peName;
            if (this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE)) {
               if (this.fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE)) {
                  this.fStringBuffer2.append("&#");
                  this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2);
               } else {
                  this.fStringBuffer.append('&');
                  this.fStringBuffer2.append('&');
                  peName = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
                  if (peName == null) {
                     this.reportFatalError("NameRequiredInReference", (Object[])null);
                  } else {
                     this.fStringBuffer.append(peName);
                     this.fStringBuffer2.append(peName);
                  }

                  if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
                     this.reportFatalError("SemicolonRequiredInReference", new Object[]{peName});
                  } else {
                     this.fStringBuffer.append(';');
                     this.fStringBuffer2.append(';');
                  }
               }
            } else if (this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
               do {
                  this.fStringBuffer2.append('%');
                  peName = this.fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
                  if (peName == null) {
                     this.reportFatalError("NameRequiredInPEReference", (Object[])null);
                  } else if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
                     this.reportFatalError("SemicolonRequiredInPEReference", new Object[]{peName});
                  } else {
                     if (this.scanningInternalSubset()) {
                        this.reportFatalError("PEReferenceWithinMarkup", new Object[]{peName});
                     }

                     this.fStringBuffer2.append(peName);
                     this.fStringBuffer2.append(';');
                  }

                  this.startPE(peName, true);
                  this.fEntityScanner.skipSpaces();
               } while(this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE));
            } else {
               int c = this.fEntityScanner.peekChar();
               if (XMLChar.isHighSurrogate(c)) {
                  ++countChar;
                  this.scanSurrogates(this.fStringBuffer2);
               } else if (this.isInvalidLiteral(c)) {
                  this.reportFatalError("InvalidCharInLiteral", new Object[]{Integer.toHexString(c)});
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               } else if (c != quote || entityDepth != this.fEntityDepth) {
                  this.fStringBuffer.append((char)c);
                  this.fStringBuffer2.append((char)c);
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               }
            }

            this.checkEntityLimit(isPEDecl, entityName, this.fStringBuffer.length - offset + countChar);
         } while(this.fEntityScanner.scanLiteral(quote, this.fString, false) != quote);

         this.checkEntityLimit(isPEDecl, entityName, this.fString.length);
         this.fStringBuffer.append(this.fString);
         this.fStringBuffer2.append(this.fString);
         literal = this.fStringBuffer;
         literal2 = this.fStringBuffer2;
      } else {
         this.checkEntityLimit(isPEDecl, entityName, (XMLString)literal);
      }

      value.setValues((XMLString)literal);
      nonNormalizedValue.setValues((XMLString)literal2);
      if (this.fLimitAnalyzer != null) {
         if (isPEDecl) {
            this.fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, entityName);
         } else {
            this.fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entityName);
         }
      }

      if (!this.fEntityScanner.skipChar(quote, (XMLScanner.NameType)null)) {
         this.reportFatalError("CloseQuoteMissingInDecl", (Object[])null);
      }

   }

   private final void scanNotationDecl() throws IOException, XNIException {
      this.fReportEntity = false;
      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL", (Object[])null);
      }

      String name = this.fEntityScanner.scanName(XMLScanner.NameType.NOTATION);
      if (name == null) {
         this.reportFatalError("MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL", (Object[])null);
      }

      if (!this.skipSeparator(true, !this.scanningInternalSubset())) {
         this.reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL", new Object[]{name});
      }

      this.scanExternalID(this.fStrings, true);
      String systemId = this.fStrings[0];
      String publicId = this.fStrings[1];
      String baseSystemId = this.fEntityScanner.getBaseSystemId();
      if (systemId == null && publicId == null) {
         this.reportFatalError("ExternalIDorPublicIDRequired", new Object[]{name});
      }

      this.skipSeparator(false, !this.scanningInternalSubset());
      if (!this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
         this.reportFatalError("NotationDeclUnterminated", new Object[]{name});
      }

      --this.fMarkUpDepth;
      this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
      if (this.nonValidatingMode) {
         this.nvGrammarInfo.notationDecl(name, this.fResourceIdentifier, (Augmentations)null);
      }

      if (this.fDTDHandler != null) {
         this.fDTDHandler.notationDecl(name, this.fResourceIdentifier, (Augmentations)null);
      }

      this.fReportEntity = true;
   }

   private final void scanConditionalSect(int currPEDepth) throws IOException, XNIException {
      this.fReportEntity = false;
      this.skipSeparator(false, !this.scanningInternalSubset());
      if (this.fEntityScanner.skipString("INCLUDE")) {
         this.skipSeparator(false, !this.scanningInternalSubset());
         if (currPEDepth != this.fPEDepth && this.fValidation) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[]{this.fEntityManager.fCurrentEntity.name}, (short)1);
         }

         if (!this.fEntityScanner.skipChar(91, (XMLScanner.NameType)null)) {
            this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
         }

         if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional((short)0, (Augmentations)null);
         }

         ++this.fIncludeSectDepth;
         this.fReportEntity = true;
      } else {
         if (this.fEntityScanner.skipString("IGNORE")) {
            this.skipSeparator(false, !this.scanningInternalSubset());
            if (currPEDepth != this.fPEDepth && this.fValidation) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[]{this.fEntityManager.fCurrentEntity.name}, (short)1);
            }

            if (this.fDTDHandler != null) {
               this.fDTDHandler.startConditional((short)1, (Augmentations)null);
            }

            if (!this.fEntityScanner.skipChar(91, (XMLScanner.NameType)null)) {
               this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
            }

            this.fReportEntity = true;
            int initialDepth = ++this.fIncludeSectDepth;
            if (this.fDTDHandler != null) {
               this.fIgnoreConditionalBuffer.clear();
            }

            while(true) {
               while(!this.fEntityScanner.skipChar(60, (XMLScanner.NameType)null)) {
                  if (!this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
                     int c = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                     if (this.fScannerState == 0) {
                        this.reportFatalError("IgnoreSectUnterminated", (Object[])null);
                        return;
                     }

                     if (this.fDTDHandler != null) {
                        this.fIgnoreConditionalBuffer.append((char)c);
                     }
                  } else {
                     if (this.fDTDHandler != null) {
                        this.fIgnoreConditionalBuffer.append(']');
                     }

                     if (this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
                        if (this.fDTDHandler != null) {
                           this.fIgnoreConditionalBuffer.append(']');
                        }

                        while(this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
                           if (this.fDTDHandler != null) {
                              this.fIgnoreConditionalBuffer.append(']');
                           }
                        }

                        if (this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
                           if (this.fIncludeSectDepth-- == initialDepth) {
                              --this.fMarkUpDepth;
                              if (this.fDTDHandler != null) {
                                 this.fLiteral.setValues(this.fIgnoreConditionalBuffer.ch, 0, this.fIgnoreConditionalBuffer.length - 2);
                                 this.fDTDHandler.ignoredCharacters(this.fLiteral, (Augmentations)null);
                                 this.fDTDHandler.endConditional((Augmentations)null);
                              }

                              return;
                           }

                           if (this.fDTDHandler != null) {
                              this.fIgnoreConditionalBuffer.append('>');
                           }
                        }
                     }
                  }
               }

               if (this.fDTDHandler != null) {
                  this.fIgnoreConditionalBuffer.append('<');
               }

               if (this.fEntityScanner.skipChar(33, (XMLScanner.NameType)null)) {
                  if (this.fEntityScanner.skipChar(91, (XMLScanner.NameType)null)) {
                     if (this.fDTDHandler != null) {
                        this.fIgnoreConditionalBuffer.append("![");
                     }

                     ++this.fIncludeSectDepth;
                  } else if (this.fDTDHandler != null) {
                     this.fIgnoreConditionalBuffer.append("!");
                  }
               }
            }
         }

         this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
      }

   }

   protected final boolean scanDecls(boolean complete) throws IOException, XNIException {
      this.skipSeparator(false, true);

      for(boolean again = true; again && this.fScannerState == 2; this.skipSeparator(false, true)) {
         again = complete;
         if (this.fEntityScanner.skipChar(60, (XMLScanner.NameType)null)) {
            ++this.fMarkUpDepth;
            if (this.fEntityScanner.skipChar(63, (XMLScanner.NameType)null)) {
               this.fStringBuffer.clear();
               this.scanPI(this.fStringBuffer);
               --this.fMarkUpDepth;
            } else if (this.fEntityScanner.skipChar(33, (XMLScanner.NameType)null)) {
               if (this.fEntityScanner.skipChar(45, (XMLScanner.NameType)null)) {
                  if (!this.fEntityScanner.skipChar(45, (XMLScanner.NameType)null)) {
                     this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
                  } else {
                     this.scanComment();
                  }
               } else if (this.fEntityScanner.skipString("ELEMENT")) {
                  this.scanElementDecl();
               } else if (this.fEntityScanner.skipString("ATTLIST")) {
                  this.scanAttlistDecl();
               } else if (this.fEntityScanner.skipString("ENTITY")) {
                  this.scanEntityDecl();
               } else if (this.fEntityScanner.skipString("NOTATION")) {
                  this.scanNotationDecl();
               } else if (this.fEntityScanner.skipChar(91, (XMLScanner.NameType)null) && !this.scanningInternalSubset()) {
                  this.scanConditionalSect(this.fPEDepth);
               } else {
                  --this.fMarkUpDepth;
                  this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
               }
            } else {
               --this.fMarkUpDepth;
               this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
            }
         } else if (this.fIncludeSectDepth > 0 && this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null)) {
            if (!this.fEntityScanner.skipChar(93, (XMLScanner.NameType)null) || !this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
               this.reportFatalError("IncludeSectUnterminated", (Object[])null);
            }

            if (this.fDTDHandler != null) {
               this.fDTDHandler.endConditional((Augmentations)null);
            }

            --this.fIncludeSectDepth;
            --this.fMarkUpDepth;
         } else {
            if (this.scanningInternalSubset() && this.fEntityScanner.peekChar() == 93) {
               return false;
            }

            if (!this.fEntityScanner.skipSpaces()) {
               this.reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", (Object[])null);
            }
         }
      }

      return this.fScannerState != 0;
   }

   private boolean skipSeparator(boolean spaceRequired, boolean lookForPERefs) throws IOException, XNIException {
      int depth = this.fPEDepth;
      boolean sawSpace = this.fEntityScanner.skipSpaces();
      if (lookForPERefs && this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
         do {
            String name = this.fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
            if (name == null) {
               this.reportFatalError("NameRequiredInPEReference", (Object[])null);
            } else if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
               this.reportFatalError("SemicolonRequiredInPEReference", new Object[]{name});
            }

            this.startPE(name, false);
            this.fEntityScanner.skipSpaces();
         } while(this.fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE));

         return true;
      } else {
         return !spaceRequired || sawSpace || depth != this.fPEDepth;
      }
   }

   private final void pushContentStack(int c) {
      if (this.fContentStack.length == this.fContentDepth) {
         int[] newStack = new int[this.fContentDepth * 2];
         System.arraycopy(this.fContentStack, 0, newStack, 0, this.fContentDepth);
         this.fContentStack = newStack;
      }

      this.fContentStack[this.fContentDepth++] = c;
   }

   private final int popContentStack() {
      return this.fContentStack[--this.fContentDepth];
   }

   private final void pushPEStack(int depth, boolean report) {
      if (this.fPEStack.length == this.fPEDepth) {
         int[] newIntStack = new int[this.fPEDepth * 2];
         System.arraycopy(this.fPEStack, 0, newIntStack, 0, this.fPEDepth);
         this.fPEStack = newIntStack;
         boolean[] newBooleanStack = new boolean[this.fPEDepth * 2];
         System.arraycopy(this.fPEReport, 0, newBooleanStack, 0, this.fPEDepth);
         this.fPEReport = newBooleanStack;
      }

      this.fPEReport[this.fPEDepth] = report;
      this.fPEStack[this.fPEDepth++] = depth;
   }

   private final int popPEStack() {
      return this.fPEStack[--this.fPEDepth];
   }

   private final boolean peekReportEntity() {
      return this.fPEReport[this.fPEDepth - 1];
   }

   private final void ensureEnumerationSize(int size) {
      if (this.fEnumeration.length == size) {
         String[] newEnum = new String[size * 2];
         System.arraycopy(this.fEnumeration, 0, newEnum, 0, size);
         this.fEnumeration = newEnum;
      }

   }

   private void init() {
      this.fStartDTDCalled = false;
      this.fExtEntityDepth = 0;
      this.fIncludeSectDepth = 0;
      this.fMarkUpDepth = 0;
      this.fPEDepth = 0;
      this.fStandalone = false;
      this.fSeenExternalDTD = false;
      this.fSeenExternalPE = false;
      this.setScannerState(1);
      this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
      this.fSecurityManager = this.fEntityManager.fSecurityManager;
   }

   public DTDGrammar getGrammar() {
      return this.nvGrammarInfo;
   }

   static {
      FEATURE_DEFAULTS = new Boolean[]{null, Boolean.FALSE};
      RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager"};
      PROPERTY_DEFAULTS = new Object[]{null, null, null};
   }
}
