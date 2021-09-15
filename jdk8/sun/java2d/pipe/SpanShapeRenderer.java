package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public abstract class SpanShapeRenderer implements ShapeDrawPipe {
   static final RenderingEngine RenderEngine = RenderingEngine.getInstance();
   public static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.stroke instanceof BasicStroke) {
         ShapeSpanIterator var3 = LoopPipe.getStrokeSpans(var1, var2);

         try {
            this.renderSpans(var1, var1.getCompClip(), var2, var3);
         } finally {
            var3.dispose();
         }
      } else {
         this.fill(var1, var1.stroke.createStrokedShape(var2));
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      if (var2 instanceof Rectangle2D && (var1.transform.getType() & 48) == 0) {
         this.renderRect(var1, (Rectangle2D)var2);
      } else {
         Region var3 = var1.getCompClip();
         ShapeSpanIterator var4 = LoopPipe.getFillSSI(var1);

         try {
            var4.setOutputArea(var3);
            var4.appendPath(var2.getPathIterator(var1.transform));
            this.renderSpans(var1, var3, var2, var4);
         } finally {
            var4.dispose();
         }

      }
   }

   public abstract Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4);

   public abstract void renderBox(Object var1, int var2, int var3, int var4, int var5);

   public abstract void endSequence(Object var1);

   public void renderRect(SunGraphics2D var1, Rectangle2D var2) {
      double[] var3 = new double[]{var2.getX(), var2.getY(), var2.getWidth(), var2.getHeight()};
      var3[2] += var3[0];
      var3[3] += var3[1];
      if (var3[2] > var3[0] && var3[3] > var3[1]) {
         var1.transform.transform((double[])var3, 0, (double[])var3, 0, 2);
         double var4;
         if (var3[2] < var3[0]) {
            var4 = var3[2];
            var3[2] = var3[0];
            var3[0] = var4;
         }

         if (var3[3] < var3[1]) {
            var4 = var3[3];
            var3[3] = var3[1];
            var3[1] = var4;
         }

         int[] var9 = new int[]{(int)var3[0], (int)var3[1], (int)var3[2], (int)var3[3]};
         Rectangle var5 = new Rectangle(var9[0], var9[1], var9[2] - var9[0], var9[3] - var9[1]);
         Region var6 = var1.getCompClip();
         var6.clipBoxToBounds(var9);
         if (var9[0] < var9[2] && var9[1] < var9[3]) {
            Object var7 = this.startSequence(var1, var2, var5, var9);
            if (var6.isRectangular()) {
               this.renderBox(var7, var9[0], var9[1], var9[2] - var9[0], var9[3] - var9[1]);
            } else {
               SpanIterator var8 = var6.getSpanIterator(var9);

               while(var8.nextSpan(var9)) {
                  this.renderBox(var7, var9[0], var9[1], var9[2] - var9[0], var9[3] - var9[1]);
               }
            }

            this.endSequence(var7);
         }
      }
   }

   public void renderSpans(SunGraphics2D var1, Region var2, Shape var3, ShapeSpanIterator var4) {
      Object var5 = null;
      int[] var6 = new int[4];

      try {
         var4.getPathBox(var6);
         Rectangle var7 = new Rectangle(var6[0], var6[1], var6[2] - var6[0], var6[3] - var6[1]);
         var2.clipBoxToBounds(var6);
         if (var6[0] < var6[2] && var6[1] < var6[3]) {
            var4.intersectClipBox(var6[0], var6[1], var6[2], var6[3]);
            var5 = this.startSequence(var1, var3, var7, var6);
            this.spanClipLoop(var5, var4, var2, var6);
            return;
         }
      } finally {
         if (var5 != null) {
            this.endSequence(var5);
         }

      }

   }

   public void spanClipLoop(Object var1, SpanIterator var2, Region var3, int[] var4) {
      if (!var3.isRectangular()) {
         var2 = var3.filter(var2);
      }

      while(var2.nextSpan(var4)) {
         int var5 = var4[0];
         int var6 = var4[1];
         this.renderBox(var1, var5, var6, var4[2] - var5, var4[3] - var6);
      }

   }

   public static class Simple extends SpanShapeRenderer implements LoopBasedPipe {
      public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
         return var1;
      }

      public void renderBox(Object var1, int var2, int var3, int var4, int var5) {
         SunGraphics2D var6 = (SunGraphics2D)var1;
         SurfaceData var7 = var6.getSurfaceData();
         var6.loops.fillRectLoop.FillRect(var6, var7, var2, var3, var4, var5);
      }

      public void endSequence(Object var1) {
      }
   }

   public static class Composite extends SpanShapeRenderer {
      CompositePipe comppipe;

      public Composite(CompositePipe var1) {
         this.comppipe = var1;
      }

      public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
         return this.comppipe.startSequence(var1, var2, var3, var4);
      }

      public void renderBox(Object var1, int var2, int var3, int var4, int var5) {
         this.comppipe.renderPathTile(var1, (byte[])null, 0, var4, var2, var3, var4, var5);
      }

      public void endSequence(Object var1) {
         this.comppipe.endSequence(var1);
      }
   }
}
