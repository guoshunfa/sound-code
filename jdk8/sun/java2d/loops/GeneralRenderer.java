package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public final class GeneralRenderer {
   static final int OUTCODE_TOP = 1;
   static final int OUTCODE_BOTTOM = 2;
   static final int OUTCODE_LEFT = 4;
   static final int OUTCODE_RIGHT = 8;

   public static void register() {
      Class var0 = GeneralRenderer.class;
      GraphicsPrimitive[] var1 = new GraphicsPrimitive[]{new GraphicsPrimitiveProxy(var0, "SetFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "SetDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawGlyphListANY", DrawGlyphList.methodSignature, DrawGlyphList.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorDrawGlyphListAAANY", DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any)};
      GraphicsPrimitiveMgr.register(var1);
   }

   static void doDrawPoly(SurfaceData var0, PixelWriter var1, int[] var2, int[] var3, int var4, int var5, Region var6, int var7, int var8, boolean var9) {
      int[] var14 = null;
      if (var5 > 0) {
         int var12;
         int var10 = var12 = var2[var4] + var7;
         int var13;
         int var11 = var13 = var3[var4] + var8;

         while(true) {
            --var5;
            if (var5 <= 0) {
               if (var9 && (var12 != var10 || var13 != var11)) {
                  doDrawLine(var0, var1, var14, var6, var12, var13, var10, var11);
               }

               return;
            }

            ++var4;
            int var15 = var2[var4] + var7;
            int var16 = var3[var4] + var8;
            var14 = doDrawLine(var0, var1, var14, var6, var12, var13, var15, var16);
            var12 = var15;
            var13 = var16;
         }
      }
   }

   static void doSetRect(SurfaceData var0, PixelWriter var1, int var2, int var3, int var4, int var5) {
      WritableRaster var6 = (WritableRaster)var0.getRaster(var2, var3, var4 - var2, var5 - var3);
      var1.setRaster(var6);

      while(var3 < var5) {
         for(int var7 = var2; var7 < var4; ++var7) {
            var1.writePixel(var7, var3);
         }

         ++var3;
      }

   }

   static int[] doDrawLine(SurfaceData var0, PixelWriter var1, int[] var2, Region var3, int var4, int var5, int var6, int var7) {
      if (var2 == null) {
         var2 = new int[8];
      }

      var2[0] = var4;
      var2[1] = var5;
      var2[2] = var6;
      var2[3] = var7;
      if (!adjustLine(var2, var3.getLoX(), var3.getLoY(), var3.getHiX(), var3.getHiY())) {
         return var2;
      } else {
         int var8 = var2[0];
         int var9 = var2[1];
         int var10 = var2[2];
         int var11 = var2[3];
         WritableRaster var12 = (WritableRaster)var0.getRaster(Math.min(var8, var10), Math.min(var9, var11), Math.abs(var8 - var10) + 1, Math.abs(var9 - var11) + 1);
         var1.setRaster(var12);
         if (var8 == var10) {
            if (var9 > var11) {
               do {
                  var1.writePixel(var8, var9);
                  --var9;
               } while(var9 >= var11);
            } else {
               do {
                  var1.writePixel(var8, var9);
                  ++var9;
               } while(var9 <= var11);
            }
         } else if (var9 == var11) {
            if (var8 > var10) {
               do {
                  var1.writePixel(var8, var9);
                  --var8;
               } while(var8 >= var10);
            } else {
               do {
                  var1.writePixel(var8, var9);
                  ++var8;
               } while(var8 <= var10);
            }
         } else {
            int var13 = var2[4];
            int var14 = var2[5];
            int var15 = var2[6];
            int var16 = var2[7];
            int var17;
            int var18;
            int var19;
            int var20;
            int var21;
            boolean var23;
            if (var15 >= var16) {
               var23 = true;
               var21 = var16 * 2;
               var20 = var15 * 2;
               var18 = var13 < 0 ? -1 : 1;
               var19 = var14 < 0 ? -1 : 1;
               var15 = -var15;
               var17 = var10 - var8;
            } else {
               var23 = false;
               var21 = var15 * 2;
               var20 = var16 * 2;
               var18 = var14 < 0 ? -1 : 1;
               var19 = var13 < 0 ? -1 : 1;
               var16 = -var16;
               var17 = var11 - var9;
            }

            int var22 = -(var20 / 2);
            int var24;
            if (var9 != var5) {
               var24 = var9 - var5;
               if (var24 < 0) {
                  var24 = -var24;
               }

               var22 += var24 * var15 * 2;
            }

            if (var8 != var4) {
               var24 = var8 - var4;
               if (var24 < 0) {
                  var24 = -var24;
               }

               var22 += var24 * var16 * 2;
            }

            if (var17 < 0) {
               var17 = -var17;
            }

            if (var23) {
               do {
                  var1.writePixel(var8, var9);
                  var8 += var18;
                  var22 += var21;
                  if (var22 >= 0) {
                     var9 += var19;
                     var22 -= var20;
                  }

                  --var17;
               } while(var17 >= 0);
            } else {
               do {
                  var1.writePixel(var8, var9);
                  var9 += var18;
                  var22 += var21;
                  if (var22 >= 0) {
                     var8 += var19;
                     var22 -= var20;
                  }

                  --var17;
               } while(var17 >= 0);
            }
         }

         return var2;
      }
   }

   public static void doDrawRect(PixelWriter var0, SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      if (var5 >= 0 && var6 >= 0) {
         int var7 = Region.dimAdd(Region.dimAdd(var3, var5), 1);
         int var8 = Region.dimAdd(Region.dimAdd(var4, var6), 1);
         Region var9 = var1.getCompClip().getBoundsIntersectionXYXY(var3, var4, var7, var8);
         if (!var9.isEmpty()) {
            int var10 = var9.getLoX();
            int var11 = var9.getLoY();
            int var12 = var9.getHiX();
            int var13 = var9.getHiY();
            if (var5 >= 2 && var6 >= 2) {
               if (var11 == var4) {
                  doSetRect(var2, var0, var10, var11, var12, var11 + 1);
               }

               if (var10 == var3) {
                  doSetRect(var2, var0, var10, var11 + 1, var10 + 1, var13 - 1);
               }

               if (var12 == var7) {
                  doSetRect(var2, var0, var12 - 1, var11 + 1, var12, var13 - 1);
               }

               if (var13 == var8) {
                  doSetRect(var2, var0, var10, var13 - 1, var12, var13);
               }

            } else {
               doSetRect(var2, var0, var10, var11, var12, var13);
            }
         }
      }
   }

   static void doDrawGlyphList(SurfaceData var0, PixelWriter var1, GlyphList var2, Region var3) {
      int[] var4 = var2.getBounds();
      var3.clipBoxToBounds(var4);
      int var5 = var4[0];
      int var6 = var4[1];
      int var7 = var4[2];
      int var8 = var4[3];
      WritableRaster var9 = (WritableRaster)var0.getRaster(var5, var6, var7 - var5, var8 - var6);
      var1.setRaster(var9);
      int var10 = var2.getNumGlyphs();

      for(int var11 = 0; var11 < var10; ++var11) {
         var2.setGlyphIndex(var11);
         int[] var12 = var2.getMetrics();
         int var13 = var12[0];
         int var14 = var12[1];
         int var15 = var12[2];
         int var16 = var13 + var15;
         int var17 = var14 + var12[3];
         int var18 = 0;
         if (var13 < var5) {
            var18 = var5 - var13;
            var13 = var5;
         }

         if (var14 < var6) {
            var18 += (var6 - var14) * var15;
            var14 = var6;
         }

         if (var16 > var7) {
            var16 = var7;
         }

         if (var17 > var8) {
            var17 = var8;
         }

         if (var16 > var13 && var17 > var14) {
            byte[] var19 = var2.getGrayBits();
            var15 -= var16 - var13;

            for(int var20 = var14; var20 < var17; ++var20) {
               for(int var21 = var13; var21 < var16; ++var21) {
                  if (var19[var18++] < 0) {
                     var1.writePixel(var21, var20);
                  }
               }

               var18 += var15;
            }
         }
      }

   }

   static int outcode(int var0, int var1, int var2, int var3, int var4, int var5) {
      int var6;
      if (var1 < var3) {
         var6 = 1;
      } else if (var1 > var5) {
         var6 = 2;
      } else {
         var6 = 0;
      }

      if (var0 < var2) {
         var6 |= 4;
      } else if (var0 > var4) {
         var6 |= 8;
      }

      return var6;
   }

   public static boolean adjustLine(int[] var0, int var1, int var2, int var3, int var4) {
      int var5 = var3 - 1;
      int var6 = var4 - 1;
      int var7 = var0[0];
      int var8 = var0[1];
      int var9 = var0[2];
      int var10 = var0[3];
      if (var5 >= var1 && var6 >= var2) {
         int var11;
         if (var7 == var9) {
            if (var7 < var1 || var7 > var5) {
               return false;
            }

            if (var8 > var10) {
               var11 = var8;
               var8 = var10;
               var10 = var11;
            }

            if (var8 < var2) {
               var8 = var2;
            }

            if (var10 > var6) {
               var10 = var6;
            }

            if (var8 > var10) {
               return false;
            }

            var0[1] = var8;
            var0[3] = var10;
         } else if (var8 == var10) {
            if (var8 < var2 || var8 > var6) {
               return false;
            }

            if (var7 > var9) {
               var11 = var7;
               var7 = var9;
               var9 = var11;
            }

            if (var7 < var1) {
               var7 = var1;
            }

            if (var9 > var5) {
               var9 = var5;
            }

            if (var7 > var9) {
               return false;
            }

            var0[0] = var7;
            var0[2] = var9;
         } else {
            int var13 = var9 - var7;
            int var14 = var10 - var8;
            int var15 = var13 < 0 ? -var13 : var13;
            int var16 = var14 < 0 ? -var14 : var14;
            boolean var17 = var15 >= var16;
            var11 = outcode(var7, var8, var1, var2, var5, var6);
            int var12 = outcode(var9, var10, var1, var2, var5, var6);

            while((var11 | var12) != 0) {
               if ((var11 & var12) != 0) {
                  return false;
               }

               int var18;
               int var19;
               if (var11 != 0) {
                  if (0 != (var11 & 3)) {
                     if (0 != (var11 & 1)) {
                        var8 = var2;
                     } else {
                        var8 = var6;
                     }

                     var19 = var8 - var0[1];
                     if (var19 < 0) {
                        var19 = -var19;
                     }

                     var18 = 2 * var19 * var15 + var16;
                     if (var17) {
                        var18 += var16 - var15 - 1;
                     }

                     var18 /= 2 * var16;
                     if (var13 < 0) {
                        var18 = -var18;
                     }

                     var7 = var0[0] + var18;
                  } else if (0 != (var11 & 12)) {
                     if (0 != (var11 & 4)) {
                        var7 = var1;
                     } else {
                        var7 = var5;
                     }

                     var18 = var7 - var0[0];
                     if (var18 < 0) {
                        var18 = -var18;
                     }

                     var19 = 2 * var18 * var16 + var15;
                     if (!var17) {
                        var19 += var15 - var16 - 1;
                     }

                     var19 /= 2 * var15;
                     if (var14 < 0) {
                        var19 = -var19;
                     }

                     var8 = var0[1] + var19;
                  }

                  var11 = outcode(var7, var8, var1, var2, var5, var6);
               } else {
                  if (0 != (var12 & 3)) {
                     if (0 != (var12 & 1)) {
                        var10 = var2;
                     } else {
                        var10 = var6;
                     }

                     var19 = var10 - var0[3];
                     if (var19 < 0) {
                        var19 = -var19;
                     }

                     var18 = 2 * var19 * var15 + var16;
                     if (var17) {
                        var18 += var16 - var15;
                     } else {
                        --var18;
                     }

                     var18 /= 2 * var16;
                     if (var13 > 0) {
                        var18 = -var18;
                     }

                     var9 = var0[2] + var18;
                  } else if (0 != (var12 & 12)) {
                     if (0 != (var12 & 4)) {
                        var9 = var1;
                     } else {
                        var9 = var5;
                     }

                     var18 = var9 - var0[2];
                     if (var18 < 0) {
                        var18 = -var18;
                     }

                     var19 = 2 * var18 * var16 + var15;
                     if (var17) {
                        --var19;
                     } else {
                        var19 += var15 - var16;
                     }

                     var19 /= 2 * var15;
                     if (var14 > 0) {
                        var19 = -var19;
                     }

                     var10 = var0[3] + var19;
                  }

                  var12 = outcode(var9, var10, var1, var2, var5, var6);
               }
            }

            var0[0] = var7;
            var0[1] = var8;
            var0[2] = var9;
            var0[3] = var10;
            var0[4] = var13;
            var0[5] = var14;
            var0[6] = var15;
            var0[7] = var16;
         }

         return true;
      } else {
         return false;
      }
   }

   static PixelWriter createSolidPixelWriter(SunGraphics2D var0, SurfaceData var1) {
      ColorModel var2 = var1.getColorModel();
      Object var3 = var2.getDataElements(var0.eargb, (Object)null);
      return new SolidPixelWriter(var3);
   }

   static PixelWriter createXorPixelWriter(SunGraphics2D var0, SurfaceData var1) {
      ColorModel var2 = var1.getColorModel();
      Object var3 = var2.getDataElements(var0.eargb, (Object)null);
      XORComposite var4 = (XORComposite)var0.getComposite();
      int var5 = var4.getXorColor().getRGB();
      Object var6 = var2.getDataElements(var5, (Object)null);
      switch(var2.getTransferType()) {
      case 0:
         return new XorPixelWriter.ByteData(var3, var6);
      case 1:
      case 2:
         return new XorPixelWriter.ShortData(var3, var6);
      case 3:
         return new XorPixelWriter.IntData(var3, var6);
      case 4:
         return new XorPixelWriter.FloatData(var3, var6);
      case 5:
         return new XorPixelWriter.DoubleData(var3, var6);
      default:
         throw new InternalError("Unsupported XOR pixel type");
      }
   }
}
