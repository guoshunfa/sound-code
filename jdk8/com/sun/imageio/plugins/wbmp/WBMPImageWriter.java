package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriter extends ImageWriter {
   private ImageOutputStream stream = null;

   private static int getNumBits(int var0) {
      int var1 = 32;

      for(int var2 = Integer.MIN_VALUE; var2 != 0 && (var0 & var2) == 0; var2 >>>= 1) {
         --var1;
      }

      return var1;
   }

   private static byte[] intToMultiByte(int var0) {
      int var1 = getNumBits(var0);
      byte[] var2 = new byte[(var1 + 6) / 7];
      int var3 = var2.length - 1;

      for(int var4 = 0; var4 <= var3; ++var4) {
         var2[var4] = (byte)(var0 >>> (var3 - var4) * 7 & 127);
         if (var4 != var3) {
            var2[var4] |= -128;
         }
      }

      return var2;
   }

   public WBMPImageWriter(ImageWriterSpi var1) {
      super(var1);
   }

   public void setOutput(Object var1) {
      super.setOutput(var1);
      if (var1 != null) {
         if (!(var1 instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("WBMPImageWriter"));
         }

         this.stream = (ImageOutputStream)var1;
      } else {
         this.stream = null;
      }

   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2) {
      WBMPMetadata var3 = new WBMPMetadata();
      var3.wbmpType = 0;
      return var3;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2) {
      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      return null;
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
      } else if (var2 == null) {
         throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
      } else {
         this.clearAbortRequest();
         this.processImageStarted(0);
         if (var3 == null) {
            var3 = this.getDefaultWriteParam();
         }

         RenderedImage var4 = null;
         Object var5 = null;
         boolean var6 = var2.hasRaster();
         Rectangle var7 = var3.getSourceRegion();
         SampleModel var8 = null;
         if (var6) {
            var5 = var2.getRaster();
            var8 = ((Raster)var5).getSampleModel();
         } else {
            var4 = var2.getRenderedImage();
            var8 = var4.getSampleModel();
            var5 = var4.getData();
         }

         this.checkSampleModel(var8);
         if (var7 == null) {
            var7 = ((Raster)var5).getBounds();
         } else {
            var7 = var7.intersection(((Raster)var5).getBounds());
         }

         if (var7.isEmpty()) {
            throw new RuntimeException(I18N.getString("WBMPImageWriter1"));
         } else {
            int var9 = var3.getSourceXSubsampling();
            int var10 = var3.getSourceYSubsampling();
            int var11 = var3.getSubsamplingXOffset();
            int var12 = var3.getSubsamplingYOffset();
            var7.translate(var11, var12);
            var7.width -= var11;
            var7.height -= var12;
            int var13 = var7.x / var9;
            int var14 = var7.y / var10;
            int var15 = (var7.width + var9 - 1) / var9;
            int var16 = (var7.height + var10 - 1) / var10;
            Rectangle var17 = new Rectangle(var13, var14, var15, var16);
            var8 = var8.createCompatibleSampleModel(var15, var16);
            Object var18 = var8;
            if (var8.getDataType() != 0 || !(var8 instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)var8).getDataBitOffset() != 0) {
               var18 = new MultiPixelPackedSampleModel(0, var15, var16, 1, var15 + 7 >> 3, 0);
            }

            WritableRaster var19;
            int var21;
            int var23;
            int var24;
            int var25;
            int var26;
            if (!var17.equals(var7)) {
               if (var9 == 1 && var10 == 1) {
                  var5 = ((Raster)var5).createChild(((Raster)var5).getMinX(), ((Raster)var5).getMinY(), var15, var16, var13, var14, (int[])null);
               } else {
                  var19 = Raster.createWritableRaster((SampleModel)var18, new Point(var13, var14));
                  byte[] var20 = ((DataBufferByte)var19.getDataBuffer()).getData();
                  var21 = var14;
                  int var22 = var7.y;

                  for(var23 = 0; var21 < var14 + var16; var22 += var10) {
                     var24 = 0;

                     for(var25 = var7.x; var24 < var15; var25 += var9) {
                        var26 = ((Raster)var5).getSample(var25, var22, 0);
                        var20[var23 + (var24 >> 3)] = (byte)(var20[var23 + (var24 >> 3)] | var26 << 7 - (var24 & 7));
                        ++var24;
                     }

                     var23 += var15 + 7 >> 3;
                     ++var21;
                  }

                  var5 = var19;
               }
            }

            if (!var18.equals(((Raster)var5).getSampleModel())) {
               var19 = Raster.createWritableRaster((SampleModel)var18, new Point(((Raster)var5).getMinX(), ((Raster)var5).getMinY()));
               var19.setRect((Raster)var5);
               var5 = var19;
            }

            boolean var27 = false;
            if (!var6 && var4.getColorModel() instanceof IndexColorModel) {
               IndexColorModel var28 = (IndexColorModel)var4.getColorModel();
               var27 = var28.getRed(0) > var28.getRed(1);
            }

            int var29 = ((MultiPixelPackedSampleModel)var18).getScanlineStride();
            var21 = (var15 + 7) / 8;
            byte[] var30 = ((DataBufferByte)((Raster)var5).getDataBuffer()).getData();
            this.stream.write(0);
            this.stream.write(0);
            this.stream.write(intToMultiByte(var15));
            this.stream.write(intToMultiByte(var16));
            if (!var27 && var29 == var21) {
               this.stream.write(var30, 0, var16 * var21);
               this.processImageProgress(100.0F);
            } else {
               var23 = 0;
               if (!var27) {
                  for(var24 = 0; var24 < var16 && !this.abortRequested(); ++var24) {
                     this.stream.write(var30, var23, var21);
                     var23 += var29;
                     this.processImageProgress(100.0F * (float)var24 / (float)var16);
                  }
               } else {
                  byte[] var31 = new byte[var21];

                  for(var25 = 0; var25 < var16 && !this.abortRequested(); ++var25) {
                     for(var26 = 0; var26 < var21; ++var26) {
                        var31[var26] = (byte)(~var30[var26 + var23]);
                     }

                     this.stream.write(var31, 0, var21);
                     var23 += var29;
                     this.processImageProgress(100.0F * (float)var25 / (float)var16);
                  }
               }
            }

            if (this.abortRequested()) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
               this.stream.flushBefore(this.stream.getStreamPosition());
            }

         }
      }
   }

   public void reset() {
      super.reset();
      this.stream = null;
   }

   private void checkSampleModel(SampleModel var1) {
      int var2 = var1.getDataType();
      if (var2 < 0 || var2 > 3 || var1.getNumBands() != 1 || var1.getSampleSize(0) != 1) {
         throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2"));
      }
   }
}
