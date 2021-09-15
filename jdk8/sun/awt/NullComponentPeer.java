package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.CanvasPeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.PanelPeer;
import sun.java2d.pipe.Region;

public class NullComponentPeer implements LightweightPeer, CanvasPeer, PanelPeer {
   public boolean isObscured() {
      return false;
   }

   public boolean canDetermineObscurity() {
      return false;
   }

   public boolean isFocusable() {
      return false;
   }

   public void setVisible(boolean var1) {
   }

   public void show() {
   }

   public void hide() {
   }

   public void setEnabled(boolean var1) {
   }

   public void enable() {
   }

   public void disable() {
   }

   public void paint(Graphics var1) {
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
   }

   public void print(Graphics var1) {
   }

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
   }

   public void reshape(int var1, int var2, int var3, int var4) {
   }

   public void coalescePaintEvent(PaintEvent var1) {
   }

   public boolean handleEvent(Event var1) {
      return false;
   }

   public void handleEvent(AWTEvent var1) {
   }

   public Dimension getPreferredSize() {
      return new Dimension(1, 1);
   }

   public Dimension getMinimumSize() {
      return new Dimension(1, 1);
   }

   public ColorModel getColorModel() {
      return null;
   }

   public Graphics getGraphics() {
      return null;
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
   }

   public FontMetrics getFontMetrics(Font var1) {
      return null;
   }

   public void dispose() {
   }

   public void setForeground(Color var1) {
   }

   public void setBackground(Color var1) {
   }

   public void setFont(Font var1) {
   }

   public void updateCursorImmediately() {
   }

   public void setCursor(Cursor var1) {
   }

   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return false;
   }

   public Image createImage(ImageProducer var1) {
      return null;
   }

   public Image createImage(int var1, int var2) {
      return null;
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      return false;
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      return 0;
   }

   public Dimension preferredSize() {
      return this.getPreferredSize();
   }

   public Dimension minimumSize() {
      return this.getMinimumSize();
   }

   public Point getLocationOnScreen() {
      return new Point(0, 0);
   }

   public Insets getInsets() {
      return this.insets();
   }

   public void beginValidate() {
   }

   public void endValidate() {
   }

   public Insets insets() {
      return new Insets(0, 0, 0, 0);
   }

   public boolean isPaintPending() {
      return false;
   }

   public boolean handlesWheelScrolling() {
      return false;
   }

   public VolatileImage createVolatileImage(int var1, int var2) {
      return null;
   }

   public void beginLayout() {
   }

   public void endLayout() {
   }

   public void createBuffers(int var1, BufferCapabilities var2) throws AWTException {
      throw new AWTException("Page-flipping is not allowed on a lightweight component");
   }

   public Image getBackBuffer() {
      throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
   }

   public void flip(int var1, int var2, int var3, int var4, BufferCapabilities.FlipContents var5) {
      throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
   }

   public void destroyBuffers() {
   }

   public boolean isReparentSupported() {
      return false;
   }

   public void reparent(ContainerPeer var1) {
      throw new UnsupportedOperationException();
   }

   public void layout() {
   }

   public Rectangle getBounds() {
      return new Rectangle(0, 0, 0, 0);
   }

   public void applyShape(Region var1) {
   }

   public void setZOrder(ComponentPeer var1) {
   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      return false;
   }

   public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration var1) {
      return var1;
   }
}
