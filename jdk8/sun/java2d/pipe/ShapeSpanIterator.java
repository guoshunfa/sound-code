package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;

public final class ShapeSpanIterator implements SpanIterator, PathConsumer2D {
   long pData;

   public static native void initIDs();

   public ShapeSpanIterator(boolean var1) {
      this.setNormalize(var1);
   }

   public void appendPath(PathIterator var1) {
      float[] var2 = new float[6];
      this.setRule(var1.getWindingRule());

      while(!var1.isDone()) {
         this.addSegment(var1.currentSegment(var2), var2);
         var1.next();
      }

      this.pathDone();
   }

   public native void appendPoly(int[] var1, int[] var2, int var3, int var4, int var5);

   private native void setNormalize(boolean var1);

   public void setOutputAreaXYWH(int var1, int var2, int var3, int var4) {
      this.setOutputAreaXYXY(var1, var2, Region.dimAdd(var1, var3), Region.dimAdd(var2, var4));
   }

   public native void setOutputAreaXYXY(int var1, int var2, int var3, int var4);

   public void setOutputArea(Rectangle var1) {
      this.setOutputAreaXYWH(var1.x, var1.y, var1.width, var1.height);
   }

   public void setOutputArea(Region var1) {
      this.setOutputAreaXYXY(var1.lox, var1.loy, var1.hix, var1.hiy);
   }

   public native void setRule(int var1);

   public native void addSegment(int var1, float[] var2);

   public native void getPathBox(int[] var1);

   public native void intersectClipBox(int var1, int var2, int var3, int var4);

   public native boolean nextSpan(int[] var1);

   public native void skipDownTo(int var1);

   public native long getNativeIterator();

   public native void dispose();

   public native void moveTo(float var1, float var2);

   public native void lineTo(float var1, float var2);

   public native void quadTo(float var1, float var2, float var3, float var4);

   public native void curveTo(float var1, float var2, float var3, float var4, float var5, float var6);

   public native void closePath();

   public native void pathDone();

   public native long getNativeConsumer();

   static {
      initIDs();
   }
}
