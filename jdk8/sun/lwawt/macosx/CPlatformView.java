package sun.lwawt.macosx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import sun.awt.CGraphicsConfig;
import sun.awt.CGraphicsEnvironment;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.CGLLayer;
import sun.java2d.opengl.CGLSurfaceData;
import sun.lwawt.LWWindowPeer;

public class CPlatformView extends CFRetainedResource {
   private LWWindowPeer peer;
   private SurfaceData surfaceData;
   private CGLLayer windowLayer;
   private CPlatformResponder responder;

   private native long nativeCreateView(int var1, int var2, int var3, int var4, long var5);

   private static native void nativeSetAutoResizable(long var0, boolean var2);

   private static native int nativeGetNSViewDisplayID(long var0);

   private static native Rectangle2D nativeGetLocationOnScreen(long var0);

   private static native boolean nativeIsViewUnderMouse(long var0);

   public CPlatformView() {
      super(0L, true);
   }

   public void initialize(LWWindowPeer var1, CPlatformResponder var2) {
      this.initializeBase(var1, var2);
      if (!LWCToolkit.getSunAwtDisableCALayers()) {
         this.windowLayer = this.createCGLayer();
      }

      this.setPtr(this.nativeCreateView(0, 0, 0, 0, this.getWindowLayerPtr()));
   }

   public CGLLayer createCGLayer() {
      return new CGLLayer(this.peer);
   }

   protected void initializeBase(LWWindowPeer var1, CPlatformResponder var2) {
      this.peer = var1;
      this.responder = var2;
   }

   public long getAWTView() {
      return this.ptr;
   }

   public boolean isOpaque() {
      return !this.peer.isTranslucent();
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.execute((var4x) -> {
         CWrapper.NSView.setFrame(var4x, var1, var2, var3, var4);
      });
   }

   public Rectangle getBounds() {
      return this.peer.getBounds();
   }

   public Object getDestination() {
      return this.peer;
   }

   public void setToolTip(String var1) {
      this.execute((var1x) -> {
         CWrapper.NSView.setToolTip(var1x, var1);
      });
   }

   public SurfaceData replaceSurfaceData() {
      if (!LWCToolkit.getSunAwtDisableCALayers()) {
         this.surfaceData = this.windowLayer.replaceSurfaceData();
      } else if (this.surfaceData == null) {
         CGraphicsConfig var1 = (CGraphicsConfig)this.getGraphicsConfiguration();
         this.surfaceData = var1.createSurfaceData(this);
      } else {
         this.validateSurface();
      }

      return this.surfaceData;
   }

   private void validateSurface() {
      if (this.surfaceData != null) {
         ((CGLSurfaceData)this.surfaceData).validate();
      }

   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return this.peer.getGraphicsConfiguration();
   }

   public SurfaceData getSurfaceData() {
      return this.surfaceData;
   }

   public void dispose() {
      if (!LWCToolkit.getSunAwtDisableCALayers()) {
         this.windowLayer.dispose();
      }

      super.dispose();
   }

   public long getWindowLayerPtr() {
      return !LWCToolkit.getSunAwtDisableCALayers() ? this.windowLayer.getPointer() : 0L;
   }

   public void setAutoResizable(boolean var1) {
      this.execute((var1x) -> {
         nativeSetAutoResizable(var1x, var1);
      });
   }

   public boolean isUnderMouse() {
      AtomicBoolean var1 = new AtomicBoolean();
      this.execute((var1x) -> {
         var1.set(nativeIsViewUnderMouse(var1x));
      });
      return var1.get();
   }

   public GraphicsDevice getGraphicsDevice() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      CGraphicsEnvironment var2 = (CGraphicsEnvironment)var1;
      AtomicInteger var3 = new AtomicInteger();
      this.execute((var1x) -> {
         var3.set(nativeGetNSViewDisplayID(var1x));
      });
      GraphicsDevice var4 = var2.getScreenDevice(var3.get());
      if (var4 == null) {
         var4 = var1.getDefaultScreenDevice();
      }

      return var4;
   }

   public Point getLocationOnScreen() {
      AtomicReference var1 = new AtomicReference();
      this.execute((var1x) -> {
         var1.set(nativeGetLocationOnScreen(var1x).getBounds());
      });
      Rectangle var2 = (Rectangle)var1.get();
      return var2 != null ? new Point(var2.x, var2.y) : new Point(0, 0);
   }

   private void deliverResize(int var1, int var2, int var3, int var4) {
      this.peer.notifyReshape(var1, var2, var3, var4);
   }

   private void deliverMouseEvent(NSEvent var1) {
      int var2 = var1.getX();
      int var3 = this.getBounds().height - var1.getY();
      if (var1.getType() == 22) {
         this.responder.handleScrollEvent(var2, var3, var1.getModifierFlags(), var1.getScrollDeltaX(), var1.getScrollDeltaY(), var1.getScrollPhase());
      } else {
         this.responder.handleMouseEvent(var1.getType(), var1.getModifierFlags(), var1.getButtonNumber(), var1.getClickCount(), var2, var3, var1.getAbsX(), var1.getAbsY());
      }

   }

   private void deliverKeyEvent(NSEvent var1) {
      this.responder.handleKeyEvent(var1.getType(), var1.getModifierFlags(), var1.getCharacters(), var1.getCharactersIgnoringModifiers(), var1.getKeyCode(), true, false);
   }

   private void deliverWindowDidExposeEvent() {
      this.peer.notifyExpose(this.peer.getSize());
   }
}
