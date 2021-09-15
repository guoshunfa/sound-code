package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class AquaButtonExtendedTypes {
   protected static final AquaUtils.RecyclableSingleton<Map<String, AquaButtonExtendedTypes.TypeSpecifier>> typeDefinitions = new AquaUtils.RecyclableSingleton<Map<String, AquaButtonExtendedTypes.TypeSpecifier>>() {
      protected Map<String, AquaButtonExtendedTypes.TypeSpecifier> getInstance() {
         return AquaButtonExtendedTypes.getAllTypes();
      }
   };

   protected static Border getBorderForPosition(AbstractButton var0, Object var1, Object var2) {
      String var3 = var2 == null ? (String)var1 : var1 + "-" + getRealPositionForLogicalPosition((String)var2, var0.getComponentOrientation().isLeftToRight());
      AquaButtonExtendedTypes.TypeSpecifier var4 = getSpecifierByName(var3);
      if (var4 == null) {
         return null;
      } else {
         Border var5 = var4.getBorder();
         return (Border)(!(var5 instanceof AquaBorder) ? var5 : ((AquaBorder)var5).deriveBorderForSize(AquaUtilControlSize.getUserSizeFrom(var0)));
      }
   }

   protected static String getRealPositionForLogicalPosition(String var0, boolean var1) {
      if (!var1) {
         if ("first".equalsIgnoreCase(var0)) {
            return "last";
         }

         if ("last".equalsIgnoreCase(var0)) {
            return "first";
         }
      }

      return var0;
   }

   protected static AquaButtonExtendedTypes.TypeSpecifier getSpecifierByName(String var0) {
      return (AquaButtonExtendedTypes.TypeSpecifier)((Map)typeDefinitions.get()).get(var0);
   }

   protected static Map<String, AquaButtonExtendedTypes.TypeSpecifier> getAllTypes() {
      HashMap var0 = new HashMap();
      Insets var1 = new Insets(4, 4, 4, 4);
      AquaButtonExtendedTypes.TypeSpecifier[] var2 = new AquaButtonExtendedTypes.TypeSpecifier[]{new AquaButtonExtendedTypes.TypeSpecifier("toolbar", true) {
         Border getBorder() {
            return AquaButtonBorder.getToolBarButtonBorder();
         }
      }, new AquaButtonExtendedTypes.TypeSpecifier("icon", true) {
         Border getBorder() {
            return AquaButtonBorder.getToggleButtonBorder();
         }
      }, new AquaButtonExtendedTypes.TypeSpecifier("text", false) {
         Border getBorder() {
            return UIManager.getBorder("Button.border");
         }
      }, new AquaButtonExtendedTypes.TypeSpecifier("toggle", false) {
         Border getBorder() {
            return AquaButtonBorder.getToggleButtonBorder();
         }
      }, new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("combobox", JRSUIConstants.Widget.BUTTON_POP_DOWN, (new AquaUtilControlSize.SizeVariant()).alterMargins(7, 10, 6, 30).alterInsets(1, 2, 0, 2).alterMinSize(0, 29), 0, -3, 0, -6) {
         void patchUp(AquaUtilControlSize.SizeDescriptor var1) {
            var1.small.alterMargins(0, 0, 0, -4);
            var1.mini.alterMargins(0, 0, 0, -6);
         }
      }, new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("comboboxInternal", JRSUIConstants.Widget.BUTTON_POP_DOWN, (new AquaUtilControlSize.SizeVariant()).alterInsets(1, 2, 0, 2).alterMinSize(0, 29), 0, -3, 0, -6), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("comboboxEndCap", JRSUIConstants.Widget.BUTTON_COMBO_BOX, (new AquaUtilControlSize.SizeVariant()).alterMargins(5, 10, 6, 10).alterInsets(1, 2, 0, 2).alterMinSize(0, 29), 0, -3, 0, -6) {
         void patchUp(AquaUtilControlSize.SizeDescriptor var1) {
            this.border.painter.state.set(JRSUIConstants.IndicatorOnly.YES);
         }
      }, new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("square", JRSUIConstants.Widget.BUTTON_BEVEL, (new AquaUtilControlSize.SizeVariant(16, 16)).alterMargins(5, 7, 5, 7).replaceInsets(var1)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("gradient", JRSUIConstants.Widget.BUTTON_BEVEL_INSET, (new AquaUtilControlSize.SizeVariant(18, 18)).alterMargins(8, 9, 8, 9).replaceInsets(var1)) {
         void patchUp(AquaUtilControlSize.SizeDescriptor var1) {
            var1.small.alterMargins(0, 0, 0, 0);
         }
      }, new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("bevel", JRSUIConstants.Widget.BUTTON_BEVEL_ROUND, (new AquaUtilControlSize.SizeVariant(22, 22)).alterMargins(7, 8, 9, 8).alterInsets(0, 0, 0, 0)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("textured", JRSUIConstants.Widget.BUTTON_PUSH_TEXTURED, (new AquaUtilControlSize.SizeVariant(28, 28)).alterMargins(5, 10, 6, 10).alterInsets(1, 2, 0, 2)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("roundRect", JRSUIConstants.Widget.BUTTON_PUSH_INSET, (new AquaUtilControlSize.SizeVariant(28, 28)).alterMargins(4, 14, 4, 14).replaceInsets(var1)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("recessed", JRSUIConstants.Widget.BUTTON_PUSH_SCOPE, (new AquaUtilControlSize.SizeVariant(28, 28)).alterMargins(4, 14, 4, 14).replaceInsets(var1)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("well", JRSUIConstants.Widget.FRAME_WELL, new AquaUtilControlSize.SizeVariant(32, 32)), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("help", JRSUIConstants.Widget.BUTTON_ROUND_HELP, (new AquaUtilControlSize.SizeVariant()).alterInsets(2, 0, 0, 0).alterMinSize(28, 28), -3, -3, -3, -3), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("round", JRSUIConstants.Widget.BUTTON_ROUND, (new AquaUtilControlSize.SizeVariant()).alterInsets(2, 0, 0, 0).alterMinSize(28, 28), -3, -3, -3, -3), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("texturedRound", JRSUIConstants.Widget.BUTTON_ROUND_INSET, (new AquaUtilControlSize.SizeVariant()).alterInsets(0, 0, 0, 0).alterMinSize(26, 26), -2, -2, 0, 0), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmented-first", JRSUIConstants.Widget.BUTTON_SEGMENTED, JRSUIConstants.SegmentPosition.FIRST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 16, 6, 10).alterInsets(2, 3, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmented-middle", JRSUIConstants.Widget.BUTTON_SEGMENTED, JRSUIConstants.SegmentPosition.MIDDLE, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 9, 6, 10).alterInsets(2, 0, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmented-last", JRSUIConstants.Widget.BUTTON_SEGMENTED, JRSUIConstants.SegmentPosition.LAST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 9, 6, 16).alterInsets(2, 0, 2, 3).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmented-only", JRSUIConstants.Widget.BUTTON_SEGMENTED, JRSUIConstants.SegmentPosition.ONLY, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 16, 6, 16).alterInsets(2, 3, 2, 3).alterMinSize(34, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedRoundRect-first", JRSUIConstants.Widget.BUTTON_SEGMENTED_INSET, JRSUIConstants.SegmentPosition.FIRST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 8).alterInsets(2, 2, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedRoundRect-middle", JRSUIConstants.Widget.BUTTON_SEGMENTED_INSET, JRSUIConstants.SegmentPosition.MIDDLE, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 8).alterInsets(2, 0, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedRoundRect-last", JRSUIConstants.Widget.BUTTON_SEGMENTED_INSET, JRSUIConstants.SegmentPosition.LAST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 12).alterInsets(2, 0, 2, 2).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedRoundRect-only", JRSUIConstants.Widget.BUTTON_SEGMENTED_INSET, JRSUIConstants.SegmentPosition.ONLY, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 12).alterInsets(2, 2, 2, 2).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTexturedRounded-first", JRSUIConstants.Widget.BUTTON_SEGMENTED_SCURVE, JRSUIConstants.SegmentPosition.FIRST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 8).alterInsets(2, 2, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTexturedRounded-middle", JRSUIConstants.Widget.BUTTON_SEGMENTED_SCURVE, JRSUIConstants.SegmentPosition.MIDDLE, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 8).alterInsets(2, 0, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTexturedRounded-last", JRSUIConstants.Widget.BUTTON_SEGMENTED_SCURVE, JRSUIConstants.SegmentPosition.LAST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 12).alterInsets(2, 0, 2, 2).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTexturedRounded-only", JRSUIConstants.Widget.BUTTON_SEGMENTED_SCURVE, JRSUIConstants.SegmentPosition.ONLY, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 12).alterInsets(2, 2, 2, 2).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTextured-first", JRSUIConstants.Widget.BUTTON_SEGMENTED_TEXTURED, JRSUIConstants.SegmentPosition.FIRST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 8).alterInsets(2, 3, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTextured-middle", JRSUIConstants.Widget.BUTTON_SEGMENTED_TEXTURED, JRSUIConstants.SegmentPosition.MIDDLE, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 8).alterInsets(2, 0, 2, 0).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTextured-last", JRSUIConstants.Widget.BUTTON_SEGMENTED_TEXTURED, JRSUIConstants.SegmentPosition.LAST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 12).alterInsets(2, 0, 2, 3).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedTextured-only", JRSUIConstants.Widget.BUTTON_SEGMENTED_TEXTURED, JRSUIConstants.SegmentPosition.ONLY, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 12).alterInsets(2, 3, 2, 3).alterMinSize(0, 28), 0, -3, 0, -3), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedCapsule-first", JRSUIConstants.Widget.BUTTON_SEGMENTED_TOOLBAR, JRSUIConstants.SegmentPosition.FIRST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 8).alterInsets(2, 2, 2, 0).alterMinSize(0, 28), 0, 0, 0, 0), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedCapsule-middle", JRSUIConstants.Widget.BUTTON_SEGMENTED_TOOLBAR, JRSUIConstants.SegmentPosition.MIDDLE, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 8).alterInsets(2, 0, 2, 0).alterMinSize(0, 28), 0, 0, 0, 0), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedCapsule-last", JRSUIConstants.Widget.BUTTON_SEGMENTED_TOOLBAR, JRSUIConstants.SegmentPosition.LAST, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 8, 6, 12).alterInsets(2, 0, 2, 2).alterMinSize(0, 28), 0, 0, 0, 0), new AquaButtonExtendedTypes.SegmentedBorderDefinedTypeSpecifier("segmentedCapsule-only", JRSUIConstants.Widget.BUTTON_SEGMENTED_TOOLBAR, JRSUIConstants.SegmentPosition.ONLY, (new AquaUtilControlSize.SizeVariant()).alterMargins(6, 12, 6, 12).alterInsets(2, 2, 2, 2).alterMinSize(34, 28), 0, 0, 0, 0), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("segmentedGradient-first", JRSUIConstants.Widget.BUTTON_BEVEL_INSET, (new AquaUtilControlSize.SizeVariant(18, 18)).alterMargins(4, 5, 4, 5).replaceInsets(new Insets(-2, 0, -2, 0))), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("segmentedGradient-middle", JRSUIConstants.Widget.BUTTON_BEVEL_INSET, (new AquaUtilControlSize.SizeVariant(18, 18)).alterMargins(4, 5, 4, 5).replaceInsets(new Insets(-2, -1, -2, 0))), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("segmentedGradient-last", JRSUIConstants.Widget.BUTTON_BEVEL_INSET, (new AquaUtilControlSize.SizeVariant(18, 18)).alterMargins(4, 5, 4, 5).replaceInsets(new Insets(-2, -1, -2, 0))), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("segmentedGradient-only", JRSUIConstants.Widget.BUTTON_BEVEL_INSET, (new AquaUtilControlSize.SizeVariant(18, 18)).alterMargins(4, 5, 4, 5).replaceInsets(new Insets(-2, -1, -2, -1))), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("disclosure", JRSUIConstants.Widget.BUTTON_DISCLOSURE, (new AquaUtilControlSize.SizeVariant()).alterMargins(10, 10, 10, 10).replaceInsets(var1).alterMinSize(27, 27), -1, -1, -1, -1), new AquaButtonExtendedTypes.BorderDefinedTypeSpecifier("scrollColumnSizer", JRSUIConstants.Widget.SCROLL_COLUMN_SIZER, new AquaUtilControlSize.SizeVariant(14, 14))};
      AquaButtonExtendedTypes.TypeSpecifier[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         AquaButtonExtendedTypes.TypeSpecifier var6 = var3[var5];
         var0.put(var6.name, var6);
      }

      return var0;
   }

   public static class SegmentedNamedBorder extends AquaButtonBorder.Named {
      public SegmentedNamedBorder(AquaButtonExtendedTypes.SegmentedNamedBorder var1) {
         super(var1);
      }

      public SegmentedNamedBorder(JRSUIConstants.Widget var1, AquaUtilControlSize.SizeDescriptor var2) {
         super(var1, var2);
      }

      protected boolean isSelectionPressing() {
         return false;
      }
   }

   static class SegmentedBorderDefinedTypeSpecifier extends AquaButtonExtendedTypes.BorderDefinedTypeSpecifier {
      public SegmentedBorderDefinedTypeSpecifier(String var1, JRSUIConstants.Widget var2, JRSUIConstants.SegmentPosition var3, AquaUtilControlSize.SizeVariant var4) {
         this(var1, var2, var3, var4, 0, 0, 0, 0);
      }

      public SegmentedBorderDefinedTypeSpecifier(String var1, JRSUIConstants.Widget var2, JRSUIConstants.SegmentPosition var3, AquaUtilControlSize.SizeVariant var4, int var5, int var6, int var7, int var8) {
         super(var1, var2, var4, var5, var6, var7, var8);
         this.border.painter.state.set(JRSUIConstants.SegmentTrailingSeparator.YES);
         this.border.painter.state.set(var3);
      }

      AquaBorder initBorder(JRSUIConstants.Widget var1, AquaUtilControlSize.SizeDescriptor var2) {
         return new AquaButtonExtendedTypes.SegmentedNamedBorder(var1, var2);
      }
   }

   static class BorderDefinedTypeSpecifier extends AquaButtonExtendedTypes.TypeSpecifier {
      final AquaBorder border;

      BorderDefinedTypeSpecifier(String var1, JRSUIConstants.Widget var2, AquaUtilControlSize.SizeVariant var3) {
         this(var1, var2, var3, 0, 0, 0, 0);
      }

      BorderDefinedTypeSpecifier(String var1, JRSUIConstants.Widget var2, AquaUtilControlSize.SizeVariant var3, final int var4, final int var5, final int var6, final int var7) {
         super(var1, false);
         this.border = this.initBorder(var2, new AquaUtilControlSize.SizeDescriptor(var3) {
            public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
               var1.alterMinSize(var4, var5);
               return super.deriveSmall(var1);
            }

            public AquaUtilControlSize.SizeVariant deriveMini(AquaUtilControlSize.SizeVariant var1) {
               var1.alterMinSize(var6, var7);
               return super.deriveMini(var1);
            }
         });
         this.patchUp(this.border.sizeDescriptor);
      }

      Border getBorder() {
         return this.border;
      }

      void patchUp(AquaUtilControlSize.SizeDescriptor var1) {
      }

      AquaBorder initBorder(JRSUIConstants.Widget var1, AquaUtilControlSize.SizeDescriptor var2) {
         return new AquaButtonBorder.Named(var1, var2);
      }
   }

   abstract static class TypeSpecifier {
      final String name;
      final boolean setIconFont;

      TypeSpecifier(String var1, boolean var2) {
         this.name = var1;
         this.setIconFont = var2;
      }

      abstract Border getBorder();
   }
}
