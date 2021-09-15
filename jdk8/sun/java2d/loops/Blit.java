package sun.java2d.loops;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import java.util.Map;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

public class Blit extends GraphicsPrimitive {
   public static final String methodSignature = "Blit(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static RenderCache blitcache = new RenderCache(20);

   public static Blit locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (Blit)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static Blit getFromCache(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      Object var3 = blitcache.get(var0, var1, var2);
      if (var3 != null) {
         return (Blit)var3;
      } else {
         Blit var4 = locate(var0, var1, var2);
         if (var4 == null) {
            System.out.println("blit loop not found for:");
            System.out.println("src:  " + var0);
            System.out.println("comp: " + var1);
            System.out.println("dst:  " + var2);
         } else {
            blitcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected Blit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public Blit(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      if (var2.isDerivedFrom(CompositeType.Xor)) {
         Blit.GeneralXorBlit var4 = new Blit.GeneralXorBlit(var1, var2, var3);
         this.setupGeneralBinaryOp(var4);
         return var4;
      } else {
         return (GraphicsPrimitive)(var2.isDerivedFrom(CompositeType.AnyAlpha) ? new Blit.GeneralMaskBlit(var1, var2, var3) : Blit.AnyBlit.instance);
      }
   }

   public GraphicsPrimitive traceWrap() {
      return new Blit.TraceBlit(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new Blit((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceBlit extends Blit {
      Blit target;

      public TraceBlit(Blit var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         tracePrimitive(this.target);
         this.target.Blit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   private static class GeneralXorBlit extends Blit implements GraphicsPrimitive.GeneralBinaryOp {
      Blit convertsrc;
      Blit convertdst;
      Blit performop;
      Blit convertresult;
      WeakReference srcTmp;
      WeakReference dstTmp;

      public GeneralXorBlit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
      }

      public void setPrimitives(Blit var1, Blit var2, GraphicsPrimitive var3, Blit var4) {
         this.convertsrc = var1;
         this.convertdst = var2;
         this.performop = (Blit)var3;
         this.convertresult = var4;
      }

      public synchronized void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         SurfaceData var11;
         int var14;
         int var15;
         SurfaceData var18;
         if (this.convertsrc == null) {
            var11 = var1;
            var14 = var5;
            var15 = var6;
         } else {
            var18 = null;
            if (this.srcTmp != null) {
               var18 = (SurfaceData)this.srcTmp.get();
            }

            var11 = convertFrom(this.convertsrc, var1, var5, var6, var9, var10, var18);
            var14 = 0;
            var15 = 0;
            if (var11 != var18) {
               this.srcTmp = new WeakReference(var11);
            }
         }

         SurfaceData var12;
         Region var13;
         int var16;
         int var17;
         if (this.convertdst == null) {
            var12 = var2;
            var16 = var7;
            var17 = var8;
            var13 = var4;
         } else {
            var18 = null;
            if (this.dstTmp != null) {
               var18 = (SurfaceData)this.dstTmp.get();
            }

            var12 = convertFrom(this.convertdst, var2, var7, var8, var9, var10, var18);
            var16 = 0;
            var17 = 0;
            var13 = null;
            if (var12 != var18) {
               this.dstTmp = new WeakReference(var12);
            }
         }

         this.performop.Blit(var11, var12, var3, var13, var14, var15, var16, var17, var9, var10);
         if (this.convertresult != null) {
            convertTo(this.convertresult, var12, var2, var4, var7, var8, var9, var10);
         }

      }
   }

   private static class GeneralMaskBlit extends Blit {
      MaskBlit performop;

      public GeneralMaskBlit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.performop = MaskBlit.locate(var1, var2, var3);
      }

      public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         this.performop.MaskBlit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, (byte[])null, 0, 0);
      }
   }

   private static class AnyBlit extends Blit {
      public static Blit.AnyBlit instance = new Blit.AnyBlit();

      public AnyBlit() {
         super(SurfaceType.Any, CompositeType.Any, SurfaceType.Any);
      }

      public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         ColorModel var11 = var1.getColorModel();
         ColorModel var12 = var2.getColorModel();
         CompositeContext var13 = var3.createContext(var11, var12, new RenderingHints((Map)null));
         Raster var14 = var1.getRaster(var5, var6, var9, var10);
         WritableRaster var15 = (WritableRaster)var2.getRaster(var7, var8, var9, var10);
         if (var4 == null) {
            var4 = Region.getInstanceXYWH(var7, var8, var9, var10);
         }

         int[] var16 = new int[]{var7, var8, var7 + var9, var8 + var10};
         SpanIterator var17 = var4.getSpanIterator(var16);
         var5 -= var7;
         var6 -= var8;

         while(var17.nextSpan(var16)) {
            int var18 = var16[2] - var16[0];
            int var19 = var16[3] - var16[1];
            Raster var20 = var14.createChild(var5 + var16[0], var6 + var16[1], var18, var19, 0, 0, (int[])null);
            WritableRaster var21 = var15.createWritableChild(var16[0], var16[1], var18, var19, 0, 0, (int[])null);
            var13.compose(var20, var21, var21);
         }

         var13.dispose();
      }
   }
}
