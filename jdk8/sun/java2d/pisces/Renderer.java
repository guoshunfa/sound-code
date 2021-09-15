package sun.java2d.pisces;

import java.util.Arrays;
import sun.awt.geom.PathConsumer2D;

final class Renderer implements PathConsumer2D {
   private static final int YMAX = 0;
   private static final int CURX = 1;
   private static final int OR = 2;
   private static final int SLOPE = 3;
   private static final int NEXT = 4;
   private float edgeMinY = Float.POSITIVE_INFINITY;
   private float edgeMaxY = Float.NEGATIVE_INFINITY;
   private float edgeMinX = Float.POSITIVE_INFINITY;
   private float edgeMaxX = Float.NEGATIVE_INFINITY;
   private static final int SIZEOF_EDGE = 5;
   private static final int NULL = -5;
   private float[] edges = null;
   private static final int INIT_NUM_EDGES = 8;
   private int[] edgeBuckets = null;
   private int[] edgeBucketCounts = null;
   private int numEdges;
   private static final float DEC_BND = 20.0F;
   private static final float INC_BND = 8.0F;
   public static final int WIND_EVEN_ODD = 0;
   public static final int WIND_NON_ZERO = 1;
   private final int SUBPIXEL_LG_POSITIONS_X;
   private final int SUBPIXEL_LG_POSITIONS_Y;
   private final int SUBPIXEL_POSITIONS_X;
   private final int SUBPIXEL_POSITIONS_Y;
   private final int SUBPIXEL_MASK_X;
   private final int SUBPIXEL_MASK_Y;
   final int MAX_AA_ALPHA;
   PiscesCache cache;
   private final int boundsMinX;
   private final int boundsMinY;
   private final int boundsMaxX;
   private final int boundsMaxY;
   private final int windingRule;
   private float x0;
   private float y0;
   private float pix_sx0;
   private float pix_sy0;
   private Curve c = new Curve();

   private void addEdgeToBucket(int var1, int var2) {
      this.edges[var1 + 4] = (float)this.edgeBuckets[var2];
      this.edgeBuckets[var2] = var1;
      int[] var10000 = this.edgeBucketCounts;
      var10000[var2] += 2;
   }

   private void quadBreakIntoLinesAndAdd(float var1, float var2, Curve var3, float var4, float var5) {
      int var8 = 16;
      int var9 = var8 * var8;

      for(float var10 = Math.max(var3.dbx / (float)var9, var3.dby / (float)var9); var10 > 32.0F; var8 <<= 1) {
         var10 /= 4.0F;
      }

      var9 = var8 * var8;
      float var11 = var3.dbx / (float)var9;
      float var12 = var3.dby / (float)var9;
      float var13 = var3.bx / (float)var9 + var3.cx / (float)var8;

      float var16;
      for(float var14 = var3.by / (float)var9 + var3.cy / (float)var8; var8-- > 1; var2 = var16) {
         float var15 = var1 + var13;
         var13 += var11;
         var16 = var2 + var14;
         var14 += var12;
         this.addLine(var1, var2, var15, var16);
         var1 = var15;
      }

      this.addLine(var1, var2, var4, var5);
   }

   private void curveBreakIntoLinesAndAdd(float var1, float var2, Curve var3, float var4, float var5) {
      int var7 = 8;
      float var8 = 2.0F * var3.dax / 512.0F;
      float var9 = 2.0F * var3.day / 512.0F;
      float var10 = var8 + var3.dbx / 64.0F;
      float var11 = var9 + var3.dby / 64.0F;
      float var12 = var3.ax / 512.0F + var3.bx / 64.0F + var3.cx / 8.0F;
      float var13 = var3.ay / 512.0F + var3.by / 64.0F + var3.cy / 8.0F;
      float var14 = var1;

      for(float var15 = var2; var7 > 0; var2 = var15) {
         while(Math.abs(var10) > 20.0F || Math.abs(var11) > 20.0F) {
            var8 /= 8.0F;
            var9 /= 8.0F;
            var10 = var10 / 4.0F - var8;
            var11 = var11 / 4.0F - var9;
            var12 = (var12 - var10) / 2.0F;
            var13 = (var13 - var11) / 2.0F;
            var7 <<= 1;
         }

         while(var7 % 2 == 0 && Math.abs(var12) <= 8.0F && Math.abs(var13) <= 8.0F) {
            var12 = 2.0F * var12 + var10;
            var13 = 2.0F * var13 + var11;
            var10 = 4.0F * (var10 + var8);
            var11 = 4.0F * (var11 + var9);
            var8 = 8.0F * var8;
            var9 = 8.0F * var9;
            var7 >>= 1;
         }

         --var7;
         if (var7 > 0) {
            var14 += var12;
            var12 += var10;
            var10 += var8;
            var15 += var13;
            var13 += var11;
            var11 += var9;
         } else {
            var14 = var4;
            var15 = var5;
         }

         this.addLine(var1, var2, var14, var15);
         var1 = var14;
      }

   }

   private void addLine(float var1, float var2, float var3, float var4) {
      float var5 = 1.0F;
      if (var4 < var2) {
         var5 = var4;
         var4 = var2;
         var2 = var5;
         var5 = var3;
         var3 = var1;
         var1 = var5;
         var5 = 0.0F;
      }

      int var6 = Math.max((int)Math.ceil((double)var2), this.boundsMinY);
      int var7 = Math.min((int)Math.ceil((double)var4), this.boundsMaxY);
      if (var6 < var7) {
         if (var2 < this.edgeMinY) {
            this.edgeMinY = var2;
         }

         if (var4 > this.edgeMaxY) {
            this.edgeMaxY = var4;
         }

         float var8 = (var3 - var1) / (var4 - var2);
         if (var8 > 0.0F) {
            if (var1 < this.edgeMinX) {
               this.edgeMinX = var1;
            }

            if (var3 > this.edgeMaxX) {
               this.edgeMaxX = var3;
            }
         } else {
            if (var3 < this.edgeMinX) {
               this.edgeMinX = var3;
            }

            if (var1 > this.edgeMaxX) {
               this.edgeMaxX = var1;
            }
         }

         int var9 = this.numEdges * 5;
         this.edges = Helpers.widenArray((float[])this.edges, var9, 5);
         ++this.numEdges;
         this.edges[var9 + 2] = var5;
         this.edges[var9 + 1] = var1 + ((float)var6 - var2) * var8;
         this.edges[var9 + 3] = var8;
         this.edges[var9 + 0] = (float)var7;
         int var10 = var6 - this.boundsMinY;
         this.addEdgeToBucket(var9, var10);
         int[] var10000 = this.edgeBucketCounts;
         int var10001 = var7 - this.boundsMinY;
         var10000[var10001] |= 1;
      }
   }

   public Renderer(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.SUBPIXEL_LG_POSITIONS_X = var1;
      this.SUBPIXEL_LG_POSITIONS_Y = var2;
      this.SUBPIXEL_MASK_X = (1 << this.SUBPIXEL_LG_POSITIONS_X) - 1;
      this.SUBPIXEL_MASK_Y = (1 << this.SUBPIXEL_LG_POSITIONS_Y) - 1;
      this.SUBPIXEL_POSITIONS_X = 1 << this.SUBPIXEL_LG_POSITIONS_X;
      this.SUBPIXEL_POSITIONS_Y = 1 << this.SUBPIXEL_LG_POSITIONS_Y;
      this.MAX_AA_ALPHA = this.SUBPIXEL_POSITIONS_X * this.SUBPIXEL_POSITIONS_Y;
      this.windingRule = var7;
      this.boundsMinX = var3 * this.SUBPIXEL_POSITIONS_X;
      this.boundsMinY = var4 * this.SUBPIXEL_POSITIONS_Y;
      this.boundsMaxX = (var3 + var5) * this.SUBPIXEL_POSITIONS_X;
      this.boundsMaxY = (var4 + var6) * this.SUBPIXEL_POSITIONS_Y;
      this.edges = new float[40];
      this.numEdges = 0;
      this.edgeBuckets = new int[this.boundsMaxY - this.boundsMinY];
      Arrays.fill((int[])this.edgeBuckets, (int)-5);
      this.edgeBucketCounts = new int[this.edgeBuckets.length + 1];
   }

   private float tosubpixx(float var1) {
      return var1 * (float)this.SUBPIXEL_POSITIONS_X;
   }

   private float tosubpixy(float var1) {
      return var1 * (float)this.SUBPIXEL_POSITIONS_Y;
   }

   public void moveTo(float var1, float var2) {
      this.closePath();
      this.pix_sx0 = var1;
      this.pix_sy0 = var2;
      this.y0 = this.tosubpixy(var2);
      this.x0 = this.tosubpixx(var1);
   }

   public void lineTo(float var1, float var2) {
      float var3 = this.tosubpixx(var1);
      float var4 = this.tosubpixy(var2);
      this.addLine(this.x0, this.y0, var3, var4);
      this.x0 = var3;
      this.y0 = var4;
   }

   public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = this.tosubpixx(var5);
      float var8 = this.tosubpixy(var6);
      this.c.set(this.x0, this.y0, this.tosubpixx(var1), this.tosubpixy(var2), this.tosubpixx(var3), this.tosubpixy(var4), var7, var8);
      this.curveBreakIntoLinesAndAdd(this.x0, this.y0, this.c, var7, var8);
      this.x0 = var7;
      this.y0 = var8;
   }

   public void quadTo(float var1, float var2, float var3, float var4) {
      float var5 = this.tosubpixx(var3);
      float var6 = this.tosubpixy(var4);
      this.c.set(this.x0, this.y0, this.tosubpixx(var1), this.tosubpixy(var2), var5, var6);
      this.quadBreakIntoLinesAndAdd(this.x0, this.y0, this.c, var5, var6);
      this.x0 = var5;
      this.y0 = var6;
   }

   public void closePath() {
      this.lineTo(this.pix_sx0, this.pix_sy0);
   }

   public void pathDone() {
      this.closePath();
   }

   public long getNativeConsumer() {
      throw new InternalError("Renderer does not use a native consumer.");
   }

   private void _endRendering(int var1, int var2, int var3, int var4) {
      int var5 = this.windingRule == 0 ? 1 : -1;
      int var6 = var2 - var1;
      int[] var7 = new int[var6 + 2];
      int var8 = var1 << this.SUBPIXEL_LG_POSITIONS_X;
      int var9 = var2 << this.SUBPIXEL_LG_POSITIONS_X;
      int var10 = Integer.MIN_VALUE;
      int var11 = Integer.MAX_VALUE;
      int var12 = this.boundsMinY;
      Renderer.ScanlineIterator var13 = new Renderer.ScanlineIterator(var3, var4);

      while(var13.hasNext()) {
         int var14 = var13.next();
         int[] var15 = var13.crossings;
         var12 = var13.curY();
         int var16;
         int var17;
         int var18;
         int var19;
         if (var14 > 0) {
            var16 = var15[0] >> 1;
            var17 = var15[var14 - 1] >> 1;
            var18 = Math.max(var16, var8);
            var19 = Math.min(var17, var9);
            var11 = Math.min(var11, var18 >> this.SUBPIXEL_LG_POSITIONS_X);
            var10 = Math.max(var10, var19 >> this.SUBPIXEL_LG_POSITIONS_X);
         }

         var16 = 0;
         var17 = var8;

         for(var18 = 0; var18 < var14; ++var18) {
            var19 = var15[var18];
            int var20 = var19 >> 1;
            int var21 = ((var19 & 1) << 1) - 1;
            if ((var16 & var5) != 0) {
               int var22 = Math.max(var17, var8);
               int var23 = Math.min(var20, var9);
               if (var22 < var23) {
                  var22 -= var8;
                  var23 -= var8;
                  int var24 = var22 >> this.SUBPIXEL_LG_POSITIONS_X;
                  int var25 = var23 - 1 >> this.SUBPIXEL_LG_POSITIONS_X;
                  if (var24 == var25) {
                     var7[var24] += var23 - var22;
                     var7[var24 + 1] -= var23 - var22;
                  } else {
                     int var26 = var23 >> this.SUBPIXEL_LG_POSITIONS_X;
                     var7[var24] += this.SUBPIXEL_POSITIONS_X - (var22 & this.SUBPIXEL_MASK_X);
                     var7[var24 + 1] += var22 & this.SUBPIXEL_MASK_X;
                     var7[var26] -= this.SUBPIXEL_POSITIONS_X - (var23 & this.SUBPIXEL_MASK_X);
                     var7[var26 + 1] -= var23 & this.SUBPIXEL_MASK_X;
                  }
               }
            }

            var16 += var21;
            var17 = var20;
         }

         if ((var12 & this.SUBPIXEL_MASK_Y) == this.SUBPIXEL_MASK_Y) {
            this.emitRow(var7, var12 >> this.SUBPIXEL_LG_POSITIONS_Y, var11, var10);
            var11 = Integer.MAX_VALUE;
            var10 = Integer.MIN_VALUE;
         }
      }

      if (var10 >= var11) {
         this.emitRow(var7, var12 >> this.SUBPIXEL_LG_POSITIONS_Y, var11, var10);
      }

   }

   public void endRendering() {
      int var1 = Math.max((int)Math.ceil((double)this.edgeMinX), this.boundsMinX);
      int var2 = Math.min((int)Math.ceil((double)this.edgeMaxX), this.boundsMaxX);
      int var3 = Math.max((int)Math.ceil((double)this.edgeMinY), this.boundsMinY);
      int var4 = Math.min((int)Math.ceil((double)this.edgeMaxY), this.boundsMaxY);
      int var5 = var1 >> this.SUBPIXEL_LG_POSITIONS_X;
      int var6 = var2 + this.SUBPIXEL_MASK_X >> this.SUBPIXEL_LG_POSITIONS_X;
      int var7 = var3 >> this.SUBPIXEL_LG_POSITIONS_Y;
      int var8 = var4 + this.SUBPIXEL_MASK_Y >> this.SUBPIXEL_LG_POSITIONS_Y;
      if (var5 <= var6 && var7 <= var8) {
         this.cache = new PiscesCache(var5, var7, var6, var8);
         this._endRendering(var5, var6, var3, var4);
      } else {
         this.cache = new PiscesCache(this.boundsMinX >> this.SUBPIXEL_LG_POSITIONS_X, this.boundsMinY >> this.SUBPIXEL_LG_POSITIONS_Y, this.boundsMaxX >> this.SUBPIXEL_LG_POSITIONS_X, this.boundsMaxY >> this.SUBPIXEL_LG_POSITIONS_Y);
      }
   }

   public PiscesCache getCache() {
      if (this.cache == null) {
         throw new InternalError("cache not yet initialized");
      } else {
         return this.cache;
      }
   }

   private void emitRow(int[] var1, int var2, int var3, int var4) {
      if (this.cache != null && var4 >= var3) {
         this.cache.startRow(var2, var3);
         int var5 = var3 - this.cache.bboxX0;
         int var6 = var4 - this.cache.bboxX0;
         int var7 = 1;
         int var8 = var1[var5];

         for(int var9 = var5 + 1; var9 <= var6; ++var9) {
            int var10 = var8 + var1[var9];
            if (var10 == var8) {
               ++var7;
            } else {
               this.cache.addRLERun(var8, var7);
               var7 = 1;
               var8 = var10;
            }
         }

         this.cache.addRLERun(var8, var7);
      }

      Arrays.fill((int[])var1, (int)0);
   }

   private class ScanlineIterator {
      private int[] crossings;
      private final int maxY;
      private int nextY;
      private int edgeCount;
      private int[] edgePtrs;
      private static final int INIT_CROSSINGS_SIZE = 10;

      private ScanlineIterator(int var2, int var3) {
         this.crossings = new int[10];
         this.edgePtrs = new int[10];
         this.nextY = var2;
         this.maxY = var3;
         this.edgeCount = 0;
      }

      private int next() {
         int var1 = this.nextY++;
         int var2 = var1 - Renderer.this.boundsMinY;
         int var3 = this.edgeCount;
         int[] var4 = this.edgePtrs;
         int var5 = Renderer.this.edgeBucketCounts[var2];
         int var6;
         int var7;
         int var8;
         if ((var5 & 1) != 0) {
            var6 = 0;

            for(var7 = 0; var7 < var3; ++var7) {
               var8 = var4[var7];
               if (Renderer.this.edges[var8 + 0] > (float)var1) {
                  var4[var6++] = var8;
               }
            }

            var3 = var6;
         }

         var4 = Helpers.widenArray(var4, var3, var5 >> 1);

         for(var6 = Renderer.this.edgeBuckets[var2]; var6 != -5; var6 = (int)Renderer.this.edges[var6 + 4]) {
            var4[var3++] = var6;
         }

         this.edgePtrs = var4;
         this.edgeCount = var3;
         int[] var13 = this.crossings;
         if (var13.length < var3) {
            this.crossings = var13 = new int[var4.length];
         }

         for(var7 = 0; var7 < var3; ++var7) {
            var8 = var4[var7];
            float var9 = Renderer.this.edges[var8 + 1];
            int var10 = (int)var9 << 1;
            Renderer.this.edges[var8 + 1] = var9 + Renderer.this.edges[var8 + 3];
            if (Renderer.this.edges[var8 + 2] > 0.0F) {
               var10 |= 1;
            }

            int var11 = var7;

            while(true) {
               --var11;
               if (var11 < 0) {
                  break;
               }

               int var12 = var13[var11];
               if (var12 <= var10) {
                  break;
               }

               var13[var11 + 1] = var12;
               var4[var11 + 1] = var4[var11];
            }

            var13[var11 + 1] = var10;
            var4[var11 + 1] = var8;
         }

         return var3;
      }

      private boolean hasNext() {
         return this.nextY < this.maxY;
      }

      private int curY() {
         return this.nextY - 1;
      }

      // $FF: synthetic method
      ScanlineIterator(int var2, int var3, Object var4) {
         this(var2, var3);
      }
   }
}
