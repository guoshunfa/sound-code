package com.apple.eawt;

import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import java.beans.Beans;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JMenuBar;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;
import sun.lwawt.macosx.CPlatformWindow;

public class Application {
   static Application sApplication = null;
   final _AppEventHandler eventHandler = _AppEventHandler.getInstance();
   final _AppMenuBarHandler menuBarHandler = _AppMenuBarHandler.getInstance();
   final _AppDockIconHandler iconHandler = new _AppDockIconHandler();

   private static native void nativeInitializeApplicationDelegate();

   private static void checkSecurity() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("canProcessApplicationEvents"));
      }
   }

   public static Application getApplication() {
      checkSecurity();
      return sApplication;
   }

   /** @deprecated */
   @Deprecated
   public Application() {
      checkSecurity();
   }

   public void addAppEventListener(AppEventListener var1) {
      this.eventHandler.addListener(var1);
   }

   public void removeAppEventListener(AppEventListener var1) {
      this.eventHandler.removeListener(var1);
   }

   public void setAboutHandler(AboutHandler var1) {
      this.eventHandler.aboutDispatcher.setHandler(var1);
   }

   public void setPreferencesHandler(PreferencesHandler var1) {
      this.eventHandler.preferencesDispatcher.setHandler(var1);
   }

   public void setOpenFileHandler(OpenFilesHandler var1) {
      this.eventHandler.openFilesDispatcher.setHandler(var1);
   }

   public void setPrintFileHandler(PrintFilesHandler var1) {
      this.eventHandler.printFilesDispatcher.setHandler(var1);
   }

   public void setOpenURIHandler(OpenURIHandler var1) {
      this.eventHandler.openURIDispatcher.setHandler(var1);
   }

   public void setQuitHandler(QuitHandler var1) {
      this.eventHandler.quitDispatcher.setHandler(var1);
   }

   public void setQuitStrategy(QuitStrategy var1) {
      this.eventHandler.setDefaultQuitStrategy(var1);
   }

   public void enableSuddenTermination() {
      _AppMiscHandlers.enableSuddenTermination();
   }

   public void disableSuddenTermination() {
      _AppMiscHandlers.disableSuddenTermination();
   }

   public void requestForeground(boolean var1) {
      _AppMiscHandlers.requestActivation(var1);
   }

   public void requestUserAttention(boolean var1) {
      _AppMiscHandlers.requestUserAttention(var1);
   }

   public void openHelpViewer() {
      _AppMiscHandlers.openHelpViewer();
   }

   public void setDockMenu(PopupMenu var1) {
      this.iconHandler.setDockMenu(var1);
   }

   public PopupMenu getDockMenu() {
      return this.iconHandler.getDockMenu();
   }

   public void setDockIconImage(Image var1) {
      this.iconHandler.setDockIconImage(var1);
   }

   public Image getDockIconImage() {
      return this.iconHandler.getDockIconImage();
   }

   public void setDockIconBadge(String var1) {
      this.iconHandler.setDockIconBadge(var1);
   }

   public void setDefaultMenuBar(JMenuBar var1) {
      this.menuBarHandler.setDefaultMenuBar(var1);
   }

   public void requestToggleFullScreen(Window var1) {
      ComponentPeer var2 = var1.getPeer();
      if (var2 instanceof LWWindowPeer) {
         PlatformWindow var3 = ((LWWindowPeer)var2).getPlatformWindow();
         if (var3 instanceof CPlatformWindow) {
            ((CPlatformWindow)var3).toggleFullScreen();
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addApplicationListener(ApplicationListener var1) {
      this.eventHandler.legacyHandler.addLegacyAppListener(var1);
   }

   /** @deprecated */
   @Deprecated
   public void removeApplicationListener(ApplicationListener var1) {
      this.eventHandler.legacyHandler.removeLegacyAppListener(var1);
   }

   /** @deprecated */
   @Deprecated
   public void setEnabledPreferencesMenu(boolean var1) {
      this.menuBarHandler.setPreferencesMenuItemVisible(true);
      this.menuBarHandler.setPreferencesMenuItemEnabled(var1);
   }

   /** @deprecated */
   @Deprecated
   public void setEnabledAboutMenu(boolean var1) {
      this.menuBarHandler.setAboutMenuItemEnabled(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean getEnabledPreferencesMenu() {
      return this.menuBarHandler.isPreferencesMenuItemEnabled();
   }

   /** @deprecated */
   @Deprecated
   public boolean getEnabledAboutMenu() {
      return this.menuBarHandler.isAboutMenuItemEnabled();
   }

   /** @deprecated */
   @Deprecated
   public boolean isAboutMenuItemPresent() {
      return this.menuBarHandler.isAboutMenuItemVisible();
   }

   /** @deprecated */
   @Deprecated
   public void addAboutMenuItem() {
      this.menuBarHandler.setAboutMenuItemVisible(true);
   }

   /** @deprecated */
   @Deprecated
   public void removeAboutMenuItem() {
      this.menuBarHandler.setAboutMenuItemVisible(false);
   }

   /** @deprecated */
   @Deprecated
   public boolean isPreferencesMenuItemPresent() {
      return this.menuBarHandler.isPreferencesMenuItemVisible();
   }

   /** @deprecated */
   @Deprecated
   public void addPreferencesMenuItem() {
      this.menuBarHandler.setPreferencesMenuItemVisible(true);
   }

   /** @deprecated */
   @Deprecated
   public void removePreferencesMenuItem() {
      this.menuBarHandler.setPreferencesMenuItemVisible(false);
   }

   /** @deprecated */
   @Deprecated
   public static Point getMouseLocationOnScreen() {
      return MouseInfo.getPointerInfo().getLocation();
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
      checkSecurity();
      if (!Beans.isDesignTime()) {
         nativeInitializeApplicationDelegate();
      }

      sApplication = new Application();
   }
}
