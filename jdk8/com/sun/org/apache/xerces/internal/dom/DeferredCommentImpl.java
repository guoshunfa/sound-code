package com.sun.org.apache.xerces.internal.dom;

public class DeferredCommentImpl extends CommentImpl implements DeferredNode {
   static final long serialVersionUID = 6498796371083589338L;
   protected transient int fNodeIndex;

   DeferredCommentImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
