package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.awt.geom.PathConsumer2D;
import sun.security.action.GetPropertyAction;

public abstract class RenderingEngine {
   private static RenderingEngine reImpl;

   public static synchronized RenderingEngine getInstance() {
      if (reImpl != null) {
         return reImpl;
      } else {
         reImpl = (RenderingEngine)AccessController.doPrivileged(new PrivilegedAction<RenderingEngine>() {
            public RenderingEngine run() {
               String var2 = System.getProperty("sun.java2d.renderer", "sun.dc.DuctusRenderingEngine");
               if (var2.equals("sun.dc.DuctusRenderingEngine")) {
                  try {
                     Class var8 = Class.forName("sun.dc.DuctusRenderingEngine");
                     return (RenderingEngine)var8.newInstance();
                  } catch (ReflectiveOperationException var7) {
                  }
               }

               ServiceLoader var3 = ServiceLoader.loadInstalled(RenderingEngine.class);
               RenderingEngine var4 = null;
               Iterator var5 = var3.iterator();

               while(var5.hasNext()) {
                  RenderingEngine var6 = (RenderingEngine)var5.next();
                  var4 = var6;
                  if (var6.getClass().getName().equals(var2)) {
                     break;
                  }
               }

               return var4;
            }
         });
         if (reImpl == null) {
            throw new InternalError("No RenderingEngine module found");
         } else {
            GetPropertyAction var0 = new GetPropertyAction("sun.java2d.renderer.trace");
            String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
            if (var1 != null) {
               reImpl = new RenderingEngine.Tracer(reImpl);
            }

            return reImpl;
         }
      }
   }

   public abstract Shape createStrokedShape(Shape var1, float var2, int var3, int var4, float var5, float[] var6, float var7);

   public abstract void strokeTo(Shape var1, AffineTransform var2, BasicStroke var3, boolean var4, boolean var5, boolean var6, PathConsumer2D var7);

   public abstract AATileGenerator getAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7);

   public abstract AATileGenerator getAATileGenerator(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Region var17, int[] var18);

   public abstract float getMinimumAAPenSize();

   public static void feedConsumer(PathIterator var0, PathConsumer2D var1) {
      for(float[] var2 = new float[6]; !var0.isDone(); var0.next()) {
         switch(var0.currentSegment(var2)) {
         case 0:
            var1.moveTo(var2[0], var2[1]);
            break;
         case 1:
            var1.lineTo(var2[0], var2[1]);
            break;
         case 2:
            var1.quadTo(var2[0], var2[1], var2[2], var2[3]);
            break;
         case 3:
            var1.curveTo(var2[0], var2[1], var2[2], var2[3], var2[4], var2[5]);
            break;
         case 4:
            var1.closePath();
         }
      }

   }

   static class Tracer extends RenderingEngine {
      RenderingEngine target;
      String name;

      public Tracer(RenderingEngine var1) {
         this.target = var1;
         this.name = var1.getClass().getName();
      }

      public Shape createStrokedShape(Shape var1, float var2, int var3, int var4, float var5, float[] var6, float var7) {
         System.out.println(this.name + ".createStrokedShape(" + var1.getClass().getName() + ", width = " + var2 + ", caps = " + var3 + ", join = " + var4 + ", miter = " + var5 + ", dashes = " + var6 + ", dashphase = " + var7 + ")");
         return this.target.createStrokedShape(var1, var2, var3, var4, var5, var6, var7);
      }

      public void strokeTo(Shape var1, AffineTransform var2, BasicStroke var3, boolean var4, boolean var5, boolean var6, PathConsumer2D var7) {
         System.out.println(this.name + ".strokeTo(" + var1.getClass().getName() + ", " + var2 + ", " + var3 + ", " + (var4 ? "thin" : "wide") + ", " + (var5 ? "normalized" : "pure") + ", " + (var6 ? "AA" : "non-AA") + ", " + var7.getClass().getName() + ")");
         this.target.strokeTo(var1, var2, var3, var4, var5, var6, var7);
      }

      public float getMinimumAAPenSize() {
         System.out.println(this.name + ".getMinimumAAPenSize()");
         return this.target.getMinimumAAPenSize();
      }

      public AATileGenerator getAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7) {
         System.out.println(this.name + ".getAATileGenerator(" + var1.getClass().getName() + ", " + var2 + ", " + var3 + ", " + var4 + ", " + (var5 ? "thin" : "wide") + ", " + (var6 ? "normalized" : "pure") + ")");
         return this.target.getAATileGenerator(var1, var2, var3, var4, var5, var6, var7);
      }

      public AATileGenerator getAATileGenerator(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Region var17, int[] var18) {
         System.out.println(this.name + ".getAATileGenerator(" + var1 + ", " + var3 + ", " + var5 + ", " + var7 + ", " + var9 + ", " + var11 + ", " + var13 + ", " + var15 + ", " + var17 + ")");
         return this.target.getAATileGenerator(var1, var3, var5, var7, var9, var11, var13, var15, var17, var18);
      }
   }
}
