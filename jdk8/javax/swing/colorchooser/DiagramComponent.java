package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

final class DiagramComponent extends JComponent implements MouseListener, MouseMotionListener {
   private final ColorPanel panel;
   private final boolean diagram;
   private final Insets insets = new Insets(0, 0, 0, 0);
   private int width;
   private int height;
   private int[] array;
   private BufferedImage image;

   DiagramComponent(ColorPanel var1, boolean var2) {
      this.panel = var1;
      this.diagram = var2;
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
   }

   protected void paintComponent(Graphics var1) {
      this.getInsets(this.insets);
      this.width = this.getWidth() - this.insets.left - this.insets.right;
      this.height = this.getHeight() - this.insets.top - this.insets.bottom;
      boolean var2 = this.image == null || this.width != this.image.getWidth() || this.height != this.image.getHeight();
      int var3;
      if (var2) {
         var3 = this.width * this.height;
         if (this.array == null || this.array.length < var3) {
            this.array = new int[var3];
         }

         this.image = new BufferedImage(this.width, this.height, 1);
      }

      float var10 = 1.0F / (float)(this.width - 1);
      float var4 = 1.0F / (float)(this.height - 1);
      int var5 = 0;
      float var6 = 0.0F;

      for(int var7 = 0; var7 < this.height; var6 += var4) {
         int var9;
         if (this.diagram) {
            float var12 = 0.0F;

            for(var9 = 0; var9 < this.width; ++var5) {
               this.array[var5] = this.panel.getColor(var12, var6);
               ++var9;
               var12 += var10;
            }
         } else {
            int var8 = this.panel.getColor(var6);

            for(var9 = 0; var9 < this.width; ++var5) {
               this.array[var5] = var8;
               ++var9;
            }
         }

         ++var7;
      }

      this.image.setRGB(0, 0, this.width, this.height, this.array, 0, this.width);
      var1.drawImage(this.image, this.insets.left, this.insets.top, this.width, this.height, this);
      if (this.isEnabled()) {
         --this.width;
         --this.height;
         var1.setXORMode(Color.WHITE);
         var1.setColor(Color.BLACK);
         if (this.diagram) {
            var3 = getValue(this.panel.getValueX(), this.insets.left, this.width);
            int var11 = getValue(this.panel.getValueY(), this.insets.top, this.height);
            var1.drawLine(var3 - 8, var11, var3 + 8, var11);
            var1.drawLine(var3, var11 - 8, var3, var11 + 8);
         } else {
            var3 = getValue(this.panel.getValueZ(), this.insets.top, this.height);
            var1.drawLine(this.insets.left, var3, this.insets.left + this.width, var3);
         }

         var1.setPaintMode();
      }

   }

   public void mousePressed(MouseEvent var1) {
      this.mouseDragged(var1);
   }

   public void mouseReleased(MouseEvent var1) {
   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mouseMoved(MouseEvent var1) {
   }

   public void mouseDragged(MouseEvent var1) {
      if (this.isEnabled()) {
         float var2 = getValue(var1.getY(), this.insets.top, this.height);
         if (this.diagram) {
            float var3 = getValue(var1.getX(), this.insets.left, this.width);
            this.panel.setValue(var3, var2);
         } else {
            this.panel.setValue(var2);
         }
      }

   }

   private static int getValue(float var0, int var1, int var2) {
      return var1 + (int)(var0 * (float)var2);
   }

   private static float getValue(int var0, int var1, int var2) {
      if (var1 < var0) {
         var0 -= var1;
         return var0 < var2 ? (float)var0 / (float)var2 : 1.0F;
      } else {
         return 0.0F;
      }
   }
}
