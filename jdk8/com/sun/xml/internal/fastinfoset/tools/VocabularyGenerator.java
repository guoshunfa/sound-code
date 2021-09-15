package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyGenerator extends DefaultHandler implements LexicalHandler {
   protected SerializerVocabulary _serializerVocabulary;
   protected ParserVocabulary _parserVocabulary;
   protected Vocabulary _v;
   protected int attributeValueSizeConstraint = 32;
   protected int characterContentChunkSizeContraint = 32;

   public VocabularyGenerator() {
      this._serializerVocabulary = new SerializerVocabulary();
      this._parserVocabulary = new ParserVocabulary();
      this._v = new Vocabulary();
   }

   public VocabularyGenerator(SerializerVocabulary serializerVocabulary) {
      this._serializerVocabulary = serializerVocabulary;
      this._parserVocabulary = new ParserVocabulary();
      this._v = new Vocabulary();
   }

   public VocabularyGenerator(ParserVocabulary parserVocabulary) {
      this._serializerVocabulary = new SerializerVocabulary();
      this._parserVocabulary = parserVocabulary;
      this._v = new Vocabulary();
   }

   public VocabularyGenerator(SerializerVocabulary serializerVocabulary, ParserVocabulary parserVocabulary) {
      this._serializerVocabulary = serializerVocabulary;
      this._parserVocabulary = parserVocabulary;
      this._v = new Vocabulary();
   }

   public Vocabulary getVocabulary() {
      return this._v;
   }

   public void setCharacterContentChunkSizeLimit(int size) {
      if (size < 0) {
         size = 0;
      }

      this.characterContentChunkSizeContraint = size;
   }

   public int getCharacterContentChunkSizeLimit() {
      return this.characterContentChunkSizeContraint;
   }

   public void setAttributeValueSizeLimit(int size) {
      if (size < 0) {
         size = 0;
      }

      this.attributeValueSizeConstraint = size;
   }

   public int getAttributeValueSizeLimit() {
      return this.attributeValueSizeConstraint;
   }

   public void startDocument() throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      this.addToTable(prefix, this._v.prefixes, this._serializerVocabulary.prefix, this._parserVocabulary.prefix);
      this.addToTable(uri, this._v.namespaceNames, this._serializerVocabulary.namespaceName, this._parserVocabulary.namespaceName);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      this.addToNameTable(namespaceURI, qName, localName, this._v.elements, this._serializerVocabulary.elementName, this._parserVocabulary.elementName, false);

      for(int a = 0; a < atts.getLength(); ++a) {
         this.addToNameTable(atts.getURI(a), atts.getQName(a), atts.getLocalName(a), this._v.attributes, this._serializerVocabulary.attributeName, this._parserVocabulary.attributeName, true);
         String value = atts.getValue(a);
         if (value.length() < this.attributeValueSizeConstraint) {
            this.addToTable(value, this._v.attributeValues, this._serializerVocabulary.attributeValue, this._parserVocabulary.attributeValue);
         }
      }

   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (length < this.characterContentChunkSizeContraint) {
         this.addToCharArrayTable(new CharArray(ch, start, length, true));
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
   }

   public void processingInstruction(String target, String data) throws SAXException {
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void skippedEntity(String name) throws SAXException {
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
   }

   public void startCDATA() throws SAXException {
   }

   public void endCDATA() throws SAXException {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void addToTable(String s, Set v, StringIntMap m, StringArray a) {
      if (s.length() != 0) {
         if (m.obtainIndex(s) == -1) {
            a.add(s);
         }

         v.add(s);
      }
   }

   public void addToTable(String s, Set v, StringIntMap m, PrefixArray a) {
      if (s.length() != 0) {
         if (m.obtainIndex(s) == -1) {
            a.add(s);
         }

         v.add(s);
      }
   }

   public void addToCharArrayTable(CharArray c) {
      if (this._serializerVocabulary.characterContentChunk.obtainIndex(c.ch, c.start, c.length, false) == -1) {
         this._parserVocabulary.characterContentChunk.add(c.ch, c.length);
      }

      this._v.characterContentChunks.add(c.toString());
   }

   public void addToNameTable(String namespaceURI, String qName, String localName, Set v, LocalNameQualifiedNamesMap m, QualifiedNameArray a, boolean isAttribute) throws SAXException {
      LocalNameQualifiedNamesMap.Entry entry = m.obtainEntry(qName);
      int namespaceURIIndex;
      if (entry._valueIndex > 0) {
         QualifiedName[] names = entry._value;

         for(namespaceURIIndex = 0; namespaceURIIndex < entry._valueIndex; ++namespaceURIIndex) {
            if (namespaceURI == names[namespaceURIIndex].namespaceName || namespaceURI.equals(names[namespaceURIIndex].namespaceName)) {
               return;
            }
         }
      }

      String prefix = getPrefixFromQualifiedName(qName);
      namespaceURIIndex = -1;
      int prefixIndex = -1;
      int localNameIndex = true;
      if (namespaceURI.length() > 0) {
         namespaceURIIndex = this._serializerVocabulary.namespaceName.get(namespaceURI);
         if (namespaceURIIndex == -1) {
            throw new SAXException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[]{namespaceURIIndex}));
         }

         if (prefix.length() > 0) {
            prefixIndex = this._serializerVocabulary.prefix.get(prefix);
            if (prefixIndex == -1) {
               throw new SAXException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[]{prefixIndex}));
            }
         }
      }

      int localNameIndex = this._serializerVocabulary.localName.obtainIndex(localName);
      if (localNameIndex == -1) {
         this._parserVocabulary.localName.add(localName);
         localNameIndex = this._parserVocabulary.localName.getSize() - 1;
      }

      QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, m.getNextIndex(), prefixIndex, namespaceURIIndex, localNameIndex);
      if (isAttribute) {
         name.createAttributeValues(256);
      }

      entry.addQualifiedName(name);
      a.add(name);
      v.add(name.getQName());
   }

   public static String getPrefixFromQualifiedName(String qName) {
      int i = qName.indexOf(58);
      String prefix = "";
      if (i != -1) {
         prefix = qName.substring(0, i);
      }

      return prefix;
   }
}
