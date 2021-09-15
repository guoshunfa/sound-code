package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class TreeWalkerImpl implements TreeWalker {
   private boolean fEntityReferenceExpansion = false;
   int fWhatToShow = -1;
   NodeFilter fNodeFilter;
   Node fCurrentNode;
   Node fRoot;

   public TreeWalkerImpl(Node root, int whatToShow, NodeFilter nodeFilter, boolean entityReferenceExpansion) {
      this.fCurrentNode = root;
      this.fRoot = root;
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

   public void setWhatShow(int whatToShow) {
      this.fWhatToShow = whatToShow;
   }

   public NodeFilter getFilter() {
      return this.fNodeFilter;
   }

   public boolean getExpandEntityReferences() {
      return this.fEntityReferenceExpansion;
   }

   public Node getCurrentNode() {
      return this.fCurrentNode;
   }

   public void setCurrentNode(Node node) {
      if (node == null) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      } else {
         this.fCurrentNode = node;
      }
   }

   public Node parentNode() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node node = this.getParentNode(this.fCurrentNode);
         if (node != null) {
            this.fCurrentNode = node;
         }

         return node;
      }
   }

   public Node firstChild() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node node = this.getFirstChild(this.fCurrentNode);
         if (node != null) {
            this.fCurrentNode = node;
         }

         return node;
      }
   }

   public Node lastChild() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node node = this.getLastChild(this.fCurrentNode);
         if (node != null) {
            this.fCurrentNode = node;
         }

         return node;
      }
   }

   public Node previousSibling() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node node = this.getPreviousSibling(this.fCurrentNode);
         if (node != null) {
            this.fCurrentNode = node;
         }

         return node;
      }
   }

   public Node nextSibling() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node node = this.getNextSibling(this.fCurrentNode);
         if (node != null) {
            this.fCurrentNode = node;
         }

         return node;
      }
   }

   public Node previousNode() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node result = this.getPreviousSibling(this.fCurrentNode);
         if (result == null) {
            result = this.getParentNode(this.fCurrentNode);
            if (result != null) {
               this.fCurrentNode = result;
               return this.fCurrentNode;
            } else {
               return null;
            }
         } else {
            Node lastChild = this.getLastChild(result);

            Node prev;
            for(prev = lastChild; lastChild != null; lastChild = this.getLastChild(lastChild)) {
               prev = lastChild;
            }

            if (prev != null) {
               this.fCurrentNode = prev;
               return this.fCurrentNode;
            } else if (result != null) {
               this.fCurrentNode = result;
               return this.fCurrentNode;
            } else {
               return null;
            }
         }
      }
   }

   public Node nextNode() {
      if (this.fCurrentNode == null) {
         return null;
      } else {
         Node result = this.getFirstChild(this.fCurrentNode);
         if (result != null) {
            this.fCurrentNode = result;
            return result;
         } else {
            result = this.getNextSibling(this.fCurrentNode);
            if (result != null) {
               this.fCurrentNode = result;
               return result;
            } else {
               for(Node parent = this.getParentNode(this.fCurrentNode); parent != null; parent = this.getParentNode(parent)) {
                  result = this.getNextSibling(parent);
                  if (result != null) {
                     this.fCurrentNode = result;
                     return result;
                  }
               }

               return null;
            }
         }
      }
   }

   Node getParentNode(Node node) {
      if (node != null && node != this.fRoot) {
         Node newNode = node.getParentNode();
         if (newNode == null) {
            return null;
         } else {
            int accept = this.acceptNode(newNode);
            return accept == 1 ? newNode : this.getParentNode(newNode);
         }
      } else {
         return null;
      }
   }

   Node getNextSibling(Node node) {
      return this.getNextSibling(node, this.fRoot);
   }

   Node getNextSibling(Node node, Node root) {
      if (node != null && node != root) {
         Node newNode = node.getNextSibling();
         short accept;
         if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode != null && newNode != root) {
               accept = this.acceptNode(newNode);
               return accept == 3 ? this.getNextSibling(newNode, root) : null;
            } else {
               return null;
            }
         } else {
            accept = this.acceptNode(newNode);
            if (accept == 1) {
               return newNode;
            } else if (accept == 3) {
               Node fChild = this.getFirstChild(newNode);
               return fChild == null ? this.getNextSibling(newNode, root) : fChild;
            } else {
               return this.getNextSibling(newNode, root);
            }
         }
      } else {
         return null;
      }
   }

   Node getPreviousSibling(Node node) {
      return this.getPreviousSibling(node, this.fRoot);
   }

   Node getPreviousSibling(Node node, Node root) {
      if (node != null && node != root) {
         Node newNode = node.getPreviousSibling();
         short accept;
         if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode != null && newNode != root) {
               accept = this.acceptNode(newNode);
               return accept == 3 ? this.getPreviousSibling(newNode, root) : null;
            } else {
               return null;
            }
         } else {
            accept = this.acceptNode(newNode);
            if (accept == 1) {
               return newNode;
            } else if (accept == 3) {
               Node fChild = this.getLastChild(newNode);
               return fChild == null ? this.getPreviousSibling(newNode, root) : fChild;
            } else {
               return this.getPreviousSibling(newNode, root);
            }
         }
      } else {
         return null;
      }
   }

   Node getFirstChild(Node node) {
      if (node == null) {
         return null;
      } else if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
         return null;
      } else {
         Node newNode = node.getFirstChild();
         if (newNode == null) {
            return null;
         } else {
            int accept = this.acceptNode(newNode);
            if (accept == 1) {
               return newNode;
            } else if (accept == 3 && newNode.hasChildNodes()) {
               Node fChild = this.getFirstChild(newNode);
               return fChild == null ? this.getNextSibling(newNode, node) : fChild;
            } else {
               return this.getNextSibling(newNode, node);
            }
         }
      }
   }

   Node getLastChild(Node node) {
      if (node == null) {
         return null;
      } else if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
         return null;
      } else {
         Node newNode = node.getLastChild();
         if (newNode == null) {
            return null;
         } else {
            int accept = this.acceptNode(newNode);
            if (accept == 1) {
               return newNode;
            } else if (accept == 3 && newNode.hasChildNodes()) {
               Node lChild = this.getLastChild(newNode);
               return lChild == null ? this.getPreviousSibling(newNode, node) : lChild;
            } else {
               return this.getPreviousSibling(newNode, node);
            }
         }
      }
   }

   short acceptNode(Node node) {
      if (this.fNodeFilter == null) {
         return (short)((this.fWhatToShow & 1 << node.getNodeType() - 1) != 0 ? 1 : 3);
      } else {
         return (this.fWhatToShow & 1 << node.getNodeType() - 1) != 0 ? this.fNodeFilter.acceptNode(node) : 3;
      }
   }
}
