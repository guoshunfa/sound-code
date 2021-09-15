package com.sun.awt;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.geom.Point2D;
import sun.awt.AWTAccessor;

public final class SecurityWarning {
   private SecurityWarning() {
   }

   public static Dimension getSize(Window var0) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else if (var0.getWarningString() == null) {
         throw new IllegalArgumentException("The window must have a non-null warning string.");
      } else {
         return AWTAccessor.getWindowAccessor().getSecurityWarningSize(var0);
      }
   }

   public static void setPosition(Window var0, Point2D var1, float var2, float var3) {
      if (var0 == null) {
         throw new NullPointerException("The window argument should not be null.");
      } else if (var0.getWarningString() == null) {
         throw new IllegalArgumentException("The window must have a non-null warning string.");
      } else if (var1 == null) {
         throw new NullPointerException("The point argument must not be null");
      } else if (var2 >= 0.0F && var2 <= 1.0F) {
         if (var3 >= 0.0F && var3 <= 1.0F) {
            AWTAccessor.getWindowAccessor().setSecurityWarningPosition(var0, var1, var2, var3);
         } else {
            throw new IllegalArgumentException("alignmentY must be in the range [0.0f ... 1.0f].");
         }
      } else {
         throw new IllegalArgumentException("alignmentX must be in the range [0.0f ... 1.0f].");
      }
   }
}
