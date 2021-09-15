package java.awt;

import java.awt.peer.SystemTrayPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;
import sun.security.util.SecurityConstants;

public class SystemTray {
   private static SystemTray systemTray;
   private int currentIconID = 0;
   private transient SystemTrayPeer peer;
   private static final TrayIcon[] EMPTY_TRAY_ARRAY = new TrayIcon[0];

   private SystemTray() {
      this.addNotify();
   }

   public static SystemTray getSystemTray() {
      checkSystemTrayAllowed();
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         initializeSystemTrayIfNeeded();
         if (!isSupported()) {
            throw new UnsupportedOperationException("The system tray is not supported on the current platform.");
         } else {
            return systemTray;
         }
      }
   }

   public static boolean isSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      if (var0 instanceof SunToolkit) {
         initializeSystemTrayIfNeeded();
         return ((SunToolkit)var0).isTraySupported();
      } else {
         return var0 instanceof HeadlessToolkit ? ((HeadlessToolkit)var0).isTraySupported() : false;
      }
   }

   public void add(TrayIcon var1) throws AWTException {
      if (var1 == null) {
         throw new NullPointerException("adding null TrayIcon");
      } else {
         TrayIcon[] var2 = null;
         TrayIcon[] var3 = null;
         Vector var4 = null;
         synchronized(this) {
            var2 = systemTray.getTrayIcons();
            var4 = (Vector)AppContext.getAppContext().get(TrayIcon.class);
            if (var4 == null) {
               var4 = new Vector(3);
               AppContext.getAppContext().put(TrayIcon.class, var4);
            } else if (var4.contains(var1)) {
               throw new IllegalArgumentException("adding TrayIcon that is already added");
            }

            var4.add(var1);
            var3 = systemTray.getTrayIcons();
            var1.setID(++this.currentIconID);
         }

         try {
            var1.addNotify();
         } catch (AWTException var7) {
            var4.remove(var1);
            throw var7;
         }

         this.firePropertyChange("trayIcons", var2, var3);
      }
   }

   public void remove(TrayIcon var1) {
      if (var1 != null) {
         TrayIcon[] var2 = null;
         TrayIcon[] var3 = null;
         synchronized(this) {
            var2 = systemTray.getTrayIcons();
            Vector var5 = (Vector)AppContext.getAppContext().get(TrayIcon.class);
            if (var5 == null || !var5.remove(var1)) {
               return;
            }

            var1.removeNotify();
            var3 = systemTray.getTrayIcons();
         }

         this.firePropertyChange("trayIcons", var2, var3);
      }
   }

   public TrayIcon[] getTrayIcons() {
      Vector var1 = (Vector)AppContext.getAppContext().get(TrayIcon.class);
      return var1 != null ? (TrayIcon[])((TrayIcon[])var1.toArray(new TrayIcon[var1.size()])) : EMPTY_TRAY_ARRAY;
   }

   public Dimension getTrayIconSize() {
      return this.peer.getTrayIconSize();
   }

   public synchronized void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null) {
         this.getCurrentChangeSupport().addPropertyChangeListener(var1, var2);
      }
   }

   public synchronized void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null) {
         this.getCurrentChangeSupport().removePropertyChangeListener(var1, var2);
      }
   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      return this.getCurrentChangeSupport().getPropertyChangeListeners(var1);
   }

   private void firePropertyChange(String var1, Object var2, Object var3) {
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         this.getCurrentChangeSupport().firePropertyChange(var1, var2, var3);
      }
   }

   private synchronized PropertyChangeSupport getCurrentChangeSupport() {
      PropertyChangeSupport var1 = (PropertyChangeSupport)AppContext.getAppContext().get(SystemTray.class);
      if (var1 == null) {
         var1 = new PropertyChangeSupport(this);
         AppContext.getAppContext().put(SystemTray.class, var1);
      }

      return var1;
   }

   synchronized void addNotify() {
      if (this.peer == null) {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         if (var1 instanceof SunToolkit) {
            this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
         } else if (var1 instanceof HeadlessToolkit) {
            this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
         }
      }

   }

   static void checkSystemTrayAllowed() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.AWT.ACCESS_SYSTEM_TRAY_PERMISSION);
      }

   }

   private static void initializeSystemTrayIfNeeded() {
      Class var0 = SystemTray.class;
      synchronized(SystemTray.class) {
         if (systemTray == null) {
            systemTray = new SystemTray();
         }

      }
   }

   static {
      AWTAccessor.setSystemTrayAccessor(new AWTAccessor.SystemTrayAccessor() {
         public void firePropertyChange(SystemTray var1, String var2, Object var3, Object var4) {
            var1.firePropertyChange(var2, var3, var4);
         }
      });
   }
}
