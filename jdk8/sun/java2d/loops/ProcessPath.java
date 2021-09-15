package sun.java2d.loops;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ProcessPath {
   public static final int PH_MODE_DRAW_CLIP = 0;
   public static final int PH_MODE_FILL_CLIP = 1;
   public static ProcessPath.EndSubPathHandler noopEndSubPathHandler = new ProcessPath.EndSubPathHandler() {
      public void processEndSubPath() {
      }
   };
   private static final float UPPER_BND = 8.5070587E37F;
   private static final float LOWER_BND = -8.5070587E37F;
   private static final int FWD_PREC = 7;
   private static final int MDP_PREC = 10;
   private static final int MDP_MULT = 1024;
   private static final int MDP_HALF_MULT = 512;
   private static final int UPPER_OUT_BND = 1048576;
   private static final int LOWER_OUT_BND = -1048576;
   private static final float CALC_UBND = 1048576.0F;
   private static final float CALC_LBND = -1048576.0F;
   public static final int EPSFX = 1;
   public static final float EPSF = 9.765625E-4F;
   private static final int MDP_W_MASK = -1024;
   private static final int MDP_F_MASK = 1023;
   private static final int MAX_CUB_SIZE = 256;
   private static final int MAX_QUAD_SIZE = 1024;
   private static final int DF_CUB_STEPS = 3;
   private static final int DF_QUAD_STEPS = 2;
   private static final int DF_CUB_SHIFT = 6;
   private static final int DF_QUAD_SHIFT = 1;
   private static final int DF_CUB_COUNT = 8;
   private static final int DF_QUAD_COUNT = 4;
   private static final int DF_CUB_DEC_BND = 262144;
   private static final int DF_CUB_INC_BND = 32768;
   private static final int DF_QUAD_DEC_BND = 8192;
   private static final int DF_QUAD_INC_BND = 1024;
   private static final int CUB_A_SHIFT = 7;
   private static final int CUB_B_SHIFT = 11;
   private static final int CUB_C_SHIFT = 13;
   private static final int CUB_A_MDP_MULT = 128;
   private static final int CUB_B_MDP_MULT = 2048;
   private static final int CUB_C_MDP_MULT = 8192;
   private static final int QUAD_A_SHIFT = 7;
   private static final int QUAD_B_SHIFT = 9;
   private static final int QUAD_A_MDP_MULT = 128;
   private static final int QUAD_B_MDP_MULT = 512;
   private static final int CRES_MIN_CLIPPED = 0;
   private static final int CRES_MAX_CLIPPED = 1;
   private static final int CRES_NOT_CLIPPED = 3;
   private static final int CRES_INVISIBLE = 4;
   private static final int DF_MAX_POINT = 256;

   public static boolean fillPath(ProcessPath.DrawHandler var0, Path2D.Float var1, int var2, int var3) {
      ProcessPath.FillProcessHandler var4 = new ProcessPath.FillProcessHandler(var0);
      if (!doProcessPath(var4, var1, (float)var2, (float)var3)) {
         return false;
      } else {
         FillPolygon(var4, var1.getWindingRule());
         return true;
      }
   }

   public static boolean drawPath(ProcessPath.DrawHandler var0, ProcessPath.EndSubPathHandler var1, Path2D.Float var2, int var3, int var4) {
      return doProcessPath(new ProcessPath.DrawProcessHandler(var0, var1), var2, (float)var3, (float)var4);
   }

   public static boolean drawPath(ProcessPath.DrawHandler var0, Path2D.Float var1, int var2, int var3) {
      return doProcessPath(new ProcessPath.DrawProcessHandler(var0, noopEndSubPathHandler), var1, (float)var2, (float)var3);
   }

   private static float CLIP(float var0, float var1, float var2, float var3, double var4) {
      return (float)((double)var1 + (var4 - (double)var0) * (double)(var3 - var1) / (double)(var2 - var0));
   }

   private static int CLIP(int var0, int var1, int var2, int var3, double var4) {
      return (int)((double)var1 + (var4 - (double)var0) * (double)(var3 - var1) / (double)(var2 - var0));
   }

   private static boolean IS_CLIPPED(int var0) {
      return var0 == 0 || var0 == 1;
   }

   private static int TESTANDCLIP(float var0, float var1, float[] var2, int var3, int var4, int var5, int var6) {
      byte var9 = 3;
      if (var2[var3] < var0 || var2[var3] > var1) {
         double var7;
         if (var2[var3] < var0) {
            if (var2[var5] < var0) {
               return 4;
            }

            var9 = 0;
            var7 = (double)var0;
         } else {
            if (var2[var5] > var1) {
               return 4;
            }

            var9 = 1;
            var7 = (double)var1;
         }

         var2[var4] = CLIP(var2[var3], var2[var4], var2[var5], var2[var6], var7);
         var2[var3] = (float)var7;
      }

      return var9;
   }

   private static int TESTANDCLIP(int var0, int var1, int[] var2, int var3, int var4, int var5, int var6) {
      byte var9 = 3;
      if (var2[var3] < var0 || var2[var3] > var1) {
         double var7;
         if (var2[var3] < var0) {
            if (var2[var5] < var0) {
               return 4;
            }

            var9 = 0;
            var7 = (double)var0;
         } else {
            if (var2[var5] > var1) {
               return 4;
            }

            var9 = 1;
            var7 = (double)var1;
         }

         var2[var4] = CLIP(var2[var3], var2[var4], var2[var5], var2[var6], var7);
         var2[var3] = (int)var7;
      }

      return var9;
   }

   private static int CLIPCLAMP(float var0, float var1, float[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      var2[var7] = var2[var3];
      var2[var8] = var2[var4];
      int var9 = TESTANDCLIP(var0, var1, var2, var3, var4, var5, var6);
      if (var9 == 0) {
         var2[var7] = var2[var3];
      } else if (var9 == 1) {
         var2[var7] = var2[var3];
         var9 = 1;
      } else if (var9 == 4) {
         if (var2[var3] > var1) {
            var9 = 4;
         } else {
            var2[var3] = var0;
            var2[var5] = var0;
            var9 = 3;
         }
      }

      return var9;
   }

   private static int CLIPCLAMP(int var0, int var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      var2[var7] = var2[var3];
      var2[var8] = var2[var4];
      int var9 = TESTANDCLIP(var0, var1, var2, var3, var4, var5, var6);
      if (var9 == 0) {
         var2[var7] = var2[var3];
      } else if (var9 == 1) {
         var2[var7] = var2[var3];
         var9 = 1;
      } else if (var9 == 4) {
         if (var2[var3] > var1) {
            var9 = 4;
         } else {
            var2[var3] = var0;
            var2[var5] = var0;
            var9 = 3;
         }
      }

      return var9;
   }

   private static void DrawMonotonicQuad(ProcessPath.ProcessHandler var0, float[] var1, boolean var2, int[] var3) {
      int var4 = (int)(var1[0] * 1024.0F);
      int var5 = (int)(var1[1] * 1024.0F);
      int var6 = (int)(var1[4] * 1024.0F);
      int var7 = (int)(var1[5] * 1024.0F);
      int var8 = (var4 & 1023) << 1;
      int var9 = (var5 & 1023) << 1;
      int var10 = 4;
      int var11 = 1;
      int var12 = (int)((var1[0] - 2.0F * var1[2] + var1[4]) * 128.0F);
      int var13 = (int)((var1[1] - 2.0F * var1[3] + var1[5]) * 128.0F);
      int var14 = (int)((-2.0F * var1[0] + 2.0F * var1[2]) * 512.0F);
      int var15 = (int)((-2.0F * var1[1] + 2.0F * var1[3]) * 512.0F);
      int var16 = 2 * var12;
      int var17 = 2 * var13;
      int var18 = var12 + var14;
      int var19 = var13 + var15;
      int var22 = var4;
      int var23 = var5;
      int var24 = Math.max(Math.abs(var16), Math.abs(var17));
      int var25 = var6 - var4;
      int var26 = var7 - var5;
      int var27 = var4 & -1024;

      int var28;
      for(var28 = var5 & -1024; var24 > 8192; var11 += 2) {
         var18 = (var18 << 1) - var12;
         var19 = (var19 << 1) - var13;
         var10 <<= 1;
         var24 >>= 2;
         var8 <<= 2;
         var9 <<= 2;
      }

      int var20;
      int var21;
      for(; var10-- > 1; var0.processFixedLine(var20, var21, var22, var23, var3, var2, false)) {
         var8 += var18;
         var9 += var19;
         var18 += var16;
         var19 += var17;
         var20 = var22;
         var21 = var23;
         var22 = var27 + (var8 >> var11);
         var23 = var28 + (var9 >> var11);
         if ((var6 - var22 ^ var25) < 0) {
            var22 = var6;
         }

         if ((var7 - var23 ^ var26) < 0) {
            var23 = var7;
         }
      }

      var0.processFixedLine(var22, var23, var6, var7, var3, var2, false);
   }

   private static void ProcessMonotonicQuad(ProcessPath.ProcessHandler var0, float[] var1, int[] var2) {
      float[] var3 = new float[6];
      float var8;
      float var6 = var8 = var1[0];
      float var9;
      float var7 = var9 = var1[1];

      for(int var10 = 2; var10 < 6; var10 += 2) {
         var6 = var6 > var1[var10] ? var1[var10] : var6;
         var8 = var8 < var1[var10] ? var1[var10] : var8;
         var7 = var7 > var1[var10 + 1] ? var1[var10 + 1] : var7;
         var9 = var9 < var1[var10 + 1] ? var1[var10 + 1] : var9;
      }

      if (var0.clipMode == 0) {
         if (var0.dhnd.xMaxf < var6 || var0.dhnd.xMinf > var8 || var0.dhnd.yMaxf < var7 || var0.dhnd.yMinf > var9) {
            return;
         }
      } else {
         if (var0.dhnd.yMaxf < var7 || var0.dhnd.yMinf > var9 || var0.dhnd.xMaxf < var6) {
            return;
         }

         if (var0.dhnd.xMinf > var8) {
            var1[0] = var1[2] = var1[4] = var0.dhnd.xMinf;
         }
      }

      if (var8 - var6 <= 1024.0F && var9 - var7 <= 1024.0F) {
         DrawMonotonicQuad(var0, var1, var0.dhnd.xMinf >= var6 || var0.dhnd.xMaxf <= var8 || var0.dhnd.yMinf >= var7 || var0.dhnd.yMaxf <= var9, var2);
      } else {
         var3[4] = var1[4];
         var3[5] = var1[5];
         var3[2] = (var1[2] + var1[4]) / 2.0F;
         var3[3] = (var1[3] + var1[5]) / 2.0F;
         var1[2] = (var1[0] + var1[2]) / 2.0F;
         var1[3] = (var1[1] + var1[3]) / 2.0F;
         var1[4] = var3[0] = (var1[2] + var3[2]) / 2.0F;
         var1[5] = var3[1] = (var1[3] + var3[3]) / 2.0F;
         ProcessMonotonicQuad(var0, var1, var2);
         ProcessMonotonicQuad(var0, var3, var2);
      }

   }

   private static void ProcessQuad(ProcessPath.ProcessHandler var0, float[] var1, int[] var2) {
      double[] var3 = new double[2];
      int var4 = 0;
      double var5;
      double var7;
      double var9;
      if ((var1[0] > var1[2] || var1[2] > var1[4]) && (var1[0] < var1[2] || var1[2] < var1[4])) {
         var7 = (double)(var1[0] - 2.0F * var1[2] + var1[4]);
         if (var7 != 0.0D) {
            var9 = (double)(var1[0] - var1[2]);
            var5 = var9 / var7;
            if (var5 < 1.0D && var5 > 0.0D) {
               var3[var4++] = var5;
            }
         }
      }

      if ((var1[1] > var1[3] || var1[3] > var1[5]) && (var1[1] < var1[3] || var1[3] < var1[5])) {
         var7 = (double)(var1[1] - 2.0F * var1[3] + var1[5]);
         if (var7 != 0.0D) {
            var9 = (double)(var1[1] - var1[3]);
            var5 = var9 / var7;
            if (var5 < 1.0D && var5 > 0.0D) {
               if (var4 > 0) {
                  if (var3[0] > var5) {
                     var3[var4++] = var3[0];
                     var3[0] = var5;
                  } else if (var3[0] < var5) {
                     var3[var4++] = var5;
                  }
               } else {
                  var3[var4++] = var5;
               }
            }
         }
      }

      switch(var4) {
      case 0:
      default:
         break;
      case 1:
         ProcessFirstMonotonicPartOfQuad(var0, var1, var2, (float)var3[0]);
         break;
      case 2:
         ProcessFirstMonotonicPartOfQuad(var0, var1, var2, (float)var3[0]);
         var5 = var3[1] - var3[0];
         if (var5 > 0.0D) {
            ProcessFirstMonotonicPartOfQuad(var0, var1, var2, (float)(var5 / (1.0D - var3[0])));
         }
      }

      ProcessMonotonicQuad(var0, var1, var2);
   }

   private static void ProcessFirstMonotonicPartOfQuad(ProcessPath.ProcessHandler var0, float[] var1, int[] var2, float var3) {
      float[] var4 = new float[]{var1[0], var1[1], var1[0] + var3 * (var1[2] - var1[0]), var1[1] + var3 * (var1[3] - var1[1]), 0.0F, 0.0F};
      var1[2] += var3 * (var1[4] - var1[2]);
      var1[3] += var3 * (var1[5] - var1[3]);
      var1[0] = var4[4] = var4[2] + var3 * (var1[2] - var4[2]);
      var1[1] = var4[5] = var4[3] + var3 * (var1[3] - var4[3]);
      ProcessMonotonicQuad(var0, var4, var2);
   }

   private static void DrawMonotonicCubic(ProcessPath.ProcessHandler var0, float[] var1, boolean var2, int[] var3) {
      int var4 = (int)(var1[0] * 1024.0F);
      int var5 = (int)(var1[1] * 1024.0F);
      int var6 = (int)(var1[6] * 1024.0F);
      int var7 = (int)(var1[7] * 1024.0F);
      int var8 = (var4 & 1023) << 6;
      int var9 = (var5 & 1023) << 6;
      int var10 = 32768;
      int var11 = 262144;
      int var12 = 8;
      int var13 = 6;
      int var14 = (int)((-var1[0] + 3.0F * var1[2] - 3.0F * var1[4] + var1[6]) * 128.0F);
      int var15 = (int)((-var1[1] + 3.0F * var1[3] - 3.0F * var1[5] + var1[7]) * 128.0F);
      int var16 = (int)((3.0F * var1[0] - 6.0F * var1[2] + 3.0F * var1[4]) * 2048.0F);
      int var17 = (int)((3.0F * var1[1] - 6.0F * var1[3] + 3.0F * var1[5]) * 2048.0F);
      int var18 = (int)((-3.0F * var1[0] + 3.0F * var1[2]) * 8192.0F);
      int var19 = (int)((-3.0F * var1[1] + 3.0F * var1[3]) * 8192.0F);
      int var20 = 6 * var14;
      int var21 = 6 * var15;
      int var22 = var20 + var16;
      int var23 = var21 + var17;
      int var24 = var14 + (var16 >> 1) + var18;
      int var25 = var15 + (var17 >> 1) + var19;
      int var28 = var4;
      int var29 = var5;
      int var30 = var4 & -1024;
      int var31 = var5 & -1024;
      int var32 = var6 - var4;
      int var33 = var7 - var5;

      while(var12 > 0) {
         while(Math.abs(var22) > var11 || Math.abs(var23) > var11) {
            var22 = (var22 << 1) - var20;
            var23 = (var23 << 1) - var21;
            var24 = (var24 << 2) - (var22 >> 1);
            var25 = (var25 << 2) - (var23 >> 1);
            var12 <<= 1;
            var11 <<= 3;
            var10 <<= 3;
            var8 <<= 3;
            var9 <<= 3;
            var13 += 3;
         }

         while((var12 & 1) == 0 && var13 > 6 && Math.abs(var24) <= var10 && Math.abs(var25) <= var10) {
            var24 = (var24 >> 2) + (var22 >> 3);
            var25 = (var25 >> 2) + (var23 >> 3);
            var22 = var22 + var20 >> 1;
            var23 = var23 + var21 >> 1;
            var12 >>= 1;
            var11 >>= 3;
            var10 >>= 3;
            var8 >>= 3;
            var9 >>= 3;
            var13 -= 3;
         }

         --var12;
         if (var12 > 0) {
            var8 += var24;
            var9 += var25;
            var24 += var22;
            var25 += var23;
            var22 += var20;
            var23 += var21;
            int var26 = var28;
            int var27 = var29;
            var28 = var30 + (var8 >> var13);
            var29 = var31 + (var9 >> var13);
            if ((var6 - var28 ^ var32) < 0) {
               var28 = var6;
            }

            if ((var7 - var29 ^ var33) < 0) {
               var29 = var7;
            }

            var0.processFixedLine(var26, var27, var28, var29, var3, var2, false);
         } else {
            var0.processFixedLine(var28, var29, var6, var7, var3, var2, false);
         }
      }

   }

   private static void ProcessMonotonicCubic(ProcessPath.ProcessHandler var0, float[] var1, int[] var2) {
      float[] var3 = new float[8];
      float var7;
      float var6 = var7 = var1[0];
      float var9;
      float var8 = var9 = var1[1];

      for(int var10 = 2; var10 < 8; var10 += 2) {
         var6 = var6 > var1[var10] ? var1[var10] : var6;
         var7 = var7 < var1[var10] ? var1[var10] : var7;
         var8 = var8 > var1[var10 + 1] ? var1[var10 + 1] : var8;
         var9 = var9 < var1[var10 + 1] ? var1[var10 + 1] : var9;
      }

      if (var0.clipMode == 0) {
         if (var0.dhnd.xMaxf < var6 || var0.dhnd.xMinf > var7 || var0.dhnd.yMaxf < var8 || var0.dhnd.yMinf > var9) {
            return;
         }
      } else {
         if (var0.dhnd.yMaxf < var8 || var0.dhnd.yMinf > var9 || var0.dhnd.xMaxf < var6) {
            return;
         }

         if (var0.dhnd.xMinf > var7) {
            var1[0] = var1[2] = var1[4] = var1[6] = var0.dhnd.xMinf;
         }
      }

      if (var7 - var6 <= 256.0F && var9 - var8 <= 256.0F) {
         DrawMonotonicCubic(var0, var1, var0.dhnd.xMinf > var6 || var0.dhnd.xMaxf < var7 || var0.dhnd.yMinf > var8 || var0.dhnd.yMaxf < var9, var2);
      } else {
         var3[6] = var1[6];
         var3[7] = var1[7];
         var3[4] = (var1[4] + var1[6]) / 2.0F;
         var3[5] = (var1[5] + var1[7]) / 2.0F;
         float var4 = (var1[2] + var1[4]) / 2.0F;
         float var5 = (var1[3] + var1[5]) / 2.0F;
         var3[2] = (var4 + var3[4]) / 2.0F;
         var3[3] = (var5 + var3[5]) / 2.0F;
         var1[2] = (var1[0] + var1[2]) / 2.0F;
         var1[3] = (var1[1] + var1[3]) / 2.0F;
         var1[4] = (var1[2] + var4) / 2.0F;
         var1[5] = (var1[3] + var5) / 2.0F;
         var1[6] = var3[0] = (var1[4] + var3[2]) / 2.0F;
         var1[7] = var3[1] = (var1[5] + var3[3]) / 2.0F;
         ProcessMonotonicCubic(var0, var1, var2);
         ProcessMonotonicCubic(var0, var3, var2);
      }

   }

   private static void ProcessCubic(ProcessPath.ProcessHandler var0, float[] var1, int[] var2) {
      double[] var3 = new double[4];
      double[] var4 = new double[3];
      double[] var5 = new double[2];
      int var6 = 0;
      int var7;
      int var8;
      if ((var1[0] > var1[2] || var1[2] > var1[4] || var1[4] > var1[6]) && (var1[0] < var1[2] || var1[2] < var1[4] || var1[4] < var1[6])) {
         var4[2] = (double)(-var1[0] + 3.0F * var1[2] - 3.0F * var1[4] + var1[6]);
         var4[1] = (double)(2.0F * (var1[0] - 2.0F * var1[2] + var1[4]));
         var4[0] = (double)(-var1[0] + var1[2]);
         var7 = QuadCurve2D.solveQuadratic(var4, var5);

         for(var8 = 0; var8 < var7; ++var8) {
            if (var5[var8] > 0.0D && var5[var8] < 1.0D) {
               var3[var6++] = var5[var8];
            }
         }
      }

      if ((var1[1] > var1[3] || var1[3] > var1[5] || var1[5] > var1[7]) && (var1[1] < var1[3] || var1[3] < var1[5] || var1[5] < var1[7])) {
         var4[2] = (double)(-var1[1] + 3.0F * var1[3] - 3.0F * var1[5] + var1[7]);
         var4[1] = (double)(2.0F * (var1[1] - 2.0F * var1[3] + var1[5]));
         var4[0] = (double)(-var1[1] + var1[3]);
         var7 = QuadCurve2D.solveQuadratic(var4, var5);

         for(var8 = 0; var8 < var7; ++var8) {
            if (var5[var8] > 0.0D && var5[var8] < 1.0D) {
               var3[var6++] = var5[var8];
            }
         }
      }

      if (var6 > 0) {
         Arrays.sort((double[])var3, 0, var6);
         ProcessFirstMonotonicPartOfCubic(var0, var1, var2, (float)var3[0]);

         for(var7 = 1; var7 < var6; ++var7) {
            double var10 = var3[var7] - var3[var7 - 1];
            if (var10 > 0.0D) {
               ProcessFirstMonotonicPartOfCubic(var0, var1, var2, (float)(var10 / (1.0D - var3[var7 - 1])));
            }
         }
      }

      ProcessMonotonicCubic(var0, var1, var2);
   }

   private static void ProcessFirstMonotonicPartOfCubic(ProcessPath.ProcessHandler var0, float[] var1, int[] var2, float var3) {
      float[] var4 = new float[8];
      var4[0] = var1[0];
      var4[1] = var1[1];
      float var5 = var1[2] + var3 * (var1[4] - var1[2]);
      float var6 = var1[3] + var3 * (var1[5] - var1[3]);
      var4[2] = var1[0] + var3 * (var1[2] - var1[0]);
      var4[3] = var1[1] + var3 * (var1[3] - var1[1]);
      var4[4] = var4[2] + var3 * (var5 - var4[2]);
      var4[5] = var4[3] + var3 * (var6 - var4[3]);
      var1[4] += var3 * (var1[6] - var1[4]);
      var1[5] += var3 * (var1[7] - var1[5]);
      var1[2] = var5 + var3 * (var1[4] - var5);
      var1[3] = var6 + var3 * (var1[5] - var6);
      var1[0] = var4[6] = var4[4] + var3 * (var1[2] - var4[4]);
      var1[1] = var4[7] = var4[5] + var3 * (var1[3] - var4[5]);
      ProcessMonotonicCubic(var0, var4, var2);
   }

   private static void ProcessLine(ProcessPath.ProcessHandler var0, float var1, float var2, float var3, float var4, int[] var5) {
      boolean var17 = false;
      float[] var20 = new float[]{var1, var2, var3, var4, 0.0F, 0.0F};
      float var6 = var0.dhnd.xMinf;
      float var7 = var0.dhnd.yMinf;
      float var8 = var0.dhnd.xMaxf;
      float var9 = var0.dhnd.yMaxf;
      int var16 = TESTANDCLIP(var7, var9, var20, 1, 0, 3, 2);
      if (var16 != 4) {
         var17 = IS_CLIPPED(var16);
         var16 = TESTANDCLIP(var7, var9, var20, 3, 2, 1, 0);
         if (var16 != 4) {
            boolean var21 = IS_CLIPPED(var16);
            var17 = var17 || var21;
            int var10;
            int var11;
            int var12;
            int var13;
            if (var0.clipMode == 0) {
               var16 = TESTANDCLIP(var6, var8, var20, 0, 1, 2, 3);
               if (var16 == 4) {
                  return;
               }

               var17 = var17 || IS_CLIPPED(var16);
               var16 = TESTANDCLIP(var6, var8, var20, 2, 3, 0, 1);
               if (var16 == 4) {
                  return;
               }

               var21 = var21 || IS_CLIPPED(var16);
               var17 = var17 || var21;
               var10 = (int)(var20[0] * 1024.0F);
               var11 = (int)(var20[1] * 1024.0F);
               var12 = (int)(var20[2] * 1024.0F);
               var13 = (int)(var20[3] * 1024.0F);
               var0.processFixedLine(var10, var11, var12, var13, var5, var17, var21);
            } else {
               var16 = CLIPCLAMP(var6, var8, var20, 0, 1, 2, 3, 4, 5);
               var10 = (int)(var20[0] * 1024.0F);
               var11 = (int)(var20[1] * 1024.0F);
               int var14;
               int var15;
               if (var16 == 0) {
                  var14 = (int)(var20[4] * 1024.0F);
                  var15 = (int)(var20[5] * 1024.0F);
                  var0.processFixedLine(var14, var15, var10, var11, var5, false, var21);
               } else if (var16 == 4) {
                  return;
               }

               var16 = CLIPCLAMP(var6, var8, var20, 2, 3, 0, 1, 4, 5);
               var21 = var21 || var16 == 1;
               var12 = (int)(var20[2] * 1024.0F);
               var13 = (int)(var20[3] * 1024.0F);
               var0.processFixedLine(var10, var11, var12, var13, var5, false, var21);
               if (var16 == 0) {
                  var14 = (int)(var20[4] * 1024.0F);
                  var15 = (int)(var20[5] * 1024.0F);
                  var0.processFixedLine(var12, var13, var14, var15, var5, false, var21);
               }
            }

         }
      }
   }

   private static boolean doProcessPath(ProcessPath.ProcessHandler var0, Path2D.Float var1, float var2, float var3) {
      float[] var4 = new float[8];
      float[] var5 = new float[8];
      float[] var6 = new float[]{0.0F, 0.0F};
      float[] var7 = new float[2];
      int[] var8 = new int[5];
      boolean var9 = false;
      boolean var10 = false;
      var8[0] = 0;
      var0.dhnd.adjustBounds(-1048576, -1048576, 1048576, 1048576);
      if (var0.dhnd.strokeControl == 2) {
         var6[0] = -0.5F;
         var6[1] = -0.5F;
         var2 = (float)((double)var2 - 0.5D);
         var3 = (float)((double)var3 - 0.5D);
      }

      for(PathIterator var13 = var1.getPathIterator((AffineTransform)null); !var13.isDone(); var13.next()) {
         float var11;
         float var12;
         switch(var13.currentSegment(var4)) {
         case 0:
            if (var9 && !var10) {
               if (var0.clipMode == 1 && (var5[0] != var6[0] || var5[1] != var6[1])) {
                  ProcessLine(var0, var5[0], var5[1], var6[0], var6[1], var8);
               }

               var0.processEndSubPath();
            }

            var5[0] = var4[0] + var2;
            var5[1] = var4[1] + var3;
            if (var5[0] < 8.5070587E37F && var5[0] > -8.5070587E37F && var5[1] < 8.5070587E37F && var5[1] > -8.5070587E37F) {
               var9 = true;
               var10 = false;
               var6[0] = var5[0];
               var6[1] = var5[1];
            } else {
               var10 = true;
            }

            var8[0] = 0;
            break;
         case 1:
            var11 = var5[2] = var4[0] + var2;
            var12 = var5[3] = var4[1] + var3;
            if (var11 < 8.5070587E37F && var11 > -8.5070587E37F && var12 < 8.5070587E37F && var12 > -8.5070587E37F) {
               if (var10) {
                  var5[0] = var6[0] = var11;
                  var5[1] = var6[1] = var12;
                  var9 = true;
                  var10 = false;
               } else {
                  ProcessLine(var0, var5[0], var5[1], var5[2], var5[3], var8);
                  var5[0] = var11;
                  var5[1] = var12;
               }
            }
            break;
         case 2:
            var5[2] = var4[0] + var2;
            var5[3] = var4[1] + var3;
            var11 = var5[4] = var4[2] + var2;
            var12 = var5[5] = var4[3] + var3;
            if (var11 >= 8.5070587E37F || var11 <= -8.5070587E37F || var12 >= 8.5070587E37F || var12 <= -8.5070587E37F) {
               break;
            }

            if (var10) {
               var5[0] = var6[0] = var11;
               var5[1] = var6[1] = var12;
               var9 = true;
               var10 = false;
               break;
            }

            if (var5[2] < 8.5070587E37F && var5[2] > -8.5070587E37F && var5[3] < 8.5070587E37F && var5[3] > -8.5070587E37F) {
               ProcessQuad(var0, var5, var8);
            } else {
               ProcessLine(var0, var5[0], var5[1], var5[4], var5[5], var8);
            }

            var5[0] = var11;
            var5[1] = var12;
            break;
         case 3:
            var5[2] = var4[0] + var2;
            var5[3] = var4[1] + var3;
            var5[4] = var4[2] + var2;
            var5[5] = var4[3] + var3;
            var11 = var5[6] = var4[4] + var2;
            var12 = var5[7] = var4[5] + var3;
            if (var11 >= 8.5070587E37F || var11 <= -8.5070587E37F || var12 >= 8.5070587E37F || var12 <= -8.5070587E37F) {
               break;
            }

            if (var10) {
               var5[0] = var6[0] = var5[6];
               var5[1] = var6[1] = var5[7];
               var9 = true;
               var10 = false;
               break;
            }

            if (var5[2] < 8.5070587E37F && var5[2] > -8.5070587E37F && var5[3] < 8.5070587E37F && var5[3] > -8.5070587E37F && var5[4] < 8.5070587E37F && var5[4] > -8.5070587E37F && var5[5] < 8.5070587E37F && var5[5] > -8.5070587E37F) {
               ProcessCubic(var0, var5, var8);
            } else {
               ProcessLine(var0, var5[0], var5[1], var5[6], var5[7], var8);
            }

            var5[0] = var11;
            var5[1] = var12;
            break;
         case 4:
            if (var9 && !var10) {
               var10 = false;
               if (var5[0] != var6[0] || var5[1] != var6[1]) {
                  ProcessLine(var0, var5[0], var5[1], var6[0], var6[1], var8);
                  var5[0] = var6[0];
                  var5[1] = var6[1];
               }

               var0.processEndSubPath();
            }
         }
      }

      if (var9 & !var10) {
         if (var0.clipMode == 1 && (var5[0] != var6[0] || var5[1] != var6[1])) {
            ProcessLine(var0, var5[0], var5[1], var6[0], var6[1], var8);
         }

         var0.processEndSubPath();
      }

      return true;
   }

   private static void FillPolygon(ProcessPath.FillProcessHandler var0, int var1) {
      int var7 = var0.dhnd.xMax - 1;
      ProcessPath.FillData var8 = var0.fd;
      int var9 = var8.plgYMin;
      int var10 = var8.plgYMax;
      int var11 = (var10 - var9 >> 10) + 4;
      int var12 = var9 - 1 & -1024;
      int var14 = var1 == 1 ? -1 : 1;
      List var16 = var8.plgPnts;
      int var4 = var16.size();
      if (var4 > 1) {
         ProcessPath.Point[] var17 = new ProcessPath.Point[var11];
         ProcessPath.Point var18 = (ProcessPath.Point)var16.get(0);
         var18.prev = null;

         for(int var19 = 0; var19 < var4 - 1; ++var19) {
            var18 = (ProcessPath.Point)var16.get(var19);
            ProcessPath.Point var20 = (ProcessPath.Point)var16.get(var19 + 1);
            int var21 = var18.y - var12 - 1 >> 10;
            var18.nextByY = var17[var21];
            var17[var21] = var18;
            var18.next = var20;
            var20.prev = var18;
         }

         ProcessPath.Point var25 = (ProcessPath.Point)var16.get(var4 - 1);
         int var26 = var25.y - var12 - 1 >> 10;
         var25.nextByY = var17[var26];
         var17[var26] = var25;
         ProcessPath.ActiveEdgeList var27 = new ProcessPath.ActiveEdgeList();
         int var3 = var12 + 1024;

         for(int var2 = 0; var3 <= var10 && var2 < var11; ++var2) {
            for(ProcessPath.Point var22 = var17[var2]; var22 != null; var22 = var22.nextByY) {
               if (var22.prev != null && !var22.prev.lastPoint) {
                  if (var22.prev.edge != null && var22.prev.y <= var3) {
                     var27.delete(var22.prev.edge);
                     var22.prev.edge = null;
                  } else if (var22.prev.y > var3) {
                     var27.insert(var22.prev, var3);
                  }
               }

               if (!var22.lastPoint && var22.next != null) {
                  if (var22.edge != null && var22.next.y <= var3) {
                     var27.delete(var22.edge);
                     var22.edge = null;
                  } else if (var22.next.y > var3) {
                     var27.insert(var22, var3);
                  }
               }
            }

            if (!var27.isEmpty()) {
               var27.sort();
               int var13 = 0;
               boolean var5 = false;
               int var28 = var0.dhnd.xMin;

               for(ProcessPath.Edge var24 = var27.head; var24 != null; var24 = var24.next) {
                  var13 += var24.dir;
                  if ((var13 & var14) != 0 && !var5) {
                     var28 = var24.x + 1024 - 1 >> 10;
                     var5 = true;
                  }

                  if ((var13 & var14) == 0 && var5) {
                     int var23 = var24.x - 1 >> 10;
                     if (var28 <= var23) {
                        var0.dhnd.drawScanline(var28, var23, var3 >> 10);
                     }

                     var5 = false;
                  }

                  var24.x += var24.dx;
               }

               if (var5 && var28 <= var7) {
                  var0.dhnd.drawScanline(var28, var7, var3 >> 10);
               }
            }

            var3 += 1024;
         }

      }
   }

   private static class FillProcessHandler extends ProcessPath.ProcessHandler {
      ProcessPath.FillData fd = new ProcessPath.FillData();

      public void processFixedLine(int var1, int var2, int var3, int var4, int[] var5, boolean var6, boolean var7) {
         if (var6) {
            int[] var14 = new int[]{var1, var2, var3, var4, 0, 0};
            int var8 = (int)(this.dhnd.xMinf * 1024.0F);
            int var9 = (int)(this.dhnd.xMaxf * 1024.0F);
            int var10 = (int)(this.dhnd.yMinf * 1024.0F);
            int var11 = (int)(this.dhnd.yMaxf * 1024.0F);
            int var12 = ProcessPath.TESTANDCLIP(var10, var11, var14, 1, 0, 3, 2);
            if (var12 != 4) {
               var12 = ProcessPath.TESTANDCLIP(var10, var11, var14, 3, 2, 1, 0);
               if (var12 != 4) {
                  boolean var13 = ProcessPath.IS_CLIPPED(var12);
                  var12 = ProcessPath.CLIPCLAMP(var8, var9, var14, 0, 1, 2, 3, 4, 5);
                  if (var12 == 0) {
                     this.processFixedLine(var14[4], var14[5], var14[0], var14[1], var5, false, var13);
                  } else if (var12 == 4) {
                     return;
                  }

                  var12 = ProcessPath.CLIPCLAMP(var8, var9, var14, 2, 3, 0, 1, 4, 5);
                  var13 = var13 || var12 == 1;
                  this.processFixedLine(var14[0], var14[1], var14[2], var14[3], var5, false, var13);
                  if (var12 == 0) {
                     this.processFixedLine(var14[2], var14[3], var14[4], var14[5], var5, false, var13);
                  }

               }
            }
         } else {
            if (this.fd.isEmpty() || this.fd.isEnded()) {
               this.fd.addPoint(var1, var2, false);
            }

            this.fd.addPoint(var3, var4, false);
            if (var7) {
               this.fd.setEnded();
            }

         }
      }

      FillProcessHandler(ProcessPath.DrawHandler var1) {
         super(var1, 1);
      }

      public void processEndSubPath() {
         if (!this.fd.isEmpty()) {
            this.fd.setEnded();
         }

      }
   }

   private static class ActiveEdgeList {
      ProcessPath.Edge head;

      private ActiveEdgeList() {
      }

      public boolean isEmpty() {
         return this.head == null;
      }

      public void insert(ProcessPath.Point var1, int var2) {
         ProcessPath.Point var3 = var1.next;
         int var4 = var1.x;
         int var5 = var1.y;
         int var6 = var3.x;
         int var7 = var3.y;
         if (var5 != var7) {
            int var9 = var6 - var4;
            int var10 = var7 - var5;
            int var12;
            int var13;
            byte var14;
            if (var5 < var7) {
               var12 = var4;
               var13 = var2 - var5;
               var14 = -1;
            } else {
               var12 = var6;
               var13 = var2 - var7;
               var14 = 1;
            }

            int var11;
            if ((float)var9 <= 1048576.0F && (float)var9 >= -1048576.0F) {
               var11 = (var9 << 10) / var10;
               var12 += var9 * var13 / var10;
            } else {
               var11 = (int)((double)var9 * 1024.0D / (double)var10);
               var12 += (int)((double)var9 * (double)var13 / (double)var10);
            }

            ProcessPath.Edge var8 = new ProcessPath.Edge(var1, var12, var11, var14);
            var8.next = this.head;
            var8.prev = null;
            if (this.head != null) {
               this.head.prev = var8;
            }

            this.head = var1.edge = var8;
         }
      }

      public void delete(ProcessPath.Edge var1) {
         ProcessPath.Edge var2 = var1.prev;
         ProcessPath.Edge var3 = var1.next;
         if (var2 != null) {
            var2.next = var3;
         } else {
            this.head = var3;
         }

         if (var3 != null) {
            var3.prev = var2;
         }

      }

      public void sort() {
         ProcessPath.Edge var4 = null;
         boolean var6 = true;

         ProcessPath.Edge var1;
         ProcessPath.Edge var2;
         while(var4 != this.head.next && var6) {
            ProcessPath.Edge var3 = var1 = this.head;
            var2 = var1.next;
            var6 = false;

            while(var1 != var4) {
               if (var1.x >= var2.x) {
                  var6 = true;
                  ProcessPath.Edge var5;
                  if (var1 == this.head) {
                     var5 = var2.next;
                     var2.next = var1;
                     var1.next = var5;
                     this.head = var2;
                     var3 = var2;
                  } else {
                     var5 = var2.next;
                     var2.next = var1;
                     var1.next = var5;
                     var3.next = var2;
                     var3 = var2;
                  }
               } else {
                  var3 = var1;
                  var1 = var1.next;
               }

               var2 = var1.next;
               if (var2 == var4) {
                  var4 = var1;
               }
            }
         }

         var1 = this.head;

         for(var2 = null; var1 != null; var1 = var1.next) {
            var1.prev = var2;
            var2 = var1;
         }

      }

      // $FF: synthetic method
      ActiveEdgeList(Object var1) {
         this();
      }
   }

   private static class FillData {
      List<ProcessPath.Point> plgPnts = new Vector(256);
      public int plgYMin;
      public int plgYMax;

      public FillData() {
      }

      public void addPoint(int var1, int var2, boolean var3) {
         if (this.plgPnts.size() == 0) {
            this.plgYMin = this.plgYMax = var2;
         } else {
            this.plgYMin = this.plgYMin > var2 ? var2 : this.plgYMin;
            this.plgYMax = this.plgYMax < var2 ? var2 : this.plgYMax;
         }

         this.plgPnts.add(new ProcessPath.Point(var1, var2, var3));
      }

      public boolean isEmpty() {
         return this.plgPnts.size() == 0;
      }

      public boolean isEnded() {
         return ((ProcessPath.Point)this.plgPnts.get(this.plgPnts.size() - 1)).lastPoint;
      }

      public boolean setEnded() {
         return ((ProcessPath.Point)this.plgPnts.get(this.plgPnts.size() - 1)).lastPoint = true;
      }
   }

   private static class Edge {
      int x;
      int dx;
      ProcessPath.Point p;
      int dir;
      ProcessPath.Edge prev;
      ProcessPath.Edge next;

      public Edge(ProcessPath.Point var1, int var2, int var3, int var4) {
         this.p = var1;
         this.x = var2;
         this.dx = var3;
         this.dir = var4;
      }
   }

   private static class Point {
      public int x;
      public int y;
      public boolean lastPoint;
      public ProcessPath.Point prev;
      public ProcessPath.Point next;
      public ProcessPath.Point nextByY;
      public ProcessPath.Edge edge;

      public Point(int var1, int var2, boolean var3) {
         this.x = var1;
         this.y = var2;
         this.lastPoint = var3;
      }
   }

   private static class DrawProcessHandler extends ProcessPath.ProcessHandler {
      ProcessPath.EndSubPathHandler processESP;

      public DrawProcessHandler(ProcessPath.DrawHandler var1, ProcessPath.EndSubPathHandler var2) {
         super(var1, 0);
         this.dhnd = var1;
         this.processESP = var2;
      }

      public void processEndSubPath() {
         this.processESP.processEndSubPath();
      }

      void PROCESS_LINE(int var1, int var2, int var3, int var4, boolean var5, int[] var6) {
         int var7 = var1 >> 10;
         int var8 = var2 >> 10;
         int var9 = var3 >> 10;
         int var10 = var4 >> 10;
         if ((var7 ^ var9 | var8 ^ var10) == 0) {
            if (!var5 || this.dhnd.yMin <= var8 && this.dhnd.yMax > var8 && this.dhnd.xMin <= var7 && this.dhnd.xMax > var7) {
               if (var6[0] == 0) {
                  var6[0] = 1;
                  var6[1] = var7;
                  var6[2] = var8;
                  var6[3] = var7;
                  var6[4] = var8;
                  this.dhnd.drawPixel(var7, var8);
               } else if ((var7 != var6[3] || var8 != var6[4]) && (var7 != var6[1] || var8 != var6[2])) {
                  this.dhnd.drawPixel(var7, var8);
                  var6[3] = var7;
                  var6[4] = var8;
               }

            }
         } else {
            if ((!var5 || this.dhnd.yMin <= var8 && this.dhnd.yMax > var8 && this.dhnd.xMin <= var7 && this.dhnd.xMax > var7) && var6[0] == 1 && (var6[1] == var7 && var6[2] == var8 || var6[3] == var7 && var6[4] == var8)) {
               this.dhnd.drawPixel(var7, var8);
            }

            this.dhnd.drawLine(var7, var8, var9, var10);
            if (var6[0] == 0) {
               var6[0] = 1;
               var6[1] = var7;
               var6[2] = var8;
               var6[3] = var7;
               var6[4] = var8;
            }

            if (var6[1] == var9 && var6[2] == var10 || var6[3] == var9 && var6[4] == var10) {
               if (var5 && (this.dhnd.yMin > var10 || this.dhnd.yMax <= var10 || this.dhnd.xMin > var9 || this.dhnd.xMax <= var9)) {
                  return;
               }

               this.dhnd.drawPixel(var9, var10);
            }

            var6[3] = var9;
            var6[4] = var10;
         }
      }

      void PROCESS_POINT(int var1, int var2, boolean var3, int[] var4) {
         int var5 = var1 >> 10;
         int var6 = var2 >> 10;
         if (!var3 || this.dhnd.yMin <= var6 && this.dhnd.yMax > var6 && this.dhnd.xMin <= var5 && this.dhnd.xMax > var5) {
            if (var4[0] == 0) {
               var4[0] = 1;
               var4[1] = var5;
               var4[2] = var6;
               var4[3] = var5;
               var4[4] = var6;
               this.dhnd.drawPixel(var5, var6);
            } else if ((var5 != var4[3] || var6 != var4[4]) && (var5 != var4[1] || var6 != var4[2])) {
               this.dhnd.drawPixel(var5, var6);
               var4[3] = var5;
               var4[4] = var6;
            }

         }
      }

      public void processFixedLine(int var1, int var2, int var3, int var4, int[] var5, boolean var6, boolean var7) {
         int var8 = var1 ^ var3 | var2 ^ var4;
         if ((var8 & -1024) == 0) {
            if (var8 == 0) {
               this.PROCESS_POINT(var1 + 512, var2 + 512, var6, var5);
            }

         } else {
            int var9;
            int var10;
            int var11;
            int var12;
            if (var1 != var3 && var2 != var4) {
               int var13 = var3 - var1;
               int var14 = var4 - var2;
               int var15 = var1 & -1024;
               int var16 = var2 & -1024;
               int var17 = var3 & -1024;
               int var18 = var4 & -1024;
               int var19;
               int var20;
               int var21;
               if (var15 != var1 && var16 != var2) {
                  var19 = var1 < var3 ? var15 + 1024 : var15;
                  var20 = var2 < var4 ? var16 + 1024 : var16;
                  var21 = var2 + (var19 - var1) * var14 / var13;
                  if (var21 >= var16 && var21 <= var16 + 1024) {
                     var9 = var19;
                     var10 = var21 + 512;
                  } else {
                     var21 = var1 + (var20 - var2) * var13 / var14;
                     var9 = var21 + 512;
                     var10 = var20;
                  }
               } else {
                  var9 = var1 + 512;
                  var10 = var2 + 512;
               }

               if (var17 != var3 && var18 != var4) {
                  var19 = var1 > var3 ? var17 + 1024 : var17;
                  var20 = var2 > var4 ? var18 + 1024 : var18;
                  var21 = var4 + (var19 - var3) * var14 / var13;
                  if (var21 >= var18 && var21 <= var18 + 1024) {
                     var11 = var19;
                     var12 = var21 + 512;
                  } else {
                     var21 = var3 + (var20 - var4) * var13 / var14;
                     var11 = var21 + 512;
                     var12 = var20;
                  }
               } else {
                  var11 = var3 + 512;
                  var12 = var4 + 512;
               }
            } else {
               var9 = var1 + 512;
               var11 = var3 + 512;
               var10 = var2 + 512;
               var12 = var4 + 512;
            }

            this.PROCESS_LINE(var9, var10, var11, var12, var6, var5);
         }
      }
   }

   public abstract static class ProcessHandler implements ProcessPath.EndSubPathHandler {
      ProcessPath.DrawHandler dhnd;
      int clipMode;

      public ProcessHandler(ProcessPath.DrawHandler var1, int var2) {
         this.dhnd = var1;
         this.clipMode = var2;
      }

      public abstract void processFixedLine(int var1, int var2, int var3, int var4, int[] var5, boolean var6, boolean var7);
   }

   public interface EndSubPathHandler {
      void processEndSubPath();
   }

   public abstract static class DrawHandler {
      public int xMin;
      public int yMin;
      public int xMax;
      public int yMax;
      public float xMinf;
      public float yMinf;
      public float xMaxf;
      public float yMaxf;
      public int strokeControl;

      public DrawHandler(int var1, int var2, int var3, int var4, int var5) {
         this.setBounds(var1, var2, var3, var4, var5);
      }

      public void setBounds(int var1, int var2, int var3, int var4) {
         this.xMin = var1;
         this.yMin = var2;
         this.xMax = var3;
         this.yMax = var4;
         this.xMinf = (float)var1 - 0.5F;
         this.yMinf = (float)var2 - 0.5F;
         this.xMaxf = (float)var3 - 0.5F - 9.765625E-4F;
         this.yMaxf = (float)var4 - 0.5F - 9.765625E-4F;
      }

      public void setBounds(int var1, int var2, int var3, int var4, int var5) {
         this.strokeControl = var5;
         this.setBounds(var1, var2, var3, var4);
      }

      public void adjustBounds(int var1, int var2, int var3, int var4) {
         if (this.xMin > var1) {
            var1 = this.xMin;
         }

         if (this.xMax < var3) {
            var3 = this.xMax;
         }

         if (this.yMin > var2) {
            var2 = this.yMin;
         }

         if (this.yMax < var4) {
            var4 = this.yMax;
         }

         this.setBounds(var1, var2, var3, var4);
      }

      public DrawHandler(int var1, int var2, int var3, int var4) {
         this(var1, var2, var3, var4, 0);
      }

      public abstract void drawLine(int var1, int var2, int var3, int var4);

      public abstract void drawPixel(int var1, int var2);

      public abstract void drawScanline(int var1, int var2, int var3);
   }
}
