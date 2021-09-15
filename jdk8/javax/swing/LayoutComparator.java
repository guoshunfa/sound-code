package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Window;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

final class LayoutComparator implements Comparator<Component>, Serializable {
   private static final int ROW_TOLERANCE = 10;
   private boolean horizontal = true;
   private boolean leftToRight = true;

   void setComponentOrientation(ComponentOrientation var1) {
      this.horizontal = var1.isHorizontal();
      this.leftToRight = var1.isLeftToRight();
   }

   public int compare(Component var1, Component var2) {
      if (var1 == var2) {
         return 0;
      } else {
         if (((Component)var1).getParent() != ((Component)var2).getParent()) {
            LinkedList var3;
            for(var3 = new LinkedList(); var1 != null; var1 = ((Component)var1).getParent()) {
               var3.add(var1);
               if (var1 instanceof Window) {
                  break;
               }
            }

            if (var1 == null) {
               throw new ClassCastException();
            }

            LinkedList var4 = new LinkedList();

            label132:
            while(true) {
               if (var2 != null) {
                  var4.add(var2);
                  if (!(var2 instanceof Window)) {
                     var2 = ((Component)var2).getParent();
                     continue;
                  }
               }

               if (var2 == null) {
                  throw new ClassCastException();
               }

               ListIterator var5 = var3.listIterator(var3.size());
               ListIterator var6 = var4.listIterator(var4.size());

               while(var5.hasPrevious()) {
                  var1 = (Component)var5.previous();
                  if (!var6.hasPrevious()) {
                     return 1;
                  }

                  var2 = (Component)var6.previous();
                  if (var1 != var2) {
                     break label132;
                  }
               }

               return -1;
            }
         }

         int var8 = ((Component)var1).getX();
         int var9 = ((Component)var1).getY();
         int var10 = ((Component)var2).getX();
         int var11 = ((Component)var2).getY();
         int var7 = ((Component)var1).getParent().getComponentZOrder((Component)var1) - ((Component)var2).getParent().getComponentZOrder((Component)var2);
         if (this.horizontal) {
            if (this.leftToRight) {
               if (Math.abs(var9 - var11) < 10) {
                  return var8 < var10 ? -1 : (var8 > var10 ? 1 : var7);
               } else {
                  return var9 < var11 ? -1 : 1;
               }
            } else if (Math.abs(var9 - var11) < 10) {
               return var8 > var10 ? -1 : (var8 < var10 ? 1 : var7);
            } else {
               return var9 < var11 ? -1 : 1;
            }
         } else if (this.leftToRight) {
            if (Math.abs(var8 - var10) < 10) {
               return var9 < var11 ? -1 : (var9 > var11 ? 1 : var7);
            } else {
               return var8 < var10 ? -1 : 1;
            }
         } else if (Math.abs(var8 - var10) < 10) {
            return var9 < var11 ? -1 : (var9 > var11 ? 1 : var7);
         } else {
            return var8 > var10 ? -1 : 1;
         }
      }
   }
}
