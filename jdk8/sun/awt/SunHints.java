package sun.awt;

import java.awt.RenderingHints;

public class SunHints {
   private static final int NUM_KEYS = 10;
   private static final int VALS_PER_KEY = 8;
   public static final int INTKEY_RENDERING = 0;
   public static final int INTVAL_RENDER_DEFAULT = 0;
   public static final int INTVAL_RENDER_SPEED = 1;
   public static final int INTVAL_RENDER_QUALITY = 2;
   public static final int INTKEY_ANTIALIASING = 1;
   public static final int INTVAL_ANTIALIAS_DEFAULT = 0;
   public static final int INTVAL_ANTIALIAS_OFF = 1;
   public static final int INTVAL_ANTIALIAS_ON = 2;
   public static final int INTKEY_TEXT_ANTIALIASING = 2;
   public static final int INTVAL_TEXT_ANTIALIAS_DEFAULT = 0;
   public static final int INTVAL_TEXT_ANTIALIAS_OFF = 1;
   public static final int INTVAL_TEXT_ANTIALIAS_ON = 2;
   public static final int INTVAL_TEXT_ANTIALIAS_GASP = 3;
   public static final int INTVAL_TEXT_ANTIALIAS_LCD_HRGB = 4;
   public static final int INTVAL_TEXT_ANTIALIAS_LCD_HBGR = 5;
   public static final int INTVAL_TEXT_ANTIALIAS_LCD_VRGB = 6;
   public static final int INTVAL_TEXT_ANTIALIAS_LCD_VBGR = 7;
   public static final int INTKEY_FRACTIONALMETRICS = 3;
   public static final int INTVAL_FRACTIONALMETRICS_DEFAULT = 0;
   public static final int INTVAL_FRACTIONALMETRICS_OFF = 1;
   public static final int INTVAL_FRACTIONALMETRICS_ON = 2;
   public static final int INTKEY_DITHERING = 4;
   public static final int INTVAL_DITHER_DEFAULT = 0;
   public static final int INTVAL_DITHER_DISABLE = 1;
   public static final int INTVAL_DITHER_ENABLE = 2;
   public static final int INTKEY_INTERPOLATION = 5;
   public static final int INTVAL_INTERPOLATION_NEAREST_NEIGHBOR = 0;
   public static final int INTVAL_INTERPOLATION_BILINEAR = 1;
   public static final int INTVAL_INTERPOLATION_BICUBIC = 2;
   public static final int INTKEY_ALPHA_INTERPOLATION = 6;
   public static final int INTVAL_ALPHA_INTERPOLATION_DEFAULT = 0;
   public static final int INTVAL_ALPHA_INTERPOLATION_SPEED = 1;
   public static final int INTVAL_ALPHA_INTERPOLATION_QUALITY = 2;
   public static final int INTKEY_COLOR_RENDERING = 7;
   public static final int INTVAL_COLOR_RENDER_DEFAULT = 0;
   public static final int INTVAL_COLOR_RENDER_SPEED = 1;
   public static final int INTVAL_COLOR_RENDER_QUALITY = 2;
   public static final int INTKEY_STROKE_CONTROL = 8;
   public static final int INTVAL_STROKE_DEFAULT = 0;
   public static final int INTVAL_STROKE_NORMALIZE = 1;
   public static final int INTVAL_STROKE_PURE = 2;
   public static final int INTKEY_RESOLUTION_VARIANT = 9;
   public static final int INTVAL_RESOLUTION_VARIANT_DEFAULT = 0;
   public static final int INTVAL_RESOLUTION_VARIANT_OFF = 1;
   public static final int INTVAL_RESOLUTION_VARIANT_ON = 2;
   public static final int INTKEY_AATEXT_LCD_CONTRAST = 100;
   public static final SunHints.Key KEY_RENDERING = new SunHints.Key(0, "Global rendering quality key");
   public static final Object VALUE_RENDER_SPEED;
   public static final Object VALUE_RENDER_QUALITY;
   public static final Object VALUE_RENDER_DEFAULT;
   public static final SunHints.Key KEY_ANTIALIASING;
   public static final Object VALUE_ANTIALIAS_ON;
   public static final Object VALUE_ANTIALIAS_OFF;
   public static final Object VALUE_ANTIALIAS_DEFAULT;
   public static final SunHints.Key KEY_TEXT_ANTIALIASING;
   public static final Object VALUE_TEXT_ANTIALIAS_ON;
   public static final Object VALUE_TEXT_ANTIALIAS_OFF;
   public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT;
   public static final Object VALUE_TEXT_ANTIALIAS_GASP;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB;
   public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR;
   public static final SunHints.Key KEY_FRACTIONALMETRICS;
   public static final Object VALUE_FRACTIONALMETRICS_ON;
   public static final Object VALUE_FRACTIONALMETRICS_OFF;
   public static final Object VALUE_FRACTIONALMETRICS_DEFAULT;
   public static final SunHints.Key KEY_DITHERING;
   public static final Object VALUE_DITHER_ENABLE;
   public static final Object VALUE_DITHER_DISABLE;
   public static final Object VALUE_DITHER_DEFAULT;
   public static final SunHints.Key KEY_INTERPOLATION;
   public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
   public static final Object VALUE_INTERPOLATION_BILINEAR;
   public static final Object VALUE_INTERPOLATION_BICUBIC;
   public static final SunHints.Key KEY_ALPHA_INTERPOLATION;
   public static final Object VALUE_ALPHA_INTERPOLATION_SPEED;
   public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY;
   public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT;
   public static final SunHints.Key KEY_COLOR_RENDERING;
   public static final Object VALUE_COLOR_RENDER_SPEED;
   public static final Object VALUE_COLOR_RENDER_QUALITY;
   public static final Object VALUE_COLOR_RENDER_DEFAULT;
   public static final SunHints.Key KEY_STROKE_CONTROL;
   public static final Object VALUE_STROKE_DEFAULT;
   public static final Object VALUE_STROKE_NORMALIZE;
   public static final Object VALUE_STROKE_PURE;
   public static final SunHints.Key KEY_RESOLUTION_VARIANT;
   public static final Object VALUE_RESOLUTION_VARIANT_DEFAULT;
   public static final Object VALUE_RESOLUTION_VARIANT_OFF;
   public static final Object VALUE_RESOLUTION_VARIANT_ON;
   public static final RenderingHints.Key KEY_TEXT_ANTIALIAS_LCD_CONTRAST;

   static {
      VALUE_RENDER_SPEED = new SunHints.Value(KEY_RENDERING, 1, "Fastest rendering methods");
      VALUE_RENDER_QUALITY = new SunHints.Value(KEY_RENDERING, 2, "Highest quality rendering methods");
      VALUE_RENDER_DEFAULT = new SunHints.Value(KEY_RENDERING, 0, "Default rendering methods");
      KEY_ANTIALIASING = new SunHints.Key(1, "Global antialiasing enable key");
      VALUE_ANTIALIAS_ON = new SunHints.Value(KEY_ANTIALIASING, 2, "Antialiased rendering mode");
      VALUE_ANTIALIAS_OFF = new SunHints.Value(KEY_ANTIALIASING, 1, "Nonantialiased rendering mode");
      VALUE_ANTIALIAS_DEFAULT = new SunHints.Value(KEY_ANTIALIASING, 0, "Default antialiasing rendering mode");
      KEY_TEXT_ANTIALIASING = new SunHints.Key(2, "Text-specific antialiasing enable key");
      VALUE_TEXT_ANTIALIAS_ON = new SunHints.Value(KEY_TEXT_ANTIALIASING, 2, "Antialiased text mode");
      VALUE_TEXT_ANTIALIAS_OFF = new SunHints.Value(KEY_TEXT_ANTIALIASING, 1, "Nonantialiased text mode");
      VALUE_TEXT_ANTIALIAS_DEFAULT = new SunHints.Value(KEY_TEXT_ANTIALIASING, 0, "Default antialiasing text mode");
      VALUE_TEXT_ANTIALIAS_GASP = new SunHints.Value(KEY_TEXT_ANTIALIASING, 3, "gasp antialiasing text mode");
      VALUE_TEXT_ANTIALIAS_LCD_HRGB = new SunHints.Value(KEY_TEXT_ANTIALIASING, 4, "LCD HRGB antialiasing text mode");
      VALUE_TEXT_ANTIALIAS_LCD_HBGR = new SunHints.Value(KEY_TEXT_ANTIALIASING, 5, "LCD HBGR antialiasing text mode");
      VALUE_TEXT_ANTIALIAS_LCD_VRGB = new SunHints.Value(KEY_TEXT_ANTIALIASING, 6, "LCD VRGB antialiasing text mode");
      VALUE_TEXT_ANTIALIAS_LCD_VBGR = new SunHints.Value(KEY_TEXT_ANTIALIASING, 7, "LCD VBGR antialiasing text mode");
      KEY_FRACTIONALMETRICS = new SunHints.Key(3, "Fractional metrics enable key");
      VALUE_FRACTIONALMETRICS_ON = new SunHints.Value(KEY_FRACTIONALMETRICS, 2, "Fractional text metrics mode");
      VALUE_FRACTIONALMETRICS_OFF = new SunHints.Value(KEY_FRACTIONALMETRICS, 1, "Integer text metrics mode");
      VALUE_FRACTIONALMETRICS_DEFAULT = new SunHints.Value(KEY_FRACTIONALMETRICS, 0, "Default fractional text metrics mode");
      KEY_DITHERING = new SunHints.Key(4, "Dithering quality key");
      VALUE_DITHER_ENABLE = new SunHints.Value(KEY_DITHERING, 2, "Dithered rendering mode");
      VALUE_DITHER_DISABLE = new SunHints.Value(KEY_DITHERING, 1, "Nondithered rendering mode");
      VALUE_DITHER_DEFAULT = new SunHints.Value(KEY_DITHERING, 0, "Default dithering mode");
      KEY_INTERPOLATION = new SunHints.Key(5, "Image interpolation method key");
      VALUE_INTERPOLATION_NEAREST_NEIGHBOR = new SunHints.Value(KEY_INTERPOLATION, 0, "Nearest Neighbor image interpolation mode");
      VALUE_INTERPOLATION_BILINEAR = new SunHints.Value(KEY_INTERPOLATION, 1, "Bilinear image interpolation mode");
      VALUE_INTERPOLATION_BICUBIC = new SunHints.Value(KEY_INTERPOLATION, 2, "Bicubic image interpolation mode");
      KEY_ALPHA_INTERPOLATION = new SunHints.Key(6, "Alpha blending interpolation method key");
      VALUE_ALPHA_INTERPOLATION_SPEED = new SunHints.Value(KEY_ALPHA_INTERPOLATION, 1, "Fastest alpha blending methods");
      VALUE_ALPHA_INTERPOLATION_QUALITY = new SunHints.Value(KEY_ALPHA_INTERPOLATION, 2, "Highest quality alpha blending methods");
      VALUE_ALPHA_INTERPOLATION_DEFAULT = new SunHints.Value(KEY_ALPHA_INTERPOLATION, 0, "Default alpha blending methods");
      KEY_COLOR_RENDERING = new SunHints.Key(7, "Color rendering quality key");
      VALUE_COLOR_RENDER_SPEED = new SunHints.Value(KEY_COLOR_RENDERING, 1, "Fastest color rendering mode");
      VALUE_COLOR_RENDER_QUALITY = new SunHints.Value(KEY_COLOR_RENDERING, 2, "Highest quality color rendering mode");
      VALUE_COLOR_RENDER_DEFAULT = new SunHints.Value(KEY_COLOR_RENDERING, 0, "Default color rendering mode");
      KEY_STROKE_CONTROL = new SunHints.Key(8, "Stroke normalization control key");
      VALUE_STROKE_DEFAULT = new SunHints.Value(KEY_STROKE_CONTROL, 0, "Default stroke normalization");
      VALUE_STROKE_NORMALIZE = new SunHints.Value(KEY_STROKE_CONTROL, 1, "Normalize strokes for consistent rendering");
      VALUE_STROKE_PURE = new SunHints.Value(KEY_STROKE_CONTROL, 2, "Pure stroke conversion for accurate paths");
      KEY_RESOLUTION_VARIANT = new SunHints.Key(9, "Global image resolution variant key");
      VALUE_RESOLUTION_VARIANT_DEFAULT = new SunHints.Value(KEY_RESOLUTION_VARIANT, 0, "Choose image resolutions based on a default heuristic");
      VALUE_RESOLUTION_VARIANT_OFF = new SunHints.Value(KEY_RESOLUTION_VARIANT, 1, "Use only the standard resolution of an image");
      VALUE_RESOLUTION_VARIANT_ON = new SunHints.Value(KEY_RESOLUTION_VARIANT, 2, "Always use resolution-specific variants of images");
      KEY_TEXT_ANTIALIAS_LCD_CONTRAST = new SunHints.LCDContrastKey(100, "Text-specific LCD contrast key");
   }

   public static class LCDContrastKey extends SunHints.Key {
      public LCDContrastKey(int var1, String var2) {
         super(var1, var2);
      }

      public final boolean isCompatibleValue(Object var1) {
         if (!(var1 instanceof Integer)) {
            return false;
         } else {
            int var2 = (Integer)var1;
            return var2 >= 100 && var2 <= 250;
         }
      }
   }

   public static class Value {
      private SunHints.Key myKey;
      private int index;
      private String description;
      private static SunHints.Value[][] ValueObjects = new SunHints.Value[10][8];

      private static synchronized void register(SunHints.Key var0, SunHints.Value var1) {
         int var2 = var0.getIndex();
         int var3 = var1.getIndex();
         if (ValueObjects[var2][var3] != null) {
            throw new InternalError("duplicate index: " + var3);
         } else {
            ValueObjects[var2][var3] = var1;
         }
      }

      public static SunHints.Value get(int var0, int var1) {
         return ValueObjects[var0][var1];
      }

      public Value(SunHints.Key var1, int var2, String var3) {
         this.myKey = var1;
         this.index = var2;
         this.description = var3;
         register(var1, this);
      }

      public final int getIndex() {
         return this.index;
      }

      public final String toString() {
         return this.description;
      }

      public final boolean isCompatibleKey(SunHints.Key var1) {
         return this.myKey == var1;
      }

      public final int hashCode() {
         return System.identityHashCode(this);
      }

      public final boolean equals(Object var1) {
         return this == var1;
      }
   }

   public static class Key extends RenderingHints.Key {
      String description;

      public Key(int var1, String var2) {
         super(var1);
         this.description = var2;
      }

      public final int getIndex() {
         return this.intKey();
      }

      public final String toString() {
         return this.description;
      }

      public boolean isCompatibleValue(Object var1) {
         return var1 instanceof SunHints.Value ? ((SunHints.Value)var1).isCompatibleKey(this) : false;
      }
   }
}
