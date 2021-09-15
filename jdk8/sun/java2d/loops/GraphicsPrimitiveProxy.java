package sun.java2d.loops;

public class GraphicsPrimitiveProxy extends GraphicsPrimitive {
   private Class owner;
   private String relativeClassName;

   public GraphicsPrimitiveProxy(Class var1, String var2, String var3, int var4, SurfaceType var5, CompositeType var6, SurfaceType var7) {
      super(var3, var4, var5, var6, var7);
      this.owner = var1;
      this.relativeClassName = var2;
   }

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("makePrimitive called on a Proxy!");
   }

   GraphicsPrimitive instantiate() {
      String var1 = getPackageName(this.owner.getName()) + "." + this.relativeClassName;

      try {
         Class var2 = Class.forName(var1);
         GraphicsPrimitive var3 = (GraphicsPrimitive)var2.newInstance();
         if (!this.satisfiesSameAs(var3)) {
            throw new RuntimeException("Primitive " + var3 + " incompatible with proxy for " + var1);
         } else {
            return var3;
         }
      } catch (ClassNotFoundException var4) {
         throw new RuntimeException(var4.toString());
      } catch (InstantiationException var5) {
         throw new RuntimeException(var5.toString());
      } catch (IllegalAccessException var6) {
         throw new RuntimeException(var6.toString());
      }
   }

   private static String getPackageName(String var0) {
      int var1 = var0.lastIndexOf(46);
      return var1 < 0 ? var0 : var0.substring(0, var1);
   }

   public GraphicsPrimitive traceWrap() {
      return this.instantiate().traceWrap();
   }
}
