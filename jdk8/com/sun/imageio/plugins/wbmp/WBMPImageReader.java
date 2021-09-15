package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReader extends ImageReader {
   private ImageInputStream iis = null;
   private boolean gotHeader = false;
   private int width;
   private int height;
   private int wbmpType;
   private WBMPMetadata metadata;

   public WBMPImageReader(ImageReaderSpi var1) {
      super(var1);
   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      super.setInput(var1, var2, var3);
      this.iis = (ImageInputStream)var1;
      this.gotHeader = false;
   }

   public int getNumImages(boolean var1) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("GetNumImages0"));
      } else if (this.seekForwardOnly && var1) {
         throw new IllegalStateException(I18N.getString("GetNumImages1"));
      } else {
         return 1;
      }
   }

   public int getWidth(int var1) throws IOException {
      this.checkIndex(var1);
      this.readHeader();
      return this.width;
   }

   public int getHeight(int var1) throws IOException {
      this.checkIndex(var1);
      this.readHeader();
      return this.height;
   }

   public boolean isRandomAccessEasy(int var1) throws IOException {
      this.checkIndex(var1);
      return true;
   }

   private void checkIndex(int var1) {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
      }
   }

   public void readHeader() throws IOException {
      if (!this.gotHeader) {
         if (this.iis == null) {
            throw new IllegalStateException("Input source not set!");
         } else {
            this.metadata = new WBMPMetadata();
            this.wbmpType = this.iis.readByte();
            byte var1 = this.iis.readByte();
            if (var1 == 0 && this.isValidWbmpType(this.wbmpType)) {
               this.metadata.wbmpType = this.wbmpType;
               this.width = ReaderUtil.readMultiByteInteger(this.iis);
               this.metadata.width = this.width;
               this.height = ReaderUtil.readMultiByteInteger(this.iis);
               this.metadata.height = this.height;
               this.gotHeader = true;
            } else {
               throw new IIOException(I18N.getString("WBMPImageReader2"));
            }
         }
      }
   }

   public Iterator getImageTypes(int var1) throws IOException {
      this.checkIndex(var1);
      this.readHeader();
      BufferedImage var2 = new BufferedImage(1, 1, 12);
      ArrayList var3 = new ArrayList(1);
      var3.add(new ImageTypeSpecifier(var2));
      return var3.iterator();
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getImageMetadata(int var1) throws IOException {
      this.checkIndex(var1);
      if (this.metadata == null) {
         this.readHeader();
      }

      return this.metadata;
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      return null;
   }

   public BufferedImage read(int var1, ImageReadParam var2) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
      } else {
         this.checkIndex(var1);
         this.clearAbortRequest();
         this.processImageStarted(var1);
         if (var2 == null) {
            var2 = this.getDefaultReadParam();
         }

         this.readHeader();
         Rectangle var3 = new Rectangle(0, 0, 0, 0);
         Rectangle var4 = new Rectangle(0, 0, 0, 0);
         computeRegions(var2, this.width, this.height, var2.getDestination(), var3, var4);
         int var5 = var2.getSourceXSubsampling();
         int var6 = var2.getSourceYSubsampling();
         int var7 = var2.getSubsamplingXOffset();
         int var8 = var2.getSubsamplingYOffset();
         BufferedImage var9 = var2.getDestination();
         if (var9 == null) {
            var9 = new BufferedImage(var4.x + var4.width, var4.y + var4.height, 12);
         }

         boolean var10 = var4.equals(new Rectangle(0, 0, this.width, this.height)) && var4.equals(new Rectangle(0, 0, var9.getWidth(), var9.getHeight()));
         WritableRaster var11 = var9.getWritableTile(0, 0);
         MultiPixelPackedSampleModel var12 = (MultiPixelPackedSampleModel)var9.getSampleModel();
         if (var10) {
            if (this.abortRequested()) {
               this.processReadAborted();
               return var9;
            }

            this.iis.read(((DataBufferByte)var11.getDataBuffer()).getData(), 0, this.height * var12.getScanlineStride());
            this.processImageUpdate(var9, 0, 0, this.width, this.height, 1, 1, new int[]{0});
            this.processImageProgress(100.0F);
         } else {
            int var13 = (this.width + 7) / 8;
            byte[] var14 = new byte[var13];
            byte[] var15 = ((DataBufferByte)var11.getDataBuffer()).getData();
            int var16 = var12.getScanlineStride();
            this.iis.skipBytes(var13 * var3.y);
            int var17 = var13 * (var6 - 1);
            int[] var18 = new int[var4.width];
            int[] var19 = new int[var4.width];
            int[] var20 = new int[var4.width];
            int[] var21 = new int[var4.width];
            int var22 = var4.x;
            int var23 = var3.x;

            int var24;
            for(var24 = 0; var22 < var4.x + var4.width; var23 += var5) {
               var20[var24] = var23 >> 3;
               var18[var24] = 7 - (var23 & 7);
               var21[var24] = var22 >> 3;
               var19[var24] = 7 - (var22 & 7);
               ++var22;
               ++var24;
            }

            var22 = 0;
            var23 = var3.y;

            for(var24 = var4.y * var16; var22 < var4.height && !this.abortRequested(); var23 += var6) {
               this.iis.read(var14, 0, var13);

               for(int var25 = 0; var25 < var4.width; ++var25) {
                  int var26 = var14[var20[var25]] >> var18[var25] & 1;
                  var15[var24 + var21[var25]] = (byte)(var15[var24 + var21[var25]] | var26 << var19[var25]);
               }

               var24 += var16;
               this.iis.skipBytes(var17);
               this.processImageUpdate(var9, 0, var22, var4.width, 1, 1, 1, new int[]{0});
               this.processImageProgress(100.0F * (float)var22 / (float)var4.height);
               ++var22;
            }
         }

         if (this.abortRequested()) {
            this.processReadAborted();
         } else {
            this.processImageComplete();
         }

         return var9;
      }
   }

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int var1, ImageReadParam var2) throws IOException {
      BufferedImage var3 = this.read(var1, var2);
      return var3.getData();
   }

   public void reset() {
      super.reset();
      this.iis = null;
      this.gotHeader = false;
   }

   boolean isValidWbmpType(int var1) {
      return var1 == 0;
   }
}
