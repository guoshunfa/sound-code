package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.FreezableList;
import java.util.ArrayList;
import java.util.Iterator;

public class IdentifiableContainerBase extends FreezableList {
   public IdentifiableContainerBase() {
      super(new ArrayList());
   }

   public Iterator iteratorById(final int var1) {
      return new Iterator() {
         Iterator iter = IdentifiableContainerBase.this.iterator();
         Object current = this.advance();

         private Object advance() {
            while(true) {
               if (this.iter.hasNext()) {
                  Identifiable var1x = (Identifiable)((Identifiable)this.iter.next());
                  if (var1x.getId() != var1) {
                     continue;
                  }

                  return var1x;
               }

               return null;
            }
         }

         public boolean hasNext() {
            return this.current != null;
         }

         public Object next() {
            Object var1x = this.current;
            this.current = this.advance();
            return var1x;
         }

         public void remove() {
            this.iter.remove();
         }
      };
   }
}
