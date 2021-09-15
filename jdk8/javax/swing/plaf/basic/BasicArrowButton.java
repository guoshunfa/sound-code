package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class BasicArrowButton extends JButton implements SwingConstants {
   protected int direction;
   private Color shadow;
   private Color darkShadow;
   private Color highlight;

   public BasicArrowButton(int var1, Color var2, Color var3, Color var4, Color var5) {
      this.setRequestFocusEnabled(false);
      this.setDirection(var1);
      this.setBackground(var2);
      this.shadow = var3;
      this.darkShadow = var4;
      this.highlight = var5;
   }

   public BasicArrowButton(int var1) {
      this(var1, UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
   }

   public int getDirection() {
      return this.direction;
   }

   public void setDirection(int var1) {
      this.direction = var1;
   }

   public void paint(Graphics var1) {
      int var5 = this.getSize().width;
      int var6 = this.getSize().height;
      Color var2 = var1.getColor();
      boolean var3 = this.getModel().isPressed();
      boolean var4 = this.isEnabled();
      var1.setColor(this.getBackground());
      var1.fillRect(1, 1, var5 - 2, var6 - 2);
      if (this.getBorder() != null && !(this.getBorder() instanceof UIResource)) {
         this.paintBorder(var1);
      } else if (var3) {
         var1.setColor(this.shadow);
         var1.drawRect(0, 0, var5 - 1, var6 - 1);
      } else {
         var1.drawLine(0, 0, 0, var6 - 1);
         var1.drawLine(1, 0, var5 - 2, 0);
         var1.setColor(this.highlight);
         var1.drawLine(1, 1, 1, var6 - 3);
         var1.drawLine(2, 1, var5 - 3, 1);
         var1.setColor(this.shadow);
         var1.drawLine(1, var6 - 2, var5 - 2, var6 - 2);
         var1.drawLine(var5 - 2, 1, var5 - 2, var6 - 3);
         var1.setColor(this.darkShadow);
         var1.drawLine(0, var6 - 1, var5 - 1, var6 - 1);
         var1.drawLine(var5 - 1, var6 - 1, var5 - 1, 0);
      }

      if (var6 >= 5 && var5 >= 5) {
         if (var3) {
            var1.translate(1, 1);
         }

         int var7 = Math.min((var6 - 4) / 3, (var5 - 4) / 3);
         var7 = Math.max(var7, 2);
         this.paintTriangle(var1, (var5 - var7) / 2, (var6 - var7) / 2, var7, this.direction, var4);
         if (var3) {
            var1.translate(-1, -1);
         }

         var1.setColor(var2);
      } else {
         var1.setColor(var2);
      }
   }

   public Dimension getPreferredSize() {
      return new Dimension(16, 16);
   }

   public Dimension getMinimumSize() {
      return new Dimension(5, 5);
   }

   public Dimension getMaximumSize() {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public boolean isFocusTraversable() {
      return false;
   }

   public void paintTriangle(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
      Color var7 = var1.getColor();
      int var10 = 0;
      var4 = Math.max(var4, 2);
      int var8 = var4 / 2 - 1;
      var1.translate(var2, var3);
      if (var6) {
         var1.setColor(this.darkShadow);
      } else {
         var1.setColor(this.shadow);
      }

      int var9;
      label80:
      switch(var5) {
      case 1:
         for(var9 = 0; var9 < var4; ++var9) {
            var1.drawLine(var8 - var9, var9, var8 + var9, var9);
         }

         if (!var6) {
            var1.setColor(this.highlight);
            var1.drawLine(var8 - var9 + 2, var9, var8 + var9, var9);
         }
      case 2:
      case 4:
      case 6:
      default:
         break;
      case 3:
         if (!var6) {
            var1.translate(1, 1);
            var1.setColor(this.highlight);

            for(var9 = var4 - 1; var9 >= 0; --var9) {
               var1.drawLine(var10, var8 - var9, var10, var8 + var9);
               ++var10;
            }

            var1.translate(-1, -1);
            var1.setColor(this.shadow);
         }

         var10 = 0;
         var9 = var4 - 1;

         while(true) {
            if (var9 < 0) {
               break label80;
            }

            var1.drawLine(var10, var8 - var9, var10, var8 + var9);
            ++var10;
            --var9;
         }
      case 5:
         if (!var6) {
            var1.translate(1, 1);
            var1.setColor(this.highlight);

            for(var9 = var4 - 1; var9 >= 0; --var9) {
               var1.drawLine(var8 - var9, var10, var8 + var9, var10);
               ++var10;
            }

            var1.translate(-1, -1);
            var1.setColor(this.shadow);
         }

         var10 = 0;
         var9 = var4 - 1;

         while(true) {
            if (var9 < 0) {
               break label80;
            }

            var1.drawLine(var8 - var9, var10, var8 + var9, var10);
            ++var10;
            --var9;
         }
      case 7:
         for(var9 = 0; var9 < var4; ++var9) {
            var1.drawLine(var9, var8 - var9, var9, var8 + var9);
         }

         if (!var6) {
            var1.setColor(this.highlight);
            var1.drawLine(var9, var8 - var9 + 2, var9, var8 + var9);
         }
      }

      var1.translate(-var2, -var3);
      var1.setColor(var7);
   }
}
