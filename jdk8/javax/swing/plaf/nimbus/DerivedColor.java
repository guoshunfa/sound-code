package javax.swing.plaf.nimbus;

import java.awt.Color;
import javax.swing.UIManager;

class DerivedColor extends Color {
   private final String uiDefaultParentName;
   private final float hOffset;
   private final float sOffset;
   private final float bOffset;
   private final int aOffset;
   private int argbValue;

   DerivedColor(String var1, float var2, float var3, float var4, int var5) {
      super(0);
      this.uiDefaultParentName = var1;
      this.hOffset = var2;
      this.sOffset = var3;
      this.bOffset = var4;
      this.aOffset = var5;
   }

   public String getUiDefaultParentName() {
      return this.uiDefaultParentName;
   }

   public float getHueOffset() {
      return this.hOffset;
   }

   public float getSaturationOffset() {
      return this.sOffset;
   }

   public float getBrightnessOffset() {
      return this.bOffset;
   }

   public int getAlphaOffset() {
      return this.aOffset;
   }

   public void rederiveColor() {
      Color var1 = UIManager.getColor(this.uiDefaultParentName);
      float[] var2;
      int var3;
      if (var1 != null) {
         var2 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), (float[])null);
         var2[0] = this.clamp(var2[0] + this.hOffset);
         var2[1] = this.clamp(var2[1] + this.sOffset);
         var2[2] = this.clamp(var2[2] + this.bOffset);
         var3 = this.clamp(var1.getAlpha() + this.aOffset);
         this.argbValue = Color.HSBtoRGB(var2[0], var2[1], var2[2]) & 16777215 | var3 << 24;
      } else {
         var2 = new float[]{this.clamp(this.hOffset), this.clamp(this.sOffset), this.clamp(this.bOffset)};
         var3 = this.clamp(this.aOffset);
         this.argbValue = Color.HSBtoRGB(var2[0], var2[1], var2[2]) & 16777215 | var3 << 24;
      }

   }

   public int getRGB() {
      return this.argbValue;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DerivedColor)) {
         return false;
      } else {
         DerivedColor var2 = (DerivedColor)var1;
         if (this.aOffset != var2.aOffset) {
            return false;
         } else if (Float.compare(var2.bOffset, this.bOffset) != 0) {
            return false;
         } else if (Float.compare(var2.hOffset, this.hOffset) != 0) {
            return false;
         } else if (Float.compare(var2.sOffset, this.sOffset) != 0) {
            return false;
         } else {
            return this.uiDefaultParentName.equals(var2.uiDefaultParentName);
         }
      }
   }

   public int hashCode() {
      int var1 = this.uiDefaultParentName.hashCode();
      var1 = (float)(31 * var1) + this.hOffset != 0.0F ? Float.floatToIntBits(this.hOffset) : 0;
      var1 = (float)(31 * var1) + this.sOffset != 0.0F ? Float.floatToIntBits(this.sOffset) : 0;
      var1 = (float)(31 * var1) + this.bOffset != 0.0F ? Float.floatToIntBits(this.bOffset) : 0;
      var1 = 31 * var1 + this.aOffset;
      return var1;
   }

   private float clamp(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      } else if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   private int clamp(int var1) {
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 > 255) {
         var1 = 255;
      }

      return var1;
   }

   public String toString() {
      Color var1 = UIManager.getColor(this.uiDefaultParentName);
      String var2 = "DerivedColor(color=" + this.getRed() + "," + this.getGreen() + "," + this.getBlue() + " parent=" + this.uiDefaultParentName + " offsets=" + this.getHueOffset() + "," + this.getSaturationOffset() + "," + this.getBrightnessOffset() + "," + this.getAlphaOffset();
      return var1 == null ? var2 : var2 + " pColor=" + var1.getRed() + "," + var1.getGreen() + "," + var1.getBlue();
   }

   static class UIResource extends DerivedColor implements javax.swing.plaf.UIResource {
      UIResource(String var1, float var2, float var3, float var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public boolean equals(Object var1) {
         return var1 instanceof DerivedColor.UIResource && super.equals(var1);
      }

      public int hashCode() {
         return super.hashCode() + 7;
      }
   }
}
