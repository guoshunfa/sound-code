package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentParser extends Decoder {
   protected Document _document;
   protected Node _currentNode;
   protected Element _currentElement;
   protected Attr[] _namespaceAttributes = new Attr[16];
   protected int _namespaceAttributesIndex;
   protected int[] _namespacePrefixes = new int[16];
   protected int _namespacePrefixesIndex;

   public void parse(Document d, InputStream s) throws FastInfosetException, IOException {
      this._currentNode = this._document = d;
      this._namespaceAttributesIndex = 0;
      this.parse(s);
   }

   protected final void parse(InputStream s) throws FastInfosetException, IOException {
      this.setInputStream(s);
      this.parse();
   }

   protected void resetOnError() {
      this._namespacePrefixesIndex = 0;
      if (this._v == null) {
         this._prefixTable.clearCompletely();
      }

      this._duplicateAttributeVerifier.clear();
   }

   protected final void parse() throws FastInfosetException, IOException {
      try {
         this.reset();
         this.decodeHeader();
         this.processDII();
      } catch (RuntimeException var2) {
         this.resetOnError();
         throw new FastInfosetException(var2);
      } catch (FastInfosetException var3) {
         this.resetOnError();
         throw var3;
      } catch (IOException var4) {
         this.resetOnError();
         throw var4;
      }
   }

   protected final void processDII() throws FastInfosetException, IOException {
      this._b = this.read();
      if (this._b > 0) {
         this.processDIIOptionalProperties();
      }

      boolean firstElementHasOccured = false;
      boolean documentTypeDeclarationOccured = false;

      while(!this._terminate || !firstElementHasOccured) {
         this._b = this.read();
         switch(DecoderStateTables.DII(this._b)) {
         case 0:
            this.processEII(this._elementNameTable._array[this._b], false);
            firstElementHasOccured = true;
            break;
         case 1:
            this.processEII(this._elementNameTable._array[this._b & 31], true);
            firstElementHasOccured = true;
            break;
         case 2:
            this.processEII(this.decodeEIIIndexMedium(), (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 3:
            this.processEII(this.decodeEIIIndexLarge(), (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 4:
            this.processEIIWithNamespaces();
            firstElementHasOccured = true;
            break;
         case 5:
            QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
            this._elementNameTable.add(qn);
            this.processEII(qn, (this._b & 64) > 0);
            firstElementHasOccured = true;
            break;
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 21:
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
         case 18:
            this.processCommentII();
            break;
         case 19:
            this.processProcessingII();
            break;
         case 20:
            if (documentTypeDeclarationOccured) {
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
            }

            documentTypeDeclarationOccured = true;
            Object var10000;
            if ((this._b & 2) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = null;
            }

            if ((this._b & 1) > 0) {
               this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            } else {
               var10000 = null;
            }

            this._b = this.read();

            while(this._b == 225) {
               switch(this.decodeNonIdentifyingStringOnFirstBit()) {
               case 0:
                  if (this._addToTable) {
                     this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                  }
               case 1:
               case 3:
               default:
                  this._b = this.read();
                  break;
               case 2:
                  throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
               }
            }

            if ((this._b & 240) != 240) {
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
            }

            if (this._b == 255) {
               this._terminate = true;
            }

            this._notations.clear();
            this._unparsedEntities.clear();
            break;
         case 23:
            this._doubleTerminate = true;
         case 22:
            this._terminate = true;
         }
      }

      while(!this._terminate) {
         this._b = this.read();
         switch(DecoderStateTables.DII(this._b)) {
         case 18:
            this.processCommentII();
            break;
         case 19:
            this.processProcessingII();
            break;
         case 20:
         case 21:
         default:
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
         case 23:
            this._doubleTerminate = true;
         case 22:
            this._terminate = true;
         }
      }

   }

   protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {
      if (this._b == 32) {
         this.decodeInitialVocabulary();
      } else {
         if ((this._b & 64) > 0) {
            this.decodeAdditionalData();
         }

         if ((this._b & 32) > 0) {
            this.decodeInitialVocabulary();
         }

         if ((this._b & 16) > 0) {
            this.decodeNotations();
         }

         if ((this._b & 8) > 0) {
            this.decodeUnparsedEntities();
         }

         if ((this._b & 4) > 0) {
            this.decodeCharacterEncodingScheme();
         }

         if ((this._b & 2) > 0) {
            this.read();
         }

         if ((this._b & 1) > 0) {
            this.decodeVersion();
         }

      }
   }

   protected final void processEII(QualifiedName name, boolean hasAttributes) throws FastInfosetException, IOException {
      if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
      } else {
         Node parentCurrentNode = this._currentNode;
         this._currentNode = this._currentElement = this.createElement(name.namespaceName, name.qName, name.localName);
         int index;
         if (this._namespaceAttributesIndex > 0) {
            for(index = 0; index < this._namespaceAttributesIndex; ++index) {
               this._currentElement.setAttributeNode(this._namespaceAttributes[index]);
               this._namespaceAttributes[index] = null;
            }

            this._namespaceAttributesIndex = 0;
         }

         if (hasAttributes) {
            this.processAIIs();
         }

         parentCurrentNode.appendChild(this._currentElement);

         while(!this._terminate) {
            this._b = this.read();
            String s;
            String v;
            boolean addToTable;
            switch(DecoderStateTables.EII(this._b)) {
            case 0:
               this.processEII(this._elementNameTable._array[this._b], false);
               break;
            case 1:
               this.processEII(this._elementNameTable._array[this._b & 31], true);
               break;
            case 2:
               this.processEII(this.decodeEIIIndexMedium(), (this._b & 64) > 0);
               break;
            case 3:
               this.processEII(this.decodeEIIIndexLarge(), (this._b & 64) > 0);
               break;
            case 4:
               this.processEIIWithNamespaces();
               break;
            case 5:
               QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
               this._elementNameTable.add(qn);
               this.processEII(qn, (this._b & 64) > 0);
               break;
            case 6:
               this._octetBufferLength = (this._b & 1) + 1;
               this.appendOrCreateTextData(this.processUtf8CharacterString());
               break;
            case 7:
               this._octetBufferLength = this.read() + 3;
               this.appendOrCreateTextData(this.processUtf8CharacterString());
               break;
            case 8:
               this._octetBufferLength = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
               this._octetBufferLength += 259;
               this.appendOrCreateTextData(this.processUtf8CharacterString());
               break;
            case 9:
               this._octetBufferLength = (this._b & 1) + 1;
               v = this.decodeUtf16StringAsString();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               this.appendOrCreateTextData(v);
               break;
            case 10:
               this._octetBufferLength = this.read() + 3;
               v = this.decodeUtf16StringAsString();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               this.appendOrCreateTextData(v);
               break;
            case 11:
               this._octetBufferLength = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
               this._octetBufferLength += 259;
               v = this.decodeUtf16StringAsString();
               if ((this._b & 16) > 0) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               this.appendOrCreateTextData(v);
               break;
            case 12:
               addToTable = (this._b & 16) > 0;
               this._identifier = (this._b & 2) << 6;
               this._b = this.read();
               this._identifier |= (this._b & 252) >> 2;
               this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
               s = this.decodeRestrictedAlphabetAsString();
               if (addToTable) {
                  this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
               }

               this.appendOrCreateTextData(s);
               break;
            case 13:
               addToTable = (this._b & 16) > 0;
               this._identifier = (this._b & 2) << 6;
               this._b = this.read();
               this._identifier |= (this._b & 252) >> 2;
               this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
               s = this.convertEncodingAlgorithmDataToCharacters(false);
               if (addToTable) {
                  this._characterContentChunkTable.add(s.toCharArray(), s.length());
               }

               this.appendOrCreateTextData(s);
               break;
            case 14:
               v = this._characterContentChunkTable.getString(this._b & 15);
               this.appendOrCreateTextData(v);
               break;
            case 15:
               index = ((this._b & 3) << 8 | this.read()) + 16;
               s = this._characterContentChunkTable.getString(index);
               this.appendOrCreateTextData(s);
               break;
            case 16:
               index = (this._b & 3) << 16 | this.read() << 8 | this.read();
               index += 1040;
               s = this._characterContentChunkTable.getString(index);
               this.appendOrCreateTextData(s);
               break;
            case 17:
               index = this.read() << 16 | this.read() << 8 | this.read();
               index += 263184;
               s = this._characterContentChunkTable.getString(index);
               this.appendOrCreateTextData(s);
               break;
            case 18:
               this.processCommentII();
               break;
            case 19:
               this.processProcessingII();
               break;
            case 20:
            default:
               throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
            case 21:
               v = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
               Object var10000;
               if ((this._b & 2) > 0) {
                  this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
               } else {
                  var10000 = null;
               }

               if ((this._b & 1) > 0) {
                  this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
               } else {
                  var10000 = null;
               }
               break;
            case 23:
               this._doubleTerminate = true;
            case 22:
               this._terminate = true;
            }
         }

         this._terminate = this._doubleTerminate;
         this._doubleTerminate = false;
         this._currentNode = parentCurrentNode;
      }
   }

   private void appendOrCreateTextData(String textData) {
      Node lastChild = this._currentNode.getLastChild();
      if (lastChild instanceof Text) {
         ((Text)lastChild).appendData(textData);
      } else {
         this._currentNode.appendChild(this._document.createTextNode(textData));
      }

   }

   private final String processUtf8CharacterString() throws FastInfosetException, IOException {
      if ((this._b & 16) > 0) {
         this._characterContentChunkTable.ensureSize(this._octetBufferLength);
         int charactersOffset = this._characterContentChunkTable._arrayIndex;
         this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
         this._characterContentChunkTable.add(this._charBufferLength);
         return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
      } else {
         this.decodeUtf8StringAsCharBuffer();
         return new String(this._charBuffer, 0, this._charBufferLength);
      }
   }

   protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
      boolean hasAttributes = (this._b & 64) > 0;
      if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
         this._prefixTable.clearDeclarationIds();
      }

      Attr a = null;
      int start = this._namespacePrefixesIndex;

      int b;
      for(b = this.read(); (b & 252) == 204; b = this.read()) {
         if (this._namespaceAttributesIndex == this._namespaceAttributes.length) {
            Attr[] newNamespaceAttributes = new Attr[this._namespaceAttributesIndex * 3 / 2 + 1];
            System.arraycopy(this._namespaceAttributes, 0, newNamespaceAttributes, 0, this._namespaceAttributesIndex);
            this._namespaceAttributes = newNamespaceAttributes;
         }

         if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
            int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
            System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
            this._namespacePrefixes = namespaceAIIs;
         }

         String prefix;
         switch(b & 3) {
         case 0:
            a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
            a.setValue("");
            this._prefixIndex = this._namespaceNameIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
            break;
         case 1:
            a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
            a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false));
            this._prefixIndex = this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
            break;
         case 2:
            prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
            a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
            a.setValue("");
            this._namespaceNameIndex = -1;
            this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
            break;
         case 3:
            prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
            a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
            a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true));
            this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
         }

         this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
         this._namespaceAttributes[this._namespaceAttributesIndex++] = a;
      }

      if (b != 240) {
         throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
      } else {
         int end = this._namespacePrefixesIndex;
         this._b = this.read();
         switch(DecoderStateTables.EII(this._b)) {
         case 0:
            this.processEII(this._elementNameTable._array[this._b], hasAttributes);
            break;
         case 1:
         case 4:
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
         case 2:
            this.processEII(this.decodeEIIIndexMedium(), hasAttributes);
            break;
         case 3:
            this.processEII(this.decodeEIIIndexLarge(), hasAttributes);
            break;
         case 5:
            QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
            this._elementNameTable.add(qn);
            this.processEII(qn, hasAttributes);
         }

         for(int i = start; i < end; ++i) {
            this._prefixTable.popScope(this._namespacePrefixes[i]);
         }

         this._namespacePrefixesIndex = start;
      }
   }

   protected final QualifiedName processLiteralQualifiedName(int state, QualifiedName q) throws FastInfosetException, IOException {
      if (q == null) {
         q = new QualifiedName();
      }

      switch(state) {
      case 0:
         return q.set((String)null, (String)null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, (char[])null);
      case 1:
         return q.set((String)null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, (char[])null);
      case 2:
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
      case 3:
         return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
      default:
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
      }
   }

   protected final QualifiedName processLiteralQualifiedName(int state) throws FastInfosetException, IOException {
      switch(state) {
      case 0:
         return new QualifiedName((String)null, (String)null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, (char[])null);
      case 1:
         return new QualifiedName((String)null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, (char[])null);
      case 2:
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
      case 3:
         return new QualifiedName(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
      default:
         throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
      }
   }

   protected final void processAIIs() throws FastInfosetException, IOException {
      if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
         this._duplicateAttributeVerifier.clear();
      }

      do {
         int b = this.read();
         QualifiedName name;
         int i;
         switch(DecoderStateTables.AII(b)) {
         case 0:
            name = this._attributeNameTable._array[b];
            break;
         case 1:
            i = ((b & 31) << 8 | this.read()) + 64;
            name = this._attributeNameTable._array[i];
            break;
         case 2:
            i = ((b & 15) << 16 | this.read() << 8 | this.read()) + 8256;
            name = this._attributeNameTable._array[i];
            break;
         case 3:
            name = this.processLiteralQualifiedName(b & 3, this._attributeNameTable.getNext());
            name.createAttributeValues(256);
            this._attributeNameTable.add(name);
            break;
         case 5:
            this._doubleTerminate = true;
         case 4:
            this._terminate = true;
            continue;
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
         }

         if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
         }

         this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
         Attr a = this.createAttribute(name.namespaceName, name.qName, name.localName);
         b = this.read();
         String value;
         int index;
         int length;
         boolean addToTable;
         switch(DecoderStateTables.NISTRING(b)) {
         case 0:
            addToTable = (b & 64) > 0;
            this._octetBufferLength = (b & 7) + 1;
            value = this.decodeUtf8StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 1:
            addToTable = (b & 64) > 0;
            this._octetBufferLength = this.read() + 9;
            value = this.decodeUtf8StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 2:
            addToTable = (b & 64) > 0;
            length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
            this._octetBufferLength = length + 265;
            value = this.decodeUtf8StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 3:
            addToTable = (b & 64) > 0;
            this._octetBufferLength = (b & 7) + 1;
            value = this.decodeUtf16StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 4:
            addToTable = (b & 64) > 0;
            this._octetBufferLength = this.read() + 9;
            value = this.decodeUtf16StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 5:
            addToTable = (b & 64) > 0;
            length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
            this._octetBufferLength = length + 265;
            value = this.decodeUtf16StringAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 6:
            addToTable = (b & 64) > 0;
            this._identifier = (b & 15) << 4;
            b = this.read();
            this._identifier |= (b & 240) >> 4;
            this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
            value = this.decodeRestrictedAlphabetAsString();
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 7:
            addToTable = (b & 64) > 0;
            this._identifier = (b & 15) << 4;
            b = this.read();
            this._identifier |= (b & 240) >> 4;
            this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
            value = this.convertEncodingAlgorithmDataToCharacters(true);
            if (addToTable) {
               this._attributeValueTable.add(value);
            }

            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 8:
            value = this._attributeValueTable._array[b & 63];
            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 9:
            index = ((b & 31) << 8 | this.read()) + 64;
            value = this._attributeValueTable._array[index];
            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 10:
            index = ((b & 15) << 16 | this.read() << 8 | this.read()) + 8256;
            value = this._attributeValueTable._array[index];
            a.setValue(value);
            this._currentElement.setAttributeNode(a);
            break;
         case 11:
            a.setValue("");
            this._currentElement.setAttributeNode(a);
            break;
         default:
            throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
         }
      } while(!this._terminate);

      this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
      this._terminate = this._doubleTerminate;
      this._doubleTerminate = false;
   }

   protected final void processCommentII() throws FastInfosetException, IOException {
      String s;
      switch(this.decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
         s = new String(this._charBuffer, 0, this._charBufferLength);
         if (this._addToTable) {
            this._v.otherString.add(new CharArrayString(s, false));
         }

         this._currentNode.appendChild(this._document.createComment(s));
         break;
      case 1:
         s = this._v.otherString.get(this._integer).toString();
         this._currentNode.appendChild(this._document.createComment(s));
         break;
      case 2:
         throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
      case 3:
         this._currentNode.appendChild(this._document.createComment(""));
      }

   }

   protected final void processProcessingII() throws FastInfosetException, IOException {
      String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
      String data;
      switch(this.decodeNonIdentifyingStringOnFirstBit()) {
      case 0:
         data = new String(this._charBuffer, 0, this._charBufferLength);
         if (this._addToTable) {
            this._v.otherString.add(new CharArrayString(data, false));
         }

         this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
         break;
      case 1:
         data = this._v.otherString.get(this._integer).toString();
         this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
         break;
      case 2:
         throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
      case 3:
         this._currentNode.appendChild(this._document.createProcessingInstruction(target, ""));
      }

   }

   protected Element createElement(String namespaceName, String qName, String localName) {
      return this._document.createElementNS(namespaceName, qName);
   }

   protected Attr createAttribute(String namespaceName, String qName, String localName) {
      return this._document.createAttributeNS(namespaceName, qName);
   }

   protected String convertEncodingAlgorithmDataToCharacters(boolean isAttributeValue) throws FastInfosetException, IOException {
      StringBuffer buffer = new StringBuffer();
      if (this._identifier < 9) {
         Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
         BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
      } else {
         if (this._identifier == 9) {
            if (!isAttributeValue) {
               this._octetBufferOffset -= this._octetBufferLength;
               return this.decodeUtf8StringAsString();
            }

            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
         }

         if (this._identifier >= 32) {
            String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
            EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI);
            if (ea == null) {
               throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }

            Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            ea.convertToCharacters(data, buffer);
         }
      }

      return buffer.toString();
   }
}
