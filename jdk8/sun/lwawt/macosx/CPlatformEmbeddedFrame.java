package sun.lwawt.macosx;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Window;
import sun.awt.CausedFocusEvent;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.CGLLayer;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;
import sun.util.logging.PlatformLogger;

public class CPlatformEmbeddedFrame implements PlatformWindow {
   private static final PlatformLogger focusLogger = PlatformLogger.getLogger("sun.lwawt.macosx.focus.CPlatformEmbeddedFrame");
   private CGLLayer windowLayer;
   private LWWindowPeer peer;
   private CEmbeddedFrame target;
   private volatile int screenX = 0;
   private volatile int screenY = 0;

   public void initialize(Window var1, LWWindowPeer var2, PlatformWindow var3) {
      this.peer = var2;
      this.windowLayer = new CGLLayer(var2);
      this.target = (CEmbeddedFrame)var1;
   }

   public LWWindowPeer getPeer() {
      return this.peer;
   }

   public long getLayerPtr() {
      return this.windowLayer.getPointer();
   }

   public void dispose() {
      this.windowLayer.dispose();
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.screenX = var1;
      this.screenY = var2;
      this.peer.notifyReshape(var1, var2, var3, var4);
   }

   public GraphicsDevice getGraphicsDevice() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return var1.getDefaultScreenDevice();
   }

   public Point getLocationOnScreen() {
      return new Point(this.screenX, this.screenY);
   }

   public FontMetrics getFontMetrics(Font var1) {
      throw new RuntimeException("Not implemented");
   }

   public SurfaceData getScreenSurface() {
      return this.windowLayer.getSurfaceData();
   }

   public SurfaceData replaceSurfaceData() {
      return this.windowLayer.replaceSurfaceData();
   }

   public void setVisible(boolean var1) {
   }

   public void setTitle(String var1) {
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public void toFront() {
   }

   public void toBack() {
   }

   public void setMenuBar(MenuBar var1) {
   }

   public void setAlwaysOnTop(boolean var1) {
   }

   public void updateFocusableWindowState() {
   }

   public boolean rejectFocusRequest(CausedFocusEvent.Cause var1) {
      if (var1 != CausedFocusEvent.Cause.MOUSE_EVENT && !this.target.isParentWindowActive()) {
         focusLogger.fine("the embedder is inactive, so the request is rejected");
         return true;
      } else {
         return false;
      }
   }

   public boolean requestWindowFocus() {
      return true;
   }

   public boolean isActive() {
      return true;
   }

   public void setResizable(boolean var1) {
   }

   public void setSizeConstraints(int var1, int var2, int var3, int var4) {
   }

   public Graphics transformGraphics(Graphics var1) {
      return var1;
   }

   public void updateIconImages() {
   }

   public void setOpacity(float var1) {
   }

   public void setOpaque(boolean var1) {
   }

   public void enterFullScreenMode() {
   }

   public void exitFullScreenMode() {
   }

   public boolean isFullScreenMode() {
      return false;
   }

   public void setWindowState(int var1) {
   }

   public void setModalBlocked(boolean var1) {
   }

   public boolean isUnderMouse() {
      throw new RuntimeException("Not implemented");
   }
}
