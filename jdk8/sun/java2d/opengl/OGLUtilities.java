package sun.java2d.opengl;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class OGLUtilities {
   public static final int UNDEFINED = 0;
   public static final int WINDOW = 1;
   public static final int PBUFFER = 2;
   public static final int TEXTURE = 3;
   public static final int FLIP_BACKBUFFER = 4;
   public static final int FBOBJECT = 5;

   private OGLUtilities() {
   }

   public static boolean isQueueFlusherThread() {
      return OGLRenderQueue.isQueueFlusherThread();
   }

   public static boolean invokeWithOGLContextCurrent(Graphics var0, Runnable var1) {
      OGLRenderQueue var2 = OGLRenderQueue.getInstance();
      var2.lock();

      try {
         if (var0 != null) {
            if (!(var0 instanceof SunGraphics2D)) {
               boolean var8 = false;
               return var8;
            }

            SurfaceData var3 = ((SunGraphics2D)var0).surfaceData;
            if (!(var3 instanceof OGLSurfaceData)) {
               boolean var4 = false;
               return var4;
            }

            OGLContext.validateContext((OGLSurfaceData)var3);
         }

         var2.flushAndInvokeNow(var1);
         OGLContext.invalidateCurrentContext();
         return true;
      } finally {
         var2.unlock();
      }
   }

   public static boolean invokeWithOGLSharedContextCurrent(GraphicsConfiguration var0, Runnable var1) {
      if (!(var0 instanceof OGLGraphicsConfig)) {
         return false;
      } else {
         OGLRenderQueue var2 = OGLRenderQueue.getInstance();
         var2.lock();

         try {
            OGLContext.setScratchSurface((OGLGraphicsConfig)var0);
            var2.flushAndInvokeNow(var1);
            OGLContext.invalidateCurrentContext();
         } finally {
            var2.unlock();
         }

         return true;
      }
   }

   public static Rectangle getOGLViewport(Graphics var0, int var1, int var2) {
      if (!(var0 instanceof SunGraphics2D)) {
         return null;
      } else {
         SunGraphics2D var3 = (SunGraphics2D)var0;
         SurfaceData var4 = var3.surfaceData;
         int var5 = var3.transX;
         int var6 = var3.transY;
         Rectangle var7 = var4.getBounds();
         int var9 = var7.height - (var6 + var2);
         return new Rectangle(var5, var9, var1, var2);
      }
   }

   public static Rectangle getOGLScissorBox(Graphics var0) {
      if (!(var0 instanceof SunGraphics2D)) {
         return null;
      } else {
         SunGraphics2D var1 = (SunGraphics2D)var0;
         SurfaceData var2 = var1.surfaceData;
         Region var3 = var1.getCompClip();
         if (!var3.isRectangular()) {
            return null;
         } else {
            int var4 = var3.getLoX();
            int var5 = var3.getLoY();
            int var6 = var3.getWidth();
            int var7 = var3.getHeight();
            Rectangle var8 = var2.getBounds();
            int var10 = var8.height - (var5 + var7);
            return new Rectangle(var4, var10, var6, var7);
         }
      }
   }

   public static Object getOGLSurfaceIdentifier(Graphics var0) {
      return !(var0 instanceof SunGraphics2D) ? null : ((SunGraphics2D)var0).surfaceData;
   }

   public static int getOGLSurfaceType(Graphics var0) {
      if (!(var0 instanceof SunGraphics2D)) {
         return 0;
      } else {
         SurfaceData var1 = ((SunGraphics2D)var0).surfaceData;
         return !(var1 instanceof OGLSurfaceData) ? 0 : ((OGLSurfaceData)var1).getType();
      }
   }

   public static int getOGLTextureType(Graphics var0) {
      if (!(var0 instanceof SunGraphics2D)) {
         return 0;
      } else {
         SurfaceData var1 = ((SunGraphics2D)var0).surfaceData;
         return !(var1 instanceof OGLSurfaceData) ? 0 : ((OGLSurfaceData)var1).getTextureTarget();
      }
   }
}
