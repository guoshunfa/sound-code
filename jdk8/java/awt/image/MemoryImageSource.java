package java.awt.image;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MemoryImageSource implements ImageProducer {
   int width;
   int height;
   ColorModel model;
   Object pixels;
   int pixeloffset;
   int pixelscan;
   Hashtable properties;
   Vector theConsumers = new Vector();
   boolean animating;
   boolean fullbuffers;

   public MemoryImageSource(int var1, int var2, ColorModel var3, byte[] var4, int var5, int var6) {
      this.initialize(var1, var2, var3, var4, var5, var6, (Hashtable)null);
   }

   public MemoryImageSource(int var1, int var2, ColorModel var3, byte[] var4, int var5, int var6, Hashtable<?, ?> var7) {
      this.initialize(var1, var2, var3, var4, var5, var6, var7);
   }

   public MemoryImageSource(int var1, int var2, ColorModel var3, int[] var4, int var5, int var6) {
      this.initialize(var1, var2, var3, var4, var5, var6, (Hashtable)null);
   }

   public MemoryImageSource(int var1, int var2, ColorModel var3, int[] var4, int var5, int var6, Hashtable<?, ?> var7) {
      this.initialize(var1, var2, var3, var4, var5, var6, var7);
   }

   private void initialize(int var1, int var2, ColorModel var3, Object var4, int var5, int var6, Hashtable var7) {
      this.width = var1;
      this.height = var2;
      this.model = var3;
      this.pixels = var4;
      this.pixeloffset = var5;
      this.pixelscan = var6;
      if (var7 == null) {
         var7 = new Hashtable();
      }

      this.properties = var7;
   }

   public MemoryImageSource(int var1, int var2, int[] var3, int var4, int var5) {
      this.initialize(var1, var2, ColorModel.getRGBdefault(), var3, var4, var5, (Hashtable)null);
   }

   public MemoryImageSource(int var1, int var2, int[] var3, int var4, int var5, Hashtable<?, ?> var6) {
      this.initialize(var1, var2, ColorModel.getRGBdefault(), var3, var4, var5, var6);
   }

   public synchronized void addConsumer(ImageConsumer var1) {
      if (!this.theConsumers.contains(var1)) {
         this.theConsumers.addElement(var1);

         try {
            this.initConsumer(var1);
            this.sendPixels(var1, 0, 0, this.width, this.height);
            if (this.isConsumer(var1)) {
               var1.imageComplete(this.animating ? 2 : 3);
               if (!this.animating && this.isConsumer(var1)) {
                  var1.imageComplete(1);
                  this.removeConsumer(var1);
               }
            }
         } catch (Exception var3) {
            if (this.isConsumer(var1)) {
               var1.imageComplete(1);
            }
         }

      }
   }

   public synchronized boolean isConsumer(ImageConsumer var1) {
      return this.theConsumers.contains(var1);
   }

   public synchronized void removeConsumer(ImageConsumer var1) {
      this.theConsumers.removeElement(var1);
   }

   public void startProduction(ImageConsumer var1) {
      this.addConsumer(var1);
   }

   public void requestTopDownLeftRightResend(ImageConsumer var1) {
   }

   public synchronized void setAnimated(boolean var1) {
      this.animating = var1;
      if (!this.animating) {
         Enumeration var2 = this.theConsumers.elements();

         while(var2.hasMoreElements()) {
            ImageConsumer var3 = (ImageConsumer)var2.nextElement();
            var3.imageComplete(3);
            if (this.isConsumer(var3)) {
               var3.imageComplete(1);
            }
         }

         this.theConsumers.removeAllElements();
      }

   }

   public synchronized void setFullBufferUpdates(boolean var1) {
      if (this.fullbuffers != var1) {
         this.fullbuffers = var1;
         if (this.animating) {
            Enumeration var2 = this.theConsumers.elements();

            while(var2.hasMoreElements()) {
               ImageConsumer var3 = (ImageConsumer)var2.nextElement();
               var3.setHints(var1 ? 6 : 1);
            }
         }

      }
   }

   public void newPixels() {
      this.newPixels(0, 0, this.width, this.height, true);
   }

   public synchronized void newPixels(int var1, int var2, int var3, int var4) {
      this.newPixels(var1, var2, var3, var4, true);
   }

   public synchronized void newPixels(int var1, int var2, int var3, int var4, boolean var5) {
      if (this.animating) {
         if (this.fullbuffers) {
            var2 = 0;
            var1 = 0;
            var3 = this.width;
            var4 = this.height;
         } else {
            if (var1 < 0) {
               var3 += var1;
               var1 = 0;
            }

            if (var1 + var3 > this.width) {
               var3 = this.width - var1;
            }

            if (var2 < 0) {
               var4 += var2;
               var2 = 0;
            }

            if (var2 + var4 > this.height) {
               var4 = this.height - var2;
            }
         }

         if ((var3 <= 0 || var4 <= 0) && !var5) {
            return;
         }

         Enumeration var6 = this.theConsumers.elements();

         while(var6.hasMoreElements()) {
            ImageConsumer var7 = (ImageConsumer)var6.nextElement();
            if (var3 > 0 && var4 > 0) {
               this.sendPixels(var7, var1, var2, var3, var4);
            }

            if (var5 && this.isConsumer(var7)) {
               var7.imageComplete(2);
            }
         }
      }

   }

   public synchronized void newPixels(byte[] var1, ColorModel var2, int var3, int var4) {
      this.pixels = var1;
      this.model = var2;
      this.pixeloffset = var3;
      this.pixelscan = var4;
      this.newPixels();
   }

   public synchronized void newPixels(int[] var1, ColorModel var2, int var3, int var4) {
      this.pixels = var1;
      this.model = var2;
      this.pixeloffset = var3;
      this.pixelscan = var4;
      this.newPixels();
   }

   private void initConsumer(ImageConsumer var1) {
      if (this.isConsumer(var1)) {
         var1.setDimensions(this.width, this.height);
      }

      if (this.isConsumer(var1)) {
         var1.setProperties(this.properties);
      }

      if (this.isConsumer(var1)) {
         var1.setColorModel(this.model);
      }

      if (this.isConsumer(var1)) {
         var1.setHints(this.animating ? (this.fullbuffers ? 6 : 1) : 30);
      }

   }

   private void sendPixels(ImageConsumer var1, int var2, int var3, int var4, int var5) {
      int var6 = this.pixeloffset + this.pixelscan * var3 + var2;
      if (this.isConsumer(var1)) {
         if (this.pixels instanceof byte[]) {
            var1.setPixels(var2, var3, var4, var5, this.model, (byte[])((byte[])this.pixels), var6, this.pixelscan);
         } else {
            var1.setPixels(var2, var3, var4, var5, this.model, (int[])((int[])this.pixels), var6, this.pixelscan);
         }
      }

   }
}
