package com.sun.xml.internal.stream.dtd;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLAttributeDecl;

public class DTDGrammarUtil {
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   private static final boolean DEBUG_ATTRIBUTES = false;
   private static final boolean DEBUG_ELEMENT_CHILDREN = false;
   protected DTDGrammar fDTDGrammar = null;
   protected boolean fNamespaces;
   protected SymbolTable fSymbolTable = null;
   private int fCurrentElementIndex = -1;
   private int fCurrentContentSpecType = -1;
   private boolean[] fElementContentState = new boolean[8];
   private int fElementDepth = -1;
   private boolean fInElementContent = false;
   private XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
   private QName fTempQName = new QName();
   private StringBuffer fBuffer = new StringBuffer();
   private NamespaceContext fNamespaceContext = null;

   public DTDGrammarUtil(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public DTDGrammarUtil(DTDGrammar grammar, SymbolTable symbolTable) {
      this.fDTDGrammar = grammar;
      this.fSymbolTable = symbolTable;
   }

   public DTDGrammarUtil(DTDGrammar grammar, SymbolTable symbolTable, NamespaceContext namespaceContext) {
      this.fDTDGrammar = grammar;
      this.fSymbolTable = symbolTable;
      this.fNamespaceContext = namespaceContext;
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fDTDGrammar = null;
      this.fInElementContent = false;
      this.fCurrentElementIndex = -1;
      this.fCurrentContentSpecType = -1;
      this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
      this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fElementDepth = -1;
   }

   public void startElement(QName element, XMLAttributes attributes) throws XNIException {
      this.handleStartElement(element, attributes);
   }

   public void endElement(QName element) throws XNIException {
      this.handleEndElement(element);
   }

   public void startCDATA(Augmentations augs) throws XNIException {
   }

   public void endCDATA(Augmentations augs) throws XNIException {
   }

   public void addDTDDefaultAttrs(QName elementName, XMLAttributes attributes) throws XNIException {
      int elementIndex = this.fDTDGrammar.getElementDeclIndex(elementName);
      if (elementIndex != -1 && this.fDTDGrammar != null) {
         String attRawName;
         String attValue;
         boolean changedByNormalization;
         for(int attlistIndex = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex); attlistIndex != -1; attlistIndex = this.fDTDGrammar.getNextAttributeDeclIndex(attlistIndex)) {
            this.fDTDGrammar.getAttributeDecl(attlistIndex, this.fTempAttDecl);
            String attPrefix = this.fTempAttDecl.name.prefix;
            String attLocalpart = this.fTempAttDecl.name.localpart;
            attRawName = this.fTempAttDecl.name.rawname;
            String attType = this.getAttributeTypeName(this.fTempAttDecl);
            int attDefaultType = this.fTempAttDecl.simpleType.defaultType;
            attValue = null;
            if (this.fTempAttDecl.simpleType.defaultValue != null) {
               attValue = this.fTempAttDecl.simpleType.defaultValue;
            }

            changedByNormalization = false;
            boolean required = attDefaultType == 2;
            boolean cdata = attType == XMLSymbols.fCDATASymbol;
            int index;
            if (!cdata || required || attValue != null) {
               int i;
               if (this.fNamespaceContext != null && attRawName.startsWith("xmlns")) {
                  String prefix = "";
                  i = attRawName.indexOf(58);
                  if (i != -1) {
                     prefix = attRawName.substring(0, i);
                  } else {
                     prefix = attRawName;
                  }

                  prefix = this.fSymbolTable.addSymbol(prefix);
                  if (!((NamespaceSupport)this.fNamespaceContext).containsPrefixInCurrentContext(prefix)) {
                     this.fNamespaceContext.declarePrefix(prefix, attValue);
                  }

                  changedByNormalization = true;
               } else {
                  index = attributes.getLength();

                  for(i = 0; i < index; ++i) {
                     if (attributes.getQName(i) == attRawName) {
                        changedByNormalization = true;
                        break;
                     }
                  }
               }
            }

            if (!changedByNormalization && attValue != null) {
               if (this.fNamespaces) {
                  index = attRawName.indexOf(58);
                  if (index != -1) {
                     attPrefix = attRawName.substring(0, index);
                     attPrefix = this.fSymbolTable.addSymbol(attPrefix);
                     attLocalpart = attRawName.substring(index + 1);
                     attLocalpart = this.fSymbolTable.addSymbol(attLocalpart);
                  }
               }

               this.fTempQName.setValues(attPrefix, attLocalpart, attRawName, this.fTempAttDecl.name.uri);
               attributes.addAttribute(this.fTempQName, attType, attValue);
            }
         }

         int attrCount = attributes.getLength();

         for(int i = 0; i < attrCount; ++i) {
            attRawName = attributes.getQName(i);
            boolean declared = false;

            for(int position = this.fDTDGrammar.getFirstAttributeDeclIndex(elementIndex); position != -1; position = this.fDTDGrammar.getNextAttributeDeclIndex(position)) {
               this.fDTDGrammar.getAttributeDecl(position, this.fTempAttDecl);
               if (this.fTempAttDecl.name.rawname == attRawName) {
                  declared = true;
                  break;
               }
            }

            if (declared) {
               attValue = this.getAttributeTypeName(this.fTempAttDecl);
               attributes.setType(i, attValue);
               changedByNormalization = false;
               if (attributes.isSpecified(i) && attValue != XMLSymbols.fCDATASymbol) {
                  this.normalizeAttrValue(attributes, i);
               }
            }
         }

      }
   }

   private boolean normalizeAttrValue(XMLAttributes attributes, int index) {
      boolean leadingSpace = true;
      boolean spaceStart = false;
      boolean readingNonSpace = false;
      int count = 0;
      int eaten = 0;
      String attrValue = attributes.getValue(index);
      char[] attValue = new char[attrValue.length()];
      this.fBuffer.setLength(0);
      attrValue.getChars(0, attrValue.length(), attValue, 0);

      for(int i = 0; i < attValue.length; ++i) {
         if (attValue[i] == ' ') {
            if (readingNonSpace) {
               spaceStart = true;
               readingNonSpace = false;
            }

            if (spaceStart && !leadingSpace) {
               spaceStart = false;
               this.fBuffer.append(attValue[i]);
               ++count;
            } else if (leadingSpace || !spaceStart) {
               ++eaten;
            }
         } else {
            readingNonSpace = true;
            spaceStart = false;
            leadingSpace = false;
            this.fBuffer.append(attValue[i]);
            ++count;
         }
      }

      if (count > 0 && this.fBuffer.charAt(count - 1) == ' ') {
         this.fBuffer.setLength(count - 1);
      }

      String newValue = this.fBuffer.toString();
      attributes.setValue(index, newValue);
      return !attrValue.equals(newValue);
   }

   private String getAttributeTypeName(XMLAttributeDecl attrDecl) {
      switch(attrDecl.simpleType.type) {
      case 1:
         return attrDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
      case 2:
         StringBuffer buffer = new StringBuffer();
         buffer.append('(');

         for(int i = 0; i < attrDecl.simpleType.enumeration.length; ++i) {
            if (i > 0) {
               buffer.append("|");
            }

            buffer.append(attrDecl.simpleType.enumeration[i]);
         }

         buffer.append(')');
         return this.fSymbolTable.addSymbol(buffer.toString());
      case 3:
         return XMLSymbols.fIDSymbol;
      case 4:
         return attrDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
      case 5:
         return attrDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
      case 6:
         return XMLSymbols.fNOTATIONSymbol;
      default:
         return XMLSymbols.fCDATASymbol;
      }
   }

   private void ensureStackCapacity(int newElementDepth) {
      if (newElementDepth == this.fElementContentState.length) {
         boolean[] newStack = new boolean[newElementDepth * 2];
         System.arraycopy(this.fElementContentState, 0, newStack, 0, newElementDepth);
         this.fElementContentState = newStack;
      }

   }

   protected void handleStartElement(QName element, XMLAttributes attributes) throws XNIException {
      if (this.fDTDGrammar == null) {
         this.fCurrentElementIndex = -1;
         this.fCurrentContentSpecType = -1;
         this.fInElementContent = false;
      } else {
         this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(element);
         this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
         this.addDTDDefaultAttrs(element, attributes);
         this.fInElementContent = this.fCurrentContentSpecType == 3;
         ++this.fElementDepth;
         this.ensureStackCapacity(this.fElementDepth);
         this.fElementContentState[this.fElementDepth] = this.fInElementContent;
      }
   }

   protected void handleEndElement(QName element) throws XNIException {
      if (this.fDTDGrammar != null) {
         --this.fElementDepth;
         if (this.fElementDepth < -1) {
            throw new RuntimeException("FWK008 Element stack underflow");
         } else if (this.fElementDepth < 0) {
            this.fCurrentElementIndex = -1;
            this.fCurrentContentSpecType = -1;
            this.fInElementContent = false;
         } else {
            this.fInElementContent = this.fElementContentState[this.fElementDepth];
         }
      }
   }

   public boolean isInElementContent() {
      return this.fInElementContent;
   }

   public boolean isIgnorableWhiteSpace(XMLString text) {
      if (this.isInElementContent()) {
         for(int i = text.offset; i < text.offset + text.length; ++i) {
            if (!XMLChar.isSpace(text.ch[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
