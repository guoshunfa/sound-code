package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.LZWCompressor;
import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.awt.image.ByteComponentRaster;

public class GIFImageWriter extends ImageWriter {
   private static final boolean DEBUG = false;
   static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
   static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
   static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
   private ImageOutputStream stream = null;
   private boolean isWritingSequence = false;
   private boolean wroteSequenceHeader = false;
   private GIFWritableStreamMetadata theStreamMetadata = null;
   private int imageIndex = 0;

   private static int getNumBits(int var0) throws IOException {
      byte var1;
      switch(var0) {
      case 2:
         var1 = 1;
         break;
      case 4:
         var1 = 2;
         break;
      case 8:
         var1 = 3;
         break;
      case 16:
         var1 = 4;
         break;
      case 32:
         var1 = 5;
         break;
      case 64:
         var1 = 6;
         break;
      case 128:
         var1 = 7;
         break;
      case 256:
         var1 = 8;
         break;
      default:
         throw new IOException("Bad palette length: " + var0 + "!");
      }

      return var1;
   }

   private static void computeRegions(Rectangle var0, Dimension var1, ImageWriteParam var2) {
      int var4 = 1;
      int var5 = 1;
      if (var2 != null) {
         int[] var6 = var2.getSourceBands();
         if (var6 != null && (var6.length != 1 || var6[0] != 0)) {
            throw new IllegalArgumentException("Cannot sub-band image!");
         }

         Rectangle var7 = var2.getSourceRegion();
         if (var7 != null) {
            var7 = var7.intersection(var0);
            var0.setBounds(var7);
         }

         int var8 = var2.getSubsamplingXOffset();
         int var9 = var2.getSubsamplingYOffset();
         var0.x += var8;
         var0.y += var9;
         var0.width -= var8;
         var0.height -= var9;
         var4 = var2.getSourceXSubsampling();
         var5 = var2.getSourceYSubsampling();
      }

      var1.setSize((var0.width + var4 - 1) / var4, (var0.height + var5 - 1) / var5);
      if (var1.width <= 0 || var1.height <= 0) {
         throw new IllegalArgumentException("Empty source region!");
      }
   }

   private static byte[] createColorTable(ColorModel var0, SampleModel var1) {
      byte[] var2;
      int var4;
      int var5;
      if (var0 instanceof IndexColorModel) {
         IndexColorModel var3 = (IndexColorModel)var0;
         var4 = var3.getMapSize();
         var5 = getGifPaletteSize(var4);
         byte[] var6 = new byte[var5];
         byte[] var7 = new byte[var5];
         byte[] var8 = new byte[var5];
         var3.getReds(var6);
         var3.getGreens(var7);
         var3.getBlues(var8);

         int var9;
         for(var9 = var4; var9 < var5; ++var9) {
            var6[var9] = var6[0];
            var7[var9] = var7[0];
            var8[var9] = var8[0];
         }

         var2 = new byte[3 * var5];
         var9 = 0;

         for(int var10 = 0; var10 < var5; ++var10) {
            var2[var9++] = var6[var10];
            var2[var9++] = var7[var10];
            var2[var9++] = var8[var10];
         }
      } else if (var1.getNumBands() == 1) {
         int var11 = var1.getSampleSize()[0];
         if (var11 > 8) {
            var11 = 8;
         }

         var4 = 3 * (1 << var11);
         var2 = new byte[var4];

         for(var5 = 0; var5 < var4; ++var5) {
            var2[var5] = (byte)(var5 / 3);
         }
      } else {
         var2 = null;
      }

      return var2;
   }

   private static int getGifPaletteSize(int var0) {
      if (var0 <= 2) {
         return 2;
      } else {
         --var0;
         var0 |= var0 >> 1;
         var0 |= var0 >> 2;
         var0 |= var0 >> 4;
         var0 |= var0 >> 8;
         var0 |= var0 >> 16;
         return var0 + 1;
      }
   }

   public GIFImageWriter(GIFImageWriterSpi var1) {
      super(var1);
   }

   public boolean canWriteSequence() {
      return true;
   }

   private void convertMetadata(String var1, IIOMetadata var2, IIOMetadata var3) {
      String var4 = null;
      String var5 = var2.getNativeMetadataFormatName();
      if (var5 != null && var5.equals(var1)) {
         var4 = var1;
      } else {
         String[] var6 = var2.getExtraMetadataFormatNames();
         if (var6 != null) {
            for(int var7 = 0; var7 < var6.length; ++var7) {
               if (var6[var7].equals(var1)) {
                  var4 = var1;
                  break;
               }
            }
         }
      }

      if (var4 == null && var2.isStandardMetadataFormatSupported()) {
         var4 = "javax_imageio_1.0";
      }

      if (var4 != null) {
         try {
            Node var9 = var2.getAsTree(var4);
            var3.mergeTree(var4, var9);
         } catch (IIOInvalidTreeException var8) {
         }
      }

   }

   public IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("inData == null!");
      } else {
         IIOMetadata var3 = this.getDefaultStreamMetadata(var2);
         this.convertMetadata("javax_imageio_gif_stream_1.0", var1, var3);
         return var3;
      }
   }

   public IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("inData == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("imageType == null!");
      } else {
         GIFWritableImageMetadata var4 = (GIFWritableImageMetadata)this.getDefaultImageMetadata(var2, var3);
         boolean var5 = var4.interlaceFlag;
         this.convertMetadata("javax_imageio_gif_image_1.0", var1, var4);
         if (var3 != null && var3.canWriteProgressive() && var3.getProgressiveMode() != 3) {
            var4.interlaceFlag = var5;
         }

         return var4;
      }
   }

   public void endWriteSequence() throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (!this.isWritingSequence) {
         throw new IllegalStateException("prepareWriteSequence() was not invoked!");
      } else {
         this.writeTrailer();
         this.resetLocal();
      }
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2) {
      GIFWritableImageMetadata var3 = new GIFWritableImageMetadata();
      SampleModel var4 = var1.getSampleModel();
      Rectangle var5 = new Rectangle(var4.getWidth(), var4.getHeight());
      Dimension var6 = new Dimension();
      computeRegions(var5, var6, var2);
      var3.imageWidth = var6.width;
      var3.imageHeight = var6.height;
      if (var2 != null && var2.canWriteProgressive() && var2.getProgressiveMode() == 0) {
         var3.interlaceFlag = false;
      } else {
         var3.interlaceFlag = true;
      }

      ColorModel var7 = var1.getColorModel();
      var3.localColorTable = createColorTable(var7, var4);
      if (var7 instanceof IndexColorModel) {
         int var8 = ((IndexColorModel)var7).getTransparentPixel();
         if (var8 != -1) {
            var3.transparentColorFlag = true;
            var3.transparentColorIndex = var8;
         }
      }

      return var3;
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1) {
      GIFWritableStreamMetadata var2 = new GIFWritableStreamMetadata();
      var2.version = "89a";
      return var2;
   }

   public ImageWriteParam getDefaultWriteParam() {
      return new GIFImageWriteParam(this.getLocale());
   }

   public void prepareWriteSequence(IIOMetadata var1) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("Output is not set.");
      } else {
         this.resetLocal();
         if (var1 == null) {
            this.theStreamMetadata = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata((ImageWriteParam)null);
         } else {
            this.theStreamMetadata = new GIFWritableStreamMetadata();
            this.convertMetadata("javax_imageio_gif_stream_1.0", var1, this.theStreamMetadata);
         }

         this.isWritingSequence = true;
      }
   }

   public void reset() {
      super.reset();
      this.resetLocal();
   }

   private void resetLocal() {
      this.isWritingSequence = false;
      this.wroteSequenceHeader = false;
      this.theStreamMetadata = null;
      this.imageIndex = 0;
   }

   public void setOutput(Object var1) {
      super.setOutput(var1);
      if (var1 != null) {
         if (!(var1 instanceof ImageOutputStream)) {
            throw new IllegalArgumentException("output is not an ImageOutputStream");
         }

         this.stream = (ImageOutputStream)var1;
         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      } else {
         this.stream = null;
      }

   }

   public void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("iioimage == null!");
      } else if (var2.hasRaster()) {
         throw new UnsupportedOperationException("canWriteRasters() == false!");
      } else {
         this.resetLocal();
         GIFWritableStreamMetadata var4;
         if (var1 == null) {
            var4 = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata(var3);
         } else {
            var4 = (GIFWritableStreamMetadata)this.convertStreamMetadata(var1, var3);
         }

         this.write(true, true, var4, var2, var3);
      }
   }

   public void writeToSequence(IIOImage var1, ImageWriteParam var2) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (var1 == null) {
         throw new IllegalArgumentException("image == null!");
      } else if (var1.hasRaster()) {
         throw new UnsupportedOperationException("canWriteRasters() == false!");
      } else if (!this.isWritingSequence) {
         throw new IllegalStateException("prepareWriteSequence() was not invoked!");
      } else {
         this.write(!this.wroteSequenceHeader, false, this.theStreamMetadata, var1, var2);
         if (!this.wroteSequenceHeader) {
            this.wroteSequenceHeader = true;
         }

         ++this.imageIndex;
      }
   }

   private boolean needToCreateIndex(RenderedImage var1) {
      SampleModel var2 = var1.getSampleModel();
      ColorModel var3 = var1.getColorModel();
      return var2.getNumBands() != 1 || var2.getSampleSize()[0] > 8 || var3.getComponentSize()[0] > 8;
   }

   private void write(boolean var1, boolean var2, IIOMetadata var3, IIOImage var4, ImageWriteParam var5) throws IOException {
      this.clearAbortRequest();
      RenderedImage var6 = var4.getRenderedImage();
      if (this.needToCreateIndex(var6)) {
         var6 = PaletteBuilder.createIndexedImage(var6);
         var4.setRenderedImage(var6);
      }

      ColorModel var7 = var6.getColorModel();
      SampleModel var8 = var6.getSampleModel();
      Rectangle var9 = new Rectangle(var6.getMinX(), var6.getMinY(), var6.getWidth(), var6.getHeight());
      Dimension var10 = new Dimension();
      computeRegions(var9, var10, var5);
      GIFWritableImageMetadata var11 = null;
      IndexColorModel var12;
      if (var4.getMetadata() != null) {
         var11 = new GIFWritableImageMetadata();
         this.convertMetadata("javax_imageio_gif_image_1.0", var4.getMetadata(), var11);
         if (var11.localColorTable == null) {
            var11.localColorTable = createColorTable(var7, var8);
            if (var7 instanceof IndexColorModel) {
               var12 = (IndexColorModel)var7;
               int var13 = var12.getTransparentPixel();
               var11.transparentColorFlag = var13 != -1;
               if (var11.transparentColorFlag) {
                  var11.transparentColorIndex = var13;
               }
            }
         }
      }

      var12 = null;
      byte[] var15;
      if (var1) {
         if (var3 == null) {
            throw new IllegalArgumentException("Cannot write null header!");
         }

         GIFWritableStreamMetadata var16 = (GIFWritableStreamMetadata)var3;
         if (var16.version == null) {
            var16.version = "89a";
         }

         if (var16.logicalScreenWidth == -1) {
            var16.logicalScreenWidth = var10.width;
         }

         if (var16.logicalScreenHeight == -1) {
            var16.logicalScreenHeight = var10.height;
         }

         if (var16.colorResolution == -1) {
            var16.colorResolution = var7 != null ? var7.getComponentSize()[0] : var8.getSampleSize()[0];
         }

         if (var16.globalColorTable == null) {
            if (this.isWritingSequence && var11 != null && var11.localColorTable != null) {
               var16.globalColorTable = var11.localColorTable;
            } else if (var11 == null || var11.localColorTable == null) {
               var16.globalColorTable = createColorTable(var7, var8);
            }
         }

         var15 = var16.globalColorTable;
         int var14;
         if (var15 != null) {
            var14 = getNumBits(var15.length / 3);
         } else if (var11 != null && var11.localColorTable != null) {
            var14 = getNumBits(var11.localColorTable.length / 3);
         } else {
            var14 = var8.getSampleSize(0);
         }

         this.writeHeader(var16, var14);
      } else {
         if (!this.isWritingSequence) {
            throw new IllegalArgumentException("Must write header for single image!");
         }

         var15 = this.theStreamMetadata.globalColorTable;
      }

      this.writeImage(var4.getRenderedImage(), var11, var5, var15, var9, var10);
      if (var2) {
         this.writeTrailer();
      }

   }

   private void writeImage(RenderedImage var1, GIFWritableImageMetadata var2, ImageWriteParam var3, byte[] var4, Rectangle var5, Dimension var6) throws IOException {
      ColorModel var7 = var1.getColorModel();
      SampleModel var8 = var1.getSampleModel();
      boolean var9;
      if (var2 == null) {
         var2 = (GIFWritableImageMetadata)this.getDefaultImageMetadata(new ImageTypeSpecifier(var1), var3);
         var9 = var2.transparentColorFlag;
      } else {
         NodeList var10 = null;

         try {
            IIOMetadataNode var11 = (IIOMetadataNode)var2.getAsTree("javax_imageio_gif_image_1.0");
            var10 = var11.getElementsByTagName("GraphicControlExtension");
         } catch (IllegalArgumentException var12) {
         }

         var9 = var10 != null && var10.getLength() > 0;
         if (var3 != null && var3.canWriteProgressive()) {
            if (var3.getProgressiveMode() == 0) {
               var2.interlaceFlag = false;
            } else if (var3.getProgressiveMode() == 1) {
               var2.interlaceFlag = true;
            }
         }
      }

      if (Arrays.equals(var4, var2.localColorTable)) {
         var2.localColorTable = null;
      }

      var2.imageWidth = var6.width;
      var2.imageHeight = var6.height;
      if (var9) {
         this.writeGraphicControlExtension(var2);
      }

      this.writePlainTextExtension(var2);
      this.writeApplicationExtension(var2);
      this.writeCommentExtension(var2);
      int var13 = getNumBits(var2.localColorTable == null ? (var4 == null ? var8.getSampleSize(0) : var4.length / 3) : var2.localColorTable.length / 3);
      this.writeImageDescriptor(var2, var13);
      this.writeRasterData(var1, var5, var6, var3, var2.interlaceFlag);
   }

   private void writeRows(RenderedImage var1, LZWCompressor var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) throws IOException {
      int[] var14 = new int[var7];
      byte[] var15 = new byte[var10];
      Raster var16 = var1.getNumXTiles() == 1 && var1.getNumYTiles() == 1 ? var1.getTile(0, 0) : var1.getData();

      for(int var17 = var8; var17 < var11; var17 += var9) {
         if (var12 % var13 == 0) {
            if (this.abortRequested()) {
               this.processWriteAborted();
               return;
            }

            this.processImageProgress((float)var12 * 100.0F / (float)var11);
         }

         var16.getSamples(var3, var5, var7, 1, 0, (int[])var14);
         int var18 = 0;

         for(int var19 = 0; var18 < var10; var19 += var4) {
            var15[var18] = (byte)var14[var19];
            ++var18;
         }

         var2.compress(var15, 0, var10);
         ++var12;
         var5 += var6;
      }

   }

   private void writeRowsOpt(byte[] var1, int var2, int var3, LZWCompressor var4, int var5, int var6, int var7, int var8, int var9, int var10) throws IOException {
      var2 += var5 * var3;
      var3 *= var6;

      for(int var11 = var5; var11 < var8; var11 += var6) {
         if (var9 % var10 == 0) {
            if (this.abortRequested()) {
               this.processWriteAborted();
               return;
            }

            this.processImageProgress((float)var9 * 100.0F / (float)var8);
         }

         var4.compress(var1, var2, var7);
         ++var9;
         var2 += var3;
      }

   }

   private void writeRasterData(RenderedImage var1, Rectangle var2, Dimension var3, ImageWriteParam var4, boolean var5) throws IOException {
      int var6 = var2.x;
      int var7 = var2.y;
      int var8 = var2.width;
      int var9 = var2.height;
      int var10 = var3.width;
      int var11 = var3.height;
      int var12;
      int var13;
      if (var4 == null) {
         var12 = 1;
         var13 = 1;
      } else {
         var12 = var4.getSourceXSubsampling();
         var13 = var4.getSourceYSubsampling();
      }

      SampleModel var14 = var1.getSampleModel();
      int var15 = var14.getSampleSize()[0];
      int var16 = var15;
      if (var15 == 1) {
         var16 = var15 + 1;
      }

      this.stream.write(var16);
      LZWCompressor var17 = new LZWCompressor(this.stream, var16, false);
      boolean var18 = var12 == 1 && var13 == 1 && var1.getNumXTiles() == 1 && var1.getNumYTiles() == 1 && var14 instanceof ComponentSampleModel && var1.getTile(0, 0) instanceof ByteComponentRaster && var1.getTile(0, 0).getDataBuffer() instanceof DataBufferByte;
      byte var19 = 0;
      int var20 = Math.max(var11 / 20, 1);
      this.processImageStarted(this.imageIndex);
      byte[] var22;
      ComponentSampleModel var23;
      int var24;
      int var25;
      if (var5) {
         int var26;
         if (var18) {
            ByteComponentRaster var21 = (ByteComponentRaster)var1.getTile(0, 0);
            var22 = ((DataBufferByte)var21.getDataBuffer()).getData();
            var23 = (ComponentSampleModel)var21.getSampleModel();
            var24 = var23.getOffset(var6, var7, 0);
            var24 += var21.getDataOffset(0);
            var25 = var23.getScanlineStride();
            this.writeRowsOpt(var22, var24, var25, var17, 0, 8, var10, var11, var19, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 = var19 + var11 / 8;
            this.writeRowsOpt(var22, var24, var25, var17, 4, 8, var10, var11, var26, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 += (var11 - 4) / 8;
            this.writeRowsOpt(var22, var24, var25, var17, 2, 4, var10, var11, var26, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 += (var11 - 2) / 4;
            this.writeRowsOpt(var22, var24, var25, var17, 1, 2, var10, var11, var26, var20);
         } else {
            this.writeRows(var1, var17, var6, var12, var7, 8 * var13, var8, 0, 8, var10, var11, var19, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 = var19 + var11 / 8;
            this.writeRows(var1, var17, var6, var12, var7 + 4 * var13, 8 * var13, var8, 4, 8, var10, var11, var26, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 += (var11 - 4) / 8;
            this.writeRows(var1, var17, var6, var12, var7 + 2 * var13, 4 * var13, var8, 2, 4, var10, var11, var26, var20);
            if (this.abortRequested()) {
               return;
            }

            var26 += (var11 - 2) / 4;
            this.writeRows(var1, var17, var6, var12, var7 + var13, 2 * var13, var8, 1, 2, var10, var11, var26, var20);
         }
      } else if (var18) {
         Raster var27 = var1.getTile(0, 0);
         var22 = ((DataBufferByte)var27.getDataBuffer()).getData();
         var23 = (ComponentSampleModel)var27.getSampleModel();
         var24 = var23.getOffset(var6, var7, 0);
         var25 = var23.getScanlineStride();
         this.writeRowsOpt(var22, var24, var25, var17, 0, 1, var10, var11, var19, var20);
      } else {
         this.writeRows(var1, var17, var6, var12, var7, var13, var8, 0, 1, var10, var11, var19, var20);
      }

      if (!this.abortRequested()) {
         this.processImageProgress(100.0F);
         var17.flush();
         this.stream.write(0);
         this.processImageComplete();
      }
   }

   private void writeHeader(String var1, int var2, int var3, int var4, int var5, int var6, boolean var7, int var8, byte[] var9) throws IOException {
      try {
         this.stream.writeBytes("GIF" + var1);
         this.stream.writeShort((short)var2);
         this.stream.writeShort((short)var3);
         int var10 = var9 != null ? 128 : 0;
         var10 |= (var4 - 1 & 7) << 4;
         if (var7) {
            var10 |= 8;
         }

         var10 |= var8 - 1;
         this.stream.write(var10);
         this.stream.write(var6);
         this.stream.write(var5);
         if (var9 != null) {
            this.stream.write(var9);
         }

      } catch (IOException var11) {
         throw new IIOException("I/O error writing header!", var11);
      }
   }

   private void writeHeader(IIOMetadata var1, int var2) throws IOException {
      GIFWritableStreamMetadata var3;
      if (var1 instanceof GIFWritableStreamMetadata) {
         var3 = (GIFWritableStreamMetadata)var1;
      } else {
         var3 = new GIFWritableStreamMetadata();
         Node var4 = var1.getAsTree("javax_imageio_gif_stream_1.0");
         var3.setFromTree("javax_imageio_gif_stream_1.0", var4);
      }

      this.writeHeader(var3.version, var3.logicalScreenWidth, var3.logicalScreenHeight, var3.colorResolution, var3.pixelAspectRatio, var3.backgroundColorIndex, var3.sortFlag, var2, var3.globalColorTable);
   }

   private void writeGraphicControlExtension(int var1, boolean var2, boolean var3, int var4, int var5) throws IOException {
      try {
         this.stream.write(33);
         this.stream.write(249);
         this.stream.write(4);
         int var6 = (var1 & 3) << 2;
         if (var2) {
            var6 |= 2;
         }

         if (var3) {
            var6 |= 1;
         }

         this.stream.write(var6);
         this.stream.writeShort((short)var4);
         this.stream.write(var5);
         this.stream.write(0);
      } catch (IOException var7) {
         throw new IIOException("I/O error writing Graphic Control Extension!", var7);
      }
   }

   private void writeGraphicControlExtension(GIFWritableImageMetadata var1) throws IOException {
      this.writeGraphicControlExtension(var1.disposalMethod, var1.userInputFlag, var1.transparentColorFlag, var1.delayTime, var1.transparentColorIndex);
   }

   private void writeBlocks(byte[] var1) throws IOException {
      int var3;
      if (var1 != null && var1.length > 0) {
         for(int var2 = 0; var2 < var1.length; var2 += var3) {
            var3 = Math.min(var1.length - var2, 255);
            this.stream.write(var3);
            this.stream.write(var1, var2, var3);
         }
      }

   }

   private void writePlainTextExtension(GIFWritableImageMetadata var1) throws IOException {
      if (var1.hasPlainTextExtension) {
         try {
            this.stream.write(33);
            this.stream.write(1);
            this.stream.write(12);
            this.stream.writeShort(var1.textGridLeft);
            this.stream.writeShort(var1.textGridTop);
            this.stream.writeShort(var1.textGridWidth);
            this.stream.writeShort(var1.textGridHeight);
            this.stream.write(var1.characterCellWidth);
            this.stream.write(var1.characterCellHeight);
            this.stream.write(var1.textForegroundColor);
            this.stream.write(var1.textBackgroundColor);
            this.writeBlocks(var1.text);
            this.stream.write(0);
         } catch (IOException var3) {
            throw new IIOException("I/O error writing Plain Text Extension!", var3);
         }
      }

   }

   private void writeApplicationExtension(GIFWritableImageMetadata var1) throws IOException {
      if (var1.applicationIDs != null) {
         Iterator var2 = var1.applicationIDs.iterator();
         Iterator var3 = var1.authenticationCodes.iterator();
         Iterator var4 = var1.applicationData.iterator();

         while(var2.hasNext()) {
            try {
               this.stream.write(33);
               this.stream.write(255);
               this.stream.write(11);
               this.stream.write((byte[])((byte[])var2.next()), 0, 8);
               this.stream.write((byte[])((byte[])var3.next()), 0, 3);
               this.writeBlocks((byte[])((byte[])var4.next()));
               this.stream.write(0);
            } catch (IOException var6) {
               throw new IIOException("I/O error writing Application Extension!", var6);
            }
         }
      }

   }

   private void writeCommentExtension(GIFWritableImageMetadata var1) throws IOException {
      if (var1.comments != null) {
         try {
            Iterator var2 = var1.comments.iterator();

            while(var2.hasNext()) {
               this.stream.write(33);
               this.stream.write(254);
               this.writeBlocks((byte[])((byte[])var2.next()));
               this.stream.write(0);
            }
         } catch (IOException var3) {
            throw new IIOException("I/O error writing Comment Extension!", var3);
         }
      }

   }

   private void writeImageDescriptor(int var1, int var2, int var3, int var4, boolean var5, boolean var6, int var7, byte[] var8) throws IOException {
      try {
         this.stream.write(44);
         this.stream.writeShort((short)var1);
         this.stream.writeShort((short)var2);
         this.stream.writeShort((short)var3);
         this.stream.writeShort((short)var4);
         int var9 = var8 != null ? 128 : 0;
         if (var5) {
            var9 |= 64;
         }

         if (var6) {
            var9 |= 8;
         }

         var9 |= var7 - 1;
         this.stream.write(var9);
         if (var8 != null) {
            this.stream.write(var8);
         }

      } catch (IOException var10) {
         throw new IIOException("I/O error writing Image Descriptor!", var10);
      }
   }

   private void writeImageDescriptor(GIFWritableImageMetadata var1, int var2) throws IOException {
      this.writeImageDescriptor(var1.imageLeftPosition, var1.imageTopPosition, var1.imageWidth, var1.imageHeight, var1.interlaceFlag, var1.sortFlag, var2, var1.localColorTable);
   }

   private void writeTrailer() throws IOException {
      this.stream.write(59);
   }
}
