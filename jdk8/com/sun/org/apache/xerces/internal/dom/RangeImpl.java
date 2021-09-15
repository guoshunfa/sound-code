package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.ranges.RangeException;

public class RangeImpl implements Range {
   DocumentImpl fDocument;
   Node fStartContainer;
   Node fEndContainer;
   int fStartOffset;
   int fEndOffset;
   boolean fIsCollapsed;
   boolean fDetach = false;
   Node fInsertNode = null;
   Node fDeleteNode = null;
   Node fSplitNode = null;
   boolean fInsertedFromRange = false;
   Node fRemoveChild = null;
   static final int EXTRACT_CONTENTS = 1;
   static final int CLONE_CONTENTS = 2;
   static final int DELETE_CONTENTS = 3;

   public RangeImpl(DocumentImpl document) {
      this.fDocument = document;
      this.fStartContainer = document;
      this.fEndContainer = document;
      this.fStartOffset = 0;
      this.fEndOffset = 0;
      this.fDetach = false;
   }

   public Node getStartContainer() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         return this.fStartContainer;
      }
   }

   public int getStartOffset() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         return this.fStartOffset;
      }
   }

   public Node getEndContainer() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         return this.fEndContainer;
      }
   }

   public int getEndOffset() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         return this.fEndOffset;
      }
   }

   public boolean getCollapsed() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         return this.fStartContainer == this.fEndContainer && this.fStartOffset == this.fEndOffset;
      }
   }

   public Node getCommonAncestorContainer() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         Vector startV = new Vector();

         Node node;
         for(node = this.fStartContainer; node != null; node = node.getParentNode()) {
            startV.addElement(node);
         }

         Vector endV = new Vector();

         for(node = this.fEndContainer; node != null; node = node.getParentNode()) {
            endV.addElement(node);
         }

         int s = startV.size() - 1;
         int e = endV.size() - 1;

         Object result;
         for(result = null; s >= 0 && e >= 0 && startV.elementAt(s) == endV.elementAt(e); --e) {
            result = startV.elementAt(s);
            --s;
         }

         return (Node)result;
      }
   }

   public void setStart(Node refNode, int offset) throws RangeException, DOMException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.isLegalContainer(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.checkIndex(refNode, offset);
      this.fStartContainer = refNode;
      this.fStartOffset = offset;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(true);
      }

   }

   public void setEnd(Node refNode, int offset) throws RangeException, DOMException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.isLegalContainer(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.checkIndex(refNode, offset);
      this.fEndContainer = refNode;
      this.fEndOffset = offset;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(false);
      }

   }

   public void setStartBefore(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.fStartContainer = refNode.getParentNode();
      int i = 0;

      for(Node n = refNode; n != null; n = n.getPreviousSibling()) {
         ++i;
      }

      this.fStartOffset = i - 1;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(true);
      }

   }

   public void setStartAfter(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.fStartContainer = refNode.getParentNode();
      int i = 0;

      for(Node n = refNode; n != null; n = n.getPreviousSibling()) {
         ++i;
      }

      this.fStartOffset = i;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(true);
      }

   }

   public void setEndBefore(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.fEndContainer = refNode.getParentNode();
      int i = 0;

      for(Node n = refNode; n != null; n = n.getPreviousSibling()) {
         ++i;
      }

      this.fEndOffset = i - 1;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(false);
      }

   }

   public void setEndAfter(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.fEndContainer = refNode.getParentNode();
      int i = 0;

      for(Node n = refNode; n != null; n = n.getPreviousSibling()) {
         ++i;
      }

      this.fEndOffset = i;
      if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
         this.collapse(false);
      }

   }

   public void collapse(boolean toStart) {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         if (toStart) {
            this.fEndContainer = this.fStartContainer;
            this.fEndOffset = this.fStartOffset;
         } else {
            this.fStartContainer = this.fEndContainer;
            this.fStartOffset = this.fEndOffset;
         }

      }
   }

   public void selectNode(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.isLegalContainer(refNode.getParentNode()) || !this.isLegalContainedNode(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      Node parent = refNode.getParentNode();
      if (parent != null) {
         this.fStartContainer = parent;
         this.fEndContainer = parent;
         int i = 0;

         for(Node n = refNode; n != null; n = n.getPreviousSibling()) {
            ++i;
         }

         this.fStartOffset = i - 1;
         this.fEndOffset = this.fStartOffset + 1;
      }

   }

   public void selectNodeContents(Node refNode) throws RangeException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (!this.isLegalContainer(refNode)) {
            throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
         }

         if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      this.fStartContainer = refNode;
      this.fEndContainer = refNode;
      Node first = refNode.getFirstChild();
      this.fStartOffset = 0;
      if (first == null) {
         this.fEndOffset = 0;
      } else {
         int i = 0;

         for(Node n = first; n != null; n = n.getNextSibling()) {
            ++i;
         }

         this.fEndOffset = i;
      }

   }

   public short compareBoundaryPoints(short how, Range sourceRange) throws DOMException {
      if (this.fDocument.errorChecking) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         }

         if (this.fDocument != sourceRange.getStartContainer().getOwnerDocument() && this.fDocument != sourceRange.getStartContainer() && sourceRange.getStartContainer() != null || this.fDocument != sourceRange.getEndContainer().getOwnerDocument() && this.fDocument != sourceRange.getEndContainer() && sourceRange.getStartContainer() != null) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
         }
      }

      Node endPointA;
      Node endPointB;
      int offsetA;
      int offsetB;
      if (how == 0) {
         endPointA = sourceRange.getStartContainer();
         endPointB = this.fStartContainer;
         offsetA = sourceRange.getStartOffset();
         offsetB = this.fStartOffset;
      } else if (how == 1) {
         endPointA = sourceRange.getStartContainer();
         endPointB = this.fEndContainer;
         offsetA = sourceRange.getStartOffset();
         offsetB = this.fEndOffset;
      } else if (how == 3) {
         endPointA = sourceRange.getEndContainer();
         endPointB = this.fStartContainer;
         offsetA = sourceRange.getEndOffset();
         offsetB = this.fStartOffset;
      } else {
         endPointA = sourceRange.getEndContainer();
         endPointB = this.fEndContainer;
         offsetA = sourceRange.getEndOffset();
         offsetB = this.fEndOffset;
      }

      if (endPointA == endPointB) {
         if (offsetA < offsetB) {
            return 1;
         } else if (offsetA == offsetB) {
            return 0;
         } else {
            return -1;
         }
      } else {
         Node c = endPointB;

         Node n;
         int index;
         for(n = endPointB.getParentNode(); n != null; n = n.getParentNode()) {
            if (n == endPointA) {
               index = this.indexOf(c, endPointA);
               if (offsetA <= index) {
                  return 1;
               }

               return -1;
            }

            c = n;
         }

         c = endPointA;

         for(n = endPointA.getParentNode(); n != null; n = n.getParentNode()) {
            if (n == endPointB) {
               index = this.indexOf(c, endPointB);
               if (index < offsetB) {
                  return 1;
               }

               return -1;
            }

            c = n;
         }

         int depthDiff = 0;

         for(n = endPointA; n != null; n = n.getParentNode()) {
            ++depthDiff;
         }

         for(n = endPointB; n != null; n = n.getParentNode()) {
            --depthDiff;
         }

         while(depthDiff > 0) {
            endPointA = endPointA.getParentNode();
            --depthDiff;
         }

         while(depthDiff < 0) {
            endPointB = endPointB.getParentNode();
            ++depthDiff;
         }

         n = endPointA.getParentNode();

         for(Node pB = endPointB.getParentNode(); n != pB; pB = pB.getParentNode()) {
            endPointA = n;
            endPointB = pB;
            n = n.getParentNode();
         }

         for(n = endPointA.getNextSibling(); n != null; n = n.getNextSibling()) {
            if (n == endPointB) {
               return 1;
            }
         }

         return -1;
      }
   }

   public void deleteContents() throws DOMException {
      this.traverseContents(3);
   }

   public DocumentFragment extractContents() throws DOMException {
      return this.traverseContents(1);
   }

   public DocumentFragment cloneContents() throws DOMException {
      return this.traverseContents(2);
   }

   public void insertNode(Node newNode) throws DOMException, RangeException {
      if (newNode != null) {
         int type = newNode.getNodeType();
         if (this.fDocument.errorChecking) {
            if (this.fDetach) {
               throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
            }

            if (this.fDocument != newNode.getOwnerDocument()) {
               throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null));
            }

            if (type == 2 || type == 6 || type == 12 || type == 9) {
               throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
            }
         }

         int currentChildren = 0;
         this.fInsertedFromRange = true;
         if (this.fStartContainer.getNodeType() == 3) {
            Node parent = this.fStartContainer.getParentNode();
            currentChildren = parent.getChildNodes().getLength();
            Node cloneCurrent = this.fStartContainer.cloneNode(false);
            ((TextImpl)cloneCurrent).setNodeValueInternal(cloneCurrent.getNodeValue().substring(this.fStartOffset));
            ((TextImpl)this.fStartContainer).setNodeValueInternal(this.fStartContainer.getNodeValue().substring(0, this.fStartOffset));
            Node next = this.fStartContainer.getNextSibling();
            if (next != null) {
               if (parent != null) {
                  parent.insertBefore(newNode, next);
                  parent.insertBefore(cloneCurrent, next);
               }
            } else if (parent != null) {
               parent.appendChild(newNode);
               parent.appendChild(cloneCurrent);
            }

            if (this.fEndContainer == this.fStartContainer) {
               this.fEndContainer = cloneCurrent;
               this.fEndOffset -= this.fStartOffset;
            } else if (this.fEndContainer == parent) {
               this.fEndOffset += parent.getChildNodes().getLength() - currentChildren;
            }

            this.signalSplitData(this.fStartContainer, cloneCurrent, this.fStartOffset);
         } else {
            if (this.fEndContainer == this.fStartContainer) {
               currentChildren = this.fEndContainer.getChildNodes().getLength();
            }

            Node current = this.fStartContainer.getFirstChild();
            int i = false;

            for(int i = 0; i < this.fStartOffset && current != null; ++i) {
               current = current.getNextSibling();
            }

            if (current != null) {
               this.fStartContainer.insertBefore(newNode, current);
            } else {
               this.fStartContainer.appendChild(newNode);
            }

            if (this.fEndContainer == this.fStartContainer && this.fEndOffset != 0) {
               this.fEndOffset += this.fEndContainer.getChildNodes().getLength() - currentChildren;
            }
         }

         this.fInsertedFromRange = false;
      }
   }

   public void surroundContents(Node newParent) throws DOMException, RangeException {
      if (newParent != null) {
         int type = newParent.getNodeType();
         if (this.fDocument.errorChecking) {
            if (this.fDetach) {
               throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
            }

            if (type == 2 || type == 6 || type == 12 || type == 10 || type == 9 || type == 11) {
               throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", (Object[])null));
            }
         }

         Node realStart = this.fStartContainer;
         Node realEnd = this.fEndContainer;
         if (this.fStartContainer.getNodeType() == 3) {
            realStart = this.fStartContainer.getParentNode();
         }

         if (this.fEndContainer.getNodeType() == 3) {
            realEnd = this.fEndContainer.getParentNode();
         }

         if (realStart != realEnd) {
            throw new RangeExceptionImpl((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "BAD_BOUNDARYPOINTS_ERR", (Object[])null));
         } else {
            DocumentFragment frag = this.extractContents();
            this.insertNode(newParent);
            newParent.appendChild(frag);
            this.selectNode(newParent);
         }
      }
   }

   public Range cloneRange() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         Range range = this.fDocument.createRange();
         range.setStart(this.fStartContainer, this.fStartOffset);
         range.setEnd(this.fEndContainer, this.fEndOffset);
         return range;
      }
   }

   public String toString() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         Node node = this.fStartContainer;
         Node stopNode = this.fEndContainer;
         StringBuffer sb = new StringBuffer();
         int i;
         if (this.fStartContainer.getNodeType() != 3 && this.fStartContainer.getNodeType() != 4) {
            node = node.getFirstChild();
            if (this.fStartOffset > 0) {
               for(i = 0; i < this.fStartOffset && node != null; ++i) {
                  node = node.getNextSibling();
               }
            }

            if (node == null) {
               node = this.nextNode(this.fStartContainer, false);
            }
         } else {
            if (this.fStartContainer == this.fEndContainer) {
               sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset));
               return sb.toString();
            }

            sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset));
            node = this.nextNode(node, true);
         }

         if (this.fEndContainer.getNodeType() != 3 && this.fEndContainer.getNodeType() != 4) {
            i = this.fEndOffset;

            for(stopNode = this.fEndContainer.getFirstChild(); i > 0 && stopNode != null; stopNode = stopNode.getNextSibling()) {
               --i;
            }

            if (stopNode == null) {
               stopNode = this.nextNode(this.fEndContainer, false);
            }
         }

         for(; node != stopNode && node != null; node = this.nextNode(node, true)) {
            if (node.getNodeType() == 3 || node.getNodeType() == 4) {
               sb.append(node.getNodeValue());
            }
         }

         if (this.fEndContainer.getNodeType() == 3 || this.fEndContainer.getNodeType() == 4) {
            sb.append(this.fEndContainer.getNodeValue().substring(0, this.fEndOffset));
         }

         return sb.toString();
      }
   }

   public void detach() {
      if (this.fDetach) {
         throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
      } else {
         this.fDetach = true;
         this.fDocument.removeRange(this);
      }
   }

   void signalSplitData(Node node, Node newNode, int offset) {
      this.fSplitNode = node;
      this.fDocument.splitData(node, newNode, offset);
      this.fSplitNode = null;
   }

   void receiveSplitData(Node node, Node newNode, int offset) {
      if (node != null && newNode != null) {
         if (this.fSplitNode != node) {
            if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && this.fStartOffset > offset) {
               this.fStartOffset -= offset;
               this.fStartContainer = newNode;
            }

            if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && this.fEndOffset > offset) {
               this.fEndOffset -= offset;
               this.fEndContainer = newNode;
            }

         }
      }
   }

   void deleteData(CharacterData node, int offset, int count) {
      this.fDeleteNode = node;
      node.deleteData(offset, count);
      this.fDeleteNode = null;
   }

   void receiveDeletedText(Node node, int offset, int count) {
      if (node != null) {
         if (this.fDeleteNode != node) {
            if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3) {
               if (this.fStartOffset > offset + count) {
                  this.fStartOffset = offset + (this.fStartOffset - (offset + count));
               } else if (this.fStartOffset > offset) {
                  this.fStartOffset = offset;
               }
            }

            if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3) {
               if (this.fEndOffset > offset + count) {
                  this.fEndOffset = offset + (this.fEndOffset - (offset + count));
               } else if (this.fEndOffset > offset) {
                  this.fEndOffset = offset;
               }
            }

         }
      }
   }

   void insertData(CharacterData node, int index, String insert) {
      this.fInsertNode = node;
      node.insertData(index, insert);
      this.fInsertNode = null;
   }

   void receiveInsertedText(Node node, int index, int len) {
      if (node != null) {
         if (this.fInsertNode != node) {
            if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && index < this.fStartOffset) {
               this.fStartOffset += len;
            }

            if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && index < this.fEndOffset) {
               this.fEndOffset += len;
            }

         }
      }
   }

   void receiveReplacedText(Node node) {
      if (node != null) {
         if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3) {
            this.fStartOffset = 0;
         }

         if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3) {
            this.fEndOffset = 0;
         }

      }
   }

   public void insertedNodeFromDOM(Node node) {
      if (node != null) {
         if (this.fInsertNode != node) {
            if (!this.fInsertedFromRange) {
               Node parent = node.getParentNode();
               int index;
               if (parent == this.fStartContainer) {
                  index = this.indexOf(node, this.fStartContainer);
                  if (index < this.fStartOffset) {
                     ++this.fStartOffset;
                  }
               }

               if (parent == this.fEndContainer) {
                  index = this.indexOf(node, this.fEndContainer);
                  if (index < this.fEndOffset) {
                     ++this.fEndOffset;
                  }
               }

            }
         }
      }
   }

   Node removeChild(Node parent, Node child) {
      this.fRemoveChild = child;
      Node n = parent.removeChild(child);
      this.fRemoveChild = null;
      return n;
   }

   void removeNode(Node node) {
      if (node != null) {
         if (this.fRemoveChild != node) {
            Node parent = node.getParentNode();
            int index;
            if (parent == this.fStartContainer) {
               index = this.indexOf(node, this.fStartContainer);
               if (index < this.fStartOffset) {
                  --this.fStartOffset;
               }
            }

            if (parent == this.fEndContainer) {
               index = this.indexOf(node, this.fEndContainer);
               if (index < this.fEndOffset) {
                  --this.fEndOffset;
               }
            }

            if (parent != this.fStartContainer || parent != this.fEndContainer) {
               if (this.isAncestorOf(node, this.fStartContainer)) {
                  this.fStartContainer = parent;
                  this.fStartOffset = this.indexOf(node, parent);
               }

               if (this.isAncestorOf(node, this.fEndContainer)) {
                  this.fEndContainer = parent;
                  this.fEndOffset = this.indexOf(node, parent);
               }
            }

         }
      }
   }

   private DocumentFragment traverseContents(int how) throws DOMException {
      if (this.fStartContainer != null && this.fEndContainer != null) {
         if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", (Object[])null));
         } else if (this.fStartContainer == this.fEndContainer) {
            return this.traverseSameContainer(how);
         } else {
            int endContainerDepth = 0;
            Node c = this.fEndContainer;

            Node c;
            for(c = c.getParentNode(); c != null; c = c.getParentNode()) {
               if (c == this.fStartContainer) {
                  return this.traverseCommonStartContainer(c, how);
               }

               ++endContainerDepth;
               c = c;
            }

            int startContainerDepth = 0;
            c = this.fStartContainer;

            Node startNode;
            for(startNode = c.getParentNode(); startNode != null; startNode = startNode.getParentNode()) {
               if (startNode == this.fEndContainer) {
                  return this.traverseCommonEndContainer(c, how);
               }

               ++startContainerDepth;
               c = startNode;
            }

            int depthDiff = startContainerDepth - endContainerDepth;

            for(startNode = this.fStartContainer; depthDiff > 0; --depthDiff) {
               startNode = startNode.getParentNode();
            }

            Node endNode;
            for(endNode = this.fEndContainer; depthDiff < 0; ++depthDiff) {
               endNode = endNode.getParentNode();
            }

            Node sp = startNode.getParentNode();

            for(Node ep = endNode.getParentNode(); sp != ep; ep = ep.getParentNode()) {
               startNode = sp;
               endNode = ep;
               sp = sp.getParentNode();
            }

            return this.traverseCommonAncestors(startNode, endNode, how);
         }
      } else {
         return null;
      }
   }

   private DocumentFragment traverseSameContainer(int how) {
      DocumentFragment frag = null;
      if (how != 3) {
         frag = this.fDocument.createDocumentFragment();
      }

      if (this.fStartOffset == this.fEndOffset) {
         return frag;
      } else if (this.fStartContainer.getNodeType() == 3) {
         String s = this.fStartContainer.getNodeValue();
         String sub = s.substring(this.fStartOffset, this.fEndOffset);
         if (how != 2) {
            ((TextImpl)this.fStartContainer).deleteData(this.fStartOffset, this.fEndOffset - this.fStartOffset);
            this.collapse(true);
         }

         if (how == 3) {
            return null;
         } else {
            frag.appendChild(this.fDocument.createTextNode(sub));
            return frag;
         }
      } else {
         Node n = this.getSelectedNode(this.fStartContainer, this.fStartOffset);

         Node sibling;
         for(int cnt = this.fEndOffset - this.fStartOffset; cnt > 0; n = sibling) {
            sibling = n.getNextSibling();
            Node xferNode = this.traverseFullySelected(n, how);
            if (frag != null) {
               frag.appendChild(xferNode);
            }

            --cnt;
         }

         if (how != 2) {
            this.collapse(true);
         }

         return frag;
      }
   }

   private DocumentFragment traverseCommonStartContainer(Node endAncestor, int how) {
      DocumentFragment frag = null;
      if (how != 3) {
         frag = this.fDocument.createDocumentFragment();
      }

      Node n = this.traverseRightBoundary(endAncestor, how);
      if (frag != null) {
         frag.appendChild(n);
      }

      int endIdx = this.indexOf(endAncestor, this.fStartContainer);
      int cnt = endIdx - this.fStartOffset;
      if (cnt <= 0) {
         if (how != 2) {
            this.setEndBefore(endAncestor);
            this.collapse(false);
         }

         return frag;
      } else {
         Node sibling;
         for(n = endAncestor.getPreviousSibling(); cnt > 0; n = sibling) {
            sibling = n.getPreviousSibling();
            Node xferNode = this.traverseFullySelected(n, how);
            if (frag != null) {
               frag.insertBefore(xferNode, frag.getFirstChild());
            }

            --cnt;
         }

         if (how != 2) {
            this.setEndBefore(endAncestor);
            this.collapse(false);
         }

         return frag;
      }
   }

   private DocumentFragment traverseCommonEndContainer(Node startAncestor, int how) {
      DocumentFragment frag = null;
      if (how != 3) {
         frag = this.fDocument.createDocumentFragment();
      }

      Node n = this.traverseLeftBoundary(startAncestor, how);
      if (frag != null) {
         frag.appendChild(n);
      }

      int startIdx = this.indexOf(startAncestor, this.fEndContainer);
      ++startIdx;
      int cnt = this.fEndOffset - startIdx;

      Node sibling;
      for(n = startAncestor.getNextSibling(); cnt > 0; n = sibling) {
         sibling = n.getNextSibling();
         Node xferNode = this.traverseFullySelected(n, how);
         if (frag != null) {
            frag.appendChild(xferNode);
         }

         --cnt;
      }

      if (how != 2) {
         this.setStartAfter(startAncestor);
         this.collapse(true);
      }

      return frag;
   }

   private DocumentFragment traverseCommonAncestors(Node startAncestor, Node endAncestor, int how) {
      DocumentFragment frag = null;
      if (how != 3) {
         frag = this.fDocument.createDocumentFragment();
      }

      Node n = this.traverseLeftBoundary(startAncestor, how);
      if (frag != null) {
         frag.appendChild(n);
      }

      Node commonParent = startAncestor.getParentNode();
      int startOffset = this.indexOf(startAncestor, commonParent);
      int endOffset = this.indexOf(endAncestor, commonParent);
      ++startOffset;
      int cnt = endOffset - startOffset;

      for(Node sibling = startAncestor.getNextSibling(); cnt > 0; --cnt) {
         Node nextSibling = sibling.getNextSibling();
         n = this.traverseFullySelected(sibling, how);
         if (frag != null) {
            frag.appendChild(n);
         }

         sibling = nextSibling;
      }

      n = this.traverseRightBoundary(endAncestor, how);
      if (frag != null) {
         frag.appendChild(n);
      }

      if (how != 2) {
         this.setStartAfter(startAncestor);
         this.collapse(true);
      }

      return frag;
   }

   private Node traverseRightBoundary(Node root, int how) {
      Node next = this.getSelectedNode(this.fEndContainer, this.fEndOffset - 1);
      boolean isFullySelected = next != this.fEndContainer;
      if (next == root) {
         return this.traverseNode(next, isFullySelected, false, how);
      } else {
         Node parent = next.getParentNode();

         Node clonedGrandParent;
         for(Node clonedParent = this.traverseNode(parent, false, false, how); parent != null; clonedParent = clonedGrandParent) {
            while(next != null) {
               clonedGrandParent = next.getPreviousSibling();
               Node clonedChild = this.traverseNode(next, isFullySelected, false, how);
               if (how != 3) {
                  clonedParent.insertBefore(clonedChild, clonedParent.getFirstChild());
               }

               isFullySelected = true;
               next = clonedGrandParent;
            }

            if (parent == root) {
               return clonedParent;
            }

            next = parent.getPreviousSibling();
            parent = parent.getParentNode();
            clonedGrandParent = this.traverseNode(parent, false, false, how);
            if (how != 3) {
               clonedGrandParent.appendChild(clonedParent);
            }
         }

         return null;
      }
   }

   private Node traverseLeftBoundary(Node root, int how) {
      Node next = this.getSelectedNode(this.getStartContainer(), this.getStartOffset());
      boolean isFullySelected = next != this.getStartContainer();
      if (next == root) {
         return this.traverseNode(next, isFullySelected, true, how);
      } else {
         Node parent = next.getParentNode();

         Node clonedGrandParent;
         for(Node clonedParent = this.traverseNode(parent, false, true, how); parent != null; clonedParent = clonedGrandParent) {
            while(next != null) {
               clonedGrandParent = next.getNextSibling();
               Node clonedChild = this.traverseNode(next, isFullySelected, true, how);
               if (how != 3) {
                  clonedParent.appendChild(clonedChild);
               }

               isFullySelected = true;
               next = clonedGrandParent;
            }

            if (parent == root) {
               return clonedParent;
            }

            next = parent.getNextSibling();
            parent = parent.getParentNode();
            clonedGrandParent = this.traverseNode(parent, false, true, how);
            if (how != 3) {
               clonedGrandParent.appendChild(clonedParent);
            }
         }

         return null;
      }
   }

   private Node traverseNode(Node n, boolean isFullySelected, boolean isLeft, int how) {
      if (isFullySelected) {
         return this.traverseFullySelected(n, how);
      } else {
         return n.getNodeType() == 3 ? this.traverseTextNode(n, isLeft, how) : this.traversePartiallySelected(n, how);
      }
   }

   private Node traverseFullySelected(Node n, int how) {
      switch(how) {
      case 1:
         if (n.getNodeType() == 10) {
            throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
         }

         return n;
      case 2:
         return n.cloneNode(true);
      case 3:
         n.getParentNode().removeChild(n);
         return null;
      default:
         return null;
      }
   }

   private Node traversePartiallySelected(Node n, int how) {
      switch(how) {
      case 1:
      case 2:
         return n.cloneNode(false);
      case 3:
         return null;
      default:
         return null;
      }
   }

   private Node traverseTextNode(Node n, boolean isLeft, int how) {
      String txtValue = n.getNodeValue();
      String newNodeValue;
      String oldNodeValue;
      int offset;
      if (isLeft) {
         offset = this.getStartOffset();
         newNodeValue = txtValue.substring(offset);
         oldNodeValue = txtValue.substring(0, offset);
      } else {
         offset = this.getEndOffset();
         newNodeValue = txtValue.substring(0, offset);
         oldNodeValue = txtValue.substring(offset);
      }

      if (how != 2) {
         n.setNodeValue(oldNodeValue);
      }

      if (how == 3) {
         return null;
      } else {
         Node newNode = n.cloneNode(false);
         newNode.setNodeValue(newNodeValue);
         return newNode;
      }
   }

   void checkIndex(Node refNode, int offset) throws DOMException {
      if (offset < 0) {
         throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null));
      } else {
         int type = refNode.getNodeType();
         if (type != 3 && type != 4 && type != 8 && type != 7) {
            if (offset > refNode.getChildNodes().getLength()) {
               throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null));
            }
         } else if (offset > refNode.getNodeValue().length()) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null));
         }

      }
   }

   private Node getRootContainer(Node node) {
      if (node == null) {
         return null;
      } else {
         while(node.getParentNode() != null) {
            node = node.getParentNode();
         }

         return node;
      }
   }

   private boolean isLegalContainer(Node node) {
      if (node == null) {
         return false;
      } else {
         while(node != null) {
            switch(node.getNodeType()) {
            case 6:
            case 10:
            case 12:
               return false;
            default:
               node = node.getParentNode();
            }
         }

         return true;
      }
   }

   private boolean hasLegalRootContainer(Node node) {
      if (node == null) {
         return false;
      } else {
         Node rootContainer = this.getRootContainer(node);
         switch(rootContainer.getNodeType()) {
         case 2:
         case 9:
         case 11:
            return true;
         default:
            return false;
         }
      }
   }

   private boolean isLegalContainedNode(Node node) {
      if (node == null) {
         return false;
      } else {
         switch(node.getNodeType()) {
         case 2:
         case 6:
         case 9:
         case 11:
         case 12:
            return false;
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         case 10:
         default:
            return true;
         }
      }
   }

   Node nextNode(Node node, boolean visitChildren) {
      if (node == null) {
         return null;
      } else {
         Node result;
         if (visitChildren) {
            result = node.getFirstChild();
            if (result != null) {
               return result;
            }
         }

         result = node.getNextSibling();
         if (result != null) {
            return result;
         } else {
            for(Node parent = node.getParentNode(); parent != null && parent != this.fDocument; parent = parent.getParentNode()) {
               result = parent.getNextSibling();
               if (result != null) {
                  return result;
               }
            }

            return null;
         }
      }
   }

   boolean isAncestorOf(Node a, Node b) {
      for(Node node = b; node != null; node = node.getParentNode()) {
         if (node == a) {
            return true;
         }
      }

      return false;
   }

   int indexOf(Node child, Node parent) {
      if (child.getParentNode() != parent) {
         return -1;
      } else {
         int i = 0;

         for(Node node = parent.getFirstChild(); node != child; node = node.getNextSibling()) {
            ++i;
         }

         return i;
      }
   }

   private Node getSelectedNode(Node container, int offset) {
      if (container.getNodeType() == 3) {
         return container;
      } else if (offset < 0) {
         return container;
      } else {
         Node child;
         for(child = container.getFirstChild(); child != null && offset > 0; child = child.getNextSibling()) {
            --offset;
         }

         return child != null ? child : container;
      }
   }
}
