package com.sun.imageio.plugins.jpeg;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.ImageTypeSpecifier;

class ImageTypeIterator implements Iterator<ImageTypeSpecifier> {
   private Iterator<ImageTypeProducer> producers;
   private ImageTypeSpecifier theNext = null;

   public ImageTypeIterator(Iterator<ImageTypeProducer> var1) {
      this.producers = var1;
   }

   public boolean hasNext() {
      if (this.theNext != null) {
         return true;
      } else if (!this.producers.hasNext()) {
         return false;
      } else {
         do {
            this.theNext = ((ImageTypeProducer)this.producers.next()).getType();
         } while(this.theNext == null && this.producers.hasNext());

         return this.theNext != null;
      }
   }

   public ImageTypeSpecifier next() {
      if (this.theNext == null && !this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ImageTypeSpecifier var1 = this.theNext;
         this.theNext = null;
         return var1;
      }
   }

   public void remove() {
      this.producers.remove();
   }
}
