package com.sun.xml.internal.stream.util;

import java.util.Iterator;

public class ReadOnlyIterator implements Iterator {
   Iterator iterator = null;

   public ReadOnlyIterator() {
   }

   public ReadOnlyIterator(Iterator itr) {
      this.iterator = itr;
   }

   public boolean hasNext() {
      return this.iterator != null ? this.iterator.hasNext() : false;
   }

   public Object next() {
      return this.iterator != null ? this.iterator.next() : null;
   }

   public void remove() {
      throw new UnsupportedOperationException("Remove operation is not supported");
   }
}
