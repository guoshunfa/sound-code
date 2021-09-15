package javax.swing.plaf.nimbus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class TableScrollPaneCorner extends JComponent implements UIResource {
   protected void paintComponent(Graphics var1) {
      Painter var2 = (Painter)UIManager.get("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter");
      if (var2 != null) {
         if (var1 instanceof Graphics2D) {
            var2.paint((Graphics2D)var1, this, this.getWidth() + 1, this.getHeight());
         } else {
            BufferedImage var3 = new BufferedImage(this.getWidth(), this.getHeight(), 2);
            Graphics2D var4 = (Graphics2D)var3.getGraphics();
            var2.paint(var4, this, this.getWidth() + 1, this.getHeight());
            var4.dispose();
            var1.drawImage(var3, 0, 0, (ImageObserver)null);
            var3 = null;
         }
      }

   }
}
