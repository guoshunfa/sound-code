package java.awt.geom;

import java.io.Serializable;

public abstract class Ellipse2D extends RectangularShape {
   protected Ellipse2D() {
   }

   public boolean contains(double var1, double var3) {
      double var5 = this.getWidth();
      if (var5 <= 0.0D) {
         return false;
      } else {
         double var7 = (var1 - this.getX()) / var5 - 0.5D;
         double var9 = this.getHeight();
         if (var9 <= 0.0D) {
            return false;
         } else {
            double var11 = (var3 - this.getY()) / var9 - 0.5D;
            return var7 * var7 + var11 * var11 < 0.25D;
         }
      }
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (var5 > 0.0D && var7 > 0.0D) {
         double var9 = this.getWidth();
         if (var9 <= 0.0D) {
            return false;
         } else {
            double var11 = (var1 - this.getX()) / var9 - 0.5D;
            double var13 = var11 + var5 / var9;
            double var15 = this.getHeight();
            if (var15 <= 0.0D) {
               return false;
            } else {
               double var17 = (var3 - this.getY()) / var15 - 0.5D;
               double var19 = var17 + var7 / var15;
               double var21;
               if (var11 > 0.0D) {
                  var21 = var11;
               } else if (var13 < 0.0D) {
                  var21 = var13;
               } else {
                  var21 = 0.0D;
               }

               double var23;
               if (var17 > 0.0D) {
                  var23 = var17;
               } else if (var19 < 0.0D) {
                  var23 = var19;
               } else {
                  var23 = 0.0D;
               }

               return var21 * var21 + var23 * var23 < 0.25D;
            }
         }
      } else {
         return false;
      }
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      return this.contains(var1, var3) && this.contains(var1 + var5, var3) && this.contains(var1, var3 + var7) && this.contains(var1 + var5, var3 + var7);
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new EllipseIterator(this, var1);
   }

   public int hashCode() {
      long var1 = java.lang.Double.doubleToLongBits(this.getX());
      var1 += java.lang.Double.doubleToLongBits(this.getY()) * 37L;
      var1 += java.lang.Double.doubleToLongBits(this.getWidth()) * 43L;
      var1 += java.lang.Double.doubleToLongBits(this.getHeight()) * 47L;
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Ellipse2D)) {
         return false;
      } else {
         Ellipse2D var2 = (Ellipse2D)var1;
         return this.getX() == var2.getX() && this.getY() == var2.getY() && this.getWidth() == var2.getWidth() && this.getHeight() == var2.getHeight();
      }
   }

   public static class Double extends Ellipse2D implements Serializable {
      public double x;
      public double y;
      public double width;
      public double height;
      private static final long serialVersionUID = 5555464816372320683L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7) {
         this.setFrame(var1, var3, var5, var7);
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

      public boolean isEmpty() {
         return this.width <= 0.0D || this.height <= 0.0D;
      }

      public void setFrame(double var1, double var3, double var5, double var7) {
         this.x = var1;
         this.y = var3;
         this.width = var5;
         this.height = var7;
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
      }
   }

   public static class Float extends Ellipse2D implements Serializable {
      public float x;
      public float y;
      public float width;
      public float height;
      private static final long serialVersionUID = -6633761252372475977L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4) {
         this.setFrame(var1, var2, var3, var4);
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

      public boolean isEmpty() {
         return (double)this.width <= 0.0D || (double)this.height <= 0.0D;
      }

      public void setFrame(float var1, float var2, float var3, float var4) {
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
      }

      public void setFrame(double var1, double var3, double var5, double var7) {
         this.x = (float)var1;
         this.y = (float)var3;
         this.width = (float)var5;
         this.height = (float)var7;
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
      }
   }
}
