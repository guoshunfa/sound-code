package sun.lwawt;

import com.sun.java.swing.SwingUtilities3;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import sun.awt.AWTAccessor;
import sun.awt.CausedFocusEvent;
import sun.awt.PaintEventDispatcher;
import sun.awt.RepaintArea;
import sun.awt.SunToolkit;
import sun.awt.event.IgnorePaintEvent;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.ToolkitImage;
import sun.java2d.SunGraphics2D;
import sun.java2d.opengl.OGLRenderQueue;
import sun.java2d.pipe.Region;
import sun.lwawt.macosx.CDropTarget;
import sun.util.logging.PlatformLogger;

public abstract class LWComponentPeer<T extends Component, D extends JComponent> implements ComponentPeer, DropTargetPeer {
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.lwawt.focus.LWComponentPeer");
   private final Object stateLock = new Object();
   private static final Object peerTreeLock = new Object();
   private final T target;
   private final LWContainerPeer<?, ?> containerPeer;
   private final LWWindowPeer windowPeer;
   private final AtomicBoolean disposed = new AtomicBoolean(false);
   private final Rectangle bounds = new Rectangle();
   private Region region;
   private boolean visible = false;
   private boolean enabled = true;
   private Color background;
   private Color foreground;
   private Font font;
   private final RepaintArea targetPaintArea = new LWRepaintArea();
   private volatile boolean isLayouting;
   private final D delegate;
   private Container delegateContainer;
   private Component delegateDropTarget;
   private final Object dropTargetLock = new Object();
   private int fNumDropTargets = 0;
   private CDropTarget fDropTarget = null;
   private final PlatformComponent platformComponent;
   static final char WIDE_CHAR = '0';
   private Image backBuffer;

   LWComponentPeer(T var1, PlatformComponent var2) {
      this.target = var1;
      this.platformComponent = var2;
      Container var3 = SunToolkit.getNativeContainer(var1);
      this.containerPeer = (LWContainerPeer)LWToolkit.targetToPeer(var3);
      this.windowPeer = this.containerPeer != null ? this.containerPeer.getWindowPeerOrSelf() : null;
      if (this.containerPeer != null) {
         this.containerPeer.addChildPeer(this);
      }

      AWTEventListener var4 = null;
      synchronized(Toolkit.getDefaultToolkit()) {
         try {
            var4 = this.getToolkitAWTEventListener();
            this.setToolkitAWTEventListener((AWTEventListener)null);
            synchronized(this.getDelegateLock()) {
               this.delegate = this.createDelegate();
               if (this.delegate == null) {
                  return;
               }

               this.delegate.setVisible(false);
               this.delegateContainer = new LWComponentPeer.DelegateContainer();
               this.delegateContainer.add(this.delegate);
               this.delegateContainer.addNotify();
               this.delegate.addNotify();
               resetColorsAndFont(this.delegate);
               this.delegate.setOpaque(true);
            }
         } finally {
            this.setToolkitAWTEventListener(var4);
         }

         SwingUtilities3.setDelegateRepaintManager(this.delegate, new RepaintManager() {
            public void addDirtyRegion(JComponent var1, int var2, int var3, int var4, int var5) {
               LWComponentPeer.this.repaintPeer(SwingUtilities.convertRectangle(var1, new Rectangle(var2, var3, var4, var5), LWComponentPeer.this.getDelegate()));
            }
         });
      }
   }

   protected final AWTEventListener getToolkitAWTEventListener() {
      return (AWTEventListener)AccessController.doPrivileged(new PrivilegedAction<AWTEventListener>() {
         public AWTEventListener run() {
            Toolkit var1 = Toolkit.getDefaultToolkit();

            try {
               Field var2 = Toolkit.class.getDeclaredField("eventListener");
               var2.setAccessible(true);
               return (AWTEventListener)var2.get(var1);
            } catch (Exception var3) {
               throw new InternalError(var3.toString());
            }
         }
      });
   }

   protected final void setToolkitAWTEventListener(final AWTEventListener var1) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Toolkit var1x = Toolkit.getDefaultToolkit();

            try {
               Field var2 = Toolkit.class.getDeclaredField("eventListener");
               var2.setAccessible(true);
               var2.set(var1x, var1);
               return null;
            } catch (Exception var3) {
               throw new InternalError(var3.toString());
            }
         }
      });
   }

   D createDelegate() {
      return null;
   }

   final D getDelegate() {
      return this.delegate;
   }

   Component getDelegateFocusOwner() {
      return this.getDelegate();
   }

   public final void initialize() {
      this.platformComponent.initialize(this.getPlatformWindow());
      this.initializeImpl();
      this.setVisible(this.target.isVisible());
   }

   void initializeImpl() {
      this.setBackground(this.target.getBackground());
      this.setForeground(this.target.getForeground());
      this.setFont(this.target.getFont());
      this.setBounds(this.target.getBounds());
      this.setEnabled(this.target.isEnabled());
   }

   private static void resetColorsAndFont(Container var0) {
      var0.setBackground((Color)null);
      var0.setForeground((Color)null);
      var0.setFont((Font)null);

      for(int var1 = 0; var1 < var0.getComponentCount(); ++var1) {
         resetColorsAndFont((Container)var0.getComponent(var1));
      }

   }

   final Object getStateLock() {
      return this.stateLock;
   }

   final Object getDelegateLock() {
      return this.getTarget().getTreeLock();
   }

   protected static final Object getPeerTreeLock() {
      return peerTreeLock;
   }

   public final T getTarget() {
      return this.target;
   }

   protected final LWWindowPeer getWindowPeer() {
      return this.windowPeer;
   }

   protected LWWindowPeer getWindowPeerOrSelf() {
      return this.getWindowPeer();
   }

   protected final LWContainerPeer<?, ?> getContainerPeer() {
      return this.containerPeer;
   }

   public PlatformWindow getPlatformWindow() {
      LWWindowPeer var1 = this.getWindowPeer();
      return var1.getPlatformWindow();
   }

   public LWToolkit getLWToolkit() {
      return LWToolkit.getLWToolkit();
   }

   public final void dispose() {
      if (this.disposed.compareAndSet(false, true)) {
         this.disposeImpl();
      }

   }

   protected void disposeImpl() {
      this.destroyBuffers();
      LWContainerPeer var1 = this.getContainerPeer();
      if (var1 != null) {
         var1.removeChildPeer(this);
      }

      this.platformComponent.dispose();
      LWToolkit.targetDisposedPeer(this.getTarget(), this);
   }

   public final boolean isDisposed() {
      return this.disposed.get();
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return this.getWindowPeer().getGraphicsConfiguration();
   }

   public final LWGraphicsConfig getLWGC() {
      return (LWGraphicsConfig)this.getGraphicsConfiguration();
   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      return false;
   }

   public Graphics getGraphics() {
      Graphics var1 = this.getOnscreenGraphics();
      if (var1 != null) {
         synchronized(getPeerTreeLock()) {
            this.applyConstrain(var1);
         }
      }

      return var1;
   }

   public final Graphics getOnscreenGraphics() {
      LWWindowPeer var1 = this.getWindowPeerOrSelf();
      return var1.getOnscreenGraphics(this.getForeground(), this.getBackground(), this.getFont());
   }

   private void applyConstrain(Graphics var1) {
      SunGraphics2D var2 = (SunGraphics2D)var1;
      Rectangle var3 = this.localToWindow(this.getSize());
      var2.constrain(var3.x, var3.y, var3.width, var3.height, this.getVisibleRegion());
   }

   Region getVisibleRegion() {
      return computeVisibleRect(this, this.getRegion());
   }

   static final Region computeVisibleRect(LWComponentPeer<?, ?> var0, Region var1) {
      LWContainerPeer var2 = var0.getContainerPeer();
      if (var2 != null) {
         Rectangle var3 = var0.getBounds();
         var1 = var1.getTranslatedRegion(var3.x, var3.y);
         var1 = var1.getIntersection(var2.getRegion());
         var1 = var1.getIntersection(var2.getContentSize());
         var1 = var2.cutChildren(var1, var0);
         var1 = computeVisibleRect(var2, var1);
         var1 = var1.getTranslatedRegion(-var3.x, -var3.y);
      }

      return var1;
   }

   public ColorModel getColorModel() {
      return this.getGraphicsConfiguration().getColorModel();
   }

   public boolean isTranslucent() {
      return false;
   }

   public final void createBuffers(int var1, BufferCapabilities var2) throws AWTException {
      this.getLWGC().assertOperationSupported(var1, var2);
      Image var3 = this.getLWGC().createBackBuffer(this);
      synchronized(this.getStateLock()) {
         this.backBuffer = var3;
      }
   }

   public final Image getBackBuffer() {
      synchronized(this.getStateLock()) {
         if (this.backBuffer != null) {
            return this.backBuffer;
         }
      }

      throw new IllegalStateException("Buffers have not been created");
   }

   public final void flip(int var1, int var2, int var3, int var4, BufferCapabilities.FlipContents var5) {
      this.getLWGC().flip(this, this.getBackBuffer(), var1, var2, var3, var4, var5);
   }

   public final void destroyBuffers() {
      Image var1;
      synchronized(this.getStateLock()) {
         var1 = this.backBuffer;
         this.backBuffer = null;
      }

      this.getLWGC().destroyBackBuffer(var1);
   }

   public void setBounds(Rectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height, 3);
   }

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
      this.setBounds(var1, var2, var3, var4, var5, true, false);
   }

   protected void setBounds(int var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
      Rectangle var8;
      synchronized(this.getStateLock()) {
         var8 = new Rectangle(this.bounds);
         if ((var5 & 3) != 0) {
            this.bounds.x = var1;
            this.bounds.y = var2;
         }

         if ((var5 & 3) != 0) {
            this.bounds.width = var3;
            this.bounds.height = var4;
         }
      }

      boolean var9 = var8.x != var1 || var8.y != var2;
      boolean var10 = var8.width != var3 || var8.height != var4;
      if (var9 || var10) {
         JComponent var11 = this.getDelegate();
         if (var11 != null) {
            synchronized(this.getDelegateLock()) {
               this.delegateContainer.setBounds(0, 0, var3, var4);
               var11.setBounds(this.delegateContainer.getBounds());
               var11.validate();
            }
         }

         Point var12 = this.localToWindow(0, 0);
         this.platformComponent.setBounds(var12.x, var12.y, var3, var4);
         if (var6) {
            this.repaintOldNewBounds(var8);
            if (var10) {
               this.handleResize(var3, var4, var7);
            }

            if (var9) {
               this.handleMove(var1, var2, var7);
            }
         }

      }
   }

   public final Rectangle getBounds() {
      synchronized(this.getStateLock()) {
         return this.bounds.getBounds();
      }
   }

   public final Rectangle getSize() {
      synchronized(this.getStateLock()) {
         return new Rectangle(this.bounds.width, this.bounds.height);
      }
   }

   public Point getLocationOnScreen() {
      Point var1 = this.getWindowPeer().getLocationOnScreen();
      Point var2 = this.localToWindow(0, 0);
      return new Point(var1.x + var2.x, var1.y + var2.y);
   }

   Cursor getCursor(Point var1) {
      return this.getTarget().getCursor();
   }

   public void setBackground(Color var1) {
      Color var2 = this.getBackground();
      if (var2 != var1 && (var2 == null || !var2.equals(var1))) {
         synchronized(this.getStateLock()) {
            this.background = var1;
         }

         JComponent var3 = this.getDelegate();
         if (var3 != null) {
            synchronized(this.getDelegateLock()) {
               var3.setBackground(var1);
            }
         } else {
            this.repaintPeer();
         }

      }
   }

   public final Color getBackground() {
      synchronized(this.getStateLock()) {
         return this.background;
      }
   }

   public void setForeground(Color var1) {
      Color var2 = this.getForeground();
      if (var2 != var1 && (var2 == null || !var2.equals(var1))) {
         synchronized(this.getStateLock()) {
            this.foreground = var1;
         }

         JComponent var3 = this.getDelegate();
         if (var3 != null) {
            synchronized(this.getDelegateLock()) {
               var3.setForeground(var1);
            }
         } else {
            this.repaintPeer();
         }

      }
   }

   protected final Color getForeground() {
      synchronized(this.getStateLock()) {
         return this.foreground;
      }
   }

   public void setFont(Font var1) {
      Font var2 = this.getFont();
      if (var2 != var1 && (var2 == null || !var2.equals(var1))) {
         synchronized(this.getStateLock()) {
            this.font = var1;
         }

         JComponent var3 = this.getDelegate();
         if (var3 != null) {
            synchronized(this.getDelegateLock()) {
               var3.setFont(var1);
            }
         } else {
            this.repaintPeer();
         }

      }
   }

   protected final Font getFont() {
      synchronized(this.getStateLock()) {
         return this.font;
      }
   }

   public FontMetrics getFontMetrics(Font var1) {
      Graphics var2 = this.getOnscreenGraphics();
      if (var2 != null) {
         FontMetrics var3;
         try {
            var3 = var2.getFontMetrics(var1);
         } finally {
            var2.dispose();
         }

         return var3;
      } else {
         synchronized(this.getDelegateLock()) {
            return this.delegateContainer.getFontMetrics(var1);
         }
      }
   }

   public void setEnabled(boolean var1) {
      boolean var2 = var1;
      LWContainerPeer var3 = this.getContainerPeer();
      if (var3 != null) {
         var2 = var1 & var3.isEnabled();
      }

      synchronized(this.getStateLock()) {
         if (this.enabled == var2) {
            return;
         }

         this.enabled = var2;
      }

      JComponent var4 = this.getDelegate();
      if (var4 != null) {
         synchronized(this.getDelegateLock()) {
            var4.setEnabled(var2);
         }
      } else {
         this.repaintPeer();
      }

   }

   public final boolean isEnabled() {
      synchronized(this.getStateLock()) {
         return this.enabled;
      }
   }

   public void setVisible(boolean var1) {
      synchronized(this.getStateLock()) {
         if (this.visible == var1) {
            return;
         }

         this.visible = var1;
      }

      this.setVisibleImpl(var1);
   }

   protected void setVisibleImpl(boolean var1) {
      JComponent var2 = this.getDelegate();
      if (var2 != null) {
         synchronized(this.getDelegateLock()) {
            var2.setVisible(var1);
         }
      }

      if (this.visible) {
         this.repaintPeer();
      } else {
         this.repaintParent(this.getBounds());
      }

   }

   public final boolean isVisible() {
      synchronized(this.getStateLock()) {
         return this.visible;
      }
   }

   public void paint(Graphics var1) {
      this.getTarget().paint(var1);
   }

   public void print(Graphics var1) {
      this.getTarget().print(var1);
   }

   public void reparent(ContainerPeer var1) {
      throw new UnsupportedOperationException("ComponentPeer.reparent()");
   }

   public boolean isReparentSupported() {
      return false;
   }

   public void setZOrder(ComponentPeer var1) {
      LWContainerPeer var2 = this.getContainerPeer();
      var2.setChildPeerZOrder(this, (LWComponentPeer)var1);
   }

   public void coalescePaintEvent(PaintEvent var1) {
      if (!(var1 instanceof IgnorePaintEvent)) {
         Rectangle var2 = var1.getUpdateRect();
         if (var2 != null && !var2.isEmpty()) {
            this.targetPaintArea.add(var2, var1.getID());
         }
      }

   }

   public void layout() {
   }

   public boolean isObscured() {
      return false;
   }

   public boolean canDetermineObscurity() {
      return false;
   }

   public Dimension getPreferredSize() {
      Dimension var1;
      synchronized(this.getDelegateLock()) {
         var1 = this.getDelegate().getPreferredSize();
      }

      return this.validateSize(var1);
   }

   public Dimension getMinimumSize() {
      Dimension var1;
      synchronized(this.getDelegateLock()) {
         var1 = this.getDelegate().getMinimumSize();
      }

      return this.validateSize(var1);
   }

   private Dimension validateSize(Dimension var1) {
      if (var1.width == 0 || var1.height == 0) {
         FontMetrics var2 = this.getFontMetrics(this.getFont());
         var1.width = var2.charWidth('0');
         var1.height = var2.getHeight();
      }

      return var1;
   }

   public void updateCursorImmediately() {
      this.getLWToolkit().getCursorManager().updateCursor();
   }

   public boolean isFocusable() {
      return false;
   }

   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
         focusLog.finest("lightweightChild=" + var1 + ", temporary=" + var2 + ", focusedWindowChangeAllowed=" + var3 + ", time= " + var4 + ", cause=" + var6);
      }

      if (LWKeyboardFocusManagerPeer.processSynchronousLightweightTransfer(this.getTarget(), var1, var2, var3, var4)) {
         return true;
      } else {
         int var7 = LWKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight(this.getTarget(), var1, var2, var3, var4, var6);
         switch(var7) {
         case 0:
            return false;
         case 1:
            return true;
         case 2:
            Window var8 = SunToolkit.getContainingWindow(this.getTarget());
            if (var8 == null) {
               focusLog.fine("request rejected, parentWindow is null");
               LWKeyboardFocusManagerPeer.removeLastFocusRequest(this.getTarget());
               return false;
            } else {
               LWWindowPeer var9 = (LWWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var8);
               if (var9 == null) {
                  focusLog.fine("request rejected, parentPeer is null");
                  LWKeyboardFocusManagerPeer.removeLastFocusRequest(this.getTarget());
                  return false;
               } else {
                  if (!var3) {
                     LWWindowPeer var10 = var9.isSimpleWindow() ? LWWindowPeer.getOwnerFrameDialog(var9) : var9;
                     if (var10 == null || !var10.getPlatformWindow().isActive()) {
                        if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                           focusLog.fine("request rejected, focusedWindowChangeAllowed==false, decoratedPeer is inactive: " + var10);
                        }

                        LWKeyboardFocusManagerPeer.removeLastFocusRequest(this.getTarget());
                        return false;
                     }
                  }

                  boolean var13 = var9.requestWindowFocus(var6);
                  if (var13 && var8.isFocused()) {
                     LWKeyboardFocusManagerPeer var11 = LWKeyboardFocusManagerPeer.getInstance();
                     Component var12 = var11.getCurrentFocusOwner();
                     return LWKeyboardFocusManagerPeer.deliverFocus(var1, this.getTarget(), var2, var3, var4, var6, var12);
                  }

                  if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                     focusLog.fine("request rejected, res= " + var13 + ", parentWindow.isFocused()=" + var8.isFocused());
                  }

                  LWKeyboardFocusManagerPeer.removeLastFocusRequest(this.getTarget());
                  return false;
               }
            }
         default:
            return false;
         }
      }
   }

   public final Image createImage(ImageProducer var1) {
      return new ToolkitImage(var1);
   }

   public final Image createImage(int var1, int var2) {
      return this.getLWGC().createAcceleratedImage(this.getTarget(), var1, var2);
   }

   public final VolatileImage createVolatileImage(int var1, int var2) {
      return new SunVolatileImage(this.getTarget(), var1, var2);
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      return Toolkit.getDefaultToolkit().prepareImage(var1, var2, var3, var4);
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      return Toolkit.getDefaultToolkit().checkImage(var1, var2, var3, var4);
   }

   public boolean handlesWheelScrolling() {
      return false;
   }

   public final void applyShape(Region var1) {
      synchronized(this.getStateLock()) {
         if (this.region == var1 || this.region != null && this.region.equals(var1)) {
            return;
         }
      }

      this.applyShapeImpl(var1);
   }

   void applyShapeImpl(Region var1) {
      synchronized(this.getStateLock()) {
         if (var1 != null) {
            this.region = Region.WHOLE_REGION.getIntersection(var1);
         } else {
            this.region = null;
         }
      }

      this.repaintParent(this.getBounds());
   }

   protected final Region getRegion() {
      synchronized(this.getStateLock()) {
         return this.isShaped() ? this.region : Region.getInstance(this.getSize());
      }
   }

   public boolean isShaped() {
      synchronized(this.getStateLock()) {
         return this.region != null;
      }
   }

   public void addDropTarget(DropTarget var1) {
      LWWindowPeer var2 = this.getWindowPeerOrSelf();
      if (var2 != null && var2 != this) {
         var2.addDropTarget(var1);
      } else {
         synchronized(this.dropTargetLock) {
            if (++this.fNumDropTargets == 1) {
               if (this.fDropTarget != null) {
                  System.err.println("CComponent.addDropTarget(): current drop target is non-null.");
               }

               this.fDropTarget = CDropTarget.createDropTarget(var1, this.target, this);
            }
         }
      }

   }

   public void removeDropTarget(DropTarget var1) {
      LWWindowPeer var2 = this.getWindowPeerOrSelf();
      if (var2 != null && var2 != this) {
         var2.removeDropTarget(var1);
      } else {
         synchronized(this.dropTargetLock) {
            if (--this.fNumDropTargets == 0) {
               if (this.fDropTarget != null) {
                  this.fDropTarget.dispose();
                  this.fDropTarget = null;
               } else {
                  System.err.println("CComponent.removeDropTarget(): current drop target is null.");
               }
            }
         }
      }

   }

   protected final void handleMove(int var1, int var2, boolean var3) {
      if (var3) {
         AWTAccessor.getComponentAccessor().setLocation(this.getTarget(), var1, var2);
      }

      this.postEvent(new ComponentEvent(this.getTarget(), 100));
   }

   protected final void handleResize(int var1, int var2, boolean var3) {
      Image var4 = null;
      synchronized(this.getStateLock()) {
         if (this.backBuffer != null) {
            var4 = this.backBuffer;
            this.backBuffer = this.getLWGC().createBackBuffer(this);
         }
      }

      this.getLWGC().destroyBackBuffer(var4);
      if (var3) {
         AWTAccessor.getComponentAccessor().setSize(this.getTarget(), var1, var2);
      }

      this.postEvent(new ComponentEvent(this.getTarget(), 101));
   }

   protected final void repaintOldNewBounds(Rectangle var1) {
      this.repaintParent(var1);
      this.repaintPeer(this.getSize());
   }

   protected final void repaintParent(Rectangle var1) {
      LWContainerPeer var2 = this.getContainerPeer();
      if (var2 != null) {
         var2.repaintPeer(var2.getContentSize().intersection(var1));
      }

   }

   public void postEvent(AWTEvent var1) {
      LWToolkit.postEvent(var1);
   }

   protected void postPaintEvent(int var1, int var2, int var3, int var4) {
      if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint(this.target)) {
         PaintEvent var5 = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent(this.getTarget(), var1, var2, var3, var4);
         if (var5 != null) {
            this.postEvent(var5);
         }

      }
   }

   public void handleEvent(AWTEvent var1) {
      if (!(var1 instanceof InputEvent) || !((InputEvent)var1).isConsumed()) {
         switch(var1.getID()) {
         case 501:
            this.handleJavaMouseEvent((MouseEvent)var1);
            break;
         case 800:
         case 801:
            this.handleJavaPaintEvent();
            break;
         case 1004:
         case 1005:
            this.handleJavaFocusEvent((FocusEvent)var1);
         }

         this.sendEventToDelegate(var1);
      }
   }

   protected void sendEventToDelegate(AWTEvent var1) {
      if (this.getDelegate() != null && this.isShowing() && this.isEnabled()) {
         synchronized(this.getDelegateLock()) {
            AWTEvent var3 = this.createDelegateEvent(var1);
            if (var3 != null) {
               AWTAccessor.getComponentAccessor().processEvent((Component)var3.getSource(), var3);
               if (var3 instanceof KeyEvent) {
                  KeyEvent var4 = (KeyEvent)var3;
                  SwingUtilities.processKeyBindings(var4);
               }
            }

         }
      }
   }

   private AWTEvent createDelegateEvent(AWTEvent var1) {
      Object var2 = null;
      if (var1 instanceof MouseWheelEvent) {
         MouseWheelEvent var3 = (MouseWheelEvent)var1;
         var2 = new MouseWheelEvent(this.delegate, var3.getID(), var3.getWhen(), var3.getModifiers(), var3.getX(), var3.getY(), var3.getClickCount(), var3.isPopupTrigger(), var3.getScrollType(), var3.getScrollAmount(), var3.getWheelRotation());
      } else if (var1 instanceof MouseEvent) {
         MouseEvent var5 = (MouseEvent)var1;
         Object var4 = SwingUtilities.getDeepestComponentAt(this.delegate, var5.getX(), var5.getY());
         if (var5.getID() == 506) {
            if (this.delegateDropTarget == null) {
               this.delegateDropTarget = (Component)var4;
            } else {
               var4 = this.delegateDropTarget;
            }
         }

         if (var5.getID() == 502 && this.delegateDropTarget != null) {
            var4 = this.delegateDropTarget;
            this.delegateDropTarget = null;
         }

         if (var4 == null) {
            var4 = this.delegate;
         }

         var2 = SwingUtilities.convertMouseEvent(this.getTarget(), var5, (Component)var4);
      } else if (var1 instanceof KeyEvent) {
         KeyEvent var6 = (KeyEvent)var1;
         var2 = new KeyEvent(this.getDelegateFocusOwner(), var6.getID(), var6.getWhen(), var6.getModifiers(), var6.getKeyCode(), var6.getKeyChar(), var6.getKeyLocation());
         AWTAccessor.getKeyEventAccessor().setExtendedKeyCode((KeyEvent)var2, (long)var6.getExtendedKeyCode());
      } else if (var1 instanceof FocusEvent) {
         FocusEvent var7 = (FocusEvent)var1;
         var2 = new FocusEvent(this.getDelegateFocusOwner(), var7.getID(), var7.isTemporary());
      }

      return (AWTEvent)var2;
   }

   protected void handleJavaMouseEvent(MouseEvent var1) {
      Component var2 = this.getTarget();

      assert var1.getSource() == var2;

      if (!var2.isFocusOwner() && LWKeyboardFocusManagerPeer.shouldFocusOnClick(var2)) {
         LWKeyboardFocusManagerPeer.requestFocusFor(var2, CausedFocusEvent.Cause.MOUSE_EVENT);
      }

   }

   void handleJavaFocusEvent(FocusEvent var1) {
      LWKeyboardFocusManagerPeer var2 = LWKeyboardFocusManagerPeer.getInstance();
      var2.setCurrentFocusOwner(var1.getID() == 1004 ? this.getTarget() : null);
   }

   protected final boolean shouldClearRectBeforePaint() {
      return true;
   }

   private void handleJavaPaintEvent() {
      if (!this.isLayouting()) {
         this.targetPaintArea.paint(this.getTarget(), this.shouldClearRectBeforePaint());
      }

   }

   LWComponentPeer<?, ?> findPeerAt(int var1, int var2) {
      Rectangle var3 = this.getBounds();
      Region var4 = this.getRegion();
      boolean var5 = this.isVisible() && var4.contains(var1 - var3.x, var2 - var3.y);
      return var5 ? this : null;
   }

   public Point windowToLocal(int var1, int var2, LWWindowPeer var3) {
      return this.windowToLocal(new Point(var1, var2), var3);
   }

   public Point windowToLocal(Point var1, LWWindowPeer var2) {
      for(Object var3 = this; var3 != var2; var3 = ((LWComponentPeer)var3).getContainerPeer()) {
         Rectangle var4 = ((LWComponentPeer)var3).getBounds();
         var1.x -= var4.x;
         var1.y -= var4.y;
      }

      return new Point(var1);
   }

   public Rectangle windowToLocal(Rectangle var1, LWWindowPeer var2) {
      Point var3 = this.windowToLocal(var1.getLocation(), var2);
      return new Rectangle(var3, var1.getSize());
   }

   public Point localToWindow(int var1, int var2) {
      return this.localToWindow(new Point(var1, var2));
   }

   public Point localToWindow(Point var1) {
      LWContainerPeer var2 = this.getContainerPeer();

      for(Rectangle var3 = this.getBounds(); var2 != null; var2 = var2.getContainerPeer()) {
         var1.x += var3.x;
         var1.y += var3.y;
         var3 = var2.getBounds();
      }

      return new Point(var1);
   }

   public Rectangle localToWindow(Rectangle var1) {
      Point var2 = this.localToWindow(var1.getLocation());
      return new Rectangle(var2, var1.getSize());
   }

   public final void repaintPeer() {
      this.repaintPeer(this.getSize());
   }

   void repaintPeer(Rectangle var1) {
      Rectangle var2 = this.getSize().intersection(var1);
      if (this.isShowing() && !var2.isEmpty()) {
         this.postPaintEvent(var2.x, var2.y, var2.width, var2.height);
      }
   }

   protected final boolean isShowing() {
      synchronized(getPeerTreeLock()) {
         if (!this.isVisible()) {
            return false;
         } else {
            LWContainerPeer var2 = this.getContainerPeer();
            return var2 == null || var2.isShowing();
         }
      }
   }

   protected final void paintPeer(Graphics var1) {
      JComponent var2 = this.getDelegate();
      if (var2 != null) {
         if (!SwingUtilities.isEventDispatchThread()) {
            throw new InternalError("Painting must be done on EDT");
         }

         synchronized(this.getDelegateLock()) {
            this.getDelegate().print(var1);
         }
      }

   }

   protected static final void flushOnscreenGraphics() {
      OGLRenderQueue var0 = OGLRenderQueue.getInstance();
      var0.lock();

      try {
         var0.flushNow();
      } finally {
         var0.unlock();
      }

   }

   protected final void setLayouting(boolean var1) {
      this.isLayouting = var1;
   }

   private final boolean isLayouting() {
      return this.isLayouting;
   }

   private final class DelegateContainer extends Container {
      DelegateContainer() {
         this.enableEvents(-1L);
      }

      public boolean isLightweight() {
         return false;
      }

      public Point getLocation() {
         return this.getLocationOnScreen();
      }

      public Point getLocationOnScreen() {
         return LWComponentPeer.this.getLocationOnScreen();
      }

      public int getX() {
         return this.getLocation().x;
      }

      public int getY() {
         return this.getLocation().y;
      }
   }
}
