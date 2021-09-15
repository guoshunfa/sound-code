package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrImpl extends AttrImpl implements DeferredNode {
   static final long serialVersionUID = 6903232312469148636L;
   protected transient int fNodeIndex;

   DeferredAttrImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
      int extra = ownerDocument.getNodeExtra(this.fNodeIndex);
      this.isSpecified((extra & 32) != 0);
      this.isIdAttribute((extra & 512) != 0);
      int extraNode = ownerDocument.getLastChild(this.fNodeIndex);
      this.type = ownerDocument.getTypeInfo(extraNode);
   }

   protected void synchronizeChildren() {
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      ownerDocument.synchronizeChildren((AttrImpl)this, this.fNodeIndex);
   }
}
