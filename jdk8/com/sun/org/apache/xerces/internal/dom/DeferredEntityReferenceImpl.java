package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityReferenceImpl extends EntityReferenceImpl implements DeferredNode {
   static final long serialVersionUID = 390319091370032223L;
   protected transient int fNodeIndex;

   DeferredEntityReferenceImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
      super(ownerDocument, (String)null);
      this.fNodeIndex = nodeIndex;
      this.needsSyncData(true);
   }

   public int getNodeIndex() {
      return this.fNodeIndex;
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
      this.name = ownerDocument.getNodeName(this.fNodeIndex);
      this.baseURI = ownerDocument.getNodeValue(this.fNodeIndex);
   }

   protected void synchronizeChildren() {
      this.needsSyncChildren(false);
      this.isReadOnly(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      ownerDocument.synchronizeChildren((ParentNode)this, this.fNodeIndex);
      this.setReadOnly(true, true);
   }
}
