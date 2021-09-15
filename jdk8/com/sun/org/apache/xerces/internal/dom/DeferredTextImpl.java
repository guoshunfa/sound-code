package com.sun.org.apache.xerces.internal.dom;

public class DeferredTextImpl extends TextImpl implements DeferredNode {
   static final long serialVersionUID = 2310613872100393425L;
   protected transient int fNodeIndex;

   DeferredTextImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
      this.isIgnorableWhitespace(ownerDocument.getNodeExtra(this.fNodeIndex) == 1);
   }
}
