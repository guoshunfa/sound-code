package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class NodeImpl implements Node, NodeList, EventTarget, Cloneable, Serializable {
   public static final short TREE_POSITION_PRECEDING = 1;
   public static final short TREE_POSITION_FOLLOWING = 2;
   public static final short TREE_POSITION_ANCESTOR = 4;
   public static final short TREE_POSITION_DESCENDANT = 8;
   public static final short TREE_POSITION_EQUIVALENT = 16;
   public static final short TREE_POSITION_SAME_NODE = 32;
   public static final short TREE_POSITION_DISCONNECTED = 0;
   public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
   public static final short DOCUMENT_POSITION_PRECEDING = 2;
   public static final short DOCUMENT_POSITION_FOLLOWING = 4;
   public static final short DOCUMENT_POSITION_CONTAINS = 8;
   public static final short DOCUMENT_POSITION_IS_CONTAINED = 16;
   public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
   static final long serialVersionUID = -6316591992167219696L;
   public static final short ELEMENT_DEFINITION_NODE = 21;
   protected NodeImpl ownerNode;
   protected short flags;
   protected static final short READONLY = 1;
   protected static final short SYNCDATA = 2;
   protected static final short SYNCCHILDREN = 4;
   protected static final short OWNED = 8;
   protected static final short FIRSTCHILD = 16;
   protected static final short SPECIFIED = 32;
   protected static final short IGNORABLEWS = 64;
   protected static final short HASSTRING = 128;
   protected static final short NORMALIZED = 256;
   protected static final short ID = 512;

   protected NodeImpl(CoreDocumentImpl ownerDocument) {
      this.ownerNode = ownerDocument;
   }

   public NodeImpl() {
   }

   public abstract short getNodeType();

   public abstract String getNodeName();

   public String getNodeValue() throws DOMException {
      return null;
   }

   public void setNodeValue(String x) throws DOMException {
   }

   public Node appendChild(Node newChild) throws DOMException {
      return this.insertBefore(newChild, (Node)null);
   }

   public Node cloneNode(boolean deep) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      NodeImpl newnode;
      try {
         newnode = (NodeImpl)this.clone();
      } catch (CloneNotSupportedException var4) {
         throw new RuntimeException("**Internal Error**" + var4);
      }

      newnode.ownerNode = this.ownerDocument();
      newnode.isOwned(false);
      newnode.isReadOnly(false);
      this.ownerDocument().callUserDataHandlers(this, newnode, (short)1);
      return newnode;
   }

   public Document getOwnerDocument() {
      return (Document)(this.isOwned() ? this.ownerNode.ownerDocument() : (Document)this.ownerNode);
   }

   CoreDocumentImpl ownerDocument() {
      return this.isOwned() ? this.ownerNode.ownerDocument() : (CoreDocumentImpl)this.ownerNode;
   }

   void setOwnerDocument(CoreDocumentImpl doc) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (!this.isOwned()) {
         this.ownerNode = doc;
      }

   }

   protected int getNodeNumber() {
      CoreDocumentImpl cd = (CoreDocumentImpl)((CoreDocumentImpl)this.getOwnerDocument());
      int nodeNumber = cd.getNodeNumber(this);
      return nodeNumber;
   }

   public Node getParentNode() {
      return null;
   }

   NodeImpl parentNode() {
      return null;
   }

   public Node getNextSibling() {
      return null;
   }

   public Node getPreviousSibling() {
      return null;
   }

   ChildNode previousSibling() {
      return null;
   }

   public NamedNodeMap getAttributes() {
      return null;
   }

   public boolean hasAttributes() {
      return false;
   }

   public boolean hasChildNodes() {
      return false;
   }

   public NodeList getChildNodes() {
      return this;
   }

   public Node getFirstChild() {
      return null;
   }

   public Node getLastChild() {
      return null;
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException {
      throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
   }

   public Node removeChild(Node oldChild) throws DOMException {
      throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null));
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
      throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null));
   }

   public int getLength() {
      return 0;
   }

   public Node item(int index) {
      return null;
   }

   public void normalize() {
   }

   public boolean isSupported(String feature, String version) {
      return this.ownerDocument().getImplementation().hasFeature(feature, version);
   }

   public String getNamespaceURI() {
      return null;
   }

   public String getPrefix() {
      return null;
   }

   public void setPrefix(String prefix) throws DOMException {
      throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null));
   }

   public String getLocalName() {
      return null;
   }

   public void addEventListener(String type, EventListener listener, boolean useCapture) {
      this.ownerDocument().addEventListener(this, type, listener, useCapture);
   }

   public void removeEventListener(String type, EventListener listener, boolean useCapture) {
      this.ownerDocument().removeEventListener(this, type, listener, useCapture);
   }

   public boolean dispatchEvent(Event event) {
      return this.ownerDocument().dispatchEvent(this, event);
   }

   public String getBaseURI() {
      return null;
   }

   /** @deprecated */
   public short compareTreePosition(Node other) {
      if (this == other) {
         return 48;
      } else {
         short thisType = this.getNodeType();
         short otherType = other.getNodeType();
         if (thisType != 6 && thisType != 12 && otherType != 6 && otherType != 12) {
            Node thisAncestor = this;
            Node otherAncestor = other;
            int thisDepth = 0;
            int otherDepth = 0;

            Object node;
            for(node = this; node != null; node = ((Node)node).getParentNode()) {
               ++thisDepth;
               if (node == other) {
                  return 5;
               }

               thisAncestor = node;
            }

            for(Node node = other; node != null; node = node.getParentNode()) {
               ++otherDepth;
               if (node == this) {
                  return 10;
               }

               otherAncestor = node;
            }

            Node thisNode = this;
            Node otherNode = other;
            int thisAncestorType = ((Node)thisAncestor).getNodeType();
            int otherAncestorType = ((Node)otherAncestor).getNodeType();
            if (thisAncestorType == 2) {
               thisNode = ((AttrImpl)thisAncestor).getOwnerElement();
            }

            if (otherAncestorType == 2) {
               otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
            }

            if (thisAncestorType == 2 && otherAncestorType == 2 && thisNode == otherNode) {
               return 16;
            } else {
               if (thisAncestorType == 2) {
                  thisDepth = 0;

                  for(node = thisNode; node != null; node = ((Node)node).getParentNode()) {
                     ++thisDepth;
                     if (node == otherNode) {
                        return 1;
                     }

                     thisAncestor = node;
                  }
               }

               if (otherAncestorType == 2) {
                  otherDepth = 0;

                  for(node = otherNode; node != null; node = ((Node)node).getParentNode()) {
                     ++otherDepth;
                     if (node == thisNode) {
                        return 2;
                     }

                     otherAncestor = node;
                  }
               }

               if (thisAncestor != otherAncestor) {
                  return 0;
               } else {
                  int i;
                  if (thisDepth > otherDepth) {
                     for(i = 0; i < thisDepth - otherDepth; ++i) {
                        thisNode = ((Node)thisNode).getParentNode();
                     }

                     if (thisNode == otherNode) {
                        return 1;
                     }
                  } else {
                     for(i = 0; i < otherDepth - thisDepth; ++i) {
                        otherNode = ((Node)otherNode).getParentNode();
                     }

                     if (otherNode == thisNode) {
                        return 2;
                     }
                  }

                  Node thisNodeP = ((Node)thisNode).getParentNode();

                  for(Node otherNodeP = ((Node)otherNode).getParentNode(); thisNodeP != otherNodeP; otherNodeP = otherNodeP.getParentNode()) {
                     thisNode = thisNodeP;
                     otherNode = otherNodeP;
                     thisNodeP = thisNodeP.getParentNode();
                  }

                  for(Node current = thisNodeP.getFirstChild(); current != null; current = current.getNextSibling()) {
                     if (current == otherNode) {
                        return 1;
                     }

                     if (current == thisNode) {
                        return 2;
                     }
                  }

                  return 0;
               }
            }
         } else {
            return 0;
         }
      }
   }

   public short compareDocumentPosition(Node other) throws DOMException {
      if (this == other) {
         return 0;
      } else {
         try {
            NodeImpl var2 = (NodeImpl)other;
         } catch (ClassCastException var16) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
            throw new DOMException((short)9, msg);
         }

         Document thisOwnerDoc;
         if (this.getNodeType() == 9) {
            thisOwnerDoc = (Document)this;
         } else {
            thisOwnerDoc = this.getOwnerDocument();
         }

         Document otherOwnerDoc;
         if (other.getNodeType() == 9) {
            otherOwnerDoc = (Document)other;
         } else {
            otherOwnerDoc = other.getOwnerDocument();
         }

         if (thisOwnerDoc != otherOwnerDoc && thisOwnerDoc != null && otherOwnerDoc != null) {
            int otherDocNum = ((CoreDocumentImpl)otherOwnerDoc).getNodeNumber();
            int thisDocNum = ((CoreDocumentImpl)thisOwnerDoc).getNodeNumber();
            return (short)(otherDocNum > thisDocNum ? 37 : 35);
         } else {
            Node thisAncestor = this;
            Node otherAncestor = other;
            int thisDepth = 0;
            int otherDepth = 0;

            Object node;
            for(node = this; node != null; node = ((Node)node).getParentNode()) {
               ++thisDepth;
               if (node == other) {
                  return 10;
               }

               thisAncestor = node;
            }

            for(Node node = other; node != null; node = node.getParentNode()) {
               ++otherDepth;
               if (node == this) {
                  return 20;
               }

               otherAncestor = node;
            }

            short otherAncestorType;
            Object thisNode;
            Object otherNode;
            DocumentType container;
            int thisAncestorType = ((Node)thisAncestor).getNodeType();
            otherAncestorType = ((Node)otherAncestor).getNodeType();
            thisNode = this;
            otherNode = other;
            label203:
            switch(thisAncestorType) {
            case 2:
               thisNode = ((AttrImpl)thisAncestor).getOwnerElement();
               if (otherAncestorType == 2) {
                  otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
                  if (otherNode == thisNode) {
                     if (((NamedNodeMapImpl)((Node)thisNode).getAttributes()).precedes(other, this)) {
                        return 34;
                     }

                     return 36;
                  }
               }

               thisDepth = 0;
               node = thisNode;

               while(true) {
                  if (node == null) {
                     break label203;
                  }

                  ++thisDepth;
                  if (node == otherNode) {
                     return 10;
                  }

                  thisAncestor = node;
                  node = ((Node)node).getParentNode();
               }
            case 6:
            case 12:
               container = thisOwnerDoc.getDoctype();
               if (container == otherAncestor) {
                  return 10;
               }

               switch(otherAncestorType) {
               case 6:
               case 12:
                  if (thisAncestorType != otherAncestorType) {
                     return (short)(thisAncestorType > otherAncestorType ? 2 : 4);
                  }

                  if (thisAncestorType == 12) {
                     if (((NamedNodeMapImpl)container.getNotations()).precedes((Node)otherAncestor, (Node)thisAncestor)) {
                        return 34;
                     }

                     return 36;
                  }

                  if (((NamedNodeMapImpl)container.getEntities()).precedes((Node)otherAncestor, (Node)thisAncestor)) {
                     return 34;
                  }

                  return 36;
               default:
                  thisAncestor = thisOwnerDoc;
                  thisNode = thisOwnerDoc;
                  break label203;
               }
            case 10:
               if (other == thisOwnerDoc) {
                  return 10;
               }

               if (thisOwnerDoc != null && thisOwnerDoc == otherOwnerDoc) {
                  return 4;
               }
            }

            label194:
            switch(otherAncestorType) {
            case 2:
               otherDepth = 0;
               otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
               node = otherNode;

               while(true) {
                  if (node == null) {
                     break label194;
                  }

                  ++otherDepth;
                  if (node == thisNode) {
                     return 20;
                  }

                  otherAncestor = node;
                  node = ((Node)node).getParentNode();
               }
            case 6:
            case 12:
               container = thisOwnerDoc.getDoctype();
               if (container == this) {
                  return 20;
               }

               otherAncestor = thisOwnerDoc;
               otherNode = thisOwnerDoc;
               break;
            case 10:
               if (thisNode == otherOwnerDoc) {
                  return 20;
               }

               if (otherOwnerDoc != null && thisOwnerDoc == otherOwnerDoc) {
                  return 2;
               }
            }

            int i;
            if (thisAncestor != otherAncestor) {
               i = ((NodeImpl)thisAncestor).getNodeNumber();
               int otherAncestorNum = ((NodeImpl)otherAncestor).getNodeNumber();
               return (short)(i > otherAncestorNum ? 37 : 35);
            } else {
               if (thisDepth > otherDepth) {
                  for(i = 0; i < thisDepth - otherDepth; ++i) {
                     thisNode = ((Node)thisNode).getParentNode();
                  }

                  if (thisNode == otherNode) {
                     return 2;
                  }
               } else {
                  for(i = 0; i < otherDepth - thisDepth; ++i) {
                     otherNode = ((Node)otherNode).getParentNode();
                  }

                  if (otherNode == thisNode) {
                     return 4;
                  }
               }

               Node thisNodeP = ((Node)thisNode).getParentNode();

               for(Node otherNodeP = ((Node)otherNode).getParentNode(); thisNodeP != otherNodeP; otherNodeP = otherNodeP.getParentNode()) {
                  thisNode = thisNodeP;
                  otherNode = otherNodeP;
                  thisNodeP = thisNodeP.getParentNode();
               }

               for(Node current = thisNodeP.getFirstChild(); current != null; current = current.getNextSibling()) {
                  if (current == otherNode) {
                     return 2;
                  }

                  if (current == thisNode) {
                     return 4;
                  }
               }

               return 0;
            }
         }
      }
   }

   public String getTextContent() throws DOMException {
      return this.getNodeValue();
   }

   void getTextContent(StringBuffer buf) throws DOMException {
      String content = this.getNodeValue();
      if (content != null) {
         buf.append(content);
      }

   }

   public void setTextContent(String textContent) throws DOMException {
      this.setNodeValue(textContent);
   }

   public boolean isSameNode(Node other) {
      return this == other;
   }

   public boolean isDefaultNamespace(String namespaceURI) {
      short type = this.getNodeType();
      switch(type) {
      case 1:
         String namespace = this.getNamespaceURI();
         String prefix = this.getPrefix();
         if (prefix != null && prefix.length() != 0) {
            if (this.hasAttributes()) {
               ElementImpl elem = (ElementImpl)this;
               NodeImpl attr = (NodeImpl)elem.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
               if (attr != null) {
                  String value = attr.getNodeValue();
                  if (namespaceURI == null) {
                     return namespace == value;
                  }

                  return namespaceURI.equals(value);
               }
            }

            NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
            if (ancestor != null) {
               return ancestor.isDefaultNamespace(namespaceURI);
            }

            return false;
         } else {
            if (namespaceURI == null) {
               return namespace == namespaceURI;
            }

            return namespaceURI.equals(namespace);
         }
      case 2:
         if (this.ownerNode.getNodeType() == 1) {
            return this.ownerNode.isDefaultNamespace(namespaceURI);
         }

         return false;
      case 3:
      case 4:
      case 5:
      case 7:
      case 8:
      default:
         NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
         if (ancestor != null) {
            return ancestor.isDefaultNamespace(namespaceURI);
         }

         return false;
      case 6:
      case 10:
      case 11:
      case 12:
         return false;
      case 9:
         return ((NodeImpl)((Document)this).getDocumentElement()).isDefaultNamespace(namespaceURI);
      }
   }

   public String lookupPrefix(String namespaceURI) {
      if (namespaceURI == null) {
         return null;
      } else {
         short type = this.getNodeType();
         switch(type) {
         case 1:
            String namespace = this.getNamespaceURI();
            return this.lookupNamespacePrefix(namespaceURI, (ElementImpl)this);
         case 2:
            if (this.ownerNode.getNodeType() == 1) {
               return this.ownerNode.lookupPrefix(namespaceURI);
            }

            return null;
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         default:
            NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
            if (ancestor != null) {
               return ancestor.lookupPrefix(namespaceURI);
            }

            return null;
         case 6:
         case 10:
         case 11:
         case 12:
            return null;
         case 9:
            return ((NodeImpl)((Document)this).getDocumentElement()).lookupPrefix(namespaceURI);
         }
      }
   }

   public String lookupNamespaceURI(String specifiedPrefix) {
      short type = this.getNodeType();
      switch(type) {
      case 1:
         String namespace = this.getNamespaceURI();
         String prefix = this.getPrefix();
         if (namespace != null) {
            if (specifiedPrefix == null && prefix == specifiedPrefix) {
               return namespace;
            }

            if (prefix != null && prefix.equals(specifiedPrefix)) {
               return namespace;
            }
         }

         if (this.hasAttributes()) {
            NamedNodeMap map = this.getAttributes();
            int length = map.getLength();

            for(int i = 0; i < length; ++i) {
               Node attr = map.item(i);
               String attrPrefix = attr.getPrefix();
               String value = attr.getNodeValue();
               namespace = attr.getNamespaceURI();
               if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                  if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                     return value;
                  }

                  if (attrPrefix != null && attrPrefix.equals("xmlns") && attr.getLocalName().equals(specifiedPrefix)) {
                     return value;
                  }
               }
            }
         }

         NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
         if (ancestor != null) {
            return ancestor.lookupNamespaceURI(specifiedPrefix);
         }

         return null;
      case 2:
         if (this.ownerNode.getNodeType() == 1) {
            return this.ownerNode.lookupNamespaceURI(specifiedPrefix);
         }

         return null;
      case 3:
      case 4:
      case 5:
      case 7:
      case 8:
      default:
         NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
         if (ancestor != null) {
            return ancestor.lookupNamespaceURI(specifiedPrefix);
         }

         return null;
      case 6:
      case 10:
      case 11:
      case 12:
         return null;
      case 9:
         return ((NodeImpl)((Document)this).getDocumentElement()).lookupNamespaceURI(specifiedPrefix);
      }
   }

   Node getElementAncestor(Node currentNode) {
      Node parent = currentNode.getParentNode();
      if (parent != null) {
         short type = parent.getNodeType();
         return type == 1 ? parent : this.getElementAncestor(parent);
      } else {
         return null;
      }
   }

   String lookupNamespacePrefix(String namespaceURI, ElementImpl el) {
      String namespace = this.getNamespaceURI();
      String prefix = this.getPrefix();
      if (namespace != null && namespace.equals(namespaceURI) && prefix != null) {
         String foundNamespace = el.lookupNamespaceURI(prefix);
         if (foundNamespace != null && foundNamespace.equals(namespaceURI)) {
            return prefix;
         }
      }

      if (this.hasAttributes()) {
         NamedNodeMap map = this.getAttributes();
         int length = map.getLength();

         for(int i = 0; i < length; ++i) {
            Node attr = map.item(i);
            String attrPrefix = attr.getPrefix();
            String value = attr.getNodeValue();
            namespace = attr.getNamespaceURI();
            if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/") && (attr.getNodeName().equals("xmlns") || attrPrefix != null && attrPrefix.equals("xmlns") && value.equals(namespaceURI))) {
               String localname = attr.getLocalName();
               String foundNamespace = el.lookupNamespaceURI(localname);
               if (foundNamespace != null && foundNamespace.equals(namespaceURI)) {
                  return localname;
               }
            }
         }
      }

      NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
      return ancestor != null ? ancestor.lookupNamespacePrefix(namespaceURI, el) : null;
   }

   public boolean isEqualNode(Node arg) {
      if (arg == this) {
         return true;
      } else if (arg.getNodeType() != this.getNodeType()) {
         return false;
      } else {
         if (this.getNodeName() == null) {
            if (arg.getNodeName() != null) {
               return false;
            }
         } else if (!this.getNodeName().equals(arg.getNodeName())) {
            return false;
         }

         if (this.getLocalName() == null) {
            if (arg.getLocalName() != null) {
               return false;
            }
         } else if (!this.getLocalName().equals(arg.getLocalName())) {
            return false;
         }

         if (this.getNamespaceURI() == null) {
            if (arg.getNamespaceURI() != null) {
               return false;
            }
         } else if (!this.getNamespaceURI().equals(arg.getNamespaceURI())) {
            return false;
         }

         if (this.getPrefix() == null) {
            if (arg.getPrefix() != null) {
               return false;
            }
         } else if (!this.getPrefix().equals(arg.getPrefix())) {
            return false;
         }

         if (this.getNodeValue() == null) {
            if (arg.getNodeValue() != null) {
               return false;
            }
         } else if (!this.getNodeValue().equals(arg.getNodeValue())) {
            return false;
         }

         return true;
      }
   }

   public Object getFeature(String feature, String version) {
      return this.isSupported(feature, version) ? this : null;
   }

   public Object setUserData(String key, Object data, UserDataHandler handler) {
      return this.ownerDocument().setUserData(this, key, data, handler);
   }

   public Object getUserData(String key) {
      return this.ownerDocument().getUserData(this, key);
   }

   protected Map<String, ParentNode.UserDataRecord> getUserDataRecord() {
      return this.ownerDocument().getUserDataRecord(this);
   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.isReadOnly(readOnly);
   }

   public boolean getReadOnly() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.isReadOnly();
   }

   public void setUserData(Object data) {
      this.ownerDocument().setUserData(this, data);
   }

   public Object getUserData() {
      return this.ownerDocument().getUserData(this);
   }

   protected void changed() {
      this.ownerDocument().changed();
   }

   protected int changes() {
      return this.ownerDocument().changes();
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
   }

   protected Node getContainer() {
      return null;
   }

   final boolean isReadOnly() {
      return (this.flags & 1) != 0;
   }

   final void isReadOnly(boolean value) {
      this.flags = (short)(value ? this.flags | 1 : this.flags & -2);
   }

   final boolean needsSyncData() {
      return (this.flags & 2) != 0;
   }

   final void needsSyncData(boolean value) {
      this.flags = (short)(value ? this.flags | 2 : this.flags & -3);
   }

   final boolean needsSyncChildren() {
      return (this.flags & 4) != 0;
   }

   public final void needsSyncChildren(boolean value) {
      this.flags = (short)(value ? this.flags | 4 : this.flags & -5);
   }

   final boolean isOwned() {
      return (this.flags & 8) != 0;
   }

   final void isOwned(boolean value) {
      this.flags = (short)(value ? this.flags | 8 : this.flags & -9);
   }

   final boolean isFirstChild() {
      return (this.flags & 16) != 0;
   }

   final void isFirstChild(boolean value) {
      this.flags = (short)(value ? this.flags | 16 : this.flags & -17);
   }

   final boolean isSpecified() {
      return (this.flags & 32) != 0;
   }

   final void isSpecified(boolean value) {
      this.flags = (short)(value ? this.flags | 32 : this.flags & -33);
   }

   final boolean internalIsIgnorableWhitespace() {
      return (this.flags & 64) != 0;
   }

   final void isIgnorableWhitespace(boolean value) {
      this.flags = (short)(value ? this.flags | 64 : this.flags & -65);
   }

   final boolean hasStringValue() {
      return (this.flags & 128) != 0;
   }

   final void hasStringValue(boolean value) {
      this.flags = (short)(value ? this.flags | 128 : this.flags & -129);
   }

   final boolean isNormalized() {
      return (this.flags & 256) != 0;
   }

   final void isNormalized(boolean value) {
      if (!value && this.isNormalized() && this.ownerNode != null) {
         this.ownerNode.isNormalized(false);
      }

      this.flags = (short)(value ? this.flags | 256 : this.flags & -257);
   }

   final boolean isIdAttribute() {
      return (this.flags & 512) != 0;
   }

   final void isIdAttribute(boolean value) {
      this.flags = (short)(value ? this.flags | 512 : this.flags & -513);
   }

   public String toString() {
      return "[" + this.getNodeName() + ": " + this.getNodeValue() + "]";
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      out.defaultWriteObject();
   }
}
