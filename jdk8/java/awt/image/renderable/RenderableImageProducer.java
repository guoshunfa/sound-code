package java.awt.image.renderable;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Enumeration;
import java.util.Vector;

public class RenderableImageProducer implements ImageProducer, Runnable {
   RenderableImage rdblImage;
   RenderContext rc;
   Vector ics = new Vector();

   public RenderableImageProducer(RenderableImage var1, RenderContext var2) {
      this.rdblImage = var1;
      this.rc = var2;
   }

   public synchronized void setRenderContext(RenderContext var1) {
      this.rc = var1;
   }

   public synchronized void addConsumer(ImageConsumer var1) {
      if (!this.ics.contains(var1)) {
         this.ics.addElement(var1);
      }

   }

   public synchronized boolean isConsumer(ImageConsumer var1) {
      return this.ics.contains(var1);
   }

   public synchronized void removeConsumer(ImageConsumer var1) {
      this.ics.removeElement(var1);
   }

   public synchronized void startProduction(ImageConsumer var1) {
      this.addConsumer(var1);
      Thread var2 = new Thread(this, "RenderableImageProducer Thread");
      var2.start();
   }

   public void requestTopDownLeftRightResend(ImageConsumer var1) {
   }

   public void run() {
      RenderedImage var1;
      if (this.rc != null) {
         var1 = this.rdblImage.createRendering(this.rc);
      } else {
         var1 = this.rdblImage.createDefaultRendering();
      }

      ColorModel var2 = var1.getColorModel();
      Raster var3 = var1.getData();
      SampleModel var4 = var3.getSampleModel();
      DataBuffer var5 = var3.getDataBuffer();
      if (var2 == null) {
         var2 = ColorModel.getRGBdefault();
      }

      int var6 = var3.getMinX();
      int var7 = var3.getMinY();
      int var8 = var3.getWidth();
      int var9 = var3.getHeight();
      Enumeration var10 = this.ics.elements();

      ImageConsumer var11;
      while(var10.hasMoreElements()) {
         var11 = (ImageConsumer)var10.nextElement();
         var11.setDimensions(var8, var9);
         var11.setHints(30);
      }

      int[] var12 = new int[var8];
      int var15 = var4.getNumBands();
      int[] var16 = new int[var15];

      for(int var14 = 0; var14 < var9; ++var14) {
         for(int var13 = 0; var13 < var8; ++var13) {
            var4.getPixel(var13, var14, var16, var5);
            var12[var13] = var2.getDataElement((int[])var16, 0);
         }

         var10 = this.ics.elements();

         while(var10.hasMoreElements()) {
            var11 = (ImageConsumer)var10.nextElement();
            var11.setPixels(0, var14, var8, 1, var2, (int[])var12, 0, var8);
         }
      }

      var10 = this.ics.elements();

      while(var10.hasMoreElements()) {
         var11 = (ImageConsumer)var10.nextElement();
         var11.imageComplete(3);
      }

   }
}
