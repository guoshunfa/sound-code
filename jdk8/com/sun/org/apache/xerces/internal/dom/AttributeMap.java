package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap extends NamedNodeMapImpl {
   static final long serialVersionUID = 8872606282138665383L;

   protected AttributeMap(ElementImpl ownerNode, NamedNodeMapImpl defaults) {
      super(ownerNode);
      if (defaults != null) {
         this.cloneContent(defaults);
         if (this.nodes != null) {
            this.hasDefaults(true);
         }
      }

   }

   public Node setNamedItem(Node arg) throws DOMException {
      boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
      if (errCheck) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }

         if (arg.getNodeType() != 2) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
            throw new DOMException((short)3, msg);
         }
      }

      AttrImpl argn = (AttrImpl)arg;
      if (argn.isOwned()) {
         if (errCheck && argn.getOwnerElement() != this.ownerNode) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", (Object[])null);
            throw new DOMException((short)10, msg);
         } else {
            return arg;
         }
      } else {
         argn.ownerNode = this.ownerNode;
         argn.isOwned(true);
         int i = this.findNamePoint(argn.getNodeName(), 0);
         AttrImpl previous = null;
         if (i >= 0) {
            previous = (AttrImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
            previous.ownerNode = this.ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
         } else {
            i = -1 - i;
            if (null == this.nodes) {
               this.nodes = new ArrayList(5);
            }

            this.nodes.add(i, arg);
         }

         this.ownerNode.ownerDocument().setAttrNode(argn, previous);
         if (!argn.isNormalized()) {
            this.ownerNode.isNormalized(false);
         }

         return previous;
      }
   }

   public Node setNamedItemNS(Node arg) throws DOMException {
      boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
      if (errCheck) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
            throw new DOMException((short)4, msg);
         }

         if (arg.getNodeType() != 2) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", (Object[])null);
            throw new DOMException((short)3, msg);
         }
      }

      AttrImpl argn = (AttrImpl)arg;
      if (argn.isOwned()) {
         if (errCheck && argn.getOwnerElement() != this.ownerNode) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", (Object[])null);
            throw new DOMException((short)10, msg);
         } else {
            return arg;
         }
      } else {
         argn.ownerNode = this.ownerNode;
         argn.isOwned(true);
         int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
         AttrImpl previous = null;
         if (i >= 0) {
            previous = (AttrImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
            previous.ownerNode = this.ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
         } else {
            i = this.findNamePoint(arg.getNodeName(), 0);
            if (i >= 0) {
               previous = (AttrImpl)this.nodes.get(i);
               this.nodes.add(i, arg);
            } else {
               i = -1 - i;
               if (null == this.nodes) {
                  this.nodes = new ArrayList(5);
               }

               this.nodes.add(i, arg);
            }
         }

         this.ownerNode.ownerDocument().setAttrNode(argn, previous);
         if (!argn.isNormalized()) {
            this.ownerNode.isNormalized(false);
         }

         return previous;
      }
   }

   public Node removeNamedItem(String name) throws DOMException {
      return this.internalRemoveNamedItem(name, true);
   }

   Node safeRemoveNamedItem(String name) {
      return this.internalRemoveNamedItem(name, false);
   }

   protected Node removeItem(Node item, boolean addDefault) throws DOMException {
      int index = -1;
      if (this.nodes != null) {
         int size = this.nodes.size();

         for(int i = 0; i < size; ++i) {
            if (this.nodes.get(i) == item) {
               index = i;
               break;
            }
         }
      }

      if (index < 0) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
         throw new DOMException((short)8, msg);
      } else {
         return this.remove((AttrImpl)item, index, addDefault);
      }
   }

   protected final Node internalRemoveNamedItem(String name, boolean raiseEx) {
      if (this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         int i = this.findNamePoint(name, 0);
         if (i < 0) {
            if (raiseEx) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
               throw new DOMException((short)8, msg);
            } else {
               return null;
            }
         } else {
            return this.remove((AttrImpl)this.nodes.get(i), i, true);
         }
      }
   }

   private final Node remove(AttrImpl attr, int index, boolean addDefault) {
      CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
      String name = attr.getNodeName();
      if (attr.isIdAttribute()) {
         ownerDocument.removeIdentifier(attr.getValue());
      }

      if (this.hasDefaults() && addDefault) {
         NamedNodeMapImpl defaults = ((ElementImpl)this.ownerNode).getDefaultAttributes();
         Node d;
         if (defaults != null && (d = defaults.getNamedItem(name)) != null && this.findNamePoint(name, index + 1) < 0) {
            NodeImpl clone = (NodeImpl)d.cloneNode(true);
            if (d.getLocalName() != null) {
               ((AttrNSImpl)clone).namespaceURI = attr.getNamespaceURI();
            }

            clone.ownerNode = this.ownerNode;
            clone.isOwned(true);
            clone.isSpecified(false);
            this.nodes.set(index, clone);
            if (attr.isIdAttribute()) {
               ownerDocument.putIdentifier(clone.getNodeValue(), (ElementImpl)this.ownerNode);
            }
         } else {
            this.nodes.remove(index);
         }
      } else {
         this.nodes.remove(index);
      }

      attr.ownerNode = ownerDocument;
      attr.isOwned(false);
      attr.isSpecified(true);
      attr.isIdAttribute(false);
      ownerDocument.removedAttrNode(attr, this.ownerNode, name);
      return attr;
   }

   public Node removeNamedItemNS(String namespaceURI, String name) throws DOMException {
      return this.internalRemoveNamedItemNS(namespaceURI, name, true);
   }

   Node safeRemoveNamedItemNS(String namespaceURI, String name) {
      return this.internalRemoveNamedItemNS(namespaceURI, name, false);
   }

   protected final Node internalRemoveNamedItemNS(String namespaceURI, String name, boolean raiseEx) {
      CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
      if (ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else {
         int i = this.findNamePoint(namespaceURI, name);
         if (i < 0) {
            if (raiseEx) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", (Object[])null);
               throw new DOMException((short)8, msg);
            } else {
               return null;
            }
         } else {
            AttrImpl n = (AttrImpl)this.nodes.get(i);
            if (n.isIdAttribute()) {
               ownerDocument.removeIdentifier(n.getValue());
            }

            String nodeName = n.getNodeName();
            if (this.hasDefaults()) {
               NamedNodeMapImpl defaults = ((ElementImpl)this.ownerNode).getDefaultAttributes();
               Node d;
               if (defaults != null && (d = defaults.getNamedItem(nodeName)) != null) {
                  int j = this.findNamePoint(nodeName, 0);
                  if (j >= 0 && this.findNamePoint(nodeName, j + 1) < 0) {
                     NodeImpl clone = (NodeImpl)d.cloneNode(true);
                     clone.ownerNode = this.ownerNode;
                     if (d.getLocalName() != null) {
                        ((AttrNSImpl)clone).namespaceURI = namespaceURI;
                     }

                     clone.isOwned(true);
                     clone.isSpecified(false);
                     this.nodes.set(i, clone);
                     if (clone.isIdAttribute()) {
                        ownerDocument.putIdentifier(clone.getNodeValue(), (ElementImpl)this.ownerNode);
                     }
                  } else {
                     this.nodes.remove(i);
                  }
               } else {
                  this.nodes.remove(i);
               }
            } else {
               this.nodes.remove(i);
            }

            n.ownerNode = ownerDocument;
            n.isOwned(false);
            n.isSpecified(true);
            n.isIdAttribute(false);
            ownerDocument.removedAttrNode(n, this.ownerNode, name);
            return n;
         }
      }
   }

   public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
      AttributeMap newmap = new AttributeMap((ElementImpl)ownerNode, (NamedNodeMapImpl)null);
      newmap.hasDefaults(this.hasDefaults());
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
               NodeImpl n = (NodeImpl)srcnodes.get(i);
               NodeImpl clone = (NodeImpl)n.cloneNode(true);
               clone.isSpecified(n.isSpecified());
               this.nodes.add(clone);
               clone.ownerNode = this.ownerNode;
               clone.isOwned(true);
            }
         }
      }

   }

   void moveSpecifiedAttributes(AttributeMap srcmap) {
      int nsize = srcmap.nodes != null ? srcmap.nodes.size() : 0;

      for(int i = nsize - 1; i >= 0; --i) {
         AttrImpl attr = (AttrImpl)srcmap.nodes.get(i);
         if (attr.isSpecified()) {
            srcmap.remove(attr, i, false);
            if (attr.getLocalName() != null) {
               this.setNamedItem(attr);
            } else {
               this.setNamedItemNS(attr);
            }
         }
      }

   }

   protected void reconcileDefaults(NamedNodeMapImpl defaults) {
      int nsize = this.nodes != null ? this.nodes.size() : 0;

      int dsize;
      for(dsize = nsize - 1; dsize >= 0; --dsize) {
         AttrImpl attr = (AttrImpl)this.nodes.get(dsize);
         if (!attr.isSpecified()) {
            this.remove(attr, dsize, false);
         }
      }

      if (defaults != null) {
         if (this.nodes != null && this.nodes.size() != 0) {
            dsize = defaults.nodes.size();

            for(int n = 0; n < dsize; ++n) {
               AttrImpl d = (AttrImpl)defaults.nodes.get(n);
               int i = this.findNamePoint(d.getNodeName(), 0);
               if (i < 0) {
                  i = -1 - i;
                  NodeImpl clone = (NodeImpl)d.cloneNode(true);
                  clone.ownerNode = this.ownerNode;
                  clone.isOwned(true);
                  clone.isSpecified(false);
                  this.nodes.add(i, clone);
               }
            }
         } else {
            this.cloneContent(defaults);
         }

      }
   }

   protected final int addItem(Node arg) {
      AttrImpl argn = (AttrImpl)arg;
      argn.ownerNode = this.ownerNode;
      argn.isOwned(true);
      int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
      if (i >= 0) {
         this.nodes.set(i, arg);
      } else {
         i = this.findNamePoint(argn.getNodeName(), 0);
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

      this.ownerNode.ownerDocument().setAttrNode(argn, (AttrImpl)null);
      return i;
   }
}
