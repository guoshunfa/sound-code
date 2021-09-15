package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class WindowsLabelUI extends BasicLabelUI {
   private static final Object WINDOWS_LABEL_UI_KEY = new Object();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      WindowsLabelUI var2 = (WindowsLabelUI)var1.get(WINDOWS_LABEL_UI_KEY);
      if (var2 == null) {
         var2 = new WindowsLabelUI();
         var1.put(WINDOWS_LABEL_UI_KEY, var2);
      }

      return var2;
   }

   protected void paintEnabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      if (WindowsLookAndFeel.isMnemonicHidden()) {
         var6 = -1;
      }

      var2.setColor(var1.getForeground());
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
   }

   protected void paintDisabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      if (WindowsLookAndFeel.isMnemonicHidden()) {
         var6 = -1;
      }

      if (UIManager.getColor("Label.disabledForeground") instanceof Color && UIManager.getColor("Label.disabledShadow") instanceof Color) {
         var2.setColor(UIManager.getColor("Label.disabledShadow"));
         SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4 + 1, var5 + 1);
         var2.setColor(UIManager.getColor("Label.disabledForeground"));
         SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
      } else {
         Color var7 = var1.getBackground();
         var2.setColor(var7.brighter());
         SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4 + 1, var5 + 1);
         var2.setColor(var7.darker());
         SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
      }

   }
}
