package com.apple.eawt;

import java.awt.Toolkit;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class _AppEventLegacyHandler implements AboutHandler, PreferencesHandler, _OpenAppHandler, AppReOpenedListener, OpenFilesHandler, PrintFilesHandler, QuitHandler {
   final _AppEventHandler parent;
   final Vector<ApplicationListener> legacyAppListeners = new Vector();
   boolean blockLegacyAPI;
   boolean initializedParentDispatchers;

   _AppEventLegacyHandler(_AppEventHandler var1) {
      this.parent = var1;
   }

   void blockLegacyAPI() {
      this.blockLegacyAPI = true;
   }

   void checkIfLegacyAPIBlocked() {
      if (this.blockLegacyAPI) {
         throw new IllegalStateException("Cannot add com.apple.eawt.ApplicationListener after installing an app event handler");
      }
   }

   void addLegacyAppListener(ApplicationListener var1) {
      this.checkIfLegacyAPIBlocked();
      if (!this.initializedParentDispatchers) {
         _AppMenuBarHandler var2 = Application.getApplication().menuBarHandler;
         boolean var3 = var2.prefsMenuItemExplicitlySet;
         this.parent.aboutDispatcher.setHandler(this);
         this.parent.preferencesDispatcher.setHandler((PreferencesHandler)this);
         if (!var3) {
            var2.setPreferencesMenuItemVisible(false);
         }

         this.parent.openAppDispatcher.setHandler(this);
         this.parent.reOpenAppDispatcher.addListener(this);
         this.parent.openFilesDispatcher.setHandler(this);
         this.parent.printFilesDispatcher.setHandler(this);
         this.parent.quitDispatcher.setHandler(this);
         this.initializedParentDispatchers = true;
      }

      synchronized(this.legacyAppListeners) {
         this.legacyAppListeners.addElement(var1);
      }
   }

   public void removeLegacyAppListener(ApplicationListener var1) {
      this.checkIfLegacyAPIBlocked();
      synchronized(this.legacyAppListeners) {
         this.legacyAppListeners.removeElement(var1);
      }
   }

   public void handleAbout(AppEvent.AboutEvent var1) {
      final ApplicationEvent var2 = new ApplicationEvent(Toolkit.getDefaultToolkit());
      this.sendEventToEachListenerUntilHandled(var2, new _AppEventLegacyHandler.EventDispatcher() {
         public void dispatchEvent(ApplicationListener var1) {
            var1.handleAbout(var2);
         }
      });
      if (!var2.isHandled()) {
         this.parent.openCocoaAboutWindow();
      }
   }

   public void handlePreferences(AppEvent.PreferencesEvent var1) {
      final ApplicationEvent var2 = new ApplicationEvent(Toolkit.getDefaultToolkit());
      this.sendEventToEachListenerUntilHandled(var2, new _AppEventLegacyHandler.EventDispatcher() {
         public void dispatchEvent(ApplicationListener var1) {
            var1.handlePreferences(var2);
         }
      });
   }

   public void handleOpenApp() {
      final ApplicationEvent var1 = new ApplicationEvent(Toolkit.getDefaultToolkit());
      this.sendEventToEachListenerUntilHandled(var1, new _AppEventLegacyHandler.EventDispatcher() {
         public void dispatchEvent(ApplicationListener var1x) {
            var1x.handleOpenApplication(var1);
         }
      });
   }

   public void appReOpened(AppEvent.AppReOpenedEvent var1) {
      final ApplicationEvent var2 = new ApplicationEvent(Toolkit.getDefaultToolkit());
      this.sendEventToEachListenerUntilHandled(var2, new _AppEventLegacyHandler.EventDispatcher() {
         public void dispatchEvent(ApplicationListener var1) {
            var1.handleReOpenApplication(var2);
         }
      });
   }

   public void openFiles(AppEvent.OpenFilesEvent var1) {
      List var2 = var1.getFiles();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         File var4 = (File)var3.next();
         final ApplicationEvent var5 = new ApplicationEvent(Toolkit.getDefaultToolkit(), var4.getAbsolutePath());
         this.sendEventToEachListenerUntilHandled(var5, new _AppEventLegacyHandler.EventDispatcher() {
            public void dispatchEvent(ApplicationListener var1) {
               var1.handleOpenFile(var5);
            }
         });
      }

   }

   public void printFiles(AppEvent.PrintFilesEvent var1) {
      List var2 = var1.getFiles();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         File var4 = (File)var3.next();
         final ApplicationEvent var5 = new ApplicationEvent(Toolkit.getDefaultToolkit(), var4.getAbsolutePath());
         this.sendEventToEachListenerUntilHandled(var5, new _AppEventLegacyHandler.EventDispatcher() {
            public void dispatchEvent(ApplicationListener var1) {
               var1.handlePrintFile(var5);
            }
         });
      }

   }

   public void handleQuitRequestWith(AppEvent.QuitEvent var1, QuitResponse var2) {
      final ApplicationEvent var3 = new ApplicationEvent(Toolkit.getDefaultToolkit());
      this.sendEventToEachListenerUntilHandled(var3, new _AppEventLegacyHandler.EventDispatcher() {
         public void dispatchEvent(ApplicationListener var1) {
            var1.handleQuit(var3);
         }
      });
      if (var3.isHandled()) {
         this.parent.performQuit();
      } else {
         this.parent.cancelQuit();
      }

   }

   void sendEventToEachListenerUntilHandled(ApplicationEvent var1, _AppEventLegacyHandler.EventDispatcher var2) {
      synchronized(this.legacyAppListeners) {
         if (this.legacyAppListeners.size() != 0) {
            Enumeration var4 = this.legacyAppListeners.elements();

            while(var4.hasMoreElements() && !var1.isHandled()) {
               var2.dispatchEvent((ApplicationListener)var4.nextElement());
            }

         }
      }
   }

   interface EventDispatcher {
      void dispatchEvent(ApplicationListener var1);
   }
}
