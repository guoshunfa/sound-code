package sun.awt.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import sun.java2d.DestSurfaceProvider;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceManagerFactory;
import sun.print.PrinterGraphicsConfig;

public class SunVolatileImage extends VolatileImage implements DestSurfaceProvider {
   protected VolatileSurfaceManager volSurfaceManager;
   protected Component comp;
   private GraphicsConfiguration graphicsConfig;
   private Font defaultFont;
   private int width;
   private int height;
   private int forcedAccelSurfaceType;

   protected SunVolatileImage(Component var1, GraphicsConfiguration var2, int var3, int var4, Object var5, int var6, ImageCapabilities var7, int var8) {
      this.comp = var1;
      this.graphicsConfig = var2;
      if (var3 > 0 && var4 > 0) {
         this.width = var3;
         this.height = var4;
         this.forcedAccelSurfaceType = var8;
         if (var6 != 1 && var6 != 2 && var6 != 3) {
            throw new IllegalArgumentException("Unknown transparency type:" + var6);
         } else {
            this.transparency = var6;
            this.volSurfaceManager = this.createSurfaceManager(var5, var7);
            SurfaceManager.setManager(this, this.volSurfaceManager);
            this.volSurfaceManager.initialize();
            this.volSurfaceManager.initContents();
         }
      } else {
         throw new IllegalArgumentException("Width (" + var3 + ") and height (" + var4 + ") cannot be <= 0");
      }
   }

   private SunVolatileImage(Component var1, GraphicsConfiguration var2, int var3, int var4, Object var5, ImageCapabilities var6) {
      this(var1, var2, var3, var4, var5, 1, var6, 0);
   }

   public SunVolatileImage(Component var1, int var2, int var3) {
      this(var1, var2, var3, (Object)null);
   }

   public SunVolatileImage(Component var1, int var2, int var3, Object var4) {
      this(var1, var1.getGraphicsConfiguration(), var2, var3, var4, (ImageCapabilities)null);
   }

   public SunVolatileImage(GraphicsConfiguration var1, int var2, int var3, int var4, ImageCapabilities var5) {
      this((Component)null, var1, var2, var3, (Object)null, var4, var5, 0);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public GraphicsConfiguration getGraphicsConfig() {
      return this.graphicsConfig;
   }

   public void updateGraphicsConfig() {
      if (this.comp != null) {
         GraphicsConfiguration var1 = this.comp.getGraphicsConfiguration();
         if (var1 != null) {
            this.graphicsConfig = var1;
         }
      }

   }

   public Component getComponent() {
      return this.comp;
   }

   public int getForcedAccelSurfaceType() {
      return this.forcedAccelSurfaceType;
   }

   protected VolatileSurfaceManager createSurfaceManager(Object var1, ImageCapabilities var2) {
      if (!(this.graphicsConfig instanceof BufferedImageGraphicsConfig) && !(this.graphicsConfig instanceof PrinterGraphicsConfig) && (var2 == null || var2.isAccelerated())) {
         SurfaceManagerFactory var3 = SurfaceManagerFactory.getInstance();
         return var3.createVolatileManager(this, var1);
      } else {
         return new BufImgVolatileSurfaceManager(this, var1);
      }
   }

   private Color getForeground() {
      return this.comp != null ? this.comp.getForeground() : Color.black;
   }

   private Color getBackground() {
      return this.comp != null ? this.comp.getBackground() : Color.white;
   }

   private Font getFont() {
      if (this.comp != null) {
         return this.comp.getFont();
      } else {
         if (this.defaultFont == null) {
            this.defaultFont = new Font("Dialog", 0, 12);
         }

         return this.defaultFont;
      }
   }

   public Graphics2D createGraphics() {
      return new SunGraphics2D(this.volSurfaceManager.getPrimarySurfaceData(), this.getForeground(), this.getBackground(), this.getFont());
   }

   public Object getProperty(String var1, ImageObserver var2) {
      if (var1 == null) {
         throw new NullPointerException("null property name is not allowed");
      } else {
         return Image.UndefinedProperty;
      }
   }

   public int getWidth(ImageObserver var1) {
      return this.getWidth();
   }

   public int getHeight(ImageObserver var1) {
      return this.getHeight();
   }

   public BufferedImage getBackupImage() {
      return this.graphicsConfig.createCompatibleImage(this.getWidth(), this.getHeight(), this.getTransparency());
   }

   public BufferedImage getSnapshot() {
      BufferedImage var1 = this.getBackupImage();
      Graphics2D var2 = var1.createGraphics();
      var2.setComposite(AlphaComposite.Src);
      var2.drawImage(this, 0, 0, (ImageObserver)null);
      var2.dispose();
      return var1;
   }

   public int validate(GraphicsConfiguration var1) {
      return this.volSurfaceManager.validate(var1);
   }

   public boolean contentsLost() {
      return this.volSurfaceManager.contentsLost();
   }

   public ImageCapabilities getCapabilities() {
      return this.volSurfaceManager.getCapabilities(this.graphicsConfig);
   }

   public Surface getDestSurface() {
      return this.volSurfaceManager.getPrimarySurfaceData();
   }
}
