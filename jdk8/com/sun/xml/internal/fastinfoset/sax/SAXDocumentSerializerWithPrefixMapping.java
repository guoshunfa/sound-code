package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer {
   protected Map _namespaceToPrefixMapping;
   protected Map _prefixToPrefixMapping;
   protected String _lastCheckedNamespace;
   protected String _lastCheckedPrefix;
   protected StringIntMap _declaredNamespaces;

   public SAXDocumentSerializerWithPrefixMapping(Map namespaceToPrefixMapping) {
      super(true);
      this._namespaceToPrefixMapping = new HashMap(namespaceToPrefixMapping);
      this._prefixToPrefixMapping = new HashMap();
      this._namespaceToPrefixMapping.put("", "");
      this._namespaceToPrefixMapping.put("http://www.w3.org/XML/1998/namespace", "xml");
      this._declaredNamespaces = new StringIntMap(4);
   }

   public final void startPrefixMapping(String prefix, String uri) throws SAXException {
      try {
         String p;
         if (!this._elementHasNamespaces) {
            this.encodeTermination();
            this.mark();
            this._elementHasNamespaces = true;
            this.write(56);
            this._declaredNamespaces.clear();
            this._declaredNamespaces.obtainIndex(uri);
         } else if (this._declaredNamespaces.obtainIndex(uri) != -1) {
            p = this.getPrefix(uri);
            if (p != null) {
               this._prefixToPrefixMapping.put(prefix, p);
            }

            return;
         }

         p = this.getPrefix(uri);
         if (p != null) {
            this.encodeNamespaceAttribute(p, uri);
            this._prefixToPrefixMapping.put(prefix, p);
         } else {
            this.putPrefix(uri, prefix);
            this.encodeNamespaceAttribute(prefix, uri);
         }

      } catch (IOException var4) {
         throw new SAXException("startElement", var4);
      }
   }

   protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
      if (entry._valueIndex > 0) {
         if (this.encodeElementMapEntry(entry, namespaceURI)) {
            return;
         }

         if (this._v.elementName.isQNameFromReadOnlyMap(entry._value[0])) {
            entry = this._v.elementName.obtainDynamicEntry(localName);
            if (entry._valueIndex > 0 && this.encodeElementMapEntry(entry, namespaceURI)) {
               return;
            }
         }
      }

      this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, this.getPrefix(namespaceURI), localName, entry);
   }

   protected boolean encodeElementMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {
      QualifiedName[] names = entry._value;

      for(int i = 0; i < entry._valueIndex; ++i) {
         if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
            this.encodeNonZeroIntegerOnThirdBit(names[i].index);
            return true;
         }
      }

      return false;
   }

   protected final void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
      boolean addToTable;
      String value;
      if (atts instanceof EncodingAlgorithmAttributes) {
         EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;

         for(int i = 0; i < eAtts.getLength(); ++i) {
            String uri = atts.getURI(i);
            if (this.encodeAttribute(uri, atts.getQName(i), atts.getLocalName(i))) {
               Object data = eAtts.getAlgorithmData(i);
               if (data == null) {
                  value = eAtts.getValue(i);
                  addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                  boolean mustToBeAddedToTable = eAtts.getToIndex(i);
                  String alphabet = eAtts.getAlpababet(i);
                  if (alphabet != null) {
                     if (alphabet == "0123456789-:TZ ") {
                        this.encodeDateTimeNonIdentifyingStringOnFirstBit(value, addToTable, mustToBeAddedToTable);
                     } else if (alphabet == "0123456789-+.E ") {
                        this.encodeNumericNonIdentifyingStringOnFirstBit(value, addToTable, mustToBeAddedToTable);
                     } else {
                        this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustToBeAddedToTable);
                     }
                  } else {
                     if (uri == "http://www.w3.org/2001/XMLSchema-instance" || uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                        value = this.convertQName(value);
                     }

                     this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustToBeAddedToTable);
                  }
               } else {
                  this.encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i), eAtts.getAlgorithmIndex(i), data);
               }
            }
         }
      } else {
         for(int i = 0; i < atts.getLength(); ++i) {
            String uri = atts.getURI(i);
            if (this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
               value = atts.getValue(i);
               addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
               if (uri == "http://www.w3.org/2001/XMLSchema-instance" || uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                  value = this.convertQName(value);
               }

               this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
            }
         }
      }

      this._b = 240;
      this._terminate = true;
   }

   private String convertQName(String qName) {
      int i = qName.indexOf(58);
      String prefix = "";
      String localName = qName;
      if (i != -1) {
         prefix = qName.substring(0, i);
         localName = qName.substring(i + 1);
      }

      String p = (String)this._prefixToPrefixMapping.get(prefix);
      if (p != null) {
         return p.length() == 0 ? localName : p + ":" + localName;
      } else {
         return qName;
      }
   }

   protected final boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
      if (entry._valueIndex > 0) {
         if (this.encodeAttributeMapEntry(entry, namespaceURI)) {
            return true;
         }

         if (this._v.attributeName.isQNameFromReadOnlyMap(entry._value[0])) {
            entry = this._v.attributeName.obtainDynamicEntry(localName);
            if (entry._valueIndex > 0 && this.encodeAttributeMapEntry(entry, namespaceURI)) {
               return true;
            }
         }
      }

      return this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, this.getPrefix(namespaceURI), localName, entry);
   }

   protected boolean encodeAttributeMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {
      QualifiedName[] names = entry._value;

      for(int i = 0; i < entry._valueIndex; ++i) {
         if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
            this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
            return true;
         }
      }

      return false;
   }

   protected final String getPrefix(String namespaceURI) {
      if (this._lastCheckedNamespace == namespaceURI) {
         return this._lastCheckedPrefix;
      } else {
         this._lastCheckedNamespace = namespaceURI;
         return this._lastCheckedPrefix = (String)this._namespaceToPrefixMapping.get(namespaceURI);
      }
   }

   protected final void putPrefix(String namespaceURI, String prefix) {
      this._namespaceToPrefixMapping.put(namespaceURI, prefix);
      this._lastCheckedNamespace = namespaceURI;
      this._lastCheckedPrefix = prefix;
   }
}
