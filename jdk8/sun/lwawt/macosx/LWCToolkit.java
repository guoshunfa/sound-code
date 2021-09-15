package sun.lwawt.macosx;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.InvocationEvent;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TrayIconPeer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CGraphicsConfig;
import sun.awt.CGraphicsDevice;
import sun.awt.LightweightFrame;
import sun.awt.PlatformFont;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;
import sun.java2d.opengl.OGLRenderQueue;
import sun.lwawt.LWCursorManager;
import sun.lwawt.LWToolkit;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformComponent;
import sun.lwawt.PlatformWindow;
import sun.lwawt.SecurityWarningWindow;
import sun.security.action.GetBooleanAction;
import sun.util.CoreResourceBundleControl;

public final class LWCToolkit extends LWToolkit {
   private static final int BUTTONS = 5;
   private static CInputMethodDescriptor sInputMethodDescriptor;
   private static final boolean inAWT;
   private static final int NUM_APPLE_COLORS = 3;
   public static final int KEYBOARD_FOCUS_COLOR = 0;
   public static final int INACTIVE_SELECTION_BACKGROUND_COLOR = 1;
   public static final int INACTIVE_SELECTION_FOREGROUND_COLOR = 2;
   private static int[] appleColors;
   private static boolean areExtraMouseButtonsEnabled;
   private static final String nsImagePrefix = "NSImage://";
   private static Boolean sunAwtDisableCALayers;

   private static native void initIDs();

   public LWCToolkit() {
      areExtraMouseButtonsEnabled = Boolean.parseBoolean(System.getProperty("sun.awt.enableExtraMouseButtons", "true"));
      System.setProperty("sun.awt.enableExtraMouseButtons", "" + areExtraMouseButtonsEnabled);
   }

   private native void loadNativeColors(int[] var1, int[] var2);

   protected void loadSystemColors(int[] var1) {
      if (var1 != null) {
         this.loadNativeColors(var1, appleColors);
      }
   }

   public static Color getAppleColor(int var0) {
      return new LWCToolkit.AppleSpecificColor(var0);
   }

   static void systemColorsChanged() {
      EventQueue.invokeLater(() -> {
         AccessController.doPrivileged(() -> {
            AWTAccessor.getSystemColorAccessor().updateSystemColors();
            return null;
         });
      });
   }

   public static LWCToolkit getLWCToolkit() {
      return (LWCToolkit)Toolkit.getDefaultToolkit();
   }

   protected PlatformWindow createPlatformWindow(LWWindowPeer.PeerType var1) {
      if (var1 == LWWindowPeer.PeerType.EMBEDDED_FRAME) {
         return new CPlatformEmbeddedFrame();
      } else if (var1 == LWWindowPeer.PeerType.VIEW_EMBEDDED_FRAME) {
         return new CViewPlatformEmbeddedFrame();
      } else if (var1 == LWWindowPeer.PeerType.LW_FRAME) {
         return new CPlatformLWWindow();
      } else {
         assert var1 == LWWindowPeer.PeerType.SIMPLEWINDOW || var1 == LWWindowPeer.PeerType.DIALOG || var1 == LWWindowPeer.PeerType.FRAME;

         return new CPlatformWindow();
      }
   }

   LWWindowPeer createEmbeddedFrame(CEmbeddedFrame var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.EMBEDDED_FRAME);
      return this.createDelegatedPeer(var1, var2, var3, LWWindowPeer.PeerType.EMBEDDED_FRAME);
   }

   LWWindowPeer createEmbeddedFrame(CViewEmbeddedFrame var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.VIEW_EMBEDDED_FRAME);
      return this.createDelegatedPeer(var1, var2, var3, LWWindowPeer.PeerType.VIEW_EMBEDDED_FRAME);
   }

   private CPrinterDialogPeer createCPrinterDialog(CPrinterDialog var1) {
      PlatformComponent var2 = this.createPlatformComponent();
      PlatformWindow var3 = this.createPlatformWindow(LWWindowPeer.PeerType.DIALOG);
      CPrinterDialogPeer var4 = new CPrinterDialogPeer(var1, var2, var3);
      targetCreatedPeer(var1, var4);
      return var4;
   }

   public DialogPeer createDialog(Dialog var1) {
      return (DialogPeer)(var1 instanceof CPrinterDialog ? this.createCPrinterDialog((CPrinterDialog)var1) : super.createDialog(var1));
   }

   protected SecurityWarningWindow createSecurityWarning(Window var1, LWWindowPeer var2) {
      return new CWarningWindow(var1, var2);
   }

   protected PlatformComponent createPlatformComponent() {
      return new CPlatformComponent();
   }

   protected PlatformComponent createLwPlatformComponent() {
      return new CPlatformLWComponent();
   }

   protected FileDialogPeer createFileDialogPeer(FileDialog var1) {
      return new CFileDialog(var1);
   }

   public MenuPeer createMenu(Menu var1) {
      CMenu var2 = new CMenu(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public MenuBarPeer createMenuBar(MenuBar var1) {
      CMenuBar var2 = new CMenuBar(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public MenuItemPeer createMenuItem(MenuItem var1) {
      CMenuItem var2 = new CMenuItem(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) {
      CCheckboxMenuItem var2 = new CCheckboxMenuItem(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public PopupMenuPeer createPopupMenu(PopupMenu var1) {
      CPopupMenu var2 = new CPopupMenu(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public SystemTrayPeer createSystemTray(SystemTray var1) {
      return new CSystemTray();
   }

   public TrayIconPeer createTrayIcon(TrayIcon var1) {
      CTrayIcon var2 = new CTrayIcon(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   protected DesktopPeer createDesktopPeer(Desktop var1) {
      return new CDesktopPeer();
   }

   public LWCursorManager getCursorManager() {
      return CCursorManager.getInstance();
   }

   public Cursor createCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException, HeadlessException {
      return new CCustomCursor(var1, var2, var3);
   }

   public Dimension getBestCursorSize(int var1, int var2) throws HeadlessException {
      return CCustomCursor.getBestCursorSize(var1, var2);
   }

   protected void platformCleanup() {
   }

   protected void platformInit() {
   }

   protected void platformRunMessage() {
   }

   protected void platformShutdown() {
   }

   public FontPeer getFontPeer(String var1, int var2) {
      return new LWCToolkit.OSXPlatformFont(var1, var2);
   }

   protected int getScreenHeight() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().height;
   }

   protected int getScreenWidth() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().width;
   }

   protected void initializeDesktopProperties() {
      super.initializeDesktopProperties();
      HashMap var1 = new HashMap();
      var1.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
      this.desktopProperties.put("awt.font.desktophints", var1);
      this.desktopProperties.put("awt.mouse.numButtons", 5);
      this.desktopProperties.put("DnD.Autoscroll.initialDelay", new Integer(50));
      this.desktopProperties.put("DnD.Autoscroll.interval", new Integer(50));
      this.desktopProperties.put("DnD.Autoscroll.cursorHysteresis", new Integer(5));
      this.desktopProperties.put("DnD.isDragImageSupported", new Boolean(true));
      this.desktopProperties.put("DnD.Cursor.CopyDrop", new NamedCursor("DnD.Cursor.CopyDrop"));
      this.desktopProperties.put("DnD.Cursor.MoveDrop", new NamedCursor("DnD.Cursor.MoveDrop"));
      this.desktopProperties.put("DnD.Cursor.LinkDrop", new NamedCursor("DnD.Cursor.LinkDrop"));
      this.desktopProperties.put("DnD.Cursor.CopyNoDrop", new NamedCursor("DnD.Cursor.CopyNoDrop"));
      this.desktopProperties.put("DnD.Cursor.MoveNoDrop", new NamedCursor("DnD.Cursor.MoveNoDrop"));
      this.desktopProperties.put("DnD.Cursor.LinkNoDrop", new NamedCursor("DnD.Cursor.LinkNoDrop"));
   }

   protected boolean syncNativeQueue(long var1) {
      return this.nativeSyncQueue(var1);
   }

   public native void beep();

   public int getScreenResolution() throws HeadlessException {
      return (int)((CGraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()).getXResolution();
   }

   public Insets getScreenInsets(GraphicsConfiguration var1) {
      return ((CGraphicsConfig)var1).getDevice().getScreenInsets();
   }

   public void sync() {
      OGLRenderQueue.sync();
      flushNativeSelectors();
   }

   public RobotPeer createRobot(Robot var1, GraphicsDevice var2) {
      return new CRobot(var1, (CGraphicsDevice)var2);
   }

   private native boolean isCapsLockOn();

   public boolean getLockingKeyState(int var1) throws UnsupportedOperationException {
      switch(var1) {
      case 20:
         return this.isCapsLockOn();
      case 144:
      case 145:
      case 262:
         throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
      default:
         throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
      }
   }

   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
      return areExtraMouseButtonsEnabled;
   }

   public int getNumberOfButtons() {
      return 5;
   }

   public boolean isTraySupported() {
      return true;
   }

   public DataTransferer getDataTransferer() {
      return CDataTransferer.getInstanceImpl();
   }

   public boolean isAlwaysOnTopSupported() {
      return true;
   }

   private static void installToolkitThreadInJava() {
      Thread.currentThread().setName(CThreading.APPKIT_THREAD_NAME);
      AccessController.doPrivileged(() -> {
         Thread.currentThread().setContextClassLoader((ClassLoader)null);
         return null;
      });
   }

   public boolean isWindowOpacitySupported() {
      return true;
   }

   public boolean isFrameStateSupported(int var1) throws HeadlessException {
      switch(var1) {
      case 0:
      case 1:
      case 6:
         return true;
      default:
         return false;
      }
   }

   public int getMenuShortcutKeyMask() {
      return 4;
   }

   public Image getImage(String var1) {
      Image var2 = this.checkForNSImage(var1);
      if (var2 != null) {
         return var2;
      } else if (imageCached(var1)) {
         return super.getImage(var1);
      } else {
         String var3 = getScaledImageName(var1);
         return imageExists(var3) ? this.getImageWithResolutionVariant(var1, var3) : super.getImage(var1);
      }
   }

   public Image getImage(URL var1) {
      if (imageCached(var1)) {
         return super.getImage(var1);
      } else {
         URL var2 = getScaledImageURL(var1);
         return imageExists(var2) ? this.getImageWithResolutionVariant(var1, var2) : super.getImage(var1);
      }
   }

   private Image checkForNSImage(String var1) {
      if (var1 == null) {
         return null;
      } else {
         return !var1.startsWith("NSImage://") ? null : CImage.getCreator().createImageFromName(var1.substring("NSImage://".length()));
      }
   }

   public static boolean doEquals(final Object var0, final Object var1, Component var2) {
      if (var0 == var1) {
         return true;
      } else {
         final boolean[] var3 = new boolean[1];

         try {
            invokeAndWait(new Runnable() {
               public void run() {
                  synchronized(var3) {
                     var3[0] = var0.equals(var1);
                  }
               }
            }, var2);
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         synchronized(var3) {
            return var3[0];
         }
      }
   }

   public static <T> T invokeAndWait(Callable<T> var0, Component var1) throws Exception {
      LWCToolkit.CallableWrapper var2 = new LWCToolkit.CallableWrapper(var0);
      invokeAndWait((Runnable)var2, var1);
      return var2.getResult();
   }

   public static void invokeAndWait(Runnable var0, Component var1) throws InvocationTargetException {
      long var2 = createAWTRunLoopMediator();
      InvocationEvent var4 = new InvocationEvent(var1 != null ? var1 : Toolkit.getDefaultToolkit(), var0, () -> {
         if (var2 != 0L) {
            stopAWTRunLoop(var2);
         }

      }, true);
      if (var1 != null) {
         AppContext var5 = SunToolkit.targetToAppContext(var1);
         SunToolkit.postEvent(var5, var4);
         SunToolkit.flushPendingEvents(var5);
      } else {
         ((LWCToolkit)Toolkit.getDefaultToolkit()).getSystemEventQueueForInvokeAndWait().postEvent(var4);
      }

      doAWTRunLoop(var2, false);
      Object var6 = var4.getException();
      if (var6 != null) {
         if (var6 instanceof UndeclaredThrowableException) {
            var6 = ((UndeclaredThrowableException)var6).getUndeclaredThrowable();
         }

         throw new InvocationTargetException((Throwable)var6);
      }
   }

   public static void invokeLater(Runnable var0, Component var1) throws InvocationTargetException {
      InvocationEvent var2 = new InvocationEvent(var1 != null ? var1 : Toolkit.getDefaultToolkit(), var0);
      if (var1 != null) {
         AppContext var3 = SunToolkit.targetToAppContext(var1);
         SunToolkit.postEvent(var3, var2);
         SunToolkit.flushPendingEvents(var3);
      } else {
         ((LWCToolkit)Toolkit.getDefaultToolkit()).getSystemEventQueueForInvokeAndWait().postEvent(var2);
      }

      Exception var4 = var2.getException();
      if (var4 != null) {
         if (var4 instanceof UndeclaredThrowableException) {
            throw new InvocationTargetException(((UndeclaredThrowableException)var4).getUndeclaredThrowable());
         } else {
            throw new InvocationTargetException(var4);
         }
      }
   }

   EventQueue getSystemEventQueueForInvokeAndWait() {
      return this.getSystemEventQueueImpl();
   }

   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException {
      LightweightFrame var2 = SunToolkit.getLightweightFrame(var1.getComponent());
      return (DragSourceContextPeer)(var2 != null ? var2.createDragSourceContextPeer(var1) : CDragSourceContextPeer.createDragSourceContextPeer(var1));
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      LightweightFrame var6 = SunToolkit.getLightweightFrame(var3);
      if (var6 != null) {
         return var6.createDragGestureRecognizer(var1, var2, var3, var4, var5);
      } else {
         CMouseDragGestureRecognizer var7 = null;
         if (MouseDragGestureRecognizer.class.equals(var1)) {
            var7 = new CMouseDragGestureRecognizer(var2, var3, var4, var5);
         }

         return var7;
      }
   }

   public Locale getDefaultKeyboardLocale() {
      Locale var1 = CInputMethod.getNativeLocale();
      return var1 == null ? super.getDefaultKeyboardLocale() : var1;
   }

   public InputMethodDescriptor getInputMethodAdapterDescriptor() {
      if (sInputMethodDescriptor == null) {
         sInputMethodDescriptor = new CInputMethodDescriptor();
      }

      return sInputMethodDescriptor;
   }

   public Map mapInputMethodHighlight(InputMethodHighlight var1) {
      return CInputMethod.mapInputMethodHighlight(var1);
   }

   public int getFocusAcceleratorKeyMask() {
      return 10;
   }

   public boolean isPrintableCharacterModifiersMask(int var1) {
      return (var1 & 6) == 0;
   }

   public boolean canPopupOverlapTaskBar() {
      return false;
   }

   public static synchronized boolean getSunAwtDisableCALayers() {
      if (sunAwtDisableCALayers == null) {
         sunAwtDisableCALayers = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.awt.disableCALayers")));
      }

      return sunAwtDisableCALayers;
   }

   native boolean isApplicationActive();

   public static native boolean isEmbedded();

   public native void activateApplicationIgnoringOtherApps();

   static native long createAWTRunLoopMediator();

   static void doAWTRunLoop(long var0, boolean var2) {
      doAWTRunLoopImpl(var0, var2, inAWT);
   }

   private static native void doAWTRunLoopImpl(long var0, boolean var2, boolean var3);

   static native void stopAWTRunLoop(long var0);

   private native boolean nativeSyncQueue(long var1);

   static native void flushNativeSelectors();

   public Clipboard createPlatformClipboard() {
      return new CClipboard("System");
   }

   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType var1) {
      return var1 == null || var1 == Dialog.ModalExclusionType.NO_EXCLUDE || var1 == Dialog.ModalExclusionType.APPLICATION_EXCLUDE || var1 == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE;
   }

   public boolean isModalityTypeSupported(Dialog.ModalityType var1) {
      return var1 == null || var1 == Dialog.ModalityType.MODELESS || var1 == Dialog.ModalityType.DOCUMENT_MODAL || var1 == Dialog.ModalityType.APPLICATION_MODAL || var1 == Dialog.ModalityType.TOOLKIT_MODAL;
   }

   public boolean isWindowShapingSupported() {
      return true;
   }

   public boolean isWindowTranslucencySupported() {
      return true;
   }

   public boolean isTranslucencyCapable(GraphicsConfiguration var1) {
      return true;
   }

   public boolean isSwingBackbufferTranslucencySupported() {
      return true;
   }

   public boolean enableInputMethodsForTextComponent() {
      return true;
   }

   private static URL getScaledImageURL(URL var0) {
      try {
         String var1 = getScaledImageName(var0.getPath());
         return var1 == null ? null : new URL(var0.getProtocol(), var0.getHost(), var0.getPort(), var1);
      } catch (MalformedURLException var2) {
         return null;
      }
   }

   private static String getScaledImageName(String var0) {
      if (!isValidPath(var0)) {
         return null;
      } else {
         int var1 = var0.lastIndexOf(47);
         String var2 = var1 < 0 ? var0 : var0.substring(var1 + 1);
         if (var2.contains("@2x")) {
            return null;
         } else {
            int var3 = var2.lastIndexOf(46);
            String var4 = var3 < 0 ? var2 + "@2x" : var2.substring(0, var3) + "@2x" + var2.substring(var3);
            return var1 < 0 ? var4 : var0.substring(0, var1 + 1) + var4;
         }
      }
   }

   private static boolean isValidPath(String var0) {
      return var0 != null && !var0.isEmpty() && !var0.endsWith("/") && !var0.endsWith(".");
   }

   protected PlatformWindow getPlatformWindowUnderMouse() {
      return CPlatformWindow.nativeGetTopmostPlatformWindowUnderMouse();
   }

   static {
      System.err.flush();
      ResourceBundle var0 = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
         public ResourceBundle run() {
            ResourceBundle var1 = null;

            try {
               var1 = ResourceBundle.getBundle("sun.awt.resources.awtosx", (ResourceBundle.Control)CoreResourceBundleControl.getRBControlInstance());
            } catch (MissingResourceException var3) {
            }

            System.loadLibrary("awt");
            System.loadLibrary("fontmanager");
            return var1;
         }
      });
      AWTAccessor.getToolkitAccessor().setPlatformResources(var0);
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      inAWT = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return !Boolean.parseBoolean(System.getProperty("javafx.embed.singleThread", "false"));
         }
      });
      appleColors = new int[]{-8355712, -4144960, -13619152};
      areExtraMouseButtonsEnabled = true;
      sunAwtDisableCALayers = null;
   }

   static final class CallableWrapper<T> implements Runnable {
      final Callable<T> callable;
      T object;
      Exception e;

      CallableWrapper(Callable<T> var1) {
         this.callable = var1;
      }

      public void run() {
         try {
            this.object = this.callable.call();
         } catch (Exception var2) {
            this.e = var2;
         }

      }

      public T getResult() throws Exception {
         if (this.e != null) {
            throw this.e;
         } else {
            return this.object;
         }
      }
   }

   class OSXPlatformFont extends PlatformFont {
      OSXPlatformFont(String var2, int var3) {
         super(var2, var3);
      }

      protected char getMissingGlyphCharacter() {
         return '\ufff8';
      }
   }

   private static class AppleSpecificColor extends Color {
      private final int index;

      AppleSpecificColor(int var1) {
         super(LWCToolkit.appleColors[var1]);
         this.index = var1;
      }

      public int getRGB() {
         return LWCToolkit.appleColors[this.index];
      }
   }
}
