package java.awt.geom;

import java.io.Serializable;

public abstract class RoundRectangle2D extends RectangularShape {
   protected RoundRectangle2D() {
   }

   public abstract double getArcWidth();

   public abstract double getArcHeight();

   public abstract void setRoundRect(double var1, double var3, double var5, double var7, double var9, double var11);

   public void setRoundRect(RoundRectangle2D var1) {
      this.setRoundRect(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight(), var1.getArcWidth(), var1.getArcHeight());
   }

   public void setFrame(double var1, double var3, double var5, double var7) {
      this.setRoundRect(var1, var3, var5, var7, this.getArcWidth(), this.getArcHeight());
   }

   public boolean contains(double var1, double var3) {
      if (this.isEmpty()) {
         return false;
      } else {
         double var5 = this.getX();
         double var7 = this.getY();
         double var9 = var5 + this.getWidth();
         double var11 = var7 + this.getHeight();
         if (var1 >= var5 && var3 >= var7 && var1 < var9 && var3 < var11) {
            double var13 = Math.min(this.getWidth(), Math.abs(this.getArcWidth())) / 2.0D;
            double var15 = Math.min(this.getHeight(), Math.abs(this.getArcHeight())) / 2.0D;
            if (var1 >= (var5 += var13) && var1 < (var5 = var9 - var13)) {
               return true;
            } else if (var3 >= (var7 += var15) && var3 < (var7 = var11 - var15)) {
               return true;
            } else {
               var1 = (var1 - var5) / var13;
               var3 = (var3 - var7) / var15;
               return var1 * var1 + var3 * var3 <= 1.0D;
            }
         } else {
            return false;
         }
      }
   }

   private int classify(double var1, double var3, double var5, double var7) {
      if (var1 < var3) {
         return 0;
      } else if (var1 < var3 + var7) {
         return 1;
      } else if (var1 < var5 - var7) {
         return 2;
      } else {
         return var1 < var5 ? 3 : 4;
      }
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (!this.isEmpty() && var5 > 0.0D && var7 > 0.0D) {
         double var9 = this.getX();
         double var11 = this.getY();
         double var13 = var9 + this.getWidth();
         double var15 = var11 + this.getHeight();
         if (var1 + var5 > var9 && var1 < var13 && var3 + var7 > var11 && var3 < var15) {
            double var17 = Math.min(this.getWidth(), Math.abs(this.getArcWidth())) / 2.0D;
            double var19 = Math.min(this.getHeight(), Math.abs(this.getArcHeight())) / 2.0D;
            int var21 = this.classify(var1, var9, var13, var17);
            int var22 = this.classify(var1 + var5, var9, var13, var17);
            int var23 = this.classify(var3, var11, var15, var19);
            int var24 = this.classify(var3 + var7, var11, var15, var19);
            if (var21 != 2 && var22 != 2 && var23 != 2 && var24 != 2) {
               if ((var21 >= 2 || var22 <= 2) && (var23 >= 2 || var24 <= 2)) {
                  var1 = var22 == 1 ? var1 + var5 - (var9 + var17) : var1 - (var13 - var17);
                  var3 = var24 == 1 ? var3 + var7 - (var11 + var19) : var3 - (var15 - var19);
                  var1 /= var17;
                  var3 /= var19;
                  return var1 * var1 + var3 * var3 <= 1.0D;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (!this.isEmpty() && var5 > 0.0D && var7 > 0.0D) {
         return this.contains(var1, var3) && this.contains(var1 + var5, var3) && this.contains(var1, var3 + var7) && this.contains(var1 + var5, var3 + var7);
      } else {
         return false;
      }
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new RoundRectIterator(this, var1);
   }

   public int hashCode() {
      long var1 = java.lang.Double.doubleToLongBits(this.getX());
      var1 += java.lang.Double.doubleToLongBits(this.getY()) * 37L;
      var1 += java.lang.Double.doubleToLongBits(this.getWidth()) * 43L;
      var1 += java.lang.Double.doubleToLongBits(this.getHeight()) * 47L;
      var1 += java.lang.Double.doubleToLongBits(this.getArcWidth()) * 53L;
      var1 += java.lang.Double.doubleToLongBits(this.getArcHeight()) * 59L;
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof RoundRectangle2D)) {
         return false;
      } else {
         RoundRectangle2D var2 = (RoundRectangle2D)var1;
         return this.getX() == var2.getX() && this.getY() == var2.getY() && this.getWidth() == var2.getWidth() && this.getHeight() == var2.getHeight() && this.getArcWidth() == var2.getArcWidth() && this.getArcHeight() == var2.getArcHeight();
      }
   }

   public static class Double extends RoundRectangle2D implements Serializable {
      public double x;
      public double y;
      public double width;
      public double height;
      public double arcwidth;
      public double archeight;
      private static final long serialVersionUID = 1048939333485206117L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.setRoundRect(var1, var3, var5, var7, var9, var11);
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getWidth() {
         return this.width;
      }

      public double getHeight() {
         return this.height;
      }

      public double getArcWidth() {
         return this.arcwidth;
      }

      public double getArcHeight() {
         return this.archeight;
      }

      public boolean isEmpty() {
         return this.width <= 0.0D || this.height <= 0.0D;
      }

      public void setRoundRect(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.x = var1;
         this.y = var3;
         this.width = var5;
         this.height = var7;
         this.arcwidth = var9;
         this.archeight = var11;
      }

      public void setRoundRect(RoundRectangle2D var1) {
         this.x = var1.getX();
         this.y = var1.getY();
         this.width = var1.getWidth();
         this.height = var1.getHeight();
         this.arcwidth = var1.getArcWidth();
         this.archeight = var1.getArcHeight();
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
      }
   }

   public static class Float extends RoundRectangle2D implements Serializable {
      public float x;
      public float y;
      public float width;
      public float height;
      public float arcwidth;
      public float archeight;
      private static final long serialVersionUID = -3423150618393866922L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.setRoundRect(var1, var2, var3, var4, var5, var6);
      }

      public double getX() {
         return (double)this.x;
      }

      public double getY() {
         return (double)this.y;
      }

      public double getWidth() {
         return (double)this.width;
      }

      public double getHeight() {
         return (double)this.height;
      }

      public double getArcWidth() {
         return (double)this.arcwidth;
      }

      public double getArcHeight() {
         return (double)this.archeight;
      }

      public boolean isEmpty() {
         return this.width <= 0.0F || this.height <= 0.0F;
      }

      public void setRoundRect(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
         this.arcwidth = var5;
         this.archeight = var6;
      }

      public void setRoundRect(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.x = (float)var1;
         this.y = (float)var3;
         this.width = (float)var5;
         this.height = (float)var7;
         this.arcwidth = (float)var9;
         this.archeight = (float)var11;
      }

      public void setRoundRect(RoundRectangle2D var1) {
         this.x = (float)var1.getX();
         this.y = (float)var1.getY();
         this.width = (float)var1.getWidth();
         this.height = (float)var1.getHeight();
         this.arcwidth = (float)var1.getArcWidth();
         this.archeight = (float)var1.getArcHeight();
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
      }
   }
}
