package com.apple.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;
import sun.swing.SwingUtilities2;

public class AquaMenuBarBorder implements Border {
   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var2.setColor(Color.gray);
      SwingUtilities2.drawHLine(var2, var3, var3 + var5 - 1, var4 + var6 - 1);
   }

   public Insets getBorderInsets(Component var1) {
      return new Insets(0, 0, 1, 0);
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
