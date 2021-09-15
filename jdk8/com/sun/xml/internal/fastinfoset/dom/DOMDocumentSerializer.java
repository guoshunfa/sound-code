package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMDocumentSerializer extends Encoder {
   protected NamespaceContextImplementation _namespaceScopeContext = new NamespaceContextImplementation();
   protected Node[] _attributes = new Node[32];

   public final void serialize(Node n) throws IOException {
      switch(n.getNodeType()) {
      case 1:
         this.serializeElementAsDocument(n);
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      default:
         break;
      case 7:
         this.serializeProcessingInstruction(n);
         break;
      case 8:
         this.serializeComment(n);
         break;
      case 9:
         this.serialize((Document)n);
      }

   }

   public final void serialize(Document d) throws IOException {
      this.reset();
      this.encodeHeader(false);
      this.encodeInitialVocabulary();
      NodeList nl = d.getChildNodes();

      for(int i = 0; i < nl.getLength(); ++i) {
         Node n = nl.item(i);
         switch(n.getNodeType()) {
         case 1:
            this.serializeElement(n);
            break;
         case 7:
            this.serializeProcessingInstruction(n);
            break;
         case 8:
            this.serializeComment(n);
         }
      }

      this.encodeDocumentTermination();
   }

   protected final void serializeElementAsDocument(Node e) throws IOException {
      this.reset();
      this.encodeHeader(false);
      this.encodeInitialVocabulary();
      this.serializeElement(e);
      this.encodeDocumentTermination();
   }

   protected final void serializeElement(Node e) throws IOException {
      this.encodeTermination();
      int attributesSize = 0;
      this._namespaceScopeContext.pushContext();
      String prefix;
      String value;
      String uri;
      if (e.hasAttributes()) {
         NamedNodeMap nnm = e.getAttributes();

         for(int i = 0; i < nnm.getLength(); ++i) {
            Node a = nnm.item(i);
            prefix = a.getNamespaceURI();
            if (prefix != null && prefix.equals("http://www.w3.org/2000/xmlns/")) {
               uri = a.getLocalName();
               value = a.getNodeValue();
               if (uri == "xmlns" || uri.equals("xmlns")) {
                  uri = "";
               }

               this._namespaceScopeContext.declarePrefix(uri, value);
            } else {
               if (attributesSize == this._attributes.length) {
                  Node[] attributes = new Node[attributesSize * 3 / 2 + 1];
                  System.arraycopy(this._attributes, 0, attributes, 0, attributesSize);
                  this._attributes = attributes;
               }

               this._attributes[attributesSize++] = a;
               uri = a.getNamespaceURI();
               value = a.getPrefix();
               if (value != null && !this._namespaceScopeContext.getNamespaceURI(value).equals(uri)) {
                  this._namespaceScopeContext.declarePrefix(value, uri);
               }
            }
         }
      }

      String elementNamespaceURI = e.getNamespaceURI();
      String elementPrefix = e.getPrefix();
      if (elementPrefix == null) {
         elementPrefix = "";
      }

      if (elementNamespaceURI != null && !this._namespaceScopeContext.getNamespaceURI(elementPrefix).equals(elementNamespaceURI)) {
         this._namespaceScopeContext.declarePrefix(elementPrefix, elementNamespaceURI);
      }

      if (!this._namespaceScopeContext.isCurrentContextEmpty()) {
         if (attributesSize > 0) {
            this.write(120);
         } else {
            this.write(56);
         }

         for(int i = this._namespaceScopeContext.getCurrentContextStartIndex(); i < this._namespaceScopeContext.getCurrentContextEndIndex(); ++i) {
            prefix = this._namespaceScopeContext.getPrefix(i);
            uri = this._namespaceScopeContext.getNamespaceURI(i);
            this.encodeNamespaceAttribute(prefix, uri);
         }

         this.write(240);
         this._b = 0;
      } else {
         this._b = attributesSize > 0 ? 64 : 0;
      }

      String namespaceURI = elementNamespaceURI == null ? "" : elementNamespaceURI;
      this.encodeElement(namespaceURI, e.getNodeName(), e.getLocalName());
      if (attributesSize > 0) {
         for(int i = 0; i < attributesSize; ++i) {
            Node a = this._attributes[i];
            this._attributes[i] = null;
            namespaceURI = a.getNamespaceURI();
            namespaceURI = namespaceURI == null ? "" : namespaceURI;
            this.encodeAttribute(namespaceURI, a.getNodeName(), a.getLocalName());
            value = a.getNodeValue();
            boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
            this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
         }

         this._b = 240;
         this._terminate = true;
      }

      if (e.hasChildNodes()) {
         NodeList nl = e.getChildNodes();

         for(int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            switch(n.getNodeType()) {
            case 1:
               this.serializeElement(n);
            case 2:
            case 5:
            case 6:
            default:
               break;
            case 3:
               this.serializeText(n);
               break;
            case 4:
               this.serializeCDATA(n);
               break;
            case 7:
               this.serializeProcessingInstruction(n);
               break;
            case 8:
               this.serializeComment(n);
            }
         }
      }

      this.encodeElementTermination();
      this._namespaceScopeContext.popContext();
   }

   protected final void serializeText(Node t) throws IOException {
      String text = t.getNodeValue();
      int length = text != null ? text.length() : 0;
      if (length != 0) {
         if (length < this._charBuffer.length) {
            text.getChars(0, length, this._charBuffer, 0);
            if (this.getIgnoreWhiteSpaceTextContent() && isWhiteSpace(this._charBuffer, 0, length)) {
               return;
            }

            this.encodeTermination();
            this.encodeCharacters(this._charBuffer, 0, length);
         } else {
            char[] ch = text.toCharArray();
            if (this.getIgnoreWhiteSpaceTextContent() && isWhiteSpace(ch, 0, length)) {
               return;
            }

            this.encodeTermination();
            this.encodeCharactersNoClone(ch, 0, length);
         }

      }
   }

   protected final void serializeCDATA(Node t) throws IOException {
      String text = t.getNodeValue();
      int length = text != null ? text.length() : 0;
      if (length != 0) {
         char[] ch = text.toCharArray();
         if (!this.getIgnoreWhiteSpaceTextContent() || !isWhiteSpace(ch, 0, length)) {
            this.encodeTermination();

            try {
               this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, 0, length);
            } catch (FastInfosetException var6) {
               throw new IOException("");
            }
         }
      }
   }

   protected final void serializeComment(Node c) throws IOException {
      if (!this.getIgnoreComments()) {
         this.encodeTermination();
         String comment = c.getNodeValue();
         int length = comment != null ? comment.length() : 0;
         if (length == 0) {
            this.encodeComment(this._charBuffer, 0, 0);
         } else if (length < this._charBuffer.length) {
            comment.getChars(0, length, this._charBuffer, 0);
            this.encodeComment(this._charBuffer, 0, length);
         } else {
            char[] ch = comment.toCharArray();
            this.encodeCommentNoClone(ch, 0, length);
         }

      }
   }

   protected final void serializeProcessingInstruction(Node pi) throws IOException {
      if (!this.getIgnoreProcesingInstructions()) {
         this.encodeTermination();
         String target = pi.getNodeName();
         String data = pi.getNodeValue();
         this.encodeProcessingInstruction(target, data);
      }
   }

   protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
      if (entry._valueIndex > 0) {
         QualifiedName[] names = entry._value;

         for(int i = 0; i < entry._valueIndex; ++i) {
            if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
               this.encodeNonZeroIntegerOnThirdBit(names[i].index);
               return;
            }
         }
      }

      if (localName != null) {
         this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefixFromQualifiedName(qName), localName, entry);
      } else {
         this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, "", qName, entry);
      }

   }

   protected final void encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
      LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
      if (entry._valueIndex > 0) {
         QualifiedName[] names = entry._value;

         for(int i = 0; i < entry._valueIndex; ++i) {
            if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
               this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
               return;
            }
         }
      }

      if (localName != null) {
         this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefixFromQualifiedName(qName), localName, entry);
      } else {
         this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, "", qName, entry);
      }

   }
}
