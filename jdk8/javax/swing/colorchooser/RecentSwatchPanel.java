package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.UIManager;

class RecentSwatchPanel extends SwatchPanel {
   protected void initValues() {
      this.swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", this.getLocale());
      this.numSwatches = new Dimension(5, 7);
      this.gap = new Dimension(1, 1);
   }

   protected void initColors() {
      Color var1 = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", this.getLocale());
      int var2 = this.numSwatches.width * this.numSwatches.height;
      this.colors = new Color[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.colors[var3] = var1;
      }

   }

   public void setMostRecentColor(Color var1) {
      System.arraycopy(this.colors, 0, this.colors, 1, this.colors.length - 1);
      this.colors[0] = var1;
      this.repaint();
   }
}
