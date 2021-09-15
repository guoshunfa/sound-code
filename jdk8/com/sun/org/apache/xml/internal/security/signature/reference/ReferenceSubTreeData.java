package com.sun.org.apache.xml.internal.security.signature.reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ReferenceSubTreeData implements ReferenceNodeSetData {
   private boolean excludeComments;
   private Node root;

   public ReferenceSubTreeData(Node var1, boolean var2) {
      this.root = var1;
      this.excludeComments = var2;
   }

   public Iterator<Node> iterator() {
      return new ReferenceSubTreeData.DelayedNodeIterator(this.root, this.excludeComments);
   }

   public Node getRoot() {
      return this.root;
   }

   public boolean excludeComments() {
      return this.excludeComments;
   }

   static class DelayedNodeIterator implements Iterator<Node> {
      private Node root;
      private List<Node> nodeSet;
      private ListIterator<Node> li;
      private boolean withComments;

      DelayedNodeIterator(Node var1, boolean var2) {
         this.root = var1;
         this.withComments = !var2;
      }

      public boolean hasNext() {
         if (this.nodeSet == null) {
            this.nodeSet = this.dereferenceSameDocumentURI(this.root);
            this.li = this.nodeSet.listIterator();
         }

         return this.li.hasNext();
      }

      public Node next() {
         if (this.nodeSet == null) {
            this.nodeSet = this.dereferenceSameDocumentURI(this.root);
            this.li = this.nodeSet.listIterator();
         }

         if (this.li.hasNext()) {
            return (Node)this.li.next();
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private List<Node> dereferenceSameDocumentURI(Node var1) {
         ArrayList var2 = new ArrayList();
         if (var1 != null) {
            this.nodeSetMinusCommentNodes(var1, var2, (Node)null);
         }

         return var2;
      }

      private void nodeSetMinusCommentNodes(Node var1, List<Node> var2, Node var3) {
         Node var5;
         Node var6;
         switch(var1.getNodeType()) {
         case 1:
            var2.add(var1);
            NamedNodeMap var4 = var1.getAttributes();
            if (var4 != null) {
               int var7 = 0;

               for(int var8 = var4.getLength(); var7 < var8; ++var7) {
                  var2.add(var4.item(var7));
               }
            }

            var5 = null;

            for(var6 = var1.getFirstChild(); var6 != null; var6 = var6.getNextSibling()) {
               this.nodeSetMinusCommentNodes(var6, var2, var5);
               var5 = var6;
            }
         case 2:
         case 5:
         case 6:
         default:
            break;
         case 3:
         case 4:
            if (var3 != null && (var3.getNodeType() == 3 || var3.getNodeType() == 4)) {
               return;
            }

            var2.add(var1);
            break;
         case 7:
            var2.add(var1);
            break;
         case 8:
            if (this.withComments) {
               var2.add(var1);
            }
            break;
         case 9:
            var5 = null;

            for(var6 = var1.getFirstChild(); var6 != null; var6 = var6.getNextSibling()) {
               this.nodeSetMinusCommentNodes(var6, var2, var5);
               var5 = var6;
            }
         }

      }
   }
}
