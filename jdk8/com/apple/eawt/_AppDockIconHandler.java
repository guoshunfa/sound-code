package com.apple.eawt;

import java.awt.Image;
import java.awt.MenuBar;
import java.awt.MenuContainer;
import java.awt.PopupMenu;
import java.lang.reflect.Method;
import sun.lwawt.macosx.CImage;
import sun.lwawt.macosx.CMenu;

class _AppDockIconHandler {
   PopupMenu fDockMenu = null;

   private static native void nativeSetDockMenu(long var0);

   private static native void nativeSetDockIconImage(long var0);

   private static native long nativeGetDockIconImage();

   private static native void nativeSetDockIconBadge(String var0);

   public void setDockMenu(PopupMenu var1) {
      this.fDockMenu = var1;
      if (var1 == null) {
         nativeSetDockMenu(0L);
      } else {
         MenuContainer var2 = var1.getParent();
         if (var2 == null) {
            MenuBar var3 = new MenuBar();
            var3.add(var1);
            var3.addNotify();
         }

         var1.addNotify();
         long var5 = ((CMenu)this.fDockMenu.getPeer()).getNativeMenu();
         nativeSetDockMenu(var5);
      }
   }

   public PopupMenu getDockMenu() {
      return this.fDockMenu;
   }

   public void setDockIconImage(Image var1) {
      try {
         CImage var2 = getCImageCreator().createFromImage(var1);
         var2.execute(_AppDockIconHandler::nativeSetDockIconImage);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   Image getDockIconImage() {
      try {
         long var1 = nativeGetDockIconImage();
         return var1 == 0L ? null : getCImageCreator().createImageUsingNativeSize(var1);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   void setDockIconBadge(String var1) {
      nativeSetDockIconBadge(var1);
   }

   static CImage.Creator getCImageCreator() {
      try {
         Method var0 = CImage.class.getDeclaredMethod("getCreator");
         var0.setAccessible(true);
         return (CImage.Creator)var0.invoke((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }
}
