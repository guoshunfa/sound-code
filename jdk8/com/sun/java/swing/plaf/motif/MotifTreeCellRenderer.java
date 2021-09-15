package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MotifTreeCellRenderer extends DefaultTreeCellRenderer {
   static final int LEAF_SIZE = 13;
   static final Icon LEAF_ICON = new IconUIResource(new MotifTreeCellRenderer.TreeLeafIcon());

   public static Icon loadLeafIcon() {
      return LEAF_ICON;
   }

   public static class TreeLeafIcon implements Icon, Serializable {
      Color bg = UIManager.getColor("Tree.iconBackground");
      Color shadow = UIManager.getColor("Tree.iconShadow");
      Color highlight = UIManager.getColor("Tree.iconHighlight");

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.setColor(this.bg);
         var4 -= 3;
         var2.fillRect(var3 + 4, var4 + 7, 5, 5);
         var2.drawLine(var3 + 6, var4 + 6, var3 + 6, var4 + 6);
         var2.drawLine(var3 + 3, var4 + 9, var3 + 3, var4 + 9);
         var2.drawLine(var3 + 6, var4 + 12, var3 + 6, var4 + 12);
         var2.drawLine(var3 + 9, var4 + 9, var3 + 9, var4 + 9);
         var2.setColor(this.highlight);
         var2.drawLine(var3 + 2, var4 + 9, var3 + 5, var4 + 6);
         var2.drawLine(var3 + 3, var4 + 10, var3 + 5, var4 + 12);
         var2.setColor(this.shadow);
         var2.drawLine(var3 + 6, var4 + 13, var3 + 10, var4 + 9);
         var2.drawLine(var3 + 9, var4 + 8, var3 + 7, var4 + 6);
      }

      public int getIconWidth() {
         return 13;
      }

      public int getIconHeight() {
         return 13;
      }
   }
}
