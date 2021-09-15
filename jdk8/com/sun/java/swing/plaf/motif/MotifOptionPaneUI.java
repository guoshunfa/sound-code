package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class MotifOptionPaneUI extends BasicOptionPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifOptionPaneUI();
   }

   protected Container createButtonArea() {
      Container var1 = super.createButtonArea();
      if (var1 != null && var1.getLayout() instanceof BasicOptionPaneUI.ButtonAreaLayout) {
         ((BasicOptionPaneUI.ButtonAreaLayout)var1.getLayout()).setCentersChildren(false);
      }

      return var1;
   }

   public Dimension getMinimumOptionPaneSize() {
      return null;
   }

   protected Container createSeparator() {
      return new JPanel() {
         public Dimension getPreferredSize() {
            return new Dimension(10, 2);
         }

         public void paint(Graphics var1) {
            int var2 = this.getWidth();
            var1.setColor(Color.darkGray);
            var1.drawLine(0, 0, var2, 0);
            var1.setColor(Color.white);
            var1.drawLine(0, 1, var2, 1);
         }
      };
   }

   protected void addIcon(Container var1) {
      Icon var2 = this.getIcon();
      if (var2 != null) {
         JLabel var3 = new JLabel(var2);
         var3.setVerticalAlignment(0);
         var1.add((Component)var3, (Object)"West");
      }

   }
}
