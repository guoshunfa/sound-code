package java.awt.geom;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Arc2D extends RectangularShape {
   public static final int OPEN = 0;
   public static final int CHORD = 1;
   public static final int PIE = 2;
   private int type;

   protected Arc2D() {
      this(0);
   }

   protected Arc2D(int var1) {
      this.setArcType(var1);
   }

   public abstract double getAngleStart();

   public abstract double getAngleExtent();

   public int getArcType() {
      return this.type;
   }

   public Point2D getStartPoint() {
      double var1 = Math.toRadians(-this.getAngleStart());
      double var3 = this.getX() + (Math.cos(var1) * 0.5D + 0.5D) * this.getWidth();
      double var5 = this.getY() + (Math.sin(var1) * 0.5D + 0.5D) * this.getHeight();
      return new Point2D.Double(var3, var5);
   }

   public Point2D getEndPoint() {
      double var1 = Math.toRadians(-this.getAngleStart() - this.getAngleExtent());
      double var3 = this.getX() + (Math.cos(var1) * 0.5D + 0.5D) * this.getWidth();
      double var5 = this.getY() + (Math.sin(var1) * 0.5D + 0.5D) * this.getHeight();
      return new Point2D.Double(var3, var5);
   }

   public abstract void setArc(double var1, double var3, double var5, double var7, double var9, double var11, int var13);

   public void setArc(Point2D var1, Dimension2D var2, double var3, double var5, int var7) {
      this.setArc(var1.getX(), var1.getY(), var2.getWidth(), var2.getHeight(), var3, var5, var7);
   }

   public void setArc(Rectangle2D var1, double var2, double var4, int var6) {
      this.setArc(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight(), var2, var4, var6);
   }

   public void setArc(Arc2D var1) {
      this.setArc(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight(), var1.getAngleStart(), var1.getAngleExtent(), var1.type);
   }

   public void setArcByCenter(double var1, double var3, double var5, double var7, double var9, int var11) {
      this.setArc(var1 - var5, var3 - var5, var5 * 2.0D, var5 * 2.0D, var7, var9, var11);
   }

   public void setArcByTangent(Point2D var1, Point2D var2, Point2D var3, double var4) {
      double var6 = Math.atan2(var1.getY() - var2.getY(), var1.getX() - var2.getX());
      double var8 = Math.atan2(var3.getY() - var2.getY(), var3.getX() - var2.getX());
      double var10 = var8 - var6;
      if (var10 > 3.141592653589793D) {
         var8 -= 6.283185307179586D;
      } else if (var10 < -3.141592653589793D) {
         var8 += 6.283185307179586D;
      }

      double var12 = (var6 + var8) / 2.0D;
      double var14 = Math.abs(var8 - var12);
      double var16 = var4 / Math.sin(var14);
      double var18 = var2.getX() + var16 * Math.cos(var12);
      double var20 = var2.getY() + var16 * Math.sin(var12);
      if (var6 < var8) {
         --var6;
         ++var8;
      } else {
         ++var6;
         --var8;
      }

      var6 = Math.toDegrees(-var6);
      var8 = Math.toDegrees(-var8);
      var10 = var8 - var6;
      if (var10 < 0.0D) {
         var10 += 360.0D;
      } else {
         var10 -= 360.0D;
      }

      this.setArcByCenter(var18, var20, var4, var6, var10, this.type);
   }

   public abstract void setAngleStart(double var1);

   public abstract void setAngleExtent(double var1);

   public void setAngleStart(Point2D var1) {
      double var2 = this.getHeight() * (var1.getX() - this.getCenterX());
      double var4 = this.getWidth() * (var1.getY() - this.getCenterY());
      this.setAngleStart(-Math.toDegrees(Math.atan2(var4, var2)));
   }

   public void setAngles(double var1, double var3, double var5, double var7) {
      double var9 = this.getCenterX();
      double var11 = this.getCenterY();
      double var13 = this.getWidth();
      double var15 = this.getHeight();
      double var17 = Math.atan2(var13 * (var11 - var3), var15 * (var1 - var9));
      double var19 = Math.atan2(var13 * (var11 - var7), var15 * (var5 - var9));
      var19 -= var17;
      if (var19 <= 0.0D) {
         var19 += 6.283185307179586D;
      }

      this.setAngleStart(Math.toDegrees(var17));
      this.setAngleExtent(Math.toDegrees(var19));
   }

   public void setAngles(Point2D var1, Point2D var2) {
      this.setAngles(var1.getX(), var1.getY(), var2.getX(), var2.getY());
   }

   public void setArcType(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         this.type = var1;
      } else {
         throw new IllegalArgumentException("invalid type for Arc: " + var1);
      }
   }

   public void setFrame(double var1, double var3, double var5, double var7) {
      this.setArc(var1, var3, var5, var7, this.getAngleStart(), this.getAngleExtent(), this.type);
   }

   public Rectangle2D getBounds2D() {
      if (this.isEmpty()) {
         return this.makeBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         double var1;
         double var3;
         double var5;
         double var7;
         if (this.getArcType() == 2) {
            var7 = 0.0D;
            var5 = 0.0D;
            var3 = 0.0D;
            var1 = 0.0D;
         } else {
            var3 = 1.0D;
            var1 = 1.0D;
            var7 = -1.0D;
            var5 = -1.0D;
         }

         double var9 = 0.0D;

         for(int var11 = 0; var11 < 6; ++var11) {
            if (var11 < 4) {
               var9 += 90.0D;
               if (!this.containsAngle(var9)) {
                  continue;
               }
            } else if (var11 == 4) {
               var9 = this.getAngleStart();
            } else {
               var9 += this.getAngleExtent();
            }

            double var12 = Math.toRadians(-var9);
            double var14 = Math.cos(var12);
            double var16 = Math.sin(var12);
            var1 = Math.min(var1, var14);
            var3 = Math.min(var3, var16);
            var5 = Math.max(var5, var14);
            var7 = Math.max(var7, var16);
         }

         double var18 = this.getWidth();
         double var13 = this.getHeight();
         var5 = (var5 - var1) * 0.5D * var18;
         var7 = (var7 - var3) * 0.5D * var13;
         var1 = this.getX() + (var1 * 0.5D + 0.5D) * var18;
         var3 = this.getY() + (var3 * 0.5D + 0.5D) * var13;
         return this.makeBounds(var1, var3, var5, var7);
      }
   }

   protected abstract Rectangle2D makeBounds(double var1, double var3, double var5, double var7);

   static double normalizeDegrees(double var0) {
      if (var0 > 180.0D) {
         if (var0 <= 540.0D) {
            var0 -= 360.0D;
         } else {
            var0 = Math.IEEEremainder(var0, 360.0D);
            if (var0 == -180.0D) {
               var0 = 180.0D;
            }
         }
      } else if (var0 <= -180.0D) {
         if (var0 > -540.0D) {
            var0 += 360.0D;
         } else {
            var0 = Math.IEEEremainder(var0, 360.0D);
            if (var0 == -180.0D) {
               var0 = 180.0D;
            }
         }
      }

      return var0;
   }

   public boolean containsAngle(double var1) {
      double var3 = this.getAngleExtent();
      boolean var5 = var3 < 0.0D;
      if (var5) {
         var3 = -var3;
      }

      if (var3 >= 360.0D) {
         return true;
      } else {
         var1 = normalizeDegrees(var1) - normalizeDegrees(this.getAngleStart());
         if (var5) {
            var1 = -var1;
         }

         if (var1 < 0.0D) {
            var1 += 360.0D;
         }

         return var1 >= 0.0D && var1 < var3;
      }
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
            double var13 = var7 * var7 + var11 * var11;
            if (var13 >= 0.25D) {
               return false;
            } else {
               double var15 = Math.abs(this.getAngleExtent());
               if (var15 >= 360.0D) {
                  return true;
               } else {
                  boolean var17 = this.containsAngle(-Math.toDegrees(Math.atan2(var11, var7)));
                  if (this.type == 2) {
                     return var17;
                  } else {
                     if (var17) {
                        if (var15 >= 180.0D) {
                           return true;
                        }
                     } else if (var15 <= 180.0D) {
                        return false;
                     }

                     double var18 = Math.toRadians(-this.getAngleStart());
                     double var20 = Math.cos(var18);
                     double var22 = Math.sin(var18);
                     var18 += Math.toRadians(-this.getAngleExtent());
                     double var24 = Math.cos(var18);
                     double var26 = Math.sin(var18);
                     boolean var28 = Line2D.relativeCCW(var20, var22, var24, var26, 2.0D * var7, 2.0D * var11) * Line2D.relativeCCW(var20, var22, var24, var26, 0.0D, 0.0D) >= 0;
                     return var17 ? !var28 : var28;
                  }
               }
            }
         }
      }
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      double var9 = this.getWidth();
      double var11 = this.getHeight();
      if (var5 > 0.0D && var7 > 0.0D && var9 > 0.0D && var11 > 0.0D) {
         double var13 = this.getAngleExtent();
         if (var13 == 0.0D) {
            return false;
         } else {
            double var15 = this.getX();
            double var17 = this.getY();
            double var19 = var15 + var9;
            double var21 = var17 + var11;
            double var23 = var1 + var5;
            double var25 = var3 + var7;
            if (var1 < var19 && var3 < var21 && var23 > var15 && var25 > var17) {
               double var27 = this.getCenterX();
               double var29 = this.getCenterY();
               Point2D var31 = this.getStartPoint();
               Point2D var32 = this.getEndPoint();
               double var33 = var31.getX();
               double var35 = var31.getY();
               double var37 = var32.getX();
               double var39 = var32.getY();
               if (var29 < var3 || var29 > var25 || (var33 >= var23 || var37 >= var23 || var27 >= var23 || var19 <= var1 || !this.containsAngle(0.0D)) && (var33 <= var1 || var37 <= var1 || var27 <= var1 || var15 >= var23 || !this.containsAngle(180.0D))) {
                  if (var27 >= var1 && var27 <= var23 && (var35 > var3 && var39 > var3 && var29 > var3 && var17 < var25 && this.containsAngle(90.0D) || var35 < var25 && var39 < var25 && var29 < var25 && var21 > var3 && this.containsAngle(270.0D))) {
                     return true;
                  } else {
                     Rectangle2D.Double var41 = new Rectangle2D.Double(var1, var3, var5, var7);
                     if (this.type != 2 && Math.abs(var13) <= 180.0D) {
                        if (var41.intersectsLine(var33, var35, var37, var39)) {
                           return true;
                        }
                     } else if (var41.intersectsLine(var27, var29, var33, var35) || var41.intersectsLine(var27, var29, var37, var39)) {
                        return true;
                     }

                     return this.contains(var1, var3) || this.contains(var1 + var5, var3) || this.contains(var1, var3 + var7) || this.contains(var1 + var5, var3 + var7);
                  }
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      return this.contains(var1, var3, var5, var7, (Rectangle2D)null);
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight(), var1);
   }

   private boolean contains(double var1, double var3, double var5, double var7, Rectangle2D var9) {
      if (this.contains(var1, var3) && this.contains(var1 + var5, var3) && this.contains(var1, var3 + var7) && this.contains(var1 + var5, var3 + var7)) {
         if (this.type == 2 && Math.abs(this.getAngleExtent()) > 180.0D) {
            if (var9 == null) {
               var9 = new Rectangle2D.Double(var1, var3, var5, var7);
            }

            double var10 = this.getWidth() / 2.0D;
            double var12 = this.getHeight() / 2.0D;
            double var14 = this.getX() + var10;
            double var16 = this.getY() + var12;
            double var18 = Math.toRadians(-this.getAngleStart());
            double var20 = var14 + var10 * Math.cos(var18);
            double var22 = var16 + var12 * Math.sin(var18);
            if (((Rectangle2D)var9).intersectsLine(var14, var16, var20, var22)) {
               return false;
            } else {
               var18 += Math.toRadians(-this.getAngleExtent());
               var20 = var14 + var10 * Math.cos(var18);
               var22 = var16 + var12 * Math.sin(var18);
               return !((Rectangle2D)var9).intersectsLine(var14, var16, var20, var22);
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new ArcIterator(this, var1);
   }

   public int hashCode() {
      long var1 = java.lang.Double.doubleToLongBits(this.getX());
      var1 += java.lang.Double.doubleToLongBits(this.getY()) * 37L;
      var1 += java.lang.Double.doubleToLongBits(this.getWidth()) * 43L;
      var1 += java.lang.Double.doubleToLongBits(this.getHeight()) * 47L;
      var1 += java.lang.Double.doubleToLongBits(this.getAngleStart()) * 53L;
      var1 += java.lang.Double.doubleToLongBits(this.getAngleExtent()) * 59L;
      var1 += (long)(this.getArcType() * 61);
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Arc2D)) {
         return false;
      } else {
         Arc2D var2 = (Arc2D)var1;
         return this.getX() == var2.getX() && this.getY() == var2.getY() && this.getWidth() == var2.getWidth() && this.getHeight() == var2.getHeight() && this.getAngleStart() == var2.getAngleStart() && this.getAngleExtent() == var2.getAngleExtent() && this.getArcType() == var2.getArcType();
      }
   }

   public static class Double extends Arc2D implements Serializable {
      public double x;
      public double y;
      public double width;
      public double height;
      public double start;
      public double extent;
      private static final long serialVersionUID = 728264085846882001L;

      public Double() {
         super(0);
      }

      public Double(int var1) {
         super(var1);
      }

      public Double(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
         super(var13);
         this.x = var1;
         this.y = var3;
         this.width = var5;
         this.height = var7;
         this.start = var9;
         this.extent = var11;
      }

      public Double(Rectangle2D var1, double var2, double var4, int var6) {
         super(var6);
         this.x = var1.getX();
         this.y = var1.getY();
         this.width = var1.getWidth();
         this.height = var1.getHeight();
         this.start = var2;
         this.extent = var4;
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

      public double getAngleStart() {
         return this.start;
      }

      public double getAngleExtent() {
         return this.extent;
      }

      public boolean isEmpty() {
         return this.width <= 0.0D || this.height <= 0.0D;
      }

      public void setArc(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
         this.setArcType(var13);
         this.x = var1;
         this.y = var3;
         this.width = var5;
         this.height = var7;
         this.start = var9;
         this.extent = var11;
      }

      public void setAngleStart(double var1) {
         this.start = var1;
      }

      public void setAngleExtent(double var1) {
         this.extent = var1;
      }

      protected Rectangle2D makeBounds(double var1, double var3, double var5, double var7) {
         return new Rectangle2D.Double(var1, var3, var5, var7);
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeByte(this.getArcType());
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();

         try {
            this.setArcType(var1.readByte());
         } catch (IllegalArgumentException var3) {
            throw new InvalidObjectException(var3.getMessage());
         }
      }
   }

   public static class Float extends Arc2D implements Serializable {
      public float x;
      public float y;
      public float width;
      public float height;
      public float start;
      public float extent;
      private static final long serialVersionUID = 9130893014586380278L;

      public Float() {
         super(0);
      }

      public Float(int var1) {
         super(var1);
      }

      public Float(float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
         super(var7);
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
         this.start = var5;
         this.extent = var6;
      }

      public Float(Rectangle2D var1, float var2, float var3, int var4) {
         super(var4);
         this.x = (float)var1.getX();
         this.y = (float)var1.getY();
         this.width = (float)var1.getWidth();
         this.height = (float)var1.getHeight();
         this.start = var2;
         this.extent = var3;
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

      public double getAngleStart() {
         return (double)this.start;
      }

      public double getAngleExtent() {
         return (double)this.extent;
      }

      public boolean isEmpty() {
         return (double)this.width <= 0.0D || (double)this.height <= 0.0D;
      }

      public void setArc(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
         this.setArcType(var13);
         this.x = (float)var1;
         this.y = (float)var3;
         this.width = (float)var5;
         this.height = (float)var7;
         this.start = (float)var9;
         this.extent = (float)var11;
      }

      public void setAngleStart(double var1) {
         this.start = (float)var1;
      }

      public void setAngleExtent(double var1) {
         this.extent = (float)var1;
      }

      protected Rectangle2D makeBounds(double var1, double var3, double var5, double var7) {
         return new Rectangle2D.Float((float)var1, (float)var3, (float)var5, (float)var7);
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeByte(this.getArcType());
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();

         try {
            this.setArcType(var1.readByte());
         } catch (IllegalArgumentException var3) {
            throw new InvalidObjectException(var3.getMessage());
         }
      }
   }
}
