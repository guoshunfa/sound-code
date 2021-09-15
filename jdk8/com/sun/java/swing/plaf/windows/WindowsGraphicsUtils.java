package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class WindowsGraphicsUtils {
   public static void paintText(Graphics var0, AbstractButton var1, Rectangle var2, String var3, int var4) {
      FontMetrics var5 = SwingUtilities2.getFontMetrics(var1, (Graphics)var0);
      int var6 = var1.getDisplayedMnemonicIndex();
      if (WindowsLookAndFeel.isMnemonicHidden()) {
         var6 = -1;
      }

      XPStyle var7 = XPStyle.getXP();
      if (var7 != null && !(var1 instanceof JMenuItem)) {
         paintXPText(var1, var0, var2.x + var4, var2.y + var5.getAscent() + var4, var3, var6);
      } else {
         paintClassicText(var1, var0, var2.x + var4, var2.y + var5.getAscent() + var4, var3, var6);
      }

   }

   static void paintClassicText(AbstractButton var0, Graphics var1, int var2, int var3, String var4, int var5) {
      ButtonModel var6 = var0.getModel();
      Color var7 = var0.getForeground();
      if (var6.isEnabled()) {
         if ((!(var0 instanceof JMenuItem) || !var6.isArmed()) && (!(var0 instanceof JMenu) || !var6.isSelected() && !var6.isRollover())) {
            var1.setColor(var0.getForeground());
         }

         SwingUtilities2.drawStringUnderlineCharAt(var0, var1, var4, var5, var2, var3);
      } else {
         var7 = UIManager.getColor("Button.shadow");
         Color var8 = UIManager.getColor("Button.disabledShadow");
         if (var6.isArmed()) {
            var7 = UIManager.getColor("Button.disabledForeground");
         } else {
            if (var8 == null) {
               var8 = var0.getBackground().darker();
            }

            var1.setColor(var8);
            SwingUtilities2.drawStringUnderlineCharAt(var0, var1, var4, var5, var2 + 1, var3 + 1);
         }

         if (var7 == null) {
            var7 = var0.getBackground().brighter();
         }

         var1.setColor(var7);
         SwingUtilities2.drawStringUnderlineCharAt(var0, var1, var4, var5, var2, var3);
      }

   }

   static void paintXPText(AbstractButton var0, Graphics var1, int var2, int var3, String var4, int var5) {
      TMSchema.Part var6 = WindowsButtonUI.getXPButtonType(var0);
      TMSchema.State var7 = WindowsButtonUI.getXPButtonState(var0);
      paintXPText(var0, var6, var7, var1, var2, var3, var4, var5);
   }

   static void paintXPText(AbstractButton var0, TMSchema.Part var1, TMSchema.State var2, Graphics var3, int var4, int var5, String var6, int var7) {
      XPStyle var8 = XPStyle.getXP();
      if (var8 != null) {
         Color var9 = var0.getForeground();
         if (var9 instanceof UIResource) {
            var9 = var8.getColor(var0, var1, var2, TMSchema.Prop.TEXTCOLOR, var0.getForeground());
            if (var1 == TMSchema.Part.TP_BUTTON && var2 == TMSchema.State.DISABLED) {
               Color var10 = var8.getColor(var0, var1, TMSchema.State.NORMAL, TMSchema.Prop.TEXTCOLOR, var0.getForeground());
               if (var9.equals(var10)) {
                  var9 = var8.getColor(var0, TMSchema.Part.BP_PUSHBUTTON, var2, TMSchema.Prop.TEXTCOLOR, var9);
               }
            }

            TMSchema.TypeEnum var13 = var8.getTypeEnum(var0, var1, var2, TMSchema.Prop.TEXTSHADOWTYPE);
            if (var13 == TMSchema.TypeEnum.TST_SINGLE || var13 == TMSchema.TypeEnum.TST_CONTINUOUS) {
               Color var11 = var8.getColor(var0, var1, var2, TMSchema.Prop.TEXTSHADOWCOLOR, Color.black);
               Point var12 = var8.getPoint(var0, var1, var2, TMSchema.Prop.TEXTSHADOWOFFSET);
               if (var12 != null) {
                  var3.setColor(var11);
                  SwingUtilities2.drawStringUnderlineCharAt(var0, var3, var6, var7, var4 + var12.x, var5 + var12.y);
               }
            }
         }

         var3.setColor(var9);
         SwingUtilities2.drawStringUnderlineCharAt(var0, var3, var6, var7, var4, var5);
      }
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static void repaintMnemonicsInWindow(Window var0) {
      if (var0 != null && var0.isShowing()) {
         Window[] var1 = var0.getOwnedWindows();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            repaintMnemonicsInWindow(var1[var2]);
         }

         repaintMnemonicsInContainer(var0);
      }
   }

   static void repaintMnemonicsInContainer(Container var0) {
      for(int var2 = 0; var2 < var0.getComponentCount(); ++var2) {
         Component var1 = var0.getComponent(var2);
         if (var1 != null && var1.isVisible()) {
            if (var1 instanceof AbstractButton && ((AbstractButton)var1).getMnemonic() != 0) {
               var1.repaint();
            } else if (var1 instanceof JLabel && ((JLabel)var1).getDisplayedMnemonic() != 0) {
               var1.repaint();
            } else if (var1 instanceof Container) {
               repaintMnemonicsInContainer((Container)var1);
            }
         }
      }

   }
}
