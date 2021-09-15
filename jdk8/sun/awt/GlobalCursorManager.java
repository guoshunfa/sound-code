package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;

public abstract class GlobalCursorManager {
   private final GlobalCursorManager.NativeUpdater nativeUpdater = new GlobalCursorManager.NativeUpdater();
   private long lastUpdateMillis;
   private final Object lastUpdateLock = new Object();

   public void updateCursorImmediately() {
      synchronized(this.nativeUpdater) {
         this.nativeUpdater.pending = false;
      }

      this._updateCursor(false);
   }

   public void updateCursorImmediately(InputEvent var1) {
      boolean var2;
      synchronized(this.lastUpdateLock) {
         var2 = var1.getWhen() >= this.lastUpdateMillis;
      }

      if (var2) {
         this._updateCursor(true);
      }

   }

   public void updateCursorLater(Component var1) {
      this.nativeUpdater.postIfNotPending(var1, new InvocationEvent(Toolkit.getDefaultToolkit(), this.nativeUpdater));
   }

   protected GlobalCursorManager() {
   }

   protected abstract void setCursor(Component var1, Cursor var2, boolean var3);

   protected abstract void getCursorPos(Point var1);

   protected abstract Point getLocationOnScreen(Component var1);

   protected abstract Component findHeavyweightUnderCursor(boolean var1);

   private void _updateCursor(boolean var1) {
      synchronized(this.lastUpdateLock) {
         this.lastUpdateMillis = System.currentTimeMillis();
      }

      Point var2 = null;
      Point var3 = null;

      try {
         Component var4 = this.findHeavyweightUnderCursor(var1);
         if (var4 == null) {
            this.updateCursorOutOfJava();
            return;
         }

         if (var4 instanceof Window) {
            var3 = AWTAccessor.getComponentAccessor().getLocation(var4);
         } else if (var4 instanceof Container) {
            var3 = this.getLocationOnScreen(var4);
         }

         if (var3 != null) {
            var2 = new Point();
            this.getCursorPos(var2);
            Component var5 = AWTAccessor.getContainerAccessor().findComponentAt((Container)var4, var2.x - var3.x, var2.y - var3.y, false);
            if (var5 != null) {
               var4 = var5;
            }
         }

         this.setCursor(var4, AWTAccessor.getComponentAccessor().getCursor(var4), var1);
      } catch (IllegalComponentStateException var6) {
      }

   }

   protected void updateCursorOutOfJava() {
   }

   class NativeUpdater implements Runnable {
      boolean pending = false;

      public void run() {
         boolean var1 = false;
         synchronized(this) {
            if (this.pending) {
               this.pending = false;
               var1 = true;
            }
         }

         if (var1) {
            GlobalCursorManager.this._updateCursor(false);
         }

      }

      public void postIfNotPending(Component var1, InvocationEvent var2) {
         boolean var3 = false;
         synchronized(this) {
            if (!this.pending) {
               var3 = true;
               this.pending = true;
            }
         }

         if (var3) {
            SunToolkit.postEvent(SunToolkit.targetToAppContext(var1), var2);
         }

      }
   }
}
