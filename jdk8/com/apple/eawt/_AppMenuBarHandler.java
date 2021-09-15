package com.apple.eawt;

import com.apple.laf.AquaMenuBarUI;
import com.apple.laf.ScreenMenuBar;
import java.awt.Frame;
import java.awt.peer.MenuComponentPeer;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.plaf.MenuBarUI;
import sun.lwawt.macosx.CMenuBar;

class _AppMenuBarHandler {
   private static final int MENU_ABOUT = 1;
   private static final int MENU_PREFS = 2;
   static final _AppMenuBarHandler instance = new _AppMenuBarHandler();
   boolean aboutMenuItemVisible;
   boolean aboutMenuItemEnabled;
   boolean prefsMenuItemVisible;
   boolean prefsMenuItemEnabled;
   boolean prefsMenuItemExplicitlySet;

   private static native void nativeSetMenuState(int var0, boolean var1, boolean var2);

   private static native void nativeSetDefaultMenuBar(long var0);

   static _AppMenuBarHandler getInstance() {
      return instance;
   }

   private static void initMenuStates(boolean var0, boolean var1, boolean var2, boolean var3) {
      synchronized(instance) {
         instance.aboutMenuItemVisible = var0;
         instance.aboutMenuItemEnabled = var1;
         instance.prefsMenuItemVisible = var2;
         instance.prefsMenuItemEnabled = var3;
      }
   }

   void setDefaultMenuBar(JMenuBar var1) {
      installDefaultMenuBar(var1);
      Frame[] var2 = Frame.getFrames();
      Frame[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Frame var6 = var3[var5];
         if (var6.isVisible() && !isFrameMinimized(var6)) {
            return;
         }
      }

      JFrame var7 = new JFrame();
      var7.getRootPane().putClientProperty("Window.alpha", new Float(0.0F));
      var7.setUndecorated(true);
      var7.setVisible(true);
      var7.toFront();
      var7.setVisible(false);
      var7.dispose();
   }

   static boolean isFrameMinimized(Frame var0) {
      return (var0.getExtendedState() & 1) != 0;
   }

   static void installDefaultMenuBar(JMenuBar var0) {
      if (var0 == null) {
         nativeSetDefaultMenuBar(0L);
      } else {
         MenuBarUI var1 = var0.getUI();
         if (!(var1 instanceof AquaMenuBarUI)) {
            throw new IllegalStateException("Application.setDefaultMenuBar() only works with the Aqua Look and Feel");
         } else {
            AquaMenuBarUI var2 = (AquaMenuBarUI)var1;
            ScreenMenuBar var3 = var2.getScreenMenuBar();
            if (var3 == null) {
               throw new IllegalStateException("Application.setDefaultMenuBar() only works if apple.laf.useScreenMenuBar=true");
            } else {
               var3.addNotify();
               MenuComponentPeer var4 = var3.getPeer();
               if (!(var4 instanceof CMenuBar)) {
                  throw new IllegalStateException("Unable to determine native menu bar from provided JMenuBar");
               } else {
                  ((CMenuBar)var4).execute(_AppMenuBarHandler::nativeSetDefaultMenuBar);
               }
            }
         }
      }
   }

   void setAboutMenuItemVisible(boolean var1) {
      synchronized(this) {
         if (this.aboutMenuItemVisible == var1) {
            return;
         }

         this.aboutMenuItemVisible = var1;
      }

      nativeSetMenuState(1, this.aboutMenuItemVisible, this.aboutMenuItemEnabled);
   }

   void setPreferencesMenuItemVisible(boolean var1) {
      synchronized(this) {
         this.prefsMenuItemExplicitlySet = true;
         if (this.prefsMenuItemVisible == var1) {
            return;
         }

         this.prefsMenuItemVisible = var1;
      }

      nativeSetMenuState(2, this.prefsMenuItemVisible, this.prefsMenuItemEnabled);
   }

   void setAboutMenuItemEnabled(boolean var1) {
      synchronized(this) {
         if (this.aboutMenuItemEnabled == var1) {
            return;
         }

         this.aboutMenuItemEnabled = var1;
      }

      nativeSetMenuState(1, this.aboutMenuItemVisible, this.aboutMenuItemEnabled);
   }

   void setPreferencesMenuItemEnabled(boolean var1) {
      synchronized(this) {
         this.prefsMenuItemExplicitlySet = true;
         if (this.prefsMenuItemEnabled == var1) {
            return;
         }

         this.prefsMenuItemEnabled = var1;
      }

      nativeSetMenuState(2, this.prefsMenuItemVisible, this.prefsMenuItemEnabled);
   }

   boolean isAboutMenuItemVisible() {
      return this.aboutMenuItemVisible;
   }

   boolean isPreferencesMenuItemVisible() {
      return this.prefsMenuItemVisible;
   }

   boolean isAboutMenuItemEnabled() {
      return this.aboutMenuItemEnabled;
   }

   boolean isPreferencesMenuItemEnabled() {
      return this.prefsMenuItemEnabled;
   }
}
