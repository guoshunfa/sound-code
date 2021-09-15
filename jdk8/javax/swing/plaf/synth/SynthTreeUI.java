package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.swing.plaf.synth.SynthIcon;

public class SynthTreeUI extends BasicTreeUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthStyle cellStyle;
   private SynthContext paintContext;
   private boolean drawHorizontalLines;
   private boolean drawVerticalLines;
   private Object linesStyle;
   private int padding;
   private boolean useTreeColors;
   private Icon expandedIconWrapper = new SynthTreeUI.ExpandedIconWrapper();

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTreeUI();
   }

   public Icon getExpandedIcon() {
      return this.expandedIconWrapper;
   }

   protected void installDefaults() {
      this.updateStyle(this.tree);
   }

   private void updateStyle(JTree var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         this.setExpandedIcon(this.style.getIcon(var2, "Tree.expandedIcon"));
         this.setCollapsedIcon(this.style.getIcon(var2, "Tree.collapsedIcon"));
         this.setLeftChildIndent(this.style.getInt(var2, "Tree.leftChildIndent", 0));
         this.setRightChildIndent(this.style.getInt(var2, "Tree.rightChildIndent", 0));
         this.drawHorizontalLines = this.style.getBoolean(var2, "Tree.drawHorizontalLines", true);
         this.drawVerticalLines = this.style.getBoolean(var2, "Tree.drawVerticalLines", true);
         this.linesStyle = this.style.get(var2, "Tree.linesStyle");
         Object var4 = this.style.get(var2, "Tree.rowHeight");
         if (var4 != null) {
            LookAndFeel.installProperty(var1, "rowHeight", var4);
         }

         var4 = this.style.get(var2, "Tree.scrollsOnExpand");
         LookAndFeel.installProperty(var1, "scrollsOnExpand", var4 != null ? var4 : Boolean.TRUE);
         this.padding = this.style.getInt(var2, "Tree.padding", 0);
         this.largeModel = var1.isLargeModel() && var1.getRowHeight() > 0;
         this.useTreeColors = this.style.getBoolean(var2, "Tree.rendererUseTreeColors", true);
         Boolean var5 = this.style.getBoolean(var2, "Tree.showsRootHandles", Boolean.TRUE);
         LookAndFeel.installProperty(var1, "showsRootHandles", var5);
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
      var2 = this.getContext(var1, Region.TREE_CELL, 1);
      this.cellStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected void installListeners() {
      super.installListeners();
      this.tree.addPropertyChangeListener(this);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private SynthContext getContext(JComponent var1, Region var2) {
      return this.getContext(var1, var2, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      return SynthContext.getContext(var1, var2, this.cellStyle, var3);
   }

   private int getComponentState(JComponent var1, Region var2) {
      return 513;
   }

   protected TreeCellEditor createDefaultCellEditor() {
      TreeCellRenderer var1 = this.tree.getCellRenderer();
      SynthTreeUI.SynthTreeCellEditor var2;
      if (var1 != null && var1 instanceof DefaultTreeCellRenderer) {
         var2 = new SynthTreeUI.SynthTreeCellEditor(this.tree, (DefaultTreeCellRenderer)var1);
      } else {
         var2 = new SynthTreeUI.SynthTreeCellEditor(this.tree, (DefaultTreeCellRenderer)null);
      }

      return var2;
   }

   protected TreeCellRenderer createDefaultCellRenderer() {
      return new SynthTreeUI.SynthTreeCellRenderer();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.tree, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      var1 = this.getContext(this.tree, Region.TREE_CELL, 1);
      this.cellStyle.uninstallDefaults(var1);
      var1.dispose();
      this.cellStyle = null;
      if (this.tree.getTransferHandler() instanceof UIResource) {
         this.tree.setTransferHandler((TransferHandler)null);
      }

   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.tree.removePropertyChangeListener(this);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintTreeBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTreeBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      this.paintContext = var1;
      this.updateLeadSelectionRow();
      Rectangle var3 = var2.getClipBounds();
      Insets var4 = this.tree.getInsets();
      TreePath var5 = this.getClosestPathForLocation(this.tree, 0, var3.y);
      Enumeration var6 = this.treeState.getVisiblePathsFrom(var5);
      int var7 = this.treeState.getRowForPath(var5);
      int var8 = var3.y + var3.height;
      TreeModel var9 = this.tree.getModel();
      SynthContext var10 = this.getContext(this.tree, Region.TREE_CELL);
      this.drawingCache.clear();
      this.setHashColor(var1.getStyle().getColor(var1, ColorType.FOREGROUND));
      if (var6 != null) {
         boolean var11 = false;
         Rectangle var15 = new Rectangle(0, 0, this.tree.getWidth(), 0);
         TreeCellRenderer var18 = this.tree.getCellRenderer();
         DefaultTreeCellRenderer var19 = var18 instanceof DefaultTreeCellRenderer ? (DefaultTreeCellRenderer)var18 : null;
         this.configureRenderer(var10);

         boolean var12;
         boolean var13;
         boolean var14;
         Rectangle var16;
         TreePath var17;
         for(; !var11 && var6.hasMoreElements(); ++var7) {
            var17 = (TreePath)var6.nextElement();
            var16 = this.getPathBounds(this.tree, var17);
            if (var17 != null && var16 != null) {
               var14 = var9.isLeaf(var17.getLastPathComponent());
               if (var14) {
                  var13 = false;
                  var12 = false;
               } else {
                  var12 = this.treeState.getExpandedState(var17);
                  var13 = this.tree.hasBeenExpanded(var17);
               }

               var15.y = var16.y;
               var15.height = var16.height;
               this.paintRow(var18, var19, var1, var10, var2, var3, var4, var16, var15, var17, var7, var12, var13, var14);
               if (var16.y + var16.height >= var8) {
                  var11 = true;
               }
            } else {
               var11 = true;
            }
         }

         boolean var20 = this.tree.isRootVisible();

         TreePath var21;
         for(var21 = var5.getParentPath(); var21 != null; var21 = var21.getParentPath()) {
            this.paintVerticalPartOfLeg(var2, var3, var4, var21);
            this.drawingCache.put(var21, Boolean.TRUE);
         }

         var11 = false;

         for(var6 = this.treeState.getVisiblePathsFrom(var5); !var11 && var6.hasMoreElements(); ++var7) {
            var17 = (TreePath)var6.nextElement();
            var16 = this.getPathBounds(this.tree, var17);
            if (var17 != null && var16 != null) {
               var14 = var9.isLeaf(var17.getLastPathComponent());
               if (var14) {
                  var13 = false;
                  var12 = false;
               } else {
                  var12 = this.treeState.getExpandedState(var17);
                  var13 = this.tree.hasBeenExpanded(var17);
               }

               var21 = var17.getParentPath();
               if (var21 != null) {
                  if (this.drawingCache.get(var21) == null) {
                     this.paintVerticalPartOfLeg(var2, var3, var4, var21);
                     this.drawingCache.put(var21, Boolean.TRUE);
                  }

                  this.paintHorizontalPartOfLeg(var2, var3, var4, var16, var17, var7, var12, var13, var14);
               } else if (var20 && var7 == 0) {
                  this.paintHorizontalPartOfLeg(var2, var3, var4, var16, var17, var7, var12, var13, var14);
               }

               if (this.shouldPaintExpandControl(var17, var7, var12, var13, var14)) {
                  this.paintExpandControl(var2, var3, var4, var16, var17, var7, var12, var13, var14);
               }

               if (var16.y + var16.height >= var8) {
                  var11 = true;
               }
            } else {
               var11 = true;
            }
         }
      }

      var10.dispose();
      this.paintDropLine(var2);
      this.rendererPane.removeAll();
      this.paintContext = null;
   }

   private void configureRenderer(SynthContext var1) {
      TreeCellRenderer var2 = this.tree.getCellRenderer();
      if (var2 instanceof DefaultTreeCellRenderer) {
         DefaultTreeCellRenderer var3 = (DefaultTreeCellRenderer)var2;
         SynthStyle var4 = var1.getStyle();
         var1.setComponentState(513);
         Color var5 = var3.getTextSelectionColor();
         if (var5 == null || var5 instanceof UIResource) {
            var3.setTextSelectionColor(var4.getColor(var1, ColorType.TEXT_FOREGROUND));
         }

         var5 = var3.getBackgroundSelectionColor();
         if (var5 == null || var5 instanceof UIResource) {
            var3.setBackgroundSelectionColor(var4.getColor(var1, ColorType.TEXT_BACKGROUND));
         }

         var1.setComponentState(1);
         var5 = var3.getTextNonSelectionColor();
         if (var5 == null || var5 instanceof UIResource) {
            var3.setTextNonSelectionColor(var4.getColorForState(var1, ColorType.TEXT_FOREGROUND));
         }

         var5 = var3.getBackgroundNonSelectionColor();
         if (var5 == null || var5 instanceof UIResource) {
            var3.setBackgroundNonSelectionColor(var4.getColorForState(var1, ColorType.TEXT_BACKGROUND));
         }
      }

   }

   protected void paintHorizontalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      if (this.drawHorizontalLines) {
         super.paintHorizontalPartOfLeg(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

   }

   protected void paintHorizontalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.horizontalLine", var1, var4, var3, var5, var3, this.linesStyle);
   }

   protected void paintVerticalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, TreePath var4) {
      if (this.drawVerticalLines) {
         super.paintVerticalPartOfLeg(var1, var2, var3, var4);
      }

   }

   protected void paintVerticalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.verticalLine", var1, var3, var4, var3, var5, this.linesStyle);
   }

   private void paintRow(TreeCellRenderer var1, DefaultTreeCellRenderer var2, SynthContext var3, SynthContext var4, Graphics var5, Rectangle var6, Insets var7, Rectangle var8, Rectangle var9, TreePath var10, int var11, boolean var12, boolean var13, boolean var14) {
      boolean var15 = this.tree.isRowSelected(var11);
      JTree.DropLocation var16 = this.tree.getDropLocation();
      boolean var17 = var16 != null && var16.getChildIndex() == -1 && var10 == var16.getPath();
      int var18 = 1;
      if (var15 || var17) {
         var18 |= 512;
      }

      if (this.tree.isFocusOwner() && var11 == this.getLeadSelectionRow()) {
         var18 |= 256;
      }

      var4.setComponentState(var18);
      if (var2 != null && var2.getBorderSelectionColor() instanceof UIResource) {
         var2.setBorderSelectionColor(this.style.getColor(var4, ColorType.FOCUS));
      }

      SynthLookAndFeel.updateSubregion(var4, var5, var9);
      var4.getPainter().paintTreeCellBackground(var4, var5, var9.x, var9.y, var9.width, var9.height);
      var4.getPainter().paintTreeCellBorder(var4, var5, var9.x, var9.y, var9.width, var9.height);
      if (this.editingComponent == null || this.editingRow != var11) {
         int var19;
         if (this.tree.hasFocus()) {
            var19 = this.getLeadSelectionRow();
         } else {
            var19 = -1;
         }

         Component var20 = var1.getTreeCellRendererComponent(this.tree, var10.getLastPathComponent(), var15, var12, var14, var11, var19 == var11);
         this.rendererPane.paintComponent(var5, var20, this.tree, var8.x, var8.y, var8.width, var8.height, true);
      }
   }

   private int findCenteredX(int var1, int var2) {
      return this.tree.getComponentOrientation().isLeftToRight() ? var1 - (int)Math.ceil((double)var2 / 2.0D) : var1 - (int)Math.floor((double)var2 / 2.0D);
   }

   protected void paintExpandControl(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      boolean var10 = this.tree.getSelectionModel().isPathSelected(var5);
      int var11 = this.paintContext.getComponentState();
      if (var10) {
         this.paintContext.setComponentState(var11 | 512);
      }

      super.paintExpandControl(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.paintContext.setComponentState(var11);
   }

   protected void drawCentered(Component var1, Graphics var2, Icon var3, int var4, int var5) {
      int var6 = SynthIcon.getIconWidth(var3, this.paintContext);
      int var7 = SynthIcon.getIconHeight(var3, this.paintContext);
      SynthIcon.paintIcon(var3, this.paintContext, var2, this.findCenteredX(var4, var6), var5 - var7 / 2, var6, var7);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTree)var1.getSource());
      }

      if ("dropLocation" == var1.getPropertyName()) {
         JTree.DropLocation var2 = (JTree.DropLocation)var1.getOldValue();
         this.repaintDropLocation(var2);
         this.repaintDropLocation(this.tree.getDropLocation());
      }

   }

   protected void paintDropLine(Graphics var1) {
      JTree.DropLocation var2 = this.tree.getDropLocation();
      if (this.isDropLine(var2)) {
         Color var3 = (Color)this.style.get(this.paintContext, "Tree.dropLineColor");
         if (var3 != null) {
            var1.setColor(var3);
            Rectangle var4 = this.getDropLineRect(var2);
            var1.fillRect(var4.x, var4.y, var4.width, var4.height);
         }

      }
   }

   private void repaintDropLocation(JTree.DropLocation var1) {
      if (var1 != null) {
         Rectangle var2;
         if (this.isDropLine(var1)) {
            var2 = this.getDropLineRect(var1);
         } else {
            var2 = this.tree.getPathBounds(var1.getPath());
            if (var2 != null) {
               var2.x = 0;
               var2.width = this.tree.getWidth();
            }
         }

         if (var2 != null) {
            this.tree.repaint(var2);
         }

      }
   }

   protected int getRowX(int var1, int var2) {
      return super.getRowX(var1, var2) + this.padding;
   }

   private class ExpandedIconWrapper extends SynthIcon {
      private ExpandedIconWrapper() {
      }

      public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 == null) {
            var1 = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
            SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, var1, var2, var3, var4, var5, var6);
            var1.dispose();
         } else {
            SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, var1, var2, var3, var4, var5, var6);
         }

      }

      public int getIconWidth(SynthContext var1) {
         int var2;
         if (var1 == null) {
            var1 = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
            var2 = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, var1);
            var1.dispose();
         } else {
            var2 = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, var1);
         }

         return var2;
      }

      public int getIconHeight(SynthContext var1) {
         int var2;
         if (var1 == null) {
            var1 = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
            var2 = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, var1);
            var1.dispose();
         } else {
            var2 = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, var1);
         }

         return var2;
      }

      // $FF: synthetic method
      ExpandedIconWrapper(Object var2) {
         this();
      }
   }

   private static class SynthTreeCellEditor extends DefaultTreeCellEditor {
      public SynthTreeCellEditor(JTree var1, DefaultTreeCellRenderer var2) {
         super(var1, var2);
         this.setBorderSelectionColor((Color)null);
      }

      protected TreeCellEditor createTreeCellEditor() {
         JTextField var1 = new JTextField() {
            public String getName() {
               return "Tree.cellEditor";
            }
         };
         DefaultCellEditor var2 = new DefaultCellEditor(var1);
         var2.setClickCountToStart(1);
         return var2;
      }
   }

   private class SynthTreeCellRenderer extends DefaultTreeCellRenderer implements UIResource {
      SynthTreeCellRenderer() {
      }

      public String getName() {
         return "Tree.cellRenderer";
      }

      public Component getTreeCellRendererComponent(JTree var1, Object var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
         if (SynthTreeUI.this.useTreeColors || !var3 && !var7) {
            SynthLookAndFeel.resetSelectedUI();
         } else {
            SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), var3, var7, var1.isEnabled(), false);
         }

         return super.getTreeCellRendererComponent(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paint(Graphics var1) {
         this.paintComponent(var1);
         if (this.hasFocus) {
            SynthContext var2 = SynthTreeUI.this.getContext(SynthTreeUI.this.tree, Region.TREE_CELL);
            if (var2.getStyle() == null) {
               assert false : "SynthTreeCellRenderer is being used outside of UI that created it";

               return;
            }

            int var3 = 0;
            Icon var4 = this.getIcon();
            if (var4 != null && this.getText() != null) {
               var3 = var4.getIconWidth() + Math.max(0, this.getIconTextGap() - 1);
            }

            if (this.selected) {
               var2.setComponentState(513);
            } else {
               var2.setComponentState(1);
            }

            if (this.getComponentOrientation().isLeftToRight()) {
               var2.getPainter().paintTreeCellFocus(var2, var1, var3, 0, this.getWidth() - var3, this.getHeight());
            } else {
               var2.getPainter().paintTreeCellFocus(var2, var1, 0, 0, this.getWidth() - var3, this.getHeight());
            }

            var2.dispose();
         }

         SynthLookAndFeel.resetSelectedUI();
      }
   }
}
