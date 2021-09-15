package com.sun.org.apache.xml.internal.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelperNodeList implements NodeList {
   List<Node> nodes;
   boolean allNodesMustHaveSameParent;

   public HelperNodeList() {
      this(false);
   }

   public HelperNodeList(boolean var1) {
      this.nodes = new ArrayList();
      this.allNodesMustHaveSameParent = false;
      this.allNodesMustHaveSameParent = var1;
   }

   public Node item(int var1) {
      return (Node)this.nodes.get(var1);
   }

   public int getLength() {
      return this.nodes.size();
   }

   public void appendChild(Node var1) throws IllegalArgumentException {
      if (this.allNodesMustHaveSameParent && this.getLength() > 0 && this.item(0).getParentNode() != var1.getParentNode()) {
         throw new IllegalArgumentException("Nodes have not the same Parent");
      } else {
         this.nodes.add(var1);
      }
   }

   public Document getOwnerDocument() {
      return this.getLength() == 0 ? null : XMLUtils.getOwnerDocument(this.item(0));
   }
}
