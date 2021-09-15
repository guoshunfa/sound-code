package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;
import sun.awt.image.SunWritableRaster;
import sun.java2d.loops.SurfaceType;

public class OSXOffScreenSurfaceData extends OSXSurfaceData {
   BufferedImage bim;
   BufferedImage bimBackup;
   static DirectColorModel dcmBackup;
   Object lock;
   WritableRaster bufImgRaster;
   SunWritableRaster bufImgSunRaster;
   private static final int TYPE_3BYTE_RGB = 14;
   protected ByteBuffer fImageInfo;
   IntBuffer fImageInfoInt;
   private static final int kNeedToSyncFromJavaPixelsIndex = 0;
   private static final int kNativePixelsChangedIndex = 1;
   private static final int kImageStolenIndex = 2;
   private static final int kSizeOfParameters = 3;
   BufferedImage copyWithBgColor_cache = null;

   private static native void initIDs();

   public static native SurfaceData getSurfaceData(BufferedImage var0);

   protected static native void setSurfaceData(BufferedImage var0, SurfaceData var1);

   public static SurfaceData createData(BufferedImage var0) {
      synchronized(var0) {
         SurfaceData var2 = getSurfaceData(var0);
         if (var2 != null) {
            return var2;
         } else {
            OSXOffScreenSurfaceData var3 = createNewSurface(var0);
            setSurfaceData(var0, var3);
            var3.cacheRasters(var0);
            return var3;
         }
      }
   }

   public static SurfaceData createData(Raster var0, ColorModel var1) {
      throw new InternalError("SurfaceData not implemented for Raster/CM");
   }

   static OSXOffScreenSurfaceData createNewSurface(BufferedImage var0) {
      Object var1 = null;
      ColorModel var2 = var0.getColorModel();
      int var3 = var0.getType();
      switch(var3) {
      case 0:
      case 12:
      default:
         WritableRaster var37 = var0.getRaster();
         SampleModel var5 = var0.getSampleModel();
         SurfaceType var6 = SurfaceType.Custom;
         int var7 = var2.getTransferType();
         int var8 = var2.getPixelSize();
         int var9 = var2.getNumColorComponents();
         if (var9 == 3 && var2 instanceof ComponentColorModel && var5 instanceof PixelInterleavedSampleModel) {
            int[] var10 = var2.getComponentSize();
            boolean var11 = var10[0] == 8 && var10[1] == 8 && var10[2] == 8;
            int[] var12 = ((ComponentSampleModel)var5).getBandOffsets();
            int var13 = var37.getNumBands();
            boolean var14 = var12[0] == var13 - 3 && var12[1] == var13 - 2 && var12[2] == var13 - 1;
            boolean var15 = var12[0] == var13 - 1 && var12[1] == var13 - 2 && var12[2] == var13 - 3;
            if (var8 == 32 && var7 == 3) {
               if (var11 && var14 && var2.hasAlpha() && var2.isAlphaPremultiplied() && var10[3] == 8) {
                  try {
                     var1 = createDataIC(var0, var6, 3);
                  } catch (ClassCastException var35) {
                     var1 = null;
                  }
               } else if (var11 && var14 && var2.hasAlpha() && var10[3] == 8) {
                  try {
                     var1 = createDataIC(var0, var6, 2);
                  } catch (ClassCastException var34) {
                     var1 = null;
                  }
               } else if (var11 && var15 && var2.hasAlpha() && var2.isAlphaPremultiplied() && var10[3] == 8) {
                  try {
                     var1 = createDataIC(var0, var6, 7);
                  } catch (ClassCastException var33) {
                     var1 = null;
                  }
               } else if (var11 && var15 && var2.hasAlpha() && var10[3] == 8) {
                  try {
                     var1 = createDataIC(var0, var6, 6);
                  } catch (ClassCastException var32) {
                     var1 = null;
                  }
               } else if (var11 && var14) {
                  try {
                     var1 = createDataIC(var0, var6, 1);
                  } catch (ClassCastException var31) {
                     var1 = null;
                  }
               }
            } else if (var8 == 32 && var7 == 0) {
               if (var11 && var14 && var2.hasAlpha() && var2.isAlphaPremultiplied() && var10[3] == 8) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 3);
                  } catch (ClassCastException var30) {
                     var1 = null;
                  }
               }

               if (var11 && var14 && var2.hasAlpha() && var10[3] == 8) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 2);
                  } catch (ClassCastException var29) {
                     var1 = null;
                  }
               } else if (var11 && var15 && var2.hasAlpha() && var2.isAlphaPremultiplied() && var10[3] == 8) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 7);
                  } catch (ClassCastException var28) {
                     var1 = null;
                  }
               } else if (var11 && var15 && var2.hasAlpha() && var10[3] == 8) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 6);
                  } catch (ClassCastException var27) {
                     var1 = null;
                  }
               } else if (var11 && var15) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 4);
                  } catch (ClassCastException var26) {
                     var1 = null;
                  }
               } else if (var11 && var14) {
                  try {
                     var1 = createDataBC(var0, var6, 3, 1);
                  } catch (ClassCastException var25) {
                     var1 = null;
                  }
               }
            } else if (var8 == 24 && var7 == 3) {
               if (var11 && var14) {
                  try {
                     var1 = createDataIC(var0, var6, 1);
                  } catch (ClassCastException var24) {
                     var1 = null;
                  }
               } else if (var11 && var15) {
                  try {
                     var1 = createDataIC(var0, var6, 4);
                  } catch (ClassCastException var23) {
                     var1 = null;
                  }
               }
            } else if (var8 == 24 && var7 == 0) {
               if (var11 && var14) {
                  try {
                     var1 = createDataBC(var0, var6, 0, 14);
                  } catch (ClassCastException var22) {
                     var1 = null;
                  }
               } else if (var11 && var15) {
                  try {
                     var1 = createDataBC(var0, var6, 0, 5);
                  } catch (ClassCastException var21) {
                     var1 = null;
                  }
               }
            } else if (var8 == 16 && var7 == 1) {
               var11 = var10[0] == 5 && var10[1] == 6 && var10[2] == 5;
               if (var11 && var14) {
                  try {
                     var1 = createDataSC(var0, var6, (IndexColorModel)null, 8);
                  } catch (ClassCastException var20) {
                     var1 = null;
                  }
               }
            } else if (var8 == 16 && var7 == 0) {
               var11 = var10[0] == 5 && var10[1] == 6 && var10[2] == 5;
               if (var11 && var14) {
                  try {
                     var1 = createDataBC(var0, var6, 1, 8);
                  } catch (ClassCastException var19) {
                     var1 = null;
                  }
               }
            } else if (var8 == 15 && var7 == 1) {
               var11 = var10[0] == 5 && var10[1] == 5 && var10[2] == 5;
               if (var11 && var14) {
                  try {
                     var1 = createDataSC(var0, var6, (IndexColorModel)null, 9);
                  } catch (ClassCastException var18) {
                     var1 = null;
                  }
               }
            } else if (var8 == 15 && var7 == 0) {
               var11 = var10[0] == 5 && var10[1] == 5 && var10[2] == 5;
               if (var11 && var14) {
                  try {
                     var1 = createDataBC(var0, var6, 1, 9);
                  } catch (ClassCastException var17) {
                     var1 = null;
                  }
               }
            }
         }
         break;
      case 1:
         var1 = createDataIC(var0, SurfaceType.IntRgb);
         break;
      case 2:
         var1 = createDataIC(var0, SurfaceType.IntArgb);
         break;
      case 3:
         var1 = createDataIC(var0, SurfaceType.IntArgbPre);
         break;
      case 4:
         var1 = createDataIC(var0, SurfaceType.IntBgr);
         break;
      case 5:
         var1 = createDataBC(var0, SurfaceType.ThreeByteBgr, 2);
         break;
      case 6:
         var1 = createDataBC(var0, SurfaceType.FourByteAbgr, 3);
         break;
      case 7:
         var1 = createDataBC(var0, SurfaceType.FourByteAbgrPre, 3);
         break;
      case 8:
         var1 = createDataSC(var0, SurfaceType.Ushort565Rgb, (IndexColorModel)null);
         break;
      case 9:
         var1 = createDataSC(var0, SurfaceType.Ushort555Rgb, (IndexColorModel)null);
         break;
      case 10:
         var1 = createDataBC(var0, SurfaceType.ByteGray, 0);
         break;
      case 11:
         var1 = createDataSC(var0, SurfaceType.UshortGray, (IndexColorModel)null);
         break;
      case 13:
         SurfaceType var4;
         switch(var2.getTransparency()) {
         case 1:
            if (isOpaqueGray((IndexColorModel)var2)) {
               var4 = SurfaceType.Index8Gray;
            } else {
               var4 = SurfaceType.ByteIndexedOpaque;
            }
            break;
         case 2:
            var4 = SurfaceType.ByteIndexedBm;
            break;
         case 3:
            var4 = SurfaceType.ByteIndexed;
            break;
         default:
            throw new InternalError("Unrecognized transparency");
         }

         var1 = createDataBC(var0, var4, 0);
      }

      if (var1 == null) {
         var1 = new OSXOffScreenSurfaceData(var0, SurfaceType.Custom);
         OSXOffScreenSurfaceData var38 = (OSXOffScreenSurfaceData)var1;
         IntegerNIORaster var36 = (IntegerNIORaster)IntegerNIORaster.createNIORaster(var0.getWidth(), var0.getHeight(), dcmBackup.getMasks(), (Point)null);
         var38.bimBackup = new BufferedImage(dcmBackup, var36, dcmBackup.isAlphaPremultiplied(), (Hashtable)null);
         var38.initCustomRaster(var36.getBuffer(), var36.getWidth(), var36.getHeight(), var38.fGraphicsStates, var38.fGraphicsStatesObject, var38.fImageInfo);
         var38.fImageInfoInt.put(2, 1);
      }

      return (OSXOffScreenSurfaceData)var1;
   }

   private static SurfaceData createDataIC(BufferedImage var0, SurfaceType var1, int var2) {
      OSXOffScreenSurfaceData var3 = new OSXOffScreenSurfaceData(var0, var1);
      IntegerComponentRaster var4 = (IntegerComponentRaster)var0.getRaster();
      var3.initRaster(var4.getDataStorage(), var4.getDataOffset(0) * 4, var4.getWidth(), var4.getHeight(), var4.getPixelStride() * 4, var4.getScanlineStride() * 4, (IndexColorModel)null, var2, var3.fGraphicsStates, var3.fGraphicsStatesObject, var3.fImageInfo);
      var3.fImageInfoInt.put(2, 1);
      return var3;
   }

   public static SurfaceData createDataIC(BufferedImage var0, SurfaceType var1) {
      return createDataIC(var0, var1, var0.getType());
   }

   private static SurfaceData createDataSC(BufferedImage var0, SurfaceType var1, IndexColorModel var2, int var3) {
      OSXOffScreenSurfaceData var4 = new OSXOffScreenSurfaceData(var0, var1);
      ShortComponentRaster var5 = (ShortComponentRaster)var0.getRaster();
      var4.initRaster(var5.getDataStorage(), var5.getDataOffset(0) * 2, var5.getWidth(), var5.getHeight(), var5.getPixelStride() * 2, var5.getScanlineStride() * 2, var2, var3, var4.fGraphicsStates, var4.fGraphicsStatesObject, var4.fImageInfo);
      var4.fImageInfoInt.put(2, 1);
      return var4;
   }

   public static SurfaceData createDataSC(BufferedImage var0, SurfaceType var1, IndexColorModel var2) {
      return createDataSC(var0, var1, var2, var0.getType());
   }

   private static SurfaceData createDataBC(BufferedImage var0, SurfaceType var1, int var2, int var3) {
      OSXOffScreenSurfaceData var4 = new OSXOffScreenSurfaceData(var0, var1);
      ByteComponentRaster var5 = (ByteComponentRaster)var0.getRaster();
      ColorModel var6 = var0.getColorModel();
      IndexColorModel var7 = var6 instanceof IndexColorModel ? (IndexColorModel)var6 : null;
      var4.initRaster(var5.getDataStorage(), var5.getDataOffset(var2), var5.getWidth(), var5.getHeight(), var5.getPixelStride(), var5.getScanlineStride(), var7, var3, var4.fGraphicsStates, var4.fGraphicsStatesObject, var4.fImageInfo);
      var4.fImageInfoInt.put(2, 1);
      return var4;
   }

   public static SurfaceData createDataBC(BufferedImage var0, SurfaceType var1, int var2) {
      return createDataBC(var0, var1, var2, var0.getType());
   }

   private static SurfaceData createDataBP(BufferedImage var0, SurfaceType var1, int var2) {
      OSXOffScreenSurfaceData var3 = new OSXOffScreenSurfaceData(var0, var1);
      BytePackedRaster var4 = (BytePackedRaster)var0.getRaster();
      ColorModel var5 = var0.getColorModel();
      IndexColorModel var6 = var5 instanceof IndexColorModel ? (IndexColorModel)var5 : null;
      var3.initRaster(var4.getDataStorage(), var4.getDataBitOffset(), var4.getWidth(), var4.getHeight(), var4.getPixelBitStride(), var4.getScanlineStride() * 8, var6, var2, var3.fGraphicsStates, var3.fGraphicsStatesObject, var3.fImageInfo);
      var3.fImageInfoInt.put(2, 1);
      return var3;
   }

   protected native void initRaster(Object var1, int var2, int var3, int var4, int var5, int var6, IndexColorModel var7, int var8, ByteBuffer var9, Object var10, ByteBuffer var11);

   protected native void initCustomRaster(IntBuffer var1, int var2, int var3, ByteBuffer var4, Object var5, ByteBuffer var6);

   public Object getLockObject() {
      return this.lock;
   }

   OSXOffScreenSurfaceData(BufferedImage var1, SurfaceType var2) {
      super(var2, var1.getColorModel());
      this.setBounds(0, 0, var1.getWidth(), var1.getHeight());
      this.bim = var1;
      this.fImageInfo = ByteBuffer.allocateDirect(12);
      this.fImageInfo.order(ByteOrder.nativeOrder());
      this.fImageInfoInt = this.fImageInfo.asIntBuffer();
      this.fImageInfoInt.put(0, 1);
      this.fImageInfoInt.put(1, 0);
      this.fImageInfoInt.put(2, 0);
      this.lock = new Object();
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = 0;
      int var9 = 0;
      if (var1.transformState != 2 && var1.transformState != 1) {
         if (var1.transformState != 0) {
            return false;
         }
      } else {
         var8 = (int)var1.transform.getTranslateX();
         var9 = (int)var1.transform.getTranslateY();
      }

      Shape var10 = var1.getClip();
      var1.setClip(this.getBounds());
      Rectangle var11 = this.clipCopyArea(var1, var2, var3, var4, var5, var6, var7);
      if (var11 == null) {
         return true;
      } else {
         var2 = var11.x - var8;
         var3 = var11.y - var9;
         var4 = var11.width;
         var5 = var11.height;
         var1.drawImage(this.bim, var2 + var6, var3 + var7, var2 + var6 + var4, var3 + var7 + var5, var2 + var8, var3 + var9, var2 + var4 + var8, var3 + var5 + var9, (ImageObserver)null);
         var1.setClip(var10);
         return true;
      }
   }

   public BufferedImage copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, BufferedImage var6) {
      if (var6 == null) {
         var6 = this.getDeviceConfiguration().createCompatibleImage(var4, var5);
      }

      Graphics2D var7 = var6.createGraphics();
      var7.drawImage(this.bim, 0, 0, var4, var5, var2, var3, var2 + var4, var3 + var5, (ImageObserver)null);
      var7.dispose();
      return var6;
   }

   public boolean xorSurfacePixels(SunGraphics2D var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = this.bim.getType();
      return var8 != 3 && var8 != 2 && var8 != 1 ? false : this.xorSurfacePixels(createData(var2), var7, var3, var4, var5, var6);
   }

   native boolean xorSurfacePixels(SurfaceData var1, int var2, int var3, int var4, int var5, int var6);

   public void clearRect(BufferedImage var1, int var2, int var3) {
      OSXOffScreenSurfaceData var4 = (OSXOffScreenSurfaceData)((OSXOffScreenSurfaceData)createData(var1));
      if (!var4.clearSurfacePixels(var2, var3)) {
         Graphics2D var5 = var1.createGraphics();
         var5.setComposite(AlphaComposite.Clear);
         var5.fillRect(0, 0, var2, var3);
         var5.dispose();
      }

   }

   native boolean clearSurfacePixels(int var1, int var2);

   public SurfaceData getCopyWithBgColor(Color var1) {
      int var2 = this.bim.getWidth();
      int var3 = this.bim.getHeight();
      if (this.copyWithBgColor_cache == null || this.copyWithBgColor_cache.getWidth() < var2 || this.copyWithBgColor_cache.getHeight() < var3) {
         GraphicsConfiguration var4 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
         this.copyWithBgColor_cache = var4.createCompatibleImage(var2, var3);
      }

      Graphics2D var5 = this.copyWithBgColor_cache.createGraphics();
      var5.setColor(var1);
      var5.fillRect(0, 0, var2, var3);
      var5.drawImage(this.bim, 0, 0, var2, var3, (ImageObserver)null);
      var5.dispose();
      return getSurfaceData(this.copyWithBgColor_cache);
   }

   public void rasterRead() {
      if (this.fImageInfoInt.get(1) == 1) {
         this.syncToJavaPixels();
      }

   }

   public void rasterWrite() {
      if (this.fImageInfoInt.get(1) == 1) {
         this.syncToJavaPixels();
      }

      this.fImageInfoInt.put(0, 1);
   }

   private void syncFromCustom() {
   }

   private void syncToCustom() {
   }

   private native void syncToJavaPixels();

   void cacheRasters(BufferedImage var1) {
      this.bufImgRaster = var1.getRaster();
      if (this.bufImgRaster instanceof SunWritableRaster) {
         this.bufImgSunRaster = (SunWritableRaster)this.bufImgRaster;
      }

   }

   static {
      initIDs();
      dcmBackup = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
   }
}
