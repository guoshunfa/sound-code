package sun.java2d.loops;

import java.awt.Rectangle;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public final class CustomComponent {
   public static void register() {
      Class var0 = CustomComponent.class;
      GraphicsPrimitive[] var1 = new GraphicsPrimitive[]{new GraphicsPrimitiveProxy(var0, "OpaqueCopyAnyToArgb", Blit.methodSignature, Blit.primTypeID, SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb), new GraphicsPrimitiveProxy(var0, "OpaqueCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any)};
      GraphicsPrimitiveMgr.register(var1);
   }

   public static Region getRegionOfInterest(SurfaceData var0, SurfaceData var1, Region var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      Region var9 = Region.getInstanceXYWH(var5, var6, var7, var8);
      var9 = var9.getIntersection(var1.getBounds());
      Rectangle var10 = var0.getBounds();
      var10.translate(var5 - var3, var6 - var4);
      var9 = var9.getIntersection(var10);
      if (var2 != null) {
         var9 = var9.getIntersection(var2);
      }

      return var9;
   }
}
