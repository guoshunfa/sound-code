package com.apple.eawt;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

class _AppEventHandler {
   private static final int NOTIFY_ABOUT = 1;
   private static final int NOTIFY_PREFS = 2;
   private static final int NOTIFY_OPEN_APP = 3;
   private static final int NOTIFY_REOPEN_APP = 4;
   private static final int NOTIFY_QUIT = 5;
   private static final int NOTIFY_SHUTDOWN = 6;
   private static final int NOTIFY_ACTIVE_APP_GAINED = 7;
   private static final int NOTIFY_ACTIVE_APP_LOST = 8;
   private static final int NOTIFY_APP_HIDDEN = 9;
   private static final int NOTIFY_APP_SHOWN = 10;
   private static final int NOTIFY_USER_SESSION_ACTIVE = 11;
   private static final int NOTIFY_USER_SESSION_INACTIVE = 12;
   private static final int NOTIFY_SCREEN_SLEEP = 13;
   private static final int NOTIFY_SCREEN_WAKE = 14;
   private static final int NOTIFY_SYSTEM_SLEEP = 15;
   private static final int NOTIFY_SYSTEM_WAKE = 16;
   private static final int REGISTER_USER_SESSION = 1;
   private static final int REGISTER_SCREEN_SLEEP = 2;
   private static final int REGISTER_SYSTEM_SLEEP = 3;
   static final _AppEventHandler instance = new _AppEventHandler();
   final _AppEventHandler._AboutDispatcher aboutDispatcher = new _AppEventHandler._AboutDispatcher();
   final _AppEventHandler._PreferencesDispatcher preferencesDispatcher = new _AppEventHandler._PreferencesDispatcher();
   final _AppEventHandler._OpenFileDispatcher openFilesDispatcher = new _AppEventHandler._OpenFileDispatcher();
   final _AppEventHandler._PrintFileDispatcher printFilesDispatcher = new _AppEventHandler._PrintFileDispatcher();
   final _AppEventHandler._OpenURIDispatcher openURIDispatcher = new _AppEventHandler._OpenURIDispatcher();
   final _AppEventHandler._QuitDispatcher quitDispatcher = new _AppEventHandler._QuitDispatcher();
   final _AppEventHandler._OpenAppDispatcher openAppDispatcher = new _AppEventHandler._OpenAppDispatcher();
   final _AppEventHandler._AppReOpenedDispatcher reOpenAppDispatcher = new _AppEventHandler._AppReOpenedDispatcher();
   final _AppEventHandler._AppForegroundDispatcher foregroundAppDispatcher = new _AppEventHandler._AppForegroundDispatcher();
   final _AppEventHandler._HiddenAppDispatcher hiddenAppDispatcher = new _AppEventHandler._HiddenAppDispatcher();
   final _AppEventHandler._UserSessionDispatcher userSessionDispatcher = new _AppEventHandler._UserSessionDispatcher();
   final _AppEventHandler._ScreenSleepDispatcher screenSleepDispatcher = new _AppEventHandler._ScreenSleepDispatcher();
   final _AppEventHandler._SystemSleepDispatcher systemSleepDispatcher = new _AppEventHandler._SystemSleepDispatcher();
   final _AppEventLegacyHandler legacyHandler = new _AppEventLegacyHandler(this);
   QuitStrategy defaultQuitAction;
   QuitResponse currentQuitResponse;

   private static native void nativeOpenCocoaAboutWindow();

   private static native void nativeReplyToAppShouldTerminate(boolean var0);

   private static native void nativeRegisterForNotification(int var0);

   static _AppEventHandler getInstance() {
      return instance;
   }

   _AppEventHandler() {
      this.defaultQuitAction = QuitStrategy.SYSTEM_EXIT_0;
      String var1 = System.getProperty("apple.eawt.quitStrategy");
      if (var1 != null) {
         if ("CLOSE_ALL_WINDOWS".equals(var1)) {
            this.setDefaultQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
         } else if ("SYSTEM_EXIT_O".equals(var1)) {
            this.setDefaultQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);
         } else {
            System.err.println("unrecognized apple.eawt.quitStrategy: " + var1);
         }

      }
   }

   void addListener(AppEventListener var1) {
      if (var1 instanceof AppReOpenedListener) {
         this.reOpenAppDispatcher.addListener((AppReOpenedListener)var1);
      }

      if (var1 instanceof AppForegroundListener) {
         this.foregroundAppDispatcher.addListener((AppForegroundListener)var1);
      }

      if (var1 instanceof AppHiddenListener) {
         this.hiddenAppDispatcher.addListener((AppHiddenListener)var1);
      }

      if (var1 instanceof UserSessionListener) {
         this.userSessionDispatcher.addListener((UserSessionListener)var1);
      }

      if (var1 instanceof ScreenSleepListener) {
         this.screenSleepDispatcher.addListener((ScreenSleepListener)var1);
      }

      if (var1 instanceof SystemSleepListener) {
         this.systemSleepDispatcher.addListener((SystemSleepListener)var1);
      }

   }

   void removeListener(AppEventListener var1) {
      if (var1 instanceof AppReOpenedListener) {
         this.reOpenAppDispatcher.removeListener((AppReOpenedListener)var1);
      }

      if (var1 instanceof AppForegroundListener) {
         this.foregroundAppDispatcher.removeListener((AppForegroundListener)var1);
      }

      if (var1 instanceof AppHiddenListener) {
         this.hiddenAppDispatcher.removeListener((AppHiddenListener)var1);
      }

      if (var1 instanceof UserSessionListener) {
         this.userSessionDispatcher.removeListener((UserSessionListener)var1);
      }

      if (var1 instanceof ScreenSleepListener) {
         this.screenSleepDispatcher.removeListener((ScreenSleepListener)var1);
      }

      if (var1 instanceof SystemSleepListener) {
         this.systemSleepDispatcher.removeListener((SystemSleepListener)var1);
      }

   }

   void openCocoaAboutWindow() {
      nativeOpenCocoaAboutWindow();
   }

   void setDefaultQuitStrategy(QuitStrategy var1) {
      this.defaultQuitAction = var1;
   }

   synchronized QuitResponse obtainQuitResponse() {
      return this.currentQuitResponse != null ? this.currentQuitResponse : (this.currentQuitResponse = new QuitResponse(this));
   }

   synchronized void cancelQuit() {
      this.currentQuitResponse = null;
      nativeReplyToAppShouldTerminate(false);
   }

   synchronized void performQuit() {
      this.currentQuitResponse = null;

      try {
         if (this.defaultQuitAction == QuitStrategy.SYSTEM_EXIT_0) {
            System.exit(0);
         }

         if (this.defaultQuitAction != QuitStrategy.CLOSE_ALL_WINDOWS) {
            throw new RuntimeException("Unknown quit action");
         }

         EventQueue.invokeLater(new Runnable() {
            public void run() {
               Frame[] var1 = Frame.getFrames();

               for(int var2 = var1.length - 1; var2 >= 0; --var2) {
                  Frame var3 = var1[var2];
                  var3.dispatchEvent(new WindowEvent(var3, 201));
               }

            }
         });
      } finally {
         nativeReplyToAppShouldTerminate(false);
      }

   }

   private static void handlePrintFiles(List<String> var0) {
      instance.printFilesDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{var0}));
   }

   private static void handleOpenFiles(List<String> var0, String var1) {
      instance.openFilesDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{var0, var1}));
   }

   private static void handleOpenURI(String var0) {
      instance.openURIDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{var0}));
   }

   private static void handleNativeNotification(int var0) {
      switch(var0) {
      case 1:
         instance.aboutDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[0]));
         break;
      case 2:
         instance.preferencesDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[0]));
         break;
      case 3:
         instance.openAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[0]));
         break;
      case 4:
         instance.reOpenAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[0]), new Object[0]);
         break;
      case 5:
         instance.quitDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[0]));
      case 6:
         break;
      case 7:
         instance.foregroundAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.TRUE}), new Object[0]);
         break;
      case 8:
         instance.foregroundAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.FALSE}), new Object[0]);
         break;
      case 9:
         instance.hiddenAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.TRUE}), new Object[0]);
         break;
      case 10:
         instance.hiddenAppDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.FALSE}), new Object[0]);
         break;
      case 11:
         instance.userSessionDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.TRUE}), new Object[0]);
         break;
      case 12:
         instance.userSessionDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.FALSE}), new Object[0]);
         break;
      case 13:
         instance.screenSleepDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.TRUE}), new Object[0]);
         break;
      case 14:
         instance.screenSleepDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.FALSE}), new Object[0]);
         break;
      case 15:
         instance.systemSleepDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.TRUE}), new Object[0]);
         break;
      case 16:
         instance.systemSleepDispatcher.dispatch(new _AppEventHandler._NativeEvent(new Object[]{Boolean.FALSE}), new Object[0]);
         break;
      default:
         System.err.println("EAWT unknown native notification: " + var0);
      }

   }

   abstract class _QueuingAppEventDispatcher<H> extends _AppEventHandler._AppEventDispatcher<H> {
      List<_AppEventHandler._NativeEvent> queuedEvents = new LinkedList();

      _QueuingAppEventDispatcher() {
         super();
      }

      void dispatch(_AppEventHandler._NativeEvent var1) {
         synchronized(this) {
            if (this.queuedEvents != null) {
               this.queuedEvents.add(var1);
               return;
            }
         }

         super.dispatch(var1);
      }

      synchronized void setHandler(H var1) {
         this._handler = var1;
         this.setHandlerContext(AppContext.getAppContext());
         if (this.queuedEvents != null) {
            List var2 = this.queuedEvents;
            this.queuedEvents = null;
            if (var2.size() != 0) {
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  _AppEventHandler._NativeEvent var4 = (_AppEventHandler._NativeEvent)var3.next();
                  this.dispatch(var4);
               }
            }
         }

         if (var1 != _AppEventHandler.this.legacyHandler) {
            _AppEventHandler.this.legacyHandler.blockLegacyAPI();
         }
      }
   }

   abstract class _AppEventDispatcher<H> {
      H _handler;
      AppContext handlerContext;

      void dispatch(final _AppEventHandler._NativeEvent var1) {
         final Object var2;
         AppContext var3;
         synchronized(this) {
            var2 = this._handler;
            var3 = this.handlerContext;
         }

         if (var2 == null) {
            this.performDefaultAction(var1);
         } else {
            SunToolkit.invokeLaterOnAppContext(var3, new Runnable() {
               public void run() {
                  _AppEventDispatcher.this.performUsing(var2, var1);
               }
            });
         }

      }

      synchronized void setHandler(H var1) {
         this._handler = var1;
         this.setHandlerContext(AppContext.getAppContext());
         if (var1 != _AppEventHandler.this.legacyHandler) {
            _AppEventHandler.this.legacyHandler.blockLegacyAPI();
         }
      }

      void performDefaultAction(_AppEventHandler._NativeEvent var1) {
      }

      abstract void performUsing(H var1, _AppEventHandler._NativeEvent var2);

      protected void setHandlerContext(AppContext var1) {
         if (var1 == null) {
            throw new RuntimeException("Attempting to set a handler from a thread group without AppContext");
         } else {
            this.handlerContext = var1;
         }
      }
   }

   abstract class _BooleanAppEventMultiplexor<L, E> extends _AppEventHandler._AppEventMultiplexor<L> {
      _BooleanAppEventMultiplexor() {
         super();
      }

      void performOnListener(L var1, _AppEventHandler._NativeEvent var2) {
         boolean var3 = Boolean.TRUE.equals(var2.get(0));
         Object var4 = this.createEvent(var3);
         if (var3) {
            this.performTrueEventOn(var1, var4);
         } else {
            this.performFalseEventOn(var1, var4);
         }

      }

      abstract E createEvent(boolean var1);

      abstract void performTrueEventOn(L var1, E var2);

      abstract void performFalseEventOn(L var1, E var2);
   }

   abstract class _AppEventMultiplexor<L> {
      private final Map<L, AppContext> listenerToAppContext = new IdentityHashMap();
      boolean nativeListenerRegistered;

      void dispatch(final _AppEventHandler._NativeEvent var1, Object... var2) {
         ArrayList var3;
         synchronized(this) {
            if (this.listenerToAppContext.size() == 0) {
               return;
            }

            var3 = new ArrayList(this.listenerToAppContext.size());
            var3.addAll(this.listenerToAppContext.entrySet());
         }

         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            final Object var6 = var5.getKey();
            AppContext var7 = (AppContext)var5.getValue();
            SunToolkit.invokeLaterOnAppContext(var7, new Runnable() {
               public void run() {
                  _AppEventMultiplexor.this.performOnListener(var6, var1);
               }
            });
         }

      }

      synchronized void addListener(L var1) {
         this.setListenerContext(var1, AppContext.getAppContext());
         if (!this.nativeListenerRegistered) {
            this.registerNativeListener();
            this.nativeListenerRegistered = true;
         }

      }

      synchronized void removeListener(L var1) {
         this.listenerToAppContext.remove(var1);
      }

      abstract void performOnListener(L var1, _AppEventHandler._NativeEvent var2);

      void registerNativeListener() {
      }

      private void setListenerContext(L var1, AppContext var2) {
         if (var2 == null) {
            throw new RuntimeException("Attempting to add a listener from a thread group without AppContext");
         } else {
            this.listenerToAppContext.put(var1, AppContext.getAppContext());
         }
      }
   }

   static class _NativeEvent {
      Object[] args;

      public _NativeEvent(Object... var1) {
         this.args = var1;
      }

      <T> T get(int var1) {
         return this.args == null ? null : this.args[var1];
      }
   }

   class _QuitDispatcher extends _AppEventHandler._AppEventDispatcher<QuitHandler> {
      _QuitDispatcher() {
         super();
      }

      void performDefaultAction(_AppEventHandler._NativeEvent var1) {
         _AppEventHandler.this.obtainQuitResponse().performQuit();
      }

      void performUsing(QuitHandler var1, _AppEventHandler._NativeEvent var2) {
         QuitResponse var3 = _AppEventHandler.this.obtainQuitResponse();
         var1.handleQuitRequestWith(new AppEvent.QuitEvent(), var3);
      }
   }

   class _OpenURIDispatcher extends _AppEventHandler._QueuingAppEventDispatcher<OpenURIHandler> {
      _OpenURIDispatcher() {
         super();
      }

      void performUsing(OpenURIHandler var1, _AppEventHandler._NativeEvent var2) {
         String var3 = (String)var2.get(0);

         try {
            var1.openURI(new AppEvent.OpenURIEvent(new URI(var3)));
         } catch (URISyntaxException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   class _PrintFileDispatcher extends _AppEventHandler._QueuingAppEventDispatcher<PrintFilesHandler> {
      _PrintFileDispatcher() {
         super();
      }

      void performUsing(PrintFilesHandler var1, _AppEventHandler._NativeEvent var2) {
         List var3 = (List)var2.get(0);
         ArrayList var4 = new ArrayList(var3.size());
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            var4.add(new File(var6));
         }

         var1.printFiles(new AppEvent.PrintFilesEvent(var4));
      }
   }

   class _OpenFileDispatcher extends _AppEventHandler._QueuingAppEventDispatcher<OpenFilesHandler> {
      _OpenFileDispatcher() {
         super();
      }

      void performUsing(OpenFilesHandler var1, _AppEventHandler._NativeEvent var2) {
         List var3 = (List)var2.get(0);
         ArrayList var4 = new ArrayList(var3.size());
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            var4.add(new File(var6));
         }

         String var7 = (String)var2.get(1);
         var1.openFiles(new AppEvent.OpenFilesEvent(var4, var7));
      }
   }

   class _SystemSleepDispatcher extends _AppEventHandler._BooleanAppEventMultiplexor<SystemSleepListener, AppEvent.SystemSleepEvent> {
      _SystemSleepDispatcher() {
         super();
      }

      AppEvent.SystemSleepEvent createEvent(boolean var1) {
         return new AppEvent.SystemSleepEvent();
      }

      void performFalseEventOn(SystemSleepListener var1, AppEvent.SystemSleepEvent var2) {
         var1.systemAwoke(var2);
      }

      void performTrueEventOn(SystemSleepListener var1, AppEvent.SystemSleepEvent var2) {
         var1.systemAboutToSleep(var2);
      }

      void registerNativeListener() {
         _AppEventHandler.nativeRegisterForNotification(3);
      }
   }

   class _ScreenSleepDispatcher extends _AppEventHandler._BooleanAppEventMultiplexor<ScreenSleepListener, AppEvent.ScreenSleepEvent> {
      _ScreenSleepDispatcher() {
         super();
      }

      AppEvent.ScreenSleepEvent createEvent(boolean var1) {
         return new AppEvent.ScreenSleepEvent();
      }

      void performFalseEventOn(ScreenSleepListener var1, AppEvent.ScreenSleepEvent var2) {
         var1.screenAwoke(var2);
      }

      void performTrueEventOn(ScreenSleepListener var1, AppEvent.ScreenSleepEvent var2) {
         var1.screenAboutToSleep(var2);
      }

      void registerNativeListener() {
         _AppEventHandler.nativeRegisterForNotification(2);
      }
   }

   class _UserSessionDispatcher extends _AppEventHandler._BooleanAppEventMultiplexor<UserSessionListener, AppEvent.UserSessionEvent> {
      _UserSessionDispatcher() {
         super();
      }

      AppEvent.UserSessionEvent createEvent(boolean var1) {
         return new AppEvent.UserSessionEvent();
      }

      void performFalseEventOn(UserSessionListener var1, AppEvent.UserSessionEvent var2) {
         var1.userSessionDeactivated(var2);
      }

      void performTrueEventOn(UserSessionListener var1, AppEvent.UserSessionEvent var2) {
         var1.userSessionActivated(var2);
      }

      void registerNativeListener() {
         _AppEventHandler.nativeRegisterForNotification(1);
      }
   }

   class _HiddenAppDispatcher extends _AppEventHandler._BooleanAppEventMultiplexor<AppHiddenListener, AppEvent.AppHiddenEvent> {
      _HiddenAppDispatcher() {
         super();
      }

      AppEvent.AppHiddenEvent createEvent(boolean var1) {
         return new AppEvent.AppHiddenEvent();
      }

      void performFalseEventOn(AppHiddenListener var1, AppEvent.AppHiddenEvent var2) {
         var1.appUnhidden(var2);
      }

      void performTrueEventOn(AppHiddenListener var1, AppEvent.AppHiddenEvent var2) {
         var1.appHidden(var2);
      }
   }

   class _AppForegroundDispatcher extends _AppEventHandler._BooleanAppEventMultiplexor<AppForegroundListener, AppEvent.AppForegroundEvent> {
      _AppForegroundDispatcher() {
         super();
      }

      AppEvent.AppForegroundEvent createEvent(boolean var1) {
         return new AppEvent.AppForegroundEvent();
      }

      void performFalseEventOn(AppForegroundListener var1, AppEvent.AppForegroundEvent var2) {
         var1.appMovedToBackground(var2);
      }

      void performTrueEventOn(AppForegroundListener var1, AppEvent.AppForegroundEvent var2) {
         var1.appRaisedToForeground(var2);
      }
   }

   class _AppReOpenedDispatcher extends _AppEventHandler._AppEventMultiplexor<AppReOpenedListener> {
      _AppReOpenedDispatcher() {
         super();
      }

      void performOnListener(AppReOpenedListener var1, _AppEventHandler._NativeEvent var2) {
         AppEvent.AppReOpenedEvent var3 = new AppEvent.AppReOpenedEvent();
         var1.appReOpened(var3);
      }
   }

   class _OpenAppDispatcher extends _AppEventHandler._QueuingAppEventDispatcher<_OpenAppHandler> {
      _OpenAppDispatcher() {
         super();
      }

      void performUsing(_OpenAppHandler var1, _AppEventHandler._NativeEvent var2) {
         var1.handleOpenApp();
      }
   }

   class _PreferencesDispatcher extends _AppEventHandler._AppEventDispatcher<PreferencesHandler> {
      _PreferencesDispatcher() {
         super();
      }

      synchronized void setHandler(PreferencesHandler var1) {
         super.setHandler(var1);
         _AppMenuBarHandler.getInstance().setPreferencesMenuItemVisible(var1 != null);
         _AppMenuBarHandler.getInstance().setPreferencesMenuItemEnabled(var1 != null);
      }

      void performUsing(PreferencesHandler var1, _AppEventHandler._NativeEvent var2) {
         var1.handlePreferences(new AppEvent.PreferencesEvent());
      }
   }

   class _AboutDispatcher extends _AppEventHandler._AppEventDispatcher<AboutHandler> {
      _AboutDispatcher() {
         super();
      }

      void performDefaultAction(_AppEventHandler._NativeEvent var1) {
         _AppEventHandler.this.openCocoaAboutWindow();
      }

      void performUsing(AboutHandler var1, _AppEventHandler._NativeEvent var2) {
         var1.handleAbout(new AppEvent.AboutEvent());
      }
   }
}
