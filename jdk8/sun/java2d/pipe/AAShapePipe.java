package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;

public class AAShapePipe implements ShapeDrawPipe, ParallelogramPipe {
   static RenderingEngine renderengine = RenderingEngine.getInstance();
   CompositePipe outpipe;
   private static byte[] theTile;

   public AAShapePipe(CompositePipe var1) {
      this.outpipe = var1;
   }

   public void draw(SunGraphics2D var1, Shape var2) {
      BasicStroke var3;
      if (var1.stroke instanceof BasicStroke) {
         var3 = (BasicStroke)var1.stroke;
      } else {
         var2 = var1.stroke.createStrokedShape(var2);
         var3 = null;
      }

      this.renderPath(var1, var2, var3);
   }

   public void fill(SunGraphics2D var1, Shape var2) {
      this.renderPath(var1, var2, (BasicStroke)null);
   }

   private static Rectangle2D computeBBox(double var0, double var2, double var4, double var6) {
      if ((var4 -= var0) < 0.0D) {
         var0 += var4;
         var4 = -var4;
      }

      if ((var6 -= var2) < 0.0D) {
         var2 += var6;
         var6 = -var6;
      }

      return new Rectangle2D.Double(var0, var2, var4, var6);
   }

   public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      Region var22 = var1.getCompClip();
      int[] var23 = new int[4];
      AATileGenerator var24 = renderengine.getAATileGenerator(var10, var12, var14, var16, var18, var20, 0.0D, 0.0D, var22, var23);
      if (var24 != null) {
         this.renderTiles(var1, computeBBox(var2, var4, var6, var8), var24, var23);
      }
   }

   public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
      Region var26 = var1.getCompClip();
      int[] var27 = new int[4];
      AATileGenerator var28 = renderengine.getAATileGenerator(var10, var12, var14, var16, var18, var20, var22, var24, var26, var27);
      if (var28 != null) {
         this.renderTiles(var1, computeBBox(var2, var4, var6, var8), var28, var27);
      }
   }

   private static synchronized byte[] getAlphaTile(int var0) {
      byte[] var1 = theTile;
      if (var1 != null && var1.length >= var0) {
         theTile = null;
      } else {
         var1 = new byte[var0];
      }

      return var1;
   }

   private static synchronized void dropAlphaTile(byte[] var0) {
      theTile = var0;
   }

   public void renderPath(SunGraphics2D var1, Shape var2, BasicStroke var3) {
      boolean var4 = var3 != null && var1.strokeHint != 2;
      boolean var5 = var1.strokeState <= 1;
      Region var6 = var1.getCompClip();
      int[] var7 = new int[4];
      AATileGenerator var8 = renderengine.getAATileGenerator(var2, var1.transform, var6, var3, var5, var4, var7);
      if (var8 != null) {
         this.renderTiles(var1, var2, var8, var7);
      }
   }

   public void renderTiles(SunGraphics2D var1, Shape var2, AATileGenerator var3, int[] var4) {
      Object var5 = null;
      byte[] var6 = null;

      try {
         var5 = this.outpipe.startSequence(var1, var2, new Rectangle(var4[0], var4[1], var4[2] - var4[0], var4[3] - var4[1]), var4);
         int var7 = var3.getTileWidth();
         int var8 = var3.getTileHeight();
         var6 = getAlphaTile(var7 * var8);

         for(int var10 = var4[1]; var10 < var4[3]; var10 += var8) {
            for(int var11 = var4[0]; var11 < var4[2]; var11 += var7) {
               int var12 = Math.min(var7, var4[2] - var11);
               int var13 = Math.min(var8, var4[3] - var10);
               int var14 = var3.getTypicalAlpha();
               if (var14 != 0 && this.outpipe.needTile(var5, var11, var10, var12, var13)) {
                  byte[] var9;
                  if (var14 == 255) {
                     var9 = null;
                     var3.nextTile();
                  } else {
                     var9 = var6;
                     var3.getAlpha(var6, 0, var7);
                  }

                  this.outpipe.renderPathTile(var5, var9, 0, var7, var11, var10, var12, var13);
               } else {
                  var3.nextTile();
                  this.outpipe.skipTile(var5, var11, var10);
               }
            }
         }
      } finally {
         var3.dispose();
         if (var5 != null) {
            this.outpipe.endSequence(var5);
         }

         if (var6 != null) {
            dropAlphaTile(var6);
         }

      }

   }
}
