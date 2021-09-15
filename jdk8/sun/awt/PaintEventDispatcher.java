package sun.awt;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;

public class PaintEventDispatcher {
   private static PaintEventDispatcher dispatcher;

   public static void setPaintEventDispatcher(PaintEventDispatcher var0) {
      Class var1 = PaintEventDispatcher.class;
      synchronized(PaintEventDispatcher.class) {
         dispatcher = var0;
      }
   }

   public static PaintEventDispatcher getPaintEventDispatcher() {
      Class var0 = PaintEventDispatcher.class;
      synchronized(PaintEventDispatcher.class) {
         if (dispatcher == null) {
            dispatcher = new PaintEventDispatcher();
         }

         return dispatcher;
      }
   }

   public PaintEvent createPaintEvent(Component var1, int var2, int var3, int var4, int var5) {
      return new PaintEvent(var1, 800, new Rectangle(var2, var3, var4, var5));
   }

   public boolean shouldDoNativeBackgroundErase(Component var1) {
      return true;
   }

   public boolean queueSurfaceDataReplacing(Component var1, Runnable var2) {
      return false;
   }
}
