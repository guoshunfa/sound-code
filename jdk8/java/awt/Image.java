package java.awt;

import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import sun.awt.image.SurfaceManager;

public abstract class Image {
   private static ImageCapabilities defaultImageCaps = new ImageCapabilities(false);
   protected float accelerationPriority = 0.5F;
   public static final Object UndefinedProperty = new Object();
   public static final int SCALE_DEFAULT = 1;
   public static final int SCALE_FAST = 2;
   public static final int SCALE_SMOOTH = 4;
   public static final int SCALE_REPLICATE = 8;
   public static final int SCALE_AREA_AVERAGING = 16;
   SurfaceManager surfaceManager;

   public abstract int getWidth(ImageObserver var1);

   public abstract int getHeight(ImageObserver var1);

   public abstract ImageProducer getSource();

   public abstract Graphics getGraphics();

   public abstract Object getProperty(String var1, ImageObserver var2);

   public Image getScaledInstance(int var1, int var2, int var3) {
      Object var4;
      if ((var3 & 20) != 0) {
         var4 = new AreaAveragingScaleFilter(var1, var2);
      } else {
         var4 = new ReplicateScaleFilter(var1, var2);
      }

      FilteredImageSource var5 = new FilteredImageSource(this.getSource(), (ImageFilter)var4);
      return Toolkit.getDefaultToolkit().createImage((ImageProducer)var5);
   }

   public void flush() {
      if (this.surfaceManager != null) {
         this.surfaceManager.flush();
      }

   }

   public ImageCapabilities getCapabilities(GraphicsConfiguration var1) {
      return this.surfaceManager != null ? this.surfaceManager.getCapabilities(var1) : defaultImageCaps;
   }

   public void setAccelerationPriority(float var1) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         this.accelerationPriority = var1;
         if (this.surfaceManager != null) {
            this.surfaceManager.setAccelerationPriority(this.accelerationPriority);
         }

      } else {
         throw new IllegalArgumentException("Priority must be a value between 0 and 1, inclusive");
      }
   }

   public float getAccelerationPriority() {
      return this.accelerationPriority;
   }

   static {
      SurfaceManager.setImageAccessor(new SurfaceManager.ImageAccessor() {
         public SurfaceManager getSurfaceManager(Image var1) {
            return var1.surfaceManager;
         }

         public void setSurfaceManager(Image var1, SurfaceManager var2) {
            var1.surfaceManager = var2;
         }
      });
   }
}
