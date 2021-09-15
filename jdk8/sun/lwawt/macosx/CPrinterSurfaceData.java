package sun.lwawt.macosx;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.print.PageFormat;
import java.nio.ByteBuffer;
import sun.java2d.OSXSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;

public class CPrinterSurfaceData extends OSXSurfaceData {
   public static final String DESC_INT_RGB_PQ = "Integer RGB Printer Quartz";
   public static final SurfaceType IntRgbPQ;

   static SurfaceData createData(PageFormat var0, long var1) {
      return new CPrinterSurfaceData(CPrinterGraphicsConfig.getConfig(var0), var1);
   }

   private CPrinterSurfaceData(GraphicsConfiguration var1, long var2) {
      super(IntRgbPQ, var1.getColorModel(), var1, var1.getBounds());
      this.initOps(var2, this.fGraphicsStates, this.fGraphicsStatesObject, var1.getBounds().width, var1.getBounds().height);
   }

   public SurfaceData getReplacement() {
      return this;
   }

   private native void initOps(long var1, ByteBuffer var3, Object[] var4, int var5, int var6);

   public void enableFlushing() {
      this._flush();
   }

   native void _flush();

   public Object getDestination() {
      return null;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      BufferedImage var5 = new BufferedImage(var1 + var3, var2 + var4, 3);
      return var5.getRaster();
   }

   public BufferedImage copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, BufferedImage var6) {
      if (var6 == null) {
         var6 = this.getDeviceConfiguration().createCompatibleImage(var4, var5);
      }

      Graphics2D var7 = var6.createGraphics();
      BufferedImage var8 = this.getCompositingImage(var4, var5);
      var7.drawImage(var8, 0, 0, var4, var5, var2, var3, var2 + var4, var3 + var5, (ImageObserver)null);
      var7.dispose();
      return var6;
   }

   public boolean xorSurfacePixels(SunGraphics2D var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7) {
      throw new InternalError("not implemented yet");
   }

   static {
      IntRgbPQ = SurfaceType.IntRgb.deriveSubType("Integer RGB Printer Quartz");
   }
}
