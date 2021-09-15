package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.OffScreenImageSource;
import sun.awt.image.ShortComponentRaster;

public class BufferedImage extends Image implements WritableRenderedImage, Transparency {
   private int imageType = 0;
   private ColorModel colorModel;
   private final WritableRaster raster;
   private OffScreenImageSource osis;
   private Hashtable<String, Object> properties;
   public static final int TYPE_CUSTOM = 0;
   public static final int TYPE_INT_RGB = 1;
   public static final int TYPE_INT_ARGB = 2;
   public static final int TYPE_INT_ARGB_PRE = 3;
   public static final int TYPE_INT_BGR = 4;
   public static final int TYPE_3BYTE_BGR = 5;
   public static final int TYPE_4BYTE_ABGR = 6;
   public static final int TYPE_4BYTE_ABGR_PRE = 7;
   public static final int TYPE_USHORT_565_RGB = 8;
   public static final int TYPE_USHORT_555_RGB = 9;
   public static final int TYPE_BYTE_GRAY = 10;
   public static final int TYPE_USHORT_GRAY = 11;
   public static final int TYPE_BYTE_BINARY = 12;
   public static final int TYPE_BYTE_INDEXED = 13;
   private static final int DCM_RED_MASK = 16711680;
   private static final int DCM_GREEN_MASK = 65280;
   private static final int DCM_BLUE_MASK = 255;
   private static final int DCM_ALPHA_MASK = -16777216;
   private static final int DCM_565_RED_MASK = 63488;
   private static final int DCM_565_GRN_MASK = 2016;
   private static final int DCM_565_BLU_MASK = 31;
   private static final int DCM_555_RED_MASK = 31744;
   private static final int DCM_555_GRN_MASK = 992;
   private static final int DCM_555_BLU_MASK = 31;
   private static final int DCM_BGR_RED_MASK = 255;
   private static final int DCM_BGR_GRN_MASK = 65280;
   private static final int DCM_BGR_BLU_MASK = 16711680;

   private static native void initIDs();

   public BufferedImage(int var1, int var2, int var3) {
      ColorSpace var10;
      int[] var11;
      int[] var12;
      switch(var3) {
      case 1:
         this.colorModel = new DirectColorModel(24, 16711680, 65280, 255, 0);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 2:
         this.colorModel = ColorModel.getRGBdefault();
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 3:
         this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 4:
         this.colorModel = new DirectColorModel(24, 255, 65280, 16711680);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 5:
         var10 = ColorSpace.getInstance(1000);
         var11 = new int[]{8, 8, 8};
         var12 = new int[]{2, 1, 0};
         this.colorModel = new ComponentColorModel(var10, var11, false, false, 1, 0);
         this.raster = Raster.createInterleavedRaster(0, var1, var2, var1 * 3, 3, var12, (Point)null);
         break;
      case 6:
         var10 = ColorSpace.getInstance(1000);
         var11 = new int[]{8, 8, 8, 8};
         var12 = new int[]{3, 2, 1, 0};
         this.colorModel = new ComponentColorModel(var10, var11, true, false, 3, 0);
         this.raster = Raster.createInterleavedRaster(0, var1, var2, var1 * 4, 4, var12, (Point)null);
         break;
      case 7:
         var10 = ColorSpace.getInstance(1000);
         var11 = new int[]{8, 8, 8, 8};
         var12 = new int[]{3, 2, 1, 0};
         this.colorModel = new ComponentColorModel(var10, var11, true, true, 3, 0);
         this.raster = Raster.createInterleavedRaster(0, var1, var2, var1 * 4, 4, var12, (Point)null);
         break;
      case 8:
         this.colorModel = new DirectColorModel(16, 63488, 2016, 31);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 9:
         this.colorModel = new DirectColorModel(15, 31744, 992, 31);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 10:
         var10 = ColorSpace.getInstance(1003);
         var11 = new int[]{8};
         this.colorModel = new ComponentColorModel(var10, var11, false, true, 1, 0);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 11:
         var10 = ColorSpace.getInstance(1003);
         var11 = new int[]{16};
         this.colorModel = new ComponentColorModel(var10, var11, false, true, 1, 1);
         this.raster = this.colorModel.createCompatibleWritableRaster(var1, var2);
         break;
      case 12:
         byte[] var9 = new byte[]{0, -1};
         this.colorModel = new IndexColorModel(1, 2, var9, var9, var9);
         this.raster = Raster.createPackedRaster(0, var1, var2, 1, 1, (Point)null);
         break;
      case 13:
         int[] var4 = new int[256];
         int var5 = 0;

         int var6;
         int var7;
         for(var6 = 0; var6 < 256; var6 += 51) {
            for(var7 = 0; var7 < 256; var7 += 51) {
               for(int var8 = 0; var8 < 256; var8 += 51) {
                  var4[var5++] = var6 << 16 | var7 << 8 | var8;
               }
            }
         }

         var6 = 256 / (256 - var5);

         for(var7 = var6 * 3; var5 < 256; ++var5) {
            var4[var5] = var7 << 16 | var7 << 8 | var7;
            var7 += var6;
         }

         this.colorModel = new IndexColorModel(8, 256, var4, 0, false, -1, 0);
         this.raster = Raster.createInterleavedRaster(0, var1, var2, 1, (Point)null);
         break;
      default:
         throw new IllegalArgumentException("Unknown image type " + var3);
      }

      this.imageType = var3;
   }

   public BufferedImage(int var1, int var2, int var3, IndexColorModel var4) {
      if (var4.hasAlpha() && var4.isAlphaPremultiplied()) {
         throw new IllegalArgumentException("This image types do not have premultiplied alpha.");
      } else {
         switch(var3) {
         case 12:
            int var6 = var4.getMapSize();
            byte var5;
            if (var6 <= 2) {
               var5 = 1;
            } else if (var6 <= 4) {
               var5 = 2;
            } else {
               if (var6 > 16) {
                  throw new IllegalArgumentException("Color map for TYPE_BYTE_BINARY must have no more than 16 entries");
               }

               var5 = 4;
            }

            this.raster = Raster.createPackedRaster(0, var1, var2, 1, var5, (Point)null);
            break;
         case 13:
            this.raster = Raster.createInterleavedRaster(0, var1, var2, 1, (Point)null);
            break;
         default:
            throw new IllegalArgumentException("Invalid image type (" + var3 + ").  Image type must be either TYPE_BYTE_BINARY or  TYPE_BYTE_INDEXED");
         }

         if (!var4.isCompatibleRaster(this.raster)) {
            throw new IllegalArgumentException("Incompatible image type and IndexColorModel");
         } else {
            this.colorModel = var4;
            this.imageType = var3;
         }
      }
   }

   public BufferedImage(ColorModel var1, WritableRaster var2, boolean var3, Hashtable<?, ?> var4) {
      if (!var1.isCompatibleRaster(var2)) {
         throw new IllegalArgumentException("Raster " + var2 + " is incompatible with ColorModel " + var1);
      } else if (var2.minX == 0 && var2.minY == 0) {
         this.colorModel = var1;
         this.raster = var2;
         if (var4 != null && !var4.isEmpty()) {
            this.properties = new Hashtable();
            Iterator var5 = var4.keySet().iterator();

            while(var5.hasNext()) {
               Object var6 = var5.next();
               if (var6 instanceof String) {
                  this.properties.put((String)var6, var4.get(var6));
               }
            }
         }

         int var18 = var2.getNumBands();
         boolean var19 = var1.isAlphaPremultiplied();
         boolean var7 = isStandard(var1, var2);
         this.coerceData(var3);
         SampleModel var9 = var2.getSampleModel();
         ColorSpace var8 = var1.getColorSpace();
         int var10 = var8.getType();
         if (var10 != 5) {
            if (var10 == 6 && var7 && var1 instanceof ComponentColorModel) {
               if (var9 instanceof ComponentSampleModel && ((ComponentSampleModel)var9).getPixelStride() != var18) {
                  this.imageType = 0;
               } else if (var2 instanceof ByteComponentRaster && var2.getNumBands() == 1 && var1.getComponentSize(0) == 8 && ((ByteComponentRaster)var2).getPixelStride() == 1) {
                  this.imageType = 10;
               } else if (var2 instanceof ShortComponentRaster && var2.getNumBands() == 1 && var1.getComponentSize(0) == 16 && ((ShortComponentRaster)var2).getPixelStride() == 1) {
                  this.imageType = 11;
               }
            } else {
               this.imageType = 0;
            }

         } else {
            int var23;
            if (var2 instanceof IntegerComponentRaster && (var18 == 3 || var18 == 4)) {
               IntegerComponentRaster var22 = (IntegerComponentRaster)var2;
               var23 = var1.getPixelSize();
               if (var22.getPixelStride() == 1 && var7 && var1 instanceof DirectColorModel && (var23 == 32 || var23 == 24)) {
                  DirectColorModel var24 = (DirectColorModel)var1;
                  int var25 = var24.getRedMask();
                  int var26 = var24.getGreenMask();
                  int var27 = var24.getBlueMask();
                  if (var25 == 16711680 && var26 == 65280 && var27 == 255) {
                     if (var24.getAlphaMask() == -16777216) {
                        this.imageType = var19 ? 3 : 2;
                     } else if (!var24.hasAlpha()) {
                        this.imageType = 1;
                     }
                  } else if (var25 == 255 && var26 == 65280 && var27 == 16711680 && !var24.hasAlpha()) {
                     this.imageType = 4;
                  }
               }
            } else {
               ByteComponentRaster var13;
               if (var1 instanceof IndexColorModel && var18 == 1 && var7 && (!var1.hasAlpha() || !var19)) {
                  IndexColorModel var21 = (IndexColorModel)var1;
                  var23 = var21.getPixelSize();
                  if (var2 instanceof BytePackedRaster) {
                     this.imageType = 12;
                  } else if (var2 instanceof ByteComponentRaster) {
                     var13 = (ByteComponentRaster)var2;
                     if (var13.getPixelStride() == 1 && var23 <= 8) {
                        this.imageType = 13;
                     }
                  }
               } else if (var2 instanceof ShortComponentRaster && var1 instanceof DirectColorModel && var7 && var18 == 3 && !var1.hasAlpha()) {
                  DirectColorModel var20 = (DirectColorModel)var1;
                  if (var20.getRedMask() == 63488) {
                     if (var20.getGreenMask() == 2016 && var20.getBlueMask() == 31) {
                        this.imageType = 8;
                     }
                  } else if (var20.getRedMask() == 31744 && var20.getGreenMask() == 992 && var20.getBlueMask() == 31) {
                     this.imageType = 9;
                  }
               } else if (var2 instanceof ByteComponentRaster && var1 instanceof ComponentColorModel && var7 && var2.getSampleModel() instanceof PixelInterleavedSampleModel && (var18 == 3 || var18 == 4)) {
                  ComponentColorModel var11 = (ComponentColorModel)var1;
                  PixelInterleavedSampleModel var12 = (PixelInterleavedSampleModel)var2.getSampleModel();
                  var13 = (ByteComponentRaster)var2;
                  int[] var14 = var12.getBandOffsets();
                  if (var11.getNumComponents() != var18) {
                     throw new RasterFormatException("Number of components in ColorModel (" + var11.getNumComponents() + ") does not match # in  Raster (" + var18 + ")");
                  }

                  int[] var15 = var11.getComponentSize();
                  boolean var16 = true;

                  for(int var17 = 0; var17 < var18; ++var17) {
                     if (var15[var17] != 8) {
                        var16 = false;
                        break;
                     }
                  }

                  if (var16 && var13.getPixelStride() == var18 && var14[0] == var18 - 1 && var14[1] == var18 - 2 && var14[2] == var18 - 3) {
                     if (var18 == 3 && !var11.hasAlpha()) {
                        this.imageType = 5;
                     } else if (var14[3] == 0 && var11.hasAlpha()) {
                        this.imageType = var19 ? 7 : 6;
                     }
                  }
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Raster " + var2 + " has minX or minY not equal to zero: " + var2.minX + " " + var2.minY);
      }
   }

   private static boolean isStandard(ColorModel var0, WritableRaster var1) {
      final Class var2 = var0.getClass();
      final Class var3 = var1.getClass();
      final Class var4 = var1.getSampleModel().getClass();
      PrivilegedAction var5 = new PrivilegedAction<Boolean>() {
         public Boolean run() {
            ClassLoader var1 = System.class.getClassLoader();
            return var2.getClassLoader() == var1 && var4.getClassLoader() == var1 && var3.getClassLoader() == var1;
         }
      };
      return (Boolean)AccessController.doPrivileged(var5);
   }

   public int getType() {
      return this.imageType;
   }

   public ColorModel getColorModel() {
      return this.colorModel;
   }

   public WritableRaster getRaster() {
      return this.raster;
   }

   public WritableRaster getAlphaRaster() {
      return this.colorModel.getAlphaRaster(this.raster);
   }

   public int getRGB(int var1, int var2) {
      return this.colorModel.getRGB(this.raster.getDataElements(var1, var2, (Object)null));
   }

   public int[] getRGB(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      int var8 = var6;
      int var11 = this.raster.getNumBands();
      int var12 = this.raster.getDataBuffer().getDataType();
      Object var10;
      switch(var12) {
      case 0:
         var10 = new byte[var11];
         break;
      case 1:
         var10 = new short[var11];
         break;
      case 2:
      default:
         throw new IllegalArgumentException("Unknown data buffer type: " + var12);
      case 3:
         var10 = new int[var11];
         break;
      case 4:
         var10 = new float[var11];
         break;
      case 5:
         var10 = new double[var11];
      }

      if (var5 == null) {
         var5 = new int[var6 + var4 * var7];
      }

      for(int var13 = var2; var13 < var2 + var4; var8 += var7) {
         int var9 = var8;

         for(int var14 = var1; var14 < var1 + var3; ++var14) {
            var5[var9++] = this.colorModel.getRGB(this.raster.getDataElements(var14, var13, var10));
         }

         ++var13;
      }

      return var5;
   }

   public synchronized void setRGB(int var1, int var2, int var3) {
      this.raster.setDataElements(var1, var2, this.colorModel.getDataElements(var3, (Object)null));
   }

   public void setRGB(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      int var8 = var6;
      Object var10 = null;

      for(int var11 = var2; var11 < var2 + var4; var8 += var7) {
         int var9 = var8;

         for(int var12 = var1; var12 < var1 + var3; ++var12) {
            var10 = this.colorModel.getDataElements(var5[var9++], var10);
            this.raster.setDataElements(var12, var11, var10);
         }

         ++var11;
      }

   }

   public int getWidth() {
      return this.raster.getWidth();
   }

   public int getHeight() {
      return this.raster.getHeight();
   }

   public int getWidth(ImageObserver var1) {
      return this.raster.getWidth();
   }

   public int getHeight(ImageObserver var1) {
      return this.raster.getHeight();
   }

   public ImageProducer getSource() {
      if (this.osis == null) {
         if (this.properties == null) {
            this.properties = new Hashtable();
         }

         this.osis = new OffScreenImageSource(this, this.properties);
      }

      return this.osis;
   }

   public Object getProperty(String var1, ImageObserver var2) {
      return this.getProperty(var1);
   }

   public Object getProperty(String var1) {
      if (var1 == null) {
         throw new NullPointerException("null property name is not allowed");
      } else if (this.properties == null) {
         return Image.UndefinedProperty;
      } else {
         Object var2 = this.properties.get(var1);
         if (var2 == null) {
            var2 = Image.UndefinedProperty;
         }

         return var2;
      }
   }

   public Graphics getGraphics() {
      return this.createGraphics();
   }

   public Graphics2D createGraphics() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return var1.createGraphics(this);
   }

   public BufferedImage getSubimage(int var1, int var2, int var3, int var4) {
      return new BufferedImage(this.colorModel, this.raster.createWritableChild(var1, var2, var3, var4, 0, 0, (int[])null), this.colorModel.isAlphaPremultiplied(), this.properties);
   }

   public boolean isAlphaPremultiplied() {
      return this.colorModel.isAlphaPremultiplied();
   }

   public void coerceData(boolean var1) {
      if (this.colorModel.hasAlpha() && this.colorModel.isAlphaPremultiplied() != var1) {
         this.colorModel = this.colorModel.coerceData(this.raster, var1);
      }

   }

   public String toString() {
      return "BufferedImage@" + Integer.toHexString(this.hashCode()) + ": type = " + this.imageType + " " + this.colorModel + " " + this.raster;
   }

   public Vector<RenderedImage> getSources() {
      return null;
   }

   public String[] getPropertyNames() {
      if (this.properties != null && !this.properties.isEmpty()) {
         Set var1 = this.properties.keySet();
         return (String[])var1.toArray(new String[var1.size()]);
      } else {
         return null;
      }
   }

   public int getMinX() {
      return this.raster.getMinX();
   }

   public int getMinY() {
      return this.raster.getMinY();
   }

   public SampleModel getSampleModel() {
      return this.raster.getSampleModel();
   }

   public int getNumXTiles() {
      return 1;
   }

   public int getNumYTiles() {
      return 1;
   }

   public int getMinTileX() {
      return 0;
   }

   public int getMinTileY() {
      return 0;
   }

   public int getTileWidth() {
      return this.raster.getWidth();
   }

   public int getTileHeight() {
      return this.raster.getHeight();
   }

   public int getTileGridXOffset() {
      return this.raster.getSampleModelTranslateX();
   }

   public int getTileGridYOffset() {
      return this.raster.getSampleModelTranslateY();
   }

   public Raster getTile(int var1, int var2) {
      if (var1 == 0 && var2 == 0) {
         return this.raster;
      } else {
         throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
      }
   }

   public Raster getData() {
      int var1 = this.raster.getWidth();
      int var2 = this.raster.getHeight();
      int var3 = this.raster.getMinX();
      int var4 = this.raster.getMinY();
      WritableRaster var5 = Raster.createWritableRaster(this.raster.getSampleModel(), new Point(this.raster.getSampleModelTranslateX(), this.raster.getSampleModelTranslateY()));
      Object var6 = null;

      for(int var7 = var4; var7 < var4 + var2; ++var7) {
         var6 = this.raster.getDataElements(var3, var7, var1, 1, var6);
         var5.setDataElements(var3, var7, var1, 1, var6);
      }

      return var5;
   }

   public Raster getData(Rectangle var1) {
      SampleModel var2 = this.raster.getSampleModel();
      SampleModel var3 = var2.createCompatibleSampleModel(var1.width, var1.height);
      WritableRaster var4 = Raster.createWritableRaster(var3, var1.getLocation());
      int var5 = var1.width;
      int var6 = var1.height;
      int var7 = var1.x;
      int var8 = var1.y;
      Object var9 = null;

      for(int var10 = var8; var10 < var8 + var6; ++var10) {
         var9 = this.raster.getDataElements(var7, var10, var5, 1, var9);
         var4.setDataElements(var7, var10, var5, 1, var9);
      }

      return var4;
   }

   public WritableRaster copyData(WritableRaster var1) {
      if (var1 == null) {
         return (WritableRaster)this.getData();
      } else {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         int var4 = var1.getMinX();
         int var5 = var1.getMinY();
         Object var6 = null;

         for(int var7 = var5; var7 < var5 + var3; ++var7) {
            var6 = this.raster.getDataElements(var4, var7, var2, 1, var6);
            var1.setDataElements(var4, var7, var2, 1, var6);
         }

         return var1;
      }
   }

   public void setData(Raster var1) {
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      int var4 = var1.getMinX();
      int var5 = var1.getMinY();
      int[] var6 = null;
      Rectangle var7 = new Rectangle(var4, var5, var2, var3);
      Rectangle var8 = new Rectangle(0, 0, this.raster.width, this.raster.height);
      Rectangle var9 = var7.intersection(var8);
      if (!var9.isEmpty()) {
         var2 = var9.width;
         var3 = var9.height;
         var4 = var9.x;
         var5 = var9.y;

         for(int var10 = var5; var10 < var5 + var3; ++var10) {
            var6 = var1.getPixels(var4, var10, var2, 1, (int[])var6);
            this.raster.setPixels(var4, var10, var2, 1, (int[])var6);
         }

      }
   }

   public void addTileObserver(TileObserver var1) {
   }

   public void removeTileObserver(TileObserver var1) {
   }

   public boolean isTileWritable(int var1, int var2) {
      if (var1 == 0 && var2 == 0) {
         return true;
      } else {
         throw new IllegalArgumentException("Only 1 tile in image");
      }
   }

   public Point[] getWritableTileIndices() {
      Point[] var1 = new Point[]{new Point(0, 0)};
      return var1;
   }

   public boolean hasTileWriters() {
      return true;
   }

   public WritableRaster getWritableTile(int var1, int var2) {
      return this.raster;
   }

   public void releaseWritableTile(int var1, int var2) {
   }

   public int getTransparency() {
      return this.colorModel.getTransparency();
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
