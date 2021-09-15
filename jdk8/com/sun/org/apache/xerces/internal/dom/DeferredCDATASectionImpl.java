package com.sun.org.apache.xerces.internal.dom;

public class DeferredCDATASectionImpl extends CDATASectionImpl implements DeferredNode {
   static final long serialVersionUID = 1983580632355645726L;
   protected transient int fNodeIndex;

   DeferredCDATASectionImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
      super(ownerDocument, (String)null);
      this.fNodeIndex = nodeIndex;
      this.needsSyncData(true);
   }

   public int getNodeIndex() {
      return this.fNodeIndex;
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      this.data = ownerDocument.getNodeValueString(this.fNodeIndex);
   }
}
