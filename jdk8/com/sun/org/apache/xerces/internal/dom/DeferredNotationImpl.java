package com.sun.org.apache.xerces.internal.dom;

public class DeferredNotationImpl extends NotationImpl implements DeferredNode {
   static final long serialVersionUID = 5705337172887990848L;
   protected transient int fNodeIndex;

   DeferredNotationImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
      this.name = ownerDocument.getNodeName(this.fNodeIndex);
      ownerDocument.getNodeType(this.fNodeIndex);
      this.publicId = ownerDocument.getNodeValue(this.fNodeIndex);
      this.systemId = ownerDocument.getNodeURI(this.fNodeIndex);
      int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
      ownerDocument.getNodeType(extraDataIndex);
      this.baseURI = ownerDocument.getNodeName(extraDataIndex);
   }
}
