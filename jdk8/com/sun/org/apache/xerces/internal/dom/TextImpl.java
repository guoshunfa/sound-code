package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements CharacterData, Text {
   static final long serialVersionUID = -5294980852957403469L;

   public TextImpl() {
   }

   public TextImpl(CoreDocumentImpl ownerDoc, String data) {
      super(ownerDoc, data);
   }

   public void setValues(CoreDocumentImpl ownerDoc, String data) {
      this.flags = 0;
      this.nextSibling = null;
      this.previousSibling = null;
      this.setOwnerDocument(ownerDoc);
      super.data = data;
   }

   public short getNodeType() {
      return 3;
   }

   public String getNodeName() {
      return "#text";
   }

   public void setIgnorableWhitespace(boolean ignore) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.isIgnorableWhitespace(ignore);
   }

   public boolean isElementContentWhitespace() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.internalIsIgnorableWhitespace();
   }

   public String getWholeText() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.fBufferStr == null) {
         this.fBufferStr = new StringBuffer();
      } else {
         this.fBufferStr.setLength(0);
      }

      if (this.data != null && this.data.length() != 0) {
         this.fBufferStr.append(this.data);
      }

      this.getWholeTextBackward(this.getPreviousSibling(), this.fBufferStr, this.getParentNode());
      String temp = this.fBufferStr.toString();
      this.fBufferStr.setLength(0);
      this.getWholeTextForward(this.getNextSibling(), this.fBufferStr, this.getParentNode());
      return temp + this.fBufferStr.toString();
   }

   protected void insertTextContent(StringBuffer buf) throws DOMException {
      String content = this.getNodeValue();
      if (content != null) {
         buf.insert(0, (String)content);
      }

   }

   private boolean getWholeTextForward(Node node, StringBuffer buffer, Node parent) {
      boolean inEntRef = false;
      if (parent != null) {
         inEntRef = parent.getNodeType() == 5;
      }

      for(; node != null; node = node.getNextSibling()) {
         short type = node.getNodeType();
         if (type == 5) {
            if (this.getWholeTextForward(node.getFirstChild(), buffer, node)) {
               return true;
            }
         } else {
            if (type != 3 && type != 4) {
               return true;
            }

            ((NodeImpl)node).getTextContent(buffer);
         }
      }

      if (inEntRef) {
         this.getWholeTextForward(parent.getNextSibling(), buffer, parent.getParentNode());
         return true;
      } else {
         return false;
      }
   }

   private boolean getWholeTextBackward(Node node, StringBuffer buffer, Node parent) {
      boolean inEntRef = false;
      if (parent != null) {
         inEntRef = parent.getNodeType() == 5;
      }

      for(; node != null; node = node.getPreviousSibling()) {
         short type = node.getNodeType();
         if (type == 5) {
            if (this.getWholeTextBackward(node.getLastChild(), buffer, node)) {
               return true;
            }
         } else {
            if (type != 3 && type != 4) {
               return true;
            }

            ((TextImpl)node).insertTextContent(buffer);
         }
      }

      if (inEntRef) {
         this.getWholeTextBackward(parent.getPreviousSibling(), buffer, parent.getParentNode());
         return true;
      } else {
         return false;
      }
   }

   public Text replaceWholeText(String content) throws DOMException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      Node parent = this.getParentNode();
      if (content != null && content.length() != 0) {
         if (this.ownerDocument().errorChecking) {
            if (!this.canModifyPrev(this)) {
               throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
            }

            if (!this.canModifyNext(this)) {
               throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
            }
         }

         Text currentNode = null;
         if (this.isReadOnly()) {
            Text newNode = this.ownerDocument().createTextNode(content);
            if (parent == null) {
               return newNode;
            }

            parent.insertBefore(newNode, this);
            parent.removeChild(this);
            currentNode = newNode;
         } else {
            this.setData(content);
            currentNode = this;
         }

         for(Node prev = ((Text)currentNode).getPreviousSibling(); prev != null && (prev.getNodeType() == 3 || prev.getNodeType() == 4 || prev.getNodeType() == 5 && this.hasTextOnlyChildren(prev)); prev = ((Node)currentNode).getPreviousSibling()) {
            parent.removeChild(prev);
         }

         for(Node next = ((Text)currentNode).getNextSibling(); next != null && (next.getNodeType() == 3 || next.getNodeType() == 4 || next.getNodeType() == 5 && this.hasTextOnlyChildren(next)); next = ((Node)currentNode).getNextSibling()) {
            parent.removeChild(next);
         }

         return (Text)currentNode;
      } else {
         if (parent != null) {
            parent.removeChild(this);
         }

         return null;
      }
   }

   private boolean canModifyPrev(Node node) {
      boolean textLastChild = false;

      for(Node prev = node.getPreviousSibling(); prev != null; prev = prev.getPreviousSibling()) {
         short type = prev.getNodeType();
         if (type != 5) {
            if (type != 3 && type != 4) {
               return true;
            }
         } else {
            Node lastChild = prev.getLastChild();
            if (lastChild == null) {
               return false;
            }

            for(; lastChild != null; lastChild = lastChild.getPreviousSibling()) {
               short lType = lastChild.getNodeType();
               if (lType != 3 && lType != 4) {
                  if (lType != 5) {
                     if (textLastChild) {
                        return false;
                     }

                     return true;
                  }

                  if (!this.canModifyPrev(lastChild)) {
                     return false;
                  }

                  textLastChild = true;
               } else {
                  textLastChild = true;
               }
            }
         }
      }

      return true;
   }

   private boolean canModifyNext(Node node) {
      boolean textFirstChild = false;

      for(Node next = node.getNextSibling(); next != null; next = next.getNextSibling()) {
         short type = next.getNodeType();
         if (type != 5) {
            if (type != 3 && type != 4) {
               return true;
            }
         } else {
            Node firstChild = next.getFirstChild();
            if (firstChild == null) {
               return false;
            }

            for(; firstChild != null; firstChild = firstChild.getNextSibling()) {
               short lType = firstChild.getNodeType();
               if (lType != 3 && lType != 4) {
                  if (lType != 5) {
                     if (textFirstChild) {
                        return false;
                     }

                     return true;
                  }

                  if (!this.canModifyNext(firstChild)) {
                     return false;
                  }

                  textFirstChild = true;
               } else {
                  textFirstChild = true;
               }
            }
         }
      }

      return true;
   }

   private boolean hasTextOnlyChildren(Node node) {
      if (node == null) {
         return false;
      } else {
         for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            int type = child.getNodeType();
            if (type == 5) {
               return this.hasTextOnlyChildren(child);
            }

            if (type != 3 && type != 4 && type != 5) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isIgnorableWhitespace() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.internalIsIgnorableWhitespace();
   }

   public Text splitText(int offset) throws DOMException {
      if (this.isReadOnly()) {
         throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         if (offset >= 0 && offset <= this.data.length()) {
            Text newText = this.getOwnerDocument().createTextNode(this.data.substring(offset));
            this.setNodeValue(this.data.substring(0, offset));
            Node parentNode = this.getParentNode();
            if (parentNode != null) {
               parentNode.insertBefore(newText, this.nextSibling);
            }

            return newText;
         } else {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null));
         }
      }
   }

   public void replaceData(String value) {
      this.data = value;
   }

   public String removeData() {
      String olddata = this.data;
      this.data = "";
      return olddata;
   }
}
