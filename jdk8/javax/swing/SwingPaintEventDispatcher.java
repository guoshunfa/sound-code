package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import sun.awt.event.IgnorePaintEvent;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

class SwingPaintEventDispatcher extends PaintEventDispatcher {
   private static final boolean SHOW_FROM_DOUBLE_BUFFER = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.showFromDoubleBuffer", "true"))));
   private static final boolean ERASE_BACKGROUND = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("swing.nativeErase")));

   public PaintEvent createPaintEvent(Component var1, int var2, int var3, int var4, int var5) {
      AppContext var6;
      RepaintManager var7;
      if (!(var1 instanceof RootPaneContainer)) {
         if (var1 instanceof SwingHeavyWeight) {
            var6 = SunToolkit.targetToAppContext(var1);
            var7 = RepaintManager.currentManager(var6);
            var7.nativeAddDirtyRegion(var6, (Container)var1, var2, var3, var4, var5);
            return new IgnorePaintEvent(var1, 800, new Rectangle(var2, var3, var4, var5));
         } else {
            return super.createPaintEvent(var1, var2, var3, var4, var5);
         }
      } else {
         var6 = SunToolkit.targetToAppContext(var1);
         var7 = RepaintManager.currentManager(var6);
         if (!SHOW_FROM_DOUBLE_BUFFER || !var7.show((Container)var1, var2, var3, var4, var5)) {
            var7.nativeAddDirtyRegion(var6, (Container)var1, var2, var3, var4, var5);
         }

         return new IgnorePaintEvent(var1, 800, new Rectangle(var2, var3, var4, var5));
      }
   }

   public boolean shouldDoNativeBackgroundErase(Component var1) {
      return ERASE_BACKGROUND || !(var1 instanceof RootPaneContainer);
   }

   public boolean queueSurfaceDataReplacing(Component var1, Runnable var2) {
      if (var1 instanceof RootPaneContainer) {
         AppContext var3 = SunToolkit.targetToAppContext(var1);
         RepaintManager.currentManager(var3).nativeQueueSurfaceDataRunnable(var3, var1, var2);
         return true;
      } else {
         return super.queueSurfaceDataReplacing(var1, var2);
      }
   }
}
