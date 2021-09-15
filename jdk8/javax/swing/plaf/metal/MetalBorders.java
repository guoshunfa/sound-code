package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class MetalBorders {
   static Object NO_BUTTON_ROLLOVER = new StringUIClientPropertyKey("NoButtonRollover");
   private static Border buttonBorder;
   private static Border textBorder;
   private static Border textFieldBorder;
   private static Border toggleButtonBorder;

   public static Border getButtonBorder() {
      if (buttonBorder == null) {
         buttonBorder = new BorderUIResource.CompoundBorderUIResource(new MetalBorders.ButtonBorder(), new BasicBorders.MarginBorder());
      }

      return buttonBorder;
   }

   public static Border getTextBorder() {
      if (textBorder == null) {
         textBorder = new BorderUIResource.CompoundBorderUIResource(new MetalBorders.Flush3DBorder(), new BasicBorders.MarginBorder());
      }

      return textBorder;
   }

   public static Border getTextFieldBorder() {
      if (textFieldBorder == null) {
         textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new MetalBorders.TextFieldBorder(), new BasicBorders.MarginBorder());
      }

      return textFieldBorder;
   }

   public static Border getToggleButtonBorder() {
      if (toggleButtonBorder == null) {
         toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(new MetalBorders.ToggleButtonBorder(), new BasicBorders.MarginBorder());
      }

      return toggleButtonBorder;
   }

   public static Border getDesktopIconBorder() {
      return new BorderUIResource.CompoundBorderUIResource(new LineBorder(MetalLookAndFeel.getControlDarkShadow(), 1), new MatteBorder(2, 2, 1, 2, MetalLookAndFeel.getControl()));
   }

   static Border getToolBarRolloverBorder() {
      return MetalLookAndFeel.usingOcean() ? new CompoundBorder(new MetalBorders.ButtonBorder(), new MetalBorders.RolloverMarginBorder()) : new CompoundBorder(new MetalBorders.RolloverButtonBorder(), new MetalBorders.RolloverMarginBorder());
   }

   static Border getToolBarNonrolloverBorder() {
      if (MetalLookAndFeel.usingOcean()) {
         new CompoundBorder(new MetalBorders.ButtonBorder(), new MetalBorders.RolloverMarginBorder());
      }

      return new CompoundBorder(new MetalBorders.ButtonBorder(), new MetalBorders.RolloverMarginBorder());
   }

   public static class TableHeaderBorder extends AbstractBorder {
      protected Insets editorBorderInsets = new Insets(2, 2, 2, 0);

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getControlDarkShadow());
         var2.drawLine(var5 - 1, 0, var5 - 1, var6 - 1);
         var2.drawLine(1, var6 - 1, var5 - 1, var6 - 1);
         var2.setColor(MetalLookAndFeel.getControlHighlight());
         var2.drawLine(0, 0, var5 - 2, 0);
         var2.drawLine(0, 0, 0, var6 - 2);
         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 0);
         return var2;
      }
   }

   public static class ToggleButtonBorder extends MetalBorders.ButtonBorder {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         AbstractButton var7 = (AbstractButton)var1;
         ButtonModel var8 = var7.getModel();
         if (MetalLookAndFeel.usingOcean()) {
            if (!var8.isArmed() && var7.isEnabled()) {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
            } else {
               super.paintBorder(var1, var2, var3, var4, var5, var6);
            }

         } else {
            if (!var1.isEnabled()) {
               MetalUtils.drawDisabledBorder(var2, var3, var4, var5 - 1, var6 - 1);
            } else if (var8.isPressed() && var8.isArmed()) {
               MetalUtils.drawPressed3DBorder(var2, var3, var4, var5, var6);
            } else if (var8.isSelected()) {
               MetalUtils.drawDark3DBorder(var2, var3, var4, var5, var6);
            } else {
               MetalUtils.drawFlush3DBorder(var2, var3, var4, var5, var6);
            }

         }
      }
   }

   public static class ScrollPaneBorder extends AbstractBorder implements UIResource {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JScrollPane) {
            JScrollPane var7 = (JScrollPane)var1;
            JViewport var8 = var7.getColumnHeader();
            int var9 = 0;
            if (var8 != null) {
               var9 = var8.getHeight();
            }

            JViewport var10 = var7.getRowHeader();
            int var11 = 0;
            if (var10 != null) {
               var11 = var10.getWidth();
            }

            var2.translate(var3, var4);
            var2.setColor(MetalLookAndFeel.getControlDarkShadow());
            var2.drawRect(0, 0, var5 - 2, var6 - 2);
            var2.setColor(MetalLookAndFeel.getControlHighlight());
            var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 1);
            var2.drawLine(1, var6 - 1, var5 - 1, var6 - 1);
            var2.setColor(MetalLookAndFeel.getControl());
            var2.drawLine(var5 - 2, 2 + var9, var5 - 2, 2 + var9);
            var2.drawLine(1 + var11, var6 - 2, 1 + var11, var6 - 2);
            var2.translate(-var3, -var4);
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 2, 2);
         return var2;
      }
   }

   public static class TextFieldBorder extends MetalBorders.Flush3DBorder {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (!(var1 instanceof JTextComponent)) {
            if (var1.isEnabled()) {
               MetalUtils.drawFlush3DBorder(var2, var3, var4, var5, var6);
            } else {
               MetalUtils.drawDisabledBorder(var2, var3, var4, var5, var6);
            }

         } else {
            if (var1.isEnabled() && ((JTextComponent)var1).isEditable()) {
               MetalUtils.drawFlush3DBorder(var2, var3, var4, var5, var6);
            } else {
               MetalUtils.drawDisabledBorder(var2, var3, var4, var5, var6);
            }

         }
      }
   }

   public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
      protected MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.getColor("ToolBar.background"));

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JToolBar) {
            var2.translate(var3, var4);
            if (((JToolBar)var1).isFloatable()) {
               if (((JToolBar)var1).getOrientation() == 0) {
                  int var7 = MetalLookAndFeel.usingOcean() ? -1 : 0;
                  this.bumps.setBumpArea(10, var6 - 4);
                  if (MetalUtils.isLeftToRight(var1)) {
                     this.bumps.paintIcon(var1, var2, 2, 2 + var7);
                  } else {
                     this.bumps.paintIcon(var1, var2, var5 - 12, 2 + var7);
                  }
               } else {
                  this.bumps.setBumpArea(var5 - 4, 10);
                  this.bumps.paintIcon(var1, var2, 2, 2);
               }
            }

            if (((JToolBar)var1).getOrientation() == 0 && MetalLookAndFeel.usingOcean()) {
               var2.setColor(MetalLookAndFeel.getControl());
               var2.drawLine(0, var6 - 2, var5, var6 - 2);
               var2.setColor(UIManager.getColor("ToolBar.borderColor"));
               var2.drawLine(0, var6 - 1, var5, var6 - 1);
            }

            var2.translate(-var3, -var4);
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         if (MetalLookAndFeel.usingOcean()) {
            var2.set(1, 2, 3, 2);
         } else {
            var2.top = var2.left = var2.bottom = var2.right = 2;
         }

         if (!(var1 instanceof JToolBar)) {
            return var2;
         } else {
            if (((JToolBar)var1).isFloatable()) {
               if (((JToolBar)var1).getOrientation() == 0) {
                  if (var1.getComponentOrientation().isLeftToRight()) {
                     var2.left = 16;
                  } else {
                     var2.right = 16;
                  }
               } else {
                  var2.top = 16;
               }
            }

            Insets var3 = ((JToolBar)var1).getMargin();
            if (var3 != null) {
               var2.left += var3.left;
               var2.top += var3.top;
               var2.right += var3.right;
               var2.bottom += var3.bottom;
            }

            return var2;
         }
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

   public static class RolloverButtonBorder extends MetalBorders.ButtonBorder {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         AbstractButton var7 = (AbstractButton)var1;
         ButtonModel var8 = var7.getModel();
         if (var8.isRollover() && (!var8.isPressed() || var8.isArmed())) {
            super.paintBorder(var1, var2, var3, var4, var5, var6);
         }

      }
   }

   public static class PopupMenuBorder extends AbstractBorder implements UIResource {
      protected static Insets borderInsets = new Insets(3, 1, 2, 1);

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawRect(0, 0, var5 - 1, var6 - 1);
         var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
         var2.drawLine(1, 1, var5 - 2, 1);
         var2.drawLine(1, 2, 1, 2);
         var2.drawLine(1, var6 - 2, 1, var6 - 2);
         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(3, 1, 2, 1);
         return var2;
      }
   }

   public static class MenuItemBorder extends AbstractBorder implements UIResource {
      protected static Insets borderInsets = new Insets(2, 2, 2, 2);

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JMenuItem) {
            JMenuItem var7 = (JMenuItem)var1;
            ButtonModel var8 = var7.getModel();
            var2.translate(var3, var4);
            if (var1.getParent() instanceof JMenuBar) {
               if (var8.isArmed() || var8.isSelected()) {
                  var2.setColor(MetalLookAndFeel.getControlDarkShadow());
                  var2.drawLine(0, 0, var5 - 2, 0);
                  var2.drawLine(0, 0, 0, var6 - 1);
                  var2.drawLine(var5 - 2, 2, var5 - 2, var6 - 1);
                  var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
                  var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 1);
                  var2.setColor(MetalLookAndFeel.getMenuBackground());
                  var2.drawLine(var5 - 1, 0, var5 - 1, 0);
               }
            } else if (!var8.isArmed() && (!(var1 instanceof JMenu) || !var8.isSelected())) {
               var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
               var2.drawLine(0, 0, 0, var6 - 1);
            } else {
               var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
               var2.drawLine(0, 0, var5 - 1, 0);
               var2.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
               var2.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
            }

            var2.translate(-var3, -var4);
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 2);
         return var2;
      }
   }

   public static class MenuBarBorder extends AbstractBorder implements UIResource {
      protected static Insets borderInsets = new Insets(1, 0, 1, 0);

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         if (MetalLookAndFeel.usingOcean()) {
            if (var1 instanceof JMenuBar && !MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)var1)) {
               var2.setColor(MetalLookAndFeel.getControl());
               SwingUtilities2.drawHLine(var2, 0, var5 - 1, var6 - 2);
               var2.setColor(UIManager.getColor("MenuBar.borderColor"));
               SwingUtilities2.drawHLine(var2, 0, var5 - 1, var6 - 1);
            }
         } else {
            var2.setColor(MetalLookAndFeel.getControlShadow());
            SwingUtilities2.drawHLine(var2, 0, var5 - 1, var6 - 1);
         }

         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         if (MetalLookAndFeel.usingOcean()) {
            var2.set(0, 0, 2, 0);
         } else {
            var2.set(1, 0, 1, 0);
         }

         return var2;
      }
   }

   public static class OptionDialogBorder extends AbstractBorder implements UIResource {
      int titleHeight = 0;

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         int var7 = -1;
         Object var8;
         if (var1 instanceof JInternalFrame) {
            var8 = ((JInternalFrame)var1).getClientProperty("JInternalFrame.messageType");
            if (var8 instanceof Integer) {
               var7 = (Integer)var8;
            }
         }

         switch(var7) {
         case -1:
         case 1:
         default:
            var8 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            break;
         case 0:
            var8 = UIManager.getColor("OptionPane.errorDialog.border.background");
            break;
         case 2:
            var8 = UIManager.getColor("OptionPane.warningDialog.border.background");
            break;
         case 3:
            var8 = UIManager.getColor("OptionPane.questionDialog.border.background");
         }

         var2.setColor((Color)var8);
         var2.drawLine(1, 0, var5 - 2, 0);
         var2.drawLine(0, 1, 0, var6 - 2);
         var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 2);
         var2.drawLine(1, var6 - 1, var5 - 2, var6 - 1);

         for(int var9 = 1; var9 < 3; ++var9) {
            var2.drawRect(var9, var9, var5 - var9 * 2 - 1, var6 - var9 * 2 - 1);
         }

         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(3, 3, 3, 3);
         return var2;
      }
   }

   public static class PaletteBorder extends AbstractBorder implements UIResource {
      int titleHeight = 0;

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         var2.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
         var2.drawLine(0, 1, 0, var6 - 2);
         var2.drawLine(1, var6 - 1, var5 - 2, var6 - 1);
         var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 2);
         var2.drawLine(1, 0, var5 - 2, 0);
         var2.drawRect(1, 1, var5 - 3, var6 - 3);
         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(1, 1, 1, 1);
         return var2;
      }
   }

   static class WarningDialogBorder extends MetalBorders.DialogBorder implements UIResource {
      protected Color getActiveBackground() {
         return UIManager.getColor("OptionPane.warningDialog.border.background");
      }
   }

   static class QuestionDialogBorder extends MetalBorders.DialogBorder implements UIResource {
      protected Color getActiveBackground() {
         return UIManager.getColor("OptionPane.questionDialog.border.background");
      }
   }

   static class ErrorDialogBorder extends MetalBorders.DialogBorder implements UIResource {
      protected Color getActiveBackground() {
         return UIManager.getColor("OptionPane.errorDialog.border.background");
      }
   }

   static class DialogBorder extends AbstractBorder implements UIResource {
      private static final int corner = 14;

      protected Color getActiveBackground() {
         return MetalLookAndFeel.getPrimaryControlDarkShadow();
      }

      protected Color getActiveHighlight() {
         return MetalLookAndFeel.getPrimaryControlShadow();
      }

      protected Color getActiveShadow() {
         return MetalLookAndFeel.getPrimaryControlInfo();
      }

      protected Color getInactiveBackground() {
         return MetalLookAndFeel.getControlDarkShadow();
      }

      protected Color getInactiveHighlight() {
         return MetalLookAndFeel.getControlShadow();
      }

      protected Color getInactiveShadow() {
         return MetalLookAndFeel.getControlInfo();
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Window var10 = SwingUtilities.getWindowAncestor(var1);
         Color var7;
         Color var8;
         Color var9;
         if (var10 != null && var10.isActive()) {
            var7 = this.getActiveBackground();
            var8 = this.getActiveHighlight();
            var9 = this.getActiveShadow();
         } else {
            var7 = this.getInactiveBackground();
            var8 = this.getInactiveHighlight();
            var9 = this.getInactiveShadow();
         }

         var2.setColor(var7);
         var2.drawLine(var3 + 1, var4 + 0, var3 + var5 - 2, var4 + 0);
         var2.drawLine(var3 + 0, var4 + 1, var3 + 0, var4 + var6 - 2);
         var2.drawLine(var3 + var5 - 1, var4 + 1, var3 + var5 - 1, var4 + var6 - 2);
         var2.drawLine(var3 + 1, var4 + var6 - 1, var3 + var5 - 2, var4 + var6 - 1);

         for(int var11 = 1; var11 < 5; ++var11) {
            var2.drawRect(var3 + var11, var4 + var11, var5 - var11 * 2 - 1, var6 - var11 * 2 - 1);
         }

         if (var10 instanceof Dialog && ((Dialog)var10).isResizable()) {
            var2.setColor(var8);
            var2.drawLine(15, 3, var5 - 14, 3);
            var2.drawLine(3, 15, 3, var6 - 14);
            var2.drawLine(var5 - 2, 15, var5 - 2, var6 - 14);
            var2.drawLine(15, var6 - 2, var5 - 14, var6 - 2);
            var2.setColor(var9);
            var2.drawLine(14, 2, var5 - 14 - 1, 2);
            var2.drawLine(2, 14, 2, var6 - 14 - 1);
            var2.drawLine(var5 - 3, 14, var5 - 3, var6 - 14 - 1);
            var2.drawLine(14, var6 - 3, var5 - 14 - 1, var6 - 3);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(5, 5, 5, 5);
         return var2;
      }
   }

   static class FrameBorder extends AbstractBorder implements UIResource {
      private static final int corner = 14;

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Window var10 = SwingUtilities.getWindowAncestor(var1);
         ColorUIResource var7;
         ColorUIResource var8;
         ColorUIResource var9;
         if (var10 != null && var10.isActive()) {
            var7 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var9 = MetalLookAndFeel.getPrimaryControlInfo();
         } else {
            var7 = MetalLookAndFeel.getControlDarkShadow();
            var8 = MetalLookAndFeel.getControlShadow();
            var9 = MetalLookAndFeel.getControlInfo();
         }

         var2.setColor(var7);
         var2.drawLine(var3 + 1, var4 + 0, var3 + var5 - 2, var4 + 0);
         var2.drawLine(var3 + 0, var4 + 1, var3 + 0, var4 + var6 - 2);
         var2.drawLine(var3 + var5 - 1, var4 + 1, var3 + var5 - 1, var4 + var6 - 2);
         var2.drawLine(var3 + 1, var4 + var6 - 1, var3 + var5 - 2, var4 + var6 - 1);

         for(int var11 = 1; var11 < 5; ++var11) {
            var2.drawRect(var3 + var11, var4 + var11, var5 - var11 * 2 - 1, var6 - var11 * 2 - 1);
         }

         if (var10 instanceof Frame && ((Frame)var10).isResizable()) {
            var2.setColor(var8);
            var2.drawLine(15, 3, var5 - 14, 3);
            var2.drawLine(3, 15, 3, var6 - 14);
            var2.drawLine(var5 - 2, 15, var5 - 2, var6 - 14);
            var2.drawLine(15, var6 - 2, var5 - 14, var6 - 2);
            var2.setColor(var9);
            var2.drawLine(14, 2, var5 - 14 - 1, 2);
            var2.drawLine(2, 14, 2, var6 - 14 - 1);
            var2.drawLine(var5 - 3, 14, var5 - 3, var6 - 14 - 1);
            var2.drawLine(14, var6 - 3, var5 - 14 - 1, var6 - 3);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(5, 5, 5, 5);
         return var2;
      }
   }

   public static class InternalFrameBorder extends AbstractBorder implements UIResource {
      private static final int corner = 14;

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         ColorUIResource var7;
         ColorUIResource var8;
         ColorUIResource var9;
         if (var1 instanceof JInternalFrame && ((JInternalFrame)var1).isSelected()) {
            var7 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            var8 = MetalLookAndFeel.getPrimaryControlShadow();
            var9 = MetalLookAndFeel.getPrimaryControlInfo();
         } else {
            var7 = MetalLookAndFeel.getControlDarkShadow();
            var8 = MetalLookAndFeel.getControlShadow();
            var9 = MetalLookAndFeel.getControlInfo();
         }

         var2.setColor(var7);
         var2.drawLine(1, 0, var5 - 2, 0);
         var2.drawLine(0, 1, 0, var6 - 2);
         var2.drawLine(var5 - 1, 1, var5 - 1, var6 - 2);
         var2.drawLine(1, var6 - 1, var5 - 2, var6 - 1);

         for(int var10 = 1; var10 < 5; ++var10) {
            var2.drawRect(var3 + var10, var4 + var10, var5 - var10 * 2 - 1, var6 - var10 * 2 - 1);
         }

         if (var1 instanceof JInternalFrame && ((JInternalFrame)var1).isResizable()) {
            var2.setColor(var8);
            var2.drawLine(15, 3, var5 - 14, 3);
            var2.drawLine(3, 15, 3, var6 - 14);
            var2.drawLine(var5 - 2, 15, var5 - 2, var6 - 14);
            var2.drawLine(15, var6 - 2, var5 - 14, var6 - 2);
            var2.setColor(var9);
            var2.drawLine(14, 2, var5 - 14 - 1, 2);
            var2.drawLine(2, 14, 2, var6 - 14 - 1);
            var2.drawLine(var5 - 3, 14, var5 - 3, var6 - 14 - 1);
            var2.drawLine(14, var6 - 3, var5 - 14 - 1, var6 - 3);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(5, 5, 5, 5);
         return var2;
      }
   }

   public static class ButtonBorder extends AbstractBorder implements UIResource {
      protected static Insets borderInsets = new Insets(3, 3, 3, 3);

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof AbstractButton) {
            if (MetalLookAndFeel.usingOcean()) {
               this.paintOceanBorder(var1, var2, var3, var4, var5, var6);
            } else {
               AbstractButton var7 = (AbstractButton)var1;
               ButtonModel var8 = var7.getModel();
               if (var8.isEnabled()) {
                  boolean var9 = var8.isPressed() && var8.isArmed();
                  boolean var10 = var7 instanceof JButton && ((JButton)var7).isDefaultButton();
                  if (var9 && var10) {
                     MetalUtils.drawDefaultButtonPressedBorder(var2, var3, var4, var5, var6);
                  } else if (var9) {
                     MetalUtils.drawPressed3DBorder(var2, var3, var4, var5, var6);
                  } else if (var10) {
                     MetalUtils.drawDefaultButtonBorder(var2, var3, var4, var5, var6, false);
                  } else {
                     MetalUtils.drawButtonBorder(var2, var3, var4, var5, var6, false);
                  }
               } else {
                  MetalUtils.drawDisabledBorder(var2, var3, var4, var5 - 1, var6 - 1);
               }

            }
         }
      }

      private void paintOceanBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         AbstractButton var7 = (AbstractButton)var1;
         ButtonModel var8 = ((AbstractButton)var1).getModel();
         var2.translate(var3, var4);
         if (MetalUtils.isToolBarButton(var7)) {
            if (var8.isEnabled()) {
               if (var8.isPressed()) {
                  var2.setColor(MetalLookAndFeel.getWhite());
                  var2.fillRect(1, var6 - 1, var5 - 1, 1);
                  var2.fillRect(var5 - 1, 1, 1, var6 - 1);
                  var2.setColor(MetalLookAndFeel.getControlDarkShadow());
                  var2.drawRect(0, 0, var5 - 2, var6 - 2);
                  var2.fillRect(1, 1, var5 - 3, 1);
               } else if (!var8.isSelected() && !var8.isRollover()) {
                  var2.setColor(MetalLookAndFeel.getWhite());
                  var2.drawRect(1, 1, var5 - 2, var6 - 2);
                  var2.setColor(UIManager.getColor("Button.toolBarBorderBackground"));
                  var2.drawRect(0, 0, var5 - 2, var6 - 2);
               } else {
                  var2.setColor(MetalLookAndFeel.getWhite());
                  var2.fillRect(1, var6 - 1, var5 - 1, 1);
                  var2.fillRect(var5 - 1, 1, 1, var6 - 1);
                  var2.setColor(MetalLookAndFeel.getControlDarkShadow());
                  var2.drawRect(0, 0, var5 - 2, var6 - 2);
               }
            } else {
               var2.setColor(UIManager.getColor("Button.disabledToolBarBorderBackground"));
               var2.drawRect(0, 0, var5 - 2, var6 - 2);
            }
         } else if (var8.isEnabled()) {
            boolean var9 = var8.isPressed();
            boolean var10 = var8.isArmed();
            if (var1 instanceof JButton && ((JButton)var1).isDefaultButton()) {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
               var2.drawRect(1, 1, var5 - 3, var6 - 3);
            } else if (var9) {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.fillRect(0, 0, var5, 2);
               var2.fillRect(0, 2, 2, var6 - 2);
               var2.fillRect(var5 - 1, 1, 1, var6 - 1);
               var2.fillRect(1, var6 - 1, var5 - 2, 1);
            } else if (var8.isRollover() && var7.getClientProperty(MetalBorders.NO_BUTTON_ROLLOVER) == null) {
               var2.setColor(MetalLookAndFeel.getPrimaryControl());
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
               var2.drawRect(2, 2, var5 - 5, var6 - 5);
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(1, 1, var5 - 3, var6 - 3);
            } else {
               var2.setColor(MetalLookAndFeel.getControlDarkShadow());
               var2.drawRect(0, 0, var5 - 1, var6 - 1);
            }
         } else {
            var2.setColor(MetalLookAndFeel.getInactiveControlTextColor());
            var2.drawRect(0, 0, var5 - 1, var6 - 1);
            if (var1 instanceof JButton && ((JButton)var1).isDefaultButton()) {
               var2.drawRect(1, 1, var5 - 3, var6 - 3);
            }
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(3, 3, 3, 3);
         return var2;
      }
   }

   public static class Flush3DBorder extends AbstractBorder implements UIResource {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1.isEnabled()) {
            MetalUtils.drawFlush3DBorder(var2, var3, var4, var5, var6);
         } else {
            MetalUtils.drawDisabledBorder(var2, var3, var4, var5, var6);
         }

      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.set(2, 2, 2, 2);
         return var2;
      }
   }
}
