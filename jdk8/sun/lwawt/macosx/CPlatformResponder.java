package sun.lwawt.macosx;

import java.awt.Toolkit;
import java.util.Locale;
import sun.awt.SunToolkit;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformEventNotifier;

final class CPlatformResponder {
   private final PlatformEventNotifier eventNotifier;
   private final boolean isNpapiCallback;
   private int lastKeyPressCode = 0;
   private final CPlatformResponder.DeltaAccumulator deltaAccumulatorX = new CPlatformResponder.DeltaAccumulator();
   private final CPlatformResponder.DeltaAccumulator deltaAccumulatorY = new CPlatformResponder.DeltaAccumulator();

   CPlatformResponder(PlatformEventNotifier var1, boolean var2) {
      this.eventNotifier = var1;
      this.isNpapiCallback = var2;
   }

   void handleMouseEvent(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      SunToolkit var9 = (SunToolkit)Toolkit.getDefaultToolkit();
      if ((var3 <= 2 || var9.areExtraMouseButtonsEnabled()) && var3 <= var9.getNumberOfButtons() - 1) {
         int var10 = this.isNpapiCallback ? NSEvent.npToJavaEventType(var1) : NSEvent.nsToJavaEventType(var1);
         int var11 = 0;
         int var12 = 0;
         if (var10 != 503 && var10 != 504 && var10 != 505) {
            var11 = NSEvent.nsToJavaButton(var3);
            var12 = var4;
         }

         int var13 = NSEvent.nsToJavaMouseModifiers(var3, var2);
         boolean var14 = NSEvent.isPopupTrigger(var13);
         this.eventNotifier.notifyMouseEvent(var10, System.currentTimeMillis(), var11, var5, var6, var7, var8, var13, var12, var14, (byte[])null);
      }
   }

   void handleScrollEvent(int var1, int var2, int var3, double var4, double var6, int var8) {
      int var10 = NSEvent.nsToJavaMouseModifiers(2, var3);
      boolean var11 = (var10 & 64) != 0;
      int var12 = this.deltaAccumulatorX.getRoundedDelta(var4, var8);
      int var13 = this.deltaAccumulatorY.getRoundedDelta(var6, var8);
      if (!var11 && (var6 != 0.0D || var13 != 0)) {
         this.dispatchScrollEvent(var1, var2, var10, var13, var6);
      }

      double var14 = var11 && var6 != 0.0D ? var6 : var4;
      int var16 = var11 && var13 != 0 ? var13 : var12;
      if (var14 != 0.0D || var16 != 0) {
         var10 |= 64;
         this.dispatchScrollEvent(var1, var2, var10, var16, var14);
      }

   }

   private void dispatchScrollEvent(int var1, int var2, int var3, int var4, double var5) {
      long var7 = System.currentTimeMillis();
      this.eventNotifier.notifyMouseWheelEvent(var7, var1, var2, var3, 0, 1, -var4, -var5, (byte[])null);
   }

   void handleKeyEvent(int var1, int var2, String var3, String var4, short var5, boolean var6, boolean var7) {
      boolean var8 = this.isNpapiCallback ? var1 == 10 : var1 == 12;
      boolean var9 = true;
      boolean var10 = false;
      boolean var11 = false;
      boolean var12 = false;
      char var13 = '\uffff';
      boolean var14 = var3 != null && var3.length() == 0;
      int[] var16;
      int var19;
      int var20;
      int var21;
      char var22;
      if (var8) {
         int[] var15 = new int[]{var2, var5};
         var16 = new int[3];
         NSEvent.nsKeyModifiersToJavaKeyInfo(var15, var16);
         var20 = var16[0];
         var21 = var16[1];
         var19 = var16[2];
      } else {
         if (var3 != null && var3.length() > 0) {
            var13 = var3.charAt(0);
         }

         var22 = var4 != null && var4.length() > 0 ? var4.charAt(0) : '\uffff';
         var16 = new int[]{var22, var14 ? 1 : 0, var2, var5};
         int[] var17 = new int[3];
         var12 = NSEvent.nsToJavaKeyInfo(var16, var17);
         if (!var12) {
            var13 = '\uffff';
         }

         if (var14) {
            var13 = (char)var17[2];
            if (var13 == 0) {
               return;
            }
         }

         LWCToolkit var18 = (LWCToolkit)Toolkit.getDefaultToolkit();
         if (var18.getLockingKeyState(20) && Locale.SIMPLIFIED_CHINESE.equals(var18.getDefaultKeyboardLocale())) {
            var13 = var22;
         }

         var20 = var17[0];
         var21 = var17[1];
         var19 = this.isNpapiCallback ? NSEvent.npToJavaEventType(var1) : NSEvent.nsToJavaEventType(var1);
      }

      var22 = NSEvent.nsToJavaChar(var13, var2);
      if (var22 == '\uffff') {
         var12 = false;
      }

      int var23 = NSEvent.nsToJavaKeyModifiers(var2);
      long var24 = System.currentTimeMillis();
      if (var19 == 401) {
         this.lastKeyPressCode = var20;
      }

      this.eventNotifier.notifyKeyEvent(var19, var24, var23, var20, var22, var21);
      var12 &= var6;
      if (var19 == 401 && var12 && (var23 & 256) == 0) {
         if (var7 && (var20 == 10 || var20 == 32)) {
            return;
         }

         this.eventNotifier.notifyKeyEvent(400, var24, var23, 0, var22, 0);
         if (var7) {
            this.eventNotifier.notifyKeyEvent(402, var24, var23, var20, var22, 0);
         }
      }

   }

   void handleInputEvent(String var1) {
      if (var1 != null) {
         int var2 = 0;
         int var3 = var1.length();

         char var4;
         for(var4 = 0; var2 < var3; ++var2) {
            var4 = var1.charAt(var2);
            this.eventNotifier.notifyKeyEvent(400, System.currentTimeMillis(), 0, 0, var4, 0);
         }

         this.eventNotifier.notifyKeyEvent(402, System.currentTimeMillis(), 0, this.lastKeyPressCode, var4, 0);
      }

   }

   void handleWindowFocusEvent(boolean var1, LWWindowPeer var2) {
      this.eventNotifier.notifyActivation(var1, var2);
   }

   static class DeltaAccumulator {
      double accumulatedDelta;
      boolean accumulate;

      int getRoundedDelta(double var1, int var3) {
         int var4 = (int)Math.round(var1);
         if (var3 == 1) {
            if (var4 == 0 && var1 != 0.0D) {
               var4 = var1 > 0.0D ? 1 : -1;
            }
         } else {
            if (var3 == 2) {
               this.accumulatedDelta = 0.0D;
               this.accumulate = true;
            } else if (var3 == 4) {
               this.accumulate = true;
            }

            if (this.accumulate) {
               this.accumulatedDelta += var1;
               var4 = (int)Math.round(this.accumulatedDelta);
               this.accumulatedDelta -= (double)var4;
               if (var3 == 5) {
                  this.accumulate = false;
               }
            }
         }

         return var4;
      }
   }
}
