package java.awt.geom;

import java.io.Serializable;

public abstract class Point2D implements Cloneable {
   protected Point2D() {
   }

   public abstract double getX();

   public abstract double getY();

   public abstract void setLocation(double var1, double var3);

   public void setLocation(Point2D var1) {
      this.setLocation(var1.getX(), var1.getY());
   }

   public static double distanceSq(double var0, double var2, double var4, double var6) {
      var0 -= var4;
      var2 -= var6;
      return var0 * var0 + var2 * var2;
   }

   public static double distance(double var0, double var2, double var4, double var6) {
      var0 -= var4;
      var2 -= var6;
      return Math.sqrt(var0 * var0 + var2 * var2);
   }

   public double distanceSq(double var1, double var3) {
      var1 -= this.getX();
      var3 -= this.getY();
      return var1 * var1 + var3 * var3;
   }

   public double distanceSq(Point2D var1) {
      double var2 = var1.getX() - this.getX();
      double var4 = var1.getY() - this.getY();
      return var2 * var2 + var4 * var4;
   }

   public double distance(double var1, double var3) {
      var1 -= this.getX();
      var3 -= this.getY();
      return Math.sqrt(var1 * var1 + var3 * var3);
   }

   public double distance(Point2D var1) {
      double var2 = var1.getX() - this.getX();
      double var4 = var1.getY() - this.getY();
      return Math.sqrt(var2 * var2 + var4 * var4);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public int hashCode() {
      long var1 = java.lang.Double.doubleToLongBits(this.getX());
      var1 ^= java.lang.Double.doubleToLongBits(this.getY()) * 31L;
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Point2D)) {
         return super.equals(var1);
      } else {
         Point2D var2 = (Point2D)var1;
         return this.getX() == var2.getX() && this.getY() == var2.getY();
      }
   }

   public static class Double extends Point2D implements Serializable {
      public double x;
      public double y;
      private static final long serialVersionUID = 6150783262733311327L;

      public Double() {
      }

      public Double(double var1, double var3) {
         this.x = var1;
         this.y = var3;
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public void setLocation(double var1, double var3) {
         this.x = var1;
         this.y = var3;
      }

      public String toString() {
         return "Point2D.Double[" + this.x + ", " + this.y + "]";
      }
   }

   public static class Float extends Point2D implements Serializable {
      public float x;
      public float y;
      private static final long serialVersionUID = -2870572449815403710L;

      public Float() {
      }

      public Float(float var1, float var2) {
         this.x = var1;
         this.y = var2;
      }

      public double getX() {
         return (double)this.x;
      }

      public double getY() {
         return (double)this.y;
      }

      public void setLocation(double var1, double var3) {
         this.x = (float)var1;
         this.y = (float)var3;
      }

      public void setLocation(float var1, float var2) {
         this.x = var1;
         this.y = var2;
      }

      public String toString() {
         return "Point2D.Float[" + this.x + ", " + this.y + "]";
      }
   }
}
