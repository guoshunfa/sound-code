package sun.java2d.pisces;

import java.util.Arrays;
import java.util.Iterator;
import sun.awt.geom.PathConsumer2D;

final class Stroker implements PathConsumer2D {
   private static final int MOVE_TO = 0;
   private static final int DRAWING_OP_TO = 1;
   private static final int CLOSE = 2;
   public static final int JOIN_MITER = 0;
   public static final int JOIN_ROUND = 1;
   public static final int JOIN_BEVEL = 2;
   public static final int CAP_BUTT = 0;
   public static final int CAP_ROUND = 1;
   public static final int CAP_SQUARE = 2;
   private final PathConsumer2D out;
   private final int capStyle;
   private final int joinStyle;
   private final float lineWidth2;
   private final float[][] offset = new float[3][2];
   private final float[] miter = new float[2];
   private final float miterLimitSq;
   private int prev;
   private float sx0;
   private float sy0;
   private float sdx;
   private float sdy;
   private float cx0;
   private float cy0;
   private float cdx;
   private float cdy;
   private float smx;
   private float smy;
   private float cmx;
   private float cmy;
   private final Stroker.PolyStack reverse = new Stroker.PolyStack();
   private static final float ROUND_JOIN_THRESHOLD = 0.015258789F;
   private float[] middle = new float[16];
   private float[] lp = new float[8];
   private float[] rp = new float[8];
   private static final int MAX_N_CURVES = 11;
   private float[] subdivTs = new float[10];
   private static Curve c = new Curve();

   public Stroker(PathConsumer2D var1, float var2, int var3, int var4, float var5) {
      this.out = var1;
      this.lineWidth2 = var2 / 2.0F;
      this.capStyle = var3;
      this.joinStyle = var4;
      float var6 = var5 * this.lineWidth2;
      this.miterLimitSq = var6 * var6;
      this.prev = 2;
   }

   private static void computeOffset(float var0, float var1, float var2, float[] var3) {
      float var4 = (float)Math.sqrt((double)(var0 * var0 + var1 * var1));
      if (var4 == 0.0F) {
         var3[0] = var3[1] = 0.0F;
      } else {
         var3[0] = var1 * var2 / var4;
         var3[1] = -(var0 * var2) / var4;
      }

   }

   private static boolean isCW(float var0, float var1, float var2, float var3) {
      return var0 * var3 <= var1 * var2;
   }

   private void drawRoundJoin(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8) {
      if ((var3 != 0.0F || var4 != 0.0F) && (var5 != 0.0F || var6 != 0.0F)) {
         float var9 = var3 - var5;
         float var10 = var4 - var6;
         float var11 = var9 * var9 + var10 * var10;
         if (var11 >= var8) {
            if (var7) {
               var3 = -var3;
               var4 = -var4;
               var5 = -var5;
               var6 = -var6;
            }

            this.drawRoundJoin(var1, var2, var3, var4, var5, var6, var7);
         }
      }
   }

   private void drawRoundJoin(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7) {
      double var8 = (double)(var3 * var5 + var4 * var6);
      int var10 = var8 >= 0.0D ? 1 : 2;
      switch(var10) {
      case 1:
         this.drawBezApproxForArc(var1, var2, var3, var4, var5, var6, var7);
         break;
      case 2:
         float var11 = var6 - var4;
         float var12 = var3 - var5;
         float var13 = (float)Math.sqrt((double)(var11 * var11 + var12 * var12));
         float var14 = this.lineWidth2 / var13;
         float var15 = var11 * var14;
         float var16 = var12 * var14;
         if (var7) {
            var15 = -var15;
            var16 = -var16;
         }

         this.drawBezApproxForArc(var1, var2, var3, var4, var15, var16, var7);
         this.drawBezApproxForArc(var1, var2, var15, var16, var5, var6, var7);
      }

   }

   private void drawBezApproxForArc(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7) {
      float var8 = (var3 * var5 + var4 * var6) / (2.0F * this.lineWidth2 * this.lineWidth2);
      float var9 = (float)(1.3333333333333333D * Math.sqrt(0.5D - (double)var8) / (1.0D + Math.sqrt((double)var8 + 0.5D)));
      if (var7) {
         var9 = -var9;
      }

      float var10 = var1 + var3;
      float var11 = var2 + var4;
      float var12 = var10 - var9 * var4;
      float var13 = var11 + var9 * var3;
      float var14 = var1 + var5;
      float var15 = var2 + var6;
      float var16 = var14 + var9 * var6;
      float var17 = var15 - var9 * var5;
      this.emitCurveTo(var10, var11, var12, var13, var16, var17, var14, var15, var7);
   }

   private void drawRoundCap(float var1, float var2, float var3, float var4) {
      this.emitCurveTo(var1 + var3, var2 + var4, var1 + var3 - 0.5522848F * var4, var2 + var4 + 0.5522848F * var3, var1 - var4 + 0.5522848F * var3, var2 + var3 + 0.5522848F * var4, var1 - var4, var2 + var3, false);
      this.emitCurveTo(var1 - var4, var2 + var3, var1 - var4 - 0.5522848F * var3, var2 + var3 - 0.5522848F * var4, var1 - var3 - 0.5522848F * var4, var2 - var4 + 0.5522848F * var3, var1 - var3, var2 - var4, false);
   }

   private void computeIntersection(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float[] var9, int var10) {
      float var11 = var3 - var1;
      float var12 = var4 - var2;
      float var13 = var7 - var5;
      float var14 = var8 - var6;
      float var15 = var11 * var14 - var13 * var12;
      float var16 = var13 * (var2 - var6) - var14 * (var1 - var5);
      var16 /= var15;
      var9[var10++] = var1 + var16 * var11;
      var9[var10] = var2 + var16 * var12;
   }

   private void drawMiter(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, boolean var11) {
      if ((var9 != var7 || var10 != var8) && (var1 != 0.0F || var2 != 0.0F) && (var5 != 0.0F || var6 != 0.0F)) {
         if (var11) {
            var7 = -var7;
            var8 = -var8;
            var9 = -var9;
            var10 = -var10;
         }

         this.computeIntersection(var3 - var1 + var7, var4 - var2 + var8, var3 + var7, var4 + var8, var5 + var3 + var9, var6 + var4 + var10, var3 + var9, var4 + var10, this.miter, 0);
         float var12 = (this.miter[0] - var3) * (this.miter[0] - var3) + (this.miter[1] - var4) * (this.miter[1] - var4);
         if (var12 < this.miterLimitSq) {
            this.emitLineTo(this.miter[0], this.miter[1], var11);
         }

      }
   }

   public void moveTo(float var1, float var2) {
      if (this.prev == 1) {
         this.finish();
      }

      this.sx0 = this.cx0 = var1;
      this.sy0 = this.cy0 = var2;
      this.cdx = this.sdx = 1.0F;
      this.cdy = this.sdy = 0.0F;
      this.prev = 0;
   }

   public void lineTo(float var1, float var2) {
      float var3 = var1 - this.cx0;
      float var4 = var2 - this.cy0;
      if (var3 == 0.0F && var4 == 0.0F) {
         var3 = 1.0F;
      }

      computeOffset(var3, var4, this.lineWidth2, this.offset[0]);
      float var5 = this.offset[0][0];
      float var6 = this.offset[0][1];
      this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var3, var4, this.cmx, this.cmy, var5, var6);
      this.emitLineTo(this.cx0 + var5, this.cy0 + var6);
      this.emitLineTo(var1 + var5, var2 + var6);
      this.emitLineTo(this.cx0 - var5, this.cy0 - var6, true);
      this.emitLineTo(var1 - var5, var2 - var6, true);
      this.cmx = var5;
      this.cmy = var6;
      this.cdx = var3;
      this.cdy = var4;
      this.cx0 = var1;
      this.cy0 = var2;
      this.prev = 1;
   }

   public void closePath() {
      if (this.prev != 1) {
         if (this.prev != 2) {
            this.emitMoveTo(this.cx0, this.cy0 - this.lineWidth2);
            this.cmx = this.smx = 0.0F;
            this.cmy = this.smy = -this.lineWidth2;
            this.cdx = this.sdx = 1.0F;
            this.cdy = this.sdy = 0.0F;
            this.finish();
         }
      } else {
         if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
            this.lineTo(this.sx0, this.sy0);
         }

         this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, this.sdx, this.sdy, this.cmx, this.cmy, this.smx, this.smy);
         this.emitLineTo(this.sx0 + this.smx, this.sy0 + this.smy);
         this.emitMoveTo(this.sx0 - this.smx, this.sy0 - this.smy);
         this.emitReverse();
         this.prev = 2;
         this.emitClose();
      }
   }

   private void emitReverse() {
      while(!this.reverse.isEmpty()) {
         this.reverse.pop(this.out);
      }

   }

   public void pathDone() {
      if (this.prev == 1) {
         this.finish();
      }

      this.out.pathDone();
      this.prev = 2;
   }

   private void finish() {
      if (this.capStyle == 1) {
         this.drawRoundCap(this.cx0, this.cy0, this.cmx, this.cmy);
      } else if (this.capStyle == 2) {
         this.emitLineTo(this.cx0 - this.cmy + this.cmx, this.cy0 + this.cmx + this.cmy);
         this.emitLineTo(this.cx0 - this.cmy - this.cmx, this.cy0 + this.cmx - this.cmy);
      }

      this.emitReverse();
      if (this.capStyle == 1) {
         this.drawRoundCap(this.sx0, this.sy0, -this.smx, -this.smy);
      } else if (this.capStyle == 2) {
         this.emitLineTo(this.sx0 + this.smy - this.smx, this.sy0 - this.smx - this.smy);
         this.emitLineTo(this.sx0 + this.smy + this.smx, this.sy0 - this.smx + this.smy);
      }

      this.emitClose();
   }

   private void emitMoveTo(float var1, float var2) {
      this.out.moveTo(var1, var2);
   }

   private void emitLineTo(float var1, float var2) {
      this.out.lineTo(var1, var2);
   }

   private void emitLineTo(float var1, float var2, boolean var3) {
      if (var3) {
         this.reverse.pushLine(var1, var2);
      } else {
         this.emitLineTo(var1, var2);
      }

   }

   private void emitQuadTo(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7) {
      if (var7) {
         this.reverse.pushQuad(var1, var2, var3, var4);
      } else {
         this.out.quadTo(var3, var4, var5, var6);
      }

   }

   private void emitCurveTo(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9) {
      if (var9) {
         this.reverse.pushCubic(var1, var2, var3, var4, var5, var6);
      } else {
         this.out.curveTo(var3, var4, var5, var6, var7, var8);
      }

   }

   private void emitClose() {
      this.out.closePath();
   }

   private void drawJoin(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (this.prev != 1) {
         this.emitMoveTo(var3 + var9, var4 + var10);
         this.sdx = var5;
         this.sdy = var6;
         this.smx = var9;
         this.smy = var10;
      } else {
         boolean var11 = isCW(var1, var2, var5, var6);
         if (this.joinStyle == 0) {
            this.drawMiter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         } else if (this.joinStyle == 1) {
            this.drawRoundJoin(var3, var4, var7, var8, var9, var10, var11, 0.015258789F);
         }

         this.emitLineTo(var3, var4, !var11);
      }

      this.prev = 1;
   }

   private static boolean within(float var0, float var1, float var2, float var3, float var4) {
      assert var4 > 0.0F : "";

      return Helpers.within(var0, var2, var4) && Helpers.within(var1, var3, var4);
   }

   private void getLineOffsets(float var1, float var2, float var3, float var4, float[] var5, float[] var6) {
      computeOffset(var3 - var1, var4 - var2, this.lineWidth2, this.offset[0]);
      var5[0] = var1 + this.offset[0][0];
      var5[1] = var2 + this.offset[0][1];
      var5[2] = var3 + this.offset[0][0];
      var5[3] = var4 + this.offset[0][1];
      var6[0] = var1 - this.offset[0][0];
      var6[1] = var2 - this.offset[0][1];
      var6[2] = var3 - this.offset[0][0];
      var6[3] = var4 - this.offset[0][1];
   }

   private int computeOffsetCubic(float[] var1, int var2, float[] var3, float[] var4) {
      float var5 = var1[var2 + 0];
      float var6 = var1[var2 + 1];
      float var7 = var1[var2 + 2];
      float var8 = var1[var2 + 3];
      float var9 = var1[var2 + 4];
      float var10 = var1[var2 + 5];
      float var11 = var1[var2 + 6];
      float var12 = var1[var2 + 7];
      float var13 = var11 - var9;
      float var14 = var12 - var10;
      float var15 = var7 - var5;
      float var16 = var8 - var6;
      boolean var17 = within(var5, var6, var7, var8, 6.0F * Math.ulp(var8));
      boolean var18 = within(var9, var10, var11, var12, 6.0F * Math.ulp(var12));
      if (var17 && var18) {
         this.getLineOffsets(var5, var6, var11, var12, var3, var4);
         return 4;
      } else {
         if (var17) {
            var15 = var9 - var5;
            var16 = var10 - var6;
         } else if (var18) {
            var13 = var11 - var7;
            var14 = var12 - var8;
         }

         float var19 = var15 * var13 + var16 * var14;
         var19 *= var19;
         float var20 = var15 * var15 + var16 * var16;
         float var21 = var13 * var13 + var14 * var14;
         if (Helpers.within(var19, var20 * var21, 4.0F * Math.ulp(var19))) {
            this.getLineOffsets(var5, var6, var11, var12, var3, var4);
            return 4;
         } else {
            float var22 = 0.125F * (var5 + 3.0F * (var7 + var9) + var11);
            float var23 = 0.125F * (var6 + 3.0F * (var8 + var10) + var12);
            float var24 = var9 + var11 - var5 - var7;
            float var25 = var10 + var12 - var6 - var8;
            computeOffset(var15, var16, this.lineWidth2, this.offset[0]);
            computeOffset(var24, var25, this.lineWidth2, this.offset[1]);
            computeOffset(var13, var14, this.lineWidth2, this.offset[2]);
            float var26 = var5 + this.offset[0][0];
            float var27 = var6 + this.offset[0][1];
            float var28 = var22 + this.offset[1][0];
            float var29 = var23 + this.offset[1][1];
            float var30 = var11 + this.offset[2][0];
            float var31 = var12 + this.offset[2][1];
            float var32 = 4.0F / (3.0F * (var15 * var14 - var16 * var13));
            float var33 = 2.0F * var28 - var26 - var30;
            float var34 = 2.0F * var29 - var27 - var31;
            float var35 = var32 * (var14 * var33 - var13 * var34);
            float var36 = var32 * (var15 * var34 - var16 * var33);
            float var37 = var26 + var35 * var15;
            float var38 = var27 + var35 * var16;
            float var39 = var30 + var36 * var13;
            float var40 = var31 + var36 * var14;
            var3[0] = var26;
            var3[1] = var27;
            var3[2] = var37;
            var3[3] = var38;
            var3[4] = var39;
            var3[5] = var40;
            var3[6] = var30;
            var3[7] = var31;
            var26 = var5 - this.offset[0][0];
            var27 = var6 - this.offset[0][1];
            var28 -= 2.0F * this.offset[1][0];
            var29 -= 2.0F * this.offset[1][1];
            var30 = var11 - this.offset[2][0];
            var31 = var12 - this.offset[2][1];
            var33 = 2.0F * var28 - var26 - var30;
            var34 = 2.0F * var29 - var27 - var31;
            var35 = var32 * (var14 * var33 - var13 * var34);
            var36 = var32 * (var15 * var34 - var16 * var33);
            var37 = var26 + var35 * var15;
            var38 = var27 + var35 * var16;
            var39 = var30 + var36 * var13;
            var40 = var31 + var36 * var14;
            var4[0] = var26;
            var4[1] = var27;
            var4[2] = var37;
            var4[3] = var38;
            var4[4] = var39;
            var4[5] = var40;
            var4[6] = var30;
            var4[7] = var31;
            return 8;
         }
      }
   }

   private int computeOffsetQuad(float[] var1, int var2, float[] var3, float[] var4) {
      float var5 = var1[var2 + 0];
      float var6 = var1[var2 + 1];
      float var7 = var1[var2 + 2];
      float var8 = var1[var2 + 3];
      float var9 = var1[var2 + 4];
      float var10 = var1[var2 + 5];
      float var11 = var9 - var7;
      float var12 = var10 - var8;
      float var13 = var7 - var5;
      float var14 = var8 - var6;
      computeOffset(var13, var14, this.lineWidth2, this.offset[0]);
      computeOffset(var11, var12, this.lineWidth2, this.offset[1]);
      var3[0] = var5 + this.offset[0][0];
      var3[1] = var6 + this.offset[0][1];
      var3[4] = var9 + this.offset[1][0];
      var3[5] = var10 + this.offset[1][1];
      var4[0] = var5 - this.offset[0][0];
      var4[1] = var6 - this.offset[0][1];
      var4[4] = var9 - this.offset[1][0];
      var4[5] = var10 - this.offset[1][1];
      float var15 = var3[0];
      float var16 = var3[1];
      float var17 = var3[4];
      float var18 = var3[5];
      this.computeIntersection(var15, var16, var15 + var13, var16 + var14, var17, var18, var17 - var11, var18 - var12, var3, 2);
      float var19 = var3[2];
      float var20 = var3[3];
      if (isFinite(var19) && isFinite(var20)) {
         var4[2] = 2.0F * var7 - var19;
         var4[3] = 2.0F * var8 - var20;
         return 6;
      } else {
         var15 = var4[0];
         var16 = var4[1];
         var17 = var4[4];
         var18 = var4[5];
         this.computeIntersection(var15, var16, var15 + var13, var16 + var14, var17, var18, var17 - var11, var18 - var12, var4, 2);
         var19 = var4[2];
         var20 = var4[3];
         if (isFinite(var19) && isFinite(var20)) {
            var3[2] = 2.0F * var7 - var19;
            var3[3] = 2.0F * var8 - var20;
            return 6;
         } else {
            this.getLineOffsets(var5, var6, var9, var10, var3, var4);
            return 4;
         }
      }
   }

   private static boolean isFinite(float var0) {
      return Float.NEGATIVE_INFINITY < var0 && var0 < Float.POSITIVE_INFINITY;
   }

   private static int findSubdivPoints(float[] var0, float[] var1, int var2, float var3) {
      float var4 = var0[2] - var0[0];
      float var5 = var0[3] - var0[1];
      if (var5 != 0.0F && var4 != 0.0F) {
         float var6 = (float)Math.sqrt((double)(var4 * var4 + var5 * var5));
         float var7 = var4 / var6;
         float var8 = var5 / var6;
         float var9 = var7 * var0[0] + var8 * var0[1];
         float var10 = var7 * var0[1] - var8 * var0[0];
         float var11 = var7 * var0[2] + var8 * var0[3];
         float var12 = var7 * var0[3] - var8 * var0[2];
         float var13 = var7 * var0[4] + var8 * var0[5];
         float var14 = var7 * var0[5] - var8 * var0[4];
         switch(var2) {
         case 6:
            c.set(var9, var10, var11, var12, var13, var14);
            break;
         case 8:
            float var15 = var7 * var0[6] + var8 * var0[7];
            float var16 = var7 * var0[7] - var8 * var0[6];
            c.set(var9, var10, var11, var12, var13, var14, var15, var16);
         }
      } else {
         c.set(var0, var2);
      }

      byte var17 = 0;
      int var18 = var17 + c.dxRoots(var1, var17);
      var18 += c.dyRoots(var1, var18);
      if (var2 == 8) {
         var18 += c.infPoints(var1, var18);
      }

      var18 += c.rootsOfROCMinusW(var1, var18, var3, 1.0E-4F);
      var18 = Helpers.filterOutNotInAB(var1, 0, var18, 1.0E-4F, 0.9999F);
      Helpers.isort(var1, 0, var18);
      return var18;
   }

   public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.middle[0] = this.cx0;
      this.middle[1] = this.cy0;
      this.middle[2] = var1;
      this.middle[3] = var2;
      this.middle[4] = var3;
      this.middle[5] = var4;
      this.middle[6] = var5;
      this.middle[7] = var6;
      float var7 = this.middle[6];
      float var8 = this.middle[7];
      float var9 = this.middle[2] - this.middle[0];
      float var10 = this.middle[3] - this.middle[1];
      float var11 = this.middle[6] - this.middle[4];
      float var12 = this.middle[7] - this.middle[5];
      boolean var13 = var9 == 0.0F && var10 == 0.0F;
      boolean var14 = var11 == 0.0F && var12 == 0.0F;
      if (var13) {
         var9 = this.middle[4] - this.middle[0];
         var10 = this.middle[5] - this.middle[1];
         if (var9 == 0.0F && var10 == 0.0F) {
            var9 = this.middle[6] - this.middle[0];
            var10 = this.middle[7] - this.middle[1];
         }
      }

      if (var14) {
         var11 = this.middle[6] - this.middle[2];
         var12 = this.middle[7] - this.middle[3];
         if (var11 == 0.0F && var12 == 0.0F) {
            var11 = this.middle[6] - this.middle[0];
            var12 = this.middle[7] - this.middle[1];
         }
      }

      if (var9 == 0.0F && var10 == 0.0F) {
         this.lineTo(this.middle[0], this.middle[1]);
      } else {
         float var15;
         if (Math.abs(var9) < 0.1F && Math.abs(var10) < 0.1F) {
            var15 = (float)Math.sqrt((double)(var9 * var9 + var10 * var10));
            var9 /= var15;
            var10 /= var15;
         }

         if (Math.abs(var11) < 0.1F && Math.abs(var12) < 0.1F) {
            var15 = (float)Math.sqrt((double)(var11 * var11 + var12 * var12));
            var11 /= var15;
            var12 /= var15;
         }

         computeOffset(var9, var10, this.lineWidth2, this.offset[0]);
         var15 = this.offset[0][0];
         float var16 = this.offset[0][1];
         this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var9, var10, this.cmx, this.cmy, var15, var16);
         int var17 = findSubdivPoints(this.middle, this.subdivTs, 8, this.lineWidth2);
         int var18 = 0;

         for(Iterator var19 = Curve.breakPtsAtTs(this.middle, 8, this.subdivTs, var17); var19.hasNext(); this.emitLineTo(this.rp[var18 - 2], this.rp[var18 - 1], true)) {
            int var20 = (Integer)var19.next();
            var18 = this.computeOffsetCubic(this.middle, var20, this.lp, this.rp);
            this.emitLineTo(this.lp[0], this.lp[1]);
            switch(var18) {
            case 4:
               this.emitLineTo(this.lp[2], this.lp[3]);
               this.emitLineTo(this.rp[0], this.rp[1], true);
               break;
            case 8:
               this.emitCurveTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], this.lp[6], this.lp[7], false);
               this.emitCurveTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], this.rp[6], this.rp[7], true);
            }
         }

         this.cmx = (this.lp[var18 - 2] - this.rp[var18 - 2]) / 2.0F;
         this.cmy = (this.lp[var18 - 1] - this.rp[var18 - 1]) / 2.0F;
         this.cdx = var11;
         this.cdy = var12;
         this.cx0 = var7;
         this.cy0 = var8;
         this.prev = 1;
      }
   }

   public void quadTo(float var1, float var2, float var3, float var4) {
      this.middle[0] = this.cx0;
      this.middle[1] = this.cy0;
      this.middle[2] = var1;
      this.middle[3] = var2;
      this.middle[4] = var3;
      this.middle[5] = var4;
      float var5 = this.middle[4];
      float var6 = this.middle[5];
      float var7 = this.middle[2] - this.middle[0];
      float var8 = this.middle[3] - this.middle[1];
      float var9 = this.middle[4] - this.middle[2];
      float var10 = this.middle[5] - this.middle[3];
      if (var7 == 0.0F && var8 == 0.0F || var9 == 0.0F && var10 == 0.0F) {
         var7 = var9 = this.middle[4] - this.middle[0];
         var8 = var10 = this.middle[5] - this.middle[1];
      }

      if (var7 == 0.0F && var8 == 0.0F) {
         this.lineTo(this.middle[0], this.middle[1]);
      } else {
         float var11;
         if (Math.abs(var7) < 0.1F && Math.abs(var8) < 0.1F) {
            var11 = (float)Math.sqrt((double)(var7 * var7 + var8 * var8));
            var7 /= var11;
            var8 /= var11;
         }

         if (Math.abs(var9) < 0.1F && Math.abs(var10) < 0.1F) {
            var11 = (float)Math.sqrt((double)(var9 * var9 + var10 * var10));
            var9 /= var11;
            var10 /= var11;
         }

         computeOffset(var7, var8, this.lineWidth2, this.offset[0]);
         var11 = this.offset[0][0];
         float var12 = this.offset[0][1];
         this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, var7, var8, this.cmx, this.cmy, var11, var12);
         int var13 = findSubdivPoints(this.middle, this.subdivTs, 6, this.lineWidth2);
         int var14 = 0;

         for(Iterator var15 = Curve.breakPtsAtTs(this.middle, 6, this.subdivTs, var13); var15.hasNext(); this.emitLineTo(this.rp[var14 - 2], this.rp[var14 - 1], true)) {
            int var16 = (Integer)var15.next();
            var14 = this.computeOffsetQuad(this.middle, var16, this.lp, this.rp);
            this.emitLineTo(this.lp[0], this.lp[1]);
            switch(var14) {
            case 4:
               this.emitLineTo(this.lp[2], this.lp[3]);
               this.emitLineTo(this.rp[0], this.rp[1], true);
               break;
            case 6:
               this.emitQuadTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], false);
               this.emitQuadTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], true);
            }
         }

         this.cmx = (this.lp[var14 - 2] - this.rp[var14 - 2]) / 2.0F;
         this.cmy = (this.lp[var14 - 1] - this.rp[var14 - 1]) / 2.0F;
         this.cdx = var9;
         this.cdy = var10;
         this.cx0 = var5;
         this.cy0 = var6;
         this.prev = 1;
      }
   }

   public long getNativeConsumer() {
      throw new InternalError("Stroker doesn't use a native consumer");
   }

   private static final class PolyStack {
      float[] curves = new float[400];
      int end = 0;
      int[] curveTypes = new int[50];
      int numCurves = 0;
      private static final int INIT_SIZE = 50;

      PolyStack() {
      }

      public boolean isEmpty() {
         return this.numCurves == 0;
      }

      private void ensureSpace(int var1) {
         int var2;
         if (this.end + var1 >= this.curves.length) {
            var2 = (this.end + var1) * 2;
            this.curves = Arrays.copyOf(this.curves, var2);
         }

         if (this.numCurves >= this.curveTypes.length) {
            var2 = this.numCurves * 2;
            this.curveTypes = Arrays.copyOf(this.curveTypes, var2);
         }

      }

      public void pushCubic(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.ensureSpace(6);
         this.curveTypes[this.numCurves++] = 8;
         this.curves[this.end++] = var5;
         this.curves[this.end++] = var6;
         this.curves[this.end++] = var3;
         this.curves[this.end++] = var4;
         this.curves[this.end++] = var1;
         this.curves[this.end++] = var2;
      }

      public void pushQuad(float var1, float var2, float var3, float var4) {
         this.ensureSpace(4);
         this.curveTypes[this.numCurves++] = 6;
         this.curves[this.end++] = var3;
         this.curves[this.end++] = var4;
         this.curves[this.end++] = var1;
         this.curves[this.end++] = var2;
      }

      public void pushLine(float var1, float var2) {
         this.ensureSpace(2);
         this.curveTypes[this.numCurves++] = 4;
         this.curves[this.end++] = var1;
         this.curves[this.end++] = var2;
      }

      public int pop(float[] var1) {
         int var2 = this.curveTypes[this.numCurves - 1];
         --this.numCurves;
         this.end -= var2 - 2;
         System.arraycopy(this.curves, this.end, var1, 0, var2 - 2);
         return var2;
      }

      public void pop(PathConsumer2D var1) {
         --this.numCurves;
         int var2 = this.curveTypes[this.numCurves];
         this.end -= var2 - 2;
         switch(var2) {
         case 4:
            var1.lineTo(this.curves[this.end], this.curves[this.end + 1]);
         case 5:
         case 7:
         default:
            break;
         case 6:
            var1.quadTo(this.curves[this.end + 0], this.curves[this.end + 1], this.curves[this.end + 2], this.curves[this.end + 3]);
            break;
         case 8:
            var1.curveTo(this.curves[this.end + 0], this.curves[this.end + 1], this.curves[this.end + 2], this.curves[this.end + 3], this.curves[this.end + 4], this.curves[this.end + 5]);
         }

      }

      public String toString() {
         String var1 = "";
         int var2 = this.numCurves;

         int var4;
         for(int var3 = this.end; var2 > 0; var1 = var1 + Arrays.toString(Arrays.copyOfRange(this.curves, var3, var3 + var4 - 2)) + "\n") {
            --var2;
            var4 = this.curveTypes[this.numCurves];
            var3 -= var4 - 2;
            switch(var4) {
            case 4:
               var1 = var1 + "line: ";
            case 5:
            case 7:
            default:
               break;
            case 6:
               var1 = var1 + "quad: ";
               break;
            case 8:
               var1 = var1 + "cubic: ";
            }
         }

         return var1;
      }
   }
}
