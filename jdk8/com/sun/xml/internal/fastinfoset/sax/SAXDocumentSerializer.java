package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetWriter;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXDocumentSerializer extends Encoder implements FastInfosetWriter {
   protected boolean _elementHasNamespaces = false;
   protected boolean _charactersAsCDATA = false;

   protected SAXDocumentSerializer(boolean v) {
      super(v);
   }

   public SAXDocumentSerializer() {
   }

   public void reset() {
      super.reset();
      this._elementHasNamespaces = false;
      this._charactersAsCDATA = false;
   }

   public final void startDocument() throws SAXException {
      try {
         this.reset();
         this.encodeHeader(false);
         this.encodeInitialVocabulary();
      } catch (IOException var2) {
         throw new SAXException("startDocument", var2);
      }
   }

   public final void endDocument() throws SAXException {
      try {
         this.encodeDocumentTermination();
      } catch (IOException var2) {
         throw new SAXException("endDocument", var2);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      try {
         if (!this._elementHasNamespaces) {
            this.encodeTermination();
            this.mark();
            this._elementHasNamespaces = true;
            this.write(56);
         }

         this.encodeNamespaceAttribute(prefix, uri);
      } catch (IOException var4) {
         throw new SAXException("startElement", var4);
      }
   }

   public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      int attributeCount = atts != null && atts.getLength() > 0 ? this.countAttributes(atts) : 0;

      try {
         if (this._elementHasNamespaces) {
            this._elementHasNamespaces = false;
            if (attributeCount > 0) {
               byte[] var10000 = this._octetBuffer;
               int var10001 = this._markIndex;
               var10000[var10001] = (byte)(var10000[var10001] | 64);
            }

            this.resetMark();
            this.write(240);
            this._b = 0;
         } else {
            this.encodeTermination();
            this._b = 0;
            if (attributeCount > 0) {
               this._b |= 64;
            }
         }

         this.encodeElement(namespaceURI, qName, localName);
         if (attributeCount > 0) {
            this.encodeAttributes(atts);
         }

      } catch (IOException var7) {
         throw new SAXException("startElement", var7);
      } catch (FastInfosetException var8) {
         throw new SAXException("startElement", var8);
      }
   }

   public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      try {
         this.encodeElementTermination();
      } catch (IOException var5) {
         throw new SAXException("endElement", var5);
      }
   }

   public final void characters(char[] ch, int start, int length) throws SAXException {
      if (length > 0) {
         if (!this.getIgnoreWhiteSpaceTextContent() || !isWhiteSpace(ch, start, length)) {
            try {
               this.encodeTermination();
               if (!this._charactersAsCDATA) {
                  this.encodeCharacters(ch, start, length);
               } else {
                  this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
               }

            } catch (IOException var5) {
               throw new SAXException(var5);
            } catch (FastInfosetException var6) {
               throw new SAXException(var6);
            }
         }
      }
   }

   public final void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (!this.getIgnoreWhiteSpaceTextContent()) {
         this.characters(ch, start, length);
      }
   }

   public final void processingInstruction(String target, String data) throws SAXException {
      try {
         if (!this.getIgnoreProcesingInstructions()) {
            if (target.length() == 0) {
               throw new SAXException(CommonResourceBundle.getInstance().getString("message.processingInstructionTargetIsEmpty"));
            } else {
               this.encodeTermination();
               this.encodeProcessingInstruction(target, data);
            }
         }
      } catch (IOException var4) {
         throw new SAXException("processingInstruction", var4);
      }
   }

   public final void setDocumentLocator(Locator locator) {
   }

   public final void skippedEntity(String name) throws SAXException {
   }

   public final void comment(char[] ch, int start, int length) throws SAXException {
      try {
         if (!this.getIgnoreComments()) {
            this.encodeTermination();
            this.encodeComment(ch, start, length);
         }
      } catch (IOException var5) {
         throw new SAXException("startElement", var5);
      }
   }

   public final void startCDATA() throws SAXException {
      this._charactersAsCDATA = true;
   }

   public final void endCDATA() throws SAXException {
      this._charactersAsCDATA = false;
   }

   public final void startDTD(String name, String publicId, String systemId) throws SAXException {
      if (!this.getIgnoreDTD()) {
         try {
            this.encodeTermination();
            this.encodeDocumentTypeDeclaration(publicId, systemId);
            this.encodeElementTermination();
         } catch (IOException var5) {
            throw new SAXException("startDTD", var5);
         }
      }
   }

   public final void endDTD() throws SAXException {
   }

   public final void startEntity(String name) throws SAXException {
   }

   public final void endEntity(String name) throws SAXException {
   }

   public final void octets(String URI, int id, byte[] b, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeNonIdentifyingStringOnThirdBit(URI, id, b, start, length);
         } catch (IOException var7) {
            throw new SAXException(var7);
         } catch (FastInfosetException var8) {
            throw new SAXException(var8);
         }
      }
   }

   public final void object(String URI, int id, Object data) throws SAXException {
      try {
         this.encodeTermination();
         this.encodeNonIdentifyingStringOnThirdBit(URI, id, data);
      } catch (IOException var5) {
         throw new SAXException(var5);
      } catch (FastInfosetException var6) {
         throw new SAXException(var6);
      }
   }

   public final void bytes(byte[] b, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIOctetAlgorithmData(1, b, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         }
      }
   }

   public final void shorts(short[] s, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(2, s, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public final void ints(int[] i, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(3, i, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public final void longs(long[] l, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(4, l, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public final void booleans(boolean[] b, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(5, b, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public final void floats(float[] f, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(6, f, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public final void doubles(double[] d, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(7, d, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public void uuids(long[] msblsb, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(8, msblsb, start, length);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public void numericCharacters(char[] ch, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeNumericFourBitCharacters(ch, start, length, addToTable);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public void dateTimeCharacters(char[] ch, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeDateTimeFourBitCharacters(ch, start, length, addToTable);
         } catch (IOException var5) {
            throw new SAXException(var5);
         } catch (FastInfosetException var6) {
            throw new SAXException(var6);
         }
      }
   }

   public void alphabetCharacters(String alphabet, char[] ch, int start, int length) throws SAXException {
      if (length > 0) {
         try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeAlphabetCharacters(alphabet, ch, start, length, addToTable);
         } catch (IOException var6) {
            throw new SAXException(var6);
         } catch (FastInfosetException var7) {
            throw new SAXException(var7);
         }
      }
   }

   public void characters(char[] ch, int start, int length, boolean index) throws SAXException {
      if (length > 0) {
         if (!this.getIgnoreWhiteSpaceTextContent() || !isWhiteSpace(ch, start, length)) {
            try {
               this.encodeTermination();
               if (!this._charactersAsCDATA) {
                  this.encodeNonIdentifyingStringOnThirdBit(ch, start, length, this._v.characterContentChunk, index, true);
               } else {
                  this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
               }

            } catch (IOException var6) {
               throw new SAXException(var6);
            } catch (FastInfosetException var7) {
               throw new SAXException(var7);
            }
         }
      }
   }

   protected final int countAttributes(Attributes atts) {
      int count = 0;

      for(int i = 0; i < atts.getLength(); ++i) {
         String uri = atts.getURI(i);
         if (uri != "http://www.w3.org/2000/xmlns/" && !uri.equals("http://www.w3.org/2000/xmlns/")) {
            ++count;
         }
      }

      return count;
   }

   protected void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
      boolean addToTable;
      String value;
      if (atts instanceof EncodingAlgorithmAttributes) {
         EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;

         for(int i = 0; i < eAtts.getLength(); ++i) {
            if (this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
               Object data = eAtts.getAlgorithmData(i);
               if (data == null) {
                  value = eAtts.getValue(i);
                  addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                  boolean mustBeAddedToTable = eAtts.getToIndex(i);
                  String alphabet = eAtts.getAlpababet(i);
                  if (alphabet == null) {
                     this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                  } else if (alphabet == "0123456789-:TZ ") {
                     this.encodeDateTimeNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                  } else if (alphabet == "0123456789-+.E ") {
                     this.encodeNumericNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                  } else {
                     this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                  }
               } else {
                  this.encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i), eAtts.getAlgorithmIndex(i), data);
               }
            }
         }
      } else {
         for(int i = 0; i < atts.getLength(); ++i) {
            if (this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
               value = atts.getValue(i);
               addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
               this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
            }
         }
      }

      this._b = 240;
      this._terminate = true;
   }

   protected void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
      if (entry._valueIndex > 0) {
         QualifiedName[] names = entry._value;

         for(int i = 0; i < entry._valueIndex; ++i) {
            QualifiedName n = names[i];
            if (namespaceURI == n.namespaceName || namespaceURI.equals(n.namespaceName)) {
               this.encodeNonZeroIntegerOnThirdBit(names[i].index);
               return;
            }
         }
      }

      this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefixFromQualifiedName(qName), localName, entry);
   }

   protected boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
      if (entry._valueIndex > 0) {
         QualifiedName[] names = entry._value;

         for(int i = 0; i < entry._valueIndex; ++i) {
            if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
               this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
               return true;
            }
         }
      }

      return this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefixFromQualifiedName(qName), localName, entry);
   }
}
