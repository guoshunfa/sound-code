package sun.security.provider.certpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AdjacencyList {
   private ArrayList<BuildStep> mStepList = new ArrayList();
   private List<List<Vertex>> mOrigList;

   public AdjacencyList(List<List<Vertex>> var1) {
      this.mOrigList = var1;
      this.buildList(var1, 0, (BuildStep)null);
   }

   public Iterator<BuildStep> iterator() {
      return Collections.unmodifiableList(this.mStepList).iterator();
   }

   private boolean buildList(List<List<Vertex>> var1, int var2, BuildStep var3) {
      List var4 = (List)var1.get(var2);
      boolean var5 = true;
      boolean var6 = true;

      Vertex var8;
      for(Iterator var7 = var4.iterator(); var7.hasNext(); this.mStepList.add(new BuildStep(var8, 1))) {
         var8 = (Vertex)var7.next();
         if (var8.getIndex() != -1) {
            if (((List)var1.get(var8.getIndex())).size() != 0) {
               var5 = false;
            }
         } else if (var8.getThrowable() == null) {
            var6 = false;
         }
      }

      Vertex var9;
      Iterator var13;
      if (var5) {
         if (var6) {
            if (var3 == null) {
               this.mStepList.add(new BuildStep((Vertex)null, 4));
            } else {
               this.mStepList.add(new BuildStep(var3.getVertex(), 2));
            }

            return false;
         } else {
            ArrayList var12 = new ArrayList();
            var13 = var4.iterator();

            while(var13.hasNext()) {
               var9 = (Vertex)var13.next();
               if (var9.getThrowable() == null) {
                  var12.add(var9);
               }
            }

            if (var12.size() == 1) {
               this.mStepList.add(new BuildStep((Vertex)var12.get(0), 5));
            } else {
               this.mStepList.add(new BuildStep((Vertex)var12.get(0), 5));
            }

            return true;
         }
      } else {
         boolean var11 = false;
         var13 = var4.iterator();

         while(var13.hasNext()) {
            var9 = (Vertex)var13.next();
            if (var9.getIndex() != -1 && ((List)var1.get(var9.getIndex())).size() != 0) {
               BuildStep var10 = new BuildStep(var9, 3);
               this.mStepList.add(var10);
               var11 = this.buildList(var1, var9.getIndex(), var10);
            }
         }

         if (var11) {
            return true;
         } else {
            if (var3 == null) {
               this.mStepList.add(new BuildStep((Vertex)null, 4));
            } else {
               this.mStepList.add(new BuildStep(var3.getVertex(), 2));
            }

            return false;
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[\n");
      int var2 = 0;
      Iterator var3 = this.mOrigList.iterator();

      while(var3.hasNext()) {
         List var4 = (List)var3.next();
         var1.append("LinkedList[").append(var2++).append("]:\n");
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Vertex var6 = (Vertex)var5.next();
            var1.append(var6.toString()).append("\n");
         }
      }

      var1.append("]\n");
      return var1.toString();
   }
}
