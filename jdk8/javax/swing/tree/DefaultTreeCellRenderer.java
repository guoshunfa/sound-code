package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.swing.DefaultLookup;

public class DefaultTreeCellRenderer extends JLabel implements TreeCellRenderer {
   private JTree tree;
   protected boolean selected;
   protected boolean hasFocus;
   private boolean drawsFocusBorderAroundIcon;
   private boolean drawDashedFocusIndicator;
   private Color treeBGColor;
   private Color focusBGColor;
   protected transient Icon closedIcon;
   protected transient Icon leafIcon;
   protected transient Icon openIcon;
   protected Color textSelectionColor;
   protected Color textNonSelectionColor;
   protected Color backgroundSelectionColor;
   protected Color backgroundNonSelectionColor;
   protected Color borderSelectionColor;
   private boolean isDropCell;
   private boolean fillBackground;
   private boolean inited = true;

   public void updateUI() {
      super.updateUI();
      if (!this.inited || this.getLeafIcon() instanceof UIResource) {
         this.setLeafIcon(DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon"));
      }

      if (!this.inited || this.getClosedIcon() instanceof UIResource) {
         this.setClosedIcon(DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon"));
      }

      if (!this.inited || this.getOpenIcon() instanceof UIManager) {
         this.setOpenIcon(DefaultLookup.getIcon(this, this.ui, "Tree.openIcon"));
      }

      if (!this.inited || this.getTextSelectionColor() instanceof UIResource) {
         this.setTextSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionForeground"));
      }

      if (!this.inited || this.getTextNonSelectionColor() instanceof UIResource) {
         this.setTextNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textForeground"));
      }

      if (!this.inited || this.getBackgroundSelectionColor() instanceof UIResource) {
         this.setBackgroundSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBackground"));
      }

      if (!this.inited || this.getBackgroundNonSelectionColor() instanceof UIResource) {
         this.setBackgroundNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textBackground"));
      }

      if (!this.inited || this.getBorderSelectionColor() instanceof UIResource) {
         this.setBorderSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBorderColor"));
      }

      this.drawsFocusBorderAroundIcon = DefaultLookup.getBoolean(this, this.ui, "Tree.drawsFocusBorderAroundIcon", false);
      this.drawDashedFocusIndicator = DefaultLookup.getBoolean(this, this.ui, "Tree.drawDashedFocusIndicator", false);
      this.fillBackground = DefaultLookup.getBoolean(this, this.ui, "Tree.rendererFillBackground", true);
      Insets var1 = DefaultLookup.getInsets(this, this.ui, "Tree.rendererMargins");
      if (var1 != null) {
         this.setBorder(new EmptyBorder(var1.top, var1.left, var1.bottom, var1.right));
      }

      this.setName("Tree.cellRenderer");
   }

   public Icon getDefaultOpenIcon() {
      return DefaultLookup.getIcon(this, this.ui, "Tree.openIcon");
   }

   public Icon getDefaultClosedIcon() {
      return DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon");
   }

   public Icon getDefaultLeafIcon() {
      return DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon");
   }

   public void setOpenIcon(Icon var1) {
      this.openIcon = var1;
   }

   public Icon getOpenIcon() {
      return this.openIcon;
   }

   public void setClosedIcon(Icon var1) {
      this.closedIcon = var1;
   }

   public Icon getClosedIcon() {
      return this.closedIcon;
   }

   public void setLeafIcon(Icon var1) {
      this.leafIcon = var1;
   }

   public Icon getLeafIcon() {
      return this.leafIcon;
   }

   public void setTextSelectionColor(Color var1) {
      this.textSelectionColor = var1;
   }

   public Color getTextSelectionColor() {
      return this.textSelectionColor;
   }

   public void setTextNonSelectionColor(Color var1) {
      this.textNonSelectionColor = var1;
   }

   public Color getTextNonSelectionColor() {
      return this.textNonSelectionColor;
   }

   public void setBackgroundSelectionColor(Color var1) {
      this.backgroundSelectionColor = var1;
   }

   public Color getBackgroundSelectionColor() {
      return this.backgroundSelectionColor;
   }

   public void setBackgroundNonSelectionColor(Color var1) {
      this.backgroundNonSelectionColor = var1;
   }

   public Color getBackgroundNonSelectionColor() {
      return this.backgroundNonSelectionColor;
   }

   public void setBorderSelectionColor(Color var1) {
      this.borderSelectionColor = var1;
   }

   public Color getBorderSelectionColor() {
      return this.borderSelectionColor;
   }

   public void setFont(Font var1) {
      if (var1 instanceof FontUIResource) {
         var1 = null;
      }

      super.setFont(var1);
   }

   public Font getFont() {
      Font var1 = super.getFont();
      if (var1 == null && this.tree != null) {
         var1 = this.tree.getFont();
      }

      return var1;
   }

   public void setBackground(Color var1) {
      if (var1 instanceof ColorUIResource) {
         var1 = null;
      }

      super.setBackground(var1);
   }

   public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
      String var8 = var1.convertValueToText(var2, var3, var4, var5, var6, var7);
      this.tree = var1;
      this.hasFocus = var7;
      this.setText(var8);
      Color var9 = null;
      this.isDropCell = false;
      JTree.DropLocation var10 = var1.getDropLocation();
      Color var11;
      if (var10 != null && var10.getChildIndex() == -1 && var1.getRowForPath(var10.getPath()) == var6) {
         var11 = DefaultLookup.getColor(this, this.ui, "Tree.dropCellForeground");
         if (var11 != null) {
            var9 = var11;
         } else {
            var9 = this.getTextSelectionColor();
         }

         this.isDropCell = true;
      } else if (var3) {
         var9 = this.getTextSelectionColor();
      } else {
         var9 = this.getTextNonSelectionColor();
      }

      this.setForeground(var9);
      var11 = null;
      Icon var14;
      if (var5) {
         var14 = this.getLeafIcon();
      } else if (var4) {
         var14 = this.getOpenIcon();
      } else {
         var14 = this.getClosedIcon();
      }

      if (!var1.isEnabled()) {
         this.setEnabled(false);
         LookAndFeel var12 = UIManager.getLookAndFeel();
         Icon var13 = var12.getDisabledIcon(var1, var14);
         if (var13 != null) {
            var14 = var13;
         }

         this.setDisabledIcon(var14);
      } else {
         this.setEnabled(true);
         this.setIcon(var14);
      }

      this.setComponentOrientation(var1.getComponentOrientation());
      this.selected = var3;
      return this;
   }

   public void paint(Graphics var1) {
      Color var2;
      if (this.isDropCell) {
         var2 = DefaultLookup.getColor(this, this.ui, "Tree.dropCellBackground");
         if (var2 == null) {
            var2 = this.getBackgroundSelectionColor();
         }
      } else if (this.selected) {
         var2 = this.getBackgroundSelectionColor();
      } else {
         var2 = this.getBackgroundNonSelectionColor();
         if (var2 == null) {
            var2 = this.getBackground();
         }
      }

      int var3 = -1;
      if (var2 != null && this.fillBackground) {
         var3 = this.getLabelStart();
         var1.setColor(var2);
         if (this.getComponentOrientation().isLeftToRight()) {
            var1.fillRect(var3, 0, this.getWidth() - var3, this.getHeight());
         } else {
            var1.fillRect(0, 0, this.getWidth() - var3, this.getHeight());
         }
      }

      if (this.hasFocus) {
         if (this.drawsFocusBorderAroundIcon) {
            var3 = 0;
         } else if (var3 == -1) {
            var3 = this.getLabelStart();
         }

         if (this.getComponentOrientation().isLeftToRight()) {
            this.paintFocus(var1, var3, 0, this.getWidth() - var3, this.getHeight(), var2);
         } else {
            this.paintFocus(var1, 0, 0, this.getWidth() - var3, this.getHeight(), var2);
         }
      }

      super.paint(var1);
   }

   private void paintFocus(Graphics var1, int var2, int var3, int var4, int var5, Color var6) {
      Color var7 = this.getBorderSelectionColor();
      if (var7 != null && (this.selected || !this.drawDashedFocusIndicator)) {
         var1.setColor(var7);
         var1.drawRect(var2, var3, var4 - 1, var5 - 1);
      }

      if (this.drawDashedFocusIndicator && var6 != null) {
         if (this.treeBGColor != var6) {
            this.treeBGColor = var6;
            this.focusBGColor = new Color(~var6.getRGB());
         }

         var1.setColor(this.focusBGColor);
         BasicGraphicsUtils.drawDashedRect(var1, var2, var3, var4, var5);
      }

   }

   private int getLabelStart() {
      Icon var1 = this.getIcon();
      return var1 != null && this.getText() != null ? var1.getIconWidth() + Math.max(0, this.getIconTextGap() - 1) : 0;
   }

   public Dimension getPreferredSize() {
      Dimension var1 = super.getPreferredSize();
      if (var1 != null) {
         var1 = new Dimension(var1.width + 3, var1.height);
      }

      return var1;
   }

   public void validate() {
   }

   public void invalidate() {
   }

   public void revalidate() {
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
   }

   public void repaint(Rectangle var1) {
   }

   public void repaint() {
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (var1 == "text" || (var1 == "font" || var1 == "foreground") && var2 != var3 && this.getClientProperty("html") != null) {
         super.firePropertyChange(var1, var2, var3);
      }

   }

   public void firePropertyChange(String var1, byte var2, byte var3) {
   }

   public void firePropertyChange(String var1, char var2, char var3) {
   }

   public void firePropertyChange(String var1, short var2, short var3) {
   }

   public void firePropertyChange(String var1, int var2, int var3) {
   }

   public void firePropertyChange(String var1, long var2, long var4) {
   }

   public void firePropertyChange(String var1, float var2, float var3) {
   }

   public void firePropertyChange(String var1, double var2, double var4) {
   }

   public void firePropertyChange(String var1, boolean var2, boolean var3) {
   }
}
