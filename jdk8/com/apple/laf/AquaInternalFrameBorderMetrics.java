package com.apple.laf;

import apple.laf.JRSUIUtils;
import java.awt.Font;

public abstract class AquaInternalFrameBorderMetrics {
   private static final boolean useLegacyBorderMetrics = JRSUIUtils.InternalFrame.shouldUseLegacyBorderMetrics();
   public Font font;
   public int titleBarHeight;
   public int leftSidePadding;
   public int buttonHeight;
   public int buttonWidth;
   public int buttonPadding;
   public int downShift;
   private static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics> standardMetrics = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics>() {
      protected AquaInternalFrameBorderMetrics getInstance() {
         return new AquaInternalFrameBorderMetrics() {
            protected void initialize() {
               this.font = new Font("Lucida Grande", 0, 13);
               this.titleBarHeight = 22;
               this.leftSidePadding = 7;
               this.buttonHeight = 15;
               this.buttonWidth = 15;
               this.buttonPadding = 5;
               this.downShift = 0;
            }
         };
      }
   };
   private static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics> utilityMetrics = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics>() {
      protected AquaInternalFrameBorderMetrics getInstance() {
         return new AquaInternalFrameBorderMetrics() {
            protected void initialize() {
               this.font = new Font("Lucida Grande", 0, 11);
               this.titleBarHeight = 16;
               this.leftSidePadding = 6;
               this.buttonHeight = 12;
               this.buttonWidth = 12;
               this.buttonPadding = 6;
               this.downShift = 0;
            }
         };
      }
   };
   private static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics> legacyStandardMetrics = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics>() {
      protected AquaInternalFrameBorderMetrics getInstance() {
         return new AquaInternalFrameBorderMetrics() {
            protected void initialize() {
               this.font = new Font("Lucida Grande", 0, 13);
               this.titleBarHeight = 22;
               this.leftSidePadding = 8;
               this.buttonHeight = 15;
               this.buttonWidth = 15;
               this.buttonPadding = 6;
               this.downShift = 1;
            }
         };
      }
   };
   private static final AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics> legacyUtilityMetrics = new AquaUtils.RecyclableSingleton<AquaInternalFrameBorderMetrics>() {
      protected AquaInternalFrameBorderMetrics getInstance() {
         return new AquaInternalFrameBorderMetrics() {
            protected void initialize() {
               this.font = new Font("Lucida Grande", 0, 11);
               this.titleBarHeight = 16;
               this.leftSidePadding = 5;
               this.buttonHeight = 13;
               this.buttonWidth = 13;
               this.buttonPadding = 5;
               this.downShift = 0;
            }
         };
      }
   };

   private AquaInternalFrameBorderMetrics() {
      this.initialize();
   }

   protected abstract void initialize();

   public static AquaInternalFrameBorderMetrics getMetrics(boolean var0) {
      if (useLegacyBorderMetrics) {
         return var0 ? (AquaInternalFrameBorderMetrics)legacyUtilityMetrics.get() : (AquaInternalFrameBorderMetrics)legacyStandardMetrics.get();
      } else {
         return var0 ? (AquaInternalFrameBorderMetrics)utilityMetrics.get() : (AquaInternalFrameBorderMetrics)standardMetrics.get();
      }
   }

   // $FF: synthetic method
   AquaInternalFrameBorderMetrics(Object var1) {
      this();
   }
}
