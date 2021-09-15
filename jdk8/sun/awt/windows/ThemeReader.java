package sun.awt.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

public final class ThemeReader {
   public static boolean isThemed() {
      return false;
   }

   public static boolean isXPStyleEnabled() {
      return false;
   }

   public static void paintBackground(int[] var0, String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
   }

   public static Insets getThemeMargins(String var0, int var1, int var2, int var3) {
      return null;
   }

   public static boolean isThemePartDefined(String var0, int var1, int var2) {
      return false;
   }

   public static Color getColor(String var0, int var1, int var2, int var3) {
      return null;
   }

   public static int getInt(String var0, int var1, int var2, int var3) {
      return 0;
   }

   public static int getEnum(String var0, int var1, int var2, int var3) {
      return 0;
   }

   public static boolean getBoolean(String var0, int var1, int var2, int var3) {
      return false;
   }

   public static boolean getSysBoolean(String var0, int var1) {
      return false;
   }

   public static Point getPoint(String var0, int var1, int var2, int var3) {
      return null;
   }

   public static Dimension getPosition(String var0, int var1, int var2, int var3) {
      return null;
   }

   public static Dimension getPartSize(String var0, int var1, int var2) {
      return null;
   }

   public static long getThemeTransitionDuration(String var0, int var1, int var2, int var3, int var4) {
      return 0L;
   }

   public static boolean isGetThemeTransitionDurationDefined() {
      return false;
   }

   public static Insets getThemeBackgroundContentMargins(String var0, int var1, int var2, int var3, int var4) {
      return null;
   }
}
