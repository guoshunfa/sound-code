package com.apple.eawt;

import java.awt.Window;
import javax.swing.RootPaneContainer;

public final class FullScreenUtilities {
   FullScreenUtilities() {
   }

   public static void setWindowCanFullScreen(Window var0, boolean var1) {
      if (!(var0 instanceof RootPaneContainer)) {
         throw new IllegalArgumentException("Can't mark a non-RootPaneContainer as full screen-able");
      } else {
         RootPaneContainer var2 = (RootPaneContainer)var0;
         var2.getRootPane().putClientProperty("apple.awt.fullscreenable", var1);
      }
   }

   public static void addFullScreenListenerTo(Window var0, FullScreenListener var1) {
      if (!(var0 instanceof RootPaneContainer)) {
         throw new IllegalArgumentException("Can't attach FullScreenListener to a non-RootPaneContainer");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         FullScreenHandler.addFullScreenListenerTo((RootPaneContainer)var0, var1);
      }
   }

   public static void removeFullScreenListenerFrom(Window var0, FullScreenListener var1) {
      if (!(var0 instanceof RootPaneContainer)) {
         throw new IllegalArgumentException("Can't remove FullScreenListener from non-RootPaneContainer");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         FullScreenHandler.removeFullScreenListenerFrom((RootPaneContainer)var0, var1);
      }
   }
}
