package sun.java2d.xr;

import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;
import sun.java2d.loops.SurfaceType;

public class XRUtils {
   public static final int None = 0;
   public static final byte PictOpClear = 0;
   public static final byte PictOpSrc = 1;
   public static final byte PictOpDst = 2;
   public static final byte PictOpOver = 3;
   public static final byte PictOpOverReverse = 4;
   public static final byte PictOpIn = 5;
   public static final byte PictOpInReverse = 6;
   public static final byte PictOpOut = 7;
   public static final byte PictOpOutReverse = 8;
   public static final byte PictOpAtop = 9;
   public static final byte PictOpAtopReverse = 10;
   public static final byte PictOpXor = 11;
   public static final byte PictOpAdd = 12;
   public static final byte PictOpSaturate = 13;
   public static final int RepeatNone = 0;
   public static final int RepeatNormal = 1;
   public static final int RepeatPad = 2;
   public static final int RepeatReflect = 3;
   public static final int FAST = 0;
   public static final int GOOD = 1;
   public static final int BEST = 2;
   public static final byte[] FAST_NAME = "fast".getBytes();
   public static final byte[] GOOD_NAME = "good".getBytes();
   public static final byte[] BEST_NAME = "best".getBytes();
   public static final int PictStandardARGB32 = 0;
   public static final int PictStandardRGB24 = 1;
   public static final int PictStandardA8 = 2;
   public static final int PictStandardA4 = 3;
   public static final int PictStandardA1 = 4;

   public static int ATransOpToXRQuality(int var0) {
      switch(var0) {
      case 1:
         return 0;
      case 2:
         return 1;
      case 3:
         return 2;
      default:
         return -1;
      }
   }

   public static byte[] ATransOpToXRQualityName(int var0) {
      switch(var0) {
      case 1:
         return FAST_NAME;
      case 2:
         return GOOD_NAME;
      case 3:
         return BEST_NAME;
      default:
         return null;
      }
   }

   public static byte[] getFilterName(int var0) {
      switch(var0) {
      case 0:
         return FAST_NAME;
      case 1:
         return GOOD_NAME;
      case 2:
         return BEST_NAME;
      default:
         return null;
      }
   }

   public static int getPictureFormatForTransparency(int var0) {
      switch(var0) {
      case 1:
         return 1;
      case 2:
      case 3:
         return 0;
      default:
         return -1;
      }
   }

   public static SurfaceType getXRSurfaceTypeForTransparency(int var0) {
      return var0 == 1 ? SurfaceType.IntRgb : SurfaceType.IntArgbPre;
   }

   public static int getRepeatForCycleMethod(MultipleGradientPaint.CycleMethod var0) {
      if (var0.equals(MultipleGradientPaint.CycleMethod.NO_CYCLE)) {
         return 2;
      } else if (var0.equals(MultipleGradientPaint.CycleMethod.REFLECT)) {
         return 3;
      } else {
         return var0.equals(MultipleGradientPaint.CycleMethod.REPEAT) ? 1 : 0;
      }
   }

   public static int XDoubleToFixed(double var0) {
      return (int)(var0 * 65536.0D);
   }

   public static double XFixedToDouble(int var0) {
      return (double)var0 / 65536.0D;
   }

   public static int[] convertFloatsToFixed(float[] var0) {
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = XDoubleToFixed((double)var0[var2]);
      }

      return var1;
   }

   public static long intToULong(int var0) {
      return var0 < 0 ? (long)var0 + 4294967296L : (long)var0;
   }

   public static byte j2dAlphaCompToXR(int var0) {
      switch(var0) {
      case 1:
         return 0;
      case 2:
         return 1;
      case 3:
         return 3;
      case 4:
         return 4;
      case 5:
         return 5;
      case 6:
         return 6;
      case 7:
         return 7;
      case 8:
         return 8;
      case 9:
         return 2;
      case 10:
         return 9;
      case 11:
         return 10;
      case 12:
         return 11;
      default:
         throw new InternalError("No XRender equivalent available for requested java2d composition rule: " + var0);
      }
   }

   public static short clampToShort(int var0) {
      return (short)(var0 > 32767 ? 32767 : (var0 < -32768 ? -32768 : var0));
   }

   public static int clampToUShort(int var0) {
      return var0 > 65535 ? '\uffff' : (var0 < 0 ? 0 : var0);
   }

   public static boolean isTransformQuadrantRotated(AffineTransform var0) {
      return (var0.getType() & 48) == 0;
   }

   public static boolean isMaskEvaluated(byte var0) {
      switch(var0) {
      case 3:
      case 4:
      case 9:
      case 11:
         return true;
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      default:
         return false;
      }
   }
}
