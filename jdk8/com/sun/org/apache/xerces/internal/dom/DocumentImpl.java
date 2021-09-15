package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import com.sun.org.apache.xerces.internal.dom.events.MutationEventImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

public class DocumentImpl extends CoreDocumentImpl implements DocumentTraversal, DocumentEvent, DocumentRange {
   static final long serialVersionUID = 515687835542616694L;
   protected List<NodeIterator> iterators;
   protected List<Range> ranges;
   protected Map<NodeImpl, List<DocumentImpl.LEntry>> eventListeners;
   protected boolean mutationEvents = false;
   private static final ObjectStreamField[] serialPersistentFields;
   DocumentImpl.EnclosingAttr savedEnclosingAttr;

   public DocumentImpl() {
   }

   public DocumentImpl(boolean grammarAccess) {
      super(grammarAccess);
   }

   public DocumentImpl(DocumentType doctype) {
      super(doctype);
   }

   public DocumentImpl(DocumentType doctype, boolean grammarAccess) {
      super(doctype, grammarAccess);
   }

   public Node cloneNode(boolean deep) {
      DocumentImpl newdoc = new DocumentImpl();
      this.callUserDataHandlers(this, newdoc, (short)1);
      this.cloneNode(newdoc, deep);
      newdoc.mutationEvents = this.mutationEvents;
      return newdoc;
   }

   public DOMImplementation getImplementation() {
      return DOMImplementationImpl.getDOMImplementation();
   }

   public NodeIterator createNodeIterator(Node root, short whatToShow, NodeFilter filter) {
      return this.createNodeIterator(root, whatToShow, filter, true);
   }

   public NodeIterator createNodeIterator(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
      if (root == null) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      } else {
         NodeIterator iterator = new NodeIteratorImpl(this, root, whatToShow, filter, entityReferenceExpansion);
         if (this.iterators == null) {
            this.iterators = new ArrayList();
         }

         this.iterators.add(iterator);
         return iterator;
      }
   }

   public TreeWalker createTreeWalker(Node root, short whatToShow, NodeFilter filter) {
      return this.createTreeWalker(root, whatToShow, filter, true);
   }

   public TreeWalker createTreeWalker(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
      if (root == null) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      } else {
         return new TreeWalkerImpl(root, whatToShow, filter, entityReferenceExpansion);
      }
   }

   void removeNodeIterator(NodeIterator nodeIterator) {
      if (nodeIterator != null) {
         if (this.iterators != null) {
            this.iterators.remove(nodeIterator);
         }
      }
   }

   public Range createRange() {
      if (this.ranges == null) {
         this.ranges = new ArrayList();
      }

      Range range = new RangeImpl(this);
      this.ranges.add(range);
      return range;
   }

   void removeRange(Range range) {
      if (range != null) {
         if (this.ranges != null) {
            this.ranges.remove(range);
         }
      }
   }

   void replacedText(NodeImpl node) {
      if (this.ranges != null) {
         int size = this.ranges.size();

         for(int i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).receiveReplacedText(node);
         }
      }

   }

   void deletedText(NodeImpl node, int offset, int count) {
      if (this.ranges != null) {
         int size = this.ranges.size();

         for(int i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).receiveDeletedText(node, offset, count);
         }
      }

   }

   void insertedText(NodeImpl node, int offset, int count) {
      if (this.ranges != null) {
         int size = this.ranges.size();

         for(int i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).receiveInsertedText(node, offset, count);
         }
      }

   }

   void splitData(Node node, Node newNode, int offset) {
      if (this.ranges != null) {
         int size = this.ranges.size();

         for(int i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).receiveSplitData(node, newNode, offset);
         }
      }

   }

   public Event createEvent(String type) throws DOMException {
      if (!type.equalsIgnoreCase("Events") && !"Event".equals(type)) {
         if (!type.equalsIgnoreCase("MutationEvents") && !"MutationEvent".equals(type)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
            throw new DOMException((short)9, msg);
         } else {
            return new MutationEventImpl();
         }
      } else {
         return new EventImpl();
      }
   }

   void setMutationEvents(boolean set) {
      this.mutationEvents = set;
   }

   boolean getMutationEvents() {
      return this.mutationEvents;
   }

   private void setEventListeners(NodeImpl n, List<DocumentImpl.LEntry> listeners) {
      if (this.eventListeners == null) {
         this.eventListeners = new HashMap();
      }

      if (listeners == null) {
         this.eventListeners.remove(n);
         if (this.eventListeners.isEmpty()) {
            this.mutationEvents = false;
         }
      } else {
         this.eventListeners.put(n, listeners);
         this.mutationEvents = true;
      }

   }

   private List<DocumentImpl.LEntry> getEventListeners(NodeImpl n) {
      return this.eventListeners == null ? null : (List)this.eventListeners.get(n);
   }

   protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         this.removeEventListener(node, type, listener, useCapture);
         List<DocumentImpl.LEntry> nodeListeners = this.getEventListeners(node);
         if (nodeListeners == null) {
            nodeListeners = new ArrayList();
            this.setEventListeners(node, (List)nodeListeners);
         }

         ((List)nodeListeners).add(new DocumentImpl.LEntry(type, listener, useCapture));
         LCount lc = LCount.lookup(type);
         if (useCapture) {
            ++lc.captures;
            ++lc.total;
         } else {
            ++lc.bubbles;
            ++lc.total;
         }

      }
   }

   protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         List<DocumentImpl.LEntry> nodeListeners = this.getEventListeners(node);
         if (nodeListeners != null) {
            for(int i = nodeListeners.size() - 1; i >= 0; --i) {
               DocumentImpl.LEntry le = (DocumentImpl.LEntry)nodeListeners.get(i);
               if (le.useCapture == useCapture && le.listener == listener && le.type.equals(type)) {
                  nodeListeners.remove(i);
                  if (nodeListeners.isEmpty()) {
                     this.setEventListeners(node, (List)null);
                  }

                  LCount lc = LCount.lookup(type);
                  if (useCapture) {
                     --lc.captures;
                     --lc.total;
                  } else {
                     --lc.bubbles;
                     --lc.total;
                  }
                  break;
               }
            }

         }
      }
   }

   protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {
      List<DocumentImpl.LEntry> nodeListeners = this.getEventListeners(src);
      if (nodeListeners != null) {
         this.setEventListeners(tgt, new ArrayList(nodeListeners));
      }
   }

   protected boolean dispatchEvent(NodeImpl node, Event event) {
      if (event == null) {
         return false;
      } else {
         EventImpl evt = (EventImpl)event;
         if (evt.initialized && evt.type != null && !evt.type.equals("")) {
            LCount lc = LCount.lookup(evt.getType());
            if (lc.total == 0) {
               return evt.preventDefault;
            } else {
               evt.target = node;
               evt.stopPropagation = false;
               evt.preventDefault = false;
               List<Node> pv = new ArrayList(10);

               for(Node n = node.getParentNode(); n != null; n = n.getParentNode()) {
                  pv.add(n);
               }

               int nlsize;
               if (lc.captures > 0) {
                  evt.eventPhase = 1;

                  for(int j = pv.size() - 1; j >= 0 && !evt.stopPropagation; --j) {
                     NodeImpl nn = (NodeImpl)pv.get(j);
                     evt.currentTarget = nn;
                     List<DocumentImpl.LEntry> nodeListeners = this.getEventListeners(nn);
                     if (nodeListeners != null) {
                        List<DocumentImpl.LEntry> nl = (List)((ArrayList)nodeListeners).clone();
                        int nlsize = nl.size();

                        for(nlsize = 0; nlsize < nlsize; ++nlsize) {
                           DocumentImpl.LEntry le = (DocumentImpl.LEntry)nl.get(nlsize);
                           if (le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
                              try {
                                 le.listener.handleEvent(evt);
                              } catch (Exception var19) {
                              }
                           }
                        }
                     }
                  }
               }

               if (lc.bubbles > 0) {
                  evt.eventPhase = 2;
                  evt.currentTarget = node;
                  List<DocumentImpl.LEntry> nodeListeners = this.getEventListeners(node);
                  int j;
                  if (!evt.stopPropagation && nodeListeners != null) {
                     List<DocumentImpl.LEntry> nl = (List)((ArrayList)nodeListeners).clone();
                     j = nl.size();

                     for(int i = 0; i < j; ++i) {
                        DocumentImpl.LEntry le = (DocumentImpl.LEntry)nl.get(i);
                        if (!le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
                           try {
                              le.listener.handleEvent(evt);
                           } catch (Exception var18) {
                           }
                        }
                     }
                  }

                  if (evt.bubbles) {
                     evt.eventPhase = 3;
                     int pvsize = pv.size();

                     for(j = 0; j < pvsize && !evt.stopPropagation; ++j) {
                        NodeImpl nn = (NodeImpl)pv.get(j);
                        evt.currentTarget = nn;
                        nodeListeners = this.getEventListeners(nn);
                        if (nodeListeners != null) {
                           List<DocumentImpl.LEntry> nl = (List)((ArrayList)nodeListeners).clone();
                           nlsize = nl.size();

                           for(int i = 0; i < nlsize; ++i) {
                              DocumentImpl.LEntry le = (DocumentImpl.LEntry)nl.get(i);
                              if (!le.useCapture && le.type.equals(evt.type) && nodeListeners.contains(le)) {
                                 try {
                                    le.listener.handleEvent(evt);
                                 } catch (Exception var17) {
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (lc.defaults > 0 && evt.cancelable && !evt.preventDefault) {
               }

               return evt.preventDefault;
            }
         } else {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", (Object[])null);
            throw new EventException((short)0, msg);
         }
      }
   }

   protected void dispatchEventToSubtree(Node n, Event e) {
      ((NodeImpl)n).dispatchEvent(e);
      if (n.getNodeType() == 1) {
         NamedNodeMap a = n.getAttributes();

         for(int i = a.getLength() - 1; i >= 0; --i) {
            this.dispatchingEventToSubtree(a.item(i), e);
         }
      }

      this.dispatchingEventToSubtree(n.getFirstChild(), e);
   }

   protected void dispatchingEventToSubtree(Node n, Event e) {
      if (n != null) {
         ((NodeImpl)n).dispatchEvent(e);
         if (n.getNodeType() == 1) {
            NamedNodeMap a = n.getAttributes();

            for(int i = a.getLength() - 1; i >= 0; --i) {
               this.dispatchingEventToSubtree(a.item(i), e);
            }
         }

         this.dispatchingEventToSubtree(n.getFirstChild(), e);
         this.dispatchingEventToSubtree(n.getNextSibling(), e);
      }
   }

   protected void dispatchAggregateEvents(NodeImpl node, DocumentImpl.EnclosingAttr ea) {
      if (ea != null) {
         this.dispatchAggregateEvents(node, ea.node, ea.oldvalue, (short)1);
      } else {
         this.dispatchAggregateEvents(node, (AttrImpl)null, (String)null, (short)0);
      }

   }

   protected void dispatchAggregateEvents(NodeImpl node, AttrImpl enclosingAttr, String oldvalue, short change) {
      NodeImpl owner = null;
      LCount lc;
      MutationEventImpl me;
      if (enclosingAttr != null) {
         lc = LCount.lookup("DOMAttrModified");
         owner = (NodeImpl)enclosingAttr.getOwnerElement();
         if (lc.total > 0 && owner != null) {
            me = new MutationEventImpl();
            me.initMutationEvent("DOMAttrModified", true, false, enclosingAttr, oldvalue, enclosingAttr.getNodeValue(), enclosingAttr.getNodeName(), change);
            owner.dispatchEvent(me);
         }
      }

      lc = LCount.lookup("DOMSubtreeModified");
      if (lc.total > 0) {
         me = new MutationEventImpl();
         me.initMutationEvent("DOMSubtreeModified", true, false, (Node)null, (String)null, (String)null, (String)null, (short)0);
         if (enclosingAttr != null) {
            this.dispatchEvent(enclosingAttr, me);
            if (owner != null) {
               this.dispatchEvent(owner, me);
            }
         } else {
            this.dispatchEvent(node, me);
         }
      }

   }

   protected void saveEnclosingAttr(NodeImpl node) {
      this.savedEnclosingAttr = null;
      LCount lc = LCount.lookup("DOMAttrModified");
      if (lc.total > 0) {
         NodeImpl eventAncestor = node;

         while(eventAncestor != null) {
            int type = eventAncestor.getNodeType();
            if (type == 2) {
               DocumentImpl.EnclosingAttr retval = new DocumentImpl.EnclosingAttr();
               retval.node = (AttrImpl)eventAncestor;
               retval.oldvalue = retval.node.getNodeValue();
               this.savedEnclosingAttr = retval;
               return;
            }

            if (type == 5) {
               eventAncestor = eventAncestor.parentNode();
            } else {
               if (type != 3) {
                  return;
               }

               eventAncestor = eventAncestor.parentNode();
            }
         }

      }
   }

   void modifyingCharacterData(NodeImpl node, boolean replace) {
      if (this.mutationEvents && !replace) {
         this.saveEnclosingAttr(node);
      }

   }

   void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
      if (this.mutationEvents && !replace) {
         LCount lc = LCount.lookup("DOMCharacterDataModified");
         if (lc.total > 0) {
            MutationEvent me = new MutationEventImpl();
            me.initMutationEvent("DOMCharacterDataModified", true, false, (Node)null, oldvalue, value, (String)null, (short)0);
            this.dispatchEvent(node, me);
         }

         this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
      }

   }

   void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
      this.modifiedCharacterData(node, oldvalue, value, false);
   }

   void insertingNode(NodeImpl node, boolean replace) {
      if (this.mutationEvents && !replace) {
         this.saveEnclosingAttr(node);
      }

   }

   void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
      if (this.mutationEvents) {
         LCount lc = LCount.lookup("DOMNodeInserted");
         if (lc.total > 0) {
            MutationEventImpl me = new MutationEventImpl();
            me.initMutationEvent("DOMNodeInserted", true, false, node, (String)null, (String)null, (String)null, (short)0);
            this.dispatchEvent(newInternal, me);
         }

         lc = LCount.lookup("DOMNodeInsertedIntoDocument");
         if (lc.total > 0) {
            NodeImpl eventAncestor = node;
            if (this.savedEnclosingAttr != null) {
               eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
            }

            if (eventAncestor != null) {
               NodeImpl p = eventAncestor;

               while(p != null) {
                  eventAncestor = p;
                  if (p.getNodeType() == 2) {
                     p = (NodeImpl)((AttrImpl)p).getOwnerElement();
                  } else {
                     p = p.parentNode();
                  }
               }

               if (eventAncestor.getNodeType() == 9) {
                  MutationEventImpl me = new MutationEventImpl();
                  me.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, (Node)null, (String)null, (String)null, (String)null, (short)0);
                  this.dispatchEventToSubtree(newInternal, me);
               }
            }
         }

         if (!replace) {
            this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
         }
      }

      if (this.ranges != null) {
         int size = this.ranges.size();

         for(int i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).insertedNodeFromDOM(newInternal);
         }
      }

   }

   void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
      int size;
      int i;
      if (this.iterators != null) {
         size = this.iterators.size();

         for(i = 0; i != size; ++i) {
            ((NodeIteratorImpl)this.iterators.get(i)).removeNode(oldChild);
         }
      }

      if (this.ranges != null) {
         size = this.ranges.size();

         for(i = 0; i != size; ++i) {
            ((RangeImpl)this.ranges.get(i)).removeNode(oldChild);
         }
      }

      if (this.mutationEvents) {
         if (!replace) {
            this.saveEnclosingAttr(node);
         }

         LCount lc = LCount.lookup("DOMNodeRemoved");
         if (lc.total > 0) {
            MutationEventImpl me = new MutationEventImpl();
            me.initMutationEvent("DOMNodeRemoved", true, false, node, (String)null, (String)null, (String)null, (short)0);
            this.dispatchEvent(oldChild, me);
         }

         lc = LCount.lookup("DOMNodeRemovedFromDocument");
         if (lc.total > 0) {
            NodeImpl eventAncestor = this;
            if (this.savedEnclosingAttr != null) {
               eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement();
            }

            if (eventAncestor != null) {
               for(NodeImpl p = ((NodeImpl)eventAncestor).parentNode(); p != null; p = p.parentNode()) {
                  eventAncestor = p;
               }

               if (((NodeImpl)eventAncestor).getNodeType() == 9) {
                  MutationEventImpl me = new MutationEventImpl();
                  me.initMutationEvent("DOMNodeRemovedFromDocument", false, false, (Node)null, (String)null, (String)null, (String)null, (short)0);
                  this.dispatchEventToSubtree(oldChild, me);
               }
            }
         }
      }

   }

   void removedNode(NodeImpl node, boolean replace) {
      if (this.mutationEvents && !replace) {
         this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
      }

   }

   void replacingNode(NodeImpl node) {
      if (this.mutationEvents) {
         this.saveEnclosingAttr(node);
      }

   }

   void replacingData(NodeImpl node) {
      if (this.mutationEvents) {
         this.saveEnclosingAttr(node);
      }

   }

   void replacedNode(NodeImpl node) {
      if (this.mutationEvents) {
         this.dispatchAggregateEvents(node, this.savedEnclosingAttr);
      }

   }

   void modifiedAttrValue(AttrImpl attr, String oldvalue) {
      if (this.mutationEvents) {
         this.dispatchAggregateEvents(attr, attr, oldvalue, (short)1);
      }

   }

   void setAttrNode(AttrImpl attr, AttrImpl previous) {
      if (this.mutationEvents) {
         if (previous == null) {
            this.dispatchAggregateEvents(attr.ownerNode, attr, (String)null, (short)2);
         } else {
            this.dispatchAggregateEvents(attr.ownerNode, attr, previous.getNodeValue(), (short)1);
         }
      }

   }

   void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
      if (this.mutationEvents) {
         LCount lc = LCount.lookup("DOMAttrModified");
         if (lc.total > 0) {
            MutationEventImpl me = new MutationEventImpl();
            me.initMutationEvent("DOMAttrModified", true, false, attr, attr.getNodeValue(), (String)null, name, (short)3);
            this.dispatchEvent(oldOwner, me);
         }

         this.dispatchAggregateEvents(oldOwner, (AttrImpl)null, (String)null, (short)0);
      }

   }

   void renamedAttrNode(Attr oldAt, Attr newAt) {
   }

   void renamedElement(Element oldEl, Element newEl) {
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      Vector<NodeIterator> it = this.iterators == null ? null : new Vector(this.iterators);
      Vector<Range> r = this.ranges == null ? null : new Vector(this.ranges);
      Hashtable<NodeImpl, Vector<DocumentImpl.LEntry>> el = null;
      if (this.eventListeners != null) {
         el = new Hashtable();
         Iterator var5 = this.eventListeners.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<NodeImpl, List<DocumentImpl.LEntry>> e = (Map.Entry)var5.next();
            el.put(e.getKey(), new Vector((Collection)e.getValue()));
         }
      }

      ObjectOutputStream.PutField pf = out.putFields();
      pf.put("iterators", it);
      pf.put("ranges", r);
      pf.put("eventListeners", el);
      pf.put("mutationEvents", this.mutationEvents);
      out.writeFields();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField gf = in.readFields();
      Vector<NodeIterator> it = (Vector)gf.get("iterators", (Object)null);
      Vector<Range> r = (Vector)gf.get("ranges", (Object)null);
      Hashtable<NodeImpl, Vector<DocumentImpl.LEntry>> el = (Hashtable)gf.get("eventListeners", (Object)null);
      this.mutationEvents = gf.get("mutationEvents", false);
      if (it != null) {
         this.iterators = new ArrayList(it);
      }

      if (r != null) {
         this.ranges = new ArrayList(r);
      }

      if (el != null) {
         this.eventListeners = new HashMap();
         Iterator var6 = el.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<NodeImpl, Vector<DocumentImpl.LEntry>> e = (Map.Entry)var6.next();
            this.eventListeners.put(e.getKey(), new ArrayList((Collection)e.getValue()));
         }
      }

   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("iterators", Vector.class), new ObjectStreamField("ranges", Vector.class), new ObjectStreamField("eventListeners", Hashtable.class), new ObjectStreamField("mutationEvents", Boolean.TYPE)};
   }

   class EnclosingAttr implements Serializable {
      private static final long serialVersionUID = 5208387723391647216L;
      AttrImpl node;
      String oldvalue;
   }

   class LEntry implements Serializable {
      private static final long serialVersionUID = -8426757059492421631L;
      String type;
      EventListener listener;
      boolean useCapture;

      LEntry(String type, EventListener listener, boolean useCapture) {
         this.type = type;
         this.listener = listener;
         this.useCapture = useCapture;
      }
   }
}
