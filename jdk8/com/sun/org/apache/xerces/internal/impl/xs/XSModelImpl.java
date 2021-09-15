package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItemList;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public final class XSModelImpl extends AbstractList implements XSModel, XSNamespaceItemList {
   private static final short MAX_COMP_IDX = 16;
   private static final boolean[] GLOBAL_COMP = new boolean[]{false, true, true, true, false, true, true, false, false, false, false, true, false, false, false, true, true};
   private final int fGrammarCount;
   private final String[] fNamespaces;
   private final SchemaGrammar[] fGrammarList;
   private final SymbolHash fGrammarMap;
   private final SymbolHash fSubGroupMap;
   private final XSNamedMap[] fGlobalComponents;
   private final XSNamedMap[][] fNSComponents;
   private final StringList fNamespacesList;
   private XSObjectList fAnnotations;
   private final boolean fHasIDC;

   public XSModelImpl(SchemaGrammar[] grammars) {
      this(grammars, (short)1);
   }

   public XSModelImpl(SchemaGrammar[] grammars, short s4sVersion) {
      this.fAnnotations = null;
      int len = grammars.length;
      int initialSize = Math.max(len + 1, 5);
      String[] namespaces = new String[initialSize];
      SchemaGrammar[] grammarList = new SchemaGrammar[initialSize];
      boolean hasS4S = false;

      SchemaGrammar sg2;
      for(int i = 0; i < len; ++i) {
         sg2 = grammars[i];
         String tns = sg2.getTargetNamespace();
         namespaces[i] = tns;
         grammarList[i] = sg2;
         if (tns == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            hasS4S = true;
         }
      }

      if (!hasS4S) {
         namespaces[len] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
         grammarList[len++] = SchemaGrammar.getS4SGrammar(s4sVersion);
      }

      int i;
      for(i = 0; i < len; ++i) {
         SchemaGrammar sg1 = grammarList[i];
         Vector gs = sg1.getImportedGrammars();

         for(int j = gs == null ? -1 : gs.size() - 1; j >= 0; --j) {
            sg2 = (SchemaGrammar)gs.elementAt(j);

            int k;
            for(k = 0; k < len && sg2 != grammarList[k]; ++k) {
            }

            if (k == len) {
               if (len == grammarList.length) {
                  String[] newSA = new String[len * 2];
                  System.arraycopy(namespaces, 0, newSA, 0, len);
                  namespaces = newSA;
                  SchemaGrammar[] newGA = new SchemaGrammar[len * 2];
                  System.arraycopy(grammarList, 0, newGA, 0, len);
                  grammarList = newGA;
               }

               namespaces[len] = sg2.getTargetNamespace();
               grammarList[len] = sg2;
               ++len;
            }
         }
      }

      this.fNamespaces = namespaces;
      this.fGrammarList = grammarList;
      boolean hasIDC = false;
      this.fGrammarMap = new SymbolHash(len * 2);

      for(i = 0; i < len; ++i) {
         this.fGrammarMap.put(null2EmptyString(this.fNamespaces[i]), this.fGrammarList[i]);
         if (this.fGrammarList[i].hasIDConstraints()) {
            hasIDC = true;
         }
      }

      this.fHasIDC = hasIDC;
      this.fGrammarCount = len;
      this.fGlobalComponents = new XSNamedMap[17];
      this.fNSComponents = new XSNamedMap[len][17];
      this.fNamespacesList = new StringListImpl(this.fNamespaces, this.fGrammarCount);
      this.fSubGroupMap = this.buildSubGroups();
   }

   private SymbolHash buildSubGroups_Org() {
      SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler((XSGrammarBucket)null);

      for(int i = 0; i < this.fGrammarCount; ++i) {
         sgHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
      }

      XSNamedMap elements = this.getComponents((short)2);
      int len = elements.getLength();
      SymbolHash subGroupMap = new SymbolHash(len * 2);

      for(int i = 0; i < len; ++i) {
         XSElementDecl head = (XSElementDecl)elements.item(i);
         XSElementDeclaration[] subGroup = sgHandler.getSubstitutionGroup(head);
         subGroupMap.put(head, subGroup.length > 0 ? new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
      }

      return subGroupMap;
   }

   private SymbolHash buildSubGroups() {
      SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler((XSGrammarBucket)null);

      for(int i = 0; i < this.fGrammarCount; ++i) {
         sgHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
      }

      XSObjectListImpl elements = this.getGlobalElements();
      int len = elements.getLength();
      SymbolHash subGroupMap = new SymbolHash(len * 2);

      for(int i = 0; i < len; ++i) {
         XSElementDecl head = (XSElementDecl)elements.item(i);
         XSElementDeclaration[] subGroup = sgHandler.getSubstitutionGroup(head);
         subGroupMap.put(head, subGroup.length > 0 ? new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
      }

      return subGroupMap;
   }

   private XSObjectListImpl getGlobalElements() {
      SymbolHash[] tables = new SymbolHash[this.fGrammarCount];
      int length = 0;

      for(int i = 0; i < this.fGrammarCount; ++i) {
         tables[i] = this.fGrammarList[i].fAllGlobalElemDecls;
         length += tables[i].getLength();
      }

      if (length == 0) {
         return XSObjectListImpl.EMPTY_LIST;
      } else {
         XSObject[] components = new XSObject[length];
         int start = 0;

         for(int i = 0; i < this.fGrammarCount; ++i) {
            tables[i].getValues(components, start);
            start += tables[i].getLength();
         }

         return new XSObjectListImpl(components, length);
      }
   }

   public StringList getNamespaces() {
      return this.fNamespacesList;
   }

   public XSNamespaceItemList getNamespaceItems() {
      return this;
   }

   public synchronized XSNamedMap getComponents(short objectType) {
      if (objectType > 0 && objectType <= 16 && GLOBAL_COMP[objectType]) {
         SymbolHash[] tables = new SymbolHash[this.fGrammarCount];
         if (this.fGlobalComponents[objectType] == null) {
            for(int i = 0; i < this.fGrammarCount; ++i) {
               switch(objectType) {
               case 1:
                  tables[i] = this.fGrammarList[i].fGlobalAttrDecls;
                  break;
               case 2:
                  tables[i] = this.fGrammarList[i].fGlobalElemDecls;
                  break;
               case 3:
               case 15:
               case 16:
                  tables[i] = this.fGrammarList[i].fGlobalTypeDecls;
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
                  tables[i] = this.fGrammarList[i].fGlobalAttrGrpDecls;
                  break;
               case 6:
                  tables[i] = this.fGrammarList[i].fGlobalGroupDecls;
                  break;
               case 11:
                  tables[i] = this.fGrammarList[i].fGlobalNotationDecls;
               }
            }

            if (objectType != 15 && objectType != 16) {
               this.fGlobalComponents[objectType] = new XSNamedMapImpl(this.fNamespaces, tables, this.fGrammarCount);
            } else {
               this.fGlobalComponents[objectType] = new XSNamedMap4Types(this.fNamespaces, tables, this.fGrammarCount, objectType);
            }
         }

         return this.fGlobalComponents[objectType];
      } else {
         return XSNamedMapImpl.EMPTY_MAP;
      }
   }

   public synchronized XSNamedMap getComponentsByNamespace(short objectType, String namespace) {
      if (objectType > 0 && objectType <= 16 && GLOBAL_COMP[objectType]) {
         int i = 0;
         if (namespace != null) {
            while(i < this.fGrammarCount && !namespace.equals(this.fNamespaces[i])) {
               ++i;
            }
         } else {
            while(i < this.fGrammarCount && this.fNamespaces[i] != null) {
               ++i;
            }
         }

         if (i == this.fGrammarCount) {
            return XSNamedMapImpl.EMPTY_MAP;
         } else {
            if (this.fNSComponents[i][objectType] == null) {
               SymbolHash table = null;
               switch(objectType) {
               case 1:
                  table = this.fGrammarList[i].fGlobalAttrDecls;
                  break;
               case 2:
                  table = this.fGrammarList[i].fGlobalElemDecls;
                  break;
               case 3:
               case 15:
               case 16:
                  table = this.fGrammarList[i].fGlobalTypeDecls;
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
                  table = this.fGrammarList[i].fGlobalAttrGrpDecls;
                  break;
               case 6:
                  table = this.fGrammarList[i].fGlobalGroupDecls;
                  break;
               case 11:
                  table = this.fGrammarList[i].fGlobalNotationDecls;
               }

               if (objectType != 15 && objectType != 16) {
                  this.fNSComponents[i][objectType] = new XSNamedMapImpl(namespace, table);
               } else {
                  this.fNSComponents[i][objectType] = new XSNamedMap4Types(namespace, table, objectType);
               }
            }

            return this.fNSComponents[i][objectType];
         }
      } else {
         return XSNamedMapImpl.EMPTY_MAP;
      }
   }

   public XSTypeDefinition getTypeDefinition(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSTypeDefinition)sg.fGlobalTypeDecls.get(name);
   }

   public XSTypeDefinition getTypeDefinition(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalTypeDecl(name, loc);
   }

   public XSAttributeDeclaration getAttributeDeclaration(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSAttributeDeclaration)sg.fGlobalAttrDecls.get(name);
   }

   public XSAttributeDeclaration getAttributeDeclaration(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalAttributeDecl(name, loc);
   }

   public XSElementDeclaration getElementDeclaration(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSElementDeclaration)sg.fGlobalElemDecls.get(name);
   }

   public XSElementDeclaration getElementDeclaration(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalElementDecl(name, loc);
   }

   public XSAttributeGroupDefinition getAttributeGroup(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSAttributeGroupDefinition)sg.fGlobalAttrGrpDecls.get(name);
   }

   public XSAttributeGroupDefinition getAttributeGroup(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalAttributeGroupDecl(name, loc);
   }

   public XSModelGroupDefinition getModelGroupDefinition(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSModelGroupDefinition)sg.fGlobalGroupDecls.get(name);
   }

   public XSModelGroupDefinition getModelGroupDefinition(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalGroupDecl(name, loc);
   }

   public XSNotationDeclaration getNotationDeclaration(String name, String namespace) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : (XSNotationDeclaration)sg.fGlobalNotationDecls.get(name);
   }

   public XSNotationDeclaration getNotationDeclaration(String name, String namespace, String loc) {
      SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
      return sg == null ? null : sg.getGlobalNotationDecl(name, loc);
   }

   public synchronized XSObjectList getAnnotations() {
      if (this.fAnnotations != null) {
         return this.fAnnotations;
      } else {
         int totalAnnotations = 0;

         for(int i = 0; i < this.fGrammarCount; ++i) {
            totalAnnotations += this.fGrammarList[i].fNumAnnotations;
         }

         if (totalAnnotations == 0) {
            this.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            return this.fAnnotations;
         } else {
            XSAnnotationImpl[] annotations = new XSAnnotationImpl[totalAnnotations];
            int currPos = 0;

            for(int i = 0; i < this.fGrammarCount; ++i) {
               SchemaGrammar currGrammar = this.fGrammarList[i];
               if (currGrammar.fNumAnnotations > 0) {
                  System.arraycopy(currGrammar.fAnnotations, 0, annotations, currPos, currGrammar.fNumAnnotations);
                  currPos += currGrammar.fNumAnnotations;
               }
            }

            this.fAnnotations = new XSObjectListImpl(annotations, annotations.length);
            return this.fAnnotations;
         }
      }
   }

   private static final String null2EmptyString(String str) {
      return str == null ? XMLSymbols.EMPTY_STRING : str;
   }

   public boolean hasIDConstraints() {
      return this.fHasIDC;
   }

   public XSObjectList getSubstitutionGroup(XSElementDeclaration head) {
      return (XSObjectList)this.fSubGroupMap.get(head);
   }

   public int getLength() {
      return this.fGrammarCount;
   }

   public XSNamespaceItem item(int index) {
      return index >= 0 && index < this.fGrammarCount ? this.fGrammarList[index] : null;
   }

   public Object get(int index) {
      if (index >= 0 && index < this.fGrammarCount) {
         return this.fGrammarList[index];
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   public int size() {
      return this.getLength();
   }

   public Iterator iterator() {
      return this.listIterator0(0);
   }

   public ListIterator listIterator() {
      return this.listIterator0(0);
   }

   public ListIterator listIterator(int index) {
      if (index >= 0 && index < this.fGrammarCount) {
         return this.listIterator0(index);
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   private ListIterator listIterator0(int index) {
      return new XSModelImpl.XSNamespaceItemListIterator(index);
   }

   public Object[] toArray() {
      Object[] a = new Object[this.fGrammarCount];
      this.toArray0(a);
      return a;
   }

   public Object[] toArray(Object[] a) {
      if (a.length < this.fGrammarCount) {
         Class arrayClass = a.getClass();
         Class componentType = arrayClass.getComponentType();
         a = (Object[])((Object[])Array.newInstance(componentType, this.fGrammarCount));
      }

      this.toArray0(a);
      if (a.length > this.fGrammarCount) {
         a[this.fGrammarCount] = null;
      }

      return a;
   }

   private void toArray0(Object[] a) {
      if (this.fGrammarCount > 0) {
         System.arraycopy(this.fGrammarList, 0, a, 0, this.fGrammarCount);
      }

   }

   private final class XSNamespaceItemListIterator implements ListIterator {
      private int index;

      public XSNamespaceItemListIterator(int index) {
         this.index = index;
      }

      public boolean hasNext() {
         return this.index < XSModelImpl.this.fGrammarCount;
      }

      public Object next() {
         if (this.index < XSModelImpl.this.fGrammarCount) {
            return XSModelImpl.this.fGrammarList[this.index++];
         } else {
            throw new NoSuchElementException();
         }
      }

      public boolean hasPrevious() {
         return this.index > 0;
      }

      public Object previous() {
         if (this.index > 0) {
            return XSModelImpl.this.fGrammarList[--this.index];
         } else {
            throw new NoSuchElementException();
         }
      }

      public int nextIndex() {
         return this.index;
      }

      public int previousIndex() {
         return this.index - 1;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void set(Object o) {
         throw new UnsupportedOperationException();
      }

      public void add(Object o) {
         throw new UnsupportedOperationException();
      }
   }
}
