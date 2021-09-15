package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class AssociationMap<XmlNode> {
   private final Map<XmlNode, AssociationMap.Entry<XmlNode>> byElement = new IdentityHashMap();
   private final Map<Object, AssociationMap.Entry<XmlNode>> byPeer = new IdentityHashMap();
   private final Set<XmlNode> usedNodes = new HashSet();

   public void addInner(XmlNode element, Object inner) {
      AssociationMap.Entry<XmlNode> e = (AssociationMap.Entry)this.byElement.get(element);
      if (e != null) {
         if (e.inner != null) {
            this.byPeer.remove(e.inner);
         }

         e.inner = inner;
      } else {
         e = new AssociationMap.Entry();
         e.element = element;
         e.inner = inner;
      }

      this.byElement.put(element, e);
      AssociationMap.Entry<XmlNode> old = (AssociationMap.Entry)this.byPeer.put(inner, e);
      if (old != null) {
         if (old.outer != null) {
            this.byPeer.remove(old.outer);
         }

         if (old.element != null) {
            this.byElement.remove(old.element);
         }
      }

   }

   public void addOuter(XmlNode element, Object outer) {
      AssociationMap.Entry<XmlNode> e = (AssociationMap.Entry)this.byElement.get(element);
      if (e != null) {
         if (e.outer != null) {
            this.byPeer.remove(e.outer);
         }

         e.outer = outer;
      } else {
         e = new AssociationMap.Entry();
         e.element = element;
         e.outer = outer;
      }

      this.byElement.put(element, e);
      AssociationMap.Entry<XmlNode> old = (AssociationMap.Entry)this.byPeer.put(outer, e);
      if (old != null) {
         old.outer = null;
         if (old.inner == null) {
            this.byElement.remove(old.element);
         }
      }

   }

   public void addUsed(XmlNode n) {
      this.usedNodes.add(n);
   }

   public AssociationMap.Entry<XmlNode> byElement(Object e) {
      return (AssociationMap.Entry)this.byElement.get(e);
   }

   public AssociationMap.Entry<XmlNode> byPeer(Object o) {
      return (AssociationMap.Entry)this.byPeer.get(o);
   }

   public Object getInnerPeer(XmlNode element) {
      AssociationMap.Entry e = this.byElement(element);
      return e == null ? null : e.inner;
   }

   public Object getOuterPeer(XmlNode element) {
      AssociationMap.Entry e = this.byElement(element);
      return e == null ? null : e.outer;
   }

   static final class Entry<XmlNode> {
      private XmlNode element;
      private Object inner;
      private Object outer;

      public XmlNode element() {
         return this.element;
      }

      public Object inner() {
         return this.inner;
      }

      public Object outer() {
         return this.outer;
      }
   }
}
