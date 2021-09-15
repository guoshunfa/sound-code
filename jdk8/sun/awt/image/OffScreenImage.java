package sun.awt.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class OffScreenImage extends BufferedImage {
   protected Component c;
   private OffScreenImageSource osis;
   private Font defaultFont;

   public OffScreenImage(Component var1, ColorModel var2, WritableRaster var3, boolean var4) {
      super(var2, var3, var4, (Hashtable)null);
      this.c = var1;
      this.initSurface(var3.getWidth(), var3.getHeight());
   }

   public Graphics getGraphics() {
      return this.createGraphics();
   }

   public Graphics2D createGraphics() {
      if (this.c == null) {
         GraphicsEnvironment var4 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         return var4.createGraphics(this);
      } else {
         Object var1 = this.c.getBackground();
         if (var1 == null) {
            var1 = SystemColor.window;
         }

         Object var2 = this.c.getForeground();
         if (var2 == null) {
            var2 = SystemColor.windowText;
         }

         Font var3 = this.c.getFont();
         if (var3 == null) {
            if (this.defaultFont == null) {
               this.defaultFont = new Font("Dialog", 0, 12);
            }

            var3 = this.defaultFont;
         }

         return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(this), (Color)var2, (Color)var1, var3);
      }
   }

   private void initSurface(int var1, int var2) {
      Graphics2D var3 = this.createGraphics();

      try {
         var3.clearRect(0, 0, var1, var2);
      } finally {
         var3.dispose();
      }

   }

   public ImageProducer getSource() {
      if (this.osis == null) {
         this.osis = new OffScreenImageSource(this);
      }

      return this.osis;
   }
}
