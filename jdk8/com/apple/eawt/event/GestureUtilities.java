package com.apple.eawt.event;

import javax.swing.JComponent;

public final class GestureUtilities {
   GestureUtilities() {
   }

   public static void addGestureListenerTo(JComponent var0, GestureListener var1) {
      if (var0 != null && var1 != null) {
         GestureHandler.addGestureListenerTo(var0, var1);
      } else {
         throw new NullPointerException();
      }
   }

   public static void removeGestureListenerFrom(JComponent var0, GestureListener var1) {
      if (var0 != null && var1 != null) {
         GestureHandler.removeGestureListenerFrom(var0, var1);
      } else {
         throw new NullPointerException();
      }
   }
}
