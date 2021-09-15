package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class MediaPrintableArea implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private int x;
   private int y;
   private int w;
   private int h;
   private int units;
   private static final long serialVersionUID = -1597171464050795793L;
   public static final int INCH = 25400;
   public static final int MM = 1000;

   public MediaPrintableArea(float var1, float var2, float var3, float var4, int var5) {
      if ((double)var1 >= 0.0D && (double)var2 >= 0.0D && (double)var3 > 0.0D && (double)var4 > 0.0D && var5 >= 1) {
         this.x = (int)(var1 * (float)var5 + 0.5F);
         this.y = (int)(var2 * (float)var5 + 0.5F);
         this.w = (int)(var3 * (float)var5 + 0.5F);
         this.h = (int)(var4 * (float)var5 + 0.5F);
      } else {
         throw new IllegalArgumentException("0 or negative value argument");
      }
   }

   public MediaPrintableArea(int var1, int var2, int var3, int var4, int var5) {
      if (var1 >= 0 && var2 >= 0 && var3 > 0 && var4 > 0 && var5 >= 1) {
         this.x = var1 * var5;
         this.y = var2 * var5;
         this.w = var3 * var5;
         this.h = var4 * var5;
      } else {
         throw new IllegalArgumentException("0 or negative value argument");
      }
   }

   public float[] getPrintableArea(int var1) {
      return new float[]{this.getX(var1), this.getY(var1), this.getWidth(var1), this.getHeight(var1)};
   }

   public float getX(int var1) {
      return convertFromMicrometers(this.x, var1);
   }

   public float getY(int var1) {
      return convertFromMicrometers(this.y, var1);
   }

   public float getWidth(int var1) {
      return convertFromMicrometers(this.w, var1);
   }

   public float getHeight(int var1) {
      return convertFromMicrometers(this.h, var1);
   }

   public boolean equals(Object var1) {
      boolean var2 = false;
      if (var1 instanceof MediaPrintableArea) {
         MediaPrintableArea var3 = (MediaPrintableArea)var1;
         if (this.x == var3.x && this.y == var3.y && this.w == var3.w && this.h == var3.h) {
            var2 = true;
         }
      }

      return var2;
   }

   public final Class<? extends Attribute> getCategory() {
      return MediaPrintableArea.class;
   }

   public final String getName() {
      return "media-printable-area";
   }

   public String toString(int var1, String var2) {
      if (var2 == null) {
         var2 = "";
      }

      float[] var3 = this.getPrintableArea(var1);
      String var4 = "(" + var3[0] + "," + var3[1] + ")->(" + var3[2] + "," + var3[3] + ")";
      return var4 + var2;
   }

   public String toString() {
      return this.toString(1000, "mm");
   }

   public int hashCode() {
      return this.x + 37 * this.y + 43 * this.w + 47 * this.h;
   }

   private static float convertFromMicrometers(int var0, int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("units is < 1");
      } else {
         return (float)var0 / (float)var1;
      }
   }
}
