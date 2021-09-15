package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultAugmentor implements DOMDocumentHandler {
   private DOMValidatorHelper fDOMValidatorHelper;
   private Document fDocument;
   private CoreDocumentImpl fDocumentImpl;
   private boolean fStorePSVI;
   private boolean fIgnoreChars;
   private final QName fAttributeQName = new QName();

   public DOMResultAugmentor(DOMValidatorHelper helper) {
      this.fDOMValidatorHelper = helper;
   }

   public void setDOMResult(DOMResult result) {
      this.fIgnoreChars = false;
      if (result != null) {
         Node target = result.getNode();
         this.fDocument = target.getNodeType() == 9 ? (Document)target : target.getOwnerDocument();
         this.fDocumentImpl = this.fDocument instanceof CoreDocumentImpl ? (CoreDocumentImpl)this.fDocument : null;
         this.fStorePSVI = this.fDocument instanceof PSVIDocumentImpl;
      } else {
         this.fDocument = null;
         this.fDocumentImpl = null;
         this.fStorePSVI = false;
      }
   }

   public void doctypeDecl(DocumentType node) throws XNIException {
   }

   public void characters(Text node) throws XNIException {
   }

   public void cdata(CDATASection node) throws XNIException {
   }

   public void comment(Comment node) throws XNIException {
   }

   public void processingInstruction(ProcessingInstruction node) throws XNIException {
   }

   public void setIgnoringCharacters(boolean ignore) {
      this.fIgnoreChars = ignore;
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      Element currentElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
      NamedNodeMap attrMap = currentElement.getAttributes();
      int oldLength = attrMap.getLength();
      int i;
      if (this.fDocumentImpl != null) {
         for(i = 0; i < oldLength; ++i) {
            AttrImpl attr = (AttrImpl)attrMap.item(i);
            AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            if (attrPSVI != null && this.processAttributePSVI(attr, attrPSVI)) {
               ((ElementImpl)currentElement).setIdAttributeNode(attr, true);
            }
         }
      }

      int newLength = attributes.getLength();
      if (newLength > oldLength) {
         if (this.fDocumentImpl == null) {
            for(i = oldLength; i < newLength; ++i) {
               attributes.getName(i, this.fAttributeQName);
               currentElement.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, attributes.getValue(i));
            }
         } else {
            for(i = oldLength; i < newLength; ++i) {
               attributes.getName(i, this.fAttributeQName);
               AttrImpl attr = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
               attr.setValue(attributes.getValue(i));
               AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
               if (attrPSVI != null && this.processAttributePSVI(attr, attrPSVI)) {
                  ((ElementImpl)currentElement).setIdAttributeNode(attr, true);
               }

               attr.setSpecified(false);
               currentElement.setAttributeNode(attr);
            }
         }
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      this.startElement(element, attributes, augs);
      this.endElement(element, augs);
   }

   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (!this.fIgnoreChars) {
         Element currentElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
         currentElement.appendChild(this.fDocument.createTextNode(text.toString()));
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      this.characters(text, augs);
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      Node currentElement = this.fDOMValidatorHelper.getCurrentElement();
      if (augs != null && this.fDocumentImpl != null) {
         ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
         if (elementPSVI != null) {
            if (this.fStorePSVI) {
               ((PSVIElementNSImpl)currentElement).setPSVI(elementPSVI);
            }

            XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
            if (type == null) {
               type = elementPSVI.getTypeDefinition();
            }

            ((ElementNSImpl)currentElement).setType((XSTypeDefinition)type);
         }
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
   }

   public void endCDATA(Augmentations augs) throws XNIException {
   }

   public void endDocument(Augmentations augs) throws XNIException {
   }

   public void setDocumentSource(XMLDocumentSource source) {
   }

   public XMLDocumentSource getDocumentSource() {
      return null;
   }

   private boolean processAttributePSVI(AttrImpl attr, AttributePSVI attrPSVI) {
      if (this.fStorePSVI) {
         ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
      }

      Object type = attrPSVI.getMemberTypeDefinition();
      if (type == null) {
         Object type = attrPSVI.getTypeDefinition();
         if (type != null) {
            attr.setType(type);
            return ((XSSimpleType)type).isIDType();
         } else {
            return false;
         }
      } else {
         attr.setType(type);
         return ((XSSimpleType)type).isIDType();
      }
   }
}
