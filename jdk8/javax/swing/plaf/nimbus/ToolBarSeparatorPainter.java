package javax.swing.plaf.nimbus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JComponent;

final class ToolBarSeparatorPainter extends AbstractRegionPainter {
   private static final int SPACE = 3;
   private static final int INSET = 2;

   protected AbstractRegionPainter.PaintContext getPaintContext() {
      return new AbstractRegionPainter.PaintContext(new Insets(1, 0, 1, 0), new Dimension(38, 7), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D);
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      var1.setColor(var2.getForeground());
      int var6 = var4 / 2;

      for(int var7 = 2; var7 <= var3 - 2; var7 += 3) {
         var1.fillRect(var7, var6, 1, 1);
      }

   }
}
