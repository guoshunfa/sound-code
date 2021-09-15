package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageReader extends ImageReader {
   private boolean debug = false;
   private long structPointer = 0L;
   private ImageInputStream iis = null;
   private List imagePositions = null;
   private int numImages = 0;
   protected static final int WARNING_NO_EOI = 0;
   protected static final int WARNING_NO_JFIF_IN_THUMB = 1;
   protected static final int WARNING_IGNORE_INVALID_ICC = 2;
   private static final int MAX_WARNING = 2;
   private int currentImage = -1;
   private int width;
   private int height;
   private int colorSpaceCode;
   private int outColorSpaceCode;
   private int numComponents;
   private ColorSpace iccCS = null;
   private ColorConvertOp convert = null;
   private BufferedImage image = null;
   private WritableRaster raster = null;
   private WritableRaster target = null;
   private DataBufferByte buffer = null;
   private Rectangle destROI = null;
   private int[] destinationBands = null;
   private JPEGMetadata streamMetadata = null;
   private JPEGMetadata imageMetadata = null;
   private int imageMetadataIndex = -1;
   private boolean haveSeeked = false;
   private JPEGQTable[] abbrevQTables = null;
   private JPEGHuffmanTable[] abbrevDCHuffmanTables = null;
   private JPEGHuffmanTable[] abbrevACHuffmanTables = null;
   private int minProgressivePass = 0;
   private int maxProgressivePass = Integer.MAX_VALUE;
   private static final int UNKNOWN = -1;
   private static final int MIN_ESTIMATED_PASSES = 10;
   private int knownPassCount = -1;
   private int pass = 0;
   private float percentToDate = 0.0F;
   private float previousPassPercentage = 0.0F;
   private int progInterval = 0;
   private boolean tablesOnlyChecked = false;
   private Object disposerReferent = new Object();
   private DisposerRecord disposerRecord;
   private Thread theThread = null;
   private int theLockCount = 0;
   private JPEGImageReader.CallBackLock cbLock = new JPEGImageReader.CallBackLock();

   private static native void initReaderIDs(Class var0, Class var1, Class var2);

   public JPEGImageReader(ImageReaderSpi var1) {
      super(var1);
      this.structPointer = this.initJPEGImageReader();
      this.disposerRecord = new JPEGImageReader.JPEGReaderDisposerRecord(this.structPointer);
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
   }

   private native long initJPEGImageReader();

   protected void warningOccurred(int var1) {
      this.cbLock.lock();

      try {
         if (var1 < 0 || var1 > 2) {
            throw new InternalError("Invalid warning index");
         }

         this.processWarningOccurred("com.sun.imageio.plugins.jpeg.JPEGImageReaderResources", Integer.toString(var1));
      } finally {
         this.cbLock.unlock();
      }

   }

   protected void warningWithMessage(String var1) {
      this.cbLock.lock();

      try {
         this.processWarningOccurred(var1);
      } finally {
         this.cbLock.unlock();
      }

   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      this.setThreadLock();

      try {
         this.cbLock.check();
         super.setInput(var1, var2, var3);
         this.ignoreMetadata = var3;
         this.resetInternalState();
         this.iis = (ImageInputStream)var1;
         this.setSource(this.structPointer);
      } finally {
         this.clearThreadLock();
      }

   }

   private int readInputData(byte[] var1, int var2, int var3) throws IOException {
      this.cbLock.lock();

      int var4;
      try {
         var4 = this.iis.read(var1, var2, var3);
      } finally {
         this.cbLock.unlock();
      }

      return var4;
   }

   private long skipInputBytes(long var1) throws IOException {
      this.cbLock.lock();

      long var3;
      try {
         var3 = this.iis.skipBytes(var1);
      } finally {
         this.cbLock.unlock();
      }

      return var3;
   }

   private native void setSource(long var1);

   private void checkTablesOnly() throws IOException {
      if (this.debug) {
         System.out.println("Checking for tables-only image");
      }

      long var1 = this.iis.getStreamPosition();
      if (this.debug) {
         System.out.println("saved pos is " + var1);
         System.out.println("length is " + this.iis.length());
      }

      boolean var3 = this.readNativeHeader(true);
      if (var3) {
         long var4;
         if (this.debug) {
            System.out.println("tables-only image found");
            var4 = this.iis.getStreamPosition();
            System.out.println("pos after return from native is " + var4);
         }

         if (!this.ignoreMetadata) {
            this.iis.seek(var1);
            this.haveSeeked = true;
            this.streamMetadata = new JPEGMetadata(true, false, this.iis, this);
            var4 = this.iis.getStreamPosition();
            if (this.debug) {
               System.out.println("pos after constructing stream metadata is " + var4);
            }
         }

         if (this.hasNextImage()) {
            this.imagePositions.add(new Long(this.iis.getStreamPosition()));
         }
      } else {
         this.imagePositions.add(new Long(var1));
         this.currentImage = 0;
      }

      if (this.seekForwardOnly) {
         Long var6 = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
         this.iis.flushBefore(var6);
      }

      this.tablesOnlyChecked = true;
   }

   public int getNumImages(boolean var1) throws IOException {
      this.setThreadLock();

      int var2;
      try {
         this.cbLock.check();
         var2 = this.getNumImagesOnThread(var1);
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   private void skipPastImage(int var1) {
      this.cbLock.lock();

      try {
         this.gotoImage(var1);
         this.skipImage();
      } catch (IndexOutOfBoundsException | IOException var6) {
      } finally {
         this.cbLock.unlock();
      }

   }

   private int getNumImagesOnThread(boolean var1) throws IOException {
      if (this.numImages != 0) {
         return this.numImages;
      } else if (this.iis == null) {
         throw new IllegalStateException("Input not set");
      } else if (var1) {
         if (this.seekForwardOnly) {
            throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
         } else {
            if (!this.tablesOnlyChecked) {
               this.checkTablesOnly();
            }

            this.iis.mark();
            this.gotoImage(0);
            JPEGBuffer var2 = new JPEGBuffer(this.iis);
            var2.loadBuf(0);
            boolean var3 = false;

            while(!var3) {
               var3 = var2.scanForFF(this);
               switch(var2.buf[var2.bufPtr] & 255) {
               case 216:
                  ++this.numImages;
               case 0:
               case 208:
               case 209:
               case 210:
               case 211:
               case 212:
               case 213:
               case 214:
               case 215:
               case 217:
                  --var2.bufAvail;
                  ++var2.bufPtr;
                  break;
               default:
                  --var2.bufAvail;
                  ++var2.bufPtr;
                  var2.loadBuf(2);
                  int var4 = (var2.buf[var2.bufPtr++] & 255) << 8 | var2.buf[var2.bufPtr++] & 255;
                  var2.bufAvail -= 2;
                  var4 -= 2;
                  var2.skipData(var4);
               }
            }

            this.iis.reset();
            return this.numImages;
         }
      } else {
         return -1;
      }
   }

   private void gotoImage(int var1) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException("Input not set");
      } else if (var1 < this.minIndex) {
         throw new IndexOutOfBoundsException();
      } else {
         if (!this.tablesOnlyChecked) {
            this.checkTablesOnly();
         }

         if (var1 < this.imagePositions.size()) {
            this.iis.seek((Long)((Long)this.imagePositions.get(var1)));
         } else {
            Long var2 = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
            this.iis.seek(var2);
            this.skipImage();

            for(int var3 = this.imagePositions.size(); var3 <= var1; ++var3) {
               if (!this.hasNextImage()) {
                  throw new IndexOutOfBoundsException();
               }

               var2 = new Long(this.iis.getStreamPosition());
               this.imagePositions.add(var2);
               if (this.seekForwardOnly) {
                  this.iis.flushBefore(var2);
               }

               if (var3 < var1) {
                  this.skipImage();
               }
            }
         }

         if (this.seekForwardOnly) {
            this.minIndex = var1;
         }

         this.haveSeeked = true;
      }
   }

   private void skipImage() throws IOException {
      if (this.debug) {
         System.out.println("skipImage called");
      }

      boolean var1 = false;

      for(int var2 = this.iis.read(); var2 != -1; var2 = this.iis.read()) {
         if (var1 && var2 == 217) {
            return;
         }

         var1 = var2 == 255;
      }

      throw new IndexOutOfBoundsException();
   }

   private boolean hasNextImage() throws IOException {
      if (this.debug) {
         System.out.print("hasNextImage called; returning ");
      }

      this.iis.mark();
      boolean var1 = false;

      for(int var2 = this.iis.read(); var2 != -1; var2 = this.iis.read()) {
         if (var1 && var2 == 216) {
            this.iis.reset();
            if (this.debug) {
               System.out.println("true");
            }

            return true;
         }

         var1 = var2 == 255;
      }

      this.iis.reset();
      if (this.debug) {
         System.out.println("false");
      }

      return false;
   }

   private void pushBack(int var1) throws IOException {
      if (this.debug) {
         System.out.println("pushing back " + var1 + " bytes");
      }

      this.cbLock.lock();

      try {
         this.iis.seek(this.iis.getStreamPosition() - (long)var1);
      } finally {
         this.cbLock.unlock();
      }

   }

   private void readHeader(int var1, boolean var2) throws IOException {
      this.gotoImage(var1);
      this.readNativeHeader(var2);
      this.currentImage = var1;
   }

   private boolean readNativeHeader(boolean var1) throws IOException {
      boolean var2 = false;
      var2 = this.readImageHeader(this.structPointer, this.haveSeeked, var1);
      this.haveSeeked = false;
      return var2;
   }

   private native boolean readImageHeader(long var1, boolean var3, boolean var4) throws IOException;

   private void setImageData(int var1, int var2, int var3, int var4, int var5, byte[] var6) {
      this.width = var1;
      this.height = var2;
      this.colorSpaceCode = var3;
      this.outColorSpaceCode = var4;
      this.numComponents = var5;
      if (var6 == null) {
         this.iccCS = null;
      } else {
         ICC_Profile var7 = null;

         try {
            var7 = ICC_Profile.getInstance(var6);
         } catch (IllegalArgumentException var18) {
            this.iccCS = null;
            this.warningOccurred(2);
            return;
         }

         byte[] var8 = var7.getData();
         ICC_Profile var9 = null;
         if (this.iccCS instanceof ICC_ColorSpace) {
            var9 = ((ICC_ColorSpace)this.iccCS).getProfile();
         }

         byte[] var10 = null;
         if (var9 != null) {
            var10 = var9.getData();
         }

         if (var10 == null || !Arrays.equals(var10, var8)) {
            this.iccCS = new ICC_ColorSpace(var7);

            try {
               this.iccCS.fromRGB(new float[]{1.0F, 0.0F, 0.0F});
            } catch (CMMException var17) {
               this.iccCS = null;
               this.cbLock.lock();

               try {
                  this.warningOccurred(2);
               } finally {
                  this.cbLock.unlock();
               }
            }
         }

      }
   }

   public int getWidth(int var1) throws IOException {
      this.setThreadLock();

      int var2;
      try {
         if (this.currentImage != var1) {
            this.cbLock.check();
            this.readHeader(var1, true);
         }

         var2 = this.width;
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   public int getHeight(int var1) throws IOException {
      this.setThreadLock();

      int var2;
      try {
         if (this.currentImage != var1) {
            this.cbLock.check();
            this.readHeader(var1, true);
         }

         var2 = this.height;
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   private ImageTypeProducer getImageType(int var1) {
      ImageTypeProducer var2 = null;
      if (var1 > 0 && var1 < 12) {
         var2 = ImageTypeProducer.getTypeProducer(var1);
      }

      return var2;
   }

   public ImageTypeSpecifier getRawImageType(int var1) throws IOException {
      this.setThreadLock();

      ImageTypeSpecifier var2;
      try {
         if (this.currentImage != var1) {
            this.cbLock.check();
            this.readHeader(var1, true);
         }

         var2 = this.getImageType(this.colorSpaceCode).getType();
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   public Iterator getImageTypes(int var1) throws IOException {
      this.setThreadLock();

      Iterator var2;
      try {
         var2 = this.getImageTypesOnThread(var1);
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   private Iterator getImageTypesOnThread(int var1) throws IOException {
      if (this.currentImage != var1) {
         this.cbLock.check();
         this.readHeader(var1, true);
      }

      ImageTypeProducer var2 = this.getImageType(this.colorSpaceCode);
      ArrayList var3 = new ArrayList(1);
      switch(this.colorSpaceCode) {
      case 1:
         var3.add(var2);
         var3.add(this.getImageType(2));
         break;
      case 2:
         var3.add(var2);
         var3.add(this.getImageType(1));
         var3.add(this.getImageType(5));
         break;
      case 3:
         var3.add(this.getImageType(2));
         if (this.iccCS != null) {
            var3.add(new ImageTypeProducer() {
               protected ImageTypeSpecifier produce() {
                  return ImageTypeSpecifier.createInterleaved(JPEGImageReader.this.iccCS, JPEG.bOffsRGB, 0, false, false);
               }
            });
         }

         var3.add(this.getImageType(1));
         var3.add(this.getImageType(5));
      case 4:
      case 8:
      case 9:
      default:
         break;
      case 5:
         if (var2 != null) {
            var3.add(var2);
            var3.add(this.getImageType(2));
         }
         break;
      case 6:
         var3.add(var2);
         break;
      case 7:
         var3.add(this.getImageType(6));
         break;
      case 10:
         if (var2 != null) {
            var3.add(var2);
         }
      }

      return new ImageTypeIterator(var3.iterator());
   }

   private void checkColorConversion(BufferedImage var1, ImageReadParam var2) throws IIOException {
      if (var2 == null || var2.getSourceBands() == null && var2.getDestinationBands() == null) {
         ColorModel var3 = var1.getColorModel();
         if (var3 instanceof IndexColorModel) {
            throw new IIOException("IndexColorModel not supported");
         } else {
            ColorSpace var4 = var3.getColorSpace();
            int var5 = var4.getType();
            this.convert = null;
            ColorSpace var6;
            switch(this.outColorSpaceCode) {
            case 1:
               if (var5 == 5) {
                  this.setOutColorSpace(this.structPointer, 2);
                  this.outColorSpaceCode = 2;
                  this.numComponents = 3;
               } else if (var5 != 6) {
                  throw new IIOException("Incompatible color conversion");
               }
               break;
            case 2:
               if (var5 == 6) {
                  if (this.colorSpaceCode == 3) {
                     this.setOutColorSpace(this.structPointer, 1);
                     this.outColorSpaceCode = 1;
                     this.numComponents = 1;
                  }
               } else if (this.iccCS != null && var3.getNumComponents() == this.numComponents && var4 != this.iccCS) {
                  this.convert = new ColorConvertOp(this.iccCS, var4, (RenderingHints)null);
               } else if (this.iccCS == null && !var4.isCS_sRGB() && var3.getNumComponents() == this.numComponents) {
                  this.convert = new ColorConvertOp(JPEG.JCS.sRGB, var4, (RenderingHints)null);
               } else if (var5 != 5) {
                  throw new IIOException("Incompatible color conversion");
               }
               break;
            case 3:
            case 4:
            case 7:
            case 8:
            case 9:
            default:
               throw new IIOException("Incompatible color conversion");
            case 5:
               var6 = JPEG.JCS.getYCC();
               if (var6 == null) {
                  throw new IIOException("Incompatible color conversion");
               }

               if (var4 != var6 && var3.getNumComponents() == this.numComponents) {
                  this.convert = new ColorConvertOp(var6, var4, (RenderingHints)null);
               }
               break;
            case 6:
               if (var5 == 5 && var3.getNumComponents() == this.numComponents) {
                  break;
               }

               throw new IIOException("Incompatible color conversion");
            case 10:
               var6 = JPEG.JCS.getYCC();
               if (var6 == null || var4 != var6 || var3.getNumComponents() != this.numComponents) {
                  throw new IIOException("Incompatible color conversion");
               }
            }

         }
      }
   }

   private native void setOutColorSpace(long var1, int var3);

   public ImageReadParam getDefaultReadParam() {
      return new JPEGImageReadParam();
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      this.setThreadLock();

      JPEGMetadata var1;
      try {
         if (!this.tablesOnlyChecked) {
            this.cbLock.check();
            this.checkTablesOnly();
         }

         var1 = this.streamMetadata;
      } finally {
         this.clearThreadLock();
      }

      return var1;
   }

   public IIOMetadata getImageMetadata(int var1) throws IOException {
      this.setThreadLock();

      JPEGMetadata var2;
      try {
         if (this.imageMetadataIndex == var1 && this.imageMetadata != null) {
            var2 = this.imageMetadata;
            return var2;
         }

         this.cbLock.check();
         this.gotoImage(var1);
         this.imageMetadata = new JPEGMetadata(false, false, this.iis, this);
         this.imageMetadataIndex = var1;
         var2 = this.imageMetadata;
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   public BufferedImage read(int var1, ImageReadParam var2) throws IOException {
      this.setThreadLock();

      BufferedImage var4;
      try {
         this.cbLock.check();

         try {
            this.readInternal(var1, var2, false);
         } catch (RuntimeException var9) {
            this.resetLibraryState(this.structPointer);
            throw var9;
         } catch (IOException var10) {
            this.resetLibraryState(this.structPointer);
            throw var10;
         }

         BufferedImage var3 = this.image;
         this.image = null;
         var4 = var3;
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   private Raster readInternal(int var1, ImageReadParam var2, boolean var3) throws IOException {
      this.readHeader(var1, false);
      WritableRaster var4 = null;
      int var5 = 0;
      if (!var3) {
         Iterator var6 = this.getImageTypes(var1);
         if (!var6.hasNext()) {
            throw new IIOException("Unsupported Image Type");
         }

         this.image = getDestination(var2, var6, this.width, this.height);
         var4 = this.image.getRaster();
         var5 = this.image.getSampleModel().getNumBands();
         this.checkColorConversion(this.image, var2);
         checkReadParamBandSettings(var2, this.numComponents, var5);
      } else {
         this.setOutColorSpace(this.structPointer, this.colorSpaceCode);
         this.image = null;
      }

      int[] var16 = JPEG.bandOffsets[this.numComponents - 1];
      int var7 = var3 ? this.numComponents : var5;
      this.destinationBands = null;
      Rectangle var8 = new Rectangle(0, 0, 0, 0);
      this.destROI = new Rectangle(0, 0, 0, 0);
      computeRegions(var2, this.width, this.height, this.image, var8, this.destROI);
      int var9 = 1;
      int var10 = 1;
      this.minProgressivePass = 0;
      this.maxProgressivePass = Integer.MAX_VALUE;
      if (var2 != null) {
         var9 = var2.getSourceXSubsampling();
         var10 = var2.getSourceYSubsampling();
         int[] var11 = var2.getSourceBands();
         if (var11 != null) {
            var16 = var11;
            var7 = var11.length;
         }

         if (!var3) {
            this.destinationBands = var2.getDestinationBands();
         }

         this.minProgressivePass = var2.getSourceMinProgressivePass();
         this.maxProgressivePass = var2.getSourceMaxProgressivePass();
         if (var2 instanceof JPEGImageReadParam) {
            JPEGImageReadParam var12 = (JPEGImageReadParam)var2;
            if (var12.areTablesSet()) {
               this.abbrevQTables = var12.getQTables();
               this.abbrevDCHuffmanTables = var12.getDCHuffmanTables();
               this.abbrevACHuffmanTables = var12.getACHuffmanTables();
            }
         }
      }

      int var17 = this.destROI.width * var7;
      this.buffer = new DataBufferByte(var17);
      int[] var18 = JPEG.bandOffsets[var7 - 1];
      this.raster = Raster.createInterleavedRaster(this.buffer, this.destROI.width, 1, var17, var7, var18, (Point)null);
      if (var3) {
         this.target = Raster.createInterleavedRaster(0, this.destROI.width, this.destROI.height, var17, var7, var18, (Point)null);
      } else {
         this.target = var4;
      }

      int[] var13 = this.target.getSampleModel().getSampleSize();

      for(int var14 = 0; var14 < var13.length; ++var14) {
         if (var13[var14] <= 0 || var13[var14] > 8) {
            throw new IIOException("Illegal band size: should be 0 < size <= 8");
         }
      }

      boolean var19 = this.updateListeners != null || this.progressListeners != null;
      this.initProgressData();
      if (var1 == this.imageMetadataIndex) {
         this.knownPassCount = 0;
         Iterator var15 = this.imageMetadata.markerSequence.iterator();

         while(var15.hasNext()) {
            if (var15.next() instanceof SOSMarkerSegment) {
               ++this.knownPassCount;
            }
         }
      }

      this.progInterval = Math.max((this.target.getHeight() - 1) / 20, 1);
      if (this.knownPassCount > 0) {
         this.progInterval *= this.knownPassCount;
      } else if (this.maxProgressivePass != Integer.MAX_VALUE) {
         this.progInterval *= this.maxProgressivePass - this.minProgressivePass + 1;
      }

      if (this.debug) {
         System.out.println("**** Read Data *****");
         System.out.println("numRasterBands is " + var7);
         System.out.print("srcBands:");

         int var20;
         for(var20 = 0; var20 < var16.length; ++var20) {
            System.out.print(" " + var16[var20]);
         }

         System.out.println();
         System.out.println("destination bands is " + this.destinationBands);
         if (this.destinationBands != null) {
            for(var20 = 0; var20 < this.destinationBands.length; ++var20) {
               System.out.print(" " + this.destinationBands[var20]);
            }

            System.out.println();
         }

         System.out.println("sourceROI is " + var8);
         System.out.println("destROI is " + this.destROI);
         System.out.println("periodX is " + var9);
         System.out.println("periodY is " + var10);
         System.out.println("minProgressivePass is " + this.minProgressivePass);
         System.out.println("maxProgressivePass is " + this.maxProgressivePass);
         System.out.println("callbackUpdates is " + var19);
      }

      this.processImageStarted(this.currentImage);
      boolean var21 = false;
      var21 = this.readImage(var1, this.structPointer, this.buffer.getData(), var7, var16, var13, var8.x, var8.y, var8.width, var8.height, var9, var10, this.abbrevQTables, this.abbrevDCHuffmanTables, this.abbrevACHuffmanTables, this.minProgressivePass, this.maxProgressivePass, var19);
      if (var21) {
         this.processReadAborted();
      } else {
         this.processImageComplete();
      }

      return this.target;
   }

   private void acceptPixels(int var1, boolean var2) {
      if (this.convert != null) {
         this.convert.filter((Raster)this.raster, (WritableRaster)this.raster);
      }

      this.target.setRect(this.destROI.x, this.destROI.y + var1, this.raster);
      this.cbLock.lock();

      try {
         this.processImageUpdate(this.image, this.destROI.x, this.destROI.y + var1, this.raster.getWidth(), 1, 1, 1, this.destinationBands);
         if (var1 > 0 && var1 % this.progInterval == 0) {
            int var3 = this.target.getHeight() - 1;
            float var4 = (float)var1 / (float)var3;
            if (var2) {
               if (this.knownPassCount != -1) {
                  this.processImageProgress(((float)this.pass + var4) * 100.0F / (float)this.knownPassCount);
               } else if (this.maxProgressivePass != Integer.MAX_VALUE) {
                  this.processImageProgress(((float)this.pass + var4) * 100.0F / (float)(this.maxProgressivePass - this.minProgressivePass + 1));
               } else {
                  int var5 = Math.max(2, 10 - this.pass);
                  int var6 = this.pass + var5 - 1;
                  this.progInterval = Math.max(var3 / 20 * var6, var6);
                  if (var1 % this.progInterval == 0) {
                     this.percentToDate = this.previousPassPercentage + (1.0F - this.previousPassPercentage) * var4 / (float)var5;
                     if (this.debug) {
                        System.out.print("pass= " + this.pass);
                        System.out.print(", y= " + var1);
                        System.out.print(", progInt= " + this.progInterval);
                        System.out.print(", % of pass: " + var4);
                        System.out.print(", rem. passes: " + var5);
                        System.out.print(", prev%: " + this.previousPassPercentage);
                        System.out.print(", %ToDate: " + this.percentToDate);
                        System.out.print(" ");
                     }

                     this.processImageProgress(this.percentToDate * 100.0F);
                  }
               }
            } else {
               this.processImageProgress(var4 * 100.0F);
            }
         }
      } finally {
         this.cbLock.unlock();
      }

   }

   private void initProgressData() {
      this.knownPassCount = -1;
      this.pass = 0;
      this.percentToDate = 0.0F;
      this.previousPassPercentage = 0.0F;
      this.progInterval = 0;
   }

   private void passStarted(int var1) {
      this.cbLock.lock();

      try {
         this.pass = var1;
         this.previousPassPercentage = this.percentToDate;
         this.processPassStarted(this.image, var1, this.minProgressivePass, this.maxProgressivePass, 0, 0, 1, 1, this.destinationBands);
      } finally {
         this.cbLock.unlock();
      }

   }

   private void passComplete() {
      this.cbLock.lock();

      try {
         this.processPassComplete(this.image);
      } finally {
         this.cbLock.unlock();
      }

   }

   void thumbnailStarted(int var1) {
      this.cbLock.lock();

      try {
         this.processThumbnailStarted(this.currentImage, var1);
      } finally {
         this.cbLock.unlock();
      }

   }

   void thumbnailProgress(float var1) {
      this.cbLock.lock();

      try {
         this.processThumbnailProgress(var1);
      } finally {
         this.cbLock.unlock();
      }

   }

   void thumbnailComplete() {
      this.cbLock.lock();

      try {
         this.processThumbnailComplete();
      } finally {
         this.cbLock.unlock();
      }

   }

   private native boolean readImage(int var1, long var2, byte[] var4, int var5, int[] var6, int[] var7, int var8, int var9, int var10, int var11, int var12, int var13, JPEGQTable[] var14, JPEGHuffmanTable[] var15, JPEGHuffmanTable[] var16, int var17, int var18, boolean var19);

   public void abort() {
      this.setThreadLock();

      try {
         super.abort();
         this.abortRead(this.structPointer);
      } finally {
         this.clearThreadLock();
      }

   }

   private native void abortRead(long var1);

   private native void resetLibraryState(long var1);

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int var1, ImageReadParam var2) throws IOException {
      this.setThreadLock();
      Raster var3 = null;

      try {
         this.cbLock.check();
         Point var4 = null;
         if (var2 != null) {
            var4 = var2.getDestinationOffset();
            var2.setDestinationOffset(new Point(0, 0));
         }

         var3 = this.readInternal(var1, var2, true);
         if (var4 != null) {
            this.target = this.target.createWritableTranslatedChild(var4.x, var4.y);
         }
      } catch (RuntimeException var9) {
         this.resetLibraryState(this.structPointer);
         throw var9;
      } catch (IOException var10) {
         this.resetLibraryState(this.structPointer);
         throw var10;
      } finally {
         this.clearThreadLock();
      }

      return var3;
   }

   public boolean readerSupportsThumbnails() {
      return true;
   }

   public int getNumThumbnails(int var1) throws IOException {
      this.setThreadLock();

      int var4;
      try {
         this.cbLock.check();
         this.getImageMetadata(var1);
         JFIFMarkerSegment var2 = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
         int var3 = 0;
         if (var2 != null) {
            var3 = var2.thumb == null ? 0 : 1;
            var3 += var2.extSegments.size();
         }

         var4 = var3;
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   public int getThumbnailWidth(int var1, int var2) throws IOException {
      this.setThreadLock();

      int var4;
      try {
         this.cbLock.check();
         if (var2 < 0 || var2 >= this.getNumThumbnails(var1)) {
            throw new IndexOutOfBoundsException("No such thumbnail");
         }

         JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
         var4 = var3.getThumbnailWidth(var2);
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   public int getThumbnailHeight(int var1, int var2) throws IOException {
      this.setThreadLock();

      int var4;
      try {
         this.cbLock.check();
         if (var2 < 0 || var2 >= this.getNumThumbnails(var1)) {
            throw new IndexOutOfBoundsException("No such thumbnail");
         }

         JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
         var4 = var3.getThumbnailHeight(var2);
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   public BufferedImage readThumbnail(int var1, int var2) throws IOException {
      this.setThreadLock();

      BufferedImage var4;
      try {
         this.cbLock.check();
         if (var2 < 0 || var2 >= this.getNumThumbnails(var1)) {
            throw new IndexOutOfBoundsException("No such thumbnail");
         }

         JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
         var4 = var3.getThumbnail(this.iis, var2, this);
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   private void resetInternalState() {
      this.resetReader(this.structPointer);
      this.numImages = 0;
      this.imagePositions = new ArrayList();
      this.currentImage = -1;
      this.image = null;
      this.raster = null;
      this.target = null;
      this.buffer = null;
      this.destROI = null;
      this.destinationBands = null;
      this.streamMetadata = null;
      this.imageMetadata = null;
      this.imageMetadataIndex = -1;
      this.haveSeeked = false;
      this.tablesOnlyChecked = false;
      this.iccCS = null;
      this.initProgressData();
   }

   public void reset() {
      this.setThreadLock();

      try {
         this.cbLock.check();
         super.reset();
      } finally {
         this.clearThreadLock();
      }

   }

   private native void resetReader(long var1);

   public void dispose() {
      this.setThreadLock();

      try {
         this.cbLock.check();
         if (this.structPointer != 0L) {
            this.disposerRecord.dispose();
            this.structPointer = 0L;
         }
      } finally {
         this.clearThreadLock();
      }

   }

   private static native void disposeReader(long var0);

   private synchronized void setThreadLock() {
      Thread var1 = Thread.currentThread();
      if (this.theThread != null) {
         if (this.theThread != var1) {
            throw new IllegalStateException("Attempt to use instance of " + this + " locked on thread " + this.theThread + " from thread " + var1);
         }

         ++this.theLockCount;
      } else {
         this.theThread = var1;
         this.theLockCount = 1;
      }

   }

   private synchronized void clearThreadLock() {
      Thread var1 = Thread.currentThread();
      if (this.theThread != null && this.theThread == var1) {
         --this.theLockCount;
         if (this.theLockCount == 0) {
            this.theThread = null;
         }

      } else {
         throw new IllegalStateException("Attempt to clear thread lock  form wrong thread. Locked thread: " + this.theThread + "; current thread: " + var1);
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("jpeg");
            return null;
         }
      });
      initReaderIDs(ImageInputStream.class, JPEGQTable.class, JPEGHuffmanTable.class);
   }

   private static class CallBackLock {
      private JPEGImageReader.CallBackLock.State lockState;

      CallBackLock() {
         this.lockState = JPEGImageReader.CallBackLock.State.Unlocked;
      }

      void check() {
         if (this.lockState != JPEGImageReader.CallBackLock.State.Unlocked) {
            throw new IllegalStateException("Access to the reader is not allowed");
         }
      }

      private void lock() {
         this.lockState = JPEGImageReader.CallBackLock.State.Locked;
      }

      private void unlock() {
         this.lockState = JPEGImageReader.CallBackLock.State.Unlocked;
      }

      private static enum State {
         Unlocked,
         Locked;
      }
   }

   private static class JPEGReaderDisposerRecord implements DisposerRecord {
      private long pData;

      public JPEGReaderDisposerRecord(long var1) {
         this.pData = var1;
      }

      public synchronized void dispose() {
         if (this.pData != 0L) {
            JPEGImageReader.disposeReader(this.pData);
            this.pData = 0L;
         }

      }
   }
}
