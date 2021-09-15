package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class ElementImpl extends ParentNode implements Element, TypeInfo {
   static final long serialVersionUID = 3717253516652722278L;
   protected String name;
   protected AttributeMap attributes;

   public ElementImpl(CoreDocumentImpl ownerDoc, String name) {
      super(ownerDoc);
      this.name = name;
      this.needsSyncData(true);
   }

   protected ElementImpl() {
   }

   void rename(String name) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.name = name;
      this.reconcileDefaultAttributes();
   }

   public short getNodeType() {
      return 1;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public NamedNodeMap getAttributes() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.attributes == null) {
         this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
      }

      return this.attributes;
   }

   public Node cloneNode(boolean deep) {
      ElementImpl newnode = (ElementImpl)super.cloneNode(deep);
      if (this.attributes != null) {
         newnode.attributes = (AttributeMap)this.attributes.cloneMap(newnode);
      }

      return newnode;
   }

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.attributes != null) {
         Attr attrNode = (Attr)this.attributes.getNamedItem("xml:base");
         if (attrNode != null) {
            String uri = attrNode.getNodeValue();
            if (uri.length() != 0) {
               try {
                  uri = (new URI(uri)).toString();
                  return uri;
               } catch (URI.MalformedURIException var8) {
                  String parentBaseURI = this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
                  if (parentBaseURI != null) {
                     try {
                        uri = (new URI(new URI(parentBaseURI), uri)).toString();
                        return uri;
                     } catch (URI.MalformedURIException var6) {
                        return null;
                     }
                  }

                  return null;
               }
            }
         }
      }

      String baseURI = this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
      if (baseURI != null) {
         try {
            return (new URI(baseURI)).toString();
         } catch (URI.MalformedURIException var7) {
            return null;
         }
      } else {
         return null;
      }
   }

   void setOwnerDocument(CoreDocumentImpl doc) {
      super.setOwnerDocument(doc);
      if (this.attributes != null) {
         this.attributes.setOwnerDocument(doc);
      }

   }

   public String getAttribute(String name) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.attributes == null) {
         return "";
      } else {
         Attr attr = (Attr)((Attr)this.attributes.getNamedItem(name));
         return attr == null ? "" : attr.getValue();
      }
   }

   public Attr getAttributeNode(String name) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.attributes == null ? null : (Attr)this.attributes.getNamedItem(name);
   }

   public NodeList getElementsByTagName(String tagname) {
      return new DeepNodeListImpl(this, tagname);
   }

   public String getTagName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public void normalize() {
      if (!this.isNormalized()) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         ChildNode next;
         for(ChildNode kid = this.firstChild; kid != null; kid = next) {
            next = kid.nextSibling;
            if (kid.getNodeType() == 3) {
               if (next != null && next.getNodeType() == 3) {
                  ((Text)kid).appendData(next.getNodeValue());
                  this.removeChild(next);
                  next = kid;
               } else if (kid.getNodeValue() == null || kid.getNodeValue().length() == 0) {
                  this.removeChild(kid);
               }
            } else if (kid.getNodeType() == 1) {
               kid.normalize();
            }
         }

         if (this.attributes != null) {
            for(int i = 0; i < this.attributes.getLength(); ++i) {
               Node attr = this.attributes.item(i);
               attr.normalize();
            }
         }

         this.isNormalized(true);
      }
   }

   public void removeAttribute(String name) {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (this.attributes != null) {
            this.attributes.safeRemoveNamedItem(name);
         }
      }
   }

   public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
      String msg;
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (this.attributes == null) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
            throw new DOMException((short)8, msg);
         } else {
            return (Attr)this.attributes.removeItem(oldAttr, true);
         }
      }
   }

   public void setAttribute(String name, String value) {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         Attr newAttr = this.getAttributeNode(name);
         if (newAttr == null) {
            newAttr = this.getOwnerDocument().createAttribute(name);
            if (this.attributes == null) {
               this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
            }

            newAttr.setNodeValue(value);
            this.attributes.setNamedItem(newAttr);
         } else {
            newAttr.setNodeValue(value);
         }

      }
   }

   public Attr setAttributeNode(Attr newAttr) throws DOMException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (newAttr.getOwnerDocument() != this.ownerDocument) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }
      }

      if (this.attributes == null) {
         this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
      }

      return (Attr)this.attributes.setNamedItem(newAttr);
   }

   public String getAttributeNS(String namespaceURI, String localName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.attributes == null) {
         return "";
      } else {
         Attr attr = (Attr)((Attr)this.attributes.getNamedItemNS(namespaceURI, localName));
         return attr == null ? "" : attr.getValue();
      }
   }

   public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         int index = qualifiedName.indexOf(58);
         String prefix;
         String localName;
         if (index < 0) {
            prefix = null;
            localName = qualifiedName;
         } else {
            prefix = qualifiedName.substring(0, index);
            localName = qualifiedName.substring(index + 1);
         }

         Attr newAttr = this.getAttributeNodeNS(namespaceURI, localName);
         if (newAttr == null) {
            Attr newAttr = this.getOwnerDocument().createAttributeNS(namespaceURI, qualifiedName);
            if (this.attributes == null) {
               this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
            }

            newAttr.setNodeValue(value);
            this.attributes.setNamedItemNS(newAttr);
         } else {
            if (newAttr instanceof AttrNSImpl) {
               String origNodeName = ((AttrNSImpl)newAttr).name;
               String newName = prefix != null ? prefix + ":" + localName : localName;
               ((AttrNSImpl)newAttr).name = newName;
               if (!newName.equals(origNodeName)) {
                  newAttr = (Attr)this.attributes.removeItem((Node)newAttr, false);
                  this.attributes.addItem((Node)newAttr);
               }
            } else {
               newAttr = new AttrNSImpl((CoreDocumentImpl)this.getOwnerDocument(), namespaceURI, qualifiedName, localName);
               this.attributes.setNamedItemNS((Node)newAttr);
            }

            ((Attr)newAttr).setNodeValue(value);
         }

      }
   }

   public void removeAttributeNS(String namespaceURI, String localName) {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (this.attributes != null) {
            this.attributes.safeRemoveNamedItemNS(namespaceURI, localName);
         }
      }
   }

   public Attr getAttributeNodeNS(String namespaceURI, String localName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.attributes == null ? null : (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
   }

   public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (newAttr.getOwnerDocument() != this.ownerDocument) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }
      }

      if (this.attributes == null) {
         this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
      }

      return (Attr)this.attributes.setNamedItemNS(newAttr);
   }

   protected int setXercesAttributeNode(Attr attr) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.attributes == null) {
         this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
      }

      return this.attributes.addItem(attr);
   }

   protected int getXercesAttribute(String namespaceURI, String localName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.attributes == null ? -1 : this.attributes.getNamedItemIndex(namespaceURI, localName);
   }

   public boolean hasAttributes() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.attributes != null && this.attributes.getLength() != 0;
   }

   public boolean hasAttribute(String name) {
      return this.getAttributeNode(name) != null;
   }

   public boolean hasAttributeNS(String namespaceURI, String localName) {
      return this.getAttributeNodeNS(namespaceURI, localName) != null;
   }

   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return new DeepNodeListImpl(this, namespaceURI, localName);
   }

   public boolean isEqualNode(Node arg) {
      if (!super.isEqualNode(arg)) {
         return false;
      } else {
         boolean hasAttrs = this.hasAttributes();
         if (hasAttrs != ((Element)arg).hasAttributes()) {
            return false;
         } else {
            if (hasAttrs) {
               NamedNodeMap map1 = this.getAttributes();
               NamedNodeMap map2 = ((Element)arg).getAttributes();
               int len = map1.getLength();
               if (len != map2.getLength()) {
                  return false;
               }

               for(int i = 0; i < len; ++i) {
                  Node n1 = map1.item(i);
                  Node n2;
                  if (n1.getLocalName() == null) {
                     n2 = map2.getNamedItem(n1.getNodeName());
                     if (n2 == null || !((NodeImpl)n1).isEqualNode(n2)) {
                        return false;
                     }
                  } else {
                     n2 = map2.getNamedItemNS(n1.getNamespaceURI(), n1.getLocalName());
                     if (n2 == null || !((NodeImpl)n1).isEqualNode(n2)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   public void setIdAttributeNode(Attr at, boolean makeId) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (at.getOwnerElement() != this) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
            throw new DOMException((short)8, msg);
         }
      }

      ((AttrImpl)at).isIdAttribute(makeId);
      if (!makeId) {
         this.ownerDocument.removeIdentifier(at.getValue());
      } else {
         this.ownerDocument.putIdentifier(at.getValue(), this);
      }

   }

   public void setIdAttribute(String name, boolean makeId) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      Attr at = this.getAttributeNode(name);
      String msg;
      if (at == null) {
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
         throw new DOMException((short)8, msg);
      } else {
         if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
               throw new DOMException((short)7, msg);
            }

            if (at.getOwnerElement() != this) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
               throw new DOMException((short)8, msg);
            }
         }

         ((AttrImpl)at).isIdAttribute(makeId);
         if (!makeId) {
            this.ownerDocument.removeIdentifier(at.getValue());
         } else {
            this.ownerDocument.putIdentifier(at.getValue(), this);
         }

      }
   }

   public void setIdAttributeNS(String namespaceURI, String localName, boolean makeId) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (namespaceURI != null) {
         namespaceURI = namespaceURI.length() == 0 ? null : namespaceURI;
      }

      Attr at = this.getAttributeNodeNS(namespaceURI, localName);
      String msg;
      if (at == null) {
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
         throw new DOMException((short)8, msg);
      } else {
         if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
               throw new DOMException((short)7, msg);
            }

            if (at.getOwnerElement() != this) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
               throw new DOMException((short)8, msg);
            }
         }

         ((AttrImpl)at).isIdAttribute(makeId);
         if (!makeId) {
            this.ownerDocument.removeIdentifier(at.getValue());
         } else {
            this.ownerDocument.putIdentifier(at.getValue(), this);
         }

      }
   }

   public String getTypeName() {
      return null;
   }

   public String getTypeNamespace() {
      return null;
   }

   public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
      return false;
   }

   public TypeInfo getSchemaTypeInfo() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this;
   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      super.setReadOnly(readOnly, deep);
      if (this.attributes != null) {
         this.attributes.setReadOnly(readOnly, true);
      }

   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      boolean orig = this.ownerDocument.getMutationEvents();
      this.ownerDocument.setMutationEvents(false);
      this.setupDefaultAttributes();
      this.ownerDocument.setMutationEvents(orig);
   }

   void moveSpecifiedAttributes(ElementImpl el) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (el.hasAttributes()) {
         if (this.attributes == null) {
            this.attributes = new AttributeMap(this, (NamedNodeMapImpl)null);
         }

         this.attributes.moveSpecifiedAttributes(el.attributes);
      }

   }

   protected void setupDefaultAttributes() {
      NamedNodeMapImpl defaults = this.getDefaultAttributes();
      if (defaults != null) {
         this.attributes = new AttributeMap(this, defaults);
      }

   }

   protected void reconcileDefaultAttributes() {
      if (this.attributes != null) {
         NamedNodeMapImpl defaults = this.getDefaultAttributes();
         this.attributes.reconcileDefaults(defaults);
      }

   }

   protected NamedNodeMapImpl getDefaultAttributes() {
      DocumentTypeImpl doctype = (DocumentTypeImpl)this.ownerDocument.getDoctype();
      if (doctype == null) {
         return null;
      } else {
         ElementDefinitionImpl eldef = (ElementDefinitionImpl)doctype.getElements().getNamedItem(this.getNodeName());
         return eldef == null ? null : (NamedNodeMapImpl)eldef.getAttributes();
      }
   }
}
