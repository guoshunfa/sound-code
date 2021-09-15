package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

public class MetalTreeUI extends BasicTreeUI {
   private static Color lineColor;
   private static final String LINE_STYLE = "JTree.lineStyle";
   private static final String LEG_LINE_STYLE_STRING = "Angled";
   private static final String HORIZ_STYLE_STRING = "Horizontal";
   private static final String NO_STYLE_STRING = "None";
   private static final int LEG_LINE_STYLE = 2;
   private static final int HORIZ_LINE_STYLE = 1;
   private static final int NO_LINE_STYLE = 0;
   private int lineStyle = 2;
   private PropertyChangeListener lineStyleListener = new MetalTreeUI.LineListener();

   public static ComponentUI createUI(JComponent var0) {
      return new MetalTreeUI();
   }

   protected int getHorizontalLegBuffer() {
      return 3;
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      lineColor = UIManager.getColor("Tree.line");
      Object var2 = var1.getClientProperty("JTree.lineStyle");
      this.decodeLineStyle(var2);
      var1.addPropertyChangeListener(this.lineStyleListener);
   }

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.lineStyleListener);
      super.uninstallUI(var1);
   }

   protected void decodeLineStyle(Object var1) {
      if (var1 != null && !var1.equals("Angled")) {
         if (var1.equals("None")) {
            this.lineStyle = 0;
         } else if (var1.equals("Horizontal")) {
            this.lineStyle = 1;
         }
      } else {
         this.lineStyle = 2;
      }

   }

   protected boolean isLocationInExpandControl(int var1, int var2, int var3, int var4) {
      if (this.tree != null && !this.isLeaf(var1)) {
         int var5;
         if (this.getExpandedIcon() != null) {
            var5 = this.getExpandedIcon().getIconWidth() + 6;
         } else {
            var5 = 8;
         }

         Insets var6 = this.tree.getInsets();
         int var7 = var6 != null ? var6.left : 0;
         var7 += (var2 + this.depthOffset - 1) * this.totalChildIndent + this.getLeftChildIndent() - var5 / 2;
         int var8 = var7 + var5;
         return var3 >= var7 && var3 <= var8;
      } else {
         return false;
      }
   }

   public void paint(Graphics var1, JComponent var2) {
      super.paint(var1, var2);
      if (this.lineStyle == 1 && !this.largeModel) {
         this.paintHorizontalSeparators(var1, var2);
      }

   }

   protected void paintHorizontalSeparators(Graphics var1, JComponent var2) {
      var1.setColor(lineColor);
      Rectangle var3 = var1.getClipBounds();
      int var4 = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, var3.y));
      int var5 = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, var3.y + var3.height - 1));
      if (var4 > -1 && var5 > -1) {
         for(int var6 = var4; var6 <= var5; ++var6) {
            TreePath var7 = this.getPathForRow(this.tree, var6);
            if (var7 != null && var7.getPathCount() == 2) {
               Rectangle var8 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var6));
               if (var8 != null) {
                  var1.drawLine(var3.x, var8.y, var3.x + var3.width, var8.y);
               }
            }
         }

      }
   }

   protected void paintVerticalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, TreePath var4) {
      if (this.lineStyle == 2) {
         super.paintVerticalPartOfLeg(var1, var2, var3, var4);
      }

   }

   protected void paintHorizontalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      if (this.lineStyle == 2) {
         super.paintHorizontalPartOfLeg(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

   }

   class LineListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("JTree.lineStyle")) {
            MetalTreeUI.this.decodeLineStyle(var1.getNewValue());
         }

      }
   }
}
