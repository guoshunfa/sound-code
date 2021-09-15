package sun.awt.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageRepresentation extends ImageWatched implements ImageConsumer {
   InputStreamImageSource src;
   ToolkitImage image;
   int tag;
   long pData;
   int width = -1;
   int height = -1;
   int hints;
   int availinfo;
   Rectangle newbits;
   BufferedImage bimage;
   WritableRaster biRaster;
   protected ColorModel cmodel;
   ColorModel srcModel = null;
   int[] srcLUT = null;
   int srcLUTtransIndex = -1;
   int numSrcLUT = 0;
   boolean forceCMhint;
   int sstride;
   boolean isDefaultBI = false;
   boolean isSameCM = false;
   static boolean s_useNative;
   private boolean consuming = false;
   private int numWaiters;

   private static native void initIDs();

   public ImageRepresentation(ToolkitImage var1, ColorModel var2, boolean var3) {
      this.image = var1;
      if (this.image.getSource() instanceof InputStreamImageSource) {
         this.src = (InputStreamImageSource)this.image.getSource();
      }

      this.setColorModel(var2);
      this.forceCMhint = var3;
   }

   public synchronized void reconstruct(int var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      int var2 = var1 & ~this.availinfo;
      if ((this.availinfo & 64) == 0 && var2 != 0) {
         ++this.numWaiters;

         try {
            this.startProduction();

            for(var2 = var1 & ~this.availinfo; (this.availinfo & 64) == 0 && var2 != 0; var2 = var1 & ~this.availinfo) {
               try {
                  this.wait();
               } catch (InterruptedException var7) {
                  Thread.currentThread().interrupt();
                  return;
               }
            }

         } finally {
            this.decrementWaiters();
         }
      }
   }

   public void setDimensions(int var1, int var2) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      this.image.setDimensions(var1, var2);
      this.newInfo(this.image, 3, 0, 0, var1, var2);
      if (var1 > 0 && var2 > 0) {
         if (this.width != var1 || this.height != var2) {
            this.bimage = null;
         }

         this.width = var1;
         this.height = var2;
         this.availinfo |= 3;
      } else {
         this.imageComplete(1);
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   ColorModel getColorModel() {
      return this.cmodel;
   }

   BufferedImage getBufferedImage() {
      return this.bimage;
   }

   protected BufferedImage createImage(ColorModel var1, WritableRaster var2, boolean var3, Hashtable var4) {
      BufferedImage var5 = new BufferedImage(var1, var2, var3, (Hashtable)null);
      var5.setAccelerationPriority(this.image.getAccelerationPriority());
      return var5;
   }

   public void setProperties(Hashtable<?, ?> var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      this.image.setProperties(var1);
      this.newInfo(this.image, 4, 0, 0, 0, 0);
   }

   public void setColorModel(ColorModel var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      this.srcModel = var1;
      if (var1 instanceof IndexColorModel) {
         if (var1.getTransparency() == 3) {
            this.cmodel = ColorModel.getRGBdefault();
            this.srcLUT = null;
         } else {
            IndexColorModel var2 = (IndexColorModel)var1;
            this.numSrcLUT = var2.getMapSize();
            this.srcLUT = new int[Math.max(this.numSrcLUT, 256)];
            var2.getRGBs(this.srcLUT);
            this.srcLUTtransIndex = var2.getTransparentPixel();
            this.cmodel = var1;
         }
      } else if (this.cmodel == null) {
         this.cmodel = var1;
         this.srcLUT = null;
      } else if (var1 instanceof DirectColorModel) {
         DirectColorModel var3 = (DirectColorModel)var1;
         if (var3.getRedMask() == 16711680 && var3.getGreenMask() == 65280 && var3.getBlueMask() == 255) {
            this.cmodel = var1;
            this.srcLUT = null;
         }
      }

      this.isSameCM = this.cmodel == var1;
   }

   void createBufferedImage() {
      this.isDefaultBI = false;

      try {
         this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
         this.bimage = this.createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), (Hashtable)null);
      } catch (Exception var3) {
         this.cmodel = ColorModel.getRGBdefault();
         this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
         this.bimage = this.createImage(this.cmodel, this.biRaster, false, (Hashtable)null);
      }

      int var1 = this.bimage.getType();
      if (this.cmodel != ColorModel.getRGBdefault() && var1 != 1 && var1 != 3) {
         if (this.cmodel instanceof DirectColorModel) {
            DirectColorModel var2 = (DirectColorModel)this.cmodel;
            if (var2.getRedMask() == 16711680 && var2.getGreenMask() == 65280 && var2.getBlueMask() == 255) {
               this.isDefaultBI = true;
            }
         }
      } else {
         this.isDefaultBI = true;
      }

   }

   private void convertToRGB() {
      int var1 = this.bimage.getWidth();
      int var2 = this.bimage.getHeight();
      int var3 = var1 * var2;
      DataBufferInt var4 = new DataBufferInt(var3);
      int[] var5 = SunWritableRaster.stealData((DataBufferInt)var4, 0);
      int var8;
      int var9;
      if (this.cmodel instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
         ByteComponentRaster var10 = (ByteComponentRaster)this.biRaster;
         byte[] var12 = var10.getDataStorage();
         var8 = var10.getDataOffset(0);

         for(var9 = 0; var9 < var3; ++var9) {
            var5[var9] = this.srcLUT[var12[var8 + var9] & 255];
         }
      } else {
         Object var6 = null;
         int var7 = 0;

         for(var8 = 0; var8 < var2; ++var8) {
            for(var9 = 0; var9 < var1; ++var9) {
               var6 = this.biRaster.getDataElements(var9, var8, var6);
               var5[var7++] = this.cmodel.getRGB(var6);
            }
         }
      }

      SunWritableRaster.markDirty((DataBuffer)var4);
      this.isSameCM = false;
      this.cmodel = ColorModel.getRGBdefault();
      int[] var11 = new int[]{16711680, 65280, 255, -16777216};
      this.biRaster = Raster.createPackedRaster(var4, var1, var2, var1, var11, (Point)null);
      this.bimage = this.createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), (Hashtable)null);
      this.srcLUT = null;
      this.isDefaultBI = true;
   }

   public void setHints(int var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      this.hints = var1;
   }

   private native boolean setICMpixels(int var1, int var2, int var3, int var4, int[] var5, byte[] var6, int var7, int var8, IntegerComponentRaster var9);

   private native boolean setDiffICM(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7, IndexColorModel var8, byte[] var9, int var10, int var11, ByteComponentRaster var12, int var13);

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      int var9 = var7;
      Object var11 = null;
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      synchronized(this) {
         label233: {
            if (this.bimage == null) {
               if (this.cmodel == null) {
                  this.cmodel = var5;
               }

               this.createBufferedImage();
            }

            if (var3 > 0 && var4 > 0) {
               int var13 = this.biRaster.getWidth();
               int var14 = this.biRaster.getHeight();
               int var15 = var1 + var3;
               int var16 = var2 + var4;
               if (var1 < 0) {
                  var7 -= var1;
                  var1 = 0;
               } else if (var15 < 0) {
                  var15 = var13;
               }

               if (var2 < 0) {
                  var7 -= var2 * var8;
                  var2 = 0;
               } else if (var16 < 0) {
                  var16 = var14;
               }

               if (var15 > var13) {
                  var15 = var13;
               }

               if (var16 > var14) {
                  var16 = var14;
               }

               if (var1 < var15 && var2 < var16) {
                  var3 = var15 - var1;
                  var4 = var16 - var2;
                  if (var7 >= 0 && var7 < var6.length) {
                     int var17 = var6.length - var7;
                     if (var17 < var3) {
                        throw new ArrayIndexOutOfBoundsException("Data array is too short.");
                     }

                     int var18;
                     if (var8 < 0) {
                        var18 = var7 / -var8 + 1;
                     } else if (var8 > 0) {
                        var18 = (var17 - var3) / var8 + 1;
                     } else {
                        var18 = var4;
                     }

                     if (var4 > var18) {
                        throw new ArrayIndexOutOfBoundsException("Data array is too short.");
                     }

                     int var21;
                     int var23;
                     if (this.isSameCM && this.cmodel != var5 && this.srcLUT != null && var5 instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster) {
                        IndexColorModel var19 = (IndexColorModel)var5;
                        ByteComponentRaster var20 = (ByteComponentRaster)this.biRaster;
                        var21 = this.numSrcLUT;
                        if (this.setDiffICM(var1, var2, var3, var4, this.srcLUT, this.srcLUTtransIndex, this.numSrcLUT, var19, var6, var7, var8, var20, var20.getDataOffset(0))) {
                           var20.markDirty();
                           if (var21 != this.numSrcLUT) {
                              boolean var33 = var19.hasAlpha();
                              if (this.srcLUTtransIndex != -1) {
                                 var33 = true;
                              }

                              var23 = var19.getPixelSize();
                              var19 = new IndexColorModel(var23, this.numSrcLUT, this.srcLUT, 0, var33, this.srcLUTtransIndex, var23 > 8 ? 1 : 0);
                              this.cmodel = var19;
                              this.bimage = this.createImage(var19, var20, false, (Hashtable)null);
                           }

                           return;
                        }

                        this.convertToRGB();
                     }

                     int var10;
                     if (this.isDefaultBI) {
                        IntegerComponentRaster var31 = (IntegerComponentRaster)this.biRaster;
                        int var22;
                        int[] var32;
                        if (this.srcLUT != null && var5 instanceof IndexColorModel) {
                           if (var5 != this.srcModel) {
                              ((IndexColorModel)var5).getRGBs(this.srcLUT);
                              this.srcModel = var5;
                           }

                           if (s_useNative) {
                              if (!this.setICMpixels(var1, var2, var3, var4, this.srcLUT, var6, var7, var8, var31)) {
                                 this.abort();
                                 return;
                              }

                              var31.markDirty();
                              break label233;
                           }

                           var32 = new int[var3 * var4];
                           var22 = 0;

                           for(var23 = 0; var23 < var4; var9 += var8) {
                              var10 = var9;

                              for(int var24 = 0; var24 < var3; ++var24) {
                                 var32[var22++] = this.srcLUT[var6[var10++] & 255];
                              }

                              ++var23;
                           }

                           var31.setDataElements(var1, var2, var3, var4, (Object)var32);
                           break label233;
                        }

                        var32 = new int[var3];

                        for(var22 = var2; var22 < var2 + var4; var9 += var8) {
                           var10 = var9;

                           for(var23 = 0; var23 < var3; ++var23) {
                              var32[var23] = var5.getRGB(var6[var10++] & 255);
                           }

                           var31.setDataElements(var1, var22, var3, 1, (Object)var32);
                           ++var22;
                        }

                        this.availinfo |= 8;
                        break label233;
                     }

                     if (this.cmodel == var5 && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
                        ByteComponentRaster var28 = (ByteComponentRaster)this.biRaster;
                        if (var7 == 0 && var8 == var3) {
                           var28.putByteData(var1, var2, var3, var4, var6);
                           break label233;
                        }

                        byte[] var30 = new byte[var3];
                        var10 = var7;
                        var21 = var2;

                        while(true) {
                           if (var21 >= var2 + var4) {
                              break label233;
                           }

                           System.arraycopy(var6, var10, var30, 0, var3);
                           var28.putByteData(var1, var21, var3, 1, var30);
                           var10 += var8;
                           ++var21;
                        }
                     }

                     for(int var27 = var2; var27 < var2 + var4; var9 += var8) {
                        var10 = var9;

                        for(int var29 = var1; var29 < var1 + var3; ++var29) {
                           this.bimage.setRGB(var29, var27, var5.getRGB(var6[var10++] & 255));
                        }

                        ++var27;
                     }

                     this.availinfo |= 8;
                     break label233;
                  }

                  throw new ArrayIndexOutOfBoundsException("Data offset out of bounds.");
               }

               return;
            }

            return;
         }
      }

      if ((this.availinfo & 16) == 0) {
         this.newInfo(this.image, 8, var1, var2, var3, var4);
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      int var9 = var7;
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      synchronized(this) {
         if (this.bimage == null) {
            if (this.cmodel == null) {
               this.cmodel = var5;
            }

            this.createBufferedImage();
         }

         int[] var12 = new int[var3];
         if (this.cmodel instanceof IndexColorModel) {
            this.convertToRGB();
         }

         int var13;
         IntegerComponentRaster var21;
         if (var5 == this.cmodel && this.biRaster instanceof IntegerComponentRaster) {
            var21 = (IntegerComponentRaster)this.biRaster;
            if (var7 == 0 && var8 == var3) {
               var21.setDataElements(var1, var2, var3, var4, (Object)var6);
            } else {
               for(var13 = var2; var13 < var2 + var4; var9 += var8) {
                  System.arraycopy(var6, var9, var12, 0, var3);
                  var21.setDataElements(var1, var13, var3, 1, (Object)var12);
                  ++var13;
               }
            }
         } else {
            if (var5.getTransparency() != 1) {
               int var10000 = this.cmodel.getTransparency();
               ColorModel var10001 = this.cmodel;
               if (var10000 == 1) {
                  this.convertToRGB();
               }
            }

            int var10;
            if (this.isDefaultBI) {
               var21 = (IntegerComponentRaster)this.biRaster;
               int[] var22 = var21.getDataStorage();
               int var17;
               if (this.cmodel.equals(var5)) {
                  var17 = var21.getScanlineStride();
                  int var18 = var2 * var17 + var1;

                  for(var13 = 0; var13 < var4; var9 += var8) {
                     System.arraycopy(var6, var9, var22, var18, var3);
                     var18 += var17;
                     ++var13;
                  }

                  var21.markDirty();
               } else {
                  for(var13 = var2; var13 < var2 + var4; var9 += var8) {
                     var10 = var9;

                     for(var17 = 0; var17 < var3; ++var17) {
                        var12[var17] = var5.getRGB(var6[var10++]);
                     }

                     var21.setDataElements(var1, var13, var3, 1, (Object)var12);
                     ++var13;
                  }
               }

               this.availinfo |= 8;
            } else {
               Object var15 = null;
               var13 = var2;

               while(true) {
                  if (var13 >= var2 + var4) {
                     this.availinfo |= 8;
                     break;
                  }

                  var10 = var9;

                  for(int var16 = var1; var16 < var1 + var3; ++var16) {
                     int var14 = var5.getRGB(var6[var10++]);
                     var15 = this.cmodel.getDataElements(var14, var15);
                     this.biRaster.setDataElements(var16, var13, var15);
                  }

                  ++var13;
                  var9 += var8;
               }
            }
         }
      }

      if ((this.availinfo & 16) == 0) {
         this.newInfo(this.image, 8, var1, var2, var3, var4);
      }

   }

   public BufferedImage getOpaqueRGBImage() {
      if (this.bimage.getType() == 2) {
         int var1 = this.bimage.getWidth();
         int var2 = this.bimage.getHeight();
         int var3 = var1 * var2;
         DataBufferInt var4 = (DataBufferInt)this.biRaster.getDataBuffer();
         int[] var5 = SunWritableRaster.stealData((DataBufferInt)var4, 0);

         for(int var6 = 0; var6 < var3; ++var6) {
            if (var5[var6] >>> 24 != 255) {
               return this.bimage;
            }
         }

         DirectColorModel var11 = new DirectColorModel(24, 16711680, 65280, 255);
         int[] var7 = new int[]{16711680, 65280, 255};
         WritableRaster var8 = Raster.createPackedRaster(var4, var1, var2, var1, var7, (Point)null);

         try {
            BufferedImage var9 = this.createImage(var11, var8, false, (Hashtable)null);
            return var9;
         } catch (Exception var10) {
            return this.bimage;
         }
      } else {
         return this.bimage;
      }
   }

   public void imageComplete(int var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      boolean var2;
      short var3;
      switch(var1) {
      case 1:
         this.image.addInfo(64);
         var2 = true;
         var3 = 64;
         this.dispose();
         break;
      case 2:
         var2 = false;
         var3 = 16;
         break;
      case 3:
         var2 = true;
         var3 = 32;
         break;
      case 4:
      default:
         var2 = true;
         var3 = 128;
      }

      synchronized(this) {
         if (var2) {
            this.image.getSource().removeConsumer(this);
            this.consuming = false;
            this.newbits = null;
            if (this.bimage != null) {
               this.bimage = this.getOpaqueRGBImage();
            }
         }

         this.availinfo |= var3;
         this.notifyAll();
      }

      this.newInfo(this.image, var3, 0, 0, this.width, this.height);
      this.image.infoDone(var1);
   }

   void startProduction() {
      if (!this.consuming) {
         this.consuming = true;
         this.image.getSource().startProduction(this);
      }

   }

   private synchronized void checkConsumption() {
      if (this.isWatcherListEmpty() && this.numWaiters == 0 && (this.availinfo & 32) == 0) {
         this.dispose();
      }

   }

   public synchronized void notifyWatcherListEmpty() {
      this.checkConsumption();
   }

   private synchronized void decrementWaiters() {
      --this.numWaiters;
      this.checkConsumption();
   }

   public boolean prepare(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) != 0) {
         if (var1 != null) {
            var1.imageUpdate(this.image, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         boolean var2 = (this.availinfo & 32) != 0;
         if (!var2) {
            this.addWatcher(var1);
            this.startProduction();
            var2 = (this.availinfo & 32) != 0;
         }

         return var2;
      }
   }

   public int check(ImageObserver var1) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 96) == 0) {
         this.addWatcher(var1);
      }

      return this.availinfo;
   }

   public boolean drawToBufImage(Graphics var1, ToolkitImage var2, int var3, int var4, Color var5, ImageObserver var6) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) != 0) {
         if (var6 != null) {
            var6.imageUpdate(this.image, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         boolean var7 = (this.availinfo & 32) != 0;
         boolean var8 = (this.availinfo & 128) != 0;
         if (!var7 && !var8) {
            this.addWatcher(var6);
            this.startProduction();
            var7 = (this.availinfo & 32) != 0;
         }

         if (var7 || 0 != (this.availinfo & 16)) {
            var1.drawImage(this.bimage, var3, var4, var5, (ImageObserver)null);
         }

         return var7;
      }
   }

   public boolean drawToBufImage(Graphics var1, ToolkitImage var2, int var3, int var4, int var5, int var6, Color var7, ImageObserver var8) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) != 0) {
         if (var8 != null) {
            var8.imageUpdate(this.image, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         boolean var9 = (this.availinfo & 32) != 0;
         boolean var10 = (this.availinfo & 128) != 0;
         if (!var9 && !var10) {
            this.addWatcher(var8);
            this.startProduction();
            var9 = (this.availinfo & 32) != 0;
         }

         if (var9 || 0 != (this.availinfo & 16)) {
            var1.drawImage(this.bimage, var3, var4, var5, var6, var7, (ImageObserver)null);
         }

         return var9;
      }
   }

   public boolean drawToBufImage(Graphics var1, ToolkitImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11, ImageObserver var12) {
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) != 0) {
         if (var12 != null) {
            var12.imageUpdate(this.image, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         boolean var13 = (this.availinfo & 32) != 0;
         boolean var14 = (this.availinfo & 128) != 0;
         if (!var13 && !var14) {
            this.addWatcher(var12);
            this.startProduction();
            var13 = (this.availinfo & 32) != 0;
         }

         if (var13 || 0 != (this.availinfo & 16)) {
            var1.drawImage(this.bimage, var3, var4, var5, var6, var7, var8, var9, var10, var11, (ImageObserver)null);
         }

         return var13;
      }
   }

   public boolean drawToBufImage(Graphics var1, ToolkitImage var2, AffineTransform var3, ImageObserver var4) {
      Graphics2D var5 = (Graphics2D)var1;
      if (this.src != null) {
         this.src.checkSecurity((Object)null, false);
      }

      if ((this.availinfo & 64) != 0) {
         if (var4 != null) {
            var4.imageUpdate(this.image, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         boolean var6 = (this.availinfo & 32) != 0;
         boolean var7 = (this.availinfo & 128) != 0;
         if (!var6 && !var7) {
            this.addWatcher(var4);
            this.startProduction();
            var6 = (this.availinfo & 32) != 0;
         }

         if (var6 || 0 != (this.availinfo & 16)) {
            var5.drawImage(this.bimage, var3, (ImageObserver)null);
         }

         return var6;
      }
   }

   synchronized void abort() {
      this.image.getSource().removeConsumer(this);
      this.consuming = false;
      this.newbits = null;
      this.bimage = null;
      this.biRaster = null;
      this.cmodel = null;
      this.srcLUT = null;
      this.isDefaultBI = false;
      this.isSameCM = false;
      this.newInfo(this.image, 128, -1, -1, -1, -1);
      this.availinfo &= -121;
   }

   synchronized void dispose() {
      this.image.getSource().removeConsumer(this);
      this.consuming = false;
      this.newbits = null;
      this.availinfo &= -57;
   }

   public void setAccelerationPriority(float var1) {
      if (this.bimage != null) {
         this.bimage.setAccelerationPriority(var1);
      }

   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
      s_useNative = true;
   }
}
