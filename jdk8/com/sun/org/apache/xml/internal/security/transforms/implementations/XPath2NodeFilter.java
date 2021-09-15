package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XPath2NodeFilter implements NodeFilter {
   boolean hasUnionFilter;
   boolean hasSubtractFilter;
   boolean hasIntersectFilter;
   Set<Node> unionNodes;
   Set<Node> subtractNodes;
   Set<Node> intersectNodes;
   int inSubtract = -1;
   int inIntersect = -1;
   int inUnion = -1;

   XPath2NodeFilter(List<NodeList> var1, List<NodeList> var2, List<NodeList> var3) {
      this.hasUnionFilter = !var1.isEmpty();
      this.unionNodes = convertNodeListToSet(var1);
      this.hasSubtractFilter = !var2.isEmpty();
      this.subtractNodes = convertNodeListToSet(var2);
      this.hasIntersectFilter = !var3.isEmpty();
      this.intersectNodes = convertNodeListToSet(var3);
   }

   public int isNodeInclude(Node var1) {
      byte var2 = 1;
      if (this.hasSubtractFilter && rooted(var1, this.subtractNodes)) {
         var2 = -1;
      } else if (this.hasIntersectFilter && !rooted(var1, this.intersectNodes)) {
         var2 = 0;
      }

      if (var2 == 1) {
         return 1;
      } else {
         if (this.hasUnionFilter) {
            if (rooted(var1, this.unionNodes)) {
               return 1;
            }

            var2 = 0;
         }

         return var2;
      }
   }

   public int isNodeIncludeDO(Node var1, int var2) {
      byte var3 = 1;
      if (this.hasSubtractFilter) {
         if (this.inSubtract == -1 || var2 <= this.inSubtract) {
            if (inList(var1, this.subtractNodes)) {
               this.inSubtract = var2;
            } else {
               this.inSubtract = -1;
            }
         }

         if (this.inSubtract != -1) {
            var3 = -1;
         }
      }

      if (var3 != -1 && this.hasIntersectFilter && (this.inIntersect == -1 || var2 <= this.inIntersect)) {
         if (!inList(var1, this.intersectNodes)) {
            this.inIntersect = -1;
            var3 = 0;
         } else {
            this.inIntersect = var2;
         }
      }

      if (var2 <= this.inUnion) {
         this.inUnion = -1;
      }

      if (var3 == 1) {
         return 1;
      } else {
         if (this.hasUnionFilter) {
            if (this.inUnion == -1 && inList(var1, this.unionNodes)) {
               this.inUnion = var2;
            }

            if (this.inUnion != -1) {
               return 1;
            }

            var3 = 0;
         }

         return var3;
      }
   }

   static boolean rooted(Node var0, Set<Node> var1) {
      if (var1.isEmpty()) {
         return false;
      } else if (var1.contains(var0)) {
         return true;
      } else {
         Iterator var2 = var1.iterator();

         Node var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (Node)var2.next();
         } while(!XMLUtils.isDescendantOrSelf(var3, var0));

         return true;
      }
   }

   static boolean inList(Node var0, Set<Node> var1) {
      return var1.contains(var0);
   }

   private static Set<Node> convertNodeListToSet(List<NodeList> var0) {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         NodeList var3 = (NodeList)var2.next();
         int var4 = var3.getLength();

         for(int var5 = 0; var5 < var4; ++var5) {
            Node var6 = var3.item(var5);
            var1.add(var6);
         }
      }

      return var1;
   }
}
