package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;

public class ReadIterator implements Iterator {
   Iterator iterator = EmptyIterator.getInstance();

   public ReadIterator() {
   }

   public ReadIterator(Iterator iterator) {
      if (iterator != null) {
         this.iterator = iterator;
      }

   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public Object next() {
      return this.iterator.next();
   }

   public void remove() {
      throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList"));
   }
}
