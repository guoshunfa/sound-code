package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

public class MotifTreeUI extends BasicTreeUI {
   static final int HALF_SIZE = 7;
   static final int SIZE = 14;

   public void installUI(JComponent var1) {
      super.installUI(var1);
   }

   protected void paintVerticalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      if (this.tree.getComponentOrientation().isLeftToRight()) {
         var1.fillRect(var3, var4, 2, var5 - var4 + 2);
      } else {
         var1.fillRect(var3 - 1, var4, 2, var5 - var4 + 2);
      }

   }

   protected void paintHorizontalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      var1.fillRect(var4, var3, var5 - var4 + 1, 2);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new MotifTreeUI();
   }

   public TreeCellRenderer createDefaultCellRenderer() {
      return new MotifTreeCellRenderer();
   }

   public static class MotifCollapsedIcon extends MotifTreeUI.MotifExpandedIcon {
      public static Icon createCollapsedIcon() {
         return new MotifTreeUI.MotifCollapsedIcon();
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         super.paintIcon(var1, var2, var3, var4);
         var2.drawLine(var3 + 7 - 1, var4 + 3, var3 + 7 - 1, var4 + 10);
         var2.drawLine(var3 + 7, var4 + 3, var3 + 7, var4 + 10);
      }
   }

   public static class MotifExpandedIcon implements Icon, Serializable {
      static Color bg;
      static Color fg;
      static Color highlight;
      static Color shadow;

      public MotifExpandedIcon() {
         bg = UIManager.getColor("Tree.iconBackground");
         fg = UIManager.getColor("Tree.iconForeground");
         highlight = UIManager.getColor("Tree.iconHighlight");
         shadow = UIManager.getColor("Tree.iconShadow");
      }

      public static Icon createExpandedIcon() {
         return new MotifTreeUI.MotifExpandedIcon();
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         var2.setColor(highlight);
         var2.drawLine(var3, var4, var3 + 14 - 1, var4);
         var2.drawLine(var3, var4 + 1, var3, var4 + 14 - 1);
         var2.setColor(shadow);
         var2.drawLine(var3 + 14 - 1, var4 + 1, var3 + 14 - 1, var4 + 14 - 1);
         var2.drawLine(var3 + 1, var4 + 14 - 1, var3 + 14 - 1, var4 + 14 - 1);
         var2.setColor(bg);
         var2.fillRect(var3 + 1, var4 + 1, 12, 12);
         var2.setColor(fg);
         var2.drawLine(var3 + 3, var4 + 7 - 1, var3 + 14 - 4, var4 + 7 - 1);
         var2.drawLine(var3 + 3, var4 + 7, var3 + 14 - 4, var4 + 7);
      }

      public int getIconWidth() {
         return 14;
      }

      public int getIconHeight() {
         return 14;
      }
   }
}
