package com.sun.xml.internal.fastinfoset.vocab;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.util.ValueArray;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class ParserVocabulary extends Vocabulary {
   public static final String IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems";
   public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems";
   public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY = "com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters";
   protected static final int IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems");
   protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems");
   protected static final int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS = getIntegerValueFromProperty("com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters");
   public final CharArrayArray restrictedAlphabet;
   public final StringArray encodingAlgorithm;
   public final StringArray namespaceName;
   public final PrefixArray prefix;
   public final StringArray localName;
   public final StringArray otherNCName;
   public final StringArray otherURI;
   public final StringArray attributeValue;
   public final CharArrayArray otherString;
   public final ContiguousCharArrayArray characterContentChunk;
   public final QualifiedNameArray elementName;
   public final QualifiedNameArray attributeName;
   public final ValueArray[] tables;
   protected SerializerVocabulary _readOnlyVocabulary;

   private static int getIntegerValueFromProperty(String property) {
      String value = System.getProperty(property);
      if (value == null) {
         return Integer.MAX_VALUE;
      } else {
         try {
            return Math.max(Integer.parseInt(value), 10);
         } catch (NumberFormatException var3) {
            return Integer.MAX_VALUE;
         }
      }
   }

   public ParserVocabulary() {
      this.restrictedAlphabet = new CharArrayArray(10, 256);
      this.encodingAlgorithm = new StringArray(10, 256, true);
      this.tables = new ValueArray[12];
      this.namespaceName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
      this.prefix = new PrefixArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
      this.localName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
      this.otherNCName = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, false);
      this.otherURI = new StringArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
      this.attributeValue = new StringArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, true);
      this.otherString = new CharArrayArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
      this.characterContentChunk = new ContiguousCharArrayArray(10, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, 512, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS);
      this.elementName = new QualifiedNameArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
      this.attributeName = new QualifiedNameArray(10, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
      this.tables[0] = this.restrictedAlphabet;
      this.tables[1] = this.encodingAlgorithm;
      this.tables[2] = this.prefix;
      this.tables[3] = this.namespaceName;
      this.tables[4] = this.localName;
      this.tables[5] = this.otherNCName;
      this.tables[6] = this.otherURI;
      this.tables[7] = this.attributeValue;
      this.tables[8] = this.otherString;
      this.tables[9] = this.characterContentChunk;
      this.tables[10] = this.elementName;
      this.tables[11] = this.attributeName;
   }

   public ParserVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary v) {
      this();
      this.convertVocabulary(v);
   }

   void setReadOnlyVocabulary(ParserVocabulary readOnlyVocabulary, boolean clear) {
      for(int i = 0; i < this.tables.length; ++i) {
         this.tables[i].setReadOnlyArray(readOnlyVocabulary.tables[i], clear);
      }

   }

   public void setInitialVocabulary(ParserVocabulary initialVocabulary, boolean clear) {
      this.setExternalVocabularyURI((String)null);
      this.setInitialReadOnlyVocabulary(true);
      this.setReadOnlyVocabulary(initialVocabulary, clear);
   }

   public void setReferencedVocabulary(String referencedVocabularyURI, ParserVocabulary referencedVocabulary, boolean clear) {
      if (!referencedVocabularyURI.equals(this.getExternalVocabularyURI())) {
         this.setInitialReadOnlyVocabulary(false);
         this.setExternalVocabularyURI(referencedVocabularyURI);
         this.setReadOnlyVocabulary(referencedVocabulary, clear);
      }

   }

   public void clear() {
      for(int i = 0; i < this.tables.length; ++i) {
         this.tables[i].clear();
      }

   }

   private void convertVocabulary(com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary v) {
      StringIntMap prefixMap = new FixedEntryStringIntMap("xml", 8);
      StringIntMap namespaceNameMap = new FixedEntryStringIntMap("http://www.w3.org/XML/1998/namespace", 8);
      StringIntMap localNameMap = new StringIntMap();
      this.addToTable(v.restrictedAlphabets.iterator(), this.restrictedAlphabet);
      this.addToTable(v.encodingAlgorithms.iterator(), this.encodingAlgorithm);
      this.addToTable((Iterator)v.prefixes.iterator(), (PrefixArray)this.prefix, prefixMap);
      this.addToTable((Iterator)v.namespaceNames.iterator(), (StringArray)this.namespaceName, namespaceNameMap);
      this.addToTable(v.localNames.iterator(), this.localName, localNameMap);
      this.addToTable(v.otherNCNames.iterator(), this.otherNCName);
      this.addToTable(v.otherURIs.iterator(), this.otherURI);
      this.addToTable(v.attributeValues.iterator(), this.attributeValue);
      this.addToTable(v.otherStrings.iterator(), this.otherString);
      this.addToTable(v.characterContentChunks.iterator(), this.characterContentChunk);
      this.addToTable(v.elements.iterator(), this.elementName, false, prefixMap, namespaceNameMap, localNameMap);
      this.addToTable(v.attributes.iterator(), this.attributeName, true, prefixMap, namespaceNameMap, localNameMap);
   }

   private void addToTable(Iterator i, StringArray a) {
      while(i.hasNext()) {
         this.addToTable((String)((String)i.next()), (StringArray)a, (StringIntMap)null);
      }

   }

   private void addToTable(Iterator i, StringArray a, StringIntMap m) {
      while(i.hasNext()) {
         this.addToTable((String)i.next(), a, m);
      }

   }

   private void addToTable(String s, StringArray a, StringIntMap m) {
      if (s.length() != 0) {
         if (m != null) {
            m.obtainIndex(s);
         }

         a.add(s);
      }
   }

   private void addToTable(Iterator i, PrefixArray a, StringIntMap m) {
      while(i.hasNext()) {
         this.addToTable((String)i.next(), a, m);
      }

   }

   private void addToTable(String s, PrefixArray a, StringIntMap m) {
      if (s.length() != 0) {
         if (m != null) {
            m.obtainIndex(s);
         }

         a.add(s);
      }
   }

   private void addToTable(Iterator i, ContiguousCharArrayArray a) {
      while(i.hasNext()) {
         this.addToTable((String)i.next(), a);
      }

   }

   private void addToTable(String s, ContiguousCharArrayArray a) {
      if (s.length() != 0) {
         char[] c = s.toCharArray();
         a.add(c, c.length);
      }
   }

   private void addToTable(Iterator i, CharArrayArray a) {
      while(i.hasNext()) {
         this.addToTable((String)i.next(), a);
      }

   }

   private void addToTable(String s, CharArrayArray a) {
      if (s.length() != 0) {
         char[] c = s.toCharArray();
         a.add(new CharArray(c, 0, c.length, false));
      }
   }

   private void addToTable(Iterator i, QualifiedNameArray a, boolean isAttribute, StringIntMap prefixMap, StringIntMap namespaceNameMap, StringIntMap localNameMap) {
      while(i.hasNext()) {
         this.addToNameTable((QName)i.next(), a, isAttribute, prefixMap, namespaceNameMap, localNameMap);
      }

   }

   private void addToNameTable(QName n, QualifiedNameArray a, boolean isAttribute, StringIntMap prefixMap, StringIntMap namespaceNameMap, StringIntMap localNameMap) {
      int namespaceURIIndex = -1;
      int prefixIndex = -1;
      if (n.getNamespaceURI().length() > 0) {
         namespaceURIIndex = namespaceNameMap.obtainIndex(n.getNamespaceURI());
         if (namespaceURIIndex == -1) {
            namespaceURIIndex = this.namespaceName.getSize();
            this.namespaceName.add(n.getNamespaceURI());
         }

         if (n.getPrefix().length() > 0) {
            prefixIndex = prefixMap.obtainIndex(n.getPrefix());
            if (prefixIndex == -1) {
               prefixIndex = this.prefix.getSize();
               this.prefix.add(n.getPrefix());
            }
         }
      }

      int localNameIndex = localNameMap.obtainIndex(n.getLocalPart());
      if (localNameIndex == -1) {
         localNameIndex = this.localName.getSize();
         this.localName.add(n.getLocalPart());
      }

      QualifiedName name = new QualifiedName(n.getPrefix(), n.getNamespaceURI(), n.getLocalPart(), a.getSize(), prefixIndex, namespaceURIIndex, localNameIndex);
      if (isAttribute) {
         name.createAttributeValues(256);
      }

      a.add(name);
   }
}
