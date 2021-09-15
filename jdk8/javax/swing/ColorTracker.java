package javax.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

class ColorTracker implements ActionListener, Serializable {
   JColorChooser chooser;
   Color color;

   public ColorTracker(JColorChooser var1) {
      this.chooser = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.color = this.chooser.getColor();
   }

   public Color getColor() {
      return this.color;
   }
}
