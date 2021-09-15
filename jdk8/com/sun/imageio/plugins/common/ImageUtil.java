package com.sun.imageio.plugins.common;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

public class ImageUtil {
   public static final ColorModel createColorModel(SampleModel var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("sampleModel == null!");
      } else {
         int var1 = var0.getDataType();
         switch(var1) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            Object var2 = null;
            int[] var3 = var0.getSampleSize();
            int var4;
            boolean var6;
            boolean var7;
            int var8;
            if (var0 instanceof ComponentSampleModel) {
               var4 = var0.getNumBands();
               Object var5 = null;
               if (var4 <= 2) {
                  var5 = ColorSpace.getInstance(1003);
               } else if (var4 <= 4) {
                  var5 = ColorSpace.getInstance(1000);
               } else {
                  var5 = new BogusColorSpace(var4);
               }

               var6 = var4 == 2 || var4 == 4;
               var7 = false;
               var8 = var6 ? 3 : 1;
               var2 = new ComponentColorModel((ColorSpace)var5, var3, var6, var7, var8, var1);
            } else {
               int var18;
               if (var0.getNumBands() <= 4 && var0 instanceof SinglePixelPackedSampleModel) {
                  SinglePixelPackedSampleModel var13 = (SinglePixelPackedSampleModel)var0;
                  int[] var16 = var13.getBitMasks();
                  var6 = false;
                  var7 = false;
                  boolean var19 = false;
                  int var9 = 0;
                  int var10 = var16.length;
                  int var17;
                  if (var10 <= 2) {
                     var17 = var18 = var8 = var16[0];
                     if (var10 == 2) {
                        var9 = var16[1];
                     }
                  } else {
                     var17 = var16[0];
                     var18 = var16[1];
                     var8 = var16[2];
                     if (var10 == 4) {
                        var9 = var16[3];
                     }
                  }

                  int var11 = 0;

                  for(int var12 = 0; var12 < var3.length; ++var12) {
                     var11 += var3[var12];
                  }

                  return new DirectColorModel(var11, var17, var18, var8, var9);
               }

               if (var0 instanceof MultiPixelPackedSampleModel) {
                  var4 = var3[0];
                  int var15 = 1 << var4;
                  byte[] var14 = new byte[var15];

                  for(var18 = 0; var18 < var15; ++var18) {
                     var14[var18] = (byte)(var18 * 255 / (var15 - 1));
                  }

                  var2 = new IndexColorModel(var4, var15, var14, var14, var14);
               }
            }

            return (ColorModel)var2;
         default:
            return null;
         }
      }
   }

   public static byte[] getPackedBinaryData(Raster var0, Rectangle var1) {
      SampleModel var2 = var0.getSampleModel();
      if (!isBinary(var2)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int var3 = var1.x;
         int var4 = var1.y;
         int var5 = var1.width;
         int var6 = var1.height;
         DataBuffer var7 = var0.getDataBuffer();
         int var8 = var3 - var0.getSampleModelTranslateX();
         int var9 = var4 - var0.getSampleModelTranslateY();
         MultiPixelPackedSampleModel var10 = (MultiPixelPackedSampleModel)var2;
         int var11 = var10.getScanlineStride();
         int var12 = var7.getOffset() + var10.getOffset(var8, var9);
         int var13 = var10.getBitOffset(var8);
         int var14 = (var5 + 7) / 8;
         if (var7 instanceof DataBufferByte && var12 == 0 && var13 == 0 && var14 == var11 && ((DataBufferByte)var7).getData().length == var14 * var6) {
            return ((DataBufferByte)var7).getData();
         } else {
            byte[] var15 = new byte[var14 * var6];
            int var16 = 0;
            byte[] var17;
            int var18;
            int var19;
            int var20;
            short[] var26;
            int[] var27;
            int var28;
            if (var13 == 0) {
               if (var7 instanceof DataBufferByte) {
                  var17 = ((DataBufferByte)var7).getData();
                  var18 = var14;
                  var19 = 0;

                  for(var20 = 0; var20 < var6; ++var20) {
                     System.arraycopy(var17, var12, var15, var19, var18);
                     var19 += var18;
                     var12 += var11;
                  }
               } else if (!(var7 instanceof DataBufferShort) && !(var7 instanceof DataBufferUShort)) {
                  if (var7 instanceof DataBufferInt) {
                     var27 = ((DataBufferInt)var7).getData();

                     for(var18 = 0; var18 < var6; ++var18) {
                        var19 = var5;

                        for(var20 = var12; var19 > 24; var19 -= 32) {
                           var28 = var27[var20++];
                           var15[var16++] = (byte)(var28 >>> 24 & 255);
                           var15[var16++] = (byte)(var28 >>> 16 & 255);
                           var15[var16++] = (byte)(var28 >>> 8 & 255);
                           var15[var16++] = (byte)(var28 & 255);
                        }

                        for(var28 = 24; var19 > 0; var19 -= 8) {
                           var15[var16++] = (byte)(var27[var20] >>> var28 & 255);
                           var28 -= 8;
                        }

                        var12 += var11;
                     }
                  }
               } else {
                  var26 = var7 instanceof DataBufferShort ? ((DataBufferShort)var7).getData() : ((DataBufferUShort)var7).getData();

                  for(var18 = 0; var18 < var6; ++var18) {
                     var19 = var5;

                     for(var20 = var12; var19 > 8; var19 -= 16) {
                        short var21 = var26[var20++];
                        var15[var16++] = (byte)(var21 >>> 8 & 255);
                        var15[var16++] = (byte)(var21 & 255);
                     }

                     if (var19 > 0) {
                        var15[var16++] = (byte)(var26[var20] >>> 8 & 255);
                     }

                     var12 += var11;
                  }
               }
            } else {
               int var22;
               if (var7 instanceof DataBufferByte) {
                  var17 = ((DataBufferByte)var7).getData();
                  if ((var13 & 7) == 0) {
                     var18 = var14;
                     var19 = 0;

                     for(var20 = 0; var20 < var6; ++var20) {
                        System.arraycopy(var17, var12, var15, var19, var18);
                        var19 += var18;
                        var12 += var11;
                     }
                  } else {
                     var18 = var13 & 7;
                     var19 = 8 - var18;

                     for(var20 = 0; var20 < var6; ++var20) {
                        var28 = var12;

                        for(var22 = var5; var22 > 0; var22 -= 8) {
                           if (var22 > var19) {
                              var15[var16++] = (byte)((var17[var28++] & 255) << var18 | (var17[var28] & 255) >>> var19);
                           } else {
                              var15[var16++] = (byte)((var17[var28] & 255) << var18);
                           }
                        }

                        var12 += var11;
                     }
                  }
               } else {
                  int var23;
                  int var24;
                  int var25;
                  if (!(var7 instanceof DataBufferShort) && !(var7 instanceof DataBufferUShort)) {
                     if (var7 instanceof DataBufferInt) {
                        var27 = ((DataBufferInt)var7).getData();

                        for(var18 = 0; var18 < var6; ++var18) {
                           var19 = var13;

                           for(var20 = 0; var20 < var5; var19 += 8) {
                              var28 = var12 + var19 / 32;
                              var22 = var19 % 32;
                              var23 = var27[var28];
                              if (var22 <= 24) {
                                 var15[var16++] = (byte)(var23 >>> 24 - var22);
                              } else {
                                 var24 = var22 - 24;
                                 var25 = var27[var28 + 1];
                                 var15[var16++] = (byte)(var23 << var24 | var25 >>> 32 - var24);
                              }

                              var20 += 8;
                           }

                           var12 += var11;
                        }
                     }
                  } else {
                     var26 = var7 instanceof DataBufferShort ? ((DataBufferShort)var7).getData() : ((DataBufferUShort)var7).getData();

                     for(var18 = 0; var18 < var6; ++var18) {
                        var19 = var13;

                        for(var20 = 0; var20 < var5; var19 += 8) {
                           var28 = var12 + var19 / 16;
                           var22 = var19 % 16;
                           var23 = var26[var28] & '\uffff';
                           if (var22 <= 8) {
                              var15[var16++] = (byte)(var23 >>> 8 - var22);
                           } else {
                              var24 = var22 - 8;
                              var25 = var26[var28 + 1] & '\uffff';
                              var15[var16++] = (byte)(var23 << var24 | var25 >>> 16 - var24);
                           }

                           var20 += 8;
                        }

                        var12 += var11;
                     }
                  }
               }
            }

            return var15;
         }
      }
   }

   public static byte[] getUnpackedBinaryData(Raster var0, Rectangle var1) {
      SampleModel var2 = var0.getSampleModel();
      if (!isBinary(var2)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int var3 = var1.x;
         int var4 = var1.y;
         int var5 = var1.width;
         int var6 = var1.height;
         DataBuffer var7 = var0.getDataBuffer();
         int var8 = var3 - var0.getSampleModelTranslateX();
         int var9 = var4 - var0.getSampleModelTranslateY();
         MultiPixelPackedSampleModel var10 = (MultiPixelPackedSampleModel)var2;
         int var11 = var10.getScanlineStride();
         int var12 = var7.getOffset() + var10.getOffset(var8, var9);
         int var13 = var10.getBitOffset(var8);
         byte[] var14 = new byte[var5 * var6];
         int var15 = var4 + var6;
         int var16 = var3 + var5;
         int var17 = 0;
         int var19;
         int var20;
         int var21;
         if (var7 instanceof DataBufferByte) {
            byte[] var18 = ((DataBufferByte)var7).getData();

            for(var19 = var4; var19 < var15; ++var19) {
               var20 = var12 * 8 + var13;

               for(var21 = var3; var21 < var16; ++var21) {
                  byte var22 = var18[var20 / 8];
                  var14[var17++] = (byte)(var22 >>> (7 - var20 & 7) & 1);
                  ++var20;
               }

               var12 += var11;
            }
         } else if (!(var7 instanceof DataBufferShort) && !(var7 instanceof DataBufferUShort)) {
            if (var7 instanceof DataBufferInt) {
               int[] var24 = ((DataBufferInt)var7).getData();

               for(var19 = var4; var19 < var15; ++var19) {
                  var20 = var12 * 32 + var13;

                  for(var21 = var3; var21 < var16; ++var21) {
                     int var26 = var24[var20 / 32];
                     var14[var17++] = (byte)(var26 >>> 31 - var20 % 32 & 1);
                     ++var20;
                  }

                  var12 += var11;
               }
            }
         } else {
            short[] var23 = var7 instanceof DataBufferShort ? ((DataBufferShort)var7).getData() : ((DataBufferUShort)var7).getData();

            for(var19 = var4; var19 < var15; ++var19) {
               var20 = var12 * 16 + var13;

               for(var21 = var3; var21 < var16; ++var21) {
                  short var25 = var23[var20 / 16];
                  var14[var17++] = (byte)(var25 >>> 15 - var20 % 16 & 1);
                  ++var20;
               }

               var12 += var11;
            }
         }

         return var14;
      }
   }

   public static void setPackedBinaryData(byte[] var0, WritableRaster var1, Rectangle var2) {
      SampleModel var3 = var1.getSampleModel();
      if (!isBinary(var3)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int var4 = var2.x;
         int var5 = var2.y;
         int var6 = var2.width;
         int var7 = var2.height;
         DataBuffer var8 = var1.getDataBuffer();
         int var9 = var4 - var1.getSampleModelTranslateX();
         int var10 = var5 - var1.getSampleModelTranslateY();
         MultiPixelPackedSampleModel var11 = (MultiPixelPackedSampleModel)var3;
         int var12 = var11.getScanlineStride();
         int var13 = var8.getOffset() + var11.getOffset(var9, var10);
         int var14 = var11.getBitOffset(var9);
         int var15 = 0;
         int var17;
         int var19;
         int var20;
         if (var14 == 0) {
            int var18;
            if (var8 instanceof DataBufferByte) {
               byte[] var16 = ((DataBufferByte)var8).getData();
               if (var16 == var0) {
                  return;
               }

               var17 = (var6 + 7) / 8;
               var18 = 0;

               for(var19 = 0; var19 < var7; ++var19) {
                  System.arraycopy(var0, var18, var16, var13, var17);
                  var18 += var17;
                  var13 += var12;
               }
            } else if (!(var8 instanceof DataBufferShort) && !(var8 instanceof DataBufferUShort)) {
               if (var8 instanceof DataBufferInt) {
                  int[] var34 = ((DataBufferInt)var8).getData();

                  for(var17 = 0; var17 < var7; ++var17) {
                     var18 = var6;

                     for(var19 = var13; var18 > 24; var18 -= 32) {
                        var34[var19++] = (var0[var15++] & 255) << 24 | (var0[var15++] & 255) << 16 | (var0[var15++] & 255) << 8 | var0[var15++] & 255;
                     }

                     for(var20 = 24; var18 > 0; var18 -= 8) {
                        var34[var19] |= (var0[var15++] & 255) << var20;
                        var20 -= 8;
                     }

                     var13 += var12;
                  }
               }
            } else {
               short[] var33 = var8 instanceof DataBufferShort ? ((DataBufferShort)var8).getData() : ((DataBufferUShort)var8).getData();

               for(var17 = 0; var17 < var7; ++var17) {
                  var18 = var6;

                  for(var19 = var13; var18 > 8; var18 -= 16) {
                     var33[var19++] = (short)((var0[var15++] & 255) << 8 | var0[var15++] & 255);
                  }

                  if (var18 > 0) {
                     var33[var19++] = (short)((var0[var15++] & 255) << 8);
                  }

                  var13 += var12;
               }
            }
         } else {
            int var35 = (var6 + 7) / 8;
            var17 = 0;
            int var21;
            int var24;
            int var25;
            int var26;
            int var28;
            if (var8 instanceof DataBufferByte) {
               byte[] var36 = ((DataBufferByte)var8).getData();
               if ((var14 & 7) == 0) {
                  for(var19 = 0; var19 < var7; ++var19) {
                     System.arraycopy(var0, var17, var36, var13, var35);
                     var17 += var35;
                     var13 += var12;
                  }
               } else {
                  var19 = var14 & 7;
                  var20 = 8 - var19;
                  var21 = 8 + var20;
                  byte var22 = (byte)(255 << var20);
                  byte var23 = (byte)(~var22);

                  for(var24 = 0; var24 < var7; ++var24) {
                     var25 = var13;

                     for(var26 = var6; var26 > 0; var26 -= 8) {
                        byte var27 = var0[var15++];
                        if (var26 > var21) {
                           var36[var25] = (byte)(var36[var25] & var22 | (var27 & 255) >>> var19);
                           ++var25;
                           var36[var25] = (byte)((var27 & 255) << var20);
                        } else if (var26 > var20) {
                           var36[var25] = (byte)(var36[var25] & var22 | (var27 & 255) >>> var19);
                           ++var25;
                           var36[var25] = (byte)(var36[var25] & var23 | (var27 & 255) << var20);
                        } else {
                           var28 = (1 << var20 - var26) - 1;
                           var36[var25] = (byte)(var36[var25] & (var22 | var28) | (var27 & 255) >>> var19 & ~var28);
                        }
                     }

                     var13 += var12;
                  }
               }
            } else {
               int var29;
               int var30;
               int var31;
               int var44;
               if (!(var8 instanceof DataBufferShort) && !(var8 instanceof DataBufferUShort)) {
                  if (var8 instanceof DataBufferInt) {
                     int[] var38 = ((DataBufferInt)var8).getData();
                     var19 = var14 & 7;
                     var20 = 8 - var19;
                     var21 = 32 + var20;
                     int var40 = -1 << var20;
                     int var42 = ~var40;

                     for(var24 = 0; var24 < var7; ++var24) {
                        var25 = var14;
                        var26 = var6;

                        for(var44 = 0; var44 < var6; var26 -= 8) {
                           var28 = var13 + (var25 >> 5);
                           var29 = var25 & 31;
                           var30 = var0[var15++] & 255;
                           if (var29 <= 24) {
                              var31 = 24 - var29;
                              if (var26 < 8) {
                                 var30 &= 255 << 8 - var26;
                              }

                              var38[var28] = var38[var28] & ~(255 << var31) | var30 << var31;
                           } else if (var26 > var21) {
                              var38[var28] = var38[var28] & var40 | var30 >>> var19;
                              ++var28;
                              var38[var28] = var30 << var20;
                           } else if (var26 > var20) {
                              var38[var28] = var38[var28] & var40 | var30 >>> var19;
                              ++var28;
                              var38[var28] = var38[var28] & var42 | var30 << var20;
                           } else {
                              var31 = (1 << var20 - var26) - 1;
                              var38[var28] = var38[var28] & (var40 | var31) | var30 >>> var19 & ~var31;
                           }

                           var44 += 8;
                           var25 += 8;
                        }

                        var13 += var12;
                     }
                  }
               } else {
                  short[] var37 = var8 instanceof DataBufferShort ? ((DataBufferShort)var8).getData() : ((DataBufferUShort)var8).getData();
                  var19 = var14 & 7;
                  var20 = 8 - var19;
                  var21 = 16 + var20;
                  short var39 = (short)(~(255 << var20));
                  short var41 = (short)('\uffff' << var20);
                  short var43 = (short)(~var41);

                  for(var25 = 0; var25 < var7; ++var25) {
                     var26 = var14;
                     var44 = var6;

                     for(var28 = 0; var28 < var6; var44 -= 8) {
                        var29 = var13 + (var26 >> 4);
                        var30 = var26 & 15;
                        var31 = var0[var15++] & 255;
                        if (var30 <= 8) {
                           if (var44 < 8) {
                              var31 &= 255 << 8 - var44;
                           }

                           var37[var29] = (short)(var37[var29] & var39 | var31 << var20);
                        } else if (var44 > var21) {
                           var37[var29] = (short)(var37[var29] & var41 | var31 >>> var19 & '\uffff');
                           ++var29;
                           var37[var29] = (short)(var31 << var20 & '\uffff');
                        } else if (var44 > var20) {
                           var37[var29] = (short)(var37[var29] & var41 | var31 >>> var19 & '\uffff');
                           ++var29;
                           var37[var29] = (short)(var37[var29] & var43 | var31 << var20 & '\uffff');
                        } else {
                           int var32 = (1 << var20 - var44) - 1;
                           var37[var29] = (short)(var37[var29] & (var41 | var32) | var31 >>> var19 & '\uffff' & ~var32);
                        }

                        var28 += 8;
                        var26 += 8;
                     }

                     var13 += var12;
                  }
               }
            }
         }

      }
   }

   public static void setUnpackedBinaryData(byte[] var0, WritableRaster var1, Rectangle var2) {
      SampleModel var3 = var1.getSampleModel();
      if (!isBinary(var3)) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
      } else {
         int var4 = var2.x;
         int var5 = var2.y;
         int var6 = var2.width;
         int var7 = var2.height;
         DataBuffer var8 = var1.getDataBuffer();
         int var9 = var4 - var1.getSampleModelTranslateX();
         int var10 = var5 - var1.getSampleModelTranslateY();
         MultiPixelPackedSampleModel var11 = (MultiPixelPackedSampleModel)var3;
         int var12 = var11.getScanlineStride();
         int var13 = var8.getOffset() + var11.getOffset(var9, var10);
         int var14 = var11.getBitOffset(var9);
         int var15 = 0;
         int var17;
         int var18;
         int var19;
         if (var8 instanceof DataBufferByte) {
            byte[] var16 = ((DataBufferByte)var8).getData();

            for(var17 = 0; var17 < var7; ++var17) {
               var18 = var13 * 8 + var14;

               for(var19 = 0; var19 < var6; ++var19) {
                  if (var0[var15++] != 0) {
                     var16[var18 / 8] |= (byte)(1 << (7 - var18 & 7));
                  }

                  ++var18;
               }

               var13 += var12;
            }
         } else if (!(var8 instanceof DataBufferShort) && !(var8 instanceof DataBufferUShort)) {
            if (var8 instanceof DataBufferInt) {
               int[] var21 = ((DataBufferInt)var8).getData();

               for(var17 = 0; var17 < var7; ++var17) {
                  var18 = var13 * 32 + var14;

                  for(var19 = 0; var19 < var6; ++var19) {
                     if (var0[var15++] != 0) {
                        var21[var18 / 32] |= 1 << 31 - var18 % 32;
                     }

                     ++var18;
                  }

                  var13 += var12;
               }
            }
         } else {
            short[] var20 = var8 instanceof DataBufferShort ? ((DataBufferShort)var8).getData() : ((DataBufferUShort)var8).getData();

            for(var17 = 0; var17 < var7; ++var17) {
               var18 = var13 * 16 + var14;

               for(var19 = 0; var19 < var6; ++var19) {
                  if (var0[var15++] != 0) {
                     var20[var18 / 16] |= (short)(1 << 15 - var18 % 16);
                  }

                  ++var18;
               }

               var13 += var12;
            }
         }

      }
   }

   public static boolean isBinary(SampleModel var0) {
      return var0 instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)var0).getPixelBitStride() == 1 && var0.getNumBands() == 1;
   }

   public static ColorModel createColorModel(ColorSpace var0, SampleModel var1) {
      Object var2 = null;
      if (var1 == null) {
         throw new IllegalArgumentException(I18N.getString("ImageUtil1"));
      } else {
         int var3 = var1.getNumBands();
         if (var3 >= 1 && var3 <= 4) {
            int var4 = var1.getDataType();
            int var6;
            boolean var7;
            int var8;
            int var10;
            if (var1 instanceof ComponentSampleModel) {
               if (var4 < 0 || var4 > 5) {
                  return null;
               }

               if (var0 == null) {
                  var0 = var3 <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
               }

               boolean var5 = var3 == 2 || var3 == 4;
               var6 = var5 ? 3 : 1;
               var7 = false;
               var8 = DataBuffer.getDataTypeSize(var4);
               int[] var9 = new int[var3];

               for(var10 = 0; var10 < var3; ++var10) {
                  var9[var10] = var8;
               }

               var2 = new ComponentColorModel(var0, var9, var5, var7, var6, var4);
            } else if (var1 instanceof SinglePixelPackedSampleModel) {
               SinglePixelPackedSampleModel var14 = (SinglePixelPackedSampleModel)var1;
               int[] var16 = var14.getBitMasks();
               var7 = false;
               boolean var18 = false;
               boolean var20 = false;
               var10 = 0;
               var3 = var16.length;
               int var17;
               int var21;
               if (var3 <= 2) {
                  var17 = var8 = var21 = var16[0];
                  if (var3 == 2) {
                     var10 = var16[1];
                  }
               } else {
                  var17 = var16[0];
                  var8 = var16[1];
                  var21 = var16[2];
                  if (var3 == 4) {
                     var10 = var16[3];
                  }
               }

               int[] var11 = var14.getSampleSize();
               int var12 = 0;

               for(int var13 = 0; var13 < var11.length; ++var13) {
                  var12 += var11[var13];
               }

               if (var0 == null) {
                  var0 = ColorSpace.getInstance(1000);
               }

               var2 = new DirectColorModel(var0, var12, var17, var8, var21, var10, false, var1.getDataType());
            } else if (var1 instanceof MultiPixelPackedSampleModel) {
               int var15 = ((MultiPixelPackedSampleModel)var1).getPixelBitStride();
               var6 = 1 << var15;
               byte[] var19 = new byte[var6];

               for(var8 = 0; var8 < var6; ++var8) {
                  var19[var8] = (byte)(255 * var8 / (var6 - 1));
               }

               var2 = new IndexColorModel(var15, var6, var19, var19, var19);
            }

            return (ColorModel)var2;
         } else {
            return null;
         }
      }
   }

   public static int getElementSize(SampleModel var0) {
      int var1 = DataBuffer.getDataTypeSize(var0.getDataType());
      if (var0 instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel var2 = (MultiPixelPackedSampleModel)var0;
         return var2.getSampleSize(0) * var2.getNumBands();
      } else if (var0 instanceof ComponentSampleModel) {
         return var0.getNumBands() * var1;
      } else {
         return var0 instanceof SinglePixelPackedSampleModel ? var1 : var1 * var0.getNumBands();
      }
   }

   public static long getTileSize(SampleModel var0) {
      int var1 = DataBuffer.getDataTypeSize(var0.getDataType());
      if (var0 instanceof MultiPixelPackedSampleModel) {
         MultiPixelPackedSampleModel var12 = (MultiPixelPackedSampleModel)var0;
         return (long)((var12.getScanlineStride() * var12.getHeight() + (var12.getDataBitOffset() + var1 - 1) / var1) * ((var1 + 7) / 8));
      } else if (!(var0 instanceof ComponentSampleModel)) {
         if (var0 instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel var11 = (SinglePixelPackedSampleModel)var0;
            long var13 = (long)(var11.getScanlineStride() * (var11.getHeight() - 1) + var11.getWidth());
            return var13 * (long)((var1 + 7) / 8);
         } else {
            return 0L;
         }
      } else {
         ComponentSampleModel var2 = (ComponentSampleModel)var0;
         int[] var3 = var2.getBandOffsets();
         int var4 = var3[0];

         for(int var5 = 1; var5 < var3.length; ++var5) {
            var4 = Math.max(var4, var3[var5]);
         }

         long var14 = 0L;
         int var7 = var2.getPixelStride();
         int var8 = var2.getScanlineStride();
         if (var4 >= 0) {
            var14 += (long)(var4 + 1);
         }

         if (var7 > 0) {
            var14 += (long)(var7 * (var0.getWidth() - 1));
         }

         if (var8 > 0) {
            var14 += (long)(var8 * (var0.getHeight() - 1));
         }

         int[] var9 = var2.getBankIndices();
         var4 = var9[0];

         for(int var10 = 1; var10 < var9.length; ++var10) {
            var4 = Math.max(var4, var9[var10]);
         }

         return var14 * (long)(var4 + 1) * (long)((var1 + 7) / 8);
      }
   }

   public static long getBandSize(SampleModel var0) {
      int var1 = DataBuffer.getDataTypeSize(var0.getDataType());
      if (var0 instanceof ComponentSampleModel) {
         ComponentSampleModel var2 = (ComponentSampleModel)var0;
         int var3 = var2.getPixelStride();
         int var4 = var2.getScanlineStride();
         long var5 = (long)Math.min(var3, var4);
         if (var3 > 0) {
            var5 += (long)(var3 * (var0.getWidth() - 1));
         }

         if (var4 > 0) {
            var5 += (long)(var4 * (var0.getHeight() - 1));
         }

         return var5 * (long)((var1 + 7) / 8);
      } else {
         return getTileSize(var0);
      }
   }

   public static boolean isIndicesForGrayscale(byte[] var0, byte[] var1, byte[] var2) {
      if (var0.length == var1.length && var0.length == var2.length) {
         int var3 = var0.length;
         if (var3 != 256) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               byte var5 = (byte)var4;
               if (var0[var4] != var5 || var1[var4] != var5 || var2[var4] != var5) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static String convertObjectToString(Object var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = "";
         int var3;
         if (var0 instanceof byte[]) {
            byte[] var5 = (byte[])((byte[])var0);

            for(var3 = 0; var3 < var5.length; ++var3) {
               var1 = var1 + var5[var3] + " ";
            }

            return var1;
         } else if (var0 instanceof int[]) {
            int[] var4 = (int[])((int[])var0);

            for(var3 = 0; var3 < var4.length; ++var3) {
               var1 = var1 + var4[var3] + " ";
            }

            return var1;
         } else if (!(var0 instanceof short[])) {
            return var0.toString();
         } else {
            short[] var2 = (short[])((short[])var0);

            for(var3 = 0; var3 < var2.length; ++var3) {
               var1 = var1 + var2[var3] + " ";
            }

            return var1;
         }
      }
   }

   public static final void canEncodeImage(ImageWriter var0, ImageTypeSpecifier var1) throws IIOException {
      ImageWriterSpi var2 = var0.getOriginatingProvider();
      if (var1 != null && var2 != null && !var2.canEncodeImage(var1)) {
         throw new IIOException(I18N.getString("ImageUtil2") + " " + var0.getClass().getName());
      }
   }

   public static final void canEncodeImage(ImageWriter var0, ColorModel var1, SampleModel var2) throws IIOException {
      ImageTypeSpecifier var3 = null;
      if (var1 != null && var2 != null) {
         var3 = new ImageTypeSpecifier(var1, var2);
      }

      canEncodeImage(var0, var3);
   }

   public static final boolean imageIsContiguous(RenderedImage var0) {
      SampleModel var1;
      if (var0 instanceof BufferedImage) {
         WritableRaster var2 = ((BufferedImage)var0).getRaster();
         var1 = var2.getSampleModel();
      } else {
         var1 = var0.getSampleModel();
      }

      if (var1 instanceof ComponentSampleModel) {
         ComponentSampleModel var6 = (ComponentSampleModel)var1;
         if (var6.getPixelStride() != var6.getNumBands()) {
            return false;
         } else {
            int[] var3 = var6.getBandOffsets();

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (var3[var4] != var4) {
                  return false;
               }
            }

            int[] var7 = var6.getBankIndices();

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if (var7[var5] != 0) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return isBinary(var1);
      }
   }
}
