package sun.lwawt.macosx;

import com.apple.laf.ClientPropertyApplicator;
import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import sun.awt.AWTAccessor;
import sun.awt.CGraphicsDevice;
import sun.awt.CausedFocusEvent;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.CGLSurfaceData;
import sun.lwawt.LWLightweightFramePeer;
import sun.lwawt.LWToolkit;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;
import sun.util.logging.PlatformLogger;

public class CPlatformWindow extends CFRetainedResource implements PlatformWindow {
   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.lwawt.macosx.CPlatformWindow");
   private static final PlatformLogger focusLogger = PlatformLogger.getLogger("sun.lwawt.macosx.focus.CPlatformWindow");
   public static final String WINDOW_BRUSH_METAL_LOOK = "apple.awt.brushMetalLook";
   public static final String WINDOW_DRAGGABLE_BACKGROUND = "apple.awt.draggableWindowBackground";
   public static final String WINDOW_ALPHA = "Window.alpha";
   public static final String WINDOW_SHADOW = "Window.shadow";
   public static final String WINDOW_STYLE = "Window.style";
   public static final String WINDOW_SHADOW_REVALIDATE_NOW = "apple.awt.windowShadow.revalidateNow";
   public static final String WINDOW_DOCUMENT_MODIFIED = "Window.documentModified";
   public static final String WINDOW_DOCUMENT_FILE = "Window.documentFile";
   public static final String WINDOW_CLOSEABLE = "Window.closeable";
   public static final String WINDOW_MINIMIZABLE = "Window.minimizable";
   public static final String WINDOW_ZOOMABLE = "Window.zoomable";
   public static final String WINDOW_HIDES_ON_DEACTIVATE = "Window.hidesOnDeactivate";
   public static final String WINDOW_DOC_MODAL_SHEET = "apple.awt.documentModalSheet";
   public static final String WINDOW_FADE_DELEGATE = "apple.awt._windowFadeDelegate";
   public static final String WINDOW_FADE_IN = "apple.awt._windowFadeIn";
   public static final String WINDOW_FADE_OUT = "apple.awt._windowFadeOut";
   public static final String WINDOW_FULLSCREENABLE = "apple.awt.fullscreenable";
   static final int MODELESS = 0;
   static final int DOCUMENT_MODAL = 1;
   static final int APPLICATION_MODAL = 2;
   static final int TOOLKIT_MODAL = 3;
   static final int _RESERVED_FOR_DATA = 1;
   static final int DECORATED = 2;
   static final int TEXTURED = 4;
   static final int UNIFIED = 8;
   static final int UTILITY = 16;
   static final int HUD = 32;
   static final int SHEET = 64;
   static final int CLOSEABLE = 128;
   static final int MINIMIZABLE = 256;
   static final int RESIZABLE = 512;
   static final int NONACTIVATING = 16777216;
   static final int IS_DIALOG = 33554432;
   static final int IS_MODAL = 67108864;
   static final int IS_POPUP = 134217728;
   static final int _STYLE_PROP_BITMASK = 1022;
   static final int HAS_SHADOW = 1024;
   static final int ZOOMABLE = 2048;
   static final int ALWAYS_ON_TOP = 32768;
   static final int HIDES_ON_DEACTIVATE = 131072;
   static final int DRAGGABLE_BACKGROUND = 524288;
   static final int DOCUMENT_MODIFIED = 2097152;
   static final int FULLSCREENABLE = 8388608;
   static final int _METHOD_PROP_BITMASK = 11177472;
   static final int SHOULD_BECOME_KEY = 4096;
   static final int SHOULD_BECOME_MAIN = 8192;
   static final int MODAL_EXCLUDED = 65536;
   static final int _CALLBACK_PROP_BITMASK = 77824;
   static ClientPropertyApplicator<JRootPane, CPlatformWindow> CLIENT_PROPERTY_APPLICATOR = new ClientPropertyApplicator<JRootPane, CPlatformWindow>(new ClientPropertyApplicator.Property[]{new ClientPropertyApplicator.Property<CPlatformWindow>("Window.documentModified") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(2097152, var2 == null ? false : Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("apple.awt.brushMetalLook") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(4, Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.alpha") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         AWTUtilities.setWindowOpacity(var1.target, var2 == null ? 1.0F : Float.parseFloat(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.shadow") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(1024, var2 == null ? true : Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.minimizable") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(256, Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.closeable") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(128, Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.zoomable") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(2048, Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("apple.awt.fullscreenable") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.setStyleBits(8388608, Boolean.parseBoolean(var2.toString()));
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("apple.awt.windowShadow.revalidateNow") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         var1.execute((var0) -> {
            CPlatformWindow.nativeRevalidateNSWindowShadow(var0);
         });
      }
   }, new ClientPropertyApplicator.Property<CPlatformWindow>("Window.documentFile") {
      public void applyProperty(CPlatformWindow var1, Object var2) {
         if (var2 != null && var2 instanceof File) {
            String var3 = ((File)var2).getAbsolutePath();
            var1.execute((var1x) -> {
               CPlatformWindow.nativeSetNSWindowRepresentedFilename(var1x, var3);
            });
         } else {
            var1.execute((var0) -> {
               CPlatformWindow.nativeSetNSWindowRepresentedFilename(var0, (String)null);
            });
         }
      }
   }}) {
      public CPlatformWindow convertJComponentToTarget(JRootPane var1) {
         Component var2 = SwingUtilities.getRoot(var1);
         return var2 != null && (LWWindowPeer)var2.getPeer() != null ? (CPlatformWindow)((LWWindowPeer)var2.getPeer()).getPlatformWindow() : null;
      }
   };
   private Rectangle nativeBounds = new Rectangle(0, 0, 0, 0);
   private volatile boolean isFullScreenMode;
   private boolean isFullScreenAnimationOn;
   private volatile boolean isIconifyAnimationActive;
   private volatile boolean isZoomed;
   private Window target;
   private LWWindowPeer peer;
   protected CPlatformView contentView;
   protected CPlatformWindow owner;
   protected boolean visible = false;
   private boolean undecorated;
   private Rectangle normalBounds = null;
   private CPlatformResponder responder;

   private native long nativeCreateNSWindow(long var1, long var3, long var5, double var7, double var9, double var11, double var13);

   private static native void nativeSetNSWindowStyleBits(long var0, int var2, int var3);

   private static native void nativeSetNSWindowMenuBar(long var0, long var2);

   private static native Insets nativeGetNSWindowInsets(long var0);

   private static native void nativeSetNSWindowBounds(long var0, double var2, double var4, double var6, double var8);

   private static native void nativeSetNSWindowMinMax(long var0, double var2, double var4, double var6, double var8);

   private static native void nativePushNSWindowToBack(long var0);

   private static native void nativePushNSWindowToFront(long var0);

   private static native void nativeSetNSWindowTitle(long var0, String var2);

   private static native void nativeRevalidateNSWindowShadow(long var0);

   private static native void nativeSetNSWindowMinimizedIcon(long var0, long var2);

   private static native void nativeSetNSWindowRepresentedFilename(long var0, String var2);

   private static native void nativeSetEnabled(long var0, boolean var2);

   private static native void nativeSynthesizeMouseEnteredExitedEvents();

   private static native void nativeSynthesizeMouseEnteredExitedEvents(long var0, int var2);

   private static native void nativeDispose(long var0);

   private static native void nativeEnterFullScreenMode(long var0);

   private static native void nativeExitFullScreenMode(long var0);

   static native CPlatformWindow nativeGetTopmostPlatformWindowUnderMouse();

   static int SET(int var0, int var1, boolean var2) {
      return var2 ? var0 | var1 : var0 & ~var1;
   }

   static boolean IS(int var0, int var1) {
      return (var0 & var1) != 0;
   }

   public CPlatformWindow() {
      super(0L, true);
   }

   public void initialize(Window var1, LWWindowPeer var2, PlatformWindow var3) {
      this.initializeBase(var1, var2, var3, new CPlatformView());
      int var4 = this.getInitialStyleBits();
      this.responder = this.createPlatformResponder();
      this.contentView = this.createContentView();
      this.contentView.initialize(this.peer, this.responder);
      Rectangle var5;
      if (!IS(2, var4)) {
         var5 = new Rectangle(0, 0, 1, 1);
      } else {
         var5 = var2.constrainBounds(var1.getBounds());
      }

      AtomicLong var6 = new AtomicLong();
      this.contentView.execute((var4x) -> {
         boolean var6x = false;
         if (this.owner != null) {
            var6x = 0L != this.owner.executeGet((var6xx) -> {
               var6.set(this.nativeCreateNSWindow(var4x, var6xx, (long)var4, (double)var5.x, (double)var5.y, (double)var5.width, (double)var5.height));
               return 1L;
            });
         }

         if (!var6x) {
            var6.set(this.nativeCreateNSWindow(var4x, 0L, (long)var4, (double)var5.x, (double)var5.y, (double)var5.width, (double)var5.height));
         }

      });
      this.setPtr(var6.get());
      if (this.target instanceof RootPaneContainer) {
         final JRootPane var7 = ((RootPaneContainer)this.target).getRootPane();
         if (var7 != null) {
            var7.addPropertyChangeListener("ancestor", new PropertyChangeListener() {
               public void propertyChange(PropertyChangeEvent var1) {
                  CPlatformWindow.CLIENT_PROPERTY_APPLICATOR.attachAndApplyClientProperties(var7);
                  var7.removePropertyChangeListener("ancestor", this);
               }
            });
         }
      }

      this.validateSurface();
   }

   protected void initializeBase(Window var1, LWWindowPeer var2, PlatformWindow var3, CPlatformView var4) {
      this.peer = var2;
      this.target = var1;
      if (var3 instanceof CPlatformWindow) {
         this.owner = (CPlatformWindow)var3;
      }

      this.contentView = var4;
   }

   protected CPlatformResponder createPlatformResponder() {
      return new CPlatformResponder(this.peer, false);
   }

   protected CPlatformView createContentView() {
      return new CPlatformView();
   }

   protected int getInitialStyleBits() {
      int var1 = 3970;
      if (this.isNativelyFocusableWindow()) {
         var1 = SET(var1, 4096, true);
         var1 = SET(var1, 8192, true);
      }

      boolean var2 = this.target instanceof Frame;
      boolean var3 = this.target instanceof Dialog;
      boolean var4 = this.target.getType() == Window.Type.POPUP;
      if (var3) {
         var1 = SET(var1, 256, false);
      }

      this.undecorated = var2 ? ((Frame)this.target).isUndecorated() : (var3 ? ((Dialog)this.target).isUndecorated() : true);
      if (this.undecorated) {
         var1 = SET(var1, 2, false);
      }

      boolean var5 = var2 ? ((Frame)this.target).isResizable() : (var3 ? ((Dialog)this.target).isResizable() : false);
      var1 = SET(var1, 512, var5);
      if (!var5) {
         var1 = SET(var1, 2048, false);
      }

      if (this.target.isAlwaysOnTop()) {
         var1 = SET(var1, 32768, true);
      }

      if (this.target.getModalExclusionType() == Dialog.ModalExclusionType.APPLICATION_EXCLUDE) {
         var1 = SET(var1, 65536, true);
      }

      if (var4) {
         var1 = SET(var1, 4, false);
         var1 = SET(var1, 16777216, true);
         var1 = SET(var1, 134217728, true);
      }

      if (Window.Type.UTILITY.equals(this.target.getType())) {
         var1 = SET(var1, 16, true);
      }

      if (this.target instanceof RootPaneContainer) {
         JRootPane var7 = ((RootPaneContainer)this.target).getRootPane();
         Object var6 = null;
         var6 = var7.getClientProperty("apple.awt.brushMetalLook");
         if (var6 != null) {
            var1 = SET(var1, 4, Boolean.parseBoolean(var6.toString()));
         }

         if (var3 && ((Dialog)this.target).getModalityType() == Dialog.ModalityType.DOCUMENT_MODAL) {
            var6 = var7.getClientProperty("apple.awt.documentModalSheet");
            if (var6 != null) {
               var1 = SET(var1, 64, Boolean.parseBoolean(var6.toString()));
            }
         }

         var6 = var7.getClientProperty("Window.style");
         if (var6 != null) {
            if ("small".equals(var6)) {
               var1 = SET(var1, 16, true);
               if (this.target.isAlwaysOnTop() && var7.getClientProperty("Window.hidesOnDeactivate") == null) {
                  var1 = SET(var1, 131072, true);
               }
            }

            if ("textured".equals(var6)) {
               var1 = SET(var1, 4, true);
            }

            if ("unified".equals(var6)) {
               var1 = SET(var1, 8, true);
            }

            if ("hud".equals(var6)) {
               var1 = SET(var1, 32, true);
            }
         }

         var6 = var7.getClientProperty("Window.hidesOnDeactivate");
         if (var6 != null) {
            var1 = SET(var1, 131072, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("Window.closeable");
         if (var6 != null) {
            var1 = SET(var1, 128, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("Window.minimizable");
         if (var6 != null) {
            var1 = SET(var1, 256, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("Window.zoomable");
         if (var6 != null) {
            var1 = SET(var1, 2048, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("apple.awt.fullscreenable");
         if (var6 != null) {
            var1 = SET(var1, 8388608, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("Window.shadow");
         if (var6 != null) {
            var1 = SET(var1, 1024, Boolean.parseBoolean(var6.toString()));
         }

         var6 = var7.getClientProperty("apple.awt.draggableWindowBackground");
         if (var6 != null) {
            var1 = SET(var1, 524288, Boolean.parseBoolean(var6.toString()));
         }
      }

      if (var3) {
         var1 = SET(var1, 33554432, true);
         if (((Dialog)this.target).isModal()) {
            var1 = SET(var1, 67108864, true);
         }
      }

      this.peer.setTextured(IS(4, var1));
      return var1;
   }

   private void setStyleBits(int var1, boolean var2) {
      this.execute((var2x) -> {
         nativeSetNSWindowStyleBits(var2x, var1, var2 ? var1 : 0);
      });
   }

   private native void _toggleFullScreenMode(long var1);

   public void toggleFullScreen() {
      this.execute(this::_toggleFullScreenMode);
   }

   public void setMenuBar(MenuBar var1) {
      CMenuBar var2 = (CMenuBar)LWToolkit.targetToPeer(var1);
      this.execute((var1x) -> {
         if (var2 != null) {
            var2.execute((var2x) -> {
               nativeSetNSWindowMenuBar(var1x, var2x);
            });
         } else {
            nativeSetNSWindowMenuBar(var1x, 0L);
         }

      });
   }

   public void dispose() {
      this.contentView.dispose();
      this.execute(CPlatformWindow::nativeDispose);
      access$401(this);
   }

   public FontMetrics getFontMetrics(Font var1) {
      (new RuntimeException("unimplemented")).printStackTrace();
      return null;
   }

   public Insets getInsets() {
      AtomicReference var1 = new AtomicReference();
      this.execute((var1x) -> {
         var1.set(nativeGetNSWindowInsets(var1x));
      });
      return var1.get() != null ? (Insets)var1.get() : new Insets(0, 0, 0, 0);
   }

   public Point getLocationOnScreen() {
      return new Point(this.nativeBounds.x, this.nativeBounds.y);
   }

   public GraphicsDevice getGraphicsDevice() {
      return this.contentView.getGraphicsDevice();
   }

   public SurfaceData getScreenSurface() {
      return null;
   }

   public SurfaceData replaceSurfaceData() {
      return this.contentView.replaceSurfaceData();
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      this.execute((var4x) -> {
         nativeSetNSWindowBounds(var4x, (double)var1, (double)var2, (double)var3, (double)var4);
      });
   }

   private boolean isMaximized() {
      if (this.undecorated) {
         return this.normalBounds != null;
      } else {
         return this.isZoomed;
      }
   }

   private void maximize() {
      if (this.peer != null && !this.isMaximized()) {
         if (!this.undecorated) {
            this.execute(CWrapper.NSWindow::zoom);
         } else {
            this.deliverZoom(true);
            LWCToolkit.flushNativeSelectors();
            this.normalBounds = this.peer.getBounds();
            GraphicsConfiguration var1 = this.getPeer().getGraphicsConfiguration();
            Insets var2 = ((CGraphicsDevice)var1.getDevice()).getScreenInsets();
            Rectangle var3 = var1.getBounds();
            this.setBounds(var3.x + var2.left, var3.y + var2.top, var3.width - var2.left - var2.right, var3.height - var2.top - var2.bottom);
         }

      }
   }

   private void unmaximize() {
      if (this.isMaximized()) {
         if (!this.undecorated) {
            this.execute(CWrapper.NSWindow::zoom);
         } else {
            this.deliverZoom(false);
            Rectangle var1 = this.normalBounds;
            this.normalBounds = null;
            this.setBounds(var1.x, var1.y, var1.width, var1.height);
         }

      }
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.updateIconImages();
      this.updateFocusabilityForAutoRequestFocus(false);
      boolean var2 = this.isMaximized();
      LWWindowPeer var3 = this.peer == null ? null : this.peer.getBlocker();
      if (var3 != null && var1) {
         CPlatformWindow var10 = (CPlatformWindow)var3.getPlatformWindow();
         var10.execute((var1x) -> {
            this.execute((var2) -> {
               CWrapper.NSWindow.orderWindow(var2, -1, var1x);
            });
         });
      } else if (var1) {
         this.contentView.execute((var1x) -> {
            this.execute((var2) -> {
               CWrapper.NSWindow.makeFirstResponder(var2, var1x);
            });
         });
         boolean var4 = this.target.getType() == Window.Type.POPUP;
         this.execute((var2x) -> {
            if (var4) {
               CWrapper.NSWindow.orderFrontRegardless(var2x);
            } else {
               CWrapper.NSWindow.orderFront(var2x);
            }

            boolean var4x = CWrapper.NSWindow.isKeyWindow(var2x);
            if (!var4x) {
               CWrapper.NSWindow.makeKeyWindow(var2x);
            }

            if (this.owner != null && this.owner.getPeer() instanceof LWLightweightFramePeer) {
               LWLightweightFramePeer var5 = (LWLightweightFramePeer)this.owner.getPeer();
               long var6 = var5.getOverriddenWindowHandle();
               if (var6 != 0L) {
                  CWrapper.NSWindow.addChildWindow(var6, var2x, 1);
               }
            }

         });
      } else {
         this.execute((var0) -> {
            CWrapper.NSWindow.orderOut(var0);
            CWrapper.NSWindow.close(var0);
         });
      }

      this.visible = var1;
      if (var1 && this.target instanceof Frame) {
         if (!var2 && this.isMaximized()) {
            this.deliverZoom(true);
         } else {
            int var11 = ((Frame)this.target).getExtendedState();
            if ((var11 & 1) != 0) {
               var11 = 1;
            }

            switch(var11) {
            case 1:
               this.execute(CWrapper.NSWindow::miniaturize);
               break;
            case 6:
               this.maximize();
               break;
            default:
               this.unmaximize();
            }
         }
      }

      nativeSynthesizeMouseEnteredExitedEvents();
      this.updateFocusabilityForAutoRequestFocus(true);
      if (var1) {
         if (this.owner != null && this.owner.isVisible()) {
            this.owner.execute((var1x) -> {
               this.execute((var2) -> {
                  CWrapper.NSWindow.orderWindow(var2, 1, var1x);
               });
            });
            this.applyWindowLevel(this.target);
         }

         Window[] var12 = this.target.getOwnedWindows();
         int var5 = var12.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Window var7 = var12[var6];
            WindowPeer var8 = (WindowPeer)var7.getPeer();
            if (var8 instanceof LWWindowPeer) {
               CPlatformWindow var9 = (CPlatformWindow)((LWWindowPeer)var8).getPlatformWindow();
               if (var9 != null && var9.isVisible()) {
                  var9.execute((var1x) -> {
                     this.execute((var2) -> {
                        CWrapper.NSWindow.orderWindow(var1x, 1, var2);
                     });
                  });
                  var9.applyWindowLevel(var7);
               }
            }
         }
      }

      if (var3 != null && var1) {
         ((CPlatformWindow)var3.getPlatformWindow()).orderAboveSiblings();
      }

   }

   public void setTitle(String var1) {
      this.execute((var1x) -> {
         nativeSetNSWindowTitle(var1x, var1);
      });
   }

   public void updateIconImages() {
      CImage var1 = this.getImageForTarget();
      this.execute((var1x) -> {
         if (var1 == null) {
            nativeSetNSWindowMinimizedIcon(var1x, 0L);
         } else {
            var1.execute((var2) -> {
               nativeSetNSWindowMinimizedIcon(var1x, var2);
            });
         }

      });
   }

   public SurfaceData getSurfaceData() {
      return this.contentView.getSurfaceData();
   }

   public void toBack() {
      this.execute(CPlatformWindow::nativePushNSWindowToBack);
   }

   public void toFront() {
      LWCToolkit var1 = (LWCToolkit)Toolkit.getDefaultToolkit();
      Window var2 = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      if (var2 != null && var2.getPeer() != null && ((LWWindowPeer)var2.getPeer()).getPeerType() == LWWindowPeer.PeerType.EMBEDDED_FRAME && !var1.isApplicationActive()) {
         var1.activateApplicationIgnoringOtherApps();
      }

      this.updateFocusabilityForAutoRequestFocus(false);
      this.execute(CPlatformWindow::nativePushNSWindowToFront);
      this.updateFocusabilityForAutoRequestFocus(true);
   }

   public void setResizable(boolean var1) {
      this.setStyleBits(512, var1);
   }

   public void setSizeConstraints(int var1, int var2, int var3, int var4) {
      this.execute((var4x) -> {
         nativeSetNSWindowMinMax(var4x, (double)var1, (double)var2, (double)var3, (double)var4);
      });
   }

   public boolean rejectFocusRequest(CausedFocusEvent.Cause var1) {
      if (var1 != CausedFocusEvent.Cause.MOUSE_EVENT && !((LWCToolkit)Toolkit.getDefaultToolkit()).isApplicationActive()) {
         focusLogger.fine("the app is inactive, so the request is rejected");
         return true;
      } else {
         return false;
      }
   }

   public boolean requestWindowFocus() {
      this.execute((var0) -> {
         if (CWrapper.NSWindow.canBecomeMainWindow(var0)) {
            CWrapper.NSWindow.makeMainWindow(var0);
         }

         CWrapper.NSWindow.makeKeyAndOrderFront(var0);
      });
      return true;
   }

   public boolean isActive() {
      AtomicBoolean var1 = new AtomicBoolean();
      this.execute((var1x) -> {
         var1.set(CWrapper.NSWindow.isKeyWindow(var1x));
      });
      return var1.get();
   }

   public void updateFocusableWindowState() {
      boolean var1 = this.isNativelyFocusableWindow();
      this.setStyleBits(12288, var1);
   }

   public Graphics transformGraphics(Graphics var1) {
      return var1;
   }

   public void setAlwaysOnTop(boolean var1) {
      this.setStyleBits(32768, var1);
   }

   public void setOpacity(float var1) {
      this.execute((var1x) -> {
         CWrapper.NSWindow.setAlphaValue(var1x, var1);
      });
   }

   public void setOpaque(boolean var1) {
      this.execute((var1x) -> {
         CWrapper.NSWindow.setOpaque(var1x, var1);
      });
      boolean var2 = this.peer == null ? false : this.peer.isTextured();
      if (!var2) {
         if (!var1) {
            this.execute((var0) -> {
               CWrapper.NSWindow.setBackgroundColor(var0, 0);
            });
         } else if (this.peer != null) {
            Color var3 = this.peer.getBackground();
            if (var3 != null) {
               int var4 = var3.getRGB();
               this.execute((var1x) -> {
                  CWrapper.NSWindow.setBackgroundColor(var1x, var4);
               });
            }
         }
      }

      SwingUtilities.invokeLater(this::invalidateShadow);
   }

   public void enterFullScreenMode() {
      this.isFullScreenMode = true;
      this.execute(CPlatformWindow::nativeEnterFullScreenMode);
   }

   public void exitFullScreenMode() {
      this.execute(CPlatformWindow::nativeExitFullScreenMode);
      this.isFullScreenMode = false;
   }

   public boolean isFullScreenMode() {
      return this.isFullScreenMode;
   }

   public void setWindowState(int var1) {
      if (this.peer != null && this.peer.isVisible()) {
         int var2 = this.peer.getState();
         if (var2 != var1) {
            if ((var1 & 1) != 0) {
               var1 = 1;
            }

            switch(var1) {
            case 0:
               if (var2 == 1) {
                  this.execute(CWrapper.NSWindow::deminiaturize);
               } else if (var2 == 6) {
                  this.unmaximize();
               }
               break;
            case 1:
               if (var2 == 6) {
                  this.unmaximize();
               }

               this.execute(CWrapper.NSWindow::miniaturize);
               break;
            case 6:
               if (var2 == 1) {
                  this.execute(CWrapper.NSWindow::deminiaturize);
               }

               this.maximize();
               break;
            default:
               throw new RuntimeException("Unknown window state: " + var1);
            }

         }
      }
   }

   public void setModalBlocked(boolean var1) {
      if (this.target.getModalExclusionType() != Dialog.ModalExclusionType.APPLICATION_EXCLUDE) {
         if (var1) {
            this.execute((var0) -> {
               nativeSynthesizeMouseEnteredExitedEvents(var0, 9);
            });
         }

         this.execute((var1x) -> {
            nativeSetEnabled(var1x, !var1);
         });
         this.checkBlockingAndOrder();
      }
   }

   public final void invalidateShadow() {
      this.execute((var0) -> {
         nativeRevalidateNSWindowShadow(var0);
      });
   }

   private CImage getImageForTarget() {
      CImage var1 = null;

      try {
         var1 = CImage.getCreator().createFromImages(this.target.getIconImages());
      } catch (Exception var3) {
      }

      return var1;
   }

   public LWWindowPeer getPeer() {
      return this.peer;
   }

   public boolean isUnderMouse() {
      return this.contentView.isUnderMouse();
   }

   public CPlatformView getContentView() {
      return this.contentView;
   }

   public long getLayerPtr() {
      return this.contentView.getWindowLayerPtr();
   }

   private void validateSurface() {
      SurfaceData var1 = this.getSurfaceData();
      if (var1 instanceof CGLSurfaceData) {
         ((CGLSurfaceData)var1).validate();
      }

   }

   void flushBuffers() {
      if (this.isVisible() && !this.nativeBounds.isEmpty() && !this.isFullScreenMode) {
         try {
            LWCToolkit.invokeAndWait((Runnable)(new Runnable() {
               public void run() {
               }
            }), this.target);
         } catch (InvocationTargetException var2) {
            var2.printStackTrace();
         }
      }

   }

   static long getNativeViewPtr(PlatformWindow var0) {
      long var1 = 0L;
      if (var0 instanceof CPlatformWindow) {
         var1 = ((CPlatformWindow)var0).getContentView().getAWTView();
      } else if (var0 instanceof CViewPlatformEmbeddedFrame) {
         var1 = ((CViewPlatformEmbeddedFrame)var0).getNSViewPtr();
      }

      return var1;
   }

   private void deliverWindowFocusEvent(boolean var1, CPlatformWindow var2) {
      if (var1 && !((LWCToolkit)Toolkit.getDefaultToolkit()).isApplicationActive()) {
         focusLogger.fine("the app is inactive, so the notification is ignored");
      } else {
         LWWindowPeer var3 = var2 == null ? null : var2.getPeer();
         this.responder.handleWindowFocusEvent(var1, var3);
      }
   }

   protected void deliverMoveResizeEvent(int var1, int var2, int var3, int var4, boolean var5) {
      AtomicBoolean var6 = new AtomicBoolean();
      this.execute((var1x) -> {
         var6.set(CWrapper.NSWindow.isZoomed(var1x));
      });
      this.isZoomed = var6.get();
      this.checkZoom();
      Rectangle var7 = this.nativeBounds;
      this.nativeBounds = new Rectangle(var1, var2, var3, var4);
      if (this.peer != null) {
         this.peer.notifyReshape(var1, var2, var3, var4);
         if (var5 && !var7.getSize().equals(this.nativeBounds.getSize()) || this.isFullScreenAnimationOn) {
            this.flushBuffers();
         }
      }

   }

   private void deliverWindowClosingEvent() {
      if (this.peer != null && this.peer.getBlocker() == null) {
         this.peer.postEvent(new WindowEvent(this.target, 201));
      }

   }

   private void deliverIconify(boolean var1) {
      if (this.peer != null) {
         this.peer.notifyIconify(var1);
      }

      if (var1) {
         this.isIconifyAnimationActive = false;
      }

   }

   private void deliverZoom(boolean var1) {
      if (this.peer != null) {
         this.peer.notifyZoom(var1);
      }

   }

   private void checkZoom() {
      if (this.target instanceof Frame && this.isVisible()) {
         Frame var1 = (Frame)this.target;
         if (var1.getExtendedState() != 6 && this.isMaximized()) {
            this.deliverZoom(true);
         } else if (var1.getExtendedState() == 6 && !this.isMaximized()) {
            this.deliverZoom(false);
         }
      }

   }

   private void deliverNCMouseDown() {
      if (this.peer != null) {
         this.peer.notifyNCMouseDown();
      }

   }

   private boolean isNativelyFocusableWindow() {
      if (this.peer == null) {
         return false;
      } else {
         return !this.peer.isSimpleWindow() && this.target.getFocusableWindowState();
      }
   }

   private boolean isBlocked() {
      LWWindowPeer var1 = this.peer != null ? this.peer.getBlocker() : null;
      return var1 != null;
   }

   private void updateFocusabilityForAutoRequestFocus(boolean var1) {
      if (!this.target.isAutoRequestFocus() && this.isNativelyFocusableWindow()) {
         this.setStyleBits(12288, var1);
      }
   }

   private boolean checkBlockingAndOrder() {
      LWWindowPeer var1 = this.peer == null ? null : this.peer.getBlocker();
      if (var1 == null) {
         return false;
      } else if (var1 instanceof CPrinterDialogPeer) {
         return true;
      } else {
         CPlatformWindow var2 = (CPlatformWindow)var1.getPlatformWindow();
         var2.orderAboveSiblings();
         var2.execute((var0) -> {
            CWrapper.NSWindow.orderFrontRegardless(var0);
            CWrapper.NSWindow.makeKeyAndOrderFront(var0);
            CWrapper.NSWindow.makeMainWindow(var0);
         });
         return true;
      }
   }

   private boolean isIconified() {
      boolean var1 = false;
      if (this.target instanceof Frame) {
         int var2 = ((Frame)this.target).getExtendedState();
         if ((var2 & 1) != 0) {
            var1 = true;
         }
      }

      return this.isIconifyAnimationActive || var1;
   }

   private boolean isOneOfOwnersOrSelf(CPlatformWindow var1) {
      while(var1 != null) {
         if (this == var1) {
            return true;
         }

         var1 = var1.owner;
      }

      return false;
   }

   private CPlatformWindow getRootOwner() {
      CPlatformWindow var1;
      for(var1 = this; var1.owner != null; var1 = var1.owner) {
      }

      return var1;
   }

   private void orderAboveSiblings() {
      CPlatformWindow var1 = this.getRootOwner();
      if (var1.isVisible() && !var1.isIconified()) {
         var1.execute(CWrapper.NSWindow::orderFront);
      }

      if (!var1.isIconified()) {
         AWTAccessor.WindowAccessor var2 = AWTAccessor.getWindowAccessor();
         this.orderAboveSiblingsImpl(var2.getOwnedWindows(var1.target));
      }

   }

   private void orderAboveSiblingsImpl(Window[] var1) {
      ArrayList var2 = new ArrayList();
      AWTAccessor.ComponentAccessor var3 = AWTAccessor.getComponentAccessor();
      AWTAccessor.WindowAccessor var4 = AWTAccessor.getWindowAccessor();
      Window[] var5 = var1;
      int var6 = var1.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Window var8 = var5[var7];
         boolean var9 = false;
         ComponentPeer var10 = var3.getPeer(var8);
         if (var10 instanceof LWWindowPeer) {
            CPlatformWindow var11 = (CPlatformWindow)((LWWindowPeer)var10).getPlatformWindow();
            var9 = this.isIconified();
            if (var11 != null && var11.isVisible() && !var9) {
               if (var11.isOneOfOwnersOrSelf(this)) {
                  var11.execute(CWrapper.NSWindow::orderFront);
               } else {
                  var11.owner.execute((var1x) -> {
                     var11.execute((var2) -> {
                        CWrapper.NSWindow.orderWindow(var2, 1, var1x);
                     });
                  });
               }

               var11.applyWindowLevel(var8);
            }
         }

         if (!var9) {
            var2.addAll(Arrays.asList(var4.getOwnedWindows(var8)));
         }
      }

      if (!var2.isEmpty()) {
         this.orderAboveSiblingsImpl((Window[])var2.toArray(new Window[0]));
      }

   }

   protected void applyWindowLevel(Window var1) {
      if (var1.isAlwaysOnTop() && var1.getType() != Window.Type.POPUP) {
         this.execute((var0) -> {
            CWrapper.NSWindow.setLevel(var0, 1);
         });
      } else if (var1.getType() == Window.Type.POPUP) {
         this.execute((var0) -> {
            CWrapper.NSWindow.setLevel(var0, 2);
         });
      }

   }

   private void windowWillMiniaturize() {
      this.isIconifyAnimationActive = true;
   }

   private void windowDidBecomeMain() {
      assert CThreading.assertAppKit();

      if (!this.checkBlockingAndOrder()) {
         this.orderAboveSiblings();
      }
   }

   private void windowWillEnterFullScreen() {
      this.isFullScreenAnimationOn = true;
   }

   private void windowDidEnterFullScreen() {
      this.isFullScreenAnimationOn = false;
   }

   private void windowWillExitFullScreen() {
      this.isFullScreenAnimationOn = true;
   }

   private void windowDidExitFullScreen() {
      this.isFullScreenAnimationOn = false;
   }

   // $FF: synthetic method
   static void access$401(CPlatformWindow var0) {
      var0.dispose();
   }
}
