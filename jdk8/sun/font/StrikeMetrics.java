package sun.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public final class StrikeMetrics {
   public float ascentX;
   public float ascentY;
   public float descentX;
   public float descentY;
   public float baselineX;
   public float baselineY;
   public float leadingX;
   public float leadingY;
   public float maxAdvanceX;
   public float maxAdvanceY;

   StrikeMetrics() {
      this.ascentX = this.ascentY = 2.14748365E9F;
      this.descentX = this.descentY = this.leadingX = this.leadingY = -2.14748365E9F;
      this.baselineX = this.baselineX = this.maxAdvanceX = this.maxAdvanceY = -2.14748365E9F;
   }

   StrikeMetrics(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.ascentX = var1;
      this.ascentY = var2;
      this.descentX = var3;
      this.descentY = var4;
      this.baselineX = var5;
      this.baselineY = var6;
      this.leadingX = var7;
      this.leadingY = var8;
      this.maxAdvanceX = var9;
      this.maxAdvanceY = var10;
   }

   public float getAscent() {
      return -this.ascentY;
   }

   public float getDescent() {
      return this.descentY;
   }

   public float getLeading() {
      return this.leadingY;
   }

   public float getMaxAdvance() {
      return this.maxAdvanceX;
   }

   void merge(StrikeMetrics var1) {
      if (var1 != null) {
         if (var1.ascentX < this.ascentX) {
            this.ascentX = var1.ascentX;
         }

         if (var1.ascentY < this.ascentY) {
            this.ascentY = var1.ascentY;
         }

         if (var1.descentX > this.descentX) {
            this.descentX = var1.descentX;
         }

         if (var1.descentY > this.descentY) {
            this.descentY = var1.descentY;
         }

         if (var1.baselineX > this.baselineX) {
            this.baselineX = var1.baselineX;
         }

         if (var1.baselineY > this.baselineY) {
            this.baselineY = var1.baselineY;
         }

         if (var1.leadingX > this.leadingX) {
            this.leadingX = var1.leadingX;
         }

         if (var1.leadingY > this.leadingY) {
            this.leadingY = var1.leadingY;
         }

         if (var1.maxAdvanceX > this.maxAdvanceX) {
            this.maxAdvanceX = var1.maxAdvanceX;
         }

         if (var1.maxAdvanceY > this.maxAdvanceY) {
            this.maxAdvanceY = var1.maxAdvanceY;
         }

      }
   }

   void convertToUserSpace(AffineTransform var1) {
      Point2D.Float var2 = new Point2D.Float();
      var2.x = this.ascentX;
      var2.y = this.ascentY;
      var1.deltaTransform(var2, var2);
      this.ascentX = var2.x;
      this.ascentY = var2.y;
      var2.x = this.descentX;
      var2.y = this.descentY;
      var1.deltaTransform(var2, var2);
      this.descentX = var2.x;
      this.descentY = var2.y;
      var2.x = this.baselineX;
      var2.y = this.baselineY;
      var1.deltaTransform(var2, var2);
      this.baselineX = var2.x;
      this.baselineY = var2.y;
      var2.x = this.leadingX;
      var2.y = this.leadingY;
      var1.deltaTransform(var2, var2);
      this.leadingX = var2.x;
      this.leadingY = var2.y;
      var2.x = this.maxAdvanceX;
      var2.y = this.maxAdvanceY;
      var1.deltaTransform(var2, var2);
      this.maxAdvanceX = var2.x;
      this.maxAdvanceY = var2.y;
   }

   public String toString() {
      return "ascent:x=" + this.ascentX + " y=" + this.ascentY + " descent:x=" + this.descentX + " y=" + this.descentY + " baseline:x=" + this.baselineX + " y=" + this.baselineY + " leading:x=" + this.leadingX + " y=" + this.leadingY + " maxAdvance:x=" + this.maxAdvanceX + " y=" + this.maxAdvanceY;
   }
}
