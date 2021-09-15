package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl implements NamedNodeMap, Serializable {
   static final long serialVersionUID = -7039242451046758020L;
   protected short flags;
   protected static final short READONLY = 1;
   protected static final short CHANGED = 2;
   protected static final short HASDEFAULTS = 4;
   protected List nodes;
   protected NodeImpl ownerNode;

   protected NamedNodeMapImpl(NodeImpl ownerNode) {
      this.ownerNode = ownerNode;
   }

   public int getLength() {
      return this.nodes != null ? this.nodes.size() : 0;
   }

   public Node item(int index) {
      return this.nodes != null && index < this.nodes.size() ? (Node)((Node)this.nodes.get(index)) : null;
   }

   public Node getNamedItem(String name) {
      int i = this.findNamePoint(name, 0);
      return i < 0 ? null : (Node)((Node)this.nodes.get(i));
   }

   public Node getNamedItemNS(String namespaceURI, String localName) {
      int i = this.findNamePoint(namespaceURI, localName);
      return i < 0 ? null : (Node)((Node)this.nodes.get(i));
   }

   public Node setNamedItem(Node arg) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
      if (ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (arg.getOwnerDocument() != ownerDocument) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }
      }

      int i = this.findNamePoint(arg.getNodeName(), 0);
      NodeImpl previous = null;
      if (i >= 0) {
         previous = (NodeImpl)this.nodes.get(i);
         this.nodes.set(i, arg);
      } else {
         i = -1 - i;
         if (null == this.nodes) {
            this.nodes = new ArrayList(5);
         }

         this.nodes.add(i, arg);
      }

      return previous;
   }

   public Node setNamedItemNS(Node arg) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
      if (ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (arg.getOwnerDocument() != ownerDocument) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }
      }

      int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
      NodeImpl previous = null;
      if (i >= 0) {
         previous = (NodeImpl)this.nodes.get(i);
         this.nodes.set(i, arg);
      } else {
         i = this.findNamePoint(arg.getNodeName(), 0);
         if (i >= 0) {
            previous = (NodeImpl)this.nodes.get(i);
            this.nodes.add(i, arg);
         } else {
            i = -1 - i;
            if (null == this.nodes) {
               this.nodes = new ArrayList(5);
            }

            this.nodes.add(i, arg);
         }
      }

      return previous;
   }

   public Node removeNamedItem(String name) throws DOMException {
      if (this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         int i = this.findNamePoint(name, 0);
         if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
            throw new DOMException((short)8, msg);
         } else {
            NodeImpl n = (NodeImpl)this.nodes.get(i);
            this.nodes.remove(i);
            return n;
         }
      }
   }

   public Node removeNamedItemNS(String namespaceURI, String name) throws DOMException {
      if (this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         int i = this.findNamePoint(namespaceURI, name);
         if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
            throw new DOMException((short)8, msg);
         } else {
            NodeImpl n = (NodeImpl)this.nodes.get(i);
            this.nodes.remove(i);
            return n;
         }
      }
   }

   public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
      NamedNodeMapImpl newmap = new NamedNodeMapImpl(ownerNode);
      newmap.cloneContent(this);
      return newmap;
   }

   protected void cloneContent(NamedNodeMapImpl srcmap) {
      List srcnodes = srcmap.nodes;
      if (srcnodes != null) {
         int size = srcnodes.size();
         if (size != 0) {
            if (this.nodes == null) {
               this.nodes = new ArrayList(size);
            } else {
               this.nodes.clear();
            }

            for(int i = 0; i < size; ++i) {
               NodeImpl n = (NodeImpl)srcmap.nodes.get(i);
               NodeImpl clone = (NodeImpl)n.cloneNode(true);
               clone.isSpecified(n.isSpecified());
               this.nodes.add(clone);
            }
         }
      }

   }

   void setReadOnly(boolean readOnly, boolean deep) {
      this.isReadOnly(readOnly);
      if (deep && this.nodes != null) {
         for(int i = this.nodes.size() - 1; i >= 0; --i) {
            ((NodeImpl)this.nodes.get(i)).setReadOnly(readOnly, deep);
         }
      }

   }

   boolean getReadOnly() {
      return this.isReadOnly();
   }

   protected void setOwnerDocument(CoreDocumentImpl doc) {
      if (this.nodes != null) {
         int size = this.nodes.size();

         for(int i = 0; i < size; ++i) {
            ((NodeImpl)this.item(i)).setOwnerDocument(doc);
         }
      }

   }

   final boolean isReadOnly() {
      return (this.flags & 1) != 0;
   }

   final void isReadOnly(boolean value) {
      this.flags = (short)(value ? this.flags | 1 : this.flags & -2);
   }

   final boolean changed() {
      return (this.flags & 2) != 0;
   }

   final void changed(boolean value) {
      this.flags = (short)(value ? this.flags | 2 : this.flags & -3);
   }

   final boolean hasDefaults() {
      return (this.flags & 4) != 0;
   }

   final void hasDefaults(boolean value) {
      this.flags = (short)(value ? this.flags | 4 : this.flags & -5);
   }

   protected int findNamePoint(String name, int start) {
      int i = 0;
      if (this.nodes != null) {
         int first = start;
         int last = this.nodes.size() - 1;

         while(first <= last) {
            i = (first + last) / 2;
            int test = name.compareTo(((Node)((Node)this.nodes.get(i))).getNodeName());
            if (test == 0) {
               return i;
            }

            if (test < 0) {
               last = i - 1;
            } else {
               first = i + 1;
            }
         }

         if (first > i) {
            i = first;
         }
      }

      return -1 - i;
   }

   protected int findNamePoint(String namespaceURI, String name) {
      if (this.nodes == null) {
         return -1;
      } else if (name == null) {
         return -1;
      } else {
         int size = this.nodes.size();

         for(int i = 0; i < size; ++i) {
            NodeImpl a = (NodeImpl)this.nodes.get(i);
            String aNamespaceURI = a.getNamespaceURI();
            String aLocalName = a.getLocalName();
            if (namespaceURI == null) {
               if (aNamespaceURI == null && (name.equals(aLocalName) || aLocalName == null && name.equals(a.getNodeName()))) {
                  return i;
               }
            } else if (namespaceURI.equals(aNamespaceURI) && name.equals(aLocalName)) {
               return i;
            }
         }

         return -1;
      }
   }

   protected boolean precedes(Node a, Node b) {
      if (this.nodes != null) {
         int size = this.nodes.size();

         for(int i = 0; i < size; ++i) {
            Node n = (Node)this.nodes.get(i);
            if (n == a) {
               return true;
            }

            if (n == b) {
               return false;
            }
         }
      }

      return false;
   }

   protected void removeItem(int index) {
      if (this.nodes != null && index < this.nodes.size()) {
         this.nodes.remove(index);
      }

   }

   protected Object getItem(int index) {
      return this.nodes != null ? this.nodes.get(index) : null;
   }

   protected int addItem(Node arg) {
      int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
      if (i >= 0) {
         this.nodes.set(i, arg);
      } else {
         i = this.findNamePoint(arg.getNodeName(), 0);
         if (i >= 0) {
            this.nodes.add(i, arg);
         } else {
            i = -1 - i;
            if (null == this.nodes) {
               this.nodes = new ArrayList(5);
            }

            this.nodes.add(i, arg);
         }
      }

      return i;
   }

   protected ArrayList cloneMap(ArrayList list) {
      if (list == null) {
         list = new ArrayList(5);
      }

      list.clear();
      if (this.nodes != null) {
         int size = this.nodes.size();

         for(int i = 0; i < size; ++i) {
            list.add(this.nodes.get(i));
         }
      }

      return list;
   }

   protected int getNamedItemIndex(String namespaceURI, String localName) {
      return this.findNamePoint(namespaceURI, localName);
   }

   public void removeAll() {
      if (this.nodes != null) {
         this.nodes.clear();
      }

   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      if (this.nodes != null) {
         this.nodes = new ArrayList((Vector)this.nodes);
      }

   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      List oldNodes = this.nodes;

      try {
         if (oldNodes != null) {
            this.nodes = new Vector(oldNodes);
         }

         out.defaultWriteObject();
      } finally {
         this.nodes = oldNodes;
      }

   }
}
