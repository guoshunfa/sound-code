package com.apple.laf;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class AquaToolBarSeparatorUI extends BasicToolBarSeparatorUI {
   protected static AquaUtils.RecyclableSingleton<AquaToolBarSeparatorUI> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaToolBarSeparatorUI.class);
   BasicStroke dashedStroke = new BasicStroke(1.0F, 0, 2, 0.0F, new float[]{1.0F, 2.0F}, 0.0F);

   public static ComponentUI createUI(JComponent var0) {
      return (ComponentUI)instance.get();
   }

   public void paint(Graphics var1, JComponent var2) {
      var1.setColor(var2.getForeground());
      ((Graphics2D)var1).setStroke(this.dashedStroke);
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      if (((JToolBar.Separator)var2).getOrientation() == 0) {
         var1.drawLine(2, var4 / 2, var3 - 3, var4 / 2);
      } else {
         var1.drawLine(var3 / 2, 2, var3 / 2, var4 - 3);
      }

   }

   public Dimension getMinimumSize(JComponent var1) {
      JToolBar.Separator var2 = (JToolBar.Separator)var1;
      return var2.getOrientation() == 0 ? new Dimension(1, 11) : new Dimension(11, 1);
   }

   public Dimension getPreferredSize(JComponent var1) {
      JToolBar.Separator var2 = (JToolBar.Separator)var1;
      return var2.getOrientation() == 0 ? new Dimension(1, 11) : new Dimension(11, 1);
   }

   public Dimension getMaximumSize(JComponent var1) {
      JToolBar.Separator var2 = (JToolBar.Separator)var1;
      return var2.getOrientation() == 0 ? new Dimension(Integer.MAX_VALUE, 11) : new Dimension(11, Integer.MAX_VALUE);
   }
}
