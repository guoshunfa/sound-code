package javax.imageio.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class PartialOrderIterator implements Iterator {
   LinkedList zeroList = new LinkedList();
   Map inDegrees = new HashMap();

   public PartialOrderIterator(Iterator var1) {
      while(var1.hasNext()) {
         DigraphNode var2 = (DigraphNode)var1.next();
         int var3 = var2.getInDegree();
         this.inDegrees.put(var2, new Integer(var3));
         if (var3 == 0) {
            this.zeroList.add(var2);
         }
      }

   }

   public boolean hasNext() {
      return !this.zeroList.isEmpty();
   }

   public Object next() {
      DigraphNode var1 = (DigraphNode)this.zeroList.removeFirst();
      Iterator var2 = var1.getOutNodes();

      while(var2.hasNext()) {
         DigraphNode var3 = (DigraphNode)var2.next();
         int var4 = (Integer)this.inDegrees.get(var3) - 1;
         this.inDegrees.put(var3, new Integer(var4));
         if (var4 == 0) {
            this.zeroList.add(var3);
         }
      }

      return var1.getData();
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
