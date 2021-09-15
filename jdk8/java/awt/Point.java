package java.awt;

import java.awt.geom.Point2D;
import java.beans.Transient;
import java.io.Serializable;

public class Point extends Point2D implements Serializable {
   public int x;
   public int y;
   private static final long serialVersionUID = -5276940640259749850L;

   public Point() {
      this(0, 0);
   }

   public Point(Point var1) {
      this(var1.x, var1.y);
   }

   public Point(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public double getX() {
      return (double)this.x;
   }

   public double getY() {
      return (double)this.y;
   }

   @Transient
   public Point getLocation() {
      return new Point(this.x, this.y);
   }

   public void setLocation(Point var1) {
      this.setLocation(var1.x, var1.y);
   }

   public void setLocation(int var1, int var2) {
      this.move(var1, var2);
   }

   public void setLocation(double var1, double var3) {
      this.x = (int)Math.floor(var1 + 0.5D);
      this.y = (int)Math.floor(var3 + 0.5D);
   }

   public void move(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void translate(int var1, int var2) {
      this.x += var1;
      this.y += var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Point)) {
         return super.equals(var1);
      } else {
         Point var2 = (Point)var1;
         return this.x == var2.x && this.y == var2.y;
      }
   }

   public String toString() {
      return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + "]";
   }
}
