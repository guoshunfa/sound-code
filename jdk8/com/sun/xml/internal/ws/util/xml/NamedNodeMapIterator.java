package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;

public class NamedNodeMapIterator implements Iterator {
   protected NamedNodeMap _map;
   protected int _index;

   public NamedNodeMapIterator(NamedNodeMap map) {
      this._map = map;
      this._index = 0;
   }

   public boolean hasNext() {
      if (this._map == null) {
         return false;
      } else {
         return this._index < this._map.getLength();
      }
   }

   public Object next() {
      Object obj = this._map.item(this._index);
      if (obj != null) {
         ++this._index;
      }

      return obj;
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
