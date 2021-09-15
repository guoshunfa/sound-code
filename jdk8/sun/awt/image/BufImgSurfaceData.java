package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;

public class BufImgSurfaceData extends SurfaceData {
   BufferedImage bufImg;
   private BufferedImageGraphicsConfig graphicsConfig;
   RenderLoops solidloops;
   private static final int DCM_RGBX_RED_MASK = -16777216;
   private static final int DCM_RGBX_GREEN_MASK = 16711680;
   private static final int DCM_RGBX_BLUE_MASK = 65280;
   private static final int DCM_555X_RED_MASK = 63488;
   private static final int DCM_555X_GREEN_MASK = 1984;
   private static final int DCM_555X_BLUE_MASK = 62;
   private static final int DCM_4444_RED_MASK = 3840;
   private static final int DCM_4444_GREEN_MASK = 240;
   private static final int DCM_4444_BLUE_MASK = 15;
   private static final int DCM_4444_ALPHA_MASK = 61440;
   private static final int DCM_ARGBBM_ALPHA_MASK = 16777216;
   private static final int DCM_ARGBBM_RED_MASK = 16711680;
   private static final int DCM_ARGBBM_GREEN_MASK = 65280;
   private static final int DCM_ARGBBM_BLUE_MASK = 255;
   private static final int CACHE_SIZE = 5;
   private static RenderLoops[] loopcache;
   private static SurfaceType[] typecache;

   private static native void initIDs(Class var0, Class var1);

   public static SurfaceData createData(BufferedImage var0) {
      if (var0 == null) {
         throw new NullPointerException("BufferedImage cannot be null");
      } else {
         ColorModel var2 = var0.getColorModel();
         int var3 = var0.getType();
         Object var1;
         SurfaceType var4;
         switch(var3) {
         case 0:
         default:
            WritableRaster var14 = var0.getRaster();
            int var13 = var14.getNumBands();
            SurfaceType var6;
            int var9;
            int var10;
            int var11;
            if (var14 instanceof IntegerComponentRaster && var14.getNumDataElements() == 1 && ((IntegerComponentRaster)var14).getPixelStride() == 1) {
               var6 = SurfaceType.AnyInt;
               if (var2 instanceof DirectColorModel) {
                  DirectColorModel var15 = (DirectColorModel)var2;
                  int var16 = var15.getAlphaMask();
                  var9 = var15.getRedMask();
                  var10 = var15.getGreenMask();
                  var11 = var15.getBlueMask();
                  if (var13 == 3 && var16 == 0 && var9 == -16777216 && var10 == 16711680 && var11 == 65280) {
                     var6 = SurfaceType.IntRgbx;
                  } else if (var13 == 4 && var16 == 16777216 && var9 == 16711680 && var10 == 65280 && var11 == 255) {
                     var6 = SurfaceType.IntArgbBm;
                  } else {
                     var6 = SurfaceType.AnyDcm;
                  }
               }

               var1 = createDataIC(var0, var6);
            } else if (var14 instanceof ShortComponentRaster && var14.getNumDataElements() == 1 && ((ShortComponentRaster)var14).getPixelStride() == 1) {
               var6 = SurfaceType.AnyShort;
               IndexColorModel var7 = null;
               if (var2 instanceof DirectColorModel) {
                  DirectColorModel var8 = (DirectColorModel)var2;
                  var9 = var8.getAlphaMask();
                  var10 = var8.getRedMask();
                  var11 = var8.getGreenMask();
                  int var12 = var8.getBlueMask();
                  if (var13 == 3 && var9 == 0 && var10 == 63488 && var11 == 1984 && var12 == 62) {
                     var6 = SurfaceType.Ushort555Rgbx;
                  } else if (var13 == 4 && var9 == 61440 && var10 == 3840 && var11 == 240 && var12 == 15) {
                     var6 = SurfaceType.Ushort4444Argb;
                  }
               } else if (var2 instanceof IndexColorModel) {
                  var7 = (IndexColorModel)var2;
                  if (var7.getPixelSize() == 12) {
                     if (isOpaqueGray(var7)) {
                        var6 = SurfaceType.Index12Gray;
                     } else {
                        var6 = SurfaceType.UshortIndexed;
                     }
                  } else {
                     var7 = null;
                  }
               }

               var1 = createDataSC(var0, var6, var7);
            } else {
               var1 = new BufImgSurfaceData(var14.getDataBuffer(), var0, SurfaceType.Custom);
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
         case 12:
            SampleModel var5 = var0.getRaster().getSampleModel();
            switch(var5.getSampleSize(0)) {
            case 1:
               var4 = SurfaceType.ByteBinary1Bit;
               break;
            case 2:
               var4 = SurfaceType.ByteBinary2Bit;
               break;
            case 3:
            default:
               throw new InternalError("Unrecognized pixel size");
            case 4:
               var4 = SurfaceType.ByteBinary4Bit;
            }

            var1 = createDataBP(var0, var4);
            break;
         case 13:
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

         ((BufImgSurfaceData)var1).initSolidLoops();
         return (SurfaceData)var1;
      }
   }

   public static SurfaceData createData(Raster var0, ColorModel var1) {
      throw new InternalError("SurfaceData not implemented for Raster/CM");
   }

   public static SurfaceData createDataIC(BufferedImage var0, SurfaceType var1) {
      IntegerComponentRaster var2 = (IntegerComponentRaster)var0.getRaster();
      BufImgSurfaceData var3 = new BufImgSurfaceData(var2.getDataBuffer(), var0, var1);
      var3.initRaster(var2.getDataStorage(), var2.getDataOffset(0) * 4, 0, var2.getWidth(), var2.getHeight(), var2.getPixelStride() * 4, var2.getScanlineStride() * 4, (IndexColorModel)null);
      return var3;
   }

   public static SurfaceData createDataSC(BufferedImage var0, SurfaceType var1, IndexColorModel var2) {
      ShortComponentRaster var3 = (ShortComponentRaster)var0.getRaster();
      BufImgSurfaceData var4 = new BufImgSurfaceData(var3.getDataBuffer(), var0, var1);
      var4.initRaster(var3.getDataStorage(), var3.getDataOffset(0) * 2, 0, var3.getWidth(), var3.getHeight(), var3.getPixelStride() * 2, var3.getScanlineStride() * 2, var2);
      return var4;
   }

   public static SurfaceData createDataBC(BufferedImage var0, SurfaceType var1, int var2) {
      ByteComponentRaster var3 = (ByteComponentRaster)var0.getRaster();
      BufImgSurfaceData var4 = new BufImgSurfaceData(var3.getDataBuffer(), var0, var1);
      ColorModel var5 = var0.getColorModel();
      IndexColorModel var6 = var5 instanceof IndexColorModel ? (IndexColorModel)var5 : null;
      var4.initRaster(var3.getDataStorage(), var3.getDataOffset(var2), 0, var3.getWidth(), var3.getHeight(), var3.getPixelStride(), var3.getScanlineStride(), var6);
      return var4;
   }

   public static SurfaceData createDataBP(BufferedImage var0, SurfaceType var1) {
      BytePackedRaster var2 = (BytePackedRaster)var0.getRaster();
      BufImgSurfaceData var3 = new BufImgSurfaceData(var2.getDataBuffer(), var0, var1);
      ColorModel var4 = var0.getColorModel();
      IndexColorModel var5 = var4 instanceof IndexColorModel ? (IndexColorModel)var4 : null;
      var3.initRaster(var2.getDataStorage(), var2.getDataBitOffset() / 8, var2.getDataBitOffset() & 7, var2.getWidth(), var2.getHeight(), 0, var2.getScanlineStride(), var5);
      return var3;
   }

   public RenderLoops getRenderLoops(SunGraphics2D var1) {
      return var1.paintState <= 1 && var1.compositeState <= 0 ? this.solidloops : super.getRenderLoops(var1);
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      return this.bufImg.getRaster();
   }

   protected native void initRaster(Object var1, int var2, int var3, int var4, int var5, int var6, int var7, IndexColorModel var8);

   public BufImgSurfaceData(DataBuffer var1, BufferedImage var2, SurfaceType var3) {
      super(SunWritableRaster.stealTrackable(var1), var3, var2.getColorModel());
      this.bufImg = var2;
   }

   protected BufImgSurfaceData(SurfaceType var1, ColorModel var2) {
      super(var1, var2);
   }

   public void initSolidLoops() {
      this.solidloops = getSolidLoops(this.getSurfaceType());
   }

   public static synchronized RenderLoops getSolidLoops(SurfaceType var0) {
      for(int var1 = 4; var1 >= 0; --var1) {
         SurfaceType var2 = typecache[var1];
         if (var2 == var0) {
            return loopcache[var1];
         }

         if (var2 == null) {
            break;
         }
      }

      RenderLoops var3 = makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, var0);
      System.arraycopy(loopcache, 1, loopcache, 0, 4);
      System.arraycopy(typecache, 1, typecache, 0, 4);
      loopcache[4] = var3;
      typecache[4] = var0;
      return var3;
   }

   public SurfaceData getReplacement() {
      return restoreContents(this.bufImg);
   }

   public synchronized GraphicsConfiguration getDeviceConfiguration() {
      if (this.graphicsConfig == null) {
         this.graphicsConfig = BufferedImageGraphicsConfig.getConfig(this.bufImg);
      }

      return this.graphicsConfig;
   }

   public Rectangle getBounds() {
      return new Rectangle(this.bufImg.getWidth(), this.bufImg.getHeight());
   }

   protected void checkCustomComposite() {
   }

   private static native void freeNativeICMData(long var0);

   public Object getDestination() {
      return this.bufImg;
   }

   static {
      initIDs(IndexColorModel.class, BufImgSurfaceData.ICMColorData.class);
      loopcache = new RenderLoops[5];
      typecache = new SurfaceType[5];
   }

   public static final class ICMColorData {
      private long pData = 0L;

      private ICMColorData(long var1) {
         this.pData = var1;
      }

      public void finalize() {
         if (this.pData != 0L) {
            BufImgSurfaceData.freeNativeICMData(this.pData);
            this.pData = 0L;
         }

      }
   }
}
