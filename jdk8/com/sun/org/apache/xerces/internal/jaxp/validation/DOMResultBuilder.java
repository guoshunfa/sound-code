package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
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
import java.util.ArrayList;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultBuilder implements DOMDocumentHandler {
   private static final int[] kidOK = new int[13];
   private Document fDocument;
   private CoreDocumentImpl fDocumentImpl;
   private boolean fStorePSVI;
   private Node fTarget;
   private Node fNextSibling;
   private Node fCurrentNode;
   private Node fFragmentRoot;
   private final ArrayList fTargetChildren = new ArrayList();
   private boolean fIgnoreChars;
   private final QName fAttributeQName = new QName();

   public DOMResultBuilder() {
   }

   public void setDOMResult(DOMResult result) {
      this.fCurrentNode = null;
      this.fFragmentRoot = null;
      this.fIgnoreChars = false;
      this.fTargetChildren.clear();
      if (result != null) {
         this.fTarget = result.getNode();
         this.fNextSibling = result.getNextSibling();
         this.fDocument = this.fTarget.getNodeType() == 9 ? (Document)this.fTarget : this.fTarget.getOwnerDocument();
         this.fDocumentImpl = this.fDocument instanceof CoreDocumentImpl ? (CoreDocumentImpl)this.fDocument : null;
         this.fStorePSVI = this.fDocument instanceof PSVIDocumentImpl;
      } else {
         this.fTarget = null;
         this.fNextSibling = null;
         this.fDocument = null;
         this.fDocumentImpl = null;
         this.fStorePSVI = false;
      }
   }

   public void doctypeDecl(DocumentType node) throws XNIException {
      if (this.fDocumentImpl != null) {
         DocumentType docType = this.fDocumentImpl.createDocumentType(node.getName(), node.getPublicId(), node.getSystemId());
         String internalSubset = node.getInternalSubset();
         if (internalSubset != null) {
            ((DocumentTypeImpl)docType).setInternalSubset(internalSubset);
         }

         NamedNodeMap oldMap = node.getEntities();
         NamedNodeMap newMap = docType.getEntities();
         int length = oldMap.getLength();

         int i;
         for(i = 0; i < length; ++i) {
            Entity oldEntity = (Entity)oldMap.item(i);
            EntityImpl newEntity = (EntityImpl)this.fDocumentImpl.createEntity(oldEntity.getNodeName());
            newEntity.setPublicId(oldEntity.getPublicId());
            newEntity.setSystemId(oldEntity.getSystemId());
            newEntity.setNotationName(oldEntity.getNotationName());
            newMap.setNamedItem(newEntity);
         }

         oldMap = node.getNotations();
         newMap = docType.getNotations();
         length = oldMap.getLength();

         for(i = 0; i < length; ++i) {
            Notation oldNotation = (Notation)oldMap.item(i);
            NotationImpl newNotation = (NotationImpl)this.fDocumentImpl.createNotation(oldNotation.getNodeName());
            newNotation.setPublicId(oldNotation.getPublicId());
            newNotation.setSystemId(oldNotation.getSystemId());
            newMap.setNamedItem(newNotation);
         }

         this.append(docType);
      }

   }

   public void characters(Text node) throws XNIException {
      this.append(this.fDocument.createTextNode(node.getNodeValue()));
   }

   public void cdata(CDATASection node) throws XNIException {
      this.append(this.fDocument.createCDATASection(node.getNodeValue()));
   }

   public void comment(Comment node) throws XNIException {
      this.append(this.fDocument.createComment(node.getNodeValue()));
   }

   public void processingInstruction(ProcessingInstruction node) throws XNIException {
      this.append(this.fDocument.createProcessingInstruction(node.getTarget(), node.getData()));
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
      int attrCount = attributes.getLength();
      Element elem;
      int i;
      if (this.fDocumentImpl == null) {
         elem = this.fDocument.createElementNS(element.uri, element.rawname);

         for(i = 0; i < attrCount; ++i) {
            attributes.getName(i, this.fAttributeQName);
            elem.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, attributes.getValue(i));
         }
      } else {
         elem = this.fDocumentImpl.createElementNS(element.uri, element.rawname, element.localpart);

         for(i = 0; i < attrCount; ++i) {
            attributes.getName(i, this.fAttributeQName);
            AttrImpl attr = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
            attr.setValue(attributes.getValue(i));
            AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            if (attrPSVI != null) {
               if (this.fStorePSVI) {
                  ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
               }

               Object type = attrPSVI.getMemberTypeDefinition();
               if (type == null) {
                  Object type = attrPSVI.getTypeDefinition();
                  if (type != null) {
                     attr.setType(type);
                     if (((XSSimpleType)type).isIDType()) {
                        ((ElementImpl)elem).setIdAttributeNode(attr, true);
                     }
                  }
               } else {
                  attr.setType(type);
                  if (((XSSimpleType)type).isIDType()) {
                     ((ElementImpl)elem).setIdAttributeNode(attr, true);
                  }
               }
            }

            attr.setSpecified(attributes.isSpecified(i));
            elem.setAttributeNode(attr);
         }
      }

      this.append(elem);
      this.fCurrentNode = elem;
      if (this.fFragmentRoot == null) {
         this.fFragmentRoot = elem;
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
         this.append(this.fDocument.createTextNode(text.toString()));
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      this.characters(text, augs);
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (augs != null && this.fDocumentImpl != null) {
         ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
         if (elementPSVI != null) {
            if (this.fStorePSVI) {
               ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
            }

            XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
            if (type == null) {
               type = elementPSVI.getTypeDefinition();
            }

            ((ElementNSImpl)this.fCurrentNode).setType((XSTypeDefinition)type);
         }
      }

      if (this.fCurrentNode == this.fFragmentRoot) {
         this.fCurrentNode = null;
         this.fFragmentRoot = null;
      } else {
         this.fCurrentNode = this.fCurrentNode.getParentNode();
      }
   }

   public void startCDATA(Augmentations augs) throws XNIException {
   }

   public void endCDATA(Augmentations augs) throws XNIException {
   }

   public void endDocument(Augmentations augs) throws XNIException {
      int length = this.fTargetChildren.size();
      int i;
      if (this.fNextSibling == null) {
         for(i = 0; i < length; ++i) {
            this.fTarget.appendChild((Node)this.fTargetChildren.get(i));
         }
      } else {
         for(i = 0; i < length; ++i) {
            this.fTarget.insertBefore((Node)this.fTargetChildren.get(i), this.fNextSibling);
         }
      }

   }

   public void setDocumentSource(XMLDocumentSource source) {
   }

   public XMLDocumentSource getDocumentSource() {
      return null;
   }

   private void append(Node node) throws XNIException {
      if (this.fCurrentNode != null) {
         this.fCurrentNode.appendChild(node);
      } else {
         if ((kidOK[this.fTarget.getNodeType()] & 1 << node.getNodeType()) == 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
            throw new XNIException(msg);
         }

         this.fTargetChildren.add(node);
      }

   }

   static {
      kidOK[9] = 1410;
      kidOK[11] = kidOK[6] = kidOK[5] = kidOK[1] = 442;
      kidOK[2] = 40;
      kidOK[10] = 0;
      kidOK[7] = 0;
      kidOK[8] = 0;
      kidOK[3] = 0;
      kidOK[4] = 0;
      kidOK[12] = 0;
   }
}
