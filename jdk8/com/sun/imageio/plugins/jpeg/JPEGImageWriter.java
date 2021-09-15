package com.sun.imageio.plugins.jpeg;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageWriter extends ImageWriter {
   private boolean debug = false;
   private long structPointer = 0L;
   private ImageOutputStream ios = null;
   private Raster srcRas = null;
   private WritableRaster raster = null;
   private boolean indexed = false;
   private IndexColorModel indexCM = null;
   private boolean convertTosRGB = false;
   private WritableRaster converted = null;
   private boolean isAlphaPremultiplied = false;
   private ColorModel srcCM = null;
   private List thumbnails = null;
   private ICC_Profile iccProfile = null;
   private int sourceXOffset = 0;
   private int sourceYOffset = 0;
   private int sourceWidth = 0;
   private int[] srcBands = null;
   private int sourceHeight = 0;
   private int currentImage = 0;
   private ColorConvertOp convertOp = null;
   private JPEGQTable[] streamQTables = null;
   private JPEGHuffmanTable[] streamDCHuffmanTables = null;
   private JPEGHuffmanTable[] streamACHuffmanTables = null;
   private boolean ignoreJFIF = false;
   private boolean forceJFIF = false;
   private boolean ignoreAdobe = false;
   private int newAdobeTransform = -1;
   private boolean writeDefaultJFIF = false;
   private boolean writeAdobe = false;
   private JPEGMetadata metadata = null;
   private boolean sequencePrepared = false;
   private int numScans = 0;
   private Object disposerReferent = new Object();
   private DisposerRecord disposerRecord;
   protected static final int WARNING_DEST_IGNORED = 0;
   protected static final int WARNING_STREAM_METADATA_IGNORED = 1;
   protected static final int WARNING_DEST_METADATA_COMP_MISMATCH = 2;
   protected static final int WARNING_DEST_METADATA_JFIF_MISMATCH = 3;
   protected static final int WARNING_DEST_METADATA_ADOBE_MISMATCH = 4;
   protected static final int WARNING_IMAGE_METADATA_JFIF_MISMATCH = 5;
   protected static final int WARNING_IMAGE_METADATA_ADOBE_MISMATCH = 6;
   protected static final int WARNING_METADATA_NOT_JPEG_FOR_RASTER = 7;
   protected static final int WARNING_NO_BANDS_ON_INDEXED = 8;
   protected static final int WARNING_ILLEGAL_THUMBNAIL = 9;
   protected static final int WARNING_IGNORING_THUMBS = 10;
   protected static final int WARNING_FORCING_JFIF = 11;
   protected static final int WARNING_THUMB_CLIPPED = 12;
   protected static final int WARNING_METADATA_ADJUSTED_FOR_THUMB = 13;
   protected static final int WARNING_NO_RGB_THUMB_AS_INDEXED = 14;
   protected static final int WARNING_NO_GRAY_THUMB_AS_INDEXED = 15;
   private static final int MAX_WARNING = 15;
   static final Dimension[] preferredThumbSizes;
   private Thread theThread = null;
   private int theLockCount = 0;
   private JPEGImageWriter.CallBackLock cbLock = new JPEGImageWriter.CallBackLock();

   public JPEGImageWriter(ImageWriterSpi var1) {
      super(var1);
      this.structPointer = this.initJPEGImageWriter();
      this.disposerRecord = new JPEGImageWriter.JPEGWriterDisposerRecord(this.structPointer);
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
   }

   public void setOutput(Object var1) {
      this.setThreadLock();

      try {
         this.cbLock.check();
         super.setOutput(var1);
         this.resetInternalState();
         this.ios = (ImageOutputStream)var1;
         this.setDest(this.structPointer);
      } finally {
         this.clearThreadLock();
      }

   }

   public ImageWriteParam getDefaultWriteParam() {
      return new JPEGImageWriteParam((Locale)null);
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1) {
      this.setThreadLock();

      JPEGMetadata var2;
      try {
         var2 = new JPEGMetadata(var1, this);
      } finally {
         this.clearThreadLock();
      }

      return var2;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2) {
      this.setThreadLock();

      JPEGMetadata var3;
      try {
         var3 = new JPEGMetadata(var1, var2, this);
      } finally {
         this.clearThreadLock();
      }

      return var3;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2) {
      if (var1 instanceof JPEGMetadata) {
         JPEGMetadata var3 = (JPEGMetadata)var1;
         if (var3.isStream) {
            return var1;
         }
      }

      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      this.setThreadLock();

      IIOMetadata var4;
      try {
         var4 = this.convertImageMetadataOnThread(var1, var2, var3);
      } finally {
         this.clearThreadLock();
      }

      return var4;
   }

   private IIOMetadata convertImageMetadataOnThread(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      if (var1 instanceof JPEGMetadata) {
         JPEGMetadata var9 = (JPEGMetadata)var1;
         return !var9.isStream ? var1 : null;
      } else {
         if (var1.isStandardMetadataFormatSupported()) {
            String var4 = "javax_imageio_1.0";
            Node var5 = var1.getAsTree(var4);
            if (var5 != null) {
               JPEGMetadata var6 = new JPEGMetadata(var2, var3, this);

               try {
                  var6.setFromTree(var4, var5);
                  return var6;
               } catch (IIOInvalidTreeException var8) {
                  return null;
               }
            }
         }

         return null;
      }
   }

   public int getNumThumbnailsSupported(ImageTypeSpecifier var1, ImageWriteParam var2, IIOMetadata var3, IIOMetadata var4) {
      return this.jfifOK(var1, var2, var3, var4) ? Integer.MAX_VALUE : 0;
   }

   public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier var1, ImageWriteParam var2, IIOMetadata var3, IIOMetadata var4) {
      return this.jfifOK(var1, var2, var3, var4) ? (Dimension[])((Dimension[])preferredThumbSizes.clone()) : null;
   }

   private boolean jfifOK(ImageTypeSpecifier var1, ImageWriteParam var2, IIOMetadata var3, IIOMetadata var4) {
      if (var1 != null && !JPEG.isJFIFcompliant(var1, true)) {
         return false;
      } else {
         if (var4 != null) {
            JPEGMetadata var5 = null;
            if (var4 instanceof JPEGMetadata) {
               var5 = (JPEGMetadata)var4;
            } else {
               var5 = (JPEGMetadata)this.convertImageMetadata(var4, var1, var2);
            }

            if (var5.findMarkerSegment(JFIFMarkerSegment.class, true) == null) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      this.setThreadLock();

      try {
         this.cbLock.check();
         this.writeOnThread(var1, var2, var3);
      } finally {
         this.clearThreadLock();
      }

   }

   private void writeOnThread(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      if (this.ios == null) {
         throw new IllegalStateException("Output has not been set!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("image is null!");
      } else {
         if (var1 != null) {
            this.warningOccurred(1);
         }

         boolean var4 = var2.hasRaster();
         RenderedImage var5 = null;
         if (var4) {
            this.srcRas = var2.getRaster();
         } else {
            var5 = var2.getRenderedImage();
            if (var5 instanceof BufferedImage) {
               this.srcRas = ((BufferedImage)var5).getRaster();
            } else if (var5.getNumXTiles() == 1 && var5.getNumYTiles() == 1) {
               this.srcRas = var5.getTile(var5.getMinTileX(), var5.getMinTileY());
               if (this.srcRas.getWidth() != var5.getWidth() || this.srcRas.getHeight() != var5.getHeight()) {
                  this.srcRas = this.srcRas.createChild(this.srcRas.getMinX(), this.srcRas.getMinY(), var5.getWidth(), var5.getHeight(), this.srcRas.getMinX(), this.srcRas.getMinY(), (int[])null);
               }
            } else {
               this.srcRas = var5.getData();
            }
         }

         int var6 = this.srcRas.getNumBands();
         this.indexed = false;
         this.indexCM = null;
         ColorModel var7 = null;
         ColorSpace var8 = null;
         this.isAlphaPremultiplied = false;
         this.srcCM = null;
         if (!var4) {
            var7 = var5.getColorModel();
            if (var7 != null) {
               var8 = var7.getColorSpace();
               if (var7 instanceof IndexColorModel) {
                  this.indexed = true;
                  this.indexCM = (IndexColorModel)var7;
                  var6 = var7.getNumComponents();
               }

               if (var7.isAlphaPremultiplied()) {
                  this.isAlphaPremultiplied = true;
                  this.srcCM = var7;
               }
            }
         }

         this.srcBands = JPEG.bandOffsets[var6 - 1];
         int var9 = var6;
         if (var3 != null) {
            int[] var10 = var3.getSourceBands();
            if (var10 != null) {
               if (this.indexed) {
                  this.warningOccurred(8);
               } else {
                  this.srcBands = var10;
                  var9 = this.srcBands.length;
                  if (var9 > var6) {
                     throw new IIOException("ImageWriteParam specifies too many source bands");
                  }
               }
            }
         }

         boolean var58 = var9 != var6;
         boolean var11 = !var4 && !var58;
         Object var12 = null;
         int[] var13;
         int var14;
         int[] var59;
         if (!this.indexed) {
            var59 = this.srcRas.getSampleModel().getSampleSize();
            if (var58) {
               var13 = new int[var9];

               for(var14 = 0; var14 < var9; ++var14) {
                  var13[var14] = var59[this.srcBands[var14]];
               }

               var59 = var13;
            }
         } else {
            var13 = this.srcRas.getSampleModel().getSampleSize();
            var59 = new int[var6];

            for(var14 = 0; var14 < var6; ++var14) {
               var59[var14] = var13[0];
            }
         }

         int var60;
         for(var60 = 0; var60 < var59.length; ++var60) {
            if (var59[var60] <= 0 || var59[var60] > 8) {
               throw new IIOException("Illegal band size: should be 0 < size <= 8");
            }

            if (this.indexed) {
               var59[var60] = 8;
            }
         }

         if (this.debug) {
            System.out.println("numSrcBands is " + var6);
            System.out.println("numBandsUsed is " + var9);
            System.out.println("usingBandSubset is " + var58);
            System.out.println("fullImage is " + var11);
            System.out.print("Band sizes:");

            for(var60 = 0; var60 < var59.length; ++var60) {
               System.out.print(" " + var59[var60]);
            }

            System.out.println();
         }

         ImageTypeSpecifier var61 = null;
         if (var3 != null) {
            var61 = var3.getDestinationType();
            if (var11 && var61 != null) {
               this.warningOccurred(0);
               var61 = null;
            }
         }

         this.sourceXOffset = this.srcRas.getMinX();
         this.sourceYOffset = this.srcRas.getMinY();
         var14 = this.srcRas.getWidth();
         int var15 = this.srcRas.getHeight();
         this.sourceWidth = var14;
         this.sourceHeight = var15;
         int var16 = 1;
         int var17 = 1;
         int var18 = 0;
         int var19 = 0;
         JPEGQTable[] var20 = null;
         JPEGHuffmanTable[] var21 = null;
         JPEGHuffmanTable[] var22 = null;
         boolean var23 = false;
         JPEGImageWriteParam var24 = null;
         int var25 = 0;
         if (var3 != null) {
            Rectangle var26 = var3.getSourceRegion();
            if (var26 != null) {
               Rectangle var27 = new Rectangle(this.sourceXOffset, this.sourceYOffset, this.sourceWidth, this.sourceHeight);
               var26 = var26.intersection(var27);
               this.sourceXOffset = var26.x;
               this.sourceYOffset = var26.y;
               this.sourceWidth = var26.width;
               this.sourceHeight = var26.height;
            }

            if (this.sourceWidth + this.sourceXOffset > var14) {
               this.sourceWidth = var14 - this.sourceXOffset;
            }

            if (this.sourceHeight + this.sourceYOffset > var15) {
               this.sourceHeight = var15 - this.sourceYOffset;
            }

            var16 = var3.getSourceXSubsampling();
            var17 = var3.getSourceYSubsampling();
            var18 = var3.getSubsamplingXOffset();
            var19 = var3.getSubsamplingYOffset();
            switch(var3.getCompressionMode()) {
            case 0:
               throw new IIOException("JPEG compression cannot be disabled");
            case 1:
               var20 = new JPEGQTable[]{JPEGQTable.K1Div2Luminance, JPEGQTable.K2Div2Chrominance};
               break;
            case 2:
               float var63 = var3.getCompressionQuality();
               var63 = JPEG.convertToLinearQuality(var63);
               var20 = new JPEGQTable[]{JPEGQTable.K1Luminance.getScaledInstance(var63, true), JPEGQTable.K2Chrominance.getScaledInstance(var63, true)};
            }

            var25 = var3.getProgressiveMode();
            if (var3 instanceof JPEGImageWriteParam) {
               var24 = (JPEGImageWriteParam)var3;
               var23 = var24.getOptimizeHuffmanTables();
            }
         }

         IIOMetadata var62 = var2.getMetadata();
         if (var62 != null) {
            if (var62 instanceof JPEGMetadata) {
               this.metadata = (JPEGMetadata)var62;
               if (this.debug) {
                  System.out.println("We have metadata, and it's JPEG metadata");
               }
            } else if (!var4) {
               ImageTypeSpecifier var64 = var61;
               if (var61 == null) {
                  var64 = new ImageTypeSpecifier(var5);
               }

               this.metadata = (JPEGMetadata)this.convertImageMetadata(var62, var64, var3);
            } else {
               this.warningOccurred(7);
            }
         }

         this.ignoreJFIF = false;
         this.ignoreAdobe = false;
         this.newAdobeTransform = -1;
         this.writeDefaultJFIF = false;
         this.writeAdobe = false;
         int var65 = 0;
         int var28 = 0;
         JFIFMarkerSegment var29 = null;
         AdobeMarkerSegment var30 = null;
         SOFMarkerSegment var31 = null;
         if (this.metadata != null) {
            var29 = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
            var30 = (AdobeMarkerSegment)this.metadata.findMarkerSegment(AdobeMarkerSegment.class, true);
            var31 = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
         }

         this.iccProfile = null;
         this.convertTosRGB = false;
         this.converted = null;
         int var34;
         boolean var35;
         if (var61 != null) {
            if (var9 != var61.getNumBands()) {
               throw new IIOException("Number of source bands != number of destination bands");
            }

            var8 = var61.getColorModel().getColorSpace();
            if (this.metadata != null) {
               this.checkSOFBands(var31, var9);
               this.checkJFIF(var29, var61, false);
               if (var29 != null && !this.ignoreJFIF && JPEG.isNonStandardICC(var8)) {
                  this.iccProfile = ((ICC_ColorSpace)var8).getProfile();
               }

               this.checkAdobe(var30, var61, false);
            } else {
               if (JPEG.isJFIFcompliant(var61, false)) {
                  this.writeDefaultJFIF = true;
                  if (JPEG.isNonStandardICC(var8)) {
                     this.iccProfile = ((ICC_ColorSpace)var8).getProfile();
                  }
               } else {
                  int var32 = JPEG.transformForType(var61, false);
                  if (var32 != -1) {
                     this.writeAdobe = true;
                     this.newAdobeTransform = var32;
                  }
               }

               this.metadata = new JPEGMetadata(var61, (ImageWriteParam)null, this);
            }

            var65 = this.getSrcCSType(var61);
            var28 = this.getDefaultDestCSType(var61);
         } else if (this.metadata == null) {
            if (var11) {
               this.metadata = new JPEGMetadata(new ImageTypeSpecifier(var5), var3, this);
               if (this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
                  var8 = var5.getColorModel().getColorSpace();
                  if (JPEG.isNonStandardICC(var8)) {
                     this.iccProfile = ((ICC_ColorSpace)var8).getProfile();
                  }
               }

               var65 = this.getSrcCSType(var5);
               var28 = this.getDefaultDestCSType(var5);
            }
         } else {
            this.checkSOFBands(var31, var9);
            if (var11) {
               new ImageTypeSpecifier(var5);
               var65 = this.getSrcCSType(var5);
               if (var7 != null) {
                  boolean var33 = var7.hasAlpha();
                  label1397:
                  switch(var8.getType()) {
                  case 5:
                     if (!var33) {
                        if (var29 != null) {
                           var28 = 3;
                           if (JPEG.isNonStandardICC(var8) || var8 instanceof ICC_ColorSpace && var29.iccSegment != null) {
                              this.iccProfile = ((ICC_ColorSpace)var8).getProfile();
                           }
                        } else if (var30 != null) {
                           switch(var30.transform) {
                           case 0:
                              var28 = 2;
                              break label1397;
                           case 1:
                              var28 = 3;
                              break label1397;
                           default:
                              this.warningOccurred(6);
                              this.newAdobeTransform = 0;
                              var28 = 2;
                           }
                        } else {
                           var34 = var31.getIDencodedCSType();
                           if (var34 != 0) {
                              var28 = var34;
                           } else {
                              var35 = this.isSubsampled(var31.componentSpecs);
                              if (var35) {
                                 var28 = 3;
                              } else {
                                 var28 = 2;
                              }
                           }
                        }
                     } else {
                        if (var29 != null) {
                           this.ignoreJFIF = true;
                           this.warningOccurred(5);
                        }

                        if (var30 != null) {
                           if (var30.transform != 0) {
                              this.newAdobeTransform = 0;
                              this.warningOccurred(6);
                           }

                           var28 = 6;
                        } else {
                           var34 = var31.getIDencodedCSType();
                           if (var34 != 0) {
                              var28 = var34;
                           } else {
                              var35 = this.isSubsampled(var31.componentSpecs);
                              var28 = var35 ? 7 : 6;
                           }
                        }
                     }
                     break;
                  case 6:
                     if (!var33) {
                        var28 = 1;
                     } else if (var29 != null) {
                        this.ignoreJFIF = true;
                        this.warningOccurred(5);
                     }

                     if (var30 != null && var30.transform != 0) {
                        this.newAdobeTransform = 0;
                        this.warningOccurred(6);
                     }
                     break;
                  case 13:
                     if (var8 == JPEG.JCS.getYCC()) {
                        if (!var33) {
                           if (var29 != null) {
                              this.convertTosRGB = true;
                              this.convertOp = new ColorConvertOp(var8, JPEG.JCS.sRGB, (RenderingHints)null);
                              var28 = 3;
                           } else if (var30 != null) {
                              if (var30.transform != 1) {
                                 this.newAdobeTransform = 1;
                                 this.warningOccurred(6);
                              }

                              var28 = 5;
                           } else {
                              var28 = 5;
                           }
                        } else {
                           if (var29 != null) {
                              this.ignoreJFIF = true;
                              this.warningOccurred(5);
                           } else if (var30 != null && var30.transform != 0) {
                              this.newAdobeTransform = 0;
                              this.warningOccurred(6);
                           }

                           var28 = 10;
                        }
                     }
                  }
               }
            }
         }

         boolean var66 = false;
         int[] var67 = null;
         if (this.metadata != null) {
            if (var31 == null) {
               var31 = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
            }

            if (var31 != null && var31.tag == 194) {
               var66 = true;
               if (var25 == 3) {
                  var67 = this.collectScans(this.metadata, var31);
               } else {
                  this.numScans = 0;
               }
            }

            if (var29 == null) {
               var29 = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
            }
         }

         this.thumbnails = var2.getThumbnails();
         var34 = var2.getNumThumbnails();
         this.forceJFIF = false;
         if (!this.writeDefaultJFIF) {
            if (this.metadata == null) {
               this.thumbnails = null;
               if (var34 != 0) {
                  this.warningOccurred(10);
               }
            } else if (!var11) {
               if (var29 == null) {
                  this.thumbnails = null;
                  if (var34 != 0) {
                     this.warningOccurred(10);
                  }
               }
            } else if (var29 == null) {
               if (var28 != 1 && var28 != 3) {
                  this.thumbnails = null;
                  if (var34 != 0) {
                     this.warningOccurred(10);
                  }
               } else if (var34 != 0) {
                  this.forceJFIF = true;
                  this.warningOccurred(11);
               }
            }
         }

         var35 = this.metadata != null || this.writeDefaultJFIF || this.writeAdobe;
         boolean var36 = true;
         boolean var37 = true;
         DQTMarkerSegment var38 = null;
         DHTMarkerSegment var39 = null;
         int var40 = 0;
         if (this.metadata != null) {
            var38 = (DQTMarkerSegment)this.metadata.findMarkerSegment(DQTMarkerSegment.class, true);
            var39 = (DHTMarkerSegment)this.metadata.findMarkerSegment(DHTMarkerSegment.class, true);
            DRIMarkerSegment var41 = (DRIMarkerSegment)this.metadata.findMarkerSegment(DRIMarkerSegment.class, true);
            if (var41 != null) {
               var40 = var41.restartInterval;
            }

            if (var38 == null) {
               var36 = false;
            }

            if (var39 == null) {
               var37 = false;
            }
         }

         if (var20 == null) {
            if (var38 != null) {
               var20 = this.collectQTablesFromMetadata(this.metadata);
            } else if (this.streamQTables != null) {
               var20 = this.streamQTables;
            } else if (var24 != null && var24.areTablesSet()) {
               var20 = var24.getQTables();
            } else {
               var20 = JPEG.getDefaultQTables();
            }
         }

         if (!var23) {
            if (var39 != null && !var66) {
               var21 = this.collectHTablesFromMetadata(this.metadata, true);
               var22 = this.collectHTablesFromMetadata(this.metadata, false);
            } else if (this.streamDCHuffmanTables != null) {
               var21 = this.streamDCHuffmanTables;
               var22 = this.streamACHuffmanTables;
            } else if (var24 != null && var24.areTablesSet()) {
               var21 = var24.getDCHuffmanTables();
               var22 = var24.getACHuffmanTables();
            } else {
               var21 = JPEG.getDefaultHuffmanTables(true);
               var22 = JPEG.getDefaultHuffmanTables(false);
            }
         }

         int[] var68 = new int[var9];
         int[] var42 = new int[var9];
         int[] var43 = new int[var9];
         int[] var44 = new int[var9];

         int var45;
         for(var45 = 0; var45 < var9; ++var45) {
            var68[var45] = var45 + 1;
            var42[var45] = 1;
            var43[var45] = 1;
            var44[var45] = 0;
         }

         if (var31 != null) {
            for(var45 = 0; var45 < var9; ++var45) {
               if (!this.forceJFIF) {
                  var68[var45] = var31.componentSpecs[var45].componentId;
               }

               var42[var45] = var31.componentSpecs[var45].HsamplingFactor;
               var43[var45] = var31.componentSpecs[var45].VsamplingFactor;
               var44[var45] = var31.componentSpecs[var45].QtableSelector;
            }
         }

         this.sourceXOffset += var18;
         this.sourceWidth -= var18;
         this.sourceYOffset += var19;
         this.sourceHeight -= var19;
         var45 = (this.sourceWidth + var16 - 1) / var16;
         int var46 = (this.sourceHeight + var17 - 1) / var17;
         int var47 = this.sourceWidth * var9;
         DataBufferByte var48 = new DataBufferByte(var47);
         int[] var49 = JPEG.bandOffsets[var9 - 1];
         this.raster = Raster.createInterleavedRaster(var48, this.sourceWidth, 1, var47, var9, var49, (Point)null);
         this.clearAbortRequest();
         this.cbLock.lock();

         try {
            this.processImageStarted(this.currentImage);
         } finally {
            this.cbLock.unlock();
         }

         boolean var50 = false;
         if (this.debug) {
            System.out.println("inCsType: " + var65);
            System.out.println("outCsType: " + var28);
         }

         var50 = this.writeImage(this.structPointer, var48.getData(), var65, var28, var9, var59, this.sourceWidth, var45, var46, var16, var17, var20, var36, var21, var22, var37, var23, var25 != 0, this.numScans, var67, var68, var42, var43, var44, var35, var40);
         this.cbLock.lock();

         try {
            if (var50) {
               this.processWriteAborted();
            } else {
               this.processImageComplete();
            }

            this.ios.flush();
         } finally {
            this.cbLock.unlock();
         }

         ++this.currentImage;
      }
   }

   public boolean canWriteSequence() {
      return true;
   }

   public void prepareWriteSequence(IIOMetadata var1) throws IOException {
      this.setThreadLock();

      try {
         this.cbLock.check();
         this.prepareWriteSequenceOnThread(var1);
      } finally {
         this.clearThreadLock();
      }

   }

   private void prepareWriteSequenceOnThread(IIOMetadata var1) throws IOException {
      if (this.ios == null) {
         throw new IllegalStateException("Output has not been set!");
      } else {
         if (var1 != null) {
            if (!(var1 instanceof JPEGMetadata)) {
               throw new IIOException("Stream metadata must be JPEG metadata");
            }

            JPEGMetadata var2 = (JPEGMetadata)var1;
            if (!var2.isStream) {
               throw new IllegalArgumentException("Invalid stream metadata object.");
            }

            if (this.currentImage != 0) {
               throw new IIOException("JPEG Stream metadata must precede all images");
            }

            if (this.sequencePrepared) {
               throw new IIOException("Stream metadata already written!");
            }

            this.streamQTables = this.collectQTablesFromMetadata(var2);
            if (this.debug) {
               System.out.println("after collecting from stream metadata, streamQTables.length is " + this.streamQTables.length);
            }

            if (this.streamQTables == null) {
               this.streamQTables = JPEG.getDefaultQTables();
            }

            this.streamDCHuffmanTables = this.collectHTablesFromMetadata(var2, true);
            if (this.streamDCHuffmanTables == null) {
               this.streamDCHuffmanTables = JPEG.getDefaultHuffmanTables(true);
            }

            this.streamACHuffmanTables = this.collectHTablesFromMetadata(var2, false);
            if (this.streamACHuffmanTables == null) {
               this.streamACHuffmanTables = JPEG.getDefaultHuffmanTables(false);
            }

            this.writeTables(this.structPointer, this.streamQTables, this.streamDCHuffmanTables, this.streamACHuffmanTables);
         }

         this.sequencePrepared = true;
      }
   }

   public void writeToSequence(IIOImage var1, ImageWriteParam var2) throws IOException {
      this.setThreadLock();

      try {
         this.cbLock.check();
         if (!this.sequencePrepared) {
            throw new IllegalStateException("sequencePrepared not called!");
         }

         this.write((IIOMetadata)null, var1, var2);
      } finally {
         this.clearThreadLock();
      }

   }

   public void endWriteSequence() throws IOException {
      this.setThreadLock();

      try {
         this.cbLock.check();
         if (!this.sequencePrepared) {
            throw new IllegalStateException("sequencePrepared not called!");
         }

         this.sequencePrepared = false;
      } finally {
         this.clearThreadLock();
      }

   }

   public synchronized void abort() {
      this.setThreadLock();

      try {
         super.abort();
         this.abortWrite(this.structPointer);
      } finally {
         this.clearThreadLock();
      }

   }

   protected synchronized void clearAbortRequest() {
      this.setThreadLock();

      try {
         this.cbLock.check();
         if (this.abortRequested()) {
            super.clearAbortRequest();
            this.resetWriter(this.structPointer);
            this.setDest(this.structPointer);
         }
      } finally {
         this.clearThreadLock();
      }

   }

   private void resetInternalState() {
      this.resetWriter(this.structPointer);
      this.srcRas = null;
      this.raster = null;
      this.convertTosRGB = false;
      this.currentImage = 0;
      this.numScans = 0;
      this.metadata = null;
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

   void warningOccurred(int var1) {
      this.cbLock.lock();

      try {
         if (var1 < 0 || var1 > 15) {
            throw new InternalError("Invalid warning index");
         }

         this.processWarningOccurred(this.currentImage, "com.sun.imageio.plugins.jpeg.JPEGImageWriterResources", Integer.toString(var1));
      } finally {
         this.cbLock.unlock();
      }

   }

   void warningWithMessage(String var1) {
      this.cbLock.lock();

      try {
         this.processWarningOccurred(this.currentImage, var1);
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

   private void checkSOFBands(SOFMarkerSegment var1, int var2) throws IIOException {
      if (var1 != null && var1.componentSpecs.length != var2) {
         throw new IIOException("Metadata components != number of destination bands");
      }
   }

   private void checkJFIF(JFIFMarkerSegment var1, ImageTypeSpecifier var2, boolean var3) {
      if (var1 != null && !JPEG.isJFIFcompliant(var2, var3)) {
         this.ignoreJFIF = true;
         this.warningOccurred(var3 ? 5 : 3);
      }

   }

   private void checkAdobe(AdobeMarkerSegment var1, ImageTypeSpecifier var2, boolean var3) {
      if (var1 != null) {
         int var4 = JPEG.transformForType(var2, var3);
         if (var1.transform != var4) {
            this.warningOccurred(var3 ? 6 : 4);
            if (var4 == -1) {
               this.ignoreAdobe = true;
            } else {
               this.newAdobeTransform = var4;
            }
         }
      }

   }

   private int[] collectScans(JPEGMetadata var1, SOFMarkerSegment var2) {
      ArrayList var3 = new ArrayList();
      byte var4 = 9;
      byte var5 = 4;
      Iterator var6 = var1.markerSequence.iterator();

      while(var6.hasNext()) {
         MarkerSegment var7 = (MarkerSegment)var6.next();
         if (var7 instanceof SOSMarkerSegment) {
            var3.add(var7);
         }
      }

      int[] var13 = null;
      this.numScans = 0;
      if (!var3.isEmpty()) {
         this.numScans = var3.size();
         var13 = new int[this.numScans * var4];
         int var14 = 0;

         for(int var8 = 0; var8 < this.numScans; ++var8) {
            SOSMarkerSegment var9 = (SOSMarkerSegment)var3.get(var8);
            var13[var14++] = var9.componentSpecs.length;

            for(int var10 = 0; var10 < var5; ++var10) {
               if (var10 < var9.componentSpecs.length) {
                  int var11 = var9.componentSpecs[var10].componentSelector;

                  for(int var12 = 0; var12 < var2.componentSpecs.length; ++var12) {
                     if (var11 == var2.componentSpecs[var12].componentId) {
                        var13[var14++] = var12;
                        break;
                     }
                  }
               } else {
                  var13[var14++] = 0;
               }
            }

            var13[var14++] = var9.startSpectralSelection;
            var13[var14++] = var9.endSpectralSelection;
            var13[var14++] = var9.approxHigh;
            var13[var14++] = var9.approxLow;
         }
      }

      return var13;
   }

   private JPEGQTable[] collectQTablesFromMetadata(JPEGMetadata var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.markerSequence.iterator();

      while(var3.hasNext()) {
         MarkerSegment var4 = (MarkerSegment)var3.next();
         if (var4 instanceof DQTMarkerSegment) {
            DQTMarkerSegment var5 = (DQTMarkerSegment)var4;
            var2.addAll(var5.tables);
         }
      }

      JPEGQTable[] var6 = null;
      if (var2.size() != 0) {
         var6 = new JPEGQTable[var2.size()];

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var6[var7] = new JPEGQTable(((DQTMarkerSegment.Qtable)var2.get(var7)).data);
         }
      }

      return var6;
   }

   private JPEGHuffmanTable[] collectHTablesFromMetadata(JPEGMetadata var1, boolean var2) throws IIOException {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var1.markerSequence.iterator();

      while(true) {
         MarkerSegment var5;
         int var7;
         do {
            if (!var4.hasNext()) {
               JPEGHuffmanTable[] var9 = null;
               if (var3.size() != 0) {
                  DHTMarkerSegment.Htable[] var10 = new DHTMarkerSegment.Htable[var3.size()];
                  var3.toArray(var10);
                  var9 = new JPEGHuffmanTable[var3.size()];

                  for(var7 = 0; var7 < var9.length; ++var7) {
                     var9[var7] = null;

                     for(int var11 = 0; var11 < var3.size(); ++var11) {
                        if (var10[var11].tableID == var7) {
                           if (var9[var7] != null) {
                              throw new IIOException("Metadata has duplicate Htables!");
                           }

                           var9[var7] = new JPEGHuffmanTable(var10[var11].numCodes, var10[var11].values);
                        }
                     }
                  }
               }

               return var9;
            }

            var5 = (MarkerSegment)var4.next();
         } while(!(var5 instanceof DHTMarkerSegment));

         DHTMarkerSegment var6 = (DHTMarkerSegment)var5;

         for(var7 = 0; var7 < var6.tables.size(); ++var7) {
            DHTMarkerSegment.Htable var8 = (DHTMarkerSegment.Htable)var6.tables.get(var7);
            if (var8.tableClass == (var2 ? 0 : 1)) {
               var3.add(var8);
            }
         }
      }
   }

   private int getSrcCSType(ImageTypeSpecifier var1) {
      return this.getSrcCSType(var1.getColorModel());
   }

   private int getSrcCSType(RenderedImage var1) {
      return this.getSrcCSType(var1.getColorModel());
   }

   private int getSrcCSType(ColorModel var1) {
      byte var2 = 0;
      if (var1 != null) {
         boolean var3 = var1.hasAlpha();
         ColorSpace var4 = var1.getColorSpace();
         switch(var4.getType()) {
         case 3:
            if (var3) {
               var2 = 7;
            } else {
               var2 = 3;
            }
         case 4:
         case 7:
         case 8:
         case 10:
         case 11:
         case 12:
         default:
            break;
         case 5:
            if (var3) {
               var2 = 6;
            } else {
               var2 = 2;
            }
            break;
         case 6:
            var2 = 1;
            break;
         case 13:
            if (var4 == JPEG.JCS.getYCC()) {
               boolean var5;
               if (var3) {
                  var5 = true;
               } else {
                  var5 = true;
               }
            }
         case 9:
            var2 = 4;
         }
      }

      return var2;
   }

   private int getDestCSType(ImageTypeSpecifier var1) {
      ColorModel var2 = var1.getColorModel();
      boolean var3 = var2.hasAlpha();
      ColorSpace var4 = var2.getColorSpace();
      byte var5 = 0;
      switch(var4.getType()) {
      case 3:
         if (var3) {
            var5 = 7;
         } else {
            var5 = 3;
         }
      case 4:
      case 7:
      case 8:
      case 10:
      case 11:
      case 12:
      default:
         break;
      case 5:
         if (var3) {
            var5 = 6;
         } else {
            var5 = 2;
         }
         break;
      case 6:
         var5 = 1;
         break;
      case 13:
         if (var4 == JPEG.JCS.getYCC()) {
            boolean var6;
            if (var3) {
               var6 = true;
            } else {
               var6 = true;
            }
         }
      case 9:
         var5 = 4;
      }

      return var5;
   }

   private int getDefaultDestCSType(ImageTypeSpecifier var1) {
      return this.getDefaultDestCSType(var1.getColorModel());
   }

   private int getDefaultDestCSType(RenderedImage var1) {
      return this.getDefaultDestCSType(var1.getColorModel());
   }

   private int getDefaultDestCSType(ColorModel var1) {
      byte var2 = 0;
      if (var1 != null) {
         boolean var3 = var1.hasAlpha();
         ColorSpace var4 = var1.getColorSpace();
         switch(var4.getType()) {
         case 3:
            if (var3) {
               var2 = 7;
            } else {
               var2 = 3;
            }
         case 4:
         case 7:
         case 8:
         case 10:
         case 11:
         case 12:
         default:
            break;
         case 5:
            if (var3) {
               var2 = 7;
            } else {
               var2 = 3;
            }
            break;
         case 6:
            var2 = 1;
            break;
         case 13:
            if (var4 == JPEG.JCS.getYCC()) {
               boolean var5;
               if (var3) {
                  var5 = true;
               } else {
                  var5 = true;
               }
            }
         case 9:
            var2 = 11;
         }
      }

      return var2;
   }

   private boolean isSubsampled(SOFMarkerSegment.ComponentSpec[] var1) {
      int var2 = var1[0].HsamplingFactor;
      int var3 = var1[0].VsamplingFactor;

      for(int var4 = 1; var4 < var1.length; ++var4) {
         if (var1[var4].HsamplingFactor != var2 || var1[var4].HsamplingFactor != var2) {
            return true;
         }
      }

      return false;
   }

   private static native void initWriterIDs(Class var0, Class var1);

   private native long initJPEGImageWriter();

   private native void setDest(long var1);

   private native boolean writeImage(long var1, byte[] var3, int var4, int var5, int var6, int[] var7, int var8, int var9, int var10, int var11, int var12, JPEGQTable[] var13, boolean var14, JPEGHuffmanTable[] var15, JPEGHuffmanTable[] var16, boolean var17, boolean var18, boolean var19, int var20, int[] var21, int[] var22, int[] var23, int[] var24, int[] var25, boolean var26, int var27);

   private void writeMetadata() throws IOException {
      if (this.metadata == null) {
         if (this.writeDefaultJFIF) {
            JFIFMarkerSegment.writeDefaultJFIF(this.ios, this.thumbnails, this.iccProfile, this);
         }

         if (this.writeAdobe) {
            AdobeMarkerSegment.writeAdobeSegment(this.ios, this.newAdobeTransform);
         }
      } else {
         this.metadata.writeToStream(this.ios, this.ignoreJFIF, this.forceJFIF, this.thumbnails, this.iccProfile, this.ignoreAdobe, this.newAdobeTransform, this);
      }

   }

   private native void writeTables(long var1, JPEGQTable[] var3, JPEGHuffmanTable[] var4, JPEGHuffmanTable[] var5);

   private void grabPixels(int var1) {
      Raster var2 = null;
      BufferedImage var4;
      Object var8;
      if (this.indexed) {
         var2 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + var1, this.sourceWidth, 1, 0, 0, new int[]{0});
         boolean var3 = this.indexCM.getTransparency() != 1;
         var4 = this.indexCM.convertToIntDiscrete(var2, var3);
         var8 = var4.getRaster();
      } else {
         var8 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + var1, this.sourceWidth, 1, 0, 0, this.srcBands);
      }

      if (this.convertTosRGB) {
         if (this.debug) {
            System.out.println("Converting to sRGB");
         }

         this.converted = this.convertOp.filter((Raster)var8, (WritableRaster)this.converted);
         var8 = this.converted;
      }

      if (this.isAlphaPremultiplied) {
         WritableRaster var9 = ((Raster)var8).createCompatibleWritableRaster();
         var4 = null;
         int[] var10 = ((Raster)var8).getPixels(((Raster)var8).getMinX(), ((Raster)var8).getMinY(), ((Raster)var8).getWidth(), ((Raster)var8).getHeight(), (int[])var4);
         var9.setPixels(((Raster)var8).getMinX(), ((Raster)var8).getMinY(), ((Raster)var8).getWidth(), ((Raster)var8).getHeight(), var10);
         this.srcCM.coerceData(var9, false);
         var8 = var9.createChild(var9.getMinX(), var9.getMinY(), var9.getWidth(), var9.getHeight(), 0, 0, this.srcBands);
      }

      this.raster.setRect((Raster)var8);
      if (var1 > 7 && var1 % 8 == 0) {
         this.cbLock.lock();

         try {
            this.processImageProgress((float)var1 / (float)this.sourceHeight * 100.0F);
         } finally {
            this.cbLock.unlock();
         }
      }

   }

   private native void abortWrite(long var1);

   private native void resetWriter(long var1);

   private static native void disposeWriter(long var0);

   private void writeOutputData(byte[] var1, int var2, int var3) throws IOException {
      this.cbLock.lock();

      try {
         this.ios.write(var1, var2, var3);
      } finally {
         this.cbLock.unlock();
      }

   }

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
         throw new IllegalStateException("Attempt to clear thread lock form wrong thread. Locked thread: " + this.theThread + "; current thread: " + var1);
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("jpeg");
            return null;
         }
      });
      initWriterIDs(JPEGQTable.class, JPEGHuffmanTable.class);
      preferredThumbSizes = new Dimension[]{new Dimension(1, 1), new Dimension(255, 255)};
   }

   private static class CallBackLock {
      private JPEGImageWriter.CallBackLock.State lockState;

      CallBackLock() {
         this.lockState = JPEGImageWriter.CallBackLock.State.Unlocked;
      }

      void check() {
         if (this.lockState != JPEGImageWriter.CallBackLock.State.Unlocked) {
            throw new IllegalStateException("Access to the writer is not allowed");
         }
      }

      private void lock() {
         this.lockState = JPEGImageWriter.CallBackLock.State.Locked;
      }

      private void unlock() {
         this.lockState = JPEGImageWriter.CallBackLock.State.Unlocked;
      }

      private static enum State {
         Unlocked,
         Locked;
      }
   }

   private static class JPEGWriterDisposerRecord implements DisposerRecord {
      private long pData;

      public JPEGWriterDisposerRecord(long var1) {
         this.pData = var1;
      }

      public synchronized void dispose() {
         if (this.pData != 0L) {
            JPEGImageWriter.disposeWriter(this.pData);
            this.pData = 0L;
         }

      }
   }
}
