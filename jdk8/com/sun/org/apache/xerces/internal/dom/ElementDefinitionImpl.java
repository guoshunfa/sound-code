package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl extends ParentNode {
   static final long serialVersionUID = -8373890672670022714L;
   protected String name;
   protected NamedNodeMapImpl attributes;

   public ElementDefinitionImpl(CoreDocumentImpl ownerDocument, String name) {
      super(ownerDocument);
      this.name = name;
      this.attributes = new NamedNodeMapImpl(ownerDocument);
   }

   public short getNodeType() {
      return 21;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public Node cloneNode(boolean deep) {
      ElementDefinitionImpl newnode = (ElementDefinitionImpl)super.cloneNode(deep);
      newnode.attributes = this.attributes.cloneMap((NodeImpl)newnode);
      return newnode;
   }

   public NamedNodeMap getAttributes() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.attributes;
   }
}
