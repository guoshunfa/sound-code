package sun.lwawt;

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

public interface PlatformWindow {
   void initialize(Window var1, LWWindowPeer var2, PlatformWindow var3);

   void dispose();

   void setVisible(boolean var1);

   void setTitle(String var1);

   void setBounds(int var1, int var2, int var3, int var4);

   GraphicsDevice getGraphicsDevice();

   Point getLocationOnScreen();

   Insets getInsets();

   FontMetrics getFontMetrics(Font var1);

   SurfaceData getScreenSurface();

   SurfaceData replaceSurfaceData();

   void setModalBlocked(boolean var1);

   void toFront();

   void toBack();

   void setMenuBar(MenuBar var1);

   void setAlwaysOnTop(boolean var1);

   void updateFocusableWindowState();

   boolean rejectFocusRequest(CausedFocusEvent.Cause var1);

   boolean requestWindowFocus();

   boolean isActive();

   void setResizable(boolean var1);

   void setSizeConstraints(int var1, int var2, int var3, int var4);

   Graphics transformGraphics(Graphics var1);

   void updateIconImages();

   void setOpacity(float var1);

   void setOpaque(boolean var1);

   void enterFullScreenMode();

   void exitFullScreenMode();

   boolean isFullScreenMode();

   void setWindowState(int var1);

   long getLayerPtr();

   LWWindowPeer getPeer();

   boolean isUnderMouse();
}
