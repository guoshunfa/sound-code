package com.sun.org.apache.xerces.internal.dom;

public class DeferredProcessingInstructionImpl extends ProcessingInstructionImpl implements DeferredNode {
   static final long serialVersionUID = -4643577954293565388L;
   protected transient int fNodeIndex;

   DeferredProcessingInstructionImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
      super(ownerDocument, (String)null, (String)null);
      this.fNodeIndex = nodeIndex;
      this.needsSyncData(true);
   }

   public int getNodeIndex() {
      return this.fNodeIndex;
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      this.target = ownerDocument.getNodeName(this.fNodeIndex);
      this.data = ownerDocument.getNodeValueString(this.fNodeIndex);
   }
}
