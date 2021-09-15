package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Text;

public class DocumentFragmentImpl extends ParentNode implements DocumentFragment {
   static final long serialVersionUID = -7596449967279236746L;

   public DocumentFragmentImpl(CoreDocumentImpl ownerDoc) {
      super(ownerDoc);
   }

   public DocumentFragmentImpl() {
   }

   public short getNodeType() {
      return 11;
   }

   public String getNodeName() {
      return "#document-fragment";
   }

   public void normalize() {
      if (!this.isNormalized()) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         ChildNode next;
         for(ChildNode kid = this.firstChild; kid != null; kid = next) {
            next = kid.nextSibling;
            if (kid.getNodeType() == 3) {
               if (next != null && next.getNodeType() == 3) {
                  ((Text)kid).appendData(next.getNodeValue());
                  this.removeChild(next);
                  next = kid;
               } else if (kid.getNodeValue() == null || kid.getNodeValue().length() == 0) {
                  this.removeChild(kid);
               }
            }

            kid.normalize();
         }

         this.isNormalized(true);
      }
   }
}
