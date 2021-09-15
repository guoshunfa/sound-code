package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Comparator;
import sun.awt.SunHints;
import sun.java2d.SunGraphics2D;

public final class GraphicsPrimitiveMgr {
   private static final boolean debugTrace = false;
   private static GraphicsPrimitive[] primitives;
   private static GraphicsPrimitive[] generalPrimitives;
   private static boolean needssort = true;
   private static Comparator primSorter;
   private static Comparator primFinder;

   private static native void initIDs(Class var0, Class var1, Class var2, Class var3, Class var4, Class var5, Class var6, Class var7, Class var8, Class var9, Class var10);

   private static native void registerNativeLoops();

   private GraphicsPrimitiveMgr() {
   }

   public static synchronized void register(GraphicsPrimitive[] var0) {
      GraphicsPrimitive[] var1 = primitives;
      int var2 = 0;
      int var3 = var0.length;
      if (var1 != null) {
         var2 = var1.length;
      }

      GraphicsPrimitive[] var4 = new GraphicsPrimitive[var2 + var3];
      if (var1 != null) {
         System.arraycopy(var1, 0, var4, 0, var2);
      }

      System.arraycopy(var0, 0, var4, var2, var3);
      needssort = true;
      primitives = var4;
   }

   public static synchronized void registerGeneral(GraphicsPrimitive var0) {
      if (generalPrimitives == null) {
         generalPrimitives = new GraphicsPrimitive[]{var0};
      } else {
         int var1 = generalPrimitives.length;
         GraphicsPrimitive[] var2 = new GraphicsPrimitive[var1 + 1];
         System.arraycopy(generalPrimitives, 0, var2, 0, var1);
         var2[var1] = var0;
         generalPrimitives = var2;
      }
   }

   public static synchronized GraphicsPrimitive locate(int var0, SurfaceType var1) {
      return locate(var0, SurfaceType.OpaqueColor, CompositeType.Src, var1);
   }

   public static synchronized GraphicsPrimitive locate(int var0, SurfaceType var1, CompositeType var2, SurfaceType var3) {
      GraphicsPrimitive var4 = locatePrim(var0, var1, var2, var3);
      if (var4 == null) {
         var4 = locateGeneral(var0);
         if (var4 != null) {
            var4 = var4.makePrimitive(var1, var2, var3);
            if (var4 != null && GraphicsPrimitive.traceflags != 0) {
               var4 = var4.traceWrap();
            }
         }
      }

      return var4;
   }

   public static synchronized GraphicsPrimitive locatePrim(int var0, SurfaceType var1, CompositeType var2, SurfaceType var3) {
      GraphicsPrimitiveMgr.PrimitiveSpec var8 = new GraphicsPrimitiveMgr.PrimitiveSpec();

      for(SurfaceType var5 = var3; var5 != null; var5 = var5.getSuperType()) {
         for(SurfaceType var4 = var1; var4 != null; var4 = var4.getSuperType()) {
            for(CompositeType var6 = var2; var6 != null; var6 = var6.getSuperType()) {
               var8.uniqueID = GraphicsPrimitive.makeUniqueID(var0, var4, var6, var5);
               GraphicsPrimitive var7 = locate(var8);
               if (var7 != null) {
                  return var7;
               }
            }
         }
      }

      return null;
   }

   private static GraphicsPrimitive locateGeneral(int var0) {
      if (generalPrimitives == null) {
         return null;
      } else {
         for(int var1 = 0; var1 < generalPrimitives.length; ++var1) {
            GraphicsPrimitive var2 = generalPrimitives[var1];
            if (var2.getPrimTypeID() == var0) {
               return var2;
            }
         }

         return null;
      }
   }

   private static GraphicsPrimitive locate(GraphicsPrimitiveMgr.PrimitiveSpec var0) {
      if (needssort) {
         if (GraphicsPrimitive.traceflags != 0) {
            for(int var1 = 0; var1 < primitives.length; ++var1) {
               primitives[var1] = primitives[var1].traceWrap();
            }
         }

         Arrays.sort(primitives, primSorter);
         needssort = false;
      }

      GraphicsPrimitive[] var4 = primitives;
      if (var4 == null) {
         return null;
      } else {
         int var2 = Arrays.binarySearch(var4, var0, primFinder);
         if (var2 >= 0) {
            GraphicsPrimitive var3 = var4[var2];
            if (var3 instanceof GraphicsPrimitiveProxy) {
               var3 = ((GraphicsPrimitiveProxy)var3).instantiate();
               var4[var2] = var3;
            }

            return var3;
         } else {
            return null;
         }
      }
   }

   private static void writeLog(String var0) {
   }

   public static void testPrimitiveInstantiation() {
      testPrimitiveInstantiation(false);
   }

   public static void testPrimitiveInstantiation(boolean var0) {
      int var1 = 0;
      int var2 = 0;
      GraphicsPrimitive[] var3 = primitives;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         GraphicsPrimitive var5 = var3[var4];
         if (var5 instanceof GraphicsPrimitiveProxy) {
            GraphicsPrimitive var6 = ((GraphicsPrimitiveProxy)var5).instantiate();
            if (!var6.getSignature().equals(var5.getSignature()) || var6.getUniqueID() != var5.getUniqueID()) {
               System.out.println("r.getSignature == " + var6.getSignature());
               System.out.println("r.getUniqueID == " + var6.getUniqueID());
               System.out.println("p.getSignature == " + var5.getSignature());
               System.out.println("p.getUniqueID == " + var5.getUniqueID());
               throw new RuntimeException("Primitive " + var5 + " returns wrong signature for " + var6.getClass());
            }

            ++var2;
            if (var0) {
               System.out.println((Object)var6);
            }
         } else {
            if (var0) {
               System.out.println(var5 + " (not proxied).");
            }

            ++var1;
         }
      }

      System.out.println(var1 + " graphics primitives were not proxied.");
      System.out.println(var2 + " proxied graphics primitives resolved correctly.");
      System.out.println(var1 + var2 + " total graphics primitives");
   }

   public static void main(String[] var0) {
      if (needssort) {
         Arrays.sort(primitives, primSorter);
         needssort = false;
      }

      testPrimitiveInstantiation(var0.length > 0);
   }

   static {
      initIDs(GraphicsPrimitive.class, SurfaceType.class, CompositeType.class, SunGraphics2D.class, Color.class, AffineTransform.class, XORComposite.class, AlphaComposite.class, Path2D.class, Path2D.Float.class, SunHints.class);
      CustomComponent.register();
      GeneralRenderer.register();
      registerNativeLoops();
      primSorter = new Comparator() {
         public int compare(Object var1, Object var2) {
            int var3 = ((GraphicsPrimitive)var1).getUniqueID();
            int var4 = ((GraphicsPrimitive)var2).getUniqueID();
            return var3 == var4 ? 0 : (var3 < var4 ? -1 : 1);
         }
      };
      primFinder = new Comparator() {
         public int compare(Object var1, Object var2) {
            int var3 = ((GraphicsPrimitive)var1).getUniqueID();
            int var4 = ((GraphicsPrimitiveMgr.PrimitiveSpec)var2).uniqueID;
            return var3 == var4 ? 0 : (var3 < var4 ? -1 : 1);
         }
      };
   }

   private static class PrimitiveSpec {
      public int uniqueID;

      private PrimitiveSpec() {
      }

      // $FF: synthetic method
      PrimitiveSpec(Object var1) {
         this();
      }
   }
}
