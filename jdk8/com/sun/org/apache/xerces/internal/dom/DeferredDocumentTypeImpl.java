package com.sun.org.apache.xerces.internal.dom;

public class DeferredDocumentTypeImpl extends DocumentTypeImpl implements DeferredNode {
   static final long serialVersionUID = -2172579663227313509L;
   protected transient int fNodeIndex;

   DeferredDocumentTypeImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
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
      this.publicID = ownerDocument.getNodeValue(this.fNodeIndex);
      this.systemID = ownerDocument.getNodeURI(this.fNodeIndex);
      int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
      this.internalSubset = ownerDocument.getNodeValue(extraDataIndex);
   }

   protected void synchronizeChildren() {
      boolean orig = this.ownerDocument().getMutationEvents();
      this.ownerDocument().setMutationEvents(false);
      this.needsSyncChildren(false);
      DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
      this.entities = new NamedNodeMapImpl(this);
      this.notations = new NamedNodeMapImpl(this);
      this.elements = new NamedNodeMapImpl(this);
      DeferredNode last = null;

      for(int index = ownerDocument.getLastChild(this.fNodeIndex); index != -1; index = ownerDocument.getPrevSibling(index)) {
         DeferredNode node = ownerDocument.getNodeObject(index);
         int type = node.getNodeType();
         switch(type) {
         case 1:
            if (((DocumentImpl)this.getOwnerDocument()).allowGrammarAccess) {
               this.insertBefore(node, last);
               last = node;
               break;
            }
         default:
            System.out.println("DeferredDocumentTypeImpl#synchronizeInfo: node.getNodeType() = " + node.getNodeType() + ", class = " + node.getClass().getName());
            break;
         case 6:
            this.entities.setNamedItem(node);
            break;
         case 12:
            this.notations.setNamedItem(node);
            break;
         case 21:
            this.elements.setNamedItem(node);
         }
      }

      this.ownerDocument().setMutationEvents(orig);
      this.setReadOnly(true, false);
   }
}
