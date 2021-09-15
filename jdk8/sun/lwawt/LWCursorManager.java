package sun.lwawt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.peer.ComponentPeer;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public abstract class LWCursorManager {
   private final AtomicBoolean updatePending = new AtomicBoolean(false);

   protected LWCursorManager() {
   }

   public final void updateCursor() {
      this.updatePending.set(false);
      this.updateCursorImpl();
   }

   public final void updateCursorLater(LWWindowPeer var1) {
      if (this.updatePending.compareAndSet(false, true)) {
         Runnable var2 = new Runnable() {
            public void run() {
               LWCursorManager.this.updateCursor();
            }
         };
         SunToolkit.executeOnEventHandlerThread(var1.getTarget(), var2);
      }

   }

   private void updateCursorImpl() {
      Point var1 = this.getCursorPosition();
      Component var2 = findComponent(var1);
      Object var4 = LWToolkit.targetToPeer(var2);
      Cursor var3;
      if (var4 instanceof LWComponentPeer) {
         LWComponentPeer var5 = (LWComponentPeer)var4;
         Point var6 = var5.getLocationOnScreen();
         var3 = var5.getCursor(new Point(var1.x - var6.x, var1.y - var6.y));
      } else {
         var3 = var2 != null ? var2.getCursor() : null;
      }

      this.setCursor(var3);
   }

   private static final Component findComponent(Point var0) {
      LWComponentPeer var1 = LWWindowPeer.getPeerUnderCursor();
      Object var2 = null;
      if (var1 != null && var1.getWindowPeerOrSelf().getBlocker() == null) {
         var2 = var1.getTarget();
         if (var2 instanceof Container) {
            Point var3 = var1.getLocationOnScreen();
            var2 = AWTAccessor.getContainerAccessor().findComponentAt((Container)var2, var0.x - var3.x, var0.y - var3.y, false);
         }

         while(var2 != null) {
            ComponentPeer var4 = AWTAccessor.getComponentAccessor().getPeer((Component)var2);
            if (((Component)var2).isVisible() && ((Component)var2).isEnabled() && var4 != null) {
               break;
            }

            var2 = ((Component)var2).getParent();
         }
      }

      return (Component)var2;
   }

   protected abstract Point getCursorPosition();

   protected abstract void setCursor(Cursor var1);
}
