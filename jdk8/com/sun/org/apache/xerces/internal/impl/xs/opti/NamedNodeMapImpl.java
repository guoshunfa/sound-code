package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl implements NamedNodeMap {
   Attr[] attrs;

   public NamedNodeMapImpl(Attr[] attrs) {
      this.attrs = attrs;
   }

   public Node getNamedItem(String name) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(name)) {
            return this.attrs[i];
         }
      }

      return null;
   }

   public Node item(int index) {
      return index < 0 && index > this.getLength() ? null : this.attrs[index];
   }

   public int getLength() {
      return this.attrs.length;
   }

   public Node getNamedItemNS(String namespaceURI, String localName) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(localName) && this.attrs[i].getNamespaceURI().equals(namespaceURI)) {
            return this.attrs[i];
         }
      }

      return null;
   }

   public Node setNamedItemNS(Node arg) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Node setNamedItem(Node arg) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Node removeNamedItem(String name) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }
}
