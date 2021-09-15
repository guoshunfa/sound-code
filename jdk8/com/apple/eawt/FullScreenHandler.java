package com.apple.eawt;

import java.awt.Window;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.RootPaneContainer;
import sun.awt.SunToolkit;

final class FullScreenHandler {
   private static final String CLIENT_PROPERTY = "com.apple.eawt.event.internalFullScreenHandler";
   static final int FULLSCREEN_WILL_ENTER = 1;
   static final int FULLSCREEN_DID_ENTER = 2;
   static final int FULLSCREEN_WILL_EXIT = 3;
   static final int FULLSCREEN_DID_EXIT = 4;
   final List<FullScreenListener> listeners = new LinkedList();

   static void addFullScreenListenerTo(RootPaneContainer var0, FullScreenListener var1) {
      Object var2 = var0.getRootPane().getClientProperty("com.apple.eawt.event.internalFullScreenHandler");
      if (var2 instanceof FullScreenHandler) {
         ((FullScreenHandler)var2).addListener(var1);
      } else if (var2 == null) {
         FullScreenHandler var3 = new FullScreenHandler();
         var3.addListener(var1);
         var0.getRootPane().putClientProperty("com.apple.eawt.event.internalFullScreenHandler", var3);
      }
   }

   static void removeFullScreenListenerFrom(RootPaneContainer var0, FullScreenListener var1) {
      Object var2 = var0.getRootPane().getClientProperty("com.apple.eawt.event.internalFullScreenHandler");
      if (var2 instanceof FullScreenHandler) {
         ((FullScreenHandler)var2).removeListener(var1);
      }
   }

   static FullScreenHandler getHandlerFor(RootPaneContainer var0) {
      Object var1 = var0.getRootPane().getClientProperty("com.apple.eawt.event.internalFullScreenHandler");
      return var1 instanceof FullScreenHandler ? (FullScreenHandler)var1 : null;
   }

   static void handleFullScreenEventFromNative(final Window var0, final int var1) {
      if (var0 instanceof RootPaneContainer) {
         SunToolkit.executeOnEventHandlerThread(var0, new Runnable() {
            public void run() {
               FullScreenHandler var1x = FullScreenHandler.getHandlerFor((RootPaneContainer)var0);
               if (var1x != null) {
                  var1x.notifyListener(new AppEvent.FullScreenEvent(var0), var1);
               }

            }
         });
      }
   }

   void addListener(FullScreenListener var1) {
      this.listeners.add(var1);
   }

   void removeListener(FullScreenListener var1) {
      this.listeners.remove(var1);
   }

   void notifyListener(AppEvent.FullScreenEvent var1, int var2) {
      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         FullScreenListener var4 = (FullScreenListener)var3.next();
         switch(var2) {
         case 1:
            var4.windowEnteringFullScreen(var1);
            return;
         case 2:
            var4.windowEnteredFullScreen(var1);
            return;
         case 3:
            var4.windowExitingFullScreen(var1);
            return;
         case 4:
            var4.windowExitedFullScreen(var1);
            return;
         }
      }

   }
}
