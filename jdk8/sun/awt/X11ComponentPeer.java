package sun.awt;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.java2d.SurfaceData;

public interface X11ComponentPeer {
   long getWindow();

   long getContentWindow();

   SurfaceData getSurfaceData();

   GraphicsConfiguration getGraphicsConfiguration();

   ColorModel getColorModel();

   Rectangle getBounds();

   Graphics getGraphics();

   Object getTarget();

   void setFullScreenExclusiveModeState(boolean var1);
}
