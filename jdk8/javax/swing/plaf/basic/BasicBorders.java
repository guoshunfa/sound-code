package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.swing.SwingUtilities2;

public class BasicBorders {
   public static Border getButtonBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ButtonBorder(var0.getColor("Button.shadow"), var0.getColor("Button.darkShadow"), var0.getColor("Button.light"), var0.getColor("Button.highlight")), new BasicBorders.MarginBorder());
      return var1;
   }

   public static Border getRadioButtonBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new BasicBorders.RadioButtonBorder(var0.getColor("RadioButton.shadow"), var0.getColor("RadioButton.darkShadow"), var0.getColor("RadioButton.light"), var0.getColor("RadioButton.highlight")), new BasicBorders.MarginBorder());
      return var1;
   }

   public static Border getToggleButtonBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ToggleButtonBorder(var0.getColor("ToggleButton.shadow"), var0.getColor("ToggleButton.darkShadow"), var0.getColor("ToggleButton.light"), var0.getColor("ToggleButton.highlight")), new BasicBorders.MarginBorder());
      return var1;
   }

   public static Border getMenuBarBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BasicBorders.MenuBarBorder var1 = new BasicBorders.MenuBarBorder(var0.getColor("MenuBar.shadow"), var0.getColor("MenuBar.highlight"));
      return var1;
   }

   public static Border getSplitPaneBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BasicBorders.SplitPaneBorder var1 = new BasicBorders.SplitPaneBorder(var0.getColor("SplitPane.highlight"), var0.getColor("SplitPane.darkShadow"));
      return var1;
   }

   public static Border getSplitPaneDividerBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BasicBorders.SplitPaneDividerBorder var1 = new BasicBorders.SplitPaneDividerBorder(var0.getColor("SplitPane.highlight"), var0.getColor("SplitPane.darkShadow"));
      return var1;
   }

   public static Border getTextFieldBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BasicBorders.FieldBorder var1 = new BasicBorders.FieldBorder(var0.getColor("TextField.shadow"), var0.getColor("TextField.darkShadow"), var0.getColor("TextField.light"), var0.getColor("TextField.highlight"));
      return var1;
   }

   public static Border getProgressBarBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.LineBorderUIResource var1 = new BorderUIResource.LineBorderUIResource(Color.green, 2);
      return var1;
   }

   public static Border getInternalFrameBorder() {
      UIDefaults var0 = UIManager.getLookAndFeelDefaults();
      BorderUIResource.CompoundBorderUIResource var1 = new BorderUIResource.CompoundBorderUIResource(new BevelBorder(0, var0.getColor("InternalFrame.borderLight"), var0.getColor("InternalFrame.borderHighlight"), var0.getColor("InternalFrame.borderDarkShadow"), var0.getColor("InternalFrame.borderShadow")), BorderFactory.createLineBorder(var0.getColor("InternalFrame.borderColor"), 1));
      return var1;
   }

   public static class SplitPaneBorder implements Border, UIResource {
      protected Color highlight;
      protected Color shadow;

      public SplitPaneBorder(Color var1, Color var2) {
         this.highlight = var1;
         this.shadow = var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JSplitPane) {
            JSplitPane var9 = (JSplitPane)var1;
            Component var7 = var9.getLeftComponent();
            var2.setColor(var1.getBackground());
            var2.drawRect(var3, var4, var5 - 1, var6 - 1);
            Rectangle var8;
            int var10;
            int var11;
            if (var9.getOrientation() == 1) {
               if (var7 != null) {
                  var8 = var7.getBounds();
                  var2.setColor(this.shadow);
                  var2.drawLine(0, 0, var8.width + 1, 0);
                  var2.drawLine(0, 1, 0, var8.height + 1);
                  var2.setColor(this.highlight);
                  var2.drawLine(0, var8.height + 1, var8.width + 1, var8.height + 1);
               }

               var7 = var9.getRightComponent();
               if (var7 != null) {
                  var8 = var7.getBounds();
                  var10 = var8.x + var8.width;
                  var11 = var8.y + var8.height;
                  var2.setColor(this.shadow);
                  var2.drawLine(var8.x - 1, 0, var10, 0);
                  var2.setColor(this.highlight);
                  var2.drawLine(var8.x - 1, var11, var10, var11);
                  var2.drawLine(var10, 0, var10, var11 + 1);
               }
            } else {
               if (var7 != null) {
                  var8 = var7.getBounds();
                  var2.setColor(this.shadow);
                  var2.drawLine(0, 0, var8.width + 1, 0);
                  var2.drawLine(0, 1, 0, var8.height);
                  var2.setColor(this.highlight);
                  var2.drawLine(1 + var8.width, 0, 1 + var8.width, var8.height + 1);
                  var2.drawLine(0, var8.height + 1, 0, var8.height + 1);
               }

               var7 = var9.getRightComponent();
               if (var7 != null) {
                  var8 = var7.getBounds();
                  var10 = var8.x + var8.width;
                  var11 = var8.y + var8.height;
                  var2.setColor(this.shadow);
                  var2.drawLine(0, var8.y - 1, 0, var11);
                  var2.drawLine(var10, var8.y - 1, var10, var8.y - 1);
                  var2.setColor(this.highlight);
                  var2.drawLine(0, var11, var8.width + 1, var11);
                  var2.drawLine(var10, var8.y, var10, var11);
               }
            }

         }
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(1, 1, 1, 1);
      }

      public boolean isBorderOpaque() {
         return true;
      }
   }

   static class SplitPaneDividerBorder implements Border, UIResource {
      Color highlight;
      Color shadow;

      SplitPaneDividerBorder(Color var1, Color var2) {
         this.highlight = var1;
         this.shadow = var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof BasicSplitPaneDivider) {
            JSplitPane var9 = ((BasicSplitPaneDivider)var1).getBasicSplitPaneUI().getSplitPane();
            Dimension var10 = var1.getSize();
            Component var7 = var9.getLeftComponent();
            var2.setColor(var1.getBackground());
            var2.drawRect(var3, var4, var5 - 1, var6 - 1);
            if (var9.getOrientation() == 1) {
               if (var7 != null) {
                  var2.setColor(this.highlight);
                  var2.drawLine(0, 0, 0, var10.height);
               }

               var7 = var9.getRightComponent();
               if (var7 != null) {
                  var2.setColor(this.shadow);
                  var2.drawLine(var10.width - 1, 0, var10.width - 1, var10.height);
               }
            } else {
               if (var7 != null) {
                  var2.setColor(this.highlight);
                  var2.drawLine(0, 0, var10.width, 0);
               }

               var7 = var9.getRightComponent();
               if (var7 != null) {
                  var2.setColor(this.shadow);
                  var2.drawLine(0, var10.height - 1, var10.width, var10.height - 1);
               }
            }

         }
      }

      public Insets getBorderInsets(Component var1) {
         Insets var2 = new Insets(0, 0, 0, 0);
         if (var1 instanceof BasicSplitPaneDivider) {
            BasicSplitPaneUI var3 = ((BasicSplitPaneDivider)var1).getBasicSplitPaneUI();
            if (var3 != null) {
               JSplitPane var4 = var3.getSplitPane();
               if (var4 != null) {
                  if (var4.getOrientation() == 1) {
                     var2.top = var2.bottom = 0;
                     var2.left = var2.right = 1;
                     return var2;
                  }

                  var2.top = var2.bottom = 1;
                  var2.left = var2.right = 0;
                  return var2;
               }
            }
         }

         var2.top = var2.bottom = var2.left = var2.right = 1;
         return var2;
      }

      public boolean isBorderOpaque() {
         return true;
      }
   }

   public static class FieldBorder extends AbstractBorder implements UIResource {
      protected Color shadow;
      protected Color darkShadow;
      protected Color highlight;
      protected Color lightHighlight;

      public FieldBorder(Color var1, Color var2, Color var3, Color var4) {
         this.shadow = var1;
         this.highlight = var3;
         this.darkShadow = var2;
         this.lightHighlight = var4;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         BasicGraphicsUtils.drawEtchedRect(var2, var3, var4, var5, var6, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = null;
         if (var1 instanceof JTextComponent) {
            var3 = ((JTextComponent)var1).getMargin();
         }

         var2.top = var3 != null ? 2 + var3.top : 2;
         var2.left = var3 != null ? 2 + var3.left : 2;
         var2.bottom = var3 != null ? 2 + var3.bottom : 2;
         var2.right = var3 != null ? 2 + var3.right : 2;
         return var2;
      }
   }

   public static class MarginBorder extends AbstractBorder implements UIResource {
      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = null;
         if (var1 instanceof AbstractButton) {
            AbstractButton var4 = (AbstractButton)var1;
            var3 = var4.getMargin();
         } else if (var1 instanceof JToolBar) {
            JToolBar var5 = (JToolBar)var1;
            var3 = var5.getMargin();
         } else if (var1 instanceof JTextComponent) {
            JTextComponent var6 = (JTextComponent)var1;
            var3 = var6.getMargin();
         }

         var2.top = var3 != null ? var3.top : 0;
         var2.left = var3 != null ? var3.left : 0;
         var2.bottom = var3 != null ? var3.bottom : 0;
         var2.right = var3 != null ? var3.right : 0;
         return var2;
      }
   }

   public static class MenuBarBorder extends AbstractBorder implements UIResource {
      private Color shadow;
      private Color highlight;

      public MenuBarBorder(Color var1, Color var2) {
         this.shadow = var1;
         this.highlight = var2;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Color var7 = var2.getColor();
         var2.translate(var3, var4);
         var2.setColor(this.shadow);
         SwingUtilities2.drawHLine(var2, 0, var5 - 1, var6 - 2);
         var2.setColor(this.highlight);
         SwingUtilities2.drawHLine(var2, 0, var5 - 1, var6 - 1);
         var2.translate(-var3, -var4);
         var2.setColor(var7);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(0, 0, 2, 0);
         return var2;
      }
   }

   public static class RadioButtonBorder extends BasicBorders.ButtonBorder {
      public RadioButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         super(var1, var2, var3, var4);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof AbstractButton) {
            AbstractButton var7 = (AbstractButton)var1;
            ButtonModel var8 = var7.getModel();
            if ((!var8.isArmed() || !var8.isPressed()) && !var8.isSelected()) {
               BasicGraphicsUtils.drawBezel(var2, var3, var4, var5, var6, false, var7.isFocusPainted() && var7.hasFocus(), this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
            } else {
               BasicGraphicsUtils.drawLoweredBezel(var2, var3, var4, var5, var6, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
            }
         } else {
            BasicGraphicsUtils.drawBezel(var2, var3, var4, var5, var6, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 2);
         return var2;
      }
   }

   public static class ToggleButtonBorder extends BasicBorders.ButtonBorder {
      public ToggleButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         super(var1, var2, var3, var4);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         BasicGraphicsUtils.drawBezel(var2, var3, var4, var5, var6, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 2);
         return var2;
      }
   }

   public static class ButtonBorder extends AbstractBorder implements UIResource {
      protected Color shadow;
      protected Color darkShadow;
      protected Color highlight;
      protected Color lightHighlight;

      public ButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         this.shadow = var1;
         this.darkShadow = var2;
         this.highlight = var3;
         this.lightHighlight = var4;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         boolean var7 = false;
         boolean var8 = false;
         if (var1 instanceof AbstractButton) {
            AbstractButton var9 = (AbstractButton)var1;
            ButtonModel var10 = var9.getModel();
            var7 = var10.isPressed() && var10.isArmed();
            if (var1 instanceof JButton) {
               var8 = ((JButton)var1).isDefaultButton();
            }
         }

         BasicGraphicsUtils.drawBezel(var2, var3, var4, var5, var6, var7, var8, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 3, 3, 3);
         return var2;
      }
   }

   static class RolloverMarginBorder extends EmptyBorder {
      public RolloverMarginBorder() {
         super(3, 3, 3, 3);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         Insets var3 = null;
         if (var1 instanceof AbstractButton) {
            var3 = ((AbstractButton)var1).getMargin();
         }

         if (var3 != null && !(var3 instanceof UIResource)) {
            var2.left = var3.left;
            var2.top = var3.top;
            var2.right = var3.right;
            var2.bottom = var3.bottom;
         } else {
            var2.left = this.left;
            var2.top = this.top;
            var2.right = this.right;
            var2.bottom = this.bottom;
         }

         return var2;
      }
   }

   public static class RolloverButtonBorder extends BasicBorders.ButtonBorder {
      public RolloverButtonBorder(Color var1, Color var2, Color var3, Color var4) {
         super(var1, var2, var3, var4);
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         AbstractButton var7 = (AbstractButton)var1;
         ButtonModel var8 = var7.getModel();
         Color var9 = this.shadow;
         Container var10 = var7.getParent();
         if (var10 != null && var10.getBackground().equals(this.shadow)) {
            var9 = this.darkShadow;
         }

         if (var8.isRollover() && (!var8.isPressed() || var8.isArmed()) || var8.isSelected()) {
            Color var11 = var2.getColor();
            var2.translate(var3, var4);
            if ((!var8.isPressed() || !var8.isArmed()) && !var8.isSelected()) {
               var2.setColor(this.lightHighlight);
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
               var2.setColor(var9);
               var2.drawLine(var5 - 1, 0, var5 - 1, var6 - 1);
               var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
            } else {
               var2.setColor(var9);
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
               var2.setColor(this.lightHighlight);
               var2.drawLine(var5 - 1, 0, var5 - 1, var6 - 1);
               var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
            }

            var2.translate(-var3, -var4);
            var2.setColor(var11);
         }

      }
   }
}
