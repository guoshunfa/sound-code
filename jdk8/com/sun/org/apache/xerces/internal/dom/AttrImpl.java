package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class AttrImpl extends NodeImpl implements Attr, TypeInfo {
   static final long serialVersionUID = 7277707688218972102L;
   static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
   protected Object value = null;
   protected String name;
   transient Object type;
   protected TextImpl textNode = null;

   protected AttrImpl(CoreDocumentImpl ownerDocument, String name) {
      super(ownerDocument);
      this.name = name;
      this.isSpecified(true);
      this.hasStringValue(true);
   }

   protected AttrImpl() {
   }

   void rename(String name) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.name = name;
   }

   protected void makeChildNode() {
      if (this.hasStringValue()) {
         if (this.value != null) {
            TextImpl text = (TextImpl)this.ownerDocument().createTextNode((String)this.value);
            this.value = text;
            text.isFirstChild(true);
            text.previousSibling = text;
            text.ownerNode = this;
            text.isOwned(true);
         }

         this.hasStringValue(false);
      }

   }

   void setOwnerDocument(CoreDocumentImpl doc) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      super.setOwnerDocument(doc);
      if (!this.hasStringValue()) {
         for(ChildNode child = (ChildNode)this.value; child != null; child = child.nextSibling) {
            child.setOwnerDocument(doc);
         }
      }

   }

   public void setIdAttribute(boolean id) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.isIdAttribute(id);
   }

   public boolean isId() {
      return this.isIdAttribute();
   }

   public Node cloneNode(boolean deep) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      AttrImpl clone = (AttrImpl)super.cloneNode(deep);
      if (!clone.hasStringValue()) {
         clone.value = null;

         for(Node child = (Node)this.value; child != null; child = child.getNextSibling()) {
            clone.appendChild(child.cloneNode(true));
         }
      }

      clone.isSpecified(true);
      return clone;
   }

   public short getNodeType() {
      return 2;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public void setNodeValue(String value) throws DOMException {
      this.setValue(value);
   }

   public String getTypeName() {
      return (String)this.type;
   }

   public String getTypeNamespace() {
      return this.type != null ? "http://www.w3.org/TR/REC-xml" : null;
   }

   public TypeInfo getSchemaTypeInfo() {
      return this;
   }

   public String getNodeValue() {
      return this.getValue();
   }

   public String getName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public void setValue(String newvalue) {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      if (ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         Element ownerElement = this.getOwnerElement();
         String oldvalue = "";
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         if (this.value != null) {
            if (ownerDocument.getMutationEvents()) {
               if (this.hasStringValue()) {
                  oldvalue = (String)this.value;
                  if (this.textNode == null) {
                     this.textNode = (TextImpl)ownerDocument.createTextNode((String)this.value);
                  } else {
                     this.textNode.data = (String)this.value;
                  }

                  this.value = this.textNode;
                  this.textNode.isFirstChild(true);
                  this.textNode.previousSibling = this.textNode;
                  this.textNode.ownerNode = this;
                  this.textNode.isOwned(true);
                  this.hasStringValue(false);
                  this.internalRemoveChild(this.textNode, true);
               } else {
                  oldvalue = this.getValue();

                  while(this.value != null) {
                     this.internalRemoveChild((Node)this.value, true);
                  }
               }
            } else {
               if (this.hasStringValue()) {
                  oldvalue = (String)this.value;
               } else {
                  oldvalue = this.getValue();
                  ChildNode firstChild = (ChildNode)this.value;
                  firstChild.previousSibling = null;
                  firstChild.isFirstChild(false);
                  firstChild.ownerNode = ownerDocument;
               }

               this.value = null;
               this.needsSyncChildren(false);
            }

            if (this.isIdAttribute() && ownerElement != null) {
               ownerDocument.removeIdentifier(oldvalue);
            }
         }

         this.isSpecified(true);
         if (ownerDocument.getMutationEvents()) {
            this.internalInsertBefore(ownerDocument.createTextNode(newvalue), (Node)null, true);
            this.hasStringValue(false);
            ownerDocument.modifiedAttrValue(this, oldvalue);
         } else {
            this.value = newvalue;
            this.hasStringValue(true);
            this.changed();
         }

         if (this.isIdAttribute() && ownerElement != null) {
            ownerDocument.putIdentifier(newvalue, ownerElement);
         }

      }
   }

   public String getValue() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      if (this.value == null) {
         return "";
      } else if (this.hasStringValue()) {
         return (String)this.value;
      } else {
         ChildNode firstChild = (ChildNode)this.value;
         String data = null;
         if (firstChild.getNodeType() == 5) {
            data = ((EntityReferenceImpl)firstChild).getEntityRefValue();
         } else {
            data = firstChild.getNodeValue();
         }

         ChildNode node = firstChild.nextSibling;
         if (node != null && data != null) {
            StringBuffer value;
            for(value = new StringBuffer(data); node != null; node = node.nextSibling) {
               if (node.getNodeType() == 5) {
                  data = ((EntityReferenceImpl)node).getEntityRefValue();
                  if (data == null) {
                     return "";
                  }

                  value.append(data);
               } else {
                  value.append(node.getNodeValue());
               }
            }

            return value.toString();
         } else {
            return data == null ? "" : data;
         }
      }
   }

   public boolean getSpecified() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.isSpecified();
   }

   /** @deprecated */
   public Element getElement() {
      return (Element)((Element)(this.isOwned() ? this.ownerNode : null));
   }

   public Element getOwnerElement() {
      return (Element)((Element)(this.isOwned() ? this.ownerNode : null));
   }

   public void normalize() {
      if (!this.isNormalized() && !this.hasStringValue()) {
         ChildNode firstChild = (ChildNode)this.value;

         Object next;
         for(Object kid = firstChild; kid != null; kid = next) {
            next = ((Node)kid).getNextSibling();
            if (((Node)kid).getNodeType() == 3) {
               if (next != null && ((Node)next).getNodeType() == 3) {
                  ((Text)kid).appendData(((Node)next).getNodeValue());
                  this.removeChild((Node)next);
                  next = kid;
               } else if (((Node)kid).getNodeValue() == null || ((Node)kid).getNodeValue().length() == 0) {
                  this.removeChild((Node)kid);
               }
            }
         }

         this.isNormalized(true);
      }
   }

   public void setSpecified(boolean arg) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.isSpecified(arg);
   }

   public void setType(Object type) {
      this.type = type;
   }

   public String toString() {
      return this.getName() + "=\"" + this.getValue() + "\"";
   }

   public boolean hasChildNodes() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.value != null;
   }

   public NodeList getChildNodes() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this;
   }

   public Node getFirstChild() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      this.makeChildNode();
      return (Node)this.value;
   }

   public Node getLastChild() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.lastChild();
   }

   final ChildNode lastChild() {
      this.makeChildNode();
      return this.value != null ? ((ChildNode)this.value).previousSibling : null;
   }

   final void lastChild(ChildNode node) {
      if (this.value != null) {
         ((ChildNode)this.value).previousSibling = node;
      }

   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException {
      return this.internalInsertBefore(newChild, refChild, false);
   }

   Node internalInsertBefore(Node newChild, Node refChild, boolean replace) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      boolean errorChecking = ownerDocument.errorChecking;
      String msg;
      if (newChild.getNodeType() == 11) {
         if (errorChecking) {
            for(Node kid = newChild.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
               if (!ownerDocument.isKidOK(this, kid)) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
                  throw new DOMException((short)3, msg);
               }
            }
         }

         while(newChild.hasChildNodes()) {
            this.insertBefore(newChild.getFirstChild(), refChild);
         }

         return newChild;
      } else if (newChild == refChild) {
         refChild = refChild.getNextSibling();
         this.removeChild(newChild);
         this.insertBefore(newChild, refChild);
         return newChild;
      } else {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         if (errorChecking) {
            String msg;
            if (this.isReadOnly()) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
               throw new DOMException((short)7, msg);
            }

            if (newChild.getOwnerDocument() != ownerDocument) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
               throw new DOMException((short)4, msg);
            }

            if (!ownerDocument.isKidOK(this, newChild)) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
               throw new DOMException((short)3, msg);
            }

            if (refChild != null && refChild.getParentNode() != this) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
               throw new DOMException((short)8, msg);
            }

            boolean treeSafe = true;

            for(Object a = this; treeSafe && a != null; a = ((NodeImpl)a).parentNode()) {
               treeSafe = newChild != a;
            }

            if (!treeSafe) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
               throw new DOMException((short)3, msg);
            }
         }

         this.makeChildNode();
         ownerDocument.insertingNode(this, replace);
         ChildNode newInternal = (ChildNode)newChild;
         Node oldparent = newInternal.parentNode();
         if (oldparent != null) {
            oldparent.removeChild(newInternal);
         }

         ChildNode refInternal = (ChildNode)refChild;
         newInternal.ownerNode = this;
         newInternal.isOwned(true);
         ChildNode firstChild = (ChildNode)this.value;
         if (firstChild == null) {
            this.value = newInternal;
            newInternal.isFirstChild(true);
            newInternal.previousSibling = newInternal;
         } else {
            ChildNode prev;
            if (refInternal == null) {
               prev = firstChild.previousSibling;
               prev.nextSibling = newInternal;
               newInternal.previousSibling = prev;
               firstChild.previousSibling = newInternal;
            } else if (refChild == firstChild) {
               firstChild.isFirstChild(false);
               newInternal.nextSibling = firstChild;
               newInternal.previousSibling = firstChild.previousSibling;
               firstChild.previousSibling = newInternal;
               this.value = newInternal;
               newInternal.isFirstChild(true);
            } else {
               prev = refInternal.previousSibling;
               newInternal.nextSibling = refInternal;
               prev.nextSibling = newInternal;
               refInternal.previousSibling = newInternal;
               newInternal.previousSibling = prev;
            }
         }

         this.changed();
         ownerDocument.insertedNode(this, newInternal, replace);
         this.checkNormalizationAfterInsert(newInternal);
         return newChild;
      }
   }

   public Node removeChild(Node oldChild) throws DOMException {
      if (this.hasStringValue()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
         throw new DOMException((short)8, msg);
      } else {
         return this.internalRemoveChild(oldChild, false);
      }
   }

   Node internalRemoveChild(Node oldChild, boolean replace) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      if (ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (oldChild != null && oldChild.getParentNode() != this) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
            throw new DOMException((short)8, msg);
         }
      }

      ChildNode oldInternal = (ChildNode)oldChild;
      ownerDocument.removingNode(this, oldInternal, replace);
      ChildNode oldPreviousSibling;
      if (oldInternal == this.value) {
         oldInternal.isFirstChild(false);
         this.value = oldInternal.nextSibling;
         oldPreviousSibling = (ChildNode)this.value;
         if (oldPreviousSibling != null) {
            oldPreviousSibling.isFirstChild(true);
            oldPreviousSibling.previousSibling = oldInternal.previousSibling;
         }
      } else {
         oldPreviousSibling = oldInternal.previousSibling;
         ChildNode next = oldInternal.nextSibling;
         oldPreviousSibling.nextSibling = next;
         if (next == null) {
            ChildNode firstChild = (ChildNode)this.value;
            firstChild.previousSibling = oldPreviousSibling;
         } else {
            next.previousSibling = oldPreviousSibling;
         }
      }

      oldPreviousSibling = oldInternal.previousSibling();
      oldInternal.ownerNode = ownerDocument;
      oldInternal.isOwned(false);
      oldInternal.nextSibling = null;
      oldInternal.previousSibling = null;
      this.changed();
      ownerDocument.removedNode(this, replace);
      this.checkNormalizationAfterRemove(oldPreviousSibling);
      return oldInternal;
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
      this.makeChildNode();
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      ownerDocument.replacingNode(this);
      this.internalInsertBefore(newChild, oldChild, true);
      if (newChild != oldChild) {
         this.internalRemoveChild(oldChild, true);
      }

      ownerDocument.replacedNode(this);
      return oldChild;
   }

   public int getLength() {
      if (this.hasStringValue()) {
         return 1;
      } else {
         ChildNode node = (ChildNode)this.value;

         int length;
         for(length = 0; node != null; node = node.nextSibling) {
            ++length;
         }

         return length;
      }
   }

   public Node item(int index) {
      if (this.hasStringValue()) {
         if (index == 0 && this.value != null) {
            this.makeChildNode();
            return (Node)this.value;
         } else {
            return null;
         }
      } else if (index < 0) {
         return null;
      } else {
         ChildNode node = (ChildNode)this.value;

         for(int i = 0; i < index && node != null; ++i) {
            node = node.nextSibling;
         }

         return node;
      }
   }

   public boolean isEqualNode(Node arg) {
      return super.isEqualNode(arg);
   }

   public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
      return false;
   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      super.setReadOnly(readOnly, deep);
      if (deep) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         if (this.hasStringValue()) {
            return;
         }

         for(ChildNode mykid = (ChildNode)this.value; mykid != null; mykid = mykid.nextSibling) {
            if (mykid.getNodeType() != 5) {
               mykid.setReadOnly(readOnly, true);
            }
         }
      }

   }

   protected void synchronizeChildren() {
      this.needsSyncChildren(false);
   }

   void checkNormalizationAfterInsert(ChildNode insertedChild) {
      if (insertedChild.getNodeType() == 3) {
         ChildNode prev = insertedChild.previousSibling();
         ChildNode next = insertedChild.nextSibling;
         if (prev != null && prev.getNodeType() == 3 || next != null && next.getNodeType() == 3) {
            this.isNormalized(false);
         }
      } else if (!insertedChild.isNormalized()) {
         this.isNormalized(false);
      }

   }

   void checkNormalizationAfterRemove(ChildNode previousSibling) {
      if (previousSibling != null && previousSibling.getNodeType() == 3) {
         ChildNode next = previousSibling.nextSibling;
         if (next != null && next.getNodeType() == 3) {
            this.isNormalized(false);
         }
      }

   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      out.defaultWriteObject();
   }

   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();
      this.needsSyncChildren(false);
   }
}
