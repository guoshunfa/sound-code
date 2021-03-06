package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.im.InputContext;
import sun.awt.im.SimpleInputMethodWindow;
import sun.awt.image.ByteArrayImageSource;
import sun.awt.image.FileImageSource;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.ToolkitImage;
import sun.awt.image.URLImageSource;
import sun.font.FontDesignMetrics;
import sun.misc.SoftCache;
import sun.net.util.URLUtil;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public abstract class SunToolkit extends Toolkit implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport, KeyboardFocusManagerPeerProvider {
   public static final int GRAB_EVENT_MASK = Integer.MIN_VALUE;
   private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
   protected static int numberOfButtons;
   public static final int MAX_BUTTONS_SUPPORTED = 20;
   private static final ReentrantLock AWT_LOCK;
   private static final Condition AWT_LOCK_COND;
   private static final Map<Object, AppContext> appContextMap;
   static final SoftCache fileImgCache;
   static final SoftCache urlImgCache;
   private static Locale startupLocale;
   private transient WindowClosingListener windowClosingListener = null;
   private static DefaultMouseInfoPeer mPeer;
   private static Dialog.ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE;
   private SunToolkit.ModalityListenerList modalityListeners = new SunToolkit.ModalityListenerList();
   public static final int DEFAULT_WAIT_TIME = 10000;
   private static final int MAX_ITERS = 20;
   private static final int MIN_ITERS = 0;
   private static final int MINIMAL_EDELAY = 0;
   private boolean eventDispatched = false;
   private boolean queueEmpty = false;
   private final Object waitLock = "Wait Lock";
   private static boolean touchKeyboardAutoShowIsEnabled;
   private static boolean checkedSystemAAFontSettings;
   private static boolean useSystemAAFontSettings;
   private static boolean lastExtraCondition;
   private static RenderingHints desktopFontHints;
   public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
   private static Boolean sunAwtDisableMixing;
   private static final Object DEACTIVATION_TIMES_MAP_KEY;

   private static void initEQ(AppContext var0) {
      String var2 = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");

      EventQueue var1;
      try {
         var1 = (EventQueue)Class.forName(var2).newInstance();
      } catch (Exception var4) {
         var4.printStackTrace();
         System.err.println("Failed loading " + var2 + ": " + var4);
         var1 = new EventQueue();
      }

      var0.put(AppContext.EVENT_QUEUE_KEY, var1);
      PostEventQueue var3 = new PostEventQueue(var1);
      var0.put("PostEventQueue", var3);
   }

   public boolean useBufferPerWindow() {
      return false;
   }

   public abstract WindowPeer createWindow(Window var1) throws HeadlessException;

   public abstract FramePeer createFrame(Frame var1) throws HeadlessException;

   public abstract FramePeer createLightweightFrame(LightweightFrame var1) throws HeadlessException;

   public abstract DialogPeer createDialog(Dialog var1) throws HeadlessException;

   public abstract ButtonPeer createButton(Button var1) throws HeadlessException;

   public abstract TextFieldPeer createTextField(TextField var1) throws HeadlessException;

   public abstract ChoicePeer createChoice(Choice var1) throws HeadlessException;

   public abstract LabelPeer createLabel(Label var1) throws HeadlessException;

   public abstract ListPeer createList(List var1) throws HeadlessException;

   public abstract CheckboxPeer createCheckbox(Checkbox var1) throws HeadlessException;

   public abstract ScrollbarPeer createScrollbar(Scrollbar var1) throws HeadlessException;

   public abstract ScrollPanePeer createScrollPane(ScrollPane var1) throws HeadlessException;

   public abstract TextAreaPeer createTextArea(TextArea var1) throws HeadlessException;

   public abstract FileDialogPeer createFileDialog(FileDialog var1) throws HeadlessException;

   public abstract MenuBarPeer createMenuBar(MenuBar var1) throws HeadlessException;

   public abstract MenuPeer createMenu(Menu var1) throws HeadlessException;

   public abstract PopupMenuPeer createPopupMenu(PopupMenu var1) throws HeadlessException;

   public abstract MenuItemPeer createMenuItem(MenuItem var1) throws HeadlessException;

   public abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) throws HeadlessException;

   public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException;

   public abstract TrayIconPeer createTrayIcon(TrayIcon var1) throws HeadlessException, AWTException;

   public abstract SystemTrayPeer createSystemTray(SystemTray var1);

   public abstract boolean isTraySupported();

   public abstract FontPeer getFontPeer(String var1, int var2);

   public abstract RobotPeer createRobot(Robot var1, GraphicsDevice var2) throws AWTException;

   public abstract KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException;

   public static final void awtLock() {
      AWT_LOCK.lock();
   }

   public static final boolean awtTryLock() {
      return AWT_LOCK.tryLock();
   }

   public static final void awtUnlock() {
      AWT_LOCK.unlock();
   }

   public static final void awtLockWait() throws InterruptedException {
      AWT_LOCK_COND.await();
   }

   public static final void awtLockWait(long var0) throws InterruptedException {
      AWT_LOCK_COND.await(var0, TimeUnit.MILLISECONDS);
   }

   public static final void awtLockNotify() {
      AWT_LOCK_COND.signal();
   }

   public static final void awtLockNotifyAll() {
      AWT_LOCK_COND.signalAll();
   }

   public static final boolean isAWTLockHeldByCurrentThread() {
      return AWT_LOCK.isHeldByCurrentThread();
   }

   public static AppContext createNewAppContext() {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();
      return createNewAppContext(var0);
   }

   static final AppContext createNewAppContext(ThreadGroup var0) {
      AppContext var1 = new AppContext(var0);
      initEQ(var1);
      return var1;
   }

   static void wakeupEventQueue(EventQueue var0, boolean var1) {
      AWTAccessor.getEventQueueAccessor().wakeup(var0, var1);
   }

   protected static Object targetToPeer(Object var0) {
      return var0 != null && !GraphicsEnvironment.isHeadless() ? AWTAutoShutdown.getInstance().getPeer(var0) : null;
   }

   protected static void targetCreatedPeer(Object var0, Object var1) {
      if (var0 != null && var1 != null && !GraphicsEnvironment.isHeadless()) {
         AWTAutoShutdown.getInstance().registerPeer(var0, var1);
      }

   }

   protected static void targetDisposedPeer(Object var0, Object var1) {
      if (var0 != null && var1 != null && !GraphicsEnvironment.isHeadless()) {
         AWTAutoShutdown.getInstance().unregisterPeer(var0, var1);
      }

   }

   private static boolean setAppContext(Object var0, AppContext var1) {
      if (var0 instanceof Component) {
         AWTAccessor.getComponentAccessor().setAppContext((Component)var0, var1);
      } else {
         if (!(var0 instanceof MenuComponent)) {
            return false;
         }

         AWTAccessor.getMenuComponentAccessor().setAppContext((MenuComponent)var0, var1);
      }

      return true;
   }

   private static AppContext getAppContext(Object var0) {
      if (var0 instanceof Component) {
         return AWTAccessor.getComponentAccessor().getAppContext((Component)var0);
      } else {
         return var0 instanceof MenuComponent ? AWTAccessor.getMenuComponentAccessor().getAppContext((MenuComponent)var0) : null;
      }
   }

   public static AppContext targetToAppContext(Object var0) {
      if (var0 == null) {
         return null;
      } else {
         AppContext var1 = getAppContext(var0);
         if (var1 == null) {
            var1 = (AppContext)appContextMap.get(var0);
         }

         return var1;
      }
   }

   public static void setLWRequestStatus(Window var0, boolean var1) {
      AWTAccessor.getWindowAccessor().setLWRequestStatus(var0, var1);
   }

   public static void checkAndSetPolicy(Container var0) {
      FocusTraversalPolicy var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
      var0.setFocusTraversalPolicy(var1);
   }

   private static FocusTraversalPolicy createLayoutPolicy() {
      FocusTraversalPolicy var0 = null;

      try {
         Class var1 = Class.forName("javax.swing.LayoutFocusTraversalPolicy");
         var0 = (FocusTraversalPolicy)var1.newInstance();
      } catch (ClassNotFoundException var2) {
         assert false;
      } catch (InstantiationException var3) {
         assert false;
      } catch (IllegalAccessException var4) {
         assert false;
      }

      return var0;
   }

   public static void insertTargetMapping(Object var0, AppContext var1) {
      if (!setAppContext(var0, var1)) {
         appContextMap.put(var0, var1);
      }

   }

   public static void postEvent(AppContext var0, AWTEvent var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         AWTAccessor.SequencedEventAccessor var2 = AWTAccessor.getSequencedEventAccessor();
         if (var2 != null && var2.isSequencedEvent(var1)) {
            AWTEvent var3 = var2.getNested(var1);
            if (var3.getID() == 208 && var3 instanceof TimedWindowEvent) {
               TimedWindowEvent var4 = (TimedWindowEvent)var3;
               ((SunToolkit)Toolkit.getDefaultToolkit()).setWindowDeactivationTime((Window)var4.getSource(), var4.getWhen());
            }
         }

         setSystemGenerated(var1);
         AppContext var5 = targetToAppContext(var1.getSource());
         if (var5 != null && !var5.equals(var0)) {
            throw new RuntimeException("Event posted on wrong app context : " + var1);
         } else {
            PostEventQueue var6 = (PostEventQueue)var0.get("PostEventQueue");
            if (var6 != null) {
               var6.postEvent(var1);
            }

         }
      }
   }

   public static void postPriorityEvent(final AWTEvent var0) {
      PeerEvent var1 = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
         public void run() {
            AWTAccessor.getAWTEventAccessor().setPosted(var0);
            ((Component)var0.getSource()).dispatchEvent(var0);
         }
      }, 2L);
      postEvent(targetToAppContext(var0.getSource()), var1);
   }

   public static void flushPendingEvents() {
      AppContext var0 = AppContext.getAppContext();
      flushPendingEvents(var0);
   }

   public static void flushPendingEvents(AppContext var0) {
      PostEventQueue var1 = (PostEventQueue)var0.get("PostEventQueue");
      if (var1 != null) {
         var1.flush();
      }

   }

   public static void executeOnEventHandlerThread(Object var0, Runnable var1) {
      executeOnEventHandlerThread(new PeerEvent(var0, var1, 1L));
   }

   public static void executeOnEventHandlerThread(Object var0, Runnable var1, final long var2) {
      executeOnEventHandlerThread(new PeerEvent(var0, var1, 1L) {
         public long getWhen() {
            return var2;
         }
      });
   }

   public static void executeOnEventHandlerThread(PeerEvent var0) {
      postEvent(targetToAppContext(var0.getSource()), var0);
   }

   public static void invokeLaterOnAppContext(AppContext var0, Runnable var1) {
      postEvent(var0, new PeerEvent(Toolkit.getDefaultToolkit(), var1, 1L));
   }

   public static void executeOnEDTAndWait(Object var0, Runnable var1) throws InterruptedException, InvocationTargetException {
      if (EventQueue.isDispatchThread()) {
         throw new Error("Cannot call executeOnEDTAndWait from any event dispatcher thread");
      } else {
         class AWTInvocationLock {
         }

         AWTInvocationLock var2 = new AWTInvocationLock();
         PeerEvent var3 = new PeerEvent(var0, var1, var2, true, 1L);
         synchronized(var2) {
            executeOnEventHandlerThread(var3);

            while(true) {
               if (var3.isDispatched()) {
                  break;
               }

               var2.wait();
            }
         }

         Throwable var4 = var3.getThrowable();
         if (var4 != null) {
            throw new InvocationTargetException(var4);
         }
      }
   }

   public static boolean isDispatchThreadForAppContext(Object var0) {
      AppContext var1 = targetToAppContext(var0);
      EventQueue var2 = (EventQueue)var1.get(AppContext.EVENT_QUEUE_KEY);
      AWTAccessor.EventQueueAccessor var3 = AWTAccessor.getEventQueueAccessor();
      return var3.isDispatchThreadImpl(var2);
   }

   public Dimension getScreenSize() {
      return new Dimension(this.getScreenWidth(), this.getScreenHeight());
   }

   protected abstract int getScreenWidth();

   protected abstract int getScreenHeight();

   public FontMetrics getFontMetrics(Font var1) {
      return FontDesignMetrics.getMetrics(var1);
   }

   public String[] getFontList() {
      String[] var1 = new String[]{"Dialog", "SansSerif", "Serif", "Monospaced", "DialogInput"};
      return var1;
   }

   public PanelPeer createPanel(Panel var1) {
      return (PanelPeer)this.createComponent(var1);
   }

   public CanvasPeer createCanvas(Canvas var1) {
      return (CanvasPeer)this.createComponent(var1);
   }

   public void disableBackgroundErase(Canvas var1) {
      this.disableBackgroundEraseImpl(var1);
   }

   public void disableBackgroundErase(Component var1) {
      this.disableBackgroundEraseImpl(var1);
   }

   private void disableBackgroundEraseImpl(Component var1) {
      AWTAccessor.getComponentAccessor().setBackgroundEraseDisabled(var1, true);
   }

   public static boolean getSunAwtNoerasebackground() {
      return (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.noerasebackground")));
   }

   public static boolean getSunAwtErasebackgroundonresize() {
      return (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.erasebackgroundonresize")));
   }

   static Image getImageFromHash(Toolkit var0, URL var1) {
      checkPermissions(var1);
      synchronized(urlImgCache) {
         String var3 = var1.toString();
         Image var4 = (Image)urlImgCache.get(var3);
         if (var4 == null) {
            try {
               var4 = var0.createImage((ImageProducer)(new URLImageSource(var1)));
               urlImgCache.put(var3, var4);
            } catch (Exception var7) {
            }
         }

         return var4;
      }
   }

   static Image getImageFromHash(Toolkit var0, String var1) {
      checkPermissions(var1);
      synchronized(fileImgCache) {
         Image var3 = (Image)fileImgCache.get(var1);
         if (var3 == null) {
            try {
               var3 = var0.createImage((ImageProducer)(new FileImageSource(var1)));
               fileImgCache.put(var1, var3);
            } catch (Exception var6) {
            }
         }

         return var3;
      }
   }

   public Image getImage(String var1) {
      return getImageFromHash(this, (String)var1);
   }

   public Image getImage(URL var1) {
      return getImageFromHash(this, (URL)var1);
   }

   protected Image getImageWithResolutionVariant(String var1, String var2) {
      synchronized(fileImgCache) {
         Image var4 = getImageFromHash(this, (String)var1);
         if (var4 instanceof MultiResolutionImage) {
            return var4;
         } else {
            Image var5 = getImageFromHash(this, (String)var2);
            var4 = createImageWithResolutionVariant(var4, var5);
            fileImgCache.put(var1, var4);
            return var4;
         }
      }
   }

   protected Image getImageWithResolutionVariant(URL var1, URL var2) {
      synchronized(urlImgCache) {
         Image var4 = getImageFromHash(this, (URL)var1);
         if (var4 instanceof MultiResolutionImage) {
            return var4;
         } else {
            Image var5 = getImageFromHash(this, (URL)var2);
            var4 = createImageWithResolutionVariant(var4, var5);
            String var6 = var1.toString();
            urlImgCache.put(var6, var4);
            return var4;
         }
      }
   }

   public Image createImage(String var1) {
      checkPermissions(var1);
      return this.createImage((ImageProducer)(new FileImageSource(var1)));
   }

   public Image createImage(URL var1) {
      checkPermissions(var1);
      return this.createImage((ImageProducer)(new URLImageSource(var1)));
   }

   public Image createImage(byte[] var1, int var2, int var3) {
      return this.createImage((ImageProducer)(new ByteArrayImageSource(var1, var2, var3)));
   }

   public Image createImage(ImageProducer var1) {
      return new ToolkitImage(var1);
   }

   public static Image createImageWithResolutionVariant(Image var0, Image var1) {
      return new MultiResolutionToolkitImage(var0, var1);
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      if (!(var1 instanceof ToolkitImage)) {
         return 32;
      } else {
         ToolkitImage var5 = (ToolkitImage)var1;
         int var6;
         if (var2 != 0 && var3 != 0) {
            var6 = var5.getImageRep().check(var4);
         } else {
            var6 = 32;
         }

         return (var5.check(var4) | var6) & this.checkResolutionVariant(var1, var2, var3, var4);
      }
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      if (var2 != 0 && var3 != 0) {
         if (!(var1 instanceof ToolkitImage)) {
            return true;
         } else {
            ToolkitImage var5 = (ToolkitImage)var1;
            if (var5.hasError()) {
               if (var4 != null) {
                  var4.imageUpdate(var1, 192, -1, -1, -1, -1);
               }

               return false;
            } else {
               ImageRepresentation var6 = var5.getImageRep();
               return var6.prepare(var4) & this.prepareResolutionVariant(var1, var2, var3, var4);
            }
         }
      } else {
         return true;
      }
   }

   private int checkResolutionVariant(Image var1, int var2, int var3, ImageObserver var4) {
      ToolkitImage var5 = getResolutionVariant(var1);
      int var6 = getRVSize(var2);
      int var7 = getRVSize(var3);
      return var5 != null && !var5.hasError() ? this.checkImage(var5, var6, var7, MultiResolutionToolkitImage.getResolutionVariantObserver(var1, var4, var2, var3, var6, var7, true)) : '\uffff';
   }

   private boolean prepareResolutionVariant(Image var1, int var2, int var3, ImageObserver var4) {
      ToolkitImage var5 = getResolutionVariant(var1);
      int var6 = getRVSize(var2);
      int var7 = getRVSize(var3);
      return var5 == null || var5.hasError() || this.prepareImage(var5, var6, var7, MultiResolutionToolkitImage.getResolutionVariantObserver(var1, var4, var2, var3, var6, var7, true));
   }

   private static int getRVSize(int var0) {
      return var0 == -1 ? -1 : 2 * var0;
   }

   private static ToolkitImage getResolutionVariant(Image var0) {
      if (var0 instanceof MultiResolutionToolkitImage) {
         Image var1 = ((MultiResolutionToolkitImage)var0).getResolutionVariant();
         if (var1 instanceof ToolkitImage) {
            return (ToolkitImage)var1;
         }
      }

      return null;
   }

   protected static boolean imageCached(String var0) {
      return fileImgCache.containsKey(var0);
   }

   protected static boolean imageCached(URL var0) {
      String var1 = var0.toString();
      return urlImgCache.containsKey(var1);
   }

   protected static boolean imageExists(String var0) {
      if (var0 != null) {
         checkPermissions(var0);
         return (new File(var0)).exists();
      } else {
         return false;
      }
   }

   protected static boolean imageExists(URL var0) {
      if (var0 != null) {
         checkPermissions(var0);

         try {
            InputStream var1 = var0.openStream();
            Throwable var2 = null;

            boolean var3;
            try {
               var3 = true;
            } catch (Throwable var13) {
               var2 = var13;
               throw var13;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var12) {
                        var2.addSuppressed(var12);
                     }
                  } else {
                     var1.close();
                  }
               }

            }

            return var3;
         } catch (IOException var15) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void checkPermissions(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(var0);
      }

   }

   private static void checkPermissions(URL var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            Permission var2 = URLUtil.getConnectPermission(var0);
            if (var2 != null) {
               try {
                  var1.checkPermission(var2);
               } catch (SecurityException var4) {
                  if (var2 instanceof FilePermission && var2.getActions().indexOf("read") != -1) {
                     var1.checkRead(var2.getName());
                  } else {
                     if (!(var2 instanceof SocketPermission) || var2.getActions().indexOf("connect") == -1) {
                        throw var4;
                     }

                     var1.checkConnect(var0.getHost(), var0.getPort());
                  }
               }
            }
         } catch (IOException var5) {
            var1.checkConnect(var0.getHost(), var0.getPort());
         }
      }

   }

   public static BufferedImage getScaledIconImage(java.util.List<Image> var0, int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         Image var3 = null;
         int var4 = 0;
         int var5 = 0;
         double var6 = 3.0D;
         double var8 = 0.0D;
         Iterator var10 = var0.iterator();

         int var13;
         int var29;
         while(var10.hasNext()) {
            Image var11 = (Image)var10.next();
            if (var11 != null) {
               if (var11 instanceof ToolkitImage) {
                  ImageRepresentation var12 = ((ToolkitImage)var11).getImageRep();
                  var12.reconstruct(32);
               }

               try {
                  var29 = var11.getWidth((ImageObserver)null);
                  var13 = var11.getHeight((ImageObserver)null);
               } catch (Exception var26) {
                  continue;
               }

               if (var29 > 0 && var13 > 0) {
                  double var14 = Math.min((double)var1 / (double)var29, (double)var2 / (double)var13);
                  boolean var16 = false;
                  boolean var17 = false;
                  double var18 = 1.0D;
                  double var20;
                  int var30;
                  int var31;
                  if (var14 >= 2.0D) {
                     var14 = Math.floor(var14);
                     var30 = var29 * (int)var14;
                     var31 = var13 * (int)var14;
                     var18 = 1.0D - 0.5D / var14;
                  } else if (var14 >= 1.0D) {
                     var14 = 1.0D;
                     var30 = var29;
                     var31 = var13;
                     var18 = 0.0D;
                  } else if (var14 >= 0.75D) {
                     var14 = 0.75D;
                     var30 = var29 * 3 / 4;
                     var31 = var13 * 3 / 4;
                     var18 = 0.3D;
                  } else if (var14 >= 0.6666D) {
                     var14 = 0.6666D;
                     var30 = var29 * 2 / 3;
                     var31 = var13 * 2 / 3;
                     var18 = 0.33D;
                  } else {
                     var20 = Math.ceil(1.0D / var14);
                     var14 = 1.0D / var20;
                     var30 = (int)Math.round((double)var29 / var20);
                     var31 = (int)Math.round((double)var13 / var20);
                     var18 = 1.0D - 1.0D / var20;
                  }

                  var20 = ((double)var1 - (double)var30) / (double)var1 + ((double)var2 - (double)var31) / (double)var2 + var18;
                  if (var20 < var6) {
                     var6 = var20;
                     var3 = var11;
                     var4 = var30;
                     var5 = var31;
                  }

                  if (var20 == 0.0D) {
                     break;
                  }
               }
            }
         }

         if (var3 == null) {
            return null;
         } else {
            BufferedImage var27 = new BufferedImage(var1, var2, 2);
            Graphics2D var28 = var27.createGraphics();
            var28.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            try {
               var29 = (var1 - var4) / 2;
               var13 = (var2 - var5) / 2;
               var28.drawImage(var3, var29, var13, var4, var5, (ImageObserver)null);
            } finally {
               var28.dispose();
            }

            return var27;
         }
      } else {
         return null;
      }
   }

   public static DataBufferInt getScaledIconData(java.util.List<Image> var0, int var1, int var2) {
      BufferedImage var3 = getScaledIconImage(var0, var1, var2);
      if (var3 == null) {
         return null;
      } else {
         WritableRaster var4 = var3.getRaster();
         DataBuffer var5 = var4.getDataBuffer();
         return (DataBufferInt)var5;
      }
   }

   protected EventQueue getSystemEventQueueImpl() {
      return getSystemEventQueueImplPP();
   }

   static EventQueue getSystemEventQueueImplPP() {
      return getSystemEventQueueImplPP(AppContext.getAppContext());
   }

   public static EventQueue getSystemEventQueueImplPP(AppContext var0) {
      EventQueue var1 = (EventQueue)var0.get(AppContext.EVENT_QUEUE_KEY);
      return var1;
   }

   public static Container getNativeContainer(Component var0) {
      return Toolkit.getNativeContainer(var0);
   }

   public static Component getHeavyweightComponent(Component var0) {
      while(var0 != null && AWTAccessor.getComponentAccessor().isLightweight((Component)var0)) {
         var0 = AWTAccessor.getComponentAccessor().getParent((Component)var0);
      }

      return (Component)var0;
   }

   public int getFocusAcceleratorKeyMask() {
      return 8;
   }

   public boolean isPrintableCharacterModifiersMask(int var1) {
      return (var1 & 8) == (var1 & 2);
   }

   public boolean canPopupOverlapTaskBar() {
      boolean var1 = true;

      try {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
         }
      } catch (SecurityException var3) {
         var1 = false;
      }

      return var1;
   }

   public Window createInputMethodWindow(String var1, InputContext var2) {
      return new SimpleInputMethodWindow(var1, var2);
   }

   public boolean enableInputMethodsForTextComponent() {
      return false;
   }

   public static Locale getStartupLocale() {
      if (startupLocale == null) {
         String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.language", "en")));
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.region")));
         String var2;
         String var3;
         if (var1 != null) {
            int var4 = var1.indexOf(95);
            if (var4 >= 0) {
               var2 = var1.substring(0, var4);
               var3 = var1.substring(var4 + 1);
            } else {
               var2 = var1;
               var3 = "";
            }
         } else {
            var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.country", "")));
            var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.variant", "")));
         }

         startupLocale = new Locale(var0, var2, var3);
      }

      return startupLocale;
   }

   public Locale getDefaultKeyboardLocale() {
      return getStartupLocale();
   }

   public WindowClosingListener getWindowClosingListener() {
      return this.windowClosingListener;
   }

   public void setWindowClosingListener(WindowClosingListener var1) {
      this.windowClosingListener = var1;
   }

   public RuntimeException windowClosingNotify(WindowEvent var1) {
      return this.windowClosingListener != null ? this.windowClosingListener.windowClosingNotify(var1) : null;
   }

   public RuntimeException windowClosingDelivered(WindowEvent var1) {
      return this.windowClosingListener != null ? this.windowClosingListener.windowClosingDelivered(var1) : null;
   }

   protected synchronized MouseInfoPeer getMouseInfoPeer() {
      if (mPeer == null) {
         mPeer = new DefaultMouseInfoPeer();
      }

      return mPeer;
   }

   public static boolean needsXEmbed() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.awt.noxembed", "false")));
      if ("true".equals(var0)) {
         return false;
      } else {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         return var1 instanceof SunToolkit ? ((SunToolkit)var1).needsXEmbedImpl() : false;
      }
   }

   protected boolean needsXEmbedImpl() {
      return false;
   }

   protected final boolean isXEmbedServerRequested() {
      return (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.xembedserver")));
   }

   public static boolean isModalExcludedSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return var0.isModalExclusionTypeSupported(DEFAULT_MODAL_EXCLUSION_TYPE);
   }

   protected boolean isModalExcludedSupportedImpl() {
      return false;
   }

   public static void setModalExcluded(Window var0) {
      if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
         DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
      }

      var0.setModalExclusionType(DEFAULT_MODAL_EXCLUSION_TYPE);
   }

   public static boolean isModalExcluded(Window var0) {
      if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
         DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
      }

      return var0.getModalExclusionType().compareTo(DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
   }

   public boolean isModalityTypeSupported(Dialog.ModalityType var1) {
      return var1 == Dialog.ModalityType.MODELESS || var1 == Dialog.ModalityType.APPLICATION_MODAL;
   }

   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType var1) {
      return var1 == Dialog.ModalExclusionType.NO_EXCLUDE;
   }

   public void addModalityListener(ModalityListener var1) {
      this.modalityListeners.add(var1);
   }

   public void removeModalityListener(ModalityListener var1) {
      this.modalityListeners.remove(var1);
   }

   public void notifyModalityPushed(Dialog var1) {
      this.notifyModalityChange(1300, var1);
   }

   public void notifyModalityPopped(Dialog var1) {
      this.notifyModalityChange(1301, var1);
   }

   final void notifyModalityChange(int var1, Dialog var2) {
      ModalityEvent var3 = new ModalityEvent(var2, this.modalityListeners, var1);
      var3.dispatch();
   }

   public static boolean isLightweightOrUnknown(Component var0) {
      if (!var0.isLightweight() && getDefaultToolkit() instanceof SunToolkit) {
         return !(var0 instanceof Button) && !(var0 instanceof Canvas) && !(var0 instanceof Checkbox) && !(var0 instanceof Choice) && !(var0 instanceof Label) && !(var0 instanceof List) && !(var0 instanceof Panel) && !(var0 instanceof Scrollbar) && !(var0 instanceof ScrollPane) && !(var0 instanceof TextArea) && !(var0 instanceof TextField) && !(var0 instanceof Window);
      } else {
         return true;
      }
   }

   public void realSync() throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop {
      this.realSync(10000L);
   }

   public void realSync(long var1) throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop {
      if (EventQueue.isDispatchThread()) {
         throw new SunToolkit.IllegalThreadException("The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
      } else {
         int var3 = 0;

         do {
            this.sync();

            int var4;
            for(var4 = 0; var4 < 0; ++var4) {
               this.syncNativeQueue(var1);
            }

            while(this.syncNativeQueue(var1) && var4 < 20) {
               ++var4;
            }

            if (var4 >= 20) {
               throw new SunToolkit.InfiniteLoop();
            }

            for(var4 = 0; var4 < 0; ++var4) {
               this.waitForIdle(var1);
            }

            while(this.waitForIdle(var1) && var4 < 20) {
               ++var4;
            }

            if (var4 >= 20) {
               throw new SunToolkit.InfiniteLoop();
            }

            ++var3;
         } while((this.syncNativeQueue(var1) || this.waitForIdle(var1)) && var3 < 20);

      }
   }

   protected abstract boolean syncNativeQueue(long var1);

   private boolean isEQEmpty() {
      EventQueue var1 = this.getSystemEventQueueImpl();
      return AWTAccessor.getEventQueueAccessor().noEvents(var1);
   }

   protected final boolean waitForIdle(final long var1) {
      flushPendingEvents();
      boolean var3 = this.isEQEmpty();
      this.queueEmpty = false;
      this.eventDispatched = false;
      synchronized(this.waitLock) {
         postEvent(AppContext.getAppContext(), new PeerEvent(this.getSystemEventQueueImpl(), (Runnable)null, 4L) {
            public void dispatch() {
               int var1x;
               for(var1x = 0; var1x < 0; ++var1x) {
                  SunToolkit.this.syncNativeQueue(var1);
               }

               while(SunToolkit.this.syncNativeQueue(var1) && var1x < 20) {
                  ++var1x;
               }

               SunToolkit.flushPendingEvents();
               synchronized(SunToolkit.this.waitLock) {
                  SunToolkit.this.queueEmpty = SunToolkit.this.isEQEmpty();
                  SunToolkit.this.eventDispatched = true;
                  SunToolkit.this.waitLock.notifyAll();
               }
            }
         });

         try {
            while(!this.eventDispatched) {
               this.waitLock.wait();
            }
         } catch (InterruptedException var10) {
            return false;
         }
      }

      try {
         Thread.sleep(0L);
      } catch (InterruptedException var8) {
         throw new RuntimeException("Interrupted");
      }

      flushPendingEvents();
      synchronized(this.waitLock) {
         return !this.queueEmpty || !this.isEQEmpty() || !var3;
      }
   }

   public abstract void grab(Window var1);

   public abstract void ungrab(Window var1);

   public void showOrHideTouchKeyboard(Component var1, AWTEvent var2) {
   }

   public static boolean isTouchKeyboardAutoShowEnabled() {
      return touchKeyboardAutoShowIsEnabled;
   }

   public static native void closeSplashScreen();

   private void fireDesktopFontPropertyChanges() {
      this.setDesktopProperty("awt.font.desktophints", getDesktopFontHints());
   }

   public static void setAAFontSettingsCondition(boolean var0) {
      if (var0 != lastExtraCondition) {
         lastExtraCondition = var0;
         if (checkedSystemAAFontSettings) {
            checkedSystemAAFontSettings = false;
            Toolkit var1 = Toolkit.getDefaultToolkit();
            if (var1 instanceof SunToolkit) {
               ((SunToolkit)var1).fireDesktopFontPropertyChanges();
            }
         }
      }

   }

   private static RenderingHints getDesktopAAHintsByName(String var0) {
      Object var1 = null;
      var0 = var0.toLowerCase(Locale.ENGLISH);
      if (var0.equals("on")) {
         var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
      } else if (var0.equals("gasp")) {
         var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
      } else if (!var0.equals("lcd") && !var0.equals("lcd_hrgb")) {
         if (var0.equals("lcd_hbgr")) {
            var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
         } else if (var0.equals("lcd_vrgb")) {
            var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
         } else if (var0.equals("lcd_vbgr")) {
            var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
         }
      } else {
         var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
      }

      if (var1 != null) {
         RenderingHints var2 = new RenderingHints((Map)null);
         var2.put(RenderingHints.KEY_TEXT_ANTIALIASING, var1);
         return var2;
      } else {
         return null;
      }
   }

   private static boolean useSystemAAFontSettings() {
      if (!checkedSystemAAFontSettings) {
         useSystemAAFontSettings = true;
         String var0 = null;
         Toolkit var1 = Toolkit.getDefaultToolkit();
         if (var1 instanceof SunToolkit) {
            var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.useSystemAAFontSettings")));
         }

         if (var0 != null) {
            useSystemAAFontSettings = Boolean.valueOf(var0);
            if (!useSystemAAFontSettings) {
               desktopFontHints = getDesktopAAHintsByName(var0);
            }
         }

         if (useSystemAAFontSettings) {
            useSystemAAFontSettings = lastExtraCondition;
         }

         checkedSystemAAFontSettings = true;
      }

      return useSystemAAFontSettings;
   }

   protected RenderingHints getDesktopAAHints() {
      return null;
   }

   public static RenderingHints getDesktopFontHints() {
      if (useSystemAAFontSettings()) {
         Toolkit var0 = Toolkit.getDefaultToolkit();
         if (var0 instanceof SunToolkit) {
            RenderingHints var1 = ((SunToolkit)var0).getDesktopAAHints();
            return (RenderingHints)var1;
         } else {
            return null;
         }
      } else {
         return desktopFontHints != null ? (RenderingHints)((RenderingHints)desktopFontHints.clone()) : null;
      }
   }

   public abstract boolean isDesktopSupported();

   public static synchronized void consumeNextKeyTyped(KeyEvent var0) {
      try {
         AWTAccessor.getDefaultKeyboardFocusManagerAccessor().consumeNextKeyTyped((DefaultKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager(), var0);
      } catch (ClassCastException var2) {
         var2.printStackTrace();
      }

   }

   protected static void dumpPeers(PlatformLogger var0) {
      AWTAutoShutdown.getInstance().dumpPeers(var0);
   }

   public static Window getContainingWindow(Component var0) {
      while(var0 != null && !(var0 instanceof Window)) {
         var0 = ((Component)var0).getParent();
      }

      return (Window)var0;
   }

   public static synchronized boolean getSunAwtDisableMixing() {
      if (sunAwtDisableMixing == null) {
         sunAwtDisableMixing = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.disableMixing")));
      }

      return sunAwtDisableMixing;
   }

   public boolean isNativeGTKAvailable() {
      return false;
   }

   public synchronized void setWindowDeactivationTime(Window var1, long var2) {
      AppContext var4 = getAppContext(var1);
      WeakHashMap var5 = (WeakHashMap)var4.get(DEACTIVATION_TIMES_MAP_KEY);
      if (var5 == null) {
         var5 = new WeakHashMap();
         var4.put(DEACTIVATION_TIMES_MAP_KEY, var5);
      }

      var5.put(var1, var2);
   }

   public synchronized long getWindowDeactivationTime(Window var1) {
      AppContext var2 = getAppContext(var1);
      WeakHashMap var3 = (WeakHashMap)var2.get(DEACTIVATION_TIMES_MAP_KEY);
      if (var3 == null) {
         return -1L;
      } else {
         Long var4 = (Long)var3.get(var1);
         return var4 == null ? -1L : var4;
      }
   }

   public boolean isWindowOpacitySupported() {
      return false;
   }

   public boolean isWindowShapingSupported() {
      return false;
   }

   public boolean isWindowTranslucencySupported() {
      return false;
   }

   public boolean isTranslucencyCapable(GraphicsConfiguration var1) {
      return false;
   }

   public boolean isSwingBackbufferTranslucencySupported() {
      return false;
   }

   public static boolean isContainingTopLevelOpaque(Component var0) {
      Window var1 = getContainingWindow(var0);
      return var1 != null && var1.isOpaque();
   }

   public static boolean isContainingTopLevelTranslucent(Component var0) {
      Window var1 = getContainingWindow(var0);
      return var1 != null && var1.getOpacity() < 1.0F;
   }

   public boolean needUpdateWindow() {
      return false;
   }

   public int getNumberOfButtons() {
      return 3;
   }

   public static boolean isInstanceOf(Object var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return var1 == null ? false : isInstanceOf(var0.getClass(), var1);
      }
   }

   private static boolean isInstanceOf(Class<?> var0, String var1) {
      if (var0 == null) {
         return false;
      } else if (var0.getName().equals(var1)) {
         return true;
      } else {
         Class[] var2 = var0.getInterfaces();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class var5 = var2[var4];
            if (var5.getName().equals(var1)) {
               return true;
            }
         }

         return isInstanceOf(var0.getSuperclass(), var1);
      }
   }

   protected static LightweightFrame getLightweightFrame(Component var0) {
      while(var0 != null) {
         if (var0 instanceof LightweightFrame) {
            return (LightweightFrame)var0;
         }

         if (var0 instanceof Window) {
            return null;
         }

         var0 = ((Component)var0).getParent();
      }

      return null;
   }

   public static void setSystemGenerated(AWTEvent var0) {
      AWTAccessor.getAWTEventAccessor().setSystemGenerated(var0);
   }

   public static boolean isSystemGenerated(AWTEvent var0) {
      return AWTAccessor.getAWTEventAccessor().isSystemGenerated(var0);
   }

   static {
      if ((Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.nativedebug")))) {
         DebugSettings.init();
      }

      touchKeyboardAutoShowIsEnabled = Boolean.valueOf((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("awt.touchKeyboardAutoShowIsEnabled", "true"))));
      numberOfButtons = 0;
      AWT_LOCK = new ReentrantLock();
      AWT_LOCK_COND = AWT_LOCK.newCondition();
      appContextMap = Collections.synchronizedMap(new WeakHashMap());
      fileImgCache = new SoftCache();
      urlImgCache = new SoftCache();
      startupLocale = null;
      mPeer = null;
      DEFAULT_MODAL_EXCLUSION_TYPE = null;
      lastExtraCondition = true;
      sunAwtDisableMixing = null;
      DEACTIVATION_TIMES_MAP_KEY = new Object();
   }

   public static class IllegalThreadException extends RuntimeException {
      public IllegalThreadException(String var1) {
         super(var1);
      }

      public IllegalThreadException() {
      }
   }

   public static class InfiniteLoop extends RuntimeException {
   }

   public static class OperationTimedOut extends RuntimeException {
      public OperationTimedOut(String var1) {
         super(var1);
      }

      public OperationTimedOut() {
      }
   }

   static class ModalityListenerList implements ModalityListener {
      Vector<ModalityListener> listeners = new Vector();

      void add(ModalityListener var1) {
         this.listeners.addElement(var1);
      }

      void remove(ModalityListener var1) {
         this.listeners.removeElement(var1);
      }

      public void modalityPushed(ModalityEvent var1) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            ((ModalityListener)var2.next()).modalityPushed(var1);
         }

      }

      public void modalityPopped(ModalityEvent var1) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            ((ModalityListener)var2.next()).modalityPopped(var1);
         }

      }
   }
}
