package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class GIFImageReader extends ImageReader {
   ImageInputStream stream = null;
   boolean gotHeader = false;
   GIFStreamMetadata streamMetadata = null;
   int currIndex = -1;
   GIFImageMetadata imageMetadata = null;
   List imageStartPosition = new ArrayList();
   int imageMetadataLength;
   int numImages = -1;
   byte[] block = new byte[255];
   int blockLength = 0;
   int bitPos = 0;
   int nextByte = 0;
   int initCodeSize;
   int clearCode;
   int eofCode;
   int next32Bits = 0;
   boolean lastBlockFound = false;
   BufferedImage theImage = null;
   WritableRaster theTile = null;
   int width = -1;
   int height = -1;
   int streamX = -1;
   int streamY = -1;
   int rowsDone = 0;
   int interlacePass = 0;
   private byte[] fallbackColorTable = null;
   static final int[] interlaceIncrement = new int[]{8, 8, 4, 2, -1};
   static final int[] interlaceOffset = new int[]{0, 4, 2, 1, -1};
   Rectangle sourceRegion;
   int sourceXSubsampling;
   int sourceYSubsampling;
   int sourceMinProgressivePass;
   int sourceMaxProgressivePass;
   Point destinationOffset;
   Rectangle destinationRegion;
   int updateMinY;
   int updateYStep;
   boolean decodeThisRow = true;
   int destY = 0;
   byte[] rowBuf;
   private static byte[] defaultPalette = null;

   public GIFImageReader(ImageReaderSpi var1) {
      super(var1);
   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      super.setInput(var1, var2, var3);
      if (var1 != null) {
         if (!(var1 instanceof ImageInputStream)) {
            throw new IllegalArgumentException("input not an ImageInputStream!");
         }

         this.stream = (ImageInputStream)var1;
      } else {
         this.stream = null;
      }

      this.resetStreamSettings();
   }

   public int getNumImages(boolean var1) throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else if (this.seekForwardOnly && var1) {
         throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
      } else if (this.numImages > 0) {
         return this.numImages;
      } else {
         if (var1) {
            this.numImages = this.locateImage(Integer.MAX_VALUE) + 1;
         }

         return this.numImages;
      }
   }

   private void checkIndex(int var1) {
      if (var1 < this.minIndex) {
         throw new IndexOutOfBoundsException("imageIndex < minIndex!");
      } else {
         if (this.seekForwardOnly) {
            this.minIndex = var1;
         }

      }
   }

   public int getWidth(int var1) throws IIOException {
      this.checkIndex(var1);
      int var2 = this.locateImage(var1);
      if (var2 != var1) {
         throw new IndexOutOfBoundsException();
      } else {
         this.readMetadata();
         return this.imageMetadata.imageWidth;
      }
   }

   public int getHeight(int var1) throws IIOException {
      this.checkIndex(var1);
      int var2 = this.locateImage(var1);
      if (var2 != var1) {
         throw new IndexOutOfBoundsException();
      } else {
         this.readMetadata();
         return this.imageMetadata.imageHeight;
      }
   }

   private ImageTypeSpecifier createIndexed(byte[] var1, byte[] var2, byte[] var3, int var4) {
      IndexColorModel var5;
      if (this.imageMetadata.transparentColorFlag) {
         int var6 = Math.min(this.imageMetadata.transparentColorIndex, var1.length - 1);
         var5 = new IndexColorModel(var4, var1.length, var1, var2, var3, var6);
      } else {
         var5 = new IndexColorModel(var4, var1.length, var1, var2, var3);
      }

      Object var8;
      if (var4 == 8) {
         int[] var7 = new int[]{0};
         var8 = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, var7);
      } else {
         var8 = new MultiPixelPackedSampleModel(0, 1, 1, var4);
      }

      return new ImageTypeSpecifier(var5, (SampleModel)var8);
   }

   public Iterator getImageTypes(int var1) throws IIOException {
      this.checkIndex(var1);
      int var2 = this.locateImage(var1);
      if (var2 != var1) {
         throw new IndexOutOfBoundsException();
      } else {
         this.readMetadata();
         ArrayList var3 = new ArrayList(1);
         byte[] var4;
         if (this.imageMetadata.localColorTable != null) {
            var4 = this.imageMetadata.localColorTable;
            this.fallbackColorTable = this.imageMetadata.localColorTable;
         } else {
            var4 = this.streamMetadata.globalColorTable;
         }

         if (var4 == null) {
            if (this.fallbackColorTable == null) {
               this.processWarningOccurred("Use default color table.");
               this.fallbackColorTable = getDefaultPalette();
            }

            var4 = this.fallbackColorTable;
         }

         int var5 = var4.length / 3;
         byte var6;
         if (var5 == 2) {
            var6 = 1;
         } else if (var5 == 4) {
            var6 = 2;
         } else if (var5 != 8 && var5 != 16) {
            var6 = 8;
         } else {
            var6 = 4;
         }

         int var7 = 1 << var6;
         byte[] var8 = new byte[var7];
         byte[] var9 = new byte[var7];
         byte[] var10 = new byte[var7];
         int var11 = 0;

         for(int var12 = 0; var12 < var5; ++var12) {
            var8[var12] = var4[var11++];
            var9[var12] = var4[var11++];
            var10[var12] = var4[var11++];
         }

         var3.add(this.createIndexed(var8, var9, var10, var6));
         return var3.iterator();
      }
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getStreamMetadata() throws IIOException {
      this.readHeader();
      return this.streamMetadata;
   }

   public IIOMetadata getImageMetadata(int var1) throws IIOException {
      this.checkIndex(var1);
      int var2 = this.locateImage(var1);
      if (var2 != var1) {
         throw new IndexOutOfBoundsException("Bad image index!");
      } else {
         this.readMetadata();
         return this.imageMetadata;
      }
   }

   private void initNext32Bits() {
      this.next32Bits = this.block[0] & 255;
      this.next32Bits |= (this.block[1] & 255) << 8;
      this.next32Bits |= (this.block[2] & 255) << 16;
      this.next32Bits |= this.block[3] << 24;
      this.nextByte = 4;
   }

   private int getCode(int var1, int var2) throws IOException {
      if (this.bitPos + var1 > 32) {
         return this.eofCode;
      } else {
         int var3 = this.next32Bits >> this.bitPos & var2;

         for(this.bitPos += var1; this.bitPos >= 8 && !this.lastBlockFound; this.next32Bits |= this.block[this.nextByte++] << 24) {
            this.next32Bits >>>= 8;
            this.bitPos -= 8;
            if (this.nextByte >= this.blockLength) {
               this.blockLength = this.stream.readUnsignedByte();
               if (this.blockLength == 0) {
                  this.lastBlockFound = true;
                  return var3;
               }

               int var4 = this.blockLength;

               int var6;
               for(int var5 = 0; var4 > 0; var4 -= var6) {
                  var6 = this.stream.read(this.block, var5, var4);
                  var5 += var6;
               }

               this.nextByte = 0;
            }
         }

         return var3;
      }
   }

   public void initializeStringTable(int[] var1, byte[] var2, byte[] var3, int[] var4) {
      int var5 = 1 << this.initCodeSize;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         var1[var6] = -1;
         var2[var6] = (byte)var6;
         var3[var6] = (byte)var6;
         var4[var6] = 1;
      }

      for(var6 = var5; var6 < 4096; ++var6) {
         var1[var6] = -1;
         var4[var6] = 1;
      }

   }

   private void outputRow() {
      int var1 = Math.min(this.sourceRegion.width, this.destinationRegion.width * this.sourceXSubsampling);
      int var2 = this.destinationRegion.x;
      if (this.sourceXSubsampling == 1) {
         this.theTile.setDataElements(var2, this.destY, var1, 1, this.rowBuf);
      } else {
         for(int var3 = 0; var3 < var1; ++var2) {
            this.theTile.setSample(var2, this.destY, 0, this.rowBuf[var3] & 255);
            var3 += this.sourceXSubsampling;
         }
      }

      if (this.updateListeners != null) {
         int[] var4 = new int[]{0};
         this.processImageUpdate(this.theImage, var2, this.destY, var1, 1, 1, this.updateYStep, var4);
      }

   }

   private void computeDecodeThisRow() {
      this.decodeThisRow = this.destY < this.destinationRegion.y + this.destinationRegion.height && this.streamY >= this.sourceRegion.y && this.streamY < this.sourceRegion.y + this.sourceRegion.height && (this.streamY - this.sourceRegion.y) % this.sourceYSubsampling == 0;
   }

   private void outputPixels(byte[] var1, int var2) {
      if (this.interlacePass >= this.sourceMinProgressivePass && this.interlacePass <= this.sourceMaxProgressivePass) {
         for(int var3 = 0; var3 < var2; ++var3) {
            if (this.streamX >= this.sourceRegion.x) {
               this.rowBuf[this.streamX - this.sourceRegion.x] = var1[var3];
            }

            ++this.streamX;
            if (this.streamX == this.width) {
               ++this.rowsDone;
               this.processImageProgress(100.0F * (float)this.rowsDone / (float)this.height);
               if (this.decodeThisRow) {
                  this.outputRow();
               }

               this.streamX = 0;
               if (this.imageMetadata.interlaceFlag) {
                  this.streamY += interlaceIncrement[this.interlacePass];
                  if (this.streamY >= this.height) {
                     if (this.updateListeners != null) {
                        this.processPassComplete(this.theImage);
                     }

                     ++this.interlacePass;
                     if (this.interlacePass > this.sourceMaxProgressivePass) {
                        return;
                     }

                     this.streamY = interlaceOffset[this.interlacePass];
                     this.startPass(this.interlacePass);
                  }
               } else {
                  ++this.streamY;
               }

               this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
               this.computeDecodeThisRow();
            }
         }

      }
   }

   private void readHeader() throws IIOException {
      if (!this.gotHeader) {
         if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
         } else {
            this.streamMetadata = new GIFStreamMetadata();

            try {
               this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
               byte[] var1 = new byte[6];
               this.stream.readFully(var1);
               StringBuffer var2 = new StringBuffer(3);
               var2.append((char)var1[3]);
               var2.append((char)var1[4]);
               var2.append((char)var1[5]);
               this.streamMetadata.version = var2.toString();
               this.streamMetadata.logicalScreenWidth = this.stream.readUnsignedShort();
               this.streamMetadata.logicalScreenHeight = this.stream.readUnsignedShort();
               int var3 = this.stream.readUnsignedByte();
               boolean var4 = (var3 & 128) != 0;
               this.streamMetadata.colorResolution = (var3 >> 4 & 7) + 1;
               this.streamMetadata.sortFlag = (var3 & 8) != 0;
               int var5 = 1 << (var3 & 7) + 1;
               this.streamMetadata.backgroundColorIndex = this.stream.readUnsignedByte();
               this.streamMetadata.pixelAspectRatio = this.stream.readUnsignedByte();
               if (var4) {
                  this.streamMetadata.globalColorTable = new byte[3 * var5];
                  this.stream.readFully(this.streamMetadata.globalColorTable);
               } else {
                  this.streamMetadata.globalColorTable = null;
               }

               this.imageStartPosition.add(this.stream.getStreamPosition());
            } catch (IOException var6) {
               throw new IIOException("I/O error reading header!", var6);
            }

            this.gotHeader = true;
         }
      }
   }

   private boolean skipImage() throws IIOException {
      try {
         label56:
         while(true) {
            int var1 = this.stream.readUnsignedByte();
            int var2;
            int var3;
            boolean var7;
            if (var1 != 44) {
               if (var1 == 59) {
                  return false;
               }

               if (var1 == 33) {
                  var2 = this.stream.readUnsignedByte();
                  var7 = false;

                  while(true) {
                     var3 = this.stream.readUnsignedByte();
                     this.stream.skipBytes(var3);
                     if (var3 <= 0) {
                        continue label56;
                     }
                  }
               }

               if (var1 == 0) {
                  return false;
               }

               boolean var6 = false;

               while(true) {
                  var2 = this.stream.readUnsignedByte();
                  this.stream.skipBytes(var2);
                  if (var2 <= 0) {
                     continue label56;
                  }
               }
            }

            this.stream.skipBytes(8);
            var2 = this.stream.readUnsignedByte();
            if ((var2 & 128) != 0) {
               var3 = (var2 & 7) + 1;
               this.stream.skipBytes(3 * (1 << var3));
            }

            this.stream.skipBytes(1);
            var7 = false;

            do {
               var3 = this.stream.readUnsignedByte();
               this.stream.skipBytes(var3);
            } while(var3 > 0);

            return true;
         }
      } catch (EOFException var4) {
         return false;
      } catch (IOException var5) {
         throw new IIOException("I/O error locating image!", var5);
      }
   }

   private int locateImage(int var1) throws IIOException {
      this.readHeader();

      try {
         int var2 = Math.min(var1, this.imageStartPosition.size() - 1);
         Long var3 = (Long)this.imageStartPosition.get(var2);
         this.stream.seek(var3);

         while(var2 < var1) {
            if (!this.skipImage()) {
               --var2;
               return var2;
            }

            Long var4 = new Long(this.stream.getStreamPosition());
            this.imageStartPosition.add(var4);
            ++var2;
         }
      } catch (IOException var5) {
         throw new IIOException("Couldn't seek!", var5);
      }

      if (this.currIndex != var1) {
         this.imageMetadata = null;
      }

      this.currIndex = var1;
      return var1;
   }

   private byte[] concatenateBlocks() throws IOException {
      byte[] var1 = new byte[0];

      while(true) {
         int var2 = this.stream.readUnsignedByte();
         if (var2 == 0) {
            return var1;
         }

         byte[] var3 = new byte[var1.length + var2];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         this.stream.readFully(var3, var1.length, var2);
         var1 = var3;
      }
   }

   private void readMetadata() throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else {
         try {
            this.imageMetadata = new GIFImageMetadata();
            long var1 = this.stream.getStreamPosition();

            while(true) {
               int var3 = this.stream.readUnsignedByte();
               int var4;
               boolean var5;
               int var17;
               if (var3 == 44) {
                  this.imageMetadata.imageLeftPosition = this.stream.readUnsignedShort();
                  this.imageMetadata.imageTopPosition = this.stream.readUnsignedShort();
                  this.imageMetadata.imageWidth = this.stream.readUnsignedShort();
                  this.imageMetadata.imageHeight = this.stream.readUnsignedShort();
                  var4 = this.stream.readUnsignedByte();
                  var5 = (var4 & 128) != 0;
                  this.imageMetadata.interlaceFlag = (var4 & 64) != 0;
                  this.imageMetadata.sortFlag = (var4 & 32) != 0;
                  var17 = 1 << (var4 & 7) + 1;
                  if (var5) {
                     this.imageMetadata.localColorTable = new byte[3 * var17];
                     this.stream.readFully(this.imageMetadata.localColorTable);
                  } else {
                     this.imageMetadata.localColorTable = null;
                  }

                  this.imageMetadataLength = (int)(this.stream.getStreamPosition() - var1);
                  return;
               }

               if (var3 != 33) {
                  if (var3 == 59) {
                     throw new IndexOutOfBoundsException("Attempt to read past end of image sequence!");
                  }

                  throw new IIOException("Unexpected block type " + var3 + "!");
               }

               var4 = this.stream.readUnsignedByte();
               int var15;
               if (var4 == 249) {
                  var15 = this.stream.readUnsignedByte();
                  var17 = this.stream.readUnsignedByte();
                  this.imageMetadata.disposalMethod = var17 >> 2 & 3;
                  this.imageMetadata.userInputFlag = (var17 & 2) != 0;
                  this.imageMetadata.transparentColorFlag = (var17 & 1) != 0;
                  this.imageMetadata.delayTime = this.stream.readUnsignedShort();
                  this.imageMetadata.transparentColorIndex = this.stream.readUnsignedByte();
                  int var18 = this.stream.readUnsignedByte();
               } else if (var4 == 1) {
                  var15 = this.stream.readUnsignedByte();
                  this.imageMetadata.hasPlainTextExtension = true;
                  this.imageMetadata.textGridLeft = this.stream.readUnsignedShort();
                  this.imageMetadata.textGridTop = this.stream.readUnsignedShort();
                  this.imageMetadata.textGridWidth = this.stream.readUnsignedShort();
                  this.imageMetadata.textGridHeight = this.stream.readUnsignedShort();
                  this.imageMetadata.characterCellWidth = this.stream.readUnsignedByte();
                  this.imageMetadata.characterCellHeight = this.stream.readUnsignedByte();
                  this.imageMetadata.textForegroundColor = this.stream.readUnsignedByte();
                  this.imageMetadata.textBackgroundColor = this.stream.readUnsignedByte();
                  this.imageMetadata.text = this.concatenateBlocks();
               } else if (var4 == 254) {
                  byte[] var16 = this.concatenateBlocks();
                  if (this.imageMetadata.comments == null) {
                     this.imageMetadata.comments = new ArrayList();
                  }

                  this.imageMetadata.comments.add(var16);
               } else if (var4 == 255) {
                  var15 = this.stream.readUnsignedByte();
                  byte[] var6 = new byte[8];
                  byte[] var7 = new byte[3];
                  byte[] var8 = new byte[var15];
                  this.stream.readFully(var8);
                  int var9 = this.copyData(var8, 0, var6);
                  var9 = this.copyData(var8, var9, var7);
                  byte[] var10 = this.concatenateBlocks();
                  if (var9 < var15) {
                     int var11 = var15 - var9;
                     byte[] var12 = new byte[var11 + var10.length];
                     System.arraycopy(var8, var9, var12, 0, var11);
                     System.arraycopy(var10, 0, var12, var11, var10.length);
                     var10 = var12;
                  }

                  if (this.imageMetadata.applicationIDs == null) {
                     this.imageMetadata.applicationIDs = new ArrayList();
                     this.imageMetadata.authenticationCodes = new ArrayList();
                     this.imageMetadata.applicationData = new ArrayList();
                  }

                  this.imageMetadata.applicationIDs.add(var6);
                  this.imageMetadata.authenticationCodes.add(var7);
                  this.imageMetadata.applicationData.add(var10);
               } else {
                  var5 = false;

                  while(true) {
                     var15 = this.stream.readUnsignedByte();
                     this.stream.skipBytes(var15);
                     if (var15 <= 0) {
                        break;
                     }
                  }
               }
            }
         } catch (IIOException var13) {
            throw var13;
         } catch (IOException var14) {
            throw new IIOException("I/O error reading image metadata!", var14);
         }
      }
   }

   private int copyData(byte[] var1, int var2, byte[] var3) {
      int var4 = var3.length;
      int var5 = var1.length - var2;
      if (var4 > var5) {
         var4 = var5;
      }

      System.arraycopy(var1, var2, var3, 0, var4);
      return var2 + var4;
   }

   private void startPass(int var1) {
      if (this.updateListeners != null && this.imageMetadata.interlaceFlag) {
         int var2 = interlaceOffset[this.interlacePass];
         int var3 = interlaceIncrement[this.interlacePass];
         int[] var4 = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, this.destinationRegion.x, this.destinationRegion.y, this.destinationRegion.x + this.destinationRegion.width - 1, this.destinationRegion.y + this.destinationRegion.height - 1, this.sourceXSubsampling, this.sourceYSubsampling, 0, var2, this.destinationRegion.width, (this.destinationRegion.height + var3 - 1) / var3, 1, var3);
         this.updateMinY = var4[1];
         this.updateYStep = var4[5];
         int[] var5 = new int[]{0};
         this.processPassStarted(this.theImage, this.interlacePass, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, 0, this.updateMinY, 1, this.updateYStep, var5);
      }
   }

   public BufferedImage read(int var1, ImageReadParam var2) throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("Input not set!");
      } else {
         this.checkIndex(var1);
         int var3 = this.locateImage(var1);
         if (var3 != var1) {
            throw new IndexOutOfBoundsException("imageIndex out of bounds!");
         } else {
            this.clearAbortRequest();
            this.readMetadata();
            if (var2 == null) {
               var2 = this.getDefaultReadParam();
            }

            Iterator var4 = this.getImageTypes(var1);
            this.theImage = getDestination(var2, var4, this.imageMetadata.imageWidth, this.imageMetadata.imageHeight);
            this.theTile = this.theImage.getWritableTile(0, 0);
            this.width = this.imageMetadata.imageWidth;
            this.height = this.imageMetadata.imageHeight;
            this.streamX = 0;
            this.streamY = 0;
            this.rowsDone = 0;
            this.interlacePass = 0;
            this.sourceRegion = new Rectangle(0, 0, 0, 0);
            this.destinationRegion = new Rectangle(0, 0, 0, 0);
            computeRegions(var2, this.width, this.height, this.theImage, this.sourceRegion, this.destinationRegion);
            this.destinationOffset = new Point(this.destinationRegion.x, this.destinationRegion.y);
            this.sourceXSubsampling = var2.getSourceXSubsampling();
            this.sourceYSubsampling = var2.getSourceYSubsampling();
            this.sourceMinProgressivePass = Math.max(var2.getSourceMinProgressivePass(), 0);
            this.sourceMaxProgressivePass = Math.min(var2.getSourceMaxProgressivePass(), 3);
            this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
            this.computeDecodeThisRow();
            this.processImageStarted(var1);
            this.startPass(0);
            this.rowBuf = new byte[this.width];

            try {
               this.initCodeSize = this.stream.readUnsignedByte();
               this.blockLength = this.stream.readUnsignedByte();
               int var5 = this.blockLength;

               int var7;
               for(int var6 = 0; var5 > 0; var6 += var7) {
                  var7 = this.stream.read(this.block, var6, var5);
                  var5 -= var7;
               }

               this.bitPos = 0;
               this.nextByte = 0;
               this.lastBlockFound = false;
               this.interlacePass = 0;
               this.initNext32Bits();
               this.clearCode = 1 << this.initCodeSize;
               this.eofCode = this.clearCode + 1;
               int var8 = 0;
               int[] var9 = new int[4096];
               byte[] var10 = new byte[4096];
               byte[] var11 = new byte[4096];
               int[] var12 = new int[4096];
               byte[] var13 = new byte[4096];
               this.initializeStringTable(var9, var10, var11, var12);
               int var14 = (1 << this.initCodeSize) + 2;
               int var15 = this.initCodeSize + 1;

               for(int var16 = (1 << var15) - 1; !this.abortRequested(); var8 = var7) {
                  var7 = this.getCode(var15, var16);
                  int var17;
                  if (var7 == this.clearCode) {
                     this.initializeStringTable(var9, var10, var11, var12);
                     var14 = (1 << this.initCodeSize) + 2;
                     var15 = this.initCodeSize + 1;
                     var16 = (1 << var15) - 1;
                     var7 = this.getCode(var15, var16);
                     if (var7 == this.eofCode) {
                        this.processImageComplete();
                        return this.theImage;
                     }
                  } else {
                     if (var7 == this.eofCode) {
                        this.processImageComplete();
                        return this.theImage;
                     }

                     if (var7 < var14) {
                        var17 = var7;
                     } else {
                        var17 = var8;
                        if (var7 != var14) {
                           this.processWarningOccurred("Out-of-sequence code!");
                        }
                     }

                     var9[var14] = var8;
                     var10[var14] = var11[var17];
                     var11[var14] = var11[var8];
                     var12[var14] = var12[var8] + 1;
                     ++var14;
                     if (var14 == 1 << var15 && var14 < 4096) {
                        ++var15;
                        var16 = (1 << var15) - 1;
                     }
                  }

                  var17 = var7;
                  int var18 = var12[var7];

                  for(int var19 = var18 - 1; var19 >= 0; --var19) {
                     var13[var19] = var10[var17];
                     var17 = var9[var17];
                  }

                  this.outputPixels(var13, var18);
               }

               this.processReadAborted();
               return this.theImage;
            } catch (IOException var20) {
               var20.printStackTrace();
               throw new IIOException("I/O error reading image!", var20);
            }
         }
      }
   }

   public void reset() {
      super.reset();
      this.resetStreamSettings();
   }

   private void resetStreamSettings() {
      this.gotHeader = false;
      this.streamMetadata = null;
      this.currIndex = -1;
      this.imageMetadata = null;
      this.imageStartPosition = new ArrayList();
      this.numImages = -1;
      this.blockLength = 0;
      this.bitPos = 0;
      this.nextByte = 0;
      this.next32Bits = 0;
      this.lastBlockFound = false;
      this.theImage = null;
      this.theTile = null;
      this.width = -1;
      this.height = -1;
      this.streamX = -1;
      this.streamY = -1;
      this.rowsDone = 0;
      this.interlacePass = 0;
      this.fallbackColorTable = null;
   }

   private static synchronized byte[] getDefaultPalette() {
      if (defaultPalette == null) {
         BufferedImage var0 = new BufferedImage(1, 1, 13);
         IndexColorModel var1 = (IndexColorModel)var0.getColorModel();
         int var2 = var1.getMapSize();
         byte[] var3 = new byte[var2];
         byte[] var4 = new byte[var2];
         byte[] var5 = new byte[var2];
         var1.getReds(var3);
         var1.getGreens(var4);
         var1.getBlues(var5);
         defaultPalette = new byte[var2 * 3];

         for(int var6 = 0; var6 < var2; ++var6) {
            defaultPalette[3 * var6 + 0] = var3[var6];
            defaultPalette[3 * var6 + 1] = var4[var6];
            defaultPalette[3 * var6 + 2] = var5[var6];
         }
      }

      return defaultPalette;
   }
}
