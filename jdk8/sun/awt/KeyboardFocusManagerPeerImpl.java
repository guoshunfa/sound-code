package sun.awt;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.util.logging.PlatformLogger;

public abstract class KeyboardFocusManagerPeerImpl implements KeyboardFocusManagerPeer {
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
   private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
   public static final int SNFH_FAILURE = 0;
   public static final int SNFH_SUCCESS_HANDLED = 1;
   public static final int SNFH_SUCCESS_PROCEED = 2;

   public void clearGlobalFocusOwner(Window var1) {
      if (var1 != null) {
         Component var2 = var1.getFocusOwner();
         if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
            focusLog.fine("Clearing global focus owner " + var2);
         }

         if (var2 != null) {
            CausedFocusEvent var3 = new CausedFocusEvent(var2, 1005, false, (Component)null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
            SunToolkit.postPriorityEvent(var3);
         }
      }

   }

   public static boolean shouldFocusOnClick(Component var0) {
      boolean var1 = false;
      if (!(var0 instanceof Canvas) && !(var0 instanceof Scrollbar)) {
         if (var0 instanceof Panel) {
            var1 = ((Panel)var0).getComponentCount() == 0;
         } else {
            ComponentPeer var2 = var0 != null ? var0.getPeer() : null;
            var1 = var2 != null ? var2.isFocusable() : false;
         }
      } else {
         var1 = true;
      }

      return var1 && AWTAccessor.getComponentAccessor().canBeFocusOwner(var0);
   }

   public static boolean deliverFocus(Component var0, Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6, Component var7) {
      if (var0 == null) {
         var0 = var1;
      }

      Component var8 = var7;
      if (var7 != null && var7.getPeer() == null) {
         var8 = null;
      }

      CausedFocusEvent var9;
      if (var8 != null) {
         var9 = new CausedFocusEvent(var8, 1005, false, var0, var6);
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("Posting focus event: " + var9);
         }

         SunToolkit.postEvent(SunToolkit.targetToAppContext(var8), var9);
      }

      var9 = new CausedFocusEvent(var0, 1004, false, var8, var6);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("Posting focus event: " + var9);
      }

      SunToolkit.postEvent(SunToolkit.targetToAppContext(var0), var9);
      return true;
   }

   public static boolean requestFocusFor(Component var0, CausedFocusEvent.Cause var1) {
      return AWTAccessor.getComponentAccessor().requestFocus(var0, var1);
   }

   public static int shouldNativelyFocusHeavyweight(Component var0, Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return kfmAccessor.shouldNativelyFocusHeavyweight(var0, var1, var2, var3, var4, var6);
   }

   public static void removeLastFocusRequest(Component var0) {
      kfmAccessor.removeLastFocusRequest(var0);
   }

   public static boolean processSynchronousLightweightTransfer(Component var0, Component var1, boolean var2, boolean var3, long var4) {
      return kfmAccessor.processSynchronousLightweightTransfer(var0, var1, var2, var3, var4);
   }
}
