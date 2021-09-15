package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class DOM2Helper {
   private DOM2Helper() {
   }

   public static String getLocalNameOfNode(Node n) {
      String name = n.getLocalName();
      return null == name ? getLocalNameOfNodeFallback(n) : name;
   }

   private static String getLocalNameOfNodeFallback(Node n) {
      String qname = n.getNodeName();
      int index = qname.indexOf(58);
      return index < 0 ? qname : qname.substring(index + 1);
   }

   public static String getNamespaceOfNode(Node n) {
      return n.getNamespaceURI();
   }

   public static boolean isNodeAfter(Node node1, Node node2) {
      if (node1 != node2 && !isNodeTheSame(node1, node2)) {
         boolean isNodeAfter = true;
         Node parent1 = getParentOfNode(node1);
         Node parent2 = getParentOfNode(node2);
         if (parent1 != parent2 && !isNodeTheSame(parent1, parent2)) {
            int nParents1 = 2;

            int nParents2;
            for(nParents2 = 2; parent1 != null; parent1 = getParentOfNode(parent1)) {
               ++nParents1;
            }

            while(parent2 != null) {
               ++nParents2;
               parent2 = getParentOfNode(parent2);
            }

            Node startNode1 = node1;
            Node startNode2 = node2;
            int adjust;
            int i;
            if (nParents1 < nParents2) {
               adjust = nParents2 - nParents1;

               for(i = 0; i < adjust; ++i) {
                  startNode2 = getParentOfNode(startNode2);
               }
            } else if (nParents1 > nParents2) {
               adjust = nParents1 - nParents2;

               for(i = 0; i < adjust; ++i) {
                  startNode1 = getParentOfNode(startNode1);
               }
            }

            Node prevChild1 = null;

            for(Node prevChild2 = null; null != startNode1; startNode2 = getParentOfNode(startNode2)) {
               if (startNode1 == startNode2 || isNodeTheSame(startNode1, startNode2)) {
                  if (null == prevChild1) {
                     isNodeAfter = nParents1 < nParents2;
                  } else {
                     isNodeAfter = isNodeAfterSibling(startNode1, prevChild1, prevChild2);
                  }
                  break;
               }

               prevChild1 = startNode1;
               startNode1 = getParentOfNode(startNode1);
               prevChild2 = startNode2;
            }
         } else if (null != parent1) {
            isNodeAfter = isNodeAfterSibling(parent1, node1, node2);
         }

         return isNodeAfter;
      } else {
         return true;
      }
   }

   public static boolean isNodeTheSame(Node node1, Node node2) {
      if (node1 instanceof DTMNodeProxy && node2 instanceof DTMNodeProxy) {
         return ((DTMNodeProxy)node1).equals((Node)((DTMNodeProxy)node2));
      } else {
         return node1 == node2;
      }
   }

   public static Node getParentOfNode(Node node) {
      Node parent = node.getParentNode();
      if (parent == null && 2 == node.getNodeType()) {
         parent = ((Attr)node).getOwnerElement();
      }

      return (Node)parent;
   }

   private static boolean isNodeAfterSibling(Node parent, Node child1, Node child2) {
      boolean isNodeAfterSibling = false;
      short child1type = child1.getNodeType();
      short child2type = child2.getNodeType();
      if (2 != child1type && 2 == child2type) {
         isNodeAfterSibling = false;
      } else if (2 == child1type && 2 != child2type) {
         isNodeAfterSibling = true;
      } else {
         boolean found1;
         if (2 == child1type) {
            NamedNodeMap children = parent.getAttributes();
            int nNodes = children.getLength();
            found1 = false;
            boolean found2 = false;

            for(int i = 0; i < nNodes; ++i) {
               Node child = children.item(i);
               if (child1 != child && !isNodeTheSame(child1, child)) {
                  if (child2 == child || isNodeTheSame(child2, child)) {
                     if (found1) {
                        isNodeAfterSibling = true;
                        break;
                     }

                     found2 = true;
                  }
               } else {
                  if (found2) {
                     isNodeAfterSibling = false;
                     break;
                  }

                  found1 = true;
               }
            }
         } else {
            Node child = parent.getFirstChild();
            boolean found1 = false;

            for(found1 = false; null != child; child = child.getNextSibling()) {
               if (child1 != child && !isNodeTheSame(child1, child)) {
                  if (child2 == child || isNodeTheSame(child2, child)) {
                     if (found1) {
                        isNodeAfterSibling = true;
                        break;
                     }

                     found1 = true;
                  }
               } else {
                  if (found1) {
                     isNodeAfterSibling = false;
                     break;
                  }

                  found1 = true;
               }
            }
         }
      }

      return isNodeAfterSibling;
   }
}
