package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import sun.util.logging.PlatformLogger;

public abstract class SunGraphicsCallback {
   public static final int HEAVYWEIGHTS = 1;
   public static final int LIGHTWEIGHTS = 2;
   public static final int TWO_PASSES = 4;
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.SunGraphicsCallback");

   public abstract void run(Component var1, Graphics var2);

   protected void constrainGraphics(Graphics var1, Rectangle var2) {
      if (var1 instanceof ConstrainableGraphics) {
         ((ConstrainableGraphics)var1).constrain(var2.x, var2.y, var2.width, var2.height);
      } else {
         var1.translate(var2.x, var2.y);
      }

      var1.clipRect(0, 0, var2.width, var2.height);
   }

   public final void runOneComponent(Component var1, Rectangle var2, Graphics var3, Shape var4, int var5) {
      if (var1 != null && var1.getPeer() != null && var1.isVisible()) {
         boolean var6 = var1.isLightweight();
         if ((!var6 || (var5 & 2) != 0) && (var6 || (var5 & 1) != 0)) {
            if (var2 == null) {
               var2 = var1.getBounds();
            }

            if (var4 == null || var4.intersects(var2)) {
               Graphics var7 = var3.create();

               try {
                  this.constrainGraphics(var7, var2);
                  var7.setFont(var1.getFont());
                  var7.setColor(var1.getForeground());
                  if (var7 instanceof Graphics2D) {
                     ((Graphics2D)var7).setBackground(var1.getBackground());
                  } else if (var7 instanceof Graphics2Delegate) {
                     ((Graphics2Delegate)var7).setBackground(var1.getBackground());
                  }

                  this.run(var1, var7);
               } finally {
                  var7.dispose();
               }
            }

         }
      }
   }

   public final void runComponents(Component[] var1, Graphics var2, int var3) {
      int var4 = var1.length;
      Shape var5 = var2.getClip();
      if (log.isLoggable(PlatformLogger.Level.FINER) && var5 != null) {
         Rectangle var6 = var5.getBounds();
         log.finer("x = " + var6.x + ", y = " + var6.y + ", width = " + var6.width + ", height = " + var6.height);
      }

      int var7;
      if ((var3 & 4) != 0) {
         for(var7 = var4 - 1; var7 >= 0; --var7) {
            this.runOneComponent(var1[var7], (Rectangle)null, var2, var5, 2);
         }

         for(var7 = var4 - 1; var7 >= 0; --var7) {
            this.runOneComponent(var1[var7], (Rectangle)null, var2, var5, 1);
         }
      } else {
         for(var7 = var4 - 1; var7 >= 0; --var7) {
            this.runOneComponent(var1[var7], (Rectangle)null, var2, var5, var3);
         }
      }

   }

   public static final class PrintHeavyweightComponentsCallback extends SunGraphicsCallback {
      private static SunGraphicsCallback.PrintHeavyweightComponentsCallback instance = new SunGraphicsCallback.PrintHeavyweightComponentsCallback();

      private PrintHeavyweightComponentsCallback() {
      }

      public void run(Component var1, Graphics var2) {
         if (!var1.isLightweight()) {
            var1.printAll(var2);
         } else if (var1 instanceof Container) {
            this.runComponents(((Container)var1).getComponents(), var2, 3);
         }

      }

      public static SunGraphicsCallback.PrintHeavyweightComponentsCallback getInstance() {
         return instance;
      }
   }

   public static final class PaintHeavyweightComponentsCallback extends SunGraphicsCallback {
      private static SunGraphicsCallback.PaintHeavyweightComponentsCallback instance = new SunGraphicsCallback.PaintHeavyweightComponentsCallback();

      private PaintHeavyweightComponentsCallback() {
      }

      public void run(Component var1, Graphics var2) {
         if (!var1.isLightweight()) {
            var1.paintAll(var2);
         } else if (var1 instanceof Container) {
            this.runComponents(((Container)var1).getComponents(), var2, 3);
         }

      }

      public static SunGraphicsCallback.PaintHeavyweightComponentsCallback getInstance() {
         return instance;
      }
   }
}
