package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.OSInfo;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class UIManager implements Serializable {
   private static final Object classLock = new Object();
   private static final String defaultLAFKey = "swing.defaultlaf";
   private static final String auxiliaryLAFsKey = "swing.auxiliarylaf";
   private static final String multiplexingLAFKey = "swing.plaf.multiplexinglaf";
   private static final String installedLAFsKey = "swing.installedlafs";
   private static final String disableMnemonicKey = "swing.disablenavaids";
   private static UIManager.LookAndFeelInfo[] installedLAFs;

   private static UIManager.LAFState getLAFState() {
      UIManager.LAFState var0 = (UIManager.LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
      if (var0 == null) {
         synchronized(classLock) {
            var0 = (UIManager.LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
            if (var0 == null) {
               SwingUtilities.appContextPut(SwingUtilities2.LAF_STATE_KEY, var0 = new UIManager.LAFState());
            }
         }
      }

      return var0;
   }

   private static String makeInstalledLAFKey(String var0, String var1) {
      return "swing.installedlaf." + var0 + "." + var1;
   }

   private static String makeSwingPropertiesFilename() {
      String var0 = File.separator;
      String var1 = System.getProperty("java.home");
      if (var1 == null) {
         var1 = "<java.home undefined>";
      }

      return var1 + var0 + "lib" + var0 + "swing.properties";
   }

   public static UIManager.LookAndFeelInfo[] getInstalledLookAndFeels() {
      maybeInitialize();
      UIManager.LookAndFeelInfo[] var0 = getLAFState().installedLAFs;
      if (var0 == null) {
         var0 = installedLAFs;
      }

      UIManager.LookAndFeelInfo[] var1 = new UIManager.LookAndFeelInfo[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   public static void setInstalledLookAndFeels(UIManager.LookAndFeelInfo[] var0) throws SecurityException {
      maybeInitialize();
      UIManager.LookAndFeelInfo[] var1 = new UIManager.LookAndFeelInfo[var0.length];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      getLAFState().installedLAFs = var1;
   }

   public static void installLookAndFeel(UIManager.LookAndFeelInfo var0) {
      UIManager.LookAndFeelInfo[] var1 = getInstalledLookAndFeels();
      UIManager.LookAndFeelInfo[] var2 = new UIManager.LookAndFeelInfo[var1.length + 1];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      var2[var1.length] = var0;
      setInstalledLookAndFeels(var2);
   }

   public static void installLookAndFeel(String var0, String var1) {
      installLookAndFeel(new UIManager.LookAndFeelInfo(var0, var1));
   }

   public static LookAndFeel getLookAndFeel() {
      maybeInitialize();
      return getLAFState().lookAndFeel;
   }

   public static void setLookAndFeel(LookAndFeel var0) throws UnsupportedLookAndFeelException {
      if (var0 != null && !var0.isSupportedLookAndFeel()) {
         String var4 = var0.toString() + " not supported on this platform";
         throw new UnsupportedLookAndFeelException(var4);
      } else {
         UIManager.LAFState var1 = getLAFState();
         LookAndFeel var2 = var1.lookAndFeel;
         if (var2 != null) {
            var2.uninitialize();
         }

         var1.lookAndFeel = var0;
         if (var0 != null) {
            DefaultLookup.setDefaultLookup((DefaultLookup)null);
            var0.initialize();
            var1.setLookAndFeelDefaults(var0.getDefaults());
         } else {
            var1.setLookAndFeelDefaults((UIDefaults)null);
         }

         SwingPropertyChangeSupport var3 = var1.getPropertyChangeSupport(false);
         if (var3 != null) {
            var3.firePropertyChange("lookAndFeel", var2, var0);
         }

      }
   }

   public static void setLookAndFeel(String var0) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
      if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(var0)) {
         setLookAndFeel((LookAndFeel)(new MetalLookAndFeel()));
      } else {
         Class var1 = SwingUtilities.loadSystemClass(var0);
         setLookAndFeel((LookAndFeel)((LookAndFeel)var1.newInstance()));
      }

   }

   public static String getSystemLookAndFeelClassName() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.systemlaf")));
      if (var0 != null) {
         return var0;
      } else {
         OSInfo.OSType var1 = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
         if (var1 == OSInfo.OSType.WINDOWS) {
            return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
         } else {
            String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.desktop")));
            Toolkit var3 = Toolkit.getDefaultToolkit();
            if ("gnome".equals(var2) && var3 instanceof SunToolkit && ((SunToolkit)var3).isNativeGTKAvailable()) {
               return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else if (var1 == OSInfo.OSType.MACOSX && var3.getClass().getName().equals("sun.lwawt.macosx.LWCToolkit")) {
               return "com.apple.laf.AquaLookAndFeel";
            } else {
               return var1 == OSInfo.OSType.SOLARIS ? "com.sun.java.swing.plaf.motif.MotifLookAndFeel" : getCrossPlatformLookAndFeelClassName();
            }
         }
      }
   }

   public static String getCrossPlatformLookAndFeelClassName() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.crossplatformlaf")));
      return var0 != null ? var0 : "javax.swing.plaf.metal.MetalLookAndFeel";
   }

   public static UIDefaults getDefaults() {
      maybeInitialize();
      return getLAFState().multiUIDefaults;
   }

   public static Font getFont(Object var0) {
      return getDefaults().getFont(var0);
   }

   public static Font getFont(Object var0, Locale var1) {
      return getDefaults().getFont(var0, var1);
   }

   public static Color getColor(Object var0) {
      return getDefaults().getColor(var0);
   }

   public static Color getColor(Object var0, Locale var1) {
      return getDefaults().getColor(var0, var1);
   }

   public static Icon getIcon(Object var0) {
      return getDefaults().getIcon(var0);
   }

   public static Icon getIcon(Object var0, Locale var1) {
      return getDefaults().getIcon(var0, var1);
   }

   public static Border getBorder(Object var0) {
      return getDefaults().getBorder(var0);
   }

   public static Border getBorder(Object var0, Locale var1) {
      return getDefaults().getBorder(var0, var1);
   }

   public static String getString(Object var0) {
      return getDefaults().getString(var0);
   }

   public static String getString(Object var0, Locale var1) {
      return getDefaults().getString(var0, var1);
   }

   static String getString(Object var0, Component var1) {
      Locale var2 = var1 == null ? Locale.getDefault() : var1.getLocale();
      return getString(var0, var2);
   }

   public static int getInt(Object var0) {
      return getDefaults().getInt(var0);
   }

   public static int getInt(Object var0, Locale var1) {
      return getDefaults().getInt(var0, var1);
   }

   public static boolean getBoolean(Object var0) {
      return getDefaults().getBoolean(var0);
   }

   public static boolean getBoolean(Object var0, Locale var1) {
      return getDefaults().getBoolean(var0, var1);
   }

   public static Insets getInsets(Object var0) {
      return getDefaults().getInsets(var0);
   }

   public static Insets getInsets(Object var0, Locale var1) {
      return getDefaults().getInsets(var0, var1);
   }

   public static Dimension getDimension(Object var0) {
      return getDefaults().getDimension(var0);
   }

   public static Dimension getDimension(Object var0, Locale var1) {
      return getDefaults().getDimension(var0, var1);
   }

   public static Object get(Object var0) {
      return getDefaults().get(var0);
   }

   public static Object get(Object var0, Locale var1) {
      return getDefaults().get(var0, var1);
   }

   public static Object put(Object var0, Object var1) {
      return getDefaults().put(var0, var1);
   }

   public static ComponentUI getUI(JComponent var0) {
      maybeInitialize();
      maybeInitializeFocusPolicy(var0);
      ComponentUI var1 = null;
      LookAndFeel var2 = getLAFState().multiLookAndFeel;
      if (var2 != null) {
         var1 = var2.getDefaults().getUI(var0);
      }

      if (var1 == null) {
         var1 = getDefaults().getUI(var0);
      }

      return var1;
   }

   public static UIDefaults getLookAndFeelDefaults() {
      maybeInitialize();
      return getLAFState().getLookAndFeelDefaults();
   }

   private static LookAndFeel getMultiLookAndFeel() {
      LookAndFeel var0 = getLAFState().multiLookAndFeel;
      if (var0 == null) {
         String var1 = "javax.swing.plaf.multi.MultiLookAndFeel";
         String var2 = getLAFState().swingProps.getProperty("swing.plaf.multiplexinglaf", var1);

         try {
            Class var3 = SwingUtilities.loadSystemClass(var2);
            var0 = (LookAndFeel)var3.newInstance();
         } catch (Exception var4) {
            System.err.println("UIManager: failed loading " + var2);
         }
      }

      return var0;
   }

   public static void addAuxiliaryLookAndFeel(LookAndFeel var0) {
      maybeInitialize();
      if (var0.isSupportedLookAndFeel()) {
         Vector var1 = getLAFState().auxLookAndFeels;
         if (var1 == null) {
            var1 = new Vector();
         }

         if (!var1.contains(var0)) {
            var1.addElement(var0);
            var0.initialize();
            getLAFState().auxLookAndFeels = var1;
            if (getLAFState().multiLookAndFeel == null) {
               getLAFState().multiLookAndFeel = getMultiLookAndFeel();
            }
         }

      }
   }

   public static boolean removeAuxiliaryLookAndFeel(LookAndFeel var0) {
      maybeInitialize();
      Vector var2 = getLAFState().auxLookAndFeels;
      if (var2 != null && var2.size() != 0) {
         boolean var1 = var2.removeElement(var0);
         if (var1) {
            if (var2.size() == 0) {
               getLAFState().auxLookAndFeels = null;
               getLAFState().multiLookAndFeel = null;
            } else {
               getLAFState().auxLookAndFeels = var2;
            }
         }

         var0.uninitialize();
         return var1;
      } else {
         return false;
      }
   }

   public static LookAndFeel[] getAuxiliaryLookAndFeels() {
      maybeInitialize();
      Vector var0 = getLAFState().auxLookAndFeels;
      if (var0 != null && var0.size() != 0) {
         LookAndFeel[] var1 = new LookAndFeel[var0.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = (LookAndFeel)var0.elementAt(var2);
         }

         return var1;
      } else {
         return null;
      }
   }

   public static void addPropertyChangeListener(PropertyChangeListener var0) {
      synchronized(classLock) {
         getLAFState().getPropertyChangeSupport(true).addPropertyChangeListener(var0);
      }
   }

   public static void removePropertyChangeListener(PropertyChangeListener var0) {
      synchronized(classLock) {
         getLAFState().getPropertyChangeSupport(true).removePropertyChangeListener(var0);
      }
   }

   public static PropertyChangeListener[] getPropertyChangeListeners() {
      synchronized(classLock) {
         return getLAFState().getPropertyChangeSupport(true).getPropertyChangeListeners();
      }
   }

   private static Properties loadSwingProperties() {
      if (UIManager.class.getClassLoader() != null) {
         return new Properties();
      } else {
         final Properties var0 = new Properties();
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               OSInfo.OSType var1 = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
               if (var1 == OSInfo.OSType.MACOSX) {
                  var0.put("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
               }

               try {
                  File var2 = new File(UIManager.makeSwingPropertiesFilename());
                  if (var2.exists()) {
                     FileInputStream var3 = new FileInputStream(var2);
                     var0.load((InputStream)var3);
                     var3.close();
                  }
               } catch (Exception var4) {
               }

               UIManager.checkProperty(var0, "swing.defaultlaf");
               UIManager.checkProperty(var0, "swing.auxiliarylaf");
               UIManager.checkProperty(var0, "swing.plaf.multiplexinglaf");
               UIManager.checkProperty(var0, "swing.installedlafs");
               UIManager.checkProperty(var0, "swing.disablenavaids");
               return null;
            }
         });
         return var0;
      }
   }

   private static void checkProperty(Properties var0, String var1) {
      String var2 = System.getProperty(var1);
      if (var2 != null) {
         var0.put(var1, var2);
      }

   }

   private static void initializeInstalledLAFs(Properties var0) {
      String var1 = var0.getProperty("swing.installedlafs");
      if (var1 != null) {
         Vector var2 = new Vector();
         StringTokenizer var3 = new StringTokenizer(var1, ",", false);

         while(var3.hasMoreTokens()) {
            var2.addElement(var3.nextToken());
         }

         Vector var4 = new Vector(var2.size());
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            String var7 = var0.getProperty(makeInstalledLAFKey(var6, "name"), var6);
            String var8 = var0.getProperty(makeInstalledLAFKey(var6, "class"));
            if (var8 != null) {
               var4.addElement(new UIManager.LookAndFeelInfo(var7, var8));
            }
         }

         UIManager.LookAndFeelInfo[] var9 = new UIManager.LookAndFeelInfo[var4.size()];

         for(int var10 = 0; var10 < var4.size(); ++var10) {
            var9[var10] = (UIManager.LookAndFeelInfo)var4.elementAt(var10);
         }

         getLAFState().installedLAFs = var9;
      }
   }

   private static void initializeDefaultLAF(Properties var0) {
      if (getLAFState().lookAndFeel == null) {
         String var1 = null;
         HashMap var2 = (HashMap)AppContext.getAppContext().remove("swing.lafdata");
         if (var2 != null) {
            var1 = (String)var2.remove("defaultlaf");
         }

         if (var1 == null) {
            var1 = getCrossPlatformLookAndFeelClassName();
         }

         var1 = var0.getProperty("swing.defaultlaf", var1);

         try {
            setLookAndFeel(var1);
         } catch (Exception var5) {
            throw new Error("Cannot load " + var1);
         }

         if (var2 != null) {
            Iterator var3 = var2.keySet().iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               put(var4, var2.get(var4));
            }
         }

      }
   }

   private static void initializeAuxiliaryLAFs(Properties var0) {
      String var1 = var0.getProperty("swing.auxiliarylaf");
      if (var1 != null) {
         Vector var2 = new Vector();
         StringTokenizer var3 = new StringTokenizer(var1, ",");

         while(var3.hasMoreTokens()) {
            String var5 = var3.nextToken();

            try {
               Class var6 = SwingUtilities.loadSystemClass(var5);
               LookAndFeel var7 = (LookAndFeel)var6.newInstance();
               var7.initialize();
               var2.addElement(var7);
            } catch (Exception var8) {
               System.err.println("UIManager: failed loading auxiliary look and feel " + var5);
            }
         }

         if (var2.size() == 0) {
            var2 = null;
         } else {
            getLAFState().multiLookAndFeel = getMultiLookAndFeel();
            if (getLAFState().multiLookAndFeel == null) {
               var2 = null;
            }
         }

         getLAFState().auxLookAndFeels = var2;
      }
   }

   private static void initializeSystemDefaults(Properties var0) {
      getLAFState().swingProps = var0;
   }

   private static void maybeInitialize() {
      synchronized(classLock) {
         if (!getLAFState().initialized) {
            getLAFState().initialized = true;
            initialize();
         }

      }
   }

   private static void maybeInitializeFocusPolicy(JComponent var0) {
      if (var0 instanceof JRootPane) {
         synchronized(classLock) {
            if (!getLAFState().focusPolicyInitialized) {
               getLAFState().focusPolicyInitialized = true;
               if (FocusManager.isFocusManagerEnabled()) {
                  KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
               }
            }
         }
      }

   }

   private static void initialize() {
      Properties var0 = loadSwingProperties();
      initializeSystemDefaults(var0);
      initializeDefaultLAF(var0);
      initializeAuxiliaryLAFs(var0);
      initializeInstalledLAFs(var0);
      if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
         PaintEventDispatcher.setPaintEventDispatcher(new SwingPaintEventDispatcher());
      }

      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
         public boolean postProcessKeyEvent(KeyEvent var1) {
            Component var2 = var1.getComponent();
            if ((!(var2 instanceof JComponent) || var2 != null && !var2.isEnabled()) && JComponent.KeyboardState.shouldProcess(var1) && SwingUtilities.processKeyBindings(var1)) {
               var1.consume();
               return true;
            } else {
               return false;
            }
         }
      });
      AWTAccessor.getComponentAccessor().setRequestFocusController(JComponent.focusController);
   }

   static {
      ArrayList var0 = new ArrayList(4);
      var0.add(new UIManager.LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"));
      var0.add(new UIManager.LookAndFeelInfo("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"));
      var0.add(new UIManager.LookAndFeelInfo("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
      OSInfo.OSType var1 = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
      if (var1 == OSInfo.OSType.WINDOWS) {
         var0.add(new UIManager.LookAndFeelInfo("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
         if (Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive") != null) {
            var0.add(new UIManager.LookAndFeelInfo("Windows Classic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"));
         }
      } else if (var1 == OSInfo.OSType.MACOSX) {
         var0.add(new UIManager.LookAndFeelInfo("Mac OS X", "com.apple.laf.AquaLookAndFeel"));
      } else {
         var0.add(new UIManager.LookAndFeelInfo("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
      }

      installedLAFs = (UIManager.LookAndFeelInfo[])var0.toArray(new UIManager.LookAndFeelInfo[var0.size()]);
   }

   public static class LookAndFeelInfo {
      private String name;
      private String className;

      public LookAndFeelInfo(String var1, String var2) {
         this.name = var1;
         this.className = var2;
      }

      public String getName() {
         return this.name;
      }

      public String getClassName() {
         return this.className;
      }

      public String toString() {
         return this.getClass().getName() + "[" + this.getName() + " " + this.getClassName() + "]";
      }
   }

   private static class LAFState {
      Properties swingProps;
      private UIDefaults[] tables;
      boolean initialized;
      boolean focusPolicyInitialized;
      MultiUIDefaults multiUIDefaults;
      LookAndFeel lookAndFeel;
      LookAndFeel multiLookAndFeel;
      Vector<LookAndFeel> auxLookAndFeels;
      SwingPropertyChangeSupport changeSupport;
      UIManager.LookAndFeelInfo[] installedLAFs;

      private LAFState() {
         this.tables = new UIDefaults[2];
         this.initialized = false;
         this.focusPolicyInitialized = false;
         this.multiUIDefaults = new MultiUIDefaults(this.tables);
         this.multiLookAndFeel = null;
         this.auxLookAndFeels = null;
      }

      UIDefaults getLookAndFeelDefaults() {
         return this.tables[0];
      }

      void setLookAndFeelDefaults(UIDefaults var1) {
         this.tables[0] = var1;
      }

      UIDefaults getSystemDefaults() {
         return this.tables[1];
      }

      void setSystemDefaults(UIDefaults var1) {
         this.tables[1] = var1;
      }

      public synchronized SwingPropertyChangeSupport getPropertyChangeSupport(boolean var1) {
         if (var1 && this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(UIManager.class);
         }

         return this.changeSupport;
      }

      // $FF: synthetic method
      LAFState(Object var1) {
         this();
      }
   }
}
