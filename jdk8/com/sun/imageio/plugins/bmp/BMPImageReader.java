package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReader extends ImageReader implements BMPConstants {
   private static final int VERSION_2_1_BIT = 0;
   private static final int VERSION_2_4_BIT = 1;
   private static final int VERSION_2_8_BIT = 2;
   private static final int VERSION_2_24_BIT = 3;
   private static final int VERSION_3_1_BIT = 4;
   private static final int VERSION_3_4_BIT = 5;
   private static final int VERSION_3_8_BIT = 6;
   private static final int VERSION_3_24_BIT = 7;
   private static final int VERSION_3_NT_16_BIT = 8;
   private static final int VERSION_3_NT_32_BIT = 9;
   private static final int VERSION_4_1_BIT = 10;
   private static final int VERSION_4_4_BIT = 11;
   private static final int VERSION_4_8_BIT = 12;
   private static final int VERSION_4_16_BIT = 13;
   private static final int VERSION_4_24_BIT = 14;
   private static final int VERSION_4_32_BIT = 15;
   private static final int VERSION_3_XP_EMBEDDED = 16;
   private static final int VERSION_4_XP_EMBEDDED = 17;
   private static final int VERSION_5_XP_EMBEDDED = 18;
   private long bitmapFileSize;
   private long bitmapOffset;
   private long compression;
   private long imageSize;
   private byte[] palette;
   private int imageType;
   private int numBands;
   private boolean isBottomUp;
   private int bitsPerPixel;
   private int redMask;
   private int greenMask;
   private int blueMask;
   private int alphaMask;
   private SampleModel sampleModel;
   private SampleModel originalSampleModel;
   private ColorModel colorModel;
   private ColorModel originalColorModel;
   private ImageInputStream iis = null;
   private boolean gotHeader = false;
   private int width;
   private int height;
   private Rectangle destinationRegion;
   private Rectangle sourceRegion;
   private BMPMetadata metadata;
   private BufferedImage bi;
   private boolean noTransform = true;
   private boolean seleBand = false;
   private int scaleX;
   private int scaleY;
   private int[] sourceBands;
   private int[] destBands;
   private static Boolean isLinkedProfileDisabled = null;
   private static Boolean isWindowsPlatform = null;

   public BMPImageReader(ImageReaderSpi var1) {
      super(var1);
   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      super.setInput(var1, var2, var3);
      this.iis = (ImageInputStream)var1;
      if (this.iis != null) {
         this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      }

      this.resetHeaderInfo();
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

      try {
         this.readHeader();
      } catch (IllegalArgumentException var3) {
         throw new IIOException(I18N.getString("BMPImageReader6"), var3);
      }

      return this.width;
   }

   public int getHeight(int var1) throws IOException {
      this.checkIndex(var1);

      try {
         this.readHeader();
      } catch (IllegalArgumentException var3) {
         throw new IIOException(I18N.getString("BMPImageReader6"), var3);
      }

      return this.height;
   }

   private void checkIndex(int var1) {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException(I18N.getString("BMPImageReader0"));
      }
   }

   protected void readHeader() throws IOException, IllegalArgumentException {
      if (!this.gotHeader) {
         if (this.iis == null) {
            throw new IllegalStateException("Input source not set!");
         } else {
            int var1 = 0;
            int var2 = 0;
            this.metadata = new BMPMetadata();
            this.iis.mark();
            byte[] var3 = new byte[2];
            this.iis.read(var3);
            if (var3[0] == 66 && var3[1] == 77) {
               this.bitmapFileSize = this.iis.readUnsignedInt();
               this.iis.skipBytes(4);
               this.bitmapOffset = this.iis.readUnsignedInt();
               long var4 = this.iis.readUnsignedInt();
               if (var4 == 12L) {
                  this.width = this.iis.readShort();
                  this.height = this.iis.readShort();
               } else {
                  this.width = this.iis.readInt();
                  this.height = this.iis.readInt();
               }

               this.metadata.width = this.width;
               this.metadata.height = this.height;
               int var6 = this.iis.readUnsignedShort();
               this.bitsPerPixel = this.iis.readUnsignedShort();
               this.metadata.bitsPerPixel = (short)this.bitsPerPixel;
               this.numBands = 3;
               if (var4 == 12L) {
                  this.metadata.bmpVersion = "BMP v. 2.x";
                  if (this.bitsPerPixel == 1) {
                     this.imageType = 0;
                  } else if (this.bitsPerPixel == 4) {
                     this.imageType = 1;
                  } else if (this.bitsPerPixel == 8) {
                     this.imageType = 2;
                  } else if (this.bitsPerPixel == 24) {
                     this.imageType = 3;
                  }

                  int var7 = (int)((this.bitmapOffset - 14L - var4) / 3L);
                  int var8 = var7 * 3;
                  this.palette = new byte[var8];
                  this.iis.readFully((byte[])this.palette, 0, var8);
                  this.metadata.palette = this.palette;
                  this.metadata.paletteSize = var7;
               } else {
                  this.compression = this.iis.readUnsignedInt();
                  this.imageSize = this.iis.readUnsignedInt();
                  long var35 = (long)this.iis.readInt();
                  long var9 = (long)this.iis.readInt();
                  long var11 = this.iis.readUnsignedInt();
                  long var13 = this.iis.readUnsignedInt();
                  this.metadata.compression = (int)this.compression;
                  this.metadata.xPixelsPerMeter = (int)var35;
                  this.metadata.yPixelsPerMeter = (int)var9;
                  this.metadata.colorsUsed = (int)var11;
                  this.metadata.colorsImportant = (int)var13;
                  if (var4 == 40L) {
                     int var16;
                     switch((int)this.compression) {
                     case 0:
                     case 1:
                     case 2:
                        if (this.bitmapOffset < var4 + 14L) {
                           throw new IIOException(I18N.getString("BMPImageReader7"));
                        }

                        int var15 = (int)((this.bitmapOffset - 14L - var4) / 4L);
                        var16 = var15 * 4;
                        this.palette = new byte[var16];
                        this.iis.readFully((byte[])this.palette, 0, var16);
                        this.metadata.palette = this.palette;
                        this.metadata.paletteSize = var15;
                        if (this.bitsPerPixel == 1) {
                           this.imageType = 4;
                        } else if (this.bitsPerPixel == 4) {
                           this.imageType = 5;
                        } else if (this.bitsPerPixel == 8) {
                           this.imageType = 6;
                        } else if (this.bitsPerPixel == 24) {
                           this.imageType = 7;
                        } else if (this.bitsPerPixel == 16) {
                           this.imageType = 8;
                           this.redMask = 31744;
                           this.greenMask = 992;
                           this.blueMask = 31;
                           this.metadata.redMask = this.redMask;
                           this.metadata.greenMask = this.greenMask;
                           this.metadata.blueMask = this.blueMask;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 9;
                           this.redMask = 16711680;
                           this.greenMask = 65280;
                           this.blueMask = 255;
                           this.metadata.redMask = this.redMask;
                           this.metadata.greenMask = this.greenMask;
                           this.metadata.blueMask = this.blueMask;
                        }

                        this.metadata.bmpVersion = "BMP v. 3.x";
                        break;
                     case 3:
                        if (this.bitsPerPixel == 16) {
                           this.imageType = 8;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 9;
                        }

                        this.redMask = (int)this.iis.readUnsignedInt();
                        this.greenMask = (int)this.iis.readUnsignedInt();
                        this.blueMask = (int)this.iis.readUnsignedInt();
                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        if (var11 != 0L) {
                           var16 = (int)var11 * 4;
                           this.palette = new byte[var16];
                           this.iis.readFully((byte[])this.palette, 0, var16);
                           this.metadata.palette = this.palette;
                           this.metadata.paletteSize = (int)var11;
                        }

                        this.metadata.bmpVersion = "BMP v. 3.x NT";
                        break;
                     case 4:
                     case 5:
                        this.metadata.bmpVersion = "BMP v. 3.x";
                        this.imageType = 16;
                        break;
                     default:
                        throw new IIOException(I18N.getString("BMPImageReader2"));
                     }
                  } else {
                     if (var4 != 108L && var4 != 124L) {
                        throw new IIOException(I18N.getString("BMPImageReader3"));
                     }

                     if (var4 == 108L) {
                        this.metadata.bmpVersion = "BMP v. 4.x";
                     } else if (var4 == 124L) {
                        this.metadata.bmpVersion = "BMP v. 5.x";
                     }

                     this.redMask = (int)this.iis.readUnsignedInt();
                     this.greenMask = (int)this.iis.readUnsignedInt();
                     this.blueMask = (int)this.iis.readUnsignedInt();
                     this.alphaMask = (int)this.iis.readUnsignedInt();
                     long var43 = this.iis.readUnsignedInt();
                     int var17 = this.iis.readInt();
                     int var18 = this.iis.readInt();
                     int var19 = this.iis.readInt();
                     int var20 = this.iis.readInt();
                     int var21 = this.iis.readInt();
                     int var22 = this.iis.readInt();
                     int var23 = this.iis.readInt();
                     int var24 = this.iis.readInt();
                     int var25 = this.iis.readInt();
                     long var26 = this.iis.readUnsignedInt();
                     long var28 = this.iis.readUnsignedInt();
                     long var30 = this.iis.readUnsignedInt();
                     if (var4 == 124L) {
                        this.metadata.intent = this.iis.readInt();
                        var1 = this.iis.readInt();
                        var2 = this.iis.readInt();
                        this.iis.skipBytes(4);
                     }

                     this.metadata.colorSpace = (int)var43;
                     if (var43 == 0L) {
                        this.metadata.redX = (double)var17;
                        this.metadata.redY = (double)var18;
                        this.metadata.redZ = (double)var19;
                        this.metadata.greenX = (double)var20;
                        this.metadata.greenY = (double)var21;
                        this.metadata.greenZ = (double)var22;
                        this.metadata.blueX = (double)var23;
                        this.metadata.blueY = (double)var24;
                        this.metadata.blueZ = (double)var25;
                        this.metadata.gammaRed = (int)var26;
                        this.metadata.gammaGreen = (int)var28;
                        this.metadata.gammaBlue = (int)var30;
                     }

                     int var32 = (int)((this.bitmapOffset - 14L - var4) / 4L);
                     int var33 = var32 * 4;
                     this.palette = new byte[var33];
                     this.iis.readFully((byte[])this.palette, 0, var33);
                     this.metadata.palette = this.palette;
                     this.metadata.paletteSize = var32;
                     switch((int)this.compression) {
                     case 4:
                     case 5:
                        if (var4 == 108L) {
                           this.imageType = 17;
                        } else if (var4 == 124L) {
                           this.imageType = 18;
                        }
                        break;
                     default:
                        if (this.bitsPerPixel == 1) {
                           this.imageType = 10;
                        } else if (this.bitsPerPixel == 4) {
                           this.imageType = 11;
                        } else if (this.bitsPerPixel == 8) {
                           this.imageType = 12;
                        } else if (this.bitsPerPixel == 16) {
                           this.imageType = 13;
                           if ((int)this.compression == 0) {
                              this.redMask = 31744;
                              this.greenMask = 992;
                              this.blueMask = 31;
                           }
                        } else if (this.bitsPerPixel == 24) {
                           this.imageType = 14;
                        } else if (this.bitsPerPixel == 32) {
                           this.imageType = 15;
                           if ((int)this.compression == 0) {
                              this.redMask = 16711680;
                              this.greenMask = 65280;
                              this.blueMask = 255;
                           }
                        }

                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        this.metadata.alphaMask = this.alphaMask;
                     }
                  }
               }

               if (this.height > 0) {
                  this.isBottomUp = true;
               } else {
                  this.isBottomUp = false;
                  this.height = Math.abs(this.height);
               }

               Object var36 = ColorSpace.getInstance(1000);
               byte[] var37;
               if (this.metadata.colorSpace == 3 || this.metadata.colorSpace == 4) {
                  this.iis.mark();
                  this.iis.skipBytes((long)var1 - var4);
                  var37 = new byte[var2];
                  this.iis.readFully((byte[])var37, 0, var2);
                  this.iis.reset();

                  try {
                     if (this.metadata.colorSpace == 3 && isLinkedProfileAllowed() && !isUncOrDevicePath(var37)) {
                        String var39 = new String(var37, "windows-1252");
                        var36 = new ICC_ColorSpace(ICC_Profile.getInstance(var39));
                     } else {
                        var36 = new ICC_ColorSpace(ICC_Profile.getInstance(var37));
                     }
                  } catch (Exception var34) {
                     var36 = ColorSpace.getInstance(1000);
                  }
               }

               if (this.bitsPerPixel != 0 && this.compression != 4L && this.compression != 5L) {
                  int[] var38;
                  int var40;
                  if (this.bitsPerPixel != 1 && this.bitsPerPixel != 4 && this.bitsPerPixel != 8) {
                     if (this.bitsPerPixel == 16) {
                        this.numBands = 3;
                        this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[]{this.redMask, this.greenMask, this.blueMask});
                        this.colorModel = new DirectColorModel((ColorSpace)var36, 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
                     } else if (this.bitsPerPixel == 32) {
                        this.numBands = this.alphaMask == 0 ? 3 : 4;
                        var38 = this.numBands == 3 ? new int[]{this.redMask, this.greenMask, this.blueMask} : new int[]{this.redMask, this.greenMask, this.blueMask, this.alphaMask};
                        this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, var38);
                        this.colorModel = new DirectColorModel((ColorSpace)var36, 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
                     } else {
                        this.numBands = 3;
                        var38 = new int[this.numBands];

                        for(var40 = 0; var40 < this.numBands; ++var40) {
                           var38[var40] = this.numBands - 1 - var40;
                        }

                        this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, var38);
                        this.colorModel = ImageUtil.createColorModel((ColorSpace)var36, this.sampleModel);
                     }
                  } else {
                     this.numBands = 1;
                     if (this.bitsPerPixel != 8) {
                        this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
                     } else {
                        var38 = new int[this.numBands];

                        for(var40 = 0; var40 < this.numBands; ++var40) {
                           var38[var40] = this.numBands - 1 - var40;
                        }

                        this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, var38);
                     }

                     byte[] var10;
                     int var12;
                     byte[] var41;
                     int var42;
                     if (this.imageType != 0 && this.imageType != 1 && this.imageType != 2) {
                        var4 = (long)(this.palette.length / 4);
                        if (var4 > 256L) {
                           var4 = 256L;
                        }

                        var37 = new byte[(int)var4];
                        var41 = new byte[(int)var4];
                        var10 = new byte[(int)var4];

                        for(var12 = 0; (long)var12 < var4; ++var12) {
                           var42 = 4 * var12;
                           var10[var12] = this.palette[var42];
                           var41[var12] = this.palette[var42 + 1];
                           var37[var12] = this.palette[var42 + 2];
                        }
                     } else {
                        var4 = (long)(this.palette.length / 3);
                        if (var4 > 256L) {
                           var4 = 256L;
                        }

                        var37 = new byte[(int)var4];
                        var41 = new byte[(int)var4];
                        var10 = new byte[(int)var4];

                        for(var12 = 0; var12 < (int)var4; ++var12) {
                           var42 = 3 * var12;
                           var10[var12] = this.palette[var42];
                           var41[var12] = this.palette[var42 + 1];
                           var37[var12] = this.palette[var42 + 2];
                        }
                     }

                     if (ImageUtil.isIndicesForGrayscale(var37, var41, var10)) {
                        this.colorModel = ImageUtil.createColorModel((ColorSpace)null, this.sampleModel);
                     } else {
                        this.colorModel = new IndexColorModel(this.bitsPerPixel, (int)var4, var37, var41, var10);
                     }
                  }
               } else {
                  this.colorModel = null;
                  this.sampleModel = null;
               }

               this.originalSampleModel = this.sampleModel;
               this.originalColorModel = this.colorModel;
               this.iis.reset();
               this.iis.skipBytes(this.bitmapOffset);
               this.gotHeader = true;
            } else {
               throw new IllegalArgumentException(I18N.getString("BMPImageReader1"));
            }
         }
      }
   }

   public Iterator getImageTypes(int var1) throws IOException {
      this.checkIndex(var1);

      try {
         this.readHeader();
      } catch (IllegalArgumentException var3) {
         throw new IIOException(I18N.getString("BMPImageReader6"), var3);
      }

      ArrayList var2 = new ArrayList(1);
      var2.add(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel));
      return var2.iterator();
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getImageMetadata(int var1) throws IOException {
      this.checkIndex(var1);
      if (this.metadata == null) {
         try {
            this.readHeader();
         } catch (IllegalArgumentException var3) {
            throw new IIOException(I18N.getString("BMPImageReader6"), var3);
         }
      }

      return this.metadata;
   }

   public IIOMetadata getStreamMetadata() throws IOException {
      return null;
   }

   public boolean isRandomAccessEasy(int var1) throws IOException {
      this.checkIndex(var1);

      try {
         this.readHeader();
      } catch (IllegalArgumentException var3) {
         throw new IIOException(I18N.getString("BMPImageReader6"), var3);
      }

      return this.metadata.compression == 0;
   }

   public BufferedImage read(int var1, ImageReadParam var2) throws IOException {
      if (this.iis == null) {
         throw new IllegalStateException(I18N.getString("BMPImageReader5"));
      } else {
         this.checkIndex(var1);
         this.clearAbortRequest();
         this.processImageStarted(var1);
         if (var2 == null) {
            var2 = this.getDefaultReadParam();
         }

         try {
            this.readHeader();
         } catch (IllegalArgumentException var7) {
            throw new IIOException(I18N.getString("BMPImageReader6"), var7);
         }

         this.sourceRegion = new Rectangle(0, 0, 0, 0);
         this.destinationRegion = new Rectangle(0, 0, 0, 0);
         computeRegions(var2, this.width, this.height, var2.getDestination(), this.sourceRegion, this.destinationRegion);
         this.scaleX = var2.getSourceXSubsampling();
         this.scaleY = var2.getSourceYSubsampling();
         this.sourceBands = var2.getSourceBands();
         this.destBands = var2.getDestinationBands();
         this.seleBand = this.sourceBands != null && this.destBands != null;
         this.noTransform = this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand;
         if (!this.seleBand) {
            this.sourceBands = new int[this.numBands];
            this.destBands = new int[this.numBands];

            for(int var3 = 0; var3 < this.numBands; ++var3) {
               this.destBands[var3] = this.sourceBands[var3] = var3;
            }
         }

         this.bi = var2.getDestination();
         WritableRaster var8 = null;
         if (this.bi == null) {
            if (this.sampleModel != null && this.colorModel != null) {
               this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
               if (this.seleBand) {
                  this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands);
               }

               var8 = Raster.createWritableRaster(this.sampleModel, new Point());
               this.bi = new BufferedImage(this.colorModel, var8, false, (Hashtable)null);
            }
         } else {
            var8 = this.bi.getWritableTile(0, 0);
            this.sampleModel = this.bi.getSampleModel();
            this.colorModel = this.bi.getColorModel();
            this.noTransform &= this.destinationRegion.equals(var8.getBounds());
         }

         byte[] var4 = null;
         short[] var5 = null;
         int[] var6 = null;
         if (this.sampleModel != null) {
            if (this.sampleModel.getDataType() == 0) {
               var4 = (byte[])((DataBufferByte)var8.getDataBuffer()).getData();
            } else if (this.sampleModel.getDataType() == 1) {
               var5 = (short[])((DataBufferUShort)var8.getDataBuffer()).getData();
            } else if (this.sampleModel.getDataType() == 3) {
               var6 = (int[])((DataBufferInt)var8.getDataBuffer()).getData();
            }
         }

         label79:
         switch(this.imageType) {
         case 0:
            this.read1Bit(var4);
            break;
         case 1:
            this.read4Bit(var4);
            break;
         case 2:
            this.read8Bit(var4);
            break;
         case 3:
            this.read24Bit(var4);
            break;
         case 4:
            this.read1Bit(var4);
            break;
         case 5:
            switch((int)this.compression) {
            case 0:
               this.read4Bit(var4);
               break label79;
            case 2:
               this.readRLE4(var4);
               break label79;
            default:
               throw new IIOException(I18N.getString("BMPImageReader1"));
            }
         case 6:
            switch((int)this.compression) {
            case 0:
               this.read8Bit(var4);
               break label79;
            case 1:
               this.readRLE8(var4);
               break label79;
            default:
               throw new IIOException(I18N.getString("BMPImageReader1"));
            }
         case 7:
            this.read24Bit(var4);
            break;
         case 8:
            this.read16Bit(var5);
            break;
         case 9:
            this.read32Bit(var6);
            break;
         case 10:
            this.read1Bit(var4);
            break;
         case 11:
            switch((int)this.compression) {
            case 0:
               this.read4Bit(var4);
               break;
            case 2:
               this.readRLE4(var4);
               break;
            default:
               throw new IIOException(I18N.getString("BMPImageReader1"));
            }
         case 12:
            switch((int)this.compression) {
            case 0:
               this.read8Bit(var4);
               break label79;
            case 1:
               this.readRLE8(var4);
               break label79;
            default:
               throw new IIOException(I18N.getString("BMPImageReader1"));
            }
         case 13:
            this.read16Bit(var5);
            break;
         case 14:
            this.read24Bit(var4);
            break;
         case 15:
            this.read32Bit(var6);
            break;
         case 16:
         case 17:
         case 18:
            this.bi = this.readEmbedded((int)this.compression, this.bi, var2);
         }

         if (this.abortRequested()) {
            this.processReadAborted();
         } else {
            this.processImageComplete();
         }

         return this.bi;
      }
   }

   public boolean canReadRaster() {
      return true;
   }

   public Raster readRaster(int var1, ImageReadParam var2) throws IOException {
      BufferedImage var3 = this.read(var1, var2);
      return var3.getData();
   }

   private void resetHeaderInfo() {
      this.gotHeader = false;
      this.bi = null;
      this.sampleModel = this.originalSampleModel = null;
      this.colorModel = this.originalColorModel = null;
   }

   public void reset() {
      super.reset();
      this.iis = null;
      this.resetHeaderInfo();
   }

   private void read1Bit(byte[] var1) throws IOException {
      int var2 = (this.width + 7) / 8;
      int var3 = var2 % 4;
      if (var3 != 0) {
         var3 = 4 - var3;
      }

      int var4 = var2 + var3;
      int var6;
      if (this.noTransform) {
         int var5 = this.isBottomUp ? (this.height - 1) * var2 : 0;

         for(var6 = 0; var6 < this.height && !this.abortRequested(); ++var6) {
            this.iis.readFully(var1, var5, var2);
            this.iis.skipBytes(var3);
            var5 += this.isBottomUp ? -var2 : var2;
            this.processImageUpdate(this.bi, 0, var6, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var6 / (float)this.destinationRegion.height);
         }
      } else {
         byte[] var17 = new byte[var4];
         var6 = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int var7;
         if (this.isBottomUp) {
            var7 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(var4 * (this.height - 1 - var7));
         } else {
            this.iis.skipBytes(var4 * this.sourceRegion.y);
         }

         var7 = var4 * (this.scaleY - 1);
         int[] var8 = new int[this.destinationRegion.width];
         int[] var9 = new int[this.destinationRegion.width];
         int[] var10 = new int[this.destinationRegion.width];
         int[] var11 = new int[this.destinationRegion.width];
         int var12 = this.destinationRegion.x;
         int var13 = this.sourceRegion.x;

         int var14;
         for(var14 = 0; var12 < this.destinationRegion.x + this.destinationRegion.width; var13 += this.scaleX) {
            var10[var14] = var13 >> 3;
            var8[var14] = 7 - (var13 & 7);
            var11[var14] = var12 >> 3;
            var9[var14] = 7 - (var12 & 7);
            ++var12;
            ++var14;
         }

         var12 = this.destinationRegion.y * var6;
         if (this.isBottomUp) {
            var12 += (this.destinationRegion.height - 1) * var6;
         }

         var13 = 0;

         for(var14 = this.sourceRegion.y; var13 < this.destinationRegion.height && !this.abortRequested(); var14 += this.scaleY) {
            this.iis.read(var17, 0, var4);

            for(int var15 = 0; var15 < this.destinationRegion.width; ++var15) {
               int var16 = var17[var10[var15]] >> var8[var15] & 1;
               var1[var12 + var11[var15]] = (byte)(var1[var12 + var11[var15]] | var16 << var9[var15]);
            }

            var12 += this.isBottomUp ? -var6 : var6;
            this.iis.skipBytes(var7);
            this.processImageUpdate(this.bi, 0, var13, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var13 / (float)this.destinationRegion.height);
            ++var13;
         }
      }

   }

   private void read4Bit(byte[] var1) throws IOException {
      int var2 = (this.width + 1) / 2;
      int var3 = var2 % 4;
      if (var3 != 0) {
         var3 = 4 - var3;
      }

      int var4 = var2 + var3;
      int var6;
      if (this.noTransform) {
         int var5 = this.isBottomUp ? (this.height - 1) * var2 : 0;

         for(var6 = 0; var6 < this.height && !this.abortRequested(); ++var6) {
            this.iis.readFully(var1, var5, var2);
            this.iis.skipBytes(var3);
            var5 += this.isBottomUp ? -var2 : var2;
            this.processImageUpdate(this.bi, 0, var6, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var6 / (float)this.destinationRegion.height);
         }
      } else {
         byte[] var17 = new byte[var4];
         var6 = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int var7;
         if (this.isBottomUp) {
            var7 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(var4 * (this.height - 1 - var7));
         } else {
            this.iis.skipBytes(var4 * this.sourceRegion.y);
         }

         var7 = var4 * (this.scaleY - 1);
         int[] var8 = new int[this.destinationRegion.width];
         int[] var9 = new int[this.destinationRegion.width];
         int[] var10 = new int[this.destinationRegion.width];
         int[] var11 = new int[this.destinationRegion.width];
         int var12 = this.destinationRegion.x;
         int var13 = this.sourceRegion.x;

         int var14;
         for(var14 = 0; var12 < this.destinationRegion.x + this.destinationRegion.width; var13 += this.scaleX) {
            var10[var14] = var13 >> 1;
            var8[var14] = 1 - (var13 & 1) << 2;
            var11[var14] = var12 >> 1;
            var9[var14] = 1 - (var12 & 1) << 2;
            ++var12;
            ++var14;
         }

         var12 = this.destinationRegion.y * var6;
         if (this.isBottomUp) {
            var12 += (this.destinationRegion.height - 1) * var6;
         }

         var13 = 0;

         for(var14 = this.sourceRegion.y; var13 < this.destinationRegion.height && !this.abortRequested(); var14 += this.scaleY) {
            this.iis.read(var17, 0, var4);

            for(int var15 = 0; var15 < this.destinationRegion.width; ++var15) {
               int var16 = var17[var10[var15]] >> var8[var15] & 15;
               var1[var12 + var11[var15]] = (byte)(var1[var12 + var11[var15]] | var16 << var9[var15]);
            }

            var12 += this.isBottomUp ? -var6 : var6;
            this.iis.skipBytes(var7);
            this.processImageUpdate(this.bi, 0, var13, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var13 / (float)this.destinationRegion.height);
            ++var13;
         }
      }

   }

   private void read8Bit(byte[] var1) throws IOException {
      int var2 = this.width % 4;
      if (var2 != 0) {
         var2 = 4 - var2;
      }

      int var3 = this.width + var2;
      int var5;
      if (this.noTransform) {
         int var4 = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(var5 = 0; var5 < this.height && !this.abortRequested(); ++var5) {
            this.iis.readFully(var1, var4, this.width);
            this.iis.skipBytes(var2);
            var4 += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, var5, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var5 / (float)this.destinationRegion.height);
         }
      } else {
         byte[] var12 = new byte[var3];
         var5 = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
         int var6;
         if (this.isBottomUp) {
            var6 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(var3 * (this.height - 1 - var6));
         } else {
            this.iis.skipBytes(var3 * this.sourceRegion.y);
         }

         var6 = var3 * (this.scaleY - 1);
         int var7 = this.destinationRegion.y * var5;
         if (this.isBottomUp) {
            var7 += (this.destinationRegion.height - 1) * var5;
         }

         var7 += this.destinationRegion.x;
         int var8 = 0;

         for(int var9 = this.sourceRegion.y; var8 < this.destinationRegion.height && !this.abortRequested(); var9 += this.scaleY) {
            this.iis.read(var12, 0, var3);
            int var10 = 0;

            for(int var11 = this.sourceRegion.x; var10 < this.destinationRegion.width; var11 += this.scaleX) {
               var1[var7 + var10] = var12[var11];
               ++var10;
            }

            var7 += this.isBottomUp ? -var5 : var5;
            this.iis.skipBytes(var6);
            this.processImageUpdate(this.bi, 0, var8, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var8 / (float)this.destinationRegion.height);
            ++var8;
         }
      }

   }

   private void read24Bit(byte[] var1) throws IOException {
      int var2 = this.width * 3 % 4;
      if (var2 != 0) {
         var2 = 4 - var2;
      }

      int var3 = this.width * 3;
      int var4 = var3 + var2;
      int var6;
      if (this.noTransform) {
         int var5 = this.isBottomUp ? (this.height - 1) * this.width * 3 : 0;

         for(var6 = 0; var6 < this.height && !this.abortRequested(); ++var6) {
            this.iis.readFully(var1, var5, var3);
            this.iis.skipBytes(var2);
            var5 += this.isBottomUp ? -var3 : var3;
            this.processImageUpdate(this.bi, 0, var6, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var6 / (float)this.destinationRegion.height);
         }
      } else {
         byte[] var14 = new byte[var4];
         var3 = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
         if (this.isBottomUp) {
            var6 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(var4 * (this.height - 1 - var6));
         } else {
            this.iis.skipBytes(var4 * this.sourceRegion.y);
         }

         var6 = var4 * (this.scaleY - 1);
         int var7 = this.destinationRegion.y * var3;
         if (this.isBottomUp) {
            var7 += (this.destinationRegion.height - 1) * var3;
         }

         var7 += this.destinationRegion.x * 3;
         int var8 = 0;

         for(int var9 = this.sourceRegion.y; var8 < this.destinationRegion.height && !this.abortRequested(); var9 += this.scaleY) {
            this.iis.read(var14, 0, var4);
            int var10 = 0;

            for(int var11 = 3 * this.sourceRegion.x; var10 < this.destinationRegion.width; var11 += 3 * this.scaleX) {
               int var12 = 3 * var10 + var7;

               for(int var13 = 0; var13 < this.destBands.length; ++var13) {
                  var1[var12 + this.destBands[var13]] = var14[var11 + this.sourceBands[var13]];
               }

               ++var10;
            }

            var7 += this.isBottomUp ? -var3 : var3;
            this.iis.skipBytes(var6);
            this.processImageUpdate(this.bi, 0, var8, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var8 / (float)this.destinationRegion.height);
            ++var8;
         }
      }

   }

   private void read16Bit(short[] var1) throws IOException {
      int var2 = this.width * 2 % 4;
      if (var2 != 0) {
         var2 = 4 - var2;
      }

      int var3 = this.width + var2 / 2;
      int var5;
      if (this.noTransform) {
         int var4 = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(var5 = 0; var5 < this.height && !this.abortRequested(); ++var5) {
            this.iis.readFully(var1, var4, this.width);
            this.iis.skipBytes(var2);
            var4 += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, var5, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var5 / (float)this.destinationRegion.height);
         }
      } else {
         short[] var12 = new short[var3];
         var5 = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int var6;
         if (this.isBottomUp) {
            var6 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(var3 * (this.height - 1 - var6) << 1);
         } else {
            this.iis.skipBytes(var3 * this.sourceRegion.y << 1);
         }

         var6 = var3 * (this.scaleY - 1) << 1;
         int var7 = this.destinationRegion.y * var5;
         if (this.isBottomUp) {
            var7 += (this.destinationRegion.height - 1) * var5;
         }

         var7 += this.destinationRegion.x;
         int var8 = 0;

         for(int var9 = this.sourceRegion.y; var8 < this.destinationRegion.height && !this.abortRequested(); var9 += this.scaleY) {
            this.iis.readFully((short[])var12, 0, var3);
            int var10 = 0;

            for(int var11 = this.sourceRegion.x; var10 < this.destinationRegion.width; var11 += this.scaleX) {
               var1[var7 + var10] = var12[var11];
               ++var10;
            }

            var7 += this.isBottomUp ? -var5 : var5;
            this.iis.skipBytes(var6);
            this.processImageUpdate(this.bi, 0, var8, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var8 / (float)this.destinationRegion.height);
            ++var8;
         }
      }

   }

   private void read32Bit(int[] var1) throws IOException {
      int var3;
      if (this.noTransform) {
         int var2 = this.isBottomUp ? (this.height - 1) * this.width : 0;

         for(var3 = 0; var3 < this.height && !this.abortRequested(); ++var3) {
            this.iis.readFully(var1, var2, this.width);
            var2 += this.isBottomUp ? -this.width : this.width;
            this.processImageUpdate(this.bi, 0, var3, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var3 / (float)this.destinationRegion.height);
         }
      } else {
         int[] var10 = new int[this.width];
         var3 = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
         int var4;
         if (this.isBottomUp) {
            var4 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
            this.iis.skipBytes(this.width * (this.height - 1 - var4) << 2);
         } else {
            this.iis.skipBytes(this.width * this.sourceRegion.y << 2);
         }

         var4 = this.width * (this.scaleY - 1) << 2;
         int var5 = this.destinationRegion.y * var3;
         if (this.isBottomUp) {
            var5 += (this.destinationRegion.height - 1) * var3;
         }

         var5 += this.destinationRegion.x;
         int var6 = 0;

         for(int var7 = this.sourceRegion.y; var6 < this.destinationRegion.height && !this.abortRequested(); var7 += this.scaleY) {
            this.iis.readFully((int[])var10, 0, this.width);
            int var8 = 0;

            for(int var9 = this.sourceRegion.x; var8 < this.destinationRegion.width; var9 += this.scaleX) {
               var1[var5 + var8] = var10[var9];
               ++var8;
            }

            var5 += this.isBottomUp ? -var3 : var3;
            this.iis.skipBytes(var4);
            this.processImageUpdate(this.bi, 0, var6, this.destinationRegion.width, 1, 1, 1, new int[]{0});
            this.processImageProgress(100.0F * (float)var6 / (float)this.destinationRegion.height);
            ++var6;
         }
      }

   }

   private void readRLE8(byte[] var1) throws IOException {
      int var2 = (int)this.imageSize;
      if (var2 == 0) {
         var2 = (int)(this.bitmapFileSize - this.bitmapOffset);
      }

      int var3 = 0;
      int var4 = this.width % 4;
      if (var4 != 0) {
         var3 = 4 - var4;
      }

      byte[] var5 = new byte[var2];
      boolean var6 = false;
      this.iis.readFully((byte[])var5, 0, var2);
      this.decodeRLE8(var2, var3, var5, var1);
   }

   private void decodeRLE8(int var1, int var2, byte[] var3, byte[] var4) throws IOException {
      byte[] var5 = new byte[this.width * this.height];
      int var6 = 0;
      int var7 = 0;
      boolean var9 = false;
      int var10 = this.isBottomUp ? this.height - 1 : 0;
      int var11 = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
      int var12 = 0;

      while(var6 != var1) {
         int var8 = var3[var6++] & 255;
         int var13;
         if (var8 == 0) {
            int var14;
            int var15;
            switch(var3[var6++] & 255) {
            case 0:
               if (var10 >= this.sourceRegion.y && var10 < this.sourceRegion.y + this.sourceRegion.height) {
                  if (this.noTransform) {
                     var13 = var10 * this.width;

                     for(var14 = 0; var14 < this.width; ++var14) {
                        var4[var13++] = var5[var14];
                     }

                     this.processImageUpdate(this.bi, 0, var10, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                     ++var12;
                  } else if ((var10 - this.sourceRegion.y) % this.scaleY == 0) {
                     var13 = (var10 - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                     var14 = var13 * var11;
                     var14 += this.destinationRegion.x;

                     for(var15 = this.sourceRegion.x; var15 < this.sourceRegion.x + this.sourceRegion.width; var15 += this.scaleX) {
                        var4[var14++] = var5[var15];
                     }

                     this.processImageUpdate(this.bi, 0, var13, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                     ++var12;
                  }
               }

               this.processImageProgress(100.0F * (float)var12 / (float)this.destinationRegion.height);
               var10 += this.isBottomUp ? -1 : 1;
               var7 = 0;
               if (this.abortRequested()) {
                  var9 = true;
               }
               break;
            case 1:
               var9 = true;
               break;
            case 2:
               var13 = var3[var6++] & 255;
               var14 = var3[var6] & 255;
               var7 += var13 + var14 * this.width;
               break;
            default:
               var15 = var3[var6 - 1] & 255;

               for(int var16 = 0; var16 < var15; ++var16) {
                  var5[var7++] = (byte)(var3[var6++] & 255);
               }

               if ((var15 & 1) == 1) {
                  ++var6;
               }
            }
         } else {
            for(var13 = 0; var13 < var8; ++var13) {
               var5[var7++] = (byte)(var3[var6] & 255);
            }

            ++var6;
         }

         if (var9) {
            break;
         }
      }

   }

   private void readRLE4(byte[] var1) throws IOException {
      int var2 = (int)this.imageSize;
      if (var2 == 0) {
         var2 = (int)(this.bitmapFileSize - this.bitmapOffset);
      }

      int var3 = 0;
      int var4 = this.width % 4;
      if (var4 != 0) {
         var3 = 4 - var4;
      }

      byte[] var5 = new byte[var2];
      this.iis.readFully((byte[])var5, 0, var2);
      this.decodeRLE4(var2, var3, var5, var1);
   }

   private void decodeRLE4(int var1, int var2, byte[] var3, byte[] var4) throws IOException {
      byte[] var5 = new byte[this.width];
      int var6 = 0;
      int var7 = 0;
      boolean var9 = false;
      int var10 = this.isBottomUp ? this.height - 1 : 0;
      int var11 = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
      int var12 = 0;

      while(var6 != var1) {
         int var8 = var3[var6++] & 255;
         int var14;
         if (var8 == 0) {
            int var13;
            int var15;
            int var16;
            switch(var3[var6++] & 255) {
            case 0:
               if (var10 >= this.sourceRegion.y && var10 < this.sourceRegion.y + this.sourceRegion.height) {
                  if (this.noTransform) {
                     var13 = var10 * (this.width + 1 >> 1);
                     var14 = 0;

                     for(var15 = 0; var14 < this.width >> 1; ++var14) {
                        var4[var13++] = (byte)(var5[var15++] << 4 | var5[var15++]);
                     }

                     if ((this.width & 1) == 1) {
                        var4[var13] = (byte)(var4[var13] | var5[this.width - 1] << 4);
                     }

                     this.processImageUpdate(this.bi, 0, var10, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                     ++var12;
                  } else if ((var10 - this.sourceRegion.y) % this.scaleY == 0) {
                     var13 = (var10 - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                     var14 = var13 * var11;
                     var14 += this.destinationRegion.x >> 1;
                     var15 = 1 - (this.destinationRegion.x & 1) << 2;

                     for(var16 = this.sourceRegion.x; var16 < this.sourceRegion.x + this.sourceRegion.width; var16 += this.scaleX) {
                        var4[var14] = (byte)(var4[var14] | var5[var16] << var15);
                        var15 += 4;
                        if (var15 == 4) {
                           ++var14;
                        }

                        var15 &= 7;
                     }

                     this.processImageUpdate(this.bi, 0, var13, this.destinationRegion.width, 1, 1, 1, new int[]{0});
                     ++var12;
                  }
               }

               this.processImageProgress(100.0F * (float)var12 / (float)this.destinationRegion.height);
               var10 += this.isBottomUp ? -1 : 1;
               var7 = 0;
               if (this.abortRequested()) {
                  var9 = true;
               }
               break;
            case 1:
               var9 = true;
               break;
            case 2:
               var13 = var3[var6++] & 255;
               var14 = var3[var6] & 255;
               var7 += var13 + var14 * this.width;
               break;
            default:
               var15 = var3[var6 - 1] & 255;

               for(var16 = 0; var16 < var15; ++var16) {
                  var5[var7++] = (byte)((var16 & 1) == 0 ? (var3[var6] & 240) >> 4 : var3[var6++] & 15);
               }

               if ((var15 & 1) == 1) {
                  ++var6;
               }

               if (((int)Math.ceil((double)(var15 / 2)) & 1) == 1) {
                  ++var6;
               }
            }
         } else {
            int[] var17 = new int[]{(var3[var6] & 240) >> 4, var3[var6] & 15};

            for(var14 = 0; var14 < var8 && var7 < this.width; ++var14) {
               var5[var7++] = (byte)var17[var14 & 1];
            }

            ++var6;
         }

         if (var9) {
            break;
         }
      }

   }

   private BufferedImage readEmbedded(int var1, BufferedImage var2, ImageReadParam var3) throws IOException {
      String var4;
      switch(var1) {
      case 4:
         var4 = "JPEG";
         break;
      case 5:
         var4 = "PNG";
         break;
      default:
         throw new IOException("Unexpected compression type: " + var1);
      }

      ImageReader var5 = (ImageReader)ImageIO.getImageReadersByFormatName(var4).next();
      if (var5 == null) {
         throw new RuntimeException(I18N.getString("BMPImageReader4") + " " + var4);
      } else {
         byte[] var6 = new byte[(int)this.imageSize];
         this.iis.read(var6);
         var5.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(var6)));
         if (var2 == null) {
            ImageTypeSpecifier var7 = (ImageTypeSpecifier)var5.getImageTypes(0).next();
            var2 = var7.createBufferedImage(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
         }

         var5.addIIOReadProgressListener(new BMPImageReader.EmbeddedProgressAdapter() {
            public void imageProgress(ImageReader var1, float var2) {
               BMPImageReader.this.processImageProgress(var2);
            }
         });
         var5.addIIOReadUpdateListener(new IIOReadUpdateListener() {
            public void imageUpdate(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9) {
               BMPImageReader.this.processImageUpdate(var2, var3, var4, var5, var6, var7, var8, var9);
            }

            public void passComplete(ImageReader var1, BufferedImage var2) {
               BMPImageReader.this.processPassComplete(var2);
            }

            public void passStarted(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int[] var10) {
               BMPImageReader.this.processPassStarted(var2, var3, var4, var5, var6, var7, var8, var9, var10);
            }

            public void thumbnailPassComplete(ImageReader var1, BufferedImage var2) {
            }

            public void thumbnailPassStarted(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int[] var10) {
            }

            public void thumbnailUpdate(ImageReader var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9) {
            }
         });
         var5.addIIOReadWarningListener(new IIOReadWarningListener() {
            public void warningOccurred(ImageReader var1, String var2) {
               BMPImageReader.this.processWarningOccurred(var2);
            }
         });
         ImageReadParam var8 = var5.getDefaultReadParam();
         var8.setDestination(var2);
         var8.setDestinationBands(var3.getDestinationBands());
         var8.setDestinationOffset(var3.getDestinationOffset());
         var8.setSourceBands(var3.getSourceBands());
         var8.setSourceRegion(var3.getSourceRegion());
         var8.setSourceSubsampling(var3.getSourceXSubsampling(), var3.getSourceYSubsampling(), var3.getSubsamplingXOffset(), var3.getSubsamplingYOffset());
         var5.read(0, var8);
         return var2;
      }
   }

   private static boolean isLinkedProfileAllowed() {
      if (isLinkedProfileDisabled == null) {
         PrivilegedAction var0 = new PrivilegedAction<Boolean>() {
            public Boolean run() {
               return Boolean.getBoolean("sun.imageio.plugins.bmp.disableLinkedProfiles");
            }
         };
         isLinkedProfileDisabled = (Boolean)AccessController.doPrivileged(var0);
      }

      return !isLinkedProfileDisabled;
   }

   private static boolean isUncOrDevicePath(byte[] var0) {
      if (isWindowsPlatform == null) {
         PrivilegedAction var1 = new PrivilegedAction<Boolean>() {
            public Boolean run() {
               String var1 = System.getProperty("os.name");
               return var1 != null && var1.toLowerCase().startsWith("win");
            }
         };
         isWindowsPlatform = (Boolean)AccessController.doPrivileged(var1);
      }

      if (!isWindowsPlatform) {
         return false;
      } else {
         if (var0[0] == 47) {
            var0[0] = 92;
         }

         if (var0[1] == 47) {
            var0[1] = 92;
         }

         if (var0[3] == 47) {
            var0[3] = 92;
         }

         if (var0[0] == 92 && var0[1] == 92) {
            if (var0[2] == 63 && var0[3] == 92) {
               return (var0[4] == 85 || var0[4] == 117) && (var0[5] == 78 || var0[5] == 110) && (var0[6] == 67 || var0[6] == 99);
            } else {
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private class EmbeddedProgressAdapter implements IIOReadProgressListener {
      private EmbeddedProgressAdapter() {
      }

      public void imageComplete(ImageReader var1) {
      }

      public void imageProgress(ImageReader var1, float var2) {
      }

      public void imageStarted(ImageReader var1, int var2) {
      }

      public void thumbnailComplete(ImageReader var1) {
      }

      public void thumbnailProgress(ImageReader var1, float var2) {
      }

      public void thumbnailStarted(ImageReader var1, int var2, int var3) {
      }

      public void sequenceComplete(ImageReader var1) {
      }

      public void sequenceStarted(ImageReader var1, int var2) {
      }

      public void readAborted(ImageReader var1) {
      }

      // $FF: synthetic method
      EmbeddedProgressAdapter(Object var2) {
         this();
      }
   }
}
