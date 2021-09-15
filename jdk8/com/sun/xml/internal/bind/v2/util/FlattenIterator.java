package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class FlattenIterator<T> implements Iterator<T> {
   private final Iterator<? extends Map<?, ? extends T>> parent;
   private Iterator<? extends T> child = null;
   private T next;

   public FlattenIterator(Iterable<? extends Map<?, ? extends T>> core) {
      this.parent = core.iterator();
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public boolean hasNext() {
      this.getNext();
      return this.next != null;
   }

   public T next() {
      T r = this.next;
      this.next = null;
      if (r == null) {
         throw new NoSuchElementException();
      } else {
         return r;
      }
   }

   private void getNext() {
      if (this.next == null) {
         if (this.child != null && this.child.hasNext()) {
            this.next = this.child.next();
         } else {
            if (this.parent.hasNext()) {
               this.child = ((Map)this.parent.next()).values().iterator();
               this.getNext();
            }

         }
      }
   }
}
