package sun.lwawt.macosx;

import java.awt.AWTKeyStroke;
import java.awt.Point;
import java.awt.Toolkit;
import sun.awt.EmbeddedFrame;
import sun.lwawt.LWWindowPeer;

public class CEmbeddedFrame extends EmbeddedFrame {
   private CPlatformResponder responder;
   private static final Object classLock = new Object();
   private static volatile CEmbeddedFrame globalFocusedWindow;
   private CEmbeddedFrame browserWindowFocusedApplet;
   private boolean parentWindowActive = true;

   public CEmbeddedFrame() {
      this.show();
   }

   public void addNotify() {
      if (this.getPeer() == null) {
         LWCToolkit var1 = (LWCToolkit)Toolkit.getDefaultToolkit();
         LWWindowPeer var2 = var1.createEmbeddedFrame(this);
         this.setPeer(var2);
         this.responder = new CPlatformResponder(var2, true);
      }

      super.addNotify();
   }

   public void registerAccelerator(AWTKeyStroke var1) {
   }

   public void unregisterAccelerator(AWTKeyStroke var1) {
   }

   protected long getLayerPtr() {
      LWWindowPeer var1 = (LWWindowPeer)this.getPeer();
      return var1.getLayerPtr();
   }

   public void handleMouseEvent(int var1, int var2, double var3, double var5, int var7, int var8) {
      int var9 = (int)var3;
      int var10 = (int)var5;
      Point var11 = this.getLocationOnScreen();
      int var12 = var11.x + var9;
      int var13 = var11.y + var10;
      if (var1 == 5) {
         CCursorManager.nativeSetAllowsCursorSetInBackground(true);
      } else if (var1 == 6) {
         CCursorManager.nativeSetAllowsCursorSetInBackground(false);
      }

      this.responder.handleMouseEvent(var1, var2, var7, var8, var9, var10, var12, var13);
   }

   public void handleScrollEvent(double var1, double var3, int var5, double var6, double var8, double var10) {
      int var12 = (int)var1;
      int var13 = (int)var3;
      this.responder.handleScrollEvent(var12, var13, var5, var6, var8, 1);
   }

   public void handleKeyEvent(int var1, int var2, String var3, String var4, boolean var5, short var6, boolean var7) {
      this.responder.handleKeyEvent(var1, var2, var3, var4, var6, var7, var5);
   }

   public void handleInputEvent(String var1) {
      this.responder.handleInputEvent(var1);
   }

   public void handleFocusEvent(boolean var1) {
      synchronized(classLock) {
         globalFocusedWindow = var1 ? this : (globalFocusedWindow == this ? null : globalFocusedWindow);
      }

      if (globalFocusedWindow == this) {
         CClipboard var2 = (CClipboard)Toolkit.getDefaultToolkit().getSystemClipboard();
         var2.checkPasteboardAndNotify();
      }

      if (this.parentWindowActive) {
         this.responder.handleWindowFocusEvent(var1, (LWWindowPeer)null);
      }

   }

   public void handleWindowFocusEvent(boolean var1) {
      this.parentWindowActive = var1;
      synchronized(classLock) {
         if (!var1) {
            this.browserWindowFocusedApplet = globalFocusedWindow;
         }

         if (var1 && globalFocusedWindow != this && this.isParentWindowChanged()) {
            globalFocusedWindow = this.browserWindowFocusedApplet != null ? this.browserWindowFocusedApplet : this;
         }
      }

      if (globalFocusedWindow == this) {
         this.responder.handleWindowFocusEvent(var1, (LWWindowPeer)null);
      }

   }

   public boolean isParentWindowActive() {
      return this.parentWindowActive;
   }

   private boolean isParentWindowChanged() {
      return globalFocusedWindow != null ? !globalFocusedWindow.isParentWindowActive() : true;
   }
}
