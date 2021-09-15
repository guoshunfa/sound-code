package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

class SwatchPanel extends JPanel {
   protected Color[] colors;
   protected Dimension swatchSize;
   protected Dimension numSwatches;
   protected Dimension gap;
   private int selRow;
   private int selCol;

   public SwatchPanel() {
      this.initValues();
      this.initColors();
      this.setToolTipText("");
      this.setOpaque(true);
      this.setBackground(Color.white);
      this.setFocusable(true);
      this.setInheritsPopupMenu(true);
      this.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            SwatchPanel.this.repaint();
         }

         public void focusLost(FocusEvent var1) {
            SwatchPanel.this.repaint();
         }
      });
      this.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent var1) {
            int var2 = var1.getKeyCode();
            switch(var2) {
            case 35:
               SwatchPanel.this.selCol = SwatchPanel.this.numSwatches.width - 1;
               SwatchPanel.this.selRow = SwatchPanel.this.numSwatches.height - 1;
               SwatchPanel.this.repaint();
               break;
            case 36:
               SwatchPanel.this.selCol = 0;
               SwatchPanel.this.selRow = 0;
               SwatchPanel.this.repaint();
               break;
            case 37:
               if (SwatchPanel.this.selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                  SwatchPanel.this.selCol--;
                  SwatchPanel.this.repaint();
               } else if (SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                  SwatchPanel.this.selCol++;
                  SwatchPanel.this.repaint();
               }
               break;
            case 38:
               if (SwatchPanel.this.selRow > 0) {
                  SwatchPanel.this.selRow--;
                  SwatchPanel.this.repaint();
               }
               break;
            case 39:
               if (SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                  SwatchPanel.this.selCol++;
                  SwatchPanel.this.repaint();
               } else if (SwatchPanel.this.selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                  SwatchPanel.this.selCol--;
                  SwatchPanel.this.repaint();
               }
               break;
            case 40:
               if (SwatchPanel.this.selRow < SwatchPanel.this.numSwatches.height - 1) {
                  SwatchPanel.this.selRow++;
                  SwatchPanel.this.repaint();
               }
            }

         }
      });
   }

   public Color getSelectedColor() {
      return this.getColorForCell(this.selCol, this.selRow);
   }

   protected void initValues() {
   }

   public void paintComponent(Graphics var1) {
      var1.setColor(this.getBackground());
      var1.fillRect(0, 0, this.getWidth(), this.getHeight());

      for(int var2 = 0; var2 < this.numSwatches.height; ++var2) {
         int var3 = var2 * (this.swatchSize.height + this.gap.height);

         for(int var4 = 0; var4 < this.numSwatches.width; ++var4) {
            Color var5 = this.getColorForCell(var4, var2);
            var1.setColor(var5);
            int var6;
            if (!this.getComponentOrientation().isLeftToRight()) {
               var6 = (this.numSwatches.width - var4 - 1) * (this.swatchSize.width + this.gap.width);
            } else {
               var6 = var4 * (this.swatchSize.width + this.gap.width);
            }

            var1.fillRect(var6, var3, this.swatchSize.width, this.swatchSize.height);
            var1.setColor(Color.black);
            var1.drawLine(var6 + this.swatchSize.width - 1, var3, var6 + this.swatchSize.width - 1, var3 + this.swatchSize.height - 1);
            var1.drawLine(var6, var3 + this.swatchSize.height - 1, var6 + this.swatchSize.width - 1, var3 + this.swatchSize.height - 1);
            if (this.selRow == var2 && this.selCol == var4 && this.isFocusOwner()) {
               Color var7 = new Color(var5.getRed() < 125 ? 255 : 0, var5.getGreen() < 125 ? 255 : 0, var5.getBlue() < 125 ? 255 : 0);
               var1.setColor(var7);
               var1.drawLine(var6, var3, var6 + this.swatchSize.width - 1, var3);
               var1.drawLine(var6, var3, var6, var3 + this.swatchSize.height - 1);
               var1.drawLine(var6 + this.swatchSize.width - 1, var3, var6 + this.swatchSize.width - 1, var3 + this.swatchSize.height - 1);
               var1.drawLine(var6, var3 + this.swatchSize.height - 1, var6 + this.swatchSize.width - 1, var3 + this.swatchSize.height - 1);
               var1.drawLine(var6, var3, var6 + this.swatchSize.width - 1, var3 + this.swatchSize.height - 1);
               var1.drawLine(var6, var3 + this.swatchSize.height - 1, var6 + this.swatchSize.width - 1, var3);
            }
         }
      }

   }

   public Dimension getPreferredSize() {
      int var1 = this.numSwatches.width * (this.swatchSize.width + this.gap.width) - 1;
      int var2 = this.numSwatches.height * (this.swatchSize.height + this.gap.height) - 1;
      return new Dimension(var1, var2);
   }

   protected void initColors() {
   }

   public String getToolTipText(MouseEvent var1) {
      Color var2 = this.getColorForLocation(var1.getX(), var1.getY());
      return var2.getRed() + ", " + var2.getGreen() + ", " + var2.getBlue();
   }

   public void setSelectedColorFromLocation(int var1, int var2) {
      if (!this.getComponentOrientation().isLeftToRight()) {
         this.selCol = this.numSwatches.width - var1 / (this.swatchSize.width + this.gap.width) - 1;
      } else {
         this.selCol = var1 / (this.swatchSize.width + this.gap.width);
      }

      this.selRow = var2 / (this.swatchSize.height + this.gap.height);
      this.repaint();
   }

   public Color getColorForLocation(int var1, int var2) {
      int var3;
      if (!this.getComponentOrientation().isLeftToRight()) {
         var3 = this.numSwatches.width - var1 / (this.swatchSize.width + this.gap.width) - 1;
      } else {
         var3 = var1 / (this.swatchSize.width + this.gap.width);
      }

      int var4 = var2 / (this.swatchSize.height + this.gap.height);
      return this.getColorForCell(var3, var4);
   }

   private Color getColorForCell(int var1, int var2) {
      return this.colors[var2 * this.numSwatches.width + var1];
   }
}
