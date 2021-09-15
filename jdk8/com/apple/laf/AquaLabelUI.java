package com.apple.laf;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.swing.SwingUtilities2;

public class AquaLabelUI extends BasicLabelUI {
   protected static final AquaUtils.RecyclableSingleton<AquaLabelUI> aquaLabelUI = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaLabelUI.class);
   static final String DISABLED_COLOR_KEY = "Label.disabledForegroundColor";

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)aquaLabelUI.get();
   }

   protected void installListeners(JLabel var1) {
      super.installListeners(var1);
      AquaUtilControlSize.addSizePropertyListener(var1);
   }

   protected void uninstallListeners(JLabel var1) {
      AquaUtilControlSize.removeSizePropertyListener(var1);
      super.uninstallListeners(var1);
   }

   protected void paintEnabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      if (AquaMnemonicHandler.isMnemonicHidden()) {
         var6 = -1;
      }

      var2.setColor(var1.getForeground());
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
   }

   protected void paintDisabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      if (AquaMnemonicHandler.isMnemonicHidden()) {
         var6 = -1;
      }

      Color var7 = var1.getBackground();
      if (var7 instanceof UIResource) {
         var2.setColor(this.getDisabledLabelColor(var1));
         SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
      } else {
         super.paintDisabledText(var1, var2, var3, var4, var5);
      }

   }

   protected Color getDisabledLabelColor(JLabel var1) {
      Color var2 = var1.getForeground();
      Object var3 = var1.getClientProperty("Label.disabledForegroundColor");
      Color var4;
      if (var3 instanceof Color) {
         var4 = (Color)var3;
         if (var2.getRGB() << 8 == var4.getRGB() << 8) {
            return var4;
         }
      }

      var4 = new Color(var2.getRed(), var2.getGreen(), var2.getBlue(), var2.getAlpha() / 2);
      var1.putClientProperty("Label.disabledForegroundColor", var4);
      return var4;
   }
}
