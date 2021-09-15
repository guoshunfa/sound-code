package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTDGrammar {
   public static final int TOP_LEVEL_SCOPE = -1;
   private static final int CHUNK_SHIFT = 8;
   private static final int CHUNK_SIZE = 256;
   private static final int CHUNK_MASK = 255;
   private static final int INITIAL_CHUNK_COUNT = 4;
   private static final short LIST_FLAG = 128;
   private static final short LIST_MASK = -129;
   private static final boolean DEBUG = false;
   protected XMLDTDSource fDTDSource = null;
   protected XMLDTDContentModelSource fDTDContentModelSource = null;
   protected int fCurrentElementIndex;
   protected int fCurrentAttributeIndex;
   protected boolean fReadingExternalDTD = false;
   private SymbolTable fSymbolTable;
   private ArrayList notationDecls = new ArrayList();
   private int fElementDeclCount = 0;
   private QName[][] fElementDeclName = new QName[4][];
   private short[][] fElementDeclType = new short[4][];
   private int[][] fElementDeclFirstAttributeDeclIndex = new int[4][];
   private int[][] fElementDeclLastAttributeDeclIndex = new int[4][];
   private int fAttributeDeclCount = 0;
   private QName[][] fAttributeDeclName = new QName[4][];
   private short[][] fAttributeDeclType = new short[4][];
   private String[][][] fAttributeDeclEnumeration = new String[4][][];
   private short[][] fAttributeDeclDefaultType = new short[4][];
   private String[][] fAttributeDeclDefaultValue = new String[4][];
   private String[][] fAttributeDeclNonNormalizedDefaultValue = new String[4][];
   private int[][] fAttributeDeclNextAttributeDeclIndex = new int[4][];
   private final Map<String, Integer> fElementIndexMap = new HashMap();
   private final QName fQName = new QName();
   protected XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();
   private XMLElementDecl fElementDecl = new XMLElementDecl();
   private XMLSimpleType fSimpleType = new XMLSimpleType();
   Map<String, XMLElementDecl> fElementDeclTab = new HashMap();

   public DTDGrammar(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public int getAttributeDeclIndex(int elementDeclIndex, String attributeDeclName) {
      if (elementDeclIndex == -1) {
         return -1;
      } else {
         for(int attDefIndex = this.getFirstAttributeDeclIndex(elementDeclIndex); attDefIndex != -1; attDefIndex = this.getNextAttributeDeclIndex(attDefIndex)) {
            this.getAttributeDecl(attDefIndex, this.fAttributeDecl);
            if (this.fAttributeDecl.name.rawname == attributeDeclName || attributeDeclName.equals(this.fAttributeDecl.name.rawname)) {
               return attDefIndex;
            }
         }

         return -1;
      }
   }

   public void startDTD(XMLLocator locator, Augmentations augs) throws XNIException {
   }

   public void elementDecl(String name, String contentModel, Augmentations augs) throws XNIException {
      XMLElementDecl tmpElementDecl = (XMLElementDecl)this.fElementDeclTab.get(name);
      if (tmpElementDecl != null) {
         if (tmpElementDecl.type != -1) {
            return;
         }

         this.fCurrentElementIndex = this.getElementDeclIndex(name);
      } else {
         this.fCurrentElementIndex = this.createElementDecl();
      }

      XMLElementDecl elementDecl = new XMLElementDecl();
      QName elementName = new QName((String)null, name, name, (String)null);
      elementDecl.name.setValues(elementName);
      elementDecl.scope = -1;
      if (contentModel.equals("EMPTY")) {
         elementDecl.type = 1;
      } else if (contentModel.equals("ANY")) {
         elementDecl.type = 0;
      } else if (contentModel.startsWith("(")) {
         if (contentModel.indexOf("#PCDATA") > 0) {
            elementDecl.type = 2;
         } else {
            elementDecl.type = 3;
         }
      }

      this.fElementDeclTab.put(name, elementDecl);
      this.fElementDecl = elementDecl;
      this.setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
      int chunk = this.fCurrentElementIndex >> 8;
      this.ensureElementDeclCapacity(chunk);
   }

   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {
      if (type != XMLSymbols.fCDATASymbol && defaultValue != null) {
         this.normalizeDefaultAttrValue(defaultValue);
      }

      if (!this.fElementDeclTab.containsKey(elementName)) {
         this.fCurrentElementIndex = this.createElementDecl();
         XMLElementDecl elementDecl = new XMLElementDecl();
         elementDecl.name.setValues((String)null, elementName, elementName, (String)null);
         elementDecl.scope = -1;
         this.fElementDeclTab.put(elementName, elementDecl);
         this.setElementDecl(this.fCurrentElementIndex, elementDecl);
      }

      int elementIndex = this.getElementDeclIndex(elementName);
      if (this.getAttributeDeclIndex(elementIndex, attributeName) == -1) {
         this.fCurrentAttributeIndex = this.createAttributeDecl();
         this.fSimpleType.clear();
         if (defaultType != null) {
            XMLSimpleType var10001;
            if (defaultType.equals("#FIXED")) {
               var10001 = this.fSimpleType;
               this.fSimpleType.defaultType = 1;
            } else if (defaultType.equals("#IMPLIED")) {
               var10001 = this.fSimpleType;
               this.fSimpleType.defaultType = 0;
            } else if (defaultType.equals("#REQUIRED")) {
               var10001 = this.fSimpleType;
               this.fSimpleType.defaultType = 2;
            }
         }

         this.fSimpleType.defaultValue = defaultValue != null ? defaultValue.toString() : null;
         this.fSimpleType.nonNormalizedDefaultValue = nonNormalizedDefaultValue != null ? nonNormalizedDefaultValue.toString() : null;
         this.fSimpleType.enumeration = enumeration;
         if (type.equals("CDATA")) {
            this.fSimpleType.type = 0;
         } else if (type.equals("ID")) {
            this.fSimpleType.type = 3;
         } else if (type.startsWith("IDREF")) {
            this.fSimpleType.type = 4;
            if (type.indexOf("S") > 0) {
               this.fSimpleType.list = true;
            }
         } else if (type.equals("ENTITIES")) {
            this.fSimpleType.type = 1;
            this.fSimpleType.list = true;
         } else if (type.equals("ENTITY")) {
            this.fSimpleType.type = 1;
         } else if (type.equals("NMTOKENS")) {
            this.fSimpleType.type = 5;
            this.fSimpleType.list = true;
         } else if (type.equals("NMTOKEN")) {
            this.fSimpleType.type = 5;
         } else if (type.startsWith("NOTATION")) {
            this.fSimpleType.type = 6;
         } else if (type.startsWith("ENUMERATION")) {
            this.fSimpleType.type = 2;
         } else {
            System.err.println("!!! unknown attribute type " + type);
         }

         this.fQName.setValues((String)null, attributeName, attributeName, (String)null);
         this.fAttributeDecl.setValues(this.fQName, this.fSimpleType, false);
         this.setAttributeDecl(elementIndex, this.fCurrentAttributeIndex, this.fAttributeDecl);
         int chunk = this.fCurrentAttributeIndex >> 8;
         this.ensureAttributeDeclCapacity(chunk);
      }
   }

   public SymbolTable getSymbolTable() {
      return this.fSymbolTable;
   }

   public int getFirstElementDeclIndex() {
      return this.fElementDeclCount >= 0 ? 0 : -1;
   }

   public int getNextElementDeclIndex(int elementDeclIndex) {
      return elementDeclIndex < this.fElementDeclCount - 1 ? elementDeclIndex + 1 : -1;
   }

   public int getElementDeclIndex(String elementDeclName) {
      Integer mapping = (Integer)this.fElementIndexMap.get(elementDeclName);
      if (mapping == null) {
         mapping = -1;
      }

      return mapping;
   }

   public int getElementDeclIndex(QName elementDeclQName) {
      return this.getElementDeclIndex(elementDeclQName.rawname);
   }

   public short getContentSpecType(int elementIndex) {
      if (elementIndex >= 0 && elementIndex < this.fElementDeclCount) {
         int chunk = elementIndex >> 8;
         int index = elementIndex & 255;
         return this.fElementDeclType[chunk][index] == -1 ? -1 : (short)(this.fElementDeclType[chunk][index] & -129);
      } else {
         return -1;
      }
   }

   public boolean getElementDecl(int elementDeclIndex, XMLElementDecl elementDecl) {
      if (elementDeclIndex >= 0 && elementDeclIndex < this.fElementDeclCount) {
         int chunk = elementDeclIndex >> 8;
         int index = elementDeclIndex & 255;
         elementDecl.name.setValues(this.fElementDeclName[chunk][index]);
         if (this.fElementDeclType[chunk][index] == -1) {
            elementDecl.type = -1;
            elementDecl.simpleType.list = false;
         } else {
            elementDecl.type = (short)(this.fElementDeclType[chunk][index] & -129);
            elementDecl.simpleType.list = (this.fElementDeclType[chunk][index] & 128) != 0;
         }

         elementDecl.simpleType.defaultType = -1;
         elementDecl.simpleType.defaultValue = null;
         return true;
      } else {
         return false;
      }
   }

   public int getFirstAttributeDeclIndex(int elementDeclIndex) {
      int chunk = elementDeclIndex >> 8;
      int index = elementDeclIndex & 255;
      return this.fElementDeclFirstAttributeDeclIndex[chunk][index];
   }

   public int getNextAttributeDeclIndex(int attributeDeclIndex) {
      int chunk = attributeDeclIndex >> 8;
      int index = attributeDeclIndex & 255;
      return this.fAttributeDeclNextAttributeDeclIndex[chunk][index];
   }

   public boolean getAttributeDecl(int attributeDeclIndex, XMLAttributeDecl attributeDecl) {
      if (attributeDeclIndex >= 0 && attributeDeclIndex < this.fAttributeDeclCount) {
         int chunk = attributeDeclIndex >> 8;
         int index = attributeDeclIndex & 255;
         attributeDecl.name.setValues(this.fAttributeDeclName[chunk][index]);
         short attributeType;
         boolean isList;
         if (this.fAttributeDeclType[chunk][index] == -1) {
            attributeType = -1;
            isList = false;
         } else {
            attributeType = (short)(this.fAttributeDeclType[chunk][index] & -129);
            isList = (this.fAttributeDeclType[chunk][index] & 128) != 0;
         }

         attributeDecl.simpleType.setValues(attributeType, this.fAttributeDeclName[chunk][index].localpart, this.fAttributeDeclEnumeration[chunk][index], isList, this.fAttributeDeclDefaultType[chunk][index], this.fAttributeDeclDefaultValue[chunk][index], this.fAttributeDeclNonNormalizedDefaultValue[chunk][index]);
         return true;
      } else {
         return false;
      }
   }

   public boolean isCDATAAttribute(QName elName, QName atName) {
      int elDeclIdx = this.getElementDeclIndex(elName);
      return !this.getAttributeDecl(elDeclIdx, this.fAttributeDecl) || this.fAttributeDecl.simpleType.type == 0;
   }

   public void printElements() {
      int elementDeclIndex = 0;
      XMLElementDecl elementDecl = new XMLElementDecl();

      while(this.getElementDecl(elementDeclIndex++, elementDecl)) {
         System.out.println("element decl: " + elementDecl.name + ", " + elementDecl.name.rawname);
      }

   }

   public void printAttributes(int elementDeclIndex) {
      int attributeDeclIndex = this.getFirstAttributeDeclIndex(elementDeclIndex);
      System.out.print(elementDeclIndex);
      System.out.print(" [");

      while(attributeDeclIndex != -1) {
         System.out.print(' ');
         System.out.print(attributeDeclIndex);
         this.printAttribute(attributeDeclIndex);
         attributeDeclIndex = this.getNextAttributeDeclIndex(attributeDeclIndex);
         if (attributeDeclIndex != -1) {
            System.out.print(",");
         }
      }

      System.out.println(" ]");
   }

   protected int createElementDecl() {
      int chunk = this.fElementDeclCount >> 8;
      int index = this.fElementDeclCount & 255;
      this.ensureElementDeclCapacity(chunk);
      this.fElementDeclName[chunk][index] = new QName();
      this.fElementDeclType[chunk][index] = -1;
      this.fElementDeclFirstAttributeDeclIndex[chunk][index] = -1;
      this.fElementDeclLastAttributeDeclIndex[chunk][index] = -1;
      return this.fElementDeclCount++;
   }

   protected void setElementDecl(int elementDeclIndex, XMLElementDecl elementDecl) {
      if (elementDeclIndex >= 0 && elementDeclIndex < this.fElementDeclCount) {
         int chunk = elementDeclIndex >> 8;
         int index = elementDeclIndex & 255;
         int scope = elementDecl.scope;
         this.fElementDeclName[chunk][index].setValues(elementDecl.name);
         this.fElementDeclType[chunk][index] = elementDecl.type;
         if (elementDecl.simpleType.list) {
            short[] var10000 = this.fElementDeclType[chunk];
            var10000[index] = (short)(var10000[index] | 128);
         }

         this.fElementIndexMap.put(elementDecl.name.rawname, elementDeclIndex);
      }
   }

   protected void setFirstAttributeDeclIndex(int elementDeclIndex, int newFirstAttrIndex) {
      if (elementDeclIndex >= 0 && elementDeclIndex < this.fElementDeclCount) {
         int chunk = elementDeclIndex >> 8;
         int index = elementDeclIndex & 255;
         this.fElementDeclFirstAttributeDeclIndex[chunk][index] = newFirstAttrIndex;
      }
   }

   protected int createAttributeDecl() {
      int chunk = this.fAttributeDeclCount >> 8;
      int index = this.fAttributeDeclCount & 255;
      this.ensureAttributeDeclCapacity(chunk);
      this.fAttributeDeclName[chunk][index] = new QName();
      this.fAttributeDeclType[chunk][index] = -1;
      this.fAttributeDeclEnumeration[chunk][index] = null;
      this.fAttributeDeclDefaultType[chunk][index] = 0;
      this.fAttributeDeclDefaultValue[chunk][index] = null;
      this.fAttributeDeclNonNormalizedDefaultValue[chunk][index] = null;
      this.fAttributeDeclNextAttributeDeclIndex[chunk][index] = -1;
      return this.fAttributeDeclCount++;
   }

   protected void setAttributeDecl(int elementDeclIndex, int attributeDeclIndex, XMLAttributeDecl attributeDecl) {
      int attrChunk = attributeDeclIndex >> 8;
      int attrIndex = attributeDeclIndex & 255;
      this.fAttributeDeclName[attrChunk][attrIndex].setValues(attributeDecl.name);
      this.fAttributeDeclType[attrChunk][attrIndex] = attributeDecl.simpleType.type;
      if (attributeDecl.simpleType.list) {
         short[] var10000 = this.fAttributeDeclType[attrChunk];
         var10000[attrIndex] = (short)(var10000[attrIndex] | 128);
      }

      this.fAttributeDeclEnumeration[attrChunk][attrIndex] = attributeDecl.simpleType.enumeration;
      this.fAttributeDeclDefaultType[attrChunk][attrIndex] = attributeDecl.simpleType.defaultType;
      this.fAttributeDeclDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.defaultValue;
      this.fAttributeDeclNonNormalizedDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.nonNormalizedDefaultValue;
      int elemChunk = elementDeclIndex >> 8;
      int elemIndex = elementDeclIndex & 255;

      int index;
      for(index = this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex]; index != -1 && index != attributeDeclIndex; index = this.fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex]) {
         attrChunk = index >> 8;
         attrIndex = index & 255;
      }

      if (index == -1) {
         if (this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] == -1) {
            this.fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
         } else {
            index = this.fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex];
            attrChunk = index >> 8;
            attrIndex = index & 255;
            this.fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex] = attributeDeclIndex;
         }

         this.fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
      }

   }

   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augs) throws XNIException {
      XMLNotationDecl notationDecl = new XMLNotationDecl();
      notationDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(), identifier.getBaseSystemId());
      this.notationDecls.add(notationDecl);
   }

   public List getNotationDecls() {
      return this.notationDecls;
   }

   private void printAttribute(int attributeDeclIndex) {
      XMLAttributeDecl attributeDecl = new XMLAttributeDecl();
      if (this.getAttributeDecl(attributeDeclIndex, attributeDecl)) {
         System.out.print(" { ");
         System.out.print(attributeDecl.name.localpart);
         System.out.print(" }");
      }

   }

   private void ensureElementDeclCapacity(int chunk) {
      if (chunk >= this.fElementDeclName.length) {
         this.fElementDeclName = resize(this.fElementDeclName, this.fElementDeclName.length * 2);
         this.fElementDeclType = resize(this.fElementDeclType, this.fElementDeclType.length * 2);
         this.fElementDeclFirstAttributeDeclIndex = resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
         this.fElementDeclLastAttributeDeclIndex = resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
      } else if (this.fElementDeclName[chunk] != null) {
         return;
      }

      this.fElementDeclName[chunk] = new QName[256];
      this.fElementDeclType[chunk] = new short[256];
      this.fElementDeclFirstAttributeDeclIndex[chunk] = new int[256];
      this.fElementDeclLastAttributeDeclIndex[chunk] = new int[256];
   }

   private void ensureAttributeDeclCapacity(int chunk) {
      if (chunk >= this.fAttributeDeclName.length) {
         this.fAttributeDeclName = resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
         this.fAttributeDeclType = resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
         this.fAttributeDeclEnumeration = resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
         this.fAttributeDeclDefaultType = resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
         this.fAttributeDeclDefaultValue = resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
         this.fAttributeDeclNonNormalizedDefaultValue = resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
         this.fAttributeDeclNextAttributeDeclIndex = resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
      } else if (this.fAttributeDeclName[chunk] != null) {
         return;
      }

      this.fAttributeDeclName[chunk] = new QName[256];
      this.fAttributeDeclType[chunk] = new short[256];
      this.fAttributeDeclEnumeration[chunk] = new String[256][];
      this.fAttributeDeclDefaultType[chunk] = new short[256];
      this.fAttributeDeclDefaultValue[chunk] = new String[256];
      this.fAttributeDeclNonNormalizedDefaultValue[chunk] = new String[256];
      this.fAttributeDeclNextAttributeDeclIndex[chunk] = new int[256];
   }

   private static short[][] resize(short[][] array, int newsize) {
      short[][] newarray = new short[newsize][];
      System.arraycopy(array, 0, newarray, 0, array.length);
      return newarray;
   }

   private static int[][] resize(int[][] array, int newsize) {
      int[][] newarray = new int[newsize][];
      System.arraycopy(array, 0, newarray, 0, array.length);
      return newarray;
   }

   private static QName[][] resize(QName[][] array, int newsize) {
      QName[][] newarray = new QName[newsize][];
      System.arraycopy(array, 0, newarray, 0, array.length);
      return newarray;
   }

   private static String[][] resize(String[][] array, int newsize) {
      String[][] newarray = new String[newsize][];
      System.arraycopy(array, 0, newarray, 0, array.length);
      return newarray;
   }

   private static String[][][] resize(String[][][] array, int newsize) {
      String[][][] newarray = new String[newsize][][];
      System.arraycopy(array, 0, newarray, 0, array.length);
      return newarray;
   }

   private boolean normalizeDefaultAttrValue(XMLString value) {
      int oldLength = value.length;
      boolean skipSpace = true;
      int current = value.offset;
      int end = value.offset + value.length;

      for(int i = value.offset; i < end; ++i) {
         if (value.ch[i] == ' ') {
            if (!skipSpace) {
               value.ch[current++] = ' ';
               skipSpace = true;
            }
         } else {
            if (current != i) {
               value.ch[current] = value.ch[i];
            }

            ++current;
            skipSpace = false;
         }
      }

      if (current != end) {
         if (skipSpace) {
            --current;
         }

         value.length = current - value.offset;
         return true;
      } else {
         return false;
      }
   }

   public void endDTD(Augmentations augs) throws XNIException {
   }
}
