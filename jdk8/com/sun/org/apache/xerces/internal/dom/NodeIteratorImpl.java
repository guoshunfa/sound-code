package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class NodeIteratorImpl implements NodeIterator {
   private DocumentImpl fDocument;
   private Node fRoot;
   private int fWhatToShow = -1;
   private NodeFilter fNodeFilter;
   private boolean fDetach = false;
   private Node fCurrentNode;
   private boolean fForward = true;
   private boolean fEntityReferenceExpansion;

   public NodeIteratorImpl(DocumentImpl document, Node root, int whatToShow, NodeFilter nodeFilter, boolean entityReferenceExpansion) {
      this.fDocument = document;
      this.fRoot = root;
      this.fCurrentNode = null;
      this.fWhatToShow = whatToShow;
      this.fNodeFilter = nodeFilter;
      this.fEntityReferenceExpansion = entityReferenceExpansion;
   }

   public Node getRoot() {
      return this.fRoot;
   }

   public int getWhatToShow() {
      return this.fWhatToShow;
   }

   public NodeFilter getFilter() {
      return this.fNodeFilter;
   }

   public boolean getExpandEntityReferences() {
      return this.fEntityReferenceExpansion;
   }

   public Node nextNode() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else if (this.fRoot == null) {
         return null;
      } else {
         Node nextNode = this.fCurrentNode;
         boolean accepted = false;

         do {
            if (accepted) {
               return null;
            }

            if (!this.fForward && nextNode != null) {
               nextNode = this.fCurrentNode;
            } else if (!this.fEntityReferenceExpansion && nextNode != null && nextNode.getNodeType() == 5) {
               nextNode = this.nextNode(nextNode, false);
            } else {
               nextNode = this.nextNode(nextNode, true);
            }

            this.fForward = true;
            if (nextNode == null) {
               return null;
            }

            accepted = this.acceptNode(nextNode);
         } while(!accepted);

         this.fCurrentNode = nextNode;
         return this.fCurrentNode;
      }
   }

   public Node previousNode() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else if (this.fRoot != null && this.fCurrentNode != null) {
         Node previousNode = this.fCurrentNode;
         boolean accepted = false;

         do {
            if (accepted) {
               return null;
            }

            if (this.fForward && previousNode != null) {
               previousNode = this.fCurrentNode;
            } else {
               previousNode = this.previousNode(previousNode);
            }

            this.fForward = false;
            if (previousNode == null) {
               return null;
            }

            accepted = this.acceptNode(previousNode);
         } while(!accepted);

         this.fCurrentNode = previousNode;
         return this.fCurrentNode;
      } else {
         return null;
      }
   }

   boolean acceptNode(Node node) {
      if (this.fNodeFilter == null) {
         return (this.fWhatToShow & 1 << node.getNodeType() - 1) != 0;
      } else {
         return (this.fWhatToShow & 1 << node.getNodeType() - 1) != 0 && this.fNodeFilter.acceptNode(node) == 1;
      }
   }

   Node matchNodeOrParent(Node node) {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         for(Node n = this.fCurrentNode; n != this.fRoot; n = n.getParentNode()) {
            if (node == n) {
               return n;
            }
         }

         return null;
      }
   }

   Node nextNode(Node node, boolean visitChildren) {
      if (node == null) {
         return this.fRoot;
      } else {
         Node result;
         if (visitChildren && node.hasChildNodes()) {
            result = node.getFirstChild();
            return result;
         } else if (node == this.fRoot) {
            return null;
         } else {
            result = node.getNextSibling();
            if (result != null) {
               return result;
            } else {
               for(Node parent = node.getParentNode(); parent != null && parent != this.fRoot; parent = parent.getParentNode()) {
                  result = parent.getNextSibling();
                  if (result != null) {
                     return result;
                  }
               }

               return null;
            }
         }
      }
   }

   Node previousNode(Node node) {
      if (node == this.fRoot) {
         return null;
      } else {
         Node result = node.getPreviousSibling();
         if (result == null) {
            result = node.getParentNode();
            return result;
         } else {
            if (result.hasChildNodes() && (this.fEntityReferenceExpansion || result == null || result.getNodeType() != 5)) {
               while(result.hasChildNodes()) {
                  result = result.getLastChild();
               }
            }

            return result;
         }
      }
   }

   public void removeNode(Node node) {
      if (node != null) {
         Node deleted = this.matchNodeOrParent(node);
         if (deleted != null) {
            if (this.fForward) {
               this.fCurrentNode = this.previousNode(deleted);
            } else {
               Node next = this.nextNode(deleted, false);
               if (next != null) {
                  this.fCurrentNode = next;
               } else {
                  this.fCurrentNode = this.previousNode(deleted);
                  this.fForward = true;
               }
            }

         }
      }
   }

   public void detach() {
      this.fDetach = true;
      this.fDocument.removeNodeIterator(this);
   }
}
