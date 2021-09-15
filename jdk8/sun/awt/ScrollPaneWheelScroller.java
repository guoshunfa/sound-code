package sun.awt;

import java.awt.Adjustable;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.MouseWheelEvent;
import sun.util.logging.PlatformLogger;

public abstract class ScrollPaneWheelScroller {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.ScrollPaneWheelScroller");

   private ScrollPaneWheelScroller() {
   }

   public static void handleWheelScrolling(ScrollPane var0, MouseWheelEvent var1) {
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("x = " + var1.getX() + ", y = " + var1.getY() + ", src is " + var1.getSource());
      }

      boolean var2 = false;
      if (var0 != null && var1.getScrollAmount() != 0) {
         Adjustable var3 = getAdjustableToScroll(var0);
         if (var3 != null) {
            int var4 = getIncrementFromAdjustable(var3, var1);
            if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("increment from adjustable(" + var3.getClass() + ") : " + var4);
            }

            scrollAdjustable(var3, var4);
         }
      }

   }

   public static Adjustable getAdjustableToScroll(ScrollPane var0) {
      int var1 = var0.getScrollbarDisplayPolicy();
      if (var1 != 1 && var1 != 2) {
         Insets var2 = var0.getInsets();
         int var3 = var0.getVScrollbarWidth();
         if (log.isLoggable(PlatformLogger.Level.FINER)) {
            log.finer("insets: l = " + var2.left + ", r = " + var2.right + ", t = " + var2.top + ", b = " + var2.bottom);
            log.finer("vertScrollWidth = " + var3);
         }

         if (var2.right >= var3) {
            if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("using vertical scrolling because scrollbar is present");
            }

            return var0.getVAdjustable();
         } else {
            int var4 = var0.getHScrollbarHeight();
            if (var2.bottom >= var4) {
               if (log.isLoggable(PlatformLogger.Level.FINER)) {
                  log.finer("using horiz scrolling because scrollbar is present");
               }

               return var0.getHAdjustable();
            } else {
               if (log.isLoggable(PlatformLogger.Level.FINER)) {
                  log.finer("using NO scrollbar becsause neither is present");
               }

               return null;
            }
         }
      } else {
         if (log.isLoggable(PlatformLogger.Level.FINER)) {
            log.finer("using vertical scrolling due to scrollbar policy");
         }

         return var0.getVAdjustable();
      }
   }

   public static int getIncrementFromAdjustable(Adjustable var0, MouseWheelEvent var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var0 == null) {
         log.fine("Assertion (adj != null) failed");
      }

      int var2 = 0;
      if (var1.getScrollType() == 0) {
         var2 = var1.getUnitsToScroll() * var0.getUnitIncrement();
      } else if (var1.getScrollType() == 1) {
         var2 = var0.getBlockIncrement() * var1.getWheelRotation();
      }

      return var2;
   }

   public static void scrollAdjustable(Adjustable var0, int var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         if (var0 == null) {
            log.fine("Assertion (adj != null) failed");
         }

         if (var1 == 0) {
            log.fine("Assertion (amount != 0) failed");
         }
      }

      int var2 = var0.getValue();
      int var3 = var0.getMaximum() - var0.getVisibleAmount();
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("doScrolling by " + var1);
      }

      if (var1 > 0 && var2 < var3) {
         if (var2 + var1 < var3) {
            var0.setValue(var2 + var1);
         } else {
            var0.setValue(var3);
         }
      } else if (var1 < 0 && var2 > var0.getMinimum()) {
         if (var2 + var1 > var0.getMinimum()) {
            var0.setValue(var2 + var1);
         } else {
            var0.setValue(var0.getMinimum());
         }
      }
   }
}
