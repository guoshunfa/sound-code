package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;

public class MotifScrollBarButton extends BasicArrowButton {
   private Color darkShadow = UIManager.getColor("controlShadow");
   private Color lightShadow = UIManager.getColor("controlLtHighlight");

   public MotifScrollBarButton(int var1) {
      super(var1);
      switch(var1) {
      case 1:
      case 3:
      case 5:
      case 7:
         this.direction = var1;
         this.setRequestFocusEnabled(false);
         this.setOpaque(true);
         this.setBackground(UIManager.getColor("ScrollBar.background"));
         this.setForeground(UIManager.getColor("ScrollBar.foreground"));
         return;
      case 2:
      case 4:
      case 6:
      default:
         throw new IllegalArgumentException("invalid direction");
      }
   }

   public Dimension getPreferredSize() {
      switch(this.direction) {
      case 1:
      case 5:
         return new Dimension(11, 12);
      case 2:
      case 3:
      case 4:
      case 6:
      case 7:
      default:
         return new Dimension(12, 11);
      }
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public Dimension getMaximumSize() {
      return this.getPreferredSize();
   }

   public boolean isFocusTraversable() {
      return false;
   }

   public void paint(Graphics var1) {
      int var2 = this.getWidth();
      int var3 = this.getHeight();
      if (this.isOpaque()) {
         var1.setColor(this.getBackground());
         var1.fillRect(0, 0, var2, var3);
      }

      boolean var4 = this.getModel().isPressed();
      Color var5 = var4 ? this.darkShadow : this.lightShadow;
      Color var6 = var4 ? this.lightShadow : this.darkShadow;
      Color var7 = this.getBackground();
      int var8 = var2 / 2;
      int var9 = var3 / 2;
      int var10 = Math.min(var2, var3);
      int var11;
      int var12;
      int var13;
      switch(this.direction) {
      case 1:
         var1.setColor(var5);
         var1.drawLine(var8, 0, var8, 0);
         var11 = var8 - 1;
         var12 = 1;

         for(var13 = 1; var12 <= var10 - 2; var12 += 2) {
            var1.setColor(var5);
            var1.drawLine(var11, var12, var11, var12);
            if (var12 >= var10 - 2) {
               var1.drawLine(var11, var12 + 1, var11, var12 + 1);
            }

            var1.setColor(var7);
            var1.drawLine(var11 + 1, var12, var11 + var13, var12);
            if (var12 < var10 - 2) {
               var1.drawLine(var11, var12 + 1, var11 + var13 + 1, var12 + 1);
            }

            var1.setColor(var6);
            var1.drawLine(var11 + var13 + 1, var12, var11 + var13 + 1, var12);
            if (var12 >= var10 - 2) {
               var1.drawLine(var11 + 1, var12 + 1, var11 + var13 + 1, var12 + 1);
            }

            var13 += 2;
            --var11;
         }
      case 2:
      case 4:
      case 6:
      default:
         break;
      case 3:
         var1.setColor(var5);
         var1.drawLine(var10, var9, var10, var9);
         var11 = var9 - 1;
         var12 = var10 - 1;

         for(var13 = 1; var12 >= 1; var12 -= 2) {
            var1.setColor(var5);
            var1.drawLine(var12, var11, var12, var11);
            if (var12 <= 2) {
               var1.drawLine(var12 - 1, var11, var12 - 1, var11 + var13 + 1);
            }

            var1.setColor(var7);
            var1.drawLine(var12, var11 + 1, var12, var11 + var13);
            if (var12 > 2) {
               var1.drawLine(var12 - 1, var11, var12 - 1, var11 + var13 + 1);
            }

            var1.setColor(var6);
            var1.drawLine(var12, var11 + var13 + 1, var12, var11 + var13 + 1);
            var13 += 2;
            --var11;
         }

         return;
      case 5:
         var1.setColor(var6);
         var1.drawLine(var8, var10, var8, var10);
         var11 = var8 - 1;
         var12 = var10 - 1;

         for(var13 = 1; var12 >= 1; var12 -= 2) {
            var1.setColor(var5);
            var1.drawLine(var11, var12, var11, var12);
            if (var12 <= 2) {
               var1.drawLine(var11, var12 - 1, var11 + var13 + 1, var12 - 1);
            }

            var1.setColor(var7);
            var1.drawLine(var11 + 1, var12, var11 + var13, var12);
            if (var12 > 2) {
               var1.drawLine(var11, var12 - 1, var11 + var13 + 1, var12 - 1);
            }

            var1.setColor(var6);
            var1.drawLine(var11 + var13 + 1, var12, var11 + var13 + 1, var12);
            var13 += 2;
            --var11;
         }

         return;
      case 7:
         var1.setColor(var6);
         var1.drawLine(0, var9, 0, var9);
         var11 = var9 - 1;
         var12 = 1;

         for(var13 = 1; var12 <= var10 - 2; var12 += 2) {
            var1.setColor(var5);
            var1.drawLine(var12, var11, var12, var11);
            if (var12 >= var10 - 2) {
               var1.drawLine(var12 + 1, var11, var12 + 1, var11);
            }

            var1.setColor(var7);
            var1.drawLine(var12, var11 + 1, var12, var11 + var13);
            if (var12 < var10 - 2) {
               var1.drawLine(var12 + 1, var11, var12 + 1, var11 + var13 + 1);
            }

            var1.setColor(var6);
            var1.drawLine(var12, var11 + var13 + 1, var12, var11 + var13 + 1);
            if (var12 >= var10 - 2) {
               var1.drawLine(var12 + 1, var11 + 1, var12 + 1, var11 + var13 + 1);
            }

            var13 += 2;
            --var11;
         }
      }

   }
}
