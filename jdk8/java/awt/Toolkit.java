package java.awt;

import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.event.AWTEventListenerProxy;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.NullComponentPeer;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.UngrabEvent;
import sun.security.util.SecurityConstants;
import sun.util.CoreResourceBundleControl;

public abstract class Toolkit {
   private static LightweightPeer lightweightMarker;
   private static Toolkit toolkit;
   private static String atNames;
   private static ResourceBundle resources;
   private static ResourceBundle platformResources;
   private static boolean loaded = false;
   protected final Map<String, Object> desktopProperties = new HashMap();
   protected final PropertyChangeSupport desktopPropsSupport = createPropertyChangeSupport(this);
   private static final int LONG_BITS = 64;
   private int[] calls = new int[64];
   private static volatile long enabledOnToolkitMask;
   private AWTEventListener eventListener = null;
   private WeakHashMap<AWTEventListener, Toolkit.SelectiveAWTEventListener> listener2SelectiveListener = new WeakHashMap();

   protected abstract DesktopPeer createDesktopPeer(Desktop var1) throws HeadlessException;

   protected abstract ButtonPeer createButton(Button var1) throws HeadlessException;

   protected abstract TextFieldPeer createTextField(TextField var1) throws HeadlessException;

   protected abstract LabelPeer createLabel(Label var1) throws HeadlessException;

   protected abstract ListPeer createList(List var1) throws HeadlessException;

   protected abstract CheckboxPeer createCheckbox(Checkbox var1) throws HeadlessException;

   protected abstract ScrollbarPeer createScrollbar(Scrollbar var1) throws HeadlessException;

   protected abstract ScrollPanePeer createScrollPane(ScrollPane var1) throws HeadlessException;

   protected abstract TextAreaPeer createTextArea(TextArea var1) throws HeadlessException;

   protected abstract ChoicePeer createChoice(Choice var1) throws HeadlessException;

   protected abstract FramePeer createFrame(Frame var1) throws HeadlessException;

   protected abstract CanvasPeer createCanvas(Canvas var1);

   protected abstract PanelPeer createPanel(Panel var1);

   protected abstract WindowPeer createWindow(Window var1) throws HeadlessException;

   protected abstract DialogPeer createDialog(Dialog var1) throws HeadlessException;

   protected abstract MenuBarPeer createMenuBar(MenuBar var1) throws HeadlessException;

   protected abstract MenuPeer createMenu(Menu var1) throws HeadlessException;

   protected abstract PopupMenuPeer createPopupMenu(PopupMenu var1) throws HeadlessException;

   protected abstract MenuItemPeer createMenuItem(MenuItem var1) throws HeadlessException;

   protected abstract FileDialogPeer createFileDialog(FileDialog var1) throws HeadlessException;

   protected abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) throws HeadlessException;

   protected MouseInfoPeer getMouseInfoPeer() {
      throw new UnsupportedOperationException("Not implemented");
   }

   protected LightweightPeer createComponent(Component var1) {
      if (lightweightMarker == null) {
         lightweightMarker = new NullComponentPeer();
      }

      return lightweightMarker;
   }

   /** @deprecated */
   @Deprecated
   protected abstract FontPeer getFontPeer(String var1, int var2);

   protected void loadSystemColors(int[] var1) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
   }

   public void setDynamicLayout(boolean var1) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      if (this != getDefaultToolkit()) {
         getDefaultToolkit().setDynamicLayout(var1);
      }

   }

   protected boolean isDynamicLayoutSet() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return this != getDefaultToolkit() ? getDefaultToolkit().isDynamicLayoutSet() : false;
   }

   public boolean isDynamicLayoutActive() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return this != getDefaultToolkit() ? getDefaultToolkit().isDynamicLayoutActive() : false;
   }

   public abstract Dimension getScreenSize() throws HeadlessException;

   public abstract int getScreenResolution() throws HeadlessException;

   public Insets getScreenInsets(GraphicsConfiguration var1) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return this != getDefaultToolkit() ? getDefaultToolkit().getScreenInsets(var1) : new Insets(0, 0, 0, 0);
   }

   public abstract ColorModel getColorModel() throws HeadlessException;

   /** @deprecated */
   @Deprecated
   public abstract String[] getFontList();

   /** @deprecated */
   @Deprecated
   public abstract FontMetrics getFontMetrics(Font var1);

   public abstract void sync();

   private static void initAssistiveTechnologies() {
      final String var0 = File.separator;
      final Properties var1 = new Properties();
      atNames = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            File var1x;
            FileInputStream var2;
            try {
               var1x = new File(System.getProperty("user.home") + var0 + ".accessibility.properties");
               var2 = new FileInputStream(var1x);
               var1.load((InputStream)var2);
               var2.close();
            } catch (Exception var4) {
            }

            if (var1.size() == 0) {
               try {
                  var1x = new File(System.getProperty("java.home") + var0 + "lib" + var0 + "accessibility.properties");
                  var2 = new FileInputStream(var1x);
                  var1.load((InputStream)var2);
                  var2.close();
               } catch (Exception var3) {
               }
            }

            String var5 = System.getProperty("javax.accessibility.screen_magnifier_present");
            if (var5 == null) {
               var5 = var1.getProperty("screen_magnifier_present", (String)null);
               if (var5 != null) {
                  System.setProperty("javax.accessibility.screen_magnifier_present", var5);
               }
            }

            String var6 = System.getProperty("javax.accessibility.assistive_technologies");
            if (var6 == null) {
               var6 = var1.getProperty("assistive_technologies", (String)null);
               if (var6 != null) {
                  System.setProperty("javax.accessibility.assistive_technologies", var6);
               }
            }

            return var6;
         }
      });
   }

   private static void loadAssistiveTechnologies() {
      if (atNames != null) {
         ClassLoader var0 = ClassLoader.getSystemClassLoader();
         StringTokenizer var1 = new StringTokenizer(atNames, " ,");

         while(var1.hasMoreTokens()) {
            String var2 = var1.nextToken();

            try {
               Class var3;
               if (var0 != null) {
                  var3 = var0.loadClass(var2);
               } else {
                  var3 = Class.forName(var2);
               }

               var3.newInstance();
            } catch (ClassNotFoundException var4) {
               throw new AWTError("Assistive Technology not found: " + var2);
            } catch (InstantiationException var5) {
               throw new AWTError("Could not instantiate Assistive Technology: " + var2);
            } catch (IllegalAccessException var6) {
               throw new AWTError("Could not access Assistive Technology: " + var2);
            } catch (Exception var7) {
               throw new AWTError("Error trying to install Assistive Technology: " + var2 + " " + var7);
            }
         }
      }

   }

   public static synchronized Toolkit getDefaultToolkit() {
      if (toolkit == null) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Class var1 = null;
               String var2 = System.getProperty("awt.toolkit");

               try {
                  var1 = Class.forName(var2);
               } catch (ClassNotFoundException var9) {
                  ClassLoader var4 = ClassLoader.getSystemClassLoader();
                  if (var4 != null) {
                     try {
                        var1 = var4.loadClass(var2);
                     } catch (ClassNotFoundException var8) {
                        throw new AWTError("Toolkit not found: " + var2);
                     }
                  }
               }

               try {
                  if (var1 != null) {
                     Toolkit.toolkit = (Toolkit)var1.newInstance();
                     if (GraphicsEnvironment.isHeadless()) {
                        Toolkit.toolkit = new HeadlessToolkit(Toolkit.toolkit);
                     }
                  }

                  return null;
               } catch (InstantiationException var6) {
                  throw new AWTError("Could not instantiate Toolkit: " + var2);
               } catch (IllegalAccessException var7) {
                  throw new AWTError("Could not access Toolkit: " + var2);
               }
            }
         });
         loadAssistiveTechnologies();
      }

      return toolkit;
   }

   public abstract Image getImage(String var1);

   public abstract Image getImage(URL var1);

   public abstract Image createImage(String var1);

   public abstract Image createImage(URL var1);

   public abstract boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4);

   public abstract int checkImage(Image var1, int var2, int var3, ImageObserver var4);

   public abstract Image createImage(ImageProducer var1);

   public Image createImage(byte[] var1) {
      return this.createImage(var1, 0, var1.length);
   }

   public abstract Image createImage(byte[] var1, int var2, int var3);

   public abstract PrintJob getPrintJob(Frame var1, String var2, Properties var3);

   public PrintJob getPrintJob(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      return this != getDefaultToolkit() ? getDefaultToolkit().getPrintJob(var1, var2, var3, var4) : this.getPrintJob(var1, var2, (Properties)null);
   }

   public abstract void beep();

   public abstract Clipboard getSystemClipboard() throws HeadlessException;

   public Clipboard getSystemSelection() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      if (this != getDefaultToolkit()) {
         return getDefaultToolkit().getSystemSelection();
      } else {
         GraphicsEnvironment.checkHeadless();
         return null;
      }
   }

   public int getMenuShortcutKeyMask() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return 2;
   }

   public boolean getLockingKeyState(int var1) throws UnsupportedOperationException {
      GraphicsEnvironment.checkHeadless();
      if (var1 != 20 && var1 != 144 && var1 != 145 && var1 != 262) {
         throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
      } else {
         throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
      }
   }

   public void setLockingKeyState(int var1, boolean var2) throws UnsupportedOperationException {
      GraphicsEnvironment.checkHeadless();
      if (var1 != 20 && var1 != 144 && var1 != 145 && var1 != 262) {
         throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
      } else {
         throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
      }
   }

   protected static Container getNativeContainer(Component var0) {
      return var0.getNativeContainer();
   }

   public Cursor createCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException, HeadlessException {
      return this != getDefaultToolkit() ? getDefaultToolkit().createCustomCursor(var1, var2, var3) : new Cursor(0);
   }

   public Dimension getBestCursorSize(int var1, int var2) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return this != getDefaultToolkit() ? getDefaultToolkit().getBestCursorSize(var1, var2) : new Dimension(0, 0);
   }

   public int getMaximumCursorColors() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return this != getDefaultToolkit() ? getDefaultToolkit().getMaximumCursorColors() : 0;
   }

   public boolean isFrameStateSupported(int var1) throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      if (this != getDefaultToolkit()) {
         return getDefaultToolkit().isFrameStateSupported(var1);
      } else {
         return var1 == 0;
      }
   }

   private static void setPlatformResources(ResourceBundle var0) {
      platformResources = var0;
   }

   private static native void initIDs();

   static void loadLibraries() {
      if (!loaded) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.loadLibrary("awt");
               return null;
            }
         });
         loaded = true;
      }

   }

   public static String getProperty(String var0, String var1) {
      if (platformResources != null) {
         try {
            return platformResources.getString(var0);
         } catch (MissingResourceException var4) {
         }
      }

      if (resources != null) {
         try {
            return resources.getString(var0);
         } catch (MissingResourceException var3) {
         }
      }

      return var1;
   }

   public final EventQueue getSystemEventQueue() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
      }

      return this.getSystemEventQueueImpl();
   }

   protected abstract EventQueue getSystemEventQueueImpl();

   static EventQueue getEventQueue() {
      return getDefaultToolkit().getSystemEventQueueImpl();
   }

   public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException;

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      return null;
   }

   public final synchronized Object getDesktopProperty(String var1) {
      if (this instanceof HeadlessToolkit) {
         return ((HeadlessToolkit)this).getUnderlyingToolkit().getDesktopProperty(var1);
      } else {
         if (this.desktopProperties.isEmpty()) {
            this.initializeDesktopProperties();
         }

         if (var1.equals("awt.dynamicLayoutSupported")) {
            return getDefaultToolkit().lazilyLoadDesktopProperty(var1);
         } else {
            Object var2 = this.desktopProperties.get(var1);
            if (var2 == null) {
               var2 = this.lazilyLoadDesktopProperty(var1);
               if (var2 != null) {
                  this.setDesktopProperty(var1, var2);
               }
            }

            if (var2 instanceof RenderingHints) {
               var2 = ((RenderingHints)var2).clone();
            }

            return var2;
         }
      }
   }

   protected final void setDesktopProperty(String var1, Object var2) {
      if (this instanceof HeadlessToolkit) {
         ((HeadlessToolkit)this).getUnderlyingToolkit().setDesktopProperty(var1, var2);
      } else {
         Object var3;
         synchronized(this) {
            var3 = this.desktopProperties.get(var1);
            this.desktopProperties.put(var1, var2);
         }

         if (var3 != null || var2 != null) {
            this.desktopPropsSupport.firePropertyChange(var1, var3, var2);
         }

      }
   }

   protected Object lazilyLoadDesktopProperty(String var1) {
      return null;
   }

   protected void initializeDesktopProperties() {
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.desktopPropsSupport.addPropertyChangeListener(var1, var2);
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.desktopPropsSupport.removePropertyChangeListener(var1, var2);
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return this.desktopPropsSupport.getPropertyChangeListeners();
   }

   public PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      return this.desktopPropsSupport.getPropertyChangeListeners(var1);
   }

   public boolean isAlwaysOnTopSupported() {
      return true;
   }

   public abstract boolean isModalityTypeSupported(Dialog.ModalityType var1);

   public abstract boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType var1);

   private static AWTEventListener deProxyAWTEventListener(AWTEventListener var0) {
      AWTEventListener var1 = var0;
      if (var0 == null) {
         return null;
      } else {
         if (var0 instanceof AWTEventListenerProxy) {
            var1 = (AWTEventListener)((AWTEventListenerProxy)var0).getListener();
         }

         return var1;
      }
   }

   public void addAWTEventListener(AWTEventListener var1, long var2) {
      AWTEventListener var4 = deProxyAWTEventListener(var1);
      if (var4 != null) {
         SecurityManager var5 = System.getSecurityManager();
         if (var5 != null) {
            var5.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
         }

         synchronized(this) {
            Toolkit.SelectiveAWTEventListener var7 = (Toolkit.SelectiveAWTEventListener)this.listener2SelectiveListener.get(var4);
            if (var7 == null) {
               var7 = new Toolkit.SelectiveAWTEventListener(var4, var2);
               this.listener2SelectiveListener.put(var4, var7);
               this.eventListener = Toolkit.ToolkitEventMulticaster.add(this.eventListener, var7);
            }

            var7.orEventMasks(var2);
            enabledOnToolkitMask |= var2;
            long var8 = var2;

            for(int var10 = 0; var10 < 64 && var8 != 0L; ++var10) {
               if ((var8 & 1L) != 0L) {
                  int var10002 = this.calls[var10]++;
               }

               var8 >>>= 1;
            }

         }
      }
   }

   public void removeAWTEventListener(AWTEventListener var1) {
      AWTEventListener var2 = deProxyAWTEventListener(var1);
      if (var1 != null) {
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            var3.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
         }

         synchronized(this) {
            Toolkit.SelectiveAWTEventListener var5 = (Toolkit.SelectiveAWTEventListener)this.listener2SelectiveListener.get(var2);
            if (var5 != null) {
               this.listener2SelectiveListener.remove(var2);
               int[] var6 = var5.getCalls();

               for(int var7 = 0; var7 < 64; ++var7) {
                  int[] var10000 = this.calls;
                  var10000[var7] -= var6[var7];

                  assert this.calls[var7] >= 0 : "Negative Listeners count";

                  if (this.calls[var7] == 0) {
                     enabledOnToolkitMask &= ~(1L << var7);
                  }
               }
            }

            this.eventListener = Toolkit.ToolkitEventMulticaster.remove(this.eventListener, (AWTEventListener)(var5 == null ? var2 : var5));
         }
      }
   }

   static boolean enabledOnToolkit(long var0) {
      return (enabledOnToolkitMask & var0) != 0L;
   }

   synchronized int countAWTEventListeners(long var1) {
      int var3;
      for(var3 = 0; var1 != 0L; ++var3) {
         var1 >>>= 1;
      }

      --var3;
      return this.calls[var3];
   }

   public AWTEventListener[] getAWTEventListeners() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
      }

      synchronized(this) {
         EventListener[] var3 = Toolkit.ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
         AWTEventListener[] var4 = new AWTEventListener[var3.length];

         for(int var5 = 0; var5 < var3.length; ++var5) {
            Toolkit.SelectiveAWTEventListener var6 = (Toolkit.SelectiveAWTEventListener)var3[var5];
            AWTEventListener var7 = var6.getListener();
            var4[var5] = new AWTEventListenerProxy(var6.getEventMask(), var7);
         }

         return var4;
      }
   }

   public AWTEventListener[] getAWTEventListeners(long var1) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
      }

      synchronized(this) {
         EventListener[] var5 = Toolkit.ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
         ArrayList var6 = new ArrayList(var5.length);

         for(int var7 = 0; var7 < var5.length; ++var7) {
            Toolkit.SelectiveAWTEventListener var8 = (Toolkit.SelectiveAWTEventListener)var5[var7];
            if ((var8.getEventMask() & var1) == var1) {
               var6.add(new AWTEventListenerProxy(var8.getEventMask(), var8.getListener()));
            }
         }

         return (AWTEventListener[])var6.toArray(new AWTEventListener[0]);
      }
   }

   void notifyAWTEventListeners(AWTEvent var1) {
      if (this instanceof HeadlessToolkit) {
         ((HeadlessToolkit)this).getUnderlyingToolkit().notifyAWTEventListeners(var1);
      } else {
         AWTEventListener var2 = this.eventListener;
         if (var2 != null) {
            var2.eventDispatched(var1);
         }

      }
   }

   public abstract Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight var1) throws HeadlessException;

   private static PropertyChangeSupport createPropertyChangeSupport(Toolkit var0) {
      return (PropertyChangeSupport)(!(var0 instanceof SunToolkit) && !(var0 instanceof HeadlessToolkit) ? new PropertyChangeSupport(var0) : new Toolkit.DesktopPropertyChangeSupport(var0));
   }

   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      return getDefaultToolkit().areExtraMouseButtonsEnabled();
   }

   static {
      AWTAccessor.setToolkitAccessor(new AWTAccessor.ToolkitAccessor() {
         public void setPlatformResources(ResourceBundle var1) {
            Toolkit.setPlatformResources(var1);
         }
      });
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            try {
               Toolkit.resources = ResourceBundle.getBundle("sun.awt.resources.awt", Locale.getDefault(), ClassLoader.getSystemClassLoader(), CoreResourceBundleControl.getRBControlInstance());
            } catch (MissingResourceException var2) {
            }

            return null;
         }
      });
      loadLibraries();
      initAssistiveTechnologies();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }

   private static class DesktopPropertyChangeSupport extends PropertyChangeSupport {
      private static final StringBuilder PROP_CHANGE_SUPPORT_KEY = new StringBuilder("desktop property change support key");
      private final Object source;

      public DesktopPropertyChangeSupport(Object var1) {
         super(var1);
         this.source = var1;
      }

      public synchronized void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
         PropertyChangeSupport var3 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         if (null == var3) {
            var3 = new PropertyChangeSupport(this.source);
            AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, var3);
         }

         var3.addPropertyChangeListener(var1, var2);
      }

      public synchronized void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
         PropertyChangeSupport var3 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         if (null != var3) {
            var3.removePropertyChangeListener(var1, var2);
         }

      }

      public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
         PropertyChangeSupport var1 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         return null != var1 ? var1.getPropertyChangeListeners() : new PropertyChangeListener[0];
      }

      public synchronized PropertyChangeListener[] getPropertyChangeListeners(String var1) {
         PropertyChangeSupport var2 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         return null != var2 ? var2.getPropertyChangeListeners(var1) : new PropertyChangeListener[0];
      }

      public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
         PropertyChangeSupport var2 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         if (null == var2) {
            var2 = new PropertyChangeSupport(this.source);
            AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, var2);
         }

         var2.addPropertyChangeListener(var1);
      }

      public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
         PropertyChangeSupport var2 = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
         if (null != var2) {
            var2.removePropertyChangeListener(var1);
         }

      }

      public void firePropertyChange(final PropertyChangeEvent var1) {
         Object var2 = var1.getOldValue();
         Object var3 = var1.getNewValue();
         String var4 = var1.getPropertyName();
         if (var2 == null || var3 == null || !var2.equals(var3)) {
            Runnable var5 = new Runnable() {
               public void run() {
                  PropertyChangeSupport var1x = (PropertyChangeSupport)AppContext.getAppContext().get(Toolkit.DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
                  if (null != var1x) {
                     var1x.firePropertyChange(var1);
                  }

               }
            };
            AppContext var6 = AppContext.getAppContext();
            Iterator var7 = AppContext.getAppContexts().iterator();

            while(var7.hasNext()) {
               AppContext var8 = (AppContext)var7.next();
               if (null != var8 && !var8.isDisposed()) {
                  if (var6 == var8) {
                     var5.run();
                  } else {
                     PeerEvent var9 = new PeerEvent(this.source, var5, 2L);
                     SunToolkit.postEvent(var8, var9);
                  }
               }
            }

         }
      }
   }

   private class SelectiveAWTEventListener implements AWTEventListener {
      AWTEventListener listener;
      private long eventMask;
      int[] calls = new int[64];

      public AWTEventListener getListener() {
         return this.listener;
      }

      public long getEventMask() {
         return this.eventMask;
      }

      public int[] getCalls() {
         return this.calls;
      }

      public void orEventMasks(long var1) {
         this.eventMask |= var1;

         for(int var3 = 0; var3 < 64 && var1 != 0L; ++var3) {
            if ((var1 & 1L) != 0L) {
               int var10002 = this.calls[var3]++;
            }

            var1 >>>= 1;
         }

      }

      SelectiveAWTEventListener(AWTEventListener var2, long var3) {
         this.listener = var2;
         this.eventMask = var3;
      }

      public void eventDispatched(AWTEvent var1) {
         long var2 = 0L;
         if ((var2 = this.eventMask & 1L) != 0L && var1.id >= 100 && var1.id <= 103 || (var2 = this.eventMask & 2L) != 0L && var1.id >= 300 && var1.id <= 301 || (var2 = this.eventMask & 4L) != 0L && var1.id >= 1004 && var1.id <= 1005 || (var2 = this.eventMask & 8L) != 0L && var1.id >= 400 && var1.id <= 402 || (var2 = this.eventMask & 131072L) != 0L && var1.id == 507 || (var2 = this.eventMask & 32L) != 0L && (var1.id == 503 || var1.id == 506) || (var2 = this.eventMask & 16L) != 0L && var1.id != 503 && var1.id != 506 && var1.id != 507 && var1.id >= 500 && var1.id <= 507 || (var2 = this.eventMask & 64L) != 0L && var1.id >= 200 && var1.id <= 209 || (var2 = this.eventMask & 128L) != 0L && var1.id >= 1001 && var1.id <= 1001 || (var2 = this.eventMask & 256L) != 0L && var1.id >= 601 && var1.id <= 601 || (var2 = this.eventMask & 512L) != 0L && var1.id >= 701 && var1.id <= 701 || (var2 = this.eventMask & 1024L) != 0L && var1.id >= 900 && var1.id <= 900 || (var2 = this.eventMask & 2048L) != 0L && var1.id >= 1100 && var1.id <= 1101 || (var2 = this.eventMask & 8192L) != 0L && var1.id >= 800 && var1.id <= 801 || (var2 = this.eventMask & 16384L) != 0L && var1.id >= 1200 && var1.id <= 1200 || (var2 = this.eventMask & 32768L) != 0L && var1.id == 1400 || (var2 = this.eventMask & 65536L) != 0L && (var1.id == 1401 || var1.id == 1402) || (var2 = this.eventMask & 262144L) != 0L && var1.id == 209 || (var2 = this.eventMask & 524288L) != 0L && (var1.id == 207 || var1.id == 208) || (var2 = this.eventMask & -2147483648L) != 0L && var1 instanceof UngrabEvent) {
            int var4 = 0;

            for(long var5 = var2; var5 != 0L; ++var4) {
               var5 >>>= 1;
            }

            --var4;

            for(int var7 = 0; var7 < this.calls[var4]; ++var7) {
               this.listener.eventDispatched(var1);
            }
         }

      }
   }

   private static class ToolkitEventMulticaster extends AWTEventMulticaster implements AWTEventListener {
      ToolkitEventMulticaster(AWTEventListener var1, AWTEventListener var2) {
         super(var1, var2);
      }

      static AWTEventListener add(AWTEventListener var0, AWTEventListener var1) {
         if (var0 == null) {
            return var1;
         } else {
            return (AWTEventListener)(var1 == null ? var0 : new Toolkit.ToolkitEventMulticaster(var0, var1));
         }
      }

      static AWTEventListener remove(AWTEventListener var0, AWTEventListener var1) {
         return (AWTEventListener)removeInternal(var0, var1);
      }

      protected EventListener remove(EventListener var1) {
         if (var1 == this.a) {
            return this.b;
         } else if (var1 == this.b) {
            return this.a;
         } else {
            AWTEventListener var2 = (AWTEventListener)removeInternal(this.a, var1);
            AWTEventListener var3 = (AWTEventListener)removeInternal(this.b, var1);
            return (EventListener)(var2 == this.a && var3 == this.b ? this : add(var2, var3));
         }
      }

      public void eventDispatched(AWTEvent var1) {
         ((AWTEventListener)this.a).eventDispatched(var1);
         ((AWTEventListener)this.b).eventDispatched(var1);
      }
   }
}
