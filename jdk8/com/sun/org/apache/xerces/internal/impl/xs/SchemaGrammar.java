package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.util.ObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.ref.SoftReference;
import java.util.Vector;
import org.xml.sax.SAXException;

public class SchemaGrammar implements XSGrammar, XSNamespaceItem {
   String fTargetNamespace;
   SymbolHash fGlobalAttrDecls;
   SymbolHash fGlobalAttrGrpDecls;
   SymbolHash fGlobalElemDecls;
   SymbolHash fGlobalGroupDecls;
   SymbolHash fGlobalNotationDecls;
   SymbolHash fGlobalIDConstraintDecls;
   SymbolHash fGlobalTypeDecls;
   SymbolHash fGlobalAttrDeclsExt;
   SymbolHash fGlobalAttrGrpDeclsExt;
   SymbolHash fGlobalElemDeclsExt;
   SymbolHash fGlobalGroupDeclsExt;
   SymbolHash fGlobalNotationDeclsExt;
   SymbolHash fGlobalIDConstraintDeclsExt;
   SymbolHash fGlobalTypeDeclsExt;
   SymbolHash fAllGlobalElemDecls;
   XSDDescription fGrammarDescription = null;
   XSAnnotationImpl[] fAnnotations = null;
   int fNumAnnotations;
   private SymbolTable fSymbolTable = null;
   private SoftReference fSAXParser = null;
   private SoftReference fDOMParser = null;
   private boolean fIsImmutable = false;
   private static final int BASICSET_COUNT = 29;
   private static final int FULLSET_COUNT = 46;
   private static final int GRAMMAR_XS = 1;
   private static final int GRAMMAR_XSI = 2;
   Vector fImported = null;
   private static final int INITIAL_SIZE = 16;
   private static final int INC_SIZE = 16;
   private int fCTCount = 0;
   private XSComplexTypeDecl[] fComplexTypeDecls = new XSComplexTypeDecl[16];
   private SimpleLocator[] fCTLocators = new SimpleLocator[16];
   private static final int REDEFINED_GROUP_INIT_SIZE = 2;
   private int fRGCount = 0;
   private XSGroupDecl[] fRedefinedGroupDecls = new XSGroupDecl[2];
   private SimpleLocator[] fRGLocators = new SimpleLocator[1];
   boolean fFullChecked = false;
   private int fSubGroupCount = 0;
   private XSElementDecl[] fSubGroups = new XSElementDecl[16];
   public static final XSComplexTypeDecl fAnyType = new SchemaGrammar.XSAnyType();
   public static final SchemaGrammar.BuiltinSchemaGrammar SG_SchemaNS = new SchemaGrammar.BuiltinSchemaGrammar(1, (short)1);
   private static final SchemaGrammar.BuiltinSchemaGrammar SG_SchemaNSExtended = new SchemaGrammar.BuiltinSchemaGrammar(1, (short)2);
   public static final XSSimpleType fAnySimpleType;
   public static final SchemaGrammar.BuiltinSchemaGrammar SG_XSI;
   private static final short MAX_COMP_IDX = 16;
   private static final boolean[] GLOBAL_COMP;
   private XSNamedMap[] fComponents = null;
   private ObjectList[] fComponentsExt = null;
   private Vector fDocuments = null;
   private Vector fLocations = null;

   protected SchemaGrammar() {
   }

   public SchemaGrammar(String targetNamespace, XSDDescription grammarDesc, SymbolTable symbolTable) {
      this.fTargetNamespace = targetNamespace;
      this.fGrammarDescription = grammarDesc;
      this.fSymbolTable = symbolTable;
      this.fGlobalAttrDecls = new SymbolHash();
      this.fGlobalAttrGrpDecls = new SymbolHash();
      this.fGlobalElemDecls = new SymbolHash();
      this.fGlobalGroupDecls = new SymbolHash();
      this.fGlobalNotationDecls = new SymbolHash();
      this.fGlobalIDConstraintDecls = new SymbolHash();
      this.fGlobalAttrDeclsExt = new SymbolHash();
      this.fGlobalAttrGrpDeclsExt = new SymbolHash();
      this.fGlobalElemDeclsExt = new SymbolHash();
      this.fGlobalGroupDeclsExt = new SymbolHash();
      this.fGlobalNotationDeclsExt = new SymbolHash();
      this.fGlobalIDConstraintDeclsExt = new SymbolHash();
      this.fGlobalTypeDeclsExt = new SymbolHash();
      this.fAllGlobalElemDecls = new SymbolHash();
      if (this.fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
         this.fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls.makeClone();
      } else {
         this.fGlobalTypeDecls = new SymbolHash();
      }

   }

   public SchemaGrammar(SchemaGrammar grammar) {
      this.fTargetNamespace = grammar.fTargetNamespace;
      this.fGrammarDescription = grammar.fGrammarDescription.makeClone();
      this.fSymbolTable = grammar.fSymbolTable;
      this.fGlobalAttrDecls = grammar.fGlobalAttrDecls.makeClone();
      this.fGlobalAttrGrpDecls = grammar.fGlobalAttrGrpDecls.makeClone();
      this.fGlobalElemDecls = grammar.fGlobalElemDecls.makeClone();
      this.fGlobalGroupDecls = grammar.fGlobalGroupDecls.makeClone();
      this.fGlobalNotationDecls = grammar.fGlobalNotationDecls.makeClone();
      this.fGlobalIDConstraintDecls = grammar.fGlobalIDConstraintDecls.makeClone();
      this.fGlobalTypeDecls = grammar.fGlobalTypeDecls.makeClone();
      this.fGlobalAttrDeclsExt = grammar.fGlobalAttrDeclsExt.makeClone();
      this.fGlobalAttrGrpDeclsExt = grammar.fGlobalAttrGrpDeclsExt.makeClone();
      this.fGlobalElemDeclsExt = grammar.fGlobalElemDeclsExt.makeClone();
      this.fGlobalGroupDeclsExt = grammar.fGlobalGroupDeclsExt.makeClone();
      this.fGlobalNotationDeclsExt = grammar.fGlobalNotationDeclsExt.makeClone();
      this.fGlobalIDConstraintDeclsExt = grammar.fGlobalIDConstraintDeclsExt.makeClone();
      this.fGlobalTypeDeclsExt = grammar.fGlobalTypeDeclsExt.makeClone();
      this.fAllGlobalElemDecls = grammar.fAllGlobalElemDecls.makeClone();
      this.fNumAnnotations = grammar.fNumAnnotations;
      if (this.fNumAnnotations > 0) {
         this.fAnnotations = new XSAnnotationImpl[grammar.fAnnotations.length];
         System.arraycopy(grammar.fAnnotations, 0, this.fAnnotations, 0, this.fNumAnnotations);
      }

      this.fSubGroupCount = grammar.fSubGroupCount;
      if (this.fSubGroupCount > 0) {
         this.fSubGroups = new XSElementDecl[grammar.fSubGroups.length];
         System.arraycopy(grammar.fSubGroups, 0, this.fSubGroups, 0, this.fSubGroupCount);
      }

      this.fCTCount = grammar.fCTCount;
      if (this.fCTCount > 0) {
         this.fComplexTypeDecls = new XSComplexTypeDecl[grammar.fComplexTypeDecls.length];
         this.fCTLocators = new SimpleLocator[grammar.fCTLocators.length];
         System.arraycopy(grammar.fComplexTypeDecls, 0, this.fComplexTypeDecls, 0, this.fCTCount);
         System.arraycopy(grammar.fCTLocators, 0, this.fCTLocators, 0, this.fCTCount);
      }

      this.fRGCount = grammar.fRGCount;
      if (this.fRGCount > 0) {
         this.fRedefinedGroupDecls = new XSGroupDecl[grammar.fRedefinedGroupDecls.length];
         this.fRGLocators = new SimpleLocator[grammar.fRGLocators.length];
         System.arraycopy(grammar.fRedefinedGroupDecls, 0, this.fRedefinedGroupDecls, 0, this.fRGCount);
         System.arraycopy(grammar.fRGLocators, 0, this.fRGLocators, 0, this.fRGCount);
      }

      int k;
      if (grammar.fImported != null) {
         this.fImported = new Vector();

         for(k = 0; k < grammar.fImported.size(); ++k) {
            this.fImported.add(grammar.fImported.elementAt(k));
         }
      }

      if (grammar.fLocations != null) {
         for(k = 0; k < grammar.fLocations.size(); ++k) {
            this.addDocument((Object)null, (String)grammar.fLocations.elementAt(k));
         }
      }

   }

   public XMLGrammarDescription getGrammarDescription() {
      return this.fGrammarDescription;
   }

   public boolean isNamespaceAware() {
      return true;
   }

   public void setImportedGrammars(Vector importedGrammars) {
      this.fImported = importedGrammars;
   }

   public Vector getImportedGrammars() {
      return this.fImported;
   }

   public final String getTargetNamespace() {
      return this.fTargetNamespace;
   }

   public void addGlobalAttributeDecl(XSAttributeDecl decl) {
      this.fGlobalAttrDecls.put(decl.fName, decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalAttributeDecl(XSAttributeDecl decl, String location) {
      this.fGlobalAttrDeclsExt.put((location != null ? location : "") + "," + decl.fName, decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
      this.fGlobalAttrGrpDecls.put(decl.fName, decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
      this.fGlobalAttrGrpDeclsExt.put((location != null ? location : "") + "," + decl.fName, decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalElementDeclAll(XSElementDecl decl) {
      if (this.fAllGlobalElemDecls.get(decl) == null) {
         this.fAllGlobalElemDecls.put(decl, decl);
         if (decl.fSubGroup != null) {
            if (this.fSubGroupCount == this.fSubGroups.length) {
               this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount + 16);
            }

            this.fSubGroups[this.fSubGroupCount++] = decl;
         }
      }

   }

   public void addGlobalElementDecl(XSElementDecl decl) {
      this.fGlobalElemDecls.put(decl.fName, decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalElementDecl(XSElementDecl decl, String location) {
      this.fGlobalElemDeclsExt.put((location != null ? location : "") + "," + decl.fName, decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalGroupDecl(XSGroupDecl decl) {
      this.fGlobalGroupDecls.put(decl.fName, decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
      this.fGlobalGroupDeclsExt.put((location != null ? location : "") + "," + decl.fName, decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalNotationDecl(XSNotationDecl decl) {
      this.fGlobalNotationDecls.put(decl.fName, decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
      this.fGlobalNotationDeclsExt.put((location != null ? location : "") + "," + decl.fName, decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalTypeDecl(XSTypeDefinition decl) {
      this.fGlobalTypeDecls.put(decl.getName(), decl);
      if (decl instanceof XSComplexTypeDecl) {
         ((XSComplexTypeDecl)decl).setNamespaceItem(this);
      } else if (decl instanceof XSSimpleTypeDecl) {
         ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
      }

   }

   public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
      this.fGlobalTypeDeclsExt.put((location != null ? location : "") + "," + decl.getName(), decl);
      if (decl.getNamespaceItem() == null) {
         if (decl instanceof XSComplexTypeDecl) {
            ((XSComplexTypeDecl)decl).setNamespaceItem(this);
         } else if (decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
         }
      }

   }

   public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
      this.fGlobalTypeDecls.put(decl.getName(), decl);
      decl.setNamespaceItem(this);
   }

   public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
      this.fGlobalTypeDeclsExt.put((location != null ? location : "") + "," + decl.getName(), decl);
      if (decl.getNamespaceItem() == null) {
         decl.setNamespaceItem(this);
      }

   }

   public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
      this.fGlobalTypeDecls.put(decl.getName(), decl);
      if (decl instanceof XSSimpleTypeDecl) {
         ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
      }

   }

   public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
      this.fGlobalTypeDeclsExt.put((location != null ? location : "") + "," + decl.getName(), decl);
      if (decl.getNamespaceItem() == null && decl instanceof XSSimpleTypeDecl) {
         ((XSSimpleTypeDecl)decl).setNamespaceItem(this);
      }

   }

   public final void addIDConstraintDecl(XSElementDecl elmDecl, IdentityConstraint decl) {
      elmDecl.addIDConstraint(decl);
      this.fGlobalIDConstraintDecls.put(decl.getIdentityConstraintName(), decl);
   }

   public final void addIDConstraintDecl(XSElementDecl elmDecl, IdentityConstraint decl, String location) {
      this.fGlobalIDConstraintDeclsExt.put((location != null ? location : "") + "," + decl.getIdentityConstraintName(), decl);
   }

   public final XSAttributeDecl getGlobalAttributeDecl(String declName) {
      return (XSAttributeDecl)this.fGlobalAttrDecls.get(declName);
   }

   public final XSAttributeDecl getGlobalAttributeDecl(String declName, String location) {
      return (XSAttributeDecl)this.fGlobalAttrDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String declName) {
      return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(declName);
   }

   public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String declName, String location) {
      return (XSAttributeGroupDecl)this.fGlobalAttrGrpDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final XSElementDecl getGlobalElementDecl(String declName) {
      return (XSElementDecl)this.fGlobalElemDecls.get(declName);
   }

   public final XSElementDecl getGlobalElementDecl(String declName, String location) {
      return (XSElementDecl)this.fGlobalElemDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final XSGroupDecl getGlobalGroupDecl(String declName) {
      return (XSGroupDecl)this.fGlobalGroupDecls.get(declName);
   }

   public final XSGroupDecl getGlobalGroupDecl(String declName, String location) {
      return (XSGroupDecl)this.fGlobalGroupDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final XSNotationDecl getGlobalNotationDecl(String declName) {
      return (XSNotationDecl)this.fGlobalNotationDecls.get(declName);
   }

   public final XSNotationDecl getGlobalNotationDecl(String declName, String location) {
      return (XSNotationDecl)this.fGlobalNotationDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final XSTypeDefinition getGlobalTypeDecl(String declName) {
      return (XSTypeDefinition)this.fGlobalTypeDecls.get(declName);
   }

   public final XSTypeDefinition getGlobalTypeDecl(String declName, String location) {
      return (XSTypeDefinition)this.fGlobalTypeDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final IdentityConstraint getIDConstraintDecl(String declName) {
      return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(declName);
   }

   public final IdentityConstraint getIDConstraintDecl(String declName, String location) {
      return (IdentityConstraint)this.fGlobalIDConstraintDeclsExt.get((location != null ? location : "") + "," + declName);
   }

   public final boolean hasIDConstraints() {
      return this.fGlobalIDConstraintDecls.getLength() > 0;
   }

   public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
      if (this.fCTCount == this.fComplexTypeDecls.length) {
         this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount + 16);
         this.fCTLocators = resize(this.fCTLocators, this.fCTCount + 16);
      }

      this.fCTLocators[this.fCTCount] = locator;
      this.fComplexTypeDecls[this.fCTCount++] = decl;
   }

   public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
      if (this.fRGCount == this.fRedefinedGroupDecls.length) {
         this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount << 1);
         this.fRGLocators = resize(this.fRGLocators, this.fRGCount);
      }

      this.fRGLocators[this.fRGCount / 2] = locator;
      this.fRedefinedGroupDecls[this.fRGCount++] = derived;
      this.fRedefinedGroupDecls[this.fRGCount++] = base;
   }

   final XSComplexTypeDecl[] getUncheckedComplexTypeDecls() {
      if (this.fCTCount < this.fComplexTypeDecls.length) {
         this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
         this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
      }

      return this.fComplexTypeDecls;
   }

   final SimpleLocator[] getUncheckedCTLocators() {
      if (this.fCTCount < this.fCTLocators.length) {
         this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
         this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
      }

      return this.fCTLocators;
   }

   final XSGroupDecl[] getRedefinedGroupDecls() {
      if (this.fRGCount < this.fRedefinedGroupDecls.length) {
         this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
         this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
      }

      return this.fRedefinedGroupDecls;
   }

   final SimpleLocator[] getRGLocators() {
      if (this.fRGCount < this.fRedefinedGroupDecls.length) {
         this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
         this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
      }

      return this.fRGLocators;
   }

   final void setUncheckedTypeNum(int newSize) {
      this.fCTCount = newSize;
      this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
      this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
   }

   final XSElementDecl[] getSubstitutionGroups() {
      if (this.fSubGroupCount < this.fSubGroups.length) {
         this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount);
      }

      return this.fSubGroups;
   }

   public static SchemaGrammar getS4SGrammar(short schemaVersion) {
      return schemaVersion == 1 ? SG_SchemaNS : SG_SchemaNSExtended;
   }

   static final XSComplexTypeDecl[] resize(XSComplexTypeDecl[] oldArray, int newSize) {
      XSComplexTypeDecl[] newArray = new XSComplexTypeDecl[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   static final XSGroupDecl[] resize(XSGroupDecl[] oldArray, int newSize) {
      XSGroupDecl[] newArray = new XSGroupDecl[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   static final XSElementDecl[] resize(XSElementDecl[] oldArray, int newSize) {
      XSElementDecl[] newArray = new XSElementDecl[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   static final SimpleLocator[] resize(SimpleLocator[] oldArray, int newSize) {
      SimpleLocator[] newArray = new SimpleLocator[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   public synchronized void addDocument(Object document, String location) {
      if (this.fDocuments == null) {
         this.fDocuments = new Vector();
         this.fLocations = new Vector();
      }

      this.fDocuments.addElement(document);
      this.fLocations.addElement(location);
   }

   public synchronized void removeDocument(int index) {
      if (this.fDocuments != null && index >= 0 && index < this.fDocuments.size()) {
         this.fDocuments.removeElementAt(index);
         this.fLocations.removeElementAt(index);
      }

   }

   public String getSchemaNamespace() {
      return this.fTargetNamespace;
   }

   synchronized DOMParser getDOMParser() {
      if (this.fDOMParser != null) {
         DOMParser parser = (DOMParser)this.fDOMParser.get();
         if (parser != null) {
            return parser;
         }
      }

      XML11Configuration config = new XML11Configuration(this.fSymbolTable);
      config.setFeature("http://xml.org/sax/features/namespaces", true);
      config.setFeature("http://xml.org/sax/features/validation", false);
      DOMParser parser = new DOMParser(config);

      try {
         parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
      } catch (SAXException var4) {
      }

      this.fDOMParser = new SoftReference(parser);
      return parser;
   }

   synchronized SAXParser getSAXParser() {
      if (this.fSAXParser != null) {
         SAXParser parser = (SAXParser)this.fSAXParser.get();
         if (parser != null) {
            return parser;
         }
      }

      XML11Configuration config = new XML11Configuration(this.fSymbolTable);
      config.setFeature("http://xml.org/sax/features/namespaces", true);
      config.setFeature("http://xml.org/sax/features/validation", false);
      SAXParser parser = new SAXParser(config);
      this.fSAXParser = new SoftReference(parser);
      return parser;
   }

   public synchronized XSNamedMap getComponents(short objectType) {
      if (objectType > 0 && objectType <= 16 && GLOBAL_COMP[objectType]) {
         if (this.fComponents == null) {
            this.fComponents = new XSNamedMap[17];
         }

         if (this.fComponents[objectType] == null) {
            SymbolHash table = null;
            switch(objectType) {
            case 1:
               table = this.fGlobalAttrDecls;
               break;
            case 2:
               table = this.fGlobalElemDecls;
               break;
            case 3:
            case 15:
            case 16:
               table = this.fGlobalTypeDecls;
            case 4:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            default:
               break;
            case 5:
               table = this.fGlobalAttrGrpDecls;
               break;
            case 6:
               table = this.fGlobalGroupDecls;
               break;
            case 11:
               table = this.fGlobalNotationDecls;
            }

            if (objectType != 15 && objectType != 16) {
               this.fComponents[objectType] = new XSNamedMapImpl(this.fTargetNamespace, table);
            } else {
               this.fComponents[objectType] = new XSNamedMap4Types(this.fTargetNamespace, table, objectType);
            }
         }

         return this.fComponents[objectType];
      } else {
         return XSNamedMapImpl.EMPTY_MAP;
      }
   }

   public synchronized ObjectList getComponentsExt(short objectType) {
      if (objectType > 0 && objectType <= 16 && GLOBAL_COMP[objectType]) {
         if (this.fComponentsExt == null) {
            this.fComponentsExt = new ObjectList[17];
         }

         if (this.fComponentsExt[objectType] == null) {
            SymbolHash table = null;
            switch(objectType) {
            case 1:
               table = this.fGlobalAttrDeclsExt;
               break;
            case 2:
               table = this.fGlobalElemDeclsExt;
               break;
            case 3:
            case 15:
            case 16:
               table = this.fGlobalTypeDeclsExt;
            case 4:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            default:
               break;
            case 5:
               table = this.fGlobalAttrGrpDeclsExt;
               break;
            case 6:
               table = this.fGlobalGroupDeclsExt;
               break;
            case 11:
               table = this.fGlobalNotationDeclsExt;
            }

            Object[] entries = table.getEntries();
            this.fComponentsExt[objectType] = new ObjectListImpl(entries, entries.length);
         }

         return this.fComponentsExt[objectType];
      } else {
         return ObjectListImpl.EMPTY_LIST;
      }
   }

   public synchronized void resetComponents() {
      this.fComponents = null;
      this.fComponentsExt = null;
   }

   public XSTypeDefinition getTypeDefinition(String name) {
      return this.getGlobalTypeDecl(name);
   }

   public XSAttributeDeclaration getAttributeDeclaration(String name) {
      return this.getGlobalAttributeDecl(name);
   }

   public XSElementDeclaration getElementDeclaration(String name) {
      return this.getGlobalElementDecl(name);
   }

   public XSAttributeGroupDefinition getAttributeGroup(String name) {
      return this.getGlobalAttributeGroupDecl(name);
   }

   public XSModelGroupDefinition getModelGroupDefinition(String name) {
      return this.getGlobalGroupDecl(name);
   }

   public XSNotationDeclaration getNotationDeclaration(String name) {
      return this.getGlobalNotationDecl(name);
   }

   public StringList getDocumentLocations() {
      return new StringListImpl(this.fLocations);
   }

   public XSModel toXSModel() {
      return new XSModelImpl(new SchemaGrammar[]{this});
   }

   public XSModel toXSModel(XSGrammar[] grammars) {
      if (grammars != null && grammars.length != 0) {
         int len = grammars.length;
         boolean hasSelf = false;

         for(int i = 0; i < len; ++i) {
            if (grammars[i] == this) {
               hasSelf = true;
               break;
            }
         }

         SchemaGrammar[] gs = new SchemaGrammar[hasSelf ? len : len + 1];

         for(int i = 0; i < len; ++i) {
            gs[i] = (SchemaGrammar)grammars[i];
         }

         if (!hasSelf) {
            gs[len] = this;
         }

         return new XSModelImpl(gs);
      } else {
         return this.toXSModel();
      }
   }

   public XSObjectList getAnnotations() {
      return this.fNumAnnotations == 0 ? XSObjectListImpl.EMPTY_LIST : new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
   }

   public void addAnnotation(XSAnnotationImpl annotation) {
      if (annotation != null) {
         if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
         } else if (this.fNumAnnotations == this.fAnnotations.length) {
            XSAnnotationImpl[] newArray = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, newArray, 0, this.fNumAnnotations);
            this.fAnnotations = newArray;
         }

         this.fAnnotations[this.fNumAnnotations++] = annotation;
      }
   }

   public void setImmutable(boolean isImmutable) {
      this.fIsImmutable = isImmutable;
   }

   public boolean isImmutable() {
      return this.fIsImmutable;
   }

   static {
      fAnySimpleType = (XSSimpleType)SG_SchemaNS.getGlobalTypeDecl("anySimpleType");
      SG_XSI = new SchemaGrammar.BuiltinSchemaGrammar(2, (short)1);
      GLOBAL_COMP = new boolean[]{false, true, true, true, false, true, true, false, false, false, false, true, false, false, false, true, true};
   }

   private static class BuiltinAttrDecl extends XSAttributeDecl {
      public BuiltinAttrDecl(String name, String tns, XSSimpleType type, short scope) {
         this.fName = name;
         super.fTargetNamespace = tns;
         this.fType = type;
         this.fScope = scope;
      }

      public void setValues(String name, String targetNamespace, XSSimpleType simpleType, short constraintType, short scope, ValidatedInfo valInfo, XSComplexTypeDecl enclosingCT) {
      }

      public void reset() {
      }

      public XSAnnotation getAnnotation() {
         return null;
      }

      public XSNamespaceItem getNamespaceItem() {
         return SchemaGrammar.SG_XSI;
      }
   }

   private static class XSAnyType extends XSComplexTypeDecl {
      public XSAnyType() {
         this.fName = "anyType";
         super.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
         this.fBaseType = this;
         this.fDerivedBy = 2;
         this.fContentType = 3;
         this.fParticle = null;
         this.fAttrGrp = null;
      }

      public void setValues(String name, String targetNamespace, XSTypeDefinition baseType, short derivedBy, short schemaFinal, short block, short contentType, boolean isAbstract, XSAttributeGroupDecl attrGrp, XSSimpleType simpleType, XSParticleDecl particle) {
      }

      public void setName(String name) {
      }

      public void setIsAbstractType() {
      }

      public void setContainsTypeID() {
      }

      public void setIsAnonymous() {
      }

      public void reset() {
      }

      public XSObjectList getAttributeUses() {
         return XSObjectListImpl.EMPTY_LIST;
      }

      public XSAttributeGroupDecl getAttrGrp() {
         XSWildcardDecl wildcard = new XSWildcardDecl();
         wildcard.fProcessContents = 3;
         XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();
         attrGrp.fAttributeWC = wildcard;
         return attrGrp;
      }

      public XSWildcard getAttributeWildcard() {
         XSWildcardDecl wildcard = new XSWildcardDecl();
         wildcard.fProcessContents = 3;
         return wildcard;
      }

      public XSParticle getParticle() {
         XSWildcardDecl wildcard = new XSWildcardDecl();
         wildcard.fProcessContents = 3;
         XSParticleDecl particleW = new XSParticleDecl();
         particleW.fMinOccurs = 0;
         particleW.fMaxOccurs = -1;
         particleW.fType = 2;
         particleW.fValue = wildcard;
         XSModelGroupImpl group = new XSModelGroupImpl();
         group.fCompositor = 102;
         group.fParticleCount = 1;
         group.fParticles = new XSParticleDecl[1];
         group.fParticles[0] = particleW;
         XSParticleDecl particleG = new XSParticleDecl();
         particleG.fType = 3;
         particleG.fValue = group;
         return particleG;
      }

      public XSObjectList getAnnotations() {
         return XSObjectListImpl.EMPTY_LIST;
      }

      public XSNamespaceItem getNamespaceItem() {
         return SchemaGrammar.SG_SchemaNS;
      }
   }

   public static final class Schema4Annotations extends SchemaGrammar {
      public static final SchemaGrammar.Schema4Annotations INSTANCE = new SchemaGrammar.Schema4Annotations();

      private Schema4Annotations() {
         this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
         this.fGrammarDescription = new XSDDescription();
         this.fGrammarDescription.fContextType = 3;
         this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
         this.fGlobalAttrDecls = new SymbolHash(1);
         this.fGlobalAttrGrpDecls = new SymbolHash(1);
         this.fGlobalElemDecls = new SymbolHash(6);
         this.fGlobalGroupDecls = new SymbolHash(1);
         this.fGlobalNotationDecls = new SymbolHash(1);
         this.fGlobalIDConstraintDecls = new SymbolHash(1);
         this.fGlobalAttrDeclsExt = new SymbolHash(1);
         this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
         this.fGlobalElemDeclsExt = new SymbolHash(6);
         this.fGlobalGroupDeclsExt = new SymbolHash(1);
         this.fGlobalNotationDeclsExt = new SymbolHash(1);
         this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
         this.fGlobalTypeDeclsExt = new SymbolHash(1);
         this.fAllGlobalElemDecls = new SymbolHash(6);
         this.fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls;
         XSElementDecl annotationDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
         XSElementDecl documentationDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
         XSElementDecl appinfoDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);
         this.fGlobalElemDecls.put(annotationDecl.fName, annotationDecl);
         this.fGlobalElemDecls.put(documentationDecl.fName, documentationDecl);
         this.fGlobalElemDecls.put(appinfoDecl.fName, appinfoDecl);
         this.fGlobalElemDeclsExt.put("," + annotationDecl.fName, annotationDecl);
         this.fGlobalElemDeclsExt.put("," + documentationDecl.fName, documentationDecl);
         this.fGlobalElemDeclsExt.put("," + appinfoDecl.fName, appinfoDecl);
         this.fAllGlobalElemDecls.put(annotationDecl, annotationDecl);
         this.fAllGlobalElemDecls.put(documentationDecl, documentationDecl);
         this.fAllGlobalElemDecls.put(appinfoDecl, appinfoDecl);
         XSComplexTypeDecl annotationType = new XSComplexTypeDecl();
         XSComplexTypeDecl documentationType = new XSComplexTypeDecl();
         XSComplexTypeDecl appinfoType = new XSComplexTypeDecl();
         annotationDecl.fType = annotationType;
         documentationDecl.fType = documentationType;
         appinfoDecl.fType = appinfoType;
         XSAttributeGroupDecl annotationAttrs = new XSAttributeGroupDecl();
         XSAttributeGroupDecl documentationAttrs = new XSAttributeGroupDecl();
         XSAttributeGroupDecl appinfoAttrs = new XSAttributeGroupDecl();
         XSAttributeUseImpl annotationIDAttr = new XSAttributeUseImpl();
         annotationIDAttr.fAttrDecl = new XSAttributeDecl();
         annotationIDAttr.fAttrDecl.setValues(SchemaSymbols.ATT_ID, (String)null, (XSSimpleType)this.fGlobalTypeDecls.get("ID"), (short)0, (short)2, (ValidatedInfo)null, annotationType, (XSObjectList)null);
         annotationIDAttr.fUse = 0;
         annotationIDAttr.fConstraintType = 0;
         XSAttributeUseImpl documentationSourceAttr = new XSAttributeUseImpl();
         documentationSourceAttr.fAttrDecl = new XSAttributeDecl();
         documentationSourceAttr.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, (String)null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, (ValidatedInfo)null, documentationType, (XSObjectList)null);
         documentationSourceAttr.fUse = 0;
         documentationSourceAttr.fConstraintType = 0;
         XSAttributeUseImpl documentationLangAttr = new XSAttributeUseImpl();
         documentationLangAttr.fAttrDecl = new XSAttributeDecl();
         documentationLangAttr.fAttrDecl.setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType)this.fGlobalTypeDecls.get("language"), (short)0, (short)2, (ValidatedInfo)null, documentationType, (XSObjectList)null);
         documentationLangAttr.fUse = 0;
         documentationLangAttr.fConstraintType = 0;
         XSAttributeUseImpl appinfoSourceAttr = new XSAttributeUseImpl();
         appinfoSourceAttr.fAttrDecl = new XSAttributeDecl();
         appinfoSourceAttr.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, (String)null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, (ValidatedInfo)null, appinfoType, (XSObjectList)null);
         appinfoSourceAttr.fUse = 0;
         appinfoSourceAttr.fConstraintType = 0;
         XSWildcardDecl otherAttrs = new XSWildcardDecl();
         otherAttrs.fNamespaceList = new String[]{this.fTargetNamespace, null};
         otherAttrs.fType = 2;
         otherAttrs.fProcessContents = 3;
         annotationAttrs.addAttributeUse(annotationIDAttr);
         annotationAttrs.fAttributeWC = otherAttrs;
         documentationAttrs.addAttributeUse(documentationSourceAttr);
         documentationAttrs.addAttributeUse(documentationLangAttr);
         documentationAttrs.fAttributeWC = otherAttrs;
         appinfoAttrs.addAttributeUse(appinfoSourceAttr);
         appinfoAttrs.fAttributeWC = otherAttrs;
         XSParticleDecl annotationParticle = this.createUnboundedModelGroupParticle();
         XSModelGroupImpl annotationChoice = new XSModelGroupImpl();
         annotationChoice.fCompositor = 101;
         annotationChoice.fParticleCount = 2;
         annotationChoice.fParticles = new XSParticleDecl[2];
         annotationChoice.fParticles[0] = this.createChoiceElementParticle(appinfoDecl);
         annotationChoice.fParticles[1] = this.createChoiceElementParticle(documentationDecl);
         annotationParticle.fValue = annotationChoice;
         XSParticleDecl anyWCSequenceParticle = this.createUnboundedAnyWildcardSequenceParticle();
         annotationType.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)2, false, annotationAttrs, (XSSimpleType)null, annotationParticle, new XSObjectListImpl((XSObject[])null, 0));
         annotationType.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
         annotationType.setIsAnonymous();
         documentationType.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, documentationAttrs, (XSSimpleType)null, anyWCSequenceParticle, new XSObjectListImpl((XSObject[])null, 0));
         documentationType.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
         documentationType.setIsAnonymous();
         appinfoType.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, appinfoAttrs, (XSSimpleType)null, anyWCSequenceParticle, new XSObjectListImpl((XSObject[])null, 0));
         appinfoType.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
         appinfoType.setIsAnonymous();
      }

      public XMLGrammarDescription getGrammarDescription() {
         return this.fGrammarDescription.makeClone();
      }

      public void setImportedGrammars(Vector importedGrammars) {
      }

      public void addGlobalAttributeDecl(XSAttributeDecl decl) {
      }

      public void addGlobalAttributeDecl(XSAttributeGroupDecl decl, String location) {
      }

      public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
      }

      public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
      }

      public void addGlobalElementDecl(XSElementDecl decl) {
      }

      public void addGlobalElementDecl(XSElementDecl decl, String location) {
      }

      public void addGlobalElementDeclAll(XSElementDecl decl) {
      }

      public void addGlobalGroupDecl(XSGroupDecl decl) {
      }

      public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
      }

      public void addGlobalNotationDecl(XSNotationDecl decl) {
      }

      public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
      }

      public void addGlobalTypeDecl(XSTypeDefinition decl) {
      }

      public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
      }

      public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
      }

      public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
      }

      public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
      }

      public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
      }

      public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
      }

      public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
      }

      public synchronized void addDocument(Object document, String location) {
      }

      synchronized DOMParser getDOMParser() {
         return null;
      }

      synchronized SAXParser getSAXParser() {
         return null;
      }

      private XSElementDecl createAnnotationElementDecl(String localName) {
         XSElementDecl eDecl = new XSElementDecl();
         eDecl.fName = localName;
         eDecl.fTargetNamespace = this.fTargetNamespace;
         eDecl.setIsGlobal();
         eDecl.fBlock = 7;
         eDecl.setConstraintType((short)0);
         return eDecl;
      }

      private XSParticleDecl createUnboundedModelGroupParticle() {
         XSParticleDecl particle = new XSParticleDecl();
         particle.fMinOccurs = 0;
         particle.fMaxOccurs = -1;
         particle.fType = 3;
         return particle;
      }

      private XSParticleDecl createChoiceElementParticle(XSElementDecl ref) {
         XSParticleDecl particle = new XSParticleDecl();
         particle.fMinOccurs = 1;
         particle.fMaxOccurs = 1;
         particle.fType = 1;
         particle.fValue = ref;
         return particle;
      }

      private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
         XSParticleDecl particle = this.createUnboundedModelGroupParticle();
         XSModelGroupImpl sequence = new XSModelGroupImpl();
         sequence.fCompositor = 102;
         sequence.fParticleCount = 1;
         sequence.fParticles = new XSParticleDecl[1];
         sequence.fParticles[0] = this.createAnyLaxWildcardParticle();
         particle.fValue = sequence;
         return particle;
      }

      private XSParticleDecl createAnyLaxWildcardParticle() {
         XSParticleDecl particle = new XSParticleDecl();
         particle.fMinOccurs = 1;
         particle.fMaxOccurs = 1;
         particle.fType = 2;
         XSWildcardDecl anyWC = new XSWildcardDecl();
         anyWC.fNamespaceList = null;
         anyWC.fType = 1;
         anyWC.fProcessContents = 3;
         particle.fValue = anyWC;
         return particle;
      }
   }

   public static class BuiltinSchemaGrammar extends SchemaGrammar {
      private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl";

      public BuiltinSchemaGrammar(int grammar, short schemaVersion) {
         SchemaDVFactory schemaFactory;
         if (schemaVersion == 1) {
            schemaFactory = SchemaDVFactory.getInstance();
         } else {
            schemaFactory = SchemaDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl");
         }

         XSTypeDefinition[] typeDefinitions;
         if (grammar == 1) {
            this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fGrammarDescription = new XSDDescription();
            this.fGrammarDescription.fContextType = 3;
            this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
            this.fGlobalAttrDecls = new SymbolHash(1);
            this.fGlobalAttrGrpDecls = new SymbolHash(1);
            this.fGlobalElemDecls = new SymbolHash(1);
            this.fGlobalGroupDecls = new SymbolHash(1);
            this.fGlobalNotationDecls = new SymbolHash(1);
            this.fGlobalIDConstraintDecls = new SymbolHash(1);
            this.fGlobalAttrDeclsExt = new SymbolHash(1);
            this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
            this.fGlobalElemDeclsExt = new SymbolHash(1);
            this.fGlobalGroupDeclsExt = new SymbolHash(1);
            this.fGlobalNotationDeclsExt = new SymbolHash(1);
            this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
            this.fGlobalTypeDeclsExt = new SymbolHash(1);
            this.fAllGlobalElemDecls = new SymbolHash(1);
            this.fGlobalTypeDecls = schemaFactory.getBuiltInTypes();
            int length = this.fGlobalTypeDecls.getLength();
            typeDefinitions = new XSTypeDefinition[length];
            this.fGlobalTypeDecls.getValues(typeDefinitions, 0);

            for(int i = 0; i < length; ++i) {
               XSTypeDefinition xtd = typeDefinitions[i];
               if (xtd instanceof XSSimpleTypeDecl) {
                  ((XSSimpleTypeDecl)xtd).setNamespaceItem(this);
               }
            }

            this.fGlobalTypeDecls.put(fAnyType.getName(), fAnyType);
         } else if (grammar == 2) {
            this.fTargetNamespace = SchemaSymbols.URI_XSI;
            this.fGrammarDescription = new XSDDescription();
            this.fGrammarDescription.fContextType = 3;
            this.fGrammarDescription.setNamespace(SchemaSymbols.URI_XSI);
            this.fGlobalAttrGrpDecls = new SymbolHash(1);
            this.fGlobalElemDecls = new SymbolHash(1);
            this.fGlobalGroupDecls = new SymbolHash(1);
            this.fGlobalNotationDecls = new SymbolHash(1);
            this.fGlobalIDConstraintDecls = new SymbolHash(1);
            this.fGlobalTypeDecls = new SymbolHash(1);
            this.fGlobalAttrDeclsExt = new SymbolHash(1);
            this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
            this.fGlobalElemDeclsExt = new SymbolHash(1);
            this.fGlobalGroupDeclsExt = new SymbolHash(1);
            this.fGlobalNotationDeclsExt = new SymbolHash(1);
            this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
            this.fGlobalTypeDeclsExt = new SymbolHash(1);
            this.fAllGlobalElemDecls = new SymbolHash(1);
            this.fGlobalAttrDecls = new SymbolHash(8);
            String name = null;
            typeDefinitions = null;
            XSSimpleType type = null;
            short scope = 1;
            name = SchemaSymbols.XSI_TYPE;
            String tns = SchemaSymbols.URI_XSI;
            type = schemaFactory.getBuiltInType("QName");
            this.fGlobalAttrDecls.put(name, new SchemaGrammar.BuiltinAttrDecl(name, tns, type, scope));
            name = SchemaSymbols.XSI_NIL;
            tns = SchemaSymbols.URI_XSI;
            type = schemaFactory.getBuiltInType("boolean");
            this.fGlobalAttrDecls.put(name, new SchemaGrammar.BuiltinAttrDecl(name, tns, type, scope));
            XSSimpleType anyURI = schemaFactory.getBuiltInType("anyURI");
            name = SchemaSymbols.XSI_SCHEMALOCATION;
            tns = SchemaSymbols.URI_XSI;
            type = schemaFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, anyURI, (XSObjectList)null);
            if (type instanceof XSSimpleTypeDecl) {
               ((XSSimpleTypeDecl)type).setAnonymous(true);
            }

            this.fGlobalAttrDecls.put(name, new SchemaGrammar.BuiltinAttrDecl(name, tns, type, scope));
            name = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
            tns = SchemaSymbols.URI_XSI;
            this.fGlobalAttrDecls.put(name, new SchemaGrammar.BuiltinAttrDecl(name, tns, anyURI, scope));
         }

      }

      public XMLGrammarDescription getGrammarDescription() {
         return this.fGrammarDescription.makeClone();
      }

      public void setImportedGrammars(Vector importedGrammars) {
      }

      public void addGlobalAttributeDecl(XSAttributeDecl decl) {
      }

      public void addGlobalAttributeDecl(XSAttributeDecl decl, String location) {
      }

      public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
      }

      public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
      }

      public void addGlobalElementDecl(XSElementDecl decl) {
      }

      public void addGlobalElementDecl(XSElementDecl decl, String location) {
      }

      public void addGlobalElementDeclAll(XSElementDecl decl) {
      }

      public void addGlobalGroupDecl(XSGroupDecl decl) {
      }

      public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
      }

      public void addGlobalNotationDecl(XSNotationDecl decl) {
      }

      public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
      }

      public void addGlobalTypeDecl(XSTypeDefinition decl) {
      }

      public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
      }

      public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
      }

      public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
      }

      public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
      }

      public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
      }

      public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
      }

      public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
      }

      public synchronized void addDocument(Object document, String location) {
      }

      synchronized DOMParser getDOMParser() {
         return null;
      }

      synchronized SAXParser getSAXParser() {
         return null;
      }
   }
}
