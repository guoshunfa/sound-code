package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class MotifBorders {
   public static void drawBezel(Graphics var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6, Color var7, Color var8, Color var9, Color var10) {
      Color var11 = var0.getColor();
      var0.translate(var1, var2);
      if (var5) {
         if (var6) {
            var0.setColor(var10);
            var0.drawRect(0, 0, var3 - 1, var4 - 1);
         }

         var0.setColor(var7);
         var0.drawRect(1, 1, var3 - 3, var4 - 3);
         var0.setColor(var8);
         var0.drawLine(2, var4 - 3, var3 - 3, var4 - 3);
         var0.drawLine(var3 - 3, 2, var3 - 3, var4 - 4);
      } else {
         if (var6) {
            var0.setColor(var10);
            var0.drawRect(0, 0, var3 - 1, var4 - 1);
            var0.setColor(var8);
            var0.drawLine(1, 1, 1, var4 - 3);
            var0.drawLine(2, 1, var3 - 4, 1);
            var0.setColor(var7);
            var0.drawLine(2, var4 - 3, var3 - 3, var4 - 3);
            var0.drawLine(var3 - 3, 1, var3 - 3, var4 - 4);
            var0.setColor(var9);
            var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
            var0.drawLine(var3 - 2, var4 - 2, var3 - 2, 1);
         } else {
            var0.setColor(var8);
            var0.drawLine(1, 1, 1, var4 - 3);
            var0.drawLine(2, 1, var3 - 4, 1);
            var0.setColor(var7);
            var0.drawLine(2, var4 - 3, var3 - 3, var4 - 3);
            var0.drawLine(var3 - 3, 1, var3 - 3, var4 - 4);
            var0.setColor(var9);
            var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
            var0.drawLine(var3 - 2, var4 - 2, var3 - 2, 0);
         }

         var0.translate(-var1, -var2);
      }

      var0.setColor(var11);
   }

   public static class MotifPopupMenuBorder extends AbstractBorder implements UIResource {
      protected Font font;
      protected Color background;
      protected Color foreground;
      protected Color shadowColor;
      protected Color highlightColor;
      protected static final int TEXT_SPACING = 2;
      protected static final int GROOVE_HEIGHT = 2;

      public MotifPopupMenuBorder(Font var1, Color var2, Color var3, Color var4, Color var5) {
         this.font = var1;
         this.background = var2;
         this.foreground = var3;
         this.shadowColor = var4;
         this.highlightColor = var5;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JPopupMenu) {
            Font var7 = var2.getFont();
            Color var8 = var2.getColor();
            JPopupMenu var9 = (JPopupMenu)var1;
            String var10 = var9.getLabel();
            if (var10 != null) {
               var2.setFont(this.font);
               FontMetrics var11 = SwingUtilities2.getFontMetrics(var9, var2, this.font);
               int var12 = var11.getHeight();
               int var13 = var11.getDescent();
               int var14 = var11.getAscent();
               Point var15 = new Point();
               int var16 = SwingUtilities2.stringWidth(var9, var11, var10);
               var15.y = var4 + var14 + 2;
               var15.x = var3 + (var5 - var16) / 2;
               var2.setColor(this.background);
               var2.fillRect(var15.x - 2, var15.y - (var12 - var13), var16 + 4, var12 - var13);
               var2.setColor(this.foreground);
               SwingUtilities2.drawString(var9, var2, (String)var10, var15.x, var15.y);
               MotifGraphicsUtils.drawGroove(var2, var3, var15.y + 2, var5, 2, this.shadowColor, this.highlightColor);
               var2.setFont(var7);
               var2.setColor(var8);
            }
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         if (!(var1 instanceof JPopupMenu)) {
            return var2;
         } else {
            int var4 = 0;
            int var5 = 16;
            String var6 = ((JPopupMenu)var1).getLabel();
            if (var6 == null) {
               var2.left = var2.top = var2.right = var2.bottom = 0;
               return var2;
            } else {
               FontMetrics var3 = var1.getFontMetrics(this.font);
               if (var3 != null) {
                  var4 = var3.getDescent();
                  var5 = var3.getAscent();
               }

               var2.top += var5 + var4 + 2 + 2;
               return var2;
            }
         }
      }
   }

   public static class InternalFrameBorder extends MotifBorders.FrameBorder {
      JInternalFrame frame;
      public static final int CORNER_SIZE = 24;

      public InternalFrameBorder(JInternalFrame var1) {
         super(var1);
         this.frame = var1;
      }

      public void setFrame(JInternalFrame var1) {
         this.frame = var1;
      }

      public JInternalFrame frame() {
         return this.frame;
      }

      public int resizePartWidth() {
         return !this.frame.isResizable() ? 0 : 5;
      }

      protected boolean drawTopBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (super.drawTopBorder(var1, var2, var3, var4, var5, var6) && this.frame.isResizable()) {
            var2.setColor(this.getFrameShadow());
            var2.drawLine(23, var4 + 1, 23, var4 + 4);
            var2.drawLine(var5 - 24 - 1, var4 + 1, var5 - 24 - 1, var4 + 4);
            var2.setColor(this.getFrameHighlight());
            var2.drawLine(24, var4, 24, var4 + 4);
            var2.drawLine(var5 - 24, var4, var5 - 24, var4 + 4);
            return true;
         } else {
            return false;
         }
      }

      protected boolean drawLeftBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (super.drawLeftBorder(var1, var2, var3, var4, var5, var6) && this.frame.isResizable()) {
            var2.setColor(this.getFrameHighlight());
            int var7 = var4 + 24;
            var2.drawLine(var3, var7, var3 + 4, var7);
            int var8 = var6 - 24;
            var2.drawLine(var3 + 1, var8, var3 + 5, var8);
            var2.setColor(this.getFrameShadow());
            var2.drawLine(var3 + 1, var7 - 1, var3 + 5, var7 - 1);
            var2.drawLine(var3 + 1, var8 - 1, var3 + 5, var8 - 1);
            return true;
         } else {
            return false;
         }
      }

      protected boolean drawRightBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (super.drawRightBorder(var1, var2, var3, var4, var5, var6) && this.frame.isResizable()) {
            int var7 = var5 - this.getBorderInsets(var1).right;
            var2.setColor(this.getFrameHighlight());
            int var8 = var4 + 24;
            var2.drawLine(var7, var8, var5 - 2, var8);
            int var9 = var6 - 24;
            var2.drawLine(var7 + 1, var9, var7 + 3, var9);
            var2.setColor(this.getFrameShadow());
            var2.drawLine(var7 + 1, var8 - 1, var5 - 2, var8 - 1);
            var2.drawLine(var7 + 1, var9 - 1, var7 + 3, var9 - 1);
            return true;
         } else {
            return false;
         }
      }

      protected boolean drawBottomBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (super.drawBottomBorder(var1, var2, var3, var4, var5, var6) && this.frame.isResizable()) {
            int var7 = var6 - this.getBorderInsets(var1).bottom;
            var2.setColor(this.getFrameShadow());
            var2.drawLine(23, var7 + 1, 23, var6 - 1);
            var2.drawLine(var5 - 24, var7 + 1, var5 - 24, var6 - 1);
            var2.setColor(this.getFrameHighlight());
            var2.drawLine(24, var7, 24, var6 - 2);
            var2.drawLine(var5 - 24 + 1, var7, var5 - 24 + 1, var6 - 2);
            return true;
         } else {
            return false;
         }
      }

      protected boolean isActiveFrame() {
         return this.frame.isSelected();
      }
   }

   public static class FrameBorder extends AbstractBorder implements UIResource {
      JComponent jcomp;
      Color frameHighlight;
      Color frameColor;
      Color frameShadow;
      public static final int BORDER_SIZE = 5;

      public FrameBorder(JComponent var1) {
         this.jcomp = var1;
      }

      public void setComponent(JComponent var1) {
         this.jcomp = var1;
      }

      public JComponent component() {
         return this.jcomp;
      }

      protected Color getFrameHighlight() {
         return this.frameHighlight;
      }

      protected Color getFrameColor() {
         return this.frameColor;
      }

      protected Color getFrameShadow() {
         return this.frameShadow;
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(5, 5, 5, 5);
         return var2;
      }

      protected boolean drawTopBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(var3, var4, var5, 5);
         if (!var2.getClipBounds().intersects(var7)) {
            return false;
         } else {
            int var8 = var5 - 1;
            byte var9 = 4;
            var2.setColor(this.frameColor);
            var2.drawLine(var3, var4 + 2, var8 - 2, var4 + 2);
            var2.drawLine(var3, var4 + 3, var8 - 2, var4 + 3);
            var2.drawLine(var3, var4 + 4, var8 - 2, var4 + 4);
            var2.setColor(this.frameHighlight);
            var2.drawLine(var3, var4, var8, var4);
            var2.drawLine(var3, var4 + 1, var8, var4 + 1);
            var2.drawLine(var3, var4 + 2, var3, var4 + 4);
            var2.drawLine(var3 + 1, var4 + 2, var3 + 1, var4 + 4);
            var2.setColor(this.frameShadow);
            var2.drawLine(var3 + 4, var4 + 4, var8 - 4, var4 + 4);
            var2.drawLine(var8, var4 + 1, var8, var9);
            var2.drawLine(var8 - 1, var4 + 2, var8 - 1, var9);
            return true;
         }
      }

      protected boolean drawLeftBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(0, 0, this.getBorderInsets(var1).left, var6);
         if (!var2.getClipBounds().intersects(var7)) {
            return false;
         } else {
            byte var8 = 5;
            var2.setColor(this.frameHighlight);
            var2.drawLine(var3, var8, var3, var6 - 1);
            var2.drawLine(var3 + 1, var8, var3 + 1, var6 - 2);
            var2.setColor(this.frameColor);
            var2.fillRect(var3 + 2, var8, var3 + 2, var6 - 3);
            var2.setColor(this.frameShadow);
            var2.drawLine(var3 + 4, var8, var3 + 4, var6 - 5);
            return true;
         }
      }

      protected boolean drawRightBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(var5 - this.getBorderInsets(var1).right, 0, this.getBorderInsets(var1).right, var6);
         if (!var2.getClipBounds().intersects(var7)) {
            return false;
         } else {
            int var8 = var5 - this.getBorderInsets(var1).right;
            byte var9 = 5;
            var2.setColor(this.frameColor);
            var2.fillRect(var8 + 1, var9, 2, var6 - 1);
            var2.setColor(this.frameShadow);
            var2.fillRect(var8 + 3, var9, 2, var6 - 1);
            var2.setColor(this.frameHighlight);
            var2.drawLine(var8, var9, var8, var6 - 1);
            return true;
         }
      }

      protected boolean drawBottomBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(0, var6 - this.getBorderInsets(var1).bottom, var5, this.getBorderInsets(var1).bottom);
         if (!var2.getClipBounds().intersects(var7)) {
            return false;
         } else {
            int var9 = var6 - this.getBorderInsets(var1).bottom;
            var2.setColor(this.frameShadow);
            var2.drawLine(var3 + 1, var6 - 1, var5 - 1, var6 - 1);
            var2.drawLine(var3 + 2, var6 - 2, var5 - 2, var6 - 2);
            var2.setColor(this.frameColor);
            var2.fillRect(var3 + 2, var9 + 1, var5 - 4, 2);
            var2.setColor(this.frameHighlight);
            var2.drawLine(var3 + 5, var9, var5 - 5, var9);
            return true;
         }
      }

      protected boolean isActiveFrame() {
         return this.jcomp.hasFocus();
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (this.isActiveFrame()) {
            this.frameColor = UIManager.getColor("activeCaptionBorder");
         } else {
            this.frameColor = UIManager.getColor("inactiveCaptionBorder");
         }

         this.frameHighlight = this.frameColor.brighter();
         this.frameShadow = this.frameColor.darker().darker();
         this.drawTopBorder(var1, var2, var3, var4, var5, var6);
         this.drawLeftBorder(var1, var2, var3, var4, var5, var6);
         this.drawRightBorder(var1, var2, var3, var4, var5, var6);
         this.drawBottomBorder(var1, var2, var3, var4, var5, var6);
      }
   }

   public static class MenuBarBorder extends MotifBorders.ButtonBorder {
      public MenuBarBorder(Color var1, Color var2, Color var3, Color var4) {
         super(var1, var2, var3, var4);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JMenuBar) {
            JMenuBar var7 = (JMenuBar)var1;
            if (var7.isBorderPainted()) {
               Dimension var8 = var7.getSize();
               MotifBorders.drawBezel(var2, var3, var4, var8.width, var8.height, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
            }

         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(6, 6, 6, 6);
         return var2;
      }
   }

   public static class ToggleButtonBorder extends MotifBorders.ButtonBorder {
      public ToggleButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         super(var1, var2, var3, var4);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof AbstractButton) {
            AbstractButton var7 = (AbstractButton)var1;
            ButtonModel var8 = var7.getModel();
            if ((!var8.isArmed() || !var8.isPressed()) && !var8.isSelected()) {
               MotifBorders.drawBezel(var2, var3, var4, var5, var6, false, var7.isFocusPainted() && var7.hasFocus(), this.shadow, this.highlight, this.darkShadow, this.focus);
            } else {
               MotifBorders.drawBezel(var2, var3, var4, var5, var6, var8.isPressed() || var8.isSelected(), var7.isFocusPainted() && var7.hasFocus(), this.shadow, this.highlight, this.darkShadow, this.focus);
            }
         } else {
            MotifBorders.drawBezel(var2, var3, var4, var5, var6, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 3, 3);
         return var2;
      }
   }

   public static class ButtonBorder extends AbstractBorder implements UIResource {
      protected Color focus = UIManager.getColor("activeCaptionBorder");
      protected Color shadow = UIManager.getColor("Button.shadow");
      protected Color highlight = UIManager.getColor("Button.light");
      protected Color darkShadow;

      public ButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         this.shadow = var1;
         this.highlight = var2;
         this.darkShadow = var3;
         this.focus = var4;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         boolean var7 = false;
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         if (var1 instanceof AbstractButton) {
            AbstractButton var11 = (AbstractButton)var1;
            ButtonModel var12 = var11.getModel();
            var7 = var12.isArmed() && var12.isPressed();
            var8 = var12.isArmed() && var7 || var11.isFocusPainted() && var11.hasFocus();
            if (var11 instanceof JButton) {
               var9 = ((JButton)var11).isDefaultCapable();
               var10 = ((JButton)var11).isDefaultButton();
            }
         }

         int var15 = var3 + 1;
         int var16 = var4 + 1;
         int var13 = var3 + var5 - 2;
         int var14 = var4 + var6 - 2;
         if (var9) {
            if (var10) {
               var2.setColor(this.shadow);
               var2.drawLine(var3 + 3, var4 + 3, var3 + 3, var4 + var6 - 4);
               var2.drawLine(var3 + 3, var4 + 3, var3 + var5 - 4, var4 + 3);
               var2.setColor(this.highlight);
               var2.drawLine(var3 + 4, var4 + var6 - 4, var3 + var5 - 4, var4 + var6 - 4);
               var2.drawLine(var3 + var5 - 4, var4 + 3, var3 + var5 - 4, var4 + var6 - 4);
            }

            var15 += 6;
            var16 += 6;
            var13 -= 6;
            var14 -= 6;
         }

         if (var8) {
            var2.setColor(this.focus);
            if (var10) {
               var2.drawRect(var3, var4, var5 - 1, var6 - 1);
            } else {
               var2.drawRect(var15 - 1, var16 - 1, var13 - var15 + 2, var14 - var16 + 2);
            }
         }

         var2.setColor(var7 ? this.shadow : this.highlight);
         var2.drawLine(var15, var16, var13, var16);
         var2.drawLine(var15, var16, var15, var14);
         var2.setColor(var7 ? this.highlight : this.shadow);
         var2.drawLine(var13, var16 + 1, var13, var14);
         var2.drawLine(var15 + 1, var14, var13, var14);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         int var3 = var1 instanceof JButton && ((JButton)var1).isDefaultCapable() ? 8 : 2;
         var2.set(var3, var3, var3, var3);
         return var2;
      }
   }

   public static class FocusBorder extends AbstractBorder implements UIResource {
      private Color focus;
      private Color control;

      public FocusBorder(Color var1, Color var2) {
         this.control = var1;
         this.focus = var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1.hasFocus()) {
            var2.setColor(this.focus);
            var2.drawRect(var3, var4, var5 - 1, var6 - 1);
         } else {
            var2.setColor(this.control);
            var2.drawRect(var3, var4, var5 - 1, var6 - 1);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 1, 1);
         return var2;
      }
   }

   public static class BevelBorder extends AbstractBorder implements UIResource {
      private Color darkShadow = UIManager.getColor("controlShadow");
      private Color lightShadow = UIManager.getColor("controlLtHighlight");
      private boolean isRaised;

      public BevelBorder(boolean var1, Color var2, Color var3) {
         this.isRaised = var1;
         this.darkShadow = var2;
         this.lightShadow = var3;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.setColor(this.isRaised ? this.lightShadow : this.darkShadow);
         var2.drawLine(var3, var4, var3 + var5 - 1, var4);
         var2.drawLine(var3, var4 + var6 - 1, var3, var4 + 1);
         var2.setColor(this.isRaised ? this.darkShadow : this.lightShadow);
         var2.drawLine(var3 + 1, var4 + var6 - 1, var3 + var5 - 1, var4 + var6 - 1);
         var2.drawLine(var3 + var5 - 1, var4 + var6 - 1, var3 + var5 - 1, var4 + 1);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 1, 1);
         return var2;
      }

      public boolean isOpaque(Component var1) {
         return true;
      }
   }
}
