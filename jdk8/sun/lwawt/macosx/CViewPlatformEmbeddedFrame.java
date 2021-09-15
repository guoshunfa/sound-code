package sun.lwawt.macosx;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Window;
import sun.awt.CausedFocusEvent;
import sun.java2d.SurfaceData;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;

public class CViewPlatformEmbeddedFrame implements PlatformWindow {
   private CPlatformView view;
   private LWWindowPeer peer;
   private CViewEmbeddedFrame target;
   private CPlatformResponder responder;

   public void initialize(Window var1, LWWindowPeer var2, PlatformWindow var3) {
      this.peer = var2;
      this.target = (CViewEmbeddedFrame)var1;
      this.responder = new CPlatformResponder(var2, false);
      this.view = new CPlatformView();
      this.view.initialize(var2, this.responder);
      CWrapper.NSView.addSubview(this.target.getEmbedderHandle(), this.view.getAWTView());
      this.view.setAutoResizable(true);
   }

   public long getNSViewPtr() {
      return this.view.getAWTView();
   }

   public long getLayerPtr() {
      return this.view.getWindowLayerPtr();
   }

   public LWWindowPeer getPeer() {
      return this.peer;
   }

   public void dispose() {
      this.view.execute(CWrapper.NSView::removeFromSuperview);
      this.view.dispose();
   }

   public void setVisible(boolean var1) {
      this.view.execute((var1x) -> {
         CWrapper.NSView.setHidden(var1x, !var1);
      });
   }

   public void setTitle(String var1) {
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.view.setBounds(var1, var2, var3, var4);
      this.peer.notifyReshape(var1, var2, var3, var4);
   }

   public GraphicsDevice getGraphicsDevice() {
      return this.view.getGraphicsDevice();
   }

   public Point getLocationOnScreen() {
      return this.view.getLocationOnScreen();
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public FontMetrics getFontMetrics(Font var1) {
      throw new RuntimeException("Not implemented");
   }

   public SurfaceData getScreenSurface() {
      return this.view.getSurfaceData();
   }

   public SurfaceData replaceSurfaceData() {
      return this.view.replaceSurfaceData();
   }

   public void setModalBlocked(boolean var1) {
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
      return false;
   }

   public boolean requestWindowFocus() {
      return true;
   }

   public boolean isActive() {
      return this.target.isParentWindowActive();
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

   public boolean isUnderMouse() {
      return this.view.isUnderMouse();
   }
}
