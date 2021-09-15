package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class SpanClipRenderer implements CompositePipe {
   CompositePipe outpipe;
   static Class RegionClass = Region.class;
   static Class RegionIteratorClass = RegionIterator.class;

   static native void initIDs(Class var0, Class var1);

   public SpanClipRenderer(CompositePipe var1) {
      this.outpipe = var1;
   }

   public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
      RegionIterator var5 = var1.clipRegion.getIterator();
      return new SpanClipRenderer.SCRcontext(var5, this.outpipe.startSequence(var1, var2, var3, var4));
   }

   public boolean needTile(Object var1, int var2, int var3, int var4, int var5) {
      SpanClipRenderer.SCRcontext var6 = (SpanClipRenderer.SCRcontext)var1;
      return this.outpipe.needTile(var6.outcontext, var2, var3, var4, var5);
   }

   public void renderPathTile(Object var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, ShapeSpanIterator var9) {
      this.renderPathTile(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void renderPathTile(Object var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      SpanClipRenderer.SCRcontext var9 = (SpanClipRenderer.SCRcontext)var1;
      RegionIterator var10 = var9.iterator.createCopy();
      int[] var11 = var9.band;
      var11[0] = var5;
      var11[1] = var6;
      var11[2] = var5 + var7;
      var11[3] = var6 + var8;
      if (var2 == null) {
         int var12 = var7 * var8;
         var2 = var9.tile;
         if (var2 != null && var2.length < var12) {
            var2 = null;
         }

         if (var2 == null) {
            var2 = new byte[var12];
            var9.tile = var2;
         }

         var3 = 0;
         var4 = var7;
         this.fillTile(var10, var2, var3, var7, var11);
      } else {
         this.eraseTile(var10, var2, var3, var4, var11);
      }

      if (var11[2] > var11[0] && var11[3] > var11[1]) {
         var3 += (var11[1] - var6) * var4 + (var11[0] - var5);
         this.outpipe.renderPathTile(var9.outcontext, var2, var3, var4, var11[0], var11[1], var11[2] - var11[0], var11[3] - var11[1]);
      }

   }

   public native void fillTile(RegionIterator var1, byte[] var2, int var3, int var4, int[] var5);

   public native void eraseTile(RegionIterator var1, byte[] var2, int var3, int var4, int[] var5);

   public void skipTile(Object var1, int var2, int var3) {
      SpanClipRenderer.SCRcontext var4 = (SpanClipRenderer.SCRcontext)var1;
      this.outpipe.skipTile(var4.outcontext, var2, var3);
   }

   public void endSequence(Object var1) {
      SpanClipRenderer.SCRcontext var2 = (SpanClipRenderer.SCRcontext)var1;
      this.outpipe.endSequence(var2.outcontext);
   }

   static {
      initIDs(RegionClass, RegionIteratorClass);
   }

   class SCRcontext {
      RegionIterator iterator;
      Object outcontext;
      int[] band;
      byte[] tile;

      public SCRcontext(RegionIterator var2, Object var3) {
         this.iterator = var2;
         this.outcontext = var3;
         this.band = new int[4];
      }
   }
}
