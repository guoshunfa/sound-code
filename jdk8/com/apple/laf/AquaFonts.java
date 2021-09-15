package com.apple.laf;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;

public class AquaFonts {
   private static final String MAC_DEFAULT_FONT_NAME = "Lucida Grande";
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida9Pt = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 0, 9);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida11Pt = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 0, 11);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida12Pt = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 0, 12);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida13Pt = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 0, 13);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida14Pt = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 0, 14);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida13PtBold = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 1, 13);
      }
   };
   private static final AquaUtils.RecyclableSingleton<FontUIResource> lucida14PtBold = new AquaUtils.RecyclableSingleton<FontUIResource>() {
      protected FontUIResource getInstance() {
         return new AquaFonts.DerivedUIResourceFont("Lucida Grande", 1, 14);
      }
   };

   protected static FontUIResource getMiniControlTextFont() {
      return (FontUIResource)lucida9Pt.get();
   }

   protected static FontUIResource getSmallControlTextFont() {
      return (FontUIResource)lucida11Pt.get();
   }

   public static FontUIResource getControlTextFont() {
      return (FontUIResource)lucida13Pt.get();
   }

   public static FontUIResource getControlTextSmallFont() {
      return (FontUIResource)lucida11Pt.get();
   }

   public static FontUIResource getMenuFont() {
      return (FontUIResource)lucida14Pt.get();
   }

   public static Font getDockIconFont() {
      return (Font)lucida14PtBold.get();
   }

   public static FontUIResource getAlertHeaderFont() {
      return (FontUIResource)lucida13PtBold.get();
   }

   public static FontUIResource getAlertMessageFont() {
      return (FontUIResource)lucida11Pt.get();
   }

   public static FontUIResource getViewFont() {
      return (FontUIResource)lucida12Pt.get();
   }

   static class DerivedUIResourceFont extends FontUIResource implements UIResource {
      public DerivedUIResourceFont(Font var1) {
         super(var1);
      }

      public DerivedUIResourceFont(String var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public Font deriveFont(AffineTransform var1) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1));
      }

      public Font deriveFont(float var1) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1));
      }

      public Font deriveFont(int var1) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1));
      }

      public Font deriveFont(int var1, AffineTransform var2) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1, var2));
      }

      public Font deriveFont(int var1, float var2) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1, var2));
      }

      public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> var1) {
         return new AquaFonts.DerivedUIResourceFont(super.deriveFont(var1));
      }
   }
}
