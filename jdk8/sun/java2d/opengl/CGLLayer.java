package sun.java2d.opengl;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import sun.awt.CGraphicsConfig;
import sun.java2d.NullSurfaceData;
import sun.java2d.SurfaceData;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.macosx.CFRetainedResource;

public class CGLLayer extends CFRetainedResource {
   private LWWindowPeer peer;
   private int scale = 1;
   private SurfaceData surfaceData;

   private native long nativeCreateLayer();

   private static native void nativeSetScale(long var0, double var2);

   private static native void validate(long var0, CGLSurfaceData var2);

   private static native void blitTexture(long var0);

   public CGLLayer(LWWindowPeer var1) {
      super(0L, true);
      this.setPtr(this.nativeCreateLayer());
      this.peer = var1;
   }

   public long getPointer() {
      return this.ptr;
   }

   public Rectangle getBounds() {
      return this.peer.getBounds();
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return this.peer.getGraphicsConfiguration();
   }

   public boolean isOpaque() {
      return !this.peer.isTranslucent();
   }

   public int getTransparency() {
      return this.isOpaque() ? 1 : 3;
   }

   public Object getDestination() {
      return this.peer;
   }

   public SurfaceData replaceSurfaceData() {
      if (this.getBounds().isEmpty()) {
         this.surfaceData = NullSurfaceData.theInstance;
         return this.surfaceData;
      } else {
         CGraphicsConfig var1 = (CGraphicsConfig)this.getGraphicsConfiguration();
         this.surfaceData = var1.createSurfaceData(this);
         this.setScale(var1.getDevice().getScaleFactor());
         if (this.surfaceData instanceof CGLSurfaceData) {
            this.validate((CGLSurfaceData)this.surfaceData);
         }

         return this.surfaceData;
      }
   }

   public SurfaceData getSurfaceData() {
      return this.surfaceData;
   }

   public void validate(CGLSurfaceData var1) {
      OGLRenderQueue var2 = OGLRenderQueue.getInstance();
      var2.lock();

      try {
         this.execute((var1x) -> {
            validate(var1x, var1);
         });
      } finally {
         var2.unlock();
      }

   }

   public void dispose() {
      this.validate((CGLSurfaceData)null);
      super.dispose();
   }

   private void setScale(int var1) {
      if (this.scale != var1) {
         this.scale = var1;
         this.execute((var1x) -> {
            nativeSetScale(var1x, (double)this.scale);
         });
      }

   }

   private void drawInCGLContext() {
      OGLRenderQueue var1 = OGLRenderQueue.getInstance();
      var1.lock();

      try {
         this.execute((var0) -> {
            blitTexture(var0);
         });
      } finally {
         var1.unlock();
      }

   }
}
