package sun.lwawt.macosx;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import sun.awt.CGraphicsDevice;
import sun.awt.CGraphicsEnvironment;
import sun.awt.CausedFocusEvent;
import sun.awt.LightweightFrame;
import sun.java2d.SurfaceData;
import sun.lwawt.LWLightweightFramePeer;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;

public class CPlatformLWWindow extends CPlatformWindow {
   public void initialize(Window var1, LWWindowPeer var2, PlatformWindow var3) {
      this.initializeBase(var1, var2, var3, new CPlatformLWView());
   }

   public void toggleFullScreen() {
   }

   public void setMenuBar(MenuBar var1) {
   }

   public void dispose() {
   }

   public FontMetrics getFontMetrics(Font var1) {
      return null;
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public Point getLocationOnScreen() {
      return null;
   }

   public SurfaceData getScreenSurface() {
      return null;
   }

   public SurfaceData replaceSurfaceData() {
      return null;
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      if (this.getPeer() != null) {
         this.getPeer().notifyReshape(var1, var2, var3, var4);
      }

   }

   public void setVisible(boolean var1) {
   }

   public void setTitle(String var1) {
   }

   public void updateIconImages() {
   }

   public SurfaceData getSurfaceData() {
      return null;
   }

   public void toBack() {
   }

   public void toFront() {
   }

   public void setResizable(boolean var1) {
   }

   public void setSizeConstraints(int var1, int var2, int var3, int var4) {
   }

   public boolean rejectFocusRequest(CausedFocusEvent.Cause var1) {
      return false;
   }

   public boolean requestWindowFocus() {
      return true;
   }

   public boolean isActive() {
      return true;
   }

   public void updateFocusableWindowState() {
   }

   public Graphics transformGraphics(Graphics var1) {
      return null;
   }

   public void setAlwaysOnTop(boolean var1) {
   }

   public void setOpacity(float var1) {
   }

   public void setOpaque(boolean var1) {
   }

   public void enterFullScreenMode() {
   }

   public void exitFullScreenMode() {
   }

   public void setWindowState(int var1) {
   }

   public LWWindowPeer getPeer() {
      return super.getPeer();
   }

   public CPlatformView getContentView() {
      return super.getContentView();
   }

   public long getLayerPtr() {
      return 0L;
   }

   public GraphicsDevice getGraphicsDevice() {
      CGraphicsEnvironment var1 = (CGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
      LWLightweightFramePeer var2 = (LWLightweightFramePeer)this.getPeer();
      int var3 = ((LightweightFrame)var2.getTarget()).getScaleFactor();
      Rectangle var4 = ((LightweightFrame)var2.getTarget()).getHostBounds();
      GraphicsDevice[] var5 = var1.getScreenDevices();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         GraphicsDevice var8 = var5[var7];
         if (var8.getDefaultConfiguration().getBounds().intersects(var4) && ((CGraphicsDevice)var8).getScaleFactor() == var3) {
            return var8;
         }
      }

      return var1.getDefaultScreenDevice();
   }
}
