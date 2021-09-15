package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;

public class DeferredElementImpl extends ElementImpl implements DeferredNode {
   static final long serialVersionUID = -7670981133940934842L;
   protected transient int fNodeIndex;

   DeferredElementImpl(DeferredDocumentImpl ownerDoc, int nodeIndex) {
      super(ownerDoc, (String)null);
      this.fNodeIndex = nodeIndex;
      this.needsSyncChildren(true);
   }

   public final int getNodeIndex() {
      return this.fNodeIndex;
   }

   protected final void synchronizeData() {
      this.needsSyncData(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
      boolean orig = ownerDocument.mutationEvents;
      ownerDocument.mutationEvents = false;
      this.name = ownerDocument.getNodeName(this.fNodeIndex);
      this.setupDefaultAttributes();
      int index = ownerDocument.getNodeExtra(this.fNodeIndex);
      if (index != -1) {
         NamedNodeMap attrs = this.getAttributes();

         do {
            NodeImpl attr = (NodeImpl)ownerDocument.getNodeObject(index);
            attrs.setNamedItem(attr);
            index = ownerDocument.getPrevSibling(index);
         } while(index != -1);
      }

      ownerDocument.mutationEvents = orig;
   }

   protected final void synchronizeChildren() {
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
      ownerDocument.synchronizeChildren((ParentNode)this, this.fNodeIndex);
   }
}
