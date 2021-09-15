package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityImpl extends EntityImpl implements DeferredNode {
   static final long serialVersionUID = 4760180431078941638L;
   protected transient int fNodeIndex;

   DeferredEntityImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
      this.name = ownerDocument.getNodeName(this.fNodeIndex);
      this.publicId = ownerDocument.getNodeValue(this.fNodeIndex);
      this.systemId = ownerDocument.getNodeURI(this.fNodeIndex);
      int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
      ownerDocument.getNodeType(extraDataIndex);
      this.notationName = ownerDocument.getNodeName(extraDataIndex);
      this.version = ownerDocument.getNodeValue(extraDataIndex);
      this.encoding = ownerDocument.getNodeURI(extraDataIndex);
      int extraIndex2 = ownerDocument.getNodeExtra(extraDataIndex);
      this.baseURI = ownerDocument.getNodeName(extraIndex2);
      this.inputEncoding = ownerDocument.getNodeValue(extraIndex2);
   }

   protected void synchronizeChildren() {
      this.needsSyncChildren(false);
      this.isReadOnly(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      ownerDocument.synchronizeChildren((ParentNode)this, this.fNodeIndex);
      this.setReadOnly(true, true);
   }
}
