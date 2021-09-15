package javax.imageio.spi;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class PartiallyOrderedSet extends AbstractSet {
   private Map poNodes = new HashMap();
   private Set nodes;

   public PartiallyOrderedSet() {
      this.nodes = this.poNodes.keySet();
   }

   public int size() {
      return this.nodes.size();
   }

   public boolean contains(Object var1) {
      return this.nodes.contains(var1);
   }

   public Iterator iterator() {
      return new PartialOrderIterator(this.poNodes.values().iterator());
   }

   public boolean add(Object var1) {
      if (this.nodes.contains(var1)) {
         return false;
      } else {
         DigraphNode var2 = new DigraphNode(var1);
         this.poNodes.put(var1, var2);
         return true;
      }
   }

   public boolean remove(Object var1) {
      DigraphNode var2 = (DigraphNode)this.poNodes.get(var1);
      if (var2 == null) {
         return false;
      } else {
         this.poNodes.remove(var1);
         var2.dispose();
         return true;
      }
   }

   public void clear() {
      this.poNodes.clear();
   }

   public boolean setOrdering(Object var1, Object var2) {
      DigraphNode var3 = (DigraphNode)this.poNodes.get(var1);
      DigraphNode var4 = (DigraphNode)this.poNodes.get(var2);
      var4.removeEdge(var3);
      return var3.addEdge(var4);
   }

   public boolean unsetOrdering(Object var1, Object var2) {
      DigraphNode var3 = (DigraphNode)this.poNodes.get(var1);
      DigraphNode var4 = (DigraphNode)this.poNodes.get(var2);
      return var3.removeEdge(var4) || var4.removeEdge(var3);
   }

   public boolean hasOrdering(Object var1, Object var2) {
      DigraphNode var3 = (DigraphNode)this.poNodes.get(var1);
      DigraphNode var4 = (DigraphNode)this.poNodes.get(var2);
      return var3.hasEdge(var4);
   }
}
