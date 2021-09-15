package sun.lwawt.macosx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.print.PrinterJob;
import sun.print.ProxyGraphics2D;

public class CPrinterGraphics extends ProxyGraphics2D {
   public CPrinterGraphics(Graphics2D var1, PrinterJob var2) {
      super(var1, var2);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      return this.getDelegate().drawImage(var1, var2, var3, var4, var5);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      return this.getDelegate().drawImage(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      return this.getDelegate().drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }
}
