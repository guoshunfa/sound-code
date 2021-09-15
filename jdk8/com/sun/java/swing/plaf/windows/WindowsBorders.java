package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class WindowsBorders {
   public static Border getProgressBarBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new WindowsBorders.ProgressBarBorder(var0.getColor("ProgressBar.shadow"), var0.getColor("ProgressBar.highlight")), new EmptyBorder(1, 1, 1, 1));
      return var1;
   }

   public static Border getToolBarBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      WindowsBorders.ToolBarBorder var1 = new WindowsBorders.ToolBarBorder(var0.getColor("ToolBar.shadow"), var0.getColor("ToolBar.highlight"));
      return var1;
   }

   public static Border getFocusCellHighlightBorder() {
      return new WindowsBorders.ComplementDashedBorder();
   }

   public static Border getTableHeaderBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ButtonBorder(var0.getColor("Table.shadow"), var0.getColor("Table.darkShadow"), var0.getColor("Table.light"), var0.getColor("Table.highlight")), new BasicBorders.MarginBorder());
      return var1;
   }

   public static Border getInternalFrameBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createBevelBorder(0, var0.getColor("InternalFrame.borderColor"), var0.getColor("InternalFrame.borderHighlight"), var0.getColor("InternalFrame.borderDarkShadow"), var0.getColor("InternalFrame.borderShadow")), new WindowsBorders.InternalFrameLineBorder(var0.getColor("InternalFrame.activeBorderColor"), var0.getColor("InternalFrame.inactiveBorderColor"), var0.getInt("InternalFrame.borderWidth")));
      return var1;
   }

   public static class InternalFrameLineBorder extends LineBorder implements UIResource {
      protected Color activeColor;
      protected Color inactiveColor;

      public InternalFrameLineBorder(Color var1, Color var2, int var3) {
         super(var1, var3);
         this.activeColor = var1;
         this.inactiveColor = var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         JInternalFrame var7 = null;
         if (var1 instanceof JInternalFrame) {
            var7 = (JInternalFrame)var1;
         } else {
            if (!(var1 instanceof JInternalFrame.JDesktopIcon)) {
               return;
            }

            var7 = ((JInternalFrame.JDesktopIcon)var1).getInternalFrame();
         }

         if (var7.isSelected()) {
            this.lineColor = this.activeColor;
            super.paintBorder(var1, var2, var3, var4, var5, var6);
         } else {
            this.lineColor = this.inactiveColor;
            super.paintBorder(var1, var2, var3, var4, var5, var6);
         }

      }
   }

   static class ComplementDashedBorder extends LineBorder implements UIResource {
      private Color origColor;
      private Color paintColor;

      public ComplementDashedBorder() {
         super((Color)null);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Color var7 = var1.getBackground();
         if (this.origColor != var7) {
            this.origColor = var7;
            this.paintColor = new Color(~this.origColor.getRGB());
         }

         var2.setColor(this.paintColor);
         BasicGraphicsUtils.drawDashedRect(var2, var3, var4, var5, var6);
      }
   }

   public static class DashedBorder extends LineBorder implements UIResource {
      public DashedBorder(Color var1) {
         super(var1);
      }

      public DashedBorder(Color var1, int var2) {
         super(var1, var2);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Color var7 = var2.getColor();
         var2.setColor(this.lineColor);

         for(int var8 = 0; var8 < this.thickness; ++var8) {
            BasicGraphicsUtils.drawDashedRect(var2, var3 + var8, var4 + var8, var5 - var8 - var8, var6 - var8 - var8);
         }

         var2.setColor(var7);
      }
   }

   public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
      protected Color shadow;
      protected Color highlight;

      public ToolBarBorder(Color var1, Color var2) {
         this.highlight = var2;
         this.shadow = var1;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JToolBar) {
            var2.translate(var3, var4);
            XPStyle var7 = XPStyle.getXP();
            if (var7 != null) {
               Border var8 = var7.getBorder(var1, TMSchema.Part.TP_TOOLBAR);
               if (var8 != null) {
                  var8.paintBorder(var1, var2, 0, 0, var5, var6);
               }
            }

            if (((JToolBar)var1).isFloatable()) {
               boolean var15 = ((JToolBar)var1).getOrientation() == 1;
               if (var7 != null) {
                  TMSchema.Part var9 = var15 ? TMSchema.Part.RP_GRIPPERVERT : TMSchema.Part.RP_GRIPPER;
                  XPStyle.Skin var10 = var7.getSkin(var1, var9);
                  int var11;
                  byte var12;
                  int var13;
                  int var14;
                  if (var15) {
                     var11 = 0;
                     var12 = 2;
                     var13 = var5 - 1;
                     var14 = var10.getHeight();
                  } else {
                     var13 = var10.getWidth();
                     var14 = var6 - 1;
                     var11 = var1.getComponentOrientation().isLeftToRight() ? 2 : var5 - var13 - 2;
                     var12 = 0;
                  }

                  var10.paintSkin(var2, var11, var12, var13, var14, TMSchema.State.NORMAL);
               } else if (!var15) {
                  if (var1.getComponentOrientation().isLeftToRight()) {
                     var2.setColor(this.shadow);
                     var2.drawLine(4, 3, 4, var6 - 4);
                     var2.drawLine(4, var6 - 4, 2, var6 - 4);
                     var2.setColor(this.highlight);
                     var2.drawLine(2, 3, 3, 3);
                     var2.drawLine(2, 3, 2, var6 - 5);
                  } else {
                     var2.setColor(this.shadow);
                     var2.drawLine(var5 - 3, 3, var5 - 3, var6 - 4);
                     var2.drawLine(var5 - 4, var6 - 4, var5 - 4, var6 - 4);
                     var2.setColor(this.highlight);
                     var2.drawLine(var5 - 5, 3, var5 - 4, 3);
                     var2.drawLine(var5 - 5, 3, var5 - 5, var6 - 5);
                  }
               } else {
                  var2.setColor(this.shadow);
                  var2.drawLine(3, 4, var5 - 4, 4);
                  var2.drawLine(var5 - 4, 2, var5 - 4, 4);
                  var2.setColor(this.highlight);
                  var2.drawLine(3, 2, var5 - 4, 2);
                  var2.drawLine(3, 2, 3, 3);
               }
            }

            var2.translate(-var3, -var4);
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 1, 1);
         if (!(var1 instanceof JToolBar)) {
            return var2;
         } else {
            if (((JToolBar)var1).isFloatable()) {
               int var3 = XPStyle.getXP() != null ? 12 : 9;
               if (((JToolBar)var1).getOrientation() == 0) {
                  if (var1.getComponentOrientation().isLeftToRight()) {
                     var2.left = var3;
                  } else {
                     var2.right = var3;
                  }
               } else {
                  var2.top = var3;
               }
            }

            return var2;
         }
      }
   }

   public static class ProgressBarBorder extends AbstractBorder implements UIResource {
      protected Color shadow;
      protected Color highlight;

      public ProgressBarBorder(Color var1, Color var2) {
         this.highlight = var2;
         this.shadow = var1;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.setColor(this.shadow);
         var2.drawLine(var3, var4, var5 - 1, var4);
         var2.drawLine(var3, var4, var3, var6 - 1);
         var2.setColor(this.highlight);
         var2.drawLine(var3, var6 - 1, var5 - 1, var6 - 1);
         var2.drawLine(var5 - 1, var4, var5 - 1, var6 - 1);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 1, 1);
         return var2;
      }
   }
}
