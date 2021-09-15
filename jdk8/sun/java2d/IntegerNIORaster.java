package sun.java2d;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import sun.awt.image.SunWritableRaster;

public class IntegerNIORaster extends SunWritableRaster {
   protected IntBuffer data;

   public static WritableRaster createNIORaster(int var0, int var1, int[] var2, Point var3) {
      if (var0 > 0 && var1 > 0) {
         DataBufferNIOInt var4 = new DataBufferNIOInt(var0 * var1);
         if (var3 == null) {
            var3 = new Point(0, 0);
         }

         SinglePixelPackedSampleModel var5 = new SinglePixelPackedSampleModel(3, var0, var1, var0, var2);
         return new IntegerNIORaster(var5, var4, var3);
      } else {
         throw new IllegalArgumentException("Width (" + var0 + ") and height (" + var1 + ") cannot be <= 0");
      }
   }

   public IntegerNIORaster(SampleModel var1, DataBuffer var2, Point var3) {
      super(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (WritableRaster)null);
      if (!(var2 instanceof DataBufferNIOInt)) {
         throw new RasterFormatException("IntegerNIORasters must have DataBufferNIOInt DataBuffers");
      } else {
         this.data = ((DataBufferNIOInt)var2).getBuffer();
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return new IntegerNIORaster(this.sampleModel, new DataBufferNIOInt(this.sampleModel.getWidth() * this.sampleModel.getHeight()), new Point(0, 0));
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new IntegerNIORaster(var3, new DataBufferNIOInt(var1 * var2), new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster(Rectangle var1) {
      if (var1 == null) {
         throw new NullPointerException("Rect cannot be null");
      } else {
         return this.createCompatibleWritableRaster(var1.x, var1.y, var1.width, var1.height);
      }
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2, int var3, int var4) {
      WritableRaster var5 = this.createCompatibleWritableRaster(var3, var4);
      return var5.createWritableChild(0, 0, var3, var4, var1, var2, (int[])null);
   }

   public IntBuffer getBuffer() {
      return this.data;
   }

   public String toString() {
      return new String("IntegerNIORaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY);
   }
}
