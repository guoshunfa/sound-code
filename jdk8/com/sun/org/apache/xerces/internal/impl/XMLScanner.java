package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;

public abstract class XMLScanner implements XMLComponent {
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
   private boolean fNeedNonNormalizedValue = false;
   protected ArrayList<XMLString> attributeValueCache = new ArrayList();
   protected ArrayList<XMLStringBuffer> stringBufferCache = new ArrayList();
   protected int fStringBufferIndex = 0;
   protected boolean fAttributeCacheInitDone = false;
   protected int fAttributeCacheUsedCount = 0;
   protected boolean fValidation = false;
   protected boolean fNamespaces;
   protected boolean fNotifyCharRefs = false;
   protected boolean fParserSettings = true;
   protected PropertyManager fPropertyManager = null;
   protected SymbolTable fSymbolTable;
   protected XMLErrorReporter fErrorReporter;
   protected XMLEntityManager fEntityManager = null;
   protected XMLEntityStorage fEntityStore = null;
   protected XMLSecurityManager fSecurityManager = null;
   protected XMLLimitAnalyzer fLimitAnalyzer = null;
   protected XMLEvent fEvent;
   protected XMLEntityScanner fEntityScanner = null;
   protected int fEntityDepth;
   protected String fCharRefLiteral = null;
   protected boolean fScanningAttribute;
   protected boolean fReportEntity;
   protected static final String fVersionSymbol = "version".intern();
   protected static final String fEncodingSymbol = "encoding".intern();
   protected static final String fStandaloneSymbol = "standalone".intern();
   protected static final String fAmpSymbol = "amp".intern();
   protected static final String fLtSymbol = "lt".intern();
   protected static final String fGtSymbol = "gt".intern();
   protected static final String fQuotSymbol = "quot".intern();
   protected static final String fAposSymbol = "apos".intern();
   private XMLString fString = new XMLString();
   private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
   private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
   private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
   protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
   int initialCacheCount = 6;

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fParserSettings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
      if (!this.fParserSettings) {
         this.init();
      } else {
         this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
         this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
         this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
         this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
         this.fEntityStore = this.fEntityManager.getEntityStore();
         this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
         this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
         this.fNotifyCharRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
         this.init();
      }
   }

   protected void setPropertyManager(PropertyManager propertyManager) {
      this.fPropertyManager = propertyManager;
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId.startsWith("http://apache.org/xml/properties/")) {
         String property = propertyId.substring("http://apache.org/xml/properties/".length());
         if (property.equals("internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)value;
         } else if (property.equals("internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)value;
         } else if (property.equals("internal/entity-manager")) {
            this.fEntityManager = (XMLEntityManager)value;
         }
      }

      if (propertyId.equals("http://apache.org/xml/properties/security-manager")) {
         this.fSecurityManager = (XMLSecurityManager)value;
      }

   }

   public void setFeature(String featureId, boolean value) throws XMLConfigurationException {
      if ("http://xml.org/sax/features/validation".equals(featureId)) {
         this.fValidation = value;
      } else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
         this.fNotifyCharRefs = value;
      }

   }

   public boolean getFeature(String featureId) throws XMLConfigurationException {
      if ("http://xml.org/sax/features/validation".equals(featureId)) {
         return this.fValidation;
      } else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
         return this.fNotifyCharRefs;
      } else {
         throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
      }
   }

   protected void reset() {
      this.init();
      this.fValidation = true;
      this.fNotifyCharRefs = false;
   }

   public void reset(PropertyManager propertyManager) {
      this.init();
      this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fEntityManager = (XMLEntityManager)propertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
      this.fEntityStore = this.fEntityManager.getEntityStore();
      this.fEntityScanner = this.fEntityManager.getEntityScanner();
      this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
      this.fValidation = false;
      this.fNotifyCharRefs = false;
   }

   protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl, String[] pseudoAttributeValues) throws IOException, XNIException {
      String version = null;
      String encoding = null;
      String standalone = null;
      int STATE_VERSION = false;
      int STATE_ENCODING = true;
      int STATE_STANDALONE = true;
      int STATE_DONE = true;
      int state = 0;
      boolean dataFoundForTarget = false;
      boolean sawSpace = this.fEntityScanner.skipSpaces();
      Entity.ScannedEntity currEnt = this.fEntityManager.getCurrentEntity();
      boolean currLiteral = currEnt.literal;

      for(currEnt.literal = false; this.fEntityScanner.peekChar() != 63; sawSpace = this.fEntityScanner.skipSpaces()) {
         dataFoundForTarget = true;
         String name = this.scanPseudoAttribute(scanningTextDecl, this.fString);
         switch(state) {
         case 0:
            if (!name.equals(fVersionSymbol)) {
               if (name.equals(fEncodingSymbol)) {
                  if (!scanningTextDecl) {
                     this.reportFatalError("VersionInfoRequired", (Object[])null);
                  }

                  if (!sawSpace) {
                     this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", (Object[])null);
                  }

                  encoding = this.fString.toString();
                  state = scanningTextDecl ? 3 : 2;
               } else if (scanningTextDecl) {
                  this.reportFatalError("EncodingDeclRequired", (Object[])null);
               } else {
                  this.reportFatalError("VersionInfoRequired", (Object[])null);
               }
            } else {
               if (!sawSpace) {
                  this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", (Object[])null);
               }

               version = this.fString.toString();
               state = 1;
               if (!this.versionSupported(version)) {
                  this.reportFatalError("VersionNotSupported", new Object[]{version});
               }

               if (!version.equals("1.1")) {
                  continue;
               }

               Entity.ScannedEntity top = this.fEntityManager.getTopLevelEntity();
               if (top != null && (top.version == null || top.version.equals("1.0"))) {
                  this.reportFatalError("VersionMismatch", (Object[])null);
               }

               this.fEntityManager.setScannerVersion((short)2);
            }
            break;
         case 1:
            if (name.equals(fEncodingSymbol)) {
               if (!sawSpace) {
                  this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", (Object[])null);
               }

               encoding = this.fString.toString();
               state = scanningTextDecl ? 3 : 2;
            } else {
               if (!scanningTextDecl && name.equals(fStandaloneSymbol)) {
                  if (!sawSpace) {
                     this.reportFatalError("SpaceRequiredBeforeStandalone", (Object[])null);
                  }

                  standalone = this.fString.toString();
                  state = 3;
                  if (!standalone.equals("yes") && !standalone.equals("no")) {
                     this.reportFatalError("SDDeclInvalid", new Object[]{standalone});
                  }
                  continue;
               }

               this.reportFatalError("EncodingDeclRequired", (Object[])null);
            }
            break;
         case 2:
            if (name.equals(fStandaloneSymbol)) {
               if (!sawSpace) {
                  this.reportFatalError("SpaceRequiredBeforeStandalone", (Object[])null);
               }

               standalone = this.fString.toString();
               state = 3;
               if (!standalone.equals("yes") && !standalone.equals("no")) {
                  this.reportFatalError("SDDeclInvalid", new Object[]{standalone});
               }
            } else {
               this.reportFatalError("SDDeclNameInvalid", (Object[])null);
            }
            break;
         default:
            this.reportFatalError("NoMorePseudoAttributes", (Object[])null);
         }
      }

      if (currLiteral) {
         currEnt.literal = true;
      }

      if (scanningTextDecl && state != 3) {
         this.reportFatalError("MorePseudoAttributes", (Object[])null);
      }

      if (scanningTextDecl) {
         if (!dataFoundForTarget && encoding == null) {
            this.reportFatalError("EncodingDeclRequired", (Object[])null);
         }
      } else if (!dataFoundForTarget && version == null) {
         this.reportFatalError("VersionInfoRequired", (Object[])null);
      }

      if (!this.fEntityScanner.skipChar(63, (XMLScanner.NameType)null)) {
         this.reportFatalError("XMLDeclUnterminated", (Object[])null);
      }

      if (!this.fEntityScanner.skipChar(62, (XMLScanner.NameType)null)) {
         this.reportFatalError("XMLDeclUnterminated", (Object[])null);
      }

      pseudoAttributeValues[0] = version;
      pseudoAttributeValues[1] = encoding;
      pseudoAttributeValues[2] = standalone;
   }

   protected String scanPseudoAttribute(boolean scanningTextDecl, XMLString value) throws IOException, XNIException {
      String name = this.scanPseudoAttributeName();
      if (name == null) {
         this.reportFatalError("PseudoAttrNameExpected", (Object[])null);
      }

      this.fEntityScanner.skipSpaces();
      if (!this.fEntityScanner.skipChar(61, (XMLScanner.NameType)null)) {
         this.reportFatalError(scanningTextDecl ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[]{name});
      }

      this.fEntityScanner.skipSpaces();
      int quote = this.fEntityScanner.peekChar();
      if (quote != 39 && quote != 34) {
         this.reportFatalError(scanningTextDecl ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[]{name});
      }

      this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      int c = this.fEntityScanner.scanLiteral(quote, value, false);
      if (c != quote) {
         this.fStringBuffer2.clear();

         do {
            this.fStringBuffer2.append(value);
            if (c != -1) {
               if (c != 38 && c != 37 && c != 60 && c != 93) {
                  if (XMLChar.isHighSurrogate(c)) {
                     this.scanSurrogates(this.fStringBuffer2);
                  } else if (this.isInvalidLiteral(c)) {
                     String key = scanningTextDecl ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
                     this.reportFatalError(key, new Object[]{Integer.toString(c, 16)});
                     this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  }
               } else {
                  this.fStringBuffer2.append((char)this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE));
               }
            }

            c = this.fEntityScanner.scanLiteral(quote, value, false);
         } while(c != quote);

         this.fStringBuffer2.append(value);
         value.setValues(this.fStringBuffer2);
      }

      if (!this.fEntityScanner.skipChar(quote, (XMLScanner.NameType)null)) {
         this.reportFatalError(scanningTextDecl ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[]{name});
      }

      return name;
   }

   private String scanPseudoAttributeName() throws IOException, XNIException {
      int ch = this.fEntityScanner.peekChar();
      switch(ch) {
      case 101:
         if (this.fEntityScanner.skipString(fEncodingSymbol)) {
            return fEncodingSymbol;
         }
         break;
      case 115:
         if (this.fEntityScanner.skipString(fStandaloneSymbol)) {
            return fStandaloneSymbol;
         }
         break;
      case 118:
         if (this.fEntityScanner.skipString(fVersionSymbol)) {
            return fVersionSymbol;
         }
      }

      return null;
   }

   protected void scanPI(XMLStringBuffer data) throws IOException, XNIException {
      this.fReportEntity = false;
      String target = this.fEntityScanner.scanName(XMLScanner.NameType.PI);
      if (target == null) {
         this.reportFatalError("PITargetRequired", (Object[])null);
      }

      this.scanPIData(target, data);
      this.fReportEntity = true;
   }

   protected void scanPIData(String target, XMLStringBuffer data) throws IOException, XNIException {
      if (target.length() == 3) {
         char c0 = Character.toLowerCase(target.charAt(0));
         char c1 = Character.toLowerCase(target.charAt(1));
         char c2 = Character.toLowerCase(target.charAt(2));
         if (c0 == 'x' && c1 == 'm' && c2 == 'l') {
            this.reportFatalError("ReservedPITarget", (Object[])null);
         }
      }

      if (!this.fEntityScanner.skipSpaces()) {
         if (this.fEntityScanner.skipString("?>")) {
            return;
         }

         this.reportFatalError("SpaceRequiredInPI", (Object[])null);
      }

      if (this.fEntityScanner.scanData("?>", data)) {
         do {
            int c = this.fEntityScanner.peekChar();
            if (c != -1) {
               if (XMLChar.isHighSurrogate(c)) {
                  this.scanSurrogates(data);
               } else if (this.isInvalidLiteral(c)) {
                  this.reportFatalError("InvalidCharInPI", new Object[]{Integer.toHexString(c)});
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               }
            }
         } while(this.fEntityScanner.scanData("?>", data));
      }

   }

   protected void scanComment(XMLStringBuffer text) throws IOException, XNIException {
      text.clear();

      while(this.fEntityScanner.scanData("--", text)) {
         int c = this.fEntityScanner.peekChar();
         if (c != -1) {
            if (XMLChar.isHighSurrogate(c)) {
               this.scanSurrogates(text);
            } else if (this.isInvalidLiteral(c)) {
               this.reportFatalError("InvalidCharInComment", new Object[]{Integer.toHexString(c)});
               this.fEntityScanner.scanChar(XMLScanner.NameType.COMMENT);
            }
         }
      }

      if (!this.fEntityScanner.skipChar(62, XMLScanner.NameType.COMMENT)) {
         this.reportFatalError("DashDashInComment", (Object[])null);
      }

   }

   protected void scanAttributeValue(XMLString value, XMLString nonNormalizedValue, String atName, XMLAttributes attributes, int attrIndex, boolean checkEntities, String eleName, boolean isNSURI) throws IOException, XNIException {
      XMLStringBuffer stringBuffer = null;
      int quote = this.fEntityScanner.peekChar();
      if (quote != 39 && quote != 34) {
         this.reportFatalError("OpenQuoteExpected", new Object[]{eleName, atName});
      }

      this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      int entityDepth = this.fEntityDepth;
      int c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
      if (this.fNeedNonNormalizedValue) {
         this.fStringBuffer2.clear();
         this.fStringBuffer2.append(value);
      }

      if (this.fEntityScanner.whiteSpaceLen > 0) {
         this.normalizeWhitespace(value);
      }

      int ch;
      if (c != quote) {
         this.fScanningAttribute = true;
         stringBuffer = this.getStringBuffer();
         stringBuffer.clear();

         do {
            stringBuffer.append(value);
            if (c == 38) {
               this.fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE);
               if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                  this.fStringBuffer2.append('&');
               }

               if (this.fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE)) {
                  if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                     this.fStringBuffer2.append('#');
                  }

                  if (this.fNeedNonNormalizedValue) {
                     ch = this.scanCharReferenceValue(stringBuffer, this.fStringBuffer2);
                  } else {
                     ch = this.scanCharReferenceValue(stringBuffer, (XMLStringBuffer)null);
                  }

                  if (ch != -1) {
                  }
               } else {
                  String entityName = this.fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
                  if (entityName == null) {
                     this.reportFatalError("NameRequiredInReference", (Object[])null);
                  } else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                     this.fStringBuffer2.append(entityName);
                  }

                  if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
                     this.reportFatalError("SemicolonRequiredInReference", new Object[]{entityName});
                  } else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                     this.fStringBuffer2.append(';');
                  }

                  if (this.resolveCharacter(entityName, stringBuffer)) {
                     this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
                  } else if (this.fEntityStore.isExternalEntity(entityName)) {
                     this.reportFatalError("ReferenceToExternalEntity", new Object[]{entityName});
                  } else {
                     if (!this.fEntityStore.isDeclaredEntity(entityName)) {
                        if (checkEntities) {
                           if (this.fValidation) {
                              this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{entityName}, (short)1);
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
               if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                  this.fStringBuffer2.append((char)c);
               }
            } else if (c != 37 && c != 93) {
               if (c != 10 && c != 13) {
                  if (c != -1 && XMLChar.isHighSurrogate(c)) {
                     this.fStringBuffer3.clear();
                     if (this.scanSurrogates(this.fStringBuffer3)) {
                        stringBuffer.append((XMLString)this.fStringBuffer3);
                        if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                           this.fStringBuffer2.append((XMLString)this.fStringBuffer3);
                        }
                     }
                  } else if (c != -1 && this.isInvalidLiteral(c)) {
                     this.reportFatalError("InvalidCharInAttValue", new Object[]{eleName, atName, Integer.toString(c, 16)});
                     this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                     if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append((char)c);
                     }
                  }
               } else {
                  this.fEntityScanner.scanChar((XMLScanner.NameType)null);
                  stringBuffer.append(' ');
                  if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                     this.fStringBuffer2.append('\n');
                  }
               }
            } else {
               this.fEntityScanner.scanChar((XMLScanner.NameType)null);
               stringBuffer.append((char)c);
               if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                  this.fStringBuffer2.append((char)c);
               }
            }

            c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
            if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
               this.fStringBuffer2.append(value);
            }

            if (this.fEntityScanner.whiteSpaceLen > 0) {
               this.normalizeWhitespace(value);
            }
         } while(c != quote || entityDepth != this.fEntityDepth);

         stringBuffer.append(value);
         value.setValues(stringBuffer);
         this.fScanningAttribute = false;
      }

      if (this.fNeedNonNormalizedValue) {
         nonNormalizedValue.setValues(this.fStringBuffer2);
      }

      ch = this.fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      if (ch != quote) {
         this.reportFatalError("CloseQuoteExpected", new Object[]{eleName, atName});
      }

   }

   protected boolean resolveCharacter(String entityName, XMLStringBuffer stringBuffer) {
      if (entityName == fAmpSymbol) {
         stringBuffer.append('&');
         return true;
      } else if (entityName == fAposSymbol) {
         stringBuffer.append('\'');
         return true;
      } else if (entityName == fLtSymbol) {
         stringBuffer.append('<');
         return true;
      } else if (entityName == fGtSymbol) {
         this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
         stringBuffer.append('>');
         return true;
      } else if (entityName == fQuotSymbol) {
         this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
         stringBuffer.append('"');
         return true;
      } else {
         return false;
      }
   }

   protected void scanExternalID(String[] identifiers, boolean optionalSystemId) throws IOException, XNIException {
      String systemId = null;
      String publicId = null;
      if (this.fEntityScanner.skipString("PUBLIC")) {
         if (!this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("SpaceRequiredAfterPUBLIC", (Object[])null);
         }

         this.scanPubidLiteral(this.fString);
         publicId = this.fString.toString();
         if (!this.fEntityScanner.skipSpaces() && !optionalSystemId) {
            this.reportFatalError("SpaceRequiredBetweenPublicAndSystem", (Object[])null);
         }
      }

      if (publicId != null || this.fEntityScanner.skipString("SYSTEM")) {
         if (publicId == null && !this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("SpaceRequiredAfterSYSTEM", (Object[])null);
         }

         int quote = this.fEntityScanner.peekChar();
         if (quote != 39 && quote != 34) {
            if (publicId != null && optionalSystemId) {
               identifiers[0] = null;
               identifiers[1] = publicId;
               return;
            }

            this.reportFatalError("QuoteRequiredInSystemID", (Object[])null);
         }

         this.fEntityScanner.scanChar((XMLScanner.NameType)null);
         XMLString ident = this.fString;
         if (this.fEntityScanner.scanLiteral(quote, (XMLString)ident, false) != quote) {
            this.fStringBuffer.clear();

            while(true) {
               this.fStringBuffer.append((XMLString)ident);
               int c = this.fEntityScanner.peekChar();
               if (!XMLChar.isMarkup(c) && c != 93) {
                  if (c != -1 && this.isInvalidLiteral(c)) {
                     this.reportFatalError("InvalidCharInSystemID", new Object[]{Integer.toString(c, 16)});
                  }
               } else {
                  this.fStringBuffer.append((char)this.fEntityScanner.scanChar((XMLScanner.NameType)null));
               }

               if (this.fEntityScanner.scanLiteral(quote, (XMLString)ident, false) == quote) {
                  this.fStringBuffer.append((XMLString)ident);
                  ident = this.fStringBuffer;
                  break;
               }
            }
         }

         systemId = ((XMLString)ident).toString();
         if (!this.fEntityScanner.skipChar(quote, (XMLScanner.NameType)null)) {
            this.reportFatalError("SystemIDUnterminated", (Object[])null);
         }
      }

      identifiers[0] = systemId;
      identifiers[1] = publicId;
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
               if (c != 32 && c != 10 && c != 13) {
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
      int i = 0;
      int j = false;
      int[] buff = this.fEntityScanner.whiteSpaceLookup;
      int buffLen = this.fEntityScanner.whiteSpaceLen;

      for(int end = value.offset + value.length; i < buffLen; ++i) {
         int j = buff[i];
         if (j < end) {
            value.ch[j] = ' ';
         }
      }

   }

   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      ++this.fEntityDepth;
      this.fEntityScanner = this.fEntityManager.getEntityScanner();
      this.fEntityStore = this.fEntityManager.getEntityStore();
   }

   public void endEntity(String name, Augmentations augs) throws IOException, XNIException {
      --this.fEntityDepth;
   }

   protected int scanCharReferenceValue(XMLStringBuffer buf, XMLStringBuffer buf2) throws IOException, XNIException {
      int initLen = buf.length;
      boolean hex = false;
      boolean digit;
      int c;
      if (this.fEntityScanner.skipChar(120, XMLScanner.NameType.REFERENCE)) {
         if (buf2 != null) {
            buf2.append('x');
         }

         hex = true;
         this.fStringBuffer3.clear();
         digit = true;
         c = this.fEntityScanner.peekChar();
         digit = c >= 48 && c <= 57 || c >= 97 && c <= 102 || c >= 65 && c <= 70;
         if (digit) {
            if (buf2 != null) {
               buf2.append((char)c);
            }

            this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
            this.fStringBuffer3.append((char)c);

            do {
               c = this.fEntityScanner.peekChar();
               digit = c >= 48 && c <= 57 || c >= 97 && c <= 102 || c >= 65 && c <= 70;
               if (digit) {
                  if (buf2 != null) {
                     buf2.append((char)c);
                  }

                  this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
                  this.fStringBuffer3.append((char)c);
               }
            } while(digit);
         } else {
            this.reportFatalError("HexdigitRequiredInCharRef", (Object[])null);
         }
      } else {
         this.fStringBuffer3.clear();
         digit = true;
         c = this.fEntityScanner.peekChar();
         digit = c >= 48 && c <= 57;
         if (digit) {
            if (buf2 != null) {
               buf2.append((char)c);
            }

            this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
            this.fStringBuffer3.append((char)c);

            do {
               c = this.fEntityScanner.peekChar();
               digit = c >= 48 && c <= 57;
               if (digit) {
                  if (buf2 != null) {
                     buf2.append((char)c);
                  }

                  this.fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
                  this.fStringBuffer3.append((char)c);
               }
            } while(digit);
         } else {
            this.reportFatalError("DigitRequiredInCharRef", (Object[])null);
         }
      }

      if (!this.fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
         this.reportFatalError("SemicolonRequiredInCharRef", (Object[])null);
      }

      if (buf2 != null) {
         buf2.append(';');
      }

      int value = -1;

      try {
         value = Integer.parseInt(this.fStringBuffer3.toString(), hex ? 16 : 10);
         if (this.isInvalid(value)) {
            StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
            if (hex) {
               errorBuf.append('x');
            }

            errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
            this.reportFatalError("InvalidCharRef", new Object[]{errorBuf.toString()});
         }
      } catch (NumberFormatException var8) {
         StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
         if (hex) {
            errorBuf.append('x');
         }

         errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
         this.reportFatalError("InvalidCharRef", new Object[]{errorBuf.toString()});
      }

      if (!XMLChar.isSupplemental(value)) {
         buf.append((char)value);
      } else {
         buf.append(XMLChar.highSurrogate(value));
         buf.append(XMLChar.lowSurrogate(value));
      }

      if (this.fNotifyCharRefs && value != -1) {
         String literal = "#" + (hex ? "x" : "") + this.fStringBuffer3.toString();
         if (!this.fScanningAttribute) {
            this.fCharRefLiteral = literal;
         }
      }

      if (this.fEntityScanner.fCurrentEntity.isGE) {
         this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, buf.length - initLen);
      }

      return value;
   }

   protected boolean isInvalid(int value) {
      return XMLChar.isInvalid(value);
   }

   protected boolean isInvalidLiteral(int value) {
      return XMLChar.isInvalid(value);
   }

   protected boolean isValidNameChar(int value) {
      return XMLChar.isName(value);
   }

   protected boolean isValidNCName(int value) {
      return XMLChar.isNCName(value);
   }

   protected boolean isValidNameStartChar(int value) {
      return XMLChar.isNameStart(value);
   }

   protected boolean isValidNameStartHighSurrogate(int value) {
      return false;
   }

   protected boolean versionSupported(String version) {
      return version.equals("1.0") || version.equals("1.1");
   }

   protected boolean scanSurrogates(XMLStringBuffer buf) throws IOException, XNIException {
      int high = this.fEntityScanner.scanChar((XMLScanner.NameType)null);
      int low = this.fEntityScanner.peekChar();
      if (!XMLChar.isLowSurrogate(low)) {
         this.reportFatalError("InvalidCharInContent", new Object[]{Integer.toString(high, 16)});
         return false;
      } else {
         this.fEntityScanner.scanChar((XMLScanner.NameType)null);
         int c = XMLChar.supplemental((char)high, (char)low);
         if (this.isInvalid(c)) {
            this.reportFatalError("InvalidCharInContent", new Object[]{Integer.toString(c, 16)});
            return false;
         } else {
            buf.append((char)high);
            buf.append((char)low);
            return true;
         }
      }
   }

   protected void reportFatalError(String msgId, Object[] args) throws XNIException {
      this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", msgId, args, (short)2);
   }

   private void init() {
      this.fEntityScanner = null;
      this.fEntityDepth = 0;
      this.fReportEntity = true;
      this.fResourceIdentifier.clear();
      if (!this.fAttributeCacheInitDone) {
         for(int i = 0; i < this.initialCacheCount; ++i) {
            this.attributeValueCache.add(new XMLString());
            this.stringBufferCache.add(new XMLStringBuffer());
         }

         this.fAttributeCacheInitDone = true;
      }

      this.fStringBufferIndex = 0;
      this.fAttributeCacheUsedCount = 0;
   }

   XMLStringBuffer getStringBuffer() {
      if (this.fStringBufferIndex >= this.initialCacheCount && this.fStringBufferIndex >= this.stringBufferCache.size()) {
         XMLStringBuffer tmpObj = new XMLStringBuffer();
         ++this.fStringBufferIndex;
         this.stringBufferCache.add(tmpObj);
         return tmpObj;
      } else {
         return (XMLStringBuffer)this.stringBufferCache.get(this.fStringBufferIndex++);
      }
   }

   void checkEntityLimit(boolean isPEDecl, String entityName, XMLString buffer) {
      this.checkEntityLimit(isPEDecl, entityName, buffer.length);
   }

   void checkEntityLimit(boolean isPEDecl, String entityName, int len) {
      if (this.fLimitAnalyzer == null) {
         this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
      }

      if (isPEDecl) {
         this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, "%" + entityName, len);
         if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.reportFatalError("MaxEntitySizeLimit", new Object[]{"%" + entityName, this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)});
         }
      } else {
         this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entityName, len);
         if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.reportFatalError("MaxEntitySizeLimit", new Object[]{entityName, this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)});
         }
      }

      if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
         this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
         this.reportFatalError("TotalEntitySizeLimit", new Object[]{this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)});
      }

   }

   public static enum NameType {
      ATTRIBUTE("attribute"),
      ATTRIBUTENAME("attribute name"),
      COMMENT("comment"),
      DOCTYPE("doctype"),
      ELEMENTSTART("startelement"),
      ELEMENTEND("endelement"),
      ENTITY("entity"),
      NOTATION("notation"),
      PI("pi"),
      REFERENCE("reference");

      final String literal;

      private NameType(String literal) {
         this.literal = literal;
      }

      String literal() {
         return this.literal;
      }
   }
}
