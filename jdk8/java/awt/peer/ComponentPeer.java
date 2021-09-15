package java.awt.peer;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;

public interface ComponentPeer {
   int SET_LOCATION = 1;
   int SET_SIZE = 2;
   int SET_BOUNDS = 3;
   int SET_CLIENT_SIZE = 4;
   int RESET_OPERATION = 5;
   int NO_EMBEDDED_CHECK = 16384;
   int DEFAULT_OPERATION = 3;

   boolean isObscured();

   boolean canDetermineObscurity();

   void setVisible(boolean var1);

   void setEnabled(boolean var1);

   void paint(Graphics var1);

   void print(Graphics var1);

   void setBounds(int var1, int var2, int var3, int var4, int var5);

   void handleEvent(AWTEvent var1);

   void coalescePaintEvent(PaintEvent var1);

   Point getLocationOnScreen();

   Dimension getPreferredSize();

   Dimension getMinimumSize();

   ColorModel getColorModel();

   Graphics getGraphics();

   FontMetrics getFontMetrics(Font var1);

   void dispose();

   void setForeground(Color var1);

   void setBackground(Color var1);

   void setFont(Font var1);

   void updateCursorImmediately();

   boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6);

   boolean isFocusable();

   Image createImage(ImageProducer var1);

   Image createImage(int var1, int var2);

   VolatileImage createVolatileImage(int var1, int var2);

   boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4);

   int checkImage(Image var1, int var2, int var3, ImageObserver var4);

   GraphicsConfiguration getGraphicsConfiguration();

   boolean handlesWheelScrolling();

   void createBuffers(int var1, BufferCapabilities var2) throws AWTException;

   Image getBackBuffer();

   void flip(int var1, int var2, int var3, int var4, BufferCapabilities.FlipContents var5);

   void destroyBuffers();

   void reparent(ContainerPeer var1);

   boolean isReparentSupported();

   void layout();

   void applyShape(Region var1);

   void setZOrder(ComponentPeer var1);

   boolean updateGraphicsData(GraphicsConfiguration var1);
}
