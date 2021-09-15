package sun.awt.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Hashtable;

public class ToolkitImage extends Image {
   ImageProducer source;
   InputStreamImageSource src;
   ImageRepresentation imagerep;
   private int width = -1;
   private int height = -1;
   private Hashtable properties;
   private int availinfo;

   protected ToolkitImage() {
   }

   public ToolkitImage(ImageProducer var1) {
      this.source = var1;
      if (var1 instanceof InputStreamImageSource) {
         this.src = (InputStreamImageSource)var1;
      }

   }

   public ImageProducer getSource() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      return this.source;
   }

   public int getWidth() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 1) == 0) {
         this.reconstruct(1);
      }

      return this.width;
   }

   public synchronized int getWidth(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 1) == 0) {
         this.addWatcher(var1, true);
         if ((this.availinfo & 1) == 0) {
            return -1;
         }
      }

      return this.width;
   }

   public int getHeight() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 2) == 0) {
         this.reconstruct(2);
      }

      return this.height;
   }

   public synchronized int getHeight(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 2) == 0) {
         this.addWatcher(var1, true);
         if ((this.availinfo & 2) == 0) {
            return -1;
         }
      }

      return this.height;
   }

   public Object getProperty(String var1, ImageObserver var2) {
      if (var1 == null) {
         throw new NullPointerException("null property name is not allowed");
      } else {
         if (this.src != null) {
            this.src.checkSecurity((Object)null, false);
         }

         if (this.properties == null) {
            this.addWatcher(var2, true);
            if (this.properties == null) {
               return null;
            }
         }

         Object var3 = this.properties.get(var1);
         if (var3 == null) {
            var3 = Image.UndefinedProperty;
         }

         return var3;
      }
   }

   public boolean hasError() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      return (this.availinfo & 64) != 0;
   }

   public int check(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) == 0 && (~this.availinfo & 7) != 0) {
         this.addWatcher(var1, false);
      }

      return this.availinfo;
   }

   public void preload(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 32) == 0) {
         this.addWatcher(var1, true);
      }

   }

   private synchronized void addWatcher(ImageObserver var1, boolean var2) {
      if ((this.availinfo & 64) != 0) {
         if (var1 != null) {
            var1.imageUpdate(this, 192, -1, -1, -1, -1);
         }

      } else {
         ImageRepresentation var3 = this.getImageRep();
         var3.addWatcher(var1);
         if (var2) {
            var3.startProduction();
         }

      }
   }

   private synchronized void reconstruct(int var1) {
      if ((var1 & ~this.availinfo) != 0) {
         if ((this.availinfo & 64) != 0) {
            return;
         }

         ImageRepresentation var2 = this.getImageRep();
         var2.startProduction();

         while((var1 & ~this.availinfo) != 0) {
            try {
               this.wait();
            } catch (InterruptedException var4) {
               Thread.currentThread().interrupt();
               return;
            }

            if ((this.availinfo & 64) != 0) {
               return;
            }
         }
      }

   }

   synchronized void addInfo(int var1) {
      this.availinfo |= var1;
      this.notifyAll();
   }

   void setDimensions(int var1, int var2) {
      this.width = var1;
      this.height = var2;
      this.addInfo(3);
   }

   void setProperties(Hashtable var1) {
      if (var1 == null) {
         var1 = new Hashtable();
      }

      this.properties = var1;
      this.addInfo(4);
   }

   synchronized void infoDone(int var1) {
      if (var1 != 1 && (~this.availinfo & 3) == 0) {
         if ((this.availinfo & 4) == 0) {
            this.setProperties((Hashtable)null);
         }
      } else {
         this.addInfo(64);
      }

   }

   public void flush() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      ImageRepresentation var1;
      synchronized(this) {
         this.availinfo &= -65;
         var1 = this.imagerep;
         this.imagerep = null;
      }

      if (var1 != null) {
         var1.abort();
      }

      if (this.src != null) {
         this.src.flush();
      }

   }

   protected ImageRepresentation makeImageRep() {
      return new ImageRepresentation(this, ColorModel.getRGBdefault(), false);
   }

   public synchronized ImageRepresentation getImageRep() {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if (this.imagerep == null) {
         this.imagerep = this.makeImageRep();
      }

      return this.imagerep;
   }

   public Graphics getGraphics() {
      throw new UnsupportedOperationException("getGraphics() not valid for images created with createImage(producer)");
   }

   public ColorModel getColorModel() {
      ImageRepresentation var1 = this.getImageRep();
      return var1.getColorModel();
   }

   public BufferedImage getBufferedImage() {
      ImageRepresentation var1 = this.getImageRep();
      return var1.getBufferedImage();
   }

   public void setAccelerationPriority(float var1) {
      super.setAccelerationPriority(var1);
      ImageRepresentation var2 = this.getImageRep();
      var2.setAccelerationPriority(this.accelerationPriority);
   }

   static {
      NativeLibLoader.loadLibraries();
   }
}
