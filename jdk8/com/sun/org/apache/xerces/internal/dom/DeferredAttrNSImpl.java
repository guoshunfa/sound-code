package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrNSImpl extends AttrNSImpl implements DeferredNode {
   static final long serialVersionUID = 6074924934945957154L;
   protected transient int fNodeIndex;

   DeferredAttrNSImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
      super(ownerDocument, (String)null);
      this.fNodeIndex = nodeIndex;
      this.needsSyncData(true);
      this.needsSyncChildren(true);
   }

   public int getNodeIndex() {
      return this.fNodeIndex;
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      this.name = ownerDocument.getNodeName(this.fNodeIndex);
      int index = this.name.indexOf(58);
      if (index < 0) {
         this.localName = this.name;
      } else {
         this.localName = this.name.substring(index + 1);
      }

      int extra = ownerDocument.getNodeExtra(this.fNodeIndex);
      this.isSpecified((extra & 32) != 0);
      this.isIdAttribute((extra & 512) != 0);
      this.namespaceURI = ownerDocument.getNodeURI(this.fNodeIndex);
      int extraNode = ownerDocument.getLastChild(this.fNodeIndex);
      this.type = ownerDocument.getTypeInfo(extraNode);
   }

   protected void synchronizeChildren() {
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      ownerDocument.synchronizeChildren((AttrImpl)this, this.fNodeIndex);
   }
}
