package javax.swing.colorchooser;

import java.awt.Component;
import java.util.Locale;
import javax.swing.UIManager;

class ColorModel {
   private final String prefix;
   private final String[] labels;

   ColorModel(String var1, String... var2) {
      this.prefix = "ColorChooser." + var1;
      this.labels = var2;
   }

   ColorModel() {
      this("rgb", "Red", "Green", "Blue", "Alpha");
   }

   void setColor(int var1, float[] var2) {
      var2[0] = normalize(var1 >> 16);
      var2[1] = normalize(var1 >> 8);
      var2[2] = normalize(var1);
      var2[3] = normalize(var1 >> 24);
   }

   int getColor(float[] var1) {
      return to8bit(var1[2]) | to8bit(var1[1]) << 8 | to8bit(var1[0]) << 16 | to8bit(var1[3]) << 24;
   }

   int getCount() {
      return this.labels.length;
   }

   int getMinimum(int var1) {
      return 0;
   }

   int getMaximum(int var1) {
      return 255;
   }

   float getDefault(int var1) {
      return 0.0F;
   }

   final String getLabel(Component var1, int var2) {
      return this.getText(var1, this.labels[var2]);
   }

   private static float normalize(int var0) {
      return (float)(var0 & 255) / 255.0F;
   }

   private static int to8bit(float var0) {
      return (int)(255.0F * var0);
   }

   final String getText(Component var1, String var2) {
      return UIManager.getString(this.prefix + var2 + "Text", (Locale)var1.getLocale());
   }

   final int getInteger(Component var1, String var2) {
      Object var3 = UIManager.get(this.prefix + var2, var1.getLocale());
      if (var3 instanceof Integer) {
         return (Integer)var3;
      } else {
         if (var3 instanceof String) {
            try {
               return Integer.parseInt((String)var3);
            } catch (NumberFormatException var5) {
            }
         }

         return -1;
      }
   }
}
