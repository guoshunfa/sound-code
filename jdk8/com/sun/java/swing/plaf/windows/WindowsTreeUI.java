package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class WindowsTreeUI extends BasicTreeUI {
   protected static final int HALF_SIZE = 4;
   protected static final int SIZE = 9;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTreeUI();
   }

   protected void ensureRowsAreVisible(int var1, int var2) {
      if (this.tree != null && var1 >= 0 && var2 < this.getRowCount(this.tree)) {
         Rectangle var3 = this.tree.getVisibleRect();
         Rectangle var4;
         if (var1 == var2) {
            var4 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var1));
            if (var4 != null) {
               var4.x = var3.x;
               var4.width = var3.width;
               this.tree.scrollRectToVisible(var4);
            }
         } else {
            var4 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var1));
            if (var4 != null) {
               Rectangle var5 = var4;
               int var6 = var4.y;
               int var7 = var6 + var3.height;

               for(int var8 = var1 + 1; var8 <= var2; ++var8) {
                  var5 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var8));
                  if (var5 != null && var5.y + var5.height > var7) {
                     var8 = var2;
                  }
               }

               if (var5 == null) {
                  return;
               }

               this.tree.scrollRectToVisible(new Rectangle(var3.x, var6, 1, var5.y + var5.height - var6));
            }
         }
      }

   }

   protected TreeCellRenderer createDefaultCellRenderer() {
      return new WindowsTreeUI.WindowsTreeCellRenderer();
   }

   public class WindowsTreeCellRenderer extends DefaultTreeCellRenderer {
      public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
         super.getTreeCellRendererComponent(var1, var2, var3, var4, var5, var6, var7);
         if (!var1.isEnabled()) {
            this.setEnabled(false);
            if (var5) {
               this.setDisabledIcon(this.getLeafIcon());
            } else if (var3) {
               this.setDisabledIcon(this.getOpenIcon());
            } else {
               this.setDisabledIcon(this.getClosedIcon());
            }
         } else {
            this.setEnabled(true);
            if (var5) {
               this.setIcon(this.getLeafIcon());
            } else if (var3) {
               this.setIcon(this.getOpenIcon());
            } else {
               this.setIcon(this.getClosedIcon());
            }
         }

         return this;
      }
   }

   public static class CollapsedIcon extends WindowsTreeUI.ExpandedIcon {
      public static Icon createCollapsedIcon() {
         return new WindowsTreeUI.CollapsedIcon();
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         XPStyle.Skin var5 = this.getSkin(var1);
         if (var5 != null) {
            var5.paintSkin(var2, var3, var4, TMSchema.State.CLOSED);
         } else {
            super.paintIcon(var1, var2, var3, var4);
            var2.drawLine(var3 + 4, var4 + 2, var3 + 4, var4 + 6);
         }

      }
   }

   public static class ExpandedIcon implements Icon, Serializable {
      public static Icon createExpandedIcon() {
         return new WindowsTreeUI.ExpandedIcon();
      }

      XPStyle.Skin getSkin(Component var1) {
         XPStyle var2 = XPStyle.getXP();
         return var2 != null ? var2.getSkin(var1, TMSchema.Part.TVP_GLYPH) : null;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         XPStyle.Skin var5 = this.getSkin(var1);
         if (var5 != null) {
            var5.paintSkin(var2, var3, var4, TMSchema.State.OPENED);
         } else {
            Color var6 = var1.getBackground();
            if (var6 != null) {
               var2.setColor(var6);
            } else {
               var2.setColor(Color.white);
            }

            var2.fillRect(var3, var4, 8, 8);
            var2.setColor(Color.gray);
            var2.drawRect(var3, var4, 8, 8);
            var2.setColor(Color.black);
            var2.drawLine(var3 + 2, var4 + 4, var3 + 6, var4 + 4);
         }
      }

      public int getIconWidth() {
         XPStyle.Skin var1 = this.getSkin((Component)null);
         return var1 != null ? var1.getWidth() : 9;
      }

      public int getIconHeight() {
         XPStyle.Skin var1 = this.getSkin((Component)null);
         return var1 != null ? var1.getHeight() : 9;
      }
   }
}
