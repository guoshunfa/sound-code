package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public class DeferredElementDefinitionImpl extends ElementDefinitionImpl implements DeferredNode {
   static final long serialVersionUID = 6703238199538041591L;
   protected transient int fNodeIndex;

   DeferredElementDefinitionImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
   }

   protected void synchronizeChildren() {
      boolean orig = this.ownerDocument.getMutationEvents();
      this.ownerDocument.setMutationEvents(false);
      this.needsSyncChildren(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
      this.attributes = new NamedNodeMapImpl(ownerDocument);

      for(int nodeIndex = ownerDocument.getLastChild(this.fNodeIndex); nodeIndex != -1; nodeIndex = ownerDocument.getPrevSibling(nodeIndex)) {
         Node attr = ownerDocument.getNodeObject(nodeIndex);
         this.attributes.setNamedItem(attr);
      }

      ownerDocument.setMutationEvents(orig);
   }
}
