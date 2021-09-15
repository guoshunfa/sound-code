package javax.imageio.spi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class DigraphNode implements Cloneable, Serializable {
   protected Object data;
   protected Set outNodes = new HashSet();
   protected int inDegree = 0;
   private Set inNodes = new HashSet();

   public DigraphNode(Object var1) {
      this.data = var1;
   }

   public Object getData() {
      return this.data;
   }

   public Iterator getOutNodes() {
      return this.outNodes.iterator();
   }

   public boolean addEdge(DigraphNode var1) {
      if (this.outNodes.contains(var1)) {
         return false;
      } else {
         this.outNodes.add(var1);
         var1.inNodes.add(this);
         var1.incrementInDegree();
         return true;
      }
   }

   public boolean hasEdge(DigraphNode var1) {
      return this.outNodes.contains(var1);
   }

   public boolean removeEdge(DigraphNode var1) {
      if (!this.outNodes.contains(var1)) {
         return false;
      } else {
         this.outNodes.remove(var1);
         var1.inNodes.remove(this);
         var1.decrementInDegree();
         return true;
      }
   }

   public void dispose() {
      Object[] var1 = this.inNodes.toArray();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         DigraphNode var3 = (DigraphNode)var1[var2];
         var3.removeEdge(this);
      }

      Object[] var5 = this.outNodes.toArray();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         DigraphNode var4 = (DigraphNode)var5[var6];
         this.removeEdge(var4);
      }

   }

   public int getInDegree() {
      return this.inDegree;
   }

   private void incrementInDegree() {
      ++this.inDegree;
   }

   private void decrementInDegree() {
      --this.inDegree;
   }
}
