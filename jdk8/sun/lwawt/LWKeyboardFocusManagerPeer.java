package sun.lwawt;

import java.awt.Component;
import java.awt.Window;
import sun.awt.KeyboardFocusManagerPeerImpl;

public class LWKeyboardFocusManagerPeer extends KeyboardFocusManagerPeerImpl {
   private static final LWKeyboardFocusManagerPeer inst = new LWKeyboardFocusManagerPeer();
   private Window focusedWindow;
   private Component focusOwner;

   public static LWKeyboardFocusManagerPeer getInstance() {
      return inst;
   }

   private LWKeyboardFocusManagerPeer() {
   }

   public void setCurrentFocusedWindow(Window var1) {
      LWWindowPeer var2;
      LWWindowPeer var3;
      synchronized(this) {
         if (this.focusedWindow == var1) {
            return;
         }

         var2 = (LWWindowPeer)LWToolkit.targetToPeer(this.focusedWindow);
         var3 = (LWWindowPeer)LWToolkit.targetToPeer(var1);
         this.focusedWindow = var1;
      }

      if (var2 != null) {
         var2.updateSecurityWarningVisibility();
      }

      if (var3 != null) {
         var3.updateSecurityWarningVisibility();
      }

   }

   public Window getCurrentFocusedWindow() {
      synchronized(this) {
         return this.focusedWindow;
      }
   }

   public Component getCurrentFocusOwner() {
      synchronized(this) {
         return this.focusOwner;
      }
   }

   public void setCurrentFocusOwner(Component var1) {
      synchronized(this) {
         this.focusOwner = var1;
      }
   }
}
