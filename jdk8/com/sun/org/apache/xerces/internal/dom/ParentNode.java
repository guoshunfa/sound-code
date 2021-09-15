package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public abstract class ParentNode extends ChildNode {
   static final long serialVersionUID = 2815829867152120872L;
   protected CoreDocumentImpl ownerDocument;
   protected ChildNode firstChild = null;
   protected transient NodeListCache fNodeListCache = null;

   protected ParentNode(CoreDocumentImpl ownerDocument) {
      super(ownerDocument);
      this.ownerDocument = ownerDocument;
   }

   public ParentNode() {
   }

   public Node cloneNode(boolean deep) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      ParentNode newnode = (ParentNode)super.cloneNode(deep);
      newnode.ownerDocument = this.ownerDocument;
      newnode.firstChild = null;
      newnode.fNodeListCache = null;
      if (deep) {
         for(ChildNode child = this.firstChild; child != null; child = child.nextSibling) {
            newnode.appendChild(child.cloneNode(true));
         }
      }

      return newnode;
   }

   public Document getOwnerDocument() {
      return this.ownerDocument;
   }

   CoreDocumentImpl ownerDocument() {
      return this.ownerDocument;
   }

   void setOwnerDocument(CoreDocumentImpl doc) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      for(ChildNode child = this.firstChild; child != null; child = child.nextSibling) {
         child.setOwnerDocument(doc);
      }

      super.setOwnerDocument(doc);
      this.ownerDocument = doc;
   }

   public boolean hasChildNodes() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.firstChild != null;
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

      return this.firstChild;
   }

   public Node getLastChild() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.lastChild();
   }

   final ChildNode lastChild() {
      return this.firstChild != null ? this.firstChild.previousSibling : null;
   }

   final void lastChild(ChildNode node) {
      if (this.firstChild != null) {
         this.firstChild.previousSibling = node;
      }

   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException {
      return this.internalInsertBefore(newChild, refChild, false);
   }

   Node internalInsertBefore(Node newChild, Node refChild, boolean replace) throws DOMException {
      boolean errorChecking = this.ownerDocument.errorChecking;
      if (newChild.getNodeType() == 11) {
         if (errorChecking) {
            for(Node kid = newChild.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
               if (!this.ownerDocument.isKidOK(this, kid)) {
                  throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
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
            if (this.isReadOnly()) {
               throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
            }

            if (newChild.getOwnerDocument() != this.ownerDocument && newChild != this.ownerDocument) {
               throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
            }

            if (!this.ownerDocument.isKidOK(this, newChild)) {
               throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
            }

            if (refChild != null && refChild.getParentNode() != this) {
               throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null));
            }

            if (this.ownerDocument.ancestorChecking) {
               boolean treeSafe = true;

               for(Object a = this; treeSafe && a != null; a = ((NodeImpl)a).parentNode()) {
                  treeSafe = newChild != a;
               }

               if (!treeSafe) {
                  throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
               }
            }
         }

         this.ownerDocument.insertingNode(this, replace);
         ChildNode newInternal = (ChildNode)newChild;
         Node oldparent = newInternal.parentNode();
         if (oldparent != null) {
            oldparent.removeChild(newInternal);
         }

         ChildNode refInternal = (ChildNode)refChild;
         newInternal.ownerNode = this;
         newInternal.isOwned(true);
         if (this.firstChild == null) {
            this.firstChild = newInternal;
            newInternal.isFirstChild(true);
            newInternal.previousSibling = newInternal;
         } else {
            ChildNode prev;
            if (refInternal == null) {
               prev = this.firstChild.previousSibling;
               prev.nextSibling = newInternal;
               newInternal.previousSibling = prev;
               this.firstChild.previousSibling = newInternal;
            } else if (refChild == this.firstChild) {
               this.firstChild.isFirstChild(false);
               newInternal.nextSibling = this.firstChild;
               newInternal.previousSibling = this.firstChild.previousSibling;
               this.firstChild.previousSibling = newInternal;
               this.firstChild = newInternal;
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
         if (this.fNodeListCache != null) {
            if (this.fNodeListCache.fLength != -1) {
               ++this.fNodeListCache.fLength;
            }

            if (this.fNodeListCache.fChildIndex != -1) {
               if (this.fNodeListCache.fChild == refInternal) {
                  this.fNodeListCache.fChild = newInternal;
               } else {
                  this.fNodeListCache.fChildIndex = -1;
               }
            }
         }

         this.ownerDocument.insertedNode(this, newInternal, replace);
         this.checkNormalizationAfterInsert(newInternal);
         return newChild;
      }
   }

   public Node removeChild(Node oldChild) throws DOMException {
      return this.internalRemoveChild(oldChild, false);
   }

   Node internalRemoveChild(Node oldChild, boolean replace) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      if (ownerDocument.errorChecking) {
         if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
         }

         if (oldChild != null && oldChild.getParentNode() != this) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null));
         }
      }

      ChildNode oldInternal = (ChildNode)oldChild;
      ownerDocument.removingNode(this, oldInternal, replace);
      if (this.fNodeListCache != null) {
         if (this.fNodeListCache.fLength != -1) {
            --this.fNodeListCache.fLength;
         }

         if (this.fNodeListCache.fChildIndex != -1) {
            if (this.fNodeListCache.fChild == oldInternal) {
               --this.fNodeListCache.fChildIndex;
               this.fNodeListCache.fChild = oldInternal.previousSibling();
            } else {
               this.fNodeListCache.fChildIndex = -1;
            }
         }
      }

      ChildNode oldPreviousSibling;
      if (oldInternal == this.firstChild) {
         oldInternal.isFirstChild(false);
         this.firstChild = oldInternal.nextSibling;
         if (this.firstChild != null) {
            this.firstChild.isFirstChild(true);
            this.firstChild.previousSibling = oldInternal.previousSibling;
         }
      } else {
         oldPreviousSibling = oldInternal.previousSibling;
         ChildNode next = oldInternal.nextSibling;
         oldPreviousSibling.nextSibling = next;
         if (next == null) {
            this.firstChild.previousSibling = oldPreviousSibling;
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
      this.ownerDocument.replacingNode(this);
      this.internalInsertBefore(newChild, oldChild, true);
      if (newChild != oldChild) {
         this.internalRemoveChild(oldChild, true);
      }

      this.ownerDocument.replacedNode(this);
      return oldChild;
   }

   public String getTextContent() throws DOMException {
      Node child = this.getFirstChild();
      if (child != null) {
         Node next = child.getNextSibling();
         if (next == null) {
            return this.hasTextContent(child) ? ((NodeImpl)child).getTextContent() : "";
         } else {
            if (this.fBufferStr == null) {
               this.fBufferStr = new StringBuffer();
            } else {
               this.fBufferStr.setLength(0);
            }

            this.getTextContent(this.fBufferStr);
            return this.fBufferStr.toString();
         }
      } else {
         return "";
      }
   }

   void getTextContent(StringBuffer buf) throws DOMException {
      for(Node child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (this.hasTextContent(child)) {
            ((NodeImpl)child).getTextContent(buf);
         }
      }

   }

   final boolean hasTextContent(Node child) {
      return child.getNodeType() != 8 && child.getNodeType() != 7 && (child.getNodeType() != 3 || !((TextImpl)child).isIgnorableWhitespace());
   }

   public void setTextContent(String textContent) throws DOMException {
      Node child;
      while((child = this.getFirstChild()) != null) {
         this.removeChild(child);
      }

      if (textContent != null && textContent.length() != 0) {
         this.appendChild(this.ownerDocument().createTextNode(textContent));
      }

   }

   private int nodeListGetLength() {
      if (this.fNodeListCache == null) {
         if (this.firstChild == null) {
            return 0;
         }

         if (this.firstChild == this.lastChild()) {
            return 1;
         }

         this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
      }

      if (this.fNodeListCache.fLength == -1) {
         int l;
         ChildNode n;
         if (this.fNodeListCache.fChildIndex != -1 && this.fNodeListCache.fChild != null) {
            l = this.fNodeListCache.fChildIndex;
            n = this.fNodeListCache.fChild;
         } else {
            n = this.firstChild;
            l = 0;
         }

         while(n != null) {
            ++l;
            n = n.nextSibling;
         }

         this.fNodeListCache.fLength = l;
      }

      return this.fNodeListCache.fLength;
   }

   public int getLength() {
      return this.nodeListGetLength();
   }

   private Node nodeListItem(int index) {
      if (this.fNodeListCache == null) {
         if (this.firstChild == this.lastChild()) {
            return index == 0 ? this.firstChild : null;
         }

         this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
      }

      int i = this.fNodeListCache.fChildIndex;
      ChildNode n = this.fNodeListCache.fChild;
      boolean firstAccess = true;
      if (i != -1 && n != null) {
         firstAccess = false;
         if (i < index) {
            while(i < index && n != null) {
               ++i;
               n = n.nextSibling;
            }
         } else if (i > index) {
            while(i > index && n != null) {
               --i;
               n = n.previousSibling();
            }
         }
      } else {
         if (index < 0) {
            return null;
         }

         n = this.firstChild;

         for(i = 0; i < index && n != null; ++i) {
            n = n.nextSibling;
         }
      }

      if (firstAccess || n != this.firstChild && n != this.lastChild()) {
         this.fNodeListCache.fChildIndex = i;
         this.fNodeListCache.fChild = n;
      } else {
         this.fNodeListCache.fChildIndex = -1;
         this.fNodeListCache.fChild = null;
         this.ownerDocument.freeNodeListCache(this.fNodeListCache);
      }

      return n;
   }

   public Node item(int index) {
      return this.nodeListItem(index);
   }

   protected final NodeList getChildNodesUnoptimized() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return new NodeList() {
         public int getLength() {
            return ParentNode.this.nodeListGetLength();
         }

         public Node item(int index) {
            return ParentNode.this.nodeListItem(index);
         }
      };
   }

   public void normalize() {
      if (!this.isNormalized()) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         for(ChildNode kid = this.firstChild; kid != null; kid = kid.nextSibling) {
            kid.normalize();
         }

         this.isNormalized(true);
      }
   }

   public boolean isEqualNode(Node arg) {
      if (!super.isEqualNode(arg)) {
         return false;
      } else {
         Node child1 = this.getFirstChild();

         Node child2;
         for(child2 = arg.getFirstChild(); child1 != null && child2 != null; child2 = child2.getNextSibling()) {
            if (!((NodeImpl)child1).isEqualNode(child2)) {
               return false;
            }

            child1 = child1.getNextSibling();
         }

         return child1 == child2;
      }
   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      super.setReadOnly(readOnly, deep);
      if (deep) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         for(ChildNode mykid = this.firstChild; mykid != null; mykid = mykid.nextSibling) {
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

   protected class UserDataRecord implements Serializable {
      private static final long serialVersionUID = 3258126977134310455L;
      Object fData;
      UserDataHandler fHandler;

      UserDataRecord(Object data, UserDataHandler handler) {
         this.fData = data;
         this.fHandler = handler;
      }
   }
}
