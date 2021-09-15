package com.apple.laf;

import java.awt.Component;
import java.awt.Window;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JRootPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

class ScreenPopupFactory extends PopupFactory {
   static final Float TRANSLUCENT;
   static final Float OPAQUE;
   boolean fIsActive = true;

   void setActive(boolean var1) {
      this.fIsActive = var1;
   }

   private static Window getWindow(Component var0) {
      Object var1;
      for(var1 = var0; !(var1 instanceof Window) && var1 != null; var1 = ((Component)var1).getParent()) {
      }

      return (Window)var1;
   }

   native Popup _getHeavyWeightPopup(Component var1, Component var2, int var3, int var4);

   public Popup getPopup(Component var1, Component var2, int var3, int var4) {
      if (var2 == null) {
         throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
      } else {
         Popup var5;
         if (this.fIsActive) {
            var5 = this._getHeavyWeightPopup(var1, var2, var3, var4);
         } else {
            var5 = super.getPopup(var1, var2, var3, var4);
         }

         Window var6 = getWindow(var2);
         if (var6 == null) {
            return var5;
         } else if (!(var6 instanceof RootPaneContainer)) {
            return var5;
         } else {
            final JRootPane var7 = ((RootPaneContainer)var6).getRootPane();
            if (this.fIsActive) {
               var7.putClientProperty("Window.alpha", TRANSLUCENT);
               var7.putClientProperty("Window.shadow", Boolean.TRUE);
               var7.putClientProperty("apple.awt._windowFadeDelegate", var2);
               var6.setBackground(UIManager.getColor("PopupMenu.translucentBackground"));
               var7.putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     var7.putClientProperty("apple.awt.windowShadow.revalidateNow", Math.random());
                  }
               });
            } else {
               var7.putClientProperty("Window.alpha", OPAQUE);
               var7.putClientProperty("Window.shadow", Boolean.FALSE);
            }

            return var5;
         }
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osxui");
            return null;
         }
      });
      TRANSLUCENT = new Float(0.972549F);
      OPAQUE = new Float(1.0F);
   }
}
