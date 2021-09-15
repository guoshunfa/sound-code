package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicArrowButton;

public class MetalScrollButton extends BasicArrowButton {
   private static Color shadowColor;
   private static Color highlightColor;
   private boolean isFreeStanding = false;
   private int buttonWidth;

   public MetalScrollButton(int var1, int var2, boolean var3) {
      super(var1);
      shadowColor = UIManager.getColor("ScrollBar.darkShadow");
      highlightColor = UIManager.getColor("ScrollBar.highlight");
      this.buttonWidth = var2;
      this.isFreeStanding = var3;
   }

   public void setFreeStanding(boolean var1) {
      this.isFreeStanding = var1;
   }

   public void paint(Graphics var1) {
      boolean var2 = MetalUtils.isLeftToRight(this);
      boolean var3 = this.getParent().isEnabled();
      ColorUIResource var4 = var3 ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
      boolean var5 = this.getModel().isPressed();
      int var6 = this.getWidth();
      int var7 = this.getHeight();
      int var10 = (var7 + 1) / 4;
      int var11 = (var7 + 1) / 2;
      if (var5) {
         var1.setColor(MetalLookAndFeel.getControlShadow());
      } else {
         var1.setColor(this.getBackground());
      }

      var1.fillRect(0, 0, var6, var7);
      int var12;
      int var13;
      int var14;
      if (this.getDirection() == 1) {
         if (!this.isFreeStanding) {
            ++var7;
            var1.translate(0, -1);
            var6 += 2;
            if (!var2) {
               var1.translate(-1, 0);
            }
         }

         var1.setColor(var4);
         var12 = (var7 + 1 - var10) / 2;
         var13 = var6 / 2;

         for(var14 = 0; var14 < var10; ++var14) {
            var1.drawLine(var13 - var14, var12 + var14, var13 + var14 + 1, var12 + var14);
         }

         if (var3) {
            var1.setColor(highlightColor);
            if (!var5) {
               var1.drawLine(1, 1, var6 - 3, 1);
               var1.drawLine(1, 1, 1, var7 - 1);
            }

            var1.drawLine(var6 - 1, 1, var6 - 1, var7 - 1);
            var1.setColor(shadowColor);
            var1.drawLine(0, 0, var6 - 2, 0);
            var1.drawLine(0, 0, 0, var7 - 1);
            var1.drawLine(var6 - 2, 2, var6 - 2, var7 - 1);
         } else {
            MetalUtils.drawDisabledBorder(var1, 0, 0, var6, var7 + 1);
         }

         if (!this.isFreeStanding) {
            --var7;
            var1.translate(0, 1);
            var6 -= 2;
            if (!var2) {
               var1.translate(1, 0);
            }
         }
      } else if (this.getDirection() == 5) {
         if (!this.isFreeStanding) {
            ++var7;
            var6 += 2;
            if (!var2) {
               var1.translate(-1, 0);
            }
         }

         var1.setColor(var4);
         var12 = (var7 + 1 - var10) / 2 + var10 - 1;
         var13 = var6 / 2;

         for(var14 = 0; var14 < var10; ++var14) {
            var1.drawLine(var13 - var14, var12 - var14, var13 + var14 + 1, var12 - var14);
         }

         if (var3) {
            var1.setColor(highlightColor);
            if (!var5) {
               var1.drawLine(1, 0, var6 - 3, 0);
               var1.drawLine(1, 0, 1, var7 - 3);
            }

            var1.drawLine(1, var7 - 1, var6 - 1, var7 - 1);
            var1.drawLine(var6 - 1, 0, var6 - 1, var7 - 1);
            var1.setColor(shadowColor);
            var1.drawLine(0, 0, 0, var7 - 2);
            var1.drawLine(var6 - 2, 0, var6 - 2, var7 - 2);
            var1.drawLine(2, var7 - 2, var6 - 2, var7 - 2);
         } else {
            MetalUtils.drawDisabledBorder(var1, 0, -1, var6, var7 + 1);
         }

         if (!this.isFreeStanding) {
            --var7;
            var6 -= 2;
            if (!var2) {
               var1.translate(1, 0);
            }
         }
      } else if (this.getDirection() == 3) {
         if (!this.isFreeStanding) {
            var7 += 2;
            ++var6;
         }

         var1.setColor(var4);
         var12 = (var6 + 1 - var10) / 2 + var10 - 1;
         var13 = var7 / 2;

         for(var14 = 0; var14 < var10; ++var14) {
            var1.drawLine(var12 - var14, var13 - var14, var12 - var14, var13 + var14 + 1);
         }

         if (var3) {
            var1.setColor(highlightColor);
            if (!var5) {
               var1.drawLine(0, 1, var6 - 3, 1);
               var1.drawLine(0, 1, 0, var7 - 3);
            }

            var1.drawLine(var6 - 1, 1, var6 - 1, var7 - 1);
            var1.drawLine(0, var7 - 1, var6 - 1, var7 - 1);
            var1.setColor(shadowColor);
            var1.drawLine(0, 0, var6 - 2, 0);
            var1.drawLine(var6 - 2, 2, var6 - 2, var7 - 2);
            var1.drawLine(0, var7 - 2, var6 - 2, var7 - 2);
         } else {
            MetalUtils.drawDisabledBorder(var1, -1, 0, var6 + 1, var7);
         }

         if (!this.isFreeStanding) {
            var7 -= 2;
            --var6;
         }
      } else if (this.getDirection() == 7) {
         if (!this.isFreeStanding) {
            var7 += 2;
            ++var6;
            var1.translate(-1, 0);
         }

         var1.setColor(var4);
         var12 = (var6 + 1 - var10) / 2;
         var13 = var7 / 2;

         for(var14 = 0; var14 < var10; ++var14) {
            var1.drawLine(var12 + var14, var13 - var14, var12 + var14, var13 + var14 + 1);
         }

         if (var3) {
            var1.setColor(highlightColor);
            if (!var5) {
               var1.drawLine(1, 1, var6 - 1, 1);
               var1.drawLine(1, 1, 1, var7 - 3);
            }

            var1.drawLine(1, var7 - 1, var6 - 1, var7 - 1);
            var1.setColor(shadowColor);
            var1.drawLine(0, 0, var6 - 1, 0);
            var1.drawLine(0, 0, 0, var7 - 2);
            var1.drawLine(2, var7 - 2, var6 - 1, var7 - 2);
         } else {
            MetalUtils.drawDisabledBorder(var1, 0, 0, var6 + 1, var7);
         }

         if (!this.isFreeStanding) {
            var7 -= 2;
            --var6;
            var1.translate(1, 0);
         }
      }

   }

   public Dimension getPreferredSize() {
      if (this.getDirection() == 1) {
         return new Dimension(this.buttonWidth, this.buttonWidth - 2);
      } else if (this.getDirection() == 5) {
         return new Dimension(this.buttonWidth, this.buttonWidth - (this.isFreeStanding ? 1 : 2));
      } else if (this.getDirection() == 3) {
         return new Dimension(this.buttonWidth - (this.isFreeStanding ? 1 : 2), this.buttonWidth);
      } else {
         return this.getDirection() == 7 ? new Dimension(this.buttonWidth - 2, this.buttonWidth) : new Dimension(0, 0);
      }
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public Dimension getMaximumSize() {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public int getButtonWidth() {
      return this.buttonWidth;
   }
}
