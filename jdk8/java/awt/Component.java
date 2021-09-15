package java.awt;

import java.applet.Applet;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferStrategy;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.ConstrainableGraphics;
import sun.awt.EmbeddedFrame;
import sun.awt.EventQueueItem;
import sun.awt.RequestFocusController;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.awt.WindowClosingListener;
import sun.awt.dnd.SunDropTargetEvent;
import sun.awt.im.CompositionArea;
import sun.awt.image.VSyncedBSManager;
import sun.font.FontDesignMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.SunFontManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public abstract class Component implements ImageObserver, MenuContainer, Serializable {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Component");
   private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.Component");
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.Component");
   private static final PlatformLogger mixingLog = PlatformLogger.getLogger("java.awt.mixing.Component");
   transient ComponentPeer peer;
   transient Container parent;
   transient AppContext appContext;
   int x;
   int y;
   int width;
   int height;
   Color foreground;
   Color background;
   volatile Font font;
   Font peerFont;
   Cursor cursor;
   Locale locale;
   private transient volatile GraphicsConfiguration graphicsConfig;
   transient BufferStrategy bufferStrategy = null;
   boolean ignoreRepaint = false;
   boolean visible = true;
   boolean enabled = true;
   private volatile boolean valid = false;
   DropTarget dropTarget;
   Vector<PopupMenu> popups;
   private String name;
   private boolean nameExplicitlySet = false;
   private boolean focusable = true;
   private static final int FOCUS_TRAVERSABLE_UNKNOWN = 0;
   private static final int FOCUS_TRAVERSABLE_DEFAULT = 1;
   private static final int FOCUS_TRAVERSABLE_SET = 2;
   private int isFocusTraversableOverridden = 0;
   Set<AWTKeyStroke>[] focusTraversalKeys;
   private static final String[] focusTraversalKeyPropertyNames = new String[]{"forwardFocusTraversalKeys", "backwardFocusTraversalKeys", "upCycleFocusTraversalKeys", "downCycleFocusTraversalKeys"};
   private boolean focusTraversalKeysEnabled = true;
   static final Object LOCK = new Component.AWTTreeLock();
   private transient volatile AccessControlContext acc = AccessController.getContext();
   Dimension minSize;
   boolean minSizeSet;
   Dimension prefSize;
   boolean prefSizeSet;
   Dimension maxSize;
   boolean maxSizeSet;
   transient ComponentOrientation componentOrientation;
   boolean newEventsOnly;
   transient ComponentListener componentListener;
   transient FocusListener focusListener;
   transient HierarchyListener hierarchyListener;
   transient HierarchyBoundsListener hierarchyBoundsListener;
   transient KeyListener keyListener;
   transient MouseListener mouseListener;
   transient MouseMotionListener mouseMotionListener;
   transient MouseWheelListener mouseWheelListener;
   transient InputMethodListener inputMethodListener;
   transient RuntimeException windowClosingException;
   static final String actionListenerK = "actionL";
   static final String adjustmentListenerK = "adjustmentL";
   static final String componentListenerK = "componentL";
   static final String containerListenerK = "containerL";
   static final String focusListenerK = "focusL";
   static final String itemListenerK = "itemL";
   static final String keyListenerK = "keyL";
   static final String mouseListenerK = "mouseL";
   static final String mouseMotionListenerK = "mouseMotionL";
   static final String mouseWheelListenerK = "mouseWheelL";
   static final String textListenerK = "textL";
   static final String ownedWindowK = "ownedL";
   static final String windowListenerK = "windowL";
   static final String inputMethodListenerK = "inputMethodL";
   static final String hierarchyListenerK = "hierarchyL";
   static final String hierarchyBoundsListenerK = "hierarchyBoundsL";
   static final String windowStateListenerK = "windowStateL";
   static final String windowFocusListenerK = "windowFocusL";
   long eventMask;
   static boolean isInc;
   static int incRate;
   public static final float TOP_ALIGNMENT = 0.0F;
   public static final float CENTER_ALIGNMENT = 0.5F;
   public static final float BOTTOM_ALIGNMENT = 1.0F;
   public static final float LEFT_ALIGNMENT = 0.0F;
   public static final float RIGHT_ALIGNMENT = 1.0F;
   private static final long serialVersionUID = -7644114512714619750L;
   private PropertyChangeSupport changeSupport;
   private transient Object objectLock;
   boolean isPacked;
   private int boundsOp;
   private transient Region compoundShape;
   private transient Region mixingCutoutRegion;
   private transient boolean isAddNotifyComplete;
   transient boolean backgroundEraseDisabled;
   transient EventQueueItem[] eventCache;
   private transient boolean coalescingEnabled;
   private static final Map<Class<?>, Boolean> coalesceMap;
   private static final Class[] coalesceEventsParams;
   private static RequestFocusController requestFocusController;
   private boolean autoFocusTransferOnDisposal;
   private int componentSerializedDataVersion;
   protected AccessibleContext accessibleContext;

   Object getObjectLock() {
      return this.objectLock;
   }

   final AccessControlContext getAccessControlContext() {
      if (this.acc == null) {
         throw new SecurityException("Component is missing AccessControlContext");
      } else {
         return this.acc;
      }
   }

   int getBoundsOp() {
      assert Thread.holdsLock(this.getTreeLock());

      return this.boundsOp;
   }

   void setBoundsOp(int var1) {
      assert Thread.holdsLock(this.getTreeLock());

      if (var1 == 5) {
         this.boundsOp = 3;
      } else if (this.boundsOp == 3) {
         this.boundsOp = var1;
      }

   }

   protected Component() {
      this.componentOrientation = ComponentOrientation.UNKNOWN;
      this.newEventsOnly = false;
      this.windowClosingException = null;
      this.eventMask = 4096L;
      this.objectLock = new Object();
      this.isPacked = false;
      this.boundsOp = 3;
      this.compoundShape = null;
      this.mixingCutoutRegion = null;
      this.isAddNotifyComplete = false;
      this.coalescingEnabled = this.checkCoalescing();
      this.autoFocusTransferOnDisposal = true;
      this.componentSerializedDataVersion = 4;
      this.accessibleContext = null;
      this.appContext = AppContext.getAppContext();
   }

   void initializeFocusTraversalKeys() {
      this.focusTraversalKeys = new Set[3];
   }

   String constructComponentName() {
      return null;
   }

   public String getName() {
      if (this.name == null && !this.nameExplicitlySet) {
         synchronized(this.getObjectLock()) {
            if (this.name == null && !this.nameExplicitlySet) {
               this.name = this.constructComponentName();
            }
         }
      }

      return this.name;
   }

   public void setName(String var1) {
      String var2;
      synchronized(this.getObjectLock()) {
         var2 = this.name;
         this.name = var1;
         this.nameExplicitlySet = true;
      }

      this.firePropertyChange("name", var2, var1);
   }

   public Container getParent() {
      return this.getParent_NoClientCode();
   }

   final Container getParent_NoClientCode() {
      return this.parent;
   }

   Container getContainer() {
      return this.getParent_NoClientCode();
   }

   /** @deprecated */
   @Deprecated
   public ComponentPeer getPeer() {
      return this.peer;
   }

   public synchronized void setDropTarget(DropTarget var1) {
      if (var1 != this.dropTarget && (this.dropTarget == null || !this.dropTarget.equals(var1))) {
         DropTarget var2;
         if ((var2 = this.dropTarget) != null) {
            if (this.peer != null) {
               this.dropTarget.removeNotify(this.peer);
            }

            DropTarget var3 = this.dropTarget;
            this.dropTarget = null;

            try {
               var3.setComponent((Component)null);
            } catch (IllegalArgumentException var6) {
            }
         }

         if ((this.dropTarget = var1) != null) {
            try {
               this.dropTarget.setComponent(this);
               if (this.peer != null) {
                  this.dropTarget.addNotify(this.peer);
               }
            } catch (IllegalArgumentException var7) {
               if (var2 != null) {
                  try {
                     var2.setComponent(this);
                     if (this.peer != null) {
                        this.dropTarget.addNotify(this.peer);
                     }
                  } catch (IllegalArgumentException var5) {
                  }
               }
            }
         }

      }
   }

   public synchronized DropTarget getDropTarget() {
      return this.dropTarget;
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return this.getGraphicsConfiguration_NoClientCode();
   }

   final GraphicsConfiguration getGraphicsConfiguration_NoClientCode() {
      return this.graphicsConfig;
   }

   void setGraphicsConfiguration(GraphicsConfiguration var1) {
      synchronized(this.getTreeLock()) {
         if (this.updateGraphicsData(var1)) {
            this.removeNotify();
            this.addNotify();
         }

      }
   }

   boolean updateGraphicsData(GraphicsConfiguration var1) {
      this.checkTreeLock();
      if (this.graphicsConfig == var1) {
         return false;
      } else {
         this.graphicsConfig = var1;
         ComponentPeer var2 = this.getPeer();
         return var2 != null ? var2.updateGraphicsData(var1) : false;
      }
   }

   void checkGD(String var1) {
      if (this.graphicsConfig != null && !this.graphicsConfig.getDevice().getIDstring().equals(var1)) {
         throw new IllegalArgumentException("adding a container to a container on a different GraphicsDevice");
      }
   }

   public final Object getTreeLock() {
      return LOCK;
   }

   final void checkTreeLock() {
      if (!Thread.holdsLock(this.getTreeLock())) {
         throw new IllegalStateException("This function should be called while holding treeLock");
      }
   }

   public Toolkit getToolkit() {
      return this.getToolkitImpl();
   }

   final Toolkit getToolkitImpl() {
      Container var1 = this.parent;
      return var1 != null ? var1.getToolkitImpl() : Toolkit.getDefaultToolkit();
   }

   public boolean isValid() {
      return this.peer != null && this.valid;
   }

   public boolean isDisplayable() {
      return this.getPeer() != null;
   }

   @Transient
   public boolean isVisible() {
      return this.isVisible_NoClientCode();
   }

   final boolean isVisible_NoClientCode() {
      return this.visible;
   }

   boolean isRecursivelyVisible() {
      return this.visible && (this.parent == null || this.parent.isRecursivelyVisible());
   }

   private Rectangle getRecursivelyVisibleBounds() {
      Container var1 = this.getContainer();
      Rectangle var2 = this.getBounds();
      if (var1 == null) {
         return var2;
      } else {
         Rectangle var3 = var1.getRecursivelyVisibleBounds();
         var3.setLocation(0, 0);
         return var3.intersection(var2);
      }
   }

   Point pointRelativeToComponent(Point var1) {
      Point var2 = this.getLocationOnScreen();
      return new Point(var1.x - var2.x, var1.y - var2.y);
   }

   Component findUnderMouseInWindow(PointerInfo var1) {
      if (!this.isShowing()) {
         return null;
      } else {
         Window var2 = this.getContainingWindow();
         if (!Toolkit.getDefaultToolkit().getMouseInfoPeer().isWindowUnderMouse(var2)) {
            return null;
         } else {
            Point var4 = var2.pointRelativeToComponent(var1.getLocation());
            Component var5 = var2.findComponentAt(var4.x, var4.y, true);
            return var5;
         }
      }
   }

   public Point getMousePosition() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         PointerInfo var1 = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction<PointerInfo>() {
            public PointerInfo run() {
               return MouseInfo.getPointerInfo();
            }
         });
         synchronized(this.getTreeLock()) {
            Component var3 = this.findUnderMouseInWindow(var1);
            return !this.isSameOrAncestorOf(var3, true) ? null : this.pointRelativeToComponent(var1.getLocation());
         }
      }
   }

   boolean isSameOrAncestorOf(Component var1, boolean var2) {
      return var1 == this;
   }

   public boolean isShowing() {
      if (this.visible && this.peer != null) {
         Container var1 = this.parent;
         return var1 == null || var1.isShowing();
      } else {
         return false;
      }
   }

   public boolean isEnabled() {
      return this.isEnabledImpl();
   }

   final boolean isEnabledImpl() {
      return this.enabled;
   }

   public void setEnabled(boolean var1) {
      this.enable(var1);
   }

   /** @deprecated */
   @Deprecated
   public void enable() {
      if (!this.enabled) {
         synchronized(this.getTreeLock()) {
            this.enabled = true;
            ComponentPeer var2 = this.peer;
            if (var2 != null) {
               var2.setEnabled(true);
               if (this.visible && !this.getRecursivelyVisibleBounds().isEmpty()) {
                  this.updateCursorImmediately();
               }
            }
         }

         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.ENABLED);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void enable(boolean var1) {
      if (var1) {
         this.enable();
      } else {
         this.disable();
      }

   }

   /** @deprecated */
   @Deprecated
   public void disable() {
      if (this.enabled) {
         KeyboardFocusManager.clearMostRecentFocusOwner(this);
         synchronized(this.getTreeLock()) {
            this.enabled = false;
            if ((this.isFocusOwner() || this.containsFocus() && !this.isLightweight()) && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
               this.transferFocus(false);
            }

            ComponentPeer var2 = this.peer;
            if (var2 != null) {
               var2.setEnabled(false);
               if (this.visible && !this.getRecursivelyVisibleBounds().isEmpty()) {
                  this.updateCursorImmediately();
               }
            }
         }

         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.ENABLED);
         }
      }

   }

   public boolean isDoubleBuffered() {
      return false;
   }

   public void enableInputMethods(boolean var1) {
      InputContext var2;
      if (var1) {
         if ((this.eventMask & 4096L) != 0L) {
            return;
         }

         if (this.isFocusOwner()) {
            var2 = this.getInputContext();
            if (var2 != null) {
               FocusEvent var3 = new FocusEvent(this, 1004);
               var2.dispatchEvent(var3);
            }
         }

         this.eventMask |= 4096L;
      } else {
         if ((this.eventMask & 4096L) != 0L) {
            var2 = this.getInputContext();
            if (var2 != null) {
               var2.endComposition();
               var2.removeNotify(this);
            }
         }

         this.eventMask &= -4097L;
      }

   }

   public void setVisible(boolean var1) {
      this.show(var1);
   }

   /** @deprecated */
   @Deprecated
   public void show() {
      if (!this.visible) {
         synchronized(this.getTreeLock()) {
            this.visible = true;
            this.mixOnShowing();
            ComponentPeer var2 = this.peer;
            if (var2 != null) {
               var2.setVisible(true);
               this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
               if (var2 instanceof LightweightPeer) {
                  this.repaint();
               }

               this.updateCursorImmediately();
            }

            if (this.componentListener != null || (this.eventMask & 1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
               ComponentEvent var3 = new ComponentEvent(this, 102);
               Toolkit.getEventQueue().postEvent(var3);
            }
         }

         Container var1 = this.parent;
         if (var1 != null) {
            var1.invalidate();
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void show(boolean var1) {
      if (var1) {
         this.show();
      } else {
         this.hide();
      }

   }

   boolean containsFocus() {
      return this.isFocusOwner();
   }

   void clearMostRecentFocusOwnerOnHide() {
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
   }

   void clearCurrentFocusCycleRootOnHide() {
   }

   /** @deprecated */
   @Deprecated
   public void hide() {
      this.isPacked = false;
      if (this.visible) {
         this.clearCurrentFocusCycleRootOnHide();
         this.clearMostRecentFocusOwnerOnHide();
         synchronized(this.getTreeLock()) {
            this.visible = false;
            this.mixOnHiding(this.isLightweight());
            if (this.containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
               this.transferFocus(true);
            }

            ComponentPeer var2 = this.peer;
            if (var2 != null) {
               var2.setVisible(false);
               this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
               if (var2 instanceof LightweightPeer) {
                  this.repaint();
               }

               this.updateCursorImmediately();
            }

            if (this.componentListener != null || (this.eventMask & 1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
               ComponentEvent var3 = new ComponentEvent(this, 103);
               Toolkit.getEventQueue().postEvent(var3);
            }
         }

         Container var1 = this.parent;
         if (var1 != null) {
            var1.invalidate();
         }
      }

   }

   @Transient
   public Color getForeground() {
      Color var1 = this.foreground;
      if (var1 != null) {
         return var1;
      } else {
         Container var2 = this.parent;
         return var2 != null ? var2.getForeground() : null;
      }
   }

   public void setForeground(Color var1) {
      Color var2 = this.foreground;
      ComponentPeer var3 = this.peer;
      this.foreground = var1;
      if (var3 != null) {
         var1 = this.getForeground();
         if (var1 != null) {
            var3.setForeground(var1);
         }
      }

      this.firePropertyChange("foreground", var2, var1);
   }

   public boolean isForegroundSet() {
      return this.foreground != null;
   }

   @Transient
   public Color getBackground() {
      Color var1 = this.background;
      if (var1 != null) {
         return var1;
      } else {
         Container var2 = this.parent;
         return var2 != null ? var2.getBackground() : null;
      }
   }

   public void setBackground(Color var1) {
      Color var2 = this.background;
      ComponentPeer var3 = this.peer;
      this.background = var1;
      if (var3 != null) {
         var1 = this.getBackground();
         if (var1 != null) {
            var3.setBackground(var1);
         }
      }

      this.firePropertyChange("background", var2, var1);
   }

   public boolean isBackgroundSet() {
      return this.background != null;
   }

   @Transient
   public Font getFont() {
      return this.getFont_NoClientCode();
   }

   final Font getFont_NoClientCode() {
      Font var1 = this.font;
      if (var1 != null) {
         return var1;
      } else {
         Container var2 = this.parent;
         return var2 != null ? var2.getFont_NoClientCode() : null;
      }
   }

   public void setFont(Font var1) {
      Font var2;
      Font var3;
      synchronized(this.getTreeLock()) {
         var2 = this.font;
         var3 = this.font = var1;
         ComponentPeer var5 = this.peer;
         if (var5 != null) {
            var1 = this.getFont();
            if (var1 != null) {
               var5.setFont(var1);
               this.peerFont = var1;
            }
         }
      }

      this.firePropertyChange("font", var2, var3);
      if (var1 != var2 && (var2 == null || !var2.equals(var1))) {
         this.invalidateIfValid();
      }

   }

   public boolean isFontSet() {
      return this.font != null;
   }

   public Locale getLocale() {
      Locale var1 = this.locale;
      if (var1 != null) {
         return var1;
      } else {
         Container var2 = this.parent;
         if (var2 == null) {
            throw new IllegalComponentStateException("This component must have a parent in order to determine its locale");
         } else {
            return var2.getLocale();
         }
      }
   }

   public void setLocale(Locale var1) {
      Locale var2 = this.locale;
      this.locale = var1;
      this.firePropertyChange("locale", var2, var1);
      this.invalidateIfValid();
   }

   public ColorModel getColorModel() {
      ComponentPeer var1 = this.peer;
      if (var1 != null && !(var1 instanceof LightweightPeer)) {
         return var1.getColorModel();
      } else {
         return GraphicsEnvironment.isHeadless() ? ColorModel.getRGBdefault() : this.getToolkit().getColorModel();
      }
   }

   public Point getLocation() {
      return this.location();
   }

   public Point getLocationOnScreen() {
      synchronized(this.getTreeLock()) {
         return this.getLocationOnScreen_NoTreeLock();
      }
   }

   final Point getLocationOnScreen_NoTreeLock() {
      if (this.peer != null && this.isShowing()) {
         if (!(this.peer instanceof LightweightPeer)) {
            Point var4 = this.peer.getLocationOnScreen();
            return var4;
         } else {
            Container var1 = this.getNativeContainer();
            Point var2 = var1.peer.getLocationOnScreen();

            for(Object var3 = this; var3 != var1; var3 = ((Component)var3).getParent()) {
               var2.x += ((Component)var3).x;
               var2.y += ((Component)var3).y;
            }

            return var2;
         }
      } else {
         throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
      }
   }

   /** @deprecated */
   @Deprecated
   public Point location() {
      return this.location_NoClientCode();
   }

   private Point location_NoClientCode() {
      return new Point(this.x, this.y);
   }

   public void setLocation(int var1, int var2) {
      this.move(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public void move(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         this.setBoundsOp(1);
         this.setBounds(var1, var2, this.width, this.height);
      }
   }

   public void setLocation(Point var1) {
      this.setLocation(var1.x, var1.y);
   }

   public Dimension getSize() {
      return this.size();
   }

   /** @deprecated */
   @Deprecated
   public Dimension size() {
      return new Dimension(this.width, this.height);
   }

   public void setSize(int var1, int var2) {
      this.resize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public void resize(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         this.setBoundsOp(2);
         this.setBounds(this.x, this.y, var1, var2);
      }
   }

   public void setSize(Dimension var1) {
      this.resize(var1);
   }

   /** @deprecated */
   @Deprecated
   public void resize(Dimension var1) {
      this.setSize(var1.width, var1.height);
   }

   public Rectangle getBounds() {
      return this.bounds();
   }

   /** @deprecated */
   @Deprecated
   public Rectangle bounds() {
      return new Rectangle(this.x, this.y, this.width, this.height);
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.reshape(var1, var2, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public void reshape(int var1, int var2, int var3, int var4) {
      synchronized(this.getTreeLock()) {
         try {
            this.setBoundsOp(3);
            boolean var6 = this.width != var3 || this.height != var4;
            boolean var7 = this.x != var1 || this.y != var2;
            if (!var6 && !var7) {
               return;
            }

            int var8 = this.x;
            int var9 = this.y;
            int var10 = this.width;
            int var11 = this.height;
            this.x = var1;
            this.y = var2;
            this.width = var3;
            this.height = var4;
            if (var6) {
               this.isPacked = false;
            }

            boolean var12 = true;
            this.mixOnReshaping();
            if (this.peer != null) {
               if (!(this.peer instanceof LightweightPeer)) {
                  this.reshapeNativePeer(var1, var2, var3, var4, this.getBoundsOp());
                  var6 = var10 != this.width || var11 != this.height;
                  var7 = var8 != this.x || var9 != this.y;
                  if (this instanceof Window) {
                     var12 = false;
                  }
               }

               if (var6) {
                  this.invalidate();
               }

               if (this.parent != null) {
                  this.parent.invalidateIfValid();
               }
            }

            if (var12) {
               this.notifyNewBounds(var6, var7);
            }

            this.repaintParentIfNeeded(var8, var9, var10, var11);
         } finally {
            this.setBoundsOp(5);
         }

      }
   }

   private void repaintParentIfNeeded(int var1, int var2, int var3, int var4) {
      if (this.parent != null && this.peer instanceof LightweightPeer && this.isShowing()) {
         this.parent.repaint(var1, var2, var3, var4);
         this.repaint();
      }

   }

   private void reshapeNativePeer(int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1;
      int var7 = var2;

      for(Container var8 = this.parent; var8 != null && var8.peer instanceof LightweightPeer; var8 = var8.parent) {
         var6 += var8.x;
         var7 += var8.y;
      }

      this.peer.setBounds(var6, var7, var3, var4, var5);
   }

   private void notifyNewBounds(boolean var1, boolean var2) {
      if (this.componentListener == null && (this.eventMask & 1L) == 0L && !Toolkit.enabledOnToolkit(1L)) {
         if (this instanceof Container && ((Container)this).countComponents() > 0) {
            boolean var4 = Toolkit.enabledOnToolkit(65536L);
            if (var1) {
               ((Container)this).createChildHierarchyEvents(1402, 0L, var4);
            }

            if (var2) {
               ((Container)this).createChildHierarchyEvents(1401, 0L, var4);
            }
         }
      } else {
         ComponentEvent var3;
         if (var1) {
            var3 = new ComponentEvent(this, 101);
            Toolkit.getEventQueue().postEvent(var3);
         }

         if (var2) {
            var3 = new ComponentEvent(this, 100);
            Toolkit.getEventQueue().postEvent(var3);
         }
      }

   }

   public void setBounds(Rectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Rectangle getBounds(Rectangle var1) {
      if (var1 == null) {
         return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         var1.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
         return var1;
      }
   }

   public Dimension getSize(Dimension var1) {
      if (var1 == null) {
         return new Dimension(this.getWidth(), this.getHeight());
      } else {
         var1.setSize(this.getWidth(), this.getHeight());
         return var1;
      }
   }

   public Point getLocation(Point var1) {
      if (var1 == null) {
         return new Point(this.getX(), this.getY());
      } else {
         var1.setLocation(this.getX(), this.getY());
         return var1;
      }
   }

   public boolean isOpaque() {
      if (this.getPeer() == null) {
         return false;
      } else {
         return !this.isLightweight();
      }
   }

   public boolean isLightweight() {
      return this.getPeer() instanceof LightweightPeer;
   }

   public void setPreferredSize(Dimension var1) {
      Dimension var2;
      if (this.prefSizeSet) {
         var2 = this.prefSize;
      } else {
         var2 = null;
      }

      this.prefSize = var1;
      this.prefSizeSet = var1 != null;
      this.firePropertyChange("preferredSize", var2, var1);
   }

   public boolean isPreferredSizeSet() {
      return this.prefSizeSet;
   }

   public Dimension getPreferredSize() {
      return this.preferredSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize() {
      Dimension var1 = this.prefSize;
      if (var1 == null || !this.isPreferredSizeSet() && !this.isValid()) {
         synchronized(this.getTreeLock()) {
            this.prefSize = this.peer != null ? this.peer.getPreferredSize() : this.getMinimumSize();
            var1 = this.prefSize;
         }
      }

      return new Dimension(var1);
   }

   public void setMinimumSize(Dimension var1) {
      Dimension var2;
      if (this.minSizeSet) {
         var2 = this.minSize;
      } else {
         var2 = null;
      }

      this.minSize = var1;
      this.minSizeSet = var1 != null;
      this.firePropertyChange("minimumSize", var2, var1);
   }

   public boolean isMinimumSizeSet() {
      return this.minSizeSet;
   }

   public Dimension getMinimumSize() {
      return this.minimumSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize() {
      Dimension var1 = this.minSize;
      if (var1 == null || !this.isMinimumSizeSet() && !this.isValid()) {
         synchronized(this.getTreeLock()) {
            this.minSize = this.peer != null ? this.peer.getMinimumSize() : this.size();
            var1 = this.minSize;
         }
      }

      return new Dimension(var1);
   }

   public void setMaximumSize(Dimension var1) {
      Dimension var2;
      if (this.maxSizeSet) {
         var2 = this.maxSize;
      } else {
         var2 = null;
      }

      this.maxSize = var1;
      this.maxSizeSet = var1 != null;
      this.firePropertyChange("maximumSize", var2, var1);
   }

   public boolean isMaximumSizeSet() {
      return this.maxSizeSet;
   }

   public Dimension getMaximumSize() {
      return this.isMaximumSizeSet() ? new Dimension(this.maxSize) : new Dimension(32767, 32767);
   }

   public float getAlignmentX() {
      return 0.5F;
   }

   public float getAlignmentY() {
      return 0.5F;
   }

   public int getBaseline(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         return -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
      return Component.BaselineResizeBehavior.OTHER;
   }

   public void doLayout() {
      this.layout();
   }

   /** @deprecated */
   @Deprecated
   public void layout() {
   }

   public void validate() {
      synchronized(this.getTreeLock()) {
         ComponentPeer var2 = this.peer;
         boolean var3 = this.isValid();
         if (!var3 && var2 != null) {
            Font var4 = this.getFont();
            Font var5 = this.peerFont;
            if (var4 != var5 && (var5 == null || !var5.equals(var4))) {
               var2.setFont(var4);
               this.peerFont = var4;
            }

            var2.layout();
         }

         this.valid = true;
         if (!var3) {
            this.mixOnValidating();
         }

      }
   }

   public void invalidate() {
      synchronized(this.getTreeLock()) {
         this.valid = false;
         if (!this.isPreferredSizeSet()) {
            this.prefSize = null;
         }

         if (!this.isMinimumSizeSet()) {
            this.minSize = null;
         }

         if (!this.isMaximumSizeSet()) {
            this.maxSize = null;
         }

         this.invalidateParent();
      }
   }

   void invalidateParent() {
      if (this.parent != null) {
         this.parent.invalidateIfValid();
      }

   }

   final void invalidateIfValid() {
      if (this.isValid()) {
         this.invalidate();
      }

   }

   public void revalidate() {
      this.revalidateSynchronously();
   }

   final void revalidateSynchronously() {
      synchronized(this.getTreeLock()) {
         this.invalidate();
         Container var2 = this.getContainer();
         if (var2 == null) {
            this.validate();
         } else {
            while(!var2.isValidateRoot() && var2.getContainer() != null) {
               var2 = var2.getContainer();
            }

            var2.validate();
         }

      }
   }

   public Graphics getGraphics() {
      if (this.peer instanceof LightweightPeer) {
         if (this.parent == null) {
            return null;
         } else {
            Graphics var2 = this.parent.getGraphics();
            if (var2 == null) {
               return null;
            } else {
               if (var2 instanceof ConstrainableGraphics) {
                  ((ConstrainableGraphics)var2).constrain(this.x, this.y, this.width, this.height);
               } else {
                  var2.translate(this.x, this.y);
                  var2.setClip(0, 0, this.width, this.height);
               }

               var2.setFont(this.getFont());
               return var2;
            }
         }
      } else {
         ComponentPeer var1 = this.peer;
         return var1 != null ? var1.getGraphics() : null;
      }
   }

   final Graphics getGraphics_NoClientCode() {
      ComponentPeer var1 = this.peer;
      if (var1 instanceof LightweightPeer) {
         Container var2 = this.parent;
         if (var2 == null) {
            return null;
         } else {
            Graphics var3 = var2.getGraphics_NoClientCode();
            if (var3 == null) {
               return null;
            } else {
               if (var3 instanceof ConstrainableGraphics) {
                  ((ConstrainableGraphics)var3).constrain(this.x, this.y, this.width, this.height);
               } else {
                  var3.translate(this.x, this.y);
                  var3.setClip(0, 0, this.width, this.height);
               }

               var3.setFont(this.getFont_NoClientCode());
               return var3;
            }
         }
      } else {
         return var1 != null ? var1.getGraphics() : null;
      }
   }

   public FontMetrics getFontMetrics(Font var1) {
      FontManager var2 = FontManagerFactory.getInstance();
      return (FontMetrics)(var2 instanceof SunFontManager && ((SunFontManager)var2).usePlatformFontMetrics() && this.peer != null && !(this.peer instanceof LightweightPeer) ? this.peer.getFontMetrics(var1) : FontDesignMetrics.getMetrics(var1));
   }

   public void setCursor(Cursor var1) {
      this.cursor = var1;
      this.updateCursorImmediately();
   }

   final void updateCursorImmediately() {
      if (this.peer instanceof LightweightPeer) {
         Container var1 = this.getNativeContainer();
         if (var1 == null) {
            return;
         }

         ComponentPeer var2 = var1.getPeer();
         if (var2 != null) {
            var2.updateCursorImmediately();
         }
      } else if (this.peer != null) {
         this.peer.updateCursorImmediately();
      }

   }

   public Cursor getCursor() {
      return this.getCursor_NoClientCode();
   }

   final Cursor getCursor_NoClientCode() {
      Cursor var1 = this.cursor;
      if (var1 != null) {
         return var1;
      } else {
         Container var2 = this.parent;
         return var2 != null ? var2.getCursor_NoClientCode() : Cursor.getPredefinedCursor(0);
      }
   }

   public boolean isCursorSet() {
      return this.cursor != null;
   }

   public void paint(Graphics var1) {
   }

   public void update(Graphics var1) {
      this.paint(var1);
   }

   public void paintAll(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PeerPaintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), var1, var1.getClip(), 3);
      }

   }

   void lightweightPaint(Graphics var1) {
      this.paint(var1);
   }

   void paintHeavyweightComponents(Graphics var1) {
   }

   public void repaint() {
      this.repaint(0L, 0, 0, this.width, this.height);
   }

   public void repaint(long var1) {
      this.repaint(var1, 0, 0, this.width, this.height);
   }

   public void repaint(int var1, int var2, int var3, int var4) {
      this.repaint(0L, var1, var2, var3, var4);
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
      if (this.peer instanceof LightweightPeer) {
         if (this.parent != null) {
            if (var3 < 0) {
               var5 += var3;
               var3 = 0;
            }

            if (var4 < 0) {
               var6 += var4;
               var4 = 0;
            }

            int var11 = var5 > this.width ? this.width : var5;
            int var8 = var6 > this.height ? this.height : var6;
            if (var11 <= 0 || var8 <= 0) {
               return;
            }

            int var9 = this.x + var3;
            int var10 = this.y + var4;
            this.parent.repaint(var1, var9, var10, var11, var8);
         }
      } else if (this.isVisible() && this.peer != null && var5 > 0 && var6 > 0) {
         PaintEvent var7 = new PaintEvent(this, 801, new Rectangle(var3, var4, var5, var6));
         SunToolkit.postEvent(SunToolkit.targetToAppContext(this), var7);
      }

   }

   public void print(Graphics var1) {
      this.paint(var1);
   }

   public void printAll(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PeerPrintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), var1, var1.getClip(), 3);
      }

   }

   void lightweightPrint(Graphics var1) {
      this.print(var1);
   }

   void printHeavyweightComponents(Graphics var1) {
   }

   private Insets getInsets_NoClientCode() {
      ComponentPeer var1 = this.peer;
      return var1 instanceof ContainerPeer ? (Insets)((ContainerPeer)var1).getInsets().clone() : new Insets(0, 0, 0, 0);
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = -1;
      if ((var2 & 48) != 0) {
         var7 = 0;
      } else if ((var2 & 8) != 0 && isInc) {
         var7 = incRate;
         if (var7 < 0) {
            var7 = 0;
         }
      }

      if (var7 >= 0) {
         this.repaint((long)var7, 0, 0, this.width, this.height);
      }

      return (var2 & 160) == 0;
   }

   public Image createImage(ImageProducer var1) {
      ComponentPeer var2 = this.peer;
      return var2 != null && !(var2 instanceof LightweightPeer) ? var2.createImage(var1) : this.getToolkit().createImage(var1);
   }

   public Image createImage(int var1, int var2) {
      ComponentPeer var3 = this.peer;
      if (var3 instanceof LightweightPeer) {
         return this.parent != null ? this.parent.createImage(var1, var2) : null;
      } else {
         return var3 != null ? var3.createImage(var1, var2) : null;
      }
   }

   public VolatileImage createVolatileImage(int var1, int var2) {
      ComponentPeer var3 = this.peer;
      if (var3 instanceof LightweightPeer) {
         return this.parent != null ? this.parent.createVolatileImage(var1, var2) : null;
      } else {
         return var3 != null ? var3.createVolatileImage(var1, var2) : null;
      }
   }

   public VolatileImage createVolatileImage(int var1, int var2, ImageCapabilities var3) throws AWTException {
      return this.createVolatileImage(var1, var2);
   }

   public boolean prepareImage(Image var1, ImageObserver var2) {
      return this.prepareImage(var1, -1, -1, var2);
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      ComponentPeer var5 = this.peer;
      if (var5 instanceof LightweightPeer) {
         return this.parent != null ? this.parent.prepareImage(var1, var2, var3, var4) : this.getToolkit().prepareImage(var1, var2, var3, var4);
      } else {
         return var5 != null ? var5.prepareImage(var1, var2, var3, var4) : this.getToolkit().prepareImage(var1, var2, var3, var4);
      }
   }

   public int checkImage(Image var1, ImageObserver var2) {
      return this.checkImage(var1, -1, -1, var2);
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      ComponentPeer var5 = this.peer;
      if (var5 instanceof LightweightPeer) {
         return this.parent != null ? this.parent.checkImage(var1, var2, var3, var4) : this.getToolkit().checkImage(var1, var2, var3, var4);
      } else {
         return var5 != null ? var5.checkImage(var1, var2, var3, var4) : this.getToolkit().checkImage(var1, var2, var3, var4);
      }
   }

   void createBufferStrategy(int var1) {
      BufferCapabilities var2;
      if (var1 > 1) {
         var2 = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED);

         try {
            this.createBufferStrategy(var1, var2);
            return;
         } catch (AWTException var6) {
         }
      }

      var2 = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), (BufferCapabilities.FlipContents)null);

      try {
         this.createBufferStrategy(var1, var2);
      } catch (AWTException var5) {
         var2 = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), (BufferCapabilities.FlipContents)null);

         try {
            this.createBufferStrategy(var1, var2);
         } catch (AWTException var4) {
            throw new InternalError("Could not create a buffer strategy", var4);
         }
      }
   }

   void createBufferStrategy(int var1, BufferCapabilities var2) throws AWTException {
      if (var1 < 1) {
         throw new IllegalArgumentException("Number of buffers must be at least 1");
      } else if (var2 == null) {
         throw new IllegalArgumentException("No capabilities specified");
      } else {
         if (this.bufferStrategy != null) {
            this.bufferStrategy.dispose();
         }

         if (var1 == 1) {
            this.bufferStrategy = new Component.SingleBufferStrategy((BufferCapabilities)var2);
         } else {
            SunGraphicsEnvironment var3 = (SunGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (!((BufferCapabilities)var2).isPageFlipping() && var3.isFlipStrategyPreferred(this.peer)) {
               var2 = new Component.ProxyCapabilities((BufferCapabilities)var2);
            }

            if (((BufferCapabilities)var2).isPageFlipping()) {
               this.bufferStrategy = new Component.FlipSubRegionBufferStrategy(var1, (BufferCapabilities)var2);
            } else {
               this.bufferStrategy = new Component.BltSubRegionBufferStrategy(var1, (BufferCapabilities)var2);
            }
         }

      }
   }

   BufferStrategy getBufferStrategy() {
      return this.bufferStrategy;
   }

   Image getBackBuffer() {
      if (this.bufferStrategy != null) {
         if (this.bufferStrategy instanceof Component.BltBufferStrategy) {
            Component.BltBufferStrategy var2 = (Component.BltBufferStrategy)this.bufferStrategy;
            return var2.getBackBuffer();
         }

         if (this.bufferStrategy instanceof Component.FlipBufferStrategy) {
            Component.FlipBufferStrategy var1 = (Component.FlipBufferStrategy)this.bufferStrategy;
            return var1.getBackBuffer();
         }
      }

      return null;
   }

   public void setIgnoreRepaint(boolean var1) {
      this.ignoreRepaint = var1;
   }

   public boolean getIgnoreRepaint() {
      return this.ignoreRepaint;
   }

   public boolean contains(int var1, int var2) {
      return this.inside(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public boolean inside(int var1, int var2) {
      return var1 >= 0 && var1 < this.width && var2 >= 0 && var2 < this.height;
   }

   public boolean contains(Point var1) {
      return this.contains(var1.x, var1.y);
   }

   public Component getComponentAt(int var1, int var2) {
      return this.locate(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public Component locate(int var1, int var2) {
      return this.contains(var1, var2) ? this : null;
   }

   public Component getComponentAt(Point var1) {
      return this.getComponentAt(var1.x, var1.y);
   }

   /** @deprecated */
   @Deprecated
   public void deliverEvent(Event var1) {
      this.postEvent(var1);
   }

   public final void dispatchEvent(AWTEvent var1) {
      this.dispatchEventImpl(var1);
   }

   void dispatchEventImpl(AWTEvent var1) {
      int var2 = var1.getID();
      AppContext var3 = this.appContext;
      if (var3 != null && !var3.equals(AppContext.getAppContext()) && eventLog.isLoggable(PlatformLogger.Level.FINE)) {
         eventLog.fine("Event " + var1 + " is being dispatched on the wrong AppContext");
      }

      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
         eventLog.finest("{0}", var1);
      }

      if (!(var1 instanceof KeyEvent)) {
         EventQueue.setCurrentEventAndMostRecentTime(var1);
      }

      if (var1 instanceof SunDropTargetEvent) {
         ((SunDropTargetEvent)var1).dispatch();
      } else {
         if (!var1.focusManagerIsDispatching) {
            if (var1.isPosted) {
               var1 = KeyboardFocusManager.retargetFocusEvent(var1);
               var1.isPosted = true;
            }

            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(var1)) {
               return;
            }
         }

         if (var1 instanceof FocusEvent && focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("" + var1);
         }

         if (var2 != 507 || this.eventTypeEnabled(var2) || this.peer == null || this.peer.handlesWheelScrolling() || !this.dispatchMouseWheelToAncestor((MouseWheelEvent)var1)) {
            Toolkit var4 = Toolkit.getDefaultToolkit();
            var4.notifyAWTEventListeners(var1);
            if (!var1.isConsumed() && var1 instanceof KeyEvent) {
               KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, (KeyEvent)var1);
               if (var1.isConsumed()) {
                  return;
               }
            }

            InputContext var5;
            if (this.areInputMethodsEnabled()) {
               if (var1 instanceof InputMethodEvent && !(this instanceof CompositionArea) || var1 instanceof InputEvent || var1 instanceof FocusEvent) {
                  var5 = this.getInputContext();
                  if (var5 != null) {
                     var5.dispatchEvent(var1);
                     if (var1.isConsumed()) {
                        if (var1 instanceof FocusEvent && focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                           focusLog.finest("3579: Skipping " + var1);
                        }

                        return;
                     }
                  }
               }
            } else if (var2 == 1004) {
               var5 = this.getInputContext();
               if (var5 != null && var5 instanceof sun.awt.im.InputContext) {
                  ((sun.awt.im.InputContext)var5).disableNativeIM();
               }
            }

            switch(var2) {
            case 201:
               if (var4 instanceof WindowClosingListener) {
                  this.windowClosingException = ((WindowClosingListener)var4).windowClosingNotify((WindowEvent)var1);
                  if (this.checkWindowClosingException()) {
                     return;
                  }
               }
               break;
            case 401:
            case 402:
               Container var8 = (Container)((Container)(this instanceof Container ? this : this.parent));
               if (var8 != null) {
                  var8.preProcessKeyEvent((KeyEvent)var1);
                  if (var1.isConsumed()) {
                     if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        focusLog.finest("Pre-process consumed event");
                     }

                     return;
                  }
               }
            }

            if (this.newEventsOnly) {
               if (this.eventEnabled(var1)) {
                  this.processEvent(var1);
               }
            } else if (var2 == 507) {
               this.autoProcessMouseWheel((MouseWheelEvent)var1);
            } else if (!(var1 instanceof MouseEvent) || this.postsOldMouseEvents()) {
               Event var9 = var1.convertToOld();
               if (var9 != null) {
                  int var6 = var9.key;
                  int var7 = var9.modifiers;
                  this.postEvent(var9);
                  if (var9.isConsumed()) {
                     var1.consume();
                  }

                  switch(var9.id) {
                  case 401:
                  case 402:
                  case 403:
                  case 404:
                     if (var9.key != var6) {
                        ((KeyEvent)var1).setKeyChar(var9.getKeyEventChar());
                     }

                     if (var9.modifiers != var7) {
                        ((KeyEvent)var1).setModifiers(var9.modifiers);
                     }
                  }
               }
            }

            if (var2 == 201 && !var1.isConsumed() && var4 instanceof WindowClosingListener) {
               this.windowClosingException = ((WindowClosingListener)var4).windowClosingDelivered((WindowEvent)var1);
               if (this.checkWindowClosingException()) {
                  return;
               }
            }

            if (!(var1 instanceof KeyEvent)) {
               ComponentPeer var10 = this.peer;
               if (var1 instanceof FocusEvent && (var10 == null || var10 instanceof LightweightPeer)) {
                  Component var11 = (Component)var1.getSource();
                  if (var11 != null) {
                     Container var12 = var11.getNativeContainer();
                     if (var12 != null) {
                        var10 = var12.getPeer();
                     }
                  }
               }

               if (var10 != null) {
                  var10.handleEvent(var1);
               }
            }

            if (SunToolkit.isTouchKeyboardAutoShowEnabled() && var4 instanceof SunToolkit && (var1 instanceof MouseEvent || var1 instanceof FocusEvent)) {
               ((SunToolkit)var4).showOrHideTouchKeyboard(this, var1);
            }

         }
      }
   }

   void autoProcessMouseWheel(MouseWheelEvent var1) {
   }

   boolean dispatchMouseWheelToAncestor(MouseWheelEvent var1) {
      int var2 = var1.getX() + this.getX();
      int var3 = var1.getY() + this.getY();
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
         eventLog.finest("dispatchMouseWheelToAncestor");
         eventLog.finest("orig event src is of " + var1.getSource().getClass());
      }

      synchronized(this.getTreeLock()) {
         Container var6;
         for(var6 = this.getParent(); var6 != null && !var6.eventEnabled(var1); var6 = var6.getParent()) {
            var2 += var6.getX();
            var3 += var6.getY();
            if (var6 instanceof Window) {
               break;
            }
         }

         if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            eventLog.finest("new event src is " + var6.getClass());
         }

         if (var6 != null && var6.eventEnabled(var1)) {
            MouseWheelEvent var4 = new MouseWheelEvent(var6, var1.getID(), var1.getWhen(), var1.getModifiers(), var2, var3, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), var1.getScrollType(), var1.getScrollAmount(), var1.getWheelRotation(), var1.getPreciseWheelRotation());
            var1.copyPrivateDataInto(var4);
            var6.dispatchEventToSelf(var4);
            if (var4.isConsumed()) {
               var1.consume();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   boolean checkWindowClosingException() {
      if (this.windowClosingException != null) {
         if (this instanceof Dialog) {
            ((Dialog)this).interruptBlocking();
         } else {
            this.windowClosingException.fillInStackTrace();
            this.windowClosingException.printStackTrace();
            this.windowClosingException = null;
         }

         return true;
      } else {
         return false;
      }
   }

   boolean areInputMethodsEnabled() {
      return (this.eventMask & 4096L) != 0L && ((this.eventMask & 8L) != 0L || this.keyListener != null);
   }

   boolean eventEnabled(AWTEvent var1) {
      return this.eventTypeEnabled(var1.id);
   }

   boolean eventTypeEnabled(int var1) {
      switch(var1) {
      case 100:
      case 101:
      case 102:
      case 103:
         if ((this.eventMask & 1L) != 0L || this.componentListener != null) {
            return true;
         }
         break;
      case 400:
      case 401:
      case 402:
         if ((this.eventMask & 8L) != 0L || this.keyListener != null) {
            return true;
         }
         break;
      case 500:
      case 501:
      case 502:
      case 504:
      case 505:
         if ((this.eventMask & 16L) == 0L && this.mouseListener == null) {
            break;
         }

         return true;
      case 503:
      case 506:
         if ((this.eventMask & 32L) != 0L || this.mouseMotionListener != null) {
            return true;
         }
         break;
      case 507:
         if ((this.eventMask & 131072L) != 0L || this.mouseWheelListener != null) {
            return true;
         }
         break;
      case 601:
         if ((this.eventMask & 256L) != 0L) {
            return true;
         }
         break;
      case 701:
         if ((this.eventMask & 512L) != 0L) {
            return true;
         }
         break;
      case 900:
         if ((this.eventMask & 1024L) != 0L) {
            return true;
         }
         break;
      case 1001:
         if ((this.eventMask & 128L) != 0L) {
            return true;
         }
         break;
      case 1004:
      case 1005:
         if ((this.eventMask & 4L) != 0L || this.focusListener != null) {
            return true;
         }
         break;
      case 1100:
      case 1101:
         if ((this.eventMask & 2048L) != 0L || this.inputMethodListener != null) {
            return true;
         }
         break;
      case 1400:
         if ((this.eventMask & 32768L) != 0L || this.hierarchyListener != null) {
            return true;
         }
         break;
      case 1401:
      case 1402:
         if ((this.eventMask & 65536L) != 0L || this.hierarchyBoundsListener != null) {
            return true;
         }
      }

      if (var1 > 1999) {
         return true;
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean postEvent(Event var1) {
      ComponentPeer var2 = this.peer;
      if (this.handleEvent(var1)) {
         var1.consume();
         return true;
      } else {
         Container var3 = this.parent;
         int var4 = var1.x;
         int var5 = var1.y;
         if (var3 != null) {
            var1.translate(this.x, this.y);
            if (var3.postEvent(var1)) {
               var1.consume();
               return true;
            }

            var1.x = var4;
            var1.y = var5;
         }

         return false;
      }
   }

   public synchronized void addComponentListener(ComponentListener var1) {
      if (var1 != null) {
         this.componentListener = AWTEventMulticaster.add(this.componentListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeComponentListener(ComponentListener var1) {
      if (var1 != null) {
         this.componentListener = AWTEventMulticaster.remove(this.componentListener, var1);
      }
   }

   public synchronized ComponentListener[] getComponentListeners() {
      return (ComponentListener[])this.getListeners(ComponentListener.class);
   }

   public synchronized void addFocusListener(FocusListener var1) {
      if (var1 != null) {
         this.focusListener = AWTEventMulticaster.add(this.focusListener, var1);
         this.newEventsOnly = true;
         if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(4L);
         }

      }
   }

   public synchronized void removeFocusListener(FocusListener var1) {
      if (var1 != null) {
         this.focusListener = AWTEventMulticaster.remove(this.focusListener, var1);
      }
   }

   public synchronized FocusListener[] getFocusListeners() {
      return (FocusListener[])this.getListeners(FocusListener.class);
   }

   public void addHierarchyListener(HierarchyListener var1) {
      if (var1 != null) {
         boolean var2;
         synchronized(this) {
            var2 = this.hierarchyListener == null && (this.eventMask & 32768L) == 0L;
            this.hierarchyListener = AWTEventMulticaster.add(this.hierarchyListener, var1);
            var2 = var2 && this.hierarchyListener != null;
            this.newEventsOnly = true;
         }

         if (var2) {
            synchronized(this.getTreeLock()) {
               this.adjustListeningChildrenOnParent(32768L, 1);
            }
         }

      }
   }

   public void removeHierarchyListener(HierarchyListener var1) {
      if (var1 != null) {
         boolean var2;
         synchronized(this) {
            var2 = this.hierarchyListener != null && (this.eventMask & 32768L) == 0L;
            this.hierarchyListener = AWTEventMulticaster.remove(this.hierarchyListener, var1);
            var2 = var2 && this.hierarchyListener == null;
         }

         if (var2) {
            synchronized(this.getTreeLock()) {
               this.adjustListeningChildrenOnParent(32768L, -1);
            }
         }

      }
   }

   public synchronized HierarchyListener[] getHierarchyListeners() {
      return (HierarchyListener[])this.getListeners(HierarchyListener.class);
   }

   public void addHierarchyBoundsListener(HierarchyBoundsListener var1) {
      if (var1 != null) {
         boolean var2;
         synchronized(this) {
            var2 = this.hierarchyBoundsListener == null && (this.eventMask & 65536L) == 0L;
            this.hierarchyBoundsListener = AWTEventMulticaster.add(this.hierarchyBoundsListener, var1);
            var2 = var2 && this.hierarchyBoundsListener != null;
            this.newEventsOnly = true;
         }

         if (var2) {
            synchronized(this.getTreeLock()) {
               this.adjustListeningChildrenOnParent(65536L, 1);
            }
         }

      }
   }

   public void removeHierarchyBoundsListener(HierarchyBoundsListener var1) {
      if (var1 != null) {
         boolean var2;
         synchronized(this) {
            var2 = this.hierarchyBoundsListener != null && (this.eventMask & 65536L) == 0L;
            this.hierarchyBoundsListener = AWTEventMulticaster.remove(this.hierarchyBoundsListener, var1);
            var2 = var2 && this.hierarchyBoundsListener == null;
         }

         if (var2) {
            synchronized(this.getTreeLock()) {
               this.adjustListeningChildrenOnParent(65536L, -1);
            }
         }

      }
   }

   int numListening(long var1) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE) && var1 != 32768L && var1 != 65536L) {
         eventLog.fine("Assertion failed");
      }

      return (var1 != 32768L || this.hierarchyListener == null && (this.eventMask & 32768L) == 0L) && (var1 != 65536L || this.hierarchyBoundsListener == null && (this.eventMask & 65536L) == 0L) ? 0 : 1;
   }

   int countHierarchyMembers() {
      return 1;
   }

   int createHierarchyEvents(int var1, Component var2, Container var3, long var4, boolean var6) {
      HierarchyEvent var7;
      switch(var1) {
      case 1400:
         if (this.hierarchyListener != null || (this.eventMask & 32768L) != 0L || var6) {
            var7 = new HierarchyEvent(this, var1, var2, var3, var4);
            this.dispatchEvent(var7);
            return 1;
         }
         break;
      case 1401:
      case 1402:
         if (eventLog.isLoggable(PlatformLogger.Level.FINE) && var4 != 0L) {
            eventLog.fine("Assertion (changeFlags == 0) failed");
         }

         if (this.hierarchyBoundsListener != null || (this.eventMask & 65536L) != 0L || var6) {
            var7 = new HierarchyEvent(this, var1, var2, var3);
            this.dispatchEvent(var7);
            return 1;
         }
         break;
      default:
         if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            eventLog.fine("This code must never be reached");
         }
      }

      return 0;
   }

   public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() {
      return (HierarchyBoundsListener[])this.getListeners(HierarchyBoundsListener.class);
   }

   void adjustListeningChildrenOnParent(long var1, int var3) {
      if (this.parent != null) {
         this.parent.adjustListeningChildren(var1, var3);
      }

   }

   public synchronized void addKeyListener(KeyListener var1) {
      if (var1 != null) {
         this.keyListener = AWTEventMulticaster.add(this.keyListener, var1);
         this.newEventsOnly = true;
         if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(8L);
         }

      }
   }

   public synchronized void removeKeyListener(KeyListener var1) {
      if (var1 != null) {
         this.keyListener = AWTEventMulticaster.remove(this.keyListener, var1);
      }
   }

   public synchronized KeyListener[] getKeyListeners() {
      return (KeyListener[])this.getListeners(KeyListener.class);
   }

   public synchronized void addMouseListener(MouseListener var1) {
      if (var1 != null) {
         this.mouseListener = AWTEventMulticaster.add(this.mouseListener, var1);
         this.newEventsOnly = true;
         if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(16L);
         }

      }
   }

   public synchronized void removeMouseListener(MouseListener var1) {
      if (var1 != null) {
         this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, var1);
      }
   }

   public synchronized MouseListener[] getMouseListeners() {
      return (MouseListener[])this.getListeners(MouseListener.class);
   }

   public synchronized void addMouseMotionListener(MouseMotionListener var1) {
      if (var1 != null) {
         this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, var1);
         this.newEventsOnly = true;
         if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(32L);
         }

      }
   }

   public synchronized void removeMouseMotionListener(MouseMotionListener var1) {
      if (var1 != null) {
         this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, var1);
      }
   }

   public synchronized MouseMotionListener[] getMouseMotionListeners() {
      return (MouseMotionListener[])this.getListeners(MouseMotionListener.class);
   }

   public synchronized void addMouseWheelListener(MouseWheelListener var1) {
      if (var1 != null) {
         this.mouseWheelListener = AWTEventMulticaster.add(this.mouseWheelListener, var1);
         this.newEventsOnly = true;
         if (this.peer instanceof LightweightPeer) {
            this.parent.proxyEnableEvents(131072L);
         }

      }
   }

   public synchronized void removeMouseWheelListener(MouseWheelListener var1) {
      if (var1 != null) {
         this.mouseWheelListener = AWTEventMulticaster.remove(this.mouseWheelListener, var1);
      }
   }

   public synchronized MouseWheelListener[] getMouseWheelListeners() {
      return (MouseWheelListener[])this.getListeners(MouseWheelListener.class);
   }

   public synchronized void addInputMethodListener(InputMethodListener var1) {
      if (var1 != null) {
         this.inputMethodListener = AWTEventMulticaster.add(this.inputMethodListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeInputMethodListener(InputMethodListener var1) {
      if (var1 != null) {
         this.inputMethodListener = AWTEventMulticaster.remove(this.inputMethodListener, var1);
      }
   }

   public synchronized InputMethodListener[] getInputMethodListeners() {
      return (InputMethodListener[])this.getListeners(InputMethodListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      Object var2 = null;
      if (var1 == ComponentListener.class) {
         var2 = this.componentListener;
      } else if (var1 == FocusListener.class) {
         var2 = this.focusListener;
      } else if (var1 == HierarchyListener.class) {
         var2 = this.hierarchyListener;
      } else if (var1 == HierarchyBoundsListener.class) {
         var2 = this.hierarchyBoundsListener;
      } else if (var1 == KeyListener.class) {
         var2 = this.keyListener;
      } else if (var1 == MouseListener.class) {
         var2 = this.mouseListener;
      } else if (var1 == MouseMotionListener.class) {
         var2 = this.mouseMotionListener;
      } else if (var1 == MouseWheelListener.class) {
         var2 = this.mouseWheelListener;
      } else if (var1 == InputMethodListener.class) {
         var2 = this.inputMethodListener;
      } else if (var1 == PropertyChangeListener.class) {
         return (EventListener[])this.getPropertyChangeListeners();
      }

      return AWTEventMulticaster.getListeners((EventListener)var2, var1);
   }

   public InputMethodRequests getInputMethodRequests() {
      return null;
   }

   public InputContext getInputContext() {
      Container var1 = this.parent;
      return var1 == null ? null : var1.getInputContext();
   }

   protected final void enableEvents(long var1) {
      long var3 = 0L;
      synchronized(this) {
         if ((var1 & 32768L) != 0L && this.hierarchyListener == null && (this.eventMask & 32768L) == 0L) {
            var3 |= 32768L;
         }

         if ((var1 & 65536L) != 0L && this.hierarchyBoundsListener == null && (this.eventMask & 65536L) == 0L) {
            var3 |= 65536L;
         }

         this.eventMask |= var1;
         this.newEventsOnly = true;
      }

      if (this.peer instanceof LightweightPeer) {
         this.parent.proxyEnableEvents(this.eventMask);
      }

      if (var3 != 0L) {
         synchronized(this.getTreeLock()) {
            this.adjustListeningChildrenOnParent(var3, 1);
         }
      }

   }

   protected final void disableEvents(long var1) {
      long var3 = 0L;
      synchronized(this) {
         if ((var1 & 32768L) != 0L && this.hierarchyListener == null && (this.eventMask & 32768L) != 0L) {
            var3 |= 32768L;
         }

         if ((var1 & 65536L) != 0L && this.hierarchyBoundsListener == null && (this.eventMask & 65536L) != 0L) {
            var3 |= 65536L;
         }

         this.eventMask &= ~var1;
      }

      if (var3 != 0L) {
         synchronized(this.getTreeLock()) {
            this.adjustListeningChildrenOnParent(var3, -1);
         }
      }

   }

   private boolean checkCoalescing() {
      if (this.getClass().getClassLoader() == null) {
         return false;
      } else {
         final Class var1 = this.getClass();
         synchronized(coalesceMap) {
            Boolean var3 = (Boolean)coalesceMap.get(var1);
            if (var3 != null) {
               return var3;
            } else {
               Boolean var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                  public Boolean run() {
                     return Component.isCoalesceEventsOverriden(var1);
                  }
               });
               coalesceMap.put(var1, var4);
               return var4;
            }
         }
      }
   }

   private static boolean isCoalesceEventsOverriden(Class<?> var0) {
      assert Thread.holdsLock(coalesceMap);

      Class var1 = var0.getSuperclass();
      if (var1 == null) {
         return false;
      } else {
         if (var1.getClassLoader() != null) {
            Boolean var2 = (Boolean)coalesceMap.get(var1);
            if (var2 == null) {
               if (isCoalesceEventsOverriden(var1)) {
                  coalesceMap.put(var1, true);
                  return true;
               }
            } else if (var2) {
               return true;
            }
         }

         try {
            var0.getDeclaredMethod("coalesceEvents", coalesceEventsParams);
            return true;
         } catch (NoSuchMethodException var3) {
            return false;
         }
      }
   }

   final boolean isCoalescingEnabled() {
      return this.coalescingEnabled;
   }

   protected AWTEvent coalesceEvents(AWTEvent var1, AWTEvent var2) {
      return null;
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof FocusEvent) {
         this.processFocusEvent((FocusEvent)var1);
      } else if (var1 instanceof MouseEvent) {
         switch(var1.getID()) {
         case 500:
         case 501:
         case 502:
         case 504:
         case 505:
            this.processMouseEvent((MouseEvent)var1);
            break;
         case 503:
         case 506:
            this.processMouseMotionEvent((MouseEvent)var1);
            break;
         case 507:
            this.processMouseWheelEvent((MouseWheelEvent)var1);
         }
      } else if (var1 instanceof KeyEvent) {
         this.processKeyEvent((KeyEvent)var1);
      } else if (var1 instanceof ComponentEvent) {
         this.processComponentEvent((ComponentEvent)var1);
      } else if (var1 instanceof InputMethodEvent) {
         this.processInputMethodEvent((InputMethodEvent)var1);
      } else if (var1 instanceof HierarchyEvent) {
         switch(var1.getID()) {
         case 1400:
            this.processHierarchyEvent((HierarchyEvent)var1);
            break;
         case 1401:
         case 1402:
            this.processHierarchyBoundsEvent((HierarchyEvent)var1);
         }
      }

   }

   protected void processComponentEvent(ComponentEvent var1) {
      ComponentListener var2 = this.componentListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 100:
            var2.componentMoved(var1);
            break;
         case 101:
            var2.componentResized(var1);
            break;
         case 102:
            var2.componentShown(var1);
            break;
         case 103:
            var2.componentHidden(var1);
         }
      }

   }

   protected void processFocusEvent(FocusEvent var1) {
      FocusListener var2 = this.focusListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 1004:
            var2.focusGained(var1);
            break;
         case 1005:
            var2.focusLost(var1);
         }
      }

   }

   protected void processKeyEvent(KeyEvent var1) {
      KeyListener var2 = this.keyListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 400:
            var2.keyTyped(var1);
            break;
         case 401:
            var2.keyPressed(var1);
            break;
         case 402:
            var2.keyReleased(var1);
         }
      }

   }

   protected void processMouseEvent(MouseEvent var1) {
      MouseListener var2 = this.mouseListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 500:
            var2.mouseClicked(var1);
            break;
         case 501:
            var2.mousePressed(var1);
            break;
         case 502:
            var2.mouseReleased(var1);
         case 503:
         default:
            break;
         case 504:
            var2.mouseEntered(var1);
            break;
         case 505:
            var2.mouseExited(var1);
         }
      }

   }

   protected void processMouseMotionEvent(MouseEvent var1) {
      MouseMotionListener var2 = this.mouseMotionListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 503:
            var2.mouseMoved(var1);
            break;
         case 506:
            var2.mouseDragged(var1);
         }
      }

   }

   protected void processMouseWheelEvent(MouseWheelEvent var1) {
      MouseWheelListener var2 = this.mouseWheelListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 507:
            var2.mouseWheelMoved(var1);
         }
      }

   }

   boolean postsOldMouseEvents() {
      return false;
   }

   protected void processInputMethodEvent(InputMethodEvent var1) {
      InputMethodListener var2 = this.inputMethodListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 1100:
            var2.inputMethodTextChanged(var1);
            break;
         case 1101:
            var2.caretPositionChanged(var1);
         }
      }

   }

   protected void processHierarchyEvent(HierarchyEvent var1) {
      HierarchyListener var2 = this.hierarchyListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 1400:
            var2.hierarchyChanged(var1);
         }
      }

   }

   protected void processHierarchyBoundsEvent(HierarchyEvent var1) {
      HierarchyBoundsListener var2 = this.hierarchyBoundsListener;
      if (var2 != null) {
         int var3 = var1.getID();
         switch(var3) {
         case 1401:
            var2.ancestorMoved(var1);
            break;
         case 1402:
            var2.ancestorResized(var1);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public boolean handleEvent(Event var1) {
      switch(var1.id) {
      case 401:
      case 403:
         return this.keyDown(var1, var1.key);
      case 402:
      case 404:
         return this.keyUp(var1, var1.key);
      case 501:
         return this.mouseDown(var1, var1.x, var1.y);
      case 502:
         return this.mouseUp(var1, var1.x, var1.y);
      case 503:
         return this.mouseMove(var1, var1.x, var1.y);
      case 504:
         return this.mouseEnter(var1, var1.x, var1.y);
      case 505:
         return this.mouseExit(var1, var1.x, var1.y);
      case 506:
         return this.mouseDrag(var1, var1.x, var1.y);
      case 1001:
         return this.action(var1, var1.arg);
      case 1004:
         return this.gotFocus(var1, var1.arg);
      case 1005:
         return this.lostFocus(var1, var1.arg);
      default:
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseDown(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseDrag(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseUp(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseMove(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseEnter(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean mouseExit(Event var1, int var2, int var3) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean keyDown(Event var1, int var2) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean keyUp(Event var1, int var2) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean action(Event var1, Object var2) {
      return false;
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         Object var2 = this.peer;
         if (var2 != null && !(var2 instanceof LightweightPeer)) {
            Container var9 = this.getContainer();
            if (var9 != null && var9.isLightweight()) {
               this.relocateComponent();
               if (!var9.isRecursivelyVisibleUpToHeavyweightContainer()) {
                  ((ComponentPeer)var2).setVisible(false);
               }
            }
         } else {
            if (var2 == null) {
               this.peer = (ComponentPeer)(var2 = this.getToolkit().createComponent(this));
            }

            if (this.parent != null) {
               long var3 = 0L;
               if (this.mouseListener != null || (this.eventMask & 16L) != 0L) {
                  var3 |= 16L;
               }

               if (this.mouseMotionListener != null || (this.eventMask & 32L) != 0L) {
                  var3 |= 32L;
               }

               if (this.mouseWheelListener != null || (this.eventMask & 131072L) != 0L) {
                  var3 |= 131072L;
               }

               if (this.focusListener != null || (this.eventMask & 4L) != 0L) {
                  var3 |= 4L;
               }

               if (this.keyListener != null || (this.eventMask & 8L) != 0L) {
                  var3 |= 8L;
               }

               if (var3 != 0L) {
                  this.parent.proxyEnableEvents(var3);
               }
            }
         }

         this.invalidate();
         int var10 = this.popups != null ? this.popups.size() : 0;

         for(int var4 = 0; var4 < var10; ++var4) {
            PopupMenu var5 = (PopupMenu)this.popups.elementAt(var4);
            var5.addNotify();
         }

         if (this.dropTarget != null) {
            this.dropTarget.addNotify((ComponentPeer)var2);
         }

         this.peerFont = this.getFont();
         if (this.getContainer() != null && !this.isAddNotifyComplete) {
            this.getContainer().increaseComponentCount(this);
         }

         this.updateZOrder();
         if (!this.isAddNotifyComplete) {
            this.mixOnShowing();
         }

         this.isAddNotifyComplete = true;
         if (this.hierarchyListener != null || (this.eventMask & 32768L) != 0L || Toolkit.enabledOnToolkit(32768L)) {
            HierarchyEvent var8 = new HierarchyEvent(this, 1400, this, this.parent, (long)(2 | (this.isRecursivelyVisible() ? 4 : 0)));
            this.dispatchEvent(var8);
         }

      }
   }

   public void removeNotify() {
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == this) {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalPermanentFocusOwner((Component)null);
      }

      synchronized(this.getTreeLock()) {
         if (this.isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this)) {
            this.transferFocus(true);
         }

         if (this.getContainer() != null && this.isAddNotifyComplete) {
            this.getContainer().decreaseComponentCount(this);
         }

         int var2 = this.popups != null ? this.popups.size() : 0;

         for(int var3 = 0; var3 < var2; ++var3) {
            PopupMenu var4 = (PopupMenu)this.popups.elementAt(var3);
            var4.removeNotify();
         }

         if ((this.eventMask & 4096L) != 0L) {
            InputContext var7 = this.getInputContext();
            if (var7 != null) {
               var7.removeNotify(this);
            }
         }

         ComponentPeer var8 = this.peer;
         if (var8 != null) {
            boolean var9 = this.isLightweight();
            if (this.bufferStrategy instanceof Component.FlipBufferStrategy) {
               ((Component.FlipBufferStrategy)this.bufferStrategy).destroyBuffers();
            }

            if (this.dropTarget != null) {
               this.dropTarget.removeNotify(this.peer);
            }

            if (this.visible) {
               var8.setVisible(false);
            }

            this.peer = null;
            this.peerFont = null;
            Toolkit.getEventQueue().removeSourceEvents(this, false);
            KeyboardFocusManager.getCurrentKeyboardFocusManager().discardKeyEvents(this);
            var8.dispose();
            this.mixOnHiding(var9);
            this.isAddNotifyComplete = false;
            this.compoundShape = null;
         }

         if (this.hierarchyListener != null || (this.eventMask & 32768L) != 0L || Toolkit.enabledOnToolkit(32768L)) {
            HierarchyEvent var10 = new HierarchyEvent(this, 1400, this, this.parent, (long)(2 | (this.isRecursivelyVisible() ? 4 : 0)));
            this.dispatchEvent(var10);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public boolean gotFocus(Event var1, Object var2) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean lostFocus(Event var1, Object var2) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isFocusTraversable() {
      if (this.isFocusTraversableOverridden == 0) {
         this.isFocusTraversableOverridden = 1;
      }

      return this.focusable;
   }

   public boolean isFocusable() {
      return this.isFocusTraversable();
   }

   public void setFocusable(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.focusable;
         this.focusable = var1;
      }

      this.isFocusTraversableOverridden = 2;
      this.firePropertyChange("focusable", var2, var1);
      if (var2 && !var1) {
         if (this.isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabled()) {
            this.transferFocus(true);
         }

         KeyboardFocusManager.clearMostRecentFocusOwner(this);
      }

   }

   final boolean isFocusTraversableOverridden() {
      return this.isFocusTraversableOverridden != 1;
   }

   public void setFocusTraversalKeys(int var1, Set<? extends AWTKeyStroke> var2) {
      if (var1 >= 0 && var1 < 3) {
         this.setFocusTraversalKeys_NoIDCheck(var1, var2);
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public Set<AWTKeyStroke> getFocusTraversalKeys(int var1) {
      if (var1 >= 0 && var1 < 3) {
         return this.getFocusTraversalKeys_NoIDCheck(var1);
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   final void setFocusTraversalKeys_NoIDCheck(int var1, Set<? extends AWTKeyStroke> var2) {
      Set var3;
      synchronized(this) {
         if (this.focusTraversalKeys == null) {
            this.initializeFocusTraversalKeys();
         }

         if (var2 != null) {
            Iterator var5 = var2.iterator();

            while(var5.hasNext()) {
               AWTKeyStroke var6 = (AWTKeyStroke)var5.next();
               if (var6 == null) {
                  throw new IllegalArgumentException("cannot set null focus traversal key");
               }

               if (var6.getKeyChar() != '\uffff') {
                  throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
               }

               for(int var7 = 0; var7 < this.focusTraversalKeys.length; ++var7) {
                  if (var7 != var1 && this.getFocusTraversalKeys_NoIDCheck(var7).contains(var6)) {
                     throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
                  }
               }
            }
         }

         var3 = this.focusTraversalKeys[var1];
         this.focusTraversalKeys[var1] = var2 != null ? Collections.unmodifiableSet(new HashSet(var2)) : null;
      }

      this.firePropertyChange(focusTraversalKeyPropertyNames[var1], var3, var2);
   }

   final Set<AWTKeyStroke> getFocusTraversalKeys_NoIDCheck(int var1) {
      Set var2 = this.focusTraversalKeys != null ? this.focusTraversalKeys[var1] : null;
      if (var2 != null) {
         return var2;
      } else {
         Container var3 = this.parent;
         return var3 != null ? var3.getFocusTraversalKeys(var1) : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(var1);
      }
   }

   public boolean areFocusTraversalKeysSet(int var1) {
      if (var1 >= 0 && var1 < 3) {
         return this.focusTraversalKeys != null && this.focusTraversalKeys[var1] != null;
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public void setFocusTraversalKeysEnabled(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.focusTraversalKeysEnabled;
         this.focusTraversalKeysEnabled = var1;
      }

      this.firePropertyChange("focusTraversalKeysEnabled", var2, var1);
   }

   public boolean getFocusTraversalKeysEnabled() {
      return this.focusTraversalKeysEnabled;
   }

   public void requestFocus() {
      this.requestFocusHelper(false, true);
   }

   boolean requestFocus(CausedFocusEvent.Cause var1) {
      return this.requestFocusHelper(false, true, var1);
   }

   protected boolean requestFocus(boolean var1) {
      return this.requestFocusHelper(var1, true);
   }

   boolean requestFocus(boolean var1, CausedFocusEvent.Cause var2) {
      return this.requestFocusHelper(var1, true, var2);
   }

   public boolean requestFocusInWindow() {
      return this.requestFocusHelper(false, false);
   }

   boolean requestFocusInWindow(CausedFocusEvent.Cause var1) {
      return this.requestFocusHelper(false, false, var1);
   }

   protected boolean requestFocusInWindow(boolean var1) {
      return this.requestFocusHelper(var1, false);
   }

   boolean requestFocusInWindow(boolean var1, CausedFocusEvent.Cause var2) {
      return this.requestFocusHelper(var1, false, var2);
   }

   final boolean requestFocusHelper(boolean var1, boolean var2) {
      return this.requestFocusHelper(var1, var2, CausedFocusEvent.Cause.UNKNOWN);
   }

   final boolean requestFocusHelper(boolean var1, boolean var2, CausedFocusEvent.Cause var3) {
      AWTEvent var4 = EventQueue.getCurrentEvent();
      if (var4 instanceof MouseEvent && SunToolkit.isSystemGenerated(var4)) {
         Component var5 = ((MouseEvent)var4).getComponent();
         if (var5 == null || var5.getContainingWindow() == this.getContainingWindow()) {
            focusLog.finest("requesting focus by mouse event \"in window\"");
            var2 = false;
         }
      }

      if (!this.isRequestFocusAccepted(var1, var2, var3)) {
         if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("requestFocus is not accepted");
         }

         return false;
      } else {
         KeyboardFocusManager.setMostRecentFocusOwner(this);

         for(Object var11 = this; var11 != null && !(var11 instanceof Window); var11 = ((Component)var11).parent) {
            if (!((Component)var11).isVisible()) {
               if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("component is recurively invisible");
               }

               return false;
            }
         }

         ComponentPeer var6 = this.peer;
         Object var7 = var6 instanceof LightweightPeer ? this.getNativeContainer() : this;
         if (var7 != null && ((Component)var7).isVisible()) {
            var6 = ((Component)var7).peer;
            if (var6 == null) {
               if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("Peer is null");
               }

               return false;
            } else {
               long var8 = 0L;
               if (EventQueue.isDispatchThread()) {
                  var8 = Toolkit.getEventQueue().getMostRecentKeyEventTime();
               } else {
                  var8 = System.currentTimeMillis();
               }

               boolean var10 = var6.requestFocus(this, var1, var2, var8, var3);
               if (!var10) {
                  KeyboardFocusManager.getCurrentKeyboardFocusManager(this.appContext).dequeueKeyEvents(var8, this);
                  if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                     focusLog.finest("Peer request failed");
                  }
               } else if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("Pass for " + this);
               }

               return var10;
            }
         } else {
            if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
               focusLog.finest("Component is not a part of visible hierarchy");
            }

            return false;
         }
      }
   }

   private boolean isRequestFocusAccepted(boolean var1, boolean var2, CausedFocusEvent.Cause var3) {
      if (this.isFocusable() && this.isVisible()) {
         ComponentPeer var4 = this.peer;
         if (var4 == null) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
               focusLog.finest("peer is null");
            }

            return false;
         } else {
            Window var5 = this.getContainingWindow();
            if (var5 != null && var5.isFocusableWindow()) {
               Component var6 = KeyboardFocusManager.getMostRecentFocusOwner(var5);
               if (var6 == null) {
                  var6 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                  if (var6 != null && var6.getContainingWindow() != var5) {
                     var6 = null;
                  }
               }

               if (var6 != this && var6 != null) {
                  if (CausedFocusEvent.Cause.ACTIVATION == var3) {
                     if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        focusLog.finest("cause is activation");
                     }

                     return true;
                  } else {
                     boolean var7 = requestFocusController.acceptRequestFocus(var6, this, var1, var2, var3);
                     if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                        focusLog.finest("RequestFocusController returns {0}", var7);
                     }

                     return var7;
                  }
               } else {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                     focusLog.finest("focus owner is null or this");
                  }

                  return true;
               }
            } else {
               if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("Component doesn't have toplevel");
               }

               return false;
            }
         }
      } else {
         if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("Not focusable or not visible");
         }

         return false;
      }
   }

   static synchronized void setRequestFocusController(RequestFocusController var0) {
      if (var0 == null) {
         requestFocusController = new Component.DummyRequestFocusController();
      } else {
         requestFocusController = var0;
      }

   }

   public Container getFocusCycleRootAncestor() {
      Container var1;
      for(var1 = this.parent; var1 != null && !var1.isFocusCycleRoot(); var1 = var1.parent) {
      }

      return var1;
   }

   public boolean isFocusCycleRoot(Container var1) {
      Container var2 = this.getFocusCycleRootAncestor();
      return var2 == var1;
   }

   Container getTraversalRoot() {
      return this.getFocusCycleRootAncestor();
   }

   public void transferFocus() {
      this.nextFocus();
   }

   /** @deprecated */
   @Deprecated
   public void nextFocus() {
      this.transferFocus(false);
   }

   boolean transferFocus(boolean var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("clearOnFailure = " + var1);
      }

      Component var2 = this.getNextFocusCandidate();
      boolean var3 = false;
      if (var2 != null && !var2.isFocusOwner() && var2 != this) {
         var3 = var2.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_FORWARD);
      }

      if (var1 && !var3) {
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("clear global focus owner");
         }

         KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
      }

      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("returning result: " + var3);
      }

      return var3;
   }

   final Component getNextFocusCandidate() {
      Container var1 = this.getTraversalRoot();

      Object var2;
      for(var2 = this; var1 != null && (!var1.isShowing() || !var1.canBeFocusOwner()); var1 = var1.getFocusCycleRootAncestor()) {
         var2 = var1;
      }

      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("comp = " + var2 + ", root = " + var1);
      }

      Object var3 = null;
      if (var1 != null) {
         FocusTraversalPolicy var4 = var1.getFocusTraversalPolicy();
         Object var5 = var4.getComponentAfter(var1, (Component)var2);
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("component after is " + var5);
         }

         if (var5 == null) {
            var5 = var4.getDefaultComponent(var1);
            if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
               focusLog.finer("default component is " + var5);
            }
         }

         if (var5 == null) {
            Applet var6 = EmbeddedFrame.getAppletIfAncestorOf(this);
            if (var6 != null) {
               var5 = var6;
            }
         }

         var3 = var5;
      }

      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("Focus transfer candidate: " + var3);
      }

      return (Component)var3;
   }

   public void transferFocusBackward() {
      this.transferFocusBackward(false);
   }

   boolean transferFocusBackward(boolean var1) {
      Container var2 = this.getTraversalRoot();

      Object var3;
      for(var3 = this; var2 != null && (!var2.isShowing() || !var2.canBeFocusOwner()); var2 = var2.getFocusCycleRootAncestor()) {
         var3 = var2;
      }

      boolean var4 = false;
      if (var2 != null) {
         FocusTraversalPolicy var5 = var2.getFocusTraversalPolicy();
         Component var6 = var5.getComponentBefore(var2, (Component)var3);
         if (var6 == null) {
            var6 = var5.getDefaultComponent(var2);
         }

         if (var6 != null) {
            var4 = var6.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_BACKWARD);
         }
      }

      if (var1 && !var4) {
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("clear global focus owner");
         }

         KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
      }

      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("returning result: " + var4);
      }

      return var4;
   }

   public void transferFocusUpCycle() {
      Container var1;
      for(var1 = this.getFocusCycleRootAncestor(); var1 != null && (!var1.isShowing() || !var1.isFocusable() || !var1.isEnabled()); var1 = var1.getFocusCycleRootAncestor()) {
      }

      if (var1 != null) {
         Container var2 = var1.getFocusCycleRootAncestor();
         Container var3 = var2 != null ? var2 : var1;
         KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(var3);
         var1.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
      } else {
         Window var4 = this.getContainingWindow();
         if (var4 != null) {
            Component var5 = var4.getFocusTraversalPolicy().getDefaultComponent(var4);
            if (var5 != null) {
               KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(var4);
               var5.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
            }
         }
      }

   }

   public boolean hasFocus() {
      return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this;
   }

   public boolean isFocusOwner() {
      return this.hasFocus();
   }

   void setAutoFocusTransferOnDisposal(boolean var1) {
      this.autoFocusTransferOnDisposal = var1;
   }

   boolean isAutoFocusTransferOnDisposal() {
      return this.autoFocusTransferOnDisposal;
   }

   public void add(PopupMenu var1) {
      synchronized(this.getTreeLock()) {
         if (var1.parent != null) {
            var1.parent.remove(var1);
         }

         if (this.popups == null) {
            this.popups = new Vector();
         }

         this.popups.addElement(var1);
         var1.parent = this;
         if (this.peer != null && var1.peer == null) {
            var1.addNotify();
         }

      }
   }

   public void remove(MenuComponent var1) {
      synchronized(this.getTreeLock()) {
         if (this.popups != null) {
            int var3 = this.popups.indexOf(var1);
            if (var3 >= 0) {
               PopupMenu var4 = (PopupMenu)var1;
               if (var4.peer != null) {
                  var4.removeNotify();
               }

               var4.parent = null;
               this.popups.removeElementAt(var3);
               if (this.popups.size() == 0) {
                  this.popups = null;
               }
            }

         }
      }
   }

   protected String paramString() {
      String var1 = Objects.toString(this.getName(), "");
      String var2 = this.isValid() ? "" : ",invalid";
      String var3 = this.visible ? "" : ",hidden";
      String var4 = this.enabled ? "" : ",disabled";
      return var1 + ',' + this.x + ',' + this.y + ',' + this.width + 'x' + this.height + var2 + var3 + var4;
   }

   public String toString() {
      return this.getClass().getName() + '[' + this.paramString() + ']';
   }

   public void list() {
      this.list((PrintStream)System.out, 0);
   }

   public void list(PrintStream var1) {
      this.list((PrintStream)var1, 0);
   }

   public void list(PrintStream var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1.print(" ");
      }

      var1.println((Object)this);
   }

   public void list(PrintWriter var1) {
      this.list((PrintWriter)var1, 0);
   }

   public void list(PrintWriter var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1.print(" ");
      }

      var1.println((Object)this);
   }

   final Container getNativeContainer() {
      Container var1;
      for(var1 = this.getContainer(); var1 != null && var1.peer instanceof LightweightPeer; var1 = var1.getContainer()) {
      }

      return var1;
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      synchronized(this.getObjectLock()) {
         if (var1 != null) {
            if (this.changeSupport == null) {
               this.changeSupport = new PropertyChangeSupport(this);
            }

            this.changeSupport.addPropertyChangeListener(var1);
         }
      }
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      synchronized(this.getObjectLock()) {
         if (var1 != null && this.changeSupport != null) {
            this.changeSupport.removePropertyChangeListener(var1);
         }
      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      synchronized(this.getObjectLock()) {
         return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
      }
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      synchronized(this.getObjectLock()) {
         if (var2 != null) {
            if (this.changeSupport == null) {
               this.changeSupport = new PropertyChangeSupport(this);
            }

            this.changeSupport.addPropertyChangeListener(var1, var2);
         }
      }
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      synchronized(this.getObjectLock()) {
         if (var2 != null && this.changeSupport != null) {
            this.changeSupport.removePropertyChangeListener(var1, var2);
         }
      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      synchronized(this.getObjectLock()) {
         return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(var1);
      }
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      PropertyChangeSupport var4;
      synchronized(this.getObjectLock()) {
         var4 = this.changeSupport;
      }

      if (var4 != null && (var2 == null || var3 == null || !var2.equals(var3))) {
         var4.firePropertyChange(var1, var2, var3);
      }
   }

   protected void firePropertyChange(String var1, boolean var2, boolean var3) {
      PropertyChangeSupport var4 = this.changeSupport;
      if (var4 != null && var2 != var3) {
         var4.firePropertyChange(var1, var2, var3);
      }
   }

   protected void firePropertyChange(String var1, int var2, int var3) {
      PropertyChangeSupport var4 = this.changeSupport;
      if (var4 != null && var2 != var3) {
         var4.firePropertyChange(var1, var2, var3);
      }
   }

   public void firePropertyChange(String var1, byte var2, byte var3) {
      if (this.changeSupport != null && var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }
   }

   public void firePropertyChange(String var1, char var2, char var3) {
      if (this.changeSupport != null && var2 != var3) {
         this.firePropertyChange(var1, new Character(var2), new Character(var3));
      }
   }

   public void firePropertyChange(String var1, short var2, short var3) {
      if (this.changeSupport != null && var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }
   }

   public void firePropertyChange(String var1, long var2, long var4) {
      if (this.changeSupport != null && var2 != var4) {
         this.firePropertyChange(var1, var2, var4);
      }
   }

   public void firePropertyChange(String var1, float var2, float var3) {
      if (this.changeSupport != null && var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }
   }

   public void firePropertyChange(String var1, double var2, double var4) {
      if (this.changeSupport != null && var2 != var4) {
         this.firePropertyChange(var1, var2, var4);
      }
   }

   private void doSwingSerialization() {
      Package var1 = Package.getPackage("javax.swing");

      for(final Class var2 = this.getClass(); var2 != null; var2 = var2.getSuperclass()) {
         if (var2.getPackage() == var1 && var2.getClassLoader() == null) {
            Method[] var4 = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
               public Method[] run() {
                  return var2.getDeclaredMethods();
               }
            });

            for(int var5 = var4.length - 1; var5 >= 0; --var5) {
               final Method var6 = var4[var5];
               if (var6.getName().equals("compWriteObjectNotify")) {
                  AccessController.doPrivileged(new PrivilegedAction<Void>() {
                     public Void run() {
                        var6.setAccessible(true);
                        return null;
                     }
                  });

                  try {
                     var6.invoke(this, (Object[])null);
                  } catch (IllegalAccessException var8) {
                  } catch (InvocationTargetException var9) {
                  }

                  return;
               }
            }
         }
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.doSwingSerialization();
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "componentL", this.componentListener);
      AWTEventMulticaster.save(var1, "focusL", this.focusListener);
      AWTEventMulticaster.save(var1, "keyL", this.keyListener);
      AWTEventMulticaster.save(var1, "mouseL", this.mouseListener);
      AWTEventMulticaster.save(var1, "mouseMotionL", this.mouseMotionListener);
      AWTEventMulticaster.save(var1, "inputMethodL", this.inputMethodListener);
      var1.writeObject((Object)null);
      var1.writeObject(this.componentOrientation);
      AWTEventMulticaster.save(var1, "hierarchyL", this.hierarchyListener);
      AWTEventMulticaster.save(var1, "hierarchyBoundsL", this.hierarchyBoundsListener);
      var1.writeObject((Object)null);
      AWTEventMulticaster.save(var1, "mouseWheelL", this.mouseWheelListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.objectLock = new Object();
      this.acc = AccessController.getContext();
      var1.defaultReadObject();
      this.appContext = AppContext.getAppContext();
      this.coalescingEnabled = this.checkCoalescing();
      if (this.componentSerializedDataVersion < 4) {
         this.focusable = true;
         this.isFocusTraversableOverridden = 0;
         this.initializeFocusTraversalKeys();
         this.focusTraversalKeysEnabled = true;
      }

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("componentL" == var3) {
            this.addComponentListener((ComponentListener)((ComponentListener)var1.readObject()));
         } else if ("focusL" == var3) {
            this.addFocusListener((FocusListener)((FocusListener)var1.readObject()));
         } else if ("keyL" == var3) {
            this.addKeyListener((KeyListener)((KeyListener)var1.readObject()));
         } else if ("mouseL" == var3) {
            this.addMouseListener((MouseListener)((MouseListener)var1.readObject()));
         } else if ("mouseMotionL" == var3) {
            this.addMouseMotionListener((MouseMotionListener)((MouseMotionListener)var1.readObject()));
         } else if ("inputMethodL" == var3) {
            this.addInputMethodListener((InputMethodListener)((InputMethodListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

      Object var10 = null;

      try {
         var10 = var1.readObject();
      } catch (OptionalDataException var9) {
         if (!var9.eof) {
            throw var9;
         }
      }

      if (var10 != null) {
         this.componentOrientation = (ComponentOrientation)var10;
      } else {
         this.componentOrientation = ComponentOrientation.UNKNOWN;
      }

      String var4;
      try {
         while(null != (var2 = var1.readObject())) {
            var4 = ((String)var2).intern();
            if ("hierarchyL" == var4) {
               this.addHierarchyListener((HierarchyListener)((HierarchyListener)var1.readObject()));
            } else if ("hierarchyBoundsL" == var4) {
               this.addHierarchyBoundsListener((HierarchyBoundsListener)((HierarchyBoundsListener)var1.readObject()));
            } else {
               var1.readObject();
            }
         }
      } catch (OptionalDataException var8) {
         if (!var8.eof) {
            throw var8;
         }
      }

      try {
         while(null != (var2 = var1.readObject())) {
            var4 = ((String)var2).intern();
            if ("mouseWheelL" == var4) {
               this.addMouseWheelListener((MouseWheelListener)((MouseWheelListener)var1.readObject()));
            } else {
               var1.readObject();
            }
         }
      } catch (OptionalDataException var7) {
         if (!var7.eof) {
            throw var7;
         }
      }

      if (this.popups != null) {
         int var11 = this.popups.size();

         for(int var5 = 0; var5 < var11; ++var5) {
            PopupMenu var6 = (PopupMenu)this.popups.elementAt(var5);
            var6.parent = this;
         }
      }

   }

   public void setComponentOrientation(ComponentOrientation var1) {
      ComponentOrientation var2 = this.componentOrientation;
      this.componentOrientation = var1;
      this.firePropertyChange("componentOrientation", var2, var1);
      this.invalidateIfValid();
   }

   public ComponentOrientation getComponentOrientation() {
      return this.componentOrientation;
   }

   public void applyComponentOrientation(ComponentOrientation var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.setComponentOrientation(var1);
      }
   }

   final boolean canBeFocusOwner() {
      return this.isEnabled() && this.isDisplayable() && this.isVisible() && this.isFocusable();
   }

   final boolean canBeFocusOwnerRecursively() {
      if (!this.canBeFocusOwner()) {
         return false;
      } else {
         synchronized(this.getTreeLock()) {
            return this.parent != null ? this.parent.canContainFocusOwner(this) : true;
         }
      }
   }

   final void relocateComponent() {
      synchronized(this.getTreeLock()) {
         if (this.peer != null) {
            int var2 = this.x;
            int var3 = this.y;

            for(Container var4 = this.getContainer(); var4 != null && var4.isLightweight(); var4 = var4.getContainer()) {
               var2 += var4.x;
               var3 += var4.y;
            }

            this.peer.setBounds(var2, var3, this.width, this.height, 1);
         }
      }
   }

   Window getContainingWindow() {
      return SunToolkit.getContainingWindow(this);
   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      return this.accessibleContext;
   }

   int getAccessibleIndexInParent() {
      synchronized(this.getTreeLock()) {
         int var2 = -1;
         Container var3 = this.getParent();
         if (var3 != null && var3 instanceof Accessible) {
            Component[] var4 = var3.getComponents();

            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5] instanceof Accessible) {
                  ++var2;
               }

               if (this.equals(var4[var5])) {
                  return var2;
               }
            }
         }

         return -1;
      }
   }

   AccessibleStateSet getAccessibleStateSet() {
      synchronized(this.getTreeLock()) {
         AccessibleStateSet var2 = new AccessibleStateSet();
         if (this.isEnabled()) {
            var2.add(AccessibleState.ENABLED);
         }

         if (this.isFocusTraversable()) {
            var2.add(AccessibleState.FOCUSABLE);
         }

         if (this.isVisible()) {
            var2.add(AccessibleState.VISIBLE);
         }

         if (this.isShowing()) {
            var2.add(AccessibleState.SHOWING);
         }

         if (this.isFocusOwner()) {
            var2.add(AccessibleState.FOCUSED);
         }

         if (this instanceof Accessible) {
            AccessibleContext var3 = ((Accessible)this).getAccessibleContext();
            if (var3 != null) {
               Accessible var4 = var3.getAccessibleParent();
               if (var4 != null) {
                  AccessibleContext var5 = var4.getAccessibleContext();
                  if (var5 != null) {
                     AccessibleSelection var6 = var5.getAccessibleSelection();
                     if (var6 != null) {
                        var2.add(AccessibleState.SELECTABLE);
                        int var7 = var3.getAccessibleIndexInParent();
                        if (var7 >= 0 && var6.isAccessibleChildSelected(var7)) {
                           var2.add(AccessibleState.SELECTED);
                        }
                     }
                  }
               }
            }
         }

         if (isInstanceOf(this, "javax.swing.JComponent") && ((JComponent)this).isOpaque()) {
            var2.add(AccessibleState.OPAQUE);
         }

         return var2;
      }
   }

   static boolean isInstanceOf(Object var0, String var1) {
      if (var0 == null) {
         return false;
      } else if (var1 == null) {
         return false;
      } else {
         for(Class var2 = var0.getClass(); var2 != null; var2 = var2.getSuperclass()) {
            if (var2.getName().equals(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   final boolean areBoundsValid() {
      Container var1 = this.getContainer();
      return var1 == null || var1.isValid() || var1.getLayout() == null;
   }

   void applyCompoundShape(Region var1) {
      this.checkTreeLock();
      if (!this.areBoundsValid()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
         }

      } else {
         if (!this.isLightweight()) {
            ComponentPeer var2 = this.getPeer();
            if (var2 != null) {
               if (var1.isEmpty()) {
                  var1 = Region.EMPTY_REGION;
               }

               if (var1.equals(this.getNormalShape())) {
                  if (this.compoundShape == null) {
                     return;
                  }

                  this.compoundShape = null;
                  var2.applyShape((Region)null);
               } else {
                  if (var1.equals(this.getAppliedShape())) {
                     return;
                  }

                  this.compoundShape = var1;
                  Point var3 = this.getLocationOnWindow();
                  if (mixingLog.isLoggable(PlatformLogger.Level.FINER)) {
                     mixingLog.fine("this = " + this + "; compAbsolute=" + var3 + "; shape=" + var1);
                  }

                  var2.applyShape(var1.getTranslatedRegion(-var3.x, -var3.y));
               }
            }
         }

      }
   }

   private Region getAppliedShape() {
      this.checkTreeLock();
      return this.compoundShape != null && !this.isLightweight() ? this.compoundShape : this.getNormalShape();
   }

   Point getLocationOnWindow() {
      this.checkTreeLock();
      Point var1 = this.getLocation();

      for(Container var2 = this.getContainer(); var2 != null && !(var2 instanceof Window); var2 = var2.getContainer()) {
         var1.x += var2.getX();
         var1.y += var2.getY();
      }

      return var1;
   }

   final Region getNormalShape() {
      this.checkTreeLock();
      Point var1 = this.getLocationOnWindow();
      return Region.getInstanceXYWH(var1.x, var1.y, this.getWidth(), this.getHeight());
   }

   Region getOpaqueShape() {
      this.checkTreeLock();
      return this.mixingCutoutRegion != null ? this.mixingCutoutRegion : this.getNormalShape();
   }

   final int getSiblingIndexAbove() {
      this.checkTreeLock();
      Container var1 = this.getContainer();
      if (var1 == null) {
         return -1;
      } else {
         int var2 = var1.getComponentZOrder(this) - 1;
         return var2 < 0 ? -1 : var2;
      }
   }

   final ComponentPeer getHWPeerAboveMe() {
      this.checkTreeLock();
      Container var1 = this.getContainer();

      for(int var2 = this.getSiblingIndexAbove(); var1 != null; var1 = var1.getContainer()) {
         for(int var3 = var2; var3 > -1; --var3) {
            Component var4 = var1.getComponent(var3);
            if (var4 != null && var4.isDisplayable() && !var4.isLightweight()) {
               return var4.getPeer();
            }
         }

         if (!var1.isLightweight()) {
            break;
         }

         var2 = var1.getSiblingIndexAbove();
      }

      return null;
   }

   final int getSiblingIndexBelow() {
      this.checkTreeLock();
      Container var1 = this.getContainer();
      if (var1 == null) {
         return -1;
      } else {
         int var2 = var1.getComponentZOrder(this) + 1;
         return var2 >= var1.getComponentCount() ? -1 : var2;
      }
   }

   final boolean isNonOpaqueForMixing() {
      return this.mixingCutoutRegion != null && this.mixingCutoutRegion.isEmpty();
   }

   private Region calculateCurrentShape() {
      this.checkTreeLock();
      Region var1 = this.getNormalShape();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
         mixingLog.fine("this = " + this + "; normalShape=" + var1);
      }

      if (this.getContainer() != null) {
         Object var2 = this;

         for(Container var3 = this.getContainer(); var3 != null; var3 = var3.getContainer()) {
            for(int var4 = ((Component)var2).getSiblingIndexAbove(); var4 != -1; --var4) {
               Component var5 = var3.getComponent(var4);
               if (var5.isLightweight() && var5.isShowing()) {
                  var1 = var1.getDifference(var5.getOpaqueShape());
               }
            }

            if (!var3.isLightweight()) {
               break;
            }

            var1 = var1.getIntersection(var3.getNormalShape());
            var2 = var3;
         }
      }

      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
         mixingLog.fine("currentShape=" + var1);
      }

      return var1;
   }

   void applyCurrentShape() {
      this.checkTreeLock();
      if (!this.areBoundsValid()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
         }

      } else {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         this.applyCompoundShape(this.calculateCurrentShape());
      }
   }

   final void subtractAndApplyShape(Region var1) {
      this.checkTreeLock();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
         mixingLog.fine("this = " + this + "; s=" + var1);
      }

      this.applyCompoundShape(this.getAppliedShape().getDifference(var1));
   }

   private final void applyCurrentShapeBelowMe() {
      this.checkTreeLock();
      Container var1 = this.getContainer();
      if (var1 != null && var1.isShowing()) {
         var1.recursiveApplyCurrentShape(this.getSiblingIndexBelow());

         for(Container var2 = var1.getContainer(); !var1.isOpaque() && var2 != null; var2 = var2.getContainer()) {
            var2.recursiveApplyCurrentShape(var1.getSiblingIndexBelow());
            var1 = var2;
         }
      }

   }

   final void subtractAndApplyShapeBelowMe() {
      this.checkTreeLock();
      Container var1 = this.getContainer();
      if (var1 != null && this.isShowing()) {
         Region var2 = this.getOpaqueShape();
         var1.recursiveSubtractAndApplyShape(var2, this.getSiblingIndexBelow());

         for(Container var3 = var1.getContainer(); !var1.isOpaque() && var3 != null; var3 = var3.getContainer()) {
            var3.recursiveSubtractAndApplyShape(var2, var1.getSiblingIndexBelow());
            var1 = var3;
         }
      }

   }

   void mixOnShowing() {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         if (this.isMixingNeeded()) {
            if (this.isLightweight()) {
               this.subtractAndApplyShapeBelowMe();
            } else {
               this.applyCurrentShape();
            }

         }
      }
   }

   void mixOnHiding(boolean var1) {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; isLightweight = " + var1);
         }

         if (this.isMixingNeeded()) {
            if (var1) {
               this.applyCurrentShapeBelowMe();
            }

         }
      }
   }

   void mixOnReshaping() {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         if (this.isMixingNeeded()) {
            if (this.isLightweight()) {
               this.applyCurrentShapeBelowMe();
            } else {
               this.applyCurrentShape();
            }

         }
      }
   }

   void mixOnZOrderChanging(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         boolean var4 = var2 < var1;
         Container var5 = this.getContainer();
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; oldZorder=" + var1 + "; newZorder=" + var2 + "; parent=" + var5);
         }

         if (this.isMixingNeeded()) {
            if (this.isLightweight()) {
               if (var4) {
                  if (var5 != null && this.isShowing()) {
                     var5.recursiveSubtractAndApplyShape(this.getOpaqueShape(), this.getSiblingIndexBelow(), var1);
                  }
               } else if (var5 != null) {
                  var5.recursiveApplyCurrentShape(var1, var2);
               }
            } else if (var4) {
               this.applyCurrentShape();
            } else if (var5 != null) {
               Region var6 = this.getAppliedShape();

               for(int var7 = var1; var7 < var2; ++var7) {
                  Component var8 = var5.getComponent(var7);
                  if (var8.isLightweight() && var8.isShowing()) {
                     var6 = var6.getDifference(var8.getOpaqueShape());
                  }
               }

               this.applyCompoundShape(var6);
            }

         }
      }
   }

   void mixOnValidating() {
   }

   final boolean isMixingNeeded() {
      if (SunToolkit.getSunAwtDisableMixing()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINEST)) {
            mixingLog.finest("this = " + this + "; Mixing disabled via sun.awt.disableMixing");
         }

         return false;
      } else if (!this.areBoundsValid()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; areBoundsValid = " + this.areBoundsValid());
         }

         return false;
      } else {
         Window var1 = this.getContainingWindow();
         if (var1 != null) {
            if (var1.hasHeavyweightDescendants() && var1.hasLightweightDescendants() && !var1.isDisposing()) {
               return true;
            } else {
               if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
                  mixingLog.fine("containing window = " + var1 + "; has h/w descendants = " + var1.hasHeavyweightDescendants() + "; has l/w descendants = " + var1.hasLightweightDescendants() + "; disposing = " + var1.isDisposing());
               }

               return false;
            }
         } else {
            if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
               mixingLog.fine("this = " + this + "; containing window is null");
            }

            return false;
         }
      }
   }

   void updateZOrder() {
      this.peer.setZOrder(this.getHWPeerAboveMe());
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.image.incrementaldraw")));
      isInc = var0 == null || var0.equals("true");
      var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.image.redrawrate")));
      incRate = var0 != null ? Integer.parseInt(var0) : 100;
      AWTAccessor.setComponentAccessor(new AWTAccessor.ComponentAccessor() {
         public void setBackgroundEraseDisabled(Component var1, boolean var2) {
            var1.backgroundEraseDisabled = var2;
         }

         public boolean getBackgroundEraseDisabled(Component var1) {
            return var1.backgroundEraseDisabled;
         }

         public Rectangle getBounds(Component var1) {
            return new Rectangle(var1.x, var1.y, var1.width, var1.height);
         }

         public void setMixingCutoutShape(Component var1, Shape var2) {
            Region var3 = var2 == null ? null : Region.getInstance(var2, (AffineTransform)null);
            synchronized(var1.getTreeLock()) {
               boolean var5 = false;
               boolean var6 = false;
               if (!var1.isNonOpaqueForMixing()) {
                  var6 = true;
               }

               var1.mixingCutoutRegion = var3;
               if (!var1.isNonOpaqueForMixing()) {
                  var5 = true;
               }

               if (var1.isMixingNeeded()) {
                  if (var6) {
                     var1.mixOnHiding(var1.isLightweight());
                  }

                  if (var5) {
                     var1.mixOnShowing();
                  }
               }

            }
         }

         public void setGraphicsConfiguration(Component var1, GraphicsConfiguration var2) {
            var1.setGraphicsConfiguration(var2);
         }

         public boolean requestFocus(Component var1, CausedFocusEvent.Cause var2) {
            return var1.requestFocus(var2);
         }

         public boolean canBeFocusOwner(Component var1) {
            return var1.canBeFocusOwner();
         }

         public boolean isVisible(Component var1) {
            return var1.isVisible_NoClientCode();
         }

         public void setRequestFocusController(RequestFocusController var1) {
            Component.setRequestFocusController(var1);
         }

         public AppContext getAppContext(Component var1) {
            return var1.appContext;
         }

         public void setAppContext(Component var1, AppContext var2) {
            var1.appContext = var2;
         }

         public Container getParent(Component var1) {
            return var1.getParent_NoClientCode();
         }

         public void setParent(Component var1, Container var2) {
            var1.parent = var2;
         }

         public void setSize(Component var1, int var2, int var3) {
            var1.width = var2;
            var1.height = var3;
         }

         public Point getLocation(Component var1) {
            return var1.location_NoClientCode();
         }

         public void setLocation(Component var1, int var2, int var3) {
            var1.x = var2;
            var1.y = var3;
         }

         public boolean isEnabled(Component var1) {
            return var1.isEnabledImpl();
         }

         public boolean isDisplayable(Component var1) {
            return var1.peer != null;
         }

         public Cursor getCursor(Component var1) {
            return var1.getCursor_NoClientCode();
         }

         public ComponentPeer getPeer(Component var1) {
            return var1.peer;
         }

         public void setPeer(Component var1, ComponentPeer var2) {
            var1.peer = var2;
         }

         public boolean isLightweight(Component var1) {
            return var1.peer instanceof LightweightPeer;
         }

         public boolean getIgnoreRepaint(Component var1) {
            return var1.ignoreRepaint;
         }

         public int getWidth(Component var1) {
            return var1.width;
         }

         public int getHeight(Component var1) {
            return var1.height;
         }

         public int getX(Component var1) {
            return var1.x;
         }

         public int getY(Component var1) {
            return var1.y;
         }

         public Color getForeground(Component var1) {
            return var1.foreground;
         }

         public Color getBackground(Component var1) {
            return var1.background;
         }

         public void setBackground(Component var1, Color var2) {
            var1.background = var2;
         }

         public Font getFont(Component var1) {
            return var1.getFont_NoClientCode();
         }

         public void processEvent(Component var1, AWTEvent var2) {
            var1.processEvent(var2);
         }

         public AccessControlContext getAccessControlContext(Component var1) {
            return var1.getAccessControlContext();
         }

         public void revalidateSynchronously(Component var1) {
            var1.revalidateSynchronously();
         }
      });
      coalesceMap = new WeakHashMap();
      coalesceEventsParams = new Class[]{AWTEvent.class, AWTEvent.class};
      requestFocusController = new Component.DummyRequestFocusController();
   }

   protected abstract class AccessibleAWTComponent extends AccessibleContext implements Serializable, AccessibleComponent {
      private static final long serialVersionUID = 642321655757800191L;
      private transient volatile int propertyListenersCount = 0;
      protected ComponentListener accessibleAWTComponentHandler = null;
      protected FocusListener accessibleAWTFocusHandler = null;

      public void addPropertyChangeListener(PropertyChangeListener var1) {
         if (this.accessibleAWTComponentHandler == null) {
            this.accessibleAWTComponentHandler = new Component.AccessibleAWTComponent.AccessibleAWTComponentHandler();
         }

         if (this.accessibleAWTFocusHandler == null) {
            this.accessibleAWTFocusHandler = new Component.AccessibleAWTComponent.AccessibleAWTFocusHandler();
         }

         if (this.propertyListenersCount++ == 0) {
            Component.this.addComponentListener(this.accessibleAWTComponentHandler);
            Component.this.addFocusListener(this.accessibleAWTFocusHandler);
         }

         super.addPropertyChangeListener(var1);
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
         if (--this.propertyListenersCount == 0) {
            Component.this.removeComponentListener(this.accessibleAWTComponentHandler);
            Component.this.removeFocusListener(this.accessibleAWTFocusHandler);
         }

         super.removePropertyChangeListener(var1);
      }

      public String getAccessibleName() {
         return this.accessibleName;
      }

      public String getAccessibleDescription() {
         return this.accessibleDescription;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.AWT_COMPONENT;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         return Component.this.getAccessibleStateSet();
      }

      public Accessible getAccessibleParent() {
         if (this.accessibleParent != null) {
            return this.accessibleParent;
         } else {
            Container var1 = Component.this.getParent();
            return var1 instanceof Accessible ? (Accessible)var1 : null;
         }
      }

      public int getAccessibleIndexInParent() {
         return Component.this.getAccessibleIndexInParent();
      }

      public int getAccessibleChildrenCount() {
         return 0;
      }

      public Accessible getAccessibleChild(int var1) {
         return null;
      }

      public Locale getLocale() {
         return Component.this.getLocale();
      }

      public AccessibleComponent getAccessibleComponent() {
         return this;
      }

      public Color getBackground() {
         return Component.this.getBackground();
      }

      public void setBackground(Color var1) {
         Component.this.setBackground(var1);
      }

      public Color getForeground() {
         return Component.this.getForeground();
      }

      public void setForeground(Color var1) {
         Component.this.setForeground(var1);
      }

      public Cursor getCursor() {
         return Component.this.getCursor();
      }

      public void setCursor(Cursor var1) {
         Component.this.setCursor(var1);
      }

      public Font getFont() {
         return Component.this.getFont();
      }

      public void setFont(Font var1) {
         Component.this.setFont(var1);
      }

      public FontMetrics getFontMetrics(Font var1) {
         return var1 == null ? null : Component.this.getFontMetrics(var1);
      }

      public boolean isEnabled() {
         return Component.this.isEnabled();
      }

      public void setEnabled(boolean var1) {
         boolean var2 = Component.this.isEnabled();
         Component.this.setEnabled(var1);
         if (var1 != var2 && Component.this.accessibleContext != null) {
            if (var1) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.ENABLED);
            } else {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, (Object)null);
            }
         }

      }

      public boolean isVisible() {
         return Component.this.isVisible();
      }

      public void setVisible(boolean var1) {
         boolean var2 = Component.this.isVisible();
         Component.this.setVisible(var1);
         if (var1 != var2 && Component.this.accessibleContext != null) {
            if (var1) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.VISIBLE);
            } else {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, (Object)null);
            }
         }

      }

      public boolean isShowing() {
         return Component.this.isShowing();
      }

      public boolean contains(Point var1) {
         return Component.this.contains(var1);
      }

      public Point getLocationOnScreen() {
         synchronized(Component.this.getTreeLock()) {
            return Component.this.isShowing() ? Component.this.getLocationOnScreen() : null;
         }
      }

      public Point getLocation() {
         return Component.this.getLocation();
      }

      public void setLocation(Point var1) {
         Component.this.setLocation(var1);
      }

      public Rectangle getBounds() {
         return Component.this.getBounds();
      }

      public void setBounds(Rectangle var1) {
         Component.this.setBounds(var1);
      }

      public Dimension getSize() {
         return Component.this.getSize();
      }

      public void setSize(Dimension var1) {
         Component.this.setSize(var1);
      }

      public Accessible getAccessibleAt(Point var1) {
         return null;
      }

      public boolean isFocusTraversable() {
         return Component.this.isFocusTraversable();
      }

      public void requestFocus() {
         Component.this.requestFocus();
      }

      public void addFocusListener(FocusListener var1) {
         Component.this.addFocusListener(var1);
      }

      public void removeFocusListener(FocusListener var1) {
         Component.this.removeFocusListener(var1);
      }

      protected class AccessibleAWTFocusHandler implements FocusListener {
         public void focusGained(FocusEvent var1) {
            if (Component.this.accessibleContext != null) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.FOCUSED);
            }

         }

         public void focusLost(FocusEvent var1) {
            if (Component.this.accessibleContext != null) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, (Object)null);
            }

         }
      }

      protected class AccessibleAWTComponentHandler implements ComponentListener {
         public void componentHidden(ComponentEvent var1) {
            if (Component.this.accessibleContext != null) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, (Object)null);
            }

         }

         public void componentShown(ComponentEvent var1) {
            if (Component.this.accessibleContext != null) {
               Component.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.VISIBLE);
            }

         }

         public void componentMoved(ComponentEvent var1) {
         }

         public void componentResized(ComponentEvent var1) {
         }
      }
   }

   private static class DummyRequestFocusController implements RequestFocusController {
      private DummyRequestFocusController() {
      }

      public boolean acceptRequestFocus(Component var1, Component var2, boolean var3, boolean var4, CausedFocusEvent.Cause var5) {
         return true;
      }

      // $FF: synthetic method
      DummyRequestFocusController(Object var1) {
         this();
      }
   }

   private class SingleBufferStrategy extends BufferStrategy {
      private BufferCapabilities caps;

      public SingleBufferStrategy(BufferCapabilities var2) {
         this.caps = var2;
      }

      public BufferCapabilities getCapabilities() {
         return this.caps;
      }

      public Graphics getDrawGraphics() {
         return Component.this.getGraphics();
      }

      public boolean contentsLost() {
         return false;
      }

      public boolean contentsRestored() {
         return false;
      }

      public void show() {
      }
   }

   private class BltSubRegionBufferStrategy extends Component.BltBufferStrategy implements SubRegionShowable {
      protected BltSubRegionBufferStrategy(int var2, BufferCapabilities var3) {
         super(var2, var3);
      }

      public void show(int var1, int var2, int var3, int var4) {
         this.showSubRegion(var1, var2, var3, var4);
      }

      public boolean showIfNotLost(int var1, int var2, int var3, int var4) {
         if (!this.contentsLost()) {
            this.showSubRegion(var1, var2, var3, var4);
            return !this.contentsLost();
         } else {
            return false;
         }
      }
   }

   private class FlipSubRegionBufferStrategy extends Component.FlipBufferStrategy implements SubRegionShowable {
      protected FlipSubRegionBufferStrategy(int var2, BufferCapabilities var3) throws AWTException {
         super(var2, var3);
      }

      public void show(int var1, int var2, int var3, int var4) {
         this.showSubRegion(var1, var2, var3, var4);
      }

      public boolean showIfNotLost(int var1, int var2, int var3, int var4) {
         if (!this.contentsLost()) {
            this.showSubRegion(var1, var2, var3, var4);
            return !this.contentsLost();
         } else {
            return false;
         }
      }
   }

   protected class BltBufferStrategy extends BufferStrategy {
      protected BufferCapabilities caps;
      protected VolatileImage[] backBuffers;
      protected boolean validatedContents;
      protected int width;
      protected int height;
      private Insets insets;

      protected BltBufferStrategy(int var2, BufferCapabilities var3) {
         this.caps = var3;
         this.createBackBuffers(var2 - 1);
      }

      public void dispose() {
         if (this.backBuffers != null) {
            for(int var1 = this.backBuffers.length - 1; var1 >= 0; --var1) {
               if (this.backBuffers[var1] != null) {
                  this.backBuffers[var1].flush();
                  this.backBuffers[var1] = null;
               }
            }
         }

         if (Component.this.bufferStrategy == this) {
            Component.this.bufferStrategy = null;
         }

      }

      protected void createBackBuffers(int var1) {
         if (var1 == 0) {
            this.backBuffers = null;
         } else {
            this.width = Component.this.getWidth();
            this.height = Component.this.getHeight();
            this.insets = Component.this.getInsets_NoClientCode();
            int var2 = this.width - this.insets.left - this.insets.right;
            int var3 = this.height - this.insets.top - this.insets.bottom;
            var2 = Math.max(1, var2);
            var3 = Math.max(1, var3);
            int var4;
            if (this.backBuffers == null) {
               this.backBuffers = new VolatileImage[var1];
            } else {
               for(var4 = 0; var4 < var1; ++var4) {
                  if (this.backBuffers[var4] != null) {
                     this.backBuffers[var4].flush();
                     this.backBuffers[var4] = null;
                  }
               }
            }

            for(var4 = 0; var4 < var1; ++var4) {
               this.backBuffers[var4] = Component.this.createVolatileImage(var2, var3);
            }
         }

      }

      public BufferCapabilities getCapabilities() {
         return this.caps;
      }

      public Graphics getDrawGraphics() {
         this.revalidate();
         Image var1 = this.getBackBuffer();
         if (var1 == null) {
            return Component.this.getGraphics();
         } else {
            SunGraphics2D var2 = (SunGraphics2D)var1.getGraphics();
            var2.constrain(-this.insets.left, -this.insets.top, var1.getWidth((ImageObserver)null) + this.insets.left, var1.getHeight((ImageObserver)null) + this.insets.top);
            return var2;
         }
      }

      Image getBackBuffer() {
         return this.backBuffers != null ? this.backBuffers[this.backBuffers.length - 1] : null;
      }

      public void show() {
         this.showSubRegion(this.insets.left, this.insets.top, this.width - this.insets.right, this.height - this.insets.bottom);
      }

      void showSubRegion(int var1, int var2, int var3, int var4) {
         if (this.backBuffers != null) {
            var1 -= this.insets.left;
            var3 -= this.insets.left;
            var2 -= this.insets.top;
            var4 -= this.insets.top;
            Graphics var5 = Component.this.getGraphics_NoClientCode();
            if (var5 != null) {
               try {
                  var5.translate(this.insets.left, this.insets.top);

                  for(int var6 = 0; var6 < this.backBuffers.length; ++var6) {
                     var5.drawImage(this.backBuffers[var6], var1, var2, var3, var4, var1, var2, var3, var4, (ImageObserver)null);
                     var5.dispose();
                     var5 = null;
                     var5 = this.backBuffers[var6].getGraphics();
                  }
               } finally {
                  if (var5 != null) {
                     var5.dispose();
                  }

               }

            }
         }
      }

      protected void revalidate() {
         this.revalidate(true);
      }

      void revalidate(boolean var1) {
         this.validatedContents = false;
         if (this.backBuffers != null) {
            if (var1) {
               Insets var2 = Component.this.getInsets_NoClientCode();
               if (Component.this.getWidth() != this.width || Component.this.getHeight() != this.height || !var2.equals(this.insets)) {
                  this.createBackBuffers(this.backBuffers.length);
                  this.validatedContents = true;
               }
            }

            GraphicsConfiguration var4 = Component.this.getGraphicsConfiguration_NoClientCode();
            int var3 = this.backBuffers[this.backBuffers.length - 1].validate(var4);
            if (var3 == 2) {
               if (var1) {
                  this.createBackBuffers(this.backBuffers.length);
                  this.backBuffers[this.backBuffers.length - 1].validate(var4);
               }

               this.validatedContents = true;
            } else if (var3 == 1) {
               this.validatedContents = true;
            }

         }
      }

      public boolean contentsLost() {
         return this.backBuffers == null ? false : this.backBuffers[this.backBuffers.length - 1].contentsLost();
      }

      public boolean contentsRestored() {
         return this.validatedContents;
      }
   }

   protected class FlipBufferStrategy extends BufferStrategy {
      protected int numBuffers;
      protected BufferCapabilities caps;
      protected Image drawBuffer;
      protected VolatileImage drawVBuffer;
      protected boolean validatedContents;
      int width;
      int height;

      protected FlipBufferStrategy(int var2, BufferCapabilities var3) throws AWTException {
         if (!(Component.this instanceof Window) && !(Component.this instanceof Canvas)) {
            throw new ClassCastException("Component must be a Canvas or Window");
         } else {
            this.numBuffers = var2;
            this.caps = var3;
            this.createBuffers(var2, var3);
         }
      }

      protected void createBuffers(int var1, BufferCapabilities var2) throws AWTException {
         if (var1 < 2) {
            throw new IllegalArgumentException("Number of buffers cannot be less than two");
         } else if (Component.this.peer == null) {
            throw new IllegalStateException("Component must have a valid peer");
         } else if (var2 != null && ((BufferCapabilities)var2).isPageFlipping()) {
            this.width = Component.this.getWidth();
            this.height = Component.this.getHeight();
            if (this.drawBuffer != null) {
               this.drawBuffer = null;
               this.drawVBuffer = null;
               this.destroyBuffers();
            }

            if (var2 instanceof ExtendedBufferCapabilities) {
               ExtendedBufferCapabilities var3 = (ExtendedBufferCapabilities)var2;
               if (var3.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && !VSyncedBSManager.vsyncAllowed(this)) {
                  var2 = var3.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
               }
            }

            Component.this.peer.createBuffers(var1, (BufferCapabilities)var2);
            this.updateInternalBuffers();
         } else {
            throw new IllegalArgumentException("Page flipping capabilities must be specified");
         }
      }

      private void updateInternalBuffers() {
         this.drawBuffer = this.getBackBuffer();
         if (this.drawBuffer instanceof VolatileImage) {
            this.drawVBuffer = (VolatileImage)this.drawBuffer;
         } else {
            this.drawVBuffer = null;
         }

      }

      protected Image getBackBuffer() {
         if (Component.this.peer != null) {
            return Component.this.peer.getBackBuffer();
         } else {
            throw new IllegalStateException("Component must have a valid peer");
         }
      }

      protected void flip(BufferCapabilities.FlipContents var1) {
         if (Component.this.peer != null) {
            Image var2 = this.getBackBuffer();
            if (var2 != null) {
               Component.this.peer.flip(0, 0, var2.getWidth((ImageObserver)null), var2.getHeight((ImageObserver)null), var1);
            }

         } else {
            throw new IllegalStateException("Component must have a valid peer");
         }
      }

      void flipSubRegion(int var1, int var2, int var3, int var4, BufferCapabilities.FlipContents var5) {
         if (Component.this.peer != null) {
            Component.this.peer.flip(var1, var2, var3, var4, var5);
         } else {
            throw new IllegalStateException("Component must have a valid peer");
         }
      }

      protected void destroyBuffers() {
         VSyncedBSManager.releaseVsync(this);
         if (Component.this.peer != null) {
            Component.this.peer.destroyBuffers();
         } else {
            throw new IllegalStateException("Component must have a valid peer");
         }
      }

      public BufferCapabilities getCapabilities() {
         return this.caps instanceof Component.ProxyCapabilities ? ((Component.ProxyCapabilities)this.caps).orig : this.caps;
      }

      public Graphics getDrawGraphics() {
         this.revalidate();
         return this.drawBuffer.getGraphics();
      }

      protected void revalidate() {
         this.revalidate(true);
      }

      void revalidate(boolean var1) {
         this.validatedContents = false;
         if (var1 && (Component.this.getWidth() != this.width || Component.this.getHeight() != this.height)) {
            try {
               this.createBuffers(this.numBuffers, this.caps);
            } catch (AWTException var6) {
            }

            this.validatedContents = true;
         }

         this.updateInternalBuffers();
         if (this.drawVBuffer != null) {
            GraphicsConfiguration var2 = Component.this.getGraphicsConfiguration_NoClientCode();
            int var3 = this.drawVBuffer.validate(var2);
            if (var3 == 2) {
               try {
                  this.createBuffers(this.numBuffers, this.caps);
               } catch (AWTException var5) {
               }

               if (this.drawVBuffer != null) {
                  this.drawVBuffer.validate(var2);
               }

               this.validatedContents = true;
            } else if (var3 == 1) {
               this.validatedContents = true;
            }
         }

      }

      public boolean contentsLost() {
         return this.drawVBuffer == null ? false : this.drawVBuffer.contentsLost();
      }

      public boolean contentsRestored() {
         return this.validatedContents;
      }

      public void show() {
         this.flip(this.caps.getFlipContents());
      }

      void showSubRegion(int var1, int var2, int var3, int var4) {
         this.flipSubRegion(var1, var2, var3, var4, this.caps.getFlipContents());
      }

      public void dispose() {
         if (Component.this.bufferStrategy == this) {
            Component.this.bufferStrategy = null;
            if (Component.this.peer != null) {
               this.destroyBuffers();
            }
         }

      }
   }

   private class ProxyCapabilities extends ExtendedBufferCapabilities {
      private BufferCapabilities orig;

      private ProxyCapabilities(BufferCapabilities var2) {
         super(var2.getFrontBufferCapabilities(), var2.getBackBufferCapabilities(), var2.getFlipContents() == BufferCapabilities.FlipContents.BACKGROUND ? BufferCapabilities.FlipContents.BACKGROUND : BufferCapabilities.FlipContents.COPIED);
         this.orig = var2;
      }

      // $FF: synthetic method
      ProxyCapabilities(BufferCapabilities var2, Object var3) {
         this(var2);
      }
   }

   public static enum BaselineResizeBehavior {
      CONSTANT_ASCENT,
      CONSTANT_DESCENT,
      CENTER_OFFSET,
      OTHER;
   }

   static class AWTTreeLock {
   }
}
