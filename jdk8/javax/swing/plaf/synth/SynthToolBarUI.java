package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import sun.swing.plaf.synth.SynthIcon;

public class SynthToolBarUI extends BasicToolBarUI implements PropertyChangeListener, SynthUI {
   private Icon handleIcon = null;
   private Rectangle contentRect = new Rectangle();
   private SynthStyle style;
   private SynthStyle contentStyle;
   private SynthStyle dragWindowStyle;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthToolBarUI();
   }

   protected void installDefaults() {
      this.toolBar.setLayout(this.createLayout());
      this.updateStyle(this.toolBar);
   }

   protected void installListeners() {
      super.installListeners();
      this.toolBar.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.toolBar.removePropertyChangeListener(this);
   }

   private void updateStyle(JToolBar var1) {
      SynthContext var2 = this.getContext(var1, Region.TOOL_BAR_CONTENT, (SynthStyle)null, 1);
      this.contentStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
      var2 = this.getContext(var1, Region.TOOL_BAR_DRAG_WINDOW, (SynthStyle)null, 1);
      this.dragWindowStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
      var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (var3 != this.style) {
         this.handleIcon = this.style.getIcon(var2, "ToolBar.handleIcon");
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.toolBar, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      this.handleIcon = null;
      var1 = this.getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle, 1);
      this.contentStyle.uninstallDefaults(var1);
      var1.dispose();
      this.contentStyle = null;
      var1 = this.getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle, 1);
      this.dragWindowStyle.uninstallDefaults(var1);
      var1.dispose();
      this.dragWindowStyle = null;
      this.toolBar.setLayout((LayoutManager)null);
   }

   protected void installComponents() {
   }

   protected void uninstallComponents() {
   }

   protected LayoutManager createLayout() {
      return new SynthToolBarUI.SynthToolBarLayoutManager();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private SynthContext getContext(JComponent var1, Region var2, SynthStyle var3) {
      return SynthContext.getContext(var1, var2, var3, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, SynthStyle var3, int var4) {
      return SynthContext.getContext(var1, var2, var3, var4);
   }

   private int getComponentState(JComponent var1, Region var2) {
      return SynthLookAndFeel.getComponentState(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintToolBarBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight(), this.toolBar.getOrientation());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintToolBarBorder(var1, var2, var3, var4, var5, var6, this.toolBar.getOrientation());
   }

   protected void setBorderToNonRollover(Component var1) {
   }

   protected void setBorderToRollover(Component var1) {
   }

   protected void setBorderToNormal(Component var1) {
   }

   protected void paint(SynthContext var1, Graphics var2) {
      if (this.handleIcon != null && this.toolBar.isFloatable()) {
         int var3 = this.toolBar.getComponentOrientation().isLeftToRight() ? 0 : this.toolBar.getWidth() - SynthIcon.getIconWidth(this.handleIcon, var1);
         SynthIcon.paintIcon(this.handleIcon, var1, var2, var3, 0, SynthIcon.getIconWidth(this.handleIcon, var1), SynthIcon.getIconHeight(this.handleIcon, var1));
      }

      SynthContext var4 = this.getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle);
      this.paintContent(var4, var2, this.contentRect);
      var4.dispose();
   }

   protected void paintContent(SynthContext var1, Graphics var2, Rectangle var3) {
      SynthLookAndFeel.updateSubregion(var1, var2, var3);
      var1.getPainter().paintToolBarContentBackground(var1, var2, var3.x, var3.y, var3.width, var3.height, this.toolBar.getOrientation());
      var1.getPainter().paintToolBarContentBorder(var1, var2, var3.x, var3.y, var3.width, var3.height, this.toolBar.getOrientation());
   }

   protected void paintDragWindow(Graphics var1) {
      int var2 = this.dragWindow.getWidth();
      int var3 = this.dragWindow.getHeight();
      SynthContext var4 = this.getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle);
      SynthLookAndFeel.updateSubregion(var4, var1, new Rectangle(0, 0, var2, var3));
      var4.getPainter().paintToolBarDragWindowBackground(var4, var1, 0, 0, var2, var3, this.dragWindow.getOrientation());
      var4.getPainter().paintToolBarDragWindowBorder(var4, var1, 0, 0, var2, var3, this.dragWindow.getOrientation());
      var4.dispose();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JToolBar)var1.getSource());
      }

   }

   class SynthToolBarLayoutManager implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension minimumLayoutSize(Container var1) {
         JToolBar var2 = (JToolBar)var1;
         Insets var3 = var2.getInsets();
         Dimension var4 = new Dimension();
         SynthContext var5 = SynthToolBarUI.this.getContext(var2);
         Dimension var6;
         int var7;
         Component var8;
         if (var2.getOrientation() == 0) {
            var4.width = var2.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, var5) : 0;

            for(var7 = 0; var7 < var2.getComponentCount(); ++var7) {
               var8 = var2.getComponent(var7);
               if (var8.isVisible()) {
                  var6 = var8.getMinimumSize();
                  var4.width += var6.width;
                  var4.height = Math.max(var4.height, var6.height);
               }
            }
         } else {
            var4.height = var2.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, var5) : 0;

            for(var7 = 0; var7 < var2.getComponentCount(); ++var7) {
               var8 = var2.getComponent(var7);
               if (var8.isVisible()) {
                  var6 = var8.getMinimumSize();
                  var4.width = Math.max(var4.width, var6.width);
                  var4.height += var6.height;
               }
            }
         }

         var4.width += var3.left + var3.right;
         var4.height += var3.top + var3.bottom;
         var5.dispose();
         return var4;
      }

      public Dimension preferredLayoutSize(Container var1) {
         JToolBar var2 = (JToolBar)var1;
         Insets var3 = var2.getInsets();
         Dimension var4 = new Dimension();
         SynthContext var5 = SynthToolBarUI.this.getContext(var2);
         Dimension var6;
         int var7;
         Component var8;
         if (var2.getOrientation() == 0) {
            var4.width = var2.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, var5) : 0;

            for(var7 = 0; var7 < var2.getComponentCount(); ++var7) {
               var8 = var2.getComponent(var7);
               if (var8.isVisible()) {
                  var6 = var8.getPreferredSize();
                  var4.width += var6.width;
                  var4.height = Math.max(var4.height, var6.height);
               }
            }
         } else {
            var4.height = var2.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, var5) : 0;

            for(var7 = 0; var7 < var2.getComponentCount(); ++var7) {
               var8 = var2.getComponent(var7);
               if (var8.isVisible()) {
                  var6 = var8.getPreferredSize();
                  var4.width = Math.max(var4.width, var6.width);
                  var4.height += var6.height;
               }
            }
         }

         var4.width += var3.left + var3.right;
         var4.height += var3.top + var3.bottom;
         var5.dispose();
         return var4;
      }

      public void layoutContainer(Container var1) {
         JToolBar var2 = (JToolBar)var1;
         Insets var3 = var2.getInsets();
         boolean var4 = var2.getComponentOrientation().isLeftToRight();
         SynthContext var5 = SynthToolBarUI.this.getContext(var2);
         int var8 = 0;

         int var9;
         for(var9 = 0; var9 < var2.getComponentCount(); ++var9) {
            if (this.isGlue(var2.getComponent(var9))) {
               ++var8;
            }
         }

         Component var6;
         Dimension var7;
         int var10;
         int var11;
         int var12;
         int var13;
         int var14;
         int var15;
         int var16;
         if (var2.getOrientation() == 0) {
            var9 = var2.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, var5) : 0;
            SynthToolBarUI.this.contentRect.x = var4 ? var9 : 0;
            SynthToolBarUI.this.contentRect.y = 0;
            SynthToolBarUI.this.contentRect.width = var2.getWidth() - var9;
            SynthToolBarUI.this.contentRect.height = var2.getHeight();
            var10 = var4 ? var9 + var3.left : var2.getWidth() - var9 - var3.right;
            var11 = var3.top;
            var12 = var2.getHeight() - var3.top - var3.bottom;
            var13 = 0;
            if (var8 > 0) {
               var14 = this.minimumLayoutSize(var1).width;
               var13 = (var2.getWidth() - var14) / var8;
               if (var13 < 0) {
                  var13 = 0;
               }
            }

            for(var14 = 0; var14 < var2.getComponentCount(); ++var14) {
               var6 = var2.getComponent(var14);
               if (var6.isVisible()) {
                  var7 = var6.getPreferredSize();
                  if (var7.height < var12 && !(var6 instanceof JSeparator)) {
                     var15 = var11 + var12 / 2 - var7.height / 2;
                     var16 = var7.height;
                  } else {
                     var15 = var11;
                     var16 = var12;
                  }

                  if (this.isGlue(var6)) {
                     var7.width += var13;
                  }

                  var6.setBounds(var4 ? var10 : var10 - var7.width, var15, var7.width, var16);
                  var10 = var4 ? var10 + var7.width : var10 - var7.width;
               }
            }
         } else {
            var9 = var2.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, var5) : 0;
            SynthToolBarUI.this.contentRect.x = 0;
            SynthToolBarUI.this.contentRect.y = var9;
            SynthToolBarUI.this.contentRect.width = var2.getWidth();
            SynthToolBarUI.this.contentRect.height = var2.getHeight() - var9;
            var10 = var3.left;
            var11 = var2.getWidth() - var3.left - var3.right;
            var12 = var9 + var3.top;
            var13 = 0;
            if (var8 > 0) {
               var14 = this.minimumLayoutSize(var1).height;
               var13 = (var2.getHeight() - var14) / var8;
               if (var13 < 0) {
                  var13 = 0;
               }
            }

            for(var14 = 0; var14 < var2.getComponentCount(); ++var14) {
               var6 = var2.getComponent(var14);
               if (var6.isVisible()) {
                  var7 = var6.getPreferredSize();
                  if (var7.width < var11 && !(var6 instanceof JSeparator)) {
                     var15 = var10 + var11 / 2 - var7.width / 2;
                     var16 = var7.width;
                  } else {
                     var15 = var10;
                     var16 = var11;
                  }

                  if (this.isGlue(var6)) {
                     var7.height += var13;
                  }

                  var6.setBounds(var15, var12, var16, var7.height);
                  var12 += var7.height;
               }
            }
         }

         var5.dispose();
      }

      private boolean isGlue(Component var1) {
         if (var1.isVisible() && var1 instanceof Box.Filler) {
            Box.Filler var2 = (Box.Filler)var1;
            Dimension var3 = var2.getMinimumSize();
            Dimension var4 = var2.getPreferredSize();
            return var3.width == 0 && var3.height == 0 && var4.width == 0 && var4.height == 0;
         } else {
            return false;
         }
      }
   }
}
