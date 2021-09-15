package com.apple.laf;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.UIManager;

public class AquaFocus {
   static boolean paintFocus(Graphics var0, AquaFocus.Drawable var1) {
      return false;
   }

   public static Icon createFocusedIcon(Icon var0, Component var1, int var2) {
      return new AquaFocus.FocusedIcon(var0, var2);
   }

   static class FocusedIcon extends AquaUtils.ShadowBorder implements Icon {
      final Icon icon;
      final int slack;

      public FocusedIcon(final Icon var1, final int var2) {
         super(new AquaUtils.Painter() {
            public void paint(Graphics var1x, int var2x, int var3, int var4, int var5) {
               Graphics2D var6 = (Graphics2D)var1x;
               var6.setComposite(AlphaComposite.Src);
               var6.setColor(UIManager.getColor("Focus.color"));
               var6.fillRect(var2x, var3, var4 - var2 * 2, var5 - var2 * 2);
               var6.setComposite(AlphaComposite.DstAtop);
               var1.paintIcon((Component)null, var6, var2x, var3);
            }
         }, new AquaUtils.Painter() {
            public void paint(Graphics var1x, int var2, int var3, int var4, int var5) {
               ((Graphics2D)var1x).setComposite(AlphaComposite.SrcAtop);
               var1.paintIcon((Component)null, var1x, var2, var3);
            }
         }, var2, var2, 0.0F, 1.8F, 7);
         this.icon = var1;
         this.slack = var2;
      }

      public int getIconHeight() {
         return this.icon.getIconHeight() + this.slack + this.slack;
      }

      public int getIconWidth() {
         return this.icon.getIconWidth() + this.slack + this.slack;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         this.paintBorder(var1, var2, var3, var4, this.getIconWidth(), this.getIconHeight());
         this.icon.paintIcon(var1, var2, var3 + this.slack, var4 + this.slack);
      }
   }

   interface Drawable {
      void draw(Graphics2D var1);
   }
}
