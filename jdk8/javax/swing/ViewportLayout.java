package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

public class ViewportLayout implements LayoutManager, Serializable {
   static ViewportLayout SHARED_INSTANCE = new ViewportLayout();

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      Component var2 = ((JViewport)var1).getView();
      if (var2 == null) {
         return new Dimension(0, 0);
      } else {
         return var2 instanceof Scrollable ? ((Scrollable)var2).getPreferredScrollableViewportSize() : var2.getPreferredSize();
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      return new Dimension(4, 4);
   }

   public void layoutContainer(Container var1) {
      JViewport var2 = (JViewport)var1;
      Component var3 = var2.getView();
      Scrollable var4 = null;
      if (var3 != null) {
         if (var3 instanceof Scrollable) {
            var4 = (Scrollable)var3;
         }

         Insets var5 = var2.getInsets();
         Dimension var6 = var3.getPreferredSize();
         Dimension var7 = var2.getSize();
         Dimension var8 = var2.toViewCoordinates(var7);
         Dimension var9 = new Dimension(var6);
         if (var4 != null) {
            if (var4.getScrollableTracksViewportWidth()) {
               var9.width = var7.width;
            }

            if (var4.getScrollableTracksViewportHeight()) {
               var9.height = var7.height;
            }
         }

         Point var10 = var2.getViewPosition();
         if (var4 != null && var2.getParent() != null && !var2.getParent().getComponentOrientation().isLeftToRight()) {
            if (var8.width > var9.width) {
               var10.x = var9.width - var8.width;
            } else {
               var10.x = Math.max(0, Math.min(var9.width - var8.width, var10.x));
            }
         } else if (var10.x + var8.width > var9.width) {
            var10.x = Math.max(0, var9.width - var8.width);
         }

         if (var10.y + var8.height > var9.height) {
            var10.y = Math.max(0, var9.height - var8.height);
         }

         if (var4 == null) {
            if (var10.x == 0 && var7.width > var6.width) {
               var9.width = var7.width;
            }

            if (var10.y == 0 && var7.height > var6.height) {
               var9.height = var7.height;
            }
         }

         var2.setViewPosition(var10);
         var2.setViewSize(var9);
      }
   }
}
