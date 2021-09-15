package apple.laf;

import com.apple.laf.AquaImageFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class JRSUIUtils {
   static boolean isLeopard = isMacOSXLeopard();
   static boolean isSnowLeopardOrBelow = isMacOSXSnowLeopardOrBelow();

   static boolean isMacOSXLeopard() {
      return isCurrentMacOSXVersion(5);
   }

   static boolean isMacOSXSnowLeopardOrBelow() {
      return currentMacOSXVersionMatchesGivenVersionRange(6, true, true, false);
   }

   static boolean isCurrentMacOSXVersion(int var0) {
      return currentMacOSXVersionMatchesGivenVersionRange(var0, true, false, false);
   }

   static boolean currentMacOSXVersionMatchesGivenVersionRange(int var0, boolean var1, boolean var2, boolean var3) {
      String var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.version")));
      String[] var5 = var4.split("\\.");
      if (!var5[0].equals("10")) {
         return false;
      } else if (var5.length < 2) {
         return false;
      } else {
         try {
            int var6 = Integer.parseInt(var5[1]);
            if (var1 && var6 == var0) {
               return true;
            }

            if (var2 && var6 < var0) {
               return true;
            }

            if (var3 && var6 > var0) {
               return true;
            }
         } catch (NumberFormatException var7) {
         }

         return false;
      }
   }

   public interface NineSliceMetricsProvider {
      AquaImageFactory.NineSliceMetrics getNineSliceMetricsForState(JRSUIState var1);
   }

   public static class HitDetection {
      public static JRSUIConstants.Hit getHitForPoint(JRSUIControl var0, double var1, double var3, double var5, double var7, double var9, double var11) {
         return var0.getHitForPoint(var1, var3, var5, var7, var9, var11);
      }
   }

   public static class Images {
      public static boolean shouldUseLegacySecurityUIPath() {
         return JRSUIUtils.isSnowLeopardOrBelow;
      }
   }

   public static class ScrollBar {
      private static native boolean shouldUseScrollToClick();

      public static boolean useScrollToClick() {
         return shouldUseScrollToClick();
      }

      public static void getPartBounds(double[] var0, JRSUIControl var1, double var2, double var4, double var6, double var8, JRSUIConstants.ScrollBarPart var10) {
         var1.getPartBounds(var0, var2, var4, var6, var8, var10.ordinal);
      }

      public static double getNativeOffsetChange(JRSUIControl var0, double var1, double var3, double var5, double var7, int var9, int var10, int var11) {
         return var0.getScrollBarOffsetChange(var1, var3, var5, var7, var9, var10, var11);
      }
   }

   public static class Tree {
      public static boolean useLegacyTreeKnobs() {
         return JRSUIUtils.isLeopard;
      }
   }

   public static class InternalFrame {
      public static boolean shouldUseLegacyBorderMetrics() {
         return JRSUIUtils.isSnowLeopardOrBelow;
      }
   }

   public static class TabbedPane {
      public static boolean useLegacyTabs() {
         return JRSUIUtils.isLeopard;
      }

      public static boolean shouldUseTabbedPaneContrastUI() {
         return !JRSUIUtils.isSnowLeopardOrBelow;
      }
   }
}
