package java.awt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.im.InputContext;
import java.awt.image.BufferStrategy;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public class Window extends Container implements Accessible {
   String warningString;
   transient java.util.List<Image> icons;
   private transient Component temporaryLostComponent;
   static boolean systemSyncLWRequests = false;
   boolean syncLWRequests;
   transient boolean beforeFirstShow;
   private transient boolean disposing;
   transient Window.WindowDisposerRecord disposerRecord;
   static final int OPENED = 1;
   int state;
   private boolean alwaysOnTop;
   private static final IdentityArrayList<Window> allWindows = new IdentityArrayList();
   transient Vector<WeakReference<Window>> ownedWindowList;
   private transient WeakReference<Window> weakThis;
   transient boolean showWithParent;
   transient Dialog modalBlocker;
   Dialog.ModalExclusionType modalExclusionType;
   transient WindowListener windowListener;
   transient WindowStateListener windowStateListener;
   transient WindowFocusListener windowFocusListener;
   transient InputContext inputContext;
   private transient Object inputContextLock;
   private FocusManager focusMgr;
   private boolean focusableWindowState;
   private volatile boolean autoRequestFocus;
   transient boolean isInShow;
   private volatile float opacity;
   private Shape shape;
   private static final String base = "win";
   private static int nameCounter = 0;
   private static final long serialVersionUID = 4497834738069338734L;
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Window");
   private static final boolean locationByPlatformProp;
   transient boolean isTrayIconWindow;
   private transient volatile int securityWarningWidth;
   private transient volatile int securityWarningHeight;
   private transient double securityWarningPointX;
   private transient double securityWarningPointY;
   private transient float securityWarningAlignmentX;
   private transient float securityWarningAlignmentY;
   transient Object anchor;
   private static final AtomicBoolean beforeFirstWindowShown;
   private Window.Type type;
   private int windowSerializedDataVersion;
   private volatile boolean locationByPlatform;

   private static native void initIDs();

   Window(GraphicsConfiguration var1) {
      this.syncLWRequests = false;
      this.beforeFirstShow = true;
      this.disposing = false;
      this.disposerRecord = null;
      this.ownedWindowList = new Vector();
      this.inputContextLock = new Object();
      this.focusableWindowState = true;
      this.autoRequestFocus = true;
      this.isInShow = false;
      this.opacity = 1.0F;
      this.shape = null;
      this.isTrayIconWindow = false;
      this.securityWarningWidth = 0;
      this.securityWarningHeight = 0;
      this.securityWarningPointX = 2.0D;
      this.securityWarningPointY = 0.0D;
      this.securityWarningAlignmentX = 1.0F;
      this.securityWarningAlignmentY = 0.0F;
      this.anchor = new Object();
      this.type = Window.Type.NORMAL;
      this.windowSerializedDataVersion = 2;
      this.locationByPlatform = locationByPlatformProp;
      this.init(var1);
   }

   private GraphicsConfiguration initGC(GraphicsConfiguration var1) {
      GraphicsEnvironment.checkHeadless();
      if (var1 == null) {
         var1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      }

      this.setGraphicsConfiguration(var1);
      return var1;
   }

   private void init(GraphicsConfiguration var1) {
      GraphicsEnvironment.checkHeadless();
      this.syncLWRequests = systemSyncLWRequests;
      this.weakThis = new WeakReference(this);
      this.addToWindowList();
      this.setWarningString();
      this.cursor = Cursor.getPredefinedCursor(0);
      this.visible = false;
      var1 = this.initGC(var1);
      if (var1.getDevice().getType() != 0) {
         throw new IllegalArgumentException("not a screen device");
      } else {
         this.setLayout(new BorderLayout());
         Rectangle var2 = var1.getBounds();
         Insets var3 = this.getToolkit().getScreenInsets(var1);
         int var4 = this.getX() + var2.x + var3.left;
         int var5 = this.getY() + var2.y + var3.top;
         if (var4 != this.x || var5 != this.y) {
            this.setLocation(var4, var5);
            this.setLocationByPlatform(locationByPlatformProp);
         }

         this.modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
         this.disposerRecord = new Window.WindowDisposerRecord(this.appContext, this);
         Disposer.addRecord(this.anchor, this.disposerRecord);
         SunToolkit.checkAndSetPolicy(this);
      }
   }

   Window() throws HeadlessException {
      this.syncLWRequests = false;
      this.beforeFirstShow = true;
      this.disposing = false;
      this.disposerRecord = null;
      this.ownedWindowList = new Vector();
      this.inputContextLock = new Object();
      this.focusableWindowState = true;
      this.autoRequestFocus = true;
      this.isInShow = false;
      this.opacity = 1.0F;
      this.shape = null;
      this.isTrayIconWindow = false;
      this.securityWarningWidth = 0;
      this.securityWarningHeight = 0;
      this.securityWarningPointX = 2.0D;
      this.securityWarningPointY = 0.0D;
      this.securityWarningAlignmentX = 1.0F;
      this.securityWarningAlignmentY = 0.0F;
      this.anchor = new Object();
      this.type = Window.Type.NORMAL;
      this.windowSerializedDataVersion = 2;
      this.locationByPlatform = locationByPlatformProp;
      GraphicsEnvironment.checkHeadless();
      this.init((GraphicsConfiguration)null);
   }

   public Window(Frame var1) {
      this(var1 == null ? (GraphicsConfiguration)null : var1.getGraphicsConfiguration());
      this.ownedInit(var1);
   }

   public Window(Window var1) {
      this(var1 == null ? (GraphicsConfiguration)null : var1.getGraphicsConfiguration());
      this.ownedInit(var1);
   }

   public Window(Window var1, GraphicsConfiguration var2) {
      this(var2);
      this.ownedInit(var1);
   }

   private void ownedInit(Window var1) {
      this.parent = var1;
      if (var1 != null) {
         var1.addOwnedWindow(this.weakThis);
         if (var1.isAlwaysOnTop()) {
            try {
               this.setAlwaysOnTop(true);
            } catch (SecurityException var3) {
            }
         }
      }

      this.disposerRecord.updateOwner();
   }

   String constructComponentName() {
      Class var1 = Window.class;
      synchronized(Window.class) {
         return "win" + nameCounter++;
      }
   }

   public java.util.List<Image> getIconImages() {
      java.util.List var1 = this.icons;
      return var1 != null && var1.size() != 0 ? new ArrayList(var1) : new ArrayList();
   }

   public synchronized void setIconImages(java.util.List<? extends Image> var1) {
      this.icons = var1 == null ? new ArrayList() : new ArrayList(var1);
      WindowPeer var2 = (WindowPeer)this.peer;
      if (var2 != null) {
         var2.updateIconImages();
      }

      this.firePropertyChange("iconImage", (Object)null, (Object)null);
   }

   public void setIconImage(Image var1) {
      ArrayList var2 = new ArrayList();
      if (var1 != null) {
         var2.add(var1);
      }

      this.setIconImages(var2);
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         Container var2 = this.parent;
         if (var2 != null && var2.getPeer() == null) {
            var2.addNotify();
         }

         if (this.peer == null) {
            this.peer = this.getToolkit().createWindow(this);
         }

         synchronized(allWindows) {
            allWindows.add(this);
         }

         super.addNotify();
      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         synchronized(allWindows) {
            allWindows.remove(this);
         }

         super.removeNotify();
      }
   }

   public void pack() {
      Container var1 = this.parent;
      if (var1 != null && var1.getPeer() == null) {
         var1.addNotify();
      }

      if (this.peer == null) {
         this.addNotify();
      }

      Dimension var2 = this.getPreferredSize();
      if (this.peer != null) {
         this.setClientSize(var2.width, var2.height);
      }

      if (this.beforeFirstShow) {
         this.isPacked = true;
      }

      this.validateUnconditionally();
   }

   public void setMinimumSize(Dimension var1) {
      synchronized(this.getTreeLock()) {
         super.setMinimumSize(var1);
         Dimension var3 = this.getSize();
         if (this.isMinimumSizeSet() && (var3.width < var1.width || var3.height < var1.height)) {
            int var4 = Math.max(this.width, var1.width);
            int var5 = Math.max(this.height, var1.height);
            this.setSize(var4, var5);
         }

         if (this.peer != null) {
            ((WindowPeer)this.peer).updateMinimumSize();
         }

      }
   }

   public void setSize(Dimension var1) {
      super.setSize(var1);
   }

   public void setSize(int var1, int var2) {
      super.setSize(var1, var2);
   }

   public void setLocation(int var1, int var2) {
      super.setLocation(var1, var2);
   }

   public void setLocation(Point var1) {
      super.setLocation(var1);
   }

   /** @deprecated */
   @Deprecated
   public void reshape(int var1, int var2, int var3, int var4) {
      if (this.isMinimumSizeSet()) {
         Dimension var5 = this.getMinimumSize();
         if (var3 < var5.width) {
            var3 = var5.width;
         }

         if (var4 < var5.height) {
            var4 = var5.height;
         }
      }

      super.reshape(var1, var2, var3, var4);
   }

   void setClientSize(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         this.setBoundsOp(4);
         this.setBounds(this.x, this.y, var1, var2);
      }
   }

   final void closeSplashScreen() {
      if (!this.isTrayIconWindow) {
         if (beforeFirstWindowShown.getAndSet(false)) {
            SunToolkit.closeSplashScreen();
            SplashScreen.markClosed();
         }

      }
   }

   public void setVisible(boolean var1) {
      super.setVisible(var1);
   }

   /** @deprecated */
   @Deprecated
   public void show() {
      if (this.peer == null) {
         this.addNotify();
      }

      this.validateUnconditionally();
      this.isInShow = true;
      if (this.visible) {
         this.toFront();
      } else {
         this.beforeFirstShow = false;
         this.closeSplashScreen();
         Dialog.checkShouldBeBlocked(this);
         super.show();
         this.locationByPlatform = false;

         for(int var1 = 0; var1 < this.ownedWindowList.size(); ++var1) {
            Window var2 = (Window)((WeakReference)this.ownedWindowList.elementAt(var1)).get();
            if (var2 != null && var2.showWithParent) {
               var2.show();
               var2.showWithParent = false;
            }
         }

         if (!this.isModalBlocked()) {
            this.updateChildrenBlocking();
         } else {
            this.modalBlocker.toFront_NoClientCode();
         }

         if (this instanceof Frame || this instanceof Dialog) {
            updateChildFocusableWindowState(this);
         }
      }

      this.isInShow = false;
      if ((this.state & 1) == 0) {
         this.postWindowEvent(200);
         this.state |= 1;
      }

   }

   static void updateChildFocusableWindowState(Window var0) {
      if (var0.getPeer() != null && var0.isShowing()) {
         ((WindowPeer)var0.getPeer()).updateFocusableWindowState();
      }

      for(int var1 = 0; var1 < var0.ownedWindowList.size(); ++var1) {
         Window var2 = (Window)((WeakReference)var0.ownedWindowList.elementAt(var1)).get();
         if (var2 != null) {
            updateChildFocusableWindowState(var2);
         }
      }

   }

   synchronized void postWindowEvent(int var1) {
      if (this.windowListener != null || (this.eventMask & 64L) != 0L || Toolkit.enabledOnToolkit(64L)) {
         WindowEvent var2 = new WindowEvent(this, var1);
         Toolkit.getEventQueue().postEvent(var2);
      }

   }

   /** @deprecated */
   @Deprecated
   public void hide() {
      synchronized(this.ownedWindowList) {
         for(int var2 = 0; var2 < this.ownedWindowList.size(); ++var2) {
            Window var3 = (Window)((WeakReference)this.ownedWindowList.elementAt(var2)).get();
            if (var3 != null && var3.visible) {
               var3.hide();
               var3.showWithParent = true;
            }
         }
      }

      if (this.isModalBlocked()) {
         this.modalBlocker.unblockWindow(this);
      }

      super.hide();
      this.locationByPlatform = false;
   }

   final void clearMostRecentFocusOwnerOnHide() {
   }

   public void dispose() {
      this.doDispose();
   }

   void disposeImpl() {
      this.dispose();
      if (this.getPeer() != null) {
         this.doDispose();
      }

   }

   void doDispose() {
      boolean var1 = this.isDisplayable();

      class DisposeAction implements Runnable {
         public void run() {
            Window.this.disposing = true;

            try {
               GraphicsDevice var1 = Window.this.getGraphicsConfiguration().getDevice();
               if (var1.getFullScreenWindow() == Window.this) {
                  var1.setFullScreenWindow((Window)null);
               }

               Object[] var2;
               synchronized(Window.this.ownedWindowList) {
                  var2 = new Object[Window.this.ownedWindowList.size()];
                  Window.this.ownedWindowList.copyInto(var2);
               }

               for(int var3 = 0; var3 < var2.length; ++var3) {
                  Window var4 = (Window)((Window)((WeakReference)((WeakReference)var2[var3])).get());
                  if (var4 != null) {
                     var4.disposeImpl();
                  }
               }

               Window.this.hide();
               Window.this.beforeFirstShow = true;
               Window.this.removeNotify();
               synchronized(Window.this.inputContextLock) {
                  if (Window.this.inputContext != null) {
                     Window.this.inputContext.dispose();
                     Window.this.inputContext = null;
                  }
               }

               Window.this.clearCurrentFocusCycleRootOnHide();
            } finally {
               Window.this.disposing = false;
            }

         }
      }

      DisposeAction var2 = new DisposeAction();
      if (EventQueue.isDispatchThread()) {
         var2.run();
      } else {
         try {
            EventQueue.invokeAndWait(this, var2);
         } catch (InterruptedException var4) {
            System.err.println("Disposal was interrupted:");
            var4.printStackTrace();
         } catch (InvocationTargetException var5) {
            System.err.println("Exception during disposal:");
            var5.printStackTrace();
         }
      }

      if (var1) {
         this.postWindowEvent(202);
      }

   }

   void adjustListeningChildrenOnParent(long var1, int var3) {
   }

   void adjustDecendantsOnParent(int var1) {
   }

   public void toFront() {
      this.toFront_NoClientCode();
   }

   final void toFront_NoClientCode() {
      if (this.visible) {
         WindowPeer var1 = (WindowPeer)this.peer;
         if (var1 != null) {
            var1.toFront();
         }

         if (this.isModalBlocked()) {
            this.modalBlocker.toFront_NoClientCode();
         }
      }

   }

   public void toBack() {
      this.toBack_NoClientCode();
   }

   final void toBack_NoClientCode() {
      if (this.isAlwaysOnTop()) {
         try {
            this.setAlwaysOnTop(false);
         } catch (SecurityException var2) {
         }
      }

      if (this.visible) {
         WindowPeer var1 = (WindowPeer)this.peer;
         if (var1 != null) {
            var1.toBack();
         }
      }

   }

   public Toolkit getToolkit() {
      return Toolkit.getDefaultToolkit();
   }

   public final String getWarningString() {
      return this.warningString;
   }

   private void setWarningString() {
      this.warningString = null;
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
         } catch (SecurityException var3) {
            this.warningString = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.appletWarning", "Java Applet Window")));
         }
      }

   }

   public Locale getLocale() {
      return this.locale == null ? Locale.getDefault() : this.locale;
   }

   public InputContext getInputContext() {
      synchronized(this.inputContextLock) {
         if (this.inputContext == null) {
            this.inputContext = InputContext.getInstance();
         }
      }

      return this.inputContext;
   }

   public void setCursor(Cursor var1) {
      if (var1 == null) {
         var1 = Cursor.getPredefinedCursor(0);
      }

      super.setCursor(var1);
   }

   public Window getOwner() {
      return this.getOwner_NoClientCode();
   }

   final Window getOwner_NoClientCode() {
      return (Window)this.parent;
   }

   public Window[] getOwnedWindows() {
      return this.getOwnedWindows_NoClientCode();
   }

   final Window[] getOwnedWindows_NoClientCode() {
      synchronized(this.ownedWindowList) {
         int var3 = this.ownedWindowList.size();
         int var4 = 0;
         Window[] var5 = new Window[var3];

         for(int var6 = 0; var6 < var3; ++var6) {
            var5[var4] = (Window)((WeakReference)this.ownedWindowList.elementAt(var6)).get();
            if (var5[var4] != null) {
               ++var4;
            }
         }

         Window[] var1;
         if (var3 != var4) {
            var1 = (Window[])Arrays.copyOf((Object[])var5, var4);
         } else {
            var1 = var5;
         }

         return var1;
      }
   }

   boolean isModalBlocked() {
      return this.modalBlocker != null;
   }

   void setModalBlocked(Dialog var1, boolean var2, boolean var3) {
      this.modalBlocker = var2 ? var1 : null;
      if (var3) {
         WindowPeer var4 = (WindowPeer)this.peer;
         if (var4 != null) {
            var4.setModalBlocked(var1, var2);
         }
      }

   }

   Dialog getModalBlocker() {
      return this.modalBlocker;
   }

   static IdentityArrayList<Window> getAllWindows() {
      synchronized(allWindows) {
         IdentityArrayList var1 = new IdentityArrayList();
         var1.addAll(allWindows);
         return var1;
      }
   }

   static IdentityArrayList<Window> getAllUnblockedWindows() {
      synchronized(allWindows) {
         IdentityArrayList var1 = new IdentityArrayList();

         for(int var2 = 0; var2 < allWindows.size(); ++var2) {
            Window var3 = (Window)allWindows.get(var2);
            if (!var3.isModalBlocked()) {
               var1.add(var3);
            }
         }

         return var1;
      }
   }

   private static Window[] getWindows(AppContext var0) {
      Class var1 = Window.class;
      synchronized(Window.class) {
         Vector var3 = (Vector)var0.get(Window.class);
         Window[] var2;
         if (var3 != null) {
            int var4 = var3.size();
            int var5 = 0;
            Window[] var6 = new Window[var4];

            for(int var7 = 0; var7 < var4; ++var7) {
               Window var8 = (Window)((WeakReference)var3.get(var7)).get();
               if (var8 != null) {
                  var6[var5++] = var8;
               }
            }

            if (var4 != var5) {
               var2 = (Window[])Arrays.copyOf((Object[])var6, var5);
            } else {
               var2 = var6;
            }
         } else {
            var2 = new Window[0];
         }

         return var2;
      }
   }

   public static Window[] getWindows() {
      return getWindows(AppContext.getAppContext());
   }

   public static Window[] getOwnerlessWindows() {
      Window[] var0 = getWindows();
      int var1 = 0;
      Window[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Window var5 = var2[var4];
         if (var5.getOwner() == null) {
            ++var1;
         }
      }

      var2 = new Window[var1];
      var3 = 0;
      Window[] var8 = var0;
      int var9 = var0.length;

      for(int var6 = 0; var6 < var9; ++var6) {
         Window var7 = var8[var6];
         if (var7.getOwner() == null) {
            var2[var3++] = var7;
         }
      }

      return var2;
   }

   Window getDocumentRoot() {
      synchronized(this.getTreeLock()) {
         Window var2;
         for(var2 = this; var2.getOwner() != null; var2 = var2.getOwner()) {
         }

         return var2;
      }
   }

   public void setModalExclusionType(Dialog.ModalExclusionType var1) {
      if (var1 == null) {
         var1 = Dialog.ModalExclusionType.NO_EXCLUDE;
      }

      if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(var1)) {
         var1 = Dialog.ModalExclusionType.NO_EXCLUDE;
      }

      if (this.modalExclusionType != var1) {
         if (var1 == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE) {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
            }
         }

         this.modalExclusionType = var1;
      }
   }

   public Dialog.ModalExclusionType getModalExclusionType() {
      return this.modalExclusionType;
   }

   boolean isModalExcluded(Dialog.ModalExclusionType var1) {
      if (this.modalExclusionType != null && this.modalExclusionType.compareTo(var1) >= 0) {
         return true;
      } else {
         Window var2 = this.getOwner_NoClientCode();
         return var2 != null && var2.isModalExcluded(var1);
      }
   }

   void updateChildrenBlocking() {
      Vector var1 = new Vector();
      Window[] var2 = this.getOwnedWindows();

      int var3;
      for(var3 = 0; var3 < var2.length; ++var3) {
         var1.add(var2[var3]);
      }

      for(var3 = 0; var3 < var1.size(); ++var3) {
         Window var4 = (Window)var1.get(var3);
         if (var4.isVisible()) {
            if (var4.isModalBlocked()) {
               Dialog var5 = var4.getModalBlocker();
               var5.unblockWindow(var4);
            }

            Dialog.checkShouldBeBlocked(var4);
            Window[] var7 = var4.getOwnedWindows();

            for(int var6 = 0; var6 < var7.length; ++var6) {
               var1.add(var7[var6]);
            }
         }
      }

   }

   public synchronized void addWindowListener(WindowListener var1) {
      if (var1 != null) {
         this.newEventsOnly = true;
         this.windowListener = AWTEventMulticaster.add(this.windowListener, var1);
      }
   }

   public synchronized void addWindowStateListener(WindowStateListener var1) {
      if (var1 != null) {
         this.windowStateListener = AWTEventMulticaster.add(this.windowStateListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void addWindowFocusListener(WindowFocusListener var1) {
      if (var1 != null) {
         this.windowFocusListener = AWTEventMulticaster.add(this.windowFocusListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeWindowListener(WindowListener var1) {
      if (var1 != null) {
         this.windowListener = AWTEventMulticaster.remove(this.windowListener, var1);
      }
   }

   public synchronized void removeWindowStateListener(WindowStateListener var1) {
      if (var1 != null) {
         this.windowStateListener = AWTEventMulticaster.remove(this.windowStateListener, var1);
      }
   }

   public synchronized void removeWindowFocusListener(WindowFocusListener var1) {
      if (var1 != null) {
         this.windowFocusListener = AWTEventMulticaster.remove(this.windowFocusListener, var1);
      }
   }

   public synchronized WindowListener[] getWindowListeners() {
      return (WindowListener[])this.getListeners(WindowListener.class);
   }

   public synchronized WindowFocusListener[] getWindowFocusListeners() {
      return (WindowFocusListener[])this.getListeners(WindowFocusListener.class);
   }

   public synchronized WindowStateListener[] getWindowStateListeners() {
      return (WindowStateListener[])this.getListeners(WindowStateListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      Object var2 = null;
      if (var1 == WindowFocusListener.class) {
         var2 = this.windowFocusListener;
      } else if (var1 == WindowStateListener.class) {
         var2 = this.windowStateListener;
      } else {
         if (var1 != WindowListener.class) {
            return super.getListeners(var1);
         }

         var2 = this.windowListener;
      }

      return AWTEventMulticaster.getListeners((EventListener)var2, var1);
   }

   boolean eventEnabled(AWTEvent var1) {
      switch(var1.id) {
      case 200:
      case 201:
      case 202:
      case 203:
      case 204:
      case 205:
      case 206:
         if ((this.eventMask & 64L) == 0L && this.windowListener == null) {
            return false;
         }

         return true;
      case 207:
      case 208:
         if ((this.eventMask & 524288L) == 0L && this.windowFocusListener == null) {
            return false;
         }

         return true;
      case 209:
         if ((this.eventMask & 262144L) == 0L && this.windowStateListener == null) {
            return false;
         }

         return true;
      default:
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof WindowEvent) {
         switch(var1.getID()) {
         case 200:
         case 201:
         case 202:
         case 203:
         case 204:
         case 205:
         case 206:
            this.processWindowEvent((WindowEvent)var1);
            break;
         case 207:
         case 208:
            this.processWindowFocusEvent((WindowEvent)var1);
            break;
         case 209:
            this.processWindowStateEvent((WindowEvent)var1);
         }

      } else {
         super.processEvent(var1);
      }
   }

   protected void processWindowEvent(WindowEvent var1) {
      WindowListener var2 = this.windowListener;
      if (var2 != null) {
         switch(var1.getID()) {
         case 200:
            var2.windowOpened(var1);
            break;
         case 201:
            var2.windowClosing(var1);
            break;
         case 202:
            var2.windowClosed(var1);
            break;
         case 203:
            var2.windowIconified(var1);
            break;
         case 204:
            var2.windowDeiconified(var1);
            break;
         case 205:
            var2.windowActivated(var1);
            break;
         case 206:
            var2.windowDeactivated(var1);
         }
      }

   }

   protected void processWindowFocusEvent(WindowEvent var1) {
      WindowFocusListener var2 = this.windowFocusListener;
      if (var2 != null) {
         switch(var1.getID()) {
         case 207:
            var2.windowGainedFocus(var1);
            break;
         case 208:
            var2.windowLostFocus(var1);
         }
      }

   }

   protected void processWindowStateEvent(WindowEvent var1) {
      WindowStateListener var2 = this.windowStateListener;
      if (var2 != null) {
         switch(var1.getID()) {
         case 209:
            var2.windowStateChanged(var1);
         }
      }

   }

   void preProcessKeyEvent(KeyEvent var1) {
      if (var1.isActionKey() && var1.getKeyCode() == 112 && var1.isControlDown() && var1.isShiftDown() && var1.getID() == 401) {
         this.list(System.out, 0);
      }

   }

   void postProcessKeyEvent(KeyEvent var1) {
   }

   public final void setAlwaysOnTop(boolean var1) throws SecurityException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
      }

      boolean var3;
      synchronized(this) {
         var3 = this.alwaysOnTop;
         this.alwaysOnTop = var1;
      }

      if (var3 != var1) {
         if (this.isAlwaysOnTopSupported()) {
            WindowPeer var4 = (WindowPeer)this.peer;
            synchronized(this.getTreeLock()) {
               if (var4 != null) {
                  var4.updateAlwaysOnTopState();
               }
            }
         }

         this.firePropertyChange("alwaysOnTop", var3, var1);
      }

      this.setOwnedWindowsAlwaysOnTop(var1);
   }

   private void setOwnedWindowsAlwaysOnTop(boolean var1) {
      WeakReference[] var2;
      synchronized(this.ownedWindowList) {
         var2 = new WeakReference[this.ownedWindowList.size()];
         this.ownedWindowList.copyInto(var2);
      }

      WeakReference[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WeakReference var6 = var3[var5];
         Window var7 = (Window)var6.get();
         if (var7 != null) {
            try {
               var7.setAlwaysOnTop(var1);
            } catch (SecurityException var9) {
            }
         }
      }

   }

   public boolean isAlwaysOnTopSupported() {
      return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported();
   }

   public final boolean isAlwaysOnTop() {
      return this.alwaysOnTop;
   }

   public Component getFocusOwner() {
      return this.isFocused() ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : null;
   }

   public Component getMostRecentFocusOwner() {
      if (this.isFocused()) {
         return this.getFocusOwner();
      } else {
         Component var1 = KeyboardFocusManager.getMostRecentFocusOwner(this);
         if (var1 != null) {
            return var1;
         } else {
            return this.isFocusableWindow() ? this.getFocusTraversalPolicy().getInitialComponent(this) : null;
         }
      }
   }

   public boolean isActive() {
      return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this;
   }

   public boolean isFocused() {
      return KeyboardFocusManager.getCurrentKeyboardFocusManager().getGlobalFocusedWindow() == this;
   }

   public Set<AWTKeyStroke> getFocusTraversalKeys(int var1) {
      if (var1 >= 0 && var1 < 4) {
         Set var2 = this.focusTraversalKeys != null ? this.focusTraversalKeys[var1] : null;
         return var2 != null ? var2 : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(var1);
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public final void setFocusCycleRoot(boolean var1) {
   }

   public final boolean isFocusCycleRoot() {
      return true;
   }

   public final Container getFocusCycleRootAncestor() {
      return null;
   }

   public final boolean isFocusableWindow() {
      if (!this.getFocusableWindowState()) {
         return false;
      } else if (!(this instanceof Frame) && !(this instanceof Dialog)) {
         if (this.getFocusTraversalPolicy().getDefaultComponent(this) == null) {
            return false;
         } else {
            for(Window var1 = this.getOwner(); var1 != null; var1 = var1.getOwner()) {
               if (var1 instanceof Frame || var1 instanceof Dialog) {
                  return var1.isShowing();
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public boolean getFocusableWindowState() {
      return this.focusableWindowState;
   }

   public void setFocusableWindowState(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.focusableWindowState;
         this.focusableWindowState = var1;
      }

      WindowPeer var3 = (WindowPeer)this.peer;
      if (var3 != null) {
         var3.updateFocusableWindowState();
      }

      this.firePropertyChange("focusableWindowState", var2, var1);
      if (var2 && !var1 && this.isFocused()) {
         for(Window var4 = this.getOwner(); var4 != null; var4 = var4.getOwner()) {
            Component var5 = KeyboardFocusManager.getMostRecentFocusOwner(var4);
            if (var5 != null && var5.requestFocus(false, CausedFocusEvent.Cause.ACTIVATION)) {
               return;
            }
         }

         KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
      }

   }

   public void setAutoRequestFocus(boolean var1) {
      this.autoRequestFocus = var1;
   }

   public boolean isAutoRequestFocus() {
      return this.autoRequestFocus;
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      super.addPropertyChangeListener(var1);
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      super.addPropertyChangeListener(var1, var2);
   }

   public boolean isValidateRoot() {
      return true;
   }

   void dispatchEventImpl(AWTEvent var1) {
      if (var1.getID() == 101) {
         this.invalidate();
         this.validate();
      }

      super.dispatchEventImpl(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean postEvent(Event var1) {
      if (this.handleEvent(var1)) {
         var1.consume();
         return true;
      } else {
         return false;
      }
   }

   public boolean isShowing() {
      return this.visible;
   }

   boolean isDisposing() {
      return this.disposing;
   }

   /** @deprecated */
   @Deprecated
   public void applyResourceBundle(ResourceBundle var1) {
      this.applyComponentOrientation(ComponentOrientation.getOrientation(var1));
   }

   /** @deprecated */
   @Deprecated
   public void applyResourceBundle(String var1) {
      this.applyResourceBundle(ResourceBundle.getBundle(var1, Locale.getDefault(), ClassLoader.getSystemClassLoader()));
   }

   void addOwnedWindow(WeakReference<Window> var1) {
      if (var1 != null) {
         synchronized(this.ownedWindowList) {
            if (!this.ownedWindowList.contains(var1)) {
               this.ownedWindowList.addElement(var1);
            }
         }
      }

   }

   void removeOwnedWindow(WeakReference<Window> var1) {
      if (var1 != null) {
         this.ownedWindowList.removeElement(var1);
      }

   }

   void connectOwnedWindow(Window var1) {
      var1.parent = this;
      this.addOwnedWindow(var1.weakThis);
      var1.disposerRecord.updateOwner();
   }

   private void addToWindowList() {
      Class var1 = Window.class;
      synchronized(Window.class) {
         Vector var2 = (Vector)this.appContext.get(Window.class);
         if (var2 == null) {
            var2 = new Vector();
            this.appContext.put(Window.class, var2);
         }

         var2.add(this.weakThis);
      }
   }

   private static void removeFromWindowList(AppContext var0, WeakReference<Window> var1) {
      Class var2 = Window.class;
      synchronized(Window.class) {
         Vector var3 = (Vector)var0.get(Window.class);
         if (var3 != null) {
            var3.remove(var1);
         }

      }
   }

   private void removeFromWindowList() {
      removeFromWindowList(this.appContext, this.weakThis);
   }

   public void setType(Window.Type var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("type should not be null.");
      } else {
         synchronized(this.getTreeLock()) {
            if (this.isDisplayable()) {
               throw new IllegalComponentStateException("The window is displayable.");
            } else {
               synchronized(this.getObjectLock()) {
                  this.type = var1;
               }

            }
         }
      }
   }

   public Window.Type getType() {
      synchronized(this.getObjectLock()) {
         return this.type;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      synchronized(this) {
         this.focusMgr = new FocusManager();
         this.focusMgr.focusRoot = this;
         this.focusMgr.focusOwner = this.getMostRecentFocusOwner();
         var1.defaultWriteObject();
         this.focusMgr = null;
         AWTEventMulticaster.save(var1, "windowL", this.windowListener);
         AWTEventMulticaster.save(var1, "windowFocusL", this.windowFocusListener);
         AWTEventMulticaster.save(var1, "windowStateL", this.windowStateListener);
      }

      var1.writeObject((Object)null);
      synchronized(this.ownedWindowList) {
         for(int var3 = 0; var3 < this.ownedWindowList.size(); ++var3) {
            Window var4 = (Window)((WeakReference)this.ownedWindowList.elementAt(var3)).get();
            if (var4 != null) {
               var1.writeObject("ownedL");
               var1.writeObject(var4);
            }
         }
      }

      var1.writeObject((Object)null);
      if (this.icons != null) {
         Iterator var2 = this.icons.iterator();

         while(var2.hasNext()) {
            Image var8 = (Image)var2.next();
            if (var8 instanceof Serializable) {
               var1.writeObject(var8);
            }
         }
      }

      var1.writeObject((Object)null);
   }

   private void initDeserializedWindow() {
      this.setWarningString();
      this.inputContextLock = new Object();
      this.visible = false;
      this.weakThis = new WeakReference(this);
      this.anchor = new Object();
      this.disposerRecord = new Window.WindowDisposerRecord(this.appContext, this);
      Disposer.addRecord(this.anchor, this.disposerRecord);
      this.addToWindowList();
      this.initGC((GraphicsConfiguration)null);
      this.ownedWindowList = new Vector();
   }

   private void deserializeResources(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      if (this.windowSerializedDataVersion < 2) {
         if (this.focusMgr != null && this.focusMgr.focusOwner != null) {
            KeyboardFocusManager.setMostRecentFocusOwner(this, this.focusMgr.focusOwner);
         }

         this.focusableWindowState = true;
      }

      Object var2;
      String var3;
      while(null != (var2 = var1.readObject())) {
         var3 = ((String)var2).intern();
         if ("windowL" == var3) {
            this.addWindowListener((WindowListener)((WindowListener)var1.readObject()));
         } else if ("windowFocusL" == var3) {
            this.addWindowFocusListener((WindowFocusListener)((WindowFocusListener)var1.readObject()));
         } else if ("windowStateL" == var3) {
            this.addWindowStateListener((WindowStateListener)((WindowStateListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

      try {
         while(null != (var2 = var1.readObject())) {
            var3 = ((String)var2).intern();
            if ("ownedL" == var3) {
               this.connectOwnedWindow((Window)var1.readObject());
            } else {
               var1.readObject();
            }
         }

         Object var5 = var1.readObject();

         for(this.icons = new ArrayList(); var5 != null; var5 = var1.readObject()) {
            if (var5 instanceof Image) {
               this.icons.add((Image)var5);
            }
         }
      } catch (OptionalDataException var4) {
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      this.initDeserializedWindow();
      ObjectInputStream.GetField var2 = var1.readFields();
      this.syncLWRequests = var2.get("syncLWRequests", systemSyncLWRequests);
      this.state = var2.get("state", (int)0);
      this.focusableWindowState = var2.get("focusableWindowState", true);
      this.windowSerializedDataVersion = var2.get("windowSerializedDataVersion", (int)1);
      this.locationByPlatform = var2.get("locationByPlatform", locationByPlatformProp);
      this.focusMgr = (FocusManager)var2.get("focusMgr", (Object)null);
      Dialog.ModalExclusionType var3 = (Dialog.ModalExclusionType)var2.get("modalExclusionType", Dialog.ModalExclusionType.NO_EXCLUDE);
      this.setModalExclusionType(var3);
      boolean var4 = var2.get("alwaysOnTop", false);
      if (var4) {
         this.setAlwaysOnTop(var4);
      }

      this.shape = (Shape)var2.get("shape", (Object)null);
      this.opacity = Float.valueOf(var2.get("opacity", 1.0F));
      this.securityWarningWidth = 0;
      this.securityWarningHeight = 0;
      this.securityWarningPointX = 2.0D;
      this.securityWarningPointY = 0.0D;
      this.securityWarningAlignmentX = 1.0F;
      this.securityWarningAlignmentY = 0.0F;
      this.deserializeResources(var1);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Window.AccessibleAWTWindow();
      }

      return this.accessibleContext;
   }

   void setGraphicsConfiguration(GraphicsConfiguration var1) {
      if (var1 == null) {
         var1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      }

      synchronized(this.getTreeLock()) {
         super.setGraphicsConfiguration(var1);
         if (log.isLoggable(PlatformLogger.Level.FINER)) {
            log.finer("+ Window.setGraphicsConfiguration(): new GC is \n+ " + this.getGraphicsConfiguration_NoClientCode() + "\n+ this is " + this);
         }

      }
   }

   public void setLocationRelativeTo(Component var1) {
      boolean var2 = false;
      boolean var3 = false;
      GraphicsConfiguration var4 = this.getGraphicsConfiguration_NoClientCode();
      Rectangle var5 = var4.getBounds();
      Dimension var6 = this.getSize();
      Window var7 = SunToolkit.getContainingWindow(var1);
      Point var9;
      int var10;
      int var11;
      if (var1 != null && var7 != null) {
         if (!var1.isShowing()) {
            var4 = var7.getGraphicsConfiguration();
            var5 = var4.getBounds();
            var10 = var5.x + (var5.width - var6.width) / 2;
            var11 = var5.y + (var5.height - var6.height) / 2;
         } else {
            var4 = var7.getGraphicsConfiguration();
            var5 = var4.getBounds();
            Dimension var12 = var1.getSize();
            var9 = var1.getLocationOnScreen();
            var10 = var9.x + (var12.width - var6.width) / 2;
            var11 = var9.y + (var12.height - var6.height) / 2;
            if (var11 + var6.height > var5.y + var5.height) {
               var11 = var5.y + var5.height - var6.height;
               if (var9.x - var5.x + var12.width / 2 < var5.width / 2) {
                  var10 = var9.x + var12.width;
               } else {
                  var10 = var9.x - var6.width;
               }
            }
         }
      } else {
         GraphicsEnvironment var8 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         var4 = var8.getDefaultScreenDevice().getDefaultConfiguration();
         var5 = var4.getBounds();
         var9 = var8.getCenterPoint();
         var10 = var9.x - var6.width / 2;
         var11 = var9.y - var6.height / 2;
      }

      if (var11 + var6.height > var5.y + var5.height) {
         var11 = var5.y + var5.height - var6.height;
      }

      if (var11 < var5.y) {
         var11 = var5.y;
      }

      if (var10 + var6.width > var5.x + var5.width) {
         var10 = var5.x + var5.width - var6.width;
      }

      if (var10 < var5.x) {
         var10 = var5.x;
      }

      this.setLocation(var10, var11);
   }

   void deliverMouseWheelToAncestor(MouseWheelEvent var1) {
   }

   boolean dispatchMouseWheelToAncestor(MouseWheelEvent var1) {
      return false;
   }

   public void createBufferStrategy(int var1) {
      super.createBufferStrategy(var1);
   }

   public void createBufferStrategy(int var1, BufferCapabilities var2) throws AWTException {
      super.createBufferStrategy(var1, var2);
   }

   public BufferStrategy getBufferStrategy() {
      return super.getBufferStrategy();
   }

   Component getTemporaryLostComponent() {
      return this.temporaryLostComponent;
   }

   Component setTemporaryLostComponent(Component var1) {
      Component var2 = this.temporaryLostComponent;
      if (var1 != null && !var1.canBeFocusOwner()) {
         this.temporaryLostComponent = null;
      } else {
         this.temporaryLostComponent = var1;
      }

      return var2;
   }

   boolean canContainFocusOwner(Component var1) {
      return super.canContainFocusOwner(var1) && this.isFocusableWindow();
   }

   public void setLocationByPlatform(boolean var1) {
      synchronized(this.getTreeLock()) {
         if (var1 && this.isShowing()) {
            throw new IllegalComponentStateException("The window is showing on screen.");
         } else {
            this.locationByPlatform = var1;
         }
      }
   }

   public boolean isLocationByPlatform() {
      return this.locationByPlatform;
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      synchronized(this.getTreeLock()) {
         if (this.getBoundsOp() == 1 || this.getBoundsOp() == 3) {
            this.locationByPlatform = false;
         }

         super.setBounds(var1, var2, var3, var4);
      }
   }

   public void setBounds(Rectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height);
   }

   boolean isRecursivelyVisible() {
      return this.visible;
   }

   public float getOpacity() {
      return this.opacity;
   }

   public void setOpacity(float var1) {
      synchronized(this.getTreeLock()) {
         if (var1 >= 0.0F && var1 <= 1.0F) {
            if (var1 < 1.0F) {
               GraphicsConfiguration var3 = this.getGraphicsConfiguration();
               GraphicsDevice var4 = var3.getDevice();
               if (var3.getDevice().getFullScreenWindow() == this) {
                  throw new IllegalComponentStateException("Setting opacity for full-screen window is not supported.");
               }

               if (!var4.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
                  throw new UnsupportedOperationException("TRANSLUCENT translucency is not supported.");
               }
            }

            this.opacity = var1;
            WindowPeer var7 = (WindowPeer)this.getPeer();
            if (var7 != null) {
               var7.setOpacity(var1);
            }

         } else {
            throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
         }
      }
   }

   public Shape getShape() {
      synchronized(this.getTreeLock()) {
         return this.shape == null ? null : new Path2D.Float(this.shape);
      }
   }

   public void setShape(Shape var1) {
      synchronized(this.getTreeLock()) {
         if (var1 != null) {
            GraphicsConfiguration var3 = this.getGraphicsConfiguration();
            GraphicsDevice var4 = var3.getDevice();
            if (var3.getDevice().getFullScreenWindow() == this) {
               throw new IllegalComponentStateException("Setting shape for full-screen window is not supported.");
            }

            if (!var4.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
               throw new UnsupportedOperationException("PERPIXEL_TRANSPARENT translucency is not supported.");
            }
         }

         this.shape = var1 == null ? null : new Path2D.Float(var1);
         WindowPeer var7 = (WindowPeer)this.getPeer();
         if (var7 != null) {
            var7.applyShape(var1 == null ? null : Region.getInstance(var1, (AffineTransform)null));
         }

      }
   }

   public Color getBackground() {
      return super.getBackground();
   }

   public void setBackground(Color var1) {
      Color var2 = this.getBackground();
      super.setBackground(var1);
      if (var2 == null || !var2.equals(var1)) {
         int var3 = var2 != null ? var2.getAlpha() : 255;
         int var4 = var1 != null ? var1.getAlpha() : 255;
         if (var3 == 255 && var4 < 255) {
            GraphicsConfiguration var5 = this.getGraphicsConfiguration();
            GraphicsDevice var6 = var5.getDevice();
            if (var5.getDevice().getFullScreenWindow() == this) {
               throw new IllegalComponentStateException("Making full-screen window non opaque is not supported.");
            }

            if (!var5.isTranslucencyCapable()) {
               GraphicsConfiguration var7 = var6.getTranslucencyCapableGC();
               if (var7 == null) {
                  throw new UnsupportedOperationException("PERPIXEL_TRANSLUCENT translucency is not supported");
               }

               this.setGraphicsConfiguration(var7);
            }

            setLayersOpaque(this, false);
         } else if (var3 < 255 && var4 == 255) {
            setLayersOpaque(this, true);
         }

         WindowPeer var8 = (WindowPeer)this.getPeer();
         if (var8 != null) {
            var8.setOpaque(var4 == 255);
         }

      }
   }

   public boolean isOpaque() {
      Color var1 = this.getBackground();
      return var1 != null ? var1.getAlpha() == 255 : true;
   }

   private void updateWindow() {
      synchronized(this.getTreeLock()) {
         WindowPeer var2 = (WindowPeer)this.getPeer();
         if (var2 != null) {
            var2.updateWindow();
         }

      }
   }

   public void paint(Graphics var1) {
      if (!this.isOpaque()) {
         Graphics var2 = var1.create();

         try {
            if (var2 instanceof Graphics2D) {
               var2.setColor(this.getBackground());
               ((Graphics2D)var2).setComposite(AlphaComposite.getInstance(2));
               var2.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
         } finally {
            var2.dispose();
         }
      }

      super.paint(var1);
   }

   private static void setLayersOpaque(Component var0, boolean var1) {
      if (SunToolkit.isInstanceOf((Object)var0, "javax.swing.RootPaneContainer")) {
         RootPaneContainer var2 = (RootPaneContainer)var0;
         JRootPane var3 = var2.getRootPane();
         JLayeredPane var4 = var3.getLayeredPane();
         Container var5 = var3.getContentPane();
         JComponent var6 = var5 instanceof JComponent ? (JComponent)var5 : null;
         var4.setOpaque(var1);
         var3.setOpaque(var1);
         if (var6 != null) {
            var6.setOpaque(var1);
            int var7 = var6.getComponentCount();
            if (var7 > 0) {
               Component var8 = var6.getComponent(0);
               if (var8 instanceof RootPaneContainer) {
                  setLayersOpaque(var8, var1);
               }
            }
         }
      }

   }

   final Container getContainer() {
      return null;
   }

   final void applyCompoundShape(Region var1) {
   }

   final void applyCurrentShape() {
   }

   final void mixOnReshaping() {
   }

   final Point getLocationOnWindow() {
      return new Point(0, 0);
   }

   private static double limit(double var0, double var2, double var4) {
      var0 = Math.max(var0, var2);
      var0 = Math.min(var0, var4);
      return var0;
   }

   private Point2D calculateSecurityWarningPosition(double var1, double var3, double var5, double var7) {
      double var9 = var1 + var5 * (double)this.securityWarningAlignmentX + this.securityWarningPointX;
      double var11 = var3 + var7 * (double)this.securityWarningAlignmentY + this.securityWarningPointY;
      var9 = limit(var9, var1 - (double)this.securityWarningWidth - 2.0D, var1 + var5 + 2.0D);
      var11 = limit(var11, var3 - (double)this.securityWarningHeight - 2.0D, var3 + var7 + 2.0D);
      GraphicsConfiguration var13 = this.getGraphicsConfiguration_NoClientCode();
      Rectangle var14 = var13.getBounds();
      Insets var15 = Toolkit.getDefaultToolkit().getScreenInsets(var13);
      var9 = limit(var9, (double)(var14.x + var15.left), (double)(var14.x + var14.width - var15.right - this.securityWarningWidth));
      var11 = limit(var11, (double)(var14.y + var15.top), (double)(var14.y + var14.height - var15.bottom - this.securityWarningHeight));
      return new Point2D.Double(var9, var11);
   }

   void updateZOrder() {
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.awt.syncLWRequests")));
      systemSyncLWRequests = var0 != null && var0.equals("true");
      var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.awt.Window.locationByPlatform")));
      locationByPlatformProp = var0 != null && var0.equals("true");
      beforeFirstWindowShown = new AtomicBoolean(true);
      AWTAccessor.setWindowAccessor(new AWTAccessor.WindowAccessor() {
         public float getOpacity(Window var1) {
            return var1.opacity;
         }

         public void setOpacity(Window var1, float var2) {
            var1.setOpacity(var2);
         }

         public Shape getShape(Window var1) {
            return var1.getShape();
         }

         public void setShape(Window var1, Shape var2) {
            var1.setShape(var2);
         }

         public void setOpaque(Window var1, boolean var2) {
            Color var3 = var1.getBackground();
            if (var3 == null) {
               var3 = new Color(0, 0, 0, 0);
            }

            var1.setBackground(new Color(var3.getRed(), var3.getGreen(), var3.getBlue(), var2 ? 255 : 0));
         }

         public void updateWindow(Window var1) {
            var1.updateWindow();
         }

         public Dimension getSecurityWarningSize(Window var1) {
            return new Dimension(var1.securityWarningWidth, var1.securityWarningHeight);
         }

         public void setSecurityWarningSize(Window var1, int var2, int var3) {
            var1.securityWarningWidth = var2;
            var1.securityWarningHeight = var3;
         }

         public void setSecurityWarningPosition(Window var1, Point2D var2, float var3, float var4) {
            var1.securityWarningPointX = var2.getX();
            var1.securityWarningPointY = var2.getY();
            var1.securityWarningAlignmentX = var3;
            var1.securityWarningAlignmentY = var4;
            synchronized(var1.getTreeLock()) {
               WindowPeer var6 = (WindowPeer)var1.getPeer();
               if (var6 != null) {
                  var6.repositionSecurityWarning();
               }

            }
         }

         public Point2D calculateSecurityWarningPosition(Window var1, double var2, double var4, double var6, double var8) {
            return var1.calculateSecurityWarningPosition(var2, var4, var6, var8);
         }

         public void setLWRequestStatus(Window var1, boolean var2) {
            var1.syncLWRequests = var2;
         }

         public boolean isAutoRequestFocus(Window var1) {
            return var1.autoRequestFocus;
         }

         public boolean isTrayIconWindow(Window var1) {
            return var1.isTrayIconWindow;
         }

         public void setTrayIconWindow(Window var1, boolean var2) {
            var1.isTrayIconWindow = var2;
         }

         public Window[] getOwnedWindows(Window var1) {
            return var1.getOwnedWindows_NoClientCode();
         }
      });
   }

   protected class AccessibleAWTWindow extends Container.AccessibleAWTContainer {
      private static final long serialVersionUID = 4215068635060671780L;

      protected AccessibleAWTWindow() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.WINDOW;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (Window.this.getFocusOwner() != null) {
            var1.add(AccessibleState.ACTIVE);
         }

         return var1;
      }
   }

   static class WindowDisposerRecord implements DisposerRecord {
      WeakReference<Window> owner;
      final WeakReference<Window> weakThis;
      final WeakReference<AppContext> context;

      WindowDisposerRecord(AppContext var1, Window var2) {
         this.weakThis = var2.weakThis;
         this.context = new WeakReference(var1);
      }

      public void updateOwner() {
         Window var1 = (Window)this.weakThis.get();
         this.owner = var1 == null ? null : new WeakReference(var1.getOwner());
      }

      public void dispose() {
         if (this.owner != null) {
            Window var1 = (Window)this.owner.get();
            if (var1 != null) {
               var1.removeOwnedWindow(this.weakThis);
            }
         }

         AppContext var2 = (AppContext)this.context.get();
         if (null != var2) {
            Window.removeFromWindowList(var2, this.weakThis);
         }

      }
   }

   public static enum Type {
      NORMAL,
      UTILITY,
      POPUP;
   }
}
