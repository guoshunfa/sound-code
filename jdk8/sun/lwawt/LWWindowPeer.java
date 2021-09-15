package sun.lwawt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.WindowPeer;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.DisplayChangedListener;
import sun.awt.ExtendedKeyCodes;
import sun.awt.FullScreenCapable;
import sun.awt.SunToolkit;
import sun.awt.TimedWindowEvent;
import sun.awt.UngrabEvent;
import sun.java2d.NullSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.pipe.Region;
import sun.util.logging.PlatformLogger;

public class LWWindowPeer extends LWContainerPeer<Window, JComponent> implements FramePeer, DialogPeer, FullScreenCapable, DisplayChangedListener, PlatformEventNotifier {
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.lwawt.focus.LWWindowPeer");
   private final PlatformWindow platformWindow;
   private static final int MINIMUM_WIDTH = 1;
   private static final int MINIMUM_HEIGHT = 1;
   private Insets insets = new Insets(0, 0, 0, 0);
   private GraphicsDevice graphicsDevice;
   private GraphicsConfiguration graphicsConfig;
   private SurfaceData surfaceData;
   private final Object surfaceDataLock = new Object();
   private volatile int windowState = 0;
   private volatile boolean isMouseOver = false;
   private static volatile LWComponentPeer<?, ?> lastCommonMouseEventPeer;
   private volatile LWComponentPeer<?, ?> lastMouseEventPeer;
   private static final LWComponentPeer<?, ?>[] mouseDownTarget = new LWComponentPeer[3];
   private static int mouseClickButtons = 0;
   private volatile boolean isOpaque = true;
   private static final Font DEFAULT_FONT = new Font("Lucida Grande", 0, 13);
   private static LWWindowPeer grabbingWindow;
   private volatile boolean skipNextFocusChange;
   private static final Color nonOpaqueBackground = new Color(0, 0, 0, 0);
   private volatile boolean textured;
   private final LWWindowPeer.PeerType peerType;
   private final SecurityWarningWindow warningWindow;
   private volatile boolean targetFocusable;
   private LWWindowPeer blocker;

   public LWWindowPeer(Window var1, PlatformComponent var2, PlatformWindow var3, LWWindowPeer.PeerType var4) {
      super(var1, var2);
      this.platformWindow = var3;
      this.peerType = var4;
      Window var5 = var1.getOwner();
      LWWindowPeer var6 = var5 == null ? null : (LWWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var5);
      PlatformWindow var7 = var6 != null ? var6.getPlatformWindow() : null;
      GraphicsConfiguration var8 = ((Window)this.getTarget()).getGraphicsConfiguration();
      synchronized(this.getStateLock()) {
         this.graphicsConfig = var8;
      }

      if (!var1.isFontSet()) {
         var1.setFont(DEFAULT_FONT);
      }

      if (!var1.isBackgroundSet()) {
         var1.setBackground(SystemColor.window);
      }

      if (!var1.isForegroundSet()) {
         var1.setForeground(SystemColor.windowText);
      }

      var3.initialize(var1, this, var7);
      SecurityWarningWindow var9 = null;
      if (var1.getWarningString() != null && !AWTAccessor.getWindowAccessor().isTrayIconWindow(var1)) {
         LWToolkit var10 = (LWToolkit)Toolkit.getDefaultToolkit();
         var9 = var10.createSecurityWarning(var1, this);
      }

      this.warningWindow = var9;
   }

   void initializeImpl() {
      super.initializeImpl();
      if (this.getTarget() instanceof Frame) {
         this.setTitle(((Frame)this.getTarget()).getTitle());
         this.setState(((Frame)this.getTarget()).getExtendedState());
      } else if (this.getTarget() instanceof Dialog) {
         this.setTitle(((Dialog)this.getTarget()).getTitle());
      }

      this.updateAlwaysOnTopState();
      this.updateMinimumSize();
      this.updateFocusableWindowState();
      Shape var1 = ((Window)this.getTarget()).getShape();
      if (var1 != null) {
         this.applyShape(Region.getInstance(var1, (AffineTransform)null));
      }

      float var2 = ((Window)this.getTarget()).getOpacity();
      if (var2 < 1.0F) {
         this.setOpacity(var2);
      }

      this.setOpaque(((Window)this.getTarget()).isOpaque());
      this.updateInsets(this.platformWindow.getInsets());
      if (this.getSurfaceData() == null) {
         this.replaceSurfaceData(false);
      }

      this.activateDisplayListener();
   }

   public PlatformWindow getPlatformWindow() {
      return this.platformWindow;
   }

   protected LWWindowPeer getWindowPeerOrSelf() {
      return this;
   }

   protected void disposeImpl() {
      this.deactivateDisplayListener();
      SurfaceData var1 = this.getSurfaceData();
      synchronized(this.surfaceDataLock) {
         this.surfaceData = null;
      }

      if (var1 != null) {
         var1.invalidate();
      }

      if (this.isGrabbing()) {
         this.ungrab();
      }

      if (this.warningWindow != null) {
         this.warningWindow.dispose();
      }

      this.platformWindow.dispose();
      super.disposeImpl();
   }

   protected void setVisibleImpl(boolean var1) {
      if (!var1 && this.warningWindow != null) {
         this.warningWindow.setVisible(false, false);
      }

      this.updateFocusableWindowState();
      super.setVisibleImpl(var1);
      this.platformWindow.setVisible(var1);
      if (this.isSimpleWindow()) {
         LWKeyboardFocusManagerPeer var2 = LWKeyboardFocusManagerPeer.getInstance();
         if (var1) {
            if (!((Window)this.getTarget()).isAutoRequestFocus()) {
               return;
            }

            this.requestWindowFocus(CausedFocusEvent.Cause.ACTIVATION);
         } else if (var2.getCurrentFocusedWindow() == this.getTarget()) {
            LWWindowPeer var3 = getOwnerFrameDialog(this);
            if (var3 != null) {
               var3.requestWindowFocus(CausedFocusEvent.Cause.ACTIVATION);
            }
         }
      }

   }

   public final GraphicsConfiguration getGraphicsConfiguration() {
      synchronized(this.getStateLock()) {
         return this.graphicsConfig;
      }
   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      this.setGraphicsConfig(var1);
      return false;
   }

   protected final Graphics getOnscreenGraphics(Color var1, Color var2, Font var3) {
      if (this.getSurfaceData() == null) {
         return null;
      } else {
         if (var1 == null) {
            var1 = SystemColor.windowText;
         }

         if (var2 == null) {
            var2 = SystemColor.window;
         }

         if (var3 == null) {
            var3 = DEFAULT_FONT;
         }

         return this.platformWindow.transformGraphics(new SunGraphics2D(this.getSurfaceData(), (Color)var1, (Color)var2, var3));
      }
   }

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
      if ((var5 & 16384) != 0 || this.getPeerType() != LWWindowPeer.PeerType.VIEW_EMBEDDED_FRAME) {
         if ((var5 & 4) != 0) {
            var5 &= -5;
            var5 |= 2;
         }

         Rectangle var6 = this.constrainBounds(var1, var2, var3, var4);
         Rectangle var7 = new Rectangle(this.getBounds());
         if ((var5 & 3) != 0) {
            var7.x = var6.x;
            var7.y = var6.y;
         }

         if ((var5 & 3) != 0) {
            var7.width = var6.width;
            var7.height = var6.height;
         }

         this.platformWindow.setBounds(var7.x, var7.y, var7.width, var7.height);
      }
   }

   public Rectangle constrainBounds(Rectangle var1) {
      return this.constrainBounds(var1.x, var1.y, var1.width, var1.height);
   }

   public Rectangle constrainBounds(int var1, int var2, int var3, int var4) {
      if (var3 < 1) {
         var3 = 1;
      }

      if (var4 < 1) {
         var4 = 1;
      }

      int var5 = this.getLWGC().getMaxTextureWidth();
      int var6 = this.getLWGC().getMaxTextureHeight();
      if (var3 > var5) {
         var3 = var5;
      }

      if (var4 > var6) {
         var4 = var6;
      }

      return new Rectangle(var1, var2, var3, var4);
   }

   public Point getLocationOnScreen() {
      return this.platformWindow.getLocationOnScreen();
   }

   public Insets getInsets() {
      synchronized(this.getStateLock()) {
         return this.insets;
      }
   }

   public FontMetrics getFontMetrics(Font var1) {
      return this.platformWindow.getFontMetrics(var1);
   }

   public void toFront() {
      this.platformWindow.toFront();
   }

   public void toBack() {
      this.platformWindow.toBack();
   }

   public void setZOrder(ComponentPeer var1) {
      throw new RuntimeException("not implemented");
   }

   public void updateAlwaysOnTopState() {
      this.platformWindow.setAlwaysOnTop(((Window)this.getTarget()).isAlwaysOnTop());
   }

   public void updateFocusableWindowState() {
      this.targetFocusable = ((Window)this.getTarget()).isFocusableWindow();
      this.platformWindow.updateFocusableWindowState();
   }

   public void setModalBlocked(Dialog var1, boolean var2) {
      synchronized(getPeerTreeLock()) {
         ComponentPeer var4 = AWTAccessor.getComponentAccessor().getPeer(var1);
         if (var2 && var4 instanceof LWWindowPeer) {
            this.blocker = (LWWindowPeer)var4;
         } else {
            this.blocker = null;
         }
      }

      this.platformWindow.setModalBlocked(var2);
   }

   public void updateMinimumSize() {
      Dimension var1;
      if (((Window)this.getTarget()).isMinimumSizeSet()) {
         var1 = ((Window)this.getTarget()).getMinimumSize();
         var1.width = Math.max(var1.width, 1);
         var1.height = Math.max(var1.height, 1);
      } else {
         var1 = new Dimension(1, 1);
      }

      Dimension var2;
      if (((Window)this.getTarget()).isMaximumSizeSet()) {
         var2 = ((Window)this.getTarget()).getMaximumSize();
         var2.width = Math.min(var2.width, this.getLWGC().getMaxTextureWidth());
         var2.height = Math.min(var2.height, this.getLWGC().getMaxTextureHeight());
      } else {
         var2 = new Dimension(this.getLWGC().getMaxTextureWidth(), this.getLWGC().getMaxTextureHeight());
      }

      this.platformWindow.setSizeConstraints(var1.width, var1.height, var2.width, var2.height);
   }

   public void updateIconImages() {
      this.getPlatformWindow().updateIconImages();
   }

   public void setBackground(Color var1) {
      super.setBackground(var1);
      this.updateOpaque();
   }

   public void setOpacity(float var1) {
      this.getPlatformWindow().setOpacity(var1);
      this.repaintPeer();
   }

   public final void setOpaque(boolean var1) {
      if (this.isOpaque != var1) {
         this.isOpaque = var1;
         this.updateOpaque();
      }

   }

   private void updateOpaque() {
      this.getPlatformWindow().setOpaque(!this.isTranslucent());
      this.replaceSurfaceData(false);
      this.repaintPeer();
   }

   public void updateWindow() {
   }

   public final boolean isTextured() {
      return this.textured;
   }

   public final void setTextured(boolean var1) {
      this.textured = var1;
   }

   public final boolean isTranslucent() {
      synchronized(this.getStateLock()) {
         return !this.isOpaque || this.isShaped() || this.isTextured();
      }
   }

   final void applyShapeImpl(Region var1) {
      super.applyShapeImpl(var1);
      this.updateOpaque();
   }

   public void repositionSecurityWarning() {
      if (this.warningWindow != null) {
         AWTAccessor.ComponentAccessor var1 = AWTAccessor.getComponentAccessor();
         Window var2 = (Window)this.getTarget();
         int var3 = var1.getX(var2);
         int var4 = var1.getY(var2);
         int var5 = var1.getWidth(var2);
         int var6 = var1.getHeight(var2);
         this.warningWindow.reposition(var3, var4, var5, var6);
      }

   }

   public void setTitle(String var1) {
      this.platformWindow.setTitle(var1 == null ? "" : var1);
   }

   public void setMenuBar(MenuBar var1) {
      this.platformWindow.setMenuBar(var1);
   }

   public void setResizable(boolean var1) {
      this.platformWindow.setResizable(var1);
   }

   public void setState(int var1) {
      this.platformWindow.setWindowState(var1);
   }

   public int getState() {
      return this.windowState;
   }

   public void setMaximizedBounds(Rectangle var1) {
   }

   public void setBoundsPrivate(int var1, int var2, int var3, int var4) {
      this.setBounds(var1, var2, var3, var4, 16387);
   }

   public Rectangle getBoundsPrivate() {
      throw new RuntimeException("not implemented");
   }

   public void blockWindows(List<Window> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Window var3 = (Window)var2.next();
         WindowPeer var4 = (WindowPeer)AWTAccessor.getComponentAccessor().getPeer(var3);
         if (var4 != null) {
            var4.setModalBlocked((Dialog)this.getTarget(), true);
         }
      }

   }

   public void notifyIconify(boolean var1) {
      WindowEvent var2 = new WindowEvent((Window)this.getTarget(), var1 ? 203 : 204);
      this.postEvent(var2);
      int var3 = var1 ? 1 : 0;
      this.postWindowStateChangedEvent(var3);
      if (!var1) {
         this.repaintPeer();
      }

   }

   public void notifyZoom(boolean var1) {
      int var2 = var1 ? 6 : 0;
      this.postWindowStateChangedEvent(var2);
   }

   public void notifyExpose(Rectangle var1) {
      this.repaintPeer(var1);
   }

   public void notifyReshape(int var1, int var2, int var3, int var4) {
      Rectangle var5 = this.getBounds();
      boolean var6 = this.updateInsets(this.platformWindow.getInsets());
      boolean var7 = var1 != var5.x || var2 != var5.y;
      boolean var8 = var3 != var5.width || var4 != var5.height;
      if (var7 || var8 || var6) {
         this.setBounds(var1, var2, var3, var4, 3, false, false);
         boolean var9 = this.updateGraphicsDevice();
         if (var8 || var9) {
            this.replaceSurfaceData();
            this.updateMinimumSize();
         }

         if (var7 || var6) {
            this.handleMove(var1, var2, true);
         }

         if (var8 || var6 || var9) {
            this.handleResize(var3, var4, true);
            this.repaintPeer();
         }

         this.repositionSecurityWarning();
      }
   }

   private void clearBackground(int var1, int var2) {
      Graphics var3 = this.getOnscreenGraphics(this.getForeground(), this.getBackground(), this.getFont());
      if (var3 != null) {
         try {
            if (var3 instanceof Graphics2D) {
               ((Graphics2D)var3).setComposite(AlphaComposite.Src);
            }

            if (this.isTranslucent()) {
               var3.setColor(nonOpaqueBackground);
               var3.fillRect(0, 0, var1, var2);
            }

            if (!this.isTextured()) {
               if (var3 instanceof SunGraphics2D) {
                  ((SunGraphics2D)var3).constrain(0, 0, var1, var2, this.getRegion());
               }

               var3.setColor(this.getBackground());
               var3.fillRect(0, 0, var1, var2);
            }
         } finally {
            var3.dispose();
         }
      }

   }

   public void notifyUpdateCursor() {
      this.getLWToolkit().getCursorManager().updateCursorLater(this);
   }

   public void notifyActivation(boolean var1, LWWindowPeer var2) {
      Window var3 = var2 == null ? null : (Window)var2.getTarget();
      this.changeFocusedWindow(var1, var3);
   }

   public void notifyNCMouseDown() {
      if (grabbingWindow != null && !grabbingWindow.isOneOfOwnersOf(this)) {
         grabbingWindow.ungrab();
      }

   }

   public void notifyMouseEvent(int var1, long var2, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, byte[] var12) {
      Rectangle var13 = this.getBounds();
      Object var14 = this.findPeerAt(var13.x + var5, var13.y + var6);
      Point var15;
      Component var16;
      if (var1 == 505) {
         this.isMouseOver = false;
         if (this.lastMouseEventPeer != null) {
            if (this.lastMouseEventPeer.isEnabled()) {
               var15 = this.lastMouseEventPeer.windowToLocal(var5, var6, this);
               var16 = this.lastMouseEventPeer.getTarget();
               this.postMouseExitedEvent(var16, var2, var9, var15, var7, var8, var10, var11, var4);
            }

            if (lastCommonMouseEventPeer != null && lastCommonMouseEventPeer.getWindowPeerOrSelf() == this) {
               lastCommonMouseEventPeer = null;
            }

            this.lastMouseEventPeer = null;
         }
      } else if (var1 == 504) {
         this.isMouseOver = true;
         if (var14 != null) {
            if (((LWComponentPeer)var14).isEnabled()) {
               var15 = ((LWComponentPeer)var14).windowToLocal(var5, var6, this);
               var16 = ((LWComponentPeer)var14).getTarget();
               this.postMouseEnteredEvent(var16, var2, var9, var15, var7, var8, var10, var11, var4);
            }

            lastCommonMouseEventPeer = (LWComponentPeer)var14;
            this.lastMouseEventPeer = (LWComponentPeer)var14;
         }
      } else {
         PlatformWindow var22 = LWToolkit.getLWToolkit().getPlatformWindowUnderMouse();
         LWWindowPeer var23 = var22 != null ? var22.getPeer() : null;
         if (var23 != this && var23 != null) {
            LWComponentPeer var17 = var23.findPeerAt(var13.x + var5, var13.y + var6);
            var23.generateMouseEnterExitEventsForComponents(var2, var4, var5, var6, var7, var8, var9, var10, var11, var17);
         } else {
            this.generateMouseEnterExitEventsForComponents(var2, var4, var5, var6, var7, var8, var9, var10, var11, (LWComponentPeer)var14);
         }

         int var24 = var4 > 0 ? MouseEvent.getMaskForButton(var4) : 0;
         int var18 = var9 & ~var24;
         int var19 = var4 > 3 ? 1 : var4 - 1;
         if (var1 == 501) {
            if (!this.isGrabbing() && grabbingWindow != null && !grabbingWindow.isOneOfOwnersOf(this)) {
               grabbingWindow.ungrab();
            }

            if (var18 == 0) {
               mouseClickButtons = var24;
            } else {
               mouseClickButtons |= var24;
            }

            this.requestWindowFocus(CausedFocusEvent.Cause.MOUSE_EVENT);
            mouseDownTarget[var19] = (LWComponentPeer)var14;
         } else if (var1 == 506) {
            var14 = mouseDownTarget[var19];
            mouseClickButtons &= ~var9;
         } else if (var1 == 502) {
            var14 = mouseDownTarget[var19];
            if ((var9 & var24) == 0) {
               mouseDownTarget[var19] = null;
            }
         }

         if (var14 == null) {
            var14 = this;
         }

         Point var20 = ((LWComponentPeer)var14).windowToLocal(var5, var6, this);
         if (((LWComponentPeer)var14).isEnabled()) {
            MouseEvent var21 = new MouseEvent(((LWComponentPeer)var14).getTarget(), var1, var2, var9, var20.x, var20.y, var7, var8, var10, var11, var4);
            this.postEvent(var21);
         }

         if (var1 == 502) {
            if ((mouseClickButtons & var24) != 0 && ((LWComponentPeer)var14).isEnabled()) {
               this.postEvent(new MouseEvent(((LWComponentPeer)var14).getTarget(), 500, var2, var9, var20.x, var20.y, var7, var8, var10, var11, var4));
            }

            mouseClickButtons &= ~var24;
         }
      }

      this.notifyUpdateCursor();
   }

   private void generateMouseEnterExitEventsForComponents(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, LWComponentPeer<?, ?> var11) {
      if (this.isMouseOver && var11 != this.lastMouseEventPeer) {
         Point var12;
         Component var13;
         if (this.lastMouseEventPeer != null && this.lastMouseEventPeer.isEnabled()) {
            var12 = this.lastMouseEventPeer.windowToLocal(var4, var5, this);
            var13 = this.lastMouseEventPeer.getTarget();
            this.postMouseExitedEvent(var13, var1, var8, var12, var6, var7, var9, var10, var3);
         }

         lastCommonMouseEventPeer = var11;
         this.lastMouseEventPeer = var11;
         if (var11 != null && var11.isEnabled()) {
            var12 = var11.windowToLocal(var4, var5, this);
            var13 = var11.getTarget();
            this.postMouseEnteredEvent(var13, var1, var8, var12, var6, var7, var9, var10, var3);
         }

      }
   }

   private void postMouseEnteredEvent(Component var1, long var2, int var4, Point var5, int var6, int var7, int var8, boolean var9, int var10) {
      this.updateSecurityWarningVisibility();
      this.postEvent(new MouseEvent(var1, 504, var2, var4, var5.x, var5.y, var6, var7, var8, var9, var10));
   }

   private void postMouseExitedEvent(Component var1, long var2, int var4, Point var5, int var6, int var7, int var8, boolean var9, int var10) {
      this.updateSecurityWarningVisibility();
      this.postEvent(new MouseEvent(var1, 505, var2, var4, var5.x, var5.y, var6, var7, var8, var9, var10));
   }

   public void notifyMouseWheelEvent(long var1, int var3, int var4, int var5, int var6, int var7, int var8, double var9, byte[] var11) {
      Rectangle var12 = this.getBounds();
      LWComponentPeer var13 = this.findPeerAt(var12.x + var3, var12.y + var4);
      if (var13 != null && var13.isEnabled()) {
         Point var14 = var13.windowToLocal(var3, var4, this);
         this.postEvent(new MouseWheelEvent(var13.getTarget(), 507, var1, var5, var14.x, var14.y, 0, 0, 0, false, var6, var7, var8, var9));
      }
   }

   public void notifyKeyEvent(int var1, long var2, int var4, int var5, char var6, int var7) {
      LWKeyboardFocusManagerPeer var8 = LWKeyboardFocusManagerPeer.getInstance();
      Object var9 = var8.getCurrentFocusOwner();
      if (var9 == null) {
         var9 = var8.getCurrentFocusedWindow();
         if (var9 == null) {
            var9 = this.getTarget();
         }
      }

      KeyEvent var10 = new KeyEvent((Component)var9, var1, var2, var4, var5, var6, var7);
      AWTAccessor.getKeyEventAccessor().setExtendedKeyCode(var10, var6 == '\uffff' ? (long)var5 : (long)ExtendedKeyCodes.getExtendedKeyCodeForChar(var6));
      this.postEvent(var10);
   }

   private void activateDisplayListener() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ((SunGraphicsEnvironment)var1).addDisplayChangedListener(this);
   }

   private void deactivateDisplayListener() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ((SunGraphicsEnvironment)var1).removeDisplayChangedListener(this);
   }

   private void postWindowStateChangedEvent(int var1) {
      if (this.getTarget() instanceof Frame) {
         AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.getTarget(), var1);
      }

      WindowEvent var2 = new WindowEvent((Window)this.getTarget(), 209, this.windowState, var1);
      this.postEvent(var2);
      this.windowState = var1;
      this.updateSecurityWarningVisibility();
   }

   private static int getGraphicsConfigScreen(GraphicsConfiguration var0) {
      GraphicsDevice var1 = var0.getDevice();
      GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] var3 = var2.getScreenDevices();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == var1) {
            return var4;
         }
      }

      return 0;
   }

   private boolean setGraphicsConfig(GraphicsConfiguration var1) {
      synchronized(this.getStateLock()) {
         if (this.graphicsConfig == var1) {
            return false;
         } else {
            this.graphicsConfig = var1;
            return true;
         }
      }
   }

   public boolean updateGraphicsDevice() {
      GraphicsDevice var1 = this.platformWindow.getGraphicsDevice();
      synchronized(this.getStateLock()) {
         if (this.graphicsDevice == var1) {
            return false;
         }

         this.graphicsDevice = var1;
      }

      final GraphicsConfiguration var2 = var1.getDefaultConfiguration();
      if (!this.setGraphicsConfig(var2)) {
         return false;
      } else {
         SunToolkit.executeOnEventHandlerThread(this.getTarget(), new Runnable() {
            public void run() {
               AWTAccessor.getComponentAccessor().setGraphicsConfiguration(LWWindowPeer.this.getTarget(), var2);
            }
         });
         return true;
      }
   }

   public final void displayChanged() {
      if (this.updateGraphicsDevice()) {
         this.updateMinimumSize();
      }

      this.replaceSurfaceData();
      this.repaintPeer();
   }

   public final void paletteChanged() {
   }

   public SurfaceData getSurfaceData() {
      synchronized(this.surfaceDataLock) {
         return this.surfaceData;
      }
   }

   private void replaceSurfaceData() {
      this.replaceSurfaceData(true);
   }

   private void replaceSurfaceData(boolean var1) {
      synchronized(this.surfaceDataLock) {
         SurfaceData var3 = this.getSurfaceData();
         this.surfaceData = this.platformWindow.replaceSurfaceData();
         Rectangle var4 = this.getSize();
         if (this.getSurfaceData() != null && var3 != this.getSurfaceData()) {
            this.clearBackground(var4.width, var4.height);
         }

         if (var1) {
            this.blitSurfaceData(var3, this.getSurfaceData());
         }

         if (var3 != null && var3 != this.getSurfaceData()) {
            var3.flush();
         }
      }

      flushOnscreenGraphics();
   }

   private void blitSurfaceData(SurfaceData var1, SurfaceData var2) {
      if (var1 != var2 && var1 != null && var2 != null && !(var2 instanceof NullSurfaceData) && !(var1 instanceof NullSurfaceData) && var1.getSurfaceType().equals(var2.getSurfaceType()) && var1.getDefaultScale() == var2.getDefaultScale()) {
         Rectangle var3 = var1.getBounds();
         Blit var4 = Blit.locate(var1.getSurfaceType(), CompositeType.Src, var2.getSurfaceType());
         if (var4 != null) {
            var4.Blit(var1, var2, AlphaComposite.Src, (Region)null, 0, 0, 0, 0, var3.width, var3.height);
         }
      }

   }

   public final boolean updateInsets(Insets var1) {
      synchronized(this.getStateLock()) {
         if (this.insets.equals(var1)) {
            return false;
         } else {
            this.insets = var1;
            return true;
         }
      }
   }

   public static LWWindowPeer getWindowUnderCursor() {
      return lastCommonMouseEventPeer != null ? lastCommonMouseEventPeer.getWindowPeerOrSelf() : null;
   }

   public static LWComponentPeer<?, ?> getPeerUnderCursor() {
      return lastCommonMouseEventPeer;
   }

   public boolean requestWindowFocus(CausedFocusEvent.Cause var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
         focusLog.fine("requesting native focus to " + this);
      }

      if (!this.focusAllowedFor()) {
         focusLog.fine("focus is not allowed");
         return false;
      } else if (this.platformWindow.rejectFocusRequest(var1)) {
         return false;
      } else {
         AppContext var2 = AWTAccessor.getComponentAccessor().getAppContext(this.getTarget());
         KeyboardFocusManager var3 = AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentKeyboardFocusManager(var2);
         Window var4 = var3.getActiveWindow();
         Window var5 = LWKeyboardFocusManagerPeer.getInstance().getCurrentFocusedWindow();
         if (this.isSimpleWindow()) {
            LWWindowPeer var6 = getOwnerFrameDialog(this);
            if (var6 != null && !var6.platformWindow.isActive()) {
               if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                  focusLog.fine("requesting native focus to the owner " + var6);
               }

               LWWindowPeer var7 = var4 == null ? null : (LWWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var4);
               if (var7 != null && var7.platformWindow.isActive()) {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                     focusLog.fine("the opposite is " + var7);
                  }

                  var7.skipNextFocusChange = true;
               }

               var6.skipNextFocusChange = true;
               var6.platformWindow.requestWindowFocus();
            }

            this.changeFocusedWindow(true, var5);
            return true;
         } else if (this.getTarget() == var4 && !((Window)this.getTarget()).hasFocus()) {
            this.changeFocusedWindow(true, var5);
            return true;
         } else {
            return this.platformWindow.requestWindowFocus();
         }
      }
   }

   protected boolean focusAllowedFor() {
      Window var1 = (Window)this.getTarget();
      return var1.isVisible() && var1.isEnabled() && this.isFocusableWindow();
   }

   private boolean isFocusableWindow() {
      boolean var1 = this.targetFocusable;
      if (this.isSimpleWindow()) {
         LWWindowPeer var2 = getOwnerFrameDialog(this);
         if (var2 == null) {
            return false;
         } else {
            return var1 && var2.targetFocusable;
         }
      } else {
         return var1;
      }
   }

   public boolean isSimpleWindow() {
      Window var1 = (Window)this.getTarget();
      return !(var1 instanceof Dialog) && !(var1 instanceof Frame);
   }

   public void emulateActivation(boolean var1) {
      this.changeFocusedWindow(var1, (Window)null);
   }

   private boolean isOneOfOwnersOf(LWWindowPeer var1) {
      for(Window var2 = var1 != null ? ((Window)var1.getTarget()).getOwner() : null; var2 != null; var2 = var2.getOwner()) {
         if ((LWWindowPeer)var2.getPeer() == this) {
            return true;
         }
      }

      return false;
   }

   protected void changeFocusedWindow(boolean var1, Window var2) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
         focusLog.fine((var1 ? "gaining" : "loosing") + " focus window: " + this);
      }

      if (this.skipNextFocusChange) {
         focusLog.fine("skipping focus change");
         this.skipNextFocusChange = false;
      } else if (!this.isFocusableWindow() && var1) {
         focusLog.fine("the window is not focusable");
      } else {
         if (var1) {
            synchronized(getPeerTreeLock()) {
               if (this.blocker != null) {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                     focusLog.finest("the window is blocked by " + this.blocker);
                  }

                  return;
               }
            }
         }

         if (!var1 && (this.isGrabbing() || this.isOneOfOwnersOf(grabbingWindow))) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
               focusLog.fine("ungrabbing on " + grabbingWindow);
            }

            grabbingWindow.ungrab();
         }

         LWKeyboardFocusManagerPeer var3 = LWKeyboardFocusManagerPeer.getInstance();
         if (var1 || var3.getCurrentFocusedWindow() == this.getTarget()) {
            var3.setCurrentFocusedWindow(var1 ? (Window)this.getTarget() : null);
            int var4 = var1 ? 207 : 208;
            TimedWindowEvent var5 = new TimedWindowEvent((Window)this.getTarget(), var4, var2, System.currentTimeMillis());
            this.postEvent(var5);
         }
      }
   }

   static LWWindowPeer getOwnerFrameDialog(LWWindowPeer var0) {
      Window var1;
      for(var1 = var0 != null ? ((Window)var0.getTarget()).getOwner() : null; var1 != null && !(var1 instanceof Frame) && !(var1 instanceof Dialog); var1 = var1.getOwner()) {
      }

      return var1 == null ? null : (LWWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var1);
   }

   public LWWindowPeer getBlocker() {
      synchronized(getPeerTreeLock()) {
         LWWindowPeer var2 = this.blocker;
         if (var2 == null) {
            return null;
         } else {
            while(var2.blocker != null) {
               var2 = var2.blocker;
            }

            return var2;
         }
      }
   }

   public void enterFullScreenMode() {
      this.platformWindow.enterFullScreenMode();
      this.updateSecurityWarningVisibility();
   }

   public void exitFullScreenMode() {
      this.platformWindow.exitFullScreenMode();
      this.updateSecurityWarningVisibility();
   }

   public long getLayerPtr() {
      return this.getPlatformWindow().getLayerPtr();
   }

   void grab() {
      if (grabbingWindow != null && !this.isGrabbing()) {
         grabbingWindow.ungrab();
      }

      grabbingWindow = this;
   }

   final void ungrab(boolean var1) {
      if (this.isGrabbing()) {
         grabbingWindow = null;
         if (var1) {
            this.postEvent(new UngrabEvent(this.getTarget()));
         }
      }

   }

   void ungrab() {
      this.ungrab(true);
   }

   private boolean isGrabbing() {
      return this == grabbingWindow;
   }

   public LWWindowPeer.PeerType getPeerType() {
      return this.peerType;
   }

   public void updateSecurityWarningVisibility() {
      if (this.warningWindow != null) {
         if (this.isVisible()) {
            boolean var1 = false;
            if (!this.platformWindow.isFullScreenMode() && this.isVisible()) {
               if (LWKeyboardFocusManagerPeer.getInstance().getCurrentFocusedWindow() == this.getTarget()) {
                  var1 = true;
               }

               if (this.platformWindow.isUnderMouse() || this.warningWindow.isUnderMouse()) {
                  var1 = true;
               }
            }

            this.warningWindow.setVisible(var1, true);
         }
      }
   }

   public String toString() {
      return super.toString() + " [target is " + this.getTarget() + "]";
   }

   public static enum PeerType {
      SIMPLEWINDOW,
      FRAME,
      DIALOG,
      EMBEDDED_FRAME,
      VIEW_EMBEDDED_FRAME,
      LW_FRAME;
   }
}
