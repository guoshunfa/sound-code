package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import sun.awt.image.ImagingLib;

public class LookupOp implements BufferedImageOp, RasterOp {
   private LookupTable ltable;
   private int numComponents;
   RenderingHints hints;

   public LookupOp(LookupTable var1, RenderingHints var2) {
      this.ltable = var1;
      this.hints = var2;
      this.numComponents = this.ltable.getNumComponents();
   }

   public final LookupTable getTable() {
      return this.ltable;
   }

   public final BufferedImage filter(BufferedImage var1, BufferedImage var2) {
      ColorModel var3 = var1.getColorModel();
      int var4 = var3.getNumColorComponents();
      if (var3 instanceof IndexColorModel) {
         throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
      } else {
         int var6 = this.ltable.getNumComponents();
         if (var6 != 1 && var6 != var3.getNumComponents() && var6 != var3.getNumColorComponents()) {
            throw new IllegalArgumentException("Number of arrays in the  lookup table (" + var6 + " is not compatible with the  src image: " + var1);
         } else {
            boolean var7 = false;
            int var8 = var1.getWidth();
            int var9 = var1.getHeight();
            ColorModel var5;
            if (var2 == null) {
               var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
               var5 = var3;
            } else {
               if (var8 != var2.getWidth()) {
                  throw new IllegalArgumentException("Src width (" + var8 + ") not equal to dst width (" + var2.getWidth() + ")");
               }

               if (var9 != var2.getHeight()) {
                  throw new IllegalArgumentException("Src height (" + var9 + ") not equal to dst height (" + var2.getHeight() + ")");
               }

               var5 = var2.getColorModel();
               if (var3.getColorSpace().getType() != var5.getColorSpace().getType()) {
                  var7 = true;
                  var2 = this.createCompatibleDestImage(var1, (ColorModel)null);
               }
            }

            if (ImagingLib.filter((BufferedImageOp)this, (BufferedImage)var1, (BufferedImage)var2) == null) {
               WritableRaster var11 = var1.getRaster();
               WritableRaster var12 = var2.getRaster();
               int var13;
               int var14;
               if (var3.hasAlpha() && (var4 - 1 == var6 || var6 == 1)) {
                  var13 = var11.getMinX();
                  var14 = var11.getMinY();
                  int[] var15 = new int[var4 - 1];

                  for(int var16 = 0; var16 < var4 - 1; var15[var16] = var16++) {
                  }

                  var11 = var11.createWritableChild(var13, var14, var11.getWidth(), var11.getHeight(), var13, var14, var15);
               }

               if (var5.hasAlpha()) {
                  var13 = var12.getNumBands();
                  if (var13 - 1 == var6 || var6 == 1) {
                     var14 = var12.getMinX();
                     int var19 = var12.getMinY();
                     int[] var20 = new int[var4 - 1];

                     for(int var17 = 0; var17 < var4 - 1; var20[var17] = var17++) {
                     }

                     var12 = var12.createWritableChild(var14, var19, var12.getWidth(), var12.getHeight(), var14, var19, var20);
                  }
               }

               this.filter((Raster)var11, (WritableRaster)var12);
            }

            if (var7) {
               ColorConvertOp var18 = new ColorConvertOp(this.hints);
               var18.filter(var2, var2);
            }

            return var2;
         }
      }
   }

   public final WritableRaster filter(Raster var1, WritableRaster var2) {
      int var3 = var1.getNumBands();
      int var4 = var2.getNumBands();
      int var5 = var1.getHeight();
      int var6 = var1.getWidth();
      int[] var7 = new int[var3];
      if (var2 == null) {
         var2 = this.createCompatibleDestRaster(var1);
      } else if (var5 != var2.getHeight() || var6 != var2.getWidth()) {
         throw new IllegalArgumentException("Width or height of Rasters do not match");
      }

      var4 = var2.getNumBands();
      if (var3 != var4) {
         throw new IllegalArgumentException("Number of channels in the src (" + var3 + ") does not match number of channels in the destination (" + var4 + ")");
      } else {
         int var8 = this.ltable.getNumComponents();
         if (var8 != 1 && var8 != var1.getNumBands()) {
            throw new IllegalArgumentException("Number of arrays in the  lookup table (" + var8 + " is not compatible with the  src Raster: " + var1);
         } else if (ImagingLib.filter((RasterOp)this, (Raster)var1, (WritableRaster)var2) != null) {
            return var2;
         } else {
            if (this.ltable instanceof ByteLookupTable) {
               this.byteFilter((ByteLookupTable)this.ltable, var1, var2, var6, var5, var3);
            } else if (this.ltable instanceof ShortLookupTable) {
               this.shortFilter((ShortLookupTable)this.ltable, var1, var2, var6, var5, var3);
            } else {
               int var9 = var1.getMinX();
               int var10 = var1.getMinY();
               int var11 = var2.getMinX();
               int var12 = var2.getMinY();

               for(int var13 = 0; var13 < var5; ++var12) {
                  int var14 = var9;
                  int var15 = var11;

                  for(int var16 = 0; var16 < var6; ++var15) {
                     var1.getPixel(var14, var10, var7);
                     this.ltable.lookupPixel(var7, var7);
                     var2.setPixel(var15, var12, var7);
                     ++var16;
                     ++var14;
                  }

                  ++var13;
                  ++var10;
               }
            }

            return var2;
         }
      }
   }

   public final Rectangle2D getBounds2D(BufferedImage var1) {
      return this.getBounds2D((Raster)var1.getRaster());
   }

   public final Rectangle2D getBounds2D(Raster var1) {
      return var1.getBounds();
   }

   public BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2) {
      int var4 = var1.getWidth();
      int var5 = var1.getHeight();
      byte var6 = 0;
      BufferedImage var3;
      if (var2 == null) {
         Object var7 = var1.getColorModel();
         WritableRaster var8 = var1.getRaster();
         if (var7 instanceof ComponentColorModel) {
            DataBuffer var9 = var8.getDataBuffer();
            boolean var10 = ((ColorModel)var7).hasAlpha();
            boolean var11 = ((ColorModel)var7).isAlphaPremultiplied();
            int var12 = ((ColorModel)var7).getTransparency();
            int[] var13 = null;
            if (this.ltable instanceof ByteLookupTable) {
               if (var9.getDataType() == 1) {
                  if (var10) {
                     var13 = new int[2];
                     if (var12 == 2) {
                        var13[1] = 1;
                     } else {
                        var13[1] = 8;
                     }
                  } else {
                     var13 = new int[1];
                  }

                  var13[0] = 8;
               }
            } else if (this.ltable instanceof ShortLookupTable) {
               var6 = 1;
               if (var9.getDataType() == 0) {
                  if (var10) {
                     var13 = new int[2];
                     if (var12 == 2) {
                        var13[1] = 1;
                     } else {
                        var13[1] = 16;
                     }
                  } else {
                     var13 = new int[1];
                  }

                  var13[0] = 16;
               }
            }

            if (var13 != null) {
               var7 = new ComponentColorModel(((ColorModel)var7).getColorSpace(), var13, var10, var11, var12, var6);
            }
         }

         var3 = new BufferedImage((ColorModel)var7, ((ColorModel)var7).createCompatibleWritableRaster(var4, var5), ((ColorModel)var7).isAlphaPremultiplied(), (Hashtable)null);
      } else {
         var3 = new BufferedImage(var2, var2.createCompatibleWritableRaster(var4, var5), var2.isAlphaPremultiplied(), (Hashtable)null);
      }

      return var3;
   }

   public WritableRaster createCompatibleDestRaster(Raster var1) {
      return var1.createCompatibleWritableRaster();
   }

   public final Point2D getPoint2D(Point2D var1, Point2D var2) {
      if (var2 == null) {
         var2 = new Point2D.Float();
      }

      ((Point2D)var2).setLocation(var1.getX(), var1.getY());
      return (Point2D)var2;
   }

   public final RenderingHints getRenderingHints() {
      return this.hints;
   }

   private final void byteFilter(ByteLookupTable var1, Raster var2, WritableRaster var3, int var4, int var5, int var6) {
      int[] var7 = null;
      byte[][] var8 = var1.getTable();
      int var9 = var1.getOffset();
      byte var11 = 1;
      if (var8.length == 1) {
         var11 = 0;
      }

      int var15 = var8[0].length;

      for(int var13 = 0; var13 < var5; ++var13) {
         int var10 = 0;

         for(int var14 = 0; var14 < var6; var10 += var11) {
            var7 = var2.getSamples(0, var13, var4, 1, var14, (int[])var7);

            for(int var12 = 0; var12 < var4; ++var12) {
               int var16 = var7[var12] - var9;
               if (var16 < 0 || var16 > var15) {
                  throw new IllegalArgumentException("index (" + var16 + "(out of range:  srcPix[" + var12 + "]=" + var7[var12] + " offset=" + var9);
               }

               var7[var12] = var8[var10][var16];
            }

            var3.setSamples(0, var13, var4, 1, var14, (int[])var7);
            ++var14;
         }
      }

   }

   private final void shortFilter(ShortLookupTable var1, Raster var2, WritableRaster var3, int var4, int var5, int var6) {
      int[] var8 = null;
      short[][] var9 = var1.getTable();
      int var10 = var1.getOffset();
      byte var12 = 1;
      if (var9.length == 1) {
         var12 = 0;
      }

      boolean var13 = false;
      boolean var14 = false;
      char var16 = '\uffff';

      for(int var18 = 0; var18 < var5; ++var18) {
         int var11 = 0;

         for(int var7 = 0; var7 < var6; var11 += var12) {
            var8 = var2.getSamples(0, var18, var4, 1, var7, (int[])var8);

            for(int var17 = 0; var17 < var4; ++var17) {
               int var15 = var8[var17] - var10;
               if (var15 < 0 || var15 > var16) {
                  throw new IllegalArgumentException("index out of range " + var15 + " x is " + var17 + "srcPix[x]=" + var8[var17] + " offset=" + var10);
               }

               var8[var17] = var9[var11][var15];
            }

            var3.setSamples(0, var18, var4, 1, var7, (int[])var8);
            ++var7;
         }
      }

   }
}
