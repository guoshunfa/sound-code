package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XML11NSDTDValidator extends XML11DTDValidator {
   private QName fAttributeQName = new QName();

   protected final void startNamespaceScope(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      this.fNamespaceContext.pushContext();
      if (element.prefix == XMLSymbols.PREFIX_XMLNS) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{element.rawname}, (short)2);
      }

      int length = attributes.getLength();

      String aprefix;
      String auri;
      for(int i = 0; i < length; ++i) {
         String localpart = attributes.getLocalName(i);
         aprefix = attributes.getPrefix(i);
         if (aprefix == XMLSymbols.PREFIX_XMLNS || aprefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS) {
            auri = this.fSymbolTable.addSymbol(attributes.getValue(i));
            if (aprefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{attributes.getQName(i)}, (short)2);
            }

            if (auri == NamespaceContext.XMLNS_URI) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{attributes.getQName(i)}, (short)2);
            }

            if (localpart == XMLSymbols.PREFIX_XML) {
               if (auri != NamespaceContext.XML_URI) {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{attributes.getQName(i)}, (short)2);
               }
            } else if (auri == NamespaceContext.XML_URI) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{attributes.getQName(i)}, (short)2);
            }

            aprefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
            this.fNamespaceContext.declarePrefix(aprefix, auri.length() != 0 ? auri : null);
         }
      }

      String prefix = element.prefix != null ? element.prefix : XMLSymbols.EMPTY_STRING;
      element.uri = this.fNamespaceContext.getURI(prefix);
      if (element.prefix == null && element.uri != null) {
         element.prefix = XMLSymbols.EMPTY_STRING;
      }

      if (element.prefix != null && element.uri == null) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{element.prefix, element.rawname}, (short)2);
      }

      int attrCount;
      for(attrCount = 0; attrCount < length; ++attrCount) {
         attributes.getName(attrCount, this.fAttributeQName);
         aprefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
         auri = this.fAttributeQName.rawname;
         if (auri == XMLSymbols.PREFIX_XMLNS) {
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
            attributes.setName(attrCount, this.fAttributeQName);
         } else if (aprefix != XMLSymbols.EMPTY_STRING) {
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(aprefix);
            if (this.fAttributeQName.uri == null) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{element.rawname, auri, aprefix}, (short)2);
            }

            attributes.setName(attrCount, this.fAttributeQName);
         }
      }

      attrCount = attributes.getLength();

      for(int i = 0; i < attrCount - 1; ++i) {
         auri = attributes.getURI(i);
         if (auri != null && auri != NamespaceContext.XMLNS_URI) {
            String alocalpart = attributes.getLocalName(i);

            for(int j = i + 1; j < attrCount; ++j) {
               String blocalpart = attributes.getLocalName(j);
               String buri = attributes.getURI(j);
               if (alocalpart == blocalpart && auri == buri) {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{element.rawname, alocalpart, auri}, (short)2);
               }
            }
         }
      }

   }

   protected void endNamespaceScope(QName element, Augmentations augs, boolean isEmpty) throws XNIException {
      String eprefix = element.prefix != null ? element.prefix : XMLSymbols.EMPTY_STRING;
      element.uri = this.fNamespaceContext.getURI(eprefix);
      if (element.uri != null) {
         element.prefix = eprefix;
      }

      if (this.fDocumentHandler != null && !isEmpty) {
         this.fDocumentHandler.endElement(element, augs);
      }

      this.fNamespaceContext.popContext();
   }
}
