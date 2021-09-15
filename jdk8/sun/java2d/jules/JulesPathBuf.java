package sun.java2d.jules;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.X11GraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.java2d.xr.GrowableByteArray;
import sun.java2d.xr.GrowablePointArray;

public class JulesPathBuf {
   static final double[] emptyDash = new double[0];
   private static final byte CAIRO_PATH_OP_MOVE_TO = 0;
   private static final byte CAIRO_PATH_OP_LINE_TO = 1;
   private static final byte CAIRO_PATH_OP_CURVE_TO = 2;
   private static final byte CAIRO_PATH_OP_CLOSE_PATH = 3;
   private static final int CAIRO_FILL_RULE_WINDING = 0;
   private static final int CAIRO_FILL_RULE_EVEN_ODD = 1;
   GrowablePointArray points = new GrowablePointArray(128);
   GrowableByteArray ops = new GrowableByteArray(1, 128);
   int[] xTrapArray = new int[512];
   private static final boolean isCairoAvailable = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         boolean var1 = false;
         if (X11GraphicsEnvironment.isXRenderAvailable()) {
            try {
               System.loadLibrary("jules");
               var1 = true;
               if (X11GraphicsEnvironment.isXRenderVerbose()) {
                  System.out.println("Xrender: INFO: Jules library loaded");
               }
            } catch (UnsatisfiedLinkError var3) {
               var1 = false;
               if (X11GraphicsEnvironment.isXRenderVerbose()) {
                  System.out.println("Xrender: INFO: Jules library not installed.");
               }
            }
         }

         return var1;
      }
   });

   public static boolean isCairoAvailable() {
      return isCairoAvailable;
   }

   public TrapezoidList tesselateFill(Shape var1, AffineTransform var2, Region var3) {
      int var4 = this.convertPathData(var1, var2);
      this.xTrapArray[0] = 0;
      this.xTrapArray = tesselateFillNative(this.points.getArray(), this.ops.getArray(), this.points.getSize(), this.ops.getSize(), this.xTrapArray, this.xTrapArray.length, getCairoWindingRule(var4), var3.getLoX(), var3.getLoY(), var3.getHiX(), var3.getHiY());
      return new TrapezoidList(this.xTrapArray);
   }

   public TrapezoidList tesselateStroke(Shape var1, BasicStroke var2, boolean var3, boolean var4, boolean var5, AffineTransform var6, Region var7) {
      float var8;
      if (var3) {
         if (var5) {
            var8 = 0.5F;
         } else {
            var8 = 1.0F;
         }
      } else {
         var8 = var2.getLineWidth();
      }

      this.convertPathData(var1, var6);
      double[] var9 = this.floatToDoubleArray(var2.getDashArray());
      this.xTrapArray[0] = 0;
      this.xTrapArray = tesselateStrokeNative(this.points.getArray(), this.ops.getArray(), this.points.getSize(), this.ops.getSize(), this.xTrapArray, this.xTrapArray.length, (double)var8, var2.getEndCap(), var2.getLineJoin(), (double)var2.getMiterLimit(), var9, var9.length, (double)var2.getDashPhase(), 1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, var7.getLoX(), var7.getLoY(), var7.getHiX(), var7.getHiY());
      return new TrapezoidList(this.xTrapArray);
   }

   protected double[] floatToDoubleArray(float[] var1) {
      double[] var2 = emptyDash;
      if (var1 != null) {
         var2 = new double[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = (double)var1[var3];
         }
      }

      return var2;
   }

   protected int convertPathData(Shape var1, AffineTransform var2) {
      PathIterator var3 = var1.getPathIterator(var2);
      double[] var4 = new double[6];
      double var5 = 0.0D;

      for(double var7 = 0.0D; !var3.isDone(); var3.next()) {
         int var9 = var3.currentSegment(var4);
         int var10;
         switch(var9) {
         case 0:
            this.ops.addByte((byte)0);
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var4[0]));
            this.points.setY(var10, DoubleToCairoFixed(var4[1]));
            var5 = var4[0];
            var7 = var4[1];
            break;
         case 1:
            this.ops.addByte((byte)1);
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var4[0]));
            this.points.setY(var10, DoubleToCairoFixed(var4[1]));
            var5 = var4[0];
            var7 = var4[1];
            break;
         case 2:
            double var11 = var4[0];
            double var13 = var4[1];
            double var19 = var4[2];
            double var21 = var4[3];
            double var15 = var11 + (var19 - var11) / 3.0D;
            double var17 = var13 + (var21 - var13) / 3.0D;
            var11 = var5 + 2.0D * (var11 - var5) / 3.0D;
            var13 = var7 + 2.0D * (var13 - var7) / 3.0D;
            this.ops.addByte((byte)2);
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var11));
            this.points.setY(var10, DoubleToCairoFixed(var13));
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var15));
            this.points.setY(var10, DoubleToCairoFixed(var17));
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var19));
            this.points.setY(var10, DoubleToCairoFixed(var21));
            var5 = var19;
            var7 = var21;
            break;
         case 3:
            this.ops.addByte((byte)2);
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var4[0]));
            this.points.setY(var10, DoubleToCairoFixed(var4[1]));
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var4[2]));
            this.points.setY(var10, DoubleToCairoFixed(var4[3]));
            var10 = this.points.getNextIndex();
            this.points.setX(var10, DoubleToCairoFixed(var4[4]));
            this.points.setY(var10, DoubleToCairoFixed(var4[5]));
            var5 = var4[4];
            var7 = var4[5];
            break;
         case 4:
            this.ops.addByte((byte)3);
         }
      }

      return var3.getWindingRule();
   }

   private static native int[] tesselateStrokeNative(int[] var0, byte[] var1, int var2, int var3, int[] var4, int var5, double var6, int var8, int var9, double var10, double[] var12, int var13, double var14, double var16, double var18, double var20, double var22, double var24, double var26, int var28, int var29, int var30, int var31);

   private static native int[] tesselateFillNative(int[] var0, byte[] var1, int var2, int var3, int[] var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public void clear() {
      this.points.clear();
      this.ops.clear();
      this.xTrapArray[0] = 0;
   }

   private static int DoubleToCairoFixed(double var0) {
      return (int)(var0 * 256.0D);
   }

   private static int getCairoWindingRule(int var0) {
      switch(var0) {
      case 0:
         return 1;
      case 1:
         return 0;
      default:
         throw new IllegalArgumentException("Illegal Java2D winding rule specified");
      }
   }
}
