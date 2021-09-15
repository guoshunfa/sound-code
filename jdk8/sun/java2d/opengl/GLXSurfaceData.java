package sun.java2d.opengl;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.awt.X11ComponentPeer;
import sun.java2d.SurfaceData;

public abstract class GLXSurfaceData extends OGLSurfaceData {
   protected X11ComponentPeer peer;
   private GLXGraphicsConfig graphicsConfig;

   private native void initOps(X11ComponentPeer var1, long var2);

   protected native boolean initPbuffer(long var1, long var3, boolean var5, int var6, int var7);

   protected GLXSurfaceData(X11ComponentPeer var1, GLXGraphicsConfig var2, ColorModel var3, int var4) {
      super(var2, var3, var4);
      this.peer = var1;
      this.graphicsConfig = var2;
      this.initOps(var1, this.graphicsConfig.getAData());
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   public static GLXSurfaceData.GLXWindowSurfaceData createData(X11ComponentPeer var0) {
      GLXGraphicsConfig var1 = getGC(var0);
      return new GLXSurfaceData.GLXWindowSurfaceData(var0, var1);
   }

   public static GLXSurfaceData.GLXOffScreenSurfaceData createData(X11ComponentPeer var0, Image var1, int var2) {
      GLXGraphicsConfig var3 = getGC(var0);
      Rectangle var4 = var0.getBounds();
      return (GLXSurfaceData.GLXOffScreenSurfaceData)(var2 == 4 ? new GLXSurfaceData.GLXOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var0.getColorModel(), 4) : new GLXSurfaceData.GLXVSyncOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var0.getColorModel(), var2));
   }

   public static GLXSurfaceData.GLXOffScreenSurfaceData createData(GLXGraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, int var5) {
      return new GLXSurfaceData.GLXOffScreenSurfaceData((X11ComponentPeer)null, var0, var1, var2, var4, var3, var5);
   }

   public static GLXGraphicsConfig getGC(X11ComponentPeer var0) {
      if (var0 != null) {
         return (GLXGraphicsConfig)var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var2 = var1.getDefaultScreenDevice();
         return (GLXGraphicsConfig)var2.getDefaultConfiguration();
      }
   }

   public static class GLXOffScreenSurfaceData extends GLXSurfaceData {
      private Image offscreenImage;
      private int width;
      private int height;

      public GLXOffScreenSurfaceData(X11ComponentPeer var1, GLXGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var6, var7);
         this.width = var3;
         this.height = var4;
         this.offscreenImage = var5;
         this.initSurface(var3, var4);
      }

      public SurfaceData getReplacement() {
         return restoreContents(this.offscreenImage);
      }

      public Rectangle getBounds() {
         if (this.type == 4) {
            Rectangle var1 = this.peer.getBounds();
            var1.x = var1.y = 0;
            return var1;
         } else {
            return new Rectangle(this.width, this.height);
         }
      }

      public Object getDestination() {
         return this.offscreenImage;
      }
   }

   public static class GLXVSyncOffScreenSurfaceData extends GLXSurfaceData.GLXOffScreenSurfaceData {
      private GLXSurfaceData.GLXOffScreenSurfaceData flipSurface;

      public GLXVSyncOffScreenSurfaceData(X11ComponentPeer var1, GLXGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
         this.flipSurface = GLXSurfaceData.createData(var1, var5, 4);
      }

      public SurfaceData getFlipSurface() {
         return this.flipSurface;
      }

      public void flush() {
         this.flipSurface.flush();
         super.flush();
      }
   }

   public static class GLXWindowSurfaceData extends GLXSurfaceData {
      public GLXWindowSurfaceData(X11ComponentPeer var1, GLXGraphicsConfig var2) {
         super(var1, var2, var1.getColorModel(), 1);
      }

      public SurfaceData getReplacement() {
         return this.peer.getSurfaceData();
      }

      public Rectangle getBounds() {
         Rectangle var1 = this.peer.getBounds();
         var1.x = var1.y = 0;
         return var1;
      }

      public Object getDestination() {
         return this.peer.getTarget();
      }
   }
}
